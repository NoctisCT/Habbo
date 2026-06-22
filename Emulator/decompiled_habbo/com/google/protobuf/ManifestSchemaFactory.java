/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.ExtensionSchemas;
import com.google.protobuf.GeneratedMessageInfoFactory;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.Internal;
import com.google.protobuf.ListFieldSchema;
import com.google.protobuf.MapFieldSchemas;
import com.google.protobuf.MessageInfo;
import com.google.protobuf.MessageInfoFactory;
import com.google.protobuf.MessageSchema;
import com.google.protobuf.MessageSetSchema;
import com.google.protobuf.NewInstanceSchemas;
import com.google.protobuf.ProtoSyntax;
import com.google.protobuf.Schema;
import com.google.protobuf.SchemaFactory;
import com.google.protobuf.SchemaUtil;

final class ManifestSchemaFactory
implements SchemaFactory {
    private final MessageInfoFactory messageInfoFactory;
    private static final MessageInfoFactory EMPTY_FACTORY = new MessageInfoFactory(){

        @Override
        public boolean isSupported(Class<?> clazz) {
            return false;
        }

        @Override
        public MessageInfo messageInfoFor(Class<?> clazz) {
            throw new IllegalStateException("This should never be called.");
        }
    };

    public ManifestSchemaFactory() {
        this(ManifestSchemaFactory.getDefaultMessageInfoFactory());
    }

    private ManifestSchemaFactory(MessageInfoFactory messageInfoFactory) {
        this.messageInfoFactory = Internal.checkNotNull(messageInfoFactory, "messageInfoFactory");
    }

    @Override
    public <T> Schema<T> createSchema(Class<T> messageType) {
        SchemaUtil.requireGeneratedMessage(messageType);
        MessageInfo messageInfo = this.messageInfoFactory.messageInfoFor(messageType);
        if (messageInfo.isMessageSetWireFormat()) {
            if (GeneratedMessageLite.class.isAssignableFrom(messageType)) {
                return MessageSetSchema.newSchema(SchemaUtil.unknownFieldSetLiteSchema(), ExtensionSchemas.lite(), messageInfo.getDefaultInstance());
            }
            return MessageSetSchema.newSchema(SchemaUtil.proto2UnknownFieldSetSchema(), ExtensionSchemas.full(), messageInfo.getDefaultInstance());
        }
        return ManifestSchemaFactory.newSchema(messageType, messageInfo);
    }

    private static <T> Schema<T> newSchema(Class<T> messageType, MessageInfo messageInfo) {
        if (GeneratedMessageLite.class.isAssignableFrom(messageType)) {
            return ManifestSchemaFactory.isProto2(messageInfo) ? MessageSchema.newSchema(messageType, messageInfo, NewInstanceSchemas.lite(), ListFieldSchema.lite(), SchemaUtil.unknownFieldSetLiteSchema(), ExtensionSchemas.lite(), MapFieldSchemas.lite()) : MessageSchema.newSchema(messageType, messageInfo, NewInstanceSchemas.lite(), ListFieldSchema.lite(), SchemaUtil.unknownFieldSetLiteSchema(), null, MapFieldSchemas.lite());
        }
        return ManifestSchemaFactory.isProto2(messageInfo) ? MessageSchema.newSchema(messageType, messageInfo, NewInstanceSchemas.full(), ListFieldSchema.full(), SchemaUtil.proto2UnknownFieldSetSchema(), ExtensionSchemas.full(), MapFieldSchemas.full()) : MessageSchema.newSchema(messageType, messageInfo, NewInstanceSchemas.full(), ListFieldSchema.full(), SchemaUtil.proto3UnknownFieldSetSchema(), null, MapFieldSchemas.full());
    }

    private static boolean isProto2(MessageInfo messageInfo) {
        return messageInfo.getSyntax() == ProtoSyntax.PROTO2;
    }

    private static MessageInfoFactory getDefaultMessageInfoFactory() {
        return new CompositeMessageInfoFactory(GeneratedMessageInfoFactory.getInstance(), ManifestSchemaFactory.getDescriptorMessageInfoFactory());
    }

    private static MessageInfoFactory getDescriptorMessageInfoFactory() {
        try {
            Class<?> clazz = Class.forName("com.google.protobuf.DescriptorMessageInfoFactory");
            return (MessageInfoFactory)clazz.getDeclaredMethod("getInstance", new Class[0]).invoke(null, new Object[0]);
        }
        catch (Exception e) {
            return EMPTY_FACTORY;
        }
    }

    private static class CompositeMessageInfoFactory
    implements MessageInfoFactory {
        private MessageInfoFactory[] factories;

        CompositeMessageInfoFactory(MessageInfoFactory ... factories) {
            this.factories = factories;
        }

        @Override
        public boolean isSupported(Class<?> clazz) {
            for (MessageInfoFactory factory : this.factories) {
                if (!factory.isSupported(clazz)) continue;
                return true;
            }
            return false;
        }

        @Override
        public MessageInfo messageInfoFor(Class<?> clazz) {
            for (MessageInfoFactory factory : this.factories) {
                if (!factory.isSupported(clazz)) continue;
                return factory.messageInfoFor(clazz);
            }
            throw new UnsupportedOperationException("No factory is available for message type: " + clazz.getName());
        }
    }
}

