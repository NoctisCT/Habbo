package com.eu.habbo.bhrpg.combat;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.Habbo;

public class CombatCommand extends Command {
    public CombatCommand() {
        super("cmd_combat", new String[] { "combate" });
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (gameClient.getHabbo() == null)
            return false;

        Habbo habbo = gameClient.getHabbo();

        // Si ya está dentro de una partida, el comando lo saca de ella individualmente
        if (CombatManager.isInCombat(habbo)) {
            CombatManager.leaveCombat(habbo);
            return true;
        }

        // Si se especifica un nombre (ej: :combate Hokusei)
        if (params.length > 1) {
            String targetName = params[1];
            Habbo target = Emulator.getGameEnvironment().getHabboManager().getHabbo(targetName);

            if (target == null || target.getRoomUnit() == null || habbo.getRoomUnit() == null ||
                    target.getRoomUnit().getRoom() != habbo.getRoomUnit().getRoom()) {
                habbo.whisper("El usuario objetivo no se encuentra en esta sala.");
                return true;
            }

            if (target == habbo) {
                habbo.whisper("No puedes iniciar un combate contra ti mismo.");
                return true;
            }

            // Unirse al combate existente del rival o crear una nueva instancia
            // dinámicamente
            CombatManager.joinOrCreateMatch(habbo, target);
        } else {
            habbo.whisper("Uso correcto: :combate NombreDelRival");
        }

        return true;
    }
}