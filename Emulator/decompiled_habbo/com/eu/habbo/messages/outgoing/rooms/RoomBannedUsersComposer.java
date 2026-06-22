/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomBan;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.set.hash.THashSet;
import java.util.NoSuchElementException;

public class RoomBannedUsersComposer
extends MessageComposer {
    private final Room room;

    public RoomBannedUsersComposer(Room room) {
        this.room = room;
    }

    @Override
    protected ServerMessage composeInternal() {
        int timeStamp = Emulator.getIntUnixTimestamp();
        THashSet<RoomBan> roomBans = new THashSet<RoomBan>();
        TIntObjectIterator<RoomBan> iterator = this.room.getBannedHabbos().iterator();
        int i = this.room.getBannedHabbos().size();
        while (i-- > 0) {
            try {
                iterator.advance();
                if (iterator.value().endTimestamp <= timeStamp) continue;
                roomBans.add(iterator.value());
            }
            catch (NoSuchElementException e) {
                // empty catch block
                break;
            }
        }
        if (roomBans.isEmpty()) {
            return null;
        }
        this.response.init(1869);
        this.response.appendInt(this.room.getId());
        this.response.appendInt(roomBans.size());
        for (RoomBan ban : roomBans) {
            this.response.appendInt(ban.userId);
            this.response.appendString(ban.username);
        }
        return this.response;
    }

    public Room getRoom() {
        return this.room;
    }
}

