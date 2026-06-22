/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.plugin.events.users.friends;

import com.eu.habbo.habbohotel.messenger.MessengerBuddy;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.events.users.friends.UserFriendEvent;

public class UserAcceptFriendRequestEvent
extends UserFriendEvent {
    public UserAcceptFriendRequestEvent(Habbo habbo, MessengerBuddy friend) {
        super(habbo, friend);
    }
}

