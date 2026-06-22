/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.Internal;
import com.google.protobuf.LazyField;
import com.google.protobuf.MessageLite;
import com.google.protobuf.SmallSortedMap;
import com.google.protobuf.WireFormat;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

final class FieldSet<T extends FieldDescriptorLite<T>> {
    private static final int DEFAULT_FIELD_MAP_ARRAY_SIZE = 16;
    private final SmallSortedMap<T, Object> fields;
    private boolean isImmutable;
    private boolean hasLazyField;
    private static final FieldSet DEFAULT_INSTANCE = new FieldSet(true);

    private FieldSet() {
        this.fields = SmallSortedMap.newFieldMap(16);
    }

    private FieldSet(boolean dummy) {
        this(SmallSortedMap.newFieldMap(0));
        this.makeImmutable();
    }

    private FieldSet(SmallSortedMap<T, Object> fields) {
        this.fields = fields;
        this.makeImmutable();
    }

    public static <T extends FieldDescriptorLite<T>> FieldSet<T> newFieldSet() {
        return new FieldSet<T>();
    }

    public static <T extends FieldDescriptorLite<T>> FieldSet<T> emptySet() {
        return DEFAULT_INSTANCE;
    }

    public static <T extends FieldDescriptorLite<T>> Builder<T> newBuilder() {
        return new Builder();
    }

    boolean isEmpty() {
        return this.fields.isEmpty();
    }

    public void makeImmutable() {
        if (this.isImmutable) {
            return;
        }
        this.fields.makeImmutable();
        this.isImmutable = true;
    }

