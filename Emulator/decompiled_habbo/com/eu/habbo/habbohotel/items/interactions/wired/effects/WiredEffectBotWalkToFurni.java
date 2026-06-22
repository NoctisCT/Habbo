/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.wired.WiredSaveException;
import gnu.trove.set.hash.THashSet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WiredEffectBotWalkToFurni
extends InteractionWiredEffect {
    public static final WiredEffectType type = WiredEffectType.BOT_MOVE;
    private List<HabboItem> items = new ArrayList<HabboItem>();
    private String botName = "";

    public WiredEffectBotWalkToFurni(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectBotWalkToFurni(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
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
        message.appendString(this.botName);
        message.appendInt(0);
        message.appendInt(0);
        message.appendInt(this.getType().code);
        message.appendInt(this.getDelay());
        message.appendInt(0);
    }

    @Override
    public boolean saveData(WiredSettings settings, GameClient gameClient) throws WiredSaveException {
        String botName = settings.getStringParam();
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
        this.botName = botName.substring(0, Math.min(botName.length(), Emulator.getConfig().getInt("hotel.wired.message.max_length", 100)));
        this.setDelay(delay);
        return true;
    }

    @Override
    public WiredEffectType getType() {
        return type;
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        HabboItem item2;
        List<Bot> bots = room.getBots(this.botName);
        if (this.items.isEmpty() || bots.size() != 1) {
            return true;
        }
        Bot bot = bots.get(0);
        this.items.removeIf(item -> item == null || item.getRoomId() != this.getRoomId() || Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(item.getId()) == null);
        List possibleItems = this.items.stream().filter(item -> !room.getBotsOnItem((HabboItem)item).contains(bot)).collect(Collectors.toList());
        if (possibleItems.size() > 0 && (item2 = (HabboItem)possibleItems.get(Emulator.getRandom().nextInt(possibleItems.size()))).getRoomId() != 0 && item2.getRoomId() == bot.getRoom().getId()) {
            bot.getRoomUnit().setGoalLocation(room.getLayout().getTile(item2.getX(), item2.getY()));
        }
        return true;
    }

    @Override
    public String getWiredData() {
        ArrayList<Integer> itemIds = new ArrayList<Integer>();
        if (this.items != null) {
            for (HabboItem item : this.items) {
                if (item.getRoomId() == 0) continue;
                itemIds.add(item.getId());
            }
        }
        return WiredHandler.getGsonBuilder().create().toJson(new JsonData(this.botName, itemIds, this.getDelay()));
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException {
        this.items = new ArrayList<HabboItem>();
        String wiredData = set.getString("wired_data");
        if (wiredData.startsWith("{")) {
            JsonData data = WiredHandler.getGsonBuilder().create().fromJson(wiredData, JsonData.class);
            this.setDelay(data.delay);
            this.botName = data.bot_name;
            for (int itemId : data.items) {
                HabboItem item = room.getHabboItem(itemId);
                if (item == null) continue;
                this.items.add(item);
            }
        } else {
            String[] wiredDataSplit = set.getString("wired_data").split("\t");
            if (wiredDataSplit.length >= 2) {
                this.setDelay(Integer.parseInt(wiredDataSplit[0]));
                String[] data = wiredDataSplit[1].split(";");
                if (data.length > 1) {
                    this.botName = data[0];
                    for (int i = 1; i < data.length; ++i) {
                        HabboItem item = room.getHabboItem(Integer.parseInt(data[i]));
                        if (item == null) continue;
                        this.items.add(item);
                    }
                }
            }
            this.needsUpdate(true);
        }
    }

    @Override
    public void onPickUp() {
        this.items.clear();
        this.botName = "";
        this.setDelay(0);
    }

    static class JsonData {
        String bot_name;
        List<Integer> items;
        int delay;

        public JsonData(String bot_name, List<Integer> items, int delay) {
            this.bot_name = bot_name;
            this.items = items;
            this.delay = delay;
        }
    }
}

