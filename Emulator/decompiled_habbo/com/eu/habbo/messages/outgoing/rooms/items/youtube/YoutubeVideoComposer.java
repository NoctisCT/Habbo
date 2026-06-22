/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms.items.youtube;

import com.eu.habbo.habbohotel.items.YoutubeManager;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class YoutubeVideoComposer
extends MessageComposer {
    private final int itemId;
    private final YoutubeManager.YoutubeVideo video;
    private final boolean playing;
    private final int startTime;

    public YoutubeVideoComposer(int itemId, YoutubeManager.YoutubeVideo video, boolean playing, int startTime) {
        this.itemId = itemId;
        this.video = video;
        this.playing = playing;
        this.startTime = startTime;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1411);
        this.response.appendInt(this.itemId);
        this.response.appendString(this.video.getId());
        this.response.appendInt(this.startTime);
        this.response.appendInt(this.video.getDuration());
        this.response.appendInt(this.playing ? 1 : 2);
        return this.response;
    }

    public int getItemId() {
        return this.itemId;
    }

    public YoutubeManager.YoutubeVideo getVideo() {
        return this.video;
    }

    public boolean isPlaying() {
        return this.playing;
    }

    public int getStartTime() {
        return this.startTime;
    }
}

