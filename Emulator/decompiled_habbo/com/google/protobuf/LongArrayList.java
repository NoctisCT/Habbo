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

final class LongArrayList
extends AbstractProtobufList<Long>
implements Internal.LongList,
RandomAccess,
PrimitiveNonBoxingCollection {
    private static final LongArrayList EMPTY_LIST = new LongArrayList(new long[0], 0);
    private long[] array;
    private int size;

    public static LongArrayList emptyList() {
        return EMPTY_LIST;
    }

    LongArrayList() {
        this(new long[10], 0);
    }

    private LongArrayList(long[] other, int size) {
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
        if (!(o instanceof LongArrayList)) {
            return super.equals(o);
        }
        LongArrayList other = (LongArrayList)o;
        if (this.size != other.size) {
            return false;
        }
        long[] arr = other.array;
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
            result = 31 * result + Internal.hashLong(this.array[i]);
        }
        return result;
    }

    @Override
    public Internal.LongList mutableCopyWithCapacity(int capacity) {
        if (capacity < this.size) {
            throw new IllegalArgumentException();
        }
        return new LongArrayList(Arrays.copyOf(this.array, capacity), this.size);
    }

    @Override
    public Long get(int index) {
        return this.getLong(index);
    }

    @Override
    public long getLong(int index) {
        this.ensureIndexInRange(index);
        return this.array[index];
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public Long set(int index, Long element) {
        return this.setLong(index, element);
    }

    @Override
    public long setLong(int index, long element) {
        this.ensureIsMutable();
        this.ensureIndexInRange(index);
        long previousValue = this.array[index];
        this.array[index] = element;
        return previousValue;
    }

    @Override
    public boolean add(Long element) {
        this.addLong(element);
        return true;
    }

    @Override
    public void add(int index, Long element) {
        this.addLong(index, element);
    }

    @Override
    public void addLong(long element) {
        this.ensureIsMutable();
        if (this.size == this.array.length) {
            int length = this.size * 3 / 2 + 1;
            long[] newArray = new long[length];
            System.arraycopy(this.array, 0, newArray, 0, this.size);
            this.array = newArray;
        }
        this.array[this.size++] = element;
    }

    private void addLong(int index, long element) {
        this.ensureIsMutable();
        if (index < 0 || index > this.size) {
            throw new IndexOutOfBoundsException(this.makeOutOfBoundsExceptionMessage(index));
        }
        if (this.size < this.array.length) {
            System.arraycopy(this.array, index, this.array, index + 1, this.size - index);
        } else {
            int length = this.size * 3 / 2 + 1;
            long[] newArray = new long[length];
            System.arraycopy(this.array, 0, newArray, 0, index);
            System.arraycopy(this.array, index, newArray, index + 1, this.size - index);
            this.array = newArray;
        }
        this.array[index] = element;
        ++this.size;
        ++this.modCount;
    }

    @Override
    public boolean addAll(Collection<? extends Long> collection) {
        this.ensureIsMutable();
        Internal.checkNotNull(collection);
        if (!(collection instanceof LongArrayList)) {
            return super.addAll(collection);
        }
        LongArrayList list = (LongArrayList)collection;
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
    public Long remove(int index) {
        this.ensureIsMutable();
        this.ensureIndexInRange(index);
        long value = this.array[index];
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

