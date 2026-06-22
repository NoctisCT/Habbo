/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.catalog;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class ClubCenterDataComposer
extends MessageComposer {
    public final int currentHcStreak;
    public final String firstSubDate;
    public final double kickbackPercentage;
    public final int totalCreditsMissed;
    public final int totalCreditsRewarded;
    public final int totalCreditsSpent;
    public final int creditRewardForStreakBonus;
    public final int creditRewardForMonthlySpent;
    public final int timeUntilPayday;

    public ClubCenterDataComposer(int currentHcStreak, String firstSubDate, double kickbackPercentage, int totalCreditsMissed, int totalCreditsRewarded, int totalCreditsSpent, int creditRewardForStreakBonus, int creditRewardForMonthlySpent, int timeUntilPayday) {
        this.currentHcStreak = currentHcStreak;
        this.firstSubDate = firstSubDate;
        this.kickbackPercentage = kickbackPercentage;
        this.totalCreditsMissed = totalCreditsMissed;
        this.totalCreditsRewarded = totalCreditsRewarded;
        this.totalCreditsSpent = totalCreditsSpent;
        this.creditRewardForStreakBonus = creditRewardForStreakBonus;
        this.creditRewardForMonthlySpent = creditRewardForMonthlySpent;
        this.timeUntilPayday = timeUntilPayday;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(3277);
        this.response.appendInt(this.currentHcStreak);
        this.response.appendString(this.firstSubDate);
        this.response.appendDouble(this.kickbackPercentage);
        this.response.appendInt(this.totalCreditsMissed);
        this.response.appendInt(this.totalCreditsRewarded);
        this.response.appendInt(this.totalCreditsSpent);
        this.response.appendInt(this.creditRewardForStreakBonus);
        this.response.appendInt(this.creditRewardForMonthlySpent);
        this.response.appendInt(this.timeUntilPayday);
        return this.response;
    }

    public int getCurrentHcStreak() {
        return this.currentHcStreak;
    }

    public String getFirstSubDate() {
        return this.firstSubDate;
    }

    public double getKickbackPercentage() {
        return this.kickbackPercentage;
    }

    public int getTotalCreditsMissed() {
        return this.totalCreditsMissed;
    }

    public int getTotalCreditsRewarded() {
        return this.totalCreditsRewarded;
    }

    public int getTotalCreditsSpent() {
        return this.totalCreditsSpent;
    }

    public int getCreditRewardForStreakBonus() {
        return this.creditRewardForStreakBonus;
    }

    public int getCreditRewardForMonthlySpent() {
        return this.creditRewardForMonthlySpent;
    }

    public int getTimeUntilPayday() {
        return this.timeUntilPayday;
    }
}

