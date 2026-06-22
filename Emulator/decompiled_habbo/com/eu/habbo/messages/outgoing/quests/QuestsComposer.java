/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.quests;

import com.eu.habbo.messages.ISerialize;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import java.util.List;

public class QuestsComposer
extends MessageComposer {
    private final List<Quest> quests;
    private final boolean unknownBoolean;

    public QuestsComposer(List<Quest> quests, boolean unknownBoolean) {
        this.quests = quests;
        this.unknownBoolean = unknownBoolean;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(3625);
        this.response.appendInt(this.quests.size());
        for (Quest quest : this.quests) {
            this.response.append(quest);
        }
        this.response.appendBoolean(this.unknownBoolean);
        return this.response;
    }

    public List<Quest> getQuests() {
        return this.quests;
    }

    public boolean isUnknownBoolean() {
        return this.unknownBoolean;
    }

    public static class Quest
    implements ISerialize {
        private final String campaignCode;
        private final int completedQuestsInCampaign;
        private final int questCountInCampaign;
        private final int activityPointType;
        private final int id;
        private final boolean accepted;
        private final String type;
        private final String imageVersion;
        private final int rewardCurrencyAmount;
        private final String localizationCode;
        private final int completedSteps;
        private final int totalSteps;
        private final int sortOrder;
        private final String catalogPageName;
        private final String chainCode;
        private final boolean easy;

        public Quest(String campaignCode, int completedQuestsInCampaign, int questCountInCampaign, int activityPointType, int id, boolean accepted, String type, String imageVersion, int rewardCurrencyAmount, String localizationCode, int completedSteps, int totalSteps, int sortOrder, String catalogPageName, String chainCode, boolean easy) {
            this.campaignCode = campaignCode;
            this.completedQuestsInCampaign = completedQuestsInCampaign;
            this.questCountInCampaign = questCountInCampaign;
            this.activityPointType = activityPointType;
            this.id = id;
            this.accepted = accepted;
            this.type = type;
            this.imageVersion = imageVersion;
            this.rewardCurrencyAmount = rewardCurrencyAmount;
            this.localizationCode = localizationCode;
            this.completedSteps = completedSteps;
            this.totalSteps = totalSteps;
            this.sortOrder = sortOrder;
            this.catalogPageName = catalogPageName;
            this.chainCode = chainCode;
            this.easy = easy;
        }

        @Override
        public void serialize(ServerMessage message) {
            message.appendString(this.campaignCode);
            message.appendInt(this.completedQuestsInCampaign);
            message.appendInt(this.questCountInCampaign);
            message.appendInt(this.activityPointType);
            message.appendInt(this.id);
            message.appendBoolean(this.accepted);
            message.appendString(this.type);
            message.appendString(this.imageVersion);
            message.appendInt(this.rewardCurrencyAmount);
            message.appendString(this.localizationCode);
            message.appendInt(this.completedSteps);
            message.appendInt(this.totalSteps);
            message.appendInt(this.sortOrder);
            message.appendString(this.catalogPageName);
            message.appendString(this.chainCode);
            message.appendBoolean(this.easy);
        }

        public String getCampaignCode() {
            return this.campaignCode;
        }

        public int getCompletedQuestsInCampaign() {
            return this.completedQuestsInCampaign;
        }

        public int getQuestCountInCampaign() {
            return this.questCountInCampaign;
        }

        public int getActivityPointType() {
            return this.activityPointType;
        }

        public int getId() {
            return this.id;
        }

        public boolean isAccepted() {
            return this.accepted;
        }

        public String getType() {
            return this.type;
        }

        public String getImageVersion() {
            return this.imageVersion;
        }

        public int getRewardCurrencyAmount() {
            return this.rewardCurrencyAmount;
        }

        public String getLocalizationCode() {
            return this.localizationCode;
        }

        public int getCompletedSteps() {
            return this.completedSteps;
        }

        public int getTotalSteps() {
            return this.totalSteps;
        }

        public int getSortOrder() {
            return this.sortOrder;
        }

        public String getCatalogPageName() {
            return this.catalogPageName;
        }

        public String getChainCode() {
            return this.chainCode;
        }

        public boolean isEasy() {
            return this.easy;
        }
    }
}

