/*
 * Decompiled with CFR 0.152.
 */
package io.netty.buffer;

import io.netty.buffer.PoolArena;
import io.netty.buffer.PoolChunk;
import io.netty.buffer.PooledByteBuf;
import io.netty.util.internal.MathUtil;
import io.netty.util.internal.ObjectPool;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.ByteBuffer;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

final class PoolThreadCache {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(PoolThreadCache.class);
    private static final int INTEGER_SIZE_MINUS_ONE = 31;
    final PoolArena<byte[]> heapArena;
    final PoolArena<ByteBuffer> directArena;
    private final MemoryRegionCache<byte[]>[] tinySubPageHeapCaches;
    private final MemoryRegionCache<byte[]>[] smallSubPageHeapCaches;
    private final MemoryRegionCache<ByteBuffer>[] tinySubPageDirectCaches;
    private final MemoryRegionCache<ByteBuffer>[] smallSubPageDirectCaches;
    private final MemoryRegionCache<byte[]>[] normalHeapCaches;
    private final MemoryRegionCache<ByteBuffer>[] normalDirectCaches;
    private final int numShiftsNormalDirect;
    private final int numShiftsNormalHeap;
    private final int freeSweepAllocationThreshold;
    private final AtomicBoolean freed = new AtomicBoolean();
    private int allocations;

    PoolThreadCache(PoolArena<byte[]> heapArena, PoolArena<ByteBuffer> directArena, int tinyCacheSize, int smallCacheSize, int normalCacheSize, int maxCachedBufferCapacity, int freeSweepAllocationThreshold) {
        ObjectUtil.checkPositiveOrZero(maxCachedBufferCapacity, "maxCachedBufferCapacity");
        this.freeSweepAllocationThreshold = freeSweepAllocationThreshold;
        this.heapArena = heapArena;
        this.directArena = directArena;
        if (directArena != null) {
            this.tinySubPageDirectCaches = PoolThreadCache.createSubPageCaches(tinyCacheSize, 32, PoolArena.SizeClass.Tiny);
            this.smallSubPageDirectCaches = PoolThreadCache.createSubPageCaches(smallCacheSize, directArena.numSmallSubpagePools, PoolArena.SizeClass.Small);
            this.numShiftsNormalDirect = PoolThreadCache.log2(directArena.pageSize);
            this.normalDirectCaches = PoolThreadCache.createNormalCaches(normalCacheSize, maxCachedBufferCapacity, directArena);
            directArena.numThreadCaches.getAndIncrement();
        } else {
            this.tinySubPageDirectCaches = null;
            this.smallSubPageDirectCaches = null;
            this.normalDirectCaches = null;
            this.numShiftsNormalDirect = -1;
        }
        if (heapArena != null) {
            this.tinySubPageHeapCaches = PoolThreadCache.createSubPageCaches(tinyCacheSize, 32, PoolArena.SizeClass.Tiny);
            this.smallSubPageHeapCaches = PoolThreadCache.createSubPageCaches(smallCacheSize, heapArena.numSmallSubpagePools, PoolArena.SizeClass.Small);
            this.numShiftsNormalHeap = PoolThreadCache.log2(heapArena.pageSize);
            this.normalHeapCaches = PoolThreadCache.createNormalCaches(normalCacheSize, maxCachedBufferCapacity, heapArena);
            heapArena.numThreadCaches.getAndIncrement();
        } else {
            this.tinySubPageHeapCaches = null;
            this.smallSubPageHeapCaches = null;
            this.normalHeapCaches = null;
            this.numShiftsNormalHeap = -1;
        }
        if ((this.tinySubPageDirectCaches != null || this.smallSubPageDirectCaches != null || this.normalDirectCaches != null || this.tinySubPageHeapCaches != null || this.smallSubPageHeapCaches != null || this.normalHeapCaches != null) && freeSweepAllocationThreshold < 1) {
            throw new IllegalArgumentException("freeSweepAllocationThreshold: " + freeSweepAllocationThreshold + " (expected: > 0)");
        }
    }

