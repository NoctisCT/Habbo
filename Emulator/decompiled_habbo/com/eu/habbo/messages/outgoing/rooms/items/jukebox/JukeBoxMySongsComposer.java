/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms.items.jukebox;

import com.eu.habbo.habbohotel.items.interactions.InteractionMusicDisc;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import java.util.List;

public class JukeBoxMySongsComposer
extends MessageComposer {
    private final List<InteractionMusicDisc> items;

    public JukeBoxMySongsComposer(List<InteractionMusicDisc> items) {
        this.items = items;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2602);
        this.response.appendInt(this.items.size());
        for (HabboItem habboItem : this.items) {
            this.response.appendInt(habboItem.getId());
            this.response.appendInt(((InteractionMusicDisc)habboItem).getSongId());
        }
        return this.response;
    }

    public List<InteractionMusicDisc> getItems() {
        return this.items;
    }
}

