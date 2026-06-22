/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.plugin.events.users.friends;

import com.eu.habbo.habbohotel.messenger.MessengerBuddy;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.events.users.friends.UserFriendEvent;

public class UserRelationShipEvent
extends UserFriendEvent {
    public int relationShip;

    public UserRelationShipEvent(Habbo habbo, MessengerBuddy friend, int relationShip) {
        super(habbo, friend);
        this.relationShip = relationShip;
    }
}

