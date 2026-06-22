/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.plugin.events.navigator;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.events.navigator.NavigatorRoomsEvent;
import java.util.ArrayList;

public class NavigatorPopularRoomsEvent
extends NavigatorRoomsEvent {
    public final ArrayList<Room> rooms;

    public NavigatorPopularRoomsEvent(Habbo habbo, ArrayList<Room> rooms) {
        super(habbo, rooms);
        this.rooms = rooms;
    }
}

