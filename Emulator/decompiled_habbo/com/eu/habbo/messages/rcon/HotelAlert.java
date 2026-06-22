/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.rcon;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.StaffAlertWithLinkComposer;
import com.eu.habbo.messages.rcon.RCONMessage;
import com.google.gson.Gson;
import java.util.Map;

public class HotelAlert
extends RCONMessage<JSONHotelAlert> {
    public HotelAlert() {
        super(JSONHotelAlert.class);
    }

    @Override
    public void handle(Gson gson, JSONHotelAlert object) {
        ServerMessage serverMessage = object.url.isEmpty() ? new GenericAlertComposer(object.message).compose() : new StaffAlertWithLinkComposer(object.message, object.url).compose();
        if (serverMessage != null) {
            for (Map.Entry<Integer, Habbo> set : Emulator.getGameEnvironment().getHabboManager().getOnlineHabbos().entrySet()) {
                Habbo habbo = set.getValue();
                if (habbo.getHabboStats().blockStaffAlerts) continue;
                habbo.getClient().sendResponse(serverMessage);
            }
        }
    }

    static class JSONHotelAlert {
        public String message;
        public String url = "";

        JSONHotelAlert() {
        }
    }
}

