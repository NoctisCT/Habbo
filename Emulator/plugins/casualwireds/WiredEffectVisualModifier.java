package com.eu.habbo.casualwireds;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.habbohotel.gameclients.GameClient;

import com.eu.habbo.messages.outgoing.rooms.users.RoomUserEffectComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserDataComposer;
import com.eu.habbo.messages.outgoing.MessageComposer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredEffectVisualModifier extends InteractionWiredEffect {

    public WiredEffectVisualModifier(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectVisualModifier(int id, int userId, Item item, String extradata, int limitedStack,
            int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    private void broadcastComposer(Room room, MessageComposer composer) {
        if (room == null || composer == null)
            return;
        for (Habbo h : room.getHabbos()) {
            if (h != null && h.getClient() != null) {
                h.getClient().sendResponse(composer);
            }
        }
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        if (roomUnit == null || room == null)
            return false;

        Habbo habbo = room.getHabbo(roomUnit);
        if (habbo == null)
            return false;

        String extraData = this.getExtradata();
        if (extraData == null || extraData.isEmpty())
            extraData = "0";

        int type = 0;
        int duration = 10;
        String value = "1";

        try {
            if (extraData.contains(";")) {
                String[] parts = extraData.split(";");
                if (parts.length >= 1)
                    type = Integer.parseInt(parts[0]);
                if (parts.length >= 2)
                    duration = Integer.parseInt(parts[1]);
                if (parts.length >= 3)
                    value = parts[2];
            } else {
                type = Integer.parseInt(extraData.trim());
            }

            if (type == 2 && value.equals("1"))
                value = "throne";
            if (type == 3 && value.equals("1"))
                value = "4";

            System.out.println("[CasualWireds] NATIVO -> Ejecutando Tipo: " + type + " Duracion: " + duration
                    + " Valor: " + value);

            final int finalDuration = duration;

            switch (type) {
                case 0: // CAMBIAR ESCALA
                    break;

                case 1: // INVISIBILIDAD
                    roomUnit.setInvisible(true);
                    roomUnit.setEffectId(185, duration);
                    roomUnit.statusUpdate(true);
                    broadcastComposer(room, new RoomUserEffectComposer(roomUnit));

                    Emulator.getThreading().run(() -> {
                        if (habbo.getRoomUnit() != null) {
                            roomUnit.setInvisible(false);
                            roomUnit.setEffectId(0, 0);
                            roomUnit.statusUpdate(true);
                            broadcastComposer(room, new RoomUserEffectComposer(roomUnit));
                        }
                    }, finalDuration * 1000L);
                    break;

                case 2: // PROP HUNT
                    String lookOriginal = habbo.getHabboInfo().getLook();
                    habbo.getHabboInfo().setLook("furni_" + value);
                    roomUnit.statusUpdate(true);
                    broadcastComposer(room, new RoomUserDataComposer(habbo));

                    Emulator.getThreading().run(() -> {
                        if (habbo.getRoomUnit() != null) {
                            habbo.getHabboInfo().setLook(lookOriginal);
                            roomUnit.statusUpdate(true);
                            broadcastComposer(room, new RoomUserDataComposer(habbo));
                        }
                    }, finalDuration * 1000L);
                    break;

                case 3: // VELOCIDAD / SUPER VELOCIDAD DE CAMINATA
                    // 1. CLAVAMOS LA FÍSICA REAL DEL FASTWALK (COMO EL OTRO WIRED)
                    roomUnit.setFastWalk(true);

                    // 2. LE METEMOS EL EFECTO VISUAL DE LAS ZAPATILLAS (MÉTODO ESTÉTICO)
                    try {
                        int effectId = Integer.parseInt(value);
                        roomUnit.setEffectId(effectId, duration);
                    } catch (Exception e) {
                        roomUnit.setEffectId(4, duration); // Si falla, forzamos el efecto de flotar/correr
                    }

                    roomUnit.statusUpdate(true);
                    broadcastComposer(room, new RoomUserEffectComposer(roomUnit));

                    // 3. TEMPORIZADOR PARA QUITAR LA VELOCIDAD
                    Emulator.getThreading().run(() -> {
                        if (habbo.getRoomUnit() != null) {
                            roomUnit.setFastWalk(false); // Apagamos la física
                            roomUnit.setEffectId(0, 0); // Quitamos las zapatillas
                            roomUnit.statusUpdate(true);
                            broadcastComposer(room, new RoomUserEffectComposer(roomUnit));
                        }
                    }, finalDuration * 1000L);
                    break;
            }

        } catch (Exception e) {
            System.out.println("[CasualWireds] Error en ejecucion nativa: " + e.getMessage());
        }

        return true;
    }

    @Override
    public String getWiredData() {
        return this.getExtradata();
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException {
        this.setExtradata(set.getString("wired_data"));
    }

    @Override
    public void onPickUp() {
        this.setExtradata("");
    }

    @Override
    public WiredEffectType getType() {
        return WiredEffectType.SHOW_MESSAGE;
    }

    @Override
    public void serializeWiredData(ServerMessage packet, Room room) {
        packet.appendBoolean(false);
        packet.appendInt(5);
        packet.appendInt(0);
        packet.appendInt(this.getBaseItem().getSpriteId());
        packet.appendInt(this.getId());
        packet.appendString(this.getExtradata());
        packet.appendInt(0);

        packet.appendInt(88); // <--- ¡LA CLAVE! Forzamos el código 88 para tu nuevo "Act" independiente

        packet.appendInt(this.getDelay());
        packet.appendInt(0);
    }

    @Override
    public boolean saveData(WiredSettings settings, GameClient gameClient) {
        if (settings == null)
            return false;

        this.setExtradata(settings.getStringParam());
        this.setDelay(settings.getDelay());
        return true;
    }

    @Override
    public boolean requiresTriggeringUser() {
        return true;
    }
}