/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.incoming.navigator;

import com.eu.habbo.habbohotel.users.HabboNavigatorWindowSettings;
import com.eu.habbo.messages.incoming.MessageHandler;

public class SaveWindowSettingsEvent
extends MessageHandler {
    @Override
    public void handle() throws Exception {
        int unknownVar;
        boolean openSearches;
        HabboNavigatorWindowSettings windowSettings = this.client.getHabbo().getHabboStats().navigatorWindowSettings;
        windowSettings.x = this.packet.readInt();
        windowSettings.y = this.packet.readInt();
        windowSettings.width = this.packet.readInt();
        windowSettings.height = this.packet.readInt();
        windowSettings.openSearches = openSearches = this.packet.readBoolean();
        int unknown = unknownVar = this.packet.readInt().intValue();
    }
}

