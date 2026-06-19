package com.eu.habbo.bhrpg.combat;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatType;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.bhrpg.plugin.network.RpgStatsComposer; // Importación del composer de tus estadísticas

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.List;

public class CombatManager {

    private static final ScheduledExecutorService timerService = Executors.newScheduledThreadPool(2);

    public static class PlayerState {
        public int startX;
        public int startY;
        public int maxRange;

        public PlayerState(int x, int y, int range) {
            this.startX = x;
            this.startY = y;
            this.maxRange = range;
        }
    }

    public static class CombatMatch {
        public final List<Habbo> players = new CopyOnWriteArrayList<>();
        private int turnIndex = 0;
        private ScheduledFuture<?> turnTimer = null;

        private void resetTurnTimer() {
            if (turnTimer != null) {
                turnTimer.cancel(false);
                turnTimer = null;
            }
            if (players.size() < 2)
                return;
            turnTimer = timerService.schedule(() -> {
                System.out.println(
                        "[RPG TIMER] Tiempo agotado de forma oficial en el servidor! Forzando relevo de turno.");
                nextTurn();
            }, 30, TimeUnit.SECONDS);
        }

        public void addPlayer(Habbo habbo) {
            if (!players.contains(habbo)) {
                players.add(habbo);
                UserCombatStats stats = getStats(habbo.getHabboInfo().getId());
                int maxMov = stats.getVelocidad();
                activeCombats.put(habbo.getHabboInfo().getId(),
                        new PlayerState(habbo.getRoomUnit().getX(), habbo.getRoomUnit().getY(), maxMov));
                userMatches.put(habbo.getHabboInfo().getId(), this);
                rebuildTurnOrder();
            }
        }

        public void removePlayer(Habbo habbo) {
            players.remove(habbo);
            activeCombats.remove(habbo.getHabboInfo().getId());
            userMatches.remove(habbo.getHabboInfo().getId());
            habbo.whisper("Has salido del combate. Movimiento libre restaurado.");
            sendGridToClient(habbo, 0, 0, 0);
            habbo.getClient().sendResponse(new CombatMenuComposer(false, false, 0, null, null));
            if (players.size() < 2) {
                if (turnTimer != null) {
                    turnTimer.cancel(false);
                    turnTimer = null;
                }
                for (Habbo remaining : players) {
                    activeCombats.remove(remaining.getHabboInfo().getId());
                    userMatches.remove(remaining.getHabboInfo().getId());
                    remaining.whisper("Combate finalizado por falta de oponentes.");
                    sendGridToClient(remaining, 0, 0, 0);
                    remaining.getClient().sendResponse(new CombatMenuComposer(false, false, 0, null, null));
                }
                players.clear();
            } else {
                rebuildTurnOrder();
            }
        }

        public void rebuildTurnOrder() {
            Habbo activeBefore = getCurrentTurn();
            players.sort((p1, p2) -> {
                int vel1 = getStats(p1.getHabboInfo().getId()).getVelocidad();
                int vel2 = getStats(p2.getHabboInfo().getId()).getVelocidad();
                return Integer.compare(vel2, vel1);
            });
            if (activeBefore != null) {
                for (int i = 0; i < players.size(); i++) {
                    if (players.get(i).getHabboInfo().getId() == activeBefore.getHabboInfo().getId()) {
                        turnIndex = i;
                        break;
                    }
                }
            } else {
                turnIndex = 0;
            }
            broadcastMatchState();
            resetTurnTimer();
        }

        public Habbo getCurrentTurn() {
            if (players.isEmpty())
                return null;
            if (turnIndex >= players.size())
                turnIndex = 0;
            return players.get(turnIndex);
        }

