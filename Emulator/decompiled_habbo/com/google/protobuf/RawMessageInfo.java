/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.MessageInfo;
import com.google.protobuf.MessageLite;
import com.google.protobuf.ProtoSyntax;

final class RawMessageInfo
implements MessageInfo {
    private final MessageLite defaultInstance;
    private final String info;
    private final Object[] objects;
    private final int flags;

    RawMessageInfo(MessageLite defaultInstance, String info, Object[] objects) {
        this.defaultInstance = defaultInstance;
        this.info = info;
        this.objects = objects;
        int position = 0;
        char value = info.charAt(position++);
        if (value < '\ud800') {
            this.flags = value;
        } else {
            int result = value & 0x1FFF;
            int shift = 13;
            while ((value = info.charAt(position++)) >= '\ud800') {
                result |= (value & 0x1FFF) << shift;
                shift += 13;
            }
            this.flags = result | value << shift;
        }
    }

    String getStringInfo() {
        return this.info;
    }

    Object[] getObjects() {
        return this.objects;
    }

    @Override
    public MessageLite getDefaultInstance() {
        return this.defaultInstance;
    }

    @Override
    public ProtoSyntax getSyntax() {
        return (this.flags & 1) == 1 ? ProtoSyntax.PROTO2 : ProtoSyntax.PROTO3;
    }

    @Override
    public boolean isMessageSetWireFormat() {
        return (this.flags & 2) == 2;
    }
}