    private static <T> MemoryRegionCache<T>[] createSubPageCaches(int cacheSize, int numCaches, PoolArena.SizeClass sizeClass) {
        if (cacheSize > 0 && numCaches > 0) {
            MemoryRegionCache[] cache = new MemoryRegionCache[numCaches];
            for (int i = 0; i < cache.length; ++i) {
                cache[i] = new SubPageMemoryRegionCache(cacheSize, sizeClass);
            }
            return cache;
        }
        return null;
    }

    private static <T> MemoryRegionCache<T>[] createNormalCaches(int cacheSize, int maxCachedBufferCapacity, PoolArena<T> area) {
        if (cacheSize > 0 && maxCachedBufferCapacity > 0) {
            int max = Math.min(area.chunkSize, maxCachedBufferCapacity);
            int arraySize = Math.max(1, PoolThreadCache.log2(max / area.pageSize) + 1);
            MemoryRegionCache[] cache = new MemoryRegionCache[arraySize];
            for (int i = 0; i < cache.length; ++i) {
                cache[i] = new NormalMemoryRegionCache(cacheSize);
            }
            return cache;
        }
        return null;
    }

    private static int log2(int val) {
        return 31 - Integer.numberOfLeadingZeros(val);
    }

    boolean allocateTiny(PoolArena<?> area, PooledByteBuf<?> buf, int reqCapacity, int normCapacity) {
        return this.allocate(this.cacheForTiny(area, normCapacity), buf, reqCapacity);
    }

    boolean allocateSmall(PoolArena<?> area, PooledByteBuf<?> buf, int reqCapacity, int normCapacity) {
        return this.allocate(this.cacheForSmall(area, normCapacity), buf, reqCapacity);
    }

    boolean allocateNormal(PoolArena<?> area, PooledByteBuf<?> buf, int reqCapacity, int normCapacity) {
        return this.allocate(this.cacheForNormal(area, normCapacity), buf, reqCapacity);
    }

    private boolean allocate(MemoryRegionCache<?> cache, PooledByteBuf buf, int reqCapacity) {
        if (cache == null) {
            return false;
        }
        boolean allocated = cache.allocate(buf, reqCapacity, this);
        if (++this.allocations >= this.freeSweepAllocationThreshold) {
            this.allocations = 0;
            this.trim();
        }
        return allocated;
    }

    boolean add(PoolArena<?> area, PoolChunk chunk, ByteBuffer nioBuffer, long handle, int normCapacity, PoolArena.SizeClass sizeClass) {
        MemoryRegionCache<?> cache = this.cache(area, normCapacity, sizeClass);
        if (cache == null) {
            return false;
        }
        return cache.add(chunk, nioBuffer, handle);
    }

    private MemoryRegionCache<?> cache(PoolArena<?> area, int normCapacity, PoolArena.SizeClass sizeClass) {
        switch (sizeClass) {
            case Normal: {
                return this.cacheForNormal(area, normCapacity);
            }
            case Small: {
                return this.cacheForSmall(area, normCapacity);
            }
            case Tiny: {
                return this.cacheForTiny(area, normCapacity);
            }
        }
        throw new Error();
    }

    protected void finalize() throws Throwable {
        try {
            super.finalize();
        }
        finally {
            this.free(true);
        }
    }

