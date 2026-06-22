/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.incoming.modtool;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.modtool.CfhTopic;
import com.eu.habbo.habbohotel.modtool.ModToolBanType;
import com.eu.habbo.habbohotel.modtool.ModToolIssue;
import com.eu.habbo.habbohotel.modtool.ModToolPreset;
import com.eu.habbo.habbohotel.modtool.ModToolTicketState;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;

public class ModToolIssueDefaultSanctionEvent
extends MessageHandler {
    @Override
    public void handle() throws Exception {
        if (this.client.getHabbo().hasPermission(Permission.ACC_SUPPORTTOOL)) {
            int issueId = this.packet.readInt();
            int unknown = this.packet.readInt();
            int category = this.packet.readInt();
            ModToolIssue issue = Emulator.getGameEnvironment().getModToolManager().getTicket(issueId);
            if (issue.modId == this.client.getHabbo().getHabboInfo().getId()) {
                ModToolPreset defaultSanction;
                CfhTopic modToolCategory = Emulator.getGameEnvironment().getModToolManager().getCfhTopic(category);
                if (modToolCategory != null && (defaultSanction = modToolCategory.defaultSanction) != null) {
                    Habbo target = Emulator.getGameEnvironment().getHabboManager().getHabbo(issue.reportedId);
                    if (defaultSanction.banLength > 0) {
                        Emulator.getGameEnvironment().getModToolManager().ban(issue.reportedId, this.client.getHabbo(), defaultSanction.message, defaultSanction.banLength * 86400, ModToolBanType.ACCOUNT, modToolCategory.id);
                    } else if (defaultSanction.muteLength > 0 && target != null) {
                        target.mute(defaultSanction.muteLength * 86400, false);
                    }
                }
                issue.state = ModToolTicketState.CLOSED;
                Emulator.getGameEnvironment().getModToolManager().updateTicketToMods(issue);
            } else {
                this.client.getHabbo().alert(Emulator.getTexts().getValue("supporttools.not_ticket_owner"));
            }
        }
    }
}

