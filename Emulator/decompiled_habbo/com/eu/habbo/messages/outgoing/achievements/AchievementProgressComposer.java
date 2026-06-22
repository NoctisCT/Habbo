/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.achievements;

import com.eu.habbo.habbohotel.achievements.Achievement;
import com.eu.habbo.habbohotel.achievements.AchievementLevel;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class AchievementProgressComposer
extends MessageComposer {
    private final Habbo habbo;
    private final Achievement achievement;

    public AchievementProgressComposer(Habbo habbo, Achievement achievement) {
        this.habbo = habbo;
        this.achievement = achievement;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2107);
        int achievementProgress = this.habbo.getHabboStats().getAchievementProgress(this.achievement);
        AchievementLevel currentLevel = this.achievement.getLevelForProgress(achievementProgress);
        AchievementLevel nextLevel = this.achievement.getNextLevel(currentLevel != null ? currentLevel.level : 0);
        if (currentLevel != null && currentLevel.level == this.achievement.levels.size()) {
            nextLevel = null;
        }
        int targetLevel = 1;
        if (nextLevel != null) {
            targetLevel = nextLevel.level;
        }
        if (currentLevel != null && currentLevel.level == this.achievement.levels.size()) {
            targetLevel = currentLevel.level;
        }
        this.response.appendInt(this.achievement.id);
        this.response.appendInt(targetLevel);
        this.response.appendString("ACH_" + this.achievement.name + targetLevel);
        this.response.appendInt(currentLevel != null ? currentLevel.progress : 0);
        this.response.appendInt(nextLevel != null ? nextLevel.progress : 0);
        this.response.appendInt(nextLevel != null ? nextLevel.rewardAmount : 0);
        this.response.appendInt(nextLevel != null ? nextLevel.rewardType : 0);
        this.response.appendInt(achievementProgress == -1 ? 0 : achievementProgress);
        this.response.appendBoolean(AchievementManager.hasAchieved(this.habbo, this.achievement));
        this.response.appendString(this.achievement.category.toString().toLowerCase());
        this.response.appendString("");
        this.response.appendInt(this.achievement.levels.size());
        this.response.appendInt(0);
        return this.response;
    }

    public Habbo getHabbo() {
        return this.habbo;
    }

    public Achievement getAchievement() {
        return this.achievement;
    }
}

