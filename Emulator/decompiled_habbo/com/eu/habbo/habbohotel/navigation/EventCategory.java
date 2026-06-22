/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.navigation;

import com.eu.habbo.messages.ServerMessage;

public class EventCategory {
    private int id;
    private String caption;
    private boolean visible;

    public EventCategory(int id, String caption, boolean visible) {
        this.id = id;
        this.caption = caption;
        this.visible = visible;
    }

    public EventCategory(String serialized) throws Exception {
        String[] parts = serialized.split(",");
        if (parts.length != 3) {
            throw new Exception("A serialized event category should contain 3 fields");
        }
        this.id = Integer.parseInt(parts[0]);
        this.caption = parts[1];
        this.visible = parts[2].equalsIgnoreCase("true");
    }

    public int getId() {
        return this.id;
    }

    public String getCaption() {
        return this.caption;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void serialize(ServerMessage message) {
        message.appendInt(this.id);
        message.appendString(this.caption);
        message.appendBoolean(this.visible);
    }
}

