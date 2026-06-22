/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class RoomUserIgnoredComposer
extends MessageComposer {
    public static final int IGNORED = 1;
    public static final int MUTED = 2;
    public static final int UNIGNORED = 3;
    private final Habbo habbo;
    private final int state;

    public RoomUserIgnoredComposer(Habbo habbo, int state) {
        this.habbo = habbo;
        this.state = state;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(207);
        this.response.appendInt(this.state);
        this.response.appendString(this.habbo.getHabboInfo().getUsername());
        return this.response;
    }

    public Habbo getHabbo() {
        return this.habbo;
    }

    public int getState() {
        return this.state;
    }
}

