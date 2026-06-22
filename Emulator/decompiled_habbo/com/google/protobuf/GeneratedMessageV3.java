/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.AbstractMessage;
import com.google.protobuf.BooleanArrayList;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedInputStreamReader;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DoubleArrayList;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.Extension;
import com.google.protobuf.ExtensionLite;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.FieldSet;
import com.google.protobuf.FloatArrayList;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.IntArrayList;
import com.google.protobuf.Internal;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.LazyField;
import com.google.protobuf.LongArrayList;
import com.google.protobuf.MapEntry;
import com.google.protobuf.MapField;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.MessageReflection;
import com.google.protobuf.Parser;
import com.google.protobuf.Protobuf;
import com.google.protobuf.Schema;
import com.google.protobuf.UnknownFieldSet;
import com.google.protobuf.UnsafeUtil;
import com.google.protobuf.WireFormat;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public abstract class GeneratedMessageV3
extends AbstractMessage
implements Serializable {
    private static final long serialVersionUID = 1L;
    protected static boolean alwaysUseFieldBuilders = false;
    protected UnknownFieldSet unknownFields;

    protected GeneratedMessageV3() {
        this.unknownFields = UnknownFieldSet.getDefaultInstance();
    }

    protected GeneratedMessageV3(Builder<?> builder) {
        this.unknownFields = builder.getUnknownFields();
    }

    public Parser<? extends GeneratedMessageV3> getParserForType() {
        throw new UnsupportedOperationException("This is supposed to be overridden by subclasses.");
    }

    static void enableAlwaysUseFieldBuildersForTesting() {
        GeneratedMessageV3.setAlwaysUseFieldBuildersForTesting(true);
    }

    static void setAlwaysUseFieldBuildersForTesting(boolean useBuilders) {
        alwaysUseFieldBuilders = useBuilders;
    }

    protected abstract FieldAccessorTable internalGetFieldAccessorTable();

    @Override
    public Descriptors.Descriptor getDescriptorForType() {
        return this.internalGetFieldAccessorTable().descriptor;
    }

    protected void mergeFromAndMakeImmutableInternal(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        Schema<GeneratedMessageV3> schema = Protobuf.getInstance().schemaFor(this);
        try {
            schema.mergeFrom(this, CodedInputStreamReader.forCodedInput(input), extensionRegistry);
        }
        catch (InvalidProtocolBufferException e) {
            throw e.setUnfinishedMessage(this);
        }
        catch (IOException e) {
            throw new InvalidProtocolBufferException(e).setUnfinishedMessage(this);
        }
        schema.makeImmutable(this);
    }

    private Map<Descriptors.FieldDescriptor, Object> getAllFieldsMutable(boolean getBytesForString) {
        TreeMap<Descriptors.FieldDescriptor, Object> result = new TreeMap<Descriptors.FieldDescriptor, Object>();
        Descriptors.Descriptor descriptor = this.internalGetFieldAccessorTable().descriptor;
        List<Descriptors.FieldDescriptor> fields = descriptor.getFields();
        for (int i = 0; i < fields.size(); ++i) {
            Descriptors.FieldDescriptor field = fields.get(i);
            Descriptors.OneofDescriptor oneofDescriptor = field.getContainingOneof();
            if (oneofDescriptor != null) {
                i += oneofDescriptor.getFieldCount() - 1;
                if (!this.hasOneof(oneofDescriptor)) continue;
                field = this.getOneofFieldDescriptor(oneofDescriptor);
            } else {
                if (field.isRepeated()) {
                    List value = (List)this.getField(field);
                    if (value.isEmpty()) continue;
                    result.put(field, value);
                    continue;
                }
                if (!this.hasField(field)) continue;
            }
            if (getBytesForString && field.getJavaType() == Descriptors.FieldDescriptor.JavaType.STRING) {
                result.put(field, this.getFieldRaw(field));
                continue;
            }
            result.put(field, this.getField(field));
        }
        return result;
    }

    @Override
    public boolean isInitialized() {
        for (Descriptors.FieldDescriptor field : this.getDescriptorForType().getFields()) {
            if (field.isRequired() && !this.hasField(field)) {
                return false;
            }
            if (field.getJavaType() != Descriptors.FieldDescriptor.JavaType.MESSAGE) continue;
            if (field.isRepeated()) {
                List messageList = (List)this.getField(field);
                for (Message element : messageList) {
                    if (element.isInitialized()) continue;
                    return false;
                }
                continue;
            }
            if (!this.hasField(field) || ((Message)this.getField(field)).isInitialized()) continue;
            return false;
        }
        return true;
    }

    @Override
    public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
        return Collections.unmodifiableMap(this.getAllFieldsMutable(false));
    }

    Map<Descriptors.FieldDescriptor, Object> getAllFieldsRaw() {
        return Collections.unmodifiableMap(this.getAllFieldsMutable(true));
    }

    @Override
    public boolean hasOneof(Descriptors.OneofDescriptor oneof) {
        return this.internalGetFieldAccessorTable().getOneof(oneof).has(this);
    }

    @Override
    public Descriptors.FieldDescriptor getOneofFieldDescriptor(Descriptors.OneofDescriptor oneof) {
        return this.internalGetFieldAccessorTable().getOneof(oneof).get(this);
    }

    @Override
    public boolean hasField(Descriptors.FieldDescriptor field) {
        return this.internalGetFieldAccessorTable().getField(field).has(this);
    }

    @Override
    public Object getField(Descriptors.FieldDescriptor field) {
        return this.internalGetFieldAccessorTable().getField(field).get(this);
    }

    Object getFieldRaw(Descriptors.FieldDescriptor field) {
        return this.internalGetFieldAccessorTable().getField(field).getRaw(this);
    }

    @Override
    public int getRepeatedFieldCount(Descriptors.FieldDescriptor field) {
        return this.internalGetFieldAccessorTable().getField(field).getRepeatedCount(this);
    }

    @Override
    public Object getRepeatedField(Descriptors.FieldDescriptor field, int index) {
        return this.internalGetFieldAccessorTable().getField(field).getRepeated(this, index);
    }

    @Override
    public UnknownFieldSet getUnknownFields() {
        throw new UnsupportedOperationException("This is supposed to be overridden by subclasses.");
    }

    protected boolean parseUnknownField(CodedInputStream input, UnknownFieldSet.Builder unknownFields, ExtensionRegistryLite extensionRegistry, int tag) throws IOException {
        if (input.shouldDiscardUnknownFields()) {
            return input.skipField(tag);
        }
        return unknownFields.mergeFieldFrom(tag, input);
    }

    protected boolean parseUnknownFieldProto3(CodedInputStream input, UnknownFieldSet.Builder unknownFields, ExtensionRegistryLite extensionRegistry, int tag) throws IOException {
        return this.parseUnknownField(input, unknownFields, extensionRegistry, tag);
    }

    protected static <M extends Message> M parseWithIOException(Parser<M> parser, InputStream input) throws IOException {
        try {
            return (M)((Message)parser.parseFrom(input));
        }
        catch (InvalidProtocolBufferException e) {
            throw e.unwrapIOException();
        }
    }

    protected static <M extends Message> M parseWithIOException(Parser<M> parser, InputStream input, ExtensionRegistryLite extensions) throws IOException {
        try {
            return (M)((Message)parser.parseFrom(input, extensions));
        }
        catch (InvalidProtocolBufferException e) {
            throw e.unwrapIOException();
        }
    }

    protected static <M extends Message> M parseWithIOException(Parser<M> parser, CodedInputStream input) throws IOException {
        try {
            return (M)((Message)parser.parseFrom(input));
        }
        catch (InvalidProtocolBufferException e) {
            throw e.unwrapIOException();
        }
    }

    protected static <M extends Message> M parseWithIOException(Parser<M> parser, CodedInputStream input, ExtensionRegistryLite extensions) throws IOException {
        try {
            return (M)((Message)parser.parseFrom(input, extensions));
        }
        catch (InvalidProtocolBufferException e) {
            throw e.unwrapIOException();
        }
    }

    protected static <M extends Message> M parseDelimitedWithIOException(Parser<M> parser, InputStream input) throws IOException {
        try {
            return (M)((Message)parser.parseDelimitedFrom(input));
        }
        catch (InvalidProtocolBufferException e) {
            throw e.unwrapIOException();
        }
    }

    protected static <M extends Message> M parseDelimitedWithIOException(Parser<M> parser, InputStream input, ExtensionRegistryLite extensions) throws IOException {
        try {
            return (M)((Message)parser.parseDelimitedFrom(input, extensions));
        }
        catch (InvalidProtocolBufferException e) {
            throw e.unwrapIOException();
        }
    }

    protected static boolean canUseUnsafe() {
        return UnsafeUtil.hasUnsafeArrayOperations() && UnsafeUtil.hasUnsafeByteBufferOperations();
    }

    protected static Internal.IntList emptyIntList() {
        return IntArrayList.emptyList();
    }

    protected static Internal.IntList newIntList() {
        return new IntArrayList();
    }

    protected static Internal.IntList mutableCopy(Internal.IntList list) {
        int size = list.size();
        return list.mutableCopyWithCapacity(size == 0 ? 10 : size * 2);
    }

    protected static Internal.LongList emptyLongList() {
        return LongArrayList.emptyList();
    }

    protected static Internal.LongList newLongList() {
        return new LongArrayList();
    }

    protected static Internal.LongList mutableCopy(Internal.LongList list) {
        int size = list.size();
        return list.mutableCopyWithCapacity(size == 0 ? 10 : size * 2);
    }

    protected static Internal.FloatList emptyFloatList() {
        return FloatArrayList.emptyList();
    }

    protected static Internal.FloatList newFloatList() {
        return new FloatArrayList();
    }

    protected static Internal.FloatList mutableCopy(Internal.FloatList list) {
        int size = list.size();
        return list.mutableCopyWithCapacity(size == 0 ? 10 : size * 2);
    }

    protected static Internal.DoubleList emptyDoubleList() {
        return DoubleArrayList.emptyList();
    }

    protected static Internal.DoubleList newDoubleList() {
        return new DoubleArrayList();
    }

    protected static Internal.DoubleList mutableCopy(Internal.DoubleList list) {
        int size = list.size();
        return list.mutableCopyWithCapacity(size == 0 ? 10 : size * 2);
    }

    protected static Internal.BooleanList emptyBooleanList() {
        return BooleanArrayList.emptyList();
    }

    protected static Internal.BooleanList newBooleanList() {
        return new BooleanArrayList();
    }

    protected static Internal.BooleanList mutableCopy(Internal.BooleanList list) {
        int size = list.size();
        return list.mutableCopyWithCapacity(size == 0 ? 10 : size * 2);
    }

    @Override
    public void writeTo(CodedOutputStream output) throws IOException {
        MessageReflection.writeMessageTo(this, this.getAllFieldsRaw(), output, false);
    }

    @Override
    public int getSerializedSize() {
        int size = this.memoizedSize;
        if (size != -1) {
            return size;
        }
        this.memoizedSize = MessageReflection.getSerializedSize(this, this.getAllFieldsRaw());
        return this.memoizedSize;
    }

    protected Object newInstance(UnusedPrivateParameter unused) {
        throw new UnsupportedOperationException("This method must be overridden by the subclass.");
    }

    protected void makeExtensionsImmutable() {
    }

    protected abstract Message.Builder newBuilderForType(BuilderParent var1);

    @Override
    protected Message.Builder newBuilderForType(final AbstractMessage.BuilderParent parent) {
        return this.newBuilderForType(new BuilderParent(){

            @Override
            public void markDirty() {
                parent.markDirty();
            }
        });
    }

    private static Method getMethodOrDie(Class clazz, String name, Class ... params) {
        try {
            return clazz.getMethod(name, params);
        }
        catch (NoSuchMethodException e) {
            throw new RuntimeException("Generated message class \"" + clazz.getName() + "\" missing method \"" + name + "\".", e);
        }
    }

    private static Object invokeOrDie(Method method, Object object, Object ... params) {
        try {
            return method.invoke(object, params);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("Couldn't use Java reflection to implement protocol message reflection.", e);
        }
        catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException)cause;
            }
            if (cause instanceof Error) {
                throw (Error)cause;
            }
            throw new RuntimeException("Unexpected exception thrown by generated accessor method.", cause);
        }
    }

    protected MapField internalGetMapField(int fieldNumber) {
        throw new RuntimeException("No map fields found in " + this.getClass().getName());
    }

    protected Object writeReplace() throws ObjectStreamException {
        return new GeneratedMessageLite.SerializedForm(this);
    }

    private static <MessageType extends ExtendableMessage<MessageType>, T> Extension<MessageType, T> checkNotLite(ExtensionLite<MessageType, T> extension) {
        if (extension.isLite()) {
            throw new IllegalArgumentException("Expected non-lite extension.");
        }
        return (Extension)extension;
    }

    protected static int computeStringSize(int fieldNumber, Object value) {
        if (value instanceof String) {
            return CodedOutputStream.computeStringSize(fieldNumber, (String)value);
        }
        return CodedOutputStream.computeBytesSize(fieldNumber, (ByteString)value);
    }

    protected static int computeStringSizeNoTag(Object value) {
        if (value instanceof String) {
            return CodedOutputStream.computeStringSizeNoTag((String)value);
        }
        return CodedOutputStream.computeBytesSizeNoTag((ByteString)value);
    }

    protected static void writeString(CodedOutputStream output, int fieldNumber, Object value) throws IOException {
        if (value instanceof String) {
            output.writeString(fieldNumber, (String)value);
        } else {
            output.writeBytes(fieldNumber, (ByteString)value);
        }
    }

    protected static void writeStringNoTag(CodedOutputStream output, Object value) throws IOException {
        if (value instanceof String) {
            output.writeStringNoTag((String)value);
        } else {
            output.writeBytesNoTag((ByteString)value);
        }
    }

    protected static <V> void serializeIntegerMapTo(CodedOutputStream out, MapField<Integer, V> field, MapEntry<Integer, V> defaultEntry, int fieldNumber) throws IOException {
        Map<Integer, V> m = field.getMap();
        if (!out.isSerializationDeterministic()) {
            GeneratedMessageV3.serializeMapTo(out, m, defaultEntry, fieldNumber);
            return;
        }
        int[] keys = new int[m.size()];
        int index = 0;
        Iterator<Integer> iterator = m.keySet().iterator();
        while (iterator.hasNext()) {
            int k = iterator.next();
            keys[index++] = k;
        }
        Arrays.sort(keys);
        for (Object key : (Iterator<Integer>)keys) {
            out.writeMessage(fieldNumber, ((MapEntry.Builder)defaultEntry.newBuilderForType()).setKey((int)key).setValue(m.get((int)key)).build());
        }
    }

    protected static <V> void serializeLongMapTo(CodedOutputStream out, MapField<Long, V> field, MapEntry<Long, V> defaultEntry, int fieldNumber) throws IOException {
        Map<Long, V> m = field.getMap();
        if (!out.isSerializationDeterministic()) {
            GeneratedMessageV3.serializeMapTo(out, m, defaultEntry, fieldNumber);
            return;
        }
        long[] keys = new long[m.size()];
        int index = 0;
        Iterator<Long> iterator = m.keySet().iterator();
        while (iterator.hasNext()) {
            long k = iterator.next();
            keys[index++] = k;
        }
        Arrays.sort(keys);
        for (Object key : (Iterator<Long>)keys) {
            out.writeMessage(fieldNumber, ((MapEntry.Builder)defaultEntry.newBuilderForType()).setKey((long)key).setValue(m.get((long)key)).build());
        }
    }

    protected static <V> void serializeStringMapTo(CodedOutputStream out, MapField<String, V> field, MapEntry<String, V> defaultEntry, int fieldNumber) throws IOException {
        Map<String, V> m = field.getMap();
        if (!out.isSerializationDeterministic()) {
            GeneratedMessageV3.serializeMapTo(out, m, defaultEntry, fieldNumber);
            return;
        }
        Object[] keys = new String[m.size()];
        keys = m.keySet().toArray(keys);
        Arrays.sort(keys);
        for (Object key : keys) {
            out.writeMessage(fieldNumber, ((MapEntry.Builder)defaultEntry.newBuilderForType()).setKey(key).setValue(m.get(key)).build());
        }
    }

    protected static <V> void serializeBooleanMapTo(CodedOutputStream out, MapField<Boolean, V> field, MapEntry<Boolean, V> defaultEntry, int fieldNumber) throws IOException {
        Map<Boolean, V> m = field.getMap();
        if (!out.isSerializationDeterministic()) {
            GeneratedMessageV3.serializeMapTo(out, m, defaultEntry, fieldNumber);
            return;
        }
        GeneratedMessageV3.maybeSerializeBooleanEntryTo(out, m, defaultEntry, fieldNumber, false);
        GeneratedMessageV3.maybeSerializeBooleanEntryTo(out, m, defaultEntry, fieldNumber, true);
    }

    private static <V> void maybeSerializeBooleanEntryTo(CodedOutputStream out, Map<Boolean, V> m, MapEntry<Boolean, V> defaultEntry, int fieldNumber, boolean key) throws IOException {
        if (m.containsKey(key)) {
            out.writeMessage(fieldNumber, ((MapEntry.Builder)defaultEntry.newBuilderForType()).setKey(key).setValue(m.get(key)).build());
        }
    }

    private static <K, V> void serializeMapTo(CodedOutputStream out, Map<K, V> m, MapEntry<K, V> defaultEntry, int fieldNumber) throws IOException {
        for (Map.Entry<K, V> entry : m.entrySet()) {
            out.writeMessage(fieldNumber, ((MapEntry.Builder)defaultEntry.newBuilderForType()).setKey(entry.getKey()).setValue(entry.getValue()).build());
        }
    }

    public static final class FieldAccessorTable {
        private final Descriptors.Descriptor descriptor;
        private final FieldAccessor[] fields;
        private String[] camelCaseNames;
        private final OneofAccessor[] oneofs;
        private volatile boolean initialized;

        public FieldAccessorTable(Descriptors.Descriptor descriptor, String[] camelCaseNames, Class<? extends GeneratedMessageV3> messageClass, Class<? extends Builder> builderClass) {
            this(descriptor, camelCaseNames);
            this.ensureFieldAccessorsInitialized(messageClass, builderClass);
        }

        public FieldAccessorTable(Descriptors.Descriptor descriptor, String[] camelCaseNames) {
            this.descriptor = descriptor;
            this.camelCaseNames = camelCaseNames;
            this.fields = new FieldAccessor[descriptor.getFields().size()];
            this.oneofs = new OneofAccessor[descriptor.getOneofs().size()];
            this.initialized = false;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public FieldAccessorTable ensureFieldAccessorsInitialized(Class<? extends GeneratedMessageV3> messageClass, Class<? extends Builder> builderClass) {
            if (this.initialized) {
                return this;
            }
            FieldAccessorTable fieldAccessorTable = this;
            synchronized (fieldAccessorTable) {
                if (this.initialized) {
                    return this;
                }
                int fieldsSize = this.fields.length;
                for (int i = 0; i < fieldsSize; ++i) {
                    Descriptors.FieldDescriptor field = this.descriptor.getFields().get(i);
                    String containingOneofCamelCaseName = null;
                    if (field.getContainingOneof() != null) {
                        containingOneofCamelCaseName = this.camelCaseNames[fieldsSize + field.getContainingOneof().getIndex()];
                    }
                    if (field.isRepeated()) {
                        if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                            if (field.isMapField()) {
                                this.fields[i] = new MapFieldAccessor(field, this.camelCaseNames[i], messageClass, builderClass);
                                continue;
                            }
                            this.fields[i] = new RepeatedMessageFieldAccessor(field, this.camelCaseNames[i], messageClass, builderClass);
                            continue;
                        }
                        if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.ENUM) {
                            this.fields[i] = new RepeatedEnumFieldAccessor(field, this.camelCaseNames[i], messageClass, builderClass);
                            continue;
                        }
                        this.fields[i] = new RepeatedFieldAccessor(field, this.camelCaseNames[i], messageClass, builderClass);
                        continue;
                    }
                    this.fields[i] = field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE ? new SingularMessageFieldAccessor(field, this.camelCaseNames[i], messageClass, builderClass, containingOneofCamelCaseName) : (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.ENUM ? new SingularEnumFieldAccessor(field, this.camelCaseNames[i], messageClass, builderClass, containingOneofCamelCaseName) : (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.STRING ? new SingularStringFieldAccessor(field, this.camelCaseNames[i], messageClass, builderClass, containingOneofCamelCaseName) : new SingularFieldAccessor(field, this.camelCaseNames[i], messageClass, builderClass, containingOneofCamelCaseName)));
                }
                int oneofsSize = this.oneofs.length;
                for (int i = 0; i < oneofsSize; ++i) {
                    this.oneofs[i] = new OneofAccessor(this.descriptor, this.camelCaseNames[i + fieldsSize], messageClass, builderClass);
                }
                this.initialized = true;
                this.camelCaseNames = null;
                return this;
            }
        }

        private FieldAccessor getField(Descriptors.FieldDescriptor field) {
            if (field.getContainingType() != this.descriptor) {
                throw new IllegalArgumentException("FieldDescriptor does not match message type.");
            }
            if (field.isExtension()) {
                throw new IllegalArgumentException("This type does not have extensions.");
            }
            return this.fields[field.getIndex()];
        }

        private OneofAccessor getOneof(Descriptors.OneofDescriptor oneof) {
            if (oneof.getContainingType() != this.descriptor) {
                throw new IllegalArgumentException("OneofDescriptor does not match message type.");
            }
            return this.oneofs[oneof.getIndex()];
        }

        private static boolean supportFieldPresence(Descriptors.FileDescriptor file) {
            return file.getSyntax() == Descriptors.FileDescriptor.Syntax.PROTO2;
        }

        private static final class RepeatedMessageFieldAccessor
        extends RepeatedFieldAccessor {
            private final Method newBuilderMethod;
            private final Method getBuilderMethodBuilder;

            RepeatedMessageFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessageV3> messageClass, Class<? extends Builder> builderClass) {
                super(descriptor, camelCaseName, messageClass, builderClass);
                this.newBuilderMethod = GeneratedMessageV3.getMethodOrDie(this.type, "newBuilder", new Class[0]);
                this.getBuilderMethodBuilder = GeneratedMessageV3.getMethodOrDie(builderClass, "get" + camelCaseName + "Builder", new Class[]{Integer.TYPE});
            }

            private Object coerceType(Object value) {
                if (this.type.isInstance(value)) {
                    return value;
                }
                return ((Message.Builder)GeneratedMessageV3.invokeOrDie(this.newBuilderMethod, null, new Object[0])).mergeFrom((Message)value).build();
            }

            @Override
            public void setRepeated(Builder builder, int index, Object value) {
                super.setRepeated(builder, index, this.coerceType(value));
            }

            @Override
            public void addRepeated(Builder builder, Object value) {
                super.addRepeated(builder, this.coerceType(value));
            }

            @Override
            public Message.Builder newBuilder() {
                return (Message.Builder)GeneratedMessageV3.invokeOrDie(this.newBuilderMethod, null, new Object[0]);
            }

            @Override
            public Message.Builder getRepeatedBuilder(Builder builder, int index) {
                return (Message.Builder)GeneratedMessageV3.invokeOrDie(this.getBuilderMethodBuilder, builder, new Object[]{index});
            }
        }

        private static final class SingularMessageFieldAccessor
        extends SingularFieldAccessor {
            private final Method newBuilderMethod;
            private final Method getBuilderMethodBuilder;

            SingularMessageFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessageV3> messageClass, Class<? extends Builder> builderClass, String containingOneofCamelCaseName) {
                super(descriptor, camelCaseName, messageClass, builderClass, containingOneofCamelCaseName);
                this.newBuilderMethod = GeneratedMessageV3.getMethodOrDie(this.type, "newBuilder", new Class[0]);
                this.getBuilderMethodBuilder = GeneratedMessageV3.getMethodOrDie(builderClass, "get" + camelCaseName + "Builder", new Class[0]);
            }

            private Object coerceType(Object value) {
                if (this.type.isInstance(value)) {
                    return value;
                }
                return ((Message.Builder)GeneratedMessageV3.invokeOrDie(this.newBuilderMethod, null, new Object[0])).mergeFrom((Message)value).buildPartial();
            }

            @Override
            public void set(Builder builder, Object value) {
                super.set(builder, this.coerceType(value));
            }

            @Override
            public Message.Builder newBuilder() {
                return (Message.Builder)GeneratedMessageV3.invokeOrDie(this.newBuilderMethod, null, new Object[0]);
            }

            @Override
            public Message.Builder getBuilder(Builder builder) {
                return (Message.Builder)GeneratedMessageV3.invokeOrDie(this.getBuilderMethodBuilder, builder, new Object[0]);
            }
        }

        private static final class SingularStringFieldAccessor
        extends SingularFieldAccessor {
            private final Method getBytesMethod;
            private final Method getBytesMethodBuilder;
            private final Method setBytesMethodBuilder;

            SingularStringFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessageV3> messageClass, Class<? extends Builder> builderClass, String containingOneofCamelCaseName) {
                super(descriptor, camelCaseName, messageClass, builderClass, containingOneofCamelCaseName);
                this.getBytesMethod = GeneratedMessageV3.getMethodOrDie(messageClass, "get" + camelCaseName + "Bytes", new Class[0]);
                this.getBytesMethodBuilder = GeneratedMessageV3.getMethodOrDie(builderClass, "get" + camelCaseName + "Bytes", new Class[0]);
                this.setBytesMethodBuilder = GeneratedMessageV3.getMethodOrDie(builderClass, "set" + camelCaseName + "Bytes", new Class[]{ByteString.class});
            }

            @Override
            public Object getRaw(GeneratedMessageV3 message) {
                return GeneratedMessageV3.invokeOrDie(this.getBytesMethod, message, new Object[0]);
            }

            @Override
            public Object getRaw(Builder builder) {
                return GeneratedMessageV3.invokeOrDie(this.getBytesMethodBuilder, builder, new Object[0]);
            }

            @Override
            public void set(Builder builder, Object value) {
                if (value instanceof ByteString) {
                    GeneratedMessageV3.invokeOrDie(this.setBytesMethodBuilder, builder, new Object[]{value});
                } else {
                    super.set(builder, value);
                }
            }
        }

        private static final class RepeatedEnumFieldAccessor
        extends RepeatedFieldAccessor {
            private Descriptors.EnumDescriptor enumDescriptor;
            private final Method valueOfMethod;
            private final Method getValueDescriptorMethod;
            private boolean supportUnknownEnumValue;
            private Method getRepeatedValueMethod;
            private Method getRepeatedValueMethodBuilder;
            private Method setRepeatedValueMethod;
            private Method addRepeatedValueMethod;

            RepeatedEnumFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessageV3> messageClass, Class<? extends Builder> builderClass) {
                super(descriptor, camelCaseName, messageClass, builderClass);
                this.enumDescriptor = descriptor.getEnumType();
                this.valueOfMethod = GeneratedMessageV3.getMethodOrDie(this.type, "valueOf", new Class[]{Descriptors.EnumValueDescriptor.class});
                this.getValueDescriptorMethod = GeneratedMessageV3.getMethodOrDie(this.type, "getValueDescriptor", new Class[0]);
                this.supportUnknownEnumValue = descriptor.getFile().supportsUnknownEnumValue();
                if (this.supportUnknownEnumValue) {
                    this.getRepeatedValueMethod = GeneratedMessageV3.getMethodOrDie(messageClass, "get" + camelCaseName + "Value", new Class[]{Integer.TYPE});
                    this.getRepeatedValueMethodBuilder = GeneratedMessageV3.getMethodOrDie(builderClass, "get" + camelCaseName + "Value", new Class[]{Integer.TYPE});
                    this.setRepeatedValueMethod = GeneratedMessageV3.getMethodOrDie(builderClass, "set" + camelCaseName + "Value", new Class[]{Integer.TYPE, Integer.TYPE});
                    this.addRepeatedValueMethod = GeneratedMessageV3.getMethodOrDie(builderClass, "add" + camelCaseName + "Value", new Class[]{Integer.TYPE});
                }
            }

            @Override
            public Object get(GeneratedMessageV3 message) {
                ArrayList<Object> newList = new ArrayList<Object>();
                int size = this.getRepeatedCount(message);
                for (int i = 0; i < size; ++i) {
                    newList.add(this.getRepeated(message, i));
                }
                return Collections.unmodifiableList(newList);
            }

            @Override
            public Object get(Builder builder) {
                ArrayList<Object> newList = new ArrayList<Object>();
                int size = this.getRepeatedCount(builder);
                for (int i = 0; i < size; ++i) {
                    newList.add(this.getRepeated(builder, i));
                }
                return Collections.unmodifiableList(newList);
            }

            @Override
            public Object getRepeated(GeneratedMessageV3 message, int index) {
                if (this.supportUnknownEnumValue) {
                    int value = (Integer)GeneratedMessageV3.invokeOrDie(this.getRepeatedValueMethod, message, new Object[]{index});
                    return this.enumDescriptor.findValueByNumberCreatingIfUnknown(value);
                }
                return GeneratedMessageV3.invokeOrDie(this.getValueDescriptorMethod, super.getRepeated(message, index), new Object[0]);
            }

            @Override
            public Object getRepeated(Builder builder, int index) {
                if (this.supportUnknownEnumValue) {
                    int value = (Integer)GeneratedMessageV3.invokeOrDie(this.getRepeatedValueMethodBuilder, builder, new Object[]{index});
                    return this.enumDescriptor.findValueByNumberCreatingIfUnknown(value);
                }
                return GeneratedMessageV3.invokeOrDie(this.getValueDescriptorMethod, super.getRepeated(builder, index), new Object[0]);
            }

            @Override
            public void setRepeated(Builder builder, int index, Object value) {
                if (this.supportUnknownEnumValue) {
                    GeneratedMessageV3.invokeOrDie(this.setRepeatedValueMethod, builder, new Object[]{index, ((Descriptors.EnumValueDescriptor)value).getNumber()});
                    return;
                }
                super.setRepeated(builder, index, GeneratedMessageV3.invokeOrDie(this.valueOfMethod, null, new Object[]{value}));
            }

            @Override
            public void addRepeated(Builder builder, Object value) {
                if (this.supportUnknownEnumValue) {
                    GeneratedMessageV3.invokeOrDie(this.addRepeatedValueMethod, builder, new Object[]{((Descriptors.EnumValueDescriptor)value).getNumber()});
                    return;
                }
                super.addRepeated(builder, GeneratedMessageV3.invokeOrDie(this.valueOfMethod, null, new Object[]{value}));
            }
        }

        private static final class SingularEnumFieldAccessor
        extends SingularFieldAccessor {
            private Descriptors.EnumDescriptor enumDescriptor;
            private Method valueOfMethod;
            private Method getValueDescriptorMethod;
            private boolean supportUnknownEnumValue;
            private Method getValueMethod;
            private Method getValueMethodBuilder;
            private Method setValueMethod;

            SingularEnumFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessageV3> messageClass, Class<? extends Builder> builderClass, String containingOneofCamelCaseName) {
                super(descriptor, camelCaseName, messageClass, builderClass, containingOneofCamelCaseName);
                this.enumDescriptor = descriptor.getEnumType();
                this.valueOfMethod = GeneratedMessageV3.getMethodOrDie(this.type, "valueOf", new Class[]{Descriptors.EnumValueDescriptor.class});
                this.getValueDescriptorMethod = GeneratedMessageV3.getMethodOrDie(this.type, "getValueDescriptor", new Class[0]);
                this.supportUnknownEnumValue = descriptor.getFile().supportsUnknownEnumValue();
                if (this.supportUnknownEnumValue) {
                    this.getValueMethod = GeneratedMessageV3.getMethodOrDie(messageClass, "get" + camelCaseName + "Value", new Class[0]);
                    this.getValueMethodBuilder = GeneratedMessageV3.getMethodOrDie(builderClass, "get" + camelCaseName + "Value", new Class[0]);
                    this.setValueMethod = GeneratedMessageV3.getMethodOrDie(builderClass, "set" + camelCaseName + "Value", new Class[]{Integer.TYPE});
                }
            }

            @Override
            public Object get(GeneratedMessageV3 message) {
                if (this.supportUnknownEnumValue) {
                    int value = (Integer)GeneratedMessageV3.invokeOrDie(this.getValueMethod, message, new Object[0]);
                    return this.enumDescriptor.findValueByNumberCreatingIfUnknown(value);
                }
                return GeneratedMessageV3.invokeOrDie(this.getValueDescriptorMethod, super.get(message), new Object[0]);
            }

            @Override
            public Object get(Builder builder) {
                if (this.supportUnknownEnumValue) {
                    int value = (Integer)GeneratedMessageV3.invokeOrDie(this.getValueMethodBuilder, builder, new Object[0]);
                    return this.enumDescriptor.findValueByNumberCreatingIfUnknown(value);
                }
                return GeneratedMessageV3.invokeOrDie(this.getValueDescriptorMethod, super.get(builder), new Object[0]);
            }

            @Override
            public void set(Builder builder, Object value) {
                if (this.supportUnknownEnumValue) {
                    GeneratedMessageV3.invokeOrDie(this.setValueMethod, builder, new Object[]{((Descriptors.EnumValueDescriptor)value).getNumber()});
                    return;
                }
                super.set(builder, GeneratedMessageV3.invokeOrDie(this.valueOfMethod, null, new Object[]{value}));
            }
        }

        private static class MapFieldAccessor
        implements FieldAccessor {
            private final Descriptors.FieldDescriptor field;
            private final Message mapEntryMessageDefaultInstance;

            MapFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessageV3> messageClass, Class<? extends Builder> builderClass) {
                this.field = descriptor;
                Method getDefaultInstanceMethod = GeneratedMessageV3.getMethodOrDie(messageClass, "getDefaultInstance", new Class[0]);
                MapField<?, ?> defaultMapField = this.getMapField((GeneratedMessageV3)GeneratedMessageV3.invokeOrDie(getDefaultInstanceMethod, null, new Object[0]));
                this.mapEntryMessageDefaultInstance = defaultMapField.getMapEntryMessageDefaultInstance();
            }

            private MapField<?, ?> getMapField(GeneratedMessageV3 message) {
                return message.internalGetMapField(this.field.getNumber());
            }

            private MapField<?, ?> getMapField(Builder builder) {
                return builder.internalGetMapField(this.field.getNumber());
            }

            private MapField<?, ?> getMutableMapField(Builder builder) {
                return builder.internalGetMutableMapField(this.field.getNumber());
            }

            private Message coerceType(Message value) {
                if (value == null) {
                    return null;
                }
                if (this.mapEntryMessageDefaultInstance.getClass().isInstance(value)) {
                    return value;
                }
                return this.mapEntryMessageDefaultInstance.toBuilder().mergeFrom(value).build();
            }

            @Override
            public Object get(GeneratedMessageV3 message) {
                ArrayList<Object> result = new ArrayList<Object>();
                for (int i = 0; i < this.getRepeatedCount(message); ++i) {
                    result.add(this.getRepeated(message, i));
                }
                return Collections.unmodifiableList(result);
            }

            @Override
            public Object get(Builder builder) {
                ArrayList<Object> result = new ArrayList<Object>();
                for (int i = 0; i < this.getRepeatedCount(builder); ++i) {
                    result.add(this.getRepeated(builder, i));
                }
                return Collections.unmodifiableList(result);
            }

            @Override
            public Object getRaw(GeneratedMessageV3 message) {
                return this.get(message);
            }

            @Override
            public Object getRaw(Builder builder) {
                return this.get(builder);
            }

            @Override
            public void set(Builder builder, Object value) {
                this.clear(builder);
                for (Object entry : (List)value) {
                    this.addRepeated(builder, entry);
                }
            }

            @Override
            public Object getRepeated(GeneratedMessageV3 message, int index) {
                return this.getMapField(message).getList().get(index);
            }

            @Override
            public Object getRepeated(Builder builder, int index) {
                return this.getMapField(builder).getList().get(index);
            }

            @Override
            public Object getRepeatedRaw(GeneratedMessageV3 message, int index) {
                return this.getRepeated(message, index);
            }

            @Override
            public Object getRepeatedRaw(Builder builder, int index) {
                return this.getRepeated(builder, index);
            }

            @Override
            public void setRepeated(Builder builder, int index, Object value) {
                this.getMutableMapField(builder).getMutableList().set(index, this.coerceType((Message)value));
            }

            @Override
            public void addRepeated(Builder builder, Object value) {
                this.getMutableMapField(builder).getMutableList().add(this.coerceType((Message)value));
            }

            @Override
            public boolean has(GeneratedMessageV3 message) {
                throw new UnsupportedOperationException("hasField() is not supported for repeated fields.");
            }

            @Override
            public boolean has(Builder builder) {
                throw new UnsupportedOperationException("hasField() is not supported for repeated fields.");
            }

            @Override
            public int getRepeatedCount(GeneratedMessageV3 message) {
                return this.getMapField(message).getList().size();
            }

            @Override
            public int getRepeatedCount(Builder builder) {
                return this.getMapField(builder).getList().size();
            }

            @Override
            public void clear(Builder builder) {
                this.getMutableMapField(builder).getMutableList().clear();
            }

            @Override
            public Message.Builder newBuilder() {
                return this.mapEntryMessageDefaultInstance.newBuilderForType();
            }

            @Override
            public Message.Builder getBuilder(Builder builder) {
                throw new UnsupportedOperationException("Nested builder not supported for map fields.");
            }

            @Override
            public Message.Builder getRepeatedBuilder(Builder builder, int index) {
                throw new UnsupportedOperationException("Nested builder not supported for map fields.");
            }
        }

        private static class RepeatedFieldAccessor
        implements FieldAccessor {
            protected final Class type;
            protected final MethodInvoker invoker;

            RepeatedFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessageV3> messageClass, Class<? extends Builder> builderClass) {
                ReflectionInvoker reflectionInvoker = new ReflectionInvoker(descriptor, camelCaseName, messageClass, builderClass);
                this.type = reflectionInvoker.getRepeatedMethod.getReturnType();
                this.invoker = RepeatedFieldAccessor.getMethodInvoker(reflectionInvoker);
            }

            static MethodInvoker getMethodInvoker(ReflectionInvoker accessor) {
                return accessor;
            }

            @Override
            public Object get(GeneratedMessageV3 message) {
                return this.invoker.get(message);
            }

            @Override
            public Object get(Builder builder) {
                return this.invoker.get(builder);
            }

            @Override
            public Object getRaw(GeneratedMessageV3 message) {
                return this.get(message);
            }

            @Override
            public Object getRaw(Builder builder) {
                return this.get(builder);
            }

            @Override
            public void set(Builder builder, Object value) {
                this.clear(builder);
                for (Object element : (List)value) {
                    this.addRepeated(builder, element);
                }
            }

            @Override
            public Object getRepeated(GeneratedMessageV3 message, int index) {
                return this.invoker.getRepeated(message, index);
            }

            @Override
            public Object getRepeated(Builder builder, int index) {
                return this.invoker.getRepeated(builder, index);
            }

            @Override
            public Object getRepeatedRaw(GeneratedMessageV3 message, int index) {
                return this.getRepeated(message, index);
            }

            @Override
            public Object getRepeatedRaw(Builder builder, int index) {
                return this.getRepeated(builder, index);
            }

            @Override
            public void setRepeated(Builder builder, int index, Object value) {
                this.invoker.setRepeated(builder, index, value);
            }

            @Override
            public void addRepeated(Builder builder, Object value) {
                this.invoker.addRepeated(builder, value);
            }

            @Override
            public boolean has(GeneratedMessageV3 message) {
                throw new UnsupportedOperationException("hasField() called on a repeated field.");
            }

            @Override
            public boolean has(Builder builder) {
                throw new UnsupportedOperationException("hasField() called on a repeated field.");
            }

            @Override
            public int getRepeatedCount(GeneratedMessageV3 message) {
                return this.invoker.getRepeatedCount(message);
            }

            @Override
            public int getRepeatedCount(Builder builder) {
                return this.invoker.getRepeatedCount(builder);
            }

            @Override
            public void clear(Builder builder) {
                this.invoker.clear(builder);
            }

            @Override
            public Message.Builder newBuilder() {
                throw new UnsupportedOperationException("newBuilderForField() called on a non-Message type.");
            }

            @Override
            public Message.Builder getBuilder(Builder builder) {
                throw new UnsupportedOperationException("getFieldBuilder() called on a non-Message type.");
            }

            @Override
            public Message.Builder getRepeatedBuilder(Builder builder, int index) {
                throw new UnsupportedOperationException("getRepeatedFieldBuilder() called on a non-Message type.");
            }

            private static final class ReflectionInvoker
            implements MethodInvoker {
                protected final Method getMethod;
                protected final Method getMethodBuilder;
                protected final Method getRepeatedMethod;
                protected final Method getRepeatedMethodBuilder;
                protected final Method setRepeatedMethod;
                protected final Method addRepeatedMethod;
                protected final Method getCountMethod;
                protected final Method getCountMethodBuilder;
                protected final Method clearMethod;

                ReflectionInvoker(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessageV3> messageClass, Class<? extends Builder> builderClass) {
                    this.getMethod = GeneratedMessageV3.getMethodOrDie(messageClass, "get" + camelCaseName + "List", new Class[0]);
                    this.getMethodBuilder = GeneratedMessageV3.getMethodOrDie(builderClass, "get" + camelCaseName + "List", new Class[0]);
                    this.getRepeatedMethod = GeneratedMessageV3.getMethodOrDie(messageClass, "get" + camelCaseName, new Class[]{Integer.TYPE});
                    this.getRepeatedMethodBuilder = GeneratedMessageV3.getMethodOrDie(builderClass, "get" + camelCaseName, new Class[]{Integer.TYPE});
                    Class<?> type = this.getRepeatedMethod.getReturnType();
                    this.setRepeatedMethod = GeneratedMessageV3.getMethodOrDie(builderClass, "set" + camelCaseName, new Class[]{Integer.TYPE, type});
                    this.addRepeatedMethod = GeneratedMessageV3.getMethodOrDie(builderClass, "add" + camelCaseName, new Class[]{type});
                    this.getCountMethod = GeneratedMessageV3.getMethodOrDie(messageClass, "get" + camelCaseName + "Count", new Class[0]);
                    this.getCountMethodBuilder = GeneratedMessageV3.getMethodOrDie(builderClass, "get" + camelCaseName + "Count", new Class[0]);
                    this.clearMethod = GeneratedMessageV3.getMethodOrDie(builderClass, "clear" + camelCaseName, new Class[0]);
                }

                @Override
                public Object get(GeneratedMessageV3 message) {
                    return GeneratedMessageV3.invokeOrDie(this.getMethod, message, new Object[0]);
                }

                @Override
                public Object get(Builder<?> builder) {
                    return GeneratedMessageV3.invokeOrDie(this.getMethodBuilder, builder, new Object[0]);
                }

                @Override
                public Object getRepeated(GeneratedMessageV3 message, int index) {
                    return GeneratedMessageV3.invokeOrDie(this.getRepeatedMethod, message, new Object[]{index});
                }

                @Override
                public Object getRepeated(Builder<?> builder, int index) {
                    return GeneratedMessageV3.invokeOrDie(this.getRepeatedMethodBuilder, builder, new Object[]{index});
                }

                @Override
                public void setRepeated(Builder<?> builder, int index, Object value) {
                    GeneratedMessageV3.invokeOrDie(this.setRepeatedMethod, builder, new Object[]{index, value});
                }

                @Override
                public void addRepeated(Builder<?> builder, Object value) {
                    GeneratedMessageV3.invokeOrDie(this.addRepeatedMethod, builder, new Object[]{value});
                }

                @Override
                public int getRepeatedCount(GeneratedMessageV3 message) {
                    return (Integer)GeneratedMessageV3.invokeOrDie(this.getCountMethod, message, new Object[0]);
                }

                @Override
                public int getRepeatedCount(Builder<?> builder) {
                    return (Integer)GeneratedMessageV3.invokeOrDie(this.getCountMethodBuilder, builder, new Object[0]);
                }

                @Override
                public void clear(Builder<?> builder) {
                    GeneratedMessageV3.invokeOrDie(this.clearMethod, builder, new Object[0]);
                }
            }

            static interface MethodInvoker {
                public Object get(GeneratedMessageV3 var1);

                public Object get(Builder<?> var1);

                public Object getRepeated(GeneratedMessageV3 var1, int var2);

                public Object getRepeated(Builder<?> var1, int var2);

                public void setRepeated(Builder<?> var1, int var2, Object var3);

                public void addRepeated(Builder<?> var1, Object var2);

                public int getRepeatedCount(GeneratedMessageV3 var1);

                public int getRepeatedCount(Builder<?> var1);

                public void clear(Builder<?> var1);
            }
        }

        private static class SingularFieldAccessor
        implements FieldAccessor {
            protected final Class<?> type;
            protected final Descriptors.FieldDescriptor field;
            protected final boolean isOneofField;
            protected final boolean hasHasMethod;
            protected final MethodInvoker invoker;

            SingularFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessageV3> messageClass, Class<? extends Builder> builderClass, String containingOneofCamelCaseName) {
                this.isOneofField = descriptor.getContainingOneof() != null;
                this.hasHasMethod = FieldAccessorTable.supportFieldPresence(descriptor.getFile()) || !this.isOneofField && descriptor.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE;
                ReflectionInvoker reflectionInvoker = new ReflectionInvoker(descriptor, camelCaseName, messageClass, builderClass, containingOneofCamelCaseName, this.isOneofField, this.hasHasMethod);
                this.field = descriptor;
                this.type = reflectionInvoker.getMethod.getReturnType();
                this.invoker = SingularFieldAccessor.getMethodInvoker(reflectionInvoker);
            }

            static MethodInvoker getMethodInvoker(ReflectionInvoker accessor) {
                return accessor;
            }

            @Override
            public Object get(GeneratedMessageV3 message) {
                return this.invoker.get(message);
            }

            @Override
            public Object get(Builder builder) {
                return this.invoker.get(builder);
            }

            @Override
            public Object getRaw(GeneratedMessageV3 message) {
                return this.get(message);
            }

            @Override
            public Object getRaw(Builder builder) {
                return this.get(builder);
            }

            @Override
            public void set(Builder builder, Object value) {
                this.invoker.set(builder, value);
            }

            @Override
            public Object getRepeated(GeneratedMessageV3 message, int index) {
                throw new UnsupportedOperationException("getRepeatedField() called on a singular field.");
            }

            @Override
            public Object getRepeatedRaw(GeneratedMessageV3 message, int index) {
                throw new UnsupportedOperationException("getRepeatedFieldRaw() called on a singular field.");
            }

            @Override
            public Object getRepeated(Builder builder, int index) {
                throw new UnsupportedOperationException("getRepeatedField() called on a singular field.");
            }

            @Override
            public Object getRepeatedRaw(Builder builder, int index) {
                throw new UnsupportedOperationException("getRepeatedFieldRaw() called on a singular field.");
            }

            @Override
            public void setRepeated(Builder builder, int index, Object value) {
                throw new UnsupportedOperationException("setRepeatedField() called on a singular field.");
            }

            @Override
            public void addRepeated(Builder builder, Object value) {
                throw new UnsupportedOperationException("addRepeatedField() called on a singular field.");
            }

            @Override
            public boolean has(GeneratedMessageV3 message) {
                if (!this.hasHasMethod) {
                    if (this.isOneofField) {
                        return this.invoker.getOneofFieldNumber(message) == this.field.getNumber();
                    }
                    return !this.get(message).equals(this.field.getDefaultValue());
                }
                return this.invoker.has(message);
            }

            @Override
            public boolean has(Builder builder) {
                if (!this.hasHasMethod) {
                    if (this.isOneofField) {
                        return this.invoker.getOneofFieldNumber(builder) == this.field.getNumber();
                    }
                    return !this.get(builder).equals(this.field.getDefaultValue());
                }
                return this.invoker.has(builder);
            }

            @Override
            public int getRepeatedCount(GeneratedMessageV3 message) {
                throw new UnsupportedOperationException("getRepeatedFieldSize() called on a singular field.");
            }

            @Override
            public int getRepeatedCount(Builder builder) {
                throw new UnsupportedOperationException("getRepeatedFieldSize() called on a singular field.");
            }

            @Override
            public void clear(Builder builder) {
                this.invoker.clear(builder);
            }

            @Override
            public Message.Builder newBuilder() {
                throw new UnsupportedOperationException("newBuilderForField() called on a non-Message type.");
            }

            @Override
            public Message.Builder getBuilder(Builder builder) {
                throw new UnsupportedOperationException("getFieldBuilder() called on a non-Message type.");
            }

            @Override
            public Message.Builder getRepeatedBuilder(Builder builder, int index) {
                throw new UnsupportedOperationException("getRepeatedFieldBuilder() called on a non-Message type.");
            }

            private static final class ReflectionInvoker
            implements MethodInvoker {
                protected final Method getMethod;
                protected final Method getMethodBuilder;
                protected final Method setMethod;
                protected final Method hasMethod;
                protected final Method hasMethodBuilder;
                protected final Method clearMethod;
                protected final Method caseMethod;
                protected final Method caseMethodBuilder;

                ReflectionInvoker(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessageV3> messageClass, Class<? extends Builder> builderClass, String containingOneofCamelCaseName, boolean isOneofField, boolean hasHasMethod) {
                    this.getMethod = GeneratedMessageV3.getMethodOrDie(messageClass, "get" + camelCaseName, new Class[0]);
                    this.getMethodBuilder = GeneratedMessageV3.getMethodOrDie(builderClass, "get" + camelCaseName, new Class[0]);
                    Class<?> type = this.getMethod.getReturnType();
                    this.setMethod = GeneratedMessageV3.getMethodOrDie(builderClass, "set" + camelCaseName, new Class[]{type});
                    this.hasMethod = hasHasMethod ? GeneratedMessageV3.getMethodOrDie(messageClass, "has" + camelCaseName, new Class[0]) : null;
                    this.hasMethodBuilder = hasHasMethod ? GeneratedMessageV3.getMethodOrDie(builderClass, "has" + camelCaseName, new Class[0]) : null;
                    this.clearMethod = GeneratedMessageV3.getMethodOrDie(builderClass, "clear" + camelCaseName, new Class[0]);
                    this.caseMethod = isOneofField ? GeneratedMessageV3.getMethodOrDie(messageClass, "get" + containingOneofCamelCaseName + "Case", new Class[0]) : null;
                    this.caseMethodBuilder = isOneofField ? GeneratedMessageV3.getMethodOrDie(builderClass, "get" + containingOneofCamelCaseName + "Case", new Class[0]) : null;
                }

                @Override
                public Object get(GeneratedMessageV3 message) {
                    return GeneratedMessageV3.invokeOrDie(this.getMethod, message, new Object[0]);
                }

                @Override
                public Object get(Builder<?> builder) {
                    return GeneratedMessageV3.invokeOrDie(this.getMethodBuilder, builder, new Object[0]);
                }

                @Override
                public int getOneofFieldNumber(GeneratedMessageV3 message) {
                    return ((Internal.EnumLite)GeneratedMessageV3.invokeOrDie(this.caseMethod, message, new Object[0])).getNumber();
                }

                @Override
                public int getOneofFieldNumber(Builder<?> builder) {
                    return ((Internal.EnumLite)GeneratedMessageV3.invokeOrDie(this.caseMethodBuilder, builder, new Object[0])).getNumber();
                }

                @Override
                public void set(Builder<?> builder, Object value) {
                    GeneratedMessageV3.invokeOrDie(this.setMethod, builder, new Object[]{value});
                }

                @Override
                public boolean has(GeneratedMessageV3 message) {
                    return (Boolean)GeneratedMessageV3.invokeOrDie(this.hasMethod, message, new Object[0]);
                }

                @Override
                public boolean has(Builder<?> builder) {
                    return (Boolean)GeneratedMessageV3.invokeOrDie(this.hasMethodBuilder, builder, new Object[0]);
                }

                @Override
                public void clear(Builder<?> builder) {
                    GeneratedMessageV3.invokeOrDie(this.clearMethod, builder, new Object[0]);
                }
            }

            private static interface MethodInvoker {
                public Object get(GeneratedMessageV3 var1);

                public Object get(Builder<?> var1);

                public int getOneofFieldNumber(GeneratedMessageV3 var1);

                public int getOneofFieldNumber(Builder<?> var1);

                public void set(Builder<?> var1, Object var2);

                public boolean has(GeneratedMessageV3 var1);

                public boolean has(Builder<?> var1);

                public void clear(Builder<?> var1);
            }
        }

        private static class OneofAccessor {
            private final Descriptors.Descriptor descriptor;
            private final Method caseMethod;
            private final Method caseMethodBuilder;
            private final Method clearMethod;

            OneofAccessor(Descriptors.Descriptor descriptor, String camelCaseName, Class<? extends GeneratedMessageV3> messageClass, Class<? extends Builder> builderClass) {
                this.descriptor = descriptor;
                this.caseMethod = GeneratedMessageV3.getMethodOrDie(messageClass, "get" + camelCaseName + "Case", new Class[0]);
                this.caseMethodBuilder = GeneratedMessageV3.getMethodOrDie(builderClass, "get" + camelCaseName + "Case", new Class[0]);
                this.clearMethod = GeneratedMessageV3.getMethodOrDie(builderClass, "clear" + camelCaseName, new Class[0]);
            }

            public boolean has(GeneratedMessageV3 message) {
                return ((Internal.EnumLite)GeneratedMessageV3.invokeOrDie(this.caseMethod, message, new Object[0])).getNumber() != 0;
            }

            public boolean has(Builder builder) {
                return ((Internal.EnumLite)GeneratedMessageV3.invokeOrDie(this.caseMethodBuilder, builder, new Object[0])).getNumber() != 0;
            }

            public Descriptors.FieldDescriptor get(GeneratedMessageV3 message) {
                int fieldNumber = ((Internal.EnumLite)GeneratedMessageV3.invokeOrDie(this.caseMethod, message, new Object[0])).getNumber();
                if (fieldNumber > 0) {
                    return this.descriptor.findFieldByNumber(fieldNumber);
                }
                return null;
            }

            public Descriptors.FieldDescriptor get(Builder builder) {
                int fieldNumber = ((Internal.EnumLite)GeneratedMessageV3.invokeOrDie(this.caseMethodBuilder, builder, new Object[0])).getNumber();
                if (fieldNumber > 0) {
                    return this.descriptor.findFieldByNumber(fieldNumber);
                }
                return null;
            }

            public void clear(Builder builder) {
                GeneratedMessageV3.invokeOrDie(this.clearMethod, builder, new Object[0]);
            }
        }

        private static interface FieldAccessor {
            public Object get(GeneratedMessageV3 var1);

            public Object get(Builder var1);

            public Object getRaw(GeneratedMessageV3 var1);

            public Object getRaw(Builder var1);

            public void set(Builder var1, Object var2);

            public Object getRepeated(GeneratedMessageV3 var1, int var2);

            public Object getRepeated(Builder var1, int var2);

            public Object getRepeatedRaw(GeneratedMessageV3 var1, int var2);

            public Object getRepeatedRaw(Builder var1, int var2);

            public void setRepeated(Builder var1, int var2, Object var3);

            public void addRepeated(Builder var1, Object var2);

            public boolean has(GeneratedMessageV3 var1);

            public boolean has(Builder var1);

            public int getRepeatedCount(GeneratedMessageV3 var1);

            public int getRepeatedCount(Builder var1);

            public void clear(Builder var1);

            public Message.Builder newBuilder();

            public Message.Builder getBuilder(Builder var1);

            public Message.Builder getRepeatedBuilder(Builder var1, int var2);
        }
    }

    static interface ExtensionDescriptorRetriever {
        public Descriptors.FieldDescriptor getDescriptor();
    }

    public static abstract class ExtendableBuilder<MessageType extends ExtendableMessage, BuilderType extends ExtendableBuilder<MessageType, BuilderType>>
    extends Builder<BuilderType>
    implements ExtendableMessageOrBuilder<MessageType> {
        private FieldSet.Builder<Descriptors.FieldDescriptor> extensions;

        protected ExtendableBuilder() {
        }

        protected ExtendableBuilder(BuilderParent parent) {
            super(parent);
        }

        void internalSetExtensionSet(FieldSet<Descriptors.FieldDescriptor> extensions) {
            this.extensions = FieldSet.Builder.fromFieldSet(extensions);
        }

        @Override
        public BuilderType clear() {
            this.extensions = null;
            return (BuilderType)((ExtendableBuilder)super.clear());
        }

        private void ensureExtensionsIsMutable() {
            if (this.extensions == null) {
                this.extensions = FieldSet.newBuilder();
            }
        }

        private void verifyExtensionContainingType(Extension<MessageType, ?> extension) {
            if (extension.getDescriptor().getContainingType() != this.getDescriptorForType()) {
                throw new IllegalArgumentException("Extension is for type \"" + extension.getDescriptor().getContainingType().getFullName() + "\" which does not match message type \"" + this.getDescriptorForType().getFullName() + "\".");
            }
        }

        @Override
        public final <Type> boolean hasExtension(ExtensionLite<MessageType, Type> extensionLite) {
            Extension extension = GeneratedMessageV3.checkNotLite(extensionLite);
            this.verifyExtensionContainingType(extension);
            return this.extensions == null ? false : this.extensions.hasField(extension.getDescriptor());
        }

        @Override
        public final <Type> int getExtensionCount(ExtensionLite<MessageType, List<Type>> extensionLite) {
            Extension extension = GeneratedMessageV3.checkNotLite(extensionLite);
            this.verifyExtensionContainingType(extension);
            Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
            return this.extensions == null ? 0 : this.extensions.getRepeatedFieldCount(descriptor);
        }

        @Override
        public final <Type> Type getExtension(ExtensionLite<MessageType, Type> extensionLite) {
            Object value;
            Extension extension = GeneratedMessageV3.checkNotLite(extensionLite);
            this.verifyExtensionContainingType(extension);
            Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
            Object object = value = this.extensions == null ? null : this.extensions.getField(descriptor);
            if (value == null) {
                if (descriptor.isRepeated()) {
                    return (Type)Collections.emptyList();
                }
                if (descriptor.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                    return (Type)extension.getMessageDefaultInstance();
                }
                return (Type)extension.fromReflectionType(descriptor.getDefaultValue());
            }
            return (Type)extension.fromReflectionType(value);
        }

        @Override
        public final <Type> Type getExtension(ExtensionLite<MessageType, List<Type>> extensionLite, int index) {
            Extension extension = GeneratedMessageV3.checkNotLite(extensionLite);
            this.verifyExtensionContainingType(extension);
            Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
            if (this.extensions == null) {
                throw new IndexOutOfBoundsException();
            }
            return (Type)extension.singularFromReflectionType(this.extensions.getRepeatedField(descriptor, index));
        }

        public final <Type> BuilderType setExtension(ExtensionLite<MessageType, Type> extensionLite, Type value) {
            Extension extension = GeneratedMessageV3.checkNotLite(extensionLite);
            this.verifyExtensionContainingType(extension);
            this.ensureExtensionsIsMutable();
            Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
            this.extensions.setField(descriptor, extension.toReflectionType(value));
            this.onChanged();
            return (BuilderType)this;
        }

        public final <Type> BuilderType setExtension(ExtensionLite<MessageType, List<Type>> extensionLite, int index, Type value) {
            Extension extension = GeneratedMessageV3.checkNotLite(extensionLite);
            this.verifyExtensionContainingType(extension);
            this.ensureExtensionsIsMutable();
            Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
            this.extensions.setRepeatedField(descriptor, index, extension.singularToReflectionType(value));
            this.onChanged();
            return (BuilderType)this;
        }

        public final <Type> BuilderType addExtension(ExtensionLite<MessageType, List<Type>> extensionLite, Type value) {
            Extension extension = GeneratedMessageV3.checkNotLite(extensionLite);
            this.verifyExtensionContainingType(extension);
            this.ensureExtensionsIsMutable();
            Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
            this.extensions.addRepeatedField(descriptor, extension.singularToReflectionType(value));
            this.onChanged();
            return (BuilderType)this;
        }

        public final BuilderType clearExtension(ExtensionLite<MessageType, ?> extensionLite) {
            Extension extension = GeneratedMessageV3.checkNotLite(extensionLite);
            this.verifyExtensionContainingType(extension);
            this.ensureExtensionsIsMutable();
            this.extensions.clearField(extension.getDescriptor());
            this.onChanged();
            return (BuilderType)this;
        }

        @Override
        public final <Type> boolean hasExtension(Extension<MessageType, Type> extension) {
            return this.hasExtension((ExtensionLite<MessageType, Type>)extension);
        }

        @Override
        public final <Type> boolean hasExtension(GeneratedMessage.GeneratedExtension<MessageType, Type> extension) {
            return this.hasExtension((ExtensionLite<MessageType, Type>)extension);
        }

        @Override
        public final <Type> int getExtensionCount(Extension<MessageType, List<Type>> extension) {
            return this.getExtensionCount((ExtensionLite<MessageType, List<Type>>)extension);
        }

        @Override
        public final <Type> int getExtensionCount(GeneratedMessage.GeneratedExtension<MessageType, List<Type>> extension) {
            return this.getExtensionCount((ExtensionLite<MessageType, List<Type>>)extension);
        }

        @Override
        public final <Type> Type getExtension(Extension<MessageType, Type> extension) {
            return this.getExtension((ExtensionLite<MessageType, Type>)extension);
        }

        @Override
        public final <Type> Type getExtension(GeneratedMessage.GeneratedExtension<MessageType, Type> extension) {
            return this.getExtension((ExtensionLite<MessageType, Type>)extension);
        }

        @Override
        public final <Type> Type getExtension(Extension<MessageType, List<Type>> extension, int index) {
            return this.getExtension((ExtensionLite<MessageType, List<Type>>)extension, index);
        }

        @Override
        public final <Type> Type getExtension(GeneratedMessage.GeneratedExtension<MessageType, List<Type>> extension, int index) {
            return this.getExtension((ExtensionLite<MessageType, List<Type>>)extension, index);
        }

        public final <Type> BuilderType setExtension(Extension<MessageType, Type> extension, Type value) {
            return this.setExtension((ExtensionLite<MessageType, Type>)extension, value);
        }

        public <Type> BuilderType setExtension(GeneratedMessage.GeneratedExtension<MessageType, Type> extension, Type value) {
            return this.setExtension((ExtensionLite<MessageType, Type>)extension, value);
        }

        public final <Type> BuilderType setExtension(Extension<MessageType, List<Type>> extension, int index, Type value) {
            return this.setExtension((ExtensionLite<MessageType, List<Type>>)extension, index, value);
        }

        public <Type> BuilderType setExtension(GeneratedMessage.GeneratedExtension<MessageType, List<Type>> extension, int index, Type value) {
            return this.setExtension((ExtensionLite<MessageType, List<Type>>)extension, index, value);
        }

        public final <Type> BuilderType addExtension(Extension<MessageType, List<Type>> extension, Type value) {
            return this.addExtension((ExtensionLite<MessageType, List<Type>>)extension, value);
        }

        public <Type> BuilderType addExtension(GeneratedMessage.GeneratedExtension<MessageType, List<Type>> extension, Type value) {
            return this.addExtension((ExtensionLite<MessageType, List<Type>>)extension, value);
        }

        public final <Type> BuilderType clearExtension(Extension<MessageType, ?> extension) {
            return this.clearExtension((ExtensionLite<MessageType, ?>)extension);
        }

        public <Type> BuilderType clearExtension(GeneratedMessage.GeneratedExtension<MessageType, ?> extension) {
            return this.clearExtension((ExtensionLite<MessageType, ?>)extension);
        }

        protected boolean extensionsAreInitialized() {
            return this.extensions == null ? true : this.extensions.isInitialized();
        }

        private FieldSet<Descriptors.FieldDescriptor> buildExtensions() {
            return this.extensions == null ? FieldSet.emptySet() : this.extensions.build();
        }

        @Override
        public boolean isInitialized() {
            return super.isInitialized() && this.extensionsAreInitialized();
        }

        @Override
        public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
            Map result = ((Builder)this).getAllFieldsMutable();
            if (this.extensions != null) {
                result.putAll(this.extensions.getAllFields());
            }
            return Collections.unmodifiableMap(result);
        }

        @Override
        public Object getField(Descriptors.FieldDescriptor field) {
            if (field.isExtension()) {
                Object value;
                this.verifyContainingType(field);
                Object object = value = this.extensions == null ? null : this.extensions.getField(field);
                if (value == null) {
                    if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                        return DynamicMessage.getDefaultInstance(field.getMessageType());
                    }
                    return field.getDefaultValue();
                }
                return value;
            }
            return super.getField(field);
        }

        @Override
        public Message.Builder getFieldBuilder(Descriptors.FieldDescriptor field) {
            if (field.isExtension()) {
                this.verifyContainingType(field);
                if (field.getJavaType() != Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                    throw new UnsupportedOperationException("getFieldBuilder() called on a non-Message type.");
                }
                this.ensureExtensionsIsMutable();
                Object value = this.extensions.getFieldAllowBuilders(field);
                if (value == null) {
                    DynamicMessage.Builder builder = DynamicMessage.newBuilder(field.getMessageType());
                    this.extensions.setField(field, builder);
                    this.onChanged();
                    return builder;
                }
                if (value instanceof Message.Builder) {
                    return (Message.Builder)value;
                }
                if (value instanceof Message) {
                    Message.Builder builder = ((Message)value).toBuilder();
                    this.extensions.setField(field, builder);
                    this.onChanged();
                    return builder;
                }
                throw new UnsupportedOperationException("getRepeatedFieldBuilder() called on a non-Message type.");
            }
            return super.getFieldBuilder(field);
        }

        @Override
        public int getRepeatedFieldCount(Descriptors.FieldDescriptor field) {
            if (field.isExtension()) {
                this.verifyContainingType(field);
                return this.extensions == null ? 0 : this.extensions.getRepeatedFieldCount(field);
            }
            return super.getRepeatedFieldCount(field);
        }

        @Override
        public Object getRepeatedField(Descriptors.FieldDescriptor field, int index) {
            if (field.isExtension()) {
                this.verifyContainingType(field);
                if (this.extensions == null) {
                    throw new IndexOutOfBoundsException();
                }
                return this.extensions.getRepeatedField(field, index);
            }
            return super.getRepeatedField(field, index);
        }

        @Override
        public Message.Builder getRepeatedFieldBuilder(Descriptors.FieldDescriptor field, int index) {
            if (field.isExtension()) {
                this.verifyContainingType(field);
                this.ensureExtensionsIsMutable();
                if (field.getJavaType() != Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                    throw new UnsupportedOperationException("getRepeatedFieldBuilder() called on a non-Message type.");
                }
                Object value = this.extensions.getRepeatedFieldAllowBuilders(field, index);
                if (value instanceof Message.Builder) {
                    return (Message.Builder)value;
                }
                if (value instanceof Message) {
                    Message.Builder builder = ((Message)value).toBuilder();
                    this.extensions.setRepeatedField(field, index, builder);
                    this.onChanged();
                    return builder;
                }
                throw new UnsupportedOperationException("getRepeatedFieldBuilder() called on a non-Message type.");
            }
            return super.getRepeatedFieldBuilder(field, index);
        }

        @Override
        public boolean hasField(Descriptors.FieldDescriptor field) {
            if (field.isExtension()) {
                this.verifyContainingType(field);
                return this.extensions == null ? false : this.extensions.hasField(field);
            }
            return super.hasField(field);
        }

        @Override
        public BuilderType setField(Descriptors.FieldDescriptor field, Object value) {
            if (field.isExtension()) {
                this.verifyContainingType(field);
                this.ensureExtensionsIsMutable();
                this.extensions.setField(field, value);
                this.onChanged();
                return (BuilderType)this;
            }
            return (BuilderType)((ExtendableBuilder)super.setField(field, value));
        }

        @Override
        public BuilderType clearField(Descriptors.FieldDescriptor field) {
            if (field.isExtension()) {
                this.verifyContainingType(field);
                this.ensureExtensionsIsMutable();
                this.extensions.clearField(field);
                this.onChanged();
                return (BuilderType)this;
            }
            return (BuilderType)((ExtendableBuilder)super.clearField(field));
        }

        @Override
        public BuilderType setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            if (field.isExtension()) {
                this.verifyContainingType(field);
                this.ensureExtensionsIsMutable();
                this.extensions.setRepeatedField(field, index, value);
                this.onChanged();
                return (BuilderType)this;
            }
            return (BuilderType)((ExtendableBuilder)super.setRepeatedField(field, index, value));
        }

        @Override
        public BuilderType addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            if (field.isExtension()) {
                this.verifyContainingType(field);
                this.ensureExtensionsIsMutable();
                this.extensions.addRepeatedField(field, value);
                this.onChanged();
                return (BuilderType)this;
            }
            return (BuilderType)((ExtendableBuilder)super.addRepeatedField(field, value));
        }

        @Override
        public Message.Builder newBuilderForField(Descriptors.FieldDescriptor field) {
            if (field.isExtension()) {
                return DynamicMessage.newBuilder(field.getMessageType());
            }
            return super.newBuilderForField(field);
        }

        protected final void mergeExtensionFields(ExtendableMessage other) {
            if (other.extensions != null) {
                this.ensureExtensionsIsMutable();
                this.extensions.mergeFrom(other.extensions);
                this.onChanged();
            }
        }

        private void verifyContainingType(Descriptors.FieldDescriptor field) {
            if (field.getContainingType() != this.getDescriptorForType()) {
                throw new IllegalArgumentException("FieldDescriptor does not match message type.");
            }
        }
    }

    public static abstract class ExtendableMessage<MessageType extends ExtendableMessage>
    extends GeneratedMessageV3
    implements ExtendableMessageOrBuilder<MessageType> {
        private static final long serialVersionUID = 1L;
        private final FieldSet<Descriptors.FieldDescriptor> extensions;

        protected ExtendableMessage() {
            this.extensions = FieldSet.newFieldSet();
        }

        protected ExtendableMessage(ExtendableBuilder<MessageType, ?> builder) {
            super(builder);
            this.extensions = ((ExtendableBuilder)builder).buildExtensions();
        }

        private void verifyExtensionContainingType(Extension<MessageType, ?> extension) {
            if (extension.getDescriptor().getContainingType() != this.getDescriptorForType()) {
                throw new IllegalArgumentException("Extension is for type \"" + extension.getDescriptor().getContainingType().getFullName() + "\" which does not match message type \"" + this.getDescriptorForType().getFullName() + "\".");
            }
        }

        @Override
        public final <Type> boolean hasExtension(ExtensionLite<MessageType, Type> extensionLite) {
            Extension extension = GeneratedMessageV3.checkNotLite(extensionLite);
            this.verifyExtensionContainingType(extension);
            return this.extensions.hasField(extension.getDescriptor());
        }

        @Override
        public final <Type> int getExtensionCount(ExtensionLite<MessageType, List<Type>> extensionLite) {
            Extension extension = GeneratedMessageV3.checkNotLite(extensionLite);
            this.verifyExtensionContainingType(extension);
            Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
            return this.extensions.getRepeatedFieldCount(descriptor);
        }

        @Override
        public final <Type> Type getExtension(ExtensionLite<MessageType, Type> extensionLite) {
            Extension extension = GeneratedMessageV3.checkNotLite(extensionLite);
            this.verifyExtensionContainingType(extension);
            Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
            Object value = this.extensions.getField(descriptor);
            if (value == null) {
                if (descriptor.isRepeated()) {
                    return (Type)Collections.emptyList();
                }
                if (descriptor.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                    return (Type)extension.getMessageDefaultInstance();
                }
                return (Type)extension.fromReflectionType(descriptor.getDefaultValue());
            }
            return (Type)extension.fromReflectionType(value);
        }

        @Override
        public final <Type> Type getExtension(ExtensionLite<MessageType, List<Type>> extensionLite, int index) {
            Extension extension = GeneratedMessageV3.checkNotLite(extensionLite);
            this.verifyExtensionContainingType(extension);
            Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
            return (Type)extension.singularFromReflectionType(this.extensions.getRepeatedField(descriptor, index));
        }

        @Override
        public final <Type> boolean hasExtension(Extension<MessageType, Type> extension) {
            return this.hasExtension((ExtensionLite<MessageType, Type>)extension);
        }

        @Override
        public final <Type> boolean hasExtension(GeneratedMessage.GeneratedExtension<MessageType, Type> extension) {
            return this.hasExtension((ExtensionLite<MessageType, Type>)extension);
        }

        @Override
        public final <Type> int getExtensionCount(Extension<MessageType, List<Type>> extension) {
            return this.getExtensionCount((ExtensionLite<MessageType, List<Type>>)extension);
        }

        @Override
        public final <Type> int getExtensionCount(GeneratedMessage.GeneratedExtension<MessageType, List<Type>> extension) {
            return this.getExtensionCount((ExtensionLite<MessageType, List<Type>>)extension);
        }

        @Override
        public final <Type> Type getExtension(Extension<MessageType, Type> extension) {
            return this.getExtension((ExtensionLite<MessageType, Type>)extension);
        }

        @Override
        public final <Type> Type getExtension(GeneratedMessage.GeneratedExtension<MessageType, Type> extension) {
            return this.getExtension((ExtensionLite<MessageType, Type>)extension);
        }

        @Override
        public final <Type> Type getExtension(Extension<MessageType, List<Type>> extension, int index) {
            return this.getExtension((ExtensionLite<MessageType, List<Type>>)extension, index);
        }

        @Override
        public final <Type> Type getExtension(GeneratedMessage.GeneratedExtension<MessageType, List<Type>> extension, int index) {
            return this.getExtension((ExtensionLite<MessageType, List<Type>>)extension, index);
        }

        protected boolean extensionsAreInitialized() {
            return this.extensions.isInitialized();
        }

        @Override
        public boolean isInitialized() {
            return super.isInitialized() && this.extensionsAreInitialized();
        }

        @Override
        protected boolean parseUnknownField(CodedInputStream input, UnknownFieldSet.Builder unknownFields, ExtensionRegistryLite extensionRegistry, int tag) throws IOException {
            return MessageReflection.mergeFieldFrom(input, input.shouldDiscardUnknownFields() ? null : unknownFields, extensionRegistry, this.getDescriptorForType(), new MessageReflection.ExtensionAdapter(this.extensions), tag);
        }

        @Override
        protected boolean parseUnknownFieldProto3(CodedInputStream input, UnknownFieldSet.Builder unknownFields, ExtensionRegistryLite extensionRegistry, int tag) throws IOException {
            return this.parseUnknownField(input, unknownFields, extensionRegistry, tag);
        }

        @Override
        protected void makeExtensionsImmutable() {
            this.extensions.makeImmutable();
        }

        protected ExtensionWriter newExtensionWriter() {
            return new ExtensionWriter(false);
        }

        protected ExtensionWriter newMessageSetExtensionWriter() {
            return new ExtensionWriter(true);
        }

        protected int extensionsSerializedSize() {
            return this.extensions.getSerializedSize();
        }

        protected int extensionsSerializedSizeAsMessageSet() {
            return this.extensions.getMessageSetSerializedSize();
        }

        protected Map<Descriptors.FieldDescriptor, Object> getExtensionFields() {
            return this.extensions.getAllFields();
        }

        @Override
        public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
            Map result = ((GeneratedMessageV3)this).getAllFieldsMutable(false);
            result.putAll(this.getExtensionFields());
            return Collections.unmodifiableMap(result);
        }

        @Override
        public Map<Descriptors.FieldDescriptor, Object> getAllFieldsRaw() {
            Map result = ((GeneratedMessageV3)this).getAllFieldsMutable(false);
            result.putAll(this.getExtensionFields());
            return Collections.unmodifiableMap(result);
        }

        @Override
        public boolean hasField(Descriptors.FieldDescriptor field) {
            if (field.isExtension()) {
                this.verifyContainingType(field);
                return this.extensions.hasField(field);
            }
            return super.hasField(field);
        }

        @Override
        public Object getField(Descriptors.FieldDescriptor field) {
            if (field.isExtension()) {
                this.verifyContainingType(field);
                Object value = this.extensions.getField(field);
                if (value == null) {
                    if (field.isRepeated()) {
                        return Collections.emptyList();
                    }
                    if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                        return DynamicMessage.getDefaultInstance(field.getMessageType());
                    }
                    return field.getDefaultValue();
                }
                return value;
            }
            return super.getField(field);
        }

        @Override
        public int getRepeatedFieldCount(Descriptors.FieldDescriptor field) {
            if (field.isExtension()) {
                this.verifyContainingType(field);
                return this.extensions.getRepeatedFieldCount(field);
            }
            return super.getRepeatedFieldCount(field);
        }

        @Override
        public Object getRepeatedField(Descriptors.FieldDescriptor field, int index) {
            if (field.isExtension()) {
                this.verifyContainingType(field);
                return this.extensions.getRepeatedField(field, index);
            }
            return super.getRepeatedField(field, index);
        }

        private void verifyContainingType(Descriptors.FieldDescriptor field) {
            if (field.getContainingType() != this.getDescriptorForType()) {
                throw new IllegalArgumentException("FieldDescriptor does not match message type.");
            }
        }

        protected class ExtensionWriter {
            private final Iterator<Map.Entry<Descriptors.FieldDescriptor, Object>> iter;
            private Map.Entry<Descriptors.FieldDescriptor, Object> next;
            private final boolean messageSetWireFormat;

            private ExtensionWriter(boolean messageSetWireFormat) {
                this.iter = ExtendableMessage.this.extensions.iterator();
                if (this.iter.hasNext()) {
                    this.next = this.iter.next();
                }
                this.messageSetWireFormat = messageSetWireFormat;
            }

            public void writeUntil(int end, CodedOutputStream output) throws IOException {
                while (this.next != null && this.next.getKey().getNumber() < end) {
                    Descriptors.FieldDescriptor descriptor = this.next.getKey();
                    if (this.messageSetWireFormat && descriptor.getLiteJavaType() == WireFormat.JavaType.MESSAGE && !descriptor.isRepeated()) {
                        if (this.next instanceof LazyField.LazyEntry) {
                            output.writeRawMessageSetExtension(descriptor.getNumber(), ((LazyField.LazyEntry)this.next).getField().toByteString());
                        } else {
                            output.writeMessageSetExtension(descriptor.getNumber(), (Message)this.next.getValue());
                        }
                    } else {
                        FieldSet.writeField(descriptor, this.next.getValue(), output);
                    }
                    if (this.iter.hasNext()) {
                        this.next = this.iter.next();
                        continue;
                    }
                    this.next = null;
                }
            }
        }
    }

    public static interface ExtendableMessageOrBuilder<MessageType extends ExtendableMessage>
    extends MessageOrBuilder {
        @Override
        public Message getDefaultInstanceForType();

        public <Type> boolean hasExtension(ExtensionLite<MessageType, Type> var1);

        public <Type> int getExtensionCount(ExtensionLite<MessageType, List<Type>> var1);

        public <Type> Type getExtension(ExtensionLite<MessageType, Type> var1);

        public <Type> Type getExtension(ExtensionLite<MessageType, List<Type>> var1, int var2);

        public <Type> boolean hasExtension(Extension<MessageType, Type> var1);

        public <Type> boolean hasExtension(GeneratedMessage.GeneratedExtension<MessageType, Type> var1);

        public <Type> int getExtensionCount(Extension<MessageType, List<Type>> var1);

        public <Type> int getExtensionCount(GeneratedMessage.GeneratedExtension<MessageType, List<Type>> var1);

        public <Type> Type getExtension(Extension<MessageType, Type> var1);

        public <Type> Type getExtension(GeneratedMessage.GeneratedExtension<MessageType, Type> var1);

        public <Type> Type getExtension(Extension<MessageType, List<Type>> var1, int var2);

        public <Type> Type getExtension(GeneratedMessage.GeneratedExtension<MessageType, List<Type>> var1, int var2);
    }

    public static abstract class Builder<BuilderType extends Builder<BuilderType>>
    extends AbstractMessage.Builder<BuilderType> {
        private BuilderParent builderParent;
        private BuilderParentImpl meAsParent;
        private boolean isClean;
        private UnknownFieldSet unknownFields = UnknownFieldSet.getDefaultInstance();

        protected Builder() {
            this(null);
        }

        protected Builder(BuilderParent builderParent) {
            this.builderParent = builderParent;
        }

        @Override
        void dispose() {
            this.builderParent = null;
        }

        protected void onBuilt() {
            if (this.builderParent != null) {
                this.markClean();
            }
        }

        @Override
        protected void markClean() {
            this.isClean = true;
        }

        protected boolean isClean() {
            return this.isClean;
        }

        @Override
        public BuilderType clone() {
            Builder builder = (Builder)this.getDefaultInstanceForType().newBuilderForType();
            builder.mergeFrom((Message)this.buildPartial());
            return (BuilderType)builder;
        }

        @Override
        public BuilderType clear() {
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
            this.onChanged();
            return (BuilderType)this;
        }

        protected abstract FieldAccessorTable internalGetFieldAccessorTable();

        @Override
        public Descriptors.Descriptor getDescriptorForType() {
            return this.internalGetFieldAccessorTable().descriptor;
        }

        @Override
        public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
            return Collections.unmodifiableMap(this.getAllFieldsMutable());
        }

        private Map<Descriptors.FieldDescriptor, Object> getAllFieldsMutable() {
            TreeMap<Descriptors.FieldDescriptor, Object> result = new TreeMap<Descriptors.FieldDescriptor, Object>();
            Descriptors.Descriptor descriptor = this.internalGetFieldAccessorTable().descriptor;
            List<Descriptors.FieldDescriptor> fields = descriptor.getFields();
            for (int i = 0; i < fields.size(); ++i) {
                Descriptors.FieldDescriptor field = fields.get(i);
                Descriptors.OneofDescriptor oneofDescriptor = field.getContainingOneof();
                if (oneofDescriptor != null) {
                    i += oneofDescriptor.getFieldCount() - 1;
                    if (!this.hasOneof(oneofDescriptor)) continue;
                    field = this.getOneofFieldDescriptor(oneofDescriptor);
                } else {
                    if (field.isRepeated()) {
                        List value = (List)this.getField(field);
                        if (value.isEmpty()) continue;
                        result.put(field, value);
                        continue;
                    }
                    if (!this.hasField(field)) continue;
                }
                result.put(field, this.getField(field));
            }
            return result;
        }

        @Override
        public Message.Builder newBuilderForField(Descriptors.FieldDescriptor field) {
            return this.internalGetFieldAccessorTable().getField(field).newBuilder();
        }

        @Override
        public Message.Builder getFieldBuilder(Descriptors.FieldDescriptor field) {
            return this.internalGetFieldAccessorTable().getField(field).getBuilder(this);
        }

        @Override
        public Message.Builder getRepeatedFieldBuilder(Descriptors.FieldDescriptor field, int index) {
            return this.internalGetFieldAccessorTable().getField(field).getRepeatedBuilder(this, index);
        }

        @Override
        public boolean hasOneof(Descriptors.OneofDescriptor oneof) {
            return this.internalGetFieldAccessorTable().getOneof(oneof).has(this);
        }

        @Override
        public Descriptors.FieldDescriptor getOneofFieldDescriptor(Descriptors.OneofDescriptor oneof) {
            return this.internalGetFieldAccessorTable().getOneof(oneof).get(this);
        }

        @Override
        public boolean hasField(Descriptors.FieldDescriptor field) {
            return this.internalGetFieldAccessorTable().getField(field).has(this);
        }

        @Override
        public Object getField(Descriptors.FieldDescriptor field) {
            Object object = this.internalGetFieldAccessorTable().getField(field).get(this);
            if (field.isRepeated()) {
                return Collections.unmodifiableList((List)object);
            }
            return object;
        }

        public BuilderType setField(Descriptors.FieldDescriptor field, Object value) {
            this.internalGetFieldAccessorTable().getField(field).set(this, value);
            return (BuilderType)this;
        }

        public BuilderType clearField(Descriptors.FieldDescriptor field) {
            this.internalGetFieldAccessorTable().getField(field).clear(this);
            return (BuilderType)this;
        }

        @Override
        public BuilderType clearOneof(Descriptors.OneofDescriptor oneof) {
            this.internalGetFieldAccessorTable().getOneof(oneof).clear(this);
            return (BuilderType)this;
        }

        @Override
        public int getRepeatedFieldCount(Descriptors.FieldDescriptor field) {
            return this.internalGetFieldAccessorTable().getField(field).getRepeatedCount(this);
        }

        @Override
        public Object getRepeatedField(Descriptors.FieldDescriptor field, int index) {
            return this.internalGetFieldAccessorTable().getField(field).getRepeated(this, index);
        }

        public BuilderType setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            this.internalGetFieldAccessorTable().getField(field).setRepeated(this, index, value);
            return (BuilderType)this;
        }

        public BuilderType addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            this.internalGetFieldAccessorTable().getField(field).addRepeated(this, value);
            return (BuilderType)this;
        }

        private BuilderType setUnknownFieldsInternal(UnknownFieldSet unknownFields) {
            this.unknownFields = unknownFields;
            this.onChanged();
            return (BuilderType)this;
        }

        public BuilderType setUnknownFields(UnknownFieldSet unknownFields) {
            return this.setUnknownFieldsInternal(unknownFields);
        }

        protected BuilderType setUnknownFieldsProto3(UnknownFieldSet unknownFields) {
            return this.setUnknownFieldsInternal(unknownFields);
        }

        @Override
        public BuilderType mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (BuilderType)this.setUnknownFields(UnknownFieldSet.newBuilder(this.unknownFields).mergeFrom(unknownFields).build());
        }

        @Override
        public boolean isInitialized() {
            for (Descriptors.FieldDescriptor field : this.getDescriptorForType().getFields()) {
                if (field.isRequired() && !this.hasField(field)) {
                    return false;
                }
                if (field.getJavaType() != Descriptors.FieldDescriptor.JavaType.MESSAGE) continue;
                if (field.isRepeated()) {
                    List messageList = (List)this.getField(field);
                    for (Message element : messageList) {
                        if (element.isInitialized()) continue;
                        return false;
                    }
                    continue;
                }
                if (!this.hasField(field) || ((Message)this.getField(field)).isInitialized()) continue;
                return false;
            }
            return true;
        }

        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        protected BuilderParent getParentForChildren() {
            if (this.meAsParent == null) {
                this.meAsParent = new BuilderParentImpl();
            }
            return this.meAsParent;
        }

        protected final void onChanged() {
            if (this.isClean && this.builderParent != null) {
                this.builderParent.markDirty();
                this.isClean = false;
            }
        }

        protected MapField internalGetMapField(int fieldNumber) {
            throw new RuntimeException("No map fields found in " + this.getClass().getName());
        }

        protected MapField internalGetMutableMapField(int fieldNumber) {
            throw new RuntimeException("No map fields found in " + this.getClass().getName());
        }

        private class BuilderParentImpl
        implements BuilderParent {
            private BuilderParentImpl() {
            }

            @Override
            public void markDirty() {
                Builder.this.onChanged();
            }
        }
    }

    protected static interface BuilderParent
    extends AbstractMessage.BuilderParent {
    }

    protected static final class UnusedPrivateParameter {
        static final UnusedPrivateParameter INSTANCE = new UnusedPrivateParameter();

        private UnusedPrivateParameter() {
        }
    }
}

