/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.modtool;

import com.eu.habbo.habbohotel.modtool.ModToolIssue;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class ModToolIssueUpdateComposer
extends MessageComposer {
    private final ModToolIssue issue;

    public ModToolIssueUpdateComposer(ModToolIssue issue) {
        this.issue = issue;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(3150);
        this.issue.serialize(this.response);
        return this.response;
    }

    public ModToolIssue getIssue() {
        return this.issue;
    }
}

