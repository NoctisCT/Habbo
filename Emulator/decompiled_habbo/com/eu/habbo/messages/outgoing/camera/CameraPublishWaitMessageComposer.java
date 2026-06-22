/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.camera;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class CameraPublishWaitMessageComposer
extends MessageComposer {
    public final boolean isOk;
    public final int cooldownSeconds;
    public final String extraDataId;

    public CameraPublishWaitMessageComposer(boolean isOk, int cooldownSeconds, String extraDataId) {
        this.isOk = isOk;
        this.cooldownSeconds = cooldownSeconds;
        this.extraDataId = extraDataId;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2057);
        this.response.appendBoolean(this.isOk);
        this.response.appendInt(this.cooldownSeconds);
        if (!this.extraDataId.isEmpty()) {
            this.response.appendString(this.extraDataId);
        }
        return this.response;
    }

    public boolean isOk() {
        return this.isOk;
    }

    public int getCooldownSeconds() {
        return this.cooldownSeconds;
    }

    public String getExtraDataId() {
        return this.extraDataId;
    }
}

