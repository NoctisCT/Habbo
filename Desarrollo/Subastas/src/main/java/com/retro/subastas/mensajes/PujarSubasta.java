package com.retro.subastas.mensajes;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.users.UserCreditsComposer;
import com.retro.subastas.ConfiguracionSubastas;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class PujarSubasta extends MessageHandler
{
    @Override
    public void handle()
    {
        if(this.client == null || this.client.getHabbo() == null)
        {
            return;
        }

        int subastaId = this.packet.readInt().intValue();
        int cantidad = this.packet.readInt().intValue();

        if(subastaId <= 0 || cantidad <= 0 || cantidad > 1000000000)
        {
            enviarResultado(false, "La puja no es v\u00e1lida.", subastaId, 0);
            return;
        }

        int postorId = this.client.getHabbo().getHabboInfo().getId();

        Connection conexion = null;

        int nuevoSaldoPostor = -1;
        int postorAnteriorId = 0;
        int nuevoSaldoAnterior = -1;

        String nombreObjeto = "";
        String vendedorNombre = "";
        int precioInicial = 0;
        int pujaAnterior = 0;
        int segundosRestantes = 0;

        try
        {
            conexion = Emulator.getDatabase().getDataSource().getConnection();
            conexion.setAutoCommit(false);

            int vendedorId;
            Integer mejorPostorId = null;

            try(PreparedStatement consulta = conexion.prepareStatement(
                    "SELECT s.vendedor_id, s.nombre_objeto, s.precio_inicial, " +
                    "s.puja_actual, s.mejor_postor_id, u.username AS vendedor, " +
                    "GREATEST(0, TIMESTAMPDIFF(SECOND, NOW(), s.fecha_fin)) AS segundos " +
                    "FROM subastas s " +
                    "INNER JOIN users u ON u.id = s.vendedor_id " +
                    "WHERE s.id = ? AND s.estado = 'activa' FOR UPDATE"))
            {
                consulta.setInt(1, subastaId);

                try(ResultSet resultado = consulta.executeQuery())
                {
                    if(!resultado.next())
                    {
                        conexion.rollback();
                        enviarResultado(false, "Esa subasta ya no est\u00e1 activa.", subastaId, 0);
                        return;
                    }

                    vendedorId = resultado.getInt("vendedor_id");
                    nombreObjeto = resultado.getString("nombre_objeto");
                    vendedorNombre = resultado.getString("vendedor");
                    precioInicial = resultado.getInt("precio_inicial");
                    pujaAnterior = resultado.getInt("puja_actual");
                    segundosRestantes = resultado.getInt("segundos");

                    int idAnterior = resultado.getInt("mejor_postor_id");

                    if(!resultado.wasNull())
                    {
                        mejorPostorId = idAnterior;
                    }
                }
            }

            if(segundosRestantes <= 0)
            {
                conexion.rollback();
                enviarResultado(false, "La subasta ya ha terminado.", subastaId, pujaAnterior);
                return;
            }

            if(vendedorId == postorId)
            {
                conexion.rollback();
                enviarResultado(false, "No puedes pujar por tu propio furni.", subastaId, pujaAnterior);
                return;
            }

            int minimo = (pujaAnterior > 0) ? (pujaAnterior + 1) : precioInicial;
            int antiSnipingUmbral = ConfiguracionSubastas.getAntiSnipingUmbral();
            int antiSnipingExtension = ConfiguracionSubastas.getAntiSnipingExtension();
            boolean antiSniping = antiSnipingUmbral > 0 && antiSnipingExtension > 0 && segundosRestantes <= antiSnipingUmbral;

            if(cantidad < minimo)
            {
                conexion.rollback();
                enviarResultado(
                        false,
                        "La puja m\u00ednima ahora es " + minimo + " cr\u00e9ditos.",
                        subastaId,
                        pujaAnterior
                );
                return;
            }

            postorAnteriorId = (mejorPostorId == null) ? 0 : mejorPostorId;

            Map<Integer, Integer> saldos = bloquearUsuarios(
                    conexion,
                    postorId,
                    postorAnteriorId
            );

            if(!saldos.containsKey(postorId))
            {
                throw new IllegalStateException("No se encontr\u00f3 al postor.");
            }

            int saldoPostor = saldos.get(postorId);

            if(postorAnteriorId == postorId)
            {
                int diferencia = cantidad - pujaAnterior;

                if(diferencia <= 0)
                {
                    conexion.rollback();
                    enviarResultado(
                            false,
                            "Tu nueva puja debe superar la anterior.",
                            subastaId,
                            pujaAnterior
                    );
                    return;
                }

                if(saldoPostor < diferencia)
                {
                    conexion.rollback();
                    enviarResultado(
                            false,
                            "No tienes cr\u00e9ditos suficientes para aumentar la puja.",
                            subastaId,
                            pujaAnterior
                    );
                    return;
                }

                nuevoSaldoPostor = saldoPostor - diferencia;

                actualizarCreditos(conexion, postorId, nuevoSaldoPostor);
            }
            else
            {
                if(saldoPostor < cantidad)
                {
                    conexion.rollback();
                    enviarResultado(
                            false,
                            "No tienes cr\u00e9ditos suficientes para esa puja.",
                            subastaId,
                            pujaAnterior
                    );
                    return;
                }

                nuevoSaldoPostor = saldoPostor - cantidad;
                actualizarCreditos(conexion, postorId, nuevoSaldoPostor);

                if(postorAnteriorId > 0)
                {
                    Integer saldoAnterior = saldos.get(postorAnteriorId);

                    if(saldoAnterior == null)
                    {
                        throw new IllegalStateException("No se encontr\u00f3 al postor anterior.");
                    }

                    nuevoSaldoAnterior = saldoAnterior + pujaAnterior;
                    actualizarCreditos(conexion, postorAnteriorId, nuevoSaldoAnterior);
                }
            }

            try(PreparedStatement actualizar = conexion.prepareStatement(
                    antiSniping
                            ? "UPDATE subastas SET puja_actual = ?, mejor_postor_id = ?, " +
                              "fecha_fin = DATE_ADD(fecha_fin, INTERVAL " + antiSnipingExtension + " SECOND) " +
                              "WHERE id = ? AND estado = 'activa'"
                            : "UPDATE subastas SET puja_actual = ?, mejor_postor_id = ? " +
                              "WHERE id = ? AND estado = 'activa'"))
            {
                actualizar.setInt(1, cantidad);
                actualizar.setInt(2, postorId);
                actualizar.setInt(3, subastaId);

                if(actualizar.executeUpdate() != 1)
                {
                    throw new IllegalStateException("No se pudo actualizar la subasta.");
                }
            }

            if(antiSniping)
            {
                segundosRestantes += antiSnipingExtension;
            }

            try(PreparedStatement insertar = conexion.prepareStatement(
                    "INSERT INTO pujas_subastas (subasta_id, postor_id, cantidad) " +
                    "VALUES (?, ?, ?)"))
            {
                insertar.setInt(1, subastaId);
                insertar.setInt(2, postorId);
                insertar.setInt(3, cantidad);
                insertar.executeUpdate();
            }

            conexion.commit();

            sincronizarCreditosOnline(postorId, nuevoSaldoPostor);

            if(postorAnteriorId > 0 &&
                    postorAnteriorId != postorId &&
                    nuevoSaldoAnterior >= 0)
            {
                sincronizarCreditosOnline(postorAnteriorId, nuevoSaldoAnterior);
            }

            enviarResultado(
                    true,
                    antiSniping
                            ? "Puja de " + cantidad + " cr\u00e9ditos aceptada. +" + antiSnipingExtension + " s anti-sniping."
                            : "Puja de " + cantidad + " cr\u00e9ditos aceptada.",
                    subastaId,
                    cantidad
            );

            difundirEstado(
                    subastaId,
                    nombreObjeto,
                    vendedorNombre,
                    precioInicial,
                    cantidad,
                    segundosRestantes
            );

            System.out.println("[Subastas] "
                    + this.client.getHabbo().getHabboInfo().getUsername()
                    + " pujo " + cantidad
                    + " creditos en la subasta #" + subastaId);
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

            System.out.println("[Subastas] ERROR procesando puja: "
                    + error.getClass().getName() + ": " + error.getMessage());
            error.printStackTrace();

            enviarResultado(false, "No se pudo procesar la puja.", subastaId, pujaAnterior);
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

    private Map<Integer, Integer> bloquearUsuarios(
            Connection conexion,
            int postorId,
            int postorAnteriorId) throws Exception
    {
        Map<Integer, Integer> saldos = new HashMap<>();

        if(postorAnteriorId > 0 && postorAnteriorId != postorId)
        {
            int primero = Math.min(postorId, postorAnteriorId);
            int segundo = Math.max(postorId, postorAnteriorId);

            try(PreparedStatement consulta = conexion.prepareStatement(
                    "SELECT id, credits FROM users " +
                    "WHERE id IN (?, ?) ORDER BY id FOR UPDATE"))
            {
                consulta.setInt(1, primero);
                consulta.setInt(2, segundo);

                try(ResultSet resultado = consulta.executeQuery())
                {
                    while(resultado.next())
                    {
                        saldos.put(
                                resultado.getInt("id"),
                                resultado.getInt("credits")
                        );
                    }
                }
            }
        }
        else
        {
            try(PreparedStatement consulta = conexion.prepareStatement(
                    "SELECT id, credits FROM users WHERE id = ? FOR UPDATE"))
            {
                consulta.setInt(1, postorId);

                try(ResultSet resultado = consulta.executeQuery())
                {
                    if(resultado.next())
                    {
                        saldos.put(
                                resultado.getInt("id"),
                                resultado.getInt("credits")
                        );
                    }
                }
            }
        }

        return saldos;
    }

    private void actualizarCreditos(
            Connection conexion,
            int usuarioId,
            int nuevoSaldo) throws Exception
    {
        try(PreparedStatement actualizar = conexion.prepareStatement(
                "UPDATE users SET credits = ? WHERE id = ?"))
        {
            actualizar.setInt(1, nuevoSaldo);
            actualizar.setInt(2, usuarioId);

            if(actualizar.executeUpdate() != 1)
            {
                throw new IllegalStateException(
                        "No se pudo actualizar el saldo del usuario " + usuarioId
                );
            }
        }
    }

    private void sincronizarCreditosOnline(int usuarioId, int nuevoSaldo)
    {
        try
        {
            Habbo habbo = Emulator.getGameEnvironment()
                    .getHabboManager()
                    .getHabbo(usuarioId);

            if(habbo == null) return;

            habbo.getHabboInfo().setCredits(nuevoSaldo);

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

    private void difundirEstado(
            int subastaId,
            String objeto,
            String vendedor,
            int precioInicial,
            int pujaActual,
            int segundosRestantes)
    {
        int spriteId = 0;
        String tipo = "s";

        try(Connection conexion = Emulator.getDatabase().getDataSource().getConnection();
            PreparedStatement consulta = conexion.prepareStatement(
                    "SELECT ib.sprite_id, ib.type " +
                    "FROM subastas s " +
                    "INNER JOIN items_base ib ON ib.id = s.furni_id " +
                    "WHERE s.id = ? LIMIT 1"))
        {
            consulta.setInt(1, subastaId);

            try(ResultSet resultado = consulta.executeQuery())
            {
                if(resultado.next())
                {
                    spriteId = resultado.getInt("sprite_id");
                    tipo = resultado.getString("type");
                }
            }
        }
        catch(Exception error)
        {
            System.out.println("[Subastas] Aviso: no se pudo obtener la imagen de la subasta #"
                    + subastaId + ": " + error.getMessage());
        }

        for(Habbo habbo : Emulator.getGameEnvironment()
                .getHabboManager()
                .getOnlineHabbos()
                .values())
        {
            if(habbo == null || habbo.getClient() == null) continue;

            ServerMessage respuesta = new ServerMessage(5001);
            respuesta.appendBoolean(true);
            respuesta.appendString("Subasta activa.");
            respuesta.appendInt(subastaId);
            respuesta.appendString(objeto);
            respuesta.appendInt(spriteId);
            respuesta.appendString(tipo == null ? "s" : tipo);
            respuesta.appendString(vendedor);
            respuesta.appendInt(precioInicial);
            respuesta.appendInt(pujaActual);
            respuesta.appendInt(segundosRestantes);

            habbo.getClient().sendResponse(respuesta);
        }
    }

    private void enviarResultado(
            boolean exito,
            String mensaje,
            int subastaId,
            int pujaActual)
    {
        ServerMessage respuesta = new ServerMessage(5011);
        respuesta.appendBoolean(exito);
        respuesta.appendString(mensaje);
        respuesta.appendInt(subastaId);
        respuesta.appendInt(pujaActual);

        this.client.sendResponse(respuesta);
    }
}