/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.guides;

import com.eu.habbo.habbohotel.guides.GuideTour;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class GuideSessionStartedComposer
extends MessageComposer {
    private final GuideTour tour;

    public GuideSessionStartedComposer(GuideTour tour) {
        this.tour = tour;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(3209);
        this.response.appendInt(this.tour.getNoob().getHabboInfo().getId());
        this.response.appendString(this.tour.getNoob().getHabboInfo().getUsername());
        this.response.appendString(this.tour.getNoob().getHabboInfo().getLook());
        this.response.appendInt(this.tour.getHelper().getHabboInfo().getId());
        this.response.appendString(this.tour.getHelper().getHabboInfo().getUsername());
        this.response.appendString(this.tour.getHelper().getHabboInfo().getLook());
        return this.response;
    }

    public GuideTour getTour() {
        return this.tour;
    }
}

