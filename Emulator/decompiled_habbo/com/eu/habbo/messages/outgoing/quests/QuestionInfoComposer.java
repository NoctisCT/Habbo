/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.quests;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class QuestionInfoComposer
extends MessageComposer {
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(-1);
        return this.response;
    }
}

