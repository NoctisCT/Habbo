/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.interactions.InteractionYoutubeTV;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.outgoing.rooms.items.youtube.YoutubeVideoComposer;

public class YoutubeAdvanceVideo
implements Runnable {
    private final InteractionYoutubeTV tv;

    public YoutubeAdvanceVideo(InteractionYoutubeTV tv) {
        this.tv = tv;
    }

    @Override
    public void run() {
        if (this.tv.autoAdvance == null) {
            return;
        }
        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.tv.getRoomId());
        if (room == null) {
            return;
        }
        int nextIndex = this.tv.currentPlaylist.getVideos().indexOf(this.tv.currentVideo) + 1;
        if (nextIndex >= this.tv.currentPlaylist.getVideos().size()) {
            nextIndex = 0;
        }
        this.tv.currentVideo = this.tv.currentPlaylist.getVideos().get(nextIndex);
        this.tv.startedWatchingAt = Emulator.getIntUnixTimestamp();
        this.tv.offset = 0;
        room.updateItem(this.tv);
        room.sendComposer(new YoutubeVideoComposer(this.tv.getId(), this.tv.currentVideo, true, 0).compose());
        this.tv.autoAdvance = Emulator.getThreading().run(new YoutubeAdvanceVideo(this.tv), (long)this.tv.currentVideo.getDuration() * 1000L);
    }
}

