/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.guardians;

import com.eu.habbo.habbohotel.guides.GuardianTicket;
import com.eu.habbo.habbohotel.modtool.ModToolChatLog;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import gnu.trove.map.hash.TIntIntHashMap;
import java.util.Calendar;

public class GuardianVotingRequestedComposer
extends MessageComposer {
    private final GuardianTicket ticket;

    public GuardianVotingRequestedComposer(GuardianTicket ticket) {
        this.ticket = ticket;
    }

    @Override
    protected ServerMessage composeInternal() {
        TIntIntHashMap mappedUsers = new TIntIntHashMap();
        mappedUsers.put(this.ticket.getReported().getHabboInfo().getId(), 0);
        Calendar c = Calendar.getInstance();
        c.setTime(this.ticket.getDate());
        StringBuilder fullMessage = new StringBuilder(c.get(1) + " ");
        fullMessage.append(c.get(2)).append(" ");
        fullMessage.append(c.get(5)).append(" ");
        fullMessage.append(c.get(12)).append(" ");
        fullMessage.append(c.get(13)).append(";");
        fullMessage.append("\r");
        for (ModToolChatLog chatLog : this.ticket.getChatLogs()) {
            if (!mappedUsers.containsKey(chatLog.habboId)) {
                mappedUsers.put(chatLog.habboId, mappedUsers.size());
            }
            fullMessage.append("unused;").append(mappedUsers.get(chatLog.habboId)).append(";").append(chatLog.message).append("\r");
        }
        this.response.init(143);
        this.response.appendInt(this.ticket.getTimeLeft());
        this.response.appendString(fullMessage.toString());
        return this.response;
    }

    public GuardianTicket getTicket() {
        return this.ticket;
    }
}

