/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.plugin.events.support;

import com.eu.habbo.habbohotel.modtool.ModToolIssue;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.events.support.SupportTicketEvent;

public class SupportTicketStatusChangedEvent
extends SupportTicketEvent {
    public SupportTicketStatusChangedEvent(Habbo moderator, ModToolIssue ticket) {
        super(moderator, ticket);
    }
}

