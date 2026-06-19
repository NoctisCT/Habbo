package com.eu.habbo.bhrpg.combat;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;

public class RpgPassTurnCommand extends Command {

    public RpgPassTurnCommand() {
        // Al registrarse como comando oficial de Arcturus, el emulador oculta la
        // burbuja de chat automáticamente
        super("rpg_pasar_turno", new String[] {});
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (gameClient == null || gameClient.getHabbo() == null) {
            return false;
        }

        if (!CombatManager.isInCombat(gameClient.getHabbo())) {
            return true;
        }

        System.out.println("[RPG LOG] Puente invisible activado: " + gameClient.getHabbo().getHabboInfo().getUsername()
                + " ha usado el botón de la UI.");

        // Ejecutamos el relevo táctico
        CombatManager.passTurn(gameClient.getHabbo());
        return true;
    }
}