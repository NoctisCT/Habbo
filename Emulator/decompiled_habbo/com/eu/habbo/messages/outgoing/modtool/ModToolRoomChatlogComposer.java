/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.modtool;

import com.eu.habbo.habbohotel.modtool.ModToolChatLog;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ModToolRoomChatlogComposer
extends MessageComposer {
    private final Room room;
    private final ArrayList<ModToolChatLog> chatlog;

    public ModToolRoomChatlogComposer(Room room, ArrayList<ModToolChatLog> chatlog) {
        this.room = room;
        this.chatlog = chatlog;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(3434);
        this.response.appendByte(1);
        this.response.appendShort(2);
        this.response.appendString("roomName");
        this.response.appendByte(2);
        this.response.appendString(this.room.getName());
        this.response.appendString("roomId");
        this.response.appendByte(1);
        this.response.appendInt(this.room.getId());
        SimpleDateFormat formatDate = new SimpleDateFormat("HH:mm");
        this.response.appendShort(this.chatlog.size());
        for (ModToolChatLog line : this.chatlog) {
            this.response.appendString(formatDate.format(new Date((long)line.timestamp * 1000L)));
            this.response.appendInt(line.habboId);
            this.response.appendString(line.username);
            this.response.appendString(line.message);
            this.response.appendBoolean(false);
        }
        return this.response;
    }

    public Room getRoom() {
        return this.room;
    }

    public ArrayList<ModToolChatLog> getChatlog() {
        return this.chatlog;
    }
}

