/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms.items.jukebox;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class JukeBoxPlaylistFullComposer
extends MessageComposer {
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(105);
        return this.response;
    }
}

