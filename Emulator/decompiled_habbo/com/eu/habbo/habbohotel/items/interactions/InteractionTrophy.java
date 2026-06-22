/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionDefault;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionTrophy
extends InteractionDefault {
    public InteractionTrophy(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionTrophy(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean allowWiredResetState() {
        return false;
    }
}

