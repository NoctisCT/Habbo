package com.retro.rpgengine;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class ServicioRpgEngine
{
    public static final int OV_RESOURCE = 1;
    public static final int OV_BASE = 2;
    public static final int OV_DIRECTIONS = 4;
    public static final int OV_ORTHOGONAL = 8;
    public static final int OV_DIAGONAL = 16;
    public static final int OV_CORNERS = 32;
    public static final int OV_STEP_HEIGHT = 64;
    public static final int OV_FALLING = 128;
    public static final int OV_FURNITURE = 256;
    public static final int OV_UNKNOWN_BLOCKS = 512;
    public static final int OV_CONFIRMATION = 1024;

    private ServicioRpgEngine()
    {
    }

    public static final class RpgEngineException extends Exception
    {
        public final String code;

        public RpgEngineException(String code)
        {
            super(code);
            this.code = code;
        }
    }

    public static final class Movimiento
    {
        public String resourceName = "MOV";
        public double baseMovement = 4.0;
        public int directions = 8;
        public double orthogonalCost = 1.0;
        public double diagonalCost = 1.0;
        public boolean allowCornerCutting = false;
        public double maxStepHeight = 1.1;
        public boolean allowFalling = true;
        public String furniturePolicy = "respect-walkability";
        public boolean unknownFurnitureBlocks = true;
        public String confirmationMode = "confirm";
        public String sourceMode = "fixed";
        public int statId = 0;
        public int version = 1;

        public Movimiento copy()
        {
            Movimiento value = new Movimiento();
            value.resourceName = this.resourceName;
            value.baseMovement = this.baseMovement;
            value.directions = this.directions;
            value.orthogonalCost = this.orthogonalCost;
            value.diagonalCost = this.diagonalCost;
            value.allowCornerCutting = this.allowCornerCutting;
            value.maxStepHeight = this.maxStepHeight;
            value.allowFalling = this.allowFalling;
            value.furniturePolicy = this.furniturePolicy;
            value.unknownFurnitureBlocks = this.unknownFurnitureBlocks;
            value.confirmationMode = this.confirmationMode;
            value.sourceMode = this.sourceMode;
            value.statId = this.statId;
            value.version = this.version;
            return value;
        }
    }

    public static final class Proyecto
    {
        public int id;
        public int ownerUserId;
        public String name;
        public boolean enabled;
        public Movimiento movement;
    }

    public static final class Sala
    {
        public int roomId;
        public int rpgId;
        public String roomName;
        public int overrideMask;
        public int movementVersion;
    }

    public static final class StatDefinition
    {
        public int id;
        public int rpgId;
        public String key;
        public String name;
        public double defaultValue;
        public boolean enabled;
        public int version;
    }

    public static final class StatValue
    {
        public int statId;
        public int rpgId;
        public int userId;
        public String key;
        public String name;
        public double baseValue;
        public double additiveTotal;
        public double multiplierTotal;
        public double effectiveValue;
        public int version;
    }

    public static final class Contexto
    {
        public int roomId;
        public int rpgId;
        public int ownerUserId;
        public String rpgName;
        public int globalVersion;
        public int roomVersion;
        public int overrideMask;
        public Movimiento movement;

        public int actorUserId;
        public double movementBudget;
        public StatValue movementStat;
    }


    public static final class EncounterConfig
    {
        public int rpgId;
        public int reconnectGraceSeconds = 300;
        public int returnGraceSeconds = 600;
        public int version = 1;
    }

    public static final class EncounterParticipant
    {
        public int encounterId;
        public int userId;
        public int joinSequence;
        public String entryType;
        public int entryByUserId;
        public String status;
        public String exitReason;
        public long reconnectDeadlineEpoch;
        public long returnDeadlineEpoch;
        public boolean hasSavedPosition;
        public int savedX;
        public int savedY;
        public double savedZ;
    }

    public static final class EncounterReservation
    {
        public int encounterId;
        public int userId;
        public String status;
        public int x;
        public int y;
        public double z;
    }

    public static final class Encounter
    {
        public int id;
        public int rpgId;
        public int roomId;
        public int createdByUserId;
        public int initiatorUserId;
        public String creationMode;
        public String status;
        public int version;
        public long createdAtEpoch;
        public long startedAtEpoch;
        public long endedAtEpoch;
        public List<EncounterParticipant> participants = new ArrayList<EncounterParticipant>();
        public List<EncounterReservation> reservations = new ArrayList<EncounterReservation>();
    }

    private static Connection connection() throws SQLException
    {
        return Emulator.getDatabase().getDataSource().getConnection();
    }

    private static String normalizeName(String value) throws RpgEngineException
    {
        String name = value == null ? "" : value.trim();

        if(name.length() < 3 || name.length() > 80)
        {
            throw new RpgEngineException("invalid-project-name");
        }

        return name;
    }

    private static String normalizeResource(String value) throws RpgEngineException
    {
        String name = value == null ? "" : value.trim();

        if(name.length() < 1 || name.length() > 24)
        {
            throw new RpgEngineException("invalid-resource-name");
        }

        return name;
    }

    private static String normalizeStatKey(String value) throws RpgEngineException
    {
        String key = value == null ? "" : value.trim().toUpperCase(Locale.ROOT);

        if(key.length() < 1 || key.length() > 24 || !key.matches("[A-Z0-9_]+"))
            throw new RpgEngineException("invalid-stat-key");

        return key;
    }

    private static String normalizeStatName(String value) throws RpgEngineException
    {
        String name = value == null ? "" : value.trim();

        if(name.length() < 1 || name.length() > 80)
            throw new RpgEngineException("invalid-stat-name");

        return name;
    }

    private static String normalizeSourcePart(String value, int max, String error) throws RpgEngineException
    {
        String normalized = value == null ? "" : value.trim();

        if(normalized.length() < 1 || normalized.length() > max)
            throw new RpgEngineException(error);

        return normalized;
    }

    private static double validateFinite(double value, double min, double max, String error) throws RpgEngineException
    {
        if(Double.isNaN(value) || Double.isInfinite(value) || value < min || value > max)
            throw new RpgEngineException(error);

        return value;
    }

    private static double roundMovementPoints(double value)
    {
        if(Double.isNaN(value) || Double.isInfinite(value)) return 0.0;

        // RPGEngine rule: positive .5 values always round upward.
        return Math.floor(Math.max(0.0, value) + 0.5);
    }

    public static void validateMovement(Movimiento movement) throws RpgEngineException
    {
        movement.resourceName = normalizeResource(movement.resourceName);

        if(movement.baseMovement < 0 || movement.baseMovement > 100000)
            throw new RpgEngineException("invalid-base-movement");

        if(movement.directions != 4 && movement.directions != 8)
            throw new RpgEngineException("invalid-directions");

        if(movement.orthogonalCost <= 0 || movement.orthogonalCost > 100000)
            throw new RpgEngineException("invalid-orthogonal-cost");

        if(movement.diagonalCost <= 0 || movement.diagonalCost > 100000)
            throw new RpgEngineException("invalid-diagonal-cost");

        if(movement.maxStepHeight < 0 || movement.maxStepHeight > 100000)
            throw new RpgEngineException("invalid-step-height");

        if(!movement.furniturePolicy.equals("respect-walkability") &&
           !movement.furniturePolicy.equals("block-all") &&
           !movement.furniturePolicy.equals("ignore"))
            throw new RpgEngineException("invalid-furniture-policy");

        if(!movement.confirmationMode.equals("confirm") &&
           !movement.confirmationMode.equals("instant"))
            throw new RpgEngineException("invalid-confirmation-mode");

        if(!movement.sourceMode.equals("fixed") && !movement.sourceMode.equals("stat"))
            throw new RpgEngineException("invalid-movement-source");

        if(movement.sourceMode.equals("stat") && movement.statId <= 0)
            throw new RpgEngineException("movement-stat-required");
    }

    public static Proyecto createProject(int ownerUserId, String rawName) throws Exception
    {
        String name = normalizeName(rawName);

        try(Connection connection = connection())
        {
            connection.setAutoCommit(false);

            try(PreparedStatement project = connection.prepareStatement(
                    "INSERT INTO rpg_engine_projects (owner_user_id, name, enabled) VALUES (?, ?, 1)",
                    Statement.RETURN_GENERATED_KEYS))
            {
                project.setInt(1, ownerUserId);
                project.setString(2, name);
                project.executeUpdate();

                int rpgId;

                try(ResultSet keys = project.getGeneratedKeys())
                {
                    if(!keys.next()) throw new SQLException("No generated RPG id.");
                    rpgId = keys.getInt(1);
                }

                try(PreparedStatement movement = connection.prepareStatement(
                        "INSERT INTO rpg_engine_movement_config (rpg_id) VALUES (?)"))
                {
                    movement.setInt(1, rpgId);
                    movement.executeUpdate();
                }

                connection.commit();
                return getProject(rpgId);
            }
            catch(Exception error)
            {
                connection.rollback();
                throw error;
            }
            finally
            {
                connection.setAutoCommit(true);
            }
        }
    }

    public static boolean isProjectOwner(int userId, int rpgId) throws SQLException
    {
        try(Connection connection = connection();
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT 1 FROM rpg_engine_projects WHERE id = ? AND owner_user_id = ? AND enabled = 1 LIMIT 1"))
        {
            statement.setInt(1, rpgId);
            statement.setInt(2, userId);

            try(ResultSet result = statement.executeQuery())
            {
                return result.next();
            }
        }
    }

    private static void requireProjectOwner(int userId, int rpgId) throws Exception
    {
        if(!isProjectOwner(userId, rpgId))
            throw new RpgEngineException("project-owner-required");
    }

    private static Movimiento movementFromResult(ResultSet result, String prefix) throws SQLException
    {
        Movimiento movement = new Movimiento();
        movement.resourceName = result.getString(prefix + "resource_name");
        movement.baseMovement = result.getDouble(prefix + "base_movement");
        movement.directions = result.getInt(prefix + "directions");
        movement.orthogonalCost = result.getDouble(prefix + "orthogonal_cost");
        movement.diagonalCost = result.getDouble(prefix + "diagonal_cost");
        movement.allowCornerCutting = result.getBoolean(prefix + "allow_corner_cutting");
        movement.maxStepHeight = result.getDouble(prefix + "max_step_height");
        movement.allowFalling = result.getBoolean(prefix + "allow_falling");
        movement.furniturePolicy = result.getString(prefix + "furniture_policy");
        movement.unknownFurnitureBlocks = result.getBoolean(prefix + "unknown_furniture_blocks");
        movement.confirmationMode = result.getString(prefix + "confirmation_mode");
        movement.sourceMode = result.getString(prefix + "source_mode");

        int statId = result.getInt(prefix + "stat_id");
        movement.statId = result.wasNull() ? 0 : statId;

        movement.version = result.getInt(prefix + "version");
        return movement;
    }

    private static String movementSelect(String alias, String prefix)
    {
        return alias + ".resource_name AS " + prefix + "resource_name," +
                alias + ".base_movement AS " + prefix + "base_movement," +
                alias + ".directions AS " + prefix + "directions," +
                alias + ".orthogonal_cost AS " + prefix + "orthogonal_cost," +
                alias + ".diagonal_cost AS " + prefix + "diagonal_cost," +
                alias + ".allow_corner_cutting AS " + prefix + "allow_corner_cutting," +
                alias + ".max_step_height AS " + prefix + "max_step_height," +
                alias + ".allow_falling AS " + prefix + "allow_falling," +
                alias + ".furniture_policy AS " + prefix + "furniture_policy," +
                alias + ".unknown_furniture_blocks AS " + prefix + "unknown_furniture_blocks," +
                alias + ".confirmation_mode AS " + prefix + "confirmation_mode," +
                alias + ".source_mode AS " + prefix + "source_mode," +
                alias + ".stat_id AS " + prefix + "stat_id," +
                alias + ".version AS " + prefix + "version ";
    }

    public static Proyecto getProject(int rpgId) throws SQLException
    {
        String sql =
                "SELECT p.id, p.owner_user_id, p.name, p.enabled," +
                movementSelect("m", "m_") +
                "FROM rpg_engine_projects p " +
                "JOIN rpg_engine_movement_config m ON m.rpg_id = p.id " +
                "WHERE p.id = ? LIMIT 1";

        try(Connection connection = connection();
            PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setInt(1, rpgId);

            try(ResultSet result = statement.executeQuery())
            {
                if(!result.next()) return null;

                Proyecto project = new Proyecto();
                project.id = result.getInt("id");
                project.ownerUserId = result.getInt("owner_user_id");
                project.name = result.getString("name");
                project.enabled = result.getBoolean("enabled");
                project.movement = movementFromResult(result, "m_");
                return project;
            }
        }
    }

    public static List<Proyecto> listProjects(int ownerUserId) throws SQLException
    {
        List<Proyecto> projects = new ArrayList<Proyecto>();

        try(Connection connection = connection();
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT id FROM rpg_engine_projects WHERE owner_user_id = ? AND enabled = 1 ORDER BY id ASC"))
        {
            statement.setInt(1, ownerUserId);

            try(ResultSet result = statement.executeQuery())
            {
                while(result.next())
                {
                    Proyecto project = getProject(result.getInt("id"));
                    if(project != null) projects.add(project);
                }
            }
        }

        return projects;
    }

    public static void registerRoom(int userId, int rpgId, Habbo habbo, Room room) throws Exception
    {
        requireProjectOwner(userId, rpgId);

        if(room == null) throw new RpgEngineException("not-in-room");
        if(!room.isOwner(habbo)) throw new RpgEngineException("room-owner-required");

        try(Connection connection = connection();
            PreparedStatement check = connection.prepareStatement(
                    "SELECT rpg_id FROM rpg_engine_rooms WHERE room_id = ? LIMIT 1"))
        {
            check.setInt(1, room.getId());

            try(ResultSet result = check.executeQuery())
            {
                if(result.next())
                {
                    int existing = result.getInt("rpg_id");

                    if(existing == rpgId) return;

                    throw new RpgEngineException("room-already-assigned");
                }
            }
        }

        try(Connection connection = connection();
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO rpg_engine_rooms (room_id, rpg_id, enabled) VALUES (?, ?, 1)"))
        {
            statement.setInt(1, room.getId());
            statement.setInt(2, rpgId);
            statement.executeUpdate();
        }
    }

    public static void removeRoom(int userId, int rpgId, Habbo habbo, Room room) throws Exception
    {
        requireProjectOwner(userId, rpgId);

        if(room == null) throw new RpgEngineException("not-in-room");
        if(!room.isOwner(habbo)) throw new RpgEngineException("room-owner-required");

        try(Connection connection = connection();
            PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM rpg_engine_rooms WHERE room_id = ? AND rpg_id = ?"))
        {
            statement.setInt(1, room.getId());
            statement.setInt(2, rpgId);

            if(statement.executeUpdate() == 0)
                throw new RpgEngineException("room-not-registered");
        }
    }

    public static List<Sala> listRooms(int userId, int rpgId) throws Exception
    {
        requireProjectOwner(userId, rpgId);

        List<Sala> rooms = new ArrayList<Sala>();

        String sql =
                "SELECT rr.room_id, rr.rpg_id, rr.movement_version, r.name AS room_name," +
                "rr.movement_resource_name, rr.movement_base_movement, rr.movement_directions," +
                "rr.movement_orthogonal_cost, rr.movement_diagonal_cost, rr.movement_allow_corner_cutting," +
                "rr.movement_max_step_height, rr.movement_allow_falling, rr.movement_furniture_policy," +
                "rr.movement_unknown_furniture_blocks, rr.movement_confirmation_mode " +
                "FROM rpg_engine_rooms rr LEFT JOIN rooms r ON r.id = rr.room_id " +
                "WHERE rr.rpg_id = ? AND rr.enabled = 1 ORDER BY rr.room_id ASC";

        try(Connection connection = connection();
            PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setInt(1, rpgId);

            try(ResultSet result = statement.executeQuery())
            {
                while(result.next())
                {
                    Sala room = new Sala();
                    room.roomId = result.getInt("room_id");
                    room.rpgId = result.getInt("rpg_id");
                    room.roomName = result.getString("room_name");
                    if(room.roomName == null) room.roomName = "#" + room.roomId;
                    room.movementVersion = result.getInt("movement_version");
                    room.overrideMask = overrideMask(result, "movement_");
                    rooms.add(room);
                }
            }
        }

        return rooms;
    }

    private static int overrideMask(ResultSet result, String prefix) throws SQLException
    {
        int mask = 0;

        if(result.getObject(prefix + "resource_name") != null) mask |= OV_RESOURCE;
        if(result.getObject(prefix + "base_movement") != null) mask |= OV_BASE;
        if(result.getObject(prefix + "directions") != null) mask |= OV_DIRECTIONS;
        if(result.getObject(prefix + "orthogonal_cost") != null) mask |= OV_ORTHOGONAL;
        if(result.getObject(prefix + "diagonal_cost") != null) mask |= OV_DIAGONAL;
        if(result.getObject(prefix + "allow_corner_cutting") != null) mask |= OV_CORNERS;
        if(result.getObject(prefix + "max_step_height") != null) mask |= OV_STEP_HEIGHT;
        if(result.getObject(prefix + "allow_falling") != null) mask |= OV_FALLING;
        if(result.getObject(prefix + "furniture_policy") != null) mask |= OV_FURNITURE;
        if(result.getObject(prefix + "unknown_furniture_blocks") != null) mask |= OV_UNKNOWN_BLOCKS;
        if(result.getObject(prefix + "confirmation_mode") != null) mask |= OV_CONFIRMATION;

        return mask;
    }

    public static StatDefinition getStat(int rpgId, int statId) throws SQLException
    {
        String sql =
                "SELECT id, rpg_id, stat_key, name, default_value, enabled, version " +
                "FROM rpg_engine_stats WHERE id=? AND rpg_id=? AND enabled=1 LIMIT 1";

        try(Connection connection = connection();
            PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setInt(1, statId);
            statement.setInt(2, rpgId);

            try(ResultSet result = statement.executeQuery())
            {
                if(!result.next()) return null;

                StatDefinition stat = new StatDefinition();
                stat.id = result.getInt("id");
                stat.rpgId = result.getInt("rpg_id");
                stat.key = result.getString("stat_key");
                stat.name = result.getString("name");
                stat.defaultValue = result.getDouble("default_value");
                stat.enabled = result.getBoolean("enabled");
                stat.version = result.getInt("version");
                return stat;
            }
        }
    }

    private static StatDefinition requireStat(int rpgId, int statId) throws Exception
    {
        StatDefinition stat = getStat(rpgId, statId);

        if(stat == null) throw new RpgEngineException("stat-not-found");

        return stat;
    }

    public static StatDefinition createStat(
            int ownerUserId,
            int rpgId,
            String rawKey,
            String rawName,
            double defaultValue) throws Exception
    {
        requireProjectOwner(ownerUserId, rpgId);

        String key = normalizeStatKey(rawKey);
        String name = normalizeStatName(rawName);
        validateFinite(defaultValue, -1000000.0, 1000000.0, "invalid-stat-value");

        try(Connection connection = connection();
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO rpg_engine_stats (rpg_id, stat_key, name, default_value, enabled) " +
                    "VALUES (?, ?, ?, ?, 1)",
                    Statement.RETURN_GENERATED_KEYS))
        {
            statement.setInt(1, rpgId);
            statement.setString(2, key);
            statement.setString(3, name);
            statement.setDouble(4, defaultValue);

            try
            {
                statement.executeUpdate();
            }
            catch(SQLIntegrityConstraintViolationException duplicate)
            {
                throw new RpgEngineException("stat-key-exists");
            }

            try(ResultSet keys = statement.getGeneratedKeys())
            {
                if(!keys.next()) throw new SQLException("No generated stat id.");
                return getStat(rpgId, keys.getInt(1));
            }
        }
    }

    public static List<StatDefinition> listStats(int ownerUserId, int rpgId) throws Exception
    {
        requireProjectOwner(ownerUserId, rpgId);

        List<StatDefinition> stats = new ArrayList<StatDefinition>();

        try(Connection connection = connection();
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT id FROM rpg_engine_stats WHERE rpg_id=? AND enabled=1 ORDER BY id ASC"))
        {
            statement.setInt(1, rpgId);

            try(ResultSet result = statement.executeQuery())
            {
                while(result.next())
                {
                    StatDefinition stat = getStat(rpgId, result.getInt("id"));
                    if(stat != null) stats.add(stat);
                }
            }
        }

        return stats;
    }

    private static void ensurePlayerStatRow(Connection connection, int rpgId, int userId, StatDefinition stat)
            throws SQLException
    {
        try(PreparedStatement statement = connection.prepareStatement(
                "INSERT IGNORE INTO rpg_engine_player_stats " +
                "(rpg_id, user_id, stat_id, base_value, version) VALUES (?, ?, ?, ?, 1)"))
        {
            statement.setInt(1, rpgId);
            statement.setInt(2, userId);
            statement.setInt(3, stat.id);
            statement.setDouble(4, stat.defaultValue);
            statement.executeUpdate();
        }
    }

    private static void bumpPlayerStatVersion(Connection connection, int rpgId, int userId, int statId)
            throws SQLException
    {
        try(PreparedStatement statement = connection.prepareStatement(
                "UPDATE rpg_engine_player_stats SET version=version+1 " +
                "WHERE rpg_id=? AND user_id=? AND stat_id=?"))
        {
            statement.setInt(1, rpgId);
            statement.setInt(2, userId);
            statement.setInt(3, statId);
            statement.executeUpdate();
        }
    }

    public static StatValue setPlayerStatBase(
            int ownerUserId,
            int rpgId,
            int targetUserId,
            int statId,
            double baseValue) throws Exception
    {
        requireProjectOwner(ownerUserId, rpgId);
        StatDefinition stat = requireStat(rpgId, statId);
        validateFinite(baseValue, -1000000.0, 1000000.0, "invalid-stat-value");

        try(Connection connection = connection())
        {
            ensurePlayerStatRow(connection, rpgId, targetUserId, stat);

            try(PreparedStatement statement = connection.prepareStatement(
                    "UPDATE rpg_engine_player_stats SET base_value=?, version=version+1 " +
                    "WHERE rpg_id=? AND user_id=? AND stat_id=?"))
            {
                statement.setDouble(1, baseValue);
                statement.setInt(2, rpgId);
                statement.setInt(3, targetUserId);
                statement.setInt(4, statId);
                statement.executeUpdate();
            }
        }

        StatValue resolvedValue = resolveStat(rpgId, targetUserId, statId);
        RpgEngineRealtime.pushStatContext(rpgId, targetUserId, resolvedValue);
        return resolvedValue;
    }

    public static StatValue upsertStatModifier(
            int ownerUserId,
            int rpgId,
            int targetUserId,
            int statId,
            String rawSourceType,
            String rawSourceKey,
            String rawOperation,
            double value) throws Exception
    {
        requireProjectOwner(ownerUserId, rpgId);
        StatDefinition stat = requireStat(rpgId, statId);

        String sourceType = normalizeSourcePart(rawSourceType, 24, "invalid-modifier-source-type");
        String sourceKey = normalizeSourcePart(rawSourceKey, 80, "invalid-modifier-source-key");
        String operation = rawOperation == null ? "" : rawOperation.trim().toLowerCase(Locale.ROOT);

        if(!operation.equals("add") && !operation.equals("multiply"))
            throw new RpgEngineException("invalid-modifier-operation");

        if(operation.equals("multiply"))
            validateFinite(value, 0.0, 1000.0, "invalid-modifier-value");
        else
            validateFinite(value, -1000000.0, 1000000.0, "invalid-modifier-value");

        try(Connection connection = connection())
        {
            connection.setAutoCommit(false);

            try
            {
                ensurePlayerStatRow(connection, rpgId, targetUserId, stat);

                try(PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO rpg_engine_stat_modifiers " +
                        "(rpg_id, user_id, stat_id, source_type, source_key, operation, value, enabled) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, 1) " +
                        "ON DUPLICATE KEY UPDATE operation=VALUES(operation), value=VALUES(value), enabled=1"))
                {
                    statement.setInt(1, rpgId);
                    statement.setInt(2, targetUserId);
                    statement.setInt(3, statId);
                    statement.setString(4, sourceType);
                    statement.setString(5, sourceKey);
                    statement.setString(6, operation);
                    statement.setDouble(7, value);
                    statement.executeUpdate();
                }

                bumpPlayerStatVersion(connection, rpgId, targetUserId, statId);
                connection.commit();
            }
            catch(Exception error)
            {
                connection.rollback();
                throw error;
            }
            finally
            {
                connection.setAutoCommit(true);
            }
        }

        StatValue resolvedValue = resolveStat(rpgId, targetUserId, statId);
        RpgEngineRealtime.pushStatContext(rpgId, targetUserId, resolvedValue);
        return resolvedValue;
    }

    public static StatValue removeStatModifier(
            int ownerUserId,
            int rpgId,
            int targetUserId,
            int statId,
            String rawSourceType,
            String rawSourceKey) throws Exception
    {
        requireProjectOwner(ownerUserId, rpgId);
        StatDefinition stat = requireStat(rpgId, statId);

        String sourceType = normalizeSourcePart(rawSourceType, 24, "invalid-modifier-source-type");
        String sourceKey = normalizeSourcePart(rawSourceKey, 80, "invalid-modifier-source-key");

        try(Connection connection = connection())
        {
            connection.setAutoCommit(false);

            try
            {
                ensurePlayerStatRow(connection, rpgId, targetUserId, stat);

                int removed;

                try(PreparedStatement statement = connection.prepareStatement(
                        "DELETE FROM rpg_engine_stat_modifiers " +
                        "WHERE rpg_id=? AND user_id=? AND stat_id=? AND source_type=? AND source_key=?"))
                {
                    statement.setInt(1, rpgId);
                    statement.setInt(2, targetUserId);
                    statement.setInt(3, statId);
                    statement.setString(4, sourceType);
                    statement.setString(5, sourceKey);
                    removed = statement.executeUpdate();
                }

                if(removed > 0)
                    bumpPlayerStatVersion(connection, rpgId, targetUserId, statId);

                connection.commit();
            }
            catch(Exception error)
            {
                connection.rollback();
                throw error;
            }
            finally
            {
                connection.setAutoCommit(true);
            }
        }

        StatValue resolvedValue = resolveStat(rpgId, targetUserId, statId);
        RpgEngineRealtime.pushStatContext(rpgId, targetUserId, resolvedValue);
        return resolvedValue;
    }

    public static StatValue resolveStat(int rpgId, int userId, int statId) throws Exception
    {
        StatDefinition stat = requireStat(rpgId, statId);

        double baseValue = stat.defaultValue;
        int version = 0;

        try(Connection connection = connection())
        {
            try(PreparedStatement statement = connection.prepareStatement(
                    "SELECT base_value, version FROM rpg_engine_player_stats " +
                    "WHERE rpg_id=? AND user_id=? AND stat_id=? LIMIT 1"))
            {
                statement.setInt(1, rpgId);
                statement.setInt(2, userId);
                statement.setInt(3, statId);

                try(ResultSet result = statement.executeQuery())
                {
                    if(result.next())
                    {
                        baseValue = result.getDouble("base_value");
                        version = result.getInt("version");
                    }
                }
            }

            double additiveTotal = 0.0;
            double multiplierTotal = 1.0;

            try(PreparedStatement statement = connection.prepareStatement(
                    "SELECT operation, value FROM rpg_engine_stat_modifiers " +
                    "WHERE rpg_id=? AND user_id=? AND stat_id=? AND enabled=1 ORDER BY id ASC"))
            {
                statement.setInt(1, rpgId);
                statement.setInt(2, userId);
                statement.setInt(3, statId);

                try(ResultSet result = statement.executeQuery())
                {
                    while(result.next())
                    {
                        String operation = result.getString("operation");
                        double modifierValue = result.getDouble("value");

                        if("add".equals(operation))
                            additiveTotal += modifierValue;
                        else if("multiply".equals(operation))
                            multiplierTotal *= modifierValue;
                    }
                }
            }

            StatValue value = new StatValue();
            value.statId = stat.id;
            value.rpgId = rpgId;
            value.userId = userId;
            value.key = stat.key;
            value.name = stat.name;
            value.baseValue = baseValue;
            value.additiveTotal = additiveTotal;
            value.multiplierTotal = multiplierTotal;
            value.effectiveValue = (baseValue + additiveTotal) * multiplierTotal;
            value.version = version;
            return value;
        }
    }

    public static StatValue getStatValueForOwner(
            int ownerUserId,
            int rpgId,
            int targetUserId,
            int statId) throws Exception
    {
        requireProjectOwner(ownerUserId, rpgId);
        return resolveStat(rpgId, targetUserId, statId);
    }

    public static Proyecto bindMovementStat(int ownerUserId, int rpgId, int statId) throws Exception
    {
        requireProjectOwner(ownerUserId, rpgId);
        StatDefinition stat = requireStat(rpgId, statId);

        try(Connection connection = connection();
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE rpg_engine_movement_config SET source_mode='stat', stat_id=?, resource_name=?, " +
                    "version=version+1 WHERE rpg_id=?"))
        {
            statement.setInt(1, statId);
            statement.setString(2, stat.key);
            statement.setInt(3, rpgId);

            if(statement.executeUpdate() == 0)
                throw new RpgEngineException("movement-config-not-found");
        }

        return getProject(rpgId);
    }

    public static Proyecto setMovementFixedSource(int ownerUserId, int rpgId, double value) throws Exception
    {
        requireProjectOwner(ownerUserId, rpgId);
        validateFinite(value, 0.0, 100000.0, "invalid-base-movement");

        try(Connection connection = connection();
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE rpg_engine_movement_config SET source_mode='fixed', stat_id=NULL, base_movement=?, " +
                    "version=version+1 WHERE rpg_id=?"))
        {
            statement.setDouble(1, value);
            statement.setInt(2, rpgId);

            if(statement.executeUpdate() == 0)
                throw new RpgEngineException("movement-config-not-found");
        }

        return getProject(rpgId);
    }

    public static Contexto getContext(int roomId, int actorUserId) throws Exception
    {
        String sql =
                "SELECT rr.room_id, rr.rpg_id, rr.movement_version," +
                "p.owner_user_id, p.name AS rpg_name," +
                movementSelect("m", "g_") +
                ",rr.movement_resource_name, rr.movement_base_movement, rr.movement_directions," +
                "rr.movement_orthogonal_cost, rr.movement_diagonal_cost, rr.movement_allow_corner_cutting," +
                "rr.movement_max_step_height, rr.movement_allow_falling, rr.movement_furniture_policy," +
                "rr.movement_unknown_furniture_blocks, rr.movement_confirmation_mode " +
                "FROM rpg_engine_rooms rr " +
                "JOIN rpg_engine_projects p ON p.id = rr.rpg_id " +
                "JOIN rpg_engine_movement_config m ON m.rpg_id = rr.rpg_id " +
                "WHERE rr.room_id = ? AND rr.enabled = 1 AND p.enabled = 1 LIMIT 1";

        try(Connection connection = connection();
            PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setInt(1, roomId);

            try(ResultSet result = statement.executeQuery())
            {
                if(!result.next()) return null;

                Movimiento effective = movementFromResult(result, "g_");
                int mask = overrideMask(result, "movement_");

                if((mask & OV_RESOURCE) != 0) effective.resourceName = result.getString("movement_resource_name");
                if((mask & OV_BASE) != 0) effective.baseMovement = result.getDouble("movement_base_movement");
                if((mask & OV_DIRECTIONS) != 0) effective.directions = result.getInt("movement_directions");
                if((mask & OV_ORTHOGONAL) != 0) effective.orthogonalCost = result.getDouble("movement_orthogonal_cost");
                if((mask & OV_DIAGONAL) != 0) effective.diagonalCost = result.getDouble("movement_diagonal_cost");
                if((mask & OV_CORNERS) != 0) effective.allowCornerCutting = result.getBoolean("movement_allow_corner_cutting");
                if((mask & OV_STEP_HEIGHT) != 0) effective.maxStepHeight = result.getDouble("movement_max_step_height");
                if((mask & OV_FALLING) != 0) effective.allowFalling = result.getBoolean("movement_allow_falling");
                if((mask & OV_FURNITURE) != 0) effective.furniturePolicy = result.getString("movement_furniture_policy");
                if((mask & OV_UNKNOWN_BLOCKS) != 0) effective.unknownFurnitureBlocks = result.getBoolean("movement_unknown_furniture_blocks");
                if((mask & OV_CONFIRMATION) != 0) effective.confirmationMode = result.getString("movement_confirmation_mode");

                validateMovement(effective);

                Contexto context = new Contexto();
                context.roomId = result.getInt("room_id");
                context.rpgId = result.getInt("rpg_id");
                context.ownerUserId = result.getInt("owner_user_id");
                context.rpgName = result.getString("rpg_name");
                context.globalVersion = result.getInt("g_version");
                context.roomVersion = result.getInt("movement_version");
                context.overrideMask = mask;
                context.movement = effective;
                context.actorUserId = actorUserId;

                if("stat".equals(effective.sourceMode))
                {
                    context.movementStat = resolveStat(context.rpgId, actorUserId, effective.statId);
                    context.movementBudget = roundMovementPoints(context.movementStat.effectiveValue);
                }
                else
                {
                    context.movementStat = null;
                    context.movementBudget = roundMovementPoints(effective.baseMovement);
                }

                return context;
            }
        }
    }

    public static Proyecto setGlobalMovement(int userId, int rpgId, Movimiento movement) throws Exception
    {
        requireProjectOwner(userId, rpgId);
        validateMovement(movement);

        if("stat".equals(movement.sourceMode))
            requireStat(rpgId, movement.statId);

        String sql =
                "UPDATE rpg_engine_movement_config SET " +
                "resource_name=?, base_movement=?, directions=?, orthogonal_cost=?, diagonal_cost=?," +
                "allow_corner_cutting=?, max_step_height=?, allow_falling=?, furniture_policy=?," +
                "unknown_furniture_blocks=?, confirmation_mode=?, source_mode=?, stat_id=?, version=version+1 " +
                "WHERE rpg_id=?";

        try(Connection connection = connection();
            PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setString(1, movement.resourceName);
            statement.setDouble(2, movement.baseMovement);
            statement.setInt(3, movement.directions);
            statement.setDouble(4, movement.orthogonalCost);
            statement.setDouble(5, movement.diagonalCost);
            statement.setBoolean(6, movement.allowCornerCutting);
            statement.setDouble(7, movement.maxStepHeight);
            statement.setBoolean(8, movement.allowFalling);
            statement.setString(9, movement.furniturePolicy);
            statement.setBoolean(10, movement.unknownFurnitureBlocks);
            statement.setString(11, movement.confirmationMode);
            statement.setString(12, movement.sourceMode);

            if(movement.statId > 0) statement.setInt(13, movement.statId);
            else statement.setNull(13, Types.INTEGER);

            statement.setInt(14, rpgId);

            if(statement.executeUpdate() == 0)
                throw new RpgEngineException("movement-config-not-found");
        }

        return getProject(rpgId);
    }

    private static void setNullableString(PreparedStatement statement, int index, boolean enabled, String value) throws SQLException
    {
        if(enabled) statement.setString(index, value);
        else statement.setNull(index, Types.VARCHAR);
    }

    private static void setNullableDouble(PreparedStatement statement, int index, boolean enabled, double value) throws SQLException
    {
        if(enabled) statement.setDouble(index, value);
        else statement.setNull(index, Types.DECIMAL);
    }

    private static void setNullableInt(PreparedStatement statement, int index, boolean enabled, int value) throws SQLException
    {
        if(enabled) statement.setInt(index, value);
        else statement.setNull(index, Types.INTEGER);
    }

    private static void setNullableBoolean(PreparedStatement statement, int index, boolean enabled, boolean value) throws SQLException
    {
        if(enabled) statement.setBoolean(index, value);
        else statement.setNull(index, Types.TINYINT);
    }

    public static Contexto setRoomOverrides(
            int userId,
            int rpgId,
            Habbo habbo,
            Room room,
            int mask,
            Movimiento values) throws Exception
    {
        requireProjectOwner(userId, rpgId);

        if(room == null) throw new RpgEngineException("not-in-room");
        if(!room.isOwner(habbo)) throw new RpgEngineException("room-owner-required");

        validateMovement(values);

        String sql =
                "UPDATE rpg_engine_rooms SET " +
                "movement_resource_name=?, movement_base_movement=?, movement_directions=?," +
                "movement_orthogonal_cost=?, movement_diagonal_cost=?, movement_allow_corner_cutting=?," +
                "movement_max_step_height=?, movement_allow_falling=?, movement_furniture_policy=?," +
                "movement_unknown_furniture_blocks=?, movement_confirmation_mode=?," +
                "movement_version=movement_version+1 " +
                "WHERE room_id=? AND rpg_id=?";

        try(Connection connection = connection();
            PreparedStatement statement = connection.prepareStatement(sql))
        {
            setNullableString(statement, 1, (mask & OV_RESOURCE) != 0, values.resourceName);
            setNullableDouble(statement, 2, (mask & OV_BASE) != 0, values.baseMovement);
            setNullableInt(statement, 3, (mask & OV_DIRECTIONS) != 0, values.directions);
            setNullableDouble(statement, 4, (mask & OV_ORTHOGONAL) != 0, values.orthogonalCost);
            setNullableDouble(statement, 5, (mask & OV_DIAGONAL) != 0, values.diagonalCost);
            setNullableBoolean(statement, 6, (mask & OV_CORNERS) != 0, values.allowCornerCutting);
            setNullableDouble(statement, 7, (mask & OV_STEP_HEIGHT) != 0, values.maxStepHeight);
            setNullableBoolean(statement, 8, (mask & OV_FALLING) != 0, values.allowFalling);
            setNullableString(statement, 9, (mask & OV_FURNITURE) != 0, values.furniturePolicy);
            setNullableBoolean(statement, 10, (mask & OV_UNKNOWN_BLOCKS) != 0, values.unknownFurnitureBlocks);
            setNullableString(statement, 11, (mask & OV_CONFIRMATION) != 0, values.confirmationMode);
            statement.setInt(12, room.getId());
            statement.setInt(13, rpgId);

            if(statement.executeUpdate() == 0)
                throw new RpgEngineException("room-not-registered");
        }

        return getContext(room.getId(), userId);
    }

    public static Contexto clearRoomOverrides(
            int userId,
            int rpgId,
            Habbo habbo,
            Room room) throws Exception
    {
        Proyecto project = getProject(rpgId);

        if(project == null) throw new RpgEngineException("project-not-found");

        return setRoomOverrides(userId, rpgId, habbo, room, 0, project.movement.copy());
    }

    private static long epochSeconds(Timestamp timestamp)
    {
        return timestamp == null ? 0L : timestamp.getTime() / 1000L;
    }

    private static int validateGraceSeconds(int value) throws RpgEngineException
    {
        if(value < 0 || value > 604800)
            throw new RpgEngineException("invalid-grace-seconds");

        return value;
    }

    private static void ensureEncounterConfigRow(Connection connection, int rpgId) throws SQLException
    {
        try(PreparedStatement statement = connection.prepareStatement(
                "INSERT IGNORE INTO rpg_engine_encounter_config " +
                "(rpg_id, reconnect_grace_seconds, return_grace_seconds, version) VALUES (?, 300, 600, 1)"))
        {
            statement.setInt(1, rpgId);
            statement.executeUpdate();
        }
    }

    public static EncounterConfig getEncounterConfig(int userId, int rpgId) throws Exception
    {
        requireProjectOwner(userId, rpgId);

        try(Connection connection = connection())
        {
            ensureEncounterConfigRow(connection, rpgId);

            try(PreparedStatement statement = connection.prepareStatement(
                    "SELECT rpg_id, reconnect_grace_seconds, return_grace_seconds, version " +
                    "FROM rpg_engine_encounter_config WHERE rpg_id=? LIMIT 1"))
            {
                statement.setInt(1, rpgId);

                try(ResultSet result = statement.executeQuery())
                {
                    if(!result.next())
                        throw new RpgEngineException("encounter-config-not-found");

                    EncounterConfig config = new EncounterConfig();
                    config.rpgId = result.getInt("rpg_id");
                    config.reconnectGraceSeconds = result.getInt("reconnect_grace_seconds");
                    config.returnGraceSeconds = result.getInt("return_grace_seconds");
                    config.version = result.getInt("version");
                    return config;
                }
            }
        }
    }

    public static EncounterConfig setEncounterConfig(
            int userId,
            int rpgId,
            int reconnectGraceSeconds,
            int returnGraceSeconds) throws Exception
    {
        requireProjectOwner(userId, rpgId);

        reconnectGraceSeconds = validateGraceSeconds(reconnectGraceSeconds);
        returnGraceSeconds = validateGraceSeconds(returnGraceSeconds);

        try(Connection connection = connection())
        {
            ensureEncounterConfigRow(connection, rpgId);

            try(PreparedStatement statement = connection.prepareStatement(
                    "UPDATE rpg_engine_encounter_config SET " +
                    "reconnect_grace_seconds=?, return_grace_seconds=?, version=version+1 " +
                    "WHERE rpg_id=?"))
            {
                statement.setInt(1, reconnectGraceSeconds);
                statement.setInt(2, returnGraceSeconds);
                statement.setInt(3, rpgId);
                statement.executeUpdate();
            }
        }

        return getEncounterConfig(userId, rpgId);
    }

    private static EncounterParticipant participantFromResult(ResultSet result) throws SQLException
    {
        EncounterParticipant participant = new EncounterParticipant();
        participant.encounterId = result.getInt("encounter_id");
        participant.userId = result.getInt("user_id");
        participant.joinSequence = result.getInt("join_sequence");
        participant.entryType = result.getString("entry_type");

        int entryByUserId = result.getInt("entry_by_user_id");
        participant.entryByUserId = result.wasNull() ? 0 : entryByUserId;

        participant.status = result.getString("status");
        participant.exitReason = result.getString("exit_reason");
        participant.reconnectDeadlineEpoch = epochSeconds(result.getTimestamp("reconnect_deadline"));
        participant.returnDeadlineEpoch = epochSeconds(result.getTimestamp("return_deadline"));

        int savedX = result.getInt("saved_x");
        boolean xNull = result.wasNull();
        int savedY = result.getInt("saved_y");
        boolean yNull = result.wasNull();
        double savedZ = result.getDouble("saved_z");
        boolean zNull = result.wasNull();

        participant.hasSavedPosition = !xNull && !yNull;
        participant.savedX = participant.hasSavedPosition ? savedX : 0;
        participant.savedY = participant.hasSavedPosition ? savedY : 0;
        participant.savedZ = (!zNull && participant.hasSavedPosition) ? savedZ : 0.0;

        return participant;
    }

    private static List<EncounterParticipant> listEncounterParticipantsInternal(
            Connection connection,
            int encounterId) throws SQLException
    {
        List<EncounterParticipant> participants = new ArrayList<EncounterParticipant>();

        try(PreparedStatement statement = connection.prepareStatement(
                "SELECT encounter_id, user_id, join_sequence, entry_type, entry_by_user_id, " +
                "status, exit_reason, reconnect_deadline, return_deadline, saved_x, saved_y, saved_z " +
                "FROM rpg_engine_encounter_participants WHERE encounter_id=? " +
                "ORDER BY join_sequence ASC, joined_at ASC, user_id ASC"))
        {
            statement.setInt(1, encounterId);

            try(ResultSet result = statement.executeQuery())
            {
                while(result.next())
                    participants.add(participantFromResult(result));
            }
        }

        return participants;
    }

    private static List<EncounterReservation> listRoomReservationsInternal(
            Connection connection,
            int roomId) throws SQLException
    {
        List<EncounterReservation> reservations = new ArrayList<EncounterReservation>();

        try(PreparedStatement statement = connection.prepareStatement(
                "SELECT p.encounter_id, p.user_id, p.status, p.saved_x, p.saved_y, p.saved_z " +
                "FROM rpg_engine_encounter_participants p " +
                "INNER JOIN rpg_engine_encounters e ON e.id=p.encounter_id " +
                "WHERE e.status='active' AND e.room_id=? AND p.saved_x IS NOT NULL AND p.saved_y IS NOT NULL " +
                "AND ((p.status='disconnected' AND (p.reconnect_deadline IS NULL OR p.reconnect_deadline>CURRENT_TIMESTAMP)) " +
                "OR (p.status='returning' AND (p.return_deadline IS NULL OR p.return_deadline>CURRENT_TIMESTAMP))) " +
                "ORDER BY p.encounter_id ASC, p.join_sequence ASC, p.user_id ASC"))
        {
            statement.setInt(1, roomId);

            try(ResultSet result = statement.executeQuery())
            {
                while(result.next())
                {
                    EncounterReservation reservation = new EncounterReservation();
                    reservation.encounterId = result.getInt("encounter_id");
                    reservation.userId = result.getInt("user_id");
                    reservation.status = result.getString("status");
                    reservation.x = result.getInt("saved_x");
                    reservation.y = result.getInt("saved_y");

                    double z = result.getDouble("saved_z");
                    reservation.z = result.wasNull() ? 0.0 : z;
                    reservations.add(reservation);
                }
            }
        }

        return reservations;
    }

    private static void expireParticipantDeadlines(Connection connection, int encounterId) throws SQLException
    {
        try(PreparedStatement reconnect = connection.prepareStatement(
                "UPDATE rpg_engine_encounter_participants SET " +
                "status='left', exit_reason='reconnect-timeout', left_at=CURRENT_TIMESTAMP, " +
                "reconnect_deadline=NULL, return_deadline=NULL " +
                "WHERE encounter_id=? AND status='disconnected' AND reconnect_deadline IS NOT NULL " +
                "AND reconnect_deadline <= CURRENT_TIMESTAMP"))
        {
            reconnect.setInt(1, encounterId);
            reconnect.executeUpdate();
        }

        try(PreparedStatement returning = connection.prepareStatement(
                "UPDATE rpg_engine_encounter_participants SET " +
                "status='left', exit_reason='return-timeout', left_at=CURRENT_TIMESTAMP, " +
                "reconnect_deadline=NULL, return_deadline=NULL " +
                "WHERE encounter_id=? AND status='returning' AND return_deadline IS NOT NULL " +
                "AND return_deadline <= CURRENT_TIMESTAMP"))
        {
            returning.setInt(1, encounterId);
            returning.executeUpdate();
        }
    }

    private static Encounter encounterFromResult(Connection connection, ResultSet result) throws SQLException
    {
        Encounter encounter = new Encounter();
        encounter.id = result.getInt("id");
        encounter.rpgId = result.getInt("rpg_id");
        encounter.roomId = result.getInt("room_id");
        encounter.createdByUserId = result.getInt("created_by_user_id");

        int initiatorUserId = result.getInt("initiator_user_id");
        encounter.initiatorUserId = result.wasNull() ? 0 : initiatorUserId;
        encounter.creationMode = result.getString("creation_mode");

        encounter.status = result.getString("status");
        encounter.version = result.getInt("version");
        encounter.createdAtEpoch = epochSeconds(result.getTimestamp("created_at"));
        encounter.startedAtEpoch = epochSeconds(result.getTimestamp("started_at"));
        encounter.endedAtEpoch = epochSeconds(result.getTimestamp("ended_at"));
        encounter.participants = listEncounterParticipantsInternal(connection, encounter.id);
        encounter.reservations = listRoomReservationsInternal(connection, encounter.roomId);
        return encounter;
    }

    public static Encounter getEncounter(int encounterId) throws Exception
    {
        try(Connection connection = connection())
        {
            expireParticipantDeadlines(connection, encounterId);

            try(PreparedStatement statement = connection.prepareStatement(
                    "SELECT id, rpg_id, room_id, created_by_user_id, initiator_user_id, creation_mode, " +
                    "status, version, created_at, started_at, ended_at " +
                    "FROM rpg_engine_encounters WHERE id=? LIMIT 1"))
            {
                statement.setInt(1, encounterId);

                try(ResultSet result = statement.executeQuery())
                {
                    if(!result.next()) return null;
                    return encounterFromResult(connection, result);
                }
            }
        }
    }

    public static List<Encounter> getActiveEncountersInRoom(int roomId) throws Exception
    {
        List<Integer> ids = new ArrayList<Integer>();

        try(Connection connection = connection();
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT id FROM rpg_engine_encounters WHERE room_id=? AND status='active' ORDER BY id ASC"))
        {
            statement.setInt(1, roomId);

            try(ResultSet result = statement.executeQuery())
            {
                while(result.next())
                    ids.add(result.getInt("id"));
            }
        }

        List<Encounter> encounters = new ArrayList<Encounter>();

        for(Integer id : ids)
        {
            Encounter encounter = getEncounter(id.intValue());

            if(encounter != null && "active".equals(encounter.status))
                encounters.add(encounter);
        }

        return encounters;
    }

    private static Encounter requireEncounter(int encounterId) throws Exception
    {
        Encounter encounter = getEncounter(encounterId);

        if(encounter == null)
            throw new RpgEngineException("encounter-not-found");

        return encounter;
    }

    private static Encounter requireOwnedEncounter(int userId, int encounterId) throws Exception
    {
        Encounter encounter = requireEncounter(encounterId);
        requireProjectOwner(userId, encounter.rpgId);
        return encounter;
    }

    private static void requireEncounterOpen(Encounter encounter) throws RpgEngineException
    {
        if("ended".equals(encounter.status))
            throw new RpgEngineException("encounter-ended");
    }

    private static void requireRegisteredRoomForRpg(int rpgId, Room room) throws Exception
    {
        if(room == null)
            throw new RpgEngineException("not-in-room");

        try(Connection connection = connection();
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT 1 FROM rpg_engine_rooms WHERE room_id=? AND rpg_id=? AND enabled=1 LIMIT 1"))
        {
            statement.setInt(1, room.getId());
            statement.setInt(2, rpgId);

            try(ResultSet result = statement.executeQuery())
            {
                if(!result.next())
                    throw new RpgEngineException("room-not-registered");
            }
        }
    }


    private static EncounterParticipant findParticipant(Encounter encounter, int userId)
    {
        if(encounter == null || encounter.participants == null) return null;

        for(EncounterParticipant participant : encounter.participants)
        {
            if(participant.userId == userId)
                return participant;
        }

        return null;
    }

    private static int nextJoinSequence(Connection connection, int encounterId) throws SQLException
    {
        try(PreparedStatement statement = connection.prepareStatement(
                "SELECT COALESCE(MAX(join_sequence), 0) + 1 " +
                "FROM rpg_engine_encounter_participants WHERE encounter_id=?"))
        {
            statement.setInt(1, encounterId);

            try(ResultSet result = statement.executeQuery())
            {
                if(!result.next()) return 1;
                return Math.max(1, result.getInt(1));
            }
        }
    }

    private static void saveParticipantPosition(
            Connection connection,
            int encounterId,
            int userId,
            int x,
            int y,
            double z) throws SQLException
    {
        try(PreparedStatement statement = connection.prepareStatement(
                "UPDATE rpg_engine_encounter_participants SET saved_x=?, saved_y=?, saved_z=? " +
                "WHERE encounter_id=? AND user_id=?"))
        {
            statement.setInt(1, x);
            statement.setInt(2, y);
            statement.setDouble(3, z);
            statement.setInt(4, encounterId);
            statement.setInt(5, userId);
            statement.executeUpdate();
        }
    }

    private static void captureOnlineParticipantPosition(
            Connection connection,
            Encounter encounter,
            int targetUserId) throws SQLException
    {
        Habbo target = Emulator.getGameEnvironment().getHabboManager().getHabbo(targetUserId);

        if(target == null || target.getRoomUnit() == null ||
           target.getRoomUnit().getCurrentLocation() == null)
            return;

        Room room = target.getHabboInfo().getCurrentRoom();

        if(room == null || room.getId() != encounter.roomId)
            return;

        saveParticipantPosition(
                connection,
                encounter.id,
                targetUserId,
                target.getRoomUnit().getX(),
                target.getRoomUnit().getY(),
                target.getRoomUnit().getZ()
        );
    }

    private static void ensureParticipantNotInOtherOpenEncounter(
            Connection connection,
            int rpgId,
            int encounterId,
            int targetUserId) throws Exception
    {
        try(PreparedStatement existing = connection.prepareStatement(
                "SELECT e.id FROM rpg_engine_encounters e " +
                "INNER JOIN rpg_engine_encounter_participants p ON p.encounter_id=e.id " +
                "WHERE e.rpg_id=? AND e.status IN ('draft','active') AND e.id<>? " +
                "AND p.user_id=? AND p.status IN ('active','returning','disconnected') LIMIT 1"))
        {
            existing.setInt(1, rpgId);
            existing.setInt(2, encounterId);
            existing.setInt(3, targetUserId);

            try(ResultSet result = existing.executeQuery())
            {
                if(result.next())
                    throw new RpgEngineException("participant-already-in-encounter");
            }
        }
    }

    public static Encounter createEncounter(
            int userId,
            int rpgId,
            Room currentRoom) throws Exception
    {
        requireProjectOwner(userId, rpgId);
        requireRegisteredRoomForRpg(rpgId, currentRoom);

        int encounterId;

        try(Connection connection = connection();
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO rpg_engine_encounters " +
                    "(rpg_id, room_id, created_by_user_id, initiator_user_id, creation_mode, status, version) " +
                    "VALUES (?, ?, ?, NULL, 'gm', 'draft', 1)",
                    Statement.RETURN_GENERATED_KEYS))
        {
            statement.setInt(1, rpgId);
            statement.setInt(2, currentRoom.getId());
            statement.setInt(3, userId);
            statement.executeUpdate();

            try(ResultSet keys = statement.getGeneratedKeys())
            {
                if(!keys.next())
                    throw new SQLException("No generated encounter id.");

                encounterId = keys.getInt(1);
            }
        }

        return getEncounter(encounterId);
    }

    public static Encounter startEncounter(int userId, int encounterId) throws Exception
    {
        Encounter encounter = requireOwnedEncounter(userId, encounterId);
        requireEncounterOpen(encounter);

        if("active".equals(encounter.status))
            return encounter;

        boolean hasParticipant = false;

        for(EncounterParticipant participant : encounter.participants)
        {
            if(!"left".equals(participant.status))
            {
                hasParticipant = true;
                break;
            }
        }

        if(!hasParticipant)
            throw new RpgEngineException("encounter-has-no-participants");

        try(Connection connection = connection();
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE rpg_engine_encounters SET status='active', started_at=CURRENT_TIMESTAMP, " +
                    "version=version+1 WHERE id=? AND status='draft'"))
        {
            statement.setInt(1, encounterId);

            if(statement.executeUpdate() == 0)
                throw new RpgEngineException("encounter-not-draft");
        }

        Encounter updated = getEncounter(encounterId);

        try(Connection connection = connection())
        {
            for(EncounterParticipant participant : updated.participants)
            {
                if(!"left".equals(participant.status))
                    captureOnlineParticipantPosition(connection, updated, participant.userId);
            }
        }

        updated = getEncounter(encounterId);
        RpgEngineRealtime.pushEncounter(updated, "encounter-started");
        return updated;
    }

    public static Encounter addEncounterParticipant(
            int userId,
            int encounterId,
            int targetUserId) throws Exception
    {
        Encounter encounter = requireOwnedEncounter(userId, encounterId);
        requireEncounterOpen(encounter);

        try(Connection connection = connection())
        {
            ensureParticipantNotInOtherOpenEncounter(
                    connection, encounter.rpgId, encounterId, targetUserId);

            int joinSequence = nextJoinSequence(connection, encounterId);

            try(PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO rpg_engine_encounter_participants " +
                    "(encounter_id, user_id, join_sequence, entry_type, entry_by_user_id, " +
                    "status, exit_reason, reconnect_deadline, return_deadline, left_at, joined_at) " +
                    "VALUES (?, ?, ?, 'gm-added', ?, 'active', NULL, NULL, NULL, NULL, CURRENT_TIMESTAMP) " +
                    "ON DUPLICATE KEY UPDATE join_sequence=VALUES(join_sequence), " +
                    "entry_type='gm-added', entry_by_user_id=VALUES(entry_by_user_id), " +
                    "status='active', exit_reason=NULL, reconnect_deadline=NULL, return_deadline=NULL, " +
                    "left_at=NULL, joined_at=CURRENT_TIMESTAMP"))
            {
                statement.setInt(1, encounterId);
                statement.setInt(2, targetUserId);
                statement.setInt(3, joinSequence);
                statement.setInt(4, userId);
                statement.executeUpdate();
            }

            captureOnlineParticipantPosition(connection, encounter, targetUserId);
        }

        Encounter updated = getEncounter(encounterId);
        RpgEngineRealtime.pushEncounter(updated, "participant-added");
        return updated;
    }


    public static Encounter startPvpEncounter(
            int initiatorUserId,
            int targetUserId,
            Room currentRoom) throws Exception
    {
        if(currentRoom == null)
            throw new RpgEngineException("not-in-room");

        if(initiatorUserId == targetUserId)
            throw new RpgEngineException("invalid-target");

        Contexto context = getContext(currentRoom.getId(), initiatorUserId);

        if(context == null)
            throw new RpgEngineException("room-not-registered");

        Habbo target = currentRoom.getHabbo(targetUserId);

        if(target == null)
            throw new RpgEngineException("target-not-in-room");

        int rpgId = context.rpgId;
        int encounterId;

        try(Connection connection = connection())
        {
            connection.setAutoCommit(false);

            try
            {
                ensureParticipantNotInOtherOpenEncounter(connection, rpgId, 0, initiatorUserId);
                ensureParticipantNotInOtherOpenEncounter(connection, rpgId, 0, targetUserId);

                try(PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO rpg_engine_encounters " +
                        "(rpg_id, room_id, created_by_user_id, initiator_user_id, creation_mode, " +
                        "status, version, started_at) " +
                        "VALUES (?, ?, ?, ?, 'pvp', 'active', 1, CURRENT_TIMESTAMP)",
                        Statement.RETURN_GENERATED_KEYS))
                {
                    statement.setInt(1, rpgId);
                    statement.setInt(2, currentRoom.getId());
                    statement.setInt(3, initiatorUserId);
                    statement.setInt(4, initiatorUserId);
                    statement.executeUpdate();

                    try(ResultSet keys = statement.getGeneratedKeys())
                    {
                        if(!keys.next())
                            throw new SQLException("No generated encounter id.");

                        encounterId = keys.getInt(1);
                    }
                }

                try(PreparedStatement participant = connection.prepareStatement(
                        "INSERT INTO rpg_engine_encounter_participants " +
                        "(encounter_id, user_id, join_sequence, entry_type, entry_by_user_id, " +
                        "status, saved_x, saved_y, saved_z) VALUES (?, ?, ?, ?, ?, 'active', ?, ?, ?)"))
                {
                    participant.setInt(1, encounterId);
                    participant.setInt(2, initiatorUserId);
                    participant.setInt(3, 1);
                    participant.setString(4, "initiator");
                    participant.setInt(5, initiatorUserId);
                    participant.setInt(6, currentRoom.getHabbo(initiatorUserId).getRoomUnit().getX());
                    participant.setInt(7, currentRoom.getHabbo(initiatorUserId).getRoomUnit().getY());
                    participant.setDouble(8, currentRoom.getHabbo(initiatorUserId).getRoomUnit().getZ());
                    participant.executeUpdate();

                    participant.setInt(1, encounterId);
                    participant.setInt(2, targetUserId);
                    participant.setInt(3, 2);
                    participant.setString(4, "target");
                    participant.setInt(5, initiatorUserId);
                    participant.setInt(6, target.getRoomUnit().getX());
                    participant.setInt(7, target.getRoomUnit().getY());
                    participant.setDouble(8, target.getRoomUnit().getZ());
                    participant.executeUpdate();
                }

                connection.commit();
            }
            catch(Exception error)
            {
                connection.rollback();
                throw error;
            }
            finally
            {
                connection.setAutoCommit(true);
            }
        }

        Encounter updated = getEncounter(encounterId);
        RpgEngineRealtime.pushEncounter(updated, "pvp-started");
        return updated;
    }

    public static Encounter getEncounterForOwner(
            int userId,
            int encounterId) throws Exception
    {
        Encounter encounter = requireEncounter(encounterId);
        requireProjectOwner(userId, encounter.rpgId);
        return encounter;
    }

    public static Encounter getMyCurrentEncounter(
            int userId,
            int rpgId) throws Exception
    {
        // A participant can remain part of an encounter while physically traversing
        // other rooms to return. Therefore this lookup is intentionally NOT room-bound.
        try(Connection connection = connection();
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT e.id FROM rpg_engine_encounters e " +
                    "INNER JOIN rpg_engine_encounter_participants p ON p.encounter_id=e.id " +
                    "WHERE e.rpg_id=? AND e.status IN ('draft','active') AND p.user_id=? " +
                    "AND p.status IN ('active','returning','disconnected') " +
                    "ORDER BY e.id DESC LIMIT 1"))
        {
            statement.setInt(1, rpgId);
            statement.setInt(2, userId);

            try(ResultSet result = statement.executeQuery())
            {
                if(!result.next()) return null;

                Encounter encounter = getEncounter(result.getInt(1));
                EncounterParticipant participant = findParticipant(encounter, userId);

                if(participant == null || "left".equals(participant.status))
                    return null;

                return encounter;
            }
        }
    }


    public static Encounter getCurrentEncounterForUser(int userId) throws Exception
    {
        try(Connection connection = connection();
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT e.id FROM rpg_engine_encounters e " +
                    "INNER JOIN rpg_engine_encounter_participants p ON p.encounter_id=e.id " +
                    "WHERE e.status IN ('draft','active') AND p.user_id=? " +
                    "AND p.status IN ('active','returning','disconnected') " +
                    "ORDER BY e.id DESC LIMIT 1"))
        {
            statement.setInt(1, userId);

            try(ResultSet result = statement.executeQuery())
            {
                if(!result.next()) return null;
                return getEncounter(result.getInt(1));
            }
        }
    }

    private static Encounter setParticipantStateSystem(
            int encounterId,
            int targetUserId,
            String status,
            String exitReason,
            int graceSeconds) throws Exception
    {
        Encounter encounter = requireEncounter(encounterId);
        requireEncounterOpen(encounter);

        String deadlineColumn = null;

        if("disconnected".equals(status)) deadlineColumn = "reconnect_deadline";
        if("returning".equals(status)) deadlineColumn = "return_deadline";

        String sql;

        if(deadlineColumn != null)
        {
            sql =
                    "UPDATE rpg_engine_encounter_participants SET status=?, exit_reason=NULL, " +
                    deadlineColumn + "=DATE_ADD(CURRENT_TIMESTAMP, INTERVAL ? SECOND), " +
                    ("disconnected".equals(status) ? "return_deadline=NULL" : "reconnect_deadline=NULL") +
                    ", left_at=NULL WHERE encounter_id=? AND user_id=? AND status<>'left'";
        }
        else
        {
            sql =
                    "UPDATE rpg_engine_encounter_participants SET status=?, exit_reason=?, " +
                    "reconnect_deadline=NULL, return_deadline=NULL, " +
                    ("left".equals(status) ? "left_at=CURRENT_TIMESTAMP" : "left_at=NULL") +
                    " WHERE encounter_id=? AND user_id=?";
        }

        try(Connection connection = connection();
            PreparedStatement statement = connection.prepareStatement(sql))
        {
            int index = 1;
            statement.setString(index++, status);

            if(deadlineColumn != null)
                statement.setInt(index++, graceSeconds);
            else
                statement.setString(index++, exitReason);

            statement.setInt(index++, encounterId);
            statement.setInt(index, targetUserId);

            if(statement.executeUpdate() == 0)
                throw new RpgEngineException("participant-not-found");
        }

        Encounter updated = getEncounter(encounterId);
        RpgEngineRealtime.pushRoomEncounters(updated.roomId, "participant-state");
        return updated;
    }

    public static Encounter systemReconnectParticipant(int userId) throws Exception
    {
        Encounter encounter = getCurrentEncounterForUser(userId);

        if(encounter == null || !"active".equals(encounter.status))
            return encounter;

        EncounterParticipant participant = findParticipant(encounter, userId);

        if(participant == null || !"disconnected".equals(participant.status))
            return encounter;

        EncounterConfig config = encounterConfigInternal(encounter.rpgId);

        return setParticipantStateSystem(
                encounter.id,
                userId,
                "returning",
                null,
                config.returnGraceSeconds
        );
    }

    public static Encounter systemDisconnectParticipant(
            int userId,
            Room currentRoom,
            int x,
            int y,
            double z) throws Exception
    {
        Encounter encounter = getCurrentEncounterForUser(userId);

        if(encounter == null || !"active".equals(encounter.status))
            return encounter;

        EncounterParticipant participant = findParticipant(encounter, userId);

        if(participant == null || "left".equals(participant.status))
            return encounter;

        if(currentRoom != null && currentRoom.getId() == encounter.roomId)
        {
            try(Connection connection = connection())
            {
                saveParticipantPosition(connection, encounter.id, userId, x, y, z);
            }
        }

        EncounterConfig config = encounterConfigInternal(encounter.rpgId);

        return setParticipantStateSystem(
                encounter.id,
                userId,
                "disconnected",
                null,
                config.reconnectGraceSeconds
        );
    }

    public static Encounter systemLeaveCombatRoom(
            int userId,
            Room currentRoom,
            int x,
            int y,
            double z) throws Exception
    {
        Encounter encounter = getCurrentEncounterForUser(userId);

        if(encounter == null || !"active".equals(encounter.status) ||
           currentRoom == null || currentRoom.getId() != encounter.roomId)
            return encounter;

        EncounterParticipant participant = findParticipant(encounter, userId);

        if(participant == null || !"active".equals(participant.status))
            return encounter;

        try(Connection connection = connection())
        {
            saveParticipantPosition(connection, encounter.id, userId, x, y, z);
        }

        EncounterConfig config = encounterConfigInternal(encounter.rpgId);

        return setParticipantStateSystem(
                encounter.id,
                userId,
                "returning",
                null,
                config.returnGraceSeconds
        );
    }

    public static Encounter systemEnterRoom(
            int userId,
            Room room) throws Exception
    {
        Encounter encounter = getCurrentEncounterForUser(userId);

        if(encounter == null || !"active".equals(encounter.status) || room == null)
            return encounter;

        EncounterParticipant participant = findParticipant(encounter, userId);

        if(participant == null || "left".equals(participant.status))
            return encounter;

        if("disconnected".equals(participant.status))
        {
            EncounterConfig config = encounterConfigInternal(encounter.rpgId);

            return setParticipantStateSystem(
                    encounter.id,
                    userId,
                    "returning",
                    null,
                    config.returnGraceSeconds
            );
        }

        if("active".equals(participant.status) && room.getId() != encounter.roomId)
        {
            EncounterConfig config = encounterConfigInternal(encounter.rpgId);

            return setParticipantStateSystem(
                    encounter.id,
                    userId,
                    "returning",
                    null,
                    config.returnGraceSeconds
            );
        }

        return encounter;
    }

    private static boolean isReservedRoomPosition(
            int roomId,
            int x,
            int y,
            int exceptUserId) throws Exception
    {
        try(Connection connection = connection())
        {
            List<EncounterReservation> reservations =
                    listRoomReservationsInternal(connection, roomId);

            for(EncounterReservation reservation : reservations)
            {
                if(reservation.userId == exceptUserId) continue;

                if(reservation.x == x && reservation.y == y)
                    return true;
            }
        }

        return false;
    }

    private static boolean isCombatParticipantInRoom(
            int userId,
            int roomId) throws Exception
    {
        Encounter encounter = getCurrentEncounterForUser(userId);

        if(encounter == null || !"active".equals(encounter.status) ||
           encounter.roomId != roomId)
            return false;

        EncounterParticipant participant = findParticipant(encounter, userId);

        return participant != null && !"left".equals(participant.status);
    }

    private static RoomTile findNearestFreeRejoinDisplacementTile(
            Room room,
            int originX,
            int originY,
            int exceptUserId) throws Exception
    {
        if(room == null || room.getLayout() == null) return null;

        int maxRadius = Math.max(
                room.getLayout().getMapSizeX(),
                room.getLayout().getMapSizeY()
        );

        for(int radius = 1; radius <= maxRadius; radius++)
        {
            for(int dy = -radius; dy <= radius; dy++)
            {
                for(int dx = -radius; dx <= radius; dx++)
                {
                    if(Math.max(Math.abs(dx), Math.abs(dy)) != radius)
                        continue;

                    int x = originX + dx;
                    int y = originY + dy;

                    if(x < Short.MIN_VALUE || x > Short.MAX_VALUE ||
                       y < Short.MIN_VALUE || y > Short.MAX_VALUE)
                        continue;

                    RoomTile tile = room.getLayout().getTile((short)x, (short)y);

                    if(tile == null || !room.tileWalkable(tile) || tile.hasUnits())
                        continue;

                    if(isReservedRoomPosition(room.getId(), x, y, exceptUserId))
                        continue;

                    return tile;
                }
            }
        }

        return null;
    }

    public static Encounter systemRejoinCombatRoom(
            Habbo habbo,
            Room room) throws Exception
    {
        if(habbo == null || room == null)
            return null;

        int userId = habbo.getHabboInfo().getId();
        Encounter encounter = getCurrentEncounterForUser(userId);

        if(encounter == null || !"active".equals(encounter.status) ||
           encounter.roomId != room.getId())
            return encounter;

        EncounterParticipant participant = findParticipant(encounter, userId);

        if(participant == null || "left".equals(participant.status))
            return encounter;

        if(!"returning".equals(participant.status) &&
           !"disconnected".equals(participant.status))
            return encounter;

        if(!participant.hasSavedPosition)
        {
            // Compatibility fallback for old encounters created before V2:
            // keep the physical entry point, store it, and reactivate.
            if(habbo.getRoomUnit() != null &&
               habbo.getRoomUnit().getCurrentLocation() != null)
            {
                try(Connection connection = connection())
                {
                    saveParticipantPosition(
                            connection,
                            encounter.id,
                            userId,
                            habbo.getRoomUnit().getX(),
                            habbo.getRoomUnit().getY(),
                            habbo.getRoomUnit().getZ()
                    );
                }
            }

            return setParticipantStateSystem(
                    encounter.id,
                    userId,
                    "active",
                    null,
                    0
            );
        }

        short savedX = (short)participant.savedX;
        short savedY = (short)participant.savedY;
        RoomTile savedTile = room.getLayout().getTile(savedX, savedY);

        if(savedTile == null)
            throw new RpgEngineException("rejoin-position-invalid");

        List<Habbo> occupants = new ArrayList<Habbo>(
                room.getHabbosAt(savedX, savedY)
        );

        for(Habbo occupant : occupants)
        {
            if(occupant == null ||
               occupant.getHabboInfo().getId() == userId)
                continue;

            int occupantUserId = occupant.getHabboInfo().getId();
            if(isCombatParticipantInRoom(occupantUserId, room.getId()))
            {
                // Same encounter OR another encounter in this same room:
                // combat participants must never be auto-displaced.
                throw new RpgEngineException(
                        "rejoin-position-occupied-by-combatant"
                );
            }

            RoomTile displacement = findNearestFreeRejoinDisplacementTile(
                    room,
                    participant.savedX,
                    participant.savedY,
                    occupantUserId
            );

            if(displacement == null)
                throw new RpgEngineException("rejoin-no-displacement-tile");

            occupant.getRoomUnit().stopWalking();
            room.teleportRoomUnitToLocation(
                    occupant.getRoomUnit(),
                    displacement.x,
                    displacement.y,
                    displacement.getStackHeight()
            );
        }

        habbo.getRoomUnit().stopWalking();
        room.teleportRoomUnitToLocation(
                habbo.getRoomUnit(),
                savedX,
                savedY,
                Math.max(participant.savedZ, savedTile.getStackHeight())
        );

        Encounter updated = setParticipantStateSystem(
                encounter.id,
                userId,
                "active",
                null,
                0
        );

        // Refresh saved Z after the authoritative teleport in case stack height
        // differs slightly from the previous snapshot.
        if(habbo.getRoomUnit() != null &&
           habbo.getRoomUnit().getCurrentLocation() != null)
        {
            try(Connection connection = connection())
            {
                saveParticipantPosition(
                        connection,
                        encounter.id,
                        userId,
                        habbo.getRoomUnit().getX(),
                        habbo.getRoomUnit().getY(),
                        habbo.getRoomUnit().getZ()
                );
            }

            updated = getEncounter(encounter.id);
            RpgEngineRealtime.pushRoomEncounters(updated.roomId, "participant-rejoined");
        }

        return updated;
    }

    public static boolean isReservedEncounterTileForParticipant(
            int userId,
            int roomId,
            int x,
            int y) throws Exception
    {
        if(!isCombatParticipantInRoom(userId, roomId))
            return false;

        return isReservedRoomPosition(roomId, x, y, userId);
    }

    private static EncounterConfig encounterConfigInternal(int rpgId) throws Exception
    {
        try(Connection connection = connection())
        {
            ensureEncounterConfigRow(connection, rpgId);

            try(PreparedStatement statement = connection.prepareStatement(
                    "SELECT rpg_id, reconnect_grace_seconds, return_grace_seconds, version " +
                    "FROM rpg_engine_encounter_config WHERE rpg_id=? LIMIT 1"))
            {
                statement.setInt(1, rpgId);

                try(ResultSet result = statement.executeQuery())
                {
                    if(!result.next())
                        throw new RpgEngineException("encounter-config-not-found");

                    EncounterConfig config = new EncounterConfig();
                    config.rpgId = result.getInt("rpg_id");
                    config.reconnectGraceSeconds = result.getInt("reconnect_grace_seconds");
                    config.returnGraceSeconds = result.getInt("return_grace_seconds");
                    config.version = result.getInt("version");
                    return config;
                }
            }
        }
    }

    private static Encounter setParticipantStateInternal(
            int userId,
            int encounterId,
            int targetUserId,
            String status,
            String exitReason,
            int graceSeconds) throws Exception
    {
        Encounter encounter = requireOwnedEncounter(userId, encounterId);
        requireEncounterOpen(encounter);

        String deadlineColumn = null;

        if("disconnected".equals(status)) deadlineColumn = "reconnect_deadline";
        if("returning".equals(status)) deadlineColumn = "return_deadline";

        String sql;

        if(deadlineColumn != null)
        {
            sql =
                    "UPDATE rpg_engine_encounter_participants SET status=?, exit_reason=NULL, " +
                    deadlineColumn + "=DATE_ADD(CURRENT_TIMESTAMP, INTERVAL ? SECOND), " +
                    ("disconnected".equals(status) ? "return_deadline=NULL" : "reconnect_deadline=NULL") +
                    ", left_at=NULL WHERE encounter_id=? AND user_id=?";
        }
        else
        {
            sql =
                    "UPDATE rpg_engine_encounter_participants SET status=?, exit_reason=?, " +
                    "reconnect_deadline=NULL, return_deadline=NULL, " +
                    ("left".equals(status) ? "left_at=CURRENT_TIMESTAMP" : "left_at=NULL") +
                    " WHERE encounter_id=? AND user_id=?";
        }

        try(Connection connection = connection();
            PreparedStatement statement = connection.prepareStatement(sql))
        {
            int index = 1;
            statement.setString(index++, status);

            if(deadlineColumn != null)
                statement.setInt(index++, graceSeconds);
            else
                statement.setString(index++, exitReason);

            statement.setLong(index++, encounterId);
            statement.setInt(index, targetUserId);

            if(statement.executeUpdate() == 0)
                throw new RpgEngineException("participant-not-found");
        }

        Encounter updated = getEncounter(encounterId);
        RpgEngineRealtime.pushRoomEncounters(updated.roomId, "participant-state");
        return updated;
    }

    public static Encounter markParticipantDisconnected(
            int userId,
            int encounterId,
            int targetUserId) throws Exception
    {
        Encounter encounter = requireOwnedEncounter(userId, encounterId);
        EncounterConfig config = encounterConfigInternal(encounter.rpgId);

        return setParticipantStateInternal(
                userId,
                encounterId,
                targetUserId,
                "disconnected",
                null,
                config.reconnectGraceSeconds
        );
    }

    public static Encounter markParticipantReturning(
            int userId,
            int encounterId,
            int targetUserId) throws Exception
    {
        Encounter encounter = requireOwnedEncounter(userId, encounterId);
        EncounterConfig config = encounterConfigInternal(encounter.rpgId);

        return setParticipantStateInternal(
                userId,
                encounterId,
                targetUserId,
                "returning",
                null,
                config.returnGraceSeconds
        );
    }

    public static Encounter markParticipantActive(
            int userId,
            int encounterId,
            int targetUserId) throws Exception
    {
        return setParticipantStateInternal(
                userId,
                encounterId,
                targetUserId,
                "active",
                null,
                0
        );
    }

    public static Encounter fleeEncounter(
            int actorUserId,
            int encounterId) throws Exception
    {
        Encounter encounter = requireEncounter(encounterId);
        requireEncounterOpen(encounter);

        boolean participant = false;

        for(EncounterParticipant value : encounter.participants)
        {
            if(value.userId == actorUserId && !"left".equals(value.status))
            {
                participant = true;
                break;
            }
        }

        if(!participant)
            throw new RpgEngineException("participant-not-found");

        // Self-flee is deliberately allowed without project-owner permission.
        try(Connection connection = connection();
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE rpg_engine_encounter_participants SET status='left', exit_reason='fled', " +
                    "left_at=CURRENT_TIMESTAMP, reconnect_deadline=NULL, return_deadline=NULL " +
                    "WHERE encounter_id=? AND user_id=? AND status<>'left'"))
        {
            statement.setInt(1, encounterId);
            statement.setInt(2, actorUserId);

            if(statement.executeUpdate() == 0)
                throw new RpgEngineException("participant-not-found");
        }

        Encounter updated = getEncounter(encounterId);
        RpgEngineRealtime.pushRoomEncounters(updated.roomId, "participant-fled");
        return updated;
    }

    public static Encounter removeEncounterParticipant(
            int userId,
            int encounterId,
            int targetUserId) throws Exception
    {
        return setParticipantStateInternal(
                userId,
                encounterId,
                targetUserId,
                "left",
                "removed",
                0
        );
    }

    public static Encounter endEncounter(
            int userId,
            int encounterId) throws Exception
    {
        Encounter encounter = requireOwnedEncounter(userId, encounterId);

        if("ended".equals(encounter.status))
            return encounter;

        try(Connection connection = connection())
        {
            connection.setAutoCommit(false);

            try
            {
                try(PreparedStatement participants = connection.prepareStatement(
                        "UPDATE rpg_engine_encounter_participants SET " +
                        "exit_reason=CASE WHEN status='left' THEN exit_reason ELSE 'encounter-ended' END, " +
                        "status='left', " +
                        "left_at=CASE WHEN left_at IS NULL THEN CURRENT_TIMESTAMP ELSE left_at END, " +
                        "reconnect_deadline=NULL, return_deadline=NULL WHERE encounter_id=?"))
                {
                    participants.setInt(1, encounterId);
                    participants.executeUpdate();
                }

                try(PreparedStatement statement = connection.prepareStatement(
                        "UPDATE rpg_engine_encounters SET status='ended', ended_at=CURRENT_TIMESTAMP, " +
                        "version=version+1 WHERE id=?"))
                {
                    statement.setInt(1, encounterId);
                    statement.executeUpdate();
                }

                connection.commit();
            }
            catch(Exception error)
            {
                connection.rollback();
                throw error;
            }
            finally
            {
                connection.setAutoCommit(true);
            }
        }

        Encounter updated = getEncounter(encounterId);
        RpgEngineRealtime.pushEncounter(updated, "encounter-ended");
        RpgEngineRealtime.pushRoomEncounters(updated.roomId, "room-reservations-changed");
        return updated;
    }

}
