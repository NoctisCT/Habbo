/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.threading.runnables.freeze;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.games.freeze.FreezeGame;
import com.eu.habbo.habbohotel.games.freeze.FreezeGamePlayer;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.InteractionFreezeBlock;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.InteractionFreezeTile;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.threading.runnables.freeze.FreezeResetExplosionTiles;
import com.eu.habbo.threading.runnables.freeze.FreezeThrowSnowball;
import gnu.trove.set.hash.THashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class FreezeHandleSnowballExplosion
implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(FreezeHandleSnowballExplosion.class);
    private final FreezeThrowSnowball thrownData;

    public FreezeHandleSnowballExplosion(FreezeThrowSnowball thrownData) {
        this.thrownData = thrownData;
    }

    @Override
    public void run() {
        try {
            if (this.thrownData == null || this.thrownData.habbo.getHabboInfo().getGamePlayer() == null) {
                return;
            }
            FreezeGamePlayer player = (FreezeGamePlayer)this.thrownData.habbo.getHabboInfo().getGamePlayer();
            if (player == null) {
                return;
            }
            player.addSnowball();
            THashSet<RoomTile> tiles = new THashSet<RoomTile>();
            FreezeGame game = (FreezeGame)this.thrownData.room.getGame(FreezeGame.class);
            if (game == null) {
                return;
            }
            if (player.nextHorizontal) {
                tiles.addAll(game.affectedTilesByExplosion(this.thrownData.targetTile.getX(), this.thrownData.targetTile.getY(), this.thrownData.radius + 1));
            }
            if (player.nextDiagonal) {
                tiles.addAll(game.affectedTilesByExplosionDiagonal(this.thrownData.targetTile.getX(), this.thrownData.targetTile.getY(), this.thrownData.radius + 1));
                player.nextDiagonal = false;
            }
            THashSet<InteractionFreezeTile> freezeTiles = new THashSet<InteractionFreezeTile>();
            for (RoomTile roomTile : tiles) {
                THashSet<HabboItem> items = this.thrownData.room.getItemsAt(roomTile);
                for (HabboItem freezeTile : items) {
                    if (!(freezeTile instanceof InteractionFreezeTile) && !(freezeTile instanceof InteractionFreezeBlock)) continue;
                    int distance = 0;
                    distance = freezeTile.getX() != this.thrownData.targetTile.getX() && freezeTile.getY() != this.thrownData.targetTile.getY() ? Math.abs(freezeTile.getX() - this.thrownData.targetTile.getX()) : (int)Math.ceil(this.thrownData.room.getLayout().getTile(this.thrownData.targetTile.getX(), this.thrownData.targetTile.getY()).distance(roomTile));
                    if (freezeTile instanceof InteractionFreezeTile) {
                        freezeTile.setExtradata("11" + String.format("%03d", distance * 100));
                        freezeTiles.add((InteractionFreezeTile)freezeTile);
                        this.thrownData.room.updateItem(freezeTile);
                        THashSet<Habbo> habbos = new THashSet<Habbo>();
                        habbos.addAll(this.thrownData.room.getHabbosAt(freezeTile.getX(), freezeTile.getY()));
                        for (Habbo habbo : habbos) {
                            FreezeGamePlayer hPlayer;
                            if (habbo.getHabboInfo().getGamePlayer() == null || !(habbo.getHabboInfo().getGamePlayer() instanceof FreezeGamePlayer) || !(hPlayer = (FreezeGamePlayer)habbo.getHabboInfo().getGamePlayer()).canGetFrozen()) continue;
                            if (hPlayer.getTeamColor().equals((Object)player.getTeamColor())) {
                                player.addScore(-FreezeGame.FREEZE_LOOSE_POINTS);
                            } else {
                                player.addScore(FreezeGame.FREEZE_LOOSE_POINTS);
                            }
                            ((FreezeGamePlayer)habbo.getHabboInfo().getGamePlayer()).freeze();
                            if (this.thrownData.habbo == habbo) continue;
                            AchievementManager.progressAchievement(habbo, Emulator.getGameEnvironment().getAchievementManager().getAchievement("EsA"));
                        }
                        continue;
                    }
                    if (!(freezeTile instanceof InteractionFreezeBlock) || !freezeTile.getExtradata().equalsIgnoreCase("0")) continue;
                    game.explodeBox((InteractionFreezeBlock)freezeTile, distance * 100);
                    player.addScore(FreezeGame.DESTROY_BLOCK_POINTS);
                }
            }
            Emulator.getThreading().run(new FreezeResetExplosionTiles(freezeTiles, this.thrownData.room), 1000L);
        }
        catch (Exception e) {
            LOGGER.error("Caught exception", e);
        }
    }
}

