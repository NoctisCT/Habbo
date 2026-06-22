/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms.items.rentablespaces;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.interactions.InteractionRentableSpace;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class RentableSpaceInfoComposer
extends MessageComposer {
    public static final int SPACE_ALREADY_RENTED = 100;
    public static final int SPACE_EXTEND_NOT_RENTED = 101;
    public static final int SPACE_EXTEND_NOT_RENTED_BY_YOU = 102;
    public static final int CAN_RENT_ONLY_ONE_SPACE = 103;
    public static final int NOT_ENOUGH_CREDITS = 200;
    public static final int NOT_ENOUGH_PIXELS = 201;
    public static final int CANT_RENT_NO_PERMISSION = 202;
    public static final int CANT_RENT_NO_HABBO_CLUB = 203;
    public static final int CANT_RENT = 300;
    public static final int CANT_RENT_GENERIC = 400;
    private final Habbo habbo;
    private final HabboItem item;
    private final int errorCode;

    public RentableSpaceInfoComposer(Habbo habbo, HabboItem item) {
        this.habbo = habbo;
        this.item = item;
        this.errorCode = 0;
    }

    public RentableSpaceInfoComposer(Habbo habbo, HabboItem item, int errorCode) {
        this.habbo = habbo;
        this.item = item;
        this.errorCode = errorCode;
    }

    @Override
    protected ServerMessage composeInternal() {
        if (!(this.item instanceof InteractionRentableSpace)) {
            return null;
        }
        this.response.init(3559);
        this.response.appendBoolean(((InteractionRentableSpace)this.item).isRented());
        this.response.appendInt(this.errorCode);
        this.response.appendInt(((InteractionRentableSpace)this.item).getRenterId());
        this.response.appendString(((InteractionRentableSpace)this.item).getRenterName());
        this.response.appendInt(((InteractionRentableSpace)this.item).getEndTimestamp() - Emulator.getIntUnixTimestamp());
        this.response.appendInt(((InteractionRentableSpace)this.item).rentCost());
        return this.response;
    }

    public Habbo getHabbo() {
        return this.habbo;
    }

    public HabboItem getItem() {
        return this.item;
    }

    public int getErrorCode() {
        return this.errorCode;
    }
}

