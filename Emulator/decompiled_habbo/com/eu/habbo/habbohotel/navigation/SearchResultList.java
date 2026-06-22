/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.navigation;

import com.eu.habbo.habbohotel.navigation.DisplayMode;
import com.eu.habbo.habbohotel.navigation.DisplayOrder;
import com.eu.habbo.habbohotel.navigation.ListMode;
import com.eu.habbo.habbohotel.navigation.SearchAction;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomState;
import com.eu.habbo.messages.ISerialize;
import com.eu.habbo.messages.ServerMessage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchResultList
implements ISerialize,
Comparable<SearchResultList> {
    public final int order;
    public final String code;
    public final String query;
    public final SearchAction action;
    public final ListMode mode;
    public final DisplayMode hidden;
    public final List<Room> rooms;
    public final boolean filter;
    public final boolean showInvisible;
    public final DisplayOrder displayOrder;
    public final int categoryOrder;

    public SearchResultList(int order, String code, String query, SearchAction action, ListMode mode, DisplayMode hidden, List<Room> rooms, boolean filter, boolean showInvisible, DisplayOrder displayOrder, int categoryOrder) {
        this.order = order;
        this.code = code;
        this.query = query;
        this.action = action;
        this.mode = mode;
        this.rooms = rooms;
        this.hidden = hidden;
        this.filter = filter;
        this.showInvisible = showInvisible;
        this.displayOrder = displayOrder;
        this.categoryOrder = categoryOrder;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void serialize(ServerMessage message) {
        message.appendString(this.code);
        message.appendString(this.query);
        message.appendInt(this.action.type);
        message.appendBoolean(this.hidden.equals((Object)DisplayMode.COLLAPSED));
        message.appendInt(this.mode.type);
        List<Room> list = this.rooms;
        synchronized (list) {
            if (!this.showInvisible) {
                ArrayList<Room> toRemove = new ArrayList<Room>();
                for (Room room : this.rooms) {
                    if (room.getState() != RoomState.INVISIBLE) continue;
                    toRemove.add(room);
                }
                this.rooms.removeAll(toRemove);
            }
            message.appendInt(this.rooms.size());
            Collections.sort(this.rooms);
            for (Room room : this.rooms) {
                room.serialize(message);
            }
        }
    }

    @Override
    public int compareTo(SearchResultList o) {
        if (this.displayOrder == DisplayOrder.ACTIVITY) {
            if (this.code.equalsIgnoreCase("popular")) {
                return -1;
            }
            return this.rooms.size() - o.rooms.size();
        }
        return this.categoryOrder - o.categoryOrder;
    }
}

