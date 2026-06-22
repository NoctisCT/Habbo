/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.modtool;

public enum ModToolChatRecordDataType {
    UNKNOWN(0),
    ROOM_TOOL(1),
    IM_SESSION(2),
    FORUM_THREAD(3),
    FORUM_MESSAGE(4),
    SELFIE(5),
    PHOTO_REPORT(6);

    public final int type;

    private ModToolChatRecordDataType(int type) {
        this.type = type;
    }
}

