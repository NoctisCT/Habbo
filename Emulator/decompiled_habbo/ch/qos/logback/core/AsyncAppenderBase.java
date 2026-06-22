/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core;

import ch.qos.logback.core.Appender;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.spi.AppenderAttachable;
import ch.qos.logback.core.spi.AppenderAttachableImpl;
import ch.qos.logback.core.util.InterruptUtil;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class AsyncAppenderBase<E>
extends UnsynchronizedAppenderBase<E>
implements AppenderAttachable<E> {
    AppenderAttachableImpl<E> aai = new AppenderAttachableImpl();
    BlockingQueue<E> blockingQueue;
    public static final int DEFAULT_QUEUE_SIZE = 256;
    int queueSize = 256;
    int appenderCount = 0;
    static final int UNDEFINED = -1;
    int discardingThreshold = -1;
    boolean neverBlock = false;
    Worker worker = new Worker();
    public static final int DEFAULT_MAX_FLUSH_TIME = 1000;
    int maxFlushTime = 1000;

    protected boolean isDiscardable(E eventObject) {
        return false;
    }

    protected void preprocess(E eventObject) {
    }

    @Override
    public void start() {
        if (this.isStarted()) {
            return;
        }
        if (this.appenderCount == 0) {
            this.addError("No attached appenders found.");
            return;
        }
        if (this.queueSize < 1) {
            this.addError("Invalid queue size [" + this.queueSize + "]");
            return;
        }
        this.blockingQueue = new ArrayBlockingQueue(this.queueSize);
        if (this.discardingThreshold == -1) {
            this.discardingThreshold = this.queueSize / 5;
        }
        this.addInfo("Setting discardingThreshold to " + this.discardingThreshold);
        this.worker.setDaemon(true);
        this.worker.setName("AsyncAppender-Worker-" + this.getName());
        super.start();
        this.worker.start();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void stop() {
        if (!this.isStarted()) {
            return;
        }
        super.stop();
        this.worker.interrupt();
        InterruptUtil interruptUtil = new InterruptUtil(this.context);
        try {
            interruptUtil.maskInterruptFlag();
            this.worker.join(this.maxFlushTime);
            if (this.worker.isAlive()) {
                this.addWarn("Max queue flush timeout (" + this.maxFlushTime + " ms) exceeded. Approximately " + this.blockingQueue.size() + " queued events were possibly discarded.");
            } else {
                this.addInfo("Queue flush finished successfully within timeout.");
            }
        }
        catch (InterruptedException e) {
            int remaining = this.blockingQueue.size();
            this.addError("Failed to join worker thread. " + remaining + " queued events may be discarded.", e);
        }
        finally {
            interruptUtil.unmaskInterruptFlag();
        }
    }

    @Override
    protected void append(E eventObject) {
        if (this.isQueueBelowDiscardingThreshold() && this.isDiscardable(eventObject)) {
            return;
        }
        this.preprocess(eventObject);
        this.put(eventObject);
    }

    private boolean isQueueBelowDiscardingThreshold() {
        return this.blockingQueue.remainingCapacity() < this.discardingThreshold;
    }

    private void put(E eventObject) {
        if (this.neverBlock) {
            this.blockingQueue.offer(eventObject);
        } else {
            this.putUninterruptibly(eventObject);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void putUninterruptibly(E eventObject) {
        boolean interrupted = false;
        try {
            while (true) {
                try {
                    this.blockingQueue.put(eventObject);
                }
                catch (InterruptedException e) {
                    interrupted = true;
                    continue;
                }
                break;
            }
        }
        finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public int getQueueSize() {
        return this.queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public int getDiscardingThreshold() {
        return this.discardingThreshold;
    }

    public void setDiscardingThreshold(int discardingThreshold) {
        this.discardingThreshold = discardingThreshold;
    }

    public int getMaxFlushTime() {
        return this.maxFlushTime;
    }

    public void setMaxFlushTime(int maxFlushTime) {
        this.maxFlushTime = maxFlushTime;
    }

    public int getNumberOfElementsInQueue() {
        return this.blockingQueue.size();
    }

    public void setNeverBlock(boolean neverBlock) {
        this.neverBlock = neverBlock;
    }

    public boolean isNeverBlock() {
        return this.neverBlock;
    }

    public int getRemainingCapacity() {
        return this.blockingQueue.remainingCapacity();
    }

    @Override
    public void addAppender(Appender<E> newAppender) {
        if (this.appenderCount == 0) {
            ++this.appenderCount;
            this.addInfo("Attaching appender named [" + newAppender.getName() + "] to AsyncAppender.");
            this.aai.addAppender(newAppender);
        } else {
            this.addWarn("One and only one appender may be attached to AsyncAppender.");
            this.addWarn("Ignoring additional appender named [" + newAppender.getName() + "]");
        }
    }

    @Override
    public Iterator<Appender<E>> iteratorForAppenders() {
        return this.aai.iteratorForAppenders();
    }

    @Override
    public Appender<E> getAppender(String name) {
        return this.aai.getAppender(name);
    }

    @Override
    public boolean isAttached(Appender<E> eAppender) {
        return this.aai.isAttached(eAppender);
    }

    @Override
    public void detachAndStopAllAppenders() {
        this.aai.detachAndStopAllAppenders();
    }

    @Override
    public boolean detachAppender(Appender<E> eAppender) {
        return this.aai.detachAppender(eAppender);
    }

    @Override
    public boolean detachAppender(String name) {
        return this.aai.detachAppender(name);
    }

    class Worker
    extends Thread {
        Worker() {
        }

        @Override
        public void run() {
            AsyncAppenderBase parent = AsyncAppenderBase.this;
            AppenderAttachableImpl aai = parent.aai;
            while (parent.isStarted()) {
                try {
                    Object e = parent.blockingQueue.take();
                    aai.appendLoopOnAppenders(e);
                }
                catch (InterruptedException ie) {
                    // empty catch block
                    break;
                }
            }
            AsyncAppenderBase.this.addInfo("Worker thread will flush remaining events before exiting. ");
            for (Object e : parent.blockingQueue) {
                aai.appendLoopOnAppenders(e);
                parent.blockingQueue.remove(e);
            }
            aai.detachAndStopAllAppenders();
        }
    }
}

