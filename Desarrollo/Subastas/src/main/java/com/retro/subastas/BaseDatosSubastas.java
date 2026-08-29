package com.retro.subastas;

import com.eu.habbo.Emulator;

import java.sql.Connection;
import java.sql.Statement;

public final class BaseDatosSubastas
{
    private BaseDatosSubastas()
    {
    }

    public static void inicializar() throws Exception
    {
        String sqlSubastas =
                "CREATE TABLE IF NOT EXISTS subastas (" +
                "id INT NOT NULL AUTO_INCREMENT," +
                "sesion_id INT NULL," +
                "vendedor_id INT NOT NULL," +
                "objeto_id INT NOT NULL," +
                "furni_id INT NOT NULL," +
                "nombre_objeto VARCHAR(100) NOT NULL," +
                "precio_inicial INT NOT NULL," +
                "puja_actual INT NOT NULL DEFAULT 0," +
                "mejor_postor_id INT NULL," +
                "estado VARCHAR(20) NOT NULL DEFAULT 'en_cola'," +
                "posicion_cola INT NOT NULL DEFAULT 0," +
                "fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                "fecha_inicio DATETIME NULL," +
                "fecha_fin DATETIME NULL," +
                "fecha_finalizacion DATETIME NULL," +
                "PRIMARY KEY (id)," +
                "KEY idx_subastas_sesion (sesion_id)," +
                "KEY idx_subastas_estado_posicion (estado, posicion_cola)," +
                "KEY idx_subastas_vendedor (vendedor_id)," +
                "KEY idx_subastas_objeto (objeto_id)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

        String sqlPujas =
                "CREATE TABLE IF NOT EXISTS pujas_subastas (" +
                "id INT NOT NULL AUTO_INCREMENT," +
                "subasta_id INT NOT NULL," +
                "postor_id INT NOT NULL," +
                "cantidad INT NOT NULL," +
                "fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                "PRIMARY KEY (id)," +
                "KEY idx_pujas_subasta (subasta_id, id)," +
                "KEY idx_pujas_postor (postor_id)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

        String sqlConfiguracion =
                "CREATE TABLE IF NOT EXISTS configuracion_subastas (" +
                "id INT NOT NULL," +
                "dia_semana TINYINT NOT NULL DEFAULT 0," +
                "hora_apertura TIME NOT NULL DEFAULT '00:00:00'," +
                "hora_cierre TIME NOT NULL DEFAULT '00:00:00'," +
                "duracion_segundos INT NOT NULL DEFAULT 60," +
                "transicion_segundos INT NOT NULL DEFAULT 1," +
                "margen_cupo_porcentaje INT NOT NULL DEFAULT 85," +
                "comision_porcentaje INT NOT NULL DEFAULT 0," +
                "limite_normal INT NOT NULL DEFAULT 3," +
                "limite_vip INT NOT NULL DEFAULT 5," +
                "anti_sniping_umbral INT NOT NULL DEFAULT 5," +
                "anti_sniping_extension INT NOT NULL DEFAULT 5," +
                "max_mis_subastas INT NOT NULL DEFAULT 25," +
                "PRIMARY KEY (id)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

        String sqlSesiones =
                "CREATE TABLE IF NOT EXISTS sesiones_subastas (" +
                "id INT NOT NULL AUTO_INCREMENT," +
                "tipo VARCHAR(20) NOT NULL DEFAULT 'programada'," +
                "nombre VARCHAR(100) NOT NULL," +
                "fecha_inicio DATETIME NOT NULL," +
                "fecha_fin DATETIME NOT NULL," +
                "cupo_maximo INT NOT NULL," +
                "estado VARCHAR(20) NOT NULL DEFAULT 'programada'," +
                "fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                "PRIMARY KEY (id)," +
                "UNIQUE KEY uq_sesion_programada (tipo, fecha_inicio, fecha_fin)," +
                "KEY idx_sesiones_fechas (fecha_inicio, fecha_fin)," +
                "KEY idx_sesiones_estado (estado)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

        try(Connection conexion = Emulator.getDatabase().getDataSource().getConnection();
            Statement sentencia = conexion.createStatement())
        {
            sentencia.executeUpdate(sqlSubastas);
            sentencia.executeUpdate(sqlPujas);
            sentencia.executeUpdate(sqlConfiguracion);
            sentencia.executeUpdate(sqlSesiones);

            // Migraciones para instalaciones que ya tenian las tablas.
            sentencia.executeUpdate(
                    "ALTER TABLE subastas " +
                    "ADD COLUMN IF NOT EXISTS sesion_id INT NULL AFTER id"
            );

            sentencia.executeUpdate(
                    "ALTER TABLE subastas " +
                    "ADD INDEX IF NOT EXISTS idx_subastas_sesion (sesion_id)"
            );

            sentencia.executeUpdate(
                    "ALTER TABLE configuracion_subastas " +
                    "ADD COLUMN IF NOT EXISTS dia_semana TINYINT NOT NULL DEFAULT 0 AFTER id"
            );

            sentencia.executeUpdate(
                    "ALTER TABLE configuracion_subastas " +
                    "ADD COLUMN IF NOT EXISTS transicion_segundos INT NOT NULL DEFAULT 1 " +
                    "AFTER duracion_segundos"
            );

            sentencia.executeUpdate(
                    "ALTER TABLE configuracion_subastas " +
                    "ADD COLUMN IF NOT EXISTS margen_cupo_porcentaje INT NOT NULL DEFAULT 85 " +
                    "AFTER transicion_segundos"
            );

            sentencia.executeUpdate(
                    "INSERT IGNORE INTO configuracion_subastas " +
                    "(id, dia_semana, hora_apertura, hora_cierre, duracion_segundos, " +
                    "transicion_segundos, margen_cupo_porcentaje, comision_porcentaje, " +
                    "limite_normal, limite_vip, anti_sniping_umbral, " +
                    "anti_sniping_extension, max_mis_subastas) " +
                    "VALUES (1, 0, '00:00:00', '00:00:00', 60, 1, 85, 0, 3, 5, 5, 5, 25)"
            );

            sentencia.executeUpdate(
                    "UPDATE subastas SET puja_actual = 0 " +
                    "WHERE mejor_postor_id IS NULL " +
                    "AND estado IN ('en_cola', 'activa')"
            );
        }

        System.out.println("[Subastas] Base de datos de subastas preparada");
    }
}