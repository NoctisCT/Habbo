/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.trading;

import com.eu.habbo.habbohotel.rooms.RoomTradeUser;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class TradeAcceptedComposer
extends MessageComposer {
    private final RoomTradeUser tradeUser;

    public TradeAcceptedComposer(RoomTradeUser tradeUser) {
        this.tradeUser = tradeUser;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2568);
        this.response.appendInt(this.tradeUser.getUserId());
        this.response.appendInt(this.tradeUser.getAccepted());
        return this.response;
    }

    public RoomTradeUser getTradeUser() {
        return this.tradeUser;
    }
}

