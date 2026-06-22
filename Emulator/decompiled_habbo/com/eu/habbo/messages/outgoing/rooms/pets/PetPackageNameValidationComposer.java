/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms.pets;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class PetPackageNameValidationComposer
extends MessageComposer {
    public static final int CLOSE_WIDGET = 0;
    public static final int NAME_TOO_SHORT = 1;
    public static final int NAME_TOO_LONG = 2;
    public static final int CONTAINS_INVALID_CHARS = 3;
    public static final int FORBIDDEN_WORDS = 4;
    private final int itemId;
    private final int errorCode;
    private final String errorString;

    public PetPackageNameValidationComposer(int itemId, int errorCode, String errorString) {
        this.itemId = itemId;
        this.errorCode = errorCode;
        this.errorString = errorString;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(546);
        this.response.appendInt(this.itemId);
        this.response.appendInt(this.errorCode);
        this.response.appendString(this.errorString);
        return this.response;
    }

    public int getItemId() {
        return this.itemId;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public String getErrorString() {
        return this.errorString;
    }
}

