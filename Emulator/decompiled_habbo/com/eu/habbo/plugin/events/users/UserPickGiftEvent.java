/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.events.users.UserEvent;

public class UserPickGiftEvent
extends UserEvent {
    public final int keyA;
    public final int keyB;
    public final int index;

    public UserPickGiftEvent(Habbo habbo, int keyA, int keyB, int index) {
        super(habbo);
        this.keyA = keyA;
        this.keyB = keyB;
        this.index = index;
    }
}

