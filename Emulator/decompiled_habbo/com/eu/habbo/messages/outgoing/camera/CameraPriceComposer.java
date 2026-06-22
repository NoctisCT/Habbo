/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.camera;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class CameraPriceComposer
extends MessageComposer {
    public final int credits;
    public final int points;
    public final int pointsType;

    public CameraPriceComposer(int credits, int points, int pointsType) {
        this.credits = credits;
        this.points = points;
        this.pointsType = pointsType;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(3878);
        this.response.appendInt(this.credits);
        this.response.appendInt(this.points);
        this.response.appendInt(this.pointsType);
        return this.response;
    }

    public int getCredits() {
        return this.credits;
    }

    public int getPoints() {
        return this.points;
    }

    public int getPointsType() {
        return this.pointsType;
    }
}

