/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.incoming.rooms.items;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.UpdateStackHeightComposer;
import com.eu.habbo.messages.outgoing.rooms.items.RemoveFloorItemComposer;
import com.eu.habbo.plugin.events.furniture.FurnitureRedeemedEvent;
import com.eu.habbo.threading.runnables.QueryDeleteHabboItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedeemItemEvent
extends MessageHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedeemItemEvent.class);

    @Override
    public void handle() throws Exception {
        HabboItem item;
        int itemId = this.packet.readInt();
        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();
        if (room != null && (item = room.getHabboItem(itemId)) != null && this.client.getHabbo().getHabboInfo().getId() == item.getUserId()) {
            boolean furnitureRedeemEventRegistered = Emulator.getPluginManager().isRegistered(FurnitureRedeemedEvent.class, true);
            FurnitureRedeemedEvent furniRedeemEvent = new FurnitureRedeemedEvent(item, this.client.getHabbo(), 0, -1);
            if (item.getBaseItem().getName().startsWith("CF_") || item.getBaseItem().getName().startsWith("CFC_") || item.getBaseItem().getName().startsWith("DF_") || item.getBaseItem().getName().startsWith("PF_")) {
                if ((item.getBaseItem().getName().startsWith("CF_") || item.getBaseItem().getName().startsWith("CFC_")) && !item.getBaseItem().getName().contains("_diamond_")) {
                    int credits;
                    try {
                        credits = Integer.parseInt(item.getBaseItem().getName().split("_")[1]);
                    }
                    catch (Exception e) {
                        LOGGER.error("Failed to parse redeemable furniture: {}. Must be in format of CF_<amount>", (Object)item.getBaseItem().getName());
                        return;
                    }
                    furniRedeemEvent = new FurnitureRedeemedEvent(item, this.client.getHabbo(), credits, -1);
                } else if (item.getBaseItem().getName().startsWith("PF_")) {
                    int pixels;
                    try {
                        pixels = Integer.parseInt(item.getBaseItem().getName().split("_")[1]);
                    }
                    catch (Exception e) {
                        LOGGER.error("Failed to parse redeemable pixel furniture: {}. Must be in format of PF_<amount>", (Object)item.getBaseItem().getName());
                        return;
                    }
                    furniRedeemEvent = new FurnitureRedeemedEvent(item, this.client.getHabbo(), pixels, 0);
                } else if (item.getBaseItem().getName().startsWith("DF_")) {
                    int points;
                    int pointsType;
                    try {
                        pointsType = Integer.parseInt(item.getBaseItem().getName().split("_")[1]);
                    }
                    catch (Exception e) {
                        LOGGER.error("Failed to parse redeemable points furniture: {}. Must be in format of DF_<pointstype>_<amount> where <pointstype> equals integer representation of seasonal currency.", (Object)item.getBaseItem().getName());
                        return;
                    }
                    try {
                        points = Integer.parseInt(item.getBaseItem().getName().split("_")[2]);
                    }
                    catch (Exception e) {
                        LOGGER.error("Failed to parse redeemable points furniture: {}. Must be in format of DF_<pointstype>_<amount> where <pointstype> equals integer representation of seasonal currency.", (Object)item.getBaseItem().getName());
                        return;
                    }
                    furniRedeemEvent = new FurnitureRedeemedEvent(item, this.client.getHabbo(), points, pointsType);
                } else if (item.getBaseItem().getName().startsWith("CF_diamond_")) {
                    int points;
                    try {
                        points = Integer.parseInt(item.getBaseItem().getName().split("_")[2]);
                    }
                    catch (Exception e) {
                        LOGGER.error("Failed to parse redeemable diamonds furniture: {}. Must be in format of CF_diamond_<amount>", (Object)item.getBaseItem().getName());
                        return;
                    }
                    furniRedeemEvent = new FurnitureRedeemedEvent(item, this.client.getHabbo(), points, 5);
                }
                if (furnitureRedeemEventRegistered) {
                    Emulator.getPluginManager().fireEvent(furniRedeemEvent);
                    if (furniRedeemEvent.isCancelled()) {
                        return;
                    }
                }
                if (furniRedeemEvent.amount < 1) {
                    return;
                }
                if (room.getHabboItem(item.getId()) == null) {
                    return;
                }
                room.removeHabboItem(item);
                room.sendComposer(new RemoveFloorItemComposer(item).compose());
                RoomTile t = room.getLayout().getTile(item.getX(), item.getY());
                t.setStackHeight(room.getStackHeight(item.getX(), item.getY(), false));
                room.updateTile(t);
                room.sendComposer(new UpdateStackHeightComposer(item.getX(), item.getY(), t.z, t.relativeHeight()).compose());
                Emulator.getThreading().run(new QueryDeleteHabboItem(item.getId()));
                switch (furniRedeemEvent.currencyID) {
                    case -1: {
                        this.client.getHabbo().giveCredits(furniRedeemEvent.amount);
                        break;
                    }
                    case 5: {
                        this.client.getHabbo().givePoints(furniRedeemEvent.amount);
                        break;
                    }
                    case 0: {
                        this.client.getHabbo().givePixels(furniRedeemEvent.amount);
                        break;
                    }
                    default: {
                        this.client.getHabbo().givePoints(furniRedeemEvent.currencyID, furniRedeemEvent.amount);
                    }
                }
            }
        }
    }
}

