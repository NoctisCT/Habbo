/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.games;

import com.eu.habbo.habbohotel.games.GamePlayer;
import com.eu.habbo.habbohotel.games.GameTeamColors;
import com.eu.habbo.habbohotel.users.Habbo;
import gnu.trove.set.hash.THashSet;

public class GameTeam {
    public final GameTeamColors teamColor;
    private final THashSet<GamePlayer> members;
    private int teamScore;

    public GameTeam(GameTeamColors teamColor) {
        this.teamColor = teamColor;
        this.members = new THashSet();
    }

    public void initialise() {
        for (GamePlayer player : this.members) {
            player.reset();
        }
        this.teamScore = 0;
    }

    public void reset() {
        this.members.clear();
    }

    public void addTeamScore(int teamScore) {
        this.teamScore += teamScore;
    }

    public int getTeamScore() {
        return this.teamScore;
    }

    public synchronized int getTotalScore() {
        int score = this.teamScore;
        for (GamePlayer player : this.members) {
            score += player.getScore();
        }
        return score;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addMember(GamePlayer gamePlayer) {
        THashSet<GamePlayer> tHashSet = this.members;
        synchronized (tHashSet) {
            this.members.add(gamePlayer);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeMember(GamePlayer gamePlayer) {
        THashSet<GamePlayer> tHashSet = this.members;
        synchronized (tHashSet) {
            this.members.remove(gamePlayer);
        }
    }

    public void clearMembers() {
        for (GamePlayer player : this.members) {
            if (player == null || player.getHabbo() == null) continue;
            if (player.getHabbo().getHabboInfo().getGamePlayer() != null) {
                player.getHabbo().getHabboInfo().getGamePlayer().reset();
            }
            player.getHabbo().getHabboInfo().setCurrentGame(null);
            player.getHabbo().getHabboInfo().setGamePlayer(null);
        }
        this.members.clear();
    }

    public void resetScores() {
        for (GamePlayer player : this.members) {
            if (player == null) continue;
            player.reset();
        }
        this.teamScore = 0;
    }

    public THashSet<GamePlayer> getMembers() {
        return this.members;
    }

    public boolean isMember(Habbo habbo) {
        for (GamePlayer p : this.members) {
            if (!p.getHabbo().equals(habbo)) continue;
            return true;
        }
        return false;
    }

    @Deprecated
    public GamePlayer getPlayerForHabbo(Habbo habbo) {
        for (GamePlayer p : this.members) {
            if (!p.getHabbo().equals(habbo)) continue;
            return p;
        }
        return null;
    }
}

