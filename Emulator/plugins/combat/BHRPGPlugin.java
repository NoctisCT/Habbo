package com.eu.habbo.bhrpg.combat;

import com.eu.habbo.Emulator;
import com.eu.habbo.plugin.HabboPlugin;
import com.eu.habbo.habbohotel.users.Habbo;
import java.lang.reflect.Field;
import java.util.Map;

public class BHRPGPlugin extends HabboPlugin {
    @Override
    public void onEnable() {
        System.out.println("[RPG LOG] Iniciando carga del plugin BHRPG...");

        // 1. Registro de eventos estándar
        Emulator.getPluginManager().registerEvents(this, new CombatListener());

        // 2. INYECTOR DE RED: Secuestramos Walk y LookAtPoint por reflexión dinámica
        int walkHeader = -1;
        int lookHeader = -1;
        try {
            Object packetManager = Emulator.getGameServer().getPacketManager();
            Class<?> clazz = packetManager.getClass();

            while (clazz != null && (walkHeader == -1 || lookHeader == -1)) {
                for (Field f : clazz.getDeclaredFields()) {
                    if (Map.class.isAssignableFrom(f.getType()) || f.getType().getName().contains("Map")) {
                        f.setAccessible(true);
                        Map<?, ?> map = (Map<?, ?>) f.get(packetManager);
                        if (map != null) {
                            for (Map.Entry<?, ?> entry : map.entrySet()) {
                                if (entry
                                        .getValue() == com.eu.habbo.messages.incoming.rooms.users.RoomUserWalkEvent.class) {
                                    if (entry.getKey() instanceof Integer)
                                        walkHeader = (Integer) entry.getKey();
                                }
                                if (entry
                                        .getValue() == com.eu.habbo.messages.incoming.rooms.users.RoomUserLookAtPoint.class) {
                                    if (entry.getKey() instanceof Integer)
                                        lookHeader = (Integer) entry.getKey();
                                }
                            }
                        }
                    }
                }
                clazz = clazz.getSuperclass();
            }

            // Registro seguro de los handlers dentro del entorno protegido try-catch
            if (walkHeader != -1) {
                Emulator.getGameServer().getPacketManager().registerHandler(walkHeader, CustomCombatWalkEvent.class);
                System.out.println("[RPG LOG] ¡Secuestro de Walk completado (ID: " + walkHeader + ")!");
            }
            if (lookHeader != -1) {
                Emulator.getGameServer().getPacketManager().registerHandler(lookHeader, CustomCombatLookEvent.class);
                System.out.println("[RPG LOG] ¡Secuestro de Look completado (ID: " + lookHeader + ")!");
            }

        } catch (Exception e) {
            System.out.println("[RPG LOG] Error crítico configurando interceptores de red.");
            e.printStackTrace();
        }

        System.out.println("[RPG LOG] Plugin BHRPG cargado con éxito.");
    }

    @Override
    public void onDisable() {
    }

    @Override
    public boolean hasPermission(Habbo habbo, String permission) {
        return true;
    }
}