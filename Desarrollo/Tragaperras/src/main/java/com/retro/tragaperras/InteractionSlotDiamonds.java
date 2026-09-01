package com.retro.tragaperras;

import com.eu.habbo.habbohotel.items.Item;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class InteractionSlotDiamonds extends InteractionSlotMachine
{
    public InteractionSlotDiamonds(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public InteractionSlotDiamonds(
            int id,
            int userId,
            Item item,
            String extradata,
            int limitedStack,
            int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public SlotCurrency getSlotCurrency()
    {
        return SlotCurrency.DIAMONDS;
    }
}