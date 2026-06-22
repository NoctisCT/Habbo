/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util;

import io.netty.util.ReferenceCounted;
import io.netty.util.internal.ReferenceCountUpdater;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public abstract class AbstractReferenceCounted
implements ReferenceCounted {
    private static final long REFCNT_FIELD_OFFSET = ReferenceCountUpdater.getUnsafeOffset(AbstractReferenceCounted.class, "refCnt");
    private static final AtomicIntegerFieldUpdater<AbstractReferenceCounted> AIF_UPDATER = AtomicIntegerFieldUpdater.newUpdater(AbstractReferenceCounted.class, "refCnt");
    private static final ReferenceCountUpdater<AbstractReferenceCounted> updater = new ReferenceCountUpdater<AbstractReferenceCounted>(){

        @Override
        protected AtomicIntegerFieldUpdater<AbstractReferenceCounted> updater() {
            return AIF_UPDATER;
        }

        @Override
        protected long unsafeOffset() {
            return REFCNT_FIELD_OFFSET;
        }
    };
    private volatile int refCnt = updater.initialValue();

    @Override
    public int refCnt() {
        return updater.refCnt(this);
    }

    protected final void setRefCnt(int refCnt) {
        updater.setRefCnt(this, refCnt);
    }

    @Override
    public ReferenceCounted retain() {
        return updater.retain(this);
    }

    @Override
    public ReferenceCounted retain(int increment) {
        return updater.retain(this, increment);
    }

    @Override
    public ReferenceCounted touch() {
        return this.touch(null);
    }

    @Override
    public boolean release() {
        return this.handleRelease(updater.release(this));
    }

    @Override
    public boolean release(int decrement) {
        return this.handleRelease(updater.release(this, decrement));
    }

    private boolean handleRelease(boolean result) {
        if (result) {
            this.deallocate();
        }
        return result;
    }

    protected abstract void deallocate();
}

