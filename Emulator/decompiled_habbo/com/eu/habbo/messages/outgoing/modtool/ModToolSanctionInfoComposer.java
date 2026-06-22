/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.modtool;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.modtool.ModToolSanctionItem;
import com.eu.habbo.habbohotel.modtool.ModToolSanctionLevelItem;
import com.eu.habbo.habbohotel.modtool.ModToolSanctions;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import gnu.trove.map.hash.THashMap;
import java.util.ArrayList;
import java.util.Date;
import org.joda.time.DateTime;

public class ModToolSanctionInfoComposer
extends MessageComposer {
    private final Habbo habbo;

    public ModToolSanctionInfoComposer(Habbo habbo) {
        this.habbo = habbo;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    protected ServerMessage composeInternal() {
        ModToolSanctions modToolSanctions = Emulator.getGameEnvironment().getModToolSanctions();
        if (!Emulator.getConfig().getBoolean("hotel.sanctions.enabled")) return this.response;
        THashMap<Integer, ArrayList<ModToolSanctionItem>> modToolSanctionItemsHashMap = Emulator.getGameEnvironment().getModToolSanctions().getSanctions(this.habbo.getHabboInfo().getId());
        ArrayList<ModToolSanctionItem> modToolSanctionItems = modToolSanctionItemsHashMap.get(this.habbo.getHabboInfo().getId());
        if (modToolSanctionItems == null || modToolSanctionItems.size() <= 0) return this.cleanResponse();
        ModToolSanctionItem item = modToolSanctionItems.get(modToolSanctionItems.size() - 1);
        ModToolSanctionItem prevItem = null;
        if (modToolSanctionItems.size() > 1 && modToolSanctionItems.get(modToolSanctionItems.size() - 2) != null) {
            prevItem = modToolSanctionItems.get(modToolSanctionItems.size() - 2);
        }
        ModToolSanctionLevelItem modToolSanctionLevelItem = modToolSanctions.getSanctionLevelItem(item.sanctionLevel);
        ModToolSanctionLevelItem nextModToolSanctionLevelItem = modToolSanctions.getSanctionLevelItem(item.sanctionLevel + 1);
        if (item.probationTimestamp <= 0) return this.cleanResponse();
        Date probationEndTime = new Date((long)item.probationTimestamp * 1000L);
        Date probationStartTime = new DateTime(probationEndTime).minusDays(modToolSanctions.getProbationDays(modToolSanctionLevelItem)).toDate();
        Date tradeLockedUntil = null;
        if (item.tradeLockedUntil > 0) {
            tradeLockedUntil = new Date((long)item.tradeLockedUntil * 1000L);
        }
        this.response.init(2221);
        this.response.appendBoolean(prevItem != null && prevItem.probationTimestamp > 0);
        this.response.appendBoolean(item.probationTimestamp >= Emulator.getIntUnixTimestamp());
        this.response.appendString(modToolSanctions.getSanctionType(modToolSanctionLevelItem));
        this.response.appendInt(modToolSanctions.getTimeOfSanction(modToolSanctionLevelItem));
        this.response.appendInt(30);
        this.response.appendString(item.reason.equals("") ? "cfh.reason.EMPTY" : item.reason);
        this.response.appendString(probationStartTime == null ? Emulator.getDate().toString() : probationStartTime.toString());
        this.response.appendInt(0);
        this.response.appendString(modToolSanctions.getSanctionType(nextModToolSanctionLevelItem));
        this.response.appendInt(modToolSanctions.getTimeOfSanction(nextModToolSanctionLevelItem));
        this.response.appendInt(30);
        this.response.appendBoolean(item.isMuted);
        this.response.appendString(tradeLockedUntil == null ? "" : tradeLockedUntil.toString());
        return this.response;
    }

    private ServerMessage cleanResponse() {
        this.response.init(2221);
        this.response.appendBoolean(false);
        this.response.appendBoolean(false);
        this.response.appendString("ALERT");
        this.response.appendInt(0);
        this.response.appendInt(30);
        this.response.appendString("cfh.reason.EMPTY");
        this.response.appendString(Emulator.getDate().toString());
        this.response.appendInt(0);
        this.response.appendString("ALERT");
        this.response.appendInt(0);
        this.response.appendInt(30);
        this.response.appendBoolean(false);
        this.response.appendString("");
        return this.response;
    }

    public Habbo getHabbo() {
        return this.habbo;
    }
}

