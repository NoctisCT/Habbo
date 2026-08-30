package com.retro.rpgengine;

import com.eu.habbo.Emulator;

import java.sql.Connection;
import java.sql.Statement;

public final class BaseDatosRpgEngine
{
    private BaseDatosRpgEngine()
    {
    }

    private static boolean tableExists(Connection connection, String tableName) throws Exception
    {
        try(java.sql.ResultSet result = connection.getMetaData().getTables(
                connection.getCatalog(), null, tableName, new String[] { "TABLE" }))
        {
            return result.next();
        }
    }

    private static boolean columnExists(Connection connection, String tableName, String columnName) throws Exception
    {
        try(java.sql.ResultSet result = connection.getMetaData().getColumns(
                connection.getCatalog(), null, tableName, columnName))
        {
            return result.next();
        }
    }

    private static void migrateLegacyNames(Connection connection) throws Exception
    {
        boolean oldProjects = tableExists(connection, "holo_rpg_projects");
        boolean oldMovement = tableExists(connection, "holo_rpg_movement_config");
        boolean oldRooms = tableExists(connection, "holo_rpg_rooms");

        boolean newProjects = tableExists(connection, "rpg_engine_projects");
        boolean newMovement = tableExists(connection, "rpg_engine_movement_config");
        boolean newRooms = tableExists(connection, "rpg_engine_rooms");

        try(Statement statement = connection.createStatement())
        {
            if(oldProjects && !newProjects)
                statement.executeUpdate("RENAME TABLE holo_rpg_projects TO rpg_engine_projects");

            if(oldMovement && !newMovement)
                statement.executeUpdate("RENAME TABLE holo_rpg_movement_config TO rpg_engine_movement_config");

            if(oldRooms && !newRooms)
                statement.executeUpdate("RENAME TABLE holo_rpg_rooms TO rpg_engine_rooms");
        }
    }

    private static void ensureMovementSourceColumns(Connection connection) throws Exception
    {
        try(Statement statement = connection.createStatement())
        {
            if(!columnExists(connection, "rpg_engine_movement_config", "source_mode"))
            {
                statement.executeUpdate(
                        "ALTER TABLE rpg_engine_movement_config " +
                        "ADD COLUMN source_mode VARCHAR(16) NOT NULL DEFAULT 'fixed' AFTER confirmation_mode"
                );
            }

            if(!columnExists(connection, "rpg_engine_movement_config", "stat_id"))
            {
                statement.executeUpdate(
                        "ALTER TABLE rpg_engine_movement_config " +
                        "ADD COLUMN stat_id INT NULL AFTER source_mode"
                );
            }
        }
    }

    private static void ensureEncounterV2Columns(Connection connection) throws Exception
    {
        try(Statement statement = connection.createStatement())
        {
            if(!columnExists(connection, "rpg_engine_encounters", "initiator_user_id"))
            {
                statement.executeUpdate(
                        "ALTER TABLE rpg_engine_encounters " +
                        "ADD COLUMN initiator_user_id INT NULL AFTER created_by_user_id"
                );
            }

            if(!columnExists(connection, "rpg_engine_encounters", "creation_mode"))
            {
                statement.executeUpdate(
                        "ALTER TABLE rpg_engine_encounters " +
                        "ADD COLUMN creation_mode VARCHAR(16) NOT NULL DEFAULT 'gm' AFTER initiator_user_id"
                );
            }

            if(!columnExists(connection, "rpg_engine_encounter_participants", "join_sequence"))
            {
                statement.executeUpdate(
                        "ALTER TABLE rpg_engine_encounter_participants " +
                        "ADD COLUMN join_sequence INT NOT NULL DEFAULT 0 AFTER user_id"
                );
            }

            if(!columnExists(connection, "rpg_engine_encounter_participants", "entry_type"))
            {
                statement.executeUpdate(
                        "ALTER TABLE rpg_engine_encounter_participants " +
                        "ADD COLUMN entry_type VARCHAR(24) NOT NULL DEFAULT 'gm-added' AFTER join_sequence"
                );
            }

            if(!columnExists(connection, "rpg_engine_encounter_participants", "entry_by_user_id"))
            {
                statement.executeUpdate(
                        "ALTER TABLE rpg_engine_encounter_participants " +
                        "ADD COLUMN entry_by_user_id INT NULL AFTER entry_type"
                );
            }

            if(!columnExists(connection, "rpg_engine_encounter_participants", "saved_x"))
            {
                statement.executeUpdate(
                        "ALTER TABLE rpg_engine_encounter_participants " +
                        "ADD COLUMN saved_x SMALLINT NULL AFTER return_deadline"
                );
            }

            if(!columnExists(connection, "rpg_engine_encounter_participants", "saved_y"))
            {
                statement.executeUpdate(
                        "ALTER TABLE rpg_engine_encounter_participants " +
                        "ADD COLUMN saved_y SMALLINT NULL AFTER saved_x"
                );
            }

            if(!columnExists(connection, "rpg_engine_encounter_participants", "saved_z"))
            {
                statement.executeUpdate(
                        "ALTER TABLE rpg_engine_encounter_participants " +
                        "ADD COLUMN saved_z DECIMAL(10,3) NULL AFTER saved_y"
                );
            }
        }
    }

