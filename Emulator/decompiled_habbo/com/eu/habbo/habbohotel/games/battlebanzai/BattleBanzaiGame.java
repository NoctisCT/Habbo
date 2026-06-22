/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.games.battlebanzai;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.games.Game;
import com.eu.habbo.habbohotel.games.GamePlayer;
import com.eu.habbo.habbohotel.games.GameState;
import com.eu.habbo.habbohotel.games.GameTeam;
import com.eu.habbo.habbohotel.games.GameTeamColors;
import com.eu.habbo.habbohotel.games.battlebanzai.BattleBanzaiGamePlayer;
import com.eu.habbo.habbohotel.games.battlebanzai.BattleBanzaiGameTeam;
import com.eu.habbo.habbohotel.items.interactions.games.InteractionGameTimer;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.InteractionBattleBanzaiSphere;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.InteractionBattleBanzaiTile;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.gates.InteractionBattleBanzaiGate;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.scoreboards.InteractionBattleBanzaiScoreboard;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUserAction;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserActionComposer;
import com.eu.habbo.threading.runnables.BattleBanzaiTilesFlicker;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BattleBanzaiGame
extends Game {
    private static final Logger LOGGER = LoggerFactory.getLogger(BattleBanzaiGame.class);
    public static final int effectId = 32;
    public static final int POINTS_HIJACK_TILE = Emulator.getConfig().getInt("hotel.banzai.points.tile.steal", 0);
    public static final int POINTS_FILL_TILE = Emulator.getConfig().getInt("hotel.banzai.points.tile.fill", 0);
    public static final int POINTS_LOCK_TILE = Emulator.getConfig().getInt("hotel.banzai.points.tile.lock", 1);
    private static final ThreadPoolExecutor executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(Emulator.getConfig().getInt("hotel.banzai.fill.threads", 2));
    private final THashMap<GameTeamColors, THashSet<HabboItem>> lockedTiles = new THashMap();
    private final THashMap<Integer, HabboItem> gameTiles = new THashMap();
    private int tileCount;
    private int countDown;
    private int countDown2;

    public BattleBanzaiGame(Room room) {
        super(BattleBanzaiGameTeam.class, BattleBanzaiGamePlayer.class, room, true);
        room.setAllowEffects(true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void initialise() {
        if (!this.state.equals((Object)GameState.IDLE)) {
            return;
        }
        this.countDown = 3;
        this.countDown2 = 2;
        this.resetMap();
        THashMap tHashMap = this.teams;
        synchronized (tHashMap) {
            for (GameTeam t : this.teams.values()) {
                t.initialise();
            }
        }
        for (HabboItem item : this.room.getRoomSpecialTypes().getItemsOfType(InteractionBattleBanzaiSphere.class)) {
            item.setExtradata("1");
            this.room.updateItemState(item);
        }
        this.start();
    }

    @Override
    public boolean addHabbo(Habbo habbo, GameTeamColors teamColor) {
        return super.addHabbo(habbo, teamColor);
    }

    @Override
    public void start() {
        if (!this.state.equals((Object)GameState.IDLE)) {
            return;
        }
        super.start();
        this.refreshGates();
        Emulator.getThreading().run(this, 0L);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * WARNING - void declaration
     */
    @Override
    public void run() {
        try {
            void var2_7;
            if (this.state.equals((Object)GameState.IDLE)) {
                return;
            }
            if (this.countDown > 0) {
                --this.countDown;
                if (this.countDown == 0) {
                    for (HabboItem habboItem : this.room.getRoomSpecialTypes().getItemsOfType(InteractionBattleBanzaiSphere.class)) {
                        habboItem.setExtradata("1");
                        this.room.updateItemState(habboItem);
                        if (this.countDown2 <= 0) continue;
                        --this.countDown2;
                        if (this.countDown2 != 0) continue;
                        habboItem.setExtradata("2");
                        this.room.updateItemState(habboItem);
                    }
                }
                if (this.countDown > 1) {
                    Emulator.getThreading().run(this, 500L);
                    return;
                }
            }
            Emulator.getThreading().run(this, 1000L);
            if (this.state.equals((Object)GameState.PAUSED)) {
                return;
            }
            int total = 0;
            THashMap<GameTeamColors, THashSet<HabboItem>> tHashMap = this.lockedTiles;
            synchronized (tHashMap) {
                for (Map.Entry<GameTeamColors, THashSet<HabboItem>> entry : this.lockedTiles.entrySet()) {
                    total += entry.getValue().size();
                }
            }
            Object var2_6 = null;
            Iterator<InteractionGameTimer> iterator = this.teams;
            synchronized (iterator) {
                for (Map.Entry entry : this.teams.entrySet()) {
                    if (var2_7 != null && var2_7.getTotalScore() >= ((GameTeam)entry.getValue()).getTotalScore()) continue;
                    GameTeam gameTeam = (GameTeam)entry.getValue();
                }
            }
            if (var2_7 != null) {
                for (HabboItem habboItem : this.room.getRoomSpecialTypes().getItemsOfType(InteractionBattleBanzaiSphere.class)) {
                    habboItem.setExtradata("" + (var2_7.teamColor.type + 2));
                    this.room.updateItemState(habboItem);
                }
            }
            if (total >= this.tileCount && this.tileCount != 0) {
                for (InteractionGameTimer interactionGameTimer : this.room.getRoomSpecialTypes().getGameTimers().values()) {
                    if (!interactionGameTimer.isRunning()) continue;
                    interactionGameTimer.endGame(this.room);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error("Caught exception", e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onEnd() {
        GameTeam winningTeam = null;
        boolean singleTeamGame = this.teams.values().stream().filter(t -> t.getMembers().size() > 0).count() == 1L;
        for (GameTeam team : this.teams.values()) {
            if (!singleTeamGame) {
                for (GamePlayer player : team.getMembers()) {
                    if (player.getScoreAchievementValue() <= 0) continue;
                    AchievementManager.progressAchievement(player.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("BattleBallPlayer"));
                }
            }
            if (winningTeam != null && team.getTotalScore() <= winningTeam.getTotalScore()) continue;
            winningTeam = team;
        }
        if (winningTeam != null) {
            if (!singleTeamGame) {
                for (GamePlayer player : winningTeam.getMembers()) {
                    if (player.getScoreAchievementValue() <= 0) continue;
                    this.room.sendComposer(new RoomUserActionComposer(player.getHabbo().getRoomUnit(), RoomUserAction.WAVE).compose());
                    AchievementManager.progressAchievement(player.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("BattleBallWinner"));
                }
            }
            for (HabboItem item : this.room.getRoomSpecialTypes().getItemsOfType(InteractionBattleBanzaiSphere.class)) {
                item.setExtradata("" + (6 + winningTeam.teamColor.type));
                this.room.updateItemState(item);
            }
            THashMap<GameTeamColors, THashSet<HabboItem>> tHashMap = this.lockedTiles;
            synchronized (tHashMap) {
                Emulator.getThreading().run(new BattleBanzaiTilesFlicker(this.lockedTiles.get((Object)winningTeam.teamColor), winningTeam.teamColor, this.room));
            }
        }
        super.onEnd();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void stop() {
        super.stop();
        this.refreshGates();
        for (HabboItem tile : this.gameTiles.values()) {
            if (!tile.getExtradata().equals("1")) continue;
            tile.setExtradata("0");
            this.room.updateItem(tile);
        }
        THashMap<GameTeamColors, THashSet<HabboItem>> tHashMap = this.lockedTiles;
        synchronized (tHashMap) {
            this.lockedTiles.clear();
        }
    }

    private synchronized void resetMap() {
        this.tileCount = 0;
        for (HabboItem item : this.room.getFloorItems()) {
            if (item instanceof InteractionBattleBanzaiTile) {
                item.setExtradata("1");
                this.room.updateItemState(item);
                ++this.tileCount;
                this.gameTiles.put(item.getId(), item);
            }
            if (!(item instanceof InteractionBattleBanzaiScoreboard)) continue;
            item.setExtradata("0");
            this.room.updateItemState(item);
        }
    }

    public void tileLocked(GameTeamColors teamColor, HabboItem item, Habbo habbo) {
        this.tileLocked(teamColor, item, habbo, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void tileLocked(GameTeamColors teamColor, HabboItem item, Habbo habbo, boolean doNotCheckFill) {
        THashMap<GameTeamColors, THashSet<HabboItem>> tHashMap = this.lockedTiles;
        synchronized (tHashMap) {
            if (item instanceof InteractionBattleBanzaiTile) {
                if (!this.lockedTiles.containsKey((Object)teamColor)) {
                    this.lockedTiles.put(teamColor, new THashSet());
                }
                this.lockedTiles.get((Object)teamColor).add(item);
            }
            if (habbo != null) {
                AchievementManager.progressAchievement(habbo, Emulator.getGameEnvironment().getAchievementManager().getAchievement("BattleBallTilesLocked"));
            }
            if (doNotCheckFill) {
                return;
            }
            short x = item.getX();
            short y = item.getY();
            ArrayList filledAreas = new ArrayList();
            THashSet lockedTiles = new THashSet(this.lockedTiles.get((Object)teamColor));
            executor.execute(() -> {
                filledAreas.add(this.floodFill(x, y - 1, lockedTiles, new ArrayList<RoomTile>(), teamColor));
                filledAreas.add(this.floodFill(x, y + 1, lockedTiles, new ArrayList<RoomTile>(), teamColor));
                filledAreas.add(this.floodFill(x - 1, y, lockedTiles, new ArrayList<RoomTile>(), teamColor));
                filledAreas.add(this.floodFill(x + 1, y, lockedTiles, new ArrayList<RoomTile>(), teamColor));
                Optional<List> largestAreaOfAll = filledAreas.stream().filter(Objects::nonNull).max(Comparator.comparing(List::size));
                if (largestAreaOfAll.isPresent()) {
                    for (RoomTile tile : largestAreaOfAll.get()) {
                        Optional<HabboItem> tileItem = this.gameTiles.values().stream().filter(i -> i.getX() == tile.x && i.getY() == tile.y && i instanceof InteractionBattleBanzaiTile).findAny();
                        tileItem.ifPresent(habboItem -> {
                            this.tileLocked(teamColor, (HabboItem)habboItem, habbo, true);
                            habboItem.setExtradata("" + (2 + teamColor.type * 3));
                            this.room.updateItem((HabboItem)habboItem);
                        });
                    }
                    this.refreshCounters(teamColor);
                    if (habbo != null) {
                        habbo.getHabboInfo().getGamePlayer().addScore(POINTS_LOCK_TILE * largestAreaOfAll.get().size());
                    }
                }
            });
        }
    }

    private List<RoomTile> floodFill(int x, int y, THashSet<HabboItem> lockedTiles, List<RoomTile> stack, GameTeamColors color) {
        if (this.isOutOfBounds(x, y) || this.isForeignLockedTile(x, y, color)) {
            return null;
        }
        RoomTile tile = this.room.getLayout().getTile((short)x, (short)y);
        if (this.hasLockedTileAtCoordinates(x, y, lockedTiles) || stack.contains(tile)) {
            return stack;
        }
        stack.add(tile);
        ArrayList<List<RoomTile>> result = new ArrayList<List<RoomTile>>();
        result.add(this.floodFill(x, y - 1, lockedTiles, stack, color));
        result.add(this.floodFill(x, y + 1, lockedTiles, stack, color));
        result.add(this.floodFill(x - 1, y, lockedTiles, stack, color));
        result.add(this.floodFill(x + 1, y, lockedTiles, stack, color));
        if (result.contains(null)) {
            return null;
        }
        Optional<List> biggestArea = result.stream().max(Comparator.comparing(List::size));
        return biggestArea.orElse(null);
    }

    private boolean hasLockedTileAtCoordinates(int x, int y, THashSet<HabboItem> lockedTiles) {
        for (HabboItem item : lockedTiles) {
            if (item.getX() != x || item.getY() != y) continue;
            return true;
        }
        return false;
    }

    private boolean isOutOfBounds(int x, int y) {
        for (HabboItem item : this.gameTiles.values()) {
            if (item.getX() != x || item.getY() != y) continue;
            return false;
        }
        return true;
    }

    private boolean isForeignLockedTile(int x, int y, GameTeamColors color) {
        for (Map.Entry<GameTeamColors, THashSet<HabboItem>> lockedTilesForColor : this.lockedTiles.entrySet()) {
            if (lockedTilesForColor.getKey() == color) continue;
            for (HabboItem item : lockedTilesForColor.getValue()) {
                if (item.getX() != x || item.getY() != y) continue;
                return true;
            }
        }
        return false;
    }

    public void refreshCounters() {
        for (GameTeam team : this.teams.values()) {
            if (team.getMembers().isEmpty()) continue;
            this.refreshCounters(team.teamColor);
        }
    }

    public void refreshCounters(GameTeamColors teamColors) {
        if (!this.teams.containsKey((Object)teamColors)) {
            return;
        }
        int totalScore = ((GameTeam)this.teams.get((Object)teamColors)).getTotalScore();
        THashMap<Integer, InteractionBattleBanzaiScoreboard> scoreBoards = this.room.getRoomSpecialTypes().getBattleBanzaiScoreboards(teamColors);
        for (InteractionBattleBanzaiScoreboard scoreboard : scoreBoards.values()) {
            int oldScore;
            if (scoreboard.getExtradata().isEmpty()) {
                scoreboard.setExtradata("0");
            }
            if ((oldScore = Integer.parseInt(scoreboard.getExtradata())) == totalScore) continue;
            scoreboard.setExtradata("" + totalScore);
            this.room.updateItemState(scoreboard);
        }
    }

    private void refreshGates() {
        Collection<InteractionBattleBanzaiGate> gates = this.room.getRoomSpecialTypes().getBattleBanzaiGates().values();
        THashSet<RoomTile> tilesToUpdate = new THashSet<RoomTile>(gates.size());
        for (HabboItem habboItem : gates) {
            tilesToUpdate.add(this.room.getLayout().getTile(habboItem.getX(), habboItem.getY()));
        }
        this.room.updateTiles(tilesToUpdate);
    }

    public void markTile(Habbo habbo, InteractionBattleBanzaiTile tile, int state) {
        if (!this.gameTiles.contains(tile.getId())) {
            return;
        }
        int check = state - habbo.getHabboInfo().getGamePlayer().getTeamColor().type * 3;
        if (check == 0 || check == 1) {
            if (++state % 3 == 2) {
                habbo.getHabboInfo().getGamePlayer().addScore(POINTS_LOCK_TILE);
                this.tileLocked(habbo.getHabboInfo().getGamePlayer().getTeamColor(), tile, habbo);
            } else {
                habbo.getHabboInfo().getGamePlayer().addScore(POINTS_FILL_TILE);
            }
        } else {
            state = habbo.getHabboInfo().getGamePlayer().getTeamColor().type * 3;
            habbo.getHabboInfo().getGamePlayer().addScore(POINTS_HIJACK_TILE);
        }
        this.refreshCounters(habbo.getHabboInfo().getGamePlayer().getTeamColor());
        tile.setExtradata("" + state);
        this.room.updateItem(tile);
    }
}

