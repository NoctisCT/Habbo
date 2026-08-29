package com.retro.subastas;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.outgoing.inventory.AddHabboItemComposer;
import com.eu.habbo.messages.outgoing.inventory.InventoryRefreshComposer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public final class LotesSubastas
{
    private static volatile boolean estructuraPreparada = false;

    private LotesSubastas()
    {
    }

    public static synchronized void asegurarEstructura() throws Exception
    {
        if(estructuraPreparada)
        {
            return;
        }

        try(Connection conexion = Emulator.getDatabase().getDataSource().getConnection();
            Statement sentencia = conexion.createStatement())
        {
            sentencia.executeUpdate(
                    "ALTER TABLE subastas " +
                    "ADD COLUMN IF NOT EXISTS cantidad INT NOT NULL DEFAULT 1 AFTER nombre_objeto"
            );

            sentencia.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS subastas_items (" +
                    "subasta_id INT NOT NULL, " +
                    "objeto_id INT NOT NULL, " +
                    "PRIMARY KEY (subasta_id, objeto_id), " +
                    "KEY idx_subastas_items_objeto (objeto_id)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci"
            );

            sentencia.executeUpdate(
                    "INSERT IGNORE INTO subastas_items (subasta_id, objeto_id) " +
                    "SELECT id, objeto_id FROM subastas"
            );

            sentencia.executeUpdate(
                    "UPDATE subastas SET cantidad = 1 WHERE cantidad IS NULL OR cantidad < 1"
            );
        }

        estructuraPreparada = true;
    }

    public static List<Integer> obtenerObjetoIds(
            Connection conexion,
            int subastaId,
            int objetoFallback
    ) throws Exception
    {
        List<Integer> ids = new ArrayList<>();

        try(PreparedStatement consulta = conexion.prepareStatement(
                "SELECT objeto_id FROM subastas_items " +
                "WHERE subasta_id = ? ORDER BY objeto_id ASC"))
        {
            consulta.setInt(1, subastaId);

            try(ResultSet resultado = consulta.executeQuery())
            {
                while(resultado.next())
                {
                    ids.add(resultado.getInt("objeto_id"));
                }
            }
        }

        if(ids.isEmpty() && objetoFallback > 0)
        {
            ids.add(objetoFallback);
        }

        return ids;
    }

    public static void transferirDesdeEscrow(
            Connection conexion,
            List<Integer> objetoIds,
            int usuarioDestino
    ) throws Exception
    {
        if(objetoIds == null || objetoIds.isEmpty())
        {
            throw new IllegalStateException("La subasta no tiene objetos asociados.");
        }

        try(PreparedStatement actualizar = conexion.prepareStatement(
                "UPDATE items SET user_id = ? " +
                "WHERE id = ? AND user_id = -1 AND room_id = 0"))
        {
            for(int objetoId : objetoIds)
            {
                actualizar.setInt(1, usuarioDestino);
                actualizar.setInt(2, objetoId);

                if(actualizar.executeUpdate() != 1)
                {
                    throw new IllegalStateException(
                            "No se pudo transferir el objeto " + objetoId
                    );
                }
            }
        }
    }

    public static boolean estaEnSubastaActiva(
            Connection conexion,
            int objetoId
    ) throws Exception
    {
        try(PreparedStatement consulta = conexion.prepareStatement(
                "SELECT s.id FROM subastas s " +
                "LEFT JOIN subastas_items si ON si.subasta_id = s.id " +
                "WHERE s.estado IN ('en_cola', 'activa') " +
                "AND (s.objeto_id = ? OR si.objeto_id = ?) " +
                "LIMIT 1"))
        {
            consulta.setInt(1, objetoId);
            consulta.setInt(2, objetoId);

            try(ResultSet resultado = consulta.executeQuery())
            {
                return resultado.next();
            }
        }
    }

    public static void refrescarInventarioOnline(
            int usuarioId,
            List<Integer> objetoIds
    )
    {
        if(objetoIds == null || objetoIds.isEmpty())
        {
            return;
        }

        try
        {
            Habbo habbo = Emulator.getGameEnvironment()
                    .getHabboManager()
                    .getHabbo(usuarioId);

            if(habbo == null || habbo.getClient() == null)
            {
                return;
            }

            boolean actualizado = false;

            for(int objetoId : objetoIds)
            {
                if(habbo.getInventory()
                        .getItemsComponent()
                        .getHabboItem(objetoId) != null)
                {
                    continue;
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
                    continue;
                }

                habbo.getInventory()
                        .getItemsComponent()
                        .addItem(item);

                habbo.getClient().sendResponse(new AddHabboItemComposer(item));
                actualizado = true;
            }

            if(actualizado)
            {
                habbo.getClient().sendResponse(new InventoryRefreshComposer());
            }
        }
        catch(Exception error)
        {
            System.out.println("[Subastas] Aviso: no se pudo refrescar un lote en inventario: "
                    + error.getMessage());
            error.printStackTrace();
        }
    }
}