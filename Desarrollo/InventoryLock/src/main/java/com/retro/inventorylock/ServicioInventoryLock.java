package com.retro.inventorylock;

import com.eu.habbo.Emulator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

public final class ServicioInventoryLock
{
    private static final int MAX_INTENTOS = 5;
    private static final int BLOQUEO_SEGUNDOS = 30;

    private ServicioInventoryLock()
    {
    }

    public static Estado consultarEstado(int usuarioId) throws Exception
    {
        boolean enabled = false;
        int blockedSeconds = 0;

        try(Connection conexion = Emulator.getDatabase().getDataSource().getConnection();
            PreparedStatement consulta = conexion.prepareStatement(
                    "SELECT enabled, blocked_until " +
                    "FROM inventory_pattern_lock WHERE user_id = ?"))
        {
            consulta.setInt(1, usuarioId);

            try(ResultSet resultado = consulta.executeQuery())
            {
                if(resultado.next())
                {
                    enabled = resultado.getBoolean("enabled");
                    blockedSeconds = segundosRestantes(resultado.getTimestamp("blocked_until"));
                }
            }
        }

        boolean unlocked = !enabled || SesionesInventoryLock.estaDesbloqueado(usuarioId);

        return new Estado(enabled, unlocked, blockedSeconds);
    }

    public static boolean estaHabilitado(int usuarioId) throws Exception
    {
        try(Connection conexion = Emulator.getDatabase().getDataSource().getConnection();
            PreparedStatement consulta = conexion.prepareStatement(
                    "SELECT enabled FROM inventory_pattern_lock WHERE user_id = ?"))
        {
            consulta.setInt(1, usuarioId);

            try(ResultSet resultado = consulta.executeQuery())
            {
                return resultado.next() && resultado.getBoolean("enabled");
            }
        }
    }

    public static ResultadoVerificacion verificar(
            int usuarioId,
            String patron,
            boolean desbloquearSiCorrecto) throws Exception
    {
        try(Connection conexion = Emulator.getDatabase().getDataSource().getConnection())
        {
            boolean enabled;
            String hash;
            String salt;
            int intentos;
            Timestamp blockedUntil;

            try(PreparedStatement consulta = conexion.prepareStatement(
                    "SELECT enabled, pattern_hash, salt, failed_attempts, blocked_until " +
                    "FROM inventory_pattern_lock WHERE user_id = ?"))
            {
                consulta.setInt(1, usuarioId);

                try(ResultSet resultado = consulta.executeQuery())
                {
                    if(!resultado.next())
                    {
                        if(desbloquearSiCorrecto)
                        {
                            SesionesInventoryLock.desbloquear(usuarioId);
                        }

                        return new ResultadoVerificacion(true, 0);
                    }

                    enabled = resultado.getBoolean("enabled");

                    if(!enabled)
                    {
                        if(desbloquearSiCorrecto)
                        {
                            SesionesInventoryLock.desbloquear(usuarioId);
                        }

                        return new ResultadoVerificacion(true, 0);
                    }

                    hash = resultado.getString("pattern_hash");
                    salt = resultado.getString("salt");
                    intentos = resultado.getInt("failed_attempts");
                    blockedUntil = resultado.getTimestamp("blocked_until");
                }
            }

            int segundosBloqueo = segundosRestantes(blockedUntil);

            if(segundosBloqueo > 0)
            {
                return new ResultadoVerificacion(false, segundosBloqueo);
            }

            if(blockedUntil != null)
            {
                intentos = 0;

                try(PreparedStatement limpiar = conexion.prepareStatement(
                        "UPDATE inventory_pattern_lock " +
                        "SET failed_attempts = 0, blocked_until = NULL " +
                        "WHERE user_id = ?"))
                {
                    limpiar.setInt(1, usuarioId);
                    limpiar.executeUpdate();
                }
            }

            boolean correcto = SeguridadPatron.verificar(patron, salt, hash);

            if(correcto)
            {
                try(PreparedStatement limpiar = conexion.prepareStatement(
                        "UPDATE inventory_pattern_lock " +
                        "SET failed_attempts = 0, blocked_until = NULL " +
                        "WHERE user_id = ?"))
                {
                    limpiar.setInt(1, usuarioId);
                    limpiar.executeUpdate();
                }

                if(desbloquearSiCorrecto)
                {
                    SesionesInventoryLock.desbloquear(usuarioId);
                }

                return new ResultadoVerificacion(true, 0);
            }

            intentos++;

            if(intentos >= MAX_INTENTOS)
            {
                Timestamp nuevoBloqueo = new Timestamp(
                        System.currentTimeMillis() + (BLOQUEO_SEGUNDOS * 1000L)
                );

                try(PreparedStatement bloquear = conexion.prepareStatement(
                        "UPDATE inventory_pattern_lock " +
                        "SET failed_attempts = 0, blocked_until = ? " +
                        "WHERE user_id = ?"))
                {
                    bloquear.setTimestamp(1, nuevoBloqueo);
                    bloquear.setInt(2, usuarioId);
                    bloquear.executeUpdate();
                }

                return new ResultadoVerificacion(false, BLOQUEO_SEGUNDOS);
            }

            try(PreparedStatement fallo = conexion.prepareStatement(
                    "UPDATE inventory_pattern_lock " +
                    "SET failed_attempts = ? WHERE user_id = ?"))
            {
                fallo.setInt(1, intentos);
                fallo.setInt(2, usuarioId);
                fallo.executeUpdate();
            }

            return new ResultadoVerificacion(false, 0);
        }
    }

