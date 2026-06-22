/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.guides;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guides.GuideTour;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class GuideSessionAttachedComposer
extends MessageComposer {
    private final GuideTour tour;
    private final boolean isHelper;

    public GuideSessionAttachedComposer(GuideTour tour, boolean isHelper) {
        this.tour = tour;
        this.isHelper = isHelper;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1591);
        this.response.appendBoolean(this.isHelper);
        this.response.appendInt(1);
        this.response.appendString(this.tour.getHelpRequest());
        this.response.appendInt(this.isHelper ? 60 : Emulator.getGameEnvironment().getGuideManager().getAverageWaitingTime());
        return this.response;
    }

    public GuideTour getTour() {
        return this.tour;
    }

    public boolean isHelper() {
        return this.isHelper;
    }
}

