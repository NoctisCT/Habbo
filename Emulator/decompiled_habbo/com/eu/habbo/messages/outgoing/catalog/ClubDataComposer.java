/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.catalog;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.catalog.ClubOffer;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import java.util.List;

public class ClubDataComposer
extends MessageComposer {
    private final int windowId;
    private final Habbo habbo;

    public ClubDataComposer(Habbo habbo, int windowId) {
        this.habbo = habbo;
        this.windowId = windowId;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2405);
        List<ClubOffer> offers = Emulator.getGameEnvironment().getCatalogManager().getClubOffers();
        this.response.appendInt(offers.size());
        for (ClubOffer offer : offers) {
            offer.serialize(this.response, this.habbo.getHabboStats().getClubExpireTimestamp());
        }
        this.response.appendInt(this.windowId);
        return this.response;
    }

    public int getWindowId() {
        return this.windowId;
    }

    public Habbo getHabbo() {
        return this.habbo;
    }
}

