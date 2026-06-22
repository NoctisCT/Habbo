/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.outgoing.inventory.InventoryRefreshComposer;
import com.eu.habbo.threading.runnables.QueryDeleteHabboItems;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TIntIntProcedure;
import java.util.ArrayList;

public class RedeemCommand
extends Command {
    public RedeemCommand() {
        super("cmd_redeem", Emulator.getTexts().getValue("commands.keys.cmd_redeem").split(";"));
    }

    @Override
    public boolean handle(final GameClient gameClient, String[] params) throws Exception {
        if (gameClient.getHabbo().getHabboInfo().getCurrentRoom().getActiveTradeForHabbo(gameClient.getHabbo()) != null) {
            return false;
        }
        ArrayList<HabboItem> items = new ArrayList<HabboItem>();
        int credits = 0;
        int pixels = 0;
        TIntIntHashMap points = new TIntIntHashMap();
        for (HabboItem habboItem : gameClient.getHabbo().getInventory().getItemsComponent().getItemsAsValueCollection()) {
            int pointsAmount;
            if (!habboItem.getBaseItem().getName().startsWith("CF_") && !habboItem.getBaseItem().getName().startsWith("CFC_") && !habboItem.getBaseItem().getName().startsWith("DF_") && !habboItem.getBaseItem().getName().startsWith("PF_") || habboItem.getUserId() != gameClient.getHabbo().getHabboInfo().getId()) continue;
            items.add(habboItem);
            if ((habboItem.getBaseItem().getName().startsWith("CF_") || habboItem.getBaseItem().getName().startsWith("CFC_")) && !habboItem.getBaseItem().getName().contains("_diamond_")) {
                try {
                    credits += Integer.parseInt(habboItem.getBaseItem().getName().split("_")[1]);
                }
                catch (Exception exception) {}
                continue;
            }
            if (habboItem.getBaseItem().getName().startsWith("PF_")) {
                try {
                    pixels += Integer.parseInt(habboItem.getBaseItem().getName().split("_")[1]);
                }
                catch (Exception exception) {}
                continue;
            }
            if (habboItem.getBaseItem().getName().startsWith("DF_")) {
                int n = Integer.parseInt(habboItem.getBaseItem().getName().split("_")[1]);
                pointsAmount = Integer.parseInt(habboItem.getBaseItem().getName().split("_")[2]);
                points.adjustOrPutValue(n, pointsAmount, pointsAmount);
                continue;
            }
            if (!habboItem.getBaseItem().getName().startsWith("CF_diamond_")) continue;
            int n = 5;
            pointsAmount = Integer.parseInt(habboItem.getBaseItem().getName().split("_")[2]);
            points.adjustOrPutValue(n, pointsAmount, pointsAmount);
        }
        TIntObjectHashMap<HabboItem> deleted = new TIntObjectHashMap<HabboItem>();
        for (HabboItem habboItem : items) {
            gameClient.getHabbo().getInventory().getItemsComponent().removeHabboItem(habboItem);
            deleted.put(habboItem.getId(), habboItem);
        }
        Emulator.getThreading().run(new QueryDeleteHabboItems(deleted));
        gameClient.sendResponse(new InventoryRefreshComposer());
        gameClient.getHabbo().giveCredits(credits);
        gameClient.getHabbo().givePixels(pixels);
        final String[] stringArray = new String[]{Emulator.getTexts().getValue("generic.redeemed")};
        stringArray[0] = stringArray[0] + Emulator.getTexts().getValue("generic.credits");
        stringArray[0] = stringArray[0] + ": " + credits;
        if (pixels > 0) {
            stringArray[0] = stringArray[0] + ", " + Emulator.getTexts().getValue("generic.pixels");
            stringArray[0] = stringArray[0] + ": " + pixels;
        }
        if (!points.isEmpty()) {
            points.forEachEntry(new TIntIntProcedure(){
                final /* synthetic */ RedeemCommand this$0;
                {
                    this.this$0 = this$0;
                }

                @Override
                public boolean execute(int a, int b) {
                    gameClient.getHabbo().givePoints(a, b);
                    stringArray[0] = stringArray[0] + " ," + Emulator.getTexts().getValue("seasonal.name." + a) + ": " + b;
                    return true;
                }
            });
        }
        gameClient.getHabbo().whisper(stringArray[0], RoomChatMessageBubbles.ALERT);
        return true;
    }
}