    void free(boolean finalizer) {
        if (this.freed.compareAndSet(false, true)) {
            int numFreed = PoolThreadCache.free(this.tinySubPageDirectCaches, finalizer) + PoolThreadCache.free(this.smallSubPageDirectCaches, finalizer) + PoolThreadCache.free(this.normalDirectCaches, finalizer) + PoolThreadCache.free(this.tinySubPageHeapCaches, finalizer) + PoolThreadCache.free(this.smallSubPageHeapCaches, finalizer) + PoolThreadCache.free(this.normalHeapCaches, finalizer);
            if (numFreed > 0 && logger.isDebugEnabled()) {
                logger.debug("Freed {} thread-local buffer(s) from thread: {}", (Object)numFreed, (Object)Thread.currentThread().getName());
            }
            if (this.directArena != null) {
                this.directArena.numThreadCaches.getAndDecrement();
            }
            if (this.heapArena != null) {
                this.heapArena.numThreadCaches.getAndDecrement();
            }
        }
    }

    private static int free(MemoryRegionCache<?>[] caches, boolean finalizer) {
        if (caches == null) {
            return 0;
        }
        int numFreed = 0;
        for (MemoryRegionCache<?> c : caches) {
            numFreed += PoolThreadCache.free(c, finalizer);
        }
        return numFreed;
    }

    private static int free(MemoryRegionCache<?> cache, boolean finalizer) {
        if (cache == null) {
            return 0;
        }
        return cache.free(finalizer);
    }

    void trim() {
        PoolThreadCache.trim(this.tinySubPageDirectCaches);
        PoolThreadCache.trim(this.smallSubPageDirectCaches);
        PoolThreadCache.trim(this.normalDirectCaches);
        PoolThreadCache.trim(this.tinySubPageHeapCaches);
        PoolThreadCache.trim(this.smallSubPageHeapCaches);
        PoolThreadCache.trim(this.normalHeapCaches);
    }

    private static void trim(MemoryRegionCache<?>[] caches) {
        if (caches == null) {
            return;
        }
        for (MemoryRegionCache<?> c : caches) {
            PoolThreadCache.trim(c);
        }
    }

    private static void trim(MemoryRegionCache<?> cache) {
        if (cache == null) {
            return;
        }
        cache.trim();
    }

    private MemoryRegionCache<?> cacheForTiny(PoolArena<?> area, int normCapacity) {
        int idx = PoolArena.tinyIdx(normCapacity);
        if (area.isDirect()) {
            return PoolThreadCache.cache(this.tinySubPageDirectCaches, idx);
        }
        return PoolThreadCache.cache(this.tinySubPageHeapCaches, idx);
    }

    private MemoryRegionCache<?> cacheForSmall(PoolArena<?> area, int normCapacity) {
        int idx = PoolArena.smallIdx(normCapacity);
        if (area.isDirect()) {
            return PoolThreadCache.cache(this.smallSubPageDirectCaches, idx);
        }
        return PoolThreadCache.cache(this.smallSubPageHeapCaches, idx);
    }

    private MemoryRegionCache<?> cacheForNormal(PoolArena<?> area, int normCapacity) {
        if (area.isDirect()) {
            int idx = PoolThreadCache.log2(normCapacity >> this.numShiftsNormalDirect);
            return PoolThreadCache.cache(this.normalDirectCaches, idx);
        }
        int idx = PoolThreadCache.log2(normCapacity >> this.numShiftsNormalHeap);
        return PoolThreadCache.cache(this.normalHeapCaches, idx);
    }

    private static <T> MemoryRegionCache<T> cache(MemoryRegionCache<T>[] cache, int idx) {
        if (cache == null || idx > cache.length - 1) {
            return null;
        }
        return cache[idx];
    }

    private static abstract class MemoryRegionCache<T> {
        private final int size;
        private final Queue<Entry<T>> queue;
        private final PoolArena.SizeClass sizeClass;
        private int allocations;
        private static final ObjectPool<Entry> RECYCLER = ObjectPool.newPool(new ObjectPool.ObjectCreator<Entry>(){

            @Override
            public Entry newObject(ObjectPool.Handle<Entry> handle) {
                return new Entry(handle);
            }
        });

