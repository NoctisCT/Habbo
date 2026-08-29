package com.retro.subastas.mensajes;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.users.inventory.ItemsComponent;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.inventory.InventoryRefreshComposer;
import com.eu.habbo.messages.outgoing.inventory.RemoveHabboItemComposer;
import com.retro.subastas.ConfiguracionSubastas;
import com.retro.subastas.GestorSesionesSubastas;
import com.retro.subastas.LotesSubastas;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class PonerEnSubasta extends MessageHandler
{
    @Override
    public void handle()
    {
        if(this.client == null || this.client.getHabbo() == null)
        {
            return;
        }

        int cantidad = this.packet.readInt().intValue();

        if(cantidad < 1)
        {
            enviarResultado(false, "La cantidad no es valida.", 0);
            return;
        }

        List<Integer> instanciaIds = new ArrayList<>();
        Set<Integer> unicos = new LinkedHashSet<>();

        for(int i = 0; i < cantidad; i++)
        {
            int instanciaId = this.packet.readInt().intValue();

            if(instanciaId <= 0 || !unicos.add(instanciaId))
            {
                enviarResultado(false, "El lote contiene objetos duplicados o invalidos.", 0);
                return;
            }

            instanciaIds.add(instanciaId);
        }

        int precioInicial = this.packet.readInt().intValue();

        if(precioInicial < 1 || precioInicial > 1000000000)
        {
            enviarResultado(false, "El precio inicial no es valido.", 0);
            return;
        }

        int usuarioId = this.client.getHabbo().getHabboInfo().getId();
        int limite = tieneClubActivo()
                ? ConfiguracionSubastas.getLimiteVip()
                : ConfiguracionSubastas.getLimiteNormal();

        ItemsComponent itemsComponent = this.client.getHabbo().getInventory().getItemsComponent();
        List<HabboItem> items = new ArrayList<>();

        for(int instanciaId : instanciaIds)
        {
            HabboItem item = itemsComponent.getHabboItem(instanciaId);

            if(item == null)
            {
                enviarResultado(false, "Uno de los objetos ya no esta en tu inventario.", 0);
                return;
            }

            items.add(item);
        }

        Connection conexion = null;
        List<HabboItem> retiradosDeMemoria = new ArrayList<>();

        try
        {
            LotesSubastas.asegurarEstructura();
            GestorSesionesSubastas.asegurarSesionProgramada();

            conexion = Emulator.getDatabase().getDataSource().getConnection();
            conexion.setAutoCommit(false);

            int simultaneas;

            try(PreparedStatement contar = conexion.prepareStatement(
                    "SELECT COUNT(*) AS total FROM subastas " +
                    "WHERE vendedor_id = ? AND estado IN ('en_cola', 'activa') FOR UPDATE"))
            {
                contar.setInt(1, usuarioId);

                try(ResultSet resultado = contar.executeQuery())
                {
                    resultado.next();
                    simultaneas = resultado.getInt("total");
                }
            }

            if(simultaneas >= limite)
            {
                conexion.rollback();
                enviarResultado(
                        false,
                        "Has alcanzado el limite de " + limite + " subastas simultaneas.",
                        0
                );
                return;
            }

            GestorSesionesSubastas.SesionDestino sesion =
                    GestorSesionesSubastas.obtenerSesionDestino(conexion);

            if(sesion == null)
            {
                conexion.rollback();
                enviarResultado(false, "No hay una proxima sesion disponible.", 0);
                return;
            }

            if(sesion.ocupados >= sesion.cupoMaximo)
            {
                conexion.rollback();
                enviarResultado(
                        false,
                        "La proxima sesion esta completa (" +
                                sesion.cupoMaximo + "/" + sesion.cupoMaximo + ").",
                        0
                );
                return;
            }

            int furniId = 0;
            String nombreBase = null;

            String validarSql =
                    "SELECT i.item_id, ib.public_name, ib.item_name " +
                    "FROM items i " +
                    "INNER JOIN items_base ib ON ib.id = i.item_id " +
                    "WHERE i.id = ? AND i.user_id = ? AND i.room_id = 0 AND ib.allow_trade = 1 " +
                    "FOR UPDATE";

            try(PreparedStatement validar = conexion.prepareStatement(validarSql))
            {
                for(int indice = 0; indice < instanciaIds.size(); indice++)
                {
                    int instanciaId = instanciaIds.get(indice);

                    validar.setInt(1, instanciaId);
                    validar.setInt(2, usuarioId);

                    try(ResultSet resultado = validar.executeQuery())
                    {
                        if(!resultado.next())
                        {
                            conexion.rollback();
                            enviarResultado(false, "Uno de los objetos no se puede subastar.", 0);
                            return;
                        }

                        int itemIdActual = resultado.getInt("item_id");

                        if(indice == 0)
                        {
                            furniId = itemIdActual;

                            String publicName = resultado.getString("public_name");
                            String itemName = resultado.getString("item_name");

                            if(publicName == null || publicName.isBlank() ||
                                    publicName.equals("0") ||
                                    publicName.toLowerCase().endsWith("_name"))
                            {
                                nombreBase = (itemName == null || itemName.isBlank())
                                        ? "Furni"
                                        : itemName.replace('_', ' ');
                            }
                            else
                            {
                                nombreBase = publicName;
                            }
                        }
                        else if(itemIdActual != furniId)
                        {
                            conexion.rollback();
                            enviarResultado(
                                    false,
                                    "Todos los objetos del lote deben ser el mismo furni.",
                                    0
                            );
                            return;
                        }
                    }

                    if(LotesSubastas.estaEnSubastaActiva(conexion, instanciaId))
                    {
                        conexion.rollback();
                        enviarResultado(false, "Uno de los objetos ya esta en una subasta.", 0);
                        return;
                    }
                }
            }

            String nombreObjeto = cantidad > 1
                    ? nombreBase + " x" + cantidad
                    : nombreBase;

            int posicionCola = 1;

            try(PreparedStatement posicion = conexion.prepareStatement(
                    "SELECT COALESCE(MAX(posicion_cola), 0) + 1 AS siguiente " +
                    "FROM subastas WHERE estado = 'en_cola' AND sesion_id = ? FOR UPDATE"))
            {
                posicion.setInt(1, sesion.id);

                try(ResultSet resultado = posicion.executeQuery())
                {
                    if(resultado.next())
                    {
                        posicionCola = resultado.getInt("siguiente");
                    }
                }
            }

            int subastaId;

            try(PreparedStatement insertar = conexion.prepareStatement(
                    "INSERT INTO subastas " +
                    "(sesion_id, vendedor_id, objeto_id, furni_id, nombre_objeto, cantidad, " +
                    "precio_inicial, puja_actual, estado, posicion_cola) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, 0, 'en_cola', ?)",
                    Statement.RETURN_GENERATED_KEYS))
            {
                insertar.setInt(1, sesion.id);
                insertar.setInt(2, usuarioId);
                insertar.setInt(3, instanciaIds.get(0));
                insertar.setInt(4, furniId);
                insertar.setString(5, nombreObjeto);
                insertar.setInt(6, cantidad);
                insertar.setInt(7, precioInicial);
                insertar.setInt(8, posicionCola);
                insertar.executeUpdate();

                try(ResultSet claves = insertar.getGeneratedKeys())
                {
                    if(!claves.next())
                    {
                        throw new IllegalStateException("No se pudo obtener el ID de la subasta.");
                    }

                    subastaId = claves.getInt(1);
                }
            }

            try(PreparedStatement asociar = conexion.prepareStatement(
                    "INSERT INTO subastas_items (subasta_id, objeto_id) VALUES (?, ?)"))
            {
                for(int instanciaId : instanciaIds)
                {
                    asociar.setInt(1, subastaId);
                    asociar.setInt(2, instanciaId);
                    asociar.addBatch();
                }

                asociar.executeBatch();
            }

            try(PreparedStatement bloquear = conexion.prepareStatement(
                    "UPDATE items SET user_id = -1 " +
                    "WHERE id = ? AND user_id = ? AND room_id = 0"))
            {
                for(int instanciaId : instanciaIds)
                {
                    bloquear.setInt(1, instanciaId);
                    bloquear.setInt(2, usuarioId);

                    if(bloquear.executeUpdate() != 1)
                    {
                        throw new IllegalStateException(
                                "No se pudo bloquear el objeto " + instanciaId
                        );
                    }
                }
            }

            for(int indice = 0; indice < items.size(); indice++)
            {
                HabboItem item = items.get(indice);
                int instanciaId = instanciaIds.get(indice);

                itemsComponent.removeHabboItem(item);

                if(itemsComponent.getHabboItem(instanciaId) != null)
                {
                    throw new IllegalStateException(
                            "El inventario rechazo la retirada del objeto " + instanciaId
                    );
                }

                retiradosDeMemoria.add(item);
            }

            conexion.commit();

            for(HabboItem item : items)
            {
                item.setUserId(-1);
                this.client.sendResponse(new RemoveHabboItemComposer(item.getGiftAdjustedId()));
            }

            this.client.sendResponse(new InventoryRefreshComposer());

            int usados = sesion.ocupados + 1;

            enviarResultado(
                    true,
                    "Subasta #" + subastaId +
                            " anadida a " + sesion.nombre +
                            ". Lote: " + cantidad + " unidad" + (cantidad == 1 ? "" : "es") +
                            ". Posicion: " + posicionCola +
                            ". Cupo: " + usados + "/" + sesion.cupoMaximo + ".",
                    subastaId
            );

            System.out.println("[Subastas] "
                    + this.client.getHabbo().getHabboInfo().getUsername()
                    + " puso un lote de " + cantidad
                    + " objeto(s) en la subasta #" + subastaId
                    + " para la sesion #" + sesion.id
                    + " por " + precioInicial + " creditos");
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

            for(HabboItem item : retiradosDeMemoria)
            {
                try
                {
                    item.setUserId(usuarioId);

                    if(itemsComponent.getHabboItem(item.getId()) == null)
                    {
                        itemsComponent.addItem(item);
                    }
                }
                catch(Exception restaurarError)
                {
                    restaurarError.printStackTrace();
                }
            }

            System.out.println("[Subastas] ERROR creando lote de subasta: "
                    + error.getClass().getName() + ": " + error.getMessage());
            error.printStackTrace();

            enviarResultado(false, "No se pudo crear la subasta.", 0);
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

    private boolean tieneClubActivo()
    {
        try
        {
            Method getHabboStats = this.client.getHabbo().getClass().getMethod("getHabboStats");
            Object stats = getHabboStats.invoke(this.client.getHabbo());

            if(stats == null) return false;

            Method hasActiveClub = stats.getClass().getMethod("hasActiveClub");
            Object resultado = hasActiveClub.invoke(stats);

            return Boolean.TRUE.equals(resultado);
        }
        catch(Exception ignorado)
        {
            return false;
        }
    }

    private void enviarResultado(boolean exito, String mensaje, int subastaId)
    {
        ServerMessage respuesta = new ServerMessage(5005);
        respuesta.appendBoolean(exito);
        respuesta.appendString(mensaje);
        respuesta.appendInt(subastaId);

        this.client.sendResponse(respuesta);
    }
}