/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.catalog.marketplace;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class MarketplaceBuyErrorComposer
extends MessageComposer {
    public static final int REFRESH = 1;
    public static final int SOLD_OUT = 2;
    public static final int UPDATES = 3;
    public static final int NOT_ENOUGH_CREDITS = 4;
    private final int errorCode;
    private final int unknown;
    private final int offerId;
    private final int price;

    public MarketplaceBuyErrorComposer(int errorCode, int unknown, int offerId, int price) {
        this.errorCode = errorCode;
        this.unknown = unknown;
        this.offerId = offerId;
        this.price = price;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2032);
        this.response.appendInt(this.errorCode);
        this.response.appendInt(this.unknown);
        this.response.appendInt(this.offerId);
        this.response.appendInt(this.price);
        return this.response;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public int getUnknown() {
        return this.unknown;
    }

    public int getOfferId() {
        return this.offerId;
    }

    public int getPrice() {
        return this.price;
    }
}

