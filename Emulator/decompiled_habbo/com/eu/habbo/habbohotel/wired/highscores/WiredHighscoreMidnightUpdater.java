/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.wired.highscores;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredHighscore;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.highscores.WiredHighscoreManager;
import gnu.trove.set.hash.THashSet;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;

public class WiredHighscoreMidnightUpdater
implements Runnable {
    @Override
    public void run() {
        ArrayList<Room> rooms = Emulator.getGameEnvironment().getRoomManager().getActiveRooms();
        for (Room room : rooms) {
            if (room == null || room.getRoomSpecialTypes() == null) continue;
            THashSet<HabboItem> items = room.getRoomSpecialTypes().getItemsOfType(InteractionWiredHighscore.class);
            for (HabboItem item : items) {
                ((InteractionWiredHighscore)item).reloadData();
                room.updateItem(item);
            }
        }
        WiredHighscoreManager.midnightUpdater = Emulator.getThreading().run(new WiredHighscoreMidnightUpdater(), WiredHighscoreMidnightUpdater.getNextUpdaterRun());
    }

    public static int getNextUpdaterRun() {
        return Math.toIntExact(LocalDateTime.now().with(LocalTime.MIDNIGHT).plusDays(1L).plusSeconds(-1L).atZone(ZoneId.systemDefault()).toEpochSecond() - (long)Emulator.getIntUnixTimestamp()) + 5;
    }
}

