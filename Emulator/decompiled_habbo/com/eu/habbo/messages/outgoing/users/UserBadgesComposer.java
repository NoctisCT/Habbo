/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.users;

import com.eu.habbo.habbohotel.users.HabboBadge;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import java.util.ArrayList;

public class UserBadgesComposer
extends MessageComposer {
    private final ArrayList<HabboBadge> badges;
    private final int habbo;

    public UserBadgesComposer(ArrayList<HabboBadge> badges, int habbo) {
        this.badges = badges;
        this.habbo = habbo;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1087);
        this.response.appendInt(this.habbo);
        ArrayList<HabboBadge> arrayList = this.badges;
        synchronized (arrayList) {
            this.response.appendInt(this.badges.size());
            for (HabboBadge badge : this.badges) {
                this.response.appendInt(badge.getSlot());
                this.response.appendString(badge.getCode());
            }
        }
        return this.response;
    }

    public ArrayList<HabboBadge> getBadges() {
        return this.badges;
    }

    public int getHabbo() {
        return this.habbo;
    }
}

