/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.camera;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class CameraCompetitionStatusComposer
extends MessageComposer {
    private final boolean unknownBoolean;
    private final String unknownString;

    public CameraCompetitionStatusComposer(boolean unknownBoolean, String unknownString) {
        this.unknownBoolean = unknownBoolean;
        this.unknownString = unknownString;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(133);
        this.response.appendBoolean(this.unknownBoolean);
        this.response.appendString(this.unknownString);
        return this.response;
    }

    public boolean isUnknownBoolean() {
        return this.unknownBoolean;
    }

    public String getUnknownString() {
        return this.unknownString;
    }
}