    public boolean isImmutable() {
        return this.isImmutable;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FieldSet)) {
            return false;
        }
        FieldSet other = (FieldSet)o;
        return this.fields.equals(other.fields);
    }

    public int hashCode() {
        return this.fields.hashCode();
    }

    public FieldSet<T> clone() {
        FieldSet<FieldDescriptorLite> clone = FieldSet.newFieldSet();
        for (int i = 0; i < this.fields.getNumArrayEntries(); ++i) {
            Map.Entry<T, Object> entry = this.fields.getArrayEntryAt(i);
            clone.setField((FieldDescriptorLite)entry.getKey(), entry.getValue());
        }
        for (Map.Entry<T, Object> entry : this.fields.getOverflowEntries()) {
            clone.setField((FieldDescriptorLite)entry.getKey(), entry.getValue());
        }
        clone.hasLazyField = this.hasLazyField;
        return clone;
    }

    public void clear() {
        this.fields.clear();
        this.hasLazyField = false;
    }

    public Map<T, Object> getAllFields() {
        if (this.hasLazyField) {
            SmallSortedMap<T, Object> result = FieldSet.cloneAllFieldsMap(this.fields, false);
            if (this.fields.isImmutable()) {
                result.makeImmutable();
            }
            return result;
        }
        return this.fields.isImmutable() ? this.fields : Collections.unmodifiableMap(this.fields);
    }

    private static <T extends FieldDescriptorLite<T>> SmallSortedMap<T, Object> cloneAllFieldsMap(SmallSortedMap<T, Object> fields, boolean copyList) {
        SmallSortedMap result = SmallSortedMap.newFieldMap(16);
        for (int i = 0; i < fields.getNumArrayEntries(); ++i) {
            FieldSet.cloneFieldEntry(result, fields.getArrayEntryAt(i), copyList);
        }
        for (Map.Entry<T, Object> entry : fields.getOverflowEntries()) {
            FieldSet.cloneFieldEntry(result, entry, copyList);
        }
        return result;
    }

    private static <T extends FieldDescriptorLite<T>> void cloneFieldEntry(Map<T, Object> map, Map.Entry<T, Object> entry, boolean copyList) {
        FieldDescriptorLite key = (FieldDescriptorLite)entry.getKey();
        Object value = entry.getValue();
        if (value instanceof LazyField) {
            map.put(key, ((LazyField)value).getValue());
        } else if (copyList && value instanceof List) {
            map.put(key, new ArrayList((List)value));
        } else {
            map.put(key, value);
        }
    }

    public Iterator<Map.Entry<T, Object>> iterator() {
        if (this.hasLazyField) {
            return new LazyField.LazyIterator(this.fields.entrySet().iterator());
        }
        return this.fields.entrySet().iterator();
    }

    Iterator<Map.Entry<T, Object>> descendingIterator() {
        if (this.hasLazyField) {
            return new LazyField.LazyIterator(this.fields.descendingEntrySet().iterator());
        }
        return this.fields.descendingEntrySet().iterator();
    }

    public boolean hasField(T descriptor) {
        if (descriptor.isRepeated()) {
            throw new IllegalArgumentException("hasField() can only be called on non-repeated fields.");
        }
        return this.fields.get(descriptor) != null;
    }

    public Object getField(T descriptor) {
        Object o = this.fields.get(descriptor);
        if (o instanceof LazyField) {
            return ((LazyField)o).getValue();
        }
        return o;
    }

    public void setField(T descriptor, Object value) {
        if (descriptor.isRepeated()) {
            if (!(value instanceof List)) {
                throw new IllegalArgumentException("Wrong object type used with protocol message reflection.");
            }
            ArrayList newList = new ArrayList();
            newList.addAll(value);
            for (Object element : newList) {
                this.verifyType(descriptor.getLiteType(), element);
            }
            value = newList;
        } else {
            this.verifyType(descriptor.getLiteType(), value);
        }
        if (value instanceof LazyField) {
            this.hasLazyField = true;
        }
        this.fields.put(descriptor, (Object)value);
    }

    public void clearField(T descriptor) {
        this.fields.remove(descriptor);
        if (this.fields.isEmpty()) {
            this.hasLazyField = false;
        }
    }

    public int getRepeatedFieldCount(T descriptor) {
        if (!descriptor.isRepeated()) {
            throw new IllegalArgumentException("getRepeatedField() can only be called on repeated fields.");
        }
        Object value = this.getField(descriptor);
        if (value == null) {
            return 0;
        }
        return ((List)value).size();
    }

    public Object getRepeatedField(T descriptor, int index) {
        if (!descriptor.isRepeated()) {
            throw new IllegalArgumentException("getRepeatedField() can only be called on repeated fields.");
        }
        Object value = this.getField(descriptor);
        if (value == null) {
            throw new IndexOutOfBoundsException();
        }
        return ((List)value).get(index);
    }

    public void setRepeatedField(T descriptor, int index, Object value) {
        if (!descriptor.isRepeated()) {
            throw new IllegalArgumentException("getRepeatedField() can only be called on repeated fields.");
        }
        Object list = this.getField(descriptor);
        if (list == null) {
            throw new IndexOutOfBoundsException();
        }
        this.verifyType(descriptor.getLiteType(), value);
        ((List)list).set(index, value);
    }

    public void addRepeatedField(T descriptor, Object value) {
        ArrayList<Object> list;
        if (!descriptor.isRepeated()) {
            throw new IllegalArgumentException("addRepeatedField() can only be called on repeated fields.");
        }
        this.verifyType(descriptor.getLiteType(), value);
        Object existingValue = this.getField(descriptor);
        if (existingValue == null) {
            list = new ArrayList<Object>();
            this.fields.put(descriptor, (Object)list);
        } else {
            list = (ArrayList<Object>)existingValue;
        }
        list.add(value);
    }

    private void verifyType(WireFormat.FieldType type, Object value) {
        if (!FieldSet.isValidType(type, value)) {
            throw new IllegalArgumentException("Wrong object type used with protocol message reflection.");
        }
    }

    private static boolean isValidType(WireFormat.FieldType type, Object value) {
        Internal.checkNotNull(value);
        switch (type.getJavaType()) {
            case INT: {
                return value instanceof Integer;
            }
            case LONG: {
                return value instanceof Long;
            }
            case FLOAT: {
                return value instanceof Float;
            }
            case DOUBLE: {
                return value instanceof Double;
            }
            case BOOLEAN: {
                return value instanceof Boolean;
            }
            case STRING: {
                return value instanceof String;
            }
            case BYTE_STRING: {
                return value instanceof ByteString || value instanceof byte[];
            }
            case ENUM: {
                return value instanceof Integer || value instanceof Internal.EnumLite;
            }
            case MESSAGE: {
                return value instanceof MessageLite || value instanceof LazyField;
            }
        }
        return false;
    }

    public boolean isInitialized() {
        for (int i = 0; i < this.fields.getNumArrayEntries(); ++i) {
            if (FieldSet.isInitialized(this.fields.getArrayEntryAt(i))) continue;
            return false;
        }
        for (Map.Entry<T, Object> entry : this.fields.getOverflowEntries()) {
            if (FieldSet.isInitialized(entry)) continue;
            return false;
        }
        return true;
    }

    private static <T extends FieldDescriptorLite<T>> boolean isInitialized(Map.Entry<T, Object> entry) {
        FieldDescriptorLite descriptor = (FieldDescriptorLite)entry.getKey();
        if (descriptor.getLiteJavaType() == WireFormat.JavaType.MESSAGE) {
            if (descriptor.isRepeated()) {
                for (MessageLite element : (List)entry.getValue()) {
                    if (element.isInitialized()) continue;
                    return false;
                }
            } else {
                Object value = entry.getValue();
                if (value instanceof MessageLite) {
                    if (!((MessageLite)value).isInitialized()) {
                        return false;
                    }
                } else {
                    if (value instanceof LazyField) {
                        return true;
                    }
                    throw new IllegalArgumentException("Wrong object type used with protocol message reflection.");
                }
            }
        }
        return true;
    }

    static int getWireFormatForFieldType(WireFormat.FieldType type, boolean isPacked) {
        if (isPacked) {
            return 2;
        }
        return type.getWireType();
    }

    public void mergeFrom(FieldSet<T> other) {
        for (int i = 0; i < other.fields.getNumArrayEntries(); ++i) {
            this.mergeFromField(other.fields.getArrayEntryAt(i));
        }
        for (Map.Entry<T, Object> entry : other.fields.getOverflowEntries()) {
            this.mergeFromField(entry);
        }
    }

    private static Object cloneIfMutable(Object value) {
        if (value instanceof byte[]) {
            byte[] bytes = (byte[])value;
            byte[] copy = new byte[bytes.length];
            System.arraycopy(bytes, 0, copy, 0, bytes.length);
            return copy;
        }
        return value;
    }

    private void mergeFromField(Map.Entry<T, Object> entry) {
        FieldDescriptorLite descriptor = (FieldDescriptorLite)entry.getKey();
        Object otherValue = entry.getValue();
        if (otherValue instanceof LazyField) {
            otherValue = ((LazyField)otherValue).getValue();
        }
        if (descriptor.isRepeated()) {
            ArrayList value = this.getField(descriptor);
            if (value == null) {
                value = new ArrayList();
            }
            for (Object element : (List)otherValue) {
                ((List)value).add(FieldSet.cloneIfMutable(element));
            }
            this.fields.put((T)descriptor, (Object)value);
        } else if (descriptor.getLiteJavaType() == WireFormat.JavaType.MESSAGE) {
            Object value = this.getField(descriptor);
            if (value == null) {
                this.fields.put((T)descriptor, FieldSet.cloneIfMutable(otherValue));
            } else {
                value = descriptor.internalMergeFrom(((MessageLite)value).toBuilder(), (MessageLite)otherValue).build();
                this.fields.put((T)descriptor, value);
            }
        } else {
            this.fields.put((T)descriptor, FieldSet.cloneIfMutable(otherValue));
        }
    }

    public static Object readPrimitiveField(CodedInputStream input, WireFormat.FieldType type, boolean checkUtf8) throws IOException {
        if (checkUtf8) {
            return WireFormat.readPrimitiveField(input, type, WireFormat.Utf8Validation.STRICT);
        }
        return WireFormat.readPrimitiveField(input, type, WireFormat.Utf8Validation.LOOSE);
    }

    public void writeTo(CodedOutputStream output) throws IOException {
        for (int i = 0; i < this.fields.getNumArrayEntries(); ++i) {
            Map.Entry<T, Object> entry = this.fields.getArrayEntryAt(i);
            FieldSet.writeField((FieldDescriptorLite)entry.getKey(), entry.getValue(), output);
        }
        for (Map.Entry<T, Object> entry : this.fields.getOverflowEntries()) {
            FieldSet.writeField((FieldDescriptorLite)entry.getKey(), entry.getValue(), output);
        }
    }

    public void writeMessageSetTo(CodedOutputStream output) throws IOException {
        for (int i = 0; i < this.fields.getNumArrayEntries(); ++i) {
            this.writeMessageSetTo(this.fields.getArrayEntryAt(i), output);
        }
        for (Map.Entry<T, Object> entry : this.fields.getOverflowEntries()) {
            this.writeMessageSetTo(entry, output);
        }
    }

    private void writeMessageSetTo(Map.Entry<T, Object> entry, CodedOutputStream output) throws IOException {
        FieldDescriptorLite descriptor = (FieldDescriptorLite)entry.getKey();
        if (descriptor.getLiteJavaType() == WireFormat.JavaType.MESSAGE && !descriptor.isRepeated() && !descriptor.isPacked()) {
            Object value = entry.getValue();
            if (value instanceof LazyField) {
                value = ((LazyField)value).getValue();
            }
            output.writeMessageSetExtension(((FieldDescriptorLite)entry.getKey()).getNumber(), (MessageLite)value);
        } else {
            FieldSet.writeField(descriptor, entry.getValue(), output);
        }
    }

    static void writeElement(CodedOutputStream output, WireFormat.FieldType type, int number, Object value) throws IOException {
        if (type == WireFormat.FieldType.GROUP) {
            output.writeGroup(number, (MessageLite)value);
        } else {
            output.writeTag(number, FieldSet.getWireFormatForFieldType(type, false));
            FieldSet.writeElementNoTag(output, type, value);
        }
    }

    static void writeElementNoTag(CodedOutputStream output, WireFormat.FieldType type, Object value) throws IOException {
        switch (type) {
            case DOUBLE: {
                output.writeDoubleNoTag((Double)value);
                break;
            }
            case FLOAT: {
                output.writeFloatNoTag(((Float)value).floatValue());
                break;
            }
            case INT64: {
                output.writeInt64NoTag((Long)value);
                break;
            }
            case UINT64: {
                output.writeUInt64NoTag((Long)value);
                break;
            }
            case INT32: {
                output.writeInt32NoTag((Integer)value);
                break;
            }
            case FIXED64: {
                output.writeFixed64NoTag((Long)value);
                break;
            }
            case FIXED32: {
                output.writeFixed32NoTag((Integer)value);
                break;
            }
            case BOOL: {
                output.writeBoolNoTag((Boolean)value);
                break;
            }
            case GROUP: {
                output.writeGroupNoTag((MessageLite)value);
                break;
            }
            case MESSAGE: {
                output.writeMessageNoTag((MessageLite)value);
                break;
            }
            case STRING: {
                if (value instanceof ByteString) {
                    output.writeBytesNoTag((ByteString)value);
                    break;
                }
                output.writeStringNoTag((String)value);
                break;
            }
            case BYTES: {
                if (value instanceof ByteString) {
                    output.writeBytesNoTag((ByteString)value);
                    break;
                }
                output.writeByteArrayNoTag((byte[])value);
                break;
            }
            case UINT32: {
                output.writeUInt32NoTag((Integer)value);
                break;
            }
            case SFIXED32: {
                output.writeSFixed32NoTag((Integer)value);
                break;
            }
            case SFIXED64: {
                output.writeSFixed64NoTag((Long)value);
                break;
            }
            case SINT32: {
                output.writeSInt32NoTag((Integer)value);
                break;
            }
            case SINT64: {
                output.writeSInt64NoTag((Long)value);
                break;
            }
            case ENUM: {
                if (value instanceof Internal.EnumLite) {
                    output.writeEnumNoTag(((Internal.EnumLite)value).getNumber());
                    break;
                }
                output.writeEnumNoTag((Integer)value);
            }
        }
    }

    public static void writeField(FieldDescriptorLite<?> descriptor, Object value, CodedOutputStream output) throws IOException {
        WireFormat.FieldType type = descriptor.getLiteType();
        int number = descriptor.getNumber();
        if (descriptor.isRepeated()) {
            List valueList = (List)value;
            if (descriptor.isPacked()) {
                output.writeTag(number, 2);
                int dataSize = 0;
                for (Object element : valueList) {
                    dataSize += FieldSet.computeElementSizeNoTag(type, element);
                }
                output.writeRawVarint32(dataSize);
                for (Object element : valueList) {
                    FieldSet.writeElementNoTag(output, type, element);
                }
            } else {
                for (Object element : valueList) {
                    FieldSet.writeElement(output, type, number, element);
                }
            }
        } else if (value instanceof LazyField) {
            FieldSet.writeElement(output, type, number, ((LazyField)value).getValue());
        } else {
            FieldSet.writeElement(output, type, number, value);
        }
    }

    public int getSerializedSize() {
        int size = 0;
        for (int i = 0; i < this.fields.getNumArrayEntries(); ++i) {
            Map.Entry<T, Object> entry = this.fields.getArrayEntryAt(i);
            size += FieldSet.computeFieldSize((FieldDescriptorLite)entry.getKey(), entry.getValue());
        }
        for (Map.Entry<T, Object> entry : this.fields.getOverflowEntries()) {
            size += FieldSet.computeFieldSize((FieldDescriptorLite)entry.getKey(), entry.getValue());
        }
        return size;
    }

    public int getMessageSetSerializedSize() {
        int size = 0;
        for (int i = 0; i < this.fields.getNumArrayEntries(); ++i) {
            size += this.getMessageSetSerializedSize(this.fields.getArrayEntryAt(i));
        }
        for (Map.Entry<T, Object> entry : this.fields.getOverflowEntries()) {
            size += this.getMessageSetSerializedSize(entry);
        }
        return size;
    }

    private int getMessageSetSerializedSize(Map.Entry<T, Object> entry) {
        FieldDescriptorLite descriptor = (FieldDescriptorLite)entry.getKey();
        Object value = entry.getValue();
        if (descriptor.getLiteJavaType() == WireFormat.JavaType.MESSAGE && !descriptor.isRepeated() && !descriptor.isPacked()) {
            if (value instanceof LazyField) {
                return CodedOutputStream.computeLazyFieldMessageSetExtensionSize(((FieldDescriptorLite)entry.getKey()).getNumber(), (LazyField)value);
            }
            return CodedOutputStream.computeMessageSetExtensionSize(((FieldDescriptorLite)entry.getKey()).getNumber(), (MessageLite)value);
        }
        return FieldSet.computeFieldSize(descriptor, value);
    }

    static int computeElementSize(WireFormat.FieldType type, int number, Object value) {
        int tagSize = CodedOutputStream.computeTagSize(number);
        if (type == WireFormat.FieldType.GROUP) {
            tagSize *= 2;
        }
        return tagSize + FieldSet.computeElementSizeNoTag(type, value);
    }

    static int computeElementSizeNoTag(WireFormat.FieldType type, Object value) {
        switch (type) {
            case DOUBLE: {
                return CodedOutputStream.computeDoubleSizeNoTag((Double)value);
            }
            case FLOAT: {
                return CodedOutputStream.computeFloatSizeNoTag(((Float)value).floatValue());
            }
            case INT64: {
                return CodedOutputStream.computeInt64SizeNoTag((Long)value);
            }
            case UINT64: {
                return CodedOutputStream.computeUInt64SizeNoTag((Long)value);
            }
            case INT32: {
                return CodedOutputStream.computeInt32SizeNoTag((Integer)value);
            }
            case FIXED64: {
                return CodedOutputStream.computeFixed64SizeNoTag((Long)value);
            }
            case FIXED32: {
                return CodedOutputStream.computeFixed32SizeNoTag((Integer)value);
            }
            case BOOL: {
                return CodedOutputStream.computeBoolSizeNoTag((Boolean)value);
            }
            case GROUP: {
                return CodedOutputStream.computeGroupSizeNoTag((MessageLite)value);
            }
            case BYTES: {
                if (value instanceof ByteString) {
                    return CodedOutputStream.computeBytesSizeNoTag((ByteString)value);
                }
                return CodedOutputStream.computeByteArraySizeNoTag((byte[])value);
            }
            case STRING: {
                if (value instanceof ByteString) {
                    return CodedOutputStream.computeBytesSizeNoTag((ByteString)value);
                }
                return CodedOutputStream.computeStringSizeNoTag((String)value);
            }
            case UINT32: {
                return CodedOutputStream.computeUInt32SizeNoTag((Integer)value);
            }
            case SFIXED32: {
                return CodedOutputStream.computeSFixed32SizeNoTag((Integer)value);
            }
            case SFIXED64: {
                return CodedOutputStream.computeSFixed64SizeNoTag((Long)value);
            }
            case SINT32: {
                return CodedOutputStream.computeSInt32SizeNoTag((Integer)value);
            }
            case SINT64: {
                return CodedOutputStream.computeSInt64SizeNoTag((Long)value);
            }
            case MESSAGE: {
                if (value instanceof LazyField) {
                    return CodedOutputStream.computeLazyFieldSizeNoTag((LazyField)value);
                }
                return CodedOutputStream.computeMessageSizeNoTag((MessageLite)value);
            }
            case ENUM: {
                if (value instanceof Internal.EnumLite) {
                    return CodedOutputStream.computeEnumSizeNoTag(((Internal.EnumLite)value).getNumber());
                }
                return CodedOutputStream.computeEnumSizeNoTag((Integer)value);
            }
        }
        throw new RuntimeException("There is no way to get here, but the compiler thinks otherwise.");
    }

    public static int computeFieldSize(FieldDescriptorLite<?> descriptor, Object value) {
        WireFormat.FieldType type = descriptor.getLiteType();
        int number = descriptor.getNumber();
        if (descriptor.isRepeated()) {
            if (descriptor.isPacked()) {
                int dataSize = 0;
                for (Object element : (List)value) {
                    dataSize += FieldSet.computeElementSizeNoTag(type, element);
                }
                return dataSize + CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeRawVarint32Size(dataSize);
            }
            int size = 0;
            for (Object element : (List)value) {
                size += FieldSet.computeElementSize(type, number, element);
            }
            return size;
        }
        return FieldSet.computeElementSize(type, number, value);
    }

    static final class Builder<T extends FieldDescriptorLite<T>> {
        private SmallSortedMap<T, Object> fields;
        private boolean hasLazyField;
        private boolean isMutable;
        private boolean hasNestedBuilders;

        private Builder() {
            this(SmallSortedMap.newFieldMap(16));
        }

        private Builder(SmallSortedMap<T, Object> fields) {
            this.fields = fields;
            this.isMutable = true;
        }

        public FieldSet<T> build() {
            if (this.fields.isEmpty()) {
                return FieldSet.emptySet();
            }
            this.isMutable = false;
            SmallSortedMap fieldsForBuild = this.fields;
            if (this.hasNestedBuilders) {
                fieldsForBuild = FieldSet.cloneAllFieldsMap(this.fields, false);
                Builder.replaceBuilders(fieldsForBuild);
            }
            FieldSet fieldSet = new FieldSet(fieldsForBuild);
            fieldSet.hasLazyField = this.hasLazyField;
            return fieldSet;
        }

        private static <T extends FieldDescriptorLite<T>> void replaceBuilders(SmallSortedMap<T, Object> fieldMap) {
            for (int i = 0; i < fieldMap.getNumArrayEntries(); ++i) {
                Builder.replaceBuilders(fieldMap.getArrayEntryAt(i));
            }
            for (Map.Entry<T, Object> entry : fieldMap.getOverflowEntries()) {
                Builder.replaceBuilders(entry);
            }
        }

        private static <T extends FieldDescriptorLite<T>> void replaceBuilders(Map.Entry<T, Object> entry) {
            entry.setValue(Builder.replaceBuilders((FieldDescriptorLite)entry.getKey(), entry.getValue()));
        }

        private static <T extends FieldDescriptorLite<T>> Object replaceBuilders(T descriptor, Object value) {
            if (value == null) {
                return value;
            }
            if (descriptor.getLiteJavaType() == WireFormat.JavaType.MESSAGE) {
                if (descriptor.isRepeated()) {
                    if (!(value instanceof List)) {
                        throw new IllegalStateException("Repeated field should contains a List but actually contains type: " + value.getClass());
                    }
                    ArrayList<Object> list = (ArrayList<Object>)value;
                    for (int i = 0; i < list.size(); ++i) {
                        Object oldElement = list.get(i);
                        Object newElement = Builder.replaceBuilder(oldElement);
                        if (newElement == oldElement) continue;
                        if (list == value) {
                            list = new ArrayList<Object>(list);
                        }
                        list.set(i, newElement);
                    }
                    return list;
                }
                return Builder.replaceBuilder(value);
            }
            return value;
        }

        private static Object replaceBuilder(Object value) {
            return value instanceof MessageLite.Builder ? ((MessageLite.Builder)value).build() : value;
        }

        public static <T extends FieldDescriptorLite<T>> Builder<T> fromFieldSet(FieldSet<T> fieldSet) {
            Builder<T> builder = new Builder<T>(FieldSet.cloneAllFieldsMap(((FieldSet)fieldSet).fields, true));
            builder.hasLazyField = ((FieldSet)fieldSet).hasLazyField;
            return builder;
        }

        public Map<T, Object> getAllFields() {
            if (this.hasLazyField) {
                SmallSortedMap result = FieldSet.cloneAllFieldsMap(this.fields, false);
                if (this.fields.isImmutable()) {
                    result.makeImmutable();
                } else {
                    Builder.replaceBuilders(result);
                }
                return result;
            }
            return this.fields.isImmutable() ? this.fields : Collections.unmodifiableMap(this.fields);
        }

        public boolean hasField(T descriptor) {
            if (descriptor.isRepeated()) {
                throw new IllegalArgumentException("hasField() can only be called on non-repeated fields.");
            }
            return this.fields.get(descriptor) != null;
        }

        public Object getField(T descriptor) {
            Object value = this.getFieldAllowBuilders(descriptor);
            return Builder.replaceBuilders(descriptor, value);
        }

        Object getFieldAllowBuilders(T descriptor) {
            Object o = this.fields.get(descriptor);
            if (o instanceof LazyField) {
                return ((LazyField)o).getValue();
            }
            return o;
        }

        private void ensureIsMutable() {
            if (!this.isMutable) {
                this.fields = FieldSet.cloneAllFieldsMap(this.fields, true);
                this.isMutable = true;
            }
        }

        public void setField(T descriptor, Object value) {
            this.ensureIsMutable();
            if (descriptor.isRepeated()) {
                if (!(value instanceof List)) {
                    throw new IllegalArgumentException("Wrong object type used with protocol message reflection.");
                }
                ArrayList newList = new ArrayList();
                newList.addAll(value);
                for (Object element : newList) {
                    Builder.verifyType(descriptor.getLiteType(), element);
                    this.hasNestedBuilders = this.hasNestedBuilders || element instanceof MessageLite.Builder;
                }
                value = newList;
            } else {
                Builder.verifyType(descriptor.getLiteType(), value);
            }
            if (value instanceof LazyField) {
                this.hasLazyField = true;
            }
            this.hasNestedBuilders = this.hasNestedBuilders || value instanceof MessageLite.Builder;
            this.fields.put(descriptor, (Object)value);
        }

        public void clearField(T descriptor) {
            this.ensureIsMutable();
            this.fields.remove(descriptor);
            if (this.fields.isEmpty()) {
                this.hasLazyField = false;
            }
        }

        public int getRepeatedFieldCount(T descriptor) {
            if (!descriptor.isRepeated()) {
                throw new IllegalArgumentException("getRepeatedField() can only be called on repeated fields.");
            }
            Object value = this.getField(descriptor);
            if (value == null) {
                return 0;
            }
            return ((List)value).size();
        }

        public Object getRepeatedField(T descriptor, int index) {
            if (this.hasNestedBuilders) {
                this.ensureIsMutable();
            }
            Object value = this.getRepeatedFieldAllowBuilders(descriptor, index);
            return Builder.replaceBuilder(value);
        }

        Object getRepeatedFieldAllowBuilders(T descriptor, int index) {
            if (!descriptor.isRepeated()) {
                throw new IllegalArgumentException("getRepeatedField() can only be called on repeated fields.");
            }
            Object value = this.getFieldAllowBuilders(descriptor);
            if (value == null) {
                throw new IndexOutOfBoundsException();
            }
            return ((List)value).get(index);
        }

        public void setRepeatedField(T descriptor, int index, Object value) {
            this.ensureIsMutable();
            if (!descriptor.isRepeated()) {
                throw new IllegalArgumentException("getRepeatedField() can only be called on repeated fields.");
            }
            this.hasNestedBuilders = this.hasNestedBuilders || value instanceof MessageLite.Builder;
            Object list = this.getField(descriptor);
            if (list == null) {
                throw new IndexOutOfBoundsException();
            }
            Builder.verifyType(descriptor.getLiteType(), value);
            ((List)list).set(index, value);
        }

        public void addRepeatedField(T descriptor, Object value) {
            ArrayList<Object> list;
            this.ensureIsMutable();
            if (!descriptor.isRepeated()) {
                throw new IllegalArgumentException("addRepeatedField() can only be called on repeated fields.");
            }
            this.hasNestedBuilders = this.hasNestedBuilders || value instanceof MessageLite.Builder;
            Builder.verifyType(descriptor.getLiteType(), value);
            Object existingValue = this.getField(descriptor);
            if (existingValue == null) {
                list = new ArrayList<Object>();
                this.fields.put(descriptor, (Object)list);
            } else {
                list = (ArrayList<Object>)existingValue;
            }
            list.add(value);
        }

        private static void verifyType(WireFormat.FieldType type, Object value) {
            if (!FieldSet.isValidType(type, value)) {
                if (type.getJavaType() == WireFormat.JavaType.MESSAGE && value instanceof MessageLite.Builder) {
                    return;
                }
                throw new IllegalArgumentException("Wrong object type used with protocol message reflection.");
            }
        }

        public boolean isInitialized() {
            for (int i = 0; i < this.fields.getNumArrayEntries(); ++i) {
                if (FieldSet.isInitialized(this.fields.getArrayEntryAt(i))) continue;
                return false;
            }
            for (Map.Entry<T, Object> entry : this.fields.getOverflowEntries()) {
                if (FieldSet.isInitialized(entry)) continue;
                return false;
            }
            return true;
        }

        public void mergeFrom(FieldSet<T> other) {
            this.ensureIsMutable();
            for (int i = 0; i < ((FieldSet)other).fields.getNumArrayEntries(); ++i) {
                this.mergeFromField(((FieldSet)other).fields.getArrayEntryAt(i));
            }
            for (Map.Entry entry : ((FieldSet)other).fields.getOverflowEntries()) {
                this.mergeFromField(entry);
            }
        }

        private void mergeFromField(Map.Entry<T, Object> entry) {
            FieldDescriptorLite descriptor = (FieldDescriptorLite)entry.getKey();
            Object otherValue = entry.getValue();
            if (otherValue instanceof LazyField) {
                otherValue = ((LazyField)otherValue).getValue();
            }
            if (descriptor.isRepeated()) {
                ArrayList value = this.getField(descriptor);
                if (value == null) {
                    value = new ArrayList();
                }
                for (Object element : (List)otherValue) {
                    ((List)value).add(FieldSet.cloneIfMutable(element));
                }
                this.fields.put((T)descriptor, (Object)value);
            } else if (descriptor.getLiteJavaType() == WireFormat.JavaType.MESSAGE) {
                Object value = this.getField(descriptor);
                if (value == null) {
                    this.fields.put((T)descriptor, FieldSet.cloneIfMutable(otherValue));
                } else if (value instanceof MessageLite.Builder) {
                    descriptor.internalMergeFrom((MessageLite.Builder)value, (MessageLite)otherValue);
                } else {
                    value = descriptor.internalMergeFrom(((MessageLite)value).toBuilder(), (MessageLite)otherValue).build();
                    this.fields.put((T)descriptor, value);
                }
            } else {
                this.fields.put((T)descriptor, FieldSet.cloneIfMutable(otherValue));
            }
        }
    }

    public static interface FieldDescriptorLite<T extends FieldDescriptorLite<T>>
    extends Comparable<T> {
        public int getNumber();

        public WireFormat.FieldType getLiteType();

        public WireFormat.JavaType getLiteJavaType();

        public boolean isRepeated();

        public boolean isPacked();

        public Internal.EnumLiteMap<?> getEnumType();

        public MessageLite.Builder internalMergeFrom(MessageLite.Builder var1, MessageLite var2);
    }
}