    public static void guardarPatron(int usuarioId, String patron) throws Exception
    {
        SeguridadPatron.Credenciales credenciales = SeguridadPatron.crear(patron);

        try(Connection conexion = Emulator.getDatabase().getDataSource().getConnection();
            PreparedStatement guardar = conexion.prepareStatement(
                    "INSERT INTO inventory_pattern_lock " +
                    "(user_id, enabled, pattern_hash, salt, failed_attempts, blocked_until) " +
                    "VALUES (?, 1, ?, ?, 0, NULL) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "enabled = 1, pattern_hash = VALUES(pattern_hash), " +
                    "salt = VALUES(salt), failed_attempts = 0, blocked_until = NULL"))
        {
            guardar.setInt(1, usuarioId);
            guardar.setString(2, credenciales.getHash());
            guardar.setString(3, credenciales.getSalt());
            guardar.executeUpdate();
        }

        SesionesInventoryLock.bloquear(usuarioId);
    }

    public static void desactivar(int usuarioId) throws Exception
    {
        try(Connection conexion = Emulator.getDatabase().getDataSource().getConnection();
            PreparedStatement actualizar = conexion.prepareStatement(
                    "UPDATE inventory_pattern_lock " +
                    "SET enabled = 0, pattern_hash = NULL, salt = NULL, " +
                    "failed_attempts = 0, blocked_until = NULL " +
                    "WHERE user_id = ?"))
        {
            actualizar.setInt(1, usuarioId);
            actualizar.executeUpdate();
        }

        SesionesInventoryLock.bloquear(usuarioId);
    }

    private static int segundosRestantes(Timestamp blockedUntil)
    {
        if(blockedUntil == null)
        {
            return 0;
        }

        long diferencia = blockedUntil.getTime() - System.currentTimeMillis();

        if(diferencia <= 0)
        {
            return 0;
        }

        return (int) Math.ceil(diferencia / 1000.0);
    }

    public static final class Estado
    {
        public final boolean enabled;
        public final boolean unlocked;
        public final int blockedSeconds;

        public Estado(boolean enabled, boolean unlocked, int blockedSeconds)
        {
            this.enabled = enabled;
            this.unlocked = unlocked;
            this.blockedSeconds = blockedSeconds;
        }
    }

    public static final class ResultadoVerificacion
    {
        public final boolean correcto;
        public final int blockedSeconds;

        public ResultadoVerificacion(boolean correcto, int blockedSeconds)
        {
            this.correcto = correcto;
            this.blockedSeconds = blockedSeconds;
        }
    }
}