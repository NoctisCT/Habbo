package com.retro.subastas;

import com.eu.habbo.Emulator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class GestorAvisosSesiones
{
    private static ScheduledExecutorService ejecutor;

    private GestorAvisosSesiones()
    {
    }

    public static synchronized void iniciar()
    {
        if(ejecutor != null && !ejecutor.isShutdown())
        {
            return;
        }

        try
        {
            prepararBaseDatos();
        }
        catch(Exception error)
        {
            System.out.println("[Subastas] ERROR preparando avisos de sesiones: "
                    + error.getMessage());
            error.printStackTrace();
            return;
        }

        ejecutor = Executors.newSingleThreadScheduledExecutor(runnable ->
        {
            Thread hilo = new Thread(runnable, "Avisos-Sesiones-Subastas");
            hilo.setDaemon(true);
            return hilo;
        });

        ejecutor.scheduleWithFixedDelay(
                GestorAvisosSesiones::cicloSeguro,
                2,
                2,
                TimeUnit.SECONDS
        );

        System.out.println("[Subastas] Avisos de apertura/cierre iniciados");
    }

    public static synchronized void detener()
    {
        if(ejecutor == null)
        {
            return;
        }

        ejecutor.shutdownNow();
        ejecutor = null;

        System.out.println("[Subastas] Avisos de apertura/cierre detenidos");
    }

    private static void prepararBaseDatos() throws Exception
    {
        try(Connection conexion = Emulator.getDatabase().getDataSource().getConnection();
            PreparedStatement apertura = conexion.prepareStatement(
                    "ALTER TABLE sesiones_subastas " +
                    "ADD COLUMN IF NOT EXISTS aviso_apertura_enviado TINYINT(1) NOT NULL DEFAULT 0");
            PreparedStatement cierre = conexion.prepareStatement(
                    "ALTER TABLE sesiones_subastas " +
                    "ADD COLUMN IF NOT EXISTS aviso_cierre_enviado TINYINT(1) NOT NULL DEFAULT 0"))
        {
            apertura.executeUpdate();
            cierre.executeUpdate();
        }

        // Evita avisos falsos al instalar/reiniciar a mitad de una sesion.
        // Solo las sesiones futuras quedan con sus flags a 0.
        try(Connection conexion = Emulator.getDatabase().getDataSource().getConnection();
            PreparedStatement marcarAperturasPasadas = conexion.prepareStatement(
                    "UPDATE sesiones_subastas " +
                    "SET aviso_apertura_enviado = 1 " +
                    "WHERE fecha_inicio <= NOW()");
            PreparedStatement marcarCierresPasados = conexion.prepareStatement(
                    "UPDATE sesiones_subastas " +
                    "SET aviso_cierre_enviado = 1 " +
                    "WHERE fecha_fin <= NOW()"))
        {
            marcarAperturasPasadas.executeUpdate();
            marcarCierresPasados.executeUpdate();
        }
    }

    private static void cicloSeguro()
    {
        try
        {
            avisarApertura();
            avisarCierre();
        }
        catch(Exception error)
        {
            System.out.println("[Subastas] ERROR en avisos de sesiones: "
                    + error.getClass().getName() + ": " + error.getMessage());
            error.printStackTrace();
        }
    }

    private static void avisarApertura() throws Exception
    {
        int sesionId = 0;
        String nombre = "";

        Connection conexion = null;

        try
        {
            conexion = Emulator.getDatabase().getDataSource().getConnection();
            conexion.setAutoCommit(false);

            try(PreparedStatement consulta = conexion.prepareStatement(
                    "SELECT id, nombre FROM sesiones_subastas " +
                    "WHERE fecha_inicio <= NOW() AND fecha_fin > NOW() " +
                    "AND aviso_apertura_enviado = 0 " +
                    "ORDER BY fecha_inicio ASC, id ASC LIMIT 1 FOR UPDATE");
                ResultSet resultado = consulta.executeQuery())
            {
                if(!resultado.next())
                {
                    conexion.rollback();
                    return;
                }

                sesionId = resultado.getInt("id");
                nombre = resultado.getString("nombre");
            }

            try(PreparedStatement marcar = conexion.prepareStatement(
                    "UPDATE sesiones_subastas " +
                    "SET aviso_apertura_enviado = 1 " +
                    "WHERE id = ? AND aviso_apertura_enviado = 0"))
            {
                marcar.setInt(1, sesionId);

                if(marcar.executeUpdate() != 1)
                {
                    conexion.rollback();
                    return;
                }
            }

            conexion.commit();
        }
        catch(Exception error)
        {
            if(conexion != null)
            {
                try { conexion.rollback(); } catch(Exception ignorado) {}
            }

            throw error;
        }
        finally
        {
            if(conexion != null)
            {
                try
                {
                    conexion.setAutoCommit(true);
                    conexion.close();
                }
                catch(Exception ignorado)
                {
                }
            }
        }

        DifusorAvisosSubastas.difundir(
                "\u00a1Las subastas han comenzado! Abre Subastas para participar."
        );

        System.out.println("[Subastas] Apertura anunciada para sesion #"
                + sesionId + " (" + nombre + ")");
    }

    private static void avisarCierre() throws Exception
    {
        int sesionId = 0;
        String nombre = "";

        Connection conexion = null;

        try
        {
            conexion = Emulator.getDatabase().getDataSource().getConnection();
            conexion.setAutoCommit(false);

            try(PreparedStatement consulta = conexion.prepareStatement(
                    "SELECT id, nombre FROM sesiones_subastas " +
                    "WHERE fecha_fin <= NOW() " +
                    "AND aviso_apertura_enviado = 1 " +
                    "AND aviso_cierre_enviado = 0 " +
                    "ORDER BY fecha_fin ASC, id ASC LIMIT 1 FOR UPDATE");
                ResultSet resultado = consulta.executeQuery())
            {
                if(!resultado.next())
                {
                    conexion.rollback();
                    return;
                }

                sesionId = resultado.getInt("id");
                nombre = resultado.getString("nombre");
            }

            try(PreparedStatement marcar = conexion.prepareStatement(
                    "UPDATE sesiones_subastas " +
                    "SET aviso_cierre_enviado = 1 " +
                    "WHERE id = ? AND aviso_cierre_enviado = 0"))
            {
                marcar.setInt(1, sesionId);

                if(marcar.executeUpdate() != 1)
                {
                    conexion.rollback();
                    return;
                }
            }

            conexion.commit();
        }
        catch(Exception error)
        {
            if(conexion != null)
            {
                try { conexion.rollback(); } catch(Exception ignorado) {}
            }

            throw error;
        }
        finally
        {
            if(conexion != null)
            {
                try
                {
                    conexion.setAutoCommit(true);
                    conexion.close();
                }
                catch(Exception ignorado)
                {
                }
            }
        }

        DifusorAvisosSubastas.difundir(
                "La sesi\u00f3n de subastas ha finalizado. Gracias por participar."
        );

        System.out.println("[Subastas] Cierre anunciado para sesion #"
                + sesionId + " (" + nombre + ")");
    }
}