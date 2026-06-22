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
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.set.hash.THashSet;
import java.util.NoSuchElementException;

public class RoomFloorItemsComposer
extends MessageComposer {
    private final TIntObjectMap<String> furniOwnerNames;
    private final THashSet<? extends HabboItem> items;

    public RoomFloorItemsComposer(TIntObjectMap<String> furniOwnerNames, THashSet<? extends HabboItem> items) {
        this.furniOwnerNames = furniOwnerNames;
        this.items = items;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1778);
        TIntObjectIterator<String> iterator = this.furniOwnerNames.iterator();
        this.response.appendInt(this.furniOwnerNames.size());
        int i = this.furniOwnerNames.size();
        while (i-- > 0) {
            try {
                iterator.advance();
                this.response.appendInt(iterator.key());
                this.response.appendString(iterator.value());
            }
            catch (NoSuchElementException noSuchElementException) {
                // empty catch block
                break;
            }
        }
        this.response.appendInt(this.items.size());
        for (HabboItem habboItem : this.items) {
            habboItem.serializeFloorData(this.response);
            this.response.appendInt(habboItem instanceof InteractionGift ? ((InteractionGift)habboItem).getColorId() * 1000 + ((InteractionGift)habboItem).getRibbonId() : (habboItem instanceof InteractionMusicDisc ? ((InteractionMusicDisc)habboItem).getSongId() : 1));
            habboItem.serializeExtradata(this.response);
            this.response.appendInt(-1);
            this.response.appendInt(habboItem instanceof InteractionTeleport || habboItem instanceof InteractionSwitch || habboItem instanceof InteractionSwitchRemoteControl || habboItem instanceof InteractionVendingMachine || habboItem instanceof InteractionInformationTerminal || habboItem instanceof InteractionPostIt || habboItem instanceof InteractionPuzzleBox ? 2 : (habboItem.isUsable() ? 1 : 0));
            this.response.appendInt(habboItem.getUserId());
        }
        return this.response;
    }

    public TIntObjectMap<String> getFurniOwnerNames() {
        return this.furniOwnerNames;
    }

    public THashSet<? extends HabboItem> getItems() {
        return this.items;
    }
}

