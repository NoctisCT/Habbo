/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.AbstractParser;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MapEntry;
import com.google.protobuf.MapField;
import com.google.protobuf.Message;
import com.google.protobuf.Parser;
import com.google.protobuf.StructOrBuilder;
import com.google.protobuf.StructProto;
import com.google.protobuf.UnknownFieldSet;
import com.google.protobuf.Value;
import com.google.protobuf.WireFormat;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Map;

public final class Struct
extends GeneratedMessageV3
implements StructOrBuilder {
    private static final long serialVersionUID = 0L;
    public static final int FIELDS_FIELD_NUMBER = 1;
    private MapField<String, Value> fields_;
    private byte memoizedIsInitialized = (byte)-1;
    private static final Struct DEFAULT_INSTANCE = new Struct();
    private static final Parser<Struct> PARSER = new AbstractParser<Struct>(){

        @Override
        public Struct parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new Struct(input, extensionRegistry);
        }
    };

    private Struct(GeneratedMessageV3.Builder<?> builder) {
        super(builder);
    }

    private Struct() {
    }

    @Override
    protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
        return new Struct();
    }

    @Override
    public final UnknownFieldSet getUnknownFields() {
        return this.unknownFields;
    }

    private Struct(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        this();
        if (extensionRegistry == null) {
            throw new NullPointerException();
        }
        boolean mutable_bitField0_ = false;
        UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();
        try {
            boolean done = false;
            block10: while (!done) {
                int tag = input.readTag();
                switch (tag) {
                    case 0: {
                        done = true;
                        continue block10;
                    }
                    case 10: {
                        if (!(mutable_bitField0_ & true)) {
                            this.fields_ = MapField.newMapField(FieldsDefaultEntryHolder.defaultEntry);
                            mutable_bitField0_ |= true;
                        }
                        MapEntry<String, Value> fields__ = input.readMessage(FieldsDefaultEntryHolder.defaultEntry.getParserForType(), extensionRegistry);
                        this.fields_.getMutableMap().put(fields__.getKey(), fields__.getValue());
                        continue block10;
                    }
                }
                if (this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) continue;
                done = true;
            }
        }
        catch (InvalidProtocolBufferException e) {
            throw e.setUnfinishedMessage(this);
        }
        catch (IOException e) {
            throw new InvalidProtocolBufferException(e).setUnfinishedMessage(this);
        }
        finally {
            this.unknownFields = unknownFields.build();
            this.makeExtensionsImmutable();
        }
    }

    public static final Descriptors.Descriptor getDescriptor() {
        return StructProto.internal_static_google_protobuf_Struct_descriptor;
    }

    @Override
    protected MapField internalGetMapField(int number) {
        switch (number) {
            case 1: {
                return this.internalGetFields();
            }
        }
        throw new RuntimeException("Invalid map field number: " + number);
    }

    @Override
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return StructProto.internal_static_google_protobuf_Struct_fieldAccessorTable.ensureFieldAccessorsInitialized(Struct.class, Builder.class);
    }

    private MapField<String, Value> internalGetFields() {
        if (this.fields_ == null) {
            return MapField.emptyMapField(FieldsDefaultEntryHolder.defaultEntry);
        }
        return this.fields_;
    }

    @Override
    public int getFieldsCount() {
        return this.internalGetFields().getMap().size();
    }

    @Override
    public boolean containsFields(String key) {
        if (key == null) {
            throw new NullPointerException();
        }
        return this.internalGetFields().getMap().containsKey(key);
    }

    @Override
    @Deprecated
    public Map<String, Value> getFields() {
        return this.getFieldsMap();
    }

    @Override
    public Map<String, Value> getFieldsMap() {
        return this.internalGetFields().getMap();
    }

    @Override
    public Value getFieldsOrDefault(String key, Value defaultValue) {
        if (key == null) {
            throw new NullPointerException();
        }
        Map<String, Value> map = this.internalGetFields().getMap();
        return map.containsKey(key) ? map.get(key) : defaultValue;
    }

    @Override
    public Value getFieldsOrThrow(String key) {
        if (key == null) {
            throw new NullPointerException();
        }
        Map<String, Value> map = this.internalGetFields().getMap();
        if (!map.containsKey(key)) {
            throw new IllegalArgumentException();
        }
        return map.get(key);
    }

    @Override
    public final boolean isInitialized() {
        byte isInitialized = this.memoizedIsInitialized;
        if (isInitialized == 1) {
            return true;
        }
        if (isInitialized == 0) {
            return false;
        }
        this.memoizedIsInitialized = 1;
        return true;
    }

    @Override
    public void writeTo(CodedOutputStream output) throws IOException {
        GeneratedMessageV3.serializeStringMapTo(output, this.internalGetFields(), FieldsDefaultEntryHolder.defaultEntry, 1);
        this.unknownFields.writeTo(output);
    }

    @Override
    public int getSerializedSize() {
        int size = this.memoizedSize;
        if (size != -1) {
            return size;
        }
        size = 0;
        for (Map.Entry<String, Value> entry : this.internalGetFields().getMap().entrySet()) {
            Message fields__ = ((MapEntry.Builder)FieldsDefaultEntryHolder.defaultEntry.newBuilderForType()).setKey(entry.getKey()).setValue(entry.getValue()).build();
            size += CodedOutputStream.computeMessageSize(1, fields__);
        }
        this.memoizedSize = size += this.unknownFields.getSerializedSize();
        return size;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Struct)) {
            return super.equals(obj);
        }
        Struct other = (Struct)obj;
        if (!this.internalGetFields().equals(other.internalGetFields())) {
            return false;
        }
        return this.unknownFields.equals(other.unknownFields);
    }

    @Override
    public int hashCode() {
        if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
        }
        int hash = 41;
        hash = 19 * hash + Struct.getDescriptor().hashCode();
        if (!this.internalGetFields().getMap().isEmpty()) {
            hash = 37 * hash + 1;
            hash = 53 * hash + this.internalGetFields().hashCode();
        }
        this.memoizedHashCode = hash = 29 * hash + this.unknownFields.hashCode();
        return hash;
    }

    public static Struct parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static Struct parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static Struct parseFrom(ByteString data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static Struct parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static Struct parseFrom(byte[] data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static Struct parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static Struct parseFrom(InputStream input) throws IOException {
        return GeneratedMessageV3.parseWithIOException(PARSER, input);
    }

    public static Struct parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static Struct parseDelimitedFrom(InputStream input) throws IOException {
        return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }

    public static Struct parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }

    public static Struct parseFrom(CodedInputStream input) throws IOException {
        return GeneratedMessageV3.parseWithIOException(PARSER, input);
    }

    public static Struct parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }

    @Override
    public Builder newBuilderForType() {
        return Struct.newBuilder();
    }

    public static Builder newBuilder() {
        return DEFAULT_INSTANCE.toBuilder();
    }

    public static Builder newBuilder(Struct prototype) {
        return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }

    @Override
    public Builder toBuilder() {
        return this == DEFAULT_INSTANCE ? new Builder() : new Builder().mergeFrom(this);
    }

    @Override
    protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
        Builder builder = new Builder(parent);
        return builder;
    }

    public static Struct getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public static Parser<Struct> parser() {
        return PARSER;
    }

    public Parser<Struct> getParserForType() {
        return PARSER;
    }

    @Override
    public Struct getDefaultInstanceForType() {
        return DEFAULT_INSTANCE;
    }

    public static final class Builder
    extends GeneratedMessageV3.Builder<Builder>
    implements StructOrBuilder {
        private int bitField0_;
        private MapField<String, Value> fields_;

        public static final Descriptors.Descriptor getDescriptor() {
            return StructProto.internal_static_google_protobuf_Struct_descriptor;
        }

        @Override
        protected MapField internalGetMapField(int number) {
            switch (number) {
                case 1: {
                    return this.internalGetFields();
                }
            }
            throw new RuntimeException("Invalid map field number: " + number);
        }

        @Override
        protected MapField internalGetMutableMapField(int number) {
            switch (number) {
                case 1: {
                    return this.internalGetMutableFields();
                }
            }
            throw new RuntimeException("Invalid map field number: " + number);
        }

        @Override
        protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return StructProto.internal_static_google_protobuf_Struct_fieldAccessorTable.ensureFieldAccessorsInitialized(Struct.class, Builder.class);
        }

        private Builder() {
            this.maybeForceBuilderInitialization();
        }

        private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
        }

        private void maybeForceBuilderInitialization() {
            if (GeneratedMessageV3.alwaysUseFieldBuilders) {
                // empty if block
            }
        }

        @Override
        public Builder clear() {
            super.clear();
            this.internalGetMutableFields().clear();
            return this;
        }

        @Override
        public Descriptors.Descriptor getDescriptorForType() {
            return StructProto.internal_static_google_protobuf_Struct_descriptor;
        }

        @Override
        public Struct getDefaultInstanceForType() {
            return Struct.getDefaultInstance();
        }

        @Override
        public Struct build() {
            Struct result = this.buildPartial();
            if (!result.isInitialized()) {
                throw Builder.newUninitializedMessageException(result);
            }
            return result;
        }

        @Override
        public Struct buildPartial() {
            Struct result = new Struct(this);
            int from_bitField0_ = this.bitField0_;
            result.fields_ = this.internalGetFields();
            result.fields_.makeImmutable();
            this.onBuilt();
            return result;
        }

        @Override
        public Builder clone() {
            return (Builder)super.clone();
        }

        @Override
        public Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (Builder)super.setField(field, value);
        }

        @Override
        public Builder clearField(Descriptors.FieldDescriptor field) {
            return (Builder)super.clearField(field);
        }

        @Override
        public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (Builder)super.clearOneof(oneof);
        }

        @Override
        public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (Builder)super.setRepeatedField(field, index, value);
        }

        @Override
        public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (Builder)super.addRepeatedField(field, value);
        }

        @Override
        public Builder mergeFrom(Message other) {
            if (other instanceof Struct) {
                return this.mergeFrom((Struct)other);
            }
            super.mergeFrom(other);
            return this;
        }

        public Builder mergeFrom(Struct other) {
            if (other == Struct.getDefaultInstance()) {
                return this;
            }
            this.internalGetMutableFields().mergeFrom(other.internalGetFields());
            this.mergeUnknownFields(other.unknownFields);
            this.onChanged();
            return this;
        }

        @Override
        public final boolean isInitialized() {
            return true;
        }

        @Override
        public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            Struct parsedMessage = null;
            try {
                parsedMessage = (Struct)PARSER.parsePartialFrom(input, extensionRegistry);
            }
            catch (InvalidProtocolBufferException e) {
                parsedMessage = (Struct)e.getUnfinishedMessage();
                throw e.unwrapIOException();
            }
            finally {
                if (parsedMessage != null) {
                    this.mergeFrom(parsedMessage);
                }
            }
            return this;
        }

        private MapField<String, Value> internalGetFields() {
            if (this.fields_ == null) {
                return MapField.emptyMapField(FieldsDefaultEntryHolder.defaultEntry);
            }
            return this.fields_;
        }

        private MapField<String, Value> internalGetMutableFields() {
            this.onChanged();
            if (this.fields_ == null) {
                this.fields_ = MapField.newMapField(FieldsDefaultEntryHolder.defaultEntry);
            }
            if (!this.fields_.isMutable()) {
                this.fields_ = this.fields_.copy();
            }
            return this.fields_;
        }

        @Override
        public int getFieldsCount() {
            return this.internalGetFields().getMap().size();
        }

        @Override
        public boolean containsFields(String key) {
            if (key == null) {
                throw new NullPointerException();
            }
            return this.internalGetFields().getMap().containsKey(key);
        }

        @Override
        @Deprecated
        public Map<String, Value> getFields() {
            return this.getFieldsMap();
        }

        @Override
        public Map<String, Value> getFieldsMap() {
            return this.internalGetFields().getMap();
        }

        @Override
        public Value getFieldsOrDefault(String key, Value defaultValue) {
            if (key == null) {
                throw new NullPointerException();
            }
            Map<String, Value> map = this.internalGetFields().getMap();
            return map.containsKey(key) ? map.get(key) : defaultValue;
        }

        @Override
        public Value getFieldsOrThrow(String key) {
            if (key == null) {
                throw new NullPointerException();
            }
            Map<String, Value> map = this.internalGetFields().getMap();
            if (!map.containsKey(key)) {
                throw new IllegalArgumentException();
            }
            return map.get(key);
        }

        public Builder clearFields() {
            this.internalGetMutableFields().getMutableMap().clear();
            return this;
        }

        public Builder removeFields(String key) {
            if (key == null) {
                throw new NullPointerException();
            }
            this.internalGetMutableFields().getMutableMap().remove(key);
            return this;
        }

        @Deprecated
        public Map<String, Value> getMutableFields() {
            return this.internalGetMutableFields().getMutableMap();
        }

        public Builder putFields(String key, Value value) {
            if (key == null) {
                throw new NullPointerException();
            }
            if (value == null) {
                throw new NullPointerException();
            }
            this.internalGetMutableFields().getMutableMap().put(key, value);
            return this;
        }

        public Builder putAllFields(Map<String, Value> values) {
            this.internalGetMutableFields().getMutableMap().putAll(values);
            return this;
        }

        @Override
        public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (Builder)super.setUnknownFields(unknownFields);
        }

        @Override
        public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (Builder)super.mergeUnknownFields(unknownFields);
        }
    }

    private static final class FieldsDefaultEntryHolder {
        static final MapEntry<String, Value> defaultEntry = MapEntry.newDefaultInstance(StructProto.internal_static_google_protobuf_Struct_FieldsEntry_descriptor, WireFormat.FieldType.STRING, "", WireFormat.FieldType.MESSAGE, Value.getDefaultInstance());

        private FieldsDefaultEntryHolder() {
        }
    }
}

