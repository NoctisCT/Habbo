package com.eu.habbo.bhrpg.combat;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class CombatGridComposer extends MessageComposer {
    private final String gridData;

    public CombatGridComposer(String gridData) {
        this.gridData = gridData;
    }

    @Override
    protected ServerMessage composeInternal() {
        // Usamos el header 3500 (un ID libre clásico en Arcturus para paquetes custom)
        ServerMessage message = new ServerMessage(3500);
        message.appendString(this.gridData);
        return message;
    }
}