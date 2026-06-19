package com.eu.habbo.bhrpg.combat;

import com.eu.habbo.messages.incoming.rooms.users.RoomUserWalkEvent;
import com.eu.habbo.habbohotel.users.Habbo;
import java.lang.reflect.Field;

public class CustomCombatWalkEvent extends RoomUserWalkEvent {
    @Override
    public void handle() throws Exception {
        if (this.client == null || this.client.getHabbo() == null) {
            super.handle();
            return;
        }

        Habbo habbo = this.client.getHabbo();

        // Extraemos el ByteBuf de Netty mediante reflexión agnóstica por tipo
        io.netty.buffer.ByteBuf buf = null;
        try {
            Class<?> clazz = this.packet.getClass();
            while (clazz != null && buf == null) {
                for (Field f : clazz.getDeclaredFields()) {
                    if (io.netty.buffer.ByteBuf.class.isAssignableFrom(f.getType())) {
                        f.setAccessible(true);
                        buf = (io.netty.buffer.ByteBuf) f.get(this.packet);
                        break;
                    }
                }
                clazz = clazz.getSuperclass();
            }
        } catch (Exception e) {
            // Fallback silencioso
        }

        if (buf == null) {
            super.handle();
            return;
        }

        // Guardamos el índice del buffer por si el jugador solo quiere caminar normal
        buf.markReaderIndex();
        int clickX = this.packet.readInt();
        int clickY = this.packet.readInt();

        // ¿El usuario está en Modo Ataque esperando un objetivo?
        if (CombatListener.isAttacking(habbo)) {

            // REGLA 1: Validación estricta de la plantilla de ataque (Rango Melee = 1)
            int curX = habbo.getRoomUnit().getX();
            int curY = habbo.getRoomUnit().getY();
            if (Math.abs(curX - clickX) > 1 || Math.abs(curY - clickY) > 1) {
                habbo.whisper("Esa casilla está fuera de tu plantilla de ataque. Elige un objetivo válido.");
                return; // Bloqueamos el paso pero NO consumimos el modo ataque (mantiene el turno)
            }

            // Si está dentro de la plantilla, ahora sí consumimos el ataque de forma
            // definitiva
            CombatListener.removeAttacking(habbo);

            Habbo victim = null;
            if (habbo.getRoomUnit() != null && habbo.getRoomUnit().getRoom() != null) {
                for (Habbo h : habbo.getRoomUnit().getRoom().getHabbos()) {
                    if (h != habbo && CombatManager.isInCombat(h)) {
                        // REGLA MATEMÁTICA ESTRICTA: El oponente debe pisar EXACTAMENTE las coordenadas
                        // del click
                        if (h.getRoomUnit().getX() == clickX && h.getRoomUnit().getY() == clickY) {
                            victim = h;
                            break;
                        }
                    }
                }
            }

            if (victim != null) {
                int dañoProv = 25;
                habbo.whisper("¡Impacto certero! Has golpeado a " + victim.getHabboInfo().getUsername()
                        + " e infligido " + dañoProv + " DMG.");
                victim.whisper("¡Alerta! " + habbo.getHabboInfo().getUsername() + " te ha encajado un golpe de "
                        + dañoProv + " DMG.");
            } else {
                habbo.whisper("*Das un tajo violento al aire... No había nadie en la casilla (" + clickX + ", " + clickY
                        + ")*");
            }

            // Restauramos la grilla visual de movimiento normal del jugador
            CombatManager.CombatMatch match = CombatManager.getMatch(habbo);
            CombatManager.PlayerState state = CombatManager.getState(habbo);
            if (state != null) {
                CombatManager.sendGridToClient(habbo, habbo.getRoomUnit().getX(), habbo.getRoomUnit().getY(),
                        state.maxRange);
                habbo.getClient().sendResponse(new CombatMenuComposer(true, true, state.maxRange));
            }

            // Cortamos el flujo aquí. El paquete NO llega al super.handle() y el personaje
            // no camina.
            return;
        }

        // Si no estaba atacando, reseteamos el lector al inicio y dejamos que Arcturus
        // mueva al keko normalmente
        buf.resetReaderIndex();
        super.handle();
    }
}