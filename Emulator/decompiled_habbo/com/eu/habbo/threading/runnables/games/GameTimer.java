/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.threading.runnables.games;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.interactions.games.InteractionGameTimer;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;

public class GameTimer
implements Runnable {
    private final InteractionGameTimer timer;

    public GameTimer(InteractionGameTimer timer) {
        this.timer = timer;
    }

    @Override
    public void run() {
        if (this.timer.getRoomId() == 0) {
            this.timer.setRunning(false);
            return;
        }
        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.timer.getRoomId());
        if (room == null || !this.timer.isRunning() || this.timer.isPaused()) {
            this.timer.setThreadActive(false);
            return;
        }
        this.timer.reduceTime();
        if (this.timer.getTimeNow() < 0) {
            this.timer.setTimeNow(0);
        }
        if (this.timer.getTimeNow() > 0) {
            this.timer.setThreadActive(true);
            Emulator.getThreading().run(this, 1000L);
        } else {
            this.timer.setThreadActive(false);
            this.timer.setTimeNow(0);
            this.timer.endGame(room);
            WiredHandler.handle(WiredTriggerType.GAME_ENDS, null, room, new Object[0]);
        }
        room.updateItem(this.timer);
    }
}

