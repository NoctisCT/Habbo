/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.incoming.modtool;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.modtool.ModToolBanType;
import com.eu.habbo.habbohotel.modtool.ModToolSanctionItem;
import com.eu.habbo.habbohotel.modtool.ModToolSanctions;
import com.eu.habbo.habbohotel.modtool.ScripterManager;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.messages.incoming.MessageHandler;
import gnu.trove.map.hash.THashMap;
import java.util.ArrayList;

public class ModToolSanctionBanEvent
extends MessageHandler {
    public static final int BAN_18_HOURS = 3;
    public static final int BAN_7_DAYS = 4;
    public static final int BAN_30_DAYS_STEP_1 = 5;
    public static final int BAN_30_DAYS_STEP_2 = 7;
    public static final int BAN_100_YEARS = 6;
    public static final int BAN_AVATAR_ONLY_100_YEARS = 106;
    public final int DAY_IN_SECONDS = 86400;

    @Override
    public void handle() throws Exception {
        int userId = this.packet.readInt();
        String message = this.packet.readString();
        int cfhTopic = this.packet.readInt();
        int banType = this.packet.readInt();
        boolean unknown = this.packet.readBoolean();
        int duration = 0;
        switch (banType) {
            case 3: {
                duration = 64800;
                break;
            }
            case 4: {
                duration = 7 * this.DAY_IN_SECONDS;
                break;
            }
            case 5: 
            case 7: {
                duration = 30 * this.DAY_IN_SECONDS;
                break;
            }
            case 6: 
            case 106: {
                duration = Emulator.getIntUnixTimestamp();
            }
        }
        if (this.client.getHabbo().hasPermission(Permission.ACC_SUPPORTTOOL)) {
            ModToolSanctions modToolSanctions = Emulator.getGameEnvironment().getModToolSanctions();
            if (Emulator.getConfig().getBoolean("hotel.sanctions.enabled")) {
                THashMap<Integer, ArrayList<ModToolSanctionItem>> modToolSanctionItemsHashMap = Emulator.getGameEnvironment().getModToolSanctions().getSanctions(userId);
                ArrayList<ModToolSanctionItem> modToolSanctionItems = modToolSanctionItemsHashMap.get(userId);
                if (modToolSanctionItems != null && !modToolSanctionItemsHashMap.isEmpty()) {
                    ModToolSanctionItem item = modToolSanctionItems.get(modToolSanctionItems.size() - 1);
                    if (item.probationTimestamp > 0 && item.probationTimestamp >= Emulator.getIntUnixTimestamp()) {
                        modToolSanctions.run(userId, this.client.getHabbo(), item.sanctionLevel, cfhTopic, message, 0, false, 0);
                    } else {
                        modToolSanctions.run(userId, this.client.getHabbo(), item.sanctionLevel, cfhTopic, message, 0, false, 0);
                    }
                } else {
                    modToolSanctions.run(userId, this.client.getHabbo(), 0, cfhTopic, message, 0, false, 0);
                }
            } else {
                Emulator.getGameEnvironment().getModToolManager().ban(userId, this.client.getHabbo(), message, duration, ModToolBanType.ACCOUNT, cfhTopic);
            }
        } else {
            ScripterManager.scripterDetected(this.client, Emulator.getTexts().getValue("scripter.warning.modtools.ban").replace("%username%", this.client.getHabbo().getHabboInfo().getUsername()));
        }
    }
}

