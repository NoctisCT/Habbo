/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.events.resolution;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class NewYearResolutionProgressComposer
extends MessageComposer {
    private final int stuffId;
    private final int achievementId;
    private final String achievementName;
    private final int currentProgress;
    private final int progressNeeded;
    private final int timeLeft;

    public NewYearResolutionProgressComposer(int stuffId, int achievementId, String achievementName, int currentProgress, int progressNeeded, int timeLeft) {
        this.stuffId = stuffId;
        this.achievementId = achievementId;
        this.achievementName = achievementName;
        this.currentProgress = currentProgress;
        this.progressNeeded = progressNeeded;
        this.timeLeft = timeLeft;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(3370);
        this.response.appendInt(this.stuffId);
        this.response.appendInt(this.achievementId);
        this.response.appendString(this.achievementName);
        this.response.appendInt(this.currentProgress);
        this.response.appendInt(this.progressNeeded);
        this.response.appendInt(this.timeLeft);
        return this.response;
    }

    public int getStuffId() {
        return this.stuffId;
    }

    public int getAchievementId() {
        return this.achievementId;
    }

    public String getAchievementName() {
        return this.achievementName;
    }

    public int getCurrentProgress() {
        return this.currentProgress;
    }

    public int getProgressNeeded() {
        return this.progressNeeded;
    }

    public int getTimeLeft() {
        return this.timeLeft;
    }
}

