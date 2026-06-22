/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms.items;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import java.util.Map;
import java.util.NoSuchElementException;

public class RoomWallItemsComposer
extends MessageComposer {
    private final Room room;

    public RoomWallItemsComposer(Room room) {
        this.room = room;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1369);
        THashMap<Integer, String> userNames = new THashMap<Integer, String>();
        TIntObjectMap<String> furniOwnerNames = this.room.getFurniOwnerNames();
        TIntObjectIterator<String> iterator = furniOwnerNames.iterator();
        int i = furniOwnerNames.size();
        while (i-- > 0) {
            try {
                iterator.advance();
                userNames.put(iterator.key(), iterator.value());
            }
            catch (NoSuchElementException e) {
                // empty catch block
                break;
            }
        }
        this.response.appendInt(userNames.size());
        for (Map.Entry set : userNames.entrySet()) {
            this.response.appendInt((Integer)set.getKey());
            this.response.appendString((String)set.getValue());
        }
        THashSet<HabboItem> items = this.room.getWallItems();
        this.response.appendInt(items.size());
        for (HabboItem item : items) {
            item.serializeWallData(this.response);
        }
        return this.response;
    }

    public Room getRoom() {
        return this.room;
    }
}

