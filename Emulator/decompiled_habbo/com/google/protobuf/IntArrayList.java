/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.AbstractProtobufList;
import com.google.protobuf.Internal;
import com.google.protobuf.PrimitiveNonBoxingCollection;
import java.util.Arrays;
import java.util.Collection;
import java.util.RandomAccess;

final class IntArrayList
extends AbstractProtobufList<Integer>
implements Internal.IntList,
RandomAccess,
PrimitiveNonBoxingCollection {
    private static final IntArrayList EMPTY_LIST = new IntArrayList(new int[0], 0);
    private int[] array;
    private int size;

    public static IntArrayList emptyList() {
        return EMPTY_LIST;
    }

    IntArrayList() {
        this(new int[10], 0);
    }

    private IntArrayList(int[] other, int size) {
        this.array = other;
        this.size = size;
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        this.ensureIsMutable();
        if (toIndex < fromIndex) {
            throw new IndexOutOfBoundsException("toIndex < fromIndex");
        }
        System.arraycopy(this.array, toIndex, this.array, fromIndex, this.size - toIndex);
        this.size -= toIndex - fromIndex;
        ++this.modCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IntArrayList)) {
            return super.equals(o);
        }
        IntArrayList other = (IntArrayList)o;
        if (this.size != other.size) {
            return false;
        }
        int[] arr = other.array;
        for (int i = 0; i < this.size; ++i) {
            if (this.array[i] == arr[i]) continue;
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = 1;
        for (int i = 0; i < this.size; ++i) {
            result = 31 * result + this.array[i];
        }
        return result;
    }

    @Override
    public Internal.IntList mutableCopyWithCapacity(int capacity) {
        if (capacity < this.size) {
            throw new IllegalArgumentException();
        }
        return new IntArrayList(Arrays.copyOf(this.array, capacity), this.size);
    }

    @Override
    public Integer get(int index) {
        return this.getInt(index);
    }

    @Override
    public int getInt(int index) {
        this.ensureIndexInRange(index);
        return this.array[index];
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public Integer set(int index, Integer element) {
        return this.setInt(index, element);
    }

    @Override
    public int setInt(int index, int element) {
        this.ensureIsMutable();
        this.ensureIndexInRange(index);
        int previousValue = this.array[index];
        this.array[index] = element;
        return previousValue;
    }

    @Override
    public boolean add(Integer element) {
        this.addInt(element);
        return true;
    }

    @Override
    public void add(int index, Integer element) {
        this.addInt(index, element);
    }

    @Override
    public void addInt(int element) {
        this.ensureIsMutable();
        if (this.size == this.array.length) {
            int length = this.size * 3 / 2 + 1;
            int[] newArray = new int[length];
            System.arraycopy(this.array, 0, newArray, 0, this.size);
            this.array = newArray;
        }
        this.array[this.size++] = element;
    }

    private void addInt(int index, int element) {
        this.ensureIsMutable();
        if (index < 0 || index > this.size) {
            throw new IndexOutOfBoundsException(this.makeOutOfBoundsExceptionMessage(index));
        }
        if (this.size < this.array.length) {
            System.arraycopy(this.array, index, this.array, index + 1, this.size - index);
        } else {
            int length = this.size * 3 / 2 + 1;
            int[] newArray = new int[length];
            System.arraycopy(this.array, 0, newArray, 0, index);
            System.arraycopy(this.array, index, newArray, index + 1, this.size - index);
            this.array = newArray;
        }
        this.array[index] = element;
        ++this.size;
        ++this.modCount;
    }

    @Override
    public boolean addAll(Collection<? extends Integer> collection) {
        this.ensureIsMutable();
        Internal.checkNotNull(collection);
        if (!(collection instanceof IntArrayList)) {
            return super.addAll(collection);
        }
        IntArrayList list = (IntArrayList)collection;
        if (list.size == 0) {
            return false;
        }
        int overflow = Integer.MAX_VALUE - this.size;
        if (overflow < list.size) {
            throw new OutOfMemoryError();
        }
        int newSize = this.size + list.size;
        if (newSize > this.array.length) {
            this.array = Arrays.copyOf(this.array, newSize);
        }
        System.arraycopy(list.array, 0, this.array, this.size, list.size);
        this.size = newSize;
        ++this.modCount;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        this.ensureIsMutable();
        for (int i = 0; i < this.size; ++i) {
            if (!o.equals(this.array[i])) continue;
            System.arraycopy(this.array, i + 1, this.array, i, this.size - i - 1);
            --this.size;
            ++this.modCount;
            return true;
        }
        return false;
    }

    @Override
    public Integer remove(int index) {
        this.ensureIsMutable();
        this.ensureIndexInRange(index);
        int value = this.array[index];
        if (index < this.size - 1) {
            System.arraycopy(this.array, index + 1, this.array, index, this.size - index - 1);
        }
        --this.size;
        ++this.modCount;
        return value;
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

