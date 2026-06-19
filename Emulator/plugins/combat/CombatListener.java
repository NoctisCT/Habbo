package com.eu.habbo.bhrpg.combat;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.EventHandler;
import com.eu.habbo.plugin.EventListener;
import com.eu.habbo.plugin.events.users.UserTakeStepEvent;
import com.eu.habbo.plugin.events.users.UserTalkEvent;

import java.util.HashSet;

public class CombatListener implements EventListener {

    public static final HashSet<Integer> attackingHabbos = new HashSet<>();

    public static boolean isAttacking(Habbo habbo) {
        return attackingHabbos.contains(habbo.getHabboInfo().getId());
    }

    public static void removeAttacking(Habbo habbo) {
        attackingHabbos.remove(habbo.getHabboInfo().getId());
    }

    @EventHandler
    public static void onRoomUnitLookAtPoint(com.eu.habbo.plugin.events.roomunit.RoomUnitLookAtPointEvent event) {
        if (event == null)
            return;

        com.eu.habbo.habbohotel.rooms.RoomUnit eventUnit = null;
        int clickX = -1;
        int clickY = -1;

        try {
            Class<?> current = event.getClass();
            while (current != null) {
                for (java.lang.reflect.Field f : current.getDeclaredFields()) {
                    f.setAccessible(true);
                    if (com.eu.habbo.habbohotel.rooms.RoomUnit.class.isAssignableFrom(f.getType())) {
                        eventUnit = (com.eu.habbo.habbohotel.rooms.RoomUnit) f.get(event);
                    }
                    String name = f.getName().toLowerCase();
                    if (f.getType() == int.class || f.getType() == Integer.class) {
                        if (name.equals("x") || name.contains("clickx"))
                            clickX = f.getInt(event);
                        else if (name.equals("y") || name.contains("clicky"))
                            clickY = f.getInt(event);
                    } else if (f.getType().getName().contains("Point") || f.getType().getName().contains("Position")
                            || f.getType().getName().contains("Tile")) {
                        Object pointObj = f.get(event);
                        if (pointObj != null) {
                            for (java.lang.reflect.Field pf : pointObj.getClass().getDeclaredFields()) {
                                pf.setAccessible(true);
                                String pname = pf.getName().toLowerCase();
                                if (pname.equals("x"))
                                    clickX = pf.getInt(pointObj);
                                if (pname.equals("y"))
                                    clickY = pf.getInt(pointObj);
                            }
                        }
                    }
                }
                current = current.getSuperclass();
            }
        } catch (Exception e) {
        }

        if (eventUnit == null || eventUnit.getRoom() == null || clickX == -1 || clickY == -1)
            return;

        Habbo attacker = null;
        for (Habbo h : eventUnit.getRoom().getHabbos()) {
            if (h.getRoomUnit() == eventUnit) {
                attacker = h;
                break;
            }
        }

        if (attacker == null || !attackingHabbos.contains(attacker.getHabboInfo().getId()))
            return;

        int curX = attacker.getRoomUnit().getX();
        int curY = attacker.getRoomUnit().getY();

        if (Math.abs(curX - clickX) + Math.abs(curY - clickY) != 1) {
            attacker.whisper("No puedes atacar a ese objetivo.");
            return;
        }

        attackingHabbos.remove(attacker.getHabboInfo().getId());

        Habbo victim = null;
        for (Habbo h : eventUnit.getRoom().getHabbos()) {
            if (h != attacker && CombatManager.isInCombat(h)) {
                if (h.getRoomUnit().getX() == clickX && h.getRoomUnit().getY() == clickY) {
                    victim = h;
                    break;
                }
            }
        }

        if (victim != null) {
            // CONEXIÓN DIRECTA CON EL NUEVO MOTOR DINÁMICO
            CombatManager.executeAttack(attacker, victim);
        } else {
            attacker.whisper(
                    "*Das un tajo violento al aire... No había nadie en la casilla (" + clickX + ", " + clickY + ")*");
        }

        CombatManager.PlayerState state = CombatManager.getState(attacker);
        if (state != null) {
            CombatManager.sendGridToClient(attacker, curX, curY, state.maxRange);
            attacker.getClient().sendResponse(new CombatMenuComposer(true, true, state.maxRange));
        }
    }

