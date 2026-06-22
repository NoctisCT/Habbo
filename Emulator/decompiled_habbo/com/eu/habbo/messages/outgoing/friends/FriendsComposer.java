/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.friends;

import com.eu.habbo.habbohotel.messenger.MessengerBuddy;
import com.eu.habbo.habbohotel.users.HabboGender;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import gnu.trove.set.hash.THashSet;
import java.util.ArrayList;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FriendsComposer
extends MessageComposer {
    private static final Logger LOGGER = LoggerFactory.getLogger(FriendsComposer.class);
    private final int totalPages;
    private final int pageIndex;
    private final Collection<MessengerBuddy> friends;

    public FriendsComposer(int totalPages, int pageIndex, Collection<MessengerBuddy> friends) {
        this.totalPages = totalPages;
        this.pageIndex = pageIndex;
        this.friends = friends;
    }

    @Override
    protected ServerMessage composeInternal() {
        try {
            this.response.init(3130);
            this.response.appendInt(this.totalPages);
            this.response.appendInt(this.pageIndex);
            this.response.appendInt(this.friends.size());
            for (MessengerBuddy row : this.friends) {
                this.response.appendInt(row.getId());
                this.response.appendString(row.getUsername());
                this.response.appendInt(row.getGender().equals((Object)HabboGender.M) ? 0 : 1);
                this.response.appendBoolean(row.getOnline() == 1);
                this.response.appendBoolean(row.inRoom());
                this.response.appendString(row.getOnline() == 1 ? row.getLook() : "");
                this.response.appendInt(row.getCategoryId());
                this.response.appendString(row.getMotto());
                this.response.appendString("");
                this.response.appendString("");
                this.response.appendBoolean(false);
                this.response.appendBoolean(false);
                this.response.appendBoolean(false);
                this.response.appendShort(row.getRelation());
            }
            return this.response;
        }
        catch (Exception e) {
            LOGGER.error("Caught exception", e);
            return null;
        }
    }

    public static ArrayList<ServerMessage> getMessagesForBuddyList(Collection<MessengerBuddy> buddies) {
        ArrayList<ServerMessage> messages = new ArrayList<ServerMessage>();
        THashSet<MessengerBuddy> friends = new THashSet<MessengerBuddy>();
        int totalPages = (int)Math.ceil((double)buddies.size() / 750.0);
        int page = 0;
        for (MessengerBuddy buddy : buddies) {
            friends.add(buddy);
            if (friends.size() != 750) continue;
            messages.add(new FriendsComposer(totalPages, page, friends).compose());
            friends.clear();
            ++page;
        }
        if (page == 0 || friends.size() > 0) {
            messages.add(new FriendsComposer(totalPages, page, friends).compose());
        }
        return messages;
    }

    public int getTotalPages() {
        return this.totalPages;
    }

    public int getPageIndex() {
        return this.pageIndex;
    }

    public Collection<MessengerBuddy> getFriends() {
        return this.friends;
    }
}

