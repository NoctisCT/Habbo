/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.events.users.UserEvent;

public class UserExitRoomEvent
extends UserEvent {
    public final UserExitRoomReason reason;

    public UserExitRoomEvent(Habbo habbo, UserExitRoomReason reason) {
        super(habbo);
        this.reason = reason;
    }

    public static enum UserExitRoomReason {
        DOOR(false),
        KICKED_HABBO(false),
        KICKED_IDLE(true),
        TELEPORT(false);

        public final boolean cancellable;

        private UserExitRoomReason(boolean cancellable) {
            this.cancellable = cancellable;
        }
    }
}

