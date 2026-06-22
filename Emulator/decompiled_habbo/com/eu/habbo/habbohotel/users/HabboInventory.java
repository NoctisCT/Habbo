/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.users;

import com.eu.habbo.habbohotel.catalog.marketplace.MarketPlace;
import com.eu.habbo.habbohotel.catalog.marketplace.MarketPlaceOffer;
import com.eu.habbo.habbohotel.catalog.marketplace.MarketPlaceState;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.inventory.BadgesComponent;
import com.eu.habbo.habbohotel.users.inventory.BotsComponent;
import com.eu.habbo.habbohotel.users.inventory.EffectsComponent;
import com.eu.habbo.habbohotel.users.inventory.ItemsComponent;
import com.eu.habbo.habbohotel.users.inventory.PetsComponent;
import com.eu.habbo.habbohotel.users.inventory.WardrobeComponent;
import gnu.trove.set.hash.THashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HabboInventory {
    private static final Logger LOGGER = LoggerFactory.getLogger(HabboInventory.class);
    public static int MAXIMUM_ITEMS = 10000;
    private final THashSet<MarketPlaceOffer> items;
    private final Habbo habbo;
    private WardrobeComponent wardrobeComponent;
    private BadgesComponent badgesComponent;
    private BotsComponent botsComponent;
    private EffectsComponent effectsComponent;
    private ItemsComponent itemsComponent;
    private PetsComponent petsComponent;

    public HabboInventory(Habbo habbo) {
        this.habbo = habbo;
        try {
            this.badgesComponent = new BadgesComponent(this.habbo);
        }
        catch (Exception e) {
            LOGGER.error("Caught exception", e);
        }
        try {
            this.botsComponent = new BotsComponent(this.habbo);
        }
        catch (Exception e) {
            LOGGER.error("Caught exception", e);
        }
        try {
            this.effectsComponent = new EffectsComponent(this.habbo);
        }
        catch (Exception e) {
            LOGGER.error("Caught exception", e);
        }
        try {
            this.itemsComponent = new ItemsComponent(this, this.habbo);
        }
        catch (Exception e) {
            LOGGER.error("Caught exception", e);
        }
        try {
            this.petsComponent = new PetsComponent(this.habbo);
        }
        catch (Exception e) {
            LOGGER.error("Caught exception", e);
        }
        try {
            this.wardrobeComponent = new WardrobeComponent(this.habbo);
        }
        catch (Exception e) {
            LOGGER.error("Caught exception", e);
        }
        this.items = MarketPlace.getOwnOffers(this.habbo);
    }

    public WardrobeComponent getWardrobeComponent() {
        return this.wardrobeComponent;
    }

    public void setWardrobeComponent(WardrobeComponent wardrobeComponent) {
        this.wardrobeComponent = wardrobeComponent;
    }

    public BadgesComponent getBadgesComponent() {
        return this.badgesComponent;
    }

    public void setBadgesComponent(BadgesComponent badgesComponent) {
        this.badgesComponent = badgesComponent;
    }

    public BotsComponent getBotsComponent() {
        return this.botsComponent;
    }

    public void setBotsComponent(BotsComponent botsComponent) {
        this.botsComponent = botsComponent;
    }

    public EffectsComponent getEffectsComponent() {
        return this.effectsComponent;
    }

    public void setEffectsComponent(EffectsComponent effectsComponent) {
        this.effectsComponent = effectsComponent;
    }

    public ItemsComponent getItemsComponent() {
        return this.itemsComponent;
    }

    public void setItemsComponent(ItemsComponent itemsComponent) {
        this.itemsComponent = itemsComponent;
    }

    public PetsComponent getPetsComponent() {
        return this.petsComponent;
    }

    public void setPetsComponent(PetsComponent petsComponent) {
        this.petsComponent = petsComponent;
    }

    public void dispose() {
        this.badgesComponent.dispose();
        this.botsComponent.dispose();
        this.effectsComponent.dispose();
        this.itemsComponent.dispose();
        this.petsComponent.dispose();
        this.wardrobeComponent.dispose();
        this.badgesComponent = null;
        this.botsComponent = null;
        this.effectsComponent = null;
        this.itemsComponent = null;
        this.petsComponent = null;
        this.wardrobeComponent = null;
    }

    public void addMarketplaceOffer(MarketPlaceOffer marketPlaceOffer) {
        this.items.add(marketPlaceOffer);
    }

    public void removeMarketplaceOffer(MarketPlaceOffer marketPlaceOffer) {
        this.items.remove(marketPlaceOffer);
    }

    public THashSet<MarketPlaceOffer> getMarketplaceItems() {
        return this.items;
    }

    public int getSoldPriceTotal() {
        int i = 0;
        for (MarketPlaceOffer offer : this.items) {
            if (!offer.getState().equals((Object)MarketPlaceState.SOLD)) continue;
            i += offer.getPrice();
        }
        return i;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public MarketPlaceOffer getOffer(int id) {
        THashSet<MarketPlaceOffer> tHashSet = this.items;
        synchronized (tHashSet) {
            for (MarketPlaceOffer offer : this.items) {
                if (offer.getOfferId() != id) continue;
                return offer;
            }
        }
        return null;
    }

    public Habbo getHabbo() {
        return this.habbo;
    }
}

