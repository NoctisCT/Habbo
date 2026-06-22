/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.plugin.events.pets;

import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.plugin.events.pets.PetEvent;

public class PetTalkEvent
extends PetEvent {
    public RoomChatMessage message;

    public PetTalkEvent(Pet pet, RoomChatMessage message) {
        super(pet);
        this.message = message;
    }
}

