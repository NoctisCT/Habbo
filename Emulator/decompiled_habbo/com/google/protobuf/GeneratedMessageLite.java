/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.AbstractParser;
import com.google.protobuf.ArrayDecoders;
import com.google.protobuf.BooleanArrayList;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedInputStreamReader;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.CodedOutputStreamWriter;
import com.google.protobuf.DoubleArrayList;
import com.google.protobuf.ExtensionLite;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.FieldSet;
import com.google.protobuf.FloatArrayList;
import com.google.protobuf.IntArrayList;
import com.google.protobuf.Internal;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.LongArrayList;
import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLiteOrBuilder;
import com.google.protobuf.MessageLiteToString;
import com.google.protobuf.Parser;
import com.google.protobuf.Protobuf;
import com.google.protobuf.ProtobufArrayList;
import com.google.protobuf.RawMessageInfo;
import com.google.protobuf.Schema;
import com.google.protobuf.UnknownFieldSetLite;
import com.google.protobuf.UnsafeUtil;
import com.google.protobuf.WireFormat;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class GeneratedMessageLite<MessageType extends GeneratedMessageLite<MessageType, BuilderType>, BuilderType extends Builder<MessageType, BuilderType>>
extends AbstractMessageLite<MessageType, BuilderType> {
    protected UnknownFieldSetLite unknownFields = UnknownFieldSetLite.getDefaultInstance();
    protected int memoizedSerializedSize = -1;
    private static Map<Object, GeneratedMessageLite<?, ?>> defaultInstanceMap = new ConcurrentHashMap();

    public final Parser<MessageType> getParserForType() {
        return (Parser)this.dynamicMethod(MethodToInvoke.GET_PARSER);
    }

    public final MessageType getDefaultInstanceForType() {
        return (MessageType)((GeneratedMessageLite)this.dynamicMethod(MethodToInvoke.GET_DEFAULT_INSTANCE));
    }

    public final BuilderType newBuilderForType() {
        return (BuilderType)((Builder)this.dynamicMethod(MethodToInvoke.NEW_BUILDER));
    }

    public String toString() {
        return MessageLiteToString.toString(this, super.toString());
    }

    public int hashCode() {
        if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
        }
        this.memoizedHashCode = Protobuf.getInstance().schemaFor(this).hashCode(this);
        return this.memoizedHashCode;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!this.getDefaultInstanceForType().getClass().isInstance(other)) {
            return false;
        }
        return Protobuf.getInstance().schemaFor(this).equals(this, (GeneratedMessageLite)other);
    }

    private final void ensureUnknownFieldsInitialized() {
        if (this.unknownFields == UnknownFieldSetLite.getDefaultInstance()) {
            this.unknownFields = UnknownFieldSetLite.newInstance();
        }
    }

    protected boolean parseUnknownField(int tag, CodedInputStream input) throws IOException {
        if (WireFormat.getTagWireType(tag) == 4) {
            return false;
        }
        this.ensureUnknownFieldsInitialized();
        return this.unknownFields.mergeFieldFrom(tag, input);
    }

    protected void mergeVarintField(int tag, int value) {
        this.ensureUnknownFieldsInitialized();
        this.unknownFields.mergeVarintField(tag, value);
    }

    protected void mergeLengthDelimitedField(int fieldNumber, ByteString value) {
        this.ensureUnknownFieldsInitialized();
        this.unknownFields.mergeLengthDelimitedField(fieldNumber, value);
    }

    protected void makeImmutable() {
        Protobuf.getInstance().schemaFor(this).makeImmutable(this);
    }

    protected final <MessageType extends GeneratedMessageLite<MessageType, BuilderType>, BuilderType extends Builder<MessageType, BuilderType>> BuilderType createBuilder() {
        return (BuilderType)((Builder)this.dynamicMethod(MethodToInvoke.NEW_BUILDER));
    }

    protected final <MessageType extends GeneratedMessageLite<MessageType, BuilderType>, BuilderType extends Builder<MessageType, BuilderType>> BuilderType createBuilder(MessageType prototype) {
        return ((Builder)this.createBuilder()).mergeFrom(prototype);
    }

    @Override
    public final boolean isInitialized() {
        return GeneratedMessageLite.isInitialized(this, Boolean.TRUE);
    }

    public final BuilderType toBuilder() {
        Builder builder = (Builder)this.dynamicMethod(MethodToInvoke.NEW_BUILDER);
        builder.mergeFrom(this);
        return (BuilderType)builder;
    }

    protected abstract Object dynamicMethod(MethodToInvoke var1, Object var2, Object var3);

    protected Object dynamicMethod(MethodToInvoke method, Object arg0) {
        return this.dynamicMethod(method, arg0, null);
    }

    protected Object dynamicMethod(MethodToInvoke method) {
        return this.dynamicMethod(method, null, null);
    }

    @Override
    int getMemoizedSerializedSize() {
        return this.memoizedSerializedSize;
    }

    @Override
    void setMemoizedSerializedSize(int size) {
        this.memoizedSerializedSize = size;
    }

    @Override
    public void writeTo(CodedOutputStream output) throws IOException {
        Protobuf.getInstance().schemaFor(this).writeTo(this, CodedOutputStreamWriter.forCodedOutput(output));
    }

    @Override
    public int getSerializedSize() {
        if (this.memoizedSerializedSize == -1) {
            this.memoizedSerializedSize = Protobuf.getInstance().schemaFor(this).getSerializedSize(this);
        }
        return this.memoizedSerializedSize;
    }

    Object buildMessageInfo() throws Exception {
        return this.dynamicMethod(MethodToInvoke.BUILD_MESSAGE_INFO);
    }

    static <T extends GeneratedMessageLite<?, ?>> T getDefaultInstance(Class<T> clazz) {
        MessageLite result = defaultInstanceMap.get(clazz);
        if (result == null) {
            try {
                Class.forName(clazz.getName(), true, clazz.getClassLoader());
            }
            catch (ClassNotFoundException e) {
                throw new IllegalStateException("Class initialization cannot fail.", e);
            }
            result = defaultInstanceMap.get(clazz);
        }
        if (result == null) {
            result = ((GeneratedMessageLite)UnsafeUtil.allocateInstance(clazz)).getDefaultInstanceForType();
            if (result == null) {
                throw new IllegalStateException();
            }
            defaultInstanceMap.put(clazz, (GeneratedMessageLite<?, ?>)result);
        }
        return (T)result;
    }

    protected static <T extends GeneratedMessageLite<?, ?>> void registerDefaultInstance(Class<T> clazz, T defaultInstance) {
        defaultInstanceMap.put(clazz, defaultInstance);
    }

    protected static Object newMessageInfo(MessageLite defaultInstance, String info, Object[] objects) {
        return new RawMessageInfo(defaultInstance, info, objects);
    }

    protected final void mergeUnknownFields(UnknownFieldSetLite unknownFields) {
        this.unknownFields = UnknownFieldSetLite.mutableCopyOf(this.unknownFields, unknownFields);
    }

    public static <ContainingType extends MessageLite, Type> GeneratedExtension<ContainingType, Type> newSingularGeneratedExtension(ContainingType containingTypeDefaultInstance, Type defaultValue, MessageLite messageDefaultInstance, Internal.EnumLiteMap<?> enumTypeMap, int number, WireFormat.FieldType type, Class singularType) {
        return new GeneratedExtension<ContainingType, Type>(containingTypeDefaultInstance, defaultValue, messageDefaultInstance, new ExtensionDescriptor(enumTypeMap, number, type, false, false), singularType);
    }

    public static <ContainingType extends MessageLite, Type> GeneratedExtension<ContainingType, Type> newRepeatedGeneratedExtension(ContainingType containingTypeDefaultInstance, MessageLite messageDefaultInstance, Internal.EnumLiteMap<?> enumTypeMap, int number, WireFormat.FieldType type, boolean isPacked, Class singularType) {
        List emptyList = Collections.emptyList();
        return new GeneratedExtension(containingTypeDefaultInstance, emptyList, messageDefaultInstance, new ExtensionDescriptor(enumTypeMap, number, type, true, isPacked), singularType);
    }

    static Method getMethodOrDie(Class clazz, String name, Class ... params) {
        try {
            return clazz.getMethod(name, params);
        }
        catch (NoSuchMethodException e) {
            throw new RuntimeException("Generated message class \"" + clazz.getName() + "\" missing method \"" + name + "\".", e);
        }
    }

    static Object invokeOrDie(Method method, Object object, Object ... params) {
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

    private static <MessageType extends ExtendableMessage<MessageType, BuilderType>, BuilderType extends ExtendableBuilder<MessageType, BuilderType>, T> GeneratedExtension<MessageType, T> checkIsLite(ExtensionLite<MessageType, T> extension) {
        if (!extension.isLite()) {
            throw new IllegalArgumentException("Expected a lite extension.");
        }
        return (GeneratedExtension)extension;
    }

    protected static final <T extends GeneratedMessageLite<T, ?>> boolean isInitialized(T message, boolean shouldMemoize) {
        byte memoizedIsInitialized = (Byte)message.dynamicMethod(MethodToInvoke.GET_MEMOIZED_IS_INITIALIZED);
        if (memoizedIsInitialized == 1) {
            return true;
        }
        if (memoizedIsInitialized == 0) {
            return false;
        }
        boolean isInitialized = Protobuf.getInstance().schemaFor(message).isInitialized(message);
        if (shouldMemoize) {
            message.dynamicMethod(MethodToInvoke.SET_MEMOIZED_IS_INITIALIZED, isInitialized ? message : null);
        }
        return isInitialized;
    }

    protected static Internal.IntList emptyIntList() {
        return IntArrayList.emptyList();
    }

    protected static Internal.IntList mutableCopy(Internal.IntList list) {
        int size = list.size();
        return list.mutableCopyWithCapacity(size == 0 ? 10 : size * 2);
    }

    protected static Internal.LongList emptyLongList() {
        return LongArrayList.emptyList();
    }

    protected static Internal.LongList mutableCopy(Internal.LongList list) {
        int size = list.size();
        return list.mutableCopyWithCapacity(size == 0 ? 10 : size * 2);
    }

    protected static Internal.FloatList emptyFloatList() {
        return FloatArrayList.emptyList();
    }

    protected static Internal.FloatList mutableCopy(Internal.FloatList list) {
        int size = list.size();
        return list.mutableCopyWithCapacity(size == 0 ? 10 : size * 2);
    }

    protected static Internal.DoubleList emptyDoubleList() {
        return DoubleArrayList.emptyList();
    }

    protected static Internal.DoubleList mutableCopy(Internal.DoubleList list) {
        int size = list.size();
        return list.mutableCopyWithCapacity(size == 0 ? 10 : size * 2);
    }

    protected static Internal.BooleanList emptyBooleanList() {
        return BooleanArrayList.emptyList();
    }

    protected static Internal.BooleanList mutableCopy(Internal.BooleanList list) {
        int size = list.size();
        return list.mutableCopyWithCapacity(size == 0 ? 10 : size * 2);
    }

    protected static <E> Internal.ProtobufList<E> emptyProtobufList() {
        return ProtobufArrayList.emptyList();
    }

    protected static <E> Internal.ProtobufList<E> mutableCopy(Internal.ProtobufList<E> list) {
        int size = list.size();
        return list.mutableCopyWithCapacity(size == 0 ? 10 : size * 2);
    }

    static <T extends GeneratedMessageLite<T, ?>> T parsePartialFrom(T instance, CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        GeneratedMessageLite result = (GeneratedMessageLite)instance.dynamicMethod(MethodToInvoke.NEW_MUTABLE_INSTANCE);
        try {
            Schema<GeneratedMessageLite> schema = Protobuf.getInstance().schemaFor(result);
            schema.mergeFrom(result, CodedInputStreamReader.forCodedInput(input), extensionRegistry);
            schema.makeImmutable(result);
        }
        catch (IOException e) {
            if (e.getCause() instanceof InvalidProtocolBufferException) {
                throw (InvalidProtocolBufferException)e.getCause();
            }
            throw new InvalidProtocolBufferException(e.getMessage()).setUnfinishedMessage(result);
        }
        catch (RuntimeException e) {
            if (e.getCause() instanceof InvalidProtocolBufferException) {
                throw (InvalidProtocolBufferException)e.getCause();
            }
            throw e;
        }
        return (T)result;
    }

    static <T extends GeneratedMessageLite<T, ?>> T parsePartialFrom(T instance, byte[] input, int offset, int length, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        GeneratedMessageLite result = (GeneratedMessageLite)instance.dynamicMethod(MethodToInvoke.NEW_MUTABLE_INSTANCE);
        try {
            Schema<GeneratedMessageLite> schema = Protobuf.getInstance().schemaFor(result);
            schema.mergeFrom(result, input, offset, offset + length, new ArrayDecoders.Registers(extensionRegistry));
            schema.makeImmutable(result);
            if (result.memoizedHashCode != 0) {
                throw new RuntimeException();
            }
        }
        catch (IOException e) {
            if (e.getCause() instanceof InvalidProtocolBufferException) {
                throw (InvalidProtocolBufferException)e.getCause();
            }
            throw new InvalidProtocolBufferException(e.getMessage()).setUnfinishedMessage(result);
        }
        catch (IndexOutOfBoundsException e) {
            throw InvalidProtocolBufferException.truncatedMessage().setUnfinishedMessage(result);
        }
        return (T)result;
    }

    protected static <T extends GeneratedMessageLite<T, ?>> T parsePartialFrom(T defaultInstance, CodedInputStream input) throws InvalidProtocolBufferException {
        return GeneratedMessageLite.parsePartialFrom(defaultInstance, input, ExtensionRegistryLite.getEmptyRegistry());
    }

    private static <T extends GeneratedMessageLite<T, ?>> T checkMessageInitialized(T message) throws InvalidProtocolBufferException {
        if (message != null && !message.isInitialized()) {
            throw message.newUninitializedMessageException().asInvalidProtocolBufferException().setUnfinishedMessage(message);
        }
        return message;
    }

    protected static <T extends GeneratedMessageLite<T, ?>> T parseFrom(T defaultInstance, ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return GeneratedMessageLite.checkMessageInitialized(GeneratedMessageLite.parseFrom(defaultInstance, CodedInputStream.newInstance(data), extensionRegistry));
    }

    protected static <T extends GeneratedMessageLite<T, ?>> T parseFrom(T defaultInstance, ByteBuffer data) throws InvalidProtocolBufferException {
        return GeneratedMessageLite.parseFrom(defaultInstance, data, ExtensionRegistryLite.getEmptyRegistry());
    }

    protected static <T extends GeneratedMessageLite<T, ?>> T parseFrom(T defaultInstance, ByteString data) throws InvalidProtocolBufferException {
        return GeneratedMessageLite.checkMessageInitialized(GeneratedMessageLite.parseFrom(defaultInstance, data, ExtensionRegistryLite.getEmptyRegistry()));
    }

    protected static <T extends GeneratedMessageLite<T, ?>> T parseFrom(T defaultInstance, ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return GeneratedMessageLite.checkMessageInitialized(GeneratedMessageLite.parsePartialFrom(defaultInstance, data, extensionRegistry));
    }

    private static <T extends GeneratedMessageLite<T, ?>> T parsePartialFrom(T defaultInstance, ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        CodedInputStream input = data.newCodedInput();
        T message = GeneratedMessageLite.parsePartialFrom(defaultInstance, input, extensionRegistry);
        try {
            input.checkLastTagWas(0);
        }
        catch (InvalidProtocolBufferException e) {
            throw e.setUnfinishedMessage(message);
        }
        return message;
    }

    private static <T extends GeneratedMessageLite<T, ?>> T parsePartialFrom(T defaultInstance, byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return GeneratedMessageLite.checkMessageInitialized(GeneratedMessageLite.parsePartialFrom(defaultInstance, data, 0, data.length, extensionRegistry));
    }

    protected static <T extends GeneratedMessageLite<T, ?>> T parseFrom(T defaultInstance, byte[] data) throws InvalidProtocolBufferException {
        return GeneratedMessageLite.checkMessageInitialized(GeneratedMessageLite.parsePartialFrom(defaultInstance, data, 0, data.length, ExtensionRegistryLite.getEmptyRegistry()));
    }

    protected static <T extends GeneratedMessageLite<T, ?>> T parseFrom(T defaultInstance, byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return GeneratedMessageLite.checkMessageInitialized(GeneratedMessageLite.parsePartialFrom(defaultInstance, data, 0, data.length, extensionRegistry));
    }

    protected static <T extends GeneratedMessageLite<T, ?>> T parseFrom(T defaultInstance, InputStream input) throws InvalidProtocolBufferException {
        return GeneratedMessageLite.checkMessageInitialized(GeneratedMessageLite.parsePartialFrom(defaultInstance, CodedInputStream.newInstance(input), ExtensionRegistryLite.getEmptyRegistry()));
    }

    protected static <T extends GeneratedMessageLite<T, ?>> T parseFrom(T defaultInstance, InputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return GeneratedMessageLite.checkMessageInitialized(GeneratedMessageLite.parsePartialFrom(defaultInstance, CodedInputStream.newInstance(input), extensionRegistry));
    }

    protected static <T extends GeneratedMessageLite<T, ?>> T parseFrom(T defaultInstance, CodedInputStream input) throws InvalidProtocolBufferException {
        return GeneratedMessageLite.parseFrom(defaultInstance, input, ExtensionRegistryLite.getEmptyRegistry());
    }

    protected static <T extends GeneratedMessageLite<T, ?>> T parseFrom(T defaultInstance, CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return GeneratedMessageLite.checkMessageInitialized(GeneratedMessageLite.parsePartialFrom(defaultInstance, input, extensionRegistry));
    }

    protected static <T extends GeneratedMessageLite<T, ?>> T parseDelimitedFrom(T defaultInstance, InputStream input) throws InvalidProtocolBufferException {
        return GeneratedMessageLite.checkMessageInitialized(GeneratedMessageLite.parsePartialDelimitedFrom(defaultInstance, input, ExtensionRegistryLite.getEmptyRegistry()));
    }

    protected static <T extends GeneratedMessageLite<T, ?>> T parseDelimitedFrom(T defaultInstance, InputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return GeneratedMessageLite.checkMessageInitialized(GeneratedMessageLite.parsePartialDelimitedFrom(defaultInstance, input, extensionRegistry));
    }

    private static <T extends GeneratedMessageLite<T, ?>> T parsePartialDelimitedFrom(T defaultInstance, InputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        int size;
        try {
            int firstByte = input.read();
            if (firstByte == -1) {
                return null;
            }
            size = CodedInputStream.readRawVarint32(firstByte, input);
        }
        catch (IOException e) {
            throw new InvalidProtocolBufferException(e.getMessage());
        }
        AbstractMessageLite.Builder.LimitedInputStream limitedInput = new AbstractMessageLite.Builder.LimitedInputStream(input, size);
        CodedInputStream codedInput = CodedInputStream.newInstance(limitedInput);
        T message = GeneratedMessageLite.parsePartialFrom(defaultInstance, codedInput, extensionRegistry);
        try {
            codedInput.checkLastTagWas(0);
        }
        catch (InvalidProtocolBufferException e) {
            throw e.setUnfinishedMessage(message);
        }
        return message;
    }

    protected static class DefaultInstanceBasedParser<T extends GeneratedMessageLite<T, ?>>
    extends AbstractParser<T> {
        private final T defaultInstance;

        public DefaultInstanceBasedParser(T defaultInstance) {
            this.defaultInstance = defaultInstance;
        }

        @Override
        public T parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parsePartialFrom(this.defaultInstance, input, extensionRegistry);
        }

        @Override
        public T parsePartialFrom(byte[] input, int offset, int length, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GeneratedMessageLite.parsePartialFrom(this.defaultInstance, input, offset, length, extensionRegistry);
        }
    }

    protected static final class SerializedForm
    implements Serializable {
        private static final long serialVersionUID = 0L;
        private final Class<?> messageClass;
        private final String messageClassName;
        private final byte[] asBytes;

        public static SerializedForm of(MessageLite message) {
            return new SerializedForm(message);
        }

        SerializedForm(MessageLite regularForm) {
            this.messageClass = regularForm.getClass();
            this.messageClassName = this.messageClass.getName();
            this.asBytes = regularForm.toByteArray();
        }

        protected Object readResolve() throws ObjectStreamException {
            try {
                Class<?> messageClass = this.resolveMessageClass();
                Field defaultInstanceField = messageClass.getDeclaredField("DEFAULT_INSTANCE");
                defaultInstanceField.setAccessible(true);
                MessageLite defaultInstance = (MessageLite)defaultInstanceField.get(null);
                return defaultInstance.newBuilderForType().mergeFrom(this.asBytes).buildPartial();
            }
            catch (ClassNotFoundException e) {
                throw new RuntimeException("Unable to find proto buffer class: " + this.messageClassName, e);
            }
            catch (NoSuchFieldException e) {
                return this.readResolveFallback();
            }
            catch (SecurityException e) {
                throw new RuntimeException("Unable to call DEFAULT_INSTANCE in " + this.messageClassName, e);
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException("Unable to call parsePartialFrom", e);
            }
            catch (InvalidProtocolBufferException e) {
                throw new RuntimeException("Unable to understand proto buffer", e);
            }
        }

        @Deprecated
        private Object readResolveFallback() throws ObjectStreamException {
            try {
                Class<?> messageClass = this.resolveMessageClass();
                Field defaultInstanceField = messageClass.getDeclaredField("defaultInstance");
                defaultInstanceField.setAccessible(true);
                MessageLite defaultInstance = (MessageLite)defaultInstanceField.get(null);
                return defaultInstance.newBuilderForType().mergeFrom(this.asBytes).buildPartial();
            }
            catch (ClassNotFoundException e) {
                throw new RuntimeException("Unable to find proto buffer class: " + this.messageClassName, e);
            }
            catch (NoSuchFieldException e) {
                throw new RuntimeException("Unable to find defaultInstance in " + this.messageClassName, e);
            }
            catch (SecurityException e) {
                throw new RuntimeException("Unable to call defaultInstance in " + this.messageClassName, e);
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException("Unable to call parsePartialFrom", e);
            }
            catch (InvalidProtocolBufferException e) {
                throw new RuntimeException("Unable to understand proto buffer", e);
            }
        }

        private Class<?> resolveMessageClass() throws ClassNotFoundException {
            return this.messageClass != null ? this.messageClass : Class.forName(this.messageClassName);
        }
    }

    public static class GeneratedExtension<ContainingType extends MessageLite, Type>
    extends ExtensionLite<ContainingType, Type> {
        final ContainingType containingTypeDefaultInstance;
        final Type defaultValue;
        final MessageLite messageDefaultInstance;
        final ExtensionDescriptor descriptor;

        GeneratedExtension(ContainingType containingTypeDefaultInstance, Type defaultValue, MessageLite messageDefaultInstance, ExtensionDescriptor descriptor, Class singularType) {
            if (containingTypeDefaultInstance == null) {
                throw new IllegalArgumentException("Null containingTypeDefaultInstance");
            }
            if (descriptor.getLiteType() == WireFormat.FieldType.MESSAGE && messageDefaultInstance == null) {
                throw new IllegalArgumentException("Null messageDefaultInstance");
            }
            this.containingTypeDefaultInstance = containingTypeDefaultInstance;
            this.defaultValue = defaultValue;
            this.messageDefaultInstance = messageDefaultInstance;
            this.descriptor = descriptor;
        }

        public ContainingType getContainingTypeDefaultInstance() {
            return this.containingTypeDefaultInstance;
        }

        @Override
        public int getNumber() {
            return this.descriptor.getNumber();
        }

        @Override
        public MessageLite getMessageDefaultInstance() {
            return this.messageDefaultInstance;
        }

        Object fromFieldSetType(Object value) {
            if (this.descriptor.isRepeated()) {
                if (this.descriptor.getLiteJavaType() == WireFormat.JavaType.ENUM) {
                    ArrayList<Object> result = new ArrayList<Object>();
                    for (Object element : (List)value) {
                        result.add(this.singularFromFieldSetType(element));
                    }
                    return result;
                }
                return value;
            }
            return this.singularFromFieldSetType(value);
        }

        Object singularFromFieldSetType(Object value) {
            if (this.descriptor.getLiteJavaType() == WireFormat.JavaType.ENUM) {
                return this.descriptor.enumTypeMap.findValueByNumber((Integer)value);
            }
            return value;
        }

        Object toFieldSetType(Object value) {
            if (this.descriptor.isRepeated()) {
                if (this.descriptor.getLiteJavaType() == WireFormat.JavaType.ENUM) {
                    ArrayList<Object> result = new ArrayList<Object>();
                    for (Object element : (List)value) {
                        result.add(this.singularToFieldSetType(element));
                    }
                    return result;
                }
                return value;
            }
            return this.singularToFieldSetType(value);
        }

        Object singularToFieldSetType(Object value) {
            if (this.descriptor.getLiteJavaType() == WireFormat.JavaType.ENUM) {
                return ((Internal.EnumLite)value).getNumber();
            }
            return value;
        }

        @Override
        public WireFormat.FieldType getLiteType() {
            return this.descriptor.getLiteType();
        }

        @Override
        public boolean isRepeated() {
            return this.descriptor.isRepeated;
        }

        @Override
        public Type getDefaultValue() {
            return this.defaultValue;
        }
    }

    static final class ExtensionDescriptor
    implements FieldSet.FieldDescriptorLite<ExtensionDescriptor> {
        final Internal.EnumLiteMap<?> enumTypeMap;
        final int number;
        final WireFormat.FieldType type;
        final boolean isRepeated;
        final boolean isPacked;

        ExtensionDescriptor(Internal.EnumLiteMap<?> enumTypeMap, int number, WireFormat.FieldType type, boolean isRepeated, boolean isPacked) {
            this.enumTypeMap = enumTypeMap;
            this.number = number;
            this.type = type;
            this.isRepeated = isRepeated;
            this.isPacked = isPacked;
        }

        @Override
        public int getNumber() {
            return this.number;
        }

        @Override
        public WireFormat.FieldType getLiteType() {
            return this.type;
        }

        @Override
        public WireFormat.JavaType getLiteJavaType() {
            return this.type.getJavaType();
        }

        @Override
        public boolean isRepeated() {
            return this.isRepeated;
        }

        @Override
        public boolean isPacked() {
            return this.isPacked;
        }

        @Override
        public Internal.EnumLiteMap<?> getEnumType() {
            return this.enumTypeMap;
        }

        @Override
        public MessageLite.Builder internalMergeFrom(MessageLite.Builder to, MessageLite from) {
            return ((Builder)to).mergeFrom((GeneratedMessageLite)from);
        }

        @Override
        public int compareTo(ExtensionDescriptor other) {
            return this.number - other.number;
        }
    }

    public static abstract class ExtendableBuilder<MessageType extends ExtendableMessage<MessageType, BuilderType>, BuilderType extends ExtendableBuilder<MessageType, BuilderType>>
    extends Builder<MessageType, BuilderType>
    implements ExtendableMessageOrBuilder<MessageType, BuilderType> {
        protected ExtendableBuilder(MessageType defaultInstance) {
            super(defaultInstance);
        }

        void internalSetExtensionSet(FieldSet<ExtensionDescriptor> extensions) {
            this.copyOnWrite();
            ((ExtendableMessage)this.instance).extensions = extensions;
        }

        @Override
        protected void copyOnWriteInternal() {
            super.copyOnWriteInternal();
            ((ExtendableMessage)this.instance).extensions = ((ExtendableMessage)this.instance).extensions.clone();
        }

        private FieldSet<ExtensionDescriptor> ensureExtensionsAreMutable() {
            FieldSet<ExtensionDescriptor> extensions = ((ExtendableMessage)this.instance).extensions;
            if (extensions.isImmutable()) {
                ((ExtendableMessage)this.instance).extensions = extensions = extensions.clone();
            }
            return extensions;
        }

        @Override
        public final MessageType buildPartial() {
            if (this.isBuilt) {
                return (MessageType)((ExtendableMessage)this.instance);
            }
            ((ExtendableMessage)this.instance).extensions.makeImmutable();
            return (MessageType)((ExtendableMessage)super.buildPartial());
        }

        private void verifyExtensionContainingType(GeneratedExtension<MessageType, ?> extension) {
            if (extension.getContainingTypeDefaultInstance() != this.getDefaultInstanceForType()) {
                throw new IllegalArgumentException("This extension is for a different message type.  Please make sure that you are not suppressing any generics type warnings.");
            }
        }

        @Override
        public final <Type> boolean hasExtension(ExtensionLite<MessageType, Type> extension) {
            return ((ExtendableMessage)this.instance).hasExtension(extension);
        }

        @Override
        public final <Type> int getExtensionCount(ExtensionLite<MessageType, List<Type>> extension) {
            return ((ExtendableMessage)this.instance).getExtensionCount(extension);
        }

        @Override
        public final <Type> Type getExtension(ExtensionLite<MessageType, Type> extension) {
            return ((ExtendableMessage)this.instance).getExtension(extension);
        }

        @Override
        public final <Type> Type getExtension(ExtensionLite<MessageType, List<Type>> extension, int index) {
            return ((ExtendableMessage)this.instance).getExtension(extension, index);
        }

        public final <Type> BuilderType setExtension(ExtensionLite<MessageType, Type> extension, Type value) {
            GeneratedExtension extensionLite = GeneratedMessageLite.checkIsLite(extension);
            this.verifyExtensionContainingType(extensionLite);
            this.copyOnWrite();
            this.ensureExtensionsAreMutable().setField(extensionLite.descriptor, extensionLite.toFieldSetType(value));
            return (BuilderType)this;
        }

        public final <Type> BuilderType setExtension(ExtensionLite<MessageType, List<Type>> extension, int index, Type value) {
            GeneratedExtension extensionLite = GeneratedMessageLite.checkIsLite(extension);
            this.verifyExtensionContainingType(extensionLite);
            this.copyOnWrite();
            this.ensureExtensionsAreMutable().setRepeatedField(extensionLite.descriptor, index, extensionLite.singularToFieldSetType(value));
            return (BuilderType)this;
        }

        public final <Type> BuilderType addExtension(ExtensionLite<MessageType, List<Type>> extension, Type value) {
            GeneratedExtension extensionLite = GeneratedMessageLite.checkIsLite(extension);
            this.verifyExtensionContainingType(extensionLite);
            this.copyOnWrite();
            this.ensureExtensionsAreMutable().addRepeatedField(extensionLite.descriptor, extensionLite.singularToFieldSetType(value));
            return (BuilderType)this;
        }

        public final BuilderType clearExtension(ExtensionLite<MessageType, ?> extension) {
            GeneratedExtension extensionLite = GeneratedMessageLite.checkIsLite(extension);
            this.verifyExtensionContainingType(extensionLite);
            this.copyOnWrite();
            this.ensureExtensionsAreMutable().clearField(extensionLite.descriptor);
            return (BuilderType)this;
        }
    }

    public static abstract class ExtendableMessage<MessageType extends ExtendableMessage<MessageType, BuilderType>, BuilderType extends ExtendableBuilder<MessageType, BuilderType>>
    extends GeneratedMessageLite<MessageType, BuilderType>
    implements ExtendableMessageOrBuilder<MessageType, BuilderType> {
        protected FieldSet<ExtensionDescriptor> extensions = FieldSet.emptySet();

        protected final void mergeExtensionFields(MessageType other) {
            if (this.extensions.isImmutable()) {
                this.extensions = this.extensions.clone();
            }
            this.extensions.mergeFrom(((ExtendableMessage)other).extensions);
        }

        protected <MessageType extends MessageLite> boolean parseUnknownField(MessageType defaultInstance, CodedInputStream input, ExtensionRegistryLite extensionRegistry, int tag) throws IOException {
            int fieldNumber = WireFormat.getTagFieldNumber(tag);
            GeneratedExtension<MessageType, ?> extension = extensionRegistry.findLiteExtensionByNumber(defaultInstance, fieldNumber);
            return this.parseExtension(input, extensionRegistry, extension, tag, fieldNumber);
        }

        private boolean parseExtension(CodedInputStream input, ExtensionRegistryLite extensionRegistry, GeneratedExtension<?, ?> extension, int tag, int fieldNumber) throws IOException {
            int wireType = WireFormat.getTagWireType(tag);
            boolean unknown = false;
            boolean packed = false;
            if (extension == null) {
                unknown = true;
            } else if (wireType == FieldSet.getWireFormatForFieldType(extension.descriptor.getLiteType(), false)) {
                packed = false;
            } else if (extension.descriptor.isRepeated && extension.descriptor.type.isPackable() && wireType == FieldSet.getWireFormatForFieldType(extension.descriptor.getLiteType(), true)) {
                packed = true;
            } else {
                unknown = true;
            }
            if (unknown) {
                return this.parseUnknownField(tag, input);
            }
            this.ensureExtensionsAreMutable();
            if (packed) {
                int length = input.readRawVarint32();
                int limit = input.pushLimit(length);
                if (extension.descriptor.getLiteType() == WireFormat.FieldType.ENUM) {
                    while (input.getBytesUntilLimit() > 0) {
                        int rawValue = input.readEnum();
                        Object value = extension.descriptor.getEnumType().findValueByNumber(rawValue);
                        if (value == null) {
                            return true;
                        }
                        this.extensions.addRepeatedField(extension.descriptor, extension.singularToFieldSetType(value));
                    }
                } else {
                    while (input.getBytesUntilLimit() > 0) {
                        Object value = FieldSet.readPrimitiveField(input, extension.descriptor.getLiteType(), false);
                        this.extensions.addRepeatedField(extension.descriptor, value);
                    }
                }
                input.popLimit(limit);
            } else {
                Object value;
                switch (extension.descriptor.getLiteJavaType()) {
                    case MESSAGE: {
                        MessageLite existingValue;
                        MessageLite.Builder subBuilder = null;
                        if (!extension.descriptor.isRepeated() && (existingValue = (MessageLite)this.extensions.getField(extension.descriptor)) != null) {
                            subBuilder = existingValue.toBuilder();
                        }
                        if (subBuilder == null) {
                            subBuilder = extension.getMessageDefaultInstance().newBuilderForType();
                        }
                        if (extension.descriptor.getLiteType() == WireFormat.FieldType.GROUP) {
                            input.readGroup(extension.getNumber(), subBuilder, extensionRegistry);
                        } else {
                            input.readMessage(subBuilder, extensionRegistry);
                        }
                        value = subBuilder.build();
                        break;
                    }
                    case ENUM: {
                        int rawValue = input.readEnum();
                        value = extension.descriptor.getEnumType().findValueByNumber(rawValue);
                        if (value != null) break;
                        this.mergeVarintField(fieldNumber, rawValue);
                        return true;
                    }
                    default: {
                        value = FieldSet.readPrimitiveField(input, extension.descriptor.getLiteType(), false);
                    }
                }
                if (extension.descriptor.isRepeated()) {
                    this.extensions.addRepeatedField(extension.descriptor, extension.singularToFieldSetType(value));
                } else {
                    this.extensions.setField(extension.descriptor, extension.singularToFieldSetType(value));
                }
            }
            return true;
        }

        protected <MessageType extends MessageLite> boolean parseUnknownFieldAsMessageSet(MessageType defaultInstance, CodedInputStream input, ExtensionRegistryLite extensionRegistry, int tag) throws IOException {
            if (tag == WireFormat.MESSAGE_SET_ITEM_TAG) {
                this.mergeMessageSetExtensionFromCodedStream(defaultInstance, input, extensionRegistry);
                return true;
            }
            int wireType = WireFormat.getTagWireType(tag);
            if (wireType == 2) {
                return this.parseUnknownField(defaultInstance, input, extensionRegistry, tag);
            }
            return input.skipField(tag);
        }

        private <MessageType extends MessageLite> void mergeMessageSetExtensionFromCodedStream(MessageType defaultInstance, CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            int tag;
            int typeId = 0;
            ByteString rawBytes = null;
            GeneratedExtension<MessageType, ?> extension = null;
            while ((tag = input.readTag()) != 0) {
                if (tag == WireFormat.MESSAGE_SET_TYPE_ID_TAG) {
                    typeId = input.readUInt32();
                    if (typeId == 0) continue;
                    extension = extensionRegistry.findLiteExtensionByNumber(defaultInstance, typeId);
                    continue;
                }
                if (tag == WireFormat.MESSAGE_SET_MESSAGE_TAG) {
                    if (typeId != 0 && extension != null) {
                        this.eagerlyMergeMessageSetExtension(input, extension, extensionRegistry, typeId);
                        rawBytes = null;
                        continue;
                    }
                    rawBytes = input.readBytes();
                    continue;
                }
                if (input.skipField(tag)) continue;
                break;
            }
            input.checkLastTagWas(WireFormat.MESSAGE_SET_ITEM_END_TAG);
            if (rawBytes != null && typeId != 0) {
                if (extension != null) {
                    this.mergeMessageSetExtensionFromBytes(rawBytes, extensionRegistry, extension);
                } else if (rawBytes != null) {
                    this.mergeLengthDelimitedField(typeId, rawBytes);
                }
            }
        }

        private void eagerlyMergeMessageSetExtension(CodedInputStream input, GeneratedExtension<?, ?> extension, ExtensionRegistryLite extensionRegistry, int typeId) throws IOException {
            int fieldNumber = typeId;
            int tag = WireFormat.makeTag(typeId, 2);
            this.parseExtension(input, extensionRegistry, extension, tag, fieldNumber);
        }

        private void mergeMessageSetExtensionFromBytes(ByteString rawBytes, ExtensionRegistryLite extensionRegistry, GeneratedExtension<?, ?> extension) throws IOException {
            MessageLite.Builder subBuilder = null;
            MessageLite existingValue = (MessageLite)this.extensions.getField(extension.descriptor);
            if (existingValue != null) {
                subBuilder = existingValue.toBuilder();
            }
            if (subBuilder == null) {
                subBuilder = extension.getMessageDefaultInstance().newBuilderForType();
            }
            subBuilder.mergeFrom(rawBytes, extensionRegistry);
            MessageLite value = subBuilder.build();
            this.ensureExtensionsAreMutable().setField(extension.descriptor, extension.singularToFieldSetType(value));
        }

        FieldSet<ExtensionDescriptor> ensureExtensionsAreMutable() {
            if (this.extensions.isImmutable()) {
                this.extensions = this.extensions.clone();
            }
            return this.extensions;
        }

        private void verifyExtensionContainingType(GeneratedExtension<MessageType, ?> extension) {
            if (extension.getContainingTypeDefaultInstance() != this.getDefaultInstanceForType()) {
                throw new IllegalArgumentException("This extension is for a different message type.  Please make sure that you are not suppressing any generics type warnings.");
            }
        }

        @Override
        public final <Type> boolean hasExtension(ExtensionLite<MessageType, Type> extension) {
            GeneratedExtension extensionLite = GeneratedMessageLite.checkIsLite(extension);
            this.verifyExtensionContainingType(extensionLite);
            return this.extensions.hasField(extensionLite.descriptor);
        }

        @Override
        public final <Type> int getExtensionCount(ExtensionLite<MessageType, List<Type>> extension) {
            GeneratedExtension extensionLite = GeneratedMessageLite.checkIsLite(extension);
            this.verifyExtensionContainingType(extensionLite);
            return this.extensions.getRepeatedFieldCount(extensionLite.descriptor);
        }

        @Override
        public final <Type> Type getExtension(ExtensionLite<MessageType, Type> extension) {
            GeneratedExtension extensionLite = GeneratedMessageLite.checkIsLite(extension);
            this.verifyExtensionContainingType(extensionLite);
            Object value = this.extensions.getField(extensionLite.descriptor);
            if (value == null) {
                return extensionLite.defaultValue;
            }
            return (Type)extensionLite.fromFieldSetType(value);
        }

        @Override
        public final <Type> Type getExtension(ExtensionLite<MessageType, List<Type>> extension, int index) {
            GeneratedExtension extensionLite = GeneratedMessageLite.checkIsLite(extension);
            this.verifyExtensionContainingType(extensionLite);
            return (Type)extensionLite.singularFromFieldSetType(this.extensions.getRepeatedField(extensionLite.descriptor, index));
        }

        protected boolean extensionsAreInitialized() {
            return this.extensions.isInitialized();
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

        protected class ExtensionWriter {
            private final Iterator<Map.Entry<ExtensionDescriptor, Object>> iter;
            private Map.Entry<ExtensionDescriptor, Object> next;
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
                    ExtensionDescriptor extension = this.next.getKey();
                    if (this.messageSetWireFormat && extension.getLiteJavaType() == WireFormat.JavaType.MESSAGE && !extension.isRepeated()) {
                        output.writeMessageSetExtension(extension.getNumber(), (MessageLite)this.next.getValue());
                    } else {
                        FieldSet.writeField(extension, this.next.getValue(), output);
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

    public static interface ExtendableMessageOrBuilder<MessageType extends ExtendableMessage<MessageType, BuilderType>, BuilderType extends ExtendableBuilder<MessageType, BuilderType>>
    extends MessageLiteOrBuilder {
        public <Type> boolean hasExtension(ExtensionLite<MessageType, Type> var1);

        public <Type> int getExtensionCount(ExtensionLite<MessageType, List<Type>> var1);

        public <Type> Type getExtension(ExtensionLite<MessageType, Type> var1);

        public <Type> Type getExtension(ExtensionLite<MessageType, List<Type>> var1, int var2);
    }

    public static abstract class Builder<MessageType extends GeneratedMessageLite<MessageType, BuilderType>, BuilderType extends Builder<MessageType, BuilderType>>
    extends AbstractMessageLite.Builder<MessageType, BuilderType> {
        private final MessageType defaultInstance;
        protected MessageType instance;
        protected boolean isBuilt;

        protected Builder(MessageType defaultInstance) {
            this.defaultInstance = defaultInstance;
            this.instance = (GeneratedMessageLite)((GeneratedMessageLite)defaultInstance).dynamicMethod(MethodToInvoke.NEW_MUTABLE_INSTANCE);
            this.isBuilt = false;
        }

        protected final void copyOnWrite() {
            if (this.isBuilt) {
                this.copyOnWriteInternal();
                this.isBuilt = false;
            }
        }

        protected void copyOnWriteInternal() {
            GeneratedMessageLite newInstance = (GeneratedMessageLite)((GeneratedMessageLite)this.instance).dynamicMethod(MethodToInvoke.NEW_MUTABLE_INSTANCE);
            this.mergeFromInstance(newInstance, this.instance);
            this.instance = newInstance;
        }

        @Override
        public final boolean isInitialized() {
            return GeneratedMessageLite.isInitialized(this.instance, false);
        }

        public final BuilderType clear() {
            this.instance = (GeneratedMessageLite)((GeneratedMessageLite)this.instance).dynamicMethod(MethodToInvoke.NEW_MUTABLE_INSTANCE);
            return (BuilderType)this;
        }

        @Override
        public BuilderType clone() {
            MessageLite.Builder builder = ((GeneratedMessageLite)this.getDefaultInstanceForType()).newBuilderForType();
            ((Builder)builder).mergeFrom((MessageType)this.buildPartial());
            return (BuilderType)builder;
        }

        public MessageType buildPartial() {
            if (this.isBuilt) {
                return this.instance;
            }
            ((GeneratedMessageLite)this.instance).makeImmutable();
            this.isBuilt = true;
            return this.instance;
        }

        public final MessageType build() {
            MessageLite result = this.buildPartial();
            if (!((GeneratedMessageLite)result).isInitialized()) {
                throw Builder.newUninitializedMessageException(result);
            }
            return (MessageType)result;
        }

        @Override
        protected BuilderType internalMergeFrom(MessageType message) {
            return this.mergeFrom(message);
        }

        public BuilderType mergeFrom(MessageType message) {
            this.copyOnWrite();
            this.mergeFromInstance(this.instance, message);
            return (BuilderType)this;
        }

        private void mergeFromInstance(MessageType dest, MessageType src) {
            Protobuf.getInstance().schemaFor(dest).mergeFrom(dest, src);
        }

        public MessageType getDefaultInstanceForType() {
            return this.defaultInstance;
        }

        @Override
        public BuilderType mergeFrom(byte[] input, int offset, int length, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this.copyOnWrite();
            try {
                Protobuf.getInstance().schemaFor(this.instance).mergeFrom(this.instance, input, offset, offset + length, new ArrayDecoders.Registers(extensionRegistry));
            }
            catch (InvalidProtocolBufferException e) {
                throw e;
            }
            catch (IndexOutOfBoundsException e) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            catch (IOException e) {
                throw new RuntimeException("Reading from byte array should not throw IOException.", e);
            }
            return (BuilderType)this;
        }

        @Override
        public BuilderType mergeFrom(byte[] input, int offset, int length) throws InvalidProtocolBufferException {
            return (BuilderType)this.mergeFrom(input, offset, length, ExtensionRegistryLite.getEmptyRegistry());
        }

        @Override
        public BuilderType mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            this.copyOnWrite();
            try {
                Protobuf.getInstance().schemaFor(this.instance).mergeFrom(this.instance, CodedInputStreamReader.forCodedInput(input), extensionRegistry);
            }
            catch (RuntimeException e) {
                if (e.getCause() instanceof IOException) {
                    throw (IOException)e.getCause();
                }
                throw e;
            }
            return (BuilderType)this;
        }
    }

    public static enum MethodToInvoke {
        GET_MEMOIZED_IS_INITIALIZED,
        SET_MEMOIZED_IS_INITIALIZED,
        BUILD_MESSAGE_INFO,
        NEW_MUTABLE_INSTANCE,
        NEW_BUILDER,
        GET_DEFAULT_INSTANCE,
        GET_PARSER;

    }
}

