/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.users.verification;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class VerifyMobileNumberComposer
extends MessageComposer {
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(3639);
        return this.response;
    }
}

