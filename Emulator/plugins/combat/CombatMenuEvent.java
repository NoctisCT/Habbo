package com.eu.habbo.bhrpg.combat;

import com.eu.habbo.messages.incoming.MessageHandler;

public class CombatMenuEvent extends MessageHandler {

    @Override
    public void handle() throws Exception {
        // En Arcturus 3.6.0 el cliente se obtiene directamente de la variable heredada
        // 'this.client'
        if (this.client == null || this.client.getHabbo() == null
                || !CombatManager.isInCombat(this.client.getHabbo())) {
            return;
        }

        // CORRECCIÓN DEFINITIVA: El frontend ahora solo envía el String.
        // Leemos la acción directamente del buffer sin saltarnos ningún entero.
        String accion = this.packet.readString().toLowerCase();

        // CHIVATO DE CONTROL: Veremos en la consola negra la acción exacta que llega
        // desde la web
        System.out.println("[RPG LOG] ¡Paquete 4502 Recibido! Acción detectada: '" + accion + "' de "
                + this.client.getHabbo().getHabboInfo().getUsername());

        switch (accion) {
            case "atacar":
                this.client.getHabbo().whisper("Menú FF: Has seleccionado Atacar. Falta definir objetivo.");
                break;

            case "pasar_turno":
                this.client.getHabbo().whisper("Menú FF: Has pasado tu turno.");
                // Ejecutamos el relevo táctico en el manager
                CombatManager.passTurn(this.client.getHabbo());
                break;

            case "habilidades":
                this.client.getHabbo().whisper("Menú FF: Abriendo submenú de técnicas espirituales...");
                break;

            case "objetos":
                this.client.getHabbo().whisper("Menú FF: No tienes objetos en tu inventario de combate.");
                break;

            default:
                this.client.getHabbo().whisper("Acción del menú no reconocida: " + accion);
                break;
        }
    }
}