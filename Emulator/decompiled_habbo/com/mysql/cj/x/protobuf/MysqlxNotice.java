/*
 * Decompiled with CFR 0.152.
 */
package com.mysql.cj.x.protobuf;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.AbstractParser;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Internal;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.Parser;
import com.google.protobuf.ProtocolMessageEnum;
import com.google.protobuf.RepeatedFieldBuilderV3;
import com.google.protobuf.SingleFieldBuilderV3;
import com.google.protobuf.UnknownFieldSet;
import com.mysql.cj.x.protobuf.Mysqlx;
import com.mysql.cj.x.protobuf.MysqlxDatatypes;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MysqlxNotice {
    private static final Descriptors.Descriptor internal_static_Mysqlx_Notice_Frame_descriptor;
    private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Notice_Frame_fieldAccessorTable;
    private static final Descriptors.Descriptor internal_static_Mysqlx_Notice_Warning_descriptor;
    private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Notice_Warning_fieldAccessorTable;
    private static final Descriptors.Descriptor internal_static_Mysqlx_Notice_SessionVariableChanged_descriptor;
    private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Notice_SessionVariableChanged_fieldAccessorTable;
    private static final Descriptors.Descriptor internal_static_Mysqlx_Notice_SessionStateChanged_descriptor;
    private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Notice_SessionStateChanged_fieldAccessorTable;
    private static final Descriptors.Descriptor internal_static_Mysqlx_Notice_GroupReplicationStateChanged_descriptor;
    private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Notice_GroupReplicationStateChanged_fieldAccessorTable;
    private static final Descriptors.Descriptor internal_static_Mysqlx_Notice_ServerHello_descriptor;
    private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Notice_ServerHello_fieldAccessorTable;
    private static Descriptors.FileDescriptor descriptor;

    private MysqlxNotice() {
    }

    public static void registerAllExtensions(ExtensionRegistryLite registry) {
    }

    public static void registerAllExtensions(ExtensionRegistry registry) {
        MysqlxNotice.registerAllExtensions((ExtensionRegistryLite)registry);
    }

    public static Descriptors.FileDescriptor getDescriptor() {
        return descriptor;
    }

    static {
        String[] descriptorData = new String[]{"\n\u0013mysqlx_notice.proto\u0012\rMysqlx.Notice\u001a\fmysqlx.proto\u001a\u0016mysqlx_datatypes.proto\"\u0085\u0002\n\u0005Frame\u0012\f\n\u0004type\u0018\u0001 \u0002(\r\u00121\n\u0005scope\u0018\u0002 \u0001(\u000e2\u001a.Mysqlx.Notice.Frame.Scope:\u0006GLOBAL\u0012\u000f\n\u0007payload\u0018\u0003 \u0001(\f\"\u001e\n\u0005Scope\u0012\n\n\u0006GLOBAL\u0010\u0001\u0012\t\n\u0005LOCAL\u0010\u0002\"\u0083\u0001\n\u0004Type\u0012\u000b\n\u0007WARNING\u0010\u0001\u0012\u001c\n\u0018SESSION_VARIABLE_CHANGED\u0010\u0002\u0012\u0019\n\u0015SESSION_STATE_CHANGED\u0010\u0003\u0012#\n\u001fGROUP_REPLICATION_STATE_CHANGED\u0010\u0004\u0012\u0010\n\fSERVER_HELLO\u0010\u0005:\u0004\u0090\u00ea0\u000b\"\u0085\u0001\n\u0007Warning\u00124\n\u0005level\u0018\u0001 \u0001(\u000e2\u001c.Mysqlx.Notice.Warning.Level:\u0007WARNING\u0012\f\n\u0004code\u0018\u0002 \u0002(\r\u0012\u000b\n\u0003msg\u0018\u0003 \u0002(\t\")\n\u0005Level\u0012\b\n\u0004NOTE\u0010\u0001\u0012\u000b\n\u0007WARNING\u0010\u0002\u0012\t\n\u0005ERROR\u0010\u0003\"P\n\u0016SessionVariableChanged\u0012\r\n\u0005param\u0018\u0001 \u0002(\t\u0012'\n\u0005value\u0018\u0002 \u0001(\u000b2\u0018.Mysqlx.Datatypes.Scalar\"\u00f1\u0002\n\u0013SessionStateChanged\u0012;\n\u0005param\u0018\u0001 \u0002(\u000e2,.Mysqlx.Notice.SessionStateChanged.Parameter\u0012'\n\u0005value\u0018\u0002 \u0003(\u000b2\u0018.Mysqlx.Datatypes.Scalar\"\u00f3\u0001\n\tParameter\u0012\u0012\n\u000eCURRENT_SCHEMA\u0010\u0001\u0012\u0013\n\u000fACCOUNT_EXPIRED\u0010\u0002\u0012\u0017\n\u0013GENERATED_INSERT_ID\u0010\u0003\u0012\u0011\n\rROWS_AFFECTED\u0010\u0004\u0012\u000e\n\nROWS_FOUND\u0010\u0005\u0012\u0010\n\fROWS_MATCHED\u0010\u0006\u0012\u0011\n\rTRX_COMMITTED\u0010\u0007\u0012\u0012\n\u000eTRX_ROLLEDBACK\u0010\t\u0012\u0014\n\u0010PRODUCED_MESSAGE\u0010\n\u0012\u0016\n\u0012CLIENT_ID_ASSIGNED\u0010\u000b\u0012\u001a\n\u0016GENERATED_DOCUMENT_IDS\u0010\f\"\u00ae\u0001\n\u001cGroupReplicationStateChanged\u0012\f\n\u0004type\u0018\u0001 \u0002(\r\u0012\u000f\n\u0007view_id\u0018\u0002 \u0001(\t\"o\n\u0004Type\u0012\u001a\n\u0016MEMBERSHIP_QUORUM_LOSS\u0010\u0001\u0012\u001a\n\u0016MEMBERSHIP_VIEW_CHANGE\u0010\u0002\u0012\u0016\n\u0012MEMBER_ROLE_CHANGE\u0010\u0003\u0012\u0017\n\u0013MEMBER_STATE_CHANGE\u0010\u0004\"\r\n\u000bServerHelloB\u0019\n\u0017com.mysql.cj.x.protobuf"};
        descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[]{Mysqlx.getDescriptor(), MysqlxDatatypes.getDescriptor()});
        internal_static_Mysqlx_Notice_Frame_descriptor = MysqlxNotice.getDescriptor().getMessageTypes().get(0);
        internal_static_Mysqlx_Notice_Frame_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Notice_Frame_descriptor, new String[]{"Type", "Scope", "Payload"});
        internal_static_Mysqlx_Notice_Warning_descriptor = MysqlxNotice.getDescriptor().getMessageTypes().get(1);
        internal_static_Mysqlx_Notice_Warning_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Notice_Warning_descriptor, new String[]{"Level", "Code", "Msg"});
        internal_static_Mysqlx_Notice_SessionVariableChanged_descriptor = MysqlxNotice.getDescriptor().getMessageTypes().get(2);
        internal_static_Mysqlx_Notice_SessionVariableChanged_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Notice_SessionVariableChanged_descriptor, new String[]{"Param", "Value"});
        internal_static_Mysqlx_Notice_SessionStateChanged_descriptor = MysqlxNotice.getDescriptor().getMessageTypes().get(3);
        internal_static_Mysqlx_Notice_SessionStateChanged_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Notice_SessionStateChanged_descriptor, new String[]{"Param", "Value"});
        internal_static_Mysqlx_Notice_GroupReplicationStateChanged_descriptor = MysqlxNotice.getDescriptor().getMessageTypes().get(4);
        internal_static_Mysqlx_Notice_GroupReplicationStateChanged_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Notice_GroupReplicationStateChanged_descriptor, new String[]{"Type", "ViewId"});
        internal_static_Mysqlx_Notice_ServerHello_descriptor = MysqlxNotice.getDescriptor().getMessageTypes().get(5);
        internal_static_Mysqlx_Notice_ServerHello_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_Mysqlx_Notice_ServerHello_descriptor, new String[0]);
        ExtensionRegistry registry = ExtensionRegistry.newInstance();
        registry.add(Mysqlx.serverMessageId);
        Descriptors.FileDescriptor.internalUpdateFileDescriptor(descriptor, registry);
        Mysqlx.getDescriptor();
        MysqlxDatatypes.getDescriptor();
    }

    public static final class ServerHello
    extends GeneratedMessageV3
    implements ServerHelloOrBuilder {
        private static final long serialVersionUID = 0L;
        private byte memoizedIsInitialized = (byte)-1;
        private static final ServerHello DEFAULT_INSTANCE = new ServerHello();
        @Deprecated
        public static final Parser<ServerHello> PARSER = new AbstractParser<ServerHello>(){

            @Override
            public ServerHello parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return new ServerHello(input, extensionRegistry);
            }
        };

        private ServerHello(GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }

        private ServerHello() {
        }

        @Override
        protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
            return new ServerHello();
        }

        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        private ServerHello(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this();
            if (extensionRegistry == null) {
                throw new NullPointerException();
            }
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();
            try {
                boolean done = false;
                block9: while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0: {
                            done = true;
                            continue block9;
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
            return internal_static_Mysqlx_Notice_ServerHello_descriptor;
        }

        @Override
        protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return internal_static_Mysqlx_Notice_ServerHello_fieldAccessorTable.ensureFieldAccessorsInitialized(ServerHello.class, Builder.class);
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
            this.unknownFields.writeTo(output);
        }

        @Override
        public int getSerializedSize() {
            int size = this.memoizedSize;
            if (size != -1) {
                return size;
            }
            size = 0;
            this.memoizedSize = size += this.unknownFields.getSerializedSize();
            return size;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof ServerHello)) {
                return super.equals(obj);
            }
            ServerHello other = (ServerHello)obj;
            return this.unknownFields.equals(other.unknownFields);
        }

        @Override
        public int hashCode() {
            if (this.memoizedHashCode != 0) {
                return this.memoizedHashCode;
            }
            int hash = 41;
            hash = 19 * hash + ServerHello.getDescriptor().hashCode();
            this.memoizedHashCode = hash = 29 * hash + this.unknownFields.hashCode();
            return hash;
        }

        public static ServerHello parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static ServerHello parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static ServerHello parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static ServerHello parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static ServerHello parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static ServerHello parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static ServerHello parseFrom(InputStream input) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static ServerHello parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static ServerHello parseDelimitedFrom(InputStream input) throws IOException {
            return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
        }

        public static ServerHello parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }

        public static ServerHello parseFrom(CodedInputStream input) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static ServerHello parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        @Override
        public Builder newBuilderForType() {
            return ServerHello.newBuilder();
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(ServerHello prototype) {
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

        public static ServerHello getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<ServerHello> parser() {
            return PARSER;
        }

        public Parser<ServerHello> getParserForType() {
            return PARSER;
        }

        @Override
        public ServerHello getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

        public static final class Builder
        extends GeneratedMessageV3.Builder<Builder>
        implements ServerHelloOrBuilder {
            public static final Descriptors.Descriptor getDescriptor() {
                return internal_static_Mysqlx_Notice_ServerHello_descriptor;
            }

            @Override
            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                return internal_static_Mysqlx_Notice_ServerHello_fieldAccessorTable.ensureFieldAccessorsInitialized(ServerHello.class, Builder.class);
            }

            private Builder() {
                this.maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                if (alwaysUseFieldBuilders) {
                    // empty if block
                }
            }

            @Override
            public Builder clear() {
                super.clear();
                return this;
            }

            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return internal_static_Mysqlx_Notice_ServerHello_descriptor;
            }

            @Override
            public ServerHello getDefaultInstanceForType() {
                return ServerHello.getDefaultInstance();
            }

            @Override
            public ServerHello build() {
                ServerHello result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw Builder.newUninitializedMessageException(result);
                }
                return result;
            }

            @Override
            public ServerHello buildPartial() {
                ServerHello result = new ServerHello(this);
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
                if (other instanceof ServerHello) {
                    return this.mergeFrom((ServerHello)other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(ServerHello other) {
                if (other == ServerHello.getDefaultInstance()) {
                    return this;
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
                ServerHello parsedMessage = null;
                try {
                    parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (ServerHello)e.getUnfinishedMessage();
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
            public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
                return (Builder)super.setUnknownFields(unknownFields);
            }

            @Override
            public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
                return (Builder)super.mergeUnknownFields(unknownFields);
            }
        }
    }

    public static interface ServerHelloOrBuilder
    extends MessageOrBuilder {
    }

    public static final class GroupReplicationStateChanged
    extends GeneratedMessageV3
    implements GroupReplicationStateChangedOrBuilder {
        private static final long serialVersionUID = 0L;
        private int bitField0_;
        public static final int TYPE_FIELD_NUMBER = 1;
        private int type_;
        public static final int VIEW_ID_FIELD_NUMBER = 2;
        private volatile Object viewId_;
        private byte memoizedIsInitialized = (byte)-1;
        private static final GroupReplicationStateChanged DEFAULT_INSTANCE = new GroupReplicationStateChanged();
        @Deprecated
        public static final Parser<GroupReplicationStateChanged> PARSER = new AbstractParser<GroupReplicationStateChanged>(){

            @Override
            public GroupReplicationStateChanged parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return new GroupReplicationStateChanged(input, extensionRegistry);
            }
        };

        private GroupReplicationStateChanged(GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }

        private GroupReplicationStateChanged() {
            this.viewId_ = "";
        }

        @Override
        protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
            return new GroupReplicationStateChanged();
        }

        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        private GroupReplicationStateChanged(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this();
            if (extensionRegistry == null) {
                throw new NullPointerException();
            }
            boolean mutable_bitField0_ = false;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();
            try {
                boolean done = false;
                block11: while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0: {
                            done = true;
                            continue block11;
                        }
                        case 8: {
                            this.bitField0_ |= 1;
                            this.type_ = input.readUInt32();
                            continue block11;
                        }
                        case 18: {
                            ByteString bs = input.readBytes();
                            this.bitField0_ |= 2;
                            this.viewId_ = bs;
                            continue block11;
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
            return internal_static_Mysqlx_Notice_GroupReplicationStateChanged_descriptor;
        }

        @Override
        protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return internal_static_Mysqlx_Notice_GroupReplicationStateChanged_fieldAccessorTable.ensureFieldAccessorsInitialized(GroupReplicationStateChanged.class, Builder.class);
        }

        @Override
        public boolean hasType() {
            return (this.bitField0_ & 1) != 0;
        }

        @Override
        public int getType() {
            return this.type_;
        }

        @Override
        public boolean hasViewId() {
            return (this.bitField0_ & 2) != 0;
        }

        @Override
        public String getViewId() {
            Object ref = this.viewId_;
            if (ref instanceof String) {
                return (String)ref;
            }
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.viewId_ = s;
            }
            return s;
        }

        @Override
        public ByteString getViewIdBytes() {
            Object ref = this.viewId_;
            if (ref instanceof String) {
                ByteString b = ByteString.copyFromUtf8((String)ref);
                this.viewId_ = b;
                return b;
            }
            return (ByteString)ref;
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
            if (!this.hasType()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            this.memoizedIsInitialized = 1;
            return true;
        }

        @Override
        public void writeTo(CodedOutputStream output) throws IOException {
            if ((this.bitField0_ & 1) != 0) {
                output.writeUInt32(1, this.type_);
            }
            if ((this.bitField0_ & 2) != 0) {
                GeneratedMessageV3.writeString(output, 2, this.viewId_);
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
            if ((this.bitField0_ & 1) != 0) {
                size += CodedOutputStream.computeUInt32Size(1, this.type_);
            }
            if ((this.bitField0_ & 2) != 0) {
                size += GeneratedMessageV3.computeStringSize(2, this.viewId_);
            }
            this.memoizedSize = size += this.unknownFields.getSerializedSize();
            return size;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof GroupReplicationStateChanged)) {
                return super.equals(obj);
            }
            GroupReplicationStateChanged other = (GroupReplicationStateChanged)obj;
            if (this.hasType() != other.hasType()) {
                return false;
            }
            if (this.hasType() && this.getType() != other.getType()) {
                return false;
            }
            if (this.hasViewId() != other.hasViewId()) {
                return false;
            }
            if (this.hasViewId() && !this.getViewId().equals(other.getViewId())) {
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
            hash = 19 * hash + GroupReplicationStateChanged.getDescriptor().hashCode();
            if (this.hasType()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getType();
            }
            if (this.hasViewId()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + this.getViewId().hashCode();
            }
            this.memoizedHashCode = hash = 29 * hash + this.unknownFields.hashCode();
            return hash;
        }

        public static GroupReplicationStateChanged parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static GroupReplicationStateChanged parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static GroupReplicationStateChanged parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static GroupReplicationStateChanged parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static GroupReplicationStateChanged parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static GroupReplicationStateChanged parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static GroupReplicationStateChanged parseFrom(InputStream input) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static GroupReplicationStateChanged parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static GroupReplicationStateChanged parseDelimitedFrom(InputStream input) throws IOException {
            return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
        }

        public static GroupReplicationStateChanged parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }

        public static GroupReplicationStateChanged parseFrom(CodedInputStream input) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static GroupReplicationStateChanged parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        @Override
        public Builder newBuilderForType() {
            return GroupReplicationStateChanged.newBuilder();
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(GroupReplicationStateChanged prototype) {
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

        public static GroupReplicationStateChanged getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<GroupReplicationStateChanged> parser() {
            return PARSER;
        }

        public Parser<GroupReplicationStateChanged> getParserForType() {
            return PARSER;
        }

        @Override
        public GroupReplicationStateChanged getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

        public static final class Builder
        extends GeneratedMessageV3.Builder<Builder>
        implements GroupReplicationStateChangedOrBuilder {
            private int bitField0_;
            private int type_;
            private Object viewId_ = "";

            public static final Descriptors.Descriptor getDescriptor() {
                return internal_static_Mysqlx_Notice_GroupReplicationStateChanged_descriptor;
            }

            @Override
            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                return internal_static_Mysqlx_Notice_GroupReplicationStateChanged_fieldAccessorTable.ensureFieldAccessorsInitialized(GroupReplicationStateChanged.class, Builder.class);
            }

            private Builder() {
                this.maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                if (alwaysUseFieldBuilders) {
                    // empty if block
                }
            }

            @Override
            public Builder clear() {
                super.clear();
                this.type_ = 0;
                this.bitField0_ &= 0xFFFFFFFE;
                this.viewId_ = "";
                this.bitField0_ &= 0xFFFFFFFD;
                return this;
            }

            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return internal_static_Mysqlx_Notice_GroupReplicationStateChanged_descriptor;
            }

            @Override
            public GroupReplicationStateChanged getDefaultInstanceForType() {
                return GroupReplicationStateChanged.getDefaultInstance();
            }

            @Override
            public GroupReplicationStateChanged build() {
                GroupReplicationStateChanged result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw Builder.newUninitializedMessageException(result);
                }
                return result;
            }

            @Override
            public GroupReplicationStateChanged buildPartial() {
                GroupReplicationStateChanged result = new GroupReplicationStateChanged(this);
                int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 1) != 0) {
                    result.type_ = this.type_;
                    to_bitField0_ |= 1;
                }
                if ((from_bitField0_ & 2) != 0) {
                    to_bitField0_ |= 2;
                }
                result.viewId_ = this.viewId_;
                result.bitField0_ = to_bitField0_;
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
                if (other instanceof GroupReplicationStateChanged) {
                    return this.mergeFrom((GroupReplicationStateChanged)other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(GroupReplicationStateChanged other) {
                if (other == GroupReplicationStateChanged.getDefaultInstance()) {
                    return this;
                }
                if (other.hasType()) {
                    this.setType(other.getType());
                }
                if (other.hasViewId()) {
                    this.bitField0_ |= 2;
                    this.viewId_ = other.viewId_;
                    this.onChanged();
                }
                this.mergeUnknownFields(other.unknownFields);
                this.onChanged();
                return this;
            }

            @Override
            public final boolean isInitialized() {
                return this.hasType();
            }

            @Override
            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                GroupReplicationStateChanged parsedMessage = null;
                try {
                    parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (GroupReplicationStateChanged)e.getUnfinishedMessage();
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
            public boolean hasType() {
                return (this.bitField0_ & 1) != 0;
            }

            @Override
            public int getType() {
                return this.type_;
            }

            public Builder setType(int value) {
                this.bitField0_ |= 1;
                this.type_ = value;
                this.onChanged();
                return this;
            }

            public Builder clearType() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.type_ = 0;
                this.onChanged();
                return this;
            }

            @Override
            public boolean hasViewId() {
                return (this.bitField0_ & 2) != 0;
            }

            @Override
            public String getViewId() {
                Object ref = this.viewId_;
                if (!(ref instanceof String)) {
                    ByteString bs = (ByteString)ref;
                    String s = bs.toStringUtf8();
                    if (bs.isValidUtf8()) {
                        this.viewId_ = s;
                    }
                    return s;
                }
                return (String)ref;
            }

            @Override
            public ByteString getViewIdBytes() {
                Object ref = this.viewId_;
                if (ref instanceof String) {
                    ByteString b = ByteString.copyFromUtf8((String)ref);
                    this.viewId_ = b;
                    return b;
                }
                return (ByteString)ref;
            }

            public Builder setViewId(String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 2;
                this.viewId_ = value;
                this.onChanged();
                return this;
            }

            public Builder clearViewId() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.viewId_ = GroupReplicationStateChanged.getDefaultInstance().getViewId();
                this.onChanged();
                return this;
            }

            public Builder setViewIdBytes(ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 2;
                this.viewId_ = value;
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

        public static enum Type implements ProtocolMessageEnum
        {
            MEMBERSHIP_QUORUM_LOSS(1),
            MEMBERSHIP_VIEW_CHANGE(2),
            MEMBER_ROLE_CHANGE(3),
            MEMBER_STATE_CHANGE(4);

            public static final int MEMBERSHIP_QUORUM_LOSS_VALUE = 1;
            public static final int MEMBERSHIP_VIEW_CHANGE_VALUE = 2;
            public static final int MEMBER_ROLE_CHANGE_VALUE = 3;
            public static final int MEMBER_STATE_CHANGE_VALUE = 4;
            private static final Internal.EnumLiteMap<Type> internalValueMap;
            private static final Type[] VALUES;
            private final int value;

            @Override
            public final int getNumber() {
                return this.value;
            }

            @Deprecated
            public static Type valueOf(int value) {
                return Type.forNumber(value);
            }

            public static Type forNumber(int value) {
                switch (value) {
                    case 1: {
                        return MEMBERSHIP_QUORUM_LOSS;
                    }
                    case 2: {
                        return MEMBERSHIP_VIEW_CHANGE;
                    }
                    case 3: {
                        return MEMBER_ROLE_CHANGE;
                    }
                    case 4: {
                        return MEMBER_STATE_CHANGE;
                    }
                }
                return null;
            }

            public static Internal.EnumLiteMap<Type> internalGetValueMap() {
                return internalValueMap;
            }

            @Override
            public final Descriptors.EnumValueDescriptor getValueDescriptor() {
                return Type.getDescriptor().getValues().get(this.ordinal());
            }

            @Override
            public final Descriptors.EnumDescriptor getDescriptorForType() {
                return Type.getDescriptor();
            }

            public static final Descriptors.EnumDescriptor getDescriptor() {
                return GroupReplicationStateChanged.getDescriptor().getEnumTypes().get(0);
            }

            public static Type valueOf(Descriptors.EnumValueDescriptor desc) {
                if (desc.getType() != Type.getDescriptor()) {
                    throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
                }
                return VALUES[desc.getIndex()];
            }

            private Type(int value) {
                this.value = value;
            }

            static {
                internalValueMap = new Internal.EnumLiteMap<Type>(){

                    @Override
                    public Type findValueByNumber(int number) {
                        return Type.forNumber(number);
                    }
                };
                VALUES = Type.values();
            }
        }
    }

    public static interface GroupReplicationStateChangedOrBuilder
    extends MessageOrBuilder {
        public boolean hasType();

        public int getType();

        public boolean hasViewId();

        public String getViewId();

        public ByteString getViewIdBytes();
    }

    public static final class SessionStateChanged
    extends GeneratedMessageV3
    implements SessionStateChangedOrBuilder {
        private static final long serialVersionUID = 0L;
        private int bitField0_;
        public static final int PARAM_FIELD_NUMBER = 1;
        private int param_;
        public static final int VALUE_FIELD_NUMBER = 2;
        private List<MysqlxDatatypes.Scalar> value_;
        private byte memoizedIsInitialized = (byte)-1;
        private static final SessionStateChanged DEFAULT_INSTANCE = new SessionStateChanged();
        @Deprecated
        public static final Parser<SessionStateChanged> PARSER = new AbstractParser<SessionStateChanged>(){

            @Override
            public SessionStateChanged parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return new SessionStateChanged(input, extensionRegistry);
            }
        };

        private SessionStateChanged(GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }

        private SessionStateChanged() {
            this.param_ = 1;
            this.value_ = Collections.emptyList();
        }

        @Override
        protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
            return new SessionStateChanged();
        }

        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        private SessionStateChanged(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this();
            if (extensionRegistry == null) {
                throw new NullPointerException();
            }
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();
            try {
                boolean done = false;
                block11: while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0: {
                            done = true;
                            continue block11;
                        }
                        case 8: {
                            int rawValue = input.readEnum();
                            Parameter value = Parameter.valueOf(rawValue);
                            if (value == null) {
                                unknownFields.mergeVarintField(1, rawValue);
                                continue block11;
                            }
                            this.bitField0_ |= 1;
                            this.param_ = rawValue;
                            continue block11;
                        }
                        case 18: {
                            if ((mutable_bitField0_ & 2) == 0) {
                                this.value_ = new ArrayList<MysqlxDatatypes.Scalar>();
                                mutable_bitField0_ |= 2;
                            }
                            this.value_.add(input.readMessage(MysqlxDatatypes.Scalar.PARSER, extensionRegistry));
                            continue block11;
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
                if ((mutable_bitField0_ & 2) != 0) {
                    this.value_ = Collections.unmodifiableList(this.value_);
                }
                this.unknownFields = unknownFields.build();
                this.makeExtensionsImmutable();
            }
        }

        public static final Descriptors.Descriptor getDescriptor() {
            return internal_static_Mysqlx_Notice_SessionStateChanged_descriptor;
        }

        @Override
        protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return internal_static_Mysqlx_Notice_SessionStateChanged_fieldAccessorTable.ensureFieldAccessorsInitialized(SessionStateChanged.class, Builder.class);
        }

        @Override
        public boolean hasParam() {
            return (this.bitField0_ & 1) != 0;
        }

        @Override
        public Parameter getParam() {
            Parameter result = Parameter.valueOf(this.param_);
            return result == null ? Parameter.CURRENT_SCHEMA : result;
        }

        @Override
        public List<MysqlxDatatypes.Scalar> getValueList() {
            return this.value_;
        }

        @Override
        public List<? extends MysqlxDatatypes.ScalarOrBuilder> getValueOrBuilderList() {
            return this.value_;
        }

        @Override
        public int getValueCount() {
            return this.value_.size();
        }

        @Override
        public MysqlxDatatypes.Scalar getValue(int index) {
            return this.value_.get(index);
        }

        @Override
        public MysqlxDatatypes.ScalarOrBuilder getValueOrBuilder(int index) {
            return this.value_.get(index);
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
            if (!this.hasParam()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            for (int i = 0; i < this.getValueCount(); ++i) {
                if (this.getValue(i).isInitialized()) continue;
                this.memoizedIsInitialized = 0;
                return false;
            }
            this.memoizedIsInitialized = 1;
            return true;
        }

        @Override
        public void writeTo(CodedOutputStream output) throws IOException {
            if ((this.bitField0_ & 1) != 0) {
                output.writeEnum(1, this.param_);
            }
            for (int i = 0; i < this.value_.size(); ++i) {
                output.writeMessage(2, this.value_.get(i));
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
            if ((this.bitField0_ & 1) != 0) {
                size += CodedOutputStream.computeEnumSize(1, this.param_);
            }
            for (int i = 0; i < this.value_.size(); ++i) {
                size += CodedOutputStream.computeMessageSize(2, this.value_.get(i));
            }
            this.memoizedSize = size += this.unknownFields.getSerializedSize();
            return size;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof SessionStateChanged)) {
                return super.equals(obj);
            }
            SessionStateChanged other = (SessionStateChanged)obj;
            if (this.hasParam() != other.hasParam()) {
                return false;
            }
            if (this.hasParam() && this.param_ != other.param_) {
                return false;
            }
            if (!this.getValueList().equals(other.getValueList())) {
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
            hash = 19 * hash + SessionStateChanged.getDescriptor().hashCode();
            if (this.hasParam()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.param_;
            }
            if (this.getValueCount() > 0) {
                hash = 37 * hash + 2;
                hash = 53 * hash + this.getValueList().hashCode();
            }
            this.memoizedHashCode = hash = 29 * hash + this.unknownFields.hashCode();
            return hash;
        }

        public static SessionStateChanged parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static SessionStateChanged parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static SessionStateChanged parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static SessionStateChanged parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static SessionStateChanged parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static SessionStateChanged parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static SessionStateChanged parseFrom(InputStream input) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static SessionStateChanged parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static SessionStateChanged parseDelimitedFrom(InputStream input) throws IOException {
            return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
        }

        public static SessionStateChanged parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }

        public static SessionStateChanged parseFrom(CodedInputStream input) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static SessionStateChanged parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        @Override
        public Builder newBuilderForType() {
            return SessionStateChanged.newBuilder();
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(SessionStateChanged prototype) {
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

        public static SessionStateChanged getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<SessionStateChanged> parser() {
            return PARSER;
        }

        public Parser<SessionStateChanged> getParserForType() {
            return PARSER;
        }

        @Override
        public SessionStateChanged getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

        public static final class Builder
        extends GeneratedMessageV3.Builder<Builder>
        implements SessionStateChangedOrBuilder {
            private int bitField0_;
            private int param_ = 1;
            private List<MysqlxDatatypes.Scalar> value_ = Collections.emptyList();
            private RepeatedFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> valueBuilder_;

            public static final Descriptors.Descriptor getDescriptor() {
                return internal_static_Mysqlx_Notice_SessionStateChanged_descriptor;
            }

            @Override
            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                return internal_static_Mysqlx_Notice_SessionStateChanged_fieldAccessorTable.ensureFieldAccessorsInitialized(SessionStateChanged.class, Builder.class);
            }

            private Builder() {
                this.maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                if (alwaysUseFieldBuilders) {
                    this.getValueFieldBuilder();
                }
            }

            @Override
            public Builder clear() {
                super.clear();
                this.param_ = 1;
                this.bitField0_ &= 0xFFFFFFFE;
                if (this.valueBuilder_ == null) {
                    this.value_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFD;
                } else {
                    this.valueBuilder_.clear();
                }
                return this;
            }

            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return internal_static_Mysqlx_Notice_SessionStateChanged_descriptor;
            }

            @Override
            public SessionStateChanged getDefaultInstanceForType() {
                return SessionStateChanged.getDefaultInstance();
            }

            @Override
            public SessionStateChanged build() {
                SessionStateChanged result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw Builder.newUninitializedMessageException(result);
                }
                return result;
            }

            @Override
            public SessionStateChanged buildPartial() {
                SessionStateChanged result = new SessionStateChanged(this);
                int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 1) != 0) {
                    to_bitField0_ |= 1;
                }
                result.param_ = this.param_;
                if (this.valueBuilder_ == null) {
                    if ((this.bitField0_ & 2) != 0) {
                        this.value_ = Collections.unmodifiableList(this.value_);
                        this.bitField0_ &= 0xFFFFFFFD;
                    }
                    result.value_ = this.value_;
                } else {
                    result.value_ = this.valueBuilder_.build();
                }
                result.bitField0_ = to_bitField0_;
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
                if (other instanceof SessionStateChanged) {
                    return this.mergeFrom((SessionStateChanged)other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(SessionStateChanged other) {
                if (other == SessionStateChanged.getDefaultInstance()) {
                    return this;
                }
                if (other.hasParam()) {
                    this.setParam(other.getParam());
                }
                if (this.valueBuilder_ == null) {
                    if (!other.value_.isEmpty()) {
                        if (this.value_.isEmpty()) {
                            this.value_ = other.value_;
                            this.bitField0_ &= 0xFFFFFFFD;
                        } else {
                            this.ensureValueIsMutable();
                            this.value_.addAll(other.value_);
                        }
                        this.onChanged();
                    }
                } else if (!other.value_.isEmpty()) {
                    if (this.valueBuilder_.isEmpty()) {
                        this.valueBuilder_.dispose();
                        this.valueBuilder_ = null;
                        this.value_ = other.value_;
                        this.bitField0_ &= 0xFFFFFFFD;
                        this.valueBuilder_ = alwaysUseFieldBuilders ? this.getValueFieldBuilder() : null;
                    } else {
                        this.valueBuilder_.addAllMessages(other.value_);
                    }
                }
                this.mergeUnknownFields(other.unknownFields);
                this.onChanged();
                return this;
            }

            @Override
            public final boolean isInitialized() {
                if (!this.hasParam()) {
                    return false;
                }
                for (int i = 0; i < this.getValueCount(); ++i) {
                    if (this.getValue(i).isInitialized()) continue;
                    return false;
                }
                return true;
            }

            @Override
            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                SessionStateChanged parsedMessage = null;
                try {
                    parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (SessionStateChanged)e.getUnfinishedMessage();
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
            public boolean hasParam() {
                return (this.bitField0_ & 1) != 0;
            }

            @Override
            public Parameter getParam() {
                Parameter result = Parameter.valueOf(this.param_);
                return result == null ? Parameter.CURRENT_SCHEMA : result;
            }

            public Builder setParam(Parameter value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 1;
                this.param_ = value.getNumber();
                this.onChanged();
                return this;
            }

            public Builder clearParam() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.param_ = 1;
                this.onChanged();
                return this;
            }

            private void ensureValueIsMutable() {
                if ((this.bitField0_ & 2) == 0) {
                    this.value_ = new ArrayList<MysqlxDatatypes.Scalar>(this.value_);
                    this.bitField0_ |= 2;
                }
            }

            @Override
            public List<MysqlxDatatypes.Scalar> getValueList() {
                if (this.valueBuilder_ == null) {
                    return Collections.unmodifiableList(this.value_);
                }
                return this.valueBuilder_.getMessageList();
            }

            @Override
            public int getValueCount() {
                if (this.valueBuilder_ == null) {
                    return this.value_.size();
                }
                return this.valueBuilder_.getCount();
            }

            @Override
            public MysqlxDatatypes.Scalar getValue(int index) {
                if (this.valueBuilder_ == null) {
                    return this.value_.get(index);
                }
                return this.valueBuilder_.getMessage(index);
            }

            public Builder setValue(int index, MysqlxDatatypes.Scalar value) {
                if (this.valueBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureValueIsMutable();
                    this.value_.set(index, value);
                    this.onChanged();
                } else {
                    this.valueBuilder_.setMessage(index, value);
                }
                return this;
            }

            public Builder setValue(int index, MysqlxDatatypes.Scalar.Builder builderForValue) {
                if (this.valueBuilder_ == null) {
                    this.ensureValueIsMutable();
                    this.value_.set(index, builderForValue.build());
                    this.onChanged();
                } else {
                    this.valueBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addValue(MysqlxDatatypes.Scalar value) {
                if (this.valueBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureValueIsMutable();
                    this.value_.add(value);
                    this.onChanged();
                } else {
                    this.valueBuilder_.addMessage(value);
                }
                return this;
            }

            public Builder addValue(int index, MysqlxDatatypes.Scalar value) {
                if (this.valueBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureValueIsMutable();
                    this.value_.add(index, value);
                    this.onChanged();
                } else {
                    this.valueBuilder_.addMessage(index, value);
                }
                return this;
            }

            public Builder addValue(MysqlxDatatypes.Scalar.Builder builderForValue) {
                if (this.valueBuilder_ == null) {
                    this.ensureValueIsMutable();
                    this.value_.add(builderForValue.build());
                    this.onChanged();
                } else {
                    this.valueBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }

            public Builder addValue(int index, MysqlxDatatypes.Scalar.Builder builderForValue) {
                if (this.valueBuilder_ == null) {
                    this.ensureValueIsMutable();
                    this.value_.add(index, builderForValue.build());
                    this.onChanged();
                } else {
                    this.valueBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addAllValue(Iterable<? extends MysqlxDatatypes.Scalar> values) {
                if (this.valueBuilder_ == null) {
                    this.ensureValueIsMutable();
                    AbstractMessageLite.Builder.addAll(values, this.value_);
                    this.onChanged();
                } else {
                    this.valueBuilder_.addAllMessages(values);
                }
                return this;
            }

            public Builder clearValue() {
                if (this.valueBuilder_ == null) {
                    this.value_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFD;
                    this.onChanged();
                } else {
                    this.valueBuilder_.clear();
                }
                return this;
            }

            public Builder removeValue(int index) {
                if (this.valueBuilder_ == null) {
                    this.ensureValueIsMutable();
                    this.value_.remove(index);
                    this.onChanged();
                } else {
                    this.valueBuilder_.remove(index);
                }
                return this;
            }

            public MysqlxDatatypes.Scalar.Builder getValueBuilder(int index) {
                return this.getValueFieldBuilder().getBuilder(index);
            }

            @Override
            public MysqlxDatatypes.ScalarOrBuilder getValueOrBuilder(int index) {
                if (this.valueBuilder_ == null) {
                    return this.value_.get(index);
                }
                return this.valueBuilder_.getMessageOrBuilder(index);
            }

            @Override
            public List<? extends MysqlxDatatypes.ScalarOrBuilder> getValueOrBuilderList() {
                if (this.valueBuilder_ != null) {
                    return this.valueBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList(this.value_);
            }

            public MysqlxDatatypes.Scalar.Builder addValueBuilder() {
                return this.getValueFieldBuilder().addBuilder(MysqlxDatatypes.Scalar.getDefaultInstance());
            }

            public MysqlxDatatypes.Scalar.Builder addValueBuilder(int index) {
                return this.getValueFieldBuilder().addBuilder(index, MysqlxDatatypes.Scalar.getDefaultInstance());
            }

            public List<MysqlxDatatypes.Scalar.Builder> getValueBuilderList() {
                return this.getValueFieldBuilder().getBuilderList();
            }

            private RepeatedFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> getValueFieldBuilder() {
                if (this.valueBuilder_ == null) {
                    this.valueBuilder_ = new RepeatedFieldBuilderV3(this.value_, (this.bitField0_ & 2) != 0, this.getParentForChildren(), this.isClean());
                    this.value_ = null;
                }
                return this.valueBuilder_;
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

        public static enum Parameter implements ProtocolMessageEnum
        {
            CURRENT_SCHEMA(1),
            ACCOUNT_EXPIRED(2),
            GENERATED_INSERT_ID(3),
            ROWS_AFFECTED(4),
            ROWS_FOUND(5),
            ROWS_MATCHED(6),
            TRX_COMMITTED(7),
            TRX_ROLLEDBACK(9),
            PRODUCED_MESSAGE(10),
            CLIENT_ID_ASSIGNED(11),
            GENERATED_DOCUMENT_IDS(12);

            public static final int CURRENT_SCHEMA_VALUE = 1;
            public static final int ACCOUNT_EXPIRED_VALUE = 2;
            public static final int GENERATED_INSERT_ID_VALUE = 3;
            public static final int ROWS_AFFECTED_VALUE = 4;
            public static final int ROWS_FOUND_VALUE = 5;
            public static final int ROWS_MATCHED_VALUE = 6;
            public static final int TRX_COMMITTED_VALUE = 7;
            public static final int TRX_ROLLEDBACK_VALUE = 9;
            public static final int PRODUCED_MESSAGE_VALUE = 10;
            public static final int CLIENT_ID_ASSIGNED_VALUE = 11;
            public static final int GENERATED_DOCUMENT_IDS_VALUE = 12;
            private static final Internal.EnumLiteMap<Parameter> internalValueMap;
            private static final Parameter[] VALUES;
            private final int value;

            @Override
            public final int getNumber() {
                return this.value;
            }

            @Deprecated
            public static Parameter valueOf(int value) {
                return Parameter.forNumber(value);
            }

            public static Parameter forNumber(int value) {
                switch (value) {
                    case 1: {
                        return CURRENT_SCHEMA;
                    }
                    case 2: {
                        return ACCOUNT_EXPIRED;
                    }
                    case 3: {
                        return GENERATED_INSERT_ID;
                    }
                    case 4: {
                        return ROWS_AFFECTED;
                    }
                    case 5: {
                        return ROWS_FOUND;
                    }
                    case 6: {
                        return ROWS_MATCHED;
                    }
                    case 7: {
                        return TRX_COMMITTED;
                    }
                    case 9: {
                        return TRX_ROLLEDBACK;
                    }
                    case 10: {
                        return PRODUCED_MESSAGE;
                    }
                    case 11: {
                        return CLIENT_ID_ASSIGNED;
                    }
                    case 12: {
                        return GENERATED_DOCUMENT_IDS;
                    }
                }
                return null;
            }

            public static Internal.EnumLiteMap<Parameter> internalGetValueMap() {
                return internalValueMap;
            }

            @Override
            public final Descriptors.EnumValueDescriptor getValueDescriptor() {
                return Parameter.getDescriptor().getValues().get(this.ordinal());
            }

            @Override
            public final Descriptors.EnumDescriptor getDescriptorForType() {
                return Parameter.getDescriptor();
            }

            public static final Descriptors.EnumDescriptor getDescriptor() {
                return SessionStateChanged.getDescriptor().getEnumTypes().get(0);
            }

            public static Parameter valueOf(Descriptors.EnumValueDescriptor desc) {
                if (desc.getType() != Parameter.getDescriptor()) {
                    throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
                }
                return VALUES[desc.getIndex()];
            }

            private Parameter(int value) {
                this.value = value;
            }

            static {
                internalValueMap = new Internal.EnumLiteMap<Parameter>(){

                    @Override
                    public Parameter findValueByNumber(int number) {
                        return Parameter.forNumber(number);
                    }
                };
                VALUES = Parameter.values();
            }
        }
    }

    public static interface SessionStateChangedOrBuilder
    extends MessageOrBuilder {
        public boolean hasParam();

        public SessionStateChanged.Parameter getParam();

        public List<MysqlxDatatypes.Scalar> getValueList();

        public MysqlxDatatypes.Scalar getValue(int var1);

        public int getValueCount();

        public List<? extends MysqlxDatatypes.ScalarOrBuilder> getValueOrBuilderList();

        public MysqlxDatatypes.ScalarOrBuilder getValueOrBuilder(int var1);
    }

    public static final class SessionVariableChanged
    extends GeneratedMessageV3
    implements SessionVariableChangedOrBuilder {
        private static final long serialVersionUID = 0L;
        private int bitField0_;
        public static final int PARAM_FIELD_NUMBER = 1;
        private volatile Object param_;
        public static final int VALUE_FIELD_NUMBER = 2;
        private MysqlxDatatypes.Scalar value_;
        private byte memoizedIsInitialized = (byte)-1;
        private static final SessionVariableChanged DEFAULT_INSTANCE = new SessionVariableChanged();
        @Deprecated
        public static final Parser<SessionVariableChanged> PARSER = new AbstractParser<SessionVariableChanged>(){

            @Override
            public SessionVariableChanged parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return new SessionVariableChanged(input, extensionRegistry);
            }
        };

        private SessionVariableChanged(GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }

        private SessionVariableChanged() {
            this.param_ = "";
        }

        @Override
        protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
            return new SessionVariableChanged();
        }

        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        private SessionVariableChanged(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this();
            if (extensionRegistry == null) {
                throw new NullPointerException();
            }
            boolean mutable_bitField0_ = false;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();
            try {
                boolean done = false;
                block11: while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0: {
                            done = true;
                            continue block11;
                        }
                        case 10: {
                            ByteString bs = input.readBytes();
                            this.bitField0_ |= 1;
                            this.param_ = bs;
                            continue block11;
                        }
                        case 18: {
                            MysqlxDatatypes.Scalar.Builder subBuilder = null;
                            if ((this.bitField0_ & 2) != 0) {
                                subBuilder = this.value_.toBuilder();
                            }
                            this.value_ = input.readMessage(MysqlxDatatypes.Scalar.PARSER, extensionRegistry);
                            if (subBuilder != null) {
                                subBuilder.mergeFrom(this.value_);
                                this.value_ = subBuilder.buildPartial();
                            }
                            this.bitField0_ |= 2;
                            continue block11;
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
            return internal_static_Mysqlx_Notice_SessionVariableChanged_descriptor;
        }

        @Override
        protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return internal_static_Mysqlx_Notice_SessionVariableChanged_fieldAccessorTable.ensureFieldAccessorsInitialized(SessionVariableChanged.class, Builder.class);
        }

        @Override
        public boolean hasParam() {
            return (this.bitField0_ & 1) != 0;
        }

        @Override
        public String getParam() {
            Object ref = this.param_;
            if (ref instanceof String) {
                return (String)ref;
            }
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.param_ = s;
            }
            return s;
        }

        @Override
        public ByteString getParamBytes() {
            Object ref = this.param_;
            if (ref instanceof String) {
                ByteString b = ByteString.copyFromUtf8((String)ref);
                this.param_ = b;
                return b;
            }
            return (ByteString)ref;
        }

        @Override
        public boolean hasValue() {
            return (this.bitField0_ & 2) != 0;
        }

        @Override
        public MysqlxDatatypes.Scalar getValue() {
            return this.value_ == null ? MysqlxDatatypes.Scalar.getDefaultInstance() : this.value_;
        }

        @Override
        public MysqlxDatatypes.ScalarOrBuilder getValueOrBuilder() {
            return this.value_ == null ? MysqlxDatatypes.Scalar.getDefaultInstance() : this.value_;
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
            if (!this.hasParam()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            if (this.hasValue() && !this.getValue().isInitialized()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            this.memoizedIsInitialized = 1;
            return true;
        }

        @Override
        public void writeTo(CodedOutputStream output) throws IOException {
            if ((this.bitField0_ & 1) != 0) {
                GeneratedMessageV3.writeString(output, 1, this.param_);
            }
            if ((this.bitField0_ & 2) != 0) {
                output.writeMessage(2, this.getValue());
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
            if ((this.bitField0_ & 1) != 0) {
                size += GeneratedMessageV3.computeStringSize(1, this.param_);
            }
            if ((this.bitField0_ & 2) != 0) {
                size += CodedOutputStream.computeMessageSize(2, this.getValue());
            }
            this.memoizedSize = size += this.unknownFields.getSerializedSize();
            return size;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof SessionVariableChanged)) {
                return super.equals(obj);
            }
            SessionVariableChanged other = (SessionVariableChanged)obj;
            if (this.hasParam() != other.hasParam()) {
                return false;
            }
            if (this.hasParam() && !this.getParam().equals(other.getParam())) {
                return false;
            }
            if (this.hasValue() != other.hasValue()) {
                return false;
            }
            if (this.hasValue() && !this.getValue().equals(other.getValue())) {
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
            hash = 19 * hash + SessionVariableChanged.getDescriptor().hashCode();
            if (this.hasParam()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getParam().hashCode();
            }
            if (this.hasValue()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + this.getValue().hashCode();
            }
            this.memoizedHashCode = hash = 29 * hash + this.unknownFields.hashCode();
            return hash;
        }

        public static SessionVariableChanged parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static SessionVariableChanged parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static SessionVariableChanged parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static SessionVariableChanged parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static SessionVariableChanged parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static SessionVariableChanged parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static SessionVariableChanged parseFrom(InputStream input) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static SessionVariableChanged parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static SessionVariableChanged parseDelimitedFrom(InputStream input) throws IOException {
            return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
        }

        public static SessionVariableChanged parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }

        public static SessionVariableChanged parseFrom(CodedInputStream input) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static SessionVariableChanged parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        @Override
        public Builder newBuilderForType() {
            return SessionVariableChanged.newBuilder();
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(SessionVariableChanged prototype) {
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

        public static SessionVariableChanged getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<SessionVariableChanged> parser() {
            return PARSER;
        }

        public Parser<SessionVariableChanged> getParserForType() {
            return PARSER;
        }

        @Override
        public SessionVariableChanged getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

        public static final class Builder
        extends GeneratedMessageV3.Builder<Builder>
        implements SessionVariableChangedOrBuilder {
            private int bitField0_;
            private Object param_ = "";
            private MysqlxDatatypes.Scalar value_;
            private SingleFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> valueBuilder_;

            public static final Descriptors.Descriptor getDescriptor() {
                return internal_static_Mysqlx_Notice_SessionVariableChanged_descriptor;
            }

            @Override
            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                return internal_static_Mysqlx_Notice_SessionVariableChanged_fieldAccessorTable.ensureFieldAccessorsInitialized(SessionVariableChanged.class, Builder.class);
            }

            private Builder() {
                this.maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                if (alwaysUseFieldBuilders) {
                    this.getValueFieldBuilder();
                }
            }

            @Override
            public Builder clear() {
                super.clear();
                this.param_ = "";
                this.bitField0_ &= 0xFFFFFFFE;
                if (this.valueBuilder_ == null) {
                    this.value_ = null;
                } else {
                    this.valueBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFD;
                return this;
            }

            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return internal_static_Mysqlx_Notice_SessionVariableChanged_descriptor;
            }

            @Override
            public SessionVariableChanged getDefaultInstanceForType() {
                return SessionVariableChanged.getDefaultInstance();
            }

            @Override
            public SessionVariableChanged build() {
                SessionVariableChanged result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw Builder.newUninitializedMessageException(result);
                }
                return result;
            }

            @Override
            public SessionVariableChanged buildPartial() {
                SessionVariableChanged result = new SessionVariableChanged(this);
                int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 1) != 0) {
                    to_bitField0_ |= 1;
                }
                result.param_ = this.param_;
                if ((from_bitField0_ & 2) != 0) {
                    if (this.valueBuilder_ == null) {
                        result.value_ = this.value_;
                    } else {
                        result.value_ = this.valueBuilder_.build();
                    }
                    to_bitField0_ |= 2;
                }
                result.bitField0_ = to_bitField0_;
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
                if (other instanceof SessionVariableChanged) {
                    return this.mergeFrom((SessionVariableChanged)other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(SessionVariableChanged other) {
                if (other == SessionVariableChanged.getDefaultInstance()) {
                    return this;
                }
                if (other.hasParam()) {
                    this.bitField0_ |= 1;
                    this.param_ = other.param_;
                    this.onChanged();
                }
                if (other.hasValue()) {
                    this.mergeValue(other.getValue());
                }
                this.mergeUnknownFields(other.unknownFields);
                this.onChanged();
                return this;
            }

            @Override
            public final boolean isInitialized() {
                if (!this.hasParam()) {
                    return false;
                }
                return !this.hasValue() || this.getValue().isInitialized();
            }

            @Override
            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                SessionVariableChanged parsedMessage = null;
                try {
                    parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (SessionVariableChanged)e.getUnfinishedMessage();
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
            public boolean hasParam() {
                return (this.bitField0_ & 1) != 0;
            }

            @Override
            public String getParam() {
                Object ref = this.param_;
                if (!(ref instanceof String)) {
                    ByteString bs = (ByteString)ref;
                    String s = bs.toStringUtf8();
                    if (bs.isValidUtf8()) {
                        this.param_ = s;
                    }
                    return s;
                }
                return (String)ref;
            }

            @Override
            public ByteString getParamBytes() {
                Object ref = this.param_;
                if (ref instanceof String) {
                    ByteString b = ByteString.copyFromUtf8((String)ref);
                    this.param_ = b;
                    return b;
                }
                return (ByteString)ref;
            }

            public Builder setParam(String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 1;
                this.param_ = value;
                this.onChanged();
                return this;
            }

            public Builder clearParam() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.param_ = SessionVariableChanged.getDefaultInstance().getParam();
                this.onChanged();
                return this;
            }

            public Builder setParamBytes(ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 1;
                this.param_ = value;
                this.onChanged();
                return this;
            }

            @Override
            public boolean hasValue() {
                return (this.bitField0_ & 2) != 0;
            }

            @Override
            public MysqlxDatatypes.Scalar getValue() {
                if (this.valueBuilder_ == null) {
                    return this.value_ == null ? MysqlxDatatypes.Scalar.getDefaultInstance() : this.value_;
                }
                return this.valueBuilder_.getMessage();
            }

            public Builder setValue(MysqlxDatatypes.Scalar value) {
                if (this.valueBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.value_ = value;
                    this.onChanged();
                } else {
                    this.valueBuilder_.setMessage(value);
                }
                this.bitField0_ |= 2;
                return this;
            }

            public Builder setValue(MysqlxDatatypes.Scalar.Builder builderForValue) {
                if (this.valueBuilder_ == null) {
                    this.value_ = builderForValue.build();
                    this.onChanged();
                } else {
                    this.valueBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 2;
                return this;
            }

            public Builder mergeValue(MysqlxDatatypes.Scalar value) {
                if (this.valueBuilder_ == null) {
                    this.value_ = (this.bitField0_ & 2) != 0 && this.value_ != null && this.value_ != MysqlxDatatypes.Scalar.getDefaultInstance() ? MysqlxDatatypes.Scalar.newBuilder(this.value_).mergeFrom(value).buildPartial() : value;
                    this.onChanged();
                } else {
                    this.valueBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 2;
                return this;
            }

            public Builder clearValue() {
                if (this.valueBuilder_ == null) {
                    this.value_ = null;
                    this.onChanged();
                } else {
                    this.valueBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFD;
                return this;
            }

            public MysqlxDatatypes.Scalar.Builder getValueBuilder() {
                this.bitField0_ |= 2;
                this.onChanged();
                return this.getValueFieldBuilder().getBuilder();
            }

            @Override
            public MysqlxDatatypes.ScalarOrBuilder getValueOrBuilder() {
                if (this.valueBuilder_ != null) {
                    return this.valueBuilder_.getMessageOrBuilder();
                }
                return this.value_ == null ? MysqlxDatatypes.Scalar.getDefaultInstance() : this.value_;
            }

            private SingleFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> getValueFieldBuilder() {
                if (this.valueBuilder_ == null) {
                    this.valueBuilder_ = new SingleFieldBuilderV3(this.getValue(), this.getParentForChildren(), this.isClean());
                    this.value_ = null;
                }
                return this.valueBuilder_;
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

    public static interface SessionVariableChangedOrBuilder
    extends MessageOrBuilder {
        public boolean hasParam();

        public String getParam();

        public ByteString getParamBytes();

        public boolean hasValue();

        public MysqlxDatatypes.Scalar getValue();

        public MysqlxDatatypes.ScalarOrBuilder getValueOrBuilder();
    }

    public static final class Warning
    extends GeneratedMessageV3
    implements WarningOrBuilder {
        private static final long serialVersionUID = 0L;
        private int bitField0_;
        public static final int LEVEL_FIELD_NUMBER = 1;
        private int level_;
        public static final int CODE_FIELD_NUMBER = 2;
        private int code_;
        public static final int MSG_FIELD_NUMBER = 3;
        private volatile Object msg_;
        private byte memoizedIsInitialized = (byte)-1;
        private static final Warning DEFAULT_INSTANCE = new Warning();
        @Deprecated
        public static final Parser<Warning> PARSER = new AbstractParser<Warning>(){

            @Override
            public Warning parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return new Warning(input, extensionRegistry);
            }
        };

        private Warning(GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }

        private Warning() {
            this.level_ = 2;
            this.msg_ = "";
        }

        @Override
        protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
            return new Warning();
        }

        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        private Warning(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this();
            if (extensionRegistry == null) {
                throw new NullPointerException();
            }
            boolean mutable_bitField0_ = false;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();
            try {
                boolean done = false;
                block12: while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0: {
                            done = true;
                            continue block12;
                        }
                        case 8: {
                            int rawValue = input.readEnum();
                            Level value = Level.valueOf(rawValue);
                            if (value == null) {
                                unknownFields.mergeVarintField(1, rawValue);
                                continue block12;
                            }
                            this.bitField0_ |= 1;
                            this.level_ = rawValue;
                            continue block12;
                        }
                        case 16: {
                            this.bitField0_ |= 2;
                            this.code_ = input.readUInt32();
                            continue block12;
                        }
                        case 26: {
                            ByteString bs = input.readBytes();
                            this.bitField0_ |= 4;
                            this.msg_ = bs;
                            continue block12;
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
            return internal_static_Mysqlx_Notice_Warning_descriptor;
        }

        @Override
        protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return internal_static_Mysqlx_Notice_Warning_fieldAccessorTable.ensureFieldAccessorsInitialized(Warning.class, Builder.class);
        }

        @Override
        public boolean hasLevel() {
            return (this.bitField0_ & 1) != 0;
        }

        @Override
        public Level getLevel() {
            Level result = Level.valueOf(this.level_);
            return result == null ? Level.WARNING : result;
        }

        @Override
        public boolean hasCode() {
            return (this.bitField0_ & 2) != 0;
        }

        @Override
        public int getCode() {
            return this.code_;
        }

        @Override
        public boolean hasMsg() {
            return (this.bitField0_ & 4) != 0;
        }

        @Override
        public String getMsg() {
            Object ref = this.msg_;
            if (ref instanceof String) {
                return (String)ref;
            }
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.msg_ = s;
            }
            return s;
        }

        @Override
        public ByteString getMsgBytes() {
            Object ref = this.msg_;
            if (ref instanceof String) {
                ByteString b = ByteString.copyFromUtf8((String)ref);
                this.msg_ = b;
                return b;
            }
            return (ByteString)ref;
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
            if (!this.hasCode()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            if (!this.hasMsg()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            this.memoizedIsInitialized = 1;
            return true;
        }

        @Override
        public void writeTo(CodedOutputStream output) throws IOException {
            if ((this.bitField0_ & 1) != 0) {
                output.writeEnum(1, this.level_);
            }
            if ((this.bitField0_ & 2) != 0) {
                output.writeUInt32(2, this.code_);
            }
            if ((this.bitField0_ & 4) != 0) {
                GeneratedMessageV3.writeString(output, 3, this.msg_);
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
            if ((this.bitField0_ & 1) != 0) {
                size += CodedOutputStream.computeEnumSize(1, this.level_);
            }
            if ((this.bitField0_ & 2) != 0) {
                size += CodedOutputStream.computeUInt32Size(2, this.code_);
            }
            if ((this.bitField0_ & 4) != 0) {
                size += GeneratedMessageV3.computeStringSize(3, this.msg_);
            }
            this.memoizedSize = size += this.unknownFields.getSerializedSize();
            return size;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Warning)) {
                return super.equals(obj);
            }
            Warning other = (Warning)obj;
            if (this.hasLevel() != other.hasLevel()) {
                return false;
            }
            if (this.hasLevel() && this.level_ != other.level_) {
                return false;
            }
            if (this.hasCode() != other.hasCode()) {
                return false;
            }
            if (this.hasCode() && this.getCode() != other.getCode()) {
                return false;
            }
            if (this.hasMsg() != other.hasMsg()) {
                return false;
            }
            if (this.hasMsg() && !this.getMsg().equals(other.getMsg())) {
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
            hash = 19 * hash + Warning.getDescriptor().hashCode();
            if (this.hasLevel()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.level_;
            }
            if (this.hasCode()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + this.getCode();
            }
            if (this.hasMsg()) {
                hash = 37 * hash + 3;
                hash = 53 * hash + this.getMsg().hashCode();
            }
            this.memoizedHashCode = hash = 29 * hash + this.unknownFields.hashCode();
            return hash;
        }

        public static Warning parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static Warning parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static Warning parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static Warning parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static Warning parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static Warning parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static Warning parseFrom(InputStream input) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static Warning parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static Warning parseDelimitedFrom(InputStream input) throws IOException {
            return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
        }

        public static Warning parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }

        public static Warning parseFrom(CodedInputStream input) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static Warning parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        @Override
        public Builder newBuilderForType() {
            return Warning.newBuilder();
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(Warning prototype) {
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

        public static Warning getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<Warning> parser() {
            return PARSER;
        }

        public Parser<Warning> getParserForType() {
            return PARSER;
        }

        @Override
        public Warning getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

        public static final class Builder
        extends GeneratedMessageV3.Builder<Builder>
        implements WarningOrBuilder {
            private int bitField0_;
            private int level_ = 2;
            private int code_;
            private Object msg_ = "";

            public static final Descriptors.Descriptor getDescriptor() {
                return internal_static_Mysqlx_Notice_Warning_descriptor;
            }

            @Override
            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                return internal_static_Mysqlx_Notice_Warning_fieldAccessorTable.ensureFieldAccessorsInitialized(Warning.class, Builder.class);
            }

            private Builder() {
                this.maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                if (alwaysUseFieldBuilders) {
                    // empty if block
                }
            }

            @Override
            public Builder clear() {
                super.clear();
                this.level_ = 2;
                this.bitField0_ &= 0xFFFFFFFE;
                this.code_ = 0;
                this.bitField0_ &= 0xFFFFFFFD;
                this.msg_ = "";
                this.bitField0_ &= 0xFFFFFFFB;
                return this;
            }

            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return internal_static_Mysqlx_Notice_Warning_descriptor;
            }

            @Override
            public Warning getDefaultInstanceForType() {
                return Warning.getDefaultInstance();
            }

            @Override
            public Warning build() {
                Warning result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw Builder.newUninitializedMessageException(result);
                }
                return result;
            }

            @Override
            public Warning buildPartial() {
                Warning result = new Warning(this);
                int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 1) != 0) {
                    to_bitField0_ |= 1;
                }
                result.level_ = this.level_;
                if ((from_bitField0_ & 2) != 0) {
                    result.code_ = this.code_;
                    to_bitField0_ |= 2;
                }
                if ((from_bitField0_ & 4) != 0) {
                    to_bitField0_ |= 4;
                }
                result.msg_ = this.msg_;
                result.bitField0_ = to_bitField0_;
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
                if (other instanceof Warning) {
                    return this.mergeFrom((Warning)other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(Warning other) {
                if (other == Warning.getDefaultInstance()) {
                    return this;
                }
                if (other.hasLevel()) {
                    this.setLevel(other.getLevel());
                }
                if (other.hasCode()) {
                    this.setCode(other.getCode());
                }
                if (other.hasMsg()) {
                    this.bitField0_ |= 4;
                    this.msg_ = other.msg_;
                    this.onChanged();
                }
                this.mergeUnknownFields(other.unknownFields);
                this.onChanged();
                return this;
            }

            @Override
            public final boolean isInitialized() {
                if (!this.hasCode()) {
                    return false;
                }
                return this.hasMsg();
            }

            @Override
            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                Warning parsedMessage = null;
                try {
                    parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (Warning)e.getUnfinishedMessage();
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
            public boolean hasLevel() {
                return (this.bitField0_ & 1) != 0;
            }

            @Override
            public Level getLevel() {
                Level result = Level.valueOf(this.level_);
                return result == null ? Level.WARNING : result;
            }

            public Builder setLevel(Level value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 1;
                this.level_ = value.getNumber();
                this.onChanged();
                return this;
            }

            public Builder clearLevel() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.level_ = 2;
                this.onChanged();
                return this;
            }

            @Override
            public boolean hasCode() {
                return (this.bitField0_ & 2) != 0;
            }

            @Override
            public int getCode() {
                return this.code_;
            }

            public Builder setCode(int value) {
                this.bitField0_ |= 2;
                this.code_ = value;
                this.onChanged();
                return this;
            }

            public Builder clearCode() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.code_ = 0;
                this.onChanged();
                return this;
            }

            @Override
            public boolean hasMsg() {
                return (this.bitField0_ & 4) != 0;
            }

            @Override
            public String getMsg() {
                Object ref = this.msg_;
                if (!(ref instanceof String)) {
                    ByteString bs = (ByteString)ref;
                    String s = bs.toStringUtf8();
                    if (bs.isValidUtf8()) {
                        this.msg_ = s;
                    }
                    return s;
                }
                return (String)ref;
            }

            @Override
            public ByteString getMsgBytes() {
                Object ref = this.msg_;
                if (ref instanceof String) {
                    ByteString b = ByteString.copyFromUtf8((String)ref);
                    this.msg_ = b;
                    return b;
                }
                return (ByteString)ref;
            }

            public Builder setMsg(String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 4;
                this.msg_ = value;
                this.onChanged();
                return this;
            }

            public Builder clearMsg() {
                this.bitField0_ &= 0xFFFFFFFB;
                this.msg_ = Warning.getDefaultInstance().getMsg();
                this.onChanged();
                return this;
            }

            public Builder setMsgBytes(ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 4;
                this.msg_ = value;
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

        public static enum Level implements ProtocolMessageEnum
        {
            NOTE(1),
            WARNING(2),
            ERROR(3);

            public static final int NOTE_VALUE = 1;
            public static final int WARNING_VALUE = 2;
            public static final int ERROR_VALUE = 3;
            private static final Internal.EnumLiteMap<Level> internalValueMap;
            private static final Level[] VALUES;
            private final int value;

            @Override
            public final int getNumber() {
                return this.value;
            }

            @Deprecated
            public static Level valueOf(int value) {
                return Level.forNumber(value);
            }

            public static Level forNumber(int value) {
                switch (value) {
                    case 1: {
                        return NOTE;
                    }
                    case 2: {
                        return WARNING;
                    }
                    case 3: {
                        return ERROR;
                    }
                }
                return null;
            }

            public static Internal.EnumLiteMap<Level> internalGetValueMap() {
                return internalValueMap;
            }

            @Override
            public final Descriptors.EnumValueDescriptor getValueDescriptor() {
                return Level.getDescriptor().getValues().get(this.ordinal());
            }

            @Override
            public final Descriptors.EnumDescriptor getDescriptorForType() {
                return Level.getDescriptor();
            }

            public static final Descriptors.EnumDescriptor getDescriptor() {
                return Warning.getDescriptor().getEnumTypes().get(0);
            }

            public static Level valueOf(Descriptors.EnumValueDescriptor desc) {
                if (desc.getType() != Level.getDescriptor()) {
                    throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
                }
                return VALUES[desc.getIndex()];
            }

            private Level(int value) {
                this.value = value;
            }

            static {
                internalValueMap = new Internal.EnumLiteMap<Level>(){

                    @Override
                    public Level findValueByNumber(int number) {
                        return Level.forNumber(number);
                    }
                };
                VALUES = Level.values();
            }
        }
    }

    public static interface WarningOrBuilder
    extends MessageOrBuilder {
        public boolean hasLevel();

        public Warning.Level getLevel();

        public boolean hasCode();

        public int getCode();

        public boolean hasMsg();

        public String getMsg();

        public ByteString getMsgBytes();
    }

    public static final class Frame
    extends GeneratedMessageV3
    implements FrameOrBuilder {
        private static final long serialVersionUID = 0L;
        private int bitField0_;
        public static final int TYPE_FIELD_NUMBER = 1;
        private int type_;
        public static final int SCOPE_FIELD_NUMBER = 2;
        private int scope_;
        public static final int PAYLOAD_FIELD_NUMBER = 3;
        private ByteString payload_;
        private byte memoizedIsInitialized = (byte)-1;
        private static final Frame DEFAULT_INSTANCE = new Frame();
        @Deprecated
        public static final Parser<Frame> PARSER = new AbstractParser<Frame>(){

            @Override
            public Frame parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return new Frame(input, extensionRegistry);
            }
        };

        private Frame(GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }

        private Frame() {
            this.scope_ = 1;
            this.payload_ = ByteString.EMPTY;
        }

        @Override
        protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
            return new Frame();
        }

        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        private Frame(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this();
            if (extensionRegistry == null) {
                throw new NullPointerException();
            }
            boolean mutable_bitField0_ = false;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();
            try {
                boolean done = false;
                block12: while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0: {
                            done = true;
                            continue block12;
                        }
                        case 8: {
                            this.bitField0_ |= 1;
                            this.type_ = input.readUInt32();
                            continue block12;
                        }
                        case 16: {
                            int rawValue = input.readEnum();
                            Scope value = Scope.valueOf(rawValue);
                            if (value == null) {
                                unknownFields.mergeVarintField(2, rawValue);
                                continue block12;
                            }
                            this.bitField0_ |= 2;
                            this.scope_ = rawValue;
                            continue block12;
                        }
                        case 26: {
                            this.bitField0_ |= 4;
                            this.payload_ = input.readBytes();
                            continue block12;
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
            return internal_static_Mysqlx_Notice_Frame_descriptor;
        }

        @Override
        protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return internal_static_Mysqlx_Notice_Frame_fieldAccessorTable.ensureFieldAccessorsInitialized(Frame.class, Builder.class);
        }

        @Override
        public boolean hasType() {
            return (this.bitField0_ & 1) != 0;
        }

        @Override
        public int getType() {
            return this.type_;
        }

        @Override
        public boolean hasScope() {
            return (this.bitField0_ & 2) != 0;
        }

        @Override
        public Scope getScope() {
            Scope result = Scope.valueOf(this.scope_);
            return result == null ? Scope.GLOBAL : result;
        }

        @Override
        public boolean hasPayload() {
            return (this.bitField0_ & 4) != 0;
        }

        @Override
        public ByteString getPayload() {
            return this.payload_;
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
            if (!this.hasType()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            this.memoizedIsInitialized = 1;
            return true;
        }

        @Override
        public void writeTo(CodedOutputStream output) throws IOException {
            if ((this.bitField0_ & 1) != 0) {
                output.writeUInt32(1, this.type_);
            }
            if ((this.bitField0_ & 2) != 0) {
                output.writeEnum(2, this.scope_);
            }
            if ((this.bitField0_ & 4) != 0) {
                output.writeBytes(3, this.payload_);
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
            if ((this.bitField0_ & 1) != 0) {
                size += CodedOutputStream.computeUInt32Size(1, this.type_);
            }
            if ((this.bitField0_ & 2) != 0) {
                size += CodedOutputStream.computeEnumSize(2, this.scope_);
            }
            if ((this.bitField0_ & 4) != 0) {
                size += CodedOutputStream.computeBytesSize(3, this.payload_);
            }
            this.memoizedSize = size += this.unknownFields.getSerializedSize();
            return size;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Frame)) {
                return super.equals(obj);
            }
            Frame other = (Frame)obj;
            if (this.hasType() != other.hasType()) {
                return false;
            }
            if (this.hasType() && this.getType() != other.getType()) {
                return false;
            }
            if (this.hasScope() != other.hasScope()) {
                return false;
            }
            if (this.hasScope() && this.scope_ != other.scope_) {
                return false;
            }
            if (this.hasPayload() != other.hasPayload()) {
                return false;
            }
            if (this.hasPayload() && !this.getPayload().equals(other.getPayload())) {
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
            hash = 19 * hash + Frame.getDescriptor().hashCode();
            if (this.hasType()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getType();
            }
            if (this.hasScope()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + this.scope_;
            }
            if (this.hasPayload()) {
                hash = 37 * hash + 3;
                hash = 53 * hash + this.getPayload().hashCode();
            }
            this.memoizedHashCode = hash = 29 * hash + this.unknownFields.hashCode();
            return hash;
        }

        public static Frame parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static Frame parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static Frame parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static Frame parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static Frame parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static Frame parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static Frame parseFrom(InputStream input) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static Frame parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static Frame parseDelimitedFrom(InputStream input) throws IOException {
            return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
        }

        public static Frame parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }

        public static Frame parseFrom(CodedInputStream input) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static Frame parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        @Override
        public Builder newBuilderForType() {
            return Frame.newBuilder();
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(Frame prototype) {
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

        public static Frame getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<Frame> parser() {
            return PARSER;
        }

        public Parser<Frame> getParserForType() {
            return PARSER;
        }

        @Override
        public Frame getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

        public static final class Builder
        extends GeneratedMessageV3.Builder<Builder>
        implements FrameOrBuilder {
            private int bitField0_;
            private int type_;
            private int scope_ = 1;
            private ByteString payload_ = ByteString.EMPTY;

            public static final Descriptors.Descriptor getDescriptor() {
                return internal_static_Mysqlx_Notice_Frame_descriptor;
            }

            @Override
            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                return internal_static_Mysqlx_Notice_Frame_fieldAccessorTable.ensureFieldAccessorsInitialized(Frame.class, Builder.class);
            }

            private Builder() {
                this.maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                if (alwaysUseFieldBuilders) {
                    // empty if block
                }
            }

            @Override
            public Builder clear() {
                super.clear();
                this.type_ = 0;
                this.bitField0_ &= 0xFFFFFFFE;
                this.scope_ = 1;
                this.bitField0_ &= 0xFFFFFFFD;
                this.payload_ = ByteString.EMPTY;
                this.bitField0_ &= 0xFFFFFFFB;
                return this;
            }

            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return internal_static_Mysqlx_Notice_Frame_descriptor;
            }

            @Override
            public Frame getDefaultInstanceForType() {
                return Frame.getDefaultInstance();
            }

            @Override
            public Frame build() {
                Frame result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw Builder.newUninitializedMessageException(result);
                }
                return result;
            }

            @Override
            public Frame buildPartial() {
                Frame result = new Frame(this);
                int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 1) != 0) {
                    result.type_ = this.type_;
                    to_bitField0_ |= 1;
                }
                if ((from_bitField0_ & 2) != 0) {
                    to_bitField0_ |= 2;
                }
                result.scope_ = this.scope_;
                if ((from_bitField0_ & 4) != 0) {
                    to_bitField0_ |= 4;
                }
                result.payload_ = this.payload_;
                result.bitField0_ = to_bitField0_;
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
                if (other instanceof Frame) {
                    return this.mergeFrom((Frame)other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(Frame other) {
                if (other == Frame.getDefaultInstance()) {
                    return this;
                }
                if (other.hasType()) {
                    this.setType(other.getType());
                }
                if (other.hasScope()) {
                    this.setScope(other.getScope());
                }
                if (other.hasPayload()) {
                    this.setPayload(other.getPayload());
                }
                this.mergeUnknownFields(other.unknownFields);
                this.onChanged();
                return this;
            }

            @Override
            public final boolean isInitialized() {
                return this.hasType();
            }

            @Override
            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                Frame parsedMessage = null;
                try {
                    parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (Frame)e.getUnfinishedMessage();
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
            public boolean hasType() {
                return (this.bitField0_ & 1) != 0;
            }

            @Override
            public int getType() {
                return this.type_;
            }

            public Builder setType(int value) {
                this.bitField0_ |= 1;
                this.type_ = value;
                this.onChanged();
                return this;
            }

            public Builder clearType() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.type_ = 0;
                this.onChanged();
                return this;
            }

            @Override
            public boolean hasScope() {
                return (this.bitField0_ & 2) != 0;
            }

            @Override
            public Scope getScope() {
                Scope result = Scope.valueOf(this.scope_);
                return result == null ? Scope.GLOBAL : result;
            }

            public Builder setScope(Scope value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 2;
                this.scope_ = value.getNumber();
                this.onChanged();
                return this;
            }

            public Builder clearScope() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.scope_ = 1;
                this.onChanged();
                return this;
            }

            @Override
            public boolean hasPayload() {
                return (this.bitField0_ & 4) != 0;
            }

            @Override
            public ByteString getPayload() {
                return this.payload_;
            }

            public Builder setPayload(ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 4;
                this.payload_ = value;
                this.onChanged();
                return this;
            }

            public Builder clearPayload() {
                this.bitField0_ &= 0xFFFFFFFB;
                this.payload_ = Frame.getDefaultInstance().getPayload();
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

        public static enum Type implements ProtocolMessageEnum
        {
            WARNING(1),
            SESSION_VARIABLE_CHANGED(2),
            SESSION_STATE_CHANGED(3),
            GROUP_REPLICATION_STATE_CHANGED(4),
            SERVER_HELLO(5);

            public static final int WARNING_VALUE = 1;
            public static final int SESSION_VARIABLE_CHANGED_VALUE = 2;
            public static final int SESSION_STATE_CHANGED_VALUE = 3;
            public static final int GROUP_REPLICATION_STATE_CHANGED_VALUE = 4;
            public static final int SERVER_HELLO_VALUE = 5;
            private static final Internal.EnumLiteMap<Type> internalValueMap;
            private static final Type[] VALUES;
            private final int value;

            @Override
            public final int getNumber() {
                return this.value;
            }

            @Deprecated
            public static Type valueOf(int value) {
                return Type.forNumber(value);
            }

            public static Type forNumber(int value) {
                switch (value) {
                    case 1: {
                        return WARNING;
                    }
                    case 2: {
                        return SESSION_VARIABLE_CHANGED;
                    }
                    case 3: {
                        return SESSION_STATE_CHANGED;
                    }
                    case 4: {
                        return GROUP_REPLICATION_STATE_CHANGED;
                    }
                    case 5: {
                        return SERVER_HELLO;
                    }
                }
                return null;
            }

            public static Internal.EnumLiteMap<Type> internalGetValueMap() {
                return internalValueMap;
            }

            @Override
            public final Descriptors.EnumValueDescriptor getValueDescriptor() {
                return Type.getDescriptor().getValues().get(this.ordinal());
            }

            @Override
            public final Descriptors.EnumDescriptor getDescriptorForType() {
                return Type.getDescriptor();
            }

            public static final Descriptors.EnumDescriptor getDescriptor() {
                return Frame.getDescriptor().getEnumTypes().get(1);
            }

            public static Type valueOf(Descriptors.EnumValueDescriptor desc) {
                if (desc.getType() != Type.getDescriptor()) {
                    throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
                }
                return VALUES[desc.getIndex()];
            }

            private Type(int value) {
                this.value = value;
            }

            static {
                internalValueMap = new Internal.EnumLiteMap<Type>(){

                    @Override
                    public Type findValueByNumber(int number) {
                        return Type.forNumber(number);
                    }
                };
                VALUES = Type.values();
            }
        }

        public static enum Scope implements ProtocolMessageEnum
        {
            GLOBAL(1),
            LOCAL(2);

            public static final int GLOBAL_VALUE = 1;
            public static final int LOCAL_VALUE = 2;
            private static final Internal.EnumLiteMap<Scope> internalValueMap;
            private static final Scope[] VALUES;
            private final int value;

            @Override
            public final int getNumber() {
                return this.value;
            }

            @Deprecated
            public static Scope valueOf(int value) {
                return Scope.forNumber(value);
            }

            public static Scope forNumber(int value) {
                switch (value) {
                    case 1: {
                        return GLOBAL;
                    }
                    case 2: {
                        return LOCAL;
                    }
                }
                return null;
            }

            public static Internal.EnumLiteMap<Scope> internalGetValueMap() {
                return internalValueMap;
            }

            @Override
            public final Descriptors.EnumValueDescriptor getValueDescriptor() {
                return Scope.getDescriptor().getValues().get(this.ordinal());
            }

            @Override
            public final Descriptors.EnumDescriptor getDescriptorForType() {
                return Scope.getDescriptor();
            }

            public static final Descriptors.EnumDescriptor getDescriptor() {
                return Frame.getDescriptor().getEnumTypes().get(0);
            }

            public static Scope valueOf(Descriptors.EnumValueDescriptor desc) {
                if (desc.getType() != Scope.getDescriptor()) {
                    throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
                }
                return VALUES[desc.getIndex()];
            }

            private Scope(int value) {
                this.value = value;
            }

            static {
                internalValueMap = new Internal.EnumLiteMap<Scope>(){

                    @Override
                    public Scope findValueByNumber(int number) {
                        return Scope.forNumber(number);
                    }
                };
                VALUES = Scope.values();
            }
        }
    }

    public static interface FrameOrBuilder
    extends MessageOrBuilder {
        public boolean hasType();

        public int getType();

        public boolean hasScope();

        public Frame.Scope getScope();

        public boolean hasPayload();

        public ByteString getPayload();
    }
}

