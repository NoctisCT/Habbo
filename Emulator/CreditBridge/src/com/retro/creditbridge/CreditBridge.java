package com.retro.creditbridge;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.EventHandler;
import com.eu.habbo.plugin.EventListener;
import com.eu.habbo.plugin.HabboPlugin;
import com.eu.habbo.plugin.events.emulator.EmulatorLoadedEvent;
import com.retro.creditbridge.rcon.CreditBridgePing;
import com.retro.creditbridge.rcon.CreditCredits;
import com.retro.creditbridge.rcon.DebitCredits;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CreditBridge extends HabboPlugin implements EventListener
{
    private static final String HEARTBEAT_KEY = "morningstar";
    private static final long HEARTBEAT_PERIOD_SECONDS = 5L;

    private static ScheduledExecutorService heartbeatExecutor;

    @Override
    public void onEnable()
    {
        Emulator.getPluginManager()
            .registerEvents(this, this);
    }

    @EventHandler
    public void onEmulatorLoaded(
        EmulatorLoadedEvent event
    )
    {
        Emulator.getRconServer()
            .addRCONMessage(
                "creditbridgeping",
                CreditBridgePing.class
            );

        Emulator.getRconServer()
            .addRCONMessage(
                "debitcredits",
                DebitCredits.class
            );

        Emulator.getRconServer()
            .addRCONMessage(
                "creditcredits",
                CreditCredits.class
            );

        startHeartbeat();

        System.out.println(
            "[CreditBridge] RCON commands registered."
        );
    }

    private synchronized void startHeartbeat()
    {
        if (
            heartbeatExecutor != null &&
            !heartbeatExecutor.isShutdown()
        )
        {
            return;
        }

        writeHeartbeat();

        heartbeatExecutor =
            Executors.newSingleThreadScheduledExecutor(
                runnable ->
                {
                    Thread thread =
                        new Thread(
                            runnable,
                            "CreditBridge-Heartbeat"
                        );

                    thread.setDaemon(true);

                    return thread;
                }
            );

        heartbeatExecutor.scheduleAtFixedRate(
            this::writeHeartbeat,
            HEARTBEAT_PERIOD_SECONDS,
            HEARTBEAT_PERIOD_SECONDS,
            TimeUnit.SECONDS
        );
    }

    private void writeHeartbeat()
    {
        try (
            Connection connection =
                Emulator.getDatabase()
                    .getDataSource()
                    .getConnection();

            PreparedStatement statement =
                connection.prepareStatement(
                    "INSERT INTO emulator_heartbeats " +
                    "(heartbeat_key, last_seen_at) " +
                    "VALUES (?, ?) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "last_seen_at = VALUES(last_seen_at)"
                )
        )
        {
            statement.setString(
                1,
                HEARTBEAT_KEY
            );

            statement.setLong(
                2,
                Emulator.getIntUnixTimestamp()
            );

            statement.executeUpdate();
        }
        catch (SQLException exception)
        {
            System.err.println(
                "[CreditBridge] Heartbeat DB error: " +
                exception.getMessage()
            );
        }
    }

    @Override
    public void onDisable()
    {
        /*
         * Intencionadamente no se detiene el heartbeat mientras la JVM
         * siga viva. Representa vida de Morningstar, no disponibilidad
         * del plugin. El hilo daemon termina al cerrar el proceso Java.
         *
         * Asi Laravel nunca cae a SQL si Morningstar sigue vivo pero
         * RCON/CreditBridge tiene una averia temporal.
         */
    }

    @Override
    public boolean hasPermission(
        Habbo habbo,
        String permission
    )
    {
        return false;
    }
}
