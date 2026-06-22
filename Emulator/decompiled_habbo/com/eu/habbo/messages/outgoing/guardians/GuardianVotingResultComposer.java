/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.guardians;

import com.eu.habbo.habbohotel.guides.GuardianTicket;
import com.eu.habbo.habbohotel.guides.GuardianVote;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import java.util.Map;

public class GuardianVotingResultComposer
extends MessageComposer {
    private final GuardianTicket ticket;
    private final GuardianVote vote;

    public GuardianVotingResultComposer(GuardianTicket ticket, GuardianVote vote) {
        this.ticket = ticket;
        this.vote = vote;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(3276);
        this.response.appendInt(this.ticket.getVerdict().getType());
        this.response.appendInt(this.vote.type.getType());
        this.response.appendInt(this.ticket.getVotes().size() - 1);
        for (Map.Entry<Habbo, GuardianVote> set : this.ticket.getVotes().entrySet()) {
            if (set.getValue().equals(this.vote)) continue;
            this.response.appendInt(set.getValue().type.getType());
        }
        return this.response;
    }

    public GuardianTicket getTicket() {
        return this.ticket;
    }

    public GuardianVote getVote() {
        return this.vote;
    }
}

