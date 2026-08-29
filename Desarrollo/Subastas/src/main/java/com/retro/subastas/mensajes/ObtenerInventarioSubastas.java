package com.retro.subastas.mensajes;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.MessageHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ObtenerInventarioSubastas extends MessageHandler
{
    private static class FurniSubasta
    {
        int instanciaId;
        int furniId;
        int spriteId;
        String nombre;
        String itemName;
        String tipo;
        String rareza;
        String limitedData;
    }

    @Override
    public void handle()
    {
        if(this.client == null || this.client.getHabbo() == null)
        {
            return;
        }

        int usuarioId = this.client.getHabbo().getHabboInfo().getId();
        List<FurniSubasta> furnis = new ArrayList<>();

        String sql =
                "SELECT i.id AS instancia_id, i.item_id AS furni_id, ib.sprite_id, " +
                "ib.public_name, ib.item_name, ib.type, ib.rare, i.limited_data " +
                "FROM items i " +
                "INNER JOIN items_base ib ON ib.id = i.item_id " +
                "WHERE i.user_id = ? AND i.room_id = 0 AND ib.allow_trade = 1 " +
                "ORDER BY i.id DESC LIMIT 100";

        try(Connection conexion = Emulator.getDatabase().getDataSource().getConnection();
            PreparedStatement consulta = conexion.prepareStatement(sql))
        {
            consulta.setInt(1, usuarioId);

            try(ResultSet resultado = consulta.executeQuery())
            {
                while(resultado.next())
                {
                    FurniSubasta furni = new FurniSubasta();

                    furni.instanciaId = resultado.getInt("instancia_id");
                    furni.furniId = resultado.getInt("furni_id");
                    furni.spriteId = resultado.getInt("sprite_id");
                    furni.nombre = resultado.getString("public_name");
                    furni.itemName = resultado.getString("item_name");
                    furni.tipo = resultado.getString("type");
                    furni.rareza = resultado.getString("rare");
                    furni.limitedData = resultado.getString("limited_data");

                    if(furni.nombre == null || furni.nombre.isBlank() || furni.nombre.equals("0"))
                    {
                        furni.nombre = furni.itemName;
                    }

                    furnis.add(furni);
                }
            }

            ServerMessage respuesta = new ServerMessage(5003);
            respuesta.appendInt(furnis.size());

            for(FurniSubasta furni : furnis)
            {
                respuesta.appendInt(furni.instanciaId);
                respuesta.appendInt(furni.furniId);
                respuesta.appendInt(furni.spriteId);
                respuesta.appendString(furni.nombre);
                respuesta.appendString(furni.itemName);
                respuesta.appendString(furni.tipo == null ? "" : furni.tipo);
                respuesta.appendString(furni.rareza == null ? "0" : furni.rareza);
                respuesta.appendString(furni.limitedData == null ? "0:0" : furni.limitedData);
            }

            this.client.sendResponse(respuesta);

}
        catch(Exception error)
        {
            System.out.println("[Subastas] ERROR obteniendo inventario: "
                    + error.getClass().getName() + ": " + error.getMessage());
            error.printStackTrace();
        }
    }
}