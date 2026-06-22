/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.users;

import com.eu.habbo.habbohotel.messenger.MessengerBuddy;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProfileFriendsComposer
extends MessageComposer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileFriendsComposer.class);
    private final List<MessengerBuddy> lovers = new ArrayList<MessengerBuddy>();
    private final List<MessengerBuddy> friends = new ArrayList<MessengerBuddy>();
    private final List<MessengerBuddy> haters = new ArrayList<MessengerBuddy>();
    private final int userId;

    public ProfileFriendsComposer(THashMap<Integer, THashSet<MessengerBuddy>> map, int userId) {
        this.lovers.addAll((Collection<MessengerBuddy>)map.get(1));
        this.friends.addAll((Collection<MessengerBuddy>)map.get(2));
        this.haters.addAll((Collection<MessengerBuddy>)map.get(3));
        this.userId = userId;
    }

    public ProfileFriendsComposer(Habbo habbo) {
        try {
            for (Map.Entry<Integer, MessengerBuddy> map : habbo.getMessenger().getFriends().entrySet()) {
                if (map.getValue().getRelation() == 0) continue;
                switch (map.getValue().getRelation()) {
                    case 1: {
                        this.lovers.add(map.getValue());
                        break;
                    }
                    case 2: {
                        this.friends.add(map.getValue());
                        break;
                    }
                    case 3: {
                        this.haters.add(map.getValue());
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error("Caught exception", e);
        }
        this.userId = habbo.getHabboInfo().getId();
    }

    @Override
    protected ServerMessage composeInternal() {
        try {
            this.response.init(2016);
            this.response.appendInt(this.userId);
            int total = 0;
            if (!this.lovers.isEmpty()) {
                ++total;
            }
            if (!this.friends.isEmpty()) {
                ++total;
            }
            if (!this.haters.isEmpty()) {
                ++total;
            }
            this.response.appendInt(total);
            Random random = new Random();
            if (!this.lovers.isEmpty()) {
                int loversIndex = random.nextInt(this.lovers.size());
                this.response.appendInt(1);
                this.response.appendInt(this.lovers.size());
                this.response.appendInt(this.lovers.get(loversIndex).getId());
                this.response.appendString(this.lovers.get(loversIndex).getUsername());
                this.response.appendString(this.lovers.get(loversIndex).getLook());
            }
            if (!this.friends.isEmpty()) {
                int friendsIndex = random.nextInt(this.friends.size());
                this.response.appendInt(2);
                this.response.appendInt(this.friends.size());
                this.response.appendInt(this.friends.get(friendsIndex).getId());
                this.response.appendString(this.friends.get(friendsIndex).getUsername());
                this.response.appendString(this.friends.get(friendsIndex).getLook());
            }
            if (!this.haters.isEmpty()) {
                int hatersIndex = random.nextInt(this.haters.size());
                this.response.appendInt(3);
                this.response.appendInt(this.haters.size());
                this.response.appendInt(this.haters.get(hatersIndex).getId());
                this.response.appendString(this.haters.get(hatersIndex).getUsername());
                this.response.appendString(this.haters.get(hatersIndex).getLook());
            }
        }
        catch (Exception e) {
            LOGGER.error("Caught exception", e);
        }
        return this.response;
    }

    public List<MessengerBuddy> getLovers() {
        return this.lovers;
    }

    public List<MessengerBuddy> getFriends() {
        return this.friends;
    }

    public List<MessengerBuddy> getHaters() {
        return this.haters;
    }

    public int getUserId() {
        return this.userId;
    }
}