        MemoryRegionCache(int size, PoolArena.SizeClass sizeClass) {
            this.size = MathUtil.safeFindNextPositivePowerOfTwo(size);
            this.queue = PlatformDependent.newFixedMpscQueue(this.size);
            this.sizeClass = sizeClass;
        }

        protected abstract void initBuf(PoolChunk<T> var1, ByteBuffer var2, long var3, PooledByteBuf<T> var5, int var6, PoolThreadCache var7);

        public final boolean add(PoolChunk<T> chunk, ByteBuffer nioBuffer, long handle) {
            Entry entry = MemoryRegionCache.newEntry(chunk, nioBuffer, handle);
            boolean queued = this.queue.offer(entry);
            if (!queued) {
                entry.recycle();
            }
            return queued;
        }

        public final boolean allocate(PooledByteBuf<T> buf, int reqCapacity, PoolThreadCache threadCache) {
            Entry<T> entry = this.queue.poll();
            if (entry == null) {
                return false;
            }
            this.initBuf(entry.chunk, entry.nioBuffer, entry.handle, buf, reqCapacity, threadCache);
            entry.recycle();
            ++this.allocations;
            return true;
        }

        public final int free(boolean finalizer) {
            return this.free(Integer.MAX_VALUE, finalizer);
        }

        private int free(int max, boolean finalizer) {
            int numFreed;
            for (numFreed = 0; numFreed < max; ++numFreed) {
                Entry<T> entry = this.queue.poll();
                if (entry == null) {
                    return numFreed;
                }
                this.freeEntry(entry, finalizer);
            }
            return numFreed;
        }

        public final void trim() {
            int free = this.size - this.allocations;
            this.allocations = 0;
            if (free > 0) {
                this.free(free, false);
            }
        }

        private void freeEntry(Entry entry, boolean finalizer) {
            PoolChunk chunk = entry.chunk;
            long handle = entry.handle;
            ByteBuffer nioBuffer = entry.nioBuffer;
            if (!finalizer) {
                entry.recycle();
            }
            chunk.arena.freeChunk(chunk, handle, this.sizeClass, nioBuffer, finalizer);
        }

        private static Entry newEntry(PoolChunk<?> chunk, ByteBuffer nioBuffer, long handle) {
            Entry entry = RECYCLER.get();
            entry.chunk = chunk;
            entry.nioBuffer = nioBuffer;
            entry.handle = handle;
            return entry;
        }

        static final class Entry<T> {
            final ObjectPool.Handle<Entry<?>> recyclerHandle;
            PoolChunk<T> chunk;
            ByteBuffer nioBuffer;
            long handle = -1L;

            Entry(ObjectPool.Handle<Entry<?>> recyclerHandle) {
                this.recyclerHandle = recyclerHandle;
            }

            void recycle() {
                this.chunk = null;
                this.nioBuffer = null;
                this.handle = -1L;
                this.recyclerHandle.recycle(this);
            }
        }
    }

    private static final class NormalMemoryRegionCache<T>
    extends MemoryRegionCache<T> {
        NormalMemoryRegionCache(int size) {
            super(size, PoolArena.SizeClass.Normal);
        }

        @Override
        protected void initBuf(PoolChunk<T> chunk, ByteBuffer nioBuffer, long handle, PooledByteBuf<T> buf, int reqCapacity, PoolThreadCache threadCache) {
            chunk.initBuf(buf, nioBuffer, handle, reqCapacity, threadCache);
        }
    }

    private static final class SubPageMemoryRegionCache<T>
    extends MemoryRegionCache<T> {
        SubPageMemoryRegionCache(int size, PoolArena.SizeClass sizeClass) {
            super(size, sizeClass);
        }

        @Override
        protected void initBuf(PoolChunk<T> chunk, ByteBuffer nioBuffer, long handle, PooledByteBuf<T> buf, int reqCapacity, PoolThreadCache threadCache) {
            chunk.initBufWithSubpage(buf, nioBuffer, handle, reqCapacity, threadCache);
        }
    }
}

