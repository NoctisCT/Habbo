package com.retro.subastas.mensajes;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.MessageHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class ObtenerProximaSubasta extends MessageHandler
{
    @Override
    public void handle()
    {
        if(this.client == null || this.client.getHabbo() == null)
        {
            return;
        }

        String sql =
                "SELECT s.id, s.nombre_objeto, s.precio_inicial, s.cantidad, " +
                "s.posicion_cola, u.username AS vendedor, ib.sprite_id, ib.type, " +
                "ss.nombre AS sesion_nombre, " +
                "DATE_FORMAT(ss.fecha_inicio, '%d/%m/%Y %H:%i') AS inicio_formateado " +
                "FROM subastas s " +
                "INNER JOIN sesiones_subastas ss ON ss.id = s.sesion_id " +
                "INNER JOIN users u ON u.id = s.vendedor_id " +
                "INNER JOIN items_base ib ON ib.id = s.furni_id " +
                "WHERE s.estado = 'en_cola' " +
                "AND ss.estado IN ('abierta','programada') " +
                "AND ss.fecha_fin > NOW() " +
                "ORDER BY " +
                "CASE WHEN ss.fecha_inicio <= NOW() AND ss.fecha_fin > NOW() THEN 0 ELSE 1 END, " +
                "ss.fecha_inicio ASC, s.posicion_cola ASC, s.id ASC " +
                "LIMIT 1";

        try(Connection conexion = Emulator.getDatabase().getDataSource().getConnection();
            PreparedStatement consulta = conexion.prepareStatement(sql);
            ResultSet resultado = consulta.executeQuery())
        {
            if(!resultado.next())
            {
                enviarVacia();
                return;
            }

            ServerMessage respuesta = new ServerMessage(5022);

            respuesta.appendBoolean(true);
            respuesta.appendInt(resultado.getInt("id"));
            respuesta.appendString(valor(resultado.getString("nombre_objeto"), "Furni"));
            respuesta.appendInt(resultado.getInt("sprite_id"));
            respuesta.appendString(valor(resultado.getString("type"), "s"));
            respuesta.appendString(valor(resultado.getString("vendedor"), ""));
            respuesta.appendInt(resultado.getInt("precio_inicial"));
            respuesta.appendInt(Math.max(1, resultado.getInt("cantidad")));
            respuesta.appendInt(Math.max(1, resultado.getInt("posicion_cola")));
            respuesta.appendString(valor(resultado.getString("sesion_nombre"), "Próxima sesión"));

            respuesta.appendString(
                    valor(resultado.getString("inicio_formateado"), "")
            );

            this.client.sendResponse(respuesta);
        }
        catch(Exception error)
        {
            System.out.println("[Subastas] ERROR obteniendo próxima subasta: "
                    + error.getClass().getName() + ": " + error.getMessage());
            error.printStackTrace();

            enviarVacia();
        }
    }

    private void enviarVacia()
    {
        ServerMessage respuesta = new ServerMessage(5022);

        respuesta.appendBoolean(false);
        respuesta.appendInt(0);
        respuesta.appendString("");
        respuesta.appendInt(0);
        respuesta.appendString("s");
        respuesta.appendString("");
        respuesta.appendInt(0);
        respuesta.appendInt(0);
        respuesta.appendInt(0);
        respuesta.appendString("");
        respuesta.appendString("");

        this.client.sendResponse(respuesta);
    }

    private static String valor(String valor, String porDefecto)
    {
        return valor == null || valor.isBlank() ? porDefecto : valor;
    }
}