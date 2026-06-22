/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.users.verification;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class VerifyMobilePhoneWindowComposer
extends MessageComposer {
    private final int unknownInt1;
    private final int unknownInt2;
    private final int unknownInt3;

    public VerifyMobilePhoneWindowComposer(int unknownInt1, int unknownInt2, int unknownInt3) {
        this.unknownInt1 = unknownInt1;
        this.unknownInt2 = unknownInt2;
        this.unknownInt3 = unknownInt3;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2890);
        this.response.appendInt(this.unknownInt1);
        this.response.appendInt(this.unknownInt2);
        this.response.appendInt(this.unknownInt3);
        return this.response;
    }

    public int getUnknownInt1() {
        return this.unknownInt1;
    }

    public int getUnknownInt2() {
        return this.unknownInt2;
    }

    public int getUnknownInt3() {
        return this.unknownInt3;
    }
}

