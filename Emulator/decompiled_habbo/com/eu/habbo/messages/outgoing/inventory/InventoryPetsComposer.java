/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.inventory;

import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import gnu.trove.iterator.TIntObjectIterator;
import java.util.NoSuchElementException;

public class InventoryPetsComposer
extends MessageComposer {
    private final Habbo habbo;

    public InventoryPetsComposer(Habbo habbo) {
        this.habbo = habbo;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(3522);
        this.response.appendInt(1);
        this.response.appendInt(1);
        this.response.appendInt(this.habbo.getInventory().getPetsComponent().getPetsCount());
        TIntObjectIterator<Pet> petIterator = this.habbo.getInventory().getPetsComponent().getPets().iterator();
        int i = this.habbo.getInventory().getPetsComponent().getPets().size();
        while (i-- > 0) {
            try {
                petIterator.advance();
            }
            catch (NoSuchElementException e) {
                break;
            }
            petIterator.value().serialize(this.response);
        }
        return this.response;
    }

    public Habbo getHabbo() {
        return this.habbo;
    }
}

