/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms.items;

import com.eu.habbo.habbohotel.items.interactions.InteractionGift;
import com.eu.habbo.habbohotel.items.interactions.InteractionInformationTerminal;
import com.eu.habbo.habbohotel.items.interactions.InteractionMusicDisc;
import com.eu.habbo.habbohotel.items.interactions.InteractionPostIt;
import com.eu.habbo.habbohotel.items.interactions.InteractionPuzzleBox;
import com.eu.habbo.habbohotel.items.interactions.InteractionSwitch;
import com.eu.habbo.habbohotel.items.interactions.InteractionSwitchRemoteControl;
import com.eu.habbo.habbohotel.items.interactions.InteractionTeleport;
import com.eu.habbo.habbohotel.items.interactions.InteractionVendingMachine;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class AddFloorItemComposer
extends MessageComposer {
    private final HabboItem item;
    private final String itemOwnerName;

    public AddFloorItemComposer(HabboItem item, String itemOwnerName) {
        this.item = item;
        this.itemOwnerName = itemOwnerName == null ? "" : itemOwnerName;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1534);
        this.item.serializeFloorData(this.response);
        this.response.appendInt(this.item instanceof InteractionGift ? ((InteractionGift)this.item).getColorId() * 1000 + ((InteractionGift)this.item).getRibbonId() : (this.item instanceof InteractionMusicDisc ? ((InteractionMusicDisc)this.item).getSongId() : 1));
        this.item.serializeExtradata(this.response);
        this.response.appendInt(-1);
        this.response.appendInt(this.item instanceof InteractionTeleport || this.item instanceof InteractionSwitch || this.item instanceof InteractionSwitchRemoteControl || this.item instanceof InteractionVendingMachine || this.item instanceof InteractionInformationTerminal || this.item instanceof InteractionPostIt || this.item instanceof InteractionPuzzleBox ? 2 : (this.item.isUsable() ? 1 : 0));
        this.response.appendInt(this.item.getUserId());
        this.response.appendString(this.itemOwnerName);
        return this.response;
    }

    public HabboItem getItem() {
        return this.item;
    }

    public String getItemOwnerName() {
        return this.itemOwnerName;
    }
}

