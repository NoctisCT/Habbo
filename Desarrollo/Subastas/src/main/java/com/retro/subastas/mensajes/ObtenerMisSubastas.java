package com.retro.subastas.mensajes;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.retro.subastas.ConfiguracionSubastas;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ObtenerMisSubastas extends MessageHandler
{
    private static class FilaSubasta
    {
        int id;
        int objetoId;
        int furniId;
        int spriteId;
        String tipo;
        String nombre;
        int precioInicial;
        int pujaActual;
        String estado;
        int posicionCola;
        String fechaCreacion;
        String ganador;
        int comisionAplicada = -1;
        int netoVendedor = -1;
    }

    @Override
    public void handle()
    {
        if(this.client == null || this.client.getHabbo() == null)
        {
            return;
        }

        int usuarioId = this.client.getHabbo().getHabboInfo().getId();
        int limite = tieneClubActivo()
                ? ConfiguracionSubastas.getLimiteVip()
                : ConfiguracionSubastas.getLimiteNormal();

        List<FilaSubasta> filas = new ArrayList<>();

        String sql =
                "SELECT s.id, s.objeto_id, s.furni_id, s.nombre_objeto, " +
                "s.precio_inicial, s.puja_actual, s.estado, s.posicion_cola, " +
                "s.fecha_creacion, ib.sprite_id, ib.type, " +
                "ganador.username AS ganador, " +
                "s.comision_aplicada, s.neto_vendedor " +
                "FROM subastas s " +
                "INNER JOIN items_base ib ON ib.id = s.furni_id " +
                "LEFT JOIN users ganador ON ganador.id = s.mejor_postor_id " +
                "WHERE s.vendedor_id = ? " +
                "ORDER BY CASE s.estado " +
                "WHEN 'activa' THEN 1 " +
                "WHEN 'en_cola' THEN 2 " +
                "WHEN 'vendida' THEN 3 " +
                "WHEN 'sin_vender' THEN 4 " +
                "WHEN 'cancelada' THEN 5 " +
                "ELSE 6 END, s.id DESC " +
                "LIMIT " + ConfiguracionSubastas.getMaxMisSubastas();

        try(Connection conexion = Emulator.getDatabase().getDataSource().getConnection();
            PreparedStatement consulta = conexion.prepareStatement(sql))
        {
            consulta.setInt(1, usuarioId);

            try(ResultSet resultado = consulta.executeQuery())
            {
                while(resultado.next())
                {
                    FilaSubasta fila = new FilaSubasta();

                    fila.id = resultado.getInt("id");
                    fila.objetoId = resultado.getInt("objeto_id");
                    fila.furniId = resultado.getInt("furni_id");
                    fila.spriteId = resultado.getInt("sprite_id");
                    fila.tipo = resultado.getString("type");
                    fila.nombre = resultado.getString("nombre_objeto");
                    fila.precioInicial = resultado.getInt("precio_inicial");
                    fila.pujaActual = resultado.getInt("puja_actual");
                    fila.estado = resultado.getString("estado");
                    fila.posicionCola = resultado.getInt("posicion_cola");
                    fila.fechaCreacion = resultado.getTimestamp("fecha_creacion").toString();
                    fila.ganador = resultado.getString("ganador");

                    int comision = resultado.getInt("comision_aplicada");
                    fila.comisionAplicada = resultado.wasNull() ? -1 : comision;

                    int neto = resultado.getInt("neto_vendedor");
                    fila.netoVendedor = resultado.wasNull() ? -1 : neto;

                    filas.add(fila);
                }
            }
        }
        catch(Exception error)
        {
            System.out.println("[Subastas] ERROR obteniendo Mis subastas: "
                    + error.getClass().getName() + ": " + error.getMessage());
            error.printStackTrace();
        }

        int simultaneas = 0;

        for(FilaSubasta fila : filas)
        {
            if("en_cola".equals(fila.estado) || "activa".equals(fila.estado))
            {
                simultaneas++;
            }
        }

        ServerMessage respuesta = new ServerMessage(5007);
        respuesta.appendInt(limite);
        respuesta.appendInt(simultaneas);
        respuesta.appendInt(filas.size());

        for(FilaSubasta fila : filas)
        {
            respuesta.appendInt(fila.id);
            respuesta.appendInt(fila.objetoId);
            respuesta.appendInt(fila.furniId);
            respuesta.appendInt(fila.spriteId);
            respuesta.appendString(fila.tipo == null ? "s" : fila.tipo);
            respuesta.appendString(fila.nombre == null ? "Furni" : fila.nombre);
            respuesta.appendInt(fila.precioInicial);
            respuesta.appendInt(fila.pujaActual);
            respuesta.appendString(fila.estado == null ? "" : fila.estado);
            respuesta.appendInt(fila.posicionCola);
            respuesta.appendString(fila.fechaCreacion == null ? "" : fila.fechaCreacion);
            respuesta.appendString(fila.ganador == null ? "" : fila.ganador);
            respuesta.appendInt(fila.comisionAplicada);
            respuesta.appendInt(fila.netoVendedor);
        }

        this.client.sendResponse(respuesta);

}

    private boolean tieneClubActivo()
    {
        try
        {
            Method getHabboStats = this.client.getHabbo().getClass().getMethod("getHabboStats");
            Object stats = getHabboStats.invoke(this.client.getHabbo());

            if(stats == null) return false;

            Method hasActiveClub = stats.getClass().getMethod("hasActiveClub");
            Object resultado = hasActiveClub.invoke(stats);

            return Boolean.TRUE.equals(resultado);
        }
        catch(Exception ignorado)
        {
            return false;
        }
    }
}