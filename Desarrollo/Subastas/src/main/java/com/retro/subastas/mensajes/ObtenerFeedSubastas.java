package com.retro.subastas.mensajes;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.MessageHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ObtenerFeedSubastas extends MessageHandler
{
    private static final int MAX_EVENTOS = 20;

    private static final class Evento
    {
        String id;
        String tipo;
        String mensaje;
        String hora;
    }

    @Override
    public void handle()
    {
        if(this.client == null || this.client.getHabbo() == null)
        {
            return;
        }

        List<Evento> eventos = new ArrayList<>();

        String sql =
                "SELECT evento_id, tipo, mensaje, " +
                "DATE_FORMAT(fecha_evento, '%H:%i:%s') AS hora " +
                "FROM (" +

                "SELECT " +
                "CONCAT('inicio:', s.id) AS evento_id, " +
                "'inicio' AS tipo, " +
                "s.fecha_inicio AS fecha_evento, " +
                "CONCAT('Empieza ', s.nombre_objeto, ' de ', v.username, " +
                "' desde ', s.precio_inicial, ' cr\u00e9ditos.') AS mensaje " +
                "FROM subastas s " +
                "INNER JOIN users v ON v.id = s.vendedor_id " +
                "WHERE s.fecha_inicio IS NOT NULL " +

                "UNION ALL " +

                "SELECT " +
                "CONCAT('puja:', p.id) AS evento_id, " +
                "'puja' AS tipo, " +
                "p.fecha AS fecha_evento, " +
                "CONCAT(u.username, ' puj\u00f3 ', p.cantidad, ' cr\u00e9ditos por ', " +
                "s.nombre_objeto, '.') AS mensaje " +
                "FROM pujas_subastas p " +
                "INNER JOIN subastas s ON s.id = p.subasta_id " +
                "INNER JOIN users u ON u.id = p.postor_id " +

                "UNION ALL " +

                "SELECT " +
                "CONCAT('fin:', s.id) AS evento_id, " +
                "CASE WHEN s.estado = 'vendida' THEN 'venta' ELSE 'sin_pujas' END AS tipo, " +
                "s.fecha_finalizacion AS fecha_evento, " +
                "CASE " +
                "WHEN s.estado = 'vendida' " +
                "THEN CONCAT(s.nombre_objeto, ' vendido a ', COALESCE(g.username, '?'), " +
                "' por ', s.puja_actual, ' cr\u00e9ditos.') " +
                "ELSE CONCAT(s.nombre_objeto, ' termin\u00f3 sin pujas.') " +
                "END AS mensaje " +
                "FROM subastas s " +
                "LEFT JOIN users g ON g.id = s.mejor_postor_id " +
                "WHERE s.estado IN ('vendida', 'sin_vender') " +
                "AND s.fecha_finalizacion IS NOT NULL " +

                ") eventos " +
                "ORDER BY fecha_evento DESC, evento_id DESC " +
                "LIMIT ?";

        try(Connection conexion = Emulator.getDatabase().getDataSource().getConnection();
            PreparedStatement consulta = conexion.prepareStatement(sql))
        {
            consulta.setInt(1, MAX_EVENTOS);

            try(ResultSet resultado = consulta.executeQuery())
            {
                while(resultado.next())
                {
                    Evento evento = new Evento();
                    evento.id = resultado.getString("evento_id");
                    evento.tipo = resultado.getString("tipo");
                    evento.mensaje = resultado.getString("mensaje");
                    evento.hora = resultado.getString("hora");

                    eventos.add(evento);
                }
            }
        }
        catch(Exception error)
        {
            System.out.println("[Subastas] ERROR obteniendo feed: "
                    + error.getClass().getName() + ": " + error.getMessage());
            error.printStackTrace();

            enviar(new ArrayList<>());
            return;
        }

        enviar(eventos);
    }

    private void enviar(List<Evento> eventos)
    {
        ServerMessage respuesta = new ServerMessage(5018);
        respuesta.appendInt(eventos.size());

        for(Evento evento : eventos)
        {
            respuesta.appendString(evento.id == null ? "" : evento.id);
            respuesta.appendString(evento.tipo == null ? "" : evento.tipo);
            respuesta.appendString(evento.mensaje == null ? "" : evento.mensaje);
            respuesta.appendString(evento.hora == null ? "" : evento.hora);
        }

        this.client.sendResponse(respuesta);
    }
}