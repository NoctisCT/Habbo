/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.events.users.UserEvent;

public class UserNameChangedEvent
extends UserEvent {
    public final String oldName;

    public UserNameChangedEvent(Habbo habbo, String oldName) {
        super(habbo);
        this.oldName = oldName;
    }
}

