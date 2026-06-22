/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.friends;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class RoomInviteComposer
extends MessageComposer {
    private final int userId;
    private final String message;

    public RoomInviteComposer(int userId, String message) {
        this.userId = userId;
        this.message = message;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(3870);
        this.response.appendInt(this.userId);
        this.response.appendString(this.message);
        return this.response;
    }

    public int getUserId() {
        return this.userId;
    }

    public String getMessage() {
        return this.message;
    }
}

