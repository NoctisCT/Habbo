/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.guilds;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class GuildAcceptMemberErrorComposer
extends MessageComposer {
    public static final int NO_LONGER_MEMBER = 0;
    public static final int ALREADY_REJECTED = 1;
    public static final int ALREADY_ACCEPTED = 2;
    private final int guildId;
    private final int errorCode;

    public GuildAcceptMemberErrorComposer(int guildId, int errorCode) {
        this.guildId = guildId;
        this.errorCode = errorCode;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(818);
        this.response.appendInt(this.guildId);
        this.response.appendInt(this.errorCode);
        return this.response;
    }

    public int getGuildId() {
        return this.guildId;
    }

    public int getErrorCode() {
        return this.errorCode;
    }
}

