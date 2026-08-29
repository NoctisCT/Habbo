package com.retro.subastas.mensajes;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.retro.subastas.LotesSubastas;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class RetirarSubasta extends MessageHandler
{
    @Override
    public void handle()
    {
        if(this.client == null || this.client.getHabbo() == null)
        {
            return;
        }

        int subastaId = this.packet.readInt().intValue();
        int usuarioId = this.client.getHabbo().getHabboInfo().getId();

        Connection conexion = null;
        List<Integer> objetoIds = null;

        try
        {
            LotesSubastas.asegurarEstructura();

            conexion = Emulator.getDatabase().getDataSource().getConnection();
            conexion.setAutoCommit(false);

            int objetoFallback;
            int posicion;
            int sesionId;

            try(PreparedStatement consulta = conexion.prepareStatement(
                    "SELECT objeto_id, posicion_cola, estado, sesion_id " +
                    "FROM subastas " +
                    "WHERE id = ? AND vendedor_id = ? FOR UPDATE"))
            {
                consulta.setInt(1, subastaId);
                consulta.setInt(2, usuarioId);

                try(ResultSet resultado = consulta.executeQuery())
                {
                    if(!resultado.next())
                    {
                        conexion.rollback();
                        enviarResultado(false, "No se encontro esa subasta.", subastaId);
                        return;
                    }

                    String estado = resultado.getString("estado");

                    if(!"en_cola".equals(estado))
                    {
                        conexion.rollback();
                        enviarResultado(
                                false,
                                "Solo puedes retirar una subasta que siga en cola.",
                                subastaId
                        );
                        return;
                    }

                    objetoFallback = resultado.getInt("objeto_id");
                    posicion = resultado.getInt("posicion_cola");
                    sesionId = resultado.getInt("sesion_id");
                }
            }

            objetoIds = LotesSubastas.obtenerObjetoIds(
                    conexion,
                    subastaId,
                    objetoFallback
            );

            LotesSubastas.transferirDesdeEscrow(
                    conexion,
                    objetoIds,
                    usuarioId
            );

            try(PreparedStatement cancelar = conexion.prepareStatement(
                    "UPDATE subastas SET estado = 'cancelada', posicion_cola = 0, " +
                    "fecha_finalizacion = NOW() " +
                    "WHERE id = ? AND vendedor_id = ? AND estado = 'en_cola'"))
            {
                cancelar.setInt(1, subastaId);
                cancelar.setInt(2, usuarioId);

                if(cancelar.executeUpdate() != 1)
                {
                    throw new IllegalStateException("No se pudo cancelar la subasta.");
                }
            }

            try(PreparedStatement compactar = conexion.prepareStatement(
                    "UPDATE subastas SET posicion_cola = posicion_cola - 1 " +
                    "WHERE sesion_id = ? AND estado = 'en_cola' AND posicion_cola > ?"))
            {
                compactar.setInt(1, sesionId);
                compactar.setInt(2, posicion);
                compactar.executeUpdate();
            }

            conexion.commit();

            LotesSubastas.refrescarInventarioOnline(usuarioId, objetoIds);

            enviarResultado(
                    true,
                    "Subasta #" + subastaId +
                            " retirada. Lote devuelto: " + objetoIds.size() + " unidad(es).",
                    subastaId
            );

            System.out.println("[Subastas] "
                    + this.client.getHabbo().getHabboInfo().getUsername()
                    + " retiro la subasta #" + subastaId
                    + " y recupero " + objetoIds.size() + " objeto(s)");
        }
        catch(Exception error)
        {
            try
            {
                if(conexion != null)
                {
                    conexion.rollback();
                }
            }
            catch(Exception ignorado)
            {
            }

            System.out.println("[Subastas] ERROR retirando lote de subasta: "
                    + error.getClass().getName() + ": " + error.getMessage());
            error.printStackTrace();

            enviarResultado(false, "No se pudo retirar la subasta.", subastaId);
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
    }

    private void enviarResultado(boolean exito, String mensaje, int subastaId)
    {
        ServerMessage respuesta = new ServerMessage(5009);
        respuesta.appendBoolean(exito);
        respuesta.appendString(mensaje);
        respuesta.appendInt(subastaId);

        this.client.sendResponse(respuesta);
    }
}