/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.rcon;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.habbohotel.users.subscriptions.Subscription;
import com.eu.habbo.messages.rcon.RCONMessage;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModifyUserSubscription
extends RCONMessage<JSON> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModifyUserSubscription.class);

    public ModifyUserSubscription() {
        super(JSON.class);
    }

    @Override
    public void handle(Gson gson, JSON json) {
        try {
            if (json.user_id <= 0) {
                this.status = 2;
                this.message = "User not found";
                return;
            }
            if (!Emulator.getGameEnvironment().getSubscriptionManager().types.containsKey(json.type)) {
                this.status = 1;
                this.message = "%subscription% is not a valid subscription type".replace("%subscription%", json.type);
                return;
            }
            HabboInfo habbo = Emulator.getGameEnvironment().getHabboManager().getHabboInfo(json.user_id);
            if (habbo == null) {
                this.status = 2;
                this.message = "User not found";
                return;
            }
            if (json.action.equalsIgnoreCase("add") || json.action.equalsIgnoreCase("+") || json.action.equalsIgnoreCase("a")) {
                if (json.duration < 1) {
                    this.status = 1;
                    this.message = "duration must be > 0";
                    return;
                }
                habbo.getHabboStats().createSubscription(json.type, json.duration);
                this.status = 0;
                this.message = "Successfully added %time% seconds to %subscription% on %user%".replace("%time%", "" + json.duration).replace("%user%", habbo.getUsername()).replace("%subscription%", json.type);
            } else if (json.action.equalsIgnoreCase("remove") || json.action.equalsIgnoreCase("-") || json.action.equalsIgnoreCase("r")) {
                Subscription s = habbo.getHabboStats().getSubscription(json.type);
                if (s == null) {
                    this.status = 1;
                    this.message = "%user% does not have the %subscription% subscription".replace("%user%", habbo.getUsername()).replace("%subscription%", json.type);
                    return;
                }
                if (json.duration != -1) {
                    if (json.duration < 1) {
                        this.status = 1;
                        this.message = "duration must be > 0 or -1 to remove all time";
                        return;
                    }
                    s.addDuration(-json.duration);
                    this.status = 0;
                    this.message = "Successfully removed %time% seconds from %subscription% on %user%".replace("%time%", "" + json.duration).replace("%user%", habbo.getUsername()).replace("%subscription%", json.type);
                } else {
                    s.addDuration(-s.getRemaining());
                    this.status = 0;
                    this.message = "Successfully removed %subscription% sub from %user%".replace("%user%", habbo.getUsername()).replace("%subscription%", json.type);
                }
            } else {
                this.status = 1;
                this.message = "Invalid action specified. Must be add, +, remove or -";
            }
        }
        catch (Exception e) {
            this.status = 4;
            this.message = "Exception occurred";
            LOGGER.error("Exception occurred", e);
        }
    }

    static class JSON {
        public int user_id;
        public String type = "";
        public String action = "";
        public int duration = -1;

        JSON() {
        }
    }
}