    @EventHandler
    public static void onUserTakeStep(UserTakeStepEvent event) {
        if (event.habbo == null || event.habbo.getRoomUnit() == null || event.fromLocation == null)
            return;

        if (CombatManager.isInCombat(event.habbo)) {
            if (attackingHabbos.contains(event.habbo.getHabboInfo().getId())) {
                event.setCancelled(true);
                event.habbo.getRoomUnit().stopWalking();

                int goalX = -1;
                int goalY = -1;
                com.eu.habbo.habbohotel.rooms.RoomUnit unit = event.habbo.getRoomUnit();
                int curX = unit.getX();
                int curY = unit.getY();

                try {
                    try {
                        Object tileObj = unit.getClass().getMethod("getGoal").invoke(unit);
                        if (tileObj != null) {
                            goalX = ((Number) tileObj.getClass().getMethod("getX").invoke(tileObj)).intValue();
                            goalY = ((Number) tileObj.getClass().getMethod("getY").invoke(tileObj)).intValue();
                        }
                    } catch (Exception eA) {
                    }

                    if (goalX == -1 || goalY == -1) {
                        Class<?> clazz = unit.getClass();
                        while (clazz != null && (goalX == -1 || goalY == -1)) {
                            for (java.lang.reflect.Field f : clazz.getDeclaredFields()) {
                                f.setAccessible(true);
                                String name = f.getName().toLowerCase();
                                if (name.contains("location") || name.contains("current")) {
                                    continue;
                                }
                                if (name.contains("goal") || name.contains("target") || name.contains("final")) {
                                    if (f.getType() == int.class || f.getType() == Integer.class
                                            || f.getType() == short.class || f.getType() == Short.class) {
                                        if (name.contains("x"))
                                            goalX = ((Number) f.get(unit)).intValue();
                                        if (name.contains("y"))
                                            goalY = ((Number) f.get(unit)).intValue();
                                    } else {
                                        Object tileObj = f.get(unit);
                                        if (tileObj != null) {
                                            try {
                                                goalX = ((Number) tileObj.getClass().getMethod("getX").invoke(tileObj))
                                                        .intValue();
                                                goalY = ((Number) tileObj.getClass().getMethod("getY").invoke(tileObj))
                                                        .intValue();
                                            } catch (Exception e2) {
                                                for (java.lang.reflect.Field tf : tileObj.getClass()
                                                        .getDeclaredFields()) {
                                                    tf.setAccessible(true);
                                                    String tfName = tf.getName().toLowerCase();
                                                    if (tfName.equals("x"))
                                                        goalX = ((Number) tf.get(tileObj)).intValue();
                                                    if (tfName.equals("y"))
                                                        goalY = ((Number) tf.get(tileObj)).intValue();
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            clazz = clazz.getSuperclass();
                        }
                    }
                } catch (Exception e) {
                }

                if (goalX == -1 || goalY == -1) {
                    goalX = event.toLocation.x;
                    goalY = event.toLocation.y;
                }

                int distance = Math.abs(curX - goalX) + Math.abs(curY - goalY);
                if (distance != 1) {
                    event.habbo.whisper("No puedes atacar a ese objetivo.");
                    return;
                }

                attackingHabbos.remove(event.habbo.getHabboInfo().getId());
                event.habbo.whisper("*Das un tajo violento al aire... No has alcanzado a nadie en la casilla (" + goalX
                        + ", " + goalY + ")*");

                CombatManager.PlayerState state = CombatManager.getState(event.habbo);
                if (state != null) {
                    CombatManager.sendGridToClient(event.habbo, curX, curY, state.maxRange);
                    event.habbo.getClient().sendResponse(new CombatMenuComposer(true, true, state.maxRange));
                }
                return;
            }

            CombatManager.CombatMatch match = CombatManager.getMatch(event.habbo);
            CombatManager.PlayerState state = CombatManager.getState(event.habbo);
            if (match == null || state == null)
                return;

            if (match.getCurrentTurn() != event.habbo) {
                event.setCancelled(true);
                event.habbo.getRoomUnit().stopWalking();
                event.habbo.whisper("No puedes moverte. Es el turno de tu oponente.");
                return;
            }

            int nextX = event.toLocation.x;
            int nextY = event.toLocation.y;
            int stepCost = Math.abs(event.fromLocation.x - nextX) + Math.abs(event.fromLocation.y - nextY);

            if (state.maxRange - stepCost < 0) {
                event.setCancelled(true);
                event.habbo.getRoomUnit().stopWalking();
                event.habbo.whisper("No te quedan suficientes puntos de movimiento.");
                return;
            }

            state.maxRange -= stepCost;
            CombatManager.sendGridToClient(event.habbo, nextX, nextY, state.maxRange);
            event.habbo.getClient().sendResponse(new CombatMenuComposer(true, true, state.maxRange));
        }
    }

    @EventHandler
    public static void onUserTalk(UserTalkEvent event) {
        if (event.habbo == null || event.chatMessage == null)
            return;
        String message = event.chatMessage.getMessage();
        if (message == null)
            return;
        if (message.equals(":pasar_turno")) {
            event.setCancelled(true);
            if (CombatManager.isInCombat(event.habbo)) {
                attackingHabbos.remove(event.habbo.getHabboInfo().getId());
                CombatManager.passTurn(event.habbo);
            }
            return;
        }
        if (message.equals(":atacar")) {
            event.setCancelled(true);
            if (CombatManager.isInCombat(event.habbo)) {
                CombatManager.CombatMatch match = CombatManager.getMatch(event.habbo);
                if (match != null && match.getCurrentTurn() == event.habbo) {
                    attackingHabbos.add(event.habbo.getHabboInfo().getId());
                    event.habbo.whisper("¡Modo Objetivo! Haz click EXACTAMENTE sobre el rival para golpearle.");
                    int curX = event.habbo.getRoomUnit().getX();
                    int curY = event.habbo.getRoomUnit().getY();
                    CombatManager.sendGridToClient(event.habbo, curX, curY, 1);
                } else {
                    event.habbo.whisper("No puedes atacar. Es el turno de tu rival.");
                }
            }
            return;
        }
        if (message.startsWith(":combate")) {
            event.setCancelled(true);
            if (CombatManager.isInCombat(event.habbo)) {
                CombatManager.leaveCombat(event.habbo);
            } else {
                String[] parts = message.split(" ");
                if (parts.length > 1) {
                    String targetName = parts[1];
                    Habbo target = Emulator.getGameEnvironment().getHabboManager().getHabbo(targetName);
                    if (target == null || target.getRoomUnit() == null || event.habbo.getRoomUnit() == null ||
                            target.getRoomUnit().getRoom() != event.habbo.getRoomUnit().getRoom()) {
                        event.habbo.whisper("El usuario objetivo no se encuentra en esta sala.");
                        return;
                    }
                    if (target == event.habbo) {
                        event.habbo.whisper("No puedes iniciar un combate contra ti mismo.");
                        return;
                    }
                    CombatManager.joinOrCreateMatch(event.habbo, target);
                } else {
                    event.habbo.whisper("Uso correcto: :combate NombreDelRival");
                }
            }
        }
    }
}