    public static void inicializar() throws Exception
    {
        try(Connection connection = Emulator.getDatabase().getDataSource().getConnection())
        {
            migrateLegacyNames(connection);
        }

        String projects =
                "CREATE TABLE IF NOT EXISTS rpg_engine_projects (" +
                "id INT NOT NULL AUTO_INCREMENT," +
                "owner_user_id INT NOT NULL," +
                "name VARCHAR(80) NOT NULL," +
                "enabled TINYINT(1) NOT NULL DEFAULT 1," +
                "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                "updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                "PRIMARY KEY (id)," +
                "KEY idx_rpg_engine_projects_owner (owner_user_id)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

        String movement =
                "CREATE TABLE IF NOT EXISTS rpg_engine_movement_config (" +
                "rpg_id INT NOT NULL," +
                "resource_name VARCHAR(24) NOT NULL DEFAULT 'MOV'," +
                "base_movement DECIMAL(10,3) NOT NULL DEFAULT 4.000," +
                "directions TINYINT NOT NULL DEFAULT 8," +
                "orthogonal_cost DECIMAL(10,3) NOT NULL DEFAULT 1.000," +
                "diagonal_cost DECIMAL(10,3) NOT NULL DEFAULT 1.000," +
                "allow_corner_cutting TINYINT(1) NOT NULL DEFAULT 0," +
                "max_step_height DECIMAL(10,3) NOT NULL DEFAULT 1.100," +
                "allow_falling TINYINT(1) NOT NULL DEFAULT 1," +
                "furniture_policy VARCHAR(32) NOT NULL DEFAULT 'respect-walkability'," +
                "unknown_furniture_blocks TINYINT(1) NOT NULL DEFAULT 1," +
                "confirmation_mode VARCHAR(16) NOT NULL DEFAULT 'confirm'," +
                "source_mode VARCHAR(16) NOT NULL DEFAULT 'fixed'," +
                "stat_id INT NULL," +
                "version INT NOT NULL DEFAULT 1," +
                "updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                "PRIMARY KEY (rpg_id)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

        String rooms =
                "CREATE TABLE IF NOT EXISTS rpg_engine_rooms (" +
                "room_id INT NOT NULL," +
                "rpg_id INT NOT NULL," +
                "profile_id INT NULL," +
                "enabled TINYINT(1) NOT NULL DEFAULT 1," +
                "movement_resource_name VARCHAR(24) NULL," +
                "movement_base_movement DECIMAL(10,3) NULL," +
                "movement_directions TINYINT NULL," +
                "movement_orthogonal_cost DECIMAL(10,3) NULL," +
                "movement_diagonal_cost DECIMAL(10,3) NULL," +
                "movement_allow_corner_cutting TINYINT(1) NULL," +
                "movement_max_step_height DECIMAL(10,3) NULL," +
                "movement_allow_falling TINYINT(1) NULL," +
                "movement_furniture_policy VARCHAR(32) NULL," +
                "movement_unknown_furniture_blocks TINYINT(1) NULL," +
                "movement_confirmation_mode VARCHAR(16) NULL," +
                "movement_version INT NOT NULL DEFAULT 0," +
                "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                "updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                "PRIMARY KEY (room_id)," +
                "KEY idx_rpg_engine_rooms_rpg (rpg_id)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

        String stats =
                "CREATE TABLE IF NOT EXISTS rpg_engine_stats (" +
                "id INT NOT NULL AUTO_INCREMENT," +
                "rpg_id INT NOT NULL," +
                "stat_key VARCHAR(24) NOT NULL," +
                "name VARCHAR(80) NOT NULL," +
                "default_value DECIMAL(14,3) NOT NULL DEFAULT 0.000," +
                "enabled TINYINT(1) NOT NULL DEFAULT 1," +
                "version INT NOT NULL DEFAULT 1," +
                "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                "updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                "PRIMARY KEY (id)," +
                "UNIQUE KEY uq_rpg_engine_stats_key (rpg_id, stat_key)," +
                "KEY idx_rpg_engine_stats_rpg (rpg_id)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

        String playerStats =
                "CREATE TABLE IF NOT EXISTS rpg_engine_player_stats (" +
                "rpg_id INT NOT NULL," +
                "user_id INT NOT NULL," +
                "stat_id INT NOT NULL," +
                "base_value DECIMAL(14,3) NOT NULL," +
                "version INT NOT NULL DEFAULT 1," +
                "updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                "PRIMARY KEY (rpg_id, user_id, stat_id)," +
                "KEY idx_rpg_engine_player_stats_user (user_id)," +
                "KEY idx_rpg_engine_player_stats_stat (stat_id)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

        String modifiers =
                "CREATE TABLE IF NOT EXISTS rpg_engine_stat_modifiers (" +
                "id INT NOT NULL AUTO_INCREMENT," +
                "rpg_id INT NOT NULL," +
                "user_id INT NOT NULL," +
                "stat_id INT NOT NULL," +
                "source_type VARCHAR(24) NOT NULL," +
                "source_key VARCHAR(80) NOT NULL," +
                "operation VARCHAR(16) NOT NULL," +
                "value DECIMAL(14,6) NOT NULL," +
                "enabled TINYINT(1) NOT NULL DEFAULT 1," +
                "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                "updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                "PRIMARY KEY (id)," +
                "UNIQUE KEY uq_rpg_engine_stat_modifier_source " +
                    "(rpg_id, user_id, stat_id, source_type, source_key)," +
                "KEY idx_rpg_engine_stat_modifiers_target (rpg_id, user_id, stat_id)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";


        String encounterConfig =
                "CREATE TABLE IF NOT EXISTS rpg_engine_encounter_config (" +
                "rpg_id INT NOT NULL," +
                "reconnect_grace_seconds INT NOT NULL DEFAULT 300," +
                "return_grace_seconds INT NOT NULL DEFAULT 600," +
                "version INT NOT NULL DEFAULT 1," +
                "updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                "PRIMARY KEY (rpg_id)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

        String encounters =
                "CREATE TABLE IF NOT EXISTS rpg_engine_encounters (" +
                "id INT NOT NULL AUTO_INCREMENT," +
                "rpg_id INT NOT NULL," +
                "room_id INT NOT NULL," +
                "created_by_user_id INT NOT NULL," +
                "initiator_user_id INT NULL," +
                "creation_mode VARCHAR(16) NOT NULL DEFAULT 'gm'," +
                "status VARCHAR(16) NOT NULL DEFAULT 'draft'," +
                "version INT NOT NULL DEFAULT 1," +
                "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                "started_at TIMESTAMP NULL," +
                "ended_at TIMESTAMP NULL," +
                "updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                "PRIMARY KEY (id)," +
                "KEY idx_rpg_engine_encounters_rpg_status (rpg_id, status)," +
                "KEY idx_rpg_engine_encounters_room_status (room_id, status)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

        String encounterParticipants =
                "CREATE TABLE IF NOT EXISTS rpg_engine_encounter_participants (" +
                "encounter_id INT NOT NULL," +
                "user_id INT NOT NULL," +
                "join_sequence INT NOT NULL DEFAULT 0," +
                "entry_type VARCHAR(24) NOT NULL DEFAULT 'gm-added'," +
                "entry_by_user_id INT NULL," +
                "status VARCHAR(16) NOT NULL DEFAULT 'active'," +
                "exit_reason VARCHAR(32) NULL," +
                "reconnect_deadline TIMESTAMP NULL," +
                "return_deadline TIMESTAMP NULL," +
                "saved_x SMALLINT NULL," +
                "saved_y SMALLINT NULL," +
                "saved_z DECIMAL(10,3) NULL," +
                "joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                "left_at TIMESTAMP NULL," +
                "updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                "PRIMARY KEY (encounter_id, user_id)," +
                "KEY idx_rpg_engine_encounter_participant_user (user_id, status)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

        try(Connection connection = Emulator.getDatabase().getDataSource().getConnection();
            Statement statement = connection.createStatement())
        {
            statement.executeUpdate(projects);
            statement.executeUpdate(movement);
            statement.executeUpdate(rooms);
            statement.executeUpdate(stats);
            statement.executeUpdate(playerStats);
            statement.executeUpdate(modifiers);
            statement.executeUpdate(encounterConfig);
            statement.executeUpdate(encounters);
            statement.executeUpdate(encounterParticipants);

            ensureMovementSourceColumns(connection);
            ensureEncounterV2Columns(connection);
        }
    }
}
