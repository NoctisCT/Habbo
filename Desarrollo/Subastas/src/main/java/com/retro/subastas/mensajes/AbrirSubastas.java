package com.retro.subastas.mensajes;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.MessageHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AbrirSubastas extends MessageHandler
{
    @Override
    public void handle()
    {
        try(Connection conexion = Emulator.getDatabase().getDataSource().getConnection();
            PreparedStatement consulta = conexion.prepareStatement(
                    "SELECT s.id, s.nombre_objeto, s.precio_inicial, s.puja_actual, " +
                    "u.username AS vendedor, ib.sprite_id, ib.type, " +
                    "GREATEST(0, TIMESTAMPDIFF(SECOND, NOW(), s.fecha_fin)) AS segundos " +
                    "FROM subastas s " +
                    "INNER JOIN users u ON u.id = s.vendedor_id " +
                    "INNER JOIN items_base ib ON ib.id = s.furni_id " +
                    "WHERE s.estado = 'activa' " +
                    "AND (s.fecha_fin IS NULL OR s.fecha_fin > NOW()) " +
                    "ORDER BY s.id ASC LIMIT 1"))
        {
            try(ResultSet resultado = consulta.executeQuery())
            {
                if(resultado.next())
                {
                    enviarEstado(
                            true,
                            "Subasta activa.",
                            resultado.getInt("id"),
                            resultado.getString("nombre_objeto"),
                            resultado.getInt("sprite_id"),
                            resultado.getString("type"),
                            resultado.getString("vendedor"),
                            resultado.getInt("precio_inicial"),
                            resultado.getInt("puja_actual"),
                            resultado.getInt("segundos")
                    );

                    return;
                }
            }
        }
        catch(Exception error)
        {
            System.out.println("[Subastas] ERROR consultando subasta activa: "
                    + error.getClass().getName() + ": " + error.getMessage());
            error.printStackTrace();
        }

        enviarEstado(
                false,
                "No hay una subasta activa ahora mismo.",
                0,
                "",
                0,
                "s",
                "",
                0,
                0,
                0
        );
    }

    private void enviarEstado(
            boolean activa,
            String mensaje,
            int subastaId,
            String objeto,
            int spriteId,
            String tipo,
            String vendedor,
            int precioInicial,
            int pujaActual,
            int segundosRestantes)
    {
        ServerMessage respuesta = new ServerMessage(5001);
        respuesta.appendBoolean(activa);
        respuesta.appendString(mensaje);
        respuesta.appendInt(subastaId);
        respuesta.appendString(objeto);
        respuesta.appendInt(spriteId);
        respuesta.appendString(tipo == null ? "s" : tipo);
        respuesta.appendString(vendedor);
        respuesta.appendInt(precioInicial);
        respuesta.appendInt(pujaActual);
        respuesta.appendInt(segundosRestantes);

        this.client.sendResponse(respuesta);
    }
}