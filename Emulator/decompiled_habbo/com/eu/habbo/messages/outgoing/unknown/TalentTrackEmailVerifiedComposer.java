/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class TalentTrackEmailVerifiedComposer
extends MessageComposer {
    private final String email;
    private final boolean unknownB1;
    private final boolean unknownB2;

    public TalentTrackEmailVerifiedComposer(String email, boolean unknownB1, boolean unknownB2) {
        this.email = email;
        this.unknownB1 = unknownB1;
        this.unknownB2 = unknownB2;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(612);
        this.response.appendString(this.email);
        this.response.appendBoolean(this.unknownB1);
        this.response.appendBoolean(this.unknownB2);
        return this.response;
    }

    public String getEmail() {
        return this.email;
    }

    public boolean isUnknownB1() {
        return this.unknownB1;
    }

    public boolean isUnknownB2() {
        return this.unknownB2;
    }
}

