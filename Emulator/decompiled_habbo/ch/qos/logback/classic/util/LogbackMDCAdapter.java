/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.slf4j.spi.MDCAdapter;

public class LogbackMDCAdapter
implements MDCAdapter {
    final ThreadLocal<Map<String, String>> copyOnThreadLocal = new ThreadLocal();
    private static final int WRITE_OPERATION = 1;
    private static final int MAP_COPY_OPERATION = 2;
    final ThreadLocal<Integer> lastOperation = new ThreadLocal();

    private Integer getAndSetLastOperation(int op) {
        Integer lastOp = this.lastOperation.get();
        this.lastOperation.set(op);
        return lastOp;
    }

    private boolean wasLastOpReadOrNull(Integer lastOp) {
        return lastOp == null || lastOp == 2;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Map<String, String> duplicateAndInsertNewMap(Map<String, String> oldMap) {
        Map<String, String> newMap = Collections.synchronizedMap(new HashMap());
        if (oldMap != null) {
            Map<String, String> map = oldMap;
            synchronized (map) {
                newMap.putAll(oldMap);
            }
        }
        this.copyOnThreadLocal.set(newMap);
        return newMap;
    }

    @Override
    public void put(String key, String val) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }
        Map<String, String> oldMap = this.copyOnThreadLocal.get();
        Integer lastOp = this.getAndSetLastOperation(1);
        if (this.wasLastOpReadOrNull(lastOp) || oldMap == null) {
            Map<String, String> newMap = this.duplicateAndInsertNewMap(oldMap);
            newMap.put(key, val);
        } else {
            oldMap.put(key, val);
        }
    }

    @Override
    public void remove(String key) {
        if (key == null) {
            return;
        }
        Map<String, String> oldMap = this.copyOnThreadLocal.get();
        if (oldMap == null) {
            return;
        }
        Integer lastOp = this.getAndSetLastOperation(1);
        if (this.wasLastOpReadOrNull(lastOp)) {
            Map<String, String> newMap = this.duplicateAndInsertNewMap(oldMap);
            newMap.remove(key);
        } else {
            oldMap.remove(key);
        }
    }

    @Override
    public void clear() {
        this.lastOperation.set(1);
        this.copyOnThreadLocal.remove();
    }

    @Override
    public String get(String key) {
        Map<String, String> map = this.copyOnThreadLocal.get();
        if (map != null && key != null) {
            return map.get(key);
        }
        return null;
    }

    public Map<String, String> getPropertyMap() {
        this.lastOperation.set(2);
        return this.copyOnThreadLocal.get();
    }

    public Set<String> getKeys() {
        Map<String, String> map = this.getPropertyMap();
        if (map != null) {
            return map.keySet();
        }
        return null;
    }

    @Override
    public Map<String, String> getCopyOfContextMap() {
        Map<String, String> hashMap = this.copyOnThreadLocal.get();
        if (hashMap == null) {
            return null;
        }
        return new HashMap<String, String>(hashMap);
    }

    @Override
    public void setContextMap(Map<String, String> contextMap) {
        this.lastOperation.set(1);
        Map<String, String> newMap = Collections.synchronizedMap(new HashMap());
        newMap.putAll(contextMap);
        this.copyOnThreadLocal.set(newMap);
    }
}

