package com.retro.subastas;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.outgoing.inventory.AddHabboItemComposer;
import com.eu.habbo.messages.outgoing.inventory.InventoryRefreshComposer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class GestorSesionesSubastas
{
    public static final class SesionDestino
    {
        public final int id;
        public final String nombre;
        public final int cupoMaximo;
        public final int ocupados;

        private SesionDestino(int id, String nombre, int cupoMaximo, int ocupados)
        {
            this.id = id;
            this.nombre = nombre;
            this.cupoMaximo = cupoMaximo;
            this.ocupados = ocupados;
        }
    }

    private static final DateTimeFormatter FORMATO_DB =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private GestorSesionesSubastas()
    {
    }

    public static void mantener()
    {
        try
        {
            LotesSubastas.asegurarEstructura();
            asegurarSesionProgramada();
            actualizarEstados();
            adoptarSubastasAntiguas();
            devolverPendientesDeSesionesVencidas();
        }
        catch(Exception error)
        {
            System.out.println("[Subastas] ERROR manteniendo sesiones: "
                    + error.getClass().getName() + ": " + error.getMessage());
            error.printStackTrace();
        }
    }

    public static void asegurarSesionProgramada() throws Exception
    {
        int diaSemana;
        LocalTime apertura;
        LocalTime cierre;
        LocalDateTime ahora;

        try(Connection conexion = Emulator.getDatabase().getDataSource().getConnection();
            PreparedStatement consulta = conexion.prepareStatement(
                    "SELECT dia_semana, " +
                    "TIME_FORMAT(hora_apertura, '%H:%i:%s') AS apertura, " +
                    "TIME_FORMAT(hora_cierre, '%H:%i:%s') AS cierre, " +
                    "DATE_FORMAT(NOW(), '%Y-%m-%d %H:%i:%s') AS ahora " +
                    "FROM configuracion_subastas WHERE id = 1 LIMIT 1");
            ResultSet resultado = consulta.executeQuery())
        {
            if(!resultado.next())
            {
                return;
            }

            diaSemana = resultado.getInt("dia_semana");
            apertura = LocalTime.parse(resultado.getString("apertura"));
            cierre = LocalTime.parse(resultado.getString("cierre"));
            ahora = LocalDateTime.parse(resultado.getString("ahora"), FORMATO_DB);
        }

        LocalDateTime inicioElegido = null;
        LocalDateTime finElegido = null;

        // Buscamos primero una sesion que este activa ahora. Si no existe,
        // elegimos la siguiente. El rango cubre sesiones nocturnas y semanales.
        for(int offset = -7; offset <= 14; offset++)
        {
            LocalDate fecha = ahora.toLocalDate().plusDays(offset);

            if(diaSemana != 0)
            {
                int diaJava = fecha.getDayOfWeek().getValue();

                if(diaJava != diaSemana)
                {
                    continue;
                }
            }

            LocalDateTime inicio = LocalDateTime.of(fecha, apertura);
            LocalDateTime fin;

            if(apertura.equals(cierre))
            {
                fin = inicio.plusDays(1);
            }
            else if(apertura.isBefore(cierre))
            {
                fin = LocalDateTime.of(fecha, cierre);
            }
            else
            {
                fin = LocalDateTime.of(fecha.plusDays(1), cierre);
            }

            boolean activaAhora =
                    !ahora.isBefore(inicio) &&
                    ahora.isBefore(fin);

            if(activaAhora)
            {
                inicioElegido = inicio;
                finElegido = fin;
                break;
            }

            if(inicio.isAfter(ahora) &&
                    (inicioElegido == null || inicio.isBefore(inicioElegido)))
            {
                inicioElegido = inicio;
                finElegido = fin;
            }
        }

        if(inicioElegido == null || finElegido == null)
        {
            return;
        }

        long duracionSesion = java.time.Duration
                .between(inicioElegido, finElegido)
                .getSeconds();

        int bloque = Math.max(
                1,
                ConfiguracionSubastas.getDuracionSegundos() +
                ConfiguracionSubastas.getTransicionSegundos()
        );

        int teorico = Math.max(1, (int)(duracionSesion / bloque));

        int cupo = Math.max(
                1,
                (teorico * ConfiguracionSubastas.getMargenCupoPorcentaje()) / 100
        );

        String nombre = "Sesi\u00f3n del " +
                inicioElegido.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        String estado = (!ahora.isBefore(inicioElegido) && ahora.isBefore(finElegido))
                ? "abierta"
                : "programada";

        try(Connection conexion = Emulator.getDatabase().getDataSource().getConnection();
            PreparedStatement insertar = conexion.prepareStatement(
                    "INSERT IGNORE INTO sesiones_subastas " +
                    "(tipo, nombre, fecha_inicio, fecha_fin, cupo_maximo, estado) " +
                    "VALUES ('programada', ?, ?, ?, ?, ?)"))
        {
            insertar.setString(1, nombre);
            insertar.setString(2, inicioElegido.format(FORMATO_DB));
            insertar.setString(3, finElegido.format(FORMATO_DB));
            insertar.setInt(4, cupo);
            insertar.setString(5, estado);
            insertar.executeUpdate();
        }

        try(Connection conexion = Emulator.getDatabase().getDataSource().getConnection();
            PreparedStatement actualizar = conexion.prepareStatement(
                    "UPDATE sesiones_subastas " +
                    "SET nombre = ?, cupo_maximo = ?, estado = ? " +
                    "WHERE tipo = 'programada' AND fecha_inicio = ? AND fecha_fin = ?"))
        {
            actualizar.setString(1, nombre);
            actualizar.setInt(2, cupo);
            actualizar.setString(3, estado);
            actualizar.setString(4, inicioElegido.format(FORMATO_DB));
            actualizar.setString(5, finElegido.format(FORMATO_DB));
            actualizar.executeUpdate();
        }
    }

    public static SesionDestino obtenerSesionDestino(Connection conexion) throws Exception
    {
        try(PreparedStatement consulta = conexion.prepareStatement(
                "SELECT id, nombre, cupo_maximo " +
                "FROM sesiones_subastas " +
                "WHERE fecha_fin > NOW() " +
                "AND estado IN ('programada', 'abierta') " +
                "ORDER BY " +
                "CASE WHEN fecha_inicio <= NOW() THEN 0 ELSE 1 END ASC, " +
                "CASE WHEN fecha_inicio <= NOW() THEN fecha_inicio END DESC, " +
                "CASE WHEN fecha_inicio > NOW() THEN fecha_inicio END ASC " +
                "LIMIT 1 FOR UPDATE");
            ResultSet resultado = consulta.executeQuery())
        {
            if(!resultado.next())
            {
                return null;
            }

            int id = resultado.getInt("id");
            String nombre = resultado.getString("nombre");
            int cupo = resultado.getInt("cupo_maximo");

            int ocupados = 0;

            try(PreparedStatement contar = conexion.prepareStatement(
                    "SELECT COUNT(*) AS total FROM subastas " +
                    "WHERE sesion_id = ? AND estado IN ('en_cola', 'activa', 'vendida', 'sin_vender')"))
            {
                contar.setInt(1, id);

                try(ResultSet conteo = contar.executeQuery())
                {
                    if(conteo.next())
                    {
                        ocupados = conteo.getInt("total");
                    }
                }
            }

            return new SesionDestino(id, nombre, cupo, ocupados);
        }
    }

    public static int obtenerSesionActivaId() throws Exception
    {
        try(Connection conexion = Emulator.getDatabase().getDataSource().getConnection();
            PreparedStatement consulta = conexion.prepareStatement(
                    "SELECT id FROM sesiones_subastas " +
                    "WHERE fecha_inicio <= NOW() AND fecha_fin > NOW() " +
                    "AND estado = 'abierta' " +
                    "ORDER BY fecha_inicio DESC, id DESC LIMIT 1");
            ResultSet resultado = consulta.executeQuery())
        {
            return resultado.next() ? resultado.getInt("id") : 0;
        }
    }

    private static void actualizarEstados() throws Exception
    {
        try(Connection conexion = Emulator.getDatabase().getDataSource().getConnection();
            PreparedStatement actualizar = conexion.prepareStatement(
                    "UPDATE sesiones_subastas SET estado = CASE " +
                    "WHEN fecha_fin <= NOW() THEN 'cerrada' " +
                    "WHEN fecha_inicio <= NOW() THEN 'abierta' " +
                    "ELSE 'programada' END " +
                    "WHERE estado != 'cancelada'"))
        {
            actualizar.executeUpdate();
        }
    }

    private static void adoptarSubastasAntiguas() throws Exception
    {
        int sesionId = 0;

        try(Connection conexion = Emulator.getDatabase().getDataSource().getConnection();
            PreparedStatement consulta = conexion.prepareStatement(
                    "SELECT id FROM sesiones_subastas " +
                    "WHERE fecha_fin > NOW() " +
                    "AND estado IN ('programada', 'abierta') " +
                    "ORDER BY CASE WHEN fecha_inicio <= NOW() THEN 0 ELSE 1 END, " +
                    "fecha_inicio ASC LIMIT 1");
            ResultSet resultado = consulta.executeQuery())
        {
            if(resultado.next())
            {
                sesionId = resultado.getInt("id");
            }
        }

        if(sesionId <= 0)
        {
            return;
        }

        try(Connection conexion = Emulator.getDatabase().getDataSource().getConnection();
            PreparedStatement actualizar = conexion.prepareStatement(
                    "UPDATE subastas SET sesion_id = ? " +
                    "WHERE sesion_id IS NULL AND estado IN ('en_cola', 'activa')"))
        {
            actualizar.setInt(1, sesionId);
            actualizar.executeUpdate();
        }
    }

    private static void devolverPendientesDeSesionesVencidas() throws Exception
    {
        while(true)
        {
            int subastaId;
            int vendedorId;
            int objetoId;
            List<Integer> objetoIds = null;

            Connection conexion = null;

            try
            {
                conexion = Emulator.getDatabase().getDataSource().getConnection();
                conexion.setAutoCommit(false);

                try(PreparedStatement consulta = conexion.prepareStatement(
                        "SELECT s.id, s.vendedor_id, s.objeto_id " +
                        "FROM subastas s " +
                        "INNER JOIN sesiones_subastas ss ON ss.id = s.sesion_id " +
                        "WHERE s.estado = 'en_cola' AND ss.fecha_fin <= NOW() " +
                        "ORDER BY s.id ASC LIMIT 1 FOR UPDATE");
                    ResultSet resultado = consulta.executeQuery())
                {
                    if(!resultado.next())
                    {
                        conexion.rollback();
                        return;
                    }

                    subastaId = resultado.getInt("id");
                    vendedorId = resultado.getInt("vendedor_id");
                    objetoId = resultado.getInt("objeto_id");
                }

                objetoIds = LotesSubastas.obtenerObjetoIds(
                        conexion,
                        subastaId,
                        objetoId
                );

                LotesSubastas.transferirDesdeEscrow(
                        conexion,
                        objetoIds,
                        vendedorId
                );

                try(PreparedStatement finalizar = conexion.prepareStatement(
                        "UPDATE subastas SET estado = 'no_emitida', " +
                        "posicion_cola = 0, fecha_finalizacion = NOW() " +
                        "WHERE id = ? AND estado = 'en_cola'"))
                {
                    finalizar.setInt(1, subastaId);

                    if(finalizar.executeUpdate() != 1)
                    {
                        throw new IllegalStateException(
                                "No se pudo cerrar la subasta pendiente " + subastaId
                        );
                    }
                }

                conexion.commit();
            }
            catch(Exception error)
            {
                if(conexion != null)
                {
                    try
                    {
                        conexion.rollback();
                    }
                    catch(Exception ignorado)
                    {
                    }
                }

                throw error;
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

            LotesSubastas.refrescarInventarioOnline(vendedorId, objetoIds);

            System.out.println("[Subastas] Subasta #" + subastaId
                    + " no llego a salir. Furni devuelto al vendedor.");
        }
    }

    private static void refrescarInventarioOnline(int usuarioId, int objetoId)
    {
        try
        {
            Habbo habbo = Emulator.getGameEnvironment()
                    .getHabboManager()
                    .getHabbo(usuarioId);

            if(habbo == null || habbo.getClient() == null)
            {
                return;
            }

            if(habbo.getInventory()
                    .getItemsComponent()
                    .getHabboItem(objetoId) != null)
            {
                return;
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
                return;
            }

            habbo.getInventory()
                    .getItemsComponent()
                    .addItem(item);

            habbo.getClient().sendResponse(new AddHabboItemComposer(item));
            habbo.getClient().sendResponse(new InventoryRefreshComposer());
        }
        catch(Exception error)
        {
            System.out.println("[Subastas] Aviso: no se pudo refrescar el furni devuelto: "
                    + error.getMessage());
        }
    }
}