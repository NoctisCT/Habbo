/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items.interactions.wired.triggers;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredTrigger;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.ServerMessage;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.hash.THashSet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WiredTriggerBotReachedFurni
extends InteractionWiredTrigger {
    private static final Logger LOGGER = LoggerFactory.getLogger(WiredTriggerBotReachedFurni.class);
    public static final WiredTriggerType type = WiredTriggerType.WALKS_ON_FURNI;
    private THashSet<HabboItem> items = new THashSet();
    private String botName = "";

    public WiredTriggerBotReachedFurni(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredTriggerBotReachedFurni(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public WiredTriggerType getType() {
        return type;
    }

    @Override
    public void serializeWiredData(ServerMessage message, Room room) {
        THashSet<HabboItem> items = new THashSet<HabboItem>();
        if (Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()) == null) {
            items.addAll(this.items);
        } else {
            for (HabboItem item : this.items) {
                if (Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(item.getId()) != null) continue;
                items.add(item);
            }
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
        message.appendString(this.botName);
        message.appendInt(0);
        message.appendInt(0);
        message.appendInt(WiredTriggerType.BOT_REACHED_STF.code);
        if (!this.isTriggeredByRoomUnit()) {
            final ArrayList invalidTriggers = new ArrayList();
            room.getRoomSpecialTypes().getEffects(this.getX(), this.getY()).forEach(new TObjectProcedure<InteractionWiredEffect>(){
                final /* synthetic */ WiredTriggerBotReachedFurni this$0;
                {
                    this.this$0 = this$0;
                }

                @Override
                public boolean execute(InteractionWiredEffect object) {
                    if (object.requiresTriggeringUser()) {
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
    public boolean saveData(WiredSettings settings) {
        this.botName = settings.getStringParam();
        this.items.clear();
        int count = settings.getFurniIds().length;
        for (int i = 0; i < count; ++i) {
            this.items.add(Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(settings.getFurniIds()[i]));
        }
        return true;
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        if (stuff.length >= 1 && stuff[0] instanceof HabboItem) {
            return this.items.contains(stuff[0]) && room.getBots(this.botName).stream().anyMatch(bot -> bot.getRoomUnit() == roomUnit);
        }
        return false;
    }

    @Override
    public String getWiredData() {
        return WiredHandler.getGsonBuilder().create().toJson(new JsonData(this.botName, this.items.stream().map(HabboItem::getId).collect(Collectors.toList())));
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException {
        block5: {
            String[] items;
            String[] data;
            block6: {
                String wiredData;
                block4: {
                    this.items.clear();
                    wiredData = set.getString("wired_data");
                    if (!wiredData.startsWith("{")) break block4;
                    JsonData data2 = WiredHandler.getGsonBuilder().create().fromJson(wiredData, JsonData.class);
                    this.botName = data2.botName;
                    for (Integer id : data2.itemIds) {
                        HabboItem item = room.getHabboItem(id);
                        if (item == null) continue;
                        this.items.add(item);
                    }
                    break block5;
                }
                data = wiredData.split(":");
                if (data.length != 1) break block6;
                this.botName = data[0];
                break block5;
            }
            if (data.length != 2) break block5;
            this.botName = data[0];
            for (String id : items = data[1].split(";")) {
                try {
                    HabboItem item = room.getHabboItem(Integer.parseInt(id));
                    if (item == null) continue;
                    this.items.add(item);
                }
                catch (Exception e) {
                    LOGGER.error("Caught exception", e);
                }
            }
        }
    }

    @Override
    public void onPickUp() {
        this.items.clear();
        this.botName = "";
    }

    static class JsonData {
        String botName;
        List<Integer> itemIds;

        public JsonData(String botName, List<Integer> itemIds) {
            this.botName = botName;
            this.itemIds = itemIds;
        }
    }
}

