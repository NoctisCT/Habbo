/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.habbohotel.catalog.ClubOffer;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class ExtendClubMessageComposer
extends MessageComposer {
    private final Habbo habbo;
    private final ClubOffer offer;
    private final int normalCreditCost;
    private final int normalPointsCost;
    private final int pointsType;
    private final int daysRemaining;

    public ExtendClubMessageComposer(Habbo habbo, ClubOffer offer, int normalCreditCost, int normalPointsCost, int pointsType, int daysRemaining) {
        this.habbo = habbo;
        this.offer = offer;
        this.normalCreditCost = normalCreditCost;
        this.normalPointsCost = normalPointsCost;
        this.pointsType = pointsType;
        this.daysRemaining = daysRemaining;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(3964);
        this.offer.serialize(this.response, this.habbo.getHabboStats().getClubExpireTimestamp());
        this.response.appendInt(this.normalCreditCost);
        this.response.appendInt(this.normalPointsCost);
        this.response.appendInt(this.pointsType);
        this.response.appendInt(this.daysRemaining);
        return this.response;
    }

    public Habbo getHabbo() {
        return this.habbo;
    }

    public ClubOffer getOffer() {
        return this.offer;
    }

    public int getNormalCreditCost() {
        return this.normalCreditCost;
    }

    public int getNormalPointsCost() {
        return this.normalPointsCost;
    }

    public int getPointsType() {
        return this.pointsType;
    }

    public int getDaysRemaining() {
        return this.daysRemaining;
    }
}

