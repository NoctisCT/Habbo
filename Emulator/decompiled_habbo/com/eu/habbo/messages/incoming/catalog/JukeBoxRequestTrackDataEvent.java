/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.incoming.catalog;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.SoundTrack;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.items.jukebox.JukeBoxTrackDataComposer;
import java.util.ArrayList;

public class JukeBoxRequestTrackDataEvent
extends MessageHandler {
    @Override
    public void handle() throws Exception {
        int count = this.packet.readInt();
        ArrayList<SoundTrack> tracks = new ArrayList<SoundTrack>(count);
        for (int i = 0; i < count; ++i) {
            SoundTrack track = Emulator.getGameEnvironment().getItemManager().getSoundTrack(this.packet.readInt());
            if (track == null) continue;
            tracks.add(track);
        }
        this.client.sendResponse(new JukeBoxTrackDataComposer(tracks));
    }
}

