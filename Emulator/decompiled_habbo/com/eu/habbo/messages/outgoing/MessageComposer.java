/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing;

import com.eu.habbo.messages.ServerMessage;

public abstract class MessageComposer {
    private ServerMessage composed = null;
    protected final ServerMessage response = new ServerMessage();

    protected MessageComposer() {
    }

    protected abstract ServerMessage composeInternal();

    public ServerMessage compose() {
        if (this.composed == null) {
            this.composed = this.composeInternal();
            if (this.composed != null && this.composed.getComposer() == null) {
                this.composed.setComposer(this);
            }
        }
        return this.composed;
    }
}

