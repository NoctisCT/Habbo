/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class UnknownCompetitionComposer
extends MessageComposer {
    private final int unknownInt1;
    private final String unknownString1;
    private final int unknownInt2;
    private final int unknownInt3;

    public UnknownCompetitionComposer(int unknownInt1, String unknownString1, int unknownInt2, int unknownInt3) {
        this.unknownInt1 = unknownInt1;
        this.unknownString1 = unknownString1;
        this.unknownInt2 = unknownInt2;
        this.unknownInt3 = unknownInt3;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(3506);
        this.response.appendInt(this.unknownInt1);
        this.response.appendString(this.unknownString1);
        this.response.appendInt(this.unknownInt2);
        this.response.appendInt(this.unknownInt3);
        return this.response;
    }

    public int getUnknownInt1() {
        return this.unknownInt1;
    }

    public String getUnknownString1() {
        return this.unknownString1;
    }

    public int getUnknownInt2() {
        return this.unknownInt2;
    }

    public int getUnknownInt3() {
        return this.unknownInt3;
    }
}

