/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.Internal;
import com.google.protobuf.LazyStringArrayList;
import com.google.protobuf.LazyStringList;
import com.google.protobuf.PrimitiveNonBoxingCollection;
import com.google.protobuf.UnmodifiableLazyStringList;
import com.google.protobuf.UnsafeUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

abstract class ListFieldSchema {
    private static final ListFieldSchema FULL_INSTANCE = new ListFieldSchemaFull();
    private static final ListFieldSchema LITE_INSTANCE = new ListFieldSchemaLite();

    private ListFieldSchema() {
    }

    abstract <L> List<L> mutableListAt(Object var1, long var2);

    abstract void makeImmutableListAt(Object var1, long var2);

    abstract <L> void mergeListsAt(Object var1, Object var2, long var3);

    static ListFieldSchema full() {
        return FULL_INSTANCE;
    }

    static ListFieldSchema lite() {
        return LITE_INSTANCE;
    }

    private static final class ListFieldSchemaLite
    extends ListFieldSchema {
        private ListFieldSchemaLite() {
        }

        @Override
        <L> List<L> mutableListAt(Object message, long offset) {
            Internal.ProtobufList list = ListFieldSchemaLite.getProtobufList(message, offset);
            if (!list.isModifiable()) {
                int size = list.size();
                list = list.mutableCopyWithCapacity(size == 0 ? 10 : size * 2);
                UnsafeUtil.putObject(message, offset, list);
            }
            return list;
        }

        @Override
        void makeImmutableListAt(Object message, long offset) {
            Internal.ProtobufList list = ListFieldSchemaLite.getProtobufList(message, offset);
            list.makeImmutable();
        }

        @Override
        <E> void mergeListsAt(Object msg, Object otherMsg, long offset) {
            Internal.ProtobufList<E> mine = ListFieldSchemaLite.getProtobufList(msg, offset);
            Internal.ProtobufList<E> other = ListFieldSchemaLite.getProtobufList(otherMsg, offset);
            int size = mine.size();
            int otherSize = other.size();
            if (size > 0 && otherSize > 0) {
                if (!mine.isModifiable()) {
                    mine = mine.mutableCopyWithCapacity(size + otherSize);
                }
                mine.addAll(other);
            }
            Internal.ProtobufList<E> merged = size > 0 ? mine : other;
            UnsafeUtil.putObject(msg, offset, merged);
        }

        static <E> Internal.ProtobufList<E> getProtobufList(Object message, long offset) {
            return (Internal.ProtobufList)UnsafeUtil.getObject(message, offset);
        }
    }

    private static final class ListFieldSchemaFull
    extends ListFieldSchema {
        private static final Class<?> UNMODIFIABLE_LIST_CLASS = Collections.unmodifiableList(Collections.emptyList()).getClass();

        private ListFieldSchemaFull() {
        }

        @Override
        <L> List<L> mutableListAt(Object message, long offset) {
            return ListFieldSchemaFull.mutableListAt(message, offset, 10);
        }

        @Override
        void makeImmutableListAt(Object message, long offset) {
            List list = (List)UnsafeUtil.getObject(message, offset);
            List<String> immutable = null;
            if (list instanceof LazyStringList) {
                immutable = ((LazyStringList)list).getUnmodifiableView();
            } else {
                if (UNMODIFIABLE_LIST_CLASS.isAssignableFrom(list.getClass())) {
                    return;
                }
                if (list instanceof PrimitiveNonBoxingCollection && list instanceof Internal.ProtobufList) {
                    if (((Internal.ProtobufList)list).isModifiable()) {
                        ((Internal.ProtobufList)list).makeImmutable();
                    }
                    return;
                }
                immutable = Collections.unmodifiableList(list);
            }
            UnsafeUtil.putObject(message, offset, (Object)immutable);
        }

        private static <L> List<L> mutableListAt(Object message, long offset, int additionalCapacity) {
            List<String> list = ListFieldSchemaFull.getList(message, offset);
            if (list.isEmpty()) {
                list = list instanceof LazyStringList ? new LazyStringArrayList(additionalCapacity) : (list instanceof PrimitiveNonBoxingCollection && list instanceof Internal.ProtobufList ? ((Internal.ProtobufList)list).mutableCopyWithCapacity(additionalCapacity) : new ArrayList(additionalCapacity));
                UnsafeUtil.putObject(message, offset, (Object)list);
            } else if (UNMODIFIABLE_LIST_CLASS.isAssignableFrom(list.getClass())) {
                ArrayList<String> newList = new ArrayList<String>(list.size() + additionalCapacity);
                newList.addAll(list);
                list = newList;
                UnsafeUtil.putObject(message, offset, list);
            } else if (list instanceof UnmodifiableLazyStringList) {
                LazyStringArrayList newList = new LazyStringArrayList(list.size() + additionalCapacity);
                newList.addAll((UnmodifiableLazyStringList)list);
                list = newList;
                UnsafeUtil.putObject(message, offset, list);
            } else if (list instanceof PrimitiveNonBoxingCollection && list instanceof Internal.ProtobufList && !((Internal.ProtobufList)list).isModifiable()) {
                list = ((Internal.ProtobufList)list).mutableCopyWithCapacity(list.size() + additionalCapacity);
                UnsafeUtil.putObject(message, offset, list);
            }
            return list;
        }

        @Override
        <E> void mergeListsAt(Object msg, Object otherMsg, long offset) {
            List<E> other = ListFieldSchemaFull.getList(otherMsg, offset);
            List mine = ListFieldSchemaFull.mutableListAt(msg, offset, other.size());
            int size = mine.size();
            int otherSize = other.size();
            if (size > 0 && otherSize > 0) {
                mine.addAll(other);
            }
            List<Object> merged = size > 0 ? mine : other;
            UnsafeUtil.putObject(msg, offset, merged);
        }

        static <E> List<E> getList(Object message, long offset) {
            return (List)UnsafeUtil.getObject(message, offset);
        }
    }
}

