/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.habbohotel.users.HabboManager;
import com.eu.habbo.habbohotel.users.HabboStats;
import com.eu.habbo.habbohotel.users.subscriptions.Subscription;

public class SubscriptionCommand
extends Command {
    public SubscriptionCommand() {
        super("cmd_subscription", Emulator.getTexts().getValue("commands.keys.cmd_subscription").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (params.length >= 4) {
            HabboInfo info = HabboManager.getOfflineHabboInfo(params[1]);
            if (info != null) {
                HabboStats stats = info.getHabboStats();
                String subscription = params[2].toUpperCase();
                String action = params[3];
                StringBuilder message = new StringBuilder();
                if (params.length > 4) {
                    for (int i = 4; i < params.length; ++i) {
                        message.append(params[i]).append(" ");
                    }
                }
                if (!Emulator.getGameEnvironment().getSubscriptionManager().types.containsKey(subscription)) {
                    gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.error.cmd_subscription.type_not_found", "%subscription% is not a valid subscription type").replace("%subscription%", subscription), RoomChatMessageBubbles.ALERT);
                    return true;
                }
                if (action.equalsIgnoreCase("add") || action.equalsIgnoreCase("+") || action.equalsIgnoreCase("a")) {
                    int timeToAdd = Emulator.timeStringToSeconds(message.toString());
                    if (timeToAdd < 1) {
                        gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.error.cmd_subscription.invalid_params_time", "Invalid time span, try: x minutes/days/weeks/months"), RoomChatMessageBubbles.ALERT);
                        return true;
                    }
                    stats.createSubscription(subscription, timeToAdd);
                    gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.error.cmd_subscription.success_add_time", "Successfully added %time% seconds to %subscription% on %user%").replace("%time%", "" + timeToAdd).replace("%user%", params[1]).replace("%subscription%", subscription), RoomChatMessageBubbles.ALERT);
                } else if (action.equalsIgnoreCase("remove") || action.equalsIgnoreCase("-") || action.equalsIgnoreCase("r")) {
                    Subscription s = stats.getSubscription(subscription);
                    if (s == null) {
                        gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.error.cmd_subscription.user_not_have", "%user% does not have the %subscription% subscription").replace("%user%", params[1]).replace("%subscription%", subscription), RoomChatMessageBubbles.ALERT);
                        return true;
                    }
                    if (message.length() != 0) {
                        int timeToRemove = Emulator.timeStringToSeconds(message.toString());
                        if (timeToRemove < 1) {
                            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.error.cmd_subscription.invalid_params_time", "Invalid time span, try: x minutes/days/weeks/months"), RoomChatMessageBubbles.ALERT);
                            return true;
                        }
                        s.addDuration(-timeToRemove);
                        gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.error.cmd_subscription.success_remove_time", "Successfully removed %time% seconds from %subscription% on %user%").replace("%time%", "" + timeToRemove).replace("%user%", params[1]).replace("%subscription%", subscription), RoomChatMessageBubbles.ALERT);
                    } else {
                        s.addDuration(-s.getRemaining());
                        gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.error.cmd_subscription.success_remove_sub", "Successfully removed %subscription% sub from %user%").replace("%user%", params[1]).replace("%subscription%", subscription), RoomChatMessageBubbles.ALERT);
                    }
                } else {
                    gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.error.cmd_subscription.invalid_action", "Invalid action specified. Must be add, +, remove or -"), RoomChatMessageBubbles.ALERT);
                }
            } else {
                gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.error.cmd_subscription.user_not_found", "%user% was not found").replace("%user%", params[1]), RoomChatMessageBubbles.ALERT);
            }
        } else {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.error.cmd_subscription.invalid_params", "Invalid command format"), RoomChatMessageBubbles.ALERT);
        }
        return true;
    }
}

