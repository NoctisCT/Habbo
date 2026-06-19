package com.bhrpg.plugin.commands;

import com.eu.habbo.bhrpg.combat.CombatManager;
import com.eu.habbo.bhrpg.combat.UserCombatStats;
import com.bhrpg.plugin.network.RpgStatsComposer;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.Emulator;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class ModStatCommand extends Command {
    public ModStatCommand() {
        super("cmd_modstat", new String[] { "modstat", "setstat" });
    }

    @Override
    public boolean handle(GameClient client, String[] params) throws Exception {
        Habbo habbo = client.getHabbo();
        if (params.length < 4) {
            habbo.whisper("Uso correcto: :modstat [nombre] [stat] [valor]");
            return true;
        }

        String targetName = params[1];
        String stat = params[2].toLowerCase();
        int valor;

        try {
            valor = Integer.parseInt(params[3]);
            if (valor < 0) {
                habbo.whisper("El valor no puede ser negativo.");
                return true;
            }
        } catch (NumberFormatException e) {
            habbo.whisper("El valor debe ser un número entero.");
            return true;
        }

        Habbo target = Emulator.getGameEnvironment().getHabboManager().getHabbo(targetName);
        if (target == null) {
            habbo.whisper("El usuario " + targetName + " no está conectado.");
            return true;
        }

        int targetId = target.getHabboInfo().getId();
        UserCombatStats stats = CombatManager.getStats(targetId);
        boolean statValido = false;
        String columnaDb = "";

        switch (stat) {
            case "vitalidad":
                stats.setVitalidad(valor);
                columnaDb = "vitalidad";
                statValido = true;
                break;
            case "max_vitalidad":
            case "maxvitalidad":
                stats.setMaxVitalidad(valor);
                columnaDb = "max_vitalidad";
                statValido = true;
                break;
            case "reiryoku":
                stats.setReiryoku(valor);
                columnaDb = "reiryoku";
                statValido = true;
                break;
            case "max_reiryoku":
            case "maxreiryoku":
                stats.setMaxReiryoku(valor);
                columnaDb = "max_reiryoku";
                statValido = true;
                break;
            case "fuerza":
                stats.setFuerza(valor);
                columnaDb = "fuerza";
                statValido = true;
                break;
            case "defensa":
                stats.setDefensa(valor);
                columnaDb = "defensa";
                statValido = true;
                break;
            case "velocidad":
                stats.setVelocidad(valor);
                columnaDb = "velocidad";
                statValido = true;
                break;
            case "voluntad":
                stats.setVoluntad(valor);
                columnaDb = "voluntad";
                statValido = true;
                break;
            case "reiatsu":
                stats.setReiatsu(valor);
                columnaDb = "reiatsu";
                statValido = true;
                break;
        }

        if (!statValido) {
            habbo.whisper("Atributo inválido.");
            return true;
        }

        final int valorFinal = valor;
        final String columna = columnaDb;
        final int finalTargetId = targetId;

        Emulator.getThreading().run(() -> {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                    PreparedStatement statement = connection.prepareStatement(
                            "UPDATE usuarios_rpg_stats SET " + columna + " = ? WHERE user_id = ?")) {
                statement.setInt(1, valorFinal);
                statement.setInt(2, finalTargetId);
                statement.executeUpdate();
            } catch (Exception e) {
                Emulator.getLogging().logErrorLine(e);
            }
        });

        target.getClient().sendResponse(new RpgStatsComposer(stats));
        habbo.whisper("¡Has establecido " + stat + " de " + targetName + " en " + valor + "!");
        target.whisper("Un administrador ha modificado tu atributo " + stat + " a " + valor + ".");
        return true;
    }
}