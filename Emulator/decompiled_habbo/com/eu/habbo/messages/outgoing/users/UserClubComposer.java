/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.subscriptions.Subscription;
import com.eu.habbo.habbohotel.users.subscriptions.SubscriptionHabboClub;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class UserClubComposer
extends MessageComposer {
    private final Habbo habbo;
    private final String subscriptionType;
    private final int responseType;
    public static int RESPONSE_TYPE_NORMAL = 0;
    public static int RESPONSE_TYPE_LOGIN = 1;
    public static int RESPONSE_TYPE_PURCHASE = 2;
    public static int RESPONSE_TYPE_DISCOUNT_AVAILABLE = 3;
    public static int RESPONSE_TYPE_CITIZENSHIP_DISCOUNT = 4;

    public UserClubComposer(Habbo habbo) {
        this.habbo = habbo;
        this.subscriptionType = "HABBO_CLUB".toLowerCase();
        this.responseType = 0;
    }

    public UserClubComposer(Habbo habbo, String subscriptionType) {
        this.habbo = habbo;
        this.subscriptionType = subscriptionType;
        this.responseType = 0;
    }

    public UserClubComposer(Habbo habbo, String subscriptionType, int responseType) {
        this.habbo = habbo;
        this.subscriptionType = subscriptionType;
        this.responseType = responseType;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(954);
        this.response.appendString(this.subscriptionType.toLowerCase());
        if (Emulator.getGameEnvironment().getSubscriptionManager().getSubscriptionClass(this.subscriptionType.toUpperCase()) == null) {
            this.response.appendInt(0);
            this.response.appendInt(0);
            this.response.appendInt(0);
            this.response.appendInt(0);
            this.response.appendBoolean(false);
            this.response.appendBoolean(false);
            this.response.appendInt(0);
            this.response.appendInt(0);
            this.response.appendInt(0);
            this.response.appendInt(0);
            return this.response;
        }
        Subscription subscription = this.habbo.getHabboStats().getSubscription(this.subscriptionType);
        int days = 0;
        int minutes = 0;
        int timeRemaining = 0;
        int pastTimeAsHC = this.habbo.getHabboStats().getPastTimeAsClub();
        if (subscription != null) {
            timeRemaining = subscription.getRemaining();
            days = (int)Math.floor((double)timeRemaining / 86400.0);
            minutes = (int)Math.ceil((double)timeRemaining / 60.0);
            if (days < 1 && minutes > 0) {
                days = 1;
            }
        }
        int responseType = this.responseType <= RESPONSE_TYPE_LOGIN && timeRemaining > 0 && SubscriptionHabboClub.DISCOUNT_ENABLED && days <= SubscriptionHabboClub.DISCOUNT_DAYS_BEFORE_END ? RESPONSE_TYPE_DISCOUNT_AVAILABLE : this.responseType;
        this.response.appendInt(days);
        this.response.appendInt(0);
        this.response.appendInt(0);
        this.response.appendInt(responseType);
        this.response.appendBoolean(pastTimeAsHC > 0);
        this.response.appendBoolean(true);
        this.response.appendInt(0);
        this.response.appendInt((int)Math.floor((double)pastTimeAsHC / 86400.0));
        this.response.appendInt(minutes);
        this.response.appendInt((Emulator.getIntUnixTimestamp() - this.habbo.getHabboStats().hcMessageLastModified) / 60);
        this.habbo.getHabboStats().hcMessageLastModified = Emulator.getIntUnixTimestamp();
        return this.response;
    }

    public Habbo getHabbo() {
        return this.habbo;
    }

    public String getSubscriptionType() {
        return this.subscriptionType;
    }

    public int getResponseType() {
        return this.responseType;
    }
}

