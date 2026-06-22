/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.turbo;

import java.util.LinkedHashMap;
import java.util.Map;

class LRUMessageCache
extends LinkedHashMap<String, Integer> {
    private static final long serialVersionUID = 1L;
    final int cacheSize;

    LRUMessageCache(int cacheSize) {
        super((int)((float)cacheSize * 1.3333334f), 0.75f, true);
        if (cacheSize < 1) {
            throw new IllegalArgumentException("Cache size cannot be smaller than 1");
        }
        this.cacheSize = cacheSize;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    int getMessageCountAndThenIncrement(String msg) {
        Integer i;
        if (msg == null) {
            return 0;
        }
        LRUMessageCache lRUMessageCache = this;
        synchronized (lRUMessageCache) {
            i = (Integer)super.get(msg);
            i = i == null ? Integer.valueOf(0) : Integer.valueOf(i + 1);
            super.put(msg, i);
        }
        return i;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry eldest) {
        return this.size() > this.cacheSize;
    }

    @Override
    public synchronized void clear() {
        super.clear();
    }
}

