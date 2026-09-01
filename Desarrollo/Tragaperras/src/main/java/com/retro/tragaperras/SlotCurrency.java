package com.retro.tragaperras;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;

public enum SlotCurrency
{
    CREDITS("credits", "holo_slot_credits", "creditos"),
    DIAMONDS("diamonds", "holo_slot_diamonds", "diamantes"),
    DUCKETS("duckets", "holo_slot_duckets", "duckets");

    private final String key;
    private final String interactionType;
    private final String displayName;

    SlotCurrency(
            String key,
            String interactionType,
            String displayName)
    {
        this.key = key;
        this.interactionType = interactionType;
        this.displayName = displayName;
    }

    public String getKey()
    {
        return this.key;
    }

    public String getInteractionType()
    {
        return this.interactionType;
    }

    public String getDisplayName()
    {
        return this.displayName;
    }

    public int getClientType()
    {
        switch(this)
        {
            case CREDITS:
                return -1;

            case DIAMONDS:
                return Emulator.getConfig().getInt(
                        "seasonal.primary.type"
                );

            case DUCKETS:
                return 0;

            default:
                throw new IllegalStateException(
                        "Moneda no soportada: " + this.name()
                );
        }
    }

    public int getBalance(Habbo habbo)
    {
        if(habbo == null || habbo.getHabboInfo() == null)
        {
            return 0;
        }

        switch(this)
        {
            case CREDITS:
                return habbo.getHabboInfo().getCredits();

            case DIAMONDS:
                return habbo.getHabboInfo().getCurrencyAmount(
                        Emulator.getConfig().getInt("seasonal.primary.type")
                );

            case DUCKETS:
                return habbo.getHabboInfo().getCurrencyAmount(0);

            default:
                throw new IllegalStateException(
                        "Moneda no soportada: " + this.name()
                );
        }
    }

    public void applyDelta(Habbo habbo, int delta)
    {
        if(habbo == null)
        {
            throw new IllegalArgumentException("Habbo null.");
        }

        switch(this)
        {
            case CREDITS:
                habbo.giveCredits(delta);
                return;

            case DIAMONDS:
                habbo.givePoints(delta);
                return;

            case DUCKETS:
                habbo.givePoints(0, delta);
                return;

            default:
                throw new IllegalStateException(
                        "Moneda no soportada: " + this.name()
                );
        }
    }
}