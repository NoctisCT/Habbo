/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.outgoing.inventory.InventoryRefreshComposer;
import com.eu.habbo.messages.outgoing.wired.WiredRewardAlertComposer;

public class RoomGiftCommand
extends Command {
    public RoomGiftCommand() {
        super("cmd_roomgift", Emulator.getTexts().getValue("commands.keys.cmd_roomgift").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (params.length >= 2) {
            int itemId;
            try {
                itemId = Integer.parseInt(params[1]);
            }
            catch (Exception e) {
                gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.error.cmd_gift.not_a_number"), RoomChatMessageBubbles.ALERT);
                return true;
            }
            if (itemId <= 0) {
                gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.error.cmd_gift.not_a_number"), RoomChatMessageBubbles.ALERT);
                return true;
            }
            Item baseItem = Emulator.getGameEnvironment().getItemManager().getItem(itemId);
            if (baseItem == null) {
                gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.error.cmd_gift.not_found").replace("%itemid%", "" + itemId), RoomChatMessageBubbles.ALERT);
                return true;
            }
            StringBuilder message = new StringBuilder();
            if (params.length > 2) {
                for (int i = 2; i < params.length; ++i) {
                    message.append(params[i]).append(" ");
                }
            }
            String finalMessage = message.toString();
            for (Habbo habbo : gameClient.getHabbo().getHabboInfo().getCurrentRoom().getHabbos()) {
                HabboItem item = Emulator.getGameEnvironment().getItemManager().createItem(0, baseItem, 0, 0, "");
                Item giftItem = Emulator.getGameEnvironment().getItemManager().getItem((Integer)Emulator.getGameEnvironment().getCatalogManager().giftFurnis.values().toArray()[Emulator.getRandom().nextInt(Emulator.getGameEnvironment().getCatalogManager().giftFurnis.size())]);
                String extraData = "1\t" + item.getId();
                extraData = extraData + "\t0\t0\t0\t" + finalMessage + "\t0\t0";
                Emulator.getGameEnvironment().getItemManager().createGift(habbo.getHabboInfo().getUsername(), giftItem, extraData, 0, 0);
                habbo.getClient().sendResponse(new InventoryRefreshComposer());
                habbo.getClient().sendResponse(new WiredRewardAlertComposer(6));
            }
            return true;
        }
        return false;
    }
}

