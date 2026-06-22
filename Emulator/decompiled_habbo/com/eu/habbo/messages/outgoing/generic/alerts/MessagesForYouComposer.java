/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.generic.alerts;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import java.util.ArrayList;
import java.util.List;

public class MessagesForYouComposer
extends MessageComposer {
    private final String[] messages;
    private final List<String> newMessages;

    public MessagesForYouComposer(String[] messages) {
        this.messages = messages;
        this.newMessages = new ArrayList<String>();
    }

    public MessagesForYouComposer(List<String> newMessages) {
        this.newMessages = newMessages;
        this.messages = new String[0];
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2035);
        this.response.appendInt(this.messages.length + this.newMessages.size());
        for (String s : this.messages) {
            this.response.appendString(s);
        }
        for (String s : this.newMessages) {
            this.response.appendString(s);
        }
        return this.response;
    }

    public String[] getMessages() {
        return this.messages;
    }

    public List<String> getNewMessages() {
        return this.newMessages;
    }
}

