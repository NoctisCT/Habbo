/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class EpicPopupFrameComposer
extends MessageComposer {
    public static final String LIBRARY_URL = "${image.library.url}";
    private final String assetURI;

    public EpicPopupFrameComposer(String assetURI) {
        this.assetURI = assetURI;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(3945);
        this.response.appendString(this.assetURI);
        return this.response;
    }

    public String getAssetURI() {
        return this.assetURI;
    }
}

