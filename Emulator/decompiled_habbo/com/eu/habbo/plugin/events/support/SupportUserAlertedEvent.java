/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.plugin.events.support;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.events.support.SupportEvent;
import com.eu.habbo.plugin.events.support.SupportUserAlertedReason;

public class SupportUserAlertedEvent
extends SupportEvent {
    public Habbo target;
    public String message;
    public SupportUserAlertedReason reason;

    public SupportUserAlertedEvent(Habbo moderator, Habbo target, String message, SupportUserAlertedReason reason) {
        super(moderator);
        this.message = message;
        this.target = target;
        this.reason = reason;
    }
}

