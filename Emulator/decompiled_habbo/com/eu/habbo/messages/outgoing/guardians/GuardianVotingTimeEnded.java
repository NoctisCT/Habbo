/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.guardians;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class GuardianVotingTimeEnded
extends MessageComposer {
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(30);
        return this.response;
    }
}

