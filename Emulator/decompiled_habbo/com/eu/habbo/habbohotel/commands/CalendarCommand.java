/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.campaign.calendar.CalendarCampaign;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.messages.outgoing.events.calendar.AdventCalendarDataComposer;
import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class CalendarCommand
extends Command {
    public CalendarCommand() {
        super("cmd_calendar", Emulator.getTexts().getValue("commands.keys.cmd_calendar").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (Emulator.getConfig().getBoolean("hotel.calendar.enabled")) {
            CalendarCampaign campaign;
            String campaignName = Emulator.getConfig().getValue("hotel.calendar.default");
            if (params.length > 1 && gameClient.getHabbo().hasPermission("cmd_calendar_staff")) {
                campaignName = params[1];
            }
            if ((campaign = Emulator.getGameEnvironment().getCalendarManager().getCalendarCampaign(campaignName)) == null) {
                return false;
            }
            int daysBetween = (int)ChronoUnit.DAYS.between(new Timestamp((long)campaign.getStartTimestamp().intValue() * 1000L).toInstant(), new Date().toInstant());
            if (daysBetween >= 0) {
                gameClient.sendResponse(new AdventCalendarDataComposer(campaign.getName(), campaign.getImage(), campaign.getTotalDays(), daysBetween, gameClient.getHabbo().getHabboStats().calendarRewardsClaimed, campaign.getLockExpired()));
            }
        }
        return true;
    }
}

