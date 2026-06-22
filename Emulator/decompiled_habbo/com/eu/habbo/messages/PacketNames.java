/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages;

import com.eu.habbo.messages.incoming.Incoming;
import com.eu.habbo.messages.outgoing.Outgoing;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PacketNames {
    private static final Logger LOGGER = LoggerFactory.getLogger(PacketNames.class);
    private final HashMap<Integer, String> incoming = new HashMap();
    private final HashMap<Integer, String> outgoing = new HashMap();

    public void initialize() {
        PacketNames.getNames(Incoming.class, this.incoming);
        PacketNames.getNames(Outgoing.class, this.outgoing);
    }

    public String getIncomingName(int key) {
        return this.incoming.getOrDefault(key, "Unknown");
    }

    public String getOutgoingName(int key) {
        return this.outgoing.getOrDefault(key, "Unknown");
    }

    private static void getNames(Class<?> clazz, HashMap<Integer, String> target) {
        for (Field field : clazz.getFields()) {
            int modifiers = field.getModifiers();
            if (!Modifier.isPublic(modifiers) || !Modifier.isStatic(modifiers) || !Modifier.isFinal(modifiers) || field.getType() != Integer.TYPE) continue;
            try {
                int packetId = field.getInt(null);
                if (packetId <= 0) continue;
                if (target.containsKey(packetId)) {
                    LOGGER.warn("Duplicate packet id found {} for {}.", (Object)packetId, (Object)clazz.getSimpleName());
                    continue;
                }
                target.put(packetId, field.getName());
            }
            catch (IllegalAccessException e) {
                LOGGER.error("Failed to read field integer.", e);
            }
        }
    }
}

