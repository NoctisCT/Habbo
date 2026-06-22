/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionCrackable;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionRedeemableSubscriptionBox
extends InteractionCrackable {
    public InteractionRedeemableSubscriptionBox(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionRedeemableSubscriptionBox(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean userRequiredToBeAdjacent() {
        return false;
    }
}

