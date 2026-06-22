/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.modtool;

import com.eu.habbo.habbohotel.modtool.ModToolIssue;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import java.util.List;

public class ReportRoomFormComposer
extends MessageComposer {
    private final List<ModToolIssue> pendingIssues;

    public ReportRoomFormComposer(List<ModToolIssue> issues) {
        this.pendingIssues = issues;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1121);
        this.response.appendInt(this.pendingIssues.size());
        for (ModToolIssue issue : this.pendingIssues) {
            this.response.appendString("" + issue.id);
            this.response.appendString("" + issue.timestamp);
            this.response.appendString(issue.message);
        }
        return this.response;
    }

    public List<ModToolIssue> getPendingIssues() {
        return this.pendingIssues;
    }
}

