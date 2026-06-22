/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.users.subscriptions;

import com.eu.habbo.Emulator;
import com.eu.habbo.core.Scheduler;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.subscriptions.Subscription;
import com.eu.habbo.habbohotel.users.subscriptions.SubscriptionHabboClub;
import com.eu.habbo.plugin.events.users.subscriptions.UserSubscriptionExpiredEvent;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubscriptionScheduler
extends Scheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionScheduler.class);

    public SubscriptionScheduler() {
        super(Emulator.getConfig().getInt("subscriptions.scheduler.interval", 10));
        this.reloadConfig();
    }

    public void reloadConfig() {
        if (Emulator.getConfig().getBoolean("subscriptions.scheduler.enabled", true)) {
            if (this.disposed) {
                this.disposed = false;
                this.run();
            }
        } else {
            this.disposed = true;
        }
    }

    @Override
    public void run() {
        super.run();
        for (Map.Entry<Integer, Habbo> map : Emulator.getGameEnvironment().getHabboManager().getOnlineHabbos().entrySet()) {
            Habbo habbo = map.getValue();
            try {
                if (habbo == null) continue;
                for (Subscription subscription : habbo.getHabboStats().subscriptions) {
                    if (!subscription.isActive() || subscription.getRemaining() >= 0 || Emulator.getPluginManager().fireEvent(new UserSubscriptionExpiredEvent(habbo.getHabboInfo().getId(), subscription)).isCancelled()) continue;
                    subscription.onExpired();
                    subscription.setActive(false);
                }
            }
            catch (Exception e) {
                LOGGER.error("Caught exception", e);
            }
        }
        if (SubscriptionHabboClub.HC_PAYDAY_ENABLED && !SubscriptionHabboClub.isExecuting && SubscriptionHabboClub.HC_PAYDAY_NEXT_DATE < Emulator.getIntUnixTimestamp()) {
            SubscriptionHabboClub.executePayDay();
        }
    }

    @Override
    public boolean isDisposed() {
        return this.disposed;
    }

    @Override
    public void setDisposed(boolean disposed) {
        this.disposed = disposed;
    }
}

