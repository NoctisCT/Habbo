/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.rooms;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.games.GameTeamColors;
import com.eu.habbo.habbohotel.items.ICycleable;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionRoller;
import com.eu.habbo.habbohotel.items.interactions.InteractionTent;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredCondition;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredExtra;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredTrigger;
import com.eu.habbo.habbohotel.items.interactions.games.InteractionGameGate;
import com.eu.habbo.habbohotel.items.interactions.games.InteractionGameScoreboard;
import com.eu.habbo.habbohotel.items.interactions.games.InteractionGameTimer;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.InteractionBattleBanzaiTeleporter;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.gates.InteractionBattleBanzaiGate;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.scoreboards.InteractionBattleBanzaiScoreboard;
import com.eu.habbo.habbohotel.items.interactions.games.football.scoreboards.InteractionFootballScoreboard;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.InteractionFreezeExitTile;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.gates.InteractionFreezeGate;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.scoreboards.InteractionFreezeScoreboard;
import com.eu.habbo.habbohotel.items.interactions.pets.InteractionNest;
import com.eu.habbo.habbohotel.items.interactions.pets.InteractionPetDrink;
import com.eu.habbo.habbohotel.items.interactions.pets.InteractionPetFood;
import com.eu.habbo.habbohotel.items.interactions.pets.InteractionPetToy;
import com.eu.habbo.habbohotel.rooms.RoomLayout;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredConditionType;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class RoomSpecialTypes {
    private final THashMap<Integer, InteractionBattleBanzaiTeleporter> banzaiTeleporters = new THashMap(0);
    private final THashMap<Integer, InteractionNest> nests = new THashMap(0);
    private final THashMap<Integer, InteractionPetDrink> petDrinks = new THashMap(0);
    private final THashMap<Integer, InteractionPetFood> petFoods = new THashMap(0);
    private final THashMap<Integer, InteractionPetToy> petToys = new THashMap(0);
    private final THashMap<Integer, InteractionRoller> rollers = new THashMap(0);
    private final THashMap<WiredTriggerType, THashSet<InteractionWiredTrigger>> wiredTriggers = new THashMap(0);
    private final THashMap<WiredEffectType, THashSet<InteractionWiredEffect>> wiredEffects = new THashMap(0);
    private final THashMap<WiredConditionType, THashSet<InteractionWiredCondition>> wiredConditions = new THashMap(0);
    private final THashMap<Integer, InteractionWiredExtra> wiredExtras = new THashMap(0);
    private final THashMap<Integer, InteractionGameScoreboard> gameScoreboards = new THashMap(0);
    private final THashMap<Integer, InteractionGameGate> gameGates = new THashMap(0);
    private final THashMap<Integer, InteractionGameTimer> gameTimers = new THashMap(0);
    private final THashMap<Integer, InteractionFreezeExitTile> freezeExitTile = new THashMap(0);
    private final THashMap<Integer, HabboItem> undefined = new THashMap(0);
    private final THashSet<ICycleable> cycleTasks = new THashSet(0);

    public InteractionBattleBanzaiTeleporter getBanzaiTeleporter(int itemId) {
        return this.banzaiTeleporters.get(itemId);
    }

    public void addBanzaiTeleporter(InteractionBattleBanzaiTeleporter item) {
        this.banzaiTeleporters.put(item.getId(), item);
    }

    public void removeBanzaiTeleporter(InteractionBattleBanzaiTeleporter item) {
        this.banzaiTeleporters.remove(item.getId());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public THashSet<InteractionBattleBanzaiTeleporter> getBanzaiTeleporters() {
        THashMap<Integer, InteractionBattleBanzaiTeleporter> tHashMap = this.banzaiTeleporters;
        synchronized (tHashMap) {
            THashSet<InteractionBattleBanzaiTeleporter> battleBanzaiTeleporters = new THashSet<InteractionBattleBanzaiTeleporter>();
            battleBanzaiTeleporters.addAll(this.banzaiTeleporters.values());
            return battleBanzaiTeleporters;
        }
    }

    public InteractionBattleBanzaiTeleporter getRandomTeleporter(Item baseItem, InteractionBattleBanzaiTeleporter exclude) {
        ArrayList<InteractionBattleBanzaiTeleporter> teleporterList = new ArrayList<InteractionBattleBanzaiTeleporter>();
        for (InteractionBattleBanzaiTeleporter teleporter : this.banzaiTeleporters.values()) {
            if (baseItem != null && teleporter.getBaseItem() != baseItem) continue;
            teleporterList.add(teleporter);
        }
        teleporterList.remove(exclude);
        if (!teleporterList.isEmpty()) {
            Collections.shuffle(teleporterList);
            return (InteractionBattleBanzaiTeleporter)teleporterList.get(0);
        }
        return null;
    }

    public InteractionNest getNest(int itemId) {
        return this.nests.get(itemId);
    }

    public void addNest(InteractionNest item) {
        this.nests.put(item.getId(), item);
    }

    public void removeNest(InteractionNest item) {
        this.nests.remove(item.getId());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public THashSet<InteractionNest> getNests() {
        THashMap<Integer, InteractionNest> tHashMap = this.nests;
        synchronized (tHashMap) {
            THashSet<InteractionNest> nests = new THashSet<InteractionNest>();
            nests.addAll(this.nests.values());
            return nests;
        }
    }

    public InteractionPetDrink getPetDrink(int itemId) {
        return this.petDrinks.get(itemId);
    }

    public void addPetDrink(InteractionPetDrink item) {
        this.petDrinks.put(item.getId(), item);
    }

    public void removePetDrink(InteractionPetDrink item) {
        this.petDrinks.remove(item.getId());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public THashSet<InteractionPetDrink> getPetDrinks() {
        THashMap<Integer, InteractionPetDrink> tHashMap = this.petDrinks;
        synchronized (tHashMap) {
            THashSet<InteractionPetDrink> petDrinks = new THashSet<InteractionPetDrink>();
            petDrinks.addAll(this.petDrinks.values());
            return petDrinks;
        }
    }

    public InteractionPetFood getPetFood(int itemId) {
        return this.petFoods.get(itemId);
    }

    public void addPetFood(InteractionPetFood item) {
        this.petFoods.put(item.getId(), item);
    }

    public void removePetFood(InteractionPetFood petFood) {
        this.petFoods.remove(petFood.getId());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public THashSet<InteractionPetFood> getPetFoods() {
        THashMap<Integer, InteractionPetFood> tHashMap = this.petFoods;
        synchronized (tHashMap) {
            THashSet<InteractionPetFood> petFoods = new THashSet<InteractionPetFood>();
            petFoods.addAll(this.petFoods.values());
            return petFoods;
        }
    }

    public InteractionPetToy getPetToy(int itemId) {
        return this.petToys.get(itemId);
    }

    public void addPetToy(InteractionPetToy item) {
        this.petToys.put(item.getId(), item);
    }

    public void removePetToy(InteractionPetToy petToy) {
        this.petToys.remove(petToy.getId());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public THashSet<InteractionPetToy> getPetToys() {
        THashMap<Integer, InteractionPetToy> tHashMap = this.petToys;
        synchronized (tHashMap) {
            THashSet<InteractionPetToy> petToys = new THashSet<InteractionPetToy>();
            petToys.addAll(this.petToys.values());
            return petToys;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public InteractionRoller getRoller(int itemId) {
        THashMap<Integer, InteractionRoller> tHashMap = this.rollers;
        synchronized (tHashMap) {
            return this.rollers.get(itemId);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addRoller(InteractionRoller item) {
        THashMap<Integer, InteractionRoller> tHashMap = this.rollers;
        synchronized (tHashMap) {
            this.rollers.put(item.getId(), item);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeRoller(InteractionRoller roller) {
        THashMap<Integer, InteractionRoller> tHashMap = this.rollers;
        synchronized (tHashMap) {
            this.rollers.remove(roller.getId());
        }
    }

    public THashMap<Integer, InteractionRoller> getRollers() {
        return this.rollers;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public InteractionWiredTrigger getTrigger(int itemId) {
        THashMap<WiredTriggerType, THashSet<InteractionWiredTrigger>> tHashMap = this.wiredTriggers;
        synchronized (tHashMap) {
            for (Map.Entry<WiredTriggerType, THashSet<InteractionWiredTrigger>> map : this.wiredTriggers.entrySet()) {
                for (InteractionWiredTrigger trigger : map.getValue()) {
                    if (trigger.getId() != itemId) continue;
                    return trigger;
                }
            }
            return null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public THashSet<InteractionWiredTrigger> getTriggers() {
        THashMap<WiredTriggerType, THashSet<InteractionWiredTrigger>> tHashMap = this.wiredTriggers;
        synchronized (tHashMap) {
            THashSet<InteractionWiredTrigger> triggers = new THashSet<InteractionWiredTrigger>();
            for (Map.Entry<WiredTriggerType, THashSet<InteractionWiredTrigger>> map : this.wiredTriggers.entrySet()) {
                triggers.addAll((Collection<InteractionWiredTrigger>)map.getValue());
            }
            return triggers;
        }
    }

    public THashSet<InteractionWiredTrigger> getTriggers(WiredTriggerType type) {
        return this.wiredTriggers.get((Object)type);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public THashSet<InteractionWiredTrigger> getTriggers(int x, int y) {
        THashMap<WiredTriggerType, THashSet<InteractionWiredTrigger>> tHashMap = this.wiredTriggers;
        synchronized (tHashMap) {
            THashSet<InteractionWiredTrigger> triggers = new THashSet<InteractionWiredTrigger>();
            for (Map.Entry<WiredTriggerType, THashSet<InteractionWiredTrigger>> map : this.wiredTriggers.entrySet()) {
                for (InteractionWiredTrigger trigger : map.getValue()) {
                    if (trigger.getX() != x || trigger.getY() != y) continue;
                    triggers.add(trigger);
                }
            }
            return triggers;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addTrigger(InteractionWiredTrigger trigger) {
        THashMap<WiredTriggerType, THashSet<InteractionWiredTrigger>> tHashMap = this.wiredTriggers;
        synchronized (tHashMap) {
            if (!this.wiredTriggers.containsKey((Object)trigger.getType())) {
                this.wiredTriggers.put(trigger.getType(), new THashSet());
            }
            this.wiredTriggers.get((Object)trigger.getType()).add(trigger);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeTrigger(InteractionWiredTrigger trigger) {
        THashMap<WiredTriggerType, THashSet<InteractionWiredTrigger>> tHashMap = this.wiredTriggers;
        synchronized (tHashMap) {
            this.wiredTriggers.get((Object)trigger.getType()).remove(trigger);
            if (this.wiredTriggers.get((Object)trigger.getType()).isEmpty()) {
                this.wiredTriggers.remove((Object)trigger.getType());
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public InteractionWiredEffect getEffect(int itemId) {
        THashMap<WiredEffectType, THashSet<InteractionWiredEffect>> tHashMap = this.wiredEffects;
        synchronized (tHashMap) {
            for (Map.Entry<WiredEffectType, THashSet<InteractionWiredEffect>> map : this.wiredEffects.entrySet()) {
                for (InteractionWiredEffect effect : map.getValue()) {
                    if (effect.getId() != itemId) continue;
                    return effect;
                }
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public THashSet<InteractionWiredEffect> getEffects() {
        THashMap<WiredEffectType, THashSet<InteractionWiredEffect>> tHashMap = this.wiredEffects;
        synchronized (tHashMap) {
            THashSet<InteractionWiredEffect> effects = new THashSet<InteractionWiredEffect>();
            for (Map.Entry<WiredEffectType, THashSet<InteractionWiredEffect>> map : this.wiredEffects.entrySet()) {
                effects.addAll((Collection<InteractionWiredEffect>)map.getValue());
            }
            return effects;
        }
    }

    public THashSet<InteractionWiredEffect> getEffects(WiredEffectType type) {
        return this.wiredEffects.get((Object)type);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public THashSet<InteractionWiredEffect> getEffects(int x, int y) {
        THashMap<WiredEffectType, THashSet<InteractionWiredEffect>> tHashMap = this.wiredEffects;
        synchronized (tHashMap) {
            THashSet<InteractionWiredEffect> effects = new THashSet<InteractionWiredEffect>();
            for (Map.Entry<WiredEffectType, THashSet<InteractionWiredEffect>> map : this.wiredEffects.entrySet()) {
                for (InteractionWiredEffect effect : map.getValue()) {
                    if (effect.getX() != x || effect.getY() != y) continue;
                    effects.add(effect);
                }
            }
            return effects;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addEffect(InteractionWiredEffect effect) {
        THashMap<WiredEffectType, THashSet<InteractionWiredEffect>> tHashMap = this.wiredEffects;
        synchronized (tHashMap) {
            if (!this.wiredEffects.containsKey((Object)effect.getType())) {
                this.wiredEffects.put(effect.getType(), new THashSet());
            }
            this.wiredEffects.get((Object)effect.getType()).add(effect);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeEffect(InteractionWiredEffect effect) {
        THashMap<WiredEffectType, THashSet<InteractionWiredEffect>> tHashMap = this.wiredEffects;
        synchronized (tHashMap) {
            this.wiredEffects.get((Object)effect.getType()).remove(effect);
            if (this.wiredEffects.get((Object)effect.getType()).isEmpty()) {
                this.wiredEffects.remove((Object)effect.getType());
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public InteractionWiredCondition getCondition(int itemId) {
        THashMap<WiredConditionType, THashSet<InteractionWiredCondition>> tHashMap = this.wiredConditions;
        synchronized (tHashMap) {
            for (Map.Entry<WiredConditionType, THashSet<InteractionWiredCondition>> map : this.wiredConditions.entrySet()) {
                for (InteractionWiredCondition condition : map.getValue()) {
                    if (condition.getId() != itemId) continue;
                    return condition;
                }
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public THashSet<InteractionWiredCondition> getConditions() {
        THashMap<WiredConditionType, THashSet<InteractionWiredCondition>> tHashMap = this.wiredConditions;
        synchronized (tHashMap) {
            THashSet<InteractionWiredCondition> conditions = new THashSet<InteractionWiredCondition>();
            for (Map.Entry<WiredConditionType, THashSet<InteractionWiredCondition>> map : this.wiredConditions.entrySet()) {
                conditions.addAll((Collection<InteractionWiredCondition>)map.getValue());
            }
            return conditions;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public THashSet<InteractionWiredCondition> getConditions(WiredConditionType type) {
        THashMap<WiredConditionType, THashSet<InteractionWiredCondition>> tHashMap = this.wiredConditions;
        synchronized (tHashMap) {
            return this.wiredConditions.get((Object)type);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public THashSet<InteractionWiredCondition> getConditions(int x, int y) {
        THashMap<WiredConditionType, THashSet<InteractionWiredCondition>> tHashMap = this.wiredConditions;
        synchronized (tHashMap) {
            THashSet<InteractionWiredCondition> conditions = new THashSet<InteractionWiredCondition>();
            for (Map.Entry<WiredConditionType, THashSet<InteractionWiredCondition>> map : this.wiredConditions.entrySet()) {
                for (InteractionWiredCondition condition : map.getValue()) {
                    if (condition.getX() != x || condition.getY() != y) continue;
                    conditions.add(condition);
                }
            }
            return conditions;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addCondition(InteractionWiredCondition condition) {
        THashMap<WiredConditionType, THashSet<InteractionWiredCondition>> tHashMap = this.wiredConditions;
        synchronized (tHashMap) {
            if (!this.wiredConditions.containsKey((Object)condition.getType())) {
                this.wiredConditions.put(condition.getType(), new THashSet());
            }
            this.wiredConditions.get((Object)condition.getType()).add(condition);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeCondition(InteractionWiredCondition condition) {
        THashMap<WiredConditionType, THashSet<InteractionWiredCondition>> tHashMap = this.wiredConditions;
        synchronized (tHashMap) {
            this.wiredConditions.get((Object)condition.getType()).remove(condition);
            if (this.wiredConditions.get((Object)condition.getType()).isEmpty()) {
                this.wiredConditions.remove((Object)condition.getType());
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public THashSet<InteractionWiredExtra> getExtras() {
        THashMap<Integer, InteractionWiredExtra> tHashMap = this.wiredExtras;
        synchronized (tHashMap) {
            THashSet<InteractionWiredExtra> conditions = new THashSet<InteractionWiredExtra>();
            for (Map.Entry<Integer, InteractionWiredExtra> map : this.wiredExtras.entrySet()) {
                conditions.add(map.getValue());
            }
            return conditions;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public THashSet<InteractionWiredExtra> getExtras(int x, int y) {
        THashMap<Integer, InteractionWiredExtra> tHashMap = this.wiredExtras;
        synchronized (tHashMap) {
            THashSet<InteractionWiredExtra> extras = new THashSet<InteractionWiredExtra>();
            for (Map.Entry<Integer, InteractionWiredExtra> map : this.wiredExtras.entrySet()) {
                if (map.getValue().getX() != x || map.getValue().getY() != y) continue;
                extras.add(map.getValue());
            }
            return extras;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addExtra(InteractionWiredExtra extra) {
        THashMap<Integer, InteractionWiredExtra> tHashMap = this.wiredExtras;
        synchronized (tHashMap) {
            this.wiredExtras.put(extra.getId(), extra);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeExtra(InteractionWiredExtra extra) {
        THashMap<Integer, InteractionWiredExtra> tHashMap = this.wiredExtras;
        synchronized (tHashMap) {
            this.wiredExtras.remove(extra.getId());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean hasExtraType(short x, short y, Class<? extends InteractionWiredExtra> type) {
        THashMap<Integer, InteractionWiredExtra> tHashMap = this.wiredExtras;
        synchronized (tHashMap) {
            for (Map.Entry<Integer, InteractionWiredExtra> map : this.wiredExtras.entrySet()) {
                if (map.getValue().getX() != x || map.getValue().getY() != y || !map.getValue().getClass().isAssignableFrom(type)) continue;
                return true;
            }
        }
        return false;
    }

    public InteractionGameScoreboard getGameScorebord(int itemId) {
        return this.gameScoreboards.get(itemId);
    }

    public void addGameScoreboard(InteractionGameScoreboard scoreboard) {
        this.gameScoreboards.put(scoreboard.getId(), scoreboard);
    }

    public void removeScoreboard(InteractionGameScoreboard scoreboard) {
        this.gameScoreboards.remove(scoreboard.getId());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public THashMap<Integer, InteractionFreezeScoreboard> getFreezeScoreboards() {
        THashMap<Integer, InteractionGameScoreboard> tHashMap = this.gameScoreboards;
        synchronized (tHashMap) {
            THashMap<Integer, InteractionFreezeScoreboard> boards = new THashMap<Integer, InteractionFreezeScoreboard>();
            for (Map.Entry<Integer, InteractionGameScoreboard> set : this.gameScoreboards.entrySet()) {
                if (!(set.getValue() instanceof InteractionFreezeScoreboard)) continue;
                boards.put(set.getValue().getId(), (InteractionFreezeScoreboard)set.getValue());
            }
            return boards;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public THashMap<Integer, InteractionFreezeScoreboard> getFreezeScoreboards(GameTeamColors teamColor) {
        THashMap<Integer, InteractionGameScoreboard> tHashMap = this.gameScoreboards;
        synchronized (tHashMap) {
            THashMap<Integer, InteractionFreezeScoreboard> boards = new THashMap<Integer, InteractionFreezeScoreboard>();
            for (Map.Entry<Integer, InteractionGameScoreboard> set : this.gameScoreboards.entrySet()) {
                if (!(set.getValue() instanceof InteractionFreezeScoreboard) || !((InteractionFreezeScoreboard)set.getValue()).teamColor.equals((Object)teamColor)) continue;
                boards.put(set.getValue().getId(), (InteractionFreezeScoreboard)set.getValue());
            }
            return boards;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public THashMap<Integer, InteractionBattleBanzaiScoreboard> getBattleBanzaiScoreboards() {
        THashMap<Integer, InteractionGameScoreboard> tHashMap = this.gameScoreboards;
        synchronized (tHashMap) {
            THashMap<Integer, InteractionBattleBanzaiScoreboard> boards = new THashMap<Integer, InteractionBattleBanzaiScoreboard>();
            for (Map.Entry<Integer, InteractionGameScoreboard> set : this.gameScoreboards.entrySet()) {
                if (!(set.getValue() instanceof InteractionBattleBanzaiScoreboard)) continue;
                boards.put(set.getValue().getId(), (InteractionBattleBanzaiScoreboard)set.getValue());
            }
            return boards;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public THashMap<Integer, InteractionBattleBanzaiScoreboard> getBattleBanzaiScoreboards(GameTeamColors teamColor) {
        THashMap<Integer, InteractionGameScoreboard> tHashMap = this.gameScoreboards;
        synchronized (tHashMap) {
            THashMap<Integer, InteractionBattleBanzaiScoreboard> boards = new THashMap<Integer, InteractionBattleBanzaiScoreboard>();
            for (Map.Entry<Integer, InteractionGameScoreboard> set : this.gameScoreboards.entrySet()) {
                if (!(set.getValue() instanceof InteractionBattleBanzaiScoreboard) || !((InteractionBattleBanzaiScoreboard)set.getValue()).teamColor.equals((Object)teamColor)) continue;
                boards.put(set.getValue().getId(), (InteractionBattleBanzaiScoreboard)set.getValue());
            }
            return boards;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public THashMap<Integer, InteractionFootballScoreboard> getFootballScoreboards() {
        THashMap<Integer, InteractionGameScoreboard> tHashMap = this.gameScoreboards;
        synchronized (tHashMap) {
            THashMap<Integer, InteractionFootballScoreboard> boards = new THashMap<Integer, InteractionFootballScoreboard>();
            for (Map.Entry<Integer, InteractionGameScoreboard> set : this.gameScoreboards.entrySet()) {
                if (!(set.getValue() instanceof InteractionFootballScoreboard)) continue;
                boards.put(set.getValue().getId(), (InteractionFootballScoreboard)set.getValue());
            }
            return boards;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public THashMap<Integer, InteractionFootballScoreboard> getFootballScoreboards(GameTeamColors teamColor) {
        THashMap<Integer, InteractionGameScoreboard> tHashMap = this.gameScoreboards;
        synchronized (tHashMap) {
            THashMap<Integer, InteractionFootballScoreboard> boards = new THashMap<Integer, InteractionFootballScoreboard>();
            for (Map.Entry<Integer, InteractionGameScoreboard> set : this.gameScoreboards.entrySet()) {
                if (!(set.getValue() instanceof InteractionFootballScoreboard) || !((InteractionFootballScoreboard)set.getValue()).teamColor.equals((Object)teamColor)) continue;
                boards.put(set.getValue().getId(), (InteractionFootballScoreboard)set.getValue());
            }
            return boards;
        }
    }

    public InteractionGameGate getGameGate(int itemId) {
        return this.gameGates.get(itemId);
    }

    public void addGameGate(InteractionGameGate gameGate) {
        this.gameGates.put(gameGate.getId(), gameGate);
    }

    public void removeGameGate(InteractionGameGate gameGate) {
        this.gameGates.remove(gameGate.getId());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public THashMap<Integer, InteractionFreezeGate> getFreezeGates() {
        THashMap<Integer, InteractionGameGate> tHashMap = this.gameGates;
        synchronized (tHashMap) {
            THashMap<Integer, InteractionFreezeGate> gates = new THashMap<Integer, InteractionFreezeGate>();
            for (Map.Entry<Integer, InteractionGameGate> set : this.gameGates.entrySet()) {
                if (!(set.getValue() instanceof InteractionFreezeGate)) continue;
                gates.put(set.getValue().getId(), (InteractionFreezeGate)set.getValue());
            }
            return gates;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public THashMap<Integer, InteractionBattleBanzaiGate> getBattleBanzaiGates() {
        THashMap<Integer, InteractionGameGate> tHashMap = this.gameGates;
        synchronized (tHashMap) {
            THashMap<Integer, InteractionBattleBanzaiGate> gates = new THashMap<Integer, InteractionBattleBanzaiGate>();
            for (Map.Entry<Integer, InteractionGameGate> set : this.gameGates.entrySet()) {
                if (!(set.getValue() instanceof InteractionBattleBanzaiGate)) continue;
                gates.put(set.getValue().getId(), (InteractionBattleBanzaiGate)set.getValue());
            }
            return gates;
        }
    }

    public InteractionGameTimer getGameTimer(int itemId) {
        return this.gameTimers.get(itemId);
    }

    public void addGameTimer(InteractionGameTimer gameTimer) {
        this.gameTimers.put(gameTimer.getId(), gameTimer);
    }

    public void removeGameTimer(InteractionGameTimer gameTimer) {
        this.gameTimers.remove(gameTimer.getId());
    }

    public THashMap<Integer, InteractionGameTimer> getGameTimers() {
        return this.gameTimers;
    }

    public InteractionFreezeExitTile getFreezeExitTile() {
        return this.freezeExitTile.values().stream().findFirst().orElse(null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public InteractionFreezeExitTile getRandomFreezeExitTile() {
        THashMap<Integer, InteractionFreezeExitTile> tHashMap = this.freezeExitTile;
        synchronized (tHashMap) {
            return (InteractionFreezeExitTile)this.freezeExitTile.values().toArray()[Emulator.getRandom().nextInt(this.freezeExitTile.size())];
        }
    }

    public void addFreezeExitTile(InteractionFreezeExitTile freezeExitTile) {
        this.freezeExitTile.put(freezeExitTile.getId(), freezeExitTile);
    }

    public THashMap<Integer, InteractionFreezeExitTile> getFreezeExitTiles() {
        return this.freezeExitTile;
    }

    public void removeFreezeExitTile(InteractionFreezeExitTile freezeExitTile) {
        this.freezeExitTile.remove(freezeExitTile.getId());
    }

    public boolean hasFreezeExitTile() {
        return !this.freezeExitTile.isEmpty();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addUndefined(HabboItem item) {
        THashMap<Integer, HabboItem> tHashMap = this.undefined;
        synchronized (tHashMap) {
            this.undefined.put(item.getId(), item);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeUndefined(HabboItem item) {
        THashMap<Integer, HabboItem> tHashMap = this.undefined;
        synchronized (tHashMap) {
            this.undefined.remove(item.getId());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public THashSet<HabboItem> getItemsOfType(Class<? extends HabboItem> type) {
        THashSet<HabboItem> items = new THashSet<HabboItem>();
        THashMap<Integer, HabboItem> tHashMap = this.undefined;
        synchronized (tHashMap) {
            for (HabboItem item : this.undefined.values()) {
                if (item.getClass() != type) continue;
                items.add(item);
            }
        }
        return items;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public HabboItem getLowestItemsOfType(Class<? extends HabboItem> type) {
        HabboItem i = null;
        THashMap<Integer, HabboItem> tHashMap = this.undefined;
        synchronized (tHashMap) {
            for (HabboItem item : this.undefined.values()) {
                if (i != null && !(item.getZ() < i.getZ()) || !item.getClass().isAssignableFrom(type)) continue;
                i = item;
            }
        }
        return i;
    }

    public THashSet<ICycleable> getCycleTasks() {
        return this.cycleTasks;
    }

    public void addCycleTask(ICycleable task) {
        this.cycleTasks.add(task);
    }

    public void removeCycleTask(ICycleable task) {
        this.cycleTasks.remove(task);
    }

    public synchronized void dispose() {
        this.banzaiTeleporters.clear();
        this.nests.clear();
        this.petDrinks.clear();
        this.petFoods.clear();
        this.rollers.clear();
        this.wiredTriggers.clear();
        this.wiredEffects.clear();
        this.wiredConditions.clear();
        this.gameScoreboards.clear();
        this.gameGates.clear();
        this.gameTimers.clear();
        this.freezeExitTile.clear();
        this.undefined.clear();
        this.cycleTasks.clear();
    }

    public Rectangle tentAt(RoomTile location) {
        for (HabboItem item : this.getItemsOfType(InteractionTent.class)) {
            Rectangle rectangle = RoomLayout.getRectangle(item.getX(), item.getY(), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation());
            if (!RoomLayout.tileInSquare(rectangle, location)) continue;
            return rectangle;
        }
        return null;
    }
}