        public void nextTurn() {
            if (players.isEmpty())
                return;
            Habbo oldTurn = getCurrentTurn();
            if (oldTurn != null) {
                System.out.println("[RPG LOG] Finalizando turno de: " + oldTurn.getHabboInfo().getUsername());
                PlayerState oldState = activeCombats.get(oldTurn.getHabboInfo().getId());
                if (oldState != null) {
                    oldState.maxRange = getStats(oldTurn.getHabboInfo().getId()).getVelocidad();
                }
            }
            turnIndex = (turnIndex + 1) % players.size();
            Habbo newTurn = getCurrentTurn();
            if (newTurn != null) {
                System.out.println(
                        "[RPG LOG] Transfiriendo turno de iniciativa a: " + newTurn.getHabboInfo().getUsername());
                PlayerState newState = activeCombats.get(newTurn.getHabboInfo().getId());
                if (newState != null) {
                    newState.startX = newTurn.getRoomUnit().getX();
                    newState.startY = newTurn.getRoomUnit().getY();
                    newState.maxRange = getStats(newTurn.getHabboInfo().getId()).getVelocidad();
                }
            }
            broadcastMatchState();
            resetTurnTimer();
        }

        public void broadcastMatchState() {
            Habbo activePlayer = getCurrentTurn();
            if (activePlayer == null)
                return;
            for (Habbo p : players) {
                boolean isHisTurn = (p.getHabboInfo().getId() == activePlayer.getHabboInfo().getId());
                PlayerState state = activeCombats.get(p.getHabboInfo().getId());
                int currentMov = (state != null) ? state.maxRange : 0;

                // Extraemos las estadísticas del jugador actual de forma limpia
                UserCombatStats myStats = getStats(p.getHabboInfo().getId());

                // Localizamos dinámicamente a su oponente en la sala de combate
                UserCombatStats oppStats = null;
                for (Habbo opp : players) {
                    if (opp.getHabboInfo().getId() != p.getHabboInfo().getId()) {
                        oppStats = getStats(opp.getHabboInfo().getId());
                        break;
                    }
                }

                // Despachamos el JSON unificado definitivo (Paquete 3502)
                p.getClient().sendResponse(new CombatMenuComposer(true, isHisTurn, currentMov, myStats, oppStats));

                if (isHisTurn && state != null) {
                    sendGridToClient(p, p.getRoomUnit().getX(), p.getRoomUnit().getY(), state.maxRange);
                } else {
                    sendGridToClient(p, 0, 0, 0);
                }
            }
        }
    }

    private static final ConcurrentHashMap<Integer, PlayerState> activeCombats = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Integer, CombatMatch> userMatches = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Integer, UserCombatStats> cachedStats = new ConcurrentHashMap<>();

    // =========================================================================
    // MÓDULO MATEMÁTICO DE COMBATE INDESTRUCTIBLE MEDIANTE JSON UNIFICADO
    // =========================================================================
    public static void executeAttack(Habbo attacker, Habbo victim) {
        UserCombatStats attackerStats = getStats(attacker.getHabboInfo().getId());
        UserCombatStats victimStats = getStats(victim.getHabboInfo().getId());

        int fue = attackerStats.getFuerza();
        int def = victimStats.getDefensa();

        int armaDmg = getWeaponDamage(attacker);
        boolean tieneArma = armaDmg > 0;
        boolean esRango = isRangedWeapon(attacker);

        // REGLA 1: Variaciones del daño de ataque base
        int rawDamage = fue; // Desarmado por defecto = FUE
        if (tieneArma) {
            if (esRango) {
                rawDamage = armaDmg; // Arma a distancia = Solo ARMA
            } else {
                rawDamage = armaDmg + fue; // Arma cuerpo a cuerpo = ARMA + FUE
            }
        }

        // REGLA 2: Absorción reductora por la defensa del rival
        int finalDamage = rawDamage - def;
        if (finalDamage < 1) {
            finalDamage = 1; // Daño mínimo de 1 para evitar golpes nulos
        }

        // REGLA 3: Restar de la vitalidad RESTANTE de la víctima
        int vitRestante = victimStats.getVitalidad();
        int nuevaVit = Math.max(0, vitRestante - finalDamage);
        victimStats.setVitalidad(nuevaVit);

        // Guardado asíncrono seguro en Base de Datos de ambos perfiles
        final int nuevaVitVictima = nuevaVit;
        final int reiryokuAtacante = attackerStats.getReiryoku();
        final int fuerzaAtacante = attackerStats.getFuerza();

        Emulator.getThreading().run(() -> {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection()) {
                // Sincronizar Víctima
                try (PreparedStatement statement = connection.prepareStatement(
                        "UPDATE usuarios_rpg_stats SET vitalidad = ? WHERE user_id = ?")) {
                    statement.setInt(1, nuevaVitVictima);
                    statement.setInt(2, victim.getHabboInfo().getId());
                    statement.executeUpdate();
                }
                // Sincronizar Atacante (Energía consumida o bufos acumulados)
                try (PreparedStatement statement = connection.prepareStatement(
                        "UPDATE usuarios_rpg_stats SET reiryoku = ?, fuerza = ? WHERE user_id = ?")) {
                    statement.setInt(1, reiryokuAtacante);
                    statement.setInt(2, fuerzaAtacante);
                    statement.setInt(3, attacker.getHabboInfo().getId());
                    statement.executeUpdate();
                }
            } catch (Exception e) {
                Emulator.getLogging().logErrorLine(e);
            }
        });

