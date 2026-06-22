/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.friends;

import com.eu.habbo.habbohotel.messenger.FriendRequest;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import gnu.trove.set.hash.THashSet;

public class LoadFriendRequestsComposer
extends MessageComposer {
    private final Habbo habbo;

    public LoadFriendRequestsComposer(Habbo habbo) {
        this.habbo = habbo;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(280);
        THashSet<FriendRequest> tHashSet = this.habbo.getMessenger().getFriendRequests();
        synchronized (tHashSet) {
            this.response.appendInt(this.habbo.getMessenger().getFriendRequests().size());
            this.response.appendInt(this.habbo.getMessenger().getFriendRequests().size());
            for (FriendRequest friendRequest : this.habbo.getMessenger().getFriendRequests()) {
                this.response.appendInt(friendRequest.getId());
                this.response.appendString(friendRequest.getUsername());
                this.response.appendString(friendRequest.getLook());
            }
        }
        return this.response;
    }

    public Habbo getHabbo() {
        return this.habbo;
    }
}

