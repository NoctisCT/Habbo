/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util;

import io.netty.util.ResourceLeak;
import io.netty.util.ResourceLeakHint;
import io.netty.util.ResourceLeakTracker;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class ResourceLeakDetector<T> {
    private static final String PROP_LEVEL_OLD = "io.netty.leakDetectionLevel";
    private static final String PROP_LEVEL = "io.netty.leakDetection.level";
    private static final Level DEFAULT_LEVEL;
    private static final String PROP_TARGET_RECORDS = "io.netty.leakDetection.targetRecords";
    private static final int DEFAULT_TARGET_RECORDS = 4;
    private static final String PROP_SAMPLING_INTERVAL = "io.netty.leakDetection.samplingInterval";
    private static final int DEFAULT_SAMPLING_INTERVAL = 128;
    private static final int TARGET_RECORDS;
    static final int SAMPLING_INTERVAL;
    private static Level level;
    private static final InternalLogger logger;
    private final Set<DefaultResourceLeak<?>> allLeaks = Collections.newSetFromMap(new ConcurrentHashMap());
    private final ReferenceQueue<Object> refQueue = new ReferenceQueue();
    private final Set<String> reportedLeaks = Collections.newSetFromMap(new ConcurrentHashMap());
    private final String resourceType;
    private final int samplingInterval;
    private static final AtomicReference<String[]> excludedMethods;

    @Deprecated
    public static void setEnabled(boolean enabled) {
        ResourceLeakDetector.setLevel(enabled ? Level.SIMPLE : Level.DISABLED);
    }

    public static boolean isEnabled() {
        return ResourceLeakDetector.getLevel().ordinal() > Level.DISABLED.ordinal();
    }

    public static void setLevel(Level level) {
        ResourceLeakDetector.level = ObjectUtil.checkNotNull(level, "level");
    }

    public static Level getLevel() {
        return level;
    }

    @Deprecated
    public ResourceLeakDetector(Class<?> resourceType) {
        this(StringUtil.simpleClassName(resourceType));
    }

    @Deprecated
    public ResourceLeakDetector(String resourceType) {
        this(resourceType, 128, Long.MAX_VALUE);
    }

    @Deprecated
    public ResourceLeakDetector(Class<?> resourceType, int samplingInterval, long maxActive) {
        this(resourceType, samplingInterval);
    }

    public ResourceLeakDetector(Class<?> resourceType, int samplingInterval) {
        this(StringUtil.simpleClassName(resourceType), samplingInterval, Long.MAX_VALUE);
    }

    @Deprecated
    public ResourceLeakDetector(String resourceType, int samplingInterval, long maxActive) {
        this.resourceType = ObjectUtil.checkNotNull(resourceType, "resourceType");
        this.samplingInterval = samplingInterval;
    }

    @Deprecated
    public final ResourceLeak open(T obj) {
        return this.track0(obj);
    }

    public final ResourceLeakTracker<T> track(T obj) {
        return this.track0(obj);
    }

    private DefaultResourceLeak track0(T obj) {
        Level level = ResourceLeakDetector.level;
        if (level == Level.DISABLED) {
            return null;
        }
        if (level.ordinal() < Level.PARANOID.ordinal()) {
            if (PlatformDependent.threadLocalRandom().nextInt(this.samplingInterval) == 0) {
                this.reportLeak();
                return new DefaultResourceLeak(obj, this.refQueue, this.allLeaks);
            }
            return null;
        }
        this.reportLeak();
        return new DefaultResourceLeak(obj, this.refQueue, this.allLeaks);
    }

    private void clearRefQueue() {
        DefaultResourceLeak ref;
        while ((ref = (DefaultResourceLeak)this.refQueue.poll()) != null) {
            ref.dispose();
        }
    }

    protected boolean needReport() {
        return logger.isErrorEnabled();
    }

    private void reportLeak() {
        DefaultResourceLeak ref;
        if (!this.needReport()) {
            this.clearRefQueue();
            return;
        }
        while ((ref = (DefaultResourceLeak)this.refQueue.poll()) != null) {
            String records;
            if (!ref.dispose() || !this.reportedLeaks.add(records = ref.toString())) continue;
            if (records.isEmpty()) {
                this.reportUntracedLeak(this.resourceType);
                continue;
            }
            this.reportTracedLeak(this.resourceType, records);
        }
    }

    protected void reportTracedLeak(String resourceType, String records) {
        logger.error("LEAK: {}.release() was not called before it's garbage-collected. See https://netty.io/wiki/reference-counted-objects.html for more information.{}", (Object)resourceType, (Object)records);
    }

    protected void reportUntracedLeak(String resourceType) {
        logger.error("LEAK: {}.release() was not called before it's garbage-collected. Enable advanced leak reporting to find out where the leak occurred. To enable advanced leak reporting, specify the JVM option '-D{}={}' or call {}.setLevel() See https://netty.io/wiki/reference-counted-objects.html for more information.", resourceType, PROP_LEVEL, Level.ADVANCED.name().toLowerCase(), StringUtil.simpleClassName(this));
    }

    @Deprecated
    protected void reportInstancesLeak(String resourceType) {
    }

    public static void addExclusions(Class clz, String ... methodNames) {
        String[] newMethods;
        String[] oldMethods;
        Method method;
        HashSet<String> nameSet = new HashSet<String>(Arrays.asList(methodNames));
        Method[] methodArray = clz.getDeclaredMethods();
        int n = methodArray.length;
        for (int i = 0; !(i >= n || nameSet.remove((method = methodArray[i]).getName()) && nameSet.isEmpty()); ++i) {
        }
        if (!nameSet.isEmpty()) {
            throw new IllegalArgumentException("Can't find '" + nameSet + "' in " + clz.getName());
        }
        do {
            oldMethods = excludedMethods.get();
            newMethods = Arrays.copyOf(oldMethods, oldMethods.length + 2 * methodNames.length);
            for (int i = 0; i < methodNames.length; ++i) {
                newMethods[oldMethods.length + i * 2] = clz.getName();
                newMethods[oldMethods.length + i * 2 + 1] = methodNames[i];
            }
        } while (!excludedMethods.compareAndSet(oldMethods, newMethods));
    }

    static {
        boolean disabled;
        DEFAULT_LEVEL = Level.SIMPLE;
        logger = InternalLoggerFactory.getInstance(ResourceLeakDetector.class);
        if (SystemPropertyUtil.get("io.netty.noResourceLeakDetection") != null) {
            disabled = SystemPropertyUtil.getBoolean("io.netty.noResourceLeakDetection", false);
            logger.debug("-Dio.netty.noResourceLeakDetection: {}", (Object)disabled);
            logger.warn("-Dio.netty.noResourceLeakDetection is deprecated. Use '-D{}={}' instead.", (Object)PROP_LEVEL, (Object)DEFAULT_LEVEL.name().toLowerCase());
        } else {
            disabled = false;
        }
        Level defaultLevel = disabled ? Level.DISABLED : DEFAULT_LEVEL;
        String levelStr = SystemPropertyUtil.get(PROP_LEVEL_OLD, defaultLevel.name());
        levelStr = SystemPropertyUtil.get(PROP_LEVEL, levelStr);
        Level level = Level.parseLevel(levelStr);
        TARGET_RECORDS = SystemPropertyUtil.getInt(PROP_TARGET_RECORDS, 4);
        SAMPLING_INTERVAL = SystemPropertyUtil.getInt(PROP_SAMPLING_INTERVAL, 128);
        ResourceLeakDetector.level = level;
        if (logger.isDebugEnabled()) {
            logger.debug("-D{}: {}", (Object)PROP_LEVEL, (Object)level.name().toLowerCase());
            logger.debug("-D{}: {}", (Object)PROP_TARGET_RECORDS, (Object)TARGET_RECORDS);
        }
        excludedMethods = new AtomicReference<String[]>(EmptyArrays.EMPTY_STRINGS);
    }

    private static final class Record
    extends Throwable {
        private static final long serialVersionUID = 6065153674892850720L;
        private static final Record BOTTOM = new Record();
        private final String hintString;
        private final Record next;
        private final int pos;

        Record(Record next, Object hint) {
            this.hintString = hint instanceof ResourceLeakHint ? ((ResourceLeakHint)hint).toHintString() : hint.toString();
            this.next = next;
            this.pos = next.pos + 1;
        }

        Record(Record next) {
            this.hintString = null;
            this.next = next;
            this.pos = next.pos + 1;
        }

        private Record() {
            this.hintString = null;
            this.next = null;
            this.pos = -1;
        }

        @Override
        public String toString() {
            StringBuilder buf = new StringBuilder(2048);
            if (this.hintString != null) {
                buf.append("\tHint: ").append(this.hintString).append(StringUtil.NEWLINE);
            }
            StackTraceElement[] array = this.getStackTrace();
            block0: for (int i = 3; i < array.length; ++i) {
                StackTraceElement element = array[i];
                String[] exclusions = (String[])excludedMethods.get();
                for (int k = 0; k < exclusions.length; k += 2) {
                    if (exclusions[k].equals(element.getClassName()) && exclusions[k + 1].equals(element.getMethodName())) continue block0;
                }
                buf.append('\t');
                buf.append(element.toString());
                buf.append(StringUtil.NEWLINE);
            }
            return buf.toString();
        }
    }

    private static final class DefaultResourceLeak<T>
    extends WeakReference<Object>
    implements ResourceLeakTracker<T>,
    ResourceLeak {
        private static final AtomicReferenceFieldUpdater<DefaultResourceLeak<?>, Record> headUpdater = AtomicReferenceFieldUpdater.newUpdater(DefaultResourceLeak.class, Record.class, "head");
        private static final AtomicIntegerFieldUpdater<DefaultResourceLeak<?>> droppedRecordsUpdater = AtomicIntegerFieldUpdater.newUpdater(DefaultResourceLeak.class, "droppedRecords");
        private volatile Record head;
        private volatile int droppedRecords;
        private final Set<DefaultResourceLeak<?>> allLeaks;
        private final int trackedHash;

        DefaultResourceLeak(Object referent, ReferenceQueue<Object> refQueue, Set<DefaultResourceLeak<?>> allLeaks) {
            super(referent, refQueue);
            assert (referent != null);
            this.trackedHash = System.identityHashCode(referent);
            allLeaks.add(this);
            headUpdater.set(this, new Record(Record.BOTTOM));
            this.allLeaks = allLeaks;
        }

        @Override
        public void record() {
            this.record0(null);
        }

        @Override
        public void record(Object hint) {
            this.record0(hint);
        }

        private void record0(Object hint) {
            if (TARGET_RECORDS > 0) {
                boolean dropped;
                Record prevHead;
                Record newHead;
                Record oldHead;
                do {
                    prevHead = oldHead = headUpdater.get(this);
                    if (oldHead == null) {
                        return;
                    }
                    int numElements = oldHead.pos + 1;
                    if (numElements >= TARGET_RECORDS) {
                        int backOffFactor = Math.min(numElements - TARGET_RECORDS, 30);
                        dropped = PlatformDependent.threadLocalRandom().nextInt(1 << backOffFactor) != 0;
                        if (!dropped) continue;
                        prevHead = oldHead.next;
                        continue;
                    }
                    dropped = false;
                } while (!headUpdater.compareAndSet(this, oldHead, newHead = hint != null ? new Record(prevHead, hint) : new Record(prevHead)));
                if (dropped) {
                    droppedRecordsUpdater.incrementAndGet(this);
                }
            }
        }

        boolean dispose() {
            this.clear();
            return this.allLeaks.remove(this);
        }

        @Override
        public boolean close() {
            if (this.allLeaks.remove(this)) {
                this.clear();
                headUpdater.set(this, null);
                return true;
            }
            return false;
        }

        @Override
        public boolean close(T trackedObject) {
            assert (this.trackedHash == System.identityHashCode(trackedObject));
            try {
                boolean bl = this.close();
                return bl;
            }
            finally {
                DefaultResourceLeak.reachabilityFence0(trackedObject);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private static void reachabilityFence0(Object ref) {
            if (ref != null) {
                Object object = ref;
                synchronized (object) {
                }
            }
        }

        public String toString() {
            Record oldHead = headUpdater.getAndSet(this, null);
            if (oldHead == null) {
                return "";
            }
            int dropped = droppedRecordsUpdater.get(this);
            int duped = 0;
            int present = oldHead.pos + 1;
            StringBuilder buf = new StringBuilder(present * 2048).append(StringUtil.NEWLINE);
            buf.append("Recent access records: ").append(StringUtil.NEWLINE);
            int i = 1;
            HashSet<String> seen = new HashSet<String>(present);
            while (oldHead != Record.BOTTOM) {
                String s = oldHead.toString();
                if (seen.add(s)) {
                    if (oldHead.next == Record.BOTTOM) {
                        buf.append("Created at:").append(StringUtil.NEWLINE).append(s);
                    } else {
                        buf.append('#').append(i++).append(':').append(StringUtil.NEWLINE).append(s);
                    }
                } else {
                    ++duped;
                }
                oldHead = oldHead.next;
            }
            if (duped > 0) {
                buf.append(": ").append(duped).append(" leak records were discarded because they were duplicates").append(StringUtil.NEWLINE);
            }
            if (dropped > 0) {
                buf.append(": ").append(dropped).append(" leak records were discarded because the leak record count is targeted to ").append(TARGET_RECORDS).append(". Use system property ").append(ResourceLeakDetector.PROP_TARGET_RECORDS).append(" to increase the limit.").append(StringUtil.NEWLINE);
            }
            buf.setLength(buf.length() - StringUtil.NEWLINE.length());
            return buf.toString();
        }
    }

    public static enum Level {
        DISABLED,
        SIMPLE,
        ADVANCED,
        PARANOID;


        static Level parseLevel(String levelStr) {
            String trimmedLevelStr = levelStr.trim();
            for (Level l : Level.values()) {
                if (!trimmedLevelStr.equalsIgnoreCase(l.name()) && !trimmedLevelStr.equals(String.valueOf(l.ordinal()))) continue;
                return l;
            }
            return DEFAULT_LEVEL;
        }
    }
}

