/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.catalog;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GiftConfigurationComposer
extends MessageComposer {
    public static List<Integer> BOX_TYPES = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 8);
    public static List<Integer> RIBBON_TYPES = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2234);
        this.response.appendBoolean(true);
        this.response.appendInt(Emulator.getConfig().getInt("hotel.gifts.special.price", 2));
        this.response.appendInt(Emulator.getGameEnvironment().getCatalogManager().giftWrappers.size());
        for (Integer n : Emulator.getGameEnvironment().getCatalogManager().giftWrappers.keySet()) {
            this.response.appendInt(n);
        }
        this.response.appendInt(BOX_TYPES.size());
        for (Integer n : BOX_TYPES) {
            this.response.appendInt(n);
        }
        this.response.appendInt(RIBBON_TYPES.size());
        for (Integer n : RIBBON_TYPES) {
            this.response.appendInt(n);
        }
        this.response.appendInt(Emulator.getGameEnvironment().getCatalogManager().giftFurnis.size());
        for (Map.Entry entry : Emulator.getGameEnvironment().getCatalogManager().giftFurnis.entrySet()) {
            this.response.appendInt((Integer)entry.getKey());
        }
        return this.response;
    }
}

