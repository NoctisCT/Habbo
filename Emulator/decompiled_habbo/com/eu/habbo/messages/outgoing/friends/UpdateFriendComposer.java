/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.friends;

import com.eu.habbo.habbohotel.messenger.MessengerBuddy;
import com.eu.habbo.habbohotel.messenger.MessengerCategory;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboGender;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class UpdateFriendComposer
extends MessageComposer {
    private Collection<MessengerBuddy> buddies;
    private Habbo habbo;
    private int action;

    public UpdateFriendComposer(Habbo habbo, MessengerBuddy buddy, Integer action) {
        this.habbo = habbo;
        this.buddies = Collections.singletonList(buddy);
        this.action = action;
    }

    public UpdateFriendComposer(Habbo habbo, Collection<MessengerBuddy> buddies, Integer action) {
        this.habbo = habbo;
        this.buddies = buddies;
        this.action = action;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2800);
        if (this.habbo != null && !this.habbo.getHabboInfo().getMessengerCategories().isEmpty()) {
            List<MessengerCategory> messengerCategories = this.habbo.getHabboInfo().getMessengerCategories();
            this.response.appendInt(messengerCategories.size());
            for (MessengerCategory mc : messengerCategories) {
                this.response.appendInt(mc.getId());
                this.response.appendString(mc.getName());
            }
        } else {
            this.response.appendInt(0);
        }
        this.response.appendInt(this.buddies.size());
        for (MessengerBuddy buddy : this.buddies) {
            if (buddy == null) continue;
            this.response.appendInt(this.action);
            this.response.appendInt(buddy.getId());
            if (this.action == -1) continue;
            this.response.appendString(buddy.getUsername());
            this.response.appendInt(buddy.getGender().equals((Object)HabboGender.M) ? 0 : 1);
            this.response.appendBoolean(buddy.getOnline() == 1);
            this.response.appendBoolean(buddy.inRoom());
            this.response.appendString(buddy.getLook());
            this.response.appendInt(buddy.getCategoryId());
            this.response.appendString(buddy.getMotto());
            this.response.appendString("");
            this.response.appendString("");
            this.response.appendBoolean(false);
            this.response.appendBoolean(false);
            this.response.appendBoolean(false);
            this.response.appendShort(buddy.getRelation());
        }
        return this.response;
    }

    public Collection<MessengerBuddy> getBuddies() {
        return this.buddies;
    }

    public Habbo getHabbo() {
        return this.habbo;
    }

    public int getAction() {
        return this.action;
    }
}

