/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.AbstractProtobufList;
import java.util.Arrays;
import java.util.RandomAccess;

final class ProtobufArrayList<E>
extends AbstractProtobufList<E>
implements RandomAccess {
    private static final ProtobufArrayList<Object> EMPTY_LIST = new ProtobufArrayList<Object>(new Object[0], 0);
    private E[] array;
    private int size;

    public static <E> ProtobufArrayList<E> emptyList() {
        return EMPTY_LIST;
    }

    ProtobufArrayList() {
        this(new Object[10], 0);
    }

    private ProtobufArrayList(E[] array, int size) {
        this.array = array;
        this.size = size;
    }

    @Override
    public ProtobufArrayList<E> mutableCopyWithCapacity(int capacity) {
        if (capacity < this.size) {
            throw new IllegalArgumentException();
        }
        E[] newArray = Arrays.copyOf(this.array, capacity);
        return new ProtobufArrayList<E>(newArray, this.size);
    }

    @Override
    public boolean add(E element) {
        this.ensureIsMutable();
        if (this.size == this.array.length) {
            int length = this.size * 3 / 2 + 1;
            E[] newArray = Arrays.copyOf(this.array, length);
            this.array = newArray;
        }
        this.array[this.size++] = element;
        ++this.modCount;
        return true;
    }

    @Override
    public void add(int index, E element) {
        this.ensureIsMutable();
        if (index < 0 || index > this.size) {
            throw new IndexOutOfBoundsException(this.makeOutOfBoundsExceptionMessage(index));
        }
        if (this.size < this.array.length) {
            System.arraycopy(this.array, index, this.array, index + 1, this.size - index);
        } else {
            int length = this.size * 3 / 2 + 1;
            E[] newArray = ProtobufArrayList.createArray(length);
            System.arraycopy(this.array, 0, newArray, 0, index);
            System.arraycopy(this.array, index, newArray, index + 1, this.size - index);
            this.array = newArray;
        }
        this.array[index] = element;
        ++this.size;
        ++this.modCount;
    }

    @Override
    public E get(int index) {
        this.ensureIndexInRange(index);
        return this.array[index];
    }

    @Override
    public E remove(int index) {
        this.ensureIsMutable();
        this.ensureIndexInRange(index);
        E value = this.array[index];
        if (index < this.size - 1) {
            System.arraycopy(this.array, index + 1, this.array, index, this.size - index - 1);
        }
        --this.size;
        ++this.modCount;
        return value;
    }

    @Override
    public E set(int index, E element) {
        this.ensureIsMutable();
        this.ensureIndexInRange(index);
        E toReturn = this.array[index];
        this.array[index] = element;
        ++this.modCount;
        return toReturn;
    }

    @Override
    public int size() {
        return this.size;
    }

    private static <E> E[] createArray(int capacity) {
        return new Object[capacity];
    }

    private void ensureIndexInRange(int index) {
        if (index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException(this.makeOutOfBoundsExceptionMessage(index));
        }
    }

    private String makeOutOfBoundsExceptionMessage(int index) {
        return "Index:" + index + ", Size:" + this.size;
    }

    static {
        EMPTY_LIST.makeImmutable();
    }
}

