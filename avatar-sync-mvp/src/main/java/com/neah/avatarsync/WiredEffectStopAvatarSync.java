package com.neah.avatarsync;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.messages.ServerMessage;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * WIRED Effect: Stop Avatar Sync
 *
 * Detiene la sesion Avatar Sync WIRED completa
 * a la que pertenece el usuario causante.
 *
 * No afecta a sincronizaciones creadas mediante comandos.
 */
public class WiredEffectStopAvatarSync
        extends InteractionWiredEffect {

    /*
     * Layout Nitro exclusivo.
     *
     * 89 = Avatar Sync
     * 90 = Stop Avatar Sync
     */
    private static final int LAYOUT_CODE =
            90;

    public WiredEffectStopAvatarSync(
            ResultSet set,
            Item baseItem) throws SQLException {

        super(
                set,
                baseItem
        );
    }

    public WiredEffectStopAvatarSync(
            int id,
            int userId,
            Item item,
            String extradata,
            int limitedStack,
            int limitedSells) {

        super(
                id,
                userId,
                item,
                extradata,
                limitedStack,
                limitedSells
        );
    }

    @Override
    public boolean execute(
            RoomUnit roomUnit,
            Room room,
            Object[] stuff) {

        if (roomUnit == null
                || room == null) {

            return false;
        }

        final AvatarSyncPlugin plugin =
                AvatarSyncPlugin.getInstance();

        if (plugin == null) {

            return false;
        }

        return plugin
                .stopWiredAvatarSyncForParticipant(
                        roomUnit,
                        room
                );
    }

    /*
     * Este WIRED no tiene configuracion propia.
     */
    @Override
    public String getWiredData() {

        return "";
    }

    @Override
    public void loadWiredData(
            ResultSet set,
            Room room) throws SQLException {

        /*
         * Sin configuracion persistente.
         */
    }

    @Override
    public void onPickUp() {

        this.setExtradata("");
    }

    @Override
    public WiredEffectType getType() {

        /*
         * Tipo base compatible usado ya
         * por los WIRED custom locales.
         */
        return WiredEffectType.SHOW_MESSAGE;
    }

    @Override
    public void serializeWiredData(
            ServerMessage packet,
            Room room) {

        /*
         * Wired Action packet:
         *
         * false
         * 5
         * 0
         * sprite
         * item id
         * stringData
         * intParams count
         * stuffTypeSelectionCode
         * layout
         * delay
         * conflictingTriggers count
         */

        packet.appendBoolean(
                false
        );

        packet.appendInt(
                5
        );

        packet.appendInt(
                0
        );

        packet.appendInt(
                this.getBaseItem()
                        .getSpriteId()
        );

        packet.appendInt(
                this.getId()
        );

        packet.appendString(
                ""
        );

        /*
         * intParams count
         */
        packet.appendInt(
                0
        );

        /*
         * stuffTypeSelectionCode:
         * no selecciona furnis.
         */
        packet.appendInt(
                0
        );

        packet.appendInt(
                LAYOUT_CODE
        );

        packet.appendInt(
                this.getDelay()
        );

        /*
         * conflictingTriggers count
         */
        packet.appendInt(
                0
        );
    }

    @Override
    public boolean saveData(
            WiredSettings settings,
            GameClient gameClient) {

        if (settings == null) {

            return false;
        }

        this.setDelay(
                settings.getDelay()
        );

        return true;
    }

    @Override
    public boolean requiresTriggeringUser() {

        return true;
    }
}