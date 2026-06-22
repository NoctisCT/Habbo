/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.hotelview;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class HotelViewCommunityGoalComposer
extends MessageComposer {
    private final boolean achieved;
    private final int personalContributionScore;
    private final int personalRank;
    private final int totalAmount;
    private final int communityHighestAchievedLevel;
    private final int scoreRemainingUntilNextLevel;
    private final int percentCompletionTowardsNextLevel;
    private final String competitionName;
    private final int timeLeft;
    private final int[] rankData;

    public HotelViewCommunityGoalComposer(boolean achieved, int personalContributionScore, int personalRank, int totalAmount, int communityHighestAchievedLevel, int scoreRemainingUntilNextLevel, int percentCompletionTowardsNextLevel, String competitionName, int timeLeft, int[] rankData) {
        this.achieved = achieved;
        this.personalContributionScore = personalContributionScore;
        this.personalRank = personalRank;
        this.totalAmount = totalAmount;
        this.communityHighestAchievedLevel = communityHighestAchievedLevel;
        this.scoreRemainingUntilNextLevel = scoreRemainingUntilNextLevel;
        this.percentCompletionTowardsNextLevel = percentCompletionTowardsNextLevel;
        this.competitionName = competitionName;
        this.timeLeft = timeLeft;
        this.rankData = rankData;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2525);
        this.response.appendBoolean(this.achieved);
        this.response.appendInt(this.personalContributionScore);
        this.response.appendInt(this.personalRank);
        this.response.appendInt(this.personalRank);
        this.response.appendInt(this.totalAmount);
        this.response.appendInt(this.communityHighestAchievedLevel);
        this.response.appendInt(this.scoreRemainingUntilNextLevel);
        this.response.appendString(this.competitionName);
        this.response.appendInt(this.timeLeft);
        this.response.appendInt(this.rankData.length);
        for (int i : this.rankData) {
            this.response.appendInt(i);
        }
        return this.response;
    }

    public boolean isAchieved() {
        return this.achieved;
    }

    public int getPersonalContributionScore() {
        return this.personalContributionScore;
    }

    public int getPersonalRank() {
        return this.personalRank;
    }

    public int getTotalAmount() {
        return this.totalAmount;
    }

    public int getCommunityHighestAchievedLevel() {
        return this.communityHighestAchievedLevel;
    }

    public int getScoreRemainingUntilNextLevel() {
        return this.scoreRemainingUntilNextLevel;
    }

    public int getPercentCompletionTowardsNextLevel() {
        return this.percentCompletionTowardsNextLevel;
    }

    public String getCompetitionName() {
        return this.competitionName;
    }

    public int getTimeLeft() {
        return this.timeLeft;
    }

    public int[] getRankData() {
        return this.rankData;
    }
}

