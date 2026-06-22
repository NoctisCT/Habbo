/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionBadgeDisplay;
import com.eu.habbo.habbohotel.items.interactions.InteractionClothing;
import com.eu.habbo.habbohotel.items.interactions.InteractionCrackable;
import com.eu.habbo.habbohotel.items.interactions.InteractionGift;
import com.eu.habbo.habbohotel.items.interactions.InteractionGymEquipment;
import com.eu.habbo.habbohotel.items.interactions.InteractionHopper;
import com.eu.habbo.habbohotel.items.interactions.InteractionMannequin;
import com.eu.habbo.habbohotel.items.interactions.InteractionObstacle;
import com.eu.habbo.habbohotel.items.interactions.InteractionOneWayGate;
import com.eu.habbo.habbohotel.items.interactions.InteractionPressurePlate;
import com.eu.habbo.habbohotel.items.interactions.InteractionPushable;
import com.eu.habbo.habbohotel.items.interactions.InteractionPuzzleBox;
import com.eu.habbo.habbohotel.items.interactions.InteractionRoller;
import com.eu.habbo.habbohotel.items.interactions.InteractionSwitch;
import com.eu.habbo.habbohotel.items.interactions.InteractionTeleport;
import com.eu.habbo.habbohotel.items.interactions.InteractionTent;
import com.eu.habbo.habbohotel.items.interactions.InteractionTrap;
import com.eu.habbo.habbohotel.items.interactions.InteractionTrophy;
import com.eu.habbo.habbohotel.items.interactions.InteractionVendingMachine;
import com.eu.habbo.habbohotel.items.interactions.InteractionWater;
import com.eu.habbo.habbohotel.items.interactions.InteractionWired;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredTrigger;
import com.eu.habbo.habbohotel.items.interactions.games.InteractionGameGate;
import com.eu.habbo.habbohotel.items.interactions.games.InteractionGameScoreboard;
import com.eu.habbo.habbohotel.items.interactions.games.InteractionGameTimer;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.InteractionBattleBanzaiTeleporter;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.InteractionBattleBanzaiTile;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.InteractionFreezeBlock;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.InteractionFreezeExitTile;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.InteractionFreezeTile;
import com.eu.habbo.habbohotel.items.interactions.games.tag.InteractionTagField;
import com.eu.habbo.habbohotel.items.interactions.games.tag.InteractionTagPole;
import com.eu.habbo.habbohotel.items.interactions.pets.InteractionMonsterPlantSeed;
import com.eu.habbo.habbohotel.items.interactions.pets.InteractionPetBreedingNest;
import com.eu.habbo.habbohotel.items.interactions.pets.InteractionPetDrink;
import com.eu.habbo.habbohotel.items.interactions.pets.InteractionPetFood;
import com.eu.habbo.habbohotel.items.interactions.pets.InteractionPetToy;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.wired.WiredSaveException;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.hash.THashSet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WiredEffectToggleRandom
extends InteractionWiredEffect {
    private static final Logger LOGGER = LoggerFactory.getLogger(WiredEffectToggleRandom.class);
    public static final WiredEffectType type = WiredEffectType.TOGGLE_RANDOM;
    private final THashSet<HabboItem> items = new THashSet();
    private static final List<Class<? extends HabboItem>> FORBIDDEN_TYPES = new ArrayList<Class>(Arrays.asList(InteractionWired.class, InteractionTeleport.class, InteractionPushable.class, InteractionTagPole.class, InteractionTagField.class, InteractionCrackable.class, InteractionGameScoreboard.class, InteractionGameGate.class, InteractionFreezeTile.class, InteractionFreezeBlock.class, InteractionFreezeExitTile.class, InteractionBattleBanzaiTeleporter.class, InteractionBattleBanzaiTile.class, InteractionMonsterPlantSeed.class, InteractionPetBreedingNest.class, InteractionPetDrink.class, InteractionPetFood.class, InteractionPetToy.class, InteractionBadgeDisplay.class, InteractionClothing.class, InteractionVendingMachine.class, InteractionGift.class, InteractionPressurePlate.class, InteractionMannequin.class, InteractionGymEquipment.class, InteractionHopper.class, InteractionObstacle.class, InteractionOneWayGate.class, InteractionPuzzleBox.class, InteractionRoller.class, InteractionSwitch.class, InteractionTent.class, InteractionTrap.class, InteractionTrophy.class, InteractionWater.class));

    public WiredEffectToggleRandom(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectToggleRandom(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void serializeWiredData(ServerMessage message, Room room) {
        THashSet<HabboItem> items = new THashSet<HabboItem>();
        for (HabboItem item : this.items) {
            if (item.getRoomId() == this.getRoomId() && Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(item.getId()) != null) continue;
            items.add(item);
        }
        for (HabboItem item : items) {
            this.items.remove(item);
        }
        message.appendBoolean(false);
        message.appendInt(WiredHandler.MAXIMUM_FURNI_SELECTION);
        message.appendInt(this.items.size());
        for (HabboItem item : this.items) {
            message.appendInt(item.getId());
        }
        message.appendInt(this.getBaseItem().getSpriteId());
        message.appendInt(this.getId());
        message.appendString("");
        message.appendInt(0);
        message.appendInt(0);
        message.appendInt(this.getType().code);
        message.appendInt(this.getDelay());
        if (this.requiresTriggeringUser()) {
            final ArrayList invalidTriggers = new ArrayList();
            room.getRoomSpecialTypes().getTriggers(this.getX(), this.getY()).forEach(new TObjectProcedure<InteractionWiredTrigger>(){
                final /* synthetic */ WiredEffectToggleRandom this$0;
                {
                    this.this$0 = this$0;
                }

                @Override
                public boolean execute(InteractionWiredTrigger object) {
                    if (!object.isTriggeredByRoomUnit()) {
                        invalidTriggers.add(object.getBaseItem().getSpriteId());
                    }
                    return true;
                }
            });
            message.appendInt(invalidTriggers.size());
            for (Integer i : invalidTriggers) {
                message.appendInt(i);
            }
        } else {
            message.appendInt(0);
        }
    }

    @Override
    public boolean saveData(WiredSettings settings, GameClient gameClient) throws WiredSaveException {
        int itemsCount = settings.getFurniIds().length;
        if (itemsCount > Emulator.getConfig().getInt("hotel.wired.furni.selection.count")) {
            throw new WiredSaveException("Too many furni selected");
        }
        ArrayList<HabboItem> newItems = new ArrayList<HabboItem>();
        for (int i = 0; i < itemsCount; ++i) {
            int itemId = settings.getFurniIds()[i];
            HabboItem it = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(itemId);
            if (it == null) {
                throw new WiredSaveException(String.format("Item %s not found", itemId));
            }
            newItems.add(it);
        }
        int delay = settings.getDelay();
        if (delay > Emulator.getConfig().getInt("hotel.wired.max_delay", 20)) {
            throw new WiredSaveException("Delay too long");
        }
        this.items.clear();
        this.items.addAll(newItems);
        this.setDelay(delay);
        return true;
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        THashSet<HabboItem> items = this.items;
        for (HabboItem item : items) {
            if (item.getRoomId() == 0 || FORBIDDEN_TYPES.stream().anyMatch(a -> a.isAssignableFrom(item.getClass()))) {
                this.items.remove(item);
                continue;
            }
            try {
                item.setExtradata("" + Emulator.getRandom().nextInt(item.getBaseItem().getStateCount() + 1));
                room.updateItem(item);
            }
            catch (Exception e) {
                LOGGER.error("Caught exception", e);
            }
        }
        return true;
    }

    @Override
    public String getWiredData() {
        return WiredHandler.getGsonBuilder().create().toJson(new JsonData(this.getDelay(), this.items.stream().map(HabboItem::getId).collect(Collectors.toList())));
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException {
        block4: {
            String wiredData;
            block3: {
                this.items.clear();
                wiredData = set.getString("wired_data");
                if (!wiredData.startsWith("{")) break block3;
                JsonData data = WiredHandler.getGsonBuilder().create().fromJson(wiredData, JsonData.class);
                this.setDelay(data.delay);
                for (Integer id : data.itemIds) {
                    HabboItem item = room.getHabboItem(id);
                    if (item instanceof InteractionFreezeBlock || item instanceof InteractionGameTimer || item instanceof InteractionCrackable || item == null) continue;
                    this.items.add(item);
                }
                break block4;
            }
            String[] wiredDataOld = wiredData.split("\t");
            if (wiredDataOld.length >= 1) {
                this.setDelay(Integer.parseInt(wiredDataOld[0]));
            }
            if (wiredDataOld.length != 2 || !wiredDataOld[1].contains(";")) break block4;
            for (String s : wiredDataOld[1].split(";")) {
                HabboItem item = room.getHabboItem(Integer.parseInt(s));
                if (item instanceof InteractionFreezeBlock || item instanceof InteractionGameTimer || item instanceof InteractionCrackable || item == null) continue;
                this.items.add(item);
            }
        }
    }

    @Override
    public void onPickUp() {
        this.items.clear();
        this.setDelay(0);
    }

    @Override
    public WiredEffectType getType() {
        return type;
    }

    static class JsonData {
        int delay;
        List<Integer> itemIds;

        public JsonData(int delay, List<Integer> itemIds) {
            this.delay = delay;
            this.itemIds = itemIds;
        }
    }
}

