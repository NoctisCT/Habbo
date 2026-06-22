/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms.items;

import com.eu.habbo.habbohotel.rooms.RoomMoodlightData;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import gnu.trove.map.TIntObjectMap;

public class MoodLightDataComposer
extends MessageComposer {
    private final TIntObjectMap<RoomMoodlightData> moodLightData;

    public MoodLightDataComposer(TIntObjectMap<RoomMoodlightData> moodLightData) {
        this.moodLightData = moodLightData;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2710);
        this.response.appendInt(3);
        int index = 1;
        for (RoomMoodlightData data : this.moodLightData.valueCollection()) {
            if (data.isEnabled()) {
                this.response.appendInt(data.getId());
                index = -1;
                break;
            }
            ++index;
        }
        if (index != -1) {
            this.response.appendInt(1);
        }
        int i = 1;
        for (RoomMoodlightData data : this.moodLightData.valueCollection()) {
            this.response.appendInt(data.getId());
            this.response.appendInt(data.isBackgroundOnly() ? 2 : 1);
            this.response.appendString(data.getColor());
            this.response.appendInt(data.getIntensity());
            ++i;
        }
        while (i <= 3) {
            this.response.appendInt(i);
            this.response.appendInt(1);
            this.response.appendString("#000000");
            this.response.appendInt(255);
            ++i;
        }
        return this.response;
    }

    public TIntObjectMap<RoomMoodlightData> getMoodLightData() {
        return this.moodLightData;
    }
}