        // REFRESH EN TIEMPO REAL: Re-renderiza de forma estructural el HUD de combate
        // para ambos jugadores
        // Al usar broadcastMatchState(), se envía el paquete 3502 con el mapa de
        // variables claras ("myHp" y "oppHp"),
        // erradicando por completo los cruces de barras visuales en las pantallas.
        CombatMatch match = getMatch(victim);
        if (match != null) {
            match.broadcastMatchState();
        }

        // TEXTO GENERAL PÚBLICO EN DECIR
        Room room = attacker.getRoomUnit().getRoom();
        if (room != null) {
            String textoPublico = "¡Has atacado a " + victim.getHabboInfo().getUsername() +
                    " causando un daño de " + finalDamage + " DMG!";
            room.talk(attacker, new RoomChatMessage(textoPublico, attacker, RoomChatMessageBubbles.NORMAL),
                    RoomChatType.TALK);
        }
    }

    private static int getWeaponDamage(Habbo habbo) {
        return 0; // 0 Desarmado nativo
    }

    private static boolean isRangedWeapon(Habbo habbo) {
        return false;
    }

    // =========================================================================
    // MÉTODOS ESTÁNDAR DEL MANAGER
    // =========================================================================
    public static void joinOrCreateMatch(Habbo source, Habbo target) {
        if (source == null || target == null)
            return;
        Room room = source.getRoomUnit().getRoom();
        CombatMatch existingMatch = userMatches.get(target.getHabboInfo().getId());
        if (existingMatch != null) {
            if (existingMatch.players.size() >= 6) {
                source.whisper("Esa sala de combate ya ha alcanzado el límite de 3 vs 3.");
                return;
            }
            existingMatch.addPlayer(source);
            if (room != null) {
                String msgText = source.getHabboInfo().getUsername() + " se ha unido al combate a favor de "
                        + target.getHabboInfo().getUsername();
                room.talk(source, new RoomChatMessage(msgText, source, RoomChatMessageBubbles.NORMAL),
                        RoomChatType.SHOUT);
            }
        } else {
            CombatMatch newMatch = new CombatMatch();
            newMatch.addPlayer(source);
            newMatch.addPlayer(target);
            if (room != null) {
                String msgText = "¡He desafiado a " + target.getHabboInfo().getUsername() + " a un combate a muerte!";
                room.talk(source, new RoomChatMessage(msgText, source, RoomChatMessageBubbles.NORMAL),
                        RoomChatType.SHOUT);
            }
        }
    }

    public static void leaveCombat(Habbo habbo) {
        CombatMatch match = userMatches.get(habbo.getHabboInfo().getId());
        if (match != null) {
            match.removePlayer(habbo);
        } else {
            activeCombats.remove(habbo.getHabboInfo().getId());
            sendGridToClient(habbo, 0, 0, 0);
            habbo.getClient().sendResponse(new CombatMenuComposer(false, false, 0, null, null));
        }
    }

    public static void passTurn(Habbo habbo) {
        CombatMatch match = userMatches.get(habbo.getHabboInfo().getId());
        if (match == null)
            return;
        Habbo current = match.getCurrentTurn();
        if (current == null)
            return;
        if (current.getHabboInfo().getId() == habbo.getHabboInfo().getId()) {
            match.nextTurn();
        }
    }

    public static boolean isInCombat(Habbo habbo) {
        return habbo != null && habbo.getHabboInfo() != null && activeCombats.containsKey(habbo.getHabboInfo().getId());
    }

    public static PlayerState getState(Habbo habbo) {
        if (habbo == null || habbo.getHabboInfo() == null)
            return null;
        return activeCombats.get(habbo.getHabboInfo().getId());
    }

    public static CombatMatch getMatch(Habbo habbo) {
        if (habbo == null || habbo.getHabboInfo() == null)
            return null;
        return userMatches.get(habbo.getHabboInfo().getId());
    }

    public static void sendGridToClient(Habbo habbo, int currentX, int currentY, int remainingRange) {
        Room room = habbo.getRoomUnit().getRoom();
        if (room == null)
            return;
        StringBuilder gridData = new StringBuilder();
        if (remainingRange > 0) {
            for (int x = currentX - remainingRange; x <= currentX + remainingRange; x++) {
                for (int y = currentY - remainingRange; y <= currentY + remainingRange; y++) {
                    int distance = Math.abs(currentX - x) + Math.abs(currentY - y);
                    if (distance <= remainingRange && distance > 0) {
                        if (room.tileWalkable((short) x, (short) y)) {
                            gridData.append(x).append(",").append(y).append(";");
                        }
                    }
                }
            }
        }
        habbo.getClient().sendResponse(new CombatGridComposer(gridData.toString()));
    }

    public static UserCombatStats getStats(int userId) {
        if (cachedStats.containsKey(userId)) {
            return cachedStats.get(userId);
        }
        UserCombatStats stats = null;
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection()) {
            try (PreparedStatement selectStatement = connection
                    .prepareStatement("SELECT * FROM usuarios_rpg_stats WHERE user_id = ?")) {
                selectStatement.setInt(1, userId);
                try (ResultSet resultSet = selectStatement.executeQuery()) {
                    if (resultSet.next()) {
                        stats = new UserCombatStats(
                                resultSet.getInt("vitalidad"),
                                resultSet.getInt("max_vitalidad"),
                                resultSet.getInt("reiryoku"),
                                resultSet.getInt("max_reiryoku"),
                                resultSet.getInt("fuerza"),
                                resultSet.getInt("defensa"),
                                resultSet.getInt("velocidad"),
                                resultSet.getInt("voluntad"),
                                resultSet.getInt("reiatsu"),
                                resultSet.getInt("puntos"));
                    }
                }
            }
            if (stats == null) {
                try (PreparedStatement insertStatement = connection
                        .prepareStatement("INSERT INTO usuarios_rpg_stats (user_id) VALUES (?)")) {
                    insertStatement.setInt(1, userId);
                    insertStatement.executeUpdate();
                }
                stats = new UserCombatStats(100, 100, 50, 50, 10, 10, 10, 10, 10, 5);
            }
            cachedStats.put(userId, stats);
        } catch (Exception e) {
            Emulator.getLogging().logErrorLine(e);
            return new UserCombatStats(100, 100, 50, 50, 10, 10, 10, 10, 10, 0);
        }
        return stats;
    }

    public static void clearCache(int userId) {
        cachedStats.remove(userId);
    }
}