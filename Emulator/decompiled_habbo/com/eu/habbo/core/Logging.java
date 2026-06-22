/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.core;

import com.eu.habbo.Emulator;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Logging {
    private static final Logger LOGGER = LoggerFactory.getLogger("LegacyLogger");

    @Deprecated
    public void logStart(Object line) {
        LOGGER.info("[LOADING] {}", line);
    }

    @Deprecated
    public void logShutdownLine(Object line) {
        LOGGER.info("[SHUTDOWN] {}", line);
    }

    @Deprecated
    public void logUserLine(Object line) {
        LOGGER.info("[USER] {}", line);
    }

    @Deprecated
    public void logDebugLine(Object line) {
        LOGGER.debug("[DEBUG] {}", line);
    }

    @Deprecated
    public void logPacketLine(Object line) {
        if (Emulator.getConfig().getBoolean("debug.show.packets")) {
            LOGGER.debug("[PACKET] {}", line);
        }
    }

    @Deprecated
    public void logUndefinedPacketLine(Object line) {
        if (Emulator.getConfig().getBoolean("debug.show.packets.undefined")) {
            LOGGER.debug("[PACKET] [UNDEFINED] {}", line);
        }
    }

    @Deprecated
    public void logErrorLine(Object line) {
        LOGGER.error("[ERROR] {}", line);
    }

    @Deprecated
    public void logSQLException(SQLException e) {
        LOGGER.error("[ERROR] SQLException", e);
    }

    @Deprecated
    public void logPacketError(Object e) {
        LOGGER.error("[ERROR] PacketError {}", e);
    }

    @Deprecated
    public void handleException(Exception e) {
        LOGGER.error("[ERROR] Exception", e);
    }
}

