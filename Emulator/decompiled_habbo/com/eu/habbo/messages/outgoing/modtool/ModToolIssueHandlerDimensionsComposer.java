/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.modtool;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class ModToolIssueHandlerDimensionsComposer
extends MessageComposer {
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    public ModToolIssueHandlerDimensionsComposer(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1576);
        this.response.appendInt(this.x);
        this.response.appendInt(this.y);
        this.response.appendInt(this.width);
        this.response.appendInt(this.height);
        return this.response;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
}

