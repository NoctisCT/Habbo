package com.retro.subastas;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.inventory.AddHabboItemComposer;
import com.eu.habbo.messages.outgoing.inventory.InventoryRefreshComposer;
import com.eu.habbo.messages.outgoing.users.UserCreditsComposer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class GestorSubastas
{
    private static ScheduledExecutorService ejecutor;

    private GestorSubastas()
    {
    }

    public static synchronized void iniciar()
    {
        if(ejecutor != null && !ejecutor.isShutdown())
        {
            return;
        }

        ejecutor = Executors.newSingleThreadScheduledExecutor(runnable ->
        {
            Thread hilo = new Thread(runnable, "Motor-Subastas");
            hilo.setDaemon(true);
            return hilo;
        });

        ejecutor.scheduleWithFixedDelay(
                GestorSubastas::cicloSeguro,
                1,
                1,
                TimeUnit.SECONDS
        );

        System.out.println("[Subastas] Motor de cola iniciado");
    }

    public static synchronized void detener()
    {
        if(ejecutor == null)
        {
            return;
        }

        ejecutor.shutdownNow();
        ejecutor = null;

        System.out.println("[Subastas] Motor de cola detenido");
    }

    private static void cicloSeguro()
    {
        try
        {
            LotesSubastas.asegurarEstructura();
            GestorSesionesSubastas.mantener();

            if(finalizarActivaSiCorresponde())
            {
                return;
            }

            activarSiguienteSiCorresponde();
        }
        catch(Exception error)
        {
            System.out.println("[Subastas] ERROR en motor de cola: "
                    + error.getClass().getName() + ": " + error.getMessage());
            error.printStackTrace();
        }
    }

    private static boolean finalizarActivaSiCorresponde() throws Exception
    {
        Connection conexion = null;

        int subastaId = 0;
        int vendedorId = 0;
        int objetoId = 0;
        List<Integer> objetoIds = null;
        int ganadorId = 0;
        int pujaFinal = 0;
        String nombreObjeto = "";
        boolean vendida = false;
        int saldoVendedor = -1;

        try
        {
            conexion = Emulator.getDatabase().getDataSource().getConnection();
            conexion.setAutoCommit(false);

            Timestamp fechaFin;
            boolean expirada;

            try(PreparedStatement consulta = conexion.prepareStatement(
                    "SELECT id, vendedor_id, objeto_id, nombre_objeto, " +
                    "puja_actual, mejor_postor_id, fecha_fin, " +
                    "CASE WHEN fecha_fin IS NOT NULL AND fecha_fin <= NOW() " +
                    "THEN 1 ELSE 0 END AS expirada " +
                    "FROM subastas " +
                    "WHERE estado = 'activa' " +
                    "ORDER BY id ASC LIMIT 1 FOR UPDATE"))
            {
                try(ResultSet resultado = consulta.executeQuery())
                {
                    if(!resultado.next())
                    {
                        conexion.rollback();
                        return false;
                    }

                    subastaId = resultado.getInt("id");
                    vendedorId = resultado.getInt("vendedor_id");
                    objetoId = resultado.getInt("objeto_id");
                    nombreObjeto = resultado.getString("nombre_objeto");
                    pujaFinal = resultado.getInt("puja_actual");

                    int posibleGanador = resultado.getInt("mejor_postor_id");

                    if(!resultado.wasNull())
                    {
                        ganadorId = posibleGanador;
                    }

                    fechaFin = resultado.getTimestamp("fecha_fin");
                    expirada = resultado.getInt("expirada") == 1;
                }
            }

            objetoIds = LotesSubastas.obtenerObjetoIds(
                    conexion,
                    subastaId,
                    objetoId
            );

            if(fechaFin == null)
            {
                try(PreparedStatement reparar = conexion.prepareStatement(
                        "UPDATE subastas " +
                        "SET fecha_inicio = COALESCE(fecha_inicio, NOW()), " +
                        "fecha_fin = DATE_ADD(NOW(), INTERVAL ? SECOND) " +
                        "WHERE id = ? AND estado = 'activa'"))
                {
                    reparar.setInt(1, ConfiguracionSubastas.getDuracionSegundos());
                    reparar.setInt(2, subastaId);
                    reparar.executeUpdate();
                }

                conexion.commit();
                difundirEstadoActual(subastaId);

                return false;
            }

            if(!expirada)
            {
                conexion.rollback();
                return false;
            }

            vendida = (ganadorId > 0 && pujaFinal > 0);

            if(vendida)
            {
                LotesSubastas.transferirDesdeEscrow(
                        conexion,
                        objetoIds,
                        ganadorId
                );

                int comision = (pujaFinal * ConfiguracionSubastas.getComisionPorcentaje()) / 100;
                int pagoVendedor = pujaFinal - comision;

                try(PreparedStatement pagar = conexion.prepareStatement(
                        "UPDATE users SET credits = credits + ? WHERE id = ?"))
                {
                    pagar.setInt(1, pagoVendedor);
                    pagar.setInt(2, vendedorId);

                    if(pagar.executeUpdate() != 1)
                    {
                        throw new IllegalStateException(
                                "No se pudo pagar al vendedor " + vendedorId
                        );
                    }
                }

                try(PreparedStatement saldo = conexion.prepareStatement(
                        "SELECT credits FROM users WHERE id = ?"))
                {
                    saldo.setInt(1, vendedorId);

                    try(ResultSet resultado = saldo.executeQuery())
                    {
                        if(resultado.next())
                        {
                            saldoVendedor = resultado.getInt("credits");
                        }
                    }
                }

                try(PreparedStatement finalizar = conexion.prepareStatement(
                        "UPDATE subastas " +
                        "SET estado = 'vendida', fecha_finalizacion = NOW(), " +
                        "comision_aplicada = ?, neto_vendedor = ? " +
                        "WHERE id = ? AND estado = 'activa'"))
                {
                    finalizar.setInt(1, comision);
                    finalizar.setInt(2, pagoVendedor);
                    finalizar.setInt(3, subastaId);

                    if(finalizar.executeUpdate() != 1)
                    {
                        throw new IllegalStateException(
                                "No se pudo finalizar la subasta " + subastaId
                        );
                    }
                }
            }
            else
            {
                LotesSubastas.transferirDesdeEscrow(
                        conexion,
                        objetoIds,
                        vendedorId
                );

                try(PreparedStatement finalizar = conexion.prepareStatement(
                        "UPDATE subastas " +
                        "SET estado = 'sin_vender', fecha_finalizacion = NOW() " +
                        "WHERE id = ? AND estado = 'activa'"))
                {
                    finalizar.setInt(1, subastaId);

                    if(finalizar.executeUpdate() != 1)
                    {
                        throw new IllegalStateException(
                                "No se pudo finalizar la subasta " + subastaId
                        );
                    }
                }
            }

            conexion.commit();
        }
        catch(Exception error)
        {
            if(conexion != null)
            {
                try
                {
                    conexion.rollback();
                }
                catch(Exception ignorado)
                {
                }
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

        if(vendida)
        {
            LotesSubastas.refrescarInventarioOnline(ganadorId, objetoIds);

            if(saldoVendedor >= 0)
            {
                sincronizarCreditosOnline(vendedorId, saldoVendedor);
            }

            int comision = (pujaFinal * ConfiguracionSubastas.getComisionPorcentaje()) / 100;
            int pagoVendedor = pujaFinal - comision;

            System.out.println("[Subastas] Subasta #" + subastaId
                    + " vendida por " + pujaFinal
                    + " creditos. Vendedor recibe " + pagoVendedor
                    + "; comision " + comision + ".");
        }
        else
        {
            LotesSubastas.refrescarInventarioOnline(vendedorId, objetoIds);

            System.out.println("[Subastas] Subasta #" + subastaId
                    + " finalizada sin pujas. Objeto devuelto.");
        }

        difundirSinSubasta(
                vendida
                        ? nombreObjeto + " se ha vendido por " + pujaFinal + " cr\u00e9ditos."
                        : nombreObjeto + " ha finalizado sin pujas."
        );

        return true;
    }

    private static void activarSiguienteSiCorresponde() throws Exception
    {
        int sesionActivaId = GestorSesionesSubastas.obtenerSesionActivaId();

        if(sesionActivaId <= 0)
        {
            return;
        }

        Connection conexion = null;

        int subastaId = 0;
        int posicionAnterior = 0;

        try
        {
            conexion = Emulator.getDatabase().getDataSource().getConnection();
            conexion.setAutoCommit(false);

            try(PreparedStatement activa = conexion.prepareStatement(
                    "SELECT id FROM subastas " +
                    "WHERE estado = 'activa' " +
                    "ORDER BY id ASC LIMIT 1 FOR UPDATE"))
            {
                try(ResultSet resultado = activa.executeQuery())
                {
                    if(resultado.next())
                    {
                        conexion.rollback();
                        return;
                    }
                }
            }

            try(PreparedStatement siguiente = conexion.prepareStatement(
                    "SELECT id, posicion_cola FROM subastas " +
                    "WHERE sesion_id = ? AND estado = 'en_cola' " +
                    "ORDER BY posicion_cola ASC, id ASC " +
                    "LIMIT 1 FOR UPDATE"))
            {
                siguiente.setInt(1, sesionActivaId);

                try(ResultSet resultado = siguiente.executeQuery())
                {
                    if(!resultado.next())
                    {
                        conexion.rollback();
                        return;
                    }

                    subastaId = resultado.getInt("id");
                    posicionAnterior = resultado.getInt("posicion_cola");
                }
            }

            try(PreparedStatement activar = conexion.prepareStatement(
                    "UPDATE subastas " +
                    "SET estado = 'activa', posicion_cola = 0, " +
                    "fecha_inicio = NOW(), " +
                    "fecha_fin = DATE_ADD(NOW(), INTERVAL ? SECOND) " +
                    "WHERE id = ? AND estado = 'en_cola'"))
            {
                activar.setInt(1, ConfiguracionSubastas.getDuracionSegundos());
                activar.setInt(2, subastaId);

                if(activar.executeUpdate() != 1)
                {
                    throw new IllegalStateException(
                            "No se pudo activar la subasta " + subastaId
                    );
                }
            }

            try(PreparedStatement compactar = conexion.prepareStatement(
                    "UPDATE subastas " +
                    "SET posicion_cola = posicion_cola - 1 " +
                    "WHERE sesion_id = ? AND estado = 'en_cola' AND posicion_cola > ?"))
            {
                compactar.setInt(1, sesionActivaId);
                compactar.setInt(2, posicionAnterior);
                compactar.executeUpdate();
            }

            conexion.commit();
        }
        catch(Exception error)
        {
            if(conexion != null)
            {
                try
                {
                    conexion.rollback();
                }
                catch(Exception ignorado)
                {
                }
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

        System.out.println("[Subastas] Subasta #" + subastaId
                + " activada durante " + ConfiguracionSubastas.getDuracionSegundos()
                + " segundos en la sesion #" + sesionActivaId);

        difundirEstadoActual(subastaId);
    }

    private static void difundirEstadoActual(int subastaId)
    {
        try(Connection conexion = Emulator.getDatabase().getDataSource().getConnection();
            PreparedStatement consulta = conexion.prepareStatement(
                    "SELECT s.id, s.nombre_objeto, s.precio_inicial, s.puja_actual, " +
                    "u.username AS vendedor, ib.sprite_id, ib.type, " +
                    "GREATEST(0, TIMESTAMPDIFF(SECOND, NOW(), s.fecha_fin)) AS segundos " +
                    "FROM subastas s " +
                    "INNER JOIN users u ON u.id = s.vendedor_id " +
                    "INNER JOIN items_base ib ON ib.id = s.furni_id " +
                    "WHERE s.id = ? AND s.estado = 'activa' LIMIT 1"))
        {
            consulta.setInt(1, subastaId);

            try(ResultSet resultado = consulta.executeQuery())
            {
                if(!resultado.next())
                {
                    return;
                }

                int id = resultado.getInt("id");
                String objeto = resultado.getString("nombre_objeto");
                int spriteId = resultado.getInt("sprite_id");
                String tipo = resultado.getString("type");
                String vendedor = resultado.getString("vendedor");
                int precioInicial = resultado.getInt("precio_inicial");
                int pujaActual = resultado.getInt("puja_actual");
                int segundos = resultado.getInt("segundos");

                for(Habbo habbo : Emulator.getGameEnvironment()
                        .getHabboManager()
                        .getOnlineHabbos()
                        .values())
                {
                    if(habbo == null || habbo.getClient() == null)
                    {
                        continue;
                    }

                    ServerMessage respuesta = new ServerMessage(5001);
                    respuesta.appendBoolean(true);
                    respuesta.appendString("Subasta activa.");
                    respuesta.appendInt(id);
                    respuesta.appendString(objeto == null ? "Furni" : objeto);
                    respuesta.appendInt(spriteId);
                    respuesta.appendString(tipo == null ? "s" : tipo);
                    respuesta.appendString(vendedor == null ? "" : vendedor);
                    respuesta.appendInt(precioInicial);
                    respuesta.appendInt(pujaActual);
                    respuesta.appendInt(segundos);

                    habbo.getClient().sendResponse(respuesta);
                }
            }
        }
        catch(Exception error)
        {
            System.out.println("[Subastas] ERROR difundiendo subasta activa: "
                    + error.getMessage());
            error.printStackTrace();
        }
    }

    private static void difundirSinSubasta(String mensaje)
    {
        for(Habbo habbo : Emulator.getGameEnvironment()
                .getHabboManager()
                .getOnlineHabbos()
                .values())
        {
            if(habbo == null || habbo.getClient() == null)
            {
                continue;
            }

            ServerMessage respuesta = new ServerMessage(5001);
            respuesta.appendBoolean(false);
            respuesta.appendString(mensaje);
            respuesta.appendInt(0);
            respuesta.appendString("");
            respuesta.appendInt(0);
            respuesta.appendString("s");
            respuesta.appendString("");
            respuesta.appendInt(0);
            respuesta.appendInt(0);
            respuesta.appendInt(0);

            habbo.getClient().sendResponse(respuesta);
        }
    }

    private static void anadirObjetoInventarioOnline(int usuarioId, int objetoId)
    {
        try
        {
            Habbo habbo = Emulator.getGameEnvironment()
                    .getHabboManager()
                    .getHabbo(usuarioId);

            if(habbo == null || habbo.getClient() == null)
            {
                return;
            }

            if(habbo.getInventory()
                    .getItemsComponent()
                    .getHabboItem(objetoId) != null)
            {
                return;
            }

            HabboItem item = null;

            try(Connection conexion = Emulator.getDatabase().getDataSource().getConnection();
                PreparedStatement consulta = conexion.prepareStatement(
                        "SELECT * FROM items WHERE id = ? AND user_id = ? LIMIT 1"))
            {
                consulta.setInt(1, objetoId);
                consulta.setInt(2, usuarioId);

                try(ResultSet resultado = consulta.executeQuery())
                {
                    if(resultado.next())
                    {
                        item = Emulator.getGameEnvironment()
                                .getItemManager()
                                .loadHabboItem(resultado);
                    }
                }
            }

            if(item == null)
            {
                return;
            }

            habbo.getInventory()
                    .getItemsComponent()
                    .addItem(item);

            habbo.getClient().sendResponse(new AddHabboItemComposer(item));
            habbo.getClient().sendResponse(new InventoryRefreshComposer());
        }
        catch(Exception error)
        {
            System.out.println("[Subastas] Aviso: no se pudo refrescar el inventario online de "
                    + usuarioId + ": " + error.getMessage());
            error.printStackTrace();
        }
    }

    private static void sincronizarCreditosOnline(int usuarioId, int saldo)
    {
        try
        {
            Habbo habbo = Emulator.getGameEnvironment()
                    .getHabboManager()
                    .getHabbo(usuarioId);

            if(habbo == null)
            {
                return;
            }

            habbo.getHabboInfo().setCredits(saldo);

            if(habbo.getClient() != null)
            {
                habbo.getClient().sendResponse(new UserCreditsComposer(habbo));
            }
        }
        catch(Exception error)
        {
            System.out.println("[Subastas] Aviso: no se pudo refrescar el saldo online de "
                    + usuarioId + ": " + error.getMessage());
        }
    }
}