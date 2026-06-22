/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.friends;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class FriendNotificationComposer
extends MessageComposer {
    public static final int INSTANT_MESSAGE = -1;
    public static final int ROOM_EVENT = 0;
    public static final int ACHIEVEMENT_COMPLETED = 1;
    public static final int QUEST_COMPLETED = 2;
    public static final int IS_PLAYING_GAME = 3;
    public static final int FINISHED_GAME = 4;
    public static final int INVITE_TO_PLAY_GAME = 5;
    private final int userId;
    private final int type;
    private final String data;

    public FriendNotificationComposer(int userId, int type, String data) {
        this.userId = userId;
        this.type = type;
        this.data = data;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(3082);
        this.response.appendString("" + this.userId);
        this.response.appendInt(this.type);
        this.response.appendString(this.data);
        return this.response;
    }

    public int getUserId() {
        return this.userId;
    }

    public int getType() {
        return this.type;
    }

    public String getData() {
        return this.data;
    }
}

