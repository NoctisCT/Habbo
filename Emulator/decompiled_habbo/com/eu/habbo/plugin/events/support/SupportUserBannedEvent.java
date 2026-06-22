/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.plugin.events.support;

import com.eu.habbo.habbohotel.modtool.ModToolBan;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.events.support.SupportEvent;

public class SupportUserBannedEvent
extends SupportEvent {
    public final Habbo target;
    public final ModToolBan ban;

    public SupportUserBannedEvent(Habbo moderator, Habbo target, ModToolBan ban) {
        super(moderator);
        this.target = target;
        this.ban = ban;
    }
}

