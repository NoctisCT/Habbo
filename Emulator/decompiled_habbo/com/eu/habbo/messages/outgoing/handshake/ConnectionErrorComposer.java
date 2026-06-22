/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.handshake;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class ConnectionErrorComposer
extends MessageComposer {
    private final int messageId;
    private final int errorCode;
    private final String timestamp;

    public ConnectionErrorComposer(int errorCode) {
        this.messageId = 0;
        this.errorCode = errorCode;
        this.timestamp = "";
    }

    public ConnectionErrorComposer(int messageId, int errorCode, String timestamp) {
        this.messageId = messageId;
        this.errorCode = errorCode;
        this.timestamp = timestamp;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1004);
        this.response.appendInt(this.messageId);
        this.response.appendInt(this.errorCode);
        this.response.appendString(this.timestamp);
        return this.response;
    }

    public int getMessageId() {
        return this.messageId;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public String getTimestamp() {
        return this.timestamp;
    }
}

