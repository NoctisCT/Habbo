/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms.items.youtube;

import com.eu.habbo.habbohotel.items.YoutubeManager;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import java.util.ArrayList;

public class YoutubeDisplayListComposer
extends MessageComposer {
    private final int itemId;
    private final ArrayList<YoutubeManager.YoutubePlaylist> playlists;
    private final YoutubeManager.YoutubePlaylist currentPlaylist;

    public YoutubeDisplayListComposer(int itemId, ArrayList<YoutubeManager.YoutubePlaylist> playlists, YoutubeManager.YoutubePlaylist currentPlaylist) {
        this.itemId = itemId;
        this.playlists = playlists;
        this.currentPlaylist = currentPlaylist;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1112);
        this.response.appendInt(this.itemId);
        this.response.appendInt(this.playlists.size());
        for (YoutubeManager.YoutubePlaylist item : this.playlists) {
            this.response.appendString(item.getId());
            this.response.appendString(item.getName());
            this.response.appendString(item.getDescription());
        }
        this.response.appendString(this.currentPlaylist == null ? "" : this.currentPlaylist.getId());
        return this.response;
    }

    public int getItemId() {
        return this.itemId;
    }

    public ArrayList<YoutubeManager.YoutubePlaylist> getPlaylists() {
        return this.playlists;
    }

    public YoutubeManager.YoutubePlaylist getCurrentPlaylist() {
        return this.currentPlaylist;
    }
}

