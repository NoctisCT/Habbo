package com.retro.subastas.mensajes;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.MessageHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ObtenerEstadisticasSubastas extends MessageHandler
{
    private static final int MAX_TOP = 12;
    private static final int MAX_BUSQUEDA = 20;
    private static final int MAX_HISTORIAL = 20;

    private static final class FurniResumen
    {
        int furniId;
        String nombre;
        int spriteId;
        String tipo;
        int ventas;
        int precioMedioUnidad;
    }

    private static final class PuntoPrecio
    {
        String fecha;
        int precioUnidad;
        int precioLote;
        int cantidad;
    }

    private static final class Detalle
    {
        int furniId;
        String nombre;
        int spriteId;
        String tipo;

        int ventas;
        int unidadesVendidas;
        int precioMedioUnidad;
        int precioMinimoUnidad;
        int precioMaximoUnidad;
        int ultimoPrecioUnidad;
        long volumenCreditos;
        int tasaVenta;
        int pujasMediaX100;

        int radarValor;
        int radarDemanda;
        int radarVolumen;
        int radarActividad;
        int radarExito;

        List<PuntoPrecio> historial = new ArrayList<>();
    }

    @Override
    public void handle()
    {
        if(this.client == null || this.client.getHabbo() == null)
        {
            return;
        }

        int furniSolicitado = this.packet.readInt().intValue();
        String busqueda = this.packet.readString();

        if(busqueda == null)
        {
            busqueda = "";
        }

        busqueda = busqueda.trim();

        int totalVendidas = 0;
        int totalFinalizadas = 0;
        long volumenGlobal = 0L;
        int precioMedioGlobal = 0;
        int tasaVentaGlobal = 0;
        int recordPrecio = 0;
        String recordNombre = "";

        List<FurniResumen> top = new ArrayList<>();
        Detalle detalle = null;

        try(Connection conexion = Emulator.getDatabase().getDataSource().getConnection())
        {
            String generalSql =
                    "SELECT " +
                    "SUM(CASE WHEN estado = 'vendida' THEN 1 ELSE 0 END) AS vendidas, " +
                    "SUM(CASE WHEN estado IN ('vendida','sin_vender') THEN 1 ELSE 0 END) AS finalizadas, " +
                    "COALESCE(SUM(CASE WHEN estado = 'vendida' THEN puja_actual ELSE 0 END), 0) AS volumen, " +
                    "COALESCE(ROUND(AVG(CASE WHEN estado = 'vendida' " +
                    "THEN puja_actual / GREATEST(cantidad,1) END)), 0) AS media_unidad " +
                    "FROM subastas";

            try(PreparedStatement consulta = conexion.prepareStatement(generalSql);
                ResultSet resultado = consulta.executeQuery())
            {
                if(resultado.next())
                {
                    totalVendidas = resultado.getInt("vendidas");
                    totalFinalizadas = resultado.getInt("finalizadas");
                    volumenGlobal = resultado.getLong("volumen");
                    precioMedioGlobal = resultado.getInt("media_unidad");
                }
            }

            tasaVentaGlobal = totalFinalizadas > 0
                    ? Math.min(100, (int)Math.round((totalVendidas * 100.0) / totalFinalizadas))
                    : 0;

            String recordSql =
                    "SELECT s.puja_actual, ib.public_name, ib.item_name " +
                    "FROM subastas s " +
                    "INNER JOIN items_base ib ON ib.id = s.furni_id " +
                    "WHERE s.estado = 'vendida' " +
                    "ORDER BY s.puja_actual DESC, s.id DESC LIMIT 1";

            try(PreparedStatement consulta = conexion.prepareStatement(recordSql);
                ResultSet resultado = consulta.executeQuery())
            {
                if(resultado.next())
                {
                    recordPrecio = resultado.getInt("puja_actual");
                    recordNombre = nombreVisible(
                            resultado.getString("public_name"),
                            resultado.getString("item_name")
                    );
                }
            }

            boolean buscando = !busqueda.isBlank();

            String topSql =
                    "SELECT s.furni_id, ib.public_name, ib.item_name, ib.sprite_id, ib.type, " +
                    "COUNT(*) AS ventas, " +
                    "ROUND(AVG(s.puja_actual / GREATEST(s.cantidad,1)), 0) AS media_unidad, " +
                    "SUM(s.puja_actual) AS volumen " +
                    "FROM subastas s " +
                    "INNER JOIN items_base ib ON ib.id = s.furni_id " +
                    "WHERE s.estado = 'vendida' " +
                    (buscando
                            ? "AND (LOWER(COALESCE(ib.public_name,'')) LIKE ? " +
                              "OR LOWER(COALESCE(ib.item_name,'')) LIKE ?) "
                            : "") +
                    "GROUP BY s.furni_id, ib.public_name, ib.item_name, ib.sprite_id, ib.type " +
                    "ORDER BY ventas DESC, volumen DESC, s.furni_id ASC " +
                    "LIMIT ?";

            try(PreparedStatement consulta = conexion.prepareStatement(topSql))
            {
                int parametro = 1;

                if(buscando)
                {
                    String patron = "%" + busqueda.toLowerCase(Locale.ROOT) + "%";
                    consulta.setString(parametro++, patron);
                    consulta.setString(parametro++, patron);
                }

                consulta.setInt(parametro, buscando ? MAX_BUSQUEDA : MAX_TOP);

                try(ResultSet resultado = consulta.executeQuery())
                {
                    while(resultado.next())
                    {
                        FurniResumen fila = new FurniResumen();
                        fila.furniId = resultado.getInt("furni_id");
                        fila.nombre = nombreVisible(
                                resultado.getString("public_name"),
                                resultado.getString("item_name")
                        );
                        fila.spriteId = resultado.getInt("sprite_id");
                        fila.tipo = resultado.getString("type");
                        fila.ventas = resultado.getInt("ventas");
                        fila.precioMedioUnidad = resultado.getInt("media_unidad");

                        top.add(fila);
                    }
                }
            }

            int furniElegido = furniSolicitado;

            if(furniElegido <= 0 && !top.isEmpty())
            {
                furniElegido = top.get(0).furniId;
            }

            if(furniElegido > 0)
            {
                detalle = cargarDetalle(conexion, furniElegido);
            }
        }
        catch(Exception error)
        {
            System.out.println("[Subastas] ERROR obteniendo estadisticas: "
                    + error.getClass().getName() + ": " + error.getMessage());
            error.printStackTrace();

            enviar(
                    0, 0, 0L, 0, 0, 0, "",
                    new ArrayList<>(), null
            );
            return;
        }

        enviar(
                totalVendidas,
                totalFinalizadas,
                volumenGlobal,
                precioMedioGlobal,
                tasaVentaGlobal,
                recordPrecio,
                recordNombre,
                top,
                detalle
        );
    }

    private Detalle cargarDetalle(Connection conexion, int furniId) throws Exception
    {
        Detalle detalle = new Detalle();
        detalle.furniId = furniId;

        String baseSql =
                "SELECT public_name, item_name, sprite_id, type " +
                "FROM items_base WHERE id = ? LIMIT 1";

        try(PreparedStatement consulta = conexion.prepareStatement(baseSql))
        {
            consulta.setInt(1, furniId);

            try(ResultSet resultado = consulta.executeQuery())
            {
                if(!resultado.next())
                {
                    return null;
                }

                detalle.nombre = nombreVisible(
                        resultado.getString("public_name"),
                        resultado.getString("item_name")
                );
                detalle.spriteId = resultado.getInt("sprite_id");
                detalle.tipo = resultado.getString("type");
            }
        }

        String statsSql =
                "SELECT " +
                "SUM(CASE WHEN estado = 'vendida' THEN 1 ELSE 0 END) AS ventas, " +
                "SUM(CASE WHEN estado IN ('vendida','sin_vender') THEN 1 ELSE 0 END) AS finalizadas, " +
                "COALESCE(SUM(CASE WHEN estado = 'vendida' THEN cantidad ELSE 0 END),0) AS unidades, " +
                "COALESCE(ROUND(AVG(CASE WHEN estado = 'vendida' " +
                "THEN puja_actual / GREATEST(cantidad,1) END)),0) AS media_unidad, " +
                "COALESCE(ROUND(MIN(CASE WHEN estado = 'vendida' " +
                "THEN puja_actual / GREATEST(cantidad,1) END)),0) AS minimo_unidad, " +
                "COALESCE(ROUND(MAX(CASE WHEN estado = 'vendida' " +
                "THEN puja_actual / GREATEST(cantidad,1) END)),0) AS maximo_unidad, " +
                "COALESCE(SUM(CASE WHEN estado = 'vendida' THEN puja_actual ELSE 0 END),0) AS volumen " +
                "FROM subastas WHERE furni_id = ?";

        int finalizadas = 0;

        try(PreparedStatement consulta = conexion.prepareStatement(statsSql))
        {
            consulta.setInt(1, furniId);

            try(ResultSet resultado = consulta.executeQuery())
            {
                if(resultado.next())
                {
                    detalle.ventas = resultado.getInt("ventas");
                    finalizadas = resultado.getInt("finalizadas");
                    detalle.unidadesVendidas = resultado.getInt("unidades");
                    detalle.precioMedioUnidad = resultado.getInt("media_unidad");
                    detalle.precioMinimoUnidad = resultado.getInt("minimo_unidad");
                    detalle.precioMaximoUnidad = resultado.getInt("maximo_unidad");
                    detalle.volumenCreditos = resultado.getLong("volumen");
                }
            }
        }

        detalle.tasaVenta = finalizadas > 0
                ? Math.min(100, (int)Math.round((detalle.ventas * 100.0) / finalizadas))
                : 0;

        String ultimoSql =
                "SELECT ROUND(puja_actual / GREATEST(cantidad,1),0) AS precio_unidad " +
                "FROM subastas " +
                "WHERE furni_id = ? AND estado = 'vendida' " +
                "ORDER BY COALESCE(fecha_finalizacion, fecha_fin) DESC, id DESC LIMIT 1";

        try(PreparedStatement consulta = conexion.prepareStatement(ultimoSql))
        {
            consulta.setInt(1, furniId);

            try(ResultSet resultado = consulta.executeQuery())
            {
                if(resultado.next())
                {
                    detalle.ultimoPrecioUnidad = resultado.getInt("precio_unidad");
                }
            }
        }

        String pujasSql =
                "SELECT COALESCE(ROUND(AVG(total_pujas) * 100,0),0) AS media_x100 " +
                "FROM (" +
                "SELECT s.id, COUNT(p.id) AS total_pujas " +
                "FROM subastas s " +
                "LEFT JOIN pujas_subastas p ON p.subasta_id = s.id " +
                "WHERE s.furni_id = ? AND s.estado = 'vendida' " +
                "GROUP BY s.id" +
                ") x";

        try(PreparedStatement consulta = conexion.prepareStatement(pujasSql))
        {
            consulta.setInt(1, furniId);

            try(ResultSet resultado = consulta.executeQuery())
            {
                if(resultado.next())
                {
                    detalle.pujasMediaX100 = resultado.getInt("media_x100");
                }
            }
        }

        String historialSql =
                "SELECT " +
                "DATE_FORMAT(COALESCE(fecha_finalizacion, fecha_fin), '%d/%m') AS fecha, " +
                "ROUND(puja_actual / GREATEST(cantidad,1),0) AS precio_unidad, " +
                "puja_actual AS precio_lote, " +
                "GREATEST(cantidad,1) AS cantidad " +
                "FROM subastas " +
                "WHERE furni_id = ? AND estado = 'vendida' " +
                "ORDER BY COALESCE(fecha_finalizacion, fecha_fin) DESC, id DESC " +
                "LIMIT ?";

        try(PreparedStatement consulta = conexion.prepareStatement(historialSql))
        {
            consulta.setInt(1, furniId);
            consulta.setInt(2, MAX_HISTORIAL);

            try(ResultSet resultado = consulta.executeQuery())
            {
                while(resultado.next())
                {
                    PuntoPrecio punto = new PuntoPrecio();
                    punto.fecha = resultado.getString("fecha");
                    punto.precioUnidad = resultado.getInt("precio_unidad");
                    punto.precioLote = resultado.getInt("precio_lote");
                    punto.cantidad = resultado.getInt("cantidad");

                    detalle.historial.add(punto);
                }
            }
        }

        Collections.reverse(detalle.historial);
        cargarRadar(conexion, detalle);

        return detalle;
    }

    private void cargarRadar(Connection conexion, Detalle detalle) throws Exception
    {
        double maxValor = 0.0;
        double maxDemanda = 0.0;
        double maxVolumen = 0.0;

        String agregadosSql =
                "SELECT furni_id, " +
                "SUM(CASE WHEN estado = 'vendida' THEN 1 ELSE 0 END) AS ventas, " +
                "COALESCE(SUM(CASE WHEN estado = 'vendida' THEN puja_actual ELSE 0 END),0) AS volumen, " +
                "COALESCE(AVG(CASE WHEN estado = 'vendida' " +
                "THEN puja_actual / GREATEST(cantidad,1) END),0) AS valor " +
                "FROM subastas " +
                "WHERE estado IN ('vendida','sin_vender') " +
                "GROUP BY furni_id";

        try(PreparedStatement consulta = conexion.prepareStatement(agregadosSql);
            ResultSet resultado = consulta.executeQuery())
        {
            while(resultado.next())
            {
                maxDemanda = Math.max(maxDemanda, resultado.getDouble("ventas"));
                maxVolumen = Math.max(maxVolumen, resultado.getDouble("volumen"));
                maxValor = Math.max(maxValor, resultado.getDouble("valor"));
            }
        }

        double maxActividad = 0.0;

        String actividadSql =
                "SELECT MAX(media_pujas) AS max_media FROM (" +
                "SELECT furni_id, AVG(total_pujas) AS media_pujas FROM (" +
                "SELECT s.furni_id, s.id, COUNT(p.id) AS total_pujas " +
                "FROM subastas s " +
                "LEFT JOIN pujas_subastas p ON p.subasta_id = s.id " +
                "WHERE s.estado = 'vendida' " +
                "GROUP BY s.furni_id, s.id" +
                ") ventas GROUP BY furni_id" +
                ") mercado";

        try(PreparedStatement consulta = conexion.prepareStatement(actividadSql);
            ResultSet resultado = consulta.executeQuery())
        {
            if(resultado.next())
            {
                maxActividad = resultado.getDouble("max_media");
            }
        }

        detalle.radarValor = indice(detalle.precioMedioUnidad, maxValor);
        detalle.radarDemanda = indice(detalle.ventas, maxDemanda);
        detalle.radarVolumen = indice(detalle.volumenCreditos, maxVolumen);
        detalle.radarActividad = indice(detalle.pujasMediaX100 / 100.0, maxActividad);
        detalle.radarExito = Math.max(0, Math.min(100, detalle.tasaVenta));
    }

    private int indice(double valor, double maximo)
    {
        if(valor <= 0 || maximo <= 0)
        {
            return 0;
        }

        return Math.max(0, Math.min(100, (int)Math.round((valor * 100.0) / maximo)));
    }

    private String nombreVisible(String publicName, String itemName)
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

    private void enviar(
            int totalVendidas,
            int totalFinalizadas,
            long volumenGlobal,
            int precioMedioGlobal,
            int tasaVentaGlobal,
            int recordPrecio,
            String recordNombre,
            List<FurniResumen> top,
            Detalle detalle
    )
    {
        ServerMessage respuesta = new ServerMessage(5020);

        respuesta.appendInt(totalVendidas);
        respuesta.appendInt(totalFinalizadas);
        respuesta.appendString(Long.toString(volumenGlobal));
        respuesta.appendInt(precioMedioGlobal);
        respuesta.appendInt(tasaVentaGlobal);
        respuesta.appendInt(recordPrecio);
        respuesta.appendString(recordNombre == null ? "" : recordNombre);

        respuesta.appendInt(top.size());

        for(FurniResumen fila : top)
        {
            respuesta.appendInt(fila.furniId);
            respuesta.appendString(fila.nombre == null ? "Furni" : fila.nombre);
            respuesta.appendInt(fila.spriteId);
            respuesta.appendString(fila.tipo == null ? "s" : fila.tipo);
            respuesta.appendInt(fila.ventas);
            respuesta.appendInt(fila.precioMedioUnidad);
        }

        respuesta.appendBoolean(detalle != null);

        if(detalle != null)
        {
            respuesta.appendInt(detalle.furniId);
            respuesta.appendString(detalle.nombre == null ? "Furni" : detalle.nombre);
            respuesta.appendInt(detalle.spriteId);
            respuesta.appendString(detalle.tipo == null ? "s" : detalle.tipo);

            respuesta.appendInt(detalle.ventas);
            respuesta.appendInt(detalle.unidadesVendidas);
            respuesta.appendInt(detalle.precioMedioUnidad);
            respuesta.appendInt(detalle.precioMinimoUnidad);
            respuesta.appendInt(detalle.precioMaximoUnidad);
            respuesta.appendInt(detalle.ultimoPrecioUnidad);
            respuesta.appendString(Long.toString(detalle.volumenCreditos));
            respuesta.appendInt(detalle.tasaVenta);
            respuesta.appendInt(detalle.pujasMediaX100);

            respuesta.appendInt(detalle.radarValor);
            respuesta.appendInt(detalle.radarDemanda);
            respuesta.appendInt(detalle.radarVolumen);
            respuesta.appendInt(detalle.radarActividad);
            respuesta.appendInt(detalle.radarExito);

            respuesta.appendInt(detalle.historial.size());

            for(PuntoPrecio punto : detalle.historial)
            {
                respuesta.appendString(punto.fecha == null ? "" : punto.fecha);
                respuesta.appendInt(punto.precioUnidad);
                respuesta.appendInt(punto.precioLote);
                respuesta.appendInt(punto.cantidad);
            }
        }

        this.client.sendResponse(respuesta);
    }
}