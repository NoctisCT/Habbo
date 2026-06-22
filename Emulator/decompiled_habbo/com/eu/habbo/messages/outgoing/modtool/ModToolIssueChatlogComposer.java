/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.modtool;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.modtool.ModToolChatLog;
import com.eu.habbo.habbohotel.modtool.ModToolChatRecordDataContext;
import com.eu.habbo.habbohotel.modtool.ModToolIssue;
import com.eu.habbo.habbohotel.modtool.ModToolIssueChatlogType;
import com.eu.habbo.habbohotel.modtool.ModToolTicketType;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

public class ModToolIssueChatlogComposer
extends MessageComposer {
    public static SimpleDateFormat format = new SimpleDateFormat("HH:mm");
    private final ModToolIssue issue;
    private final List<ModToolChatLog> chatlog;
    private final String roomName;
    private ModToolIssueChatlogType type = ModToolIssueChatlogType.CHAT;

    public ModToolIssueChatlogComposer(ModToolIssue issue, List<ModToolChatLog> chatlog, String roomName) {
        this.issue = issue;
        this.chatlog = chatlog;
        this.roomName = roomName;
    }

    public ModToolIssueChatlogComposer(ModToolIssue issue, List<ModToolChatLog> chatlog, String roomName, ModToolIssueChatlogType type) {
        this.issue = issue;
        this.chatlog = chatlog;
        this.roomName = roomName;
        this.type = type;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(607);
        this.response.appendInt(this.issue.id);
        this.response.appendInt(this.issue.senderId);
        this.response.appendInt(this.issue.reportedId);
        this.response.appendInt(this.issue.roomId);
        Collections.sort(this.chatlog);
        if (this.chatlog.isEmpty()) {
            return null;
        }
        this.response.appendByte(this.type.getType());
        if (this.issue.type == ModToolTicketType.IM) {
            this.response.appendShort(1);
            ModToolChatRecordDataContext.MESSAGE_ID.append(this.response);
            this.response.appendInt(this.issue.senderId);
        } else if (this.issue.type == ModToolTicketType.DISCUSSION) {
            this.response.appendShort(this.type == ModToolIssueChatlogType.FORUM_COMMENT ? 3 : 2);
            ModToolChatRecordDataContext.GROUP_ID.append(this.response);
            this.response.appendInt(this.issue.groupId);
            ModToolChatRecordDataContext.THREAD_ID.append(this.response);
            this.response.appendInt(this.issue.threadId);
            if (this.type == ModToolIssueChatlogType.FORUM_COMMENT) {
                ModToolChatRecordDataContext.MESSAGE_ID.append(this.response);
                this.response.appendInt(this.issue.commentId);
            }
        } else if (this.issue.type == ModToolTicketType.PHOTO) {
            this.response.appendShort(2);
            ModToolChatRecordDataContext.ROOM_NAME.append(this.response);
            this.response.appendString(this.roomName);
            ModToolChatRecordDataContext.PHOTO_ID.append(this.response);
            this.response.appendString("" + this.issue.photoItem.getId());
        } else {
            this.response.appendShort(3);
            ModToolChatRecordDataContext.ROOM_NAME.append(this.response);
            this.response.appendString(this.roomName);
            ModToolChatRecordDataContext.ROOM_ID.append(this.response);
            this.response.appendInt(this.issue.roomId);
            ModToolChatRecordDataContext.GROUP_ID.append(this.response);
            Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.issue.roomId);
            this.response.appendInt(room == null ? 0 : room.getGuildId());
        }
        this.response.appendShort(this.chatlog.size());
        for (ModToolChatLog chatLog : this.chatlog) {
            this.response.appendString(format.format((long)chatLog.timestamp * 1000L));
            this.response.appendInt(chatLog.habboId);
            this.response.appendString(chatLog.username);
            this.response.appendString(chatLog.message);
            this.response.appendBoolean(chatLog.highlighted);
        }
        return this.response;
    }

    public ModToolIssue getIssue() {
        return this.issue;
    }

    public List<ModToolChatLog> getChatlog() {
        return this.chatlog;
    }

    public String getRoomName() {
        return this.roomName;
    }

    public ModToolIssueChatlogType getType() {
        return this.type;
    }
}

