/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.rcon;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.Achievement;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.rcon.RCONMessage;
import com.google.gson.Gson;

public class ProgressAchievement
extends RCONMessage<ProgressAchievementJSON> {
    public ProgressAchievement() {
        super(ProgressAchievementJSON.class);
    }

    @Override
    public void handle(Gson gson, ProgressAchievementJSON json) {
        Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(json.user_id);
        if (habbo != null) {
            Achievement achievement = Emulator.getGameEnvironment().getAchievementManager().getAchievement(json.achievement_id);
            if (achievement != null) {
                AchievementManager.progressAchievement(habbo, achievement, json.progress);
            } else {
                this.status = 1;
            }
        } else {
            this.status = 2;
        }
    }

    static class ProgressAchievementJSON {
        public int user_id;
        public int achievement_id;
        public int progress;

        ProgressAchievementJSON() {
        }
    }
}

