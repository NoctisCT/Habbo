/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms.items.jukebox;

import com.eu.habbo.habbohotel.items.SoundTrack;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class JukeBoxTrackCodeComposer
extends MessageComposer {
    private final SoundTrack track;

    public JukeBoxTrackCodeComposer(SoundTrack track) {
        this.track = track;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1381);
        this.response.appendString(this.track.getCode());
        this.response.appendInt(this.track.getId());
        return this.response;
    }

    public SoundTrack getTrack() {
        return this.track;
    }
}

