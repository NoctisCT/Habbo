/*
 * Decompiled with CFR 0.152.
 */
package gnu.trove.impl.sync;

import gnu.trove.impl.sync.TSynchronizedIntCollection;
import gnu.trove.set.TIntSet;

public class TSynchronizedIntSet
extends TSynchronizedIntCollection
implements TIntSet {
    private static final long serialVersionUID = 487447009682186044L;

    public TSynchronizedIntSet(TIntSet s) {
        super(s);
    }

    public TSynchronizedIntSet(TIntSet s, Object mutex) {
        super(s, mutex);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean equals(Object o) {
        Object object = this.mutex;
        synchronized (object) {
            return ((Object)this.c).equals(o);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int hashCode() {
        Object object = this.mutex;
        synchronized (object) {
            return ((Object)this.c).hashCode();
        }
    }
}

