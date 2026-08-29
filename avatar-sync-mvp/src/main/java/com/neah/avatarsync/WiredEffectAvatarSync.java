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
 * AvatarSync WIRED effect.
 *
 * stringData:
 *
 * mode;durationSeconds
 *
 * durationSeconds == 0:
 * unlimited
 */
public class WiredEffectAvatarSync
        extends InteractionWiredEffect {

    private static final int LAYOUT_CODE =
            89;

    /*
     * Configuracion persistente del WIRED.
     *
     * NO usar extradata para esto: Morningstar puede usar
     * extradata como estado runtime/visual del furni.
     */
    private volatile String wiredConfig = "1;0";

    public WiredEffectAvatarSync(
            ResultSet set,
            Item baseItem) throws SQLException {

        super(
                set,
                baseItem
        );
        // AVATARSYNC_WIRED_OPEN_DIAGNOSTIC
    }

    public WiredEffectAvatarSync(
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

        int mode =
                1;

        int durationSeconds =
                0;

        String data =
                this.wiredConfig;

        if (data == null
                || data.trim().isEmpty()) {

            data =
                    "1;0";
        }

        try {

            final String[] parts =
                    data.split(";");

            if (parts.length >= 1) {

                mode =
                        Integer.parseInt(
                                parts[0].trim()
                        );
            }

            if (parts.length >= 2) {

                durationSeconds =
                        Integer.parseInt(
                                parts[1].trim()
                        );
            }
        }
        catch (Exception ignored) {

            mode =
                    1;

            durationSeconds =
                    0;
        }

        if (mode < 1
                || mode > 3) {

            mode =
                    1;
        }

        durationSeconds =
                Math.max(
                        0,
                        Math.min(
                                3600,
                                durationSeconds
                        )
                );

        final AvatarSyncPlugin plugin =
                AvatarSyncPlugin.getInstance();

        if (plugin == null) {

            return false;
        }

        return plugin.activateWiredAvatarSync(
                this.getId(),
                roomUnit,
                room,
                mode,
                durationSeconds
        );
    }

    @Override
    public String getWiredData() {

        return this.wiredConfig;
    }

    @Override
    public void loadWiredData(
            ResultSet set,
            Room room) throws SQLException {

        String data =
                set.getString(
                        "wired_data"
                );

        this.wiredConfig =
                (data == null || data.trim().isEmpty())
                        ? "1;0"
                        : data.trim();
    }

    @Override
    public void onPickUp() {

        final AvatarSyncPlugin plugin =
                AvatarSyncPlugin.getInstance();

        if (plugin != null) {

            plugin.stopWiredAvatarSync(
                    this.getId()
            );
        }

        this.setExtradata("");
    }

    @Override
    public WiredEffectType getType() {

        /*
         * Mismo tipo base usado por el Visual Modifier
         * custom que ya funciona con este Morningstar.
         */
        return WiredEffectType.SHOW_MESSAGE;
    }

    @Override
    public void serializeWiredData(
            ServerMessage packet,
            Room room) {

        /*
         * Misma estructura del Visual Modifier,
         * usando el layout Nitro 89.
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
                this.wiredConfig
        );

        packet.appendInt(
                0
        );

        packet.appendInt(
                0
        ); // stuffTypeSelectionCode
        packet.appendInt(
                LAYOUT_CODE
        );

        packet.appendInt(
                this.getDelay()
        );

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

        String data =
                settings.getStringParam();

        if (data == null
                || data.trim().isEmpty()) {

            data =
                    "1;0";
        }

        this.wiredConfig = data.trim();

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
