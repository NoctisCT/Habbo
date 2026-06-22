/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.navigator;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import java.util.Set;

public class TagsComposer
extends MessageComposer {
    private final Set<String> tags;

    public TagsComposer(Set<String> tags) {
        this.tags = tags;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2012);
        this.response.appendInt(this.tags.size());
        int i = 1;
        for (String s : this.tags) {
            this.response.appendString(s);
            this.response.appendInt(i);
            ++i;
        }
        return this.response;
    }

    public Set<String> getTags() {
        return this.tags;
    }
}

