package com.retro.inventorylock;

import com.eu.habbo.Emulator;

import java.sql.Connection;
import java.sql.Statement;

public final class BaseDatosInventoryLock
{
    private BaseDatosInventoryLock()
    {
    }

    public static void inicializar() throws Exception
    {
        String sql =
                "CREATE TABLE IF NOT EXISTS inventory_pattern_lock (" +
                "user_id INT NOT NULL," +
                "enabled TINYINT(1) NOT NULL DEFAULT 0," +
                "pattern_hash VARCHAR(255) NULL," +
                "salt VARCHAR(64) NULL," +
                "failed_attempts INT NOT NULL DEFAULT 0," +
                "blocked_until DATETIME NULL," +
                "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                "updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                "PRIMARY KEY (user_id)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

        try(Connection conexion = Emulator.getDatabase().getDataSource().getConnection();
            Statement sentencia = conexion.createStatement())
        {
            sentencia.executeUpdate(sql);
        }

    }
}