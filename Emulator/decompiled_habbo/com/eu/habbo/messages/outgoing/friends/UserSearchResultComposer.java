/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.friends;

import com.eu.habbo.habbohotel.messenger.MessengerBuddy;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import gnu.trove.set.hash.THashSet;
import java.util.ArrayList;
import java.util.Comparator;

public class UserSearchResultComposer
extends MessageComposer {
    private final THashSet<MessengerBuddy> users;
    private final THashSet<MessengerBuddy> friends;
    private final Habbo habbo;
    private static Comparator COMPARATOR = Comparator.comparing(b -> b.getUsername().length()).thenComparing((b, b2) -> b.getUsername().compareToIgnoreCase(b2.getUsername()));

    public UserSearchResultComposer(THashSet<MessengerBuddy> users, THashSet<MessengerBuddy> friends, Habbo habbo) {
        this.users = users;
        this.friends = friends;
        this.habbo = habbo;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(973);
        ArrayList<MessengerBuddy> u = new ArrayList<MessengerBuddy>();
        for (MessengerBuddy buddy : this.users) {
            if (this.inFriendList(buddy)) continue;
            u.add(buddy);
        }
        ArrayList<MessengerBuddy> friends = new ArrayList<MessengerBuddy>(this.friends);
        u.sort(COMPARATOR);
        friends.sort(COMPARATOR);
        this.response.appendInt(this.friends.size());
        for (MessengerBuddy buddy : this.friends) {
            this.response.appendInt(buddy.getId());
            this.response.appendString(buddy.getUsername());
            this.response.appendString(buddy.getMotto());
            this.response.appendBoolean(false);
            this.response.appendBoolean(false);
            this.response.appendString("");
            this.response.appendInt(1);
            this.response.appendString(buddy.getLook());
            this.response.appendString("");
        }
        this.response.appendInt(u.size());
        for (MessengerBuddy buddy : u) {
            this.response.appendInt(buddy.getId());
            this.response.appendString(buddy.getUsername());
            this.response.appendString(buddy.getMotto());
            this.response.appendBoolean(false);
            this.response.appendBoolean(false);
            this.response.appendString("");
            this.response.appendInt(1);
            this.response.appendString(buddy.getOnline() == 1 ? buddy.getLook() : "");
            this.response.appendString("");
        }
        return this.response;
    }

    private boolean inFriendList(MessengerBuddy buddy) {
        for (MessengerBuddy friend : this.friends) {
            if (!friend.getUsername().equals(buddy.getUsername())) continue;
            return true;
        }
        return false;
    }

    public THashSet<MessengerBuddy> getUsers() {
        return this.users;
    }

    public THashSet<MessengerBuddy> getFriends() {
        return this.friends;
    }

    public Habbo getHabbo() {
        return this.habbo;
    }
}

