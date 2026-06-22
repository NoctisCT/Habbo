/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.plugin.events.users.subscriptions;

import com.eu.habbo.habbohotel.users.subscriptions.Subscription;
import com.eu.habbo.plugin.Event;

public class UserSubscriptionExpiredEvent
extends Event {
    public final int userId;
    public final Subscription subscription;

    public UserSubscriptionExpiredEvent(int userId, Subscription subscription) {
        this.userId = userId;
        this.subscription = subscription;
    }
}

