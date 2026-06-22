/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.AbstractMessage;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.Extension;
import com.google.protobuf.ExtensionLite;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.FieldSet;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.Internal;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.LazyField;
import com.google.protobuf.MapField;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.MessageReflection;
import com.google.protobuf.Parser;
import com.google.protobuf.ProtocolMessageEnum;
import com.google.protobuf.UnknownFieldSet;
import com.google.protobuf.WireFormat;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public abstract class GeneratedMessage
extends AbstractMessage
implements Serializable {
    private static final long serialVersionUID = 1L;
    protected static boolean alwaysUseFieldBuilders = false;
    protected UnknownFieldSet unknownFields;

    protected GeneratedMessage() {
        this.unknownFields = UnknownFieldSet.getDefaultInstance();
    }

    protected GeneratedMessage(Builder<?> builder) {
        this.unknownFields = builder.getUnknownFields();
    }

    public Parser<? extends GeneratedMessage> getParserForType() {
        throw new UnsupportedOperationException("This is supposed to be overridden by subclasses.");
    }

    static void enableAlwaysUseFieldBuildersForTesting() {
        alwaysUseFieldBuilders = true;
    }

    protected abstract FieldAccessorTable internalGetFieldAccessorTable();

    @Override
    public Descriptors.Descriptor getDescriptorForType() {
        return this.internalGetFieldAccessorTable().descriptor;
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
        return unknownFields.mergeFieldFrom(tag, input);
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

    public static <ContainingType extends Message, Type> GeneratedExtension<ContainingType, Type> newMessageScopedGeneratedExtension(final Message scope, final int descriptorIndex, Class singularType, Message defaultInstance) {
        return new GeneratedExtension(new CachedDescriptorRetriever(){

            @Override
            public Descriptors.FieldDescriptor loadDescriptor() {
                return scope.getDescriptorForType().getExtensions().get(descriptorIndex);
            }
        }, singularType, defaultInstance, Extension.ExtensionType.IMMUTABLE);
    }

    public static <ContainingType extends Message, Type> GeneratedExtension<ContainingType, Type> newFileScopedGeneratedExtension(Class singularType, Message defaultInstance) {
        return new GeneratedExtension(null, singularType, defaultInstance, Extension.ExtensionType.IMMUTABLE);
    }

    public static <ContainingType extends Message, Type> GeneratedExtension<ContainingType, Type> newMessageScopedGeneratedExtension(final Message scope, final String name, Class singularType, Message defaultInstance) {
        return new GeneratedExtension(new CachedDescriptorRetriever(){

            @Override
            protected Descriptors.FieldDescriptor loadDescriptor() {
                return scope.getDescriptorForType().findFieldByName(name);
            }
        }, singularType, defaultInstance, Extension.ExtensionType.MUTABLE);
    }

    public static <ContainingType extends Message, Type> GeneratedExtension<ContainingType, Type> newFileScopedGeneratedExtension(final Class singularType, Message defaultInstance, final String descriptorOuterClass, final String extensionName) {
        return new GeneratedExtension(new CachedDescriptorRetriever(){

            @Override
            protected Descriptors.FieldDescriptor loadDescriptor() {
                try {
                    Class<?> clazz = singularType.getClassLoader().loadClass(descriptorOuterClass);
                    Descriptors.FileDescriptor file = (Descriptors.FileDescriptor)clazz.getField("descriptor").get(null);
                    return file.findExtensionByName(extensionName);
                }
                catch (Exception e) {
                    throw new RuntimeException("Cannot load descriptors: " + descriptorOuterClass + " is not a valid descriptor class name", e);
                }
            }
        }, singularType, defaultInstance, Extension.ExtensionType.MUTABLE);
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

    public static final class FieldAccessorTable {
        private final Descriptors.Descriptor descriptor;
        private final FieldAccessor[] fields;
        private String[] camelCaseNames;
        private final OneofAccessor[] oneofs;
        private volatile boolean initialized;

        public FieldAccessorTable(Descriptors.Descriptor descriptor, String[] camelCaseNames, Class<? extends GeneratedMessage> messageClass, Class<? extends Builder> builderClass) {
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

        private boolean isMapFieldEnabled(Descriptors.FieldDescriptor field) {
            boolean result = true;
            return result;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public FieldAccessorTable ensureFieldAccessorsInitialized(Class<? extends GeneratedMessage> messageClass, Class<? extends Builder> builderClass) {
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
                            if (field.isMapField() && this.isMapFieldEnabled(field)) {
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

            RepeatedMessageFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessage> messageClass, Class<? extends Builder> builderClass) {
                super(descriptor, camelCaseName, messageClass, builderClass);
                this.newBuilderMethod = GeneratedMessage.getMethodOrDie(this.type, "newBuilder", new Class[0]);
                this.getBuilderMethodBuilder = GeneratedMessage.getMethodOrDie(builderClass, "get" + camelCaseName + "Builder", new Class[]{Integer.TYPE});
            }

            private Object coerceType(Object value) {
                if (this.type.isInstance(value)) {
                    return value;
                }
                return ((Message.Builder)GeneratedMessage.invokeOrDie(this.newBuilderMethod, null, new Object[0])).mergeFrom((Message)value).build();
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
                return (Message.Builder)GeneratedMessage.invokeOrDie(this.newBuilderMethod, null, new Object[0]);
            }

            @Override
            public Message.Builder getRepeatedBuilder(Builder builder, int index) {
                return (Message.Builder)GeneratedMessage.invokeOrDie(this.getBuilderMethodBuilder, builder, new Object[]{index});
            }
        }

        private static final class SingularMessageFieldAccessor
        extends SingularFieldAccessor {
            private final Method newBuilderMethod;
            private final Method getBuilderMethodBuilder;

            SingularMessageFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessage> messageClass, Class<? extends Builder> builderClass, String containingOneofCamelCaseName) {
                super(descriptor, camelCaseName, messageClass, builderClass, containingOneofCamelCaseName);
                this.newBuilderMethod = GeneratedMessage.getMethodOrDie(this.type, "newBuilder", new Class[0]);
                this.getBuilderMethodBuilder = GeneratedMessage.getMethodOrDie(builderClass, "get" + camelCaseName + "Builder", new Class[0]);
            }

            private Object coerceType(Object value) {
                if (this.type.isInstance(value)) {
                    return value;
                }
                return ((Message.Builder)GeneratedMessage.invokeOrDie(this.newBuilderMethod, null, new Object[0])).mergeFrom((Message)value).buildPartial();
            }

            @Override
            public void set(Builder builder, Object value) {
                super.set(builder, this.coerceType(value));
            }

            @Override
            public Message.Builder newBuilder() {
                return (Message.Builder)GeneratedMessage.invokeOrDie(this.newBuilderMethod, null, new Object[0]);
            }

            @Override
            public Message.Builder getBuilder(Builder builder) {
                return (Message.Builder)GeneratedMessage.invokeOrDie(this.getBuilderMethodBuilder, builder, new Object[0]);
            }
        }

        private static final class SingularStringFieldAccessor
        extends SingularFieldAccessor {
            private final Method getBytesMethod;
            private final Method getBytesMethodBuilder;
            private final Method setBytesMethodBuilder;

            SingularStringFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessage> messageClass, Class<? extends Builder> builderClass, String containingOneofCamelCaseName) {
                super(descriptor, camelCaseName, messageClass, builderClass, containingOneofCamelCaseName);
                this.getBytesMethod = GeneratedMessage.getMethodOrDie(messageClass, "get" + camelCaseName + "Bytes", new Class[0]);
                this.getBytesMethodBuilder = GeneratedMessage.getMethodOrDie(builderClass, "get" + camelCaseName + "Bytes", new Class[0]);
                this.setBytesMethodBuilder = GeneratedMessage.getMethodOrDie(builderClass, "set" + camelCaseName + "Bytes", new Class[]{ByteString.class});
            }

            @Override
            public Object getRaw(GeneratedMessage message) {
                return GeneratedMessage.invokeOrDie(this.getBytesMethod, message, new Object[0]);
            }

            @Override
            public Object getRaw(Builder builder) {
                return GeneratedMessage.invokeOrDie(this.getBytesMethodBuilder, builder, new Object[0]);
            }

            @Override
            public void set(Builder builder, Object value) {
                if (value instanceof ByteString) {
                    GeneratedMessage.invokeOrDie(this.setBytesMethodBuilder, builder, new Object[]{value});
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

            RepeatedEnumFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessage> messageClass, Class<? extends Builder> builderClass) {
                super(descriptor, camelCaseName, messageClass, builderClass);
                this.enumDescriptor = descriptor.getEnumType();
                this.valueOfMethod = GeneratedMessage.getMethodOrDie(this.type, "valueOf", new Class[]{Descriptors.EnumValueDescriptor.class});
                this.getValueDescriptorMethod = GeneratedMessage.getMethodOrDie(this.type, "getValueDescriptor", new Class[0]);
                this.supportUnknownEnumValue = descriptor.getFile().supportsUnknownEnumValue();
                if (this.supportUnknownEnumValue) {
                    this.getRepeatedValueMethod = GeneratedMessage.getMethodOrDie(messageClass, "get" + camelCaseName + "Value", new Class[]{Integer.TYPE});
                    this.getRepeatedValueMethodBuilder = GeneratedMessage.getMethodOrDie(builderClass, "get" + camelCaseName + "Value", new Class[]{Integer.TYPE});
                    this.setRepeatedValueMethod = GeneratedMessage.getMethodOrDie(builderClass, "set" + camelCaseName + "Value", new Class[]{Integer.TYPE, Integer.TYPE});
                    this.addRepeatedValueMethod = GeneratedMessage.getMethodOrDie(builderClass, "add" + camelCaseName + "Value", new Class[]{Integer.TYPE});
                }
            }

            @Override
            public Object get(GeneratedMessage message) {
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
            public Object getRepeated(GeneratedMessage message, int index) {
                if (this.supportUnknownEnumValue) {
                    int value = (Integer)GeneratedMessage.invokeOrDie(this.getRepeatedValueMethod, message, new Object[]{index});
                    return this.enumDescriptor.findValueByNumberCreatingIfUnknown(value);
                }
                return GeneratedMessage.invokeOrDie(this.getValueDescriptorMethod, super.getRepeated(message, index), new Object[0]);
            }

            @Override
            public Object getRepeated(Builder builder, int index) {
                if (this.supportUnknownEnumValue) {
                    int value = (Integer)GeneratedMessage.invokeOrDie(this.getRepeatedValueMethodBuilder, builder, new Object[]{index});
                    return this.enumDescriptor.findValueByNumberCreatingIfUnknown(value);
                }
                return GeneratedMessage.invokeOrDie(this.getValueDescriptorMethod, super.getRepeated(builder, index), new Object[0]);
            }

            @Override
            public void setRepeated(Builder builder, int index, Object value) {
                if (this.supportUnknownEnumValue) {
                    GeneratedMessage.invokeOrDie(this.setRepeatedValueMethod, builder, new Object[]{index, ((Descriptors.EnumValueDescriptor)value).getNumber()});
                    return;
                }
                super.setRepeated(builder, index, GeneratedMessage.invokeOrDie(this.valueOfMethod, null, new Object[]{value}));
            }

            @Override
            public void addRepeated(Builder builder, Object value) {
                if (this.supportUnknownEnumValue) {
                    GeneratedMessage.invokeOrDie(this.addRepeatedValueMethod, builder, new Object[]{((Descriptors.EnumValueDescriptor)value).getNumber()});
                    return;
                }
                super.addRepeated(builder, GeneratedMessage.invokeOrDie(this.valueOfMethod, null, new Object[]{value}));
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

            SingularEnumFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessage> messageClass, Class<? extends Builder> builderClass, String containingOneofCamelCaseName) {
                super(descriptor, camelCaseName, messageClass, builderClass, containingOneofCamelCaseName);
                this.enumDescriptor = descriptor.getEnumType();
                this.valueOfMethod = GeneratedMessage.getMethodOrDie(this.type, "valueOf", new Class[]{Descriptors.EnumValueDescriptor.class});
                this.getValueDescriptorMethod = GeneratedMessage.getMethodOrDie(this.type, "getValueDescriptor", new Class[0]);
                this.supportUnknownEnumValue = descriptor.getFile().supportsUnknownEnumValue();
                if (this.supportUnknownEnumValue) {
                    this.getValueMethod = GeneratedMessage.getMethodOrDie(messageClass, "get" + camelCaseName + "Value", new Class[0]);
                    this.getValueMethodBuilder = GeneratedMessage.getMethodOrDie(builderClass, "get" + camelCaseName + "Value", new Class[0]);
                    this.setValueMethod = GeneratedMessage.getMethodOrDie(builderClass, "set" + camelCaseName + "Value", new Class[]{Integer.TYPE});
                }
            }

            @Override
            public Object get(GeneratedMessage message) {
                if (this.supportUnknownEnumValue) {
                    int value = (Integer)GeneratedMessage.invokeOrDie(this.getValueMethod, message, new Object[0]);
                    return this.enumDescriptor.findValueByNumberCreatingIfUnknown(value);
                }
                return GeneratedMessage.invokeOrDie(this.getValueDescriptorMethod, super.get(message), new Object[0]);
            }

            @Override
            public Object get(Builder builder) {
                if (this.supportUnknownEnumValue) {
                    int value = (Integer)GeneratedMessage.invokeOrDie(this.getValueMethodBuilder, builder, new Object[0]);
                    return this.enumDescriptor.findValueByNumberCreatingIfUnknown(value);
                }
                return GeneratedMessage.invokeOrDie(this.getValueDescriptorMethod, super.get(builder), new Object[0]);
            }

            @Override
            public void set(Builder builder, Object value) {
                if (this.supportUnknownEnumValue) {
                    GeneratedMessage.invokeOrDie(this.setValueMethod, builder, new Object[]{((Descriptors.EnumValueDescriptor)value).getNumber()});
                    return;
                }
                super.set(builder, GeneratedMessage.invokeOrDie(this.valueOfMethod, null, new Object[]{value}));
            }
        }

        private static class MapFieldAccessor
        implements FieldAccessor {
            private final Descriptors.FieldDescriptor field;
            private final Message mapEntryMessageDefaultInstance;

            MapFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessage> messageClass, Class<? extends Builder> builderClass) {
                this.field = descriptor;
                Method getDefaultInstanceMethod = GeneratedMessage.getMethodOrDie(messageClass, "getDefaultInstance", new Class[0]);
                MapField<?, ?> defaultMapField = this.getMapField((GeneratedMessage)GeneratedMessage.invokeOrDie(getDefaultInstanceMethod, null, new Object[0]));
                this.mapEntryMessageDefaultInstance = defaultMapField.getMapEntryMessageDefaultInstance();
            }

            private MapField<?, ?> getMapField(GeneratedMessage message) {
                return message.internalGetMapField(this.field.getNumber());
            }

            private MapField<?, ?> getMapField(Builder builder) {
                return builder.internalGetMapField(this.field.getNumber());
            }

            private MapField<?, ?> getMutableMapField(Builder builder) {
                return builder.internalGetMutableMapField(this.field.getNumber());
            }

            @Override
            public Object get(GeneratedMessage message) {
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
            public Object getRaw(GeneratedMessage message) {
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
            public Object getRepeated(GeneratedMessage message, int index) {
                return this.getMapField(message).getList().get(index);
            }

            @Override
            public Object getRepeated(Builder builder, int index) {
                return this.getMapField(builder).getList().get(index);
            }

            @Override
            public Object getRepeatedRaw(GeneratedMessage message, int index) {
                return this.getRepeated(message, index);
            }

            @Override
            public Object getRepeatedRaw(Builder builder, int index) {
                return this.getRepeated(builder, index);
            }

            @Override
            public void setRepeated(Builder builder, int index, Object value) {
                this.getMutableMapField(builder).getMutableList().set(index, (Message)value);
            }

            @Override
            public void addRepeated(Builder builder, Object value) {
                this.getMutableMapField(builder).getMutableList().add((Message)value);
            }

            @Override
            public boolean has(GeneratedMessage message) {
                throw new UnsupportedOperationException("hasField() is not supported for repeated fields.");
            }

            @Override
            public boolean has(Builder builder) {
                throw new UnsupportedOperationException("hasField() is not supported for repeated fields.");
            }

            @Override
            public int getRepeatedCount(GeneratedMessage message) {
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
            protected final Method getMethod;
            protected final Method getMethodBuilder;
            protected final Method getRepeatedMethod;
            protected final Method getRepeatedMethodBuilder;
            protected final Method setRepeatedMethod;
            protected final Method addRepeatedMethod;
            protected final Method getCountMethod;
            protected final Method getCountMethodBuilder;
            protected final Method clearMethod;

            RepeatedFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessage> messageClass, Class<? extends Builder> builderClass) {
                this.getMethod = GeneratedMessage.getMethodOrDie(messageClass, "get" + camelCaseName + "List", new Class[0]);
                this.getMethodBuilder = GeneratedMessage.getMethodOrDie(builderClass, "get" + camelCaseName + "List", new Class[0]);
                this.getRepeatedMethod = GeneratedMessage.getMethodOrDie(messageClass, "get" + camelCaseName, new Class[]{Integer.TYPE});
                this.getRepeatedMethodBuilder = GeneratedMessage.getMethodOrDie(builderClass, "get" + camelCaseName, new Class[]{Integer.TYPE});
                this.type = this.getRepeatedMethod.getReturnType();
                this.setRepeatedMethod = GeneratedMessage.getMethodOrDie(builderClass, "set" + camelCaseName, new Class[]{Integer.TYPE, this.type});
                this.addRepeatedMethod = GeneratedMessage.getMethodOrDie(builderClass, "add" + camelCaseName, new Class[]{this.type});
                this.getCountMethod = GeneratedMessage.getMethodOrDie(messageClass, "get" + camelCaseName + "Count", new Class[0]);
                this.getCountMethodBuilder = GeneratedMessage.getMethodOrDie(builderClass, "get" + camelCaseName + "Count", new Class[0]);
                this.clearMethod = GeneratedMessage.getMethodOrDie(builderClass, "clear" + camelCaseName, new Class[0]);
            }

            @Override
            public Object get(GeneratedMessage message) {
                return GeneratedMessage.invokeOrDie(this.getMethod, message, new Object[0]);
            }

            @Override
            public Object get(Builder builder) {
                return GeneratedMessage.invokeOrDie(this.getMethodBuilder, builder, new Object[0]);
            }

            @Override
            public Object getRaw(GeneratedMessage message) {
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
            public Object getRepeated(GeneratedMessage message, int index) {
                return GeneratedMessage.invokeOrDie(this.getRepeatedMethod, message, new Object[]{index});
            }

            @Override
            public Object getRepeated(Builder builder, int index) {
                return GeneratedMessage.invokeOrDie(this.getRepeatedMethodBuilder, builder, new Object[]{index});
            }

            @Override
            public Object getRepeatedRaw(GeneratedMessage message, int index) {
                return this.getRepeated(message, index);
            }

            @Override
            public Object getRepeatedRaw(Builder builder, int index) {
                return this.getRepeated(builder, index);
            }

            @Override
            public void setRepeated(Builder builder, int index, Object value) {
                GeneratedMessage.invokeOrDie(this.setRepeatedMethod, builder, new Object[]{index, value});
            }

            @Override
            public void addRepeated(Builder builder, Object value) {
                GeneratedMessage.invokeOrDie(this.addRepeatedMethod, builder, new Object[]{value});
            }

            @Override
            public boolean has(GeneratedMessage message) {
                throw new UnsupportedOperationException("hasField() called on a repeated field.");
            }

            @Override
            public boolean has(Builder builder) {
                throw new UnsupportedOperationException("hasField() called on a repeated field.");
            }

            @Override
            public int getRepeatedCount(GeneratedMessage message) {
                return (Integer)GeneratedMessage.invokeOrDie(this.getCountMethod, message, new Object[0]);
            }

            @Override
            public int getRepeatedCount(Builder builder) {
                return (Integer)GeneratedMessage.invokeOrDie(this.getCountMethodBuilder, builder, new Object[0]);
            }

            @Override
            public void clear(Builder builder) {
                GeneratedMessage.invokeOrDie(this.clearMethod, builder, new Object[0]);
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
        }

        private static class SingularFieldAccessor
        implements FieldAccessor {
            protected final Class<?> type;
            protected final Method getMethod;
            protected final Method getMethodBuilder;
            protected final Method setMethod;
            protected final Method hasMethod;
            protected final Method hasMethodBuilder;
            protected final Method clearMethod;
            protected final Method caseMethod;
            protected final Method caseMethodBuilder;
            protected final Descriptors.FieldDescriptor field;
            protected final boolean isOneofField;
            protected final boolean hasHasMethod;

            SingularFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessage> messageClass, Class<? extends Builder> builderClass, String containingOneofCamelCaseName) {
                this.field = descriptor;
                this.isOneofField = descriptor.getContainingOneof() != null;
                this.hasHasMethod = FieldAccessorTable.supportFieldPresence(descriptor.getFile()) || !this.isOneofField && descriptor.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE;
                this.getMethod = GeneratedMessage.getMethodOrDie(messageClass, "get" + camelCaseName, new Class[0]);
                this.getMethodBuilder = GeneratedMessage.getMethodOrDie(builderClass, "get" + camelCaseName, new Class[0]);
                this.type = this.getMethod.getReturnType();
                this.setMethod = GeneratedMessage.getMethodOrDie(builderClass, "set" + camelCaseName, new Class[]{this.type});
                this.hasMethod = this.hasHasMethod ? GeneratedMessage.getMethodOrDie(messageClass, "has" + camelCaseName, new Class[0]) : null;
                this.hasMethodBuilder = this.hasHasMethod ? GeneratedMessage.getMethodOrDie(builderClass, "has" + camelCaseName, new Class[0]) : null;
                this.clearMethod = GeneratedMessage.getMethodOrDie(builderClass, "clear" + camelCaseName, new Class[0]);
                this.caseMethod = this.isOneofField ? GeneratedMessage.getMethodOrDie(messageClass, "get" + containingOneofCamelCaseName + "Case", new Class[0]) : null;
                this.caseMethodBuilder = this.isOneofField ? GeneratedMessage.getMethodOrDie(builderClass, "get" + containingOneofCamelCaseName + "Case", new Class[0]) : null;
            }

            private int getOneofFieldNumber(GeneratedMessage message) {
                return ((Internal.EnumLite)GeneratedMessage.invokeOrDie(this.caseMethod, message, new Object[0])).getNumber();
            }

            private int getOneofFieldNumber(Builder builder) {
                return ((Internal.EnumLite)GeneratedMessage.invokeOrDie(this.caseMethodBuilder, builder, new Object[0])).getNumber();
            }

            @Override
            public Object get(GeneratedMessage message) {
                return GeneratedMessage.invokeOrDie(this.getMethod, message, new Object[0]);
            }

            @Override
            public Object get(Builder builder) {
                return GeneratedMessage.invokeOrDie(this.getMethodBuilder, builder, new Object[0]);
            }

            @Override
            public Object getRaw(GeneratedMessage message) {
                return this.get(message);
            }

            @Override
            public Object getRaw(Builder builder) {
                return this.get(builder);
            }

            @Override
            public void set(Builder builder, Object value) {
                GeneratedMessage.invokeOrDie(this.setMethod, builder, new Object[]{value});
            }

            @Override
            public Object getRepeated(GeneratedMessage message, int index) {
                throw new UnsupportedOperationException("getRepeatedField() called on a singular field.");
            }

            @Override
            public Object getRepeatedRaw(GeneratedMessage message, int index) {
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
            public boolean has(GeneratedMessage message) {
                if (!this.hasHasMethod) {
                    if (this.isOneofField) {
                        return this.getOneofFieldNumber(message) == this.field.getNumber();
                    }
                    return !this.get(message).equals(this.field.getDefaultValue());
                }
                return (Boolean)GeneratedMessage.invokeOrDie(this.hasMethod, message, new Object[0]);
            }

            @Override
            public boolean has(Builder builder) {
                if (!this.hasHasMethod) {
                    if (this.isOneofField) {
                        return this.getOneofFieldNumber(builder) == this.field.getNumber();
                    }
                    return !this.get(builder).equals(this.field.getDefaultValue());
                }
                return (Boolean)GeneratedMessage.invokeOrDie(this.hasMethodBuilder, builder, new Object[0]);
            }

            @Override
            public int getRepeatedCount(GeneratedMessage message) {
                throw new UnsupportedOperationException("getRepeatedFieldSize() called on a singular field.");
            }

            @Override
            public int getRepeatedCount(Builder builder) {
                throw new UnsupportedOperationException("getRepeatedFieldSize() called on a singular field.");
            }

            @Override
            public void clear(Builder builder) {
                GeneratedMessage.invokeOrDie(this.clearMethod, builder, new Object[0]);
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
        }

        private static class OneofAccessor {
            private final Descriptors.Descriptor descriptor;
            private final Method caseMethod;
            private final Method caseMethodBuilder;
            private final Method clearMethod;

            OneofAccessor(Descriptors.Descriptor descriptor, String camelCaseName, Class<? extends GeneratedMessage> messageClass, Class<? extends Builder> builderClass) {
                this.descriptor = descriptor;
                this.caseMethod = GeneratedMessage.getMethodOrDie(messageClass, "get" + camelCaseName + "Case", new Class[0]);
                this.caseMethodBuilder = GeneratedMessage.getMethodOrDie(builderClass, "get" + camelCaseName + "Case", new Class[0]);
                this.clearMethod = GeneratedMessage.getMethodOrDie(builderClass, "clear" + camelCaseName, new Class[0]);
            }

            public boolean has(GeneratedMessage message) {
                return ((Internal.EnumLite)GeneratedMessage.invokeOrDie(this.caseMethod, message, new Object[0])).getNumber() != 0;
            }

            public boolean has(Builder builder) {
                return ((Internal.EnumLite)GeneratedMessage.invokeOrDie(this.caseMethodBuilder, builder, new Object[0])).getNumber() != 0;
            }

            public Descriptors.FieldDescriptor get(GeneratedMessage message) {
                int fieldNumber = ((Internal.EnumLite)GeneratedMessage.invokeOrDie(this.caseMethod, message, new Object[0])).getNumber();
                if (fieldNumber > 0) {
                    return this.descriptor.findFieldByNumber(fieldNumber);
                }
                return null;
            }

            public Descriptors.FieldDescriptor get(Builder builder) {
                int fieldNumber = ((Internal.EnumLite)GeneratedMessage.invokeOrDie(this.caseMethodBuilder, builder, new Object[0])).getNumber();
                if (fieldNumber > 0) {
                    return this.descriptor.findFieldByNumber(fieldNumber);
                }
                return null;
            }

            public void clear(Builder builder) {
                GeneratedMessage.invokeOrDie(this.clearMethod, builder, new Object[0]);
            }
        }

        private static interface FieldAccessor {
            public Object get(GeneratedMessage var1);

            public Object get(Builder var1);

            public Object getRaw(GeneratedMessage var1);

            public Object getRaw(Builder var1);

            public void set(Builder var1, Object var2);

            public Object getRepeated(GeneratedMessage var1, int var2);

            public Object getRepeated(Builder var1, int var2);

            public Object getRepeatedRaw(GeneratedMessage var1, int var2);

            public Object getRepeatedRaw(Builder var1, int var2);

            public void setRepeated(Builder var1, int var2, Object var3);

            public void addRepeated(Builder var1, Object var2);

            public boolean has(GeneratedMessage var1);

            public boolean has(Builder var1);

            public int getRepeatedCount(GeneratedMessage var1);

            public int getRepeatedCount(Builder var1);

            public void clear(Builder var1);

            public Message.Builder newBuilder();

            public Message.Builder getBuilder(Builder var1);

            public Message.Builder getRepeatedBuilder(Builder var1, int var2);
        }
    }

    public static class GeneratedExtension<ContainingType extends Message, Type>
    extends Extension<ContainingType, Type> {
        private ExtensionDescriptorRetriever descriptorRetriever;
        private final Class singularType;
        private final Message messageDefaultInstance;
        private final Method enumValueOf;
        private final Method enumGetValueDescriptor;
        private final Extension.ExtensionType extensionType;

        GeneratedExtension(ExtensionDescriptorRetriever descriptorRetriever, Class singularType, Message messageDefaultInstance, Extension.ExtensionType extensionType) {
            if (Message.class.isAssignableFrom(singularType) && !singularType.isInstance(messageDefaultInstance)) {
                throw new IllegalArgumentException("Bad messageDefaultInstance for " + singularType.getName());
            }
            this.descriptorRetriever = descriptorRetriever;
            this.singularType = singularType;
            this.messageDefaultInstance = messageDefaultInstance;
            if (ProtocolMessageEnum.class.isAssignableFrom(singularType)) {
                this.enumValueOf = GeneratedMessage.getMethodOrDie(singularType, "valueOf", new Class[]{Descriptors.EnumValueDescriptor.class});
                this.enumGetValueDescriptor = GeneratedMessage.getMethodOrDie(singularType, "getValueDescriptor", new Class[0]);
            } else {
                this.enumValueOf = null;
                this.enumGetValueDescriptor = null;
            }
            this.extensionType = extensionType;
        }

        public void internalInit(final Descriptors.FieldDescriptor descriptor) {
            if (this.descriptorRetriever != null) {
                throw new IllegalStateException("Already initialized.");
            }
            this.descriptorRetriever = new ExtensionDescriptorRetriever(){

                @Override
                public Descriptors.FieldDescriptor getDescriptor() {
                    return descriptor;
                }
            };
        }

        @Override
        public Descriptors.FieldDescriptor getDescriptor() {
            if (this.descriptorRetriever == null) {
                throw new IllegalStateException("getDescriptor() called before internalInit()");
            }
            return this.descriptorRetriever.getDescriptor();
        }

        @Override
        public Message getMessageDefaultInstance() {
            return this.messageDefaultInstance;
        }

        @Override
        protected Extension.ExtensionType getExtensionType() {
            return this.extensionType;
        }

        @Override
        protected Object fromReflectionType(Object value) {
            Descriptors.FieldDescriptor descriptor = this.getDescriptor();
            if (descriptor.isRepeated()) {
                if (descriptor.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE || descriptor.getJavaType() == Descriptors.FieldDescriptor.JavaType.ENUM) {
                    ArrayList<Object> result = new ArrayList<Object>();
                    for (Object element : (List)value) {
                        result.add(this.singularFromReflectionType(element));
                    }
                    return result;
                }
                return value;
            }
            return this.singularFromReflectionType(value);
        }

        @Override
        protected Object singularFromReflectionType(Object value) {
            Descriptors.FieldDescriptor descriptor = this.getDescriptor();
            switch (descriptor.getJavaType()) {
                case MESSAGE: {
                    if (this.singularType.isInstance(value)) {
                        return value;
                    }
                    return this.messageDefaultInstance.newBuilderForType().mergeFrom((Message)value).build();
                }
                case ENUM: {
                    return GeneratedMessage.invokeOrDie(this.enumValueOf, null, new Object[]{(Descriptors.EnumValueDescriptor)value});
                }
            }
            return value;
        }

        @Override
        protected Object toReflectionType(Object value) {
            Descriptors.FieldDescriptor descriptor = this.getDescriptor();
            if (descriptor.isRepeated()) {
                if (descriptor.getJavaType() == Descriptors.FieldDescriptor.JavaType.ENUM) {
                    ArrayList<Object> result = new ArrayList<Object>();
                    for (Object element : (List)value) {
                        result.add(this.singularToReflectionType(element));
                    }
                    return result;
                }
                return value;
            }
            return this.singularToReflectionType(value);
        }

        @Override
        protected Object singularToReflectionType(Object value) {
            Descriptors.FieldDescriptor descriptor = this.getDescriptor();
            switch (descriptor.getJavaType()) {
                case ENUM: {
                    return GeneratedMessage.invokeOrDie(this.enumGetValueDescriptor, value, new Object[0]);
                }
            }
            return value;
        }

        @Override
        public int getNumber() {
            return this.getDescriptor().getNumber();
        }

        @Override
        public WireFormat.FieldType getLiteType() {
            return this.getDescriptor().getLiteType();
        }

        @Override
        public boolean isRepeated() {
            return this.getDescriptor().isRepeated();
        }

        @Override
        public Type getDefaultValue() {
            if (this.isRepeated()) {
                return (Type)Collections.emptyList();
            }
            if (this.getDescriptor().getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                return (Type)this.messageDefaultInstance;
            }
            return (Type)this.singularFromReflectionType(this.getDescriptor().getDefaultValue());
        }
    }

    private static abstract class CachedDescriptorRetriever
    implements ExtensionDescriptorRetriever {
        private volatile Descriptors.FieldDescriptor descriptor;

        private CachedDescriptorRetriever() {
        }

        protected abstract Descriptors.FieldDescriptor loadDescriptor();

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Descriptors.FieldDescriptor getDescriptor() {
            if (this.descriptor == null) {
                CachedDescriptorRetriever cachedDescriptorRetriever = this;
                synchronized (cachedDescriptorRetriever) {
                    if (this.descriptor == null) {
                        this.descriptor = this.loadDescriptor();
                    }
                }
            }
            return this.descriptor;
        }
    }

    static interface ExtensionDescriptorRetriever {
        public Descriptors.FieldDescriptor getDescriptor();
    }

    public static abstract class ExtendableBuilder<MessageType extends ExtendableMessage, BuilderType extends ExtendableBuilder<MessageType, BuilderType>>
    extends Builder<BuilderType>
    implements ExtendableMessageOrBuilder<MessageType> {
        private FieldSet<Descriptors.FieldDescriptor> extensions = FieldSet.emptySet();

        protected ExtendableBuilder() {
        }

        protected ExtendableBuilder(BuilderParent parent) {
            super(parent);
        }

        void internalSetExtensionSet(FieldSet<Descriptors.FieldDescriptor> extensions) {
            this.extensions = extensions;
        }

        @Override
        public BuilderType clear() {
            this.extensions = FieldSet.emptySet();
            return (BuilderType)((ExtendableBuilder)super.clear());
        }

        @Override
        public BuilderType clone() {
            return (BuilderType)((ExtendableBuilder)super.clone());
        }

        private void ensureExtensionsIsMutable() {
            if (this.extensions.isImmutable()) {
                this.extensions = this.extensions.clone();
            }
        }

        private void verifyExtensionContainingType(Extension<MessageType, ?> extension) {
            if (extension.getDescriptor().getContainingType() != this.getDescriptorForType()) {
                throw new IllegalArgumentException("Extension is for type \"" + extension.getDescriptor().getContainingType().getFullName() + "\" which does not match message type \"" + this.getDescriptorForType().getFullName() + "\".");
            }
        }

        @Override
        public final <Type> boolean hasExtension(ExtensionLite<MessageType, Type> extensionLite) {
            Extension extension = GeneratedMessage.checkNotLite(extensionLite);
            this.verifyExtensionContainingType(extension);
            return this.extensions.hasField(extension.getDescriptor());
        }

        @Override
        public final <Type> int getExtensionCount(ExtensionLite<MessageType, List<Type>> extensionLite) {
            Extension extension = GeneratedMessage.checkNotLite(extensionLite);
            this.verifyExtensionContainingType(extension);
            Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
            return this.extensions.getRepeatedFieldCount(descriptor);
        }

        @Override
        public final <Type> Type getExtension(ExtensionLite<MessageType, Type> extensionLite) {
            Extension extension = GeneratedMessage.checkNotLite(extensionLite);
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
            Extension extension = GeneratedMessage.checkNotLite(extensionLite);
            this.verifyExtensionContainingType(extension);
            Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
            return (Type)extension.singularFromReflectionType(this.extensions.getRepeatedField(descriptor, index));
        }

        public final <Type> BuilderType setExtension(ExtensionLite<MessageType, Type> extensionLite, Type value) {
            Extension extension = GeneratedMessage.checkNotLite(extensionLite);
            this.verifyExtensionContainingType(extension);
            this.ensureExtensionsIsMutable();
            Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
            this.extensions.setField(descriptor, extension.toReflectionType(value));
            this.onChanged();
            return (BuilderType)this;
        }

        public final <Type> BuilderType setExtension(ExtensionLite<MessageType, List<Type>> extensionLite, int index, Type value) {
            Extension extension = GeneratedMessage.checkNotLite(extensionLite);
            this.verifyExtensionContainingType(extension);
            this.ensureExtensionsIsMutable();
            Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
            this.extensions.setRepeatedField(descriptor, index, extension.singularToReflectionType(value));
            this.onChanged();
            return (BuilderType)this;
        }

        public final <Type> BuilderType addExtension(ExtensionLite<MessageType, List<Type>> extensionLite, Type value) {
            Extension extension = GeneratedMessage.checkNotLite(extensionLite);
            this.verifyExtensionContainingType(extension);
            this.ensureExtensionsIsMutable();
            Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
            this.extensions.addRepeatedField(descriptor, extension.singularToReflectionType(value));
            this.onChanged();
            return (BuilderType)this;
        }

        public final <Type> BuilderType clearExtension(ExtensionLite<MessageType, ?> extensionLite) {
            Extension extension = GeneratedMessage.checkNotLite(extensionLite);
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
        public final <Type> boolean hasExtension(GeneratedExtension<MessageType, Type> extension) {
            return this.hasExtension((ExtensionLite<MessageType, Type>)extension);
        }

        @Override
        public final <Type> int getExtensionCount(Extension<MessageType, List<Type>> extension) {
            return this.getExtensionCount((ExtensionLite<MessageType, List<Type>>)extension);
        }

        @Override
        public final <Type> int getExtensionCount(GeneratedExtension<MessageType, List<Type>> extension) {
            return this.getExtensionCount((ExtensionLite<MessageType, List<Type>>)extension);
        }

        @Override
        public final <Type> Type getExtension(Extension<MessageType, Type> extension) {
            return this.getExtension((ExtensionLite<MessageType, Type>)extension);
        }

        @Override
        public final <Type> Type getExtension(GeneratedExtension<MessageType, Type> extension) {
            return this.getExtension((ExtensionLite<MessageType, Type>)extension);
        }

        @Override
        public final <Type> Type getExtension(Extension<MessageType, List<Type>> extension, int index) {
            return this.getExtension((ExtensionLite<MessageType, List<Type>>)extension, index);
        }

        @Override
        public final <Type> Type getExtension(GeneratedExtension<MessageType, List<Type>> extension, int index) {
            return this.getExtension((ExtensionLite<MessageType, List<Type>>)extension, index);
        }

        public final <Type> BuilderType setExtension(Extension<MessageType, Type> extension, Type value) {
            return this.setExtension((ExtensionLite<MessageType, Type>)extension, value);
        }

        public <Type> BuilderType setExtension(GeneratedExtension<MessageType, Type> extension, Type value) {
            return this.setExtension((ExtensionLite<MessageType, Type>)extension, value);
        }

        public final <Type> BuilderType setExtension(Extension<MessageType, List<Type>> extension, int index, Type value) {
            return this.setExtension((ExtensionLite<MessageType, List<Type>>)extension, index, value);
        }

        public <Type> BuilderType setExtension(GeneratedExtension<MessageType, List<Type>> extension, int index, Type value) {
            return this.setExtension((ExtensionLite<MessageType, List<Type>>)extension, index, value);
        }

        public final <Type> BuilderType addExtension(Extension<MessageType, List<Type>> extension, Type value) {
            return this.addExtension((ExtensionLite<MessageType, List<Type>>)extension, value);
        }

        public <Type> BuilderType addExtension(GeneratedExtension<MessageType, List<Type>> extension, Type value) {
            return this.addExtension((ExtensionLite<MessageType, List<Type>>)extension, value);
        }

        public final <Type> BuilderType clearExtension(Extension<MessageType, ?> extension) {
            return this.clearExtension((ExtensionLite<MessageType, ?>)extension);
        }

        public <Type> BuilderType clearExtension(GeneratedExtension<MessageType, ?> extension) {
            return this.clearExtension((ExtensionLite<MessageType, ?>)extension);
        }

        protected boolean extensionsAreInitialized() {
            return this.extensions.isInitialized();
        }

        private FieldSet<Descriptors.FieldDescriptor> buildExtensions() {
            this.extensions.makeImmutable();
            return this.extensions;
        }

        @Override
        public boolean isInitialized() {
            return super.isInitialized() && this.extensionsAreInitialized();
        }

        @Override
        protected boolean parseUnknownField(CodedInputStream input, UnknownFieldSet.Builder unknownFields, ExtensionRegistryLite extensionRegistry, int tag) throws IOException {
            return MessageReflection.mergeFieldFrom(input, unknownFields, extensionRegistry, this.getDescriptorForType(), new MessageReflection.BuilderAdapter(this), tag);
        }

        @Override
        public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
            Map result = ((Builder)this).getAllFieldsMutable();
            result.putAll(this.extensions.getAllFields());
            return Collections.unmodifiableMap(result);
        }

        @Override
        public Object getField(Descriptors.FieldDescriptor field) {
            if (field.isExtension()) {
                this.verifyContainingType(field);
                Object value = this.extensions.getField(field);
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

        @Override
        public boolean hasField(Descriptors.FieldDescriptor field) {
            if (field.isExtension()) {
                this.verifyContainingType(field);
                return this.extensions.hasField(field);
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

        protected final void mergeExtensionFields(ExtendableMessage other) {
            this.ensureExtensionsIsMutable();
            this.extensions.mergeFrom(other.extensions);
            this.onChanged();
        }

        private void verifyContainingType(Descriptors.FieldDescriptor field) {
            if (field.getContainingType() != this.getDescriptorForType()) {
                throw new IllegalArgumentException("FieldDescriptor does not match message type.");
            }
        }
    }

    public static abstract class ExtendableMessage<MessageType extends ExtendableMessage>
    extends GeneratedMessage
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
            Extension extension = GeneratedMessage.checkNotLite(extensionLite);
            this.verifyExtensionContainingType(extension);
            return this.extensions.hasField(extension.getDescriptor());
        }

        @Override
        public final <Type> int getExtensionCount(ExtensionLite<MessageType, List<Type>> extensionLite) {
            Extension extension = GeneratedMessage.checkNotLite(extensionLite);
            this.verifyExtensionContainingType(extension);
            Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
            return this.extensions.getRepeatedFieldCount(descriptor);
        }

        @Override
        public final <Type> Type getExtension(ExtensionLite<MessageType, Type> extensionLite) {
            Extension extension = GeneratedMessage.checkNotLite(extensionLite);
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
            Extension extension = GeneratedMessage.checkNotLite(extensionLite);
            this.verifyExtensionContainingType(extension);
            Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
            return (Type)extension.singularFromReflectionType(this.extensions.getRepeatedField(descriptor, index));
        }

        @Override
        public final <Type> boolean hasExtension(Extension<MessageType, Type> extension) {
            return this.hasExtension((ExtensionLite<MessageType, Type>)extension);
        }

        @Override
        public final <Type> boolean hasExtension(GeneratedExtension<MessageType, Type> extension) {
            return this.hasExtension((ExtensionLite<MessageType, Type>)extension);
        }

        @Override
        public final <Type> int getExtensionCount(Extension<MessageType, List<Type>> extension) {
            return this.getExtensionCount((ExtensionLite<MessageType, List<Type>>)extension);
        }

        @Override
        public final <Type> int getExtensionCount(GeneratedExtension<MessageType, List<Type>> extension) {
            return this.getExtensionCount((ExtensionLite<MessageType, List<Type>>)extension);
        }

        @Override
        public final <Type> Type getExtension(Extension<MessageType, Type> extension) {
            return this.getExtension((ExtensionLite<MessageType, Type>)extension);
        }

        @Override
        public final <Type> Type getExtension(GeneratedExtension<MessageType, Type> extension) {
            return this.getExtension((ExtensionLite<MessageType, Type>)extension);
        }

        @Override
        public final <Type> Type getExtension(Extension<MessageType, List<Type>> extension, int index) {
            return this.getExtension((ExtensionLite<MessageType, List<Type>>)extension, index);
        }

        @Override
        public final <Type> Type getExtension(GeneratedExtension<MessageType, List<Type>> extension, int index) {
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
            return MessageReflection.mergeFieldFrom(input, unknownFields, extensionRegistry, this.getDescriptorForType(), new MessageReflection.ExtensionAdapter(this.extensions), tag);
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
            Map result = ((GeneratedMessage)this).getAllFieldsMutable(false);
            result.putAll(this.getExtensionFields());
            return Collections.unmodifiableMap(result);
        }

        @Override
        public Map<Descriptors.FieldDescriptor, Object> getAllFieldsRaw() {
            Map result = ((GeneratedMessage)this).getAllFieldsMutable(false);
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

        public <Type> boolean hasExtension(GeneratedExtension<MessageType, Type> var1);

        public <Type> int getExtensionCount(Extension<MessageType, List<Type>> var1);

        public <Type> int getExtensionCount(GeneratedExtension<MessageType, List<Type>> var1);

        public <Type> Type getExtension(Extension<MessageType, Type> var1);

        public <Type> Type getExtension(GeneratedExtension<MessageType, Type> var1);

        public <Type> Type getExtension(Extension<MessageType, List<Type>> var1, int var2);

        public <Type> Type getExtension(GeneratedExtension<MessageType, List<Type>> var1, int var2);
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

        public BuilderType setUnknownFields(UnknownFieldSet unknownFields) {
            this.unknownFields = unknownFields;
            this.onChanged();
            return (BuilderType)this;
        }

        @Override
        public BuilderType mergeUnknownFields(UnknownFieldSet unknownFields) {
            this.unknownFields = UnknownFieldSet.newBuilder(this.unknownFields).mergeFrom(unknownFields).build();
            this.onChanged();
            return (BuilderType)this;
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

        protected boolean parseUnknownField(CodedInputStream input, UnknownFieldSet.Builder unknownFields, ExtensionRegistryLite extensionRegistry, int tag) throws IOException {
            return unknownFields.mergeFieldFrom(tag, input);
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
}

