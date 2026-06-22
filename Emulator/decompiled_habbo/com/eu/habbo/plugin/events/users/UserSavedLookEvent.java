/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboGender;
import com.eu.habbo.plugin.events.users.UserEvent;

public class UserSavedLookEvent
extends UserEvent {
    public HabboGender gender;
    public String newLook;

    public UserSavedLookEvent(Habbo habbo, HabboGender gender, String newLook) {
        super(habbo);
        this.gender = gender;
        this.newLook = newLook;
    }
}

