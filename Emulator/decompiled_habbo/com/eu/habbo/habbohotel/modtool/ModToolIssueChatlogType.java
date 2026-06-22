/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.modtool;

public enum ModToolIssueChatlogType {
    NORMAL(0),
    CHAT(1),
    IM(2),
    FORUM_THREAD(3),
    FORUM_COMMENT(4),
    SELFIE(5),
    PHOTO(6);

    private int type;

    private ModToolIssueChatlogType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }
}

