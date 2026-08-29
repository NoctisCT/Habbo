package com.retro.subastas.mensajes;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.MessageHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ObtenerHistorialSubastas extends MessageHandler
{
    private static final int POR_PAGINA = 20;

    private static class Fila
    {
        int id;
        int furniId;
        int spriteId;
        String tipo;
        String nombre;
        int cantidad;
        String vendedor;
        String ganador;
        int precioInicial;
        int precioFinal;
        String estado;
        String fecha;
    }

    @Override
    public void handle()
    {
        if(this.client == null || this.client.getHabbo() == null)
        {
            return;
        }

        int pagina = Math.max(1, this.packet.readInt().intValue());
        String busqueda = valor(this.packet.readString(), "").trim();
        String filtro = valor(this.packet.readString(), "todos")
                .trim()
                .toLowerCase(Locale.ROOT);

        if(!filtro.equals("vendidas") && !filtro.equals("sin_pujas"))
        {
            filtro = "todos";
        }

        StringBuilder where = new StringBuilder(
                " WHERE s.estado IN ('vendida','sin_vender') "
        );

        List<String> parametros = new ArrayList<>();

        if(filtro.equals("vendidas"))
        {
            where.append(" AND s.estado = 'vendida' ");
        }
        else if(filtro.equals("sin_pujas"))
        {
            where.append(" AND s.estado = 'sin_vender' ");
        }

        if(!busqueda.isBlank())
        {
            where.append(
                    " AND (" +
                    "LOWER(COALESCE(s.nombre_objeto,'')) LIKE ? " +
                    "OR LOWER(COALESCE(ib.public_name,'')) LIKE ? " +
                    "OR LOWER(COALESCE(ib.item_name,'')) LIKE ?" +
                    ") "
            );

            String patron = "%" + busqueda.toLowerCase(Locale.ROOT) + "%";
            parametros.add(patron);
            parametros.add(patron);
            parametros.add(patron);
        }

        try(Connection conexion = Emulator.getDatabase().getDataSource().getConnection())
        {
            int total;

            String sqlTotal =
                    "SELECT COUNT(*) AS total " +
                    "FROM subastas s " +
                    "INNER JOIN items_base ib ON ib.id = s.furni_id " +
                    where;

            try(PreparedStatement contar = conexion.prepareStatement(sqlTotal))
            {
                aplicarParametros(contar, parametros);

                try(ResultSet resultado = contar.executeQuery())
                {
                    total = resultado.next() ? resultado.getInt("total") : 0;
                }
            }

            int paginas = Math.max(1, (int)Math.ceil(total / (double)POR_PAGINA));
            pagina = Math.min(pagina, paginas);
            int offset = (pagina - 1) * POR_PAGINA;

            String sql =
                    "SELECT " +
                    "s.id, s.furni_id, ib.sprite_id, ib.type, " +
                    "COALESCE(NULLIF(s.nombre_objeto,''), NULLIF(ib.public_name,''), ib.item_name, 'Furni') AS nombre, " +
                    "GREATEST(1, COALESCE(s.cantidad,1)) AS cantidad, " +
                    "COALESCE(v.username,'-') AS vendedor, " +
                    "COALESCE(g.username,'') AS ganador, " +
                    "s.precio_inicial, " +
                    "CASE WHEN s.estado='vendida' THEN s.puja_actual ELSE 0 END AS precio_final, " +
                    "s.estado, " +
                    "DATE_FORMAT(s.fecha_finalizacion, '%d/%m/%Y %H:%i') AS fecha " +
                    "FROM subastas s " +
                    "INNER JOIN items_base ib ON ib.id = s.furni_id " +
                    "LEFT JOIN users v ON v.id = s.vendedor_id " +
                    "LEFT JOIN users g ON g.id = s.mejor_postor_id " +
                    where +
                    " ORDER BY s.fecha_finalizacion DESC, s.id DESC " +
                    "LIMIT ? OFFSET ?";

            List<Fila> filas = new ArrayList<>();

            try(PreparedStatement consulta = conexion.prepareStatement(sql))
            {
                int indice = aplicarParametros(consulta, parametros);
                consulta.setInt(indice++, POR_PAGINA);
                consulta.setInt(indice, offset);

                try(ResultSet resultado = consulta.executeQuery())
                {
                    while(resultado.next())
                    {
                        Fila fila = new Fila();

                        fila.id = resultado.getInt("id");
                        fila.furniId = resultado.getInt("furni_id");
                        fila.spriteId = resultado.getInt("sprite_id");
                        fila.tipo = valor(resultado.getString("type"), "s");
                        fila.nombre = valor(resultado.getString("nombre"), "Furni");
                        fila.cantidad = Math.max(1, resultado.getInt("cantidad"));
                        fila.vendedor = valor(resultado.getString("vendedor"), "-");
                        fila.ganador = valor(resultado.getString("ganador"), "");
                        fila.precioInicial = resultado.getInt("precio_inicial");
                        fila.precioFinal = resultado.getInt("precio_final");
                        fila.estado = valor(resultado.getString("estado"), "");
                        fila.fecha = valor(resultado.getString("fecha"), "");

                        filas.add(fila);
                    }
                }
            }

            ServerMessage respuesta = new ServerMessage(5016);

            respuesta.appendInt(pagina);
            respuesta.appendInt(paginas);
            respuesta.appendInt(total);
            respuesta.appendInt(filas.size());

            for(Fila fila : filas)
            {
                respuesta.appendInt(fila.id);
                respuesta.appendInt(fila.furniId);
                respuesta.appendInt(fila.spriteId);
                respuesta.appendString(fila.tipo);
                respuesta.appendString(fila.nombre);
                respuesta.appendInt(fila.cantidad);
                respuesta.appendString(fila.vendedor);
                respuesta.appendString(fila.ganador);
                respuesta.appendInt(fila.precioInicial);
                respuesta.appendInt(fila.precioFinal);
                respuesta.appendString(fila.estado);
                respuesta.appendString(fila.fecha);
            }

            this.client.sendResponse(respuesta);
        }
        catch(Exception error)
        {
            System.out.println("[Subastas] ERROR obteniendo historial: "
                    + error.getClass().getName() + ": " + error.getMessage());
            error.printStackTrace();

            ServerMessage respuesta = new ServerMessage(5016);
            respuesta.appendInt(1);
            respuesta.appendInt(1);
            respuesta.appendInt(0);
            respuesta.appendInt(0);
            this.client.sendResponse(respuesta);
        }
    }

    private static int aplicarParametros(
            PreparedStatement consulta,
            List<String> parametros
    ) throws Exception
    {
        int indice = 1;

        for(String parametro : parametros)
        {
            consulta.setString(indice++, parametro);
        }

        return indice;
    }

    private static String valor(String valor, String porDefecto)
    {
        return valor == null || valor.isBlank() ? porDefecto : valor;
    }
}