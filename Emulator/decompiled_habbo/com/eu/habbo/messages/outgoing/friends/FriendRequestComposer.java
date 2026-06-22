/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.friends;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class FriendRequestComposer
extends MessageComposer {
    private final Habbo habbo;

    public FriendRequestComposer(Habbo habbo) {
        this.habbo = habbo;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2219);
        this.response.appendInt(this.habbo.getHabboInfo().getId());
        this.response.appendString(this.habbo.getHabboInfo().getUsername());
        this.response.appendString(this.habbo.getHabboInfo().getLook());
        return this.response;
    }

    public Habbo getHabbo() {
        return this.habbo;
    }
}

