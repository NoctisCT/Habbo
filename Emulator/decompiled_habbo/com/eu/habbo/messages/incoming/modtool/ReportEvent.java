/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.incoming.modtool;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guides.GuardianTicket;
import com.eu.habbo.habbohotel.modtool.CfhActionType;
import com.eu.habbo.habbohotel.modtool.CfhTopic;
import com.eu.habbo.habbohotel.modtool.ModToolChatLog;
import com.eu.habbo.habbohotel.modtool.ModToolIssue;
import com.eu.habbo.habbohotel.modtool.ModToolTicketState;
import com.eu.habbo.habbohotel.modtool.ModToolTicketType;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.modtool.BullyReportedMessageComposer;
import com.eu.habbo.messages.outgoing.modtool.HelperRequestDisabledComposer;
import com.eu.habbo.messages.outgoing.modtool.ModToolIssueHandledComposer;
import com.eu.habbo.messages.outgoing.modtool.ModToolReportReceivedAlertComposer;
import com.eu.habbo.messages.outgoing.modtool.ReportRoomFormComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserIgnoredComposer;
import com.eu.habbo.threading.runnables.InsertModToolIssue;
import java.util.ArrayList;
import java.util.List;

public class ReportEvent
extends MessageHandler {
    @Override
    public void handle() throws Exception {
        if (!this.client.getHabbo().getHabboStats().allowTalk()) {
            this.client.sendResponse(new HelperRequestDisabledComposer());
            return;
        }
        String message = this.packet.readString();
        int topic = this.packet.readInt();
        int userId = this.packet.readInt();
        int roomId = this.packet.readInt();
        int messageCount = this.packet.readInt();
        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(roomId);
        List<ModToolIssue> issues = Emulator.getGameEnvironment().getModToolManager().openTicketsForHabbo(this.client.getHabbo());
        if (!issues.isEmpty()) {
            this.client.sendResponse(new ReportRoomFormComposer(issues));
            return;
        }
        CfhTopic cfhTopic = Emulator.getGameEnvironment().getModToolManager().getCfhTopic(topic);
        if (userId != -1) {
            Habbo reported = Emulator.getGameEnvironment().getHabboManager().getHabbo(userId);
            if (reported != null) {
                if (cfhTopic != null && cfhTopic.action == CfhActionType.GUARDIANS && Emulator.getGameEnvironment().getGuideManager().activeGuardians()) {
                    GuardianTicket ticket = Emulator.getGameEnvironment().getGuideManager().getOpenReportedHabboTicket(reported);
                    if (ticket != null) {
                        this.client.sendResponse(new BullyReportedMessageComposer(3));
                        return;
                    }
                    ArrayList<ModToolChatLog> chatLog = Emulator.getGameEnvironment().getModToolManager().getRoomChatlog(roomId);
                    if (chatLog.isEmpty()) {
                        this.client.sendResponse(new BullyReportedMessageComposer(2));
                        return;
                    }
                    Emulator.getGameEnvironment().getGuideManager().addGuardianTicket(new GuardianTicket(this.client.getHabbo(), reported, chatLog));
                    this.client.sendResponse(new BullyReportedMessageComposer(0));
                } else {
                    ModToolIssue issue = new ModToolIssue(this.client.getHabbo().getHabboInfo().getId(), this.client.getHabbo().getHabboInfo().getUsername(), reported.getHabboInfo().getId(), reported.getHabboInfo().getUsername(), roomId, message, ModToolTicketType.NORMAL);
                    issue.category = topic;
                    new InsertModToolIssue(issue).run();
                    Emulator.getGameEnvironment().getModToolManager().addTicket(issue);
                    Emulator.getGameEnvironment().getModToolManager().updateTicketToMods(issue);
                    this.client.sendResponse(new ModToolReportReceivedAlertComposer(0, cfhTopic.reply));
                    if (cfhTopic != null && cfhTopic.action != CfhActionType.MODS) {
                        Emulator.getThreading().run(() -> {
                            if (issue.state == ModToolTicketState.OPEN) {
                                if (cfhTopic.action == CfhActionType.AUTO_IGNORE && this.client.getHabbo().getHabboStats().ignoreUser(this.client, reported.getHabboInfo().getId())) {
                                    this.client.sendResponse(new RoomUserIgnoredComposer(reported, 1));
                                }
                                this.client.sendResponse(new ModToolIssueHandledComposer(cfhTopic.reply).compose());
                                Emulator.getGameEnvironment().getModToolManager().closeTicketAsHandled(issue, null);
                            }
                        }, 30000L);
                    }
                }
            }
        } else {
            ModToolIssue issue = new ModToolIssue(this.client.getHabbo().getHabboInfo().getId(), this.client.getHabbo().getHabboInfo().getUsername(), room != null ? room.getOwnerId() : 0, room != null ? room.getOwnerName() : "", roomId, message, ModToolTicketType.ROOM);
            issue.category = topic;
            new InsertModToolIssue(issue).run();
            this.client.sendResponse(new ModToolReportReceivedAlertComposer(0, message));
            Emulator.getGameEnvironment().getModToolManager().addTicket(issue);
            Emulator.getGameEnvironment().getModToolManager().updateTicketToMods(issue);
            if (cfhTopic != null && cfhTopic.action != CfhActionType.MODS) {
                Emulator.getThreading().run(() -> {
                    if (issue.state == ModToolTicketState.OPEN) {
                        Habbo reported;
                        if (cfhTopic.action == CfhActionType.AUTO_IGNORE && this.client.getHabbo().getHabboStats().ignoreUser(this.client, issue.reportedId) && (reported = Emulator.getGameEnvironment().getHabboManager().getHabbo(issue.reportedId)) != null) {
                            this.client.sendResponse(new RoomUserIgnoredComposer(reported, 1));
                        }
                        this.client.sendResponse(new ModToolIssueHandledComposer(cfhTopic.reply).compose());
                        Emulator.getGameEnvironment().getModToolManager().closeTicketAsHandled(issue, null);
                    }
                }, 30000L);
            }
        }
    }
}

