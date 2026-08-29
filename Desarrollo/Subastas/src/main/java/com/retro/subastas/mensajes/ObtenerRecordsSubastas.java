package com.retro.subastas.mensajes;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.MessageHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ObtenerRecordsSubastas extends MessageHandler
{
    private static final int MAX_RANKING = 5;

    private static final class RecordUsuario
    {
        int id;
        String username;
        String look;
        String gender;
        long valor;
        long secundario;
    }

    private static final class RecordFurni
    {
        int furniId;
        String nombre;
        int spriteId;
        String tipo;
        long valor;
    }

    @Override
    public void handle()
    {
        if(this.client == null || this.client.getHabbo() == null)
        {
            return;
        }

        try(Connection conexion = Emulator.getDatabase().getDataSource().getConnection())
        {
            List<RecordUsuario> dineroGanado = cargarUsuarios(
                    conexion,
                    "SELECT u.id, u.username, u.look, u.gender, " +
                    "COALESCE(SUM(COALESCE(s.neto_vendedor, s.puja_actual)),0) AS valor, " +
                    "0 AS secundario " +
                    "FROM subastas s " +
                    "INNER JOIN users u ON u.id=s.vendedor_id " +
                    "WHERE s.estado='vendida' " +
                    "GROUP BY u.id,u.username,u.look,u.gender " +
                    "ORDER BY valor DESC,u.id ASC LIMIT " + MAX_RANKING
            );

            List<RecordUsuario> furnisVendidos = cargarUsuarios(
                    conexion,
                    "SELECT u.id, u.username, u.look, u.gender, " +
                    "COALESCE(SUM(GREATEST(1,COALESCE(s.cantidad,1))),0) AS valor, " +
                    "0 AS secundario " +
                    "FROM subastas s " +
                    "INNER JOIN users u ON u.id=s.vendedor_id " +
                    "WHERE s.estado='vendida' " +
                    "GROUP BY u.id,u.username,u.look,u.gender " +
                    "ORDER BY valor DESC,u.id ASC LIMIT " + MAX_RANKING
            );

            List<RecordUsuario> dineroGastado = cargarUsuarios(
                    conexion,
                    "SELECT u.id, u.username, u.look, u.gender, " +
                    "COALESCE(SUM(s.puja_actual),0) AS valor, " +
                    "0 AS secundario " +
                    "FROM subastas s " +
                    "INNER JOIN users u ON u.id=s.mejor_postor_id " +
                    "WHERE s.estado='vendida' AND s.mejor_postor_id IS NOT NULL " +
                    "GROUP BY u.id,u.username,u.look,u.gender " +
                    "ORDER BY valor DESC,u.id ASC LIMIT " + MAX_RANKING
            );

            List<RecordUsuario> compras = cargarUsuarios(
                    conexion,
                    "SELECT u.id, u.username, u.look, u.gender, " +
                    "COUNT(*) AS valor, " +
                    "COALESCE(SUM(GREATEST(1,COALESCE(s.cantidad,1))),0) AS secundario " +
                    "FROM subastas s " +
                    "INNER JOIN users u ON u.id=s.mejor_postor_id " +
                    "WHERE s.estado='vendida' AND s.mejor_postor_id IS NOT NULL " +
                    "GROUP BY u.id,u.username,u.look,u.gender " +
                    "ORDER BY valor DESC,secundario DESC,u.id ASC LIMIT " + MAX_RANKING
            );

            List<RecordFurni> masCaros = cargarFurnis(
                    conexion,
                    "SELECT s.furni_id, ib.public_name, ib.item_name, ib.sprite_id, ib.type, " +
                    "COALESCE(MAX(ROUND(s.puja_actual / GREATEST(1,COALESCE(s.cantidad,1)),0)),0) AS valor " +
                    "FROM subastas s " +
                    "INNER JOIN items_base ib ON ib.id=s.furni_id " +
                    "WHERE s.estado='vendida' " +
                    "GROUP BY s.furni_id,ib.public_name,ib.item_name,ib.sprite_id,ib.type " +
                    "ORDER BY valor DESC,s.furni_id ASC LIMIT " + MAX_RANKING
            );

            List<RecordFurni> masVendidos = cargarFurnis(
                    conexion,
                    "SELECT s.furni_id, ib.public_name, ib.item_name, ib.sprite_id, ib.type, " +
                    "COALESCE(SUM(GREATEST(1,COALESCE(s.cantidad,1))),0) AS valor " +
                    "FROM subastas s " +
                    "INNER JOIN items_base ib ON ib.id=s.furni_id " +
                    "WHERE s.estado='vendida' " +
                    "GROUP BY s.furni_id,ib.public_name,ib.item_name,ib.sprite_id,ib.type " +
                    "ORDER BY valor DESC,s.furni_id ASC LIMIT " + MAX_RANKING
            );

            List<RecordFurni> mayorVolumen = cargarFurnis(
                    conexion,
                    "SELECT s.furni_id, ib.public_name, ib.item_name, ib.sprite_id, ib.type, " +
                    "COALESCE(SUM(s.puja_actual),0) AS valor " +
                    "FROM subastas s " +
                    "INNER JOIN items_base ib ON ib.id=s.furni_id " +
                    "WHERE s.estado='vendida' " +
                    "GROUP BY s.furni_id,ib.public_name,ib.item_name,ib.sprite_id,ib.type " +
                    "ORDER BY valor DESC,s.furni_id ASC LIMIT " + MAX_RANKING
            );

            List<RecordFurni> masPujados = cargarFurnis(
                    conexion,
                    "SELECT s.furni_id, ib.public_name, ib.item_name, ib.sprite_id, ib.type, " +
                    "COUNT(p.id) AS valor " +
                    "FROM subastas s " +
                    "INNER JOIN items_base ib ON ib.id=s.furni_id " +
                    "LEFT JOIN pujas_subastas p ON p.subasta_id=s.id " +
                    "WHERE s.estado='vendida' " +
                    "GROUP BY s.furni_id,ib.public_name,ib.item_name,ib.sprite_id,ib.type " +
                    "ORDER BY valor DESC,s.furni_id ASC LIMIT " + MAX_RANKING
            );

            ServerMessage respuesta = new ServerMessage(5024);

            escribirUsuarios(respuesta, dineroGanado);
            escribirUsuarios(respuesta, furnisVendidos);
            escribirUsuarios(respuesta, dineroGastado);
            escribirUsuarios(respuesta, compras);

            escribirFurnis(respuesta, masCaros);
            escribirFurnis(respuesta, masVendidos);
            escribirFurnis(respuesta, mayorVolumen);
            escribirFurnis(respuesta, masPujados);

            this.client.sendResponse(respuesta);
        }
        catch(Exception error)
        {
            System.out.println("[Subastas] ERROR obteniendo records: "
                    + error.getClass().getName() + ": " + error.getMessage());
            error.printStackTrace();

            ServerMessage respuesta = new ServerMessage(5024);

            // Ocho rankings vacíos, en el mismo orden esperado por Nitro.
            for(int i = 0; i < 8; i++)
            {
                respuesta.appendInt(0);
            }

            this.client.sendResponse(respuesta);
        }
    }

    private static List<RecordUsuario> cargarUsuarios(
            Connection conexion,
            String sql
    ) throws Exception
    {
        List<RecordUsuario> filas = new ArrayList<>();

        try(PreparedStatement consulta = conexion.prepareStatement(sql);
            ResultSet resultado = consulta.executeQuery())
        {
            while(resultado.next())
            {
                RecordUsuario fila = new RecordUsuario();

                fila.id = resultado.getInt("id");
                fila.username = valor(resultado.getString("username"), "Usuario");
                fila.look = valor(resultado.getString("look"), "");
                fila.gender = valor(resultado.getString("gender"), "M");
                fila.valor = resultado.getLong("valor");
                fila.secundario = resultado.getLong("secundario");

                filas.add(fila);
            }
        }

        return filas;
    }

    private static List<RecordFurni> cargarFurnis(
            Connection conexion,
            String sql
    ) throws Exception
    {
        List<RecordFurni> filas = new ArrayList<>();

        try(PreparedStatement consulta = conexion.prepareStatement(sql);
            ResultSet resultado = consulta.executeQuery())
        {
            while(resultado.next())
            {
                RecordFurni fila = new RecordFurni();

                fila.furniId = resultado.getInt("furni_id");
                fila.nombre = nombreVisible(
                        resultado.getString("public_name"),
                        resultado.getString("item_name")
                );
                fila.spriteId = resultado.getInt("sprite_id");
                fila.tipo = valor(resultado.getString("type"), "s");
                fila.valor = resultado.getLong("valor");

                filas.add(fila);
            }
        }

        return filas;
    }

    private static void escribirUsuarios(
            ServerMessage respuesta,
            List<RecordUsuario> filas
    )
    {
        respuesta.appendInt(filas.size());

        for(RecordUsuario fila : filas)
        {
            respuesta.appendInt(fila.id);
            respuesta.appendString(fila.username);
            respuesta.appendString(fila.look);
            respuesta.appendString(fila.gender);
            respuesta.appendString(Long.toString(fila.valor));
            respuesta.appendString(Long.toString(fila.secundario));
        }
    }

    private static void escribirFurnis(
            ServerMessage respuesta,
            List<RecordFurni> filas
    )
    {
        respuesta.appendInt(filas.size());

        for(RecordFurni fila : filas)
        {
            respuesta.appendInt(fila.furniId);
            respuesta.appendString(fila.nombre);
            respuesta.appendInt(fila.spriteId);
            respuesta.appendString(fila.tipo);
            respuesta.appendString(Long.toString(fila.valor));
        }
    }

    private static String nombreVisible(String publicName, String itemName)
    {
        if(publicName != null &&
                !publicName.isBlank() &&
                !"0".equals(publicName) &&
                !publicName.toLowerCase().endsWith("_name"))
        {
            return publicName;
        }

        if(itemName == null || itemName.isBlank())
        {
            return "Furni";
        }

        return itemName.replace('_', ' ');
    }

    private static String valor(String valor, String porDefecto)
    {
        return valor == null || valor.isBlank() ? porDefecto : valor;
    }
}