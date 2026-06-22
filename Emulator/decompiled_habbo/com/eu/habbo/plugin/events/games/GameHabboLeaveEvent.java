/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.plugin.events.games;

import com.eu.habbo.habbohotel.games.Game;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.events.games.GameUserEvent;

public class GameHabboLeaveEvent
extends GameUserEvent {
    public GameHabboLeaveEvent(Game game, Habbo habbo) {
        super(game, habbo);
    }
}

