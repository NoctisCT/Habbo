/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.incoming.catalog;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.catalog.PetNameErrorComposer;
import org.apache.commons.lang3.StringUtils;

public class CheckPetNameEvent
extends MessageHandler {
    public static int PET_NAME_LENGTH_MINIMUM = Emulator.getConfig().getInt("hotel.pets.name.length.min");
    public static int PET_NAME_LENGTH_MAXIMUM = Emulator.getConfig().getInt("hotel.pets.name.length.max");

    @Override
    public void handle() throws Exception {
        String petName = this.packet.readString();
        if (petName.length() < PET_NAME_LENGTH_MINIMUM) {
            this.client.sendResponse(new PetNameErrorComposer(2, "" + PET_NAME_LENGTH_MINIMUM));
        } else if (petName.length() > PET_NAME_LENGTH_MAXIMUM) {
            this.client.sendResponse(new PetNameErrorComposer(1, "" + PET_NAME_LENGTH_MAXIMUM));
        } else if (!StringUtils.isAlphanumeric(petName)) {
            this.client.sendResponse(new PetNameErrorComposer(3, petName));
        } else {
            this.client.sendResponse(new PetNameErrorComposer(0, petName));
        }
    }
}

