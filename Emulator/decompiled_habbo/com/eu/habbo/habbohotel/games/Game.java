/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.games;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.games.GamePlayer;
import com.eu.habbo.habbohotel.games.GameState;
import com.eu.habbo.habbohotel.games.GameTeam;
import com.eu.habbo.habbohotel.games.GameTeamColors;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredHighscore;
import com.eu.habbo.habbohotel.items.interactions.games.InteractionGameTimer;
import com.eu.habbo.habbohotel.items.interactions.wired.extra.WiredBlob;
import com.eu.habbo.habbohotel.items.interactions.wired.triggers.WiredTriggerTeamLoses;
import com.eu.habbo.habbohotel.items.interactions.wired.triggers.WiredTriggerTeamWins;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.highscores.WiredHighscoreDataEntry;
import com.eu.habbo.messages.outgoing.guides.GuideSessionPartnerIsPlayingComposer;
import com.eu.habbo.plugin.Event;
import com.eu.habbo.plugin.events.games.GameHabboJoinEvent;
import com.eu.habbo.plugin.events.games.GameHabboLeaveEvent;
import com.eu.habbo.plugin.events.games.GameStartedEvent;
import com.eu.habbo.plugin.events.games.GameStoppedEvent;
import com.eu.habbo.threading.runnables.SaveScoreForTeam;
import gnu.trove.map.hash.THashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Game
implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Game.class);
    protected final THashMap<GameTeamColors, GameTeam> teams = new THashMap();
    protected final Room room;
    private final Class<? extends GameTeam> gameTeamClazz;
    private final Class<? extends GamePlayer> gamePlayerClazz;
    private final boolean countsAchievements;
    public boolean isRunning;
    public GameState state = GameState.IDLE;
    private int startTime;
    private int endTime;

    public Game(Class<? extends GameTeam> gameTeamClazz, Class<? extends GamePlayer> gamePlayerClazz, Room room, boolean countsAchievements) {
        this.gameTeamClazz = gameTeamClazz;
        this.gamePlayerClazz = gamePlayerClazz;
        this.room = room;
        this.countsAchievements = countsAchievements;
    }

    public abstract void initialise();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean addHabbo(Habbo habbo, GameTeamColors teamColor) {
        block8: {
            try {
                Object gameHabboJoinEvent;
                if (habbo == null) break block8;
                if (Emulator.getPluginManager().isRegistered(GameHabboJoinEvent.class, true)) {
                    gameHabboJoinEvent = new GameHabboJoinEvent(this, habbo);
                    Emulator.getPluginManager().fireEvent(gameHabboJoinEvent);
                    if (((Event)gameHabboJoinEvent).isCancelled()) {
                        return false;
                    }
                }
                gameHabboJoinEvent = this.teams;
                synchronized (gameHabboJoinEvent) {
                    GameTeam team = this.getTeam(teamColor);
                    if (team == null) {
                        team = this.gameTeamClazz.getDeclaredConstructor(GameTeamColors.class).newInstance(new Object[]{teamColor});
                        this.addTeam(team);
                    }
                    GamePlayer player = this.gamePlayerClazz.getDeclaredConstructor(Habbo.class, GameTeamColors.class).newInstance(new Object[]{habbo, teamColor});
                    team.addMember(player);
                    habbo.getHabboInfo().setCurrentGame(this.getClass());
                    habbo.getHabboInfo().setGamePlayer(player);
                }
                habbo.getClient().sendResponse(new GuideSessionPartnerIsPlayingComposer(true));
                return true;
            }
            catch (Exception e) {
                LOGGER.error("Caught exception", e);
            }
        }
        return false;
    }

    public void removeHabbo(Habbo habbo) {
        if (habbo != null) {
            GameTeam team;
            if (Emulator.getPluginManager().isRegistered(GameHabboLeaveEvent.class, true)) {
                GameHabboLeaveEvent gameHabboLeaveEvent = new GameHabboLeaveEvent(this, habbo);
                Emulator.getPluginManager().fireEvent(gameHabboLeaveEvent);
                if (gameHabboLeaveEvent.isCancelled()) {
                    return;
                }
            }
            if ((team = this.getTeamForHabbo(habbo)) != null && team.isMember(habbo)) {
                if (habbo.getHabboInfo().getGamePlayer() != null) {
                    team.removeMember(habbo.getHabboInfo().getGamePlayer());
                    if (habbo.getHabboInfo().getGamePlayer() != null) {
                        habbo.getHabboInfo().getGamePlayer().reset();
                    }
                }
                habbo.getHabboInfo().setCurrentGame(null);
                habbo.getHabboInfo().setGamePlayer(null);
                habbo.getClient().sendResponse(new GuideSessionPartnerIsPlayingComposer(false));
                if (this.countsAchievements && this.endTime > this.startTime) {
                    AchievementManager.progressAchievement(habbo, Emulator.getGameEnvironment().getAchievementManager().getAchievement("GamePlayed"));
                }
            }
        }
    }

    public void start() {
        this.isRunning = false;
        this.state = GameState.RUNNING;
        this.startTime = Emulator.getIntUnixTimestamp();
        if (Emulator.getPluginManager().isRegistered(GameStartedEvent.class, true)) {
            GameStartedEvent gameStartedEvent = new GameStartedEvent(this);
            Emulator.getPluginManager().fireEvent(gameStartedEvent);
        }
        for (HabboItem item : this.room.getRoomSpecialTypes().getItemsOfType(WiredBlob.class)) {
            ((WiredBlob)item).onGameStart(this.room);
        }
        for (GameTeam team : this.teams.values()) {
            team.resetScores();
        }
    }

    public void onEnd() {
        this.endTime = Emulator.getIntUnixTimestamp();
        this.saveScores();
        int totalPointsGained = this.teams.values().stream().mapToInt(GameTeam::getTotalScore).sum();
        Habbo roomOwner = Emulator.getGameEnvironment().getHabboManager().getHabbo(this.room.getOwnerId());
        if (roomOwner != null) {
            AchievementManager.progressAchievement(roomOwner, Emulator.getGameEnvironment().getAchievementManager().getAchievement("GameAuthorExperience"), totalPointsGained);
        }
        GameTeam winningTeam = null;
        if (totalPointsGained > 0) {
            for (GameTeam team : this.teams.values()) {
                if (winningTeam != null && team.getTotalScore() <= winningTeam.getTotalScore()) continue;
                winningTeam = team;
            }
        }
        if (winningTeam != null) {
            for (GamePlayer player : winningTeam.getMembers()) {
                WiredHandler.handleCustomTrigger(WiredTriggerTeamWins.class, player.getHabbo().getRoomUnit(), this.room, new Object[]{this});
                Habbo winner = player.getHabbo();
                if (winner == null) continue;
                AchievementManager.progressAchievement(roomOwner, Emulator.getGameEnvironment().getAchievementManager().getAchievement("GamePlayerExperience"));
            }
            if (winningTeam.getMembers().size() > 0) {
                for (HabboItem item : this.room.getRoomSpecialTypes().getItemsOfType(InteractionWiredHighscore.class)) {
                    Emulator.getGameEnvironment().getItemManager().getHighscoreManager().addHighscoreData(new WiredHighscoreDataEntry(item.getId(), winningTeam.getMembers().stream().map(m -> m.getHabbo().getHabboInfo().getId()).collect(Collectors.toList()), winningTeam.getTotalScore(), true, Emulator.getIntUnixTimestamp()));
                }
            }
            for (GameTeam team : this.teams.values()) {
                if (team == winningTeam) continue;
                for (GamePlayer player : team.getMembers()) {
                    WiredHandler.handleCustomTrigger(WiredTriggerTeamLoses.class, player.getHabbo().getRoomUnit(), this.room, new Object[]{this});
                }
                if (team.getMembers().size() <= 0 || team.getTotalScore() <= 0) continue;
                for (HabboItem item : this.room.getRoomSpecialTypes().getItemsOfType(InteractionWiredHighscore.class)) {
                    Emulator.getGameEnvironment().getItemManager().getHighscoreManager().addHighscoreData(new WiredHighscoreDataEntry(item.getId(), team.getMembers().stream().map(m -> m.getHabbo().getHabboInfo().getId()).collect(Collectors.toList()), team.getTotalScore(), false, Emulator.getIntUnixTimestamp()));
                }
            }
        }
        for (HabboItem item : this.room.getRoomSpecialTypes().getItemsOfType(InteractionWiredHighscore.class)) {
            ((InteractionWiredHighscore)item).reloadData();
            this.room.updateItem(item);
        }
        for (HabboItem item : this.room.getRoomSpecialTypes().getItemsOfType(WiredBlob.class)) {
            ((WiredBlob)item).onGameEnd(this.room);
        }
    }

    @Override
    public abstract void run();

    public void pause() {
        if (this.state.equals((Object)GameState.RUNNING)) {
            this.state = GameState.PAUSED;
        }
    }

    public void unpause() {
        if (this.state.equals((Object)GameState.PAUSED)) {
            this.state = GameState.RUNNING;
        }
    }

    public void stop() {
        this.state = GameState.IDLE;
        boolean gamesActive = false;
        for (HabboItem timer : this.room.getFloorItems()) {
            if (!(timer instanceof InteractionGameTimer) || !((InteractionGameTimer)timer).isRunning()) continue;
            gamesActive = true;
        }
        if (gamesActive) {
            return;
        }
        if (Emulator.getPluginManager().isRegistered(GameStoppedEvent.class, true)) {
            GameStoppedEvent gameStoppedEvent = new GameStoppedEvent(this);
            Emulator.getPluginManager().fireEvent(gameStoppedEvent);
        }
    }

    public void dispose() {
        for (GameTeam team : this.teams.values()) {
            team.clearMembers();
        }
        this.teams.clear();
        this.stop();
    }

    private void saveScores() {
        if (this.room == null) {
            return;
        }
        THashMap<GameTeamColors, GameTeam> teamsCopy = new THashMap<GameTeamColors, GameTeam>();
        teamsCopy.putAll(this.teams);
        for (Map.Entry teamEntry : teamsCopy.entrySet()) {
            Emulator.getThreading().run(new SaveScoreForTeam((GameTeam)teamEntry.getValue(), this));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public GameTeam getTeamForHabbo(Habbo habbo) {
        if (habbo != null) {
            THashMap<GameTeamColors, GameTeam> tHashMap = this.teams;
            synchronized (tHashMap) {
                for (GameTeam team : this.teams.values()) {
                    if (!team.isMember(habbo)) continue;
                    return team;
                }
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public GameTeam getTeam(GameTeamColors teamColor) {
        THashMap<GameTeamColors, GameTeam> tHashMap = this.teams;
        synchronized (tHashMap) {
            return this.teams.get((Object)teamColor);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addTeam(GameTeam team) {
        THashMap<GameTeamColors, GameTeam> tHashMap = this.teams;
        synchronized (tHashMap) {
            this.teams.put(team.teamColor, team);
        }
    }

    public Room getRoom() {
        return this.room;
    }

    public int getStartTime() {
        return this.startTime;
    }

    public Class<? extends GameTeam> getGameTeamClass() {
        return this.gameTeamClazz;
    }

    public Class<? extends GamePlayer> getGamePlayerClass() {
        return this.gamePlayerClazz;
    }

    public THashMap<GameTeamColors, GameTeam> getTeams() {
        return this.teams;
    }

    public boolean isCountsAchievements() {
        return this.countsAchievements;
    }

    public GameState getState() {
        return this.state;
    }
}

