/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.AbstractParser;
import com.google.protobuf.ApiProto;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Internal;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.MethodOrBuilder;
import com.google.protobuf.Option;
import com.google.protobuf.OptionOrBuilder;
import com.google.protobuf.Parser;
import com.google.protobuf.RepeatedFieldBuilderV3;
import com.google.protobuf.Syntax;
import com.google.protobuf.UnknownFieldSet;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Method
extends GeneratedMessageV3
implements MethodOrBuilder {
    private static final long serialVersionUID = 0L;
    public static final int NAME_FIELD_NUMBER = 1;
    private volatile Object name_;
    public static final int REQUEST_TYPE_URL_FIELD_NUMBER = 2;
    private volatile Object requestTypeUrl_;
    public static final int REQUEST_STREAMING_FIELD_NUMBER = 3;
    private boolean requestStreaming_;
    public static final int RESPONSE_TYPE_URL_FIELD_NUMBER = 4;
    private volatile Object responseTypeUrl_;
    public static final int RESPONSE_STREAMING_FIELD_NUMBER = 5;
    private boolean responseStreaming_;
    public static final int OPTIONS_FIELD_NUMBER = 6;
    private List<Option> options_;
    public static final int SYNTAX_FIELD_NUMBER = 7;
    private int syntax_;
    private byte memoizedIsInitialized = (byte)-1;
    private static final Method DEFAULT_INSTANCE = new Method();
    private static final Parser<Method> PARSER = new AbstractParser<Method>(){

        @Override
        public Method parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new Method(input, extensionRegistry);
        }
    };

    private Method(GeneratedMessageV3.Builder<?> builder) {
        super(builder);
    }

    private Method() {
        this.name_ = "";
        this.requestTypeUrl_ = "";
        this.responseTypeUrl_ = "";
        this.options_ = Collections.emptyList();
        this.syntax_ = 0;
    }

    @Override
    protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
        return new Method();
    }

    @Override
    public final UnknownFieldSet getUnknownFields() {
        return this.unknownFields;
    }

    private Method(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        this();
        if (extensionRegistry == null) {
            throw new NullPointerException();
        }
        boolean mutable_bitField0_ = false;
        UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();
        try {
            boolean done = false;
            block16: while (!done) {
                int tag = input.readTag();
                switch (tag) {
                    case 0: {
                        done = true;
                        continue block16;
                    }
                    case 10: {
                        String s = input.readStringRequireUtf8();
                        this.name_ = s;
                        continue block16;
                    }
                    case 18: {
                        String s = input.readStringRequireUtf8();
                        this.requestTypeUrl_ = s;
                        continue block16;
                    }
                    case 24: {
                        this.requestStreaming_ = input.readBool();
                        continue block16;
                    }
                    case 34: {
                        String s = input.readStringRequireUtf8();
                        this.responseTypeUrl_ = s;
                        continue block16;
                    }
                    case 40: {
                        this.responseStreaming_ = input.readBool();
                        continue block16;
                    }
                    case 50: {
                        if (!(mutable_bitField0_ & true)) {
                            this.options_ = new ArrayList<Option>();
                            mutable_bitField0_ |= true;
                        }
                        this.options_.add(input.readMessage(Option.parser(), extensionRegistry));
                        continue block16;
                    }
                    case 56: {
                        int rawValue;
                        this.syntax_ = rawValue = input.readEnum();
                        continue block16;
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
                this.options_ = Collections.unmodifiableList(this.options_);
            }
            this.unknownFields = unknownFields.build();
            this.makeExtensionsImmutable();
        }
    }

    public static final Descriptors.Descriptor getDescriptor() {
        return ApiProto.internal_static_google_protobuf_Method_descriptor;
    }

    @Override
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return ApiProto.internal_static_google_protobuf_Method_fieldAccessorTable.ensureFieldAccessorsInitialized(Method.class, Builder.class);
    }

    @Override
    public String getName() {
        Object ref = this.name_;
        if (ref instanceof String) {
            return (String)ref;
        }
        ByteString bs = (ByteString)ref;
        String s = bs.toStringUtf8();
        this.name_ = s;
        return s;
    }

    @Override
    public ByteString getNameBytes() {
        Object ref = this.name_;
        if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.name_ = b;
            return b;
        }
        return (ByteString)ref;
    }

    @Override
    public String getRequestTypeUrl() {
        Object ref = this.requestTypeUrl_;
        if (ref instanceof String) {
            return (String)ref;
        }
        ByteString bs = (ByteString)ref;
        String s = bs.toStringUtf8();
        this.requestTypeUrl_ = s;
        return s;
    }

    @Override
    public ByteString getRequestTypeUrlBytes() {
        Object ref = this.requestTypeUrl_;
        if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.requestTypeUrl_ = b;
            return b;
        }
        return (ByteString)ref;
    }

    @Override
    public boolean getRequestStreaming() {
        return this.requestStreaming_;
    }

    @Override
    public String getResponseTypeUrl() {
        Object ref = this.responseTypeUrl_;
        if (ref instanceof String) {
            return (String)ref;
        }
        ByteString bs = (ByteString)ref;
        String s = bs.toStringUtf8();
        this.responseTypeUrl_ = s;
        return s;
    }

    @Override
    public ByteString getResponseTypeUrlBytes() {
        Object ref = this.responseTypeUrl_;
        if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.responseTypeUrl_ = b;
            return b;
        }
        return (ByteString)ref;
    }

    @Override
    public boolean getResponseStreaming() {
        return this.responseStreaming_;
    }

    @Override
    public List<Option> getOptionsList() {
        return this.options_;
    }

    @Override
    public List<? extends OptionOrBuilder> getOptionsOrBuilderList() {
        return this.options_;
    }

    @Override
    public int getOptionsCount() {
        return this.options_.size();
    }

    @Override
    public Option getOptions(int index) {
        return this.options_.get(index);
    }

    @Override
    public OptionOrBuilder getOptionsOrBuilder(int index) {
        return this.options_.get(index);
    }

    @Override
    public int getSyntaxValue() {
        return this.syntax_;
    }

    @Override
    public Syntax getSyntax() {
        Syntax result = Syntax.valueOf(this.syntax_);
        return result == null ? Syntax.UNRECOGNIZED : result;
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
        if (!this.getNameBytes().isEmpty()) {
            GeneratedMessageV3.writeString(output, 1, this.name_);
        }
        if (!this.getRequestTypeUrlBytes().isEmpty()) {
            GeneratedMessageV3.writeString(output, 2, this.requestTypeUrl_);
        }
        if (this.requestStreaming_) {
            output.writeBool(3, this.requestStreaming_);
        }
        if (!this.getResponseTypeUrlBytes().isEmpty()) {
            GeneratedMessageV3.writeString(output, 4, this.responseTypeUrl_);
        }
        if (this.responseStreaming_) {
            output.writeBool(5, this.responseStreaming_);
        }
        for (int i = 0; i < this.options_.size(); ++i) {
            output.writeMessage(6, this.options_.get(i));
        }
        if (this.syntax_ != Syntax.SYNTAX_PROTO2.getNumber()) {
            output.writeEnum(7, this.syntax_);
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
        if (!this.getNameBytes().isEmpty()) {
            size += GeneratedMessageV3.computeStringSize(1, this.name_);
        }
        if (!this.getRequestTypeUrlBytes().isEmpty()) {
            size += GeneratedMessageV3.computeStringSize(2, this.requestTypeUrl_);
        }
        if (this.requestStreaming_) {
            size += CodedOutputStream.computeBoolSize(3, this.requestStreaming_);
        }
        if (!this.getResponseTypeUrlBytes().isEmpty()) {
            size += GeneratedMessageV3.computeStringSize(4, this.responseTypeUrl_);
        }
        if (this.responseStreaming_) {
            size += CodedOutputStream.computeBoolSize(5, this.responseStreaming_);
        }
        for (int i = 0; i < this.options_.size(); ++i) {
            size += CodedOutputStream.computeMessageSize(6, this.options_.get(i));
        }
        if (this.syntax_ != Syntax.SYNTAX_PROTO2.getNumber()) {
            size += CodedOutputStream.computeEnumSize(7, this.syntax_);
        }
        this.memoizedSize = size += this.unknownFields.getSerializedSize();
        return size;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Method)) {
            return super.equals(obj);
        }
        Method other = (Method)obj;
        if (!this.getName().equals(other.getName())) {
            return false;
        }
        if (!this.getRequestTypeUrl().equals(other.getRequestTypeUrl())) {
            return false;
        }
        if (this.getRequestStreaming() != other.getRequestStreaming()) {
            return false;
        }
        if (!this.getResponseTypeUrl().equals(other.getResponseTypeUrl())) {
            return false;
        }
        if (this.getResponseStreaming() != other.getResponseStreaming()) {
            return false;
        }
        if (!this.getOptionsList().equals(other.getOptionsList())) {
            return false;
        }
        if (this.syntax_ != other.syntax_) {
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
        hash = 19 * hash + Method.getDescriptor().hashCode();
        hash = 37 * hash + 1;
        hash = 53 * hash + this.getName().hashCode();
        hash = 37 * hash + 2;
        hash = 53 * hash + this.getRequestTypeUrl().hashCode();
        hash = 37 * hash + 3;
        hash = 53 * hash + Internal.hashBoolean(this.getRequestStreaming());
        hash = 37 * hash + 4;
        hash = 53 * hash + this.getResponseTypeUrl().hashCode();
        hash = 37 * hash + 5;
        hash = 53 * hash + Internal.hashBoolean(this.getResponseStreaming());
        if (this.getOptionsCount() > 0) {
            hash = 37 * hash + 6;
            hash = 53 * hash + this.getOptionsList().hashCode();
        }
        hash = 37 * hash + 7;
        hash = 53 * hash + this.syntax_;
        this.memoizedHashCode = hash = 29 * hash + this.unknownFields.hashCode();
        return hash;
    }

    public static Method parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static Method parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static Method parseFrom(ByteString data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static Method parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static Method parseFrom(byte[] data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static Method parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static Method parseFrom(InputStream input) throws IOException {
        return GeneratedMessageV3.parseWithIOException(PARSER, input);
    }

    public static Method parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static Method parseDelimitedFrom(InputStream input) throws IOException {
        return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }

    public static Method parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }

    public static Method parseFrom(CodedInputStream input) throws IOException {
        return GeneratedMessageV3.parseWithIOException(PARSER, input);
    }

    public static Method parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }

    @Override
    public Builder newBuilderForType() {
        return Method.newBuilder();
    }

    public static Builder newBuilder() {
        return DEFAULT_INSTANCE.toBuilder();
    }

    public static Builder newBuilder(Method prototype) {
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

    public static Method getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public static Parser<Method> parser() {
        return PARSER;
    }

    public Parser<Method> getParserForType() {
        return PARSER;
    }

    @Override
    public Method getDefaultInstanceForType() {
        return DEFAULT_INSTANCE;
    }

    public static final class Builder
    extends GeneratedMessageV3.Builder<Builder>
    implements MethodOrBuilder {
        private int bitField0_;
        private Object name_ = "";
        private Object requestTypeUrl_ = "";
        private boolean requestStreaming_;
        private Object responseTypeUrl_ = "";
        private boolean responseStreaming_;
        private List<Option> options_ = Collections.emptyList();
        private RepeatedFieldBuilderV3<Option, Option.Builder, OptionOrBuilder> optionsBuilder_;
        private int syntax_ = 0;

        public static final Descriptors.Descriptor getDescriptor() {
            return ApiProto.internal_static_google_protobuf_Method_descriptor;
        }

        @Override
        protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return ApiProto.internal_static_google_protobuf_Method_fieldAccessorTable.ensureFieldAccessorsInitialized(Method.class, Builder.class);
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
                this.getOptionsFieldBuilder();
            }
        }

        @Override
        public Builder clear() {
            super.clear();
            this.name_ = "";
            this.requestTypeUrl_ = "";
            this.requestStreaming_ = false;
            this.responseTypeUrl_ = "";
            this.responseStreaming_ = false;
            if (this.optionsBuilder_ == null) {
                this.options_ = Collections.emptyList();
                this.bitField0_ &= 0xFFFFFFFE;
            } else {
                this.optionsBuilder_.clear();
            }
            this.syntax_ = 0;
            return this;
        }

        @Override
        public Descriptors.Descriptor getDescriptorForType() {
            return ApiProto.internal_static_google_protobuf_Method_descriptor;
        }

        @Override
        public Method getDefaultInstanceForType() {
            return Method.getDefaultInstance();
        }

        @Override
        public Method build() {
            Method result = this.buildPartial();
            if (!result.isInitialized()) {
                throw Builder.newUninitializedMessageException(result);
            }
            return result;
        }

        @Override
        public Method buildPartial() {
            Method result = new Method(this);
            int from_bitField0_ = this.bitField0_;
            result.name_ = this.name_;
            result.requestTypeUrl_ = this.requestTypeUrl_;
            result.requestStreaming_ = this.requestStreaming_;
            result.responseTypeUrl_ = this.responseTypeUrl_;
            result.responseStreaming_ = this.responseStreaming_;
            if (this.optionsBuilder_ == null) {
                if ((this.bitField0_ & 1) != 0) {
                    this.options_ = Collections.unmodifiableList(this.options_);
                    this.bitField0_ &= 0xFFFFFFFE;
                }
                result.options_ = this.options_;
            } else {
                result.options_ = this.optionsBuilder_.build();
            }
            result.syntax_ = this.syntax_;
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
            if (other instanceof Method) {
                return this.mergeFrom((Method)other);
            }
            super.mergeFrom(other);
            return this;
        }

        public Builder mergeFrom(Method other) {
            if (other == Method.getDefaultInstance()) {
                return this;
            }
            if (!other.getName().isEmpty()) {
                this.name_ = other.name_;
                this.onChanged();
            }
            if (!other.getRequestTypeUrl().isEmpty()) {
                this.requestTypeUrl_ = other.requestTypeUrl_;
                this.onChanged();
            }
            if (other.getRequestStreaming()) {
                this.setRequestStreaming(other.getRequestStreaming());
            }
            if (!other.getResponseTypeUrl().isEmpty()) {
                this.responseTypeUrl_ = other.responseTypeUrl_;
                this.onChanged();
            }
            if (other.getResponseStreaming()) {
                this.setResponseStreaming(other.getResponseStreaming());
            }
            if (this.optionsBuilder_ == null) {
                if (!other.options_.isEmpty()) {
                    if (this.options_.isEmpty()) {
                        this.options_ = other.options_;
                        this.bitField0_ &= 0xFFFFFFFE;
                    } else {
                        this.ensureOptionsIsMutable();
                        this.options_.addAll(other.options_);
                    }
                    this.onChanged();
                }
            } else if (!other.options_.isEmpty()) {
                if (this.optionsBuilder_.isEmpty()) {
                    this.optionsBuilder_.dispose();
                    this.optionsBuilder_ = null;
                    this.options_ = other.options_;
                    this.bitField0_ &= 0xFFFFFFFE;
                    this.optionsBuilder_ = GeneratedMessageV3.alwaysUseFieldBuilders ? this.getOptionsFieldBuilder() : null;
                } else {
                    this.optionsBuilder_.addAllMessages(other.options_);
                }
            }
            if (other.syntax_ != 0) {
                this.setSyntaxValue(other.getSyntaxValue());
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
            Method parsedMessage = null;
            try {
                parsedMessage = (Method)PARSER.parsePartialFrom(input, extensionRegistry);
            }
            catch (InvalidProtocolBufferException e) {
                parsedMessage = (Method)e.getUnfinishedMessage();
                throw e.unwrapIOException();
            }
            finally {
                if (parsedMessage != null) {
                    this.mergeFrom(parsedMessage);
                }
            }
            return this;
        }

        @Override
        public String getName() {
            Object ref = this.name_;
            if (!(ref instanceof String)) {
                ByteString bs = (ByteString)ref;
                String s = bs.toStringUtf8();
                this.name_ = s;
                return s;
            }
            return (String)ref;
        }

        @Override
        public ByteString getNameBytes() {
            Object ref = this.name_;
            if (ref instanceof String) {
                ByteString b = ByteString.copyFromUtf8((String)ref);
                this.name_ = b;
                return b;
            }
            return (ByteString)ref;
        }

        public Builder setName(String value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.name_ = value;
            this.onChanged();
            return this;
        }

        public Builder clearName() {
            this.name_ = Method.getDefaultInstance().getName();
            this.onChanged();
            return this;
        }

        public Builder setNameBytes(ByteString value) {
            if (value == null) {
                throw new NullPointerException();
            }
            AbstractMessageLite.checkByteStringIsUtf8(value);
            this.name_ = value;
            this.onChanged();
            return this;
        }

        @Override
        public String getRequestTypeUrl() {
            Object ref = this.requestTypeUrl_;
            if (!(ref instanceof String)) {
                ByteString bs = (ByteString)ref;
                String s = bs.toStringUtf8();
                this.requestTypeUrl_ = s;
                return s;
            }
            return (String)ref;
        }

        @Override
        public ByteString getRequestTypeUrlBytes() {
            Object ref = this.requestTypeUrl_;
            if (ref instanceof String) {
                ByteString b = ByteString.copyFromUtf8((String)ref);
                this.requestTypeUrl_ = b;
                return b;
            }
            return (ByteString)ref;
        }

        public Builder setRequestTypeUrl(String value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.requestTypeUrl_ = value;
            this.onChanged();
            return this;
        }

        public Builder clearRequestTypeUrl() {
            this.requestTypeUrl_ = Method.getDefaultInstance().getRequestTypeUrl();
            this.onChanged();
            return this;
        }

        public Builder setRequestTypeUrlBytes(ByteString value) {
            if (value == null) {
                throw new NullPointerException();
            }
            AbstractMessageLite.checkByteStringIsUtf8(value);
            this.requestTypeUrl_ = value;
            this.onChanged();
            return this;
        }

        @Override
        public boolean getRequestStreaming() {
            return this.requestStreaming_;
        }

        public Builder setRequestStreaming(boolean value) {
            this.requestStreaming_ = value;
            this.onChanged();
            return this;
        }

        public Builder clearRequestStreaming() {
            this.requestStreaming_ = false;
            this.onChanged();
            return this;
        }

        @Override
        public String getResponseTypeUrl() {
            Object ref = this.responseTypeUrl_;
            if (!(ref instanceof String)) {
                ByteString bs = (ByteString)ref;
                String s = bs.toStringUtf8();
                this.responseTypeUrl_ = s;
                return s;
            }
            return (String)ref;
        }

        @Override
        public ByteString getResponseTypeUrlBytes() {
            Object ref = this.responseTypeUrl_;
            if (ref instanceof String) {
                ByteString b = ByteString.copyFromUtf8((String)ref);
                this.responseTypeUrl_ = b;
                return b;
            }
            return (ByteString)ref;
        }

        public Builder setResponseTypeUrl(String value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.responseTypeUrl_ = value;
            this.onChanged();
            return this;
        }

        public Builder clearResponseTypeUrl() {
            this.responseTypeUrl_ = Method.getDefaultInstance().getResponseTypeUrl();
            this.onChanged();
            return this;
        }

        public Builder setResponseTypeUrlBytes(ByteString value) {
            if (value == null) {
                throw new NullPointerException();
            }
            AbstractMessageLite.checkByteStringIsUtf8(value);
            this.responseTypeUrl_ = value;
            this.onChanged();
            return this;
        }

        @Override
        public boolean getResponseStreaming() {
            return this.responseStreaming_;
        }

        public Builder setResponseStreaming(boolean value) {
            this.responseStreaming_ = value;
            this.onChanged();
            return this;
        }

        public Builder clearResponseStreaming() {
            this.responseStreaming_ = false;
            this.onChanged();
            return this;
        }

        private void ensureOptionsIsMutable() {
            if ((this.bitField0_ & 1) == 0) {
                this.options_ = new ArrayList<Option>(this.options_);
                this.bitField0_ |= 1;
            }
        }

        @Override
        public List<Option> getOptionsList() {
            if (this.optionsBuilder_ == null) {
                return Collections.unmodifiableList(this.options_);
            }
            return this.optionsBuilder_.getMessageList();
        }

        @Override
        public int getOptionsCount() {
            if (this.optionsBuilder_ == null) {
                return this.options_.size();
            }
            return this.optionsBuilder_.getCount();
        }

        @Override
        public Option getOptions(int index) {
            if (this.optionsBuilder_ == null) {
                return this.options_.get(index);
            }
            return this.optionsBuilder_.getMessage(index);
        }

        public Builder setOptions(int index, Option value) {
            if (this.optionsBuilder_ == null) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.ensureOptionsIsMutable();
                this.options_.set(index, value);
                this.onChanged();
            } else {
                this.optionsBuilder_.setMessage(index, value);
            }
            return this;
        }

        public Builder setOptions(int index, Option.Builder builderForValue) {
            if (this.optionsBuilder_ == null) {
                this.ensureOptionsIsMutable();
                this.options_.set(index, builderForValue.build());
                this.onChanged();
            } else {
                this.optionsBuilder_.setMessage(index, builderForValue.build());
            }
            return this;
        }

        public Builder addOptions(Option value) {
            if (this.optionsBuilder_ == null) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.ensureOptionsIsMutable();
                this.options_.add(value);
                this.onChanged();
            } else {
                this.optionsBuilder_.addMessage(value);
            }
            return this;
        }

        public Builder addOptions(int index, Option value) {
            if (this.optionsBuilder_ == null) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.ensureOptionsIsMutable();
                this.options_.add(index, value);
                this.onChanged();
            } else {
                this.optionsBuilder_.addMessage(index, value);
            }
            return this;
        }

        public Builder addOptions(Option.Builder builderForValue) {
            if (this.optionsBuilder_ == null) {
                this.ensureOptionsIsMutable();
                this.options_.add(builderForValue.build());
                this.onChanged();
            } else {
                this.optionsBuilder_.addMessage(builderForValue.build());
            }
            return this;
        }

        public Builder addOptions(int index, Option.Builder builderForValue) {
            if (this.optionsBuilder_ == null) {
                this.ensureOptionsIsMutable();
                this.options_.add(index, builderForValue.build());
                this.onChanged();
            } else {
                this.optionsBuilder_.addMessage(index, builderForValue.build());
            }
            return this;
        }

        public Builder addAllOptions(Iterable<? extends Option> values) {
            if (this.optionsBuilder_ == null) {
                this.ensureOptionsIsMutable();
                AbstractMessageLite.Builder.addAll(values, this.options_);
                this.onChanged();
            } else {
                this.optionsBuilder_.addAllMessages(values);
            }
            return this;
        }

        public Builder clearOptions() {
            if (this.optionsBuilder_ == null) {
                this.options_ = Collections.emptyList();
                this.bitField0_ &= 0xFFFFFFFE;
                this.onChanged();
            } else {
                this.optionsBuilder_.clear();
            }
            return this;
        }

        public Builder removeOptions(int index) {
            if (this.optionsBuilder_ == null) {
                this.ensureOptionsIsMutable();
                this.options_.remove(index);
                this.onChanged();
            } else {
                this.optionsBuilder_.remove(index);
            }
            return this;
        }

        public Option.Builder getOptionsBuilder(int index) {
            return this.getOptionsFieldBuilder().getBuilder(index);
        }

        @Override
        public OptionOrBuilder getOptionsOrBuilder(int index) {
            if (this.optionsBuilder_ == null) {
                return this.options_.get(index);
            }
            return this.optionsBuilder_.getMessageOrBuilder(index);
        }

        @Override
        public List<? extends OptionOrBuilder> getOptionsOrBuilderList() {
            if (this.optionsBuilder_ != null) {
                return this.optionsBuilder_.getMessageOrBuilderList();
            }
            return Collections.unmodifiableList(this.options_);
        }

        public Option.Builder addOptionsBuilder() {
            return this.getOptionsFieldBuilder().addBuilder(Option.getDefaultInstance());
        }

        public Option.Builder addOptionsBuilder(int index) {
            return this.getOptionsFieldBuilder().addBuilder(index, Option.getDefaultInstance());
        }

        public List<Option.Builder> getOptionsBuilderList() {
            return this.getOptionsFieldBuilder().getBuilderList();
        }

        private RepeatedFieldBuilderV3<Option, Option.Builder, OptionOrBuilder> getOptionsFieldBuilder() {
            if (this.optionsBuilder_ == null) {
                this.optionsBuilder_ = new RepeatedFieldBuilderV3(this.options_, (this.bitField0_ & 1) != 0, this.getParentForChildren(), this.isClean());
                this.options_ = null;
            }
            return this.optionsBuilder_;
        }

        @Override
        public int getSyntaxValue() {
            return this.syntax_;
        }

        public Builder setSyntaxValue(int value) {
            this.syntax_ = value;
            this.onChanged();
            return this;
        }

        @Override
        public Syntax getSyntax() {
            Syntax result = Syntax.valueOf(this.syntax_);
            return result == null ? Syntax.UNRECOGNIZED : result;
        }

        public Builder setSyntax(Syntax value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.syntax_ = value.getNumber();
            this.onChanged();
            return this;
        }

        public Builder clearSyntax() {
            this.syntax_ = 0;
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

