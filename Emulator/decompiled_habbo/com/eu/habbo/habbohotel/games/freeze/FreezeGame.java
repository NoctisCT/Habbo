/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.games.freeze;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.games.Game;
import com.eu.habbo.habbohotel.games.GamePlayer;
import com.eu.habbo.habbohotel.games.GameState;
import com.eu.habbo.habbohotel.games.GameTeam;
import com.eu.habbo.habbohotel.games.GameTeamColors;
import com.eu.habbo.habbohotel.games.freeze.FreezeGamePlayer;
import com.eu.habbo.habbohotel.games.freeze.FreezeGameTeam;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.InteractionFreezeBlock;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.InteractionFreezeExitTile;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.InteractionFreezeTile;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.gates.InteractionFreezeGate;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.scoreboards.InteractionFreezeScoreboard;
import com.eu.habbo.habbohotel.items.interactions.wired.effects.WiredEffectTeleport;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomLayout;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUserAction;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserActionComposer;
import com.eu.habbo.plugin.EventHandler;
import com.eu.habbo.plugin.events.emulator.EmulatorConfigUpdatedEvent;
import com.eu.habbo.threading.runnables.freeze.FreezeClearEffects;
import com.eu.habbo.threading.runnables.freeze.FreezeThrowSnowball;
import gnu.trove.map.hash.THashMap;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.hash.THashSet;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FreezeGame
extends Game {
    private static final Logger LOGGER = LoggerFactory.getLogger(FreezeGame.class);
    public static final int effectId = 39;
    public static int POWER_UP_POINTS;
    public static int POWER_UP_CHANCE;
    public static int POWER_UP_PROTECT_TIME;
    public static int DESTROY_BLOCK_POINTS;
    public static int FREEZE_TIME;
    public static int FREEZE_LOOSE_SNOWBALL;
    public static int FREEZE_LOOSE_BOOST;
    public static int MAX_LIVES;
    public static int MAX_SNOWBALLS;
    public static int FREEZE_LOOSE_POINTS;
    public static boolean POWERUP_STACK;

    public FreezeGame(Room room) {
        super(FreezeGameTeam.class, FreezeGamePlayer.class, room, true);
        room.setAllowEffects(true);
    }

    @EventHandler
    public static void onConfigurationUpdated(EmulatorConfigUpdatedEvent event) {
        POWER_UP_POINTS = Emulator.getConfig().getInt("hotel.freeze.points.effect");
        POWER_UP_CHANCE = Emulator.getConfig().getInt("hotel.freeze.powerup.chance");
        POWER_UP_PROTECT_TIME = Emulator.getConfig().getInt("hotel.freeze.powerup.protection.time");
        DESTROY_BLOCK_POINTS = Emulator.getConfig().getInt("hotel.freeze.points.block");
        FREEZE_TIME = Emulator.getConfig().getInt("hotel.freeze.onfreeze.time.frozen");
        FREEZE_LOOSE_SNOWBALL = Emulator.getConfig().getInt("hotel.freeze.onfreeze.loose.snowballs");
        FREEZE_LOOSE_BOOST = Emulator.getConfig().getInt("hotel.freeze.onfreeze.loose.explosionboost");
        MAX_LIVES = Emulator.getConfig().getInt("hotel.freeze.powerup.max.lives");
        MAX_SNOWBALLS = Emulator.getConfig().getInt("hotel.freeze.powerup.max.snowballs");
        FREEZE_LOOSE_POINTS = Emulator.getConfig().getInt("hotel.freeze.points.freeze");
        POWERUP_STACK = Emulator.getConfig().getBoolean("hotel.freeze.powerup.protection.stack");
    }

    @Override
    public synchronized void initialise() {
        if (this.state == GameState.RUNNING) {
            return;
        }
        this.resetMap();
        for (GameTeam t : this.teams.values()) {
            t.initialise();
        }
        this.start();
    }

    synchronized void resetMap() {
        for (HabboItem item : this.room.getFloorItems()) {
            if (!(item instanceof InteractionFreezeBlock) && !(item instanceof InteractionFreezeScoreboard)) continue;
            item.setExtradata("0");
            this.room.updateItemState(item);
        }
    }

    public void throwBall(Habbo habbo, InteractionFreezeTile item) {
        if (!this.state.equals((Object)GameState.RUNNING) || !habbo.getHabboInfo().isInGame() || habbo.getHabboInfo().getCurrentGame() != this.getClass()) {
            return;
        }
        if (!item.getExtradata().equalsIgnoreCase("0") && !item.getExtradata().isEmpty()) {
            return;
        }
        if (RoomLayout.tilesAdjecent(habbo.getRoomUnit().getCurrentLocation(), this.room.getLayout().getTile(item.getX(), item.getY())) && ((FreezeGamePlayer)habbo.getHabboInfo().getGamePlayer()).canThrowSnowball()) {
            Emulator.getThreading().run(new FreezeThrowSnowball(habbo, item, this.room));
        }
    }

    public THashSet<RoomTile> affectedTilesByExplosion(short x, short y, int radius) {
        THashSet<RoomTile> tiles = new THashSet<RoomTile>();
        RoomTile t = this.room.getLayout().getTile(x, y);
        tiles.add(t);
        for (int rotatation = 0; rotatation < 8; rotatation += 2) {
            for (int j = 0; j < radius; ++j) {
                t = this.room.getLayout().getTileInFront(this.room.getLayout().getTile(x, y), rotatation, j);
                if (t == null || t.x < 0 || t.y < 0 || t.x >= this.room.getLayout().getMapSizeX() || t.y >= this.room.getLayout().getMapSizeY()) continue;
                tiles.add(t);
            }
        }
        return tiles;
    }

    public THashSet<RoomTile> affectedTilesByExplosionDiagonal(short x, short y, int radius) {
        THashSet<RoomTile> tiles = new THashSet<RoomTile>();
        for (int rotation = 1; rotation < 9; rotation += 2) {
            RoomTile t = this.room.getLayout().getTile(x, y);
            for (int j = 0; j < radius; ++j) {
                t = this.room.getLayout().getTileInFront(this.room.getLayout().getTile(x, y), rotation, j);
                if (t == null || t.x < 0 || t.y < 0 || t.x >= this.room.getLayout().getMapSizeX() || t.y >= this.room.getLayout().getMapSizeY()) continue;
                tiles.add(t);
            }
        }
        return tiles;
    }

    public synchronized void explodeBox(InteractionFreezeBlock block, int delay) {
        int powerUp = 0;
        if (Emulator.getRandom().nextInt(100) + 1 <= POWER_UP_CHANCE) {
            powerUp += Emulator.getRandom().nextInt(6) + 1;
        }
        block.setExtradata(powerUp + 1 + String.format("%3d", delay));
        this.room.updateItemState(block);
    }

    public synchronized void givePowerUp(FreezeGamePlayer player, int powerUpId) {
        player.addScore(POWER_UP_POINTS);
        switch (powerUpId) {
            case 2: {
                player.increaseExplosion();
                break;
            }
            case 3: {
                player.addSnowball();
                break;
            }
            case 4: {
                player.nextDiagonal = true;
                break;
            }
            case 5: {
                player.nextHorizontal = true;
                player.nextDiagonal = true;
                player.tempMassiveExplosion = true;
                break;
            }
            case 6: {
                player.addLife();
                break;
            }
            case 7: {
                player.addProtection();
            }
        }
    }

    public synchronized void playerDies(GamePlayer player) {
        Emulator.getThreading().run(new FreezeClearEffects(player.getHabbo()), 1000L);
        if (this.room.getRoomSpecialTypes().hasFreezeExitTile()) {
            InteractionFreezeExitTile tile = this.room.getRoomSpecialTypes().getRandomFreezeExitTile();
            tile.setExtradata("1");
            this.room.updateItemState(tile);
            this.room.teleportHabboToItem(player.getHabbo(), tile);
        }
        this.removeHabbo(player.getHabbo());
    }

    @Override
    public void start() {
        if (this.state != GameState.IDLE) {
            return;
        }
        super.start();
        if (this.room.getRoomSpecialTypes().hasFreezeExitTile()) {
            for (Habbo habbo : this.room.getHabbos()) {
                if (this.getTeamForHabbo(habbo) != null) continue;
                for (HabboItem item : this.room.getItemsAt(habbo.getRoomUnit().getCurrentLocation())) {
                    if (!(item instanceof InteractionFreezeTile)) continue;
                    InteractionFreezeExitTile exitTile = this.room.getRoomSpecialTypes().getRandomFreezeExitTile();
                    WiredEffectTeleport.teleportUnitToTile(habbo.getRoomUnit(), this.room.getLayout().getTile(exitTile.getX(), exitTile.getY()));
                }
            }
        }
        this.refreshGates();
        this.setFreezeTileState("1");
        this.run();
    }

    @Override
    public synchronized void run() {
        try {
            if (this.state.equals((Object)GameState.IDLE)) {
                return;
            }
            Emulator.getThreading().run(this, 1000L);
            if (this.state.equals((Object)GameState.PAUSED)) {
                return;
            }
            for (GameTeam team : this.teams.values()) {
                for (GamePlayer player : team.getMembers()) {
                    ((FreezeGamePlayer)player).cycle();
                }
                int totalScore = team.getTotalScore();
                THashMap<Integer, InteractionFreezeScoreboard> scoreBoards = this.room.getRoomSpecialTypes().getFreezeScoreboards(team.teamColor);
                for (InteractionFreezeScoreboard scoreboard : scoreBoards.values()) {
                    int oldScore;
                    if (scoreboard.getExtradata().isEmpty()) {
                        scoreboard.setExtradata("0");
                    }
                    if ((oldScore = Integer.parseInt(scoreboard.getExtradata())) == totalScore) continue;
                    scoreboard.setExtradata("" + totalScore);
                    this.room.updateItemState(scoreboard);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error("Caught exception", e);
        }
    }

    @Override
    public void stop() {
        super.stop();
        GameTeam winningTeam = null;
        for (GameTeam team : this.teams.values()) {
            if (winningTeam != null && team.getTotalScore() <= winningTeam.getTotalScore()) continue;
            winningTeam = team;
        }
        for (GameTeam team : this.teams.values()) {
            THashSet<GamePlayer> tHashSet = new THashSet<GamePlayer>();
            tHashSet.addAll(team.getMembers());
            for (GamePlayer p : tHashSet) {
                if (p.getScoreAchievementValue() <= 0) continue;
                if (team.equals(winningTeam)) {
                    AchievementManager.progressAchievement(p.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("FreezeWinner"), p.getScoreAchievementValue());
                    this.room.sendComposer(new RoomUserActionComposer(p.getHabbo().getRoomUnit(), RoomUserAction.WAVE).compose());
                }
                AchievementManager.progressAchievement(p.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("FreezePlayer"));
            }
        }
        HashMap<GameTeamColors, Integer> teamMemberCount = new HashMap<GameTeamColors, Integer>();
        for (Map.Entry entry : this.teams.entrySet()) {
            teamMemberCount.put((GameTeamColors)((Object)entry.getKey()), ((GameTeam)entry.getValue()).getMembers().size());
        }
        for (Map.Entry<Object, Object> entry : this.room.getRoomSpecialTypes().getFreezeGates().entrySet()) {
            if (!teamMemberCount.containsKey((Object)((InteractionFreezeGate)entry.getValue()).teamColor)) continue;
            int amount = Math.min((Integer)teamMemberCount.get((Object)((InteractionFreezeGate)entry.getValue()).teamColor), 5);
            ((InteractionFreezeGate)entry.getValue()).setExtradata("" + amount);
            teamMemberCount.put(((InteractionFreezeGate)entry.getValue()).teamColor, (Integer)teamMemberCount.get((Object)((InteractionFreezeGate)entry.getValue()).teamColor) - amount);
            this.room.updateItemState((HabboItem)entry.getValue());
        }
        this.refreshGates();
        this.setFreezeTileState("0");
    }

    public void setFreezeTileState(final String state) {
        this.room.getRoomSpecialTypes().getFreezeExitTiles().forEachValue(new TObjectProcedure<InteractionFreezeExitTile>(){
            final /* synthetic */ FreezeGame this$0;
            {
                this.this$0 = this$0;
            }

            @Override
            public boolean execute(InteractionFreezeExitTile object) {
                object.setExtradata(state);
                this.this$0.room.updateItemState(object);
                return true;
            }
        });
    }

    private void refreshGates() {
        THashSet<RoomTile> tilesToUpdate = new THashSet<RoomTile>();
        for (HabboItem habboItem : this.room.getRoomSpecialTypes().getFreezeGates().values()) {
            tilesToUpdate.add(this.room.getLayout().getTile(habboItem.getX(), habboItem.getY()));
        }
        this.room.updateTiles(tilesToUpdate);
    }
}

