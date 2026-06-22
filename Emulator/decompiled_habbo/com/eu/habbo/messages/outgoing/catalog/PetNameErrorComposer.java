/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.catalog;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class PetNameErrorComposer
extends MessageComposer {
    public static final int NAME_OK = 0;
    public static final int NAME_TO_LONG = 1;
    public static final int NAME_TO_SHORT = 2;
    public static final int FORBIDDEN_CHAR = 3;
    public static final int FORBIDDEN_WORDS = 4;
    private final int type;
    private final String value;

    public PetNameErrorComposer(int type, String value) {
        this.type = type;
        this.value = value;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1503);
        this.response.appendInt(this.type);
        this.response.appendString(this.value);
        return this.response;
    }

    public int getType() {
        return this.type;
    }

    public String getValue() {
        return this.value;
    }
}

