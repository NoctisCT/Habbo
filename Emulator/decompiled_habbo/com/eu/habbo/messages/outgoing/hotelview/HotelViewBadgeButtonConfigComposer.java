/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.hotelview;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class HotelViewBadgeButtonConfigComposer
extends MessageComposer {
    private final String badge;
    private final boolean enabled;

    public HotelViewBadgeButtonConfigComposer(String badge, boolean enabled) {
        this.badge = badge;
        this.enabled = enabled;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2998);
        this.response.appendString(this.badge);
        this.response.appendBoolean(this.enabled);
        return this.response;
    }

    public String getBadge() {
        return this.badge;
    }

    public boolean isEnabled() {
        return this.enabled;
    }
}

