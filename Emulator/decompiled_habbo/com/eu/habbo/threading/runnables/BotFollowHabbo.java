/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;

public class BotFollowHabbo
implements Runnable {
    private final Bot bot;
    private final Habbo habbo;
    private final Room room;
    private boolean hasReached;

    public BotFollowHabbo(Bot bot, Habbo habbo, Room room) {
        this.bot = bot;
        this.habbo = habbo;
        this.room = room;
        this.hasReached = false;
    }

    @Override
    public void run() {
        RoomTile target;
        if (this.bot != null && this.habbo != null && this.bot.getFollowingHabboId() == this.habbo.getHabboInfo().getId() && this.habbo.getHabboInfo().getCurrentRoom() != null && this.habbo.getHabboInfo().getCurrentRoom() == this.room && this.habbo.getRoomUnit() != null && this.bot.getRoomUnit() != null && (target = this.room.getLayout().getTileInFront(this.habbo.getRoomUnit().getCurrentLocation(), Math.abs(this.habbo.getRoomUnit().getBodyRotation().getValue() + 4) % 8)) != null) {
            if (target.x < 0 || target.y < 0) {
                target = this.room.getLayout().getTileInFront(this.habbo.getRoomUnit().getCurrentLocation(), this.habbo.getRoomUnit().getBodyRotation().getValue());
            }
            if (this.habbo.getRoomUnit().getCurrentLocation().distance(this.bot.getRoomUnit().getCurrentLocation()) < 2.0) {
                if (!this.hasReached) {
                    WiredHandler.handle(WiredTriggerType.BOT_REACHED_AVTR, this.bot.getRoomUnit(), this.room, new Object[0]);
                    this.hasReached = true;
                }
            } else {
                this.hasReached = false;
            }
            if (target.x >= 0 && target.y >= 0) {
                this.bot.getRoomUnit().setGoalLocation(target);
                this.bot.getRoomUnit().setCanWalk(true);
                Emulator.getThreading().run(this, 500L);
            }
        }
    }
}

