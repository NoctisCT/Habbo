/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.games.wired;

import com.eu.habbo.habbohotel.games.Game;
import com.eu.habbo.habbohotel.games.GamePlayer;
import com.eu.habbo.habbohotel.games.GameState;
import com.eu.habbo.habbohotel.games.GameTeam;
import com.eu.habbo.habbohotel.games.GameTeamColors;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;

public class WiredGame
extends Game {
    public GameState state = GameState.RUNNING;

    public WiredGame(Room room) {
        super(GameTeam.class, GamePlayer.class, room, false);
    }

    @Override
    public void initialise() {
        this.state = GameState.RUNNING;
        for (GameTeam team : this.teams.values()) {
            team.resetScores();
        }
    }

    @Override
    public void run() {
        this.state = GameState.RUNNING;
    }

    @Override
    public boolean addHabbo(Habbo habbo, GameTeamColors teamColor) {
        this.room.giveEffect(habbo, 39 + teamColor.type, -1);
        return super.addHabbo(habbo, teamColor);
    }

    @Override
    public void removeHabbo(Habbo habbo) {
        super.removeHabbo(habbo);
        this.room.giveEffect(habbo, 0, -1);
    }

    @Override
    public void stop() {
        this.state = GameState.RUNNING;
    }

    @Override
    public GameState getState() {
        return GameState.RUNNING;
    }
}

