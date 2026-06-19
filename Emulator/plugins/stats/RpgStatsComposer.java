package com.bhrpg.plugin.network;

import com.eu.habbo.bhrpg.combat.UserCombatStats;
import com.eu.habbo.messages.ServerMessage;

public class RpgStatsComposer extends ServerMessage {
    public RpgStatsComposer(UserCombatStats stats) {
        super(3501);

        String rawData = stats.getVitalidad() + "," + stats.getMaxVitalidad() + ";"
                + stats.getReiryoku() + "," + stats.getMaxReiryoku() + ";"
                + stats.getFuerza() + ";"
                + stats.getDefensa() + ";"
                + stats.getVelocidad() + ";"
                + stats.getVoluntad() + ";"
                + stats.getReiatsu();

        this.appendString(rawData);
    }
}