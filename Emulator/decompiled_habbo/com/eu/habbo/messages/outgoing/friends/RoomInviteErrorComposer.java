/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.friends;

import com.eu.habbo.habbohotel.messenger.MessengerBuddy;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.hash.THashSet;

public class RoomInviteErrorComposer
extends MessageComposer {
    private final int errorCode;
    private final THashSet<MessengerBuddy> buddies;

    public RoomInviteErrorComposer(int errorCode, THashSet<MessengerBuddy> buddies) {
        this.errorCode = errorCode;
        this.buddies = buddies;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(462);
        this.response.appendInt(this.errorCode);
        this.response.appendInt(this.buddies.size());
        this.buddies.forEach(new TObjectProcedure<MessengerBuddy>(){

            @Override
            public boolean execute(MessengerBuddy object) {
                RoomInviteErrorComposer.this.response.appendInt(object.getId());
                return true;
            }
        });
        return this.response;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public THashSet<MessengerBuddy> getBuddies() {
        return this.buddies;
    }
}

