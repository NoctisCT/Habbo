/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.guides;

import com.eu.habbo.habbohotel.guides.GuideChatMessage;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class GuideSessionMessageComposer
extends MessageComposer {
    private final GuideChatMessage message;

    public GuideSessionMessageComposer(GuideChatMessage message) {
        this.message = message;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(841);
        this.response.appendString(this.message.message);
        this.response.appendInt(this.message.userId);
        return this.response;
    }

    public GuideChatMessage getMessage() {
        return this.message;
    }
}

