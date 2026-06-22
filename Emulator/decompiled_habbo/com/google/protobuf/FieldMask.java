/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.AbstractParser;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.FieldMaskOrBuilder;
import com.google.protobuf.FieldMaskProto;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.LazyStringArrayList;
import com.google.protobuf.LazyStringList;
import com.google.protobuf.Message;
import com.google.protobuf.Parser;
import com.google.protobuf.ProtocolStringList;
import com.google.protobuf.UnknownFieldSet;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class FieldMask
extends GeneratedMessageV3
implements FieldMaskOrBuilder {
    private static final long serialVersionUID = 0L;
    public static final int PATHS_FIELD_NUMBER = 1;
    private LazyStringList paths_;
    private byte memoizedIsInitialized = (byte)-1;
    private static final FieldMask DEFAULT_INSTANCE = new FieldMask();
    private static final Parser<FieldMask> PARSER = new AbstractParser<FieldMask>(){

        @Override
        public FieldMask parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new FieldMask(input, extensionRegistry);
        }
    };

    private FieldMask(GeneratedMessageV3.Builder<?> builder) {
        super(builder);
    }

    private FieldMask() {
        this.paths_ = LazyStringArrayList.EMPTY;
    }

    @Override
    protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
        return new FieldMask();
    }

    @Override
    public final UnknownFieldSet getUnknownFields() {
        return this.unknownFields;
    }

    private FieldMask(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                        String s = input.readStringRequireUtf8();
                        if (!(mutable_bitField0_ & true)) {
                            this.paths_ = new LazyStringArrayList();
                            mutable_bitField0_ |= true;
                        }
                        this.paths_.add(s);
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
            if (mutable_bitField0_ & true) {
                this.paths_ = this.paths_.getUnmodifiableView();
            }
            this.unknownFields = unknownFields.build();
            this.makeExtensionsImmutable();
        }
    }

    public static final Descriptors.Descriptor getDescriptor() {
        return FieldMaskProto.internal_static_google_protobuf_FieldMask_descriptor;
    }

    @Override
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return FieldMaskProto.internal_static_google_protobuf_FieldMask_fieldAccessorTable.ensureFieldAccessorsInitialized(FieldMask.class, Builder.class);
    }

    public ProtocolStringList getPathsList() {
        return this.paths_;
    }

    @Override
    public int getPathsCount() {
        return this.paths_.size();
    }

    @Override
    public String getPaths(int index) {
        return (String)this.paths_.get(index);
    }

    @Override
    public ByteString getPathsBytes(int index) {
        return this.paths_.getByteString(index);
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
        for (int i = 0; i < this.paths_.size(); ++i) {
            GeneratedMessageV3.writeString(output, 1, this.paths_.getRaw(i));
        }
        this.unknownFields.writeTo(output);
    }

    @Override
    public int getSerializedSize() {
        int size = this.memoizedSize;
        if (size != -1) {
            return size;
        }
        size = 0;
        int dataSize = 0;
        for (int i = 0; i < this.paths_.size(); ++i) {
            dataSize += FieldMask.computeStringSizeNoTag(this.paths_.getRaw(i));
        }
        size += dataSize;
        size += 1 * this.getPathsList().size();
        this.memoizedSize = size += this.unknownFields.getSerializedSize();
        return size;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof FieldMask)) {
            return super.equals(obj);
        }
        FieldMask other = (FieldMask)obj;
        if (!this.getPathsList().equals(other.getPathsList())) {
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
        hash = 19 * hash + FieldMask.getDescriptor().hashCode();
        if (this.getPathsCount() > 0) {
            hash = 37 * hash + 1;
            hash = 53 * hash + this.getPathsList().hashCode();
        }
        this.memoizedHashCode = hash = 29 * hash + this.unknownFields.hashCode();
        return hash;
    }

    public static FieldMask parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static FieldMask parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static FieldMask parseFrom(ByteString data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static FieldMask parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static FieldMask parseFrom(byte[] data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static FieldMask parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static FieldMask parseFrom(InputStream input) throws IOException {
        return GeneratedMessageV3.parseWithIOException(PARSER, input);
    }

    public static FieldMask parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static FieldMask parseDelimitedFrom(InputStream input) throws IOException {
        return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }

    public static FieldMask parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }

    public static FieldMask parseFrom(CodedInputStream input) throws IOException {
        return GeneratedMessageV3.parseWithIOException(PARSER, input);
    }

    public static FieldMask parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }

    @Override
    public Builder newBuilderForType() {
        return FieldMask.newBuilder();
    }

    public static Builder newBuilder() {
        return DEFAULT_INSTANCE.toBuilder();
    }

    public static Builder newBuilder(FieldMask prototype) {
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

    public static FieldMask getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public static Parser<FieldMask> parser() {
        return PARSER;
    }

    public Parser<FieldMask> getParserForType() {
        return PARSER;
    }

    @Override
    public FieldMask getDefaultInstanceForType() {
        return DEFAULT_INSTANCE;
    }

    public static final class Builder
    extends GeneratedMessageV3.Builder<Builder>
    implements FieldMaskOrBuilder {
        private int bitField0_;
        private LazyStringList paths_ = LazyStringArrayList.EMPTY;

        public static final Descriptors.Descriptor getDescriptor() {
            return FieldMaskProto.internal_static_google_protobuf_FieldMask_descriptor;
        }

        @Override
        protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return FieldMaskProto.internal_static_google_protobuf_FieldMask_fieldAccessorTable.ensureFieldAccessorsInitialized(FieldMask.class, Builder.class);
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
            this.paths_ = LazyStringArrayList.EMPTY;
            this.bitField0_ &= 0xFFFFFFFE;
            return this;
        }

        @Override
        public Descriptors.Descriptor getDescriptorForType() {
            return FieldMaskProto.internal_static_google_protobuf_FieldMask_descriptor;
        }

        @Override
        public FieldMask getDefaultInstanceForType() {
            return FieldMask.getDefaultInstance();
        }

        @Override
        public FieldMask build() {
            FieldMask result = this.buildPartial();
            if (!result.isInitialized()) {
                throw Builder.newUninitializedMessageException(result);
            }
            return result;
        }

        @Override
        public FieldMask buildPartial() {
            FieldMask result = new FieldMask(this);
            int from_bitField0_ = this.bitField0_;
            if ((this.bitField0_ & 1) != 0) {
                this.paths_ = this.paths_.getUnmodifiableView();
                this.bitField0_ &= 0xFFFFFFFE;
            }
            result.paths_ = this.paths_;
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
            if (other instanceof FieldMask) {
                return this.mergeFrom((FieldMask)other);
            }
            super.mergeFrom(other);
            return this;
        }

        public Builder mergeFrom(FieldMask other) {
            if (other == FieldMask.getDefaultInstance()) {
                return this;
            }
            if (!other.paths_.isEmpty()) {
                if (this.paths_.isEmpty()) {
                    this.paths_ = other.paths_;
                    this.bitField0_ &= 0xFFFFFFFE;
                } else {
                    this.ensurePathsIsMutable();
                    this.paths_.addAll(other.paths_);
                }
                this.onChanged();
            }
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
            FieldMask parsedMessage = null;
            try {
                parsedMessage = (FieldMask)PARSER.parsePartialFrom(input, extensionRegistry);
            }
            catch (InvalidProtocolBufferException e) {
                parsedMessage = (FieldMask)e.getUnfinishedMessage();
                throw e.unwrapIOException();
            }
            finally {
                if (parsedMessage != null) {
                    this.mergeFrom(parsedMessage);
                }
            }
            return this;
        }

        private void ensurePathsIsMutable() {
            if ((this.bitField0_ & 1) == 0) {
                this.paths_ = new LazyStringArrayList(this.paths_);
                this.bitField0_ |= 1;
            }
        }

        public ProtocolStringList getPathsList() {
            return this.paths_.getUnmodifiableView();
        }

        @Override
        public int getPathsCount() {
            return this.paths_.size();
        }

        @Override
        public String getPaths(int index) {
            return (String)this.paths_.get(index);
        }

        @Override
        public ByteString getPathsBytes(int index) {
            return this.paths_.getByteString(index);
        }

        public Builder setPaths(int index, String value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.ensurePathsIsMutable();
            this.paths_.set(index, value);
            this.onChanged();
            return this;
        }

        public Builder addPaths(String value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.ensurePathsIsMutable();
            this.paths_.add(value);
            this.onChanged();
            return this;
        }

        public Builder addAllPaths(Iterable<String> values) {
            this.ensurePathsIsMutable();
            AbstractMessageLite.Builder.addAll(values, this.paths_);
            this.onChanged();
            return this;
        }

        public Builder clearPaths() {
            this.paths_ = LazyStringArrayList.EMPTY;
            this.bitField0_ &= 0xFFFFFFFE;
            this.onChanged();
            return this;
        }

        public Builder addPathsBytes(ByteString value) {
            if (value == null) {
                throw new NullPointerException();
            }
            AbstractMessageLite.checkByteStringIsUtf8(value);
            this.ensurePathsIsMutable();
            this.paths_.add(value);
            this.onChanged();
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
}

