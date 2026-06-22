/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms.items.jukebox;

import com.eu.habbo.habbohotel.items.SoundTrack;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class JukeBoxNowPlayingMessageComposer
extends MessageComposer {
    private final SoundTrack track;
    private final int playListId;
    private final int msPlayed;

    public JukeBoxNowPlayingMessageComposer(SoundTrack track, int playListId, int msPlayed) {
        this.track = track;
        this.playListId = playListId;
        this.msPlayed = msPlayed;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(469);
        if (this.track != null) {
            this.response.appendInt(this.track.getId());
            this.response.appendInt(this.playListId);
            this.response.appendInt(this.track.getId());
            this.response.appendInt(this.track.getLength());
            this.response.appendInt(this.msPlayed);
        } else {
            this.response.appendInt(-1);
            this.response.appendInt(-1);
            this.response.appendInt(-1);
            this.response.appendInt(-1);
            this.response.appendInt(-1);
        }
        return this.response;
    }

    public SoundTrack getTrack() {
        return this.track;
    }

    public int getPlayListId() {
        return this.playListId;
    }

    public int getMsPlayed() {
        return this.msPlayed;
    }
}

