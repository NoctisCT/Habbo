/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.hotelview;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class BonusRareComposer
extends MessageComposer {
    private final Habbo habbo;

    public BonusRareComposer(Habbo habbo) {
        this.habbo = habbo;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1533);
        this.response.appendString(Emulator.getConfig().getValue("hotelview.promotional.reward.name", "prizetrophy_breed_gold"));
        this.response.appendInt(Emulator.getConfig().getInt("hotelview.promotional.reward.id", 0));
        this.response.appendInt(Emulator.getConfig().getInt("hotelview.promotional.points", 120));
        int points = Emulator.getConfig().getInt("hotelview.promotional.points", 120) - this.habbo.getHabboInfo().getBonusRarePoints();
        this.response.appendInt(Math.max(points, 0));
        return this.response;
    }

    public Habbo getHabbo() {
        return this.habbo;
    }
}

