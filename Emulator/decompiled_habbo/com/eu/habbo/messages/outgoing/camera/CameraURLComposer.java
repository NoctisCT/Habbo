/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.camera;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class CameraURLComposer
extends MessageComposer {
    private final String URL;

    public CameraURLComposer(String url) {
        this.URL = url;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(3696);
        this.response.appendString(this.URL);
        return this.response;
    }

    public String getURL() {
        return this.URL;
    }
}

