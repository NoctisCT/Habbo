/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.guides;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class GuideSessionPartnerIsPlayingComposer
extends MessageComposer {
    public final boolean isPlaying;

    public GuideSessionPartnerIsPlayingComposer(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(448);
        this.response.appendBoolean(this.isPlaying);
        return this.response;
    }

    public boolean isPlaying() {
        return this.isPlaying;
    }
}

