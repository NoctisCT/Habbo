/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.users;

import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class UserPermissionsComposer
extends MessageComposer {
    private final int clubLevel;
    private final Habbo habbo;

    public UserPermissionsComposer(Habbo habbo) {
        this.clubLevel = habbo.getHabboStats().hasActiveClub() ? 2 : 0;
        this.habbo = habbo;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(411);
        this.response.appendInt(this.clubLevel);
        this.response.appendInt(this.habbo.getHabboInfo().getRank().getLevel());
        this.response.appendBoolean(this.habbo.hasPermission(Permission.ACC_AMBASSADOR));
        return this.response;
    }

    public int getClubLevel() {
        return this.clubLevel;
    }

    public Habbo getHabbo() {
        return this.habbo;
    }
}

