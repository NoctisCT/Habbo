package com.bhrpg.plugin.commands;

import com.eu.habbo.bhrpg.combat.CombatManager;
import com.eu.habbo.bhrpg.combat.UserCombatStats;
import com.bhrpg.plugin.network.RpgStatsComposer;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.commands.Command;

public class RpgStatsCommand extends Command {
    public RpgStatsCommand() {
        // "cmd_rpgstats" es el permiso, "rpgstats" es lo que escribes en el chat
        super("cmd_rpgstats", new String[] { "rpgstats" });
    }

    @Override
    public boolean handle(GameClient client, String[] params) throws Exception {
        Habbo habbo = client.getHabbo();
        if (params.length < 2) {
            return true;
        }

        try {
            int targetId = Integer.parseInt(params[1]);
            UserCombatStats stats = CombatManager.getStats(targetId);
            habbo.getClient().sendResponse(new RpgStatsComposer(stats));
        } catch (NumberFormatException e) {
            // Ignorar si el parámetro no es numérico
        }

        return true;
    }
}