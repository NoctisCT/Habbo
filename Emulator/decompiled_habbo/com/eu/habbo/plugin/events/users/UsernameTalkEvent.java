/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatType;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.plugin.events.users.UserEvent;

public class UsernameTalkEvent
extends UserEvent {
    public final RoomChatMessage chatMessage;
    public final RoomChatType chatType;
    private ServerMessage customComposer = null;

    public UsernameTalkEvent(Habbo habbo, RoomChatMessage chatMessage, RoomChatType chatType) {
        super(habbo);
        this.chatMessage = chatMessage;
        this.chatType = chatType;
    }

    public void setCustomComposer(ServerMessage customComposer) {
        this.customComposer = customComposer;
    }

    public boolean hasCustomComposer() {
        return this.customComposer != null;
    }

    public ServerMessage getCustomComposer() {
        return this.customComposer;
    }
}

