package com.retro.subastas.mensajes;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.retro.subastas.GestorSesionesSubastas;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ObtenerEstadoSesionSubastas extends MessageHandler
{
    @Override
    public void handle()
    {
        try
        {
            GestorSesionesSubastas.asegurarSesionProgramada();

            try(Connection conexion = Emulator.getDatabase().getDataSource().getConnection())
            {
                GestorSesionesSubastas.SesionDestino sesion =
                        GestorSesionesSubastas.obtenerSesionDestino(conexion);

                if(sesion == null)
                {
                    enviar(false, 0, "", 0, 0, "", "");
                    return;
                }

                String nombreVisible = "";
                String inicio = "";
                String fin = "";

                try(PreparedStatement consulta = conexion.prepareStatement(
                        "SELECT " +
                        "CONCAT('Sesi\u00f3n del ', DATE_FORMAT(fecha_inicio, '%d/%m/%Y')) AS nombre_visible, " +
                        "DATE_FORMAT(fecha_inicio, '%H:%i') AS inicio, " +
                        "DATE_FORMAT(fecha_fin, '%H:%i') AS fin " +
                        "FROM sesiones_subastas WHERE id = ? LIMIT 1"))
                {
                    consulta.setInt(1, sesion.id);

                    try(ResultSet resultado = consulta.executeQuery())
                    {
                        if(resultado.next())
                        {
                            nombreVisible = resultado.getString("nombre_visible");
                            inicio = resultado.getString("inicio");
                            fin = resultado.getString("fin");
                        }
                    }
                }

                enviar(
                        true,
                        sesion.id,
                        nombreVisible,
                        sesion.cupoMaximo,
                        sesion.ocupados,
                        inicio,
                        fin
                );
            }
        }
        catch(Exception error)
        {
            System.out.println("[Subastas] ERROR obteniendo estado de sesion: "
                    + error.getClass().getName() + ": " + error.getMessage());
            error.printStackTrace();

            enviar(false, 0, "", 0, 0, "", "");
        }
    }

    private void enviar(
            boolean disponible,
            int sesionId,
            String nombre,
            int cupoMaximo,
            int ocupados,
            String inicio,
            String fin)
    {
        ServerMessage respuesta = new ServerMessage(5013);
        respuesta.appendBoolean(disponible);
        respuesta.appendInt(sesionId);
        respuesta.appendString(nombre == null ? "" : nombre);
        respuesta.appendInt(cupoMaximo);
        respuesta.appendInt(ocupados);
        respuesta.appendString(inicio == null ? "" : inicio);
        respuesta.appendString(fin == null ? "" : fin);

        this.client.sendResponse(respuesta);
    }
}