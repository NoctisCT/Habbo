/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util;

import io.netty.util.Constant;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class ConstantPool<T extends Constant<T>> {
    private final ConcurrentMap<String, T> constants = PlatformDependent.newConcurrentHashMap();
    private final AtomicInteger nextId = new AtomicInteger(1);

    public T valueOf(Class<?> firstNameComponent, String secondNameComponent) {
        return this.valueOf(ObjectUtil.checkNotNull(firstNameComponent, "firstNameComponent").getName() + '#' + ObjectUtil.checkNotNull(secondNameComponent, "secondNameComponent"));
    }

    public T valueOf(String name) {
        ConstantPool.checkNotNullAndNotEmpty(name);
        return this.getOrCreate(name);
    }

    private T getOrCreate(String name) {
        T tempConstant;
        Constant constant = (Constant)this.constants.get(name);
        if (constant == null && (constant = (Constant)this.constants.putIfAbsent(name, tempConstant = this.newConstant(this.nextId(), name))) == null) {
            return tempConstant;
        }
        return (T)constant;
    }

    public boolean exists(String name) {
        ConstantPool.checkNotNullAndNotEmpty(name);
        return this.constants.containsKey(name);
    }

    public T newInstance(String name) {
        ConstantPool.checkNotNullAndNotEmpty(name);
        return this.createOrThrow(name);
    }

    private T createOrThrow(String name) {
        T tempConstant;
        Constant constant = (Constant)this.constants.get(name);
        if (constant == null && (constant = (Constant)this.constants.putIfAbsent(name, tempConstant = this.newConstant(this.nextId(), name))) == null) {
            return tempConstant;
        }
        throw new IllegalArgumentException(String.format("'%s' is already in use", name));
    }

    private static String checkNotNullAndNotEmpty(String name) {
        ObjectUtil.checkNotNull(name, "name");
        if (name.isEmpty()) {
            throw new IllegalArgumentException("empty name");
        }
        return name;
    }

    protected abstract T newConstant(int var1, String var2);

    @Deprecated
    public final int nextId() {
        return this.nextId.getAndIncrement();
    }
}

