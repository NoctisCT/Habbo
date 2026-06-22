/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.events.users.UserEvent;

public class UserPublishPictureEvent
extends UserEvent {
    public String URL;
    public int timestamp;
    public int roomId;

    public UserPublishPictureEvent(Habbo habbo, String url, int timestamp, int roomId) {
        super(habbo);
        this.URL = url;
        this.timestamp = timestamp;
        this.roomId = roomId;
    }
}

