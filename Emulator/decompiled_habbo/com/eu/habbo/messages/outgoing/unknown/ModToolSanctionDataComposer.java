/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ISerialize;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class ModToolSanctionDataComposer
extends MessageComposer {
    private final int unknownInt1;
    private final int accountId;
    private final CFHSanction sanction;

    public ModToolSanctionDataComposer(int unknownInt1, int accountId, CFHSanction sanction) {
        this.unknownInt1 = unknownInt1;
        this.accountId = accountId;
        this.sanction = sanction;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2782);
        this.response.appendInt(this.unknownInt1);
        this.response.appendInt(this.accountId);
        this.sanction.serialize(this.response);
        return this.response;
    }

    public int getUnknownInt1() {
        return this.unknownInt1;
    }

    public int getAccountId() {
        return this.accountId;
    }

    public CFHSanction getSanction() {
        return this.sanction;
    }

    public static class CFHSanction
    implements ISerialize {
        private final String name;
        private final int length;
        private final int unknownInt1;
        private final boolean avatarOnly;
        private final String tradelockInfo;
        private final String machineBanInfo;

        public CFHSanction(String name, int length, int unknownInt1, boolean avatarOnly, String tradelockInfo, String machineBanInfo) {
            this.name = name;
            this.length = length;
            this.unknownInt1 = unknownInt1;
            this.avatarOnly = avatarOnly;
            this.tradelockInfo = tradelockInfo;
            this.machineBanInfo = machineBanInfo;
        }

        @Override
        public void serialize(ServerMessage message) {
            message.appendString(this.name);
            message.appendInt(this.length);
            message.appendInt(this.unknownInt1);
            message.appendBoolean(this.avatarOnly);
            message.appendString(this.tradelockInfo);
            message.appendString(this.machineBanInfo);
        }

        public String getName() {
            return this.name;
        }

        public int getLength() {
            return this.length;
        }

        public int getUnknownInt1() {
            return this.unknownInt1;
        }

        public boolean isAvatarOnly() {
            return this.avatarOnly;
        }

        public String getTradelockInfo() {
            return this.tradelockInfo;
        }

        public String getMachineBanInfo() {
            return this.machineBanInfo;
        }
    }
}

