package com.retro.spaceinvaders.arcade;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.rooms.RoomUserRotation;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.threading.runnables.RoomUnitWalkToLocation;
import com.eu.habbo.util.pathfinding.Rotation;
import com.retro.spaceinvaders.SpaceInvadersPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ArcadeManager
{
    private static final long ACCESS_TTL_MS =
            45L * 60L * 1000L;

    private static final long RUN_TTL_MS =
            35L * 60L * 1000L;

    private static final long POSITION_WATCH_MS =
            150L;

    private final ConcurrentHashMap<Integer, ArcadeSession> sessions =
            new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Integer, Long> pendingOpenTokens =
            new ConcurrentHashMap<>();

    private volatile boolean databaseReady = false;

    public synchronized void initializeDatabase()
            throws Exception
    {
        if(this.databaseReady)
        {
            return;
        }

        try(Connection connection =
                    Emulator.getDatabase()
                            .getDataSource()
                            .getConnection();
            Statement statement =
                    connection.createStatement())
        {
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS biribiri_arcade_scores (" +
                    "game_key VARCHAR(64) NOT NULL," +
                    "user_id INT NOT NULL," +
                    "best_score INT NOT NULL DEFAULT 0," +
                    "best_level INT NOT NULL DEFAULT 1," +
                    "best_duration_ms BIGINT NOT NULL DEFAULT 0," +
                    "achieved_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP " +
                    "ON UPDATE CURRENT_TIMESTAMP," +
                    "PRIMARY KEY (game_key,user_id)," +
                    "KEY idx_arcade_game_rank " +
                    "(game_key,best_score,best_level,best_duration_ms,user_id)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4"
            );
        }

        this.databaseReady = true;
    }

    public void clearRuntimeState()
    {
        this.pendingOpenTokens.clear();
        this.sessions.clear();
    }

    public void requestOpenMachine(
            GameClient client,
            Room room,
            HabboItem item,
            ArcadeGameDefinition game)
    {
        if(client == null ||
                client.getHabbo() == null ||
                room == null ||
                item == null ||
                game == null)
        {
            return;
        }

        Habbo habbo =
                client.getHabbo();

        RoomUnit unit =
                habbo.getRoomUnit();

        if(unit == null ||
                unit.getCurrentLocation() == null)
        {
            return;
        }

        HabboItem current =
                room.getHabboItem(
                        item.getId()
                );

        if(!this.matchesGameItem(
                current,
                game))
        {
            return;
        }

        RoomTile front =
                this.getExactFrontTile(
                        room,
                        current,
                        game
                );

        if(front == null)
        {
            return;
        }

        int userId =
                habbo.getHabboInfo()
                        .getId();

        long token =
                System.nanoTime();

        this.pendingOpenTokens.put(
                userId,
                token
        );

        if(this.isOnTile(
                unit,
                front))
        {
            this.pendingOpenTokens.remove(
                    userId,
                    token
            );

            this.openMachine(
                    client,
                    room,
                    current,
                    game
            );

            return;
        }

        ArrayList<Runnable> onSuccess =
                new ArrayList<>();

        ArrayList<Runnable> onFail =
                new ArrayList<>();

        final int roomId =
                room.getId();

        final int itemId =
                current.getId();

        final short targetX =
                front.x;

        final short targetY =
                front.y;

        onSuccess.add(
                () -> this.completePendingOpen(
                        userId,
                        roomId,
                        itemId,
                        game.getKey(),
                        targetX,
                        targetY,
                        token
                )
        );

        onFail.add(
                () -> this.pendingOpenTokens.remove(
                        userId,
                        token
                )
        );

        unit.setGoalLocation(
                front
        );

        Emulator.getThreading()
                .run(
                        new RoomUnitWalkToLocation(
                                unit,
                                front,
                                room,
                                onSuccess,
                                onFail
                        )
                );
    }

    private void completePendingOpen(
            int userId,
            int roomId,
            int itemId,
            String gameKey,
            short targetX,
            short targetY,
            long token)
    {
        Long currentToken =
                this.pendingOpenTokens.get(
                        userId
                );

        if(currentToken == null ||
                currentToken.longValue() != token)
        {
            return;
        }

        ArcadeGameDefinition game =
                ArcadeGameDefinition.byKey(
                        gameKey
                );

        Habbo habbo =
                Emulator.getGameEnvironment()
                        .getHabboManager()
                        .getHabbo(userId);

        if(game == null ||
                habbo == null ||
                habbo.getClient() == null ||
                habbo.getRoomUnit() == null ||
                habbo.getRoomUnit()
                        .getCurrentLocation() == null)
        {
            this.pendingOpenTokens.remove(
                    userId,
                    token
            );
            return;
        }

        Room room =
                habbo.getHabboInfo()
                        .getCurrentRoom();

        if(room == null ||
                room.getId() != roomId)
        {
            this.pendingOpenTokens.remove(
                    userId,
                    token
            );
            return;
        }

        HabboItem current =
                room.getHabboItem(
                        itemId
                );

        if(!this.matchesGameItem(
                current,
                game))
        {
            this.pendingOpenTokens.remove(
                    userId,
                    token
            );
            return;
        }

        RoomTile front =
                this.getExactFrontTile(
                        room,
                        current,
                        game
                );

        if(front == null ||
                front.x != targetX ||
                front.y != targetY ||
                !this.isOnTile(
                        habbo.getRoomUnit(),
                        front
                ))
        {
            this.pendingOpenTokens.remove(
                    userId,
                    token
            );
            return;
        }

        if(!this.pendingOpenTokens.remove(
                userId,
                token))
        {
            return;
        }

        this.openMachine(
                habbo.getClient(),
                room,
                current,
                game
        );
    }

    private void openMachine(
            GameClient client,
            Room room,
            HabboItem item,
            ArcadeGameDefinition game)
    {
        if(client == null ||
                client.getHabbo() == null ||
                room == null ||
                item == null ||
                game == null)
        {
            return;
        }

        if(!this.matchesGameItem(
                room.getHabboItem(item.getId()),
                game))
        {
            return;
        }

        if(!this.isExactlyInFront(
                client.getHabbo(),
                room,
                item,
                game))
        {
            return;
        }

        this.faceMachine(
                client.getHabbo(),
                item
        );

        int userId =
                client.getHabbo()
                        .getHabboInfo()
                        .getId();

        ArcadeSession session =
                new ArcadeSession(
                        room.getId(),
                        item.getId(),
                        game,
                        System.currentTimeMillis() +
                                ACCESS_TTL_MS
                );

        this.sessions.put(
                userId,
                session
        );

        ServerMessage packet =
                new ServerMessage(
                        game.getOpenPacketId()
                );

        packet.appendInt(
                item.getId()
        );

        client.sendResponse(
                packet
        );

        this.sendLeaderboardSafe(
                client,
                game,
                0,
                "",
                false
        );

        this.watchSessionPosition(
                userId,
                session
        );
    }

    public void startRun(
            GameClient client,
            int itemId,
            String gameKey)
    {
        if(client == null ||
                client.getHabbo() == null)
        {
            return;
        }

        ArcadeGameDefinition game =
                ArcadeGameDefinition.byKey(
                        gameKey
                );

        if(game == null)
        {
            this.sendRunStarted(
                    client,
                    false,
                    "Juego arcade no reconocido.",
                    itemId,
                    gameKey == null ? "" : gameKey,
                    ""
            );
            return;
        }

        int userId =
                client.getHabbo()
                        .getHabboInfo()
                        .getId();

        ArcadeSession session =
                this.sessions.get(
                        userId
                );

        if(!this.validAccess(
                client.getHabbo(),
                session,
                itemId,
                game))
        {
            if(session != null)
            {
                this.sessions.remove(
                        userId,
                        session
                );
            }

            this.sendRunStarted(
                    client,
                    false,
                    "Debes estar delante de la máquina.",
                    itemId,
                    game.getKey(),
                    ""
            );

            this.sendClose(
                    client,
                    itemId,
                    game,
                    "Debes estar delante de la máquina."
            );

            return;
        }

        long now =
                System.currentTimeMillis();

        String token =
                UUID.randomUUID()
                        .toString();

        synchronized(session)
        {
            session.runToken =
                    token;

            session.runStartedAt =
                    now;

            session.runExpiresAt =
                    now + RUN_TTL_MS;

            session.expiresAt =
                    now + ACCESS_TTL_MS;
        }

        this.sendRunStarted(
                client,
                true,
                "",
                itemId,
                game.getKey(),
                token
        );
    }

    public void submitScore(
            GameClient client,
            int itemId,
            String gameKey,
            String token,
            int score,
            int level)
    {
        if(client == null ||
                client.getHabbo() == null)
        {
            return;
        }

        ArcadeGameDefinition game =
                ArcadeGameDefinition.byKey(
                        gameKey
                );

        if(game == null)
        {
            return;
        }

        Habbo habbo =
                client.getHabbo();

        int userId =
                habbo.getHabboInfo()
                        .getId();

        ArcadeSession session =
                this.sessions.get(
                        userId
                );

        if(!this.validAccess(
                habbo,
                session,
                itemId,
                game))
        {
            if(session != null)
            {
                this.sessions.remove(
                        userId,
                        session
                );
            }

            this.sendClose(
                    client,
                    itemId,
                    game,
                    "La sesión arcade ya no es válida."
            );

            return;
        }

        long now =
                System.currentTimeMillis();

        long durationMs;

        synchronized(session)
        {
            if(session.runToken == null ||
                    session.runToken.isEmpty() ||
                    token == null ||
                    !session.runToken.equals(token) ||
                    session.runStartedAt <= 0L ||
                    session.runExpiresAt < now)
            {
                this.sendLeaderboardSafe(
                        client,
                        game,
                        2,
                        "La partida no pudo validarse.",
                        false
                );
                return;
            }

            durationMs =
                    now -
                    session.runStartedAt;

            session.runToken = "";
            session.runStartedAt = 0L;
            session.runExpiresAt = 0L;
            session.expiresAt =
                    now + ACCESS_TTL_MS;
        }

        if(!game.isValidScore(
                score,
                level))
        {
            this.sendLeaderboardSafe(
                    client,
                    game,
                    2,
                    "Puntuación fuera de los límites del juego.",
                    false
            );
            return;
        }

        long minimumDuration =
                game.minimumPlausibleDurationMs(
                        score,
                        level
                );

        if(durationMs < minimumDuration ||
                durationMs > RUN_TTL_MS)
        {
            this.sendLeaderboardSafe(
                    client,
                    game,
                    2,
                    "Duración de partida no válida.",
                    false
            );
            return;
        }

        boolean newRecord;

        try
        {
            newRecord =
                    this.storeBestScore(
                            game,
                            userId,
                            score,
                            level,
                            durationMs
                    );
        }
        catch(Exception exception)
        {
            exception.printStackTrace();

            this.sendLeaderboardSafe(
                    client,
                    game,
                    2,
                    "No se pudo guardar la puntuación.",
                    false
            );
            return;
        }

        this.sendLeaderboardSafe(
                client,
                game,
                1,
                newRecord
                        ? "Nuevo récord personal."
                        : "Puntuación registrada.",
                newRecord
        );
    }

    private boolean validAccess(
            Habbo habbo,
            ArcadeSession session,
            int itemId,
            ArcadeGameDefinition game)
    {
        if(habbo == null ||
                session == null ||
                game == null ||
                session.itemId != itemId ||
                session.game != game ||
                session.expiresAt <
                        System.currentTimeMillis())
        {
            return false;
        }

        Room room =
                habbo.getHabboInfo()
                        .getCurrentRoom();

        if(room == null ||
                room.getId() != session.roomId)
        {
            return false;
        }

        HabboItem item =
                room.getHabboItem(
                        itemId
                );

        return this.matchesGameItem(
                item,
                game) &&
                this.isExactlyInFront(
                        habbo,
                        room,
                        item,
                        game
                );
    }

    private boolean matchesGameItem(
            HabboItem item,
            ArcadeGameDefinition game)
    {
        return item != null &&
                game != null &&
                game.getInteractionClass()
                        .isInstance(item);
    }

    private RoomTile getExactFrontTile(
            Room room,
            HabboItem item,
            ArcadeGameDefinition game)
    {
        if(room == null ||
                room.getLayout() == null ||
                item == null ||
                game == null)
        {
            return null;
        }

        RoomTile machineTile =
                room.getLayout()
                        .getTile(
                                item.getX(),
                                item.getY()
                        );

        if(machineTile == null)
        {
            return null;
        }

        int visualFrontRotation =
                (
                        item.getRotation() +
                        game.getFrontRotationOffset() +
                        8
                ) % 8;

        return room.getLayout()
                .getTileInFront(
                        machineTile,
                        visualFrontRotation
                );
    }

    private boolean isOnTile(
            RoomUnit unit,
            RoomTile tile)
    {
        return unit != null &&
                unit.getCurrentLocation() != null &&
                tile != null &&
                unit.getX() == tile.x &&
                unit.getY() == tile.y;
    }

    private boolean isExactlyInFront(
            Habbo habbo,
            Room room,
            HabboItem item,
            ArcadeGameDefinition game)
    {
        if(habbo == null ||
                habbo.getRoomUnit() == null)
        {
            return false;
        }

        RoomTile front =
                this.getExactFrontTile(
                        room,
                        item,
                        game
                );

        return this.isOnTile(
                habbo.getRoomUnit(),
                front
        );
    }

    private void faceMachine(
            Habbo habbo,
            HabboItem item)
    {
        if(habbo == null ||
                habbo.getRoomUnit() == null ||
                item == null)
        {
            return;
        }

        RoomUnit unit =
                habbo.getRoomUnit();

        int rotation =
                Rotation.Calculate(
                        unit.getX(),
                        unit.getY(),
                        item.getX(),
                        item.getY()
                );

        unit.setRotation(
                RoomUserRotation.values()[
                        rotation
                ]
        );

        unit.statusUpdate(
                true
        );
    }

    private void watchSessionPosition(
            int userId,
            ArcadeSession session)
    {
        Emulator.getThreading()
                .run(
                        () ->
                        {
                            if(session == null ||
                                    this.sessions.get(userId) !=
                                            session)
                            {
                                return;
                            }

                            Habbo habbo =
                                    Emulator.getGameEnvironment()
                                            .getHabboManager()
                                            .getHabbo(userId);

                            if(habbo == null ||
                                    habbo.getClient() == null)
                            {
                                this.sessions.remove(
                                        userId,
                                        session
                                );
                                return;
                            }

                            if(session.expiresAt <
                                    System.currentTimeMillis())
                            {
                                this.invalidateSessionAndClose(
                                        userId,
                                        session,
                                        "La sesión arcade ha caducado."
                                );
                                return;
                            }

                            Room room =
                                    habbo.getHabboInfo()
                                            .getCurrentRoom();

                            if(room == null ||
                                    room.getId() !=
                                            session.roomId)
                            {
                                this.invalidateSessionAndClose(
                                        userId,
                                        session,
                                        "Has abandonado la máquina."
                                );
                                return;
                            }

                            HabboItem current =
                                    room.getHabboItem(
                                            session.itemId
                                    );

                            if(!this.matchesGameItem(
                                    current,
                                    session.game))
                            {
                                this.invalidateSessionAndClose(
                                        userId,
                                        session,
                                        "La máquina ya no está disponible."
                                );
                                return;
                            }

                            if(!this.isExactlyInFront(
                                    habbo,
                                    room,
                                    current,
                                    session.game))
                            {
                                this.invalidateSessionAndClose(
                                        userId,
                                        session,
                                        "Te has alejado de la máquina."
                                );
                                return;
                            }

                            this.watchSessionPosition(
                                    userId,
                                    session
                            );
                        },
                        POSITION_WATCH_MS
                );
    }

    private void invalidateSessionAndClose(
            int userId,
            ArcadeSession session,
            String reason)
    {
        if(session == null ||
                !this.sessions.remove(
                        userId,
                        session
                ))
        {
            return;
        }

        Habbo habbo =
                Emulator.getGameEnvironment()
                        .getHabboManager()
                        .getHabbo(userId);

        if(habbo == null ||
                habbo.getClient() == null)
        {
            return;
        }

        this.sendClose(
                habbo.getClient(),
                session.itemId,
                session.game,
                reason
        );
    }

    private void sendRunStarted(
            GameClient client,
            boolean success,
            String message,
            int itemId,
            String gameKey,
            String token)
    {
        if(client == null)
        {
            return;
        }

        ServerMessage packet =
                new ServerMessage(
                        SpaceInvadersPlugin.PACKET_STARTED
                );

        packet.appendBoolean(
                success
        );

        packet.appendString(
                message == null ? "" : message
        );

        packet.appendInt(
                itemId
        );

        packet.appendString(
                gameKey == null ? "" : gameKey
        );

        packet.appendString(
                token == null ? "" : token
        );

        client.sendResponse(
                packet
        );
    }

    private void sendClose(
            GameClient client,
            int itemId,
            ArcadeGameDefinition game,
            String reason)
    {
        if(client == null ||
                game == null)
        {
            return;
        }

        ServerMessage packet =
                new ServerMessage(
                        SpaceInvadersPlugin.PACKET_CLOSE
                );

        packet.appendInt(
                itemId
        );

        packet.appendString(
                game.getKey()
        );

        packet.appendString(
                reason == null ? "" : reason
        );

        client.sendResponse(
                packet
        );
    }

    private void sendLeaderboardSafe(
            GameClient client,
            ArcadeGameDefinition game,
            int context,
            String message,
            boolean newRecord)
    {
        if(client == null ||
                client.getHabbo() == null ||
                game == null)
        {
            return;
        }

        try
        {
            LeaderboardSnapshot snapshot =
                    this.loadLeaderboard(
                            game,
                            client.getHabbo()
                                    .getHabboInfo()
                                    .getId()
                    );

            ServerMessage packet =
                    new ServerMessage(
                            SpaceInvadersPlugin.PACKET_LEADERBOARD
                    );

            packet.appendString(
                    game.getKey()
            );

            packet.appendInt(
                    context
            );

            packet.appendString(
                    message == null ? "" : message
            );

            packet.appendBoolean(
                    newRecord
            );

            packet.appendInt(
                    snapshot.personalBest
            );

            packet.appendInt(
                    snapshot.personalLevel
            );

            packet.appendInt(
                    snapshot.personalRank
            );

            packet.appendInt(
                    snapshot.totalPlayers
            );

            packet.appendInt(
                    snapshot.entries.size()
            );

            for(LeaderboardEntry entry :
                    snapshot.entries)
            {
                packet.appendInt(
                        entry.rank
                );

                packet.appendString(
                        entry.username
                );

                packet.appendInt(
                        entry.score
                );

                packet.appendInt(
                        entry.level
                );
            }

            client.sendResponse(
                    packet
            );
        }
        catch(Exception exception)
        {
            exception.printStackTrace();

            ServerMessage packet =
                    new ServerMessage(
                            SpaceInvadersPlugin.PACKET_LEADERBOARD
                    );

            packet.appendString(
                    game.getKey()
            );

            packet.appendInt(
                    context == 0 ? 2 : context
            );

            packet.appendString(
                    "Ranking temporalmente no disponible."
            );

            packet.appendBoolean(
                    false
            );

            packet.appendInt(0);
            packet.appendInt(1);
            packet.appendInt(0);
            packet.appendInt(0);
            packet.appendInt(0);

            client.sendResponse(
                    packet
            );
        }
    }

    private boolean storeBestScore(
            ArcadeGameDefinition game,
            int userId,
            int score,
            int level,
            long durationMs)
            throws Exception
    {
        this.initializeDatabase();

        try(Connection connection =
                    Emulator.getDatabase()
                            .getDataSource()
                            .getConnection())
        {
            PersonalScore previous =
                    this.loadPersonalScore(
                            connection,
                            game.getKey(),
                            userId
                    );

            boolean better =
                    previous == null ||
                    score > previous.score ||
                    (
                            score == previous.score &&
                            level > previous.level
                    ) ||
                    (
                            score == previous.score &&
                            level == previous.level &&
                            (
                                    previous.durationMs <= 0L ||
                                    durationMs <
                                            previous.durationMs
                            )
                    );

            if(!better)
            {
                return false;
            }

            if(previous == null)
            {
                try(PreparedStatement insert =
                            connection.prepareStatement(
                                    "INSERT INTO biribiri_arcade_scores " +
                                    "(game_key,user_id,best_score,best_level," +
                                    "best_duration_ms,achieved_at) " +
                                    "VALUES (?,?,?,?,?,CURRENT_TIMESTAMP)"
                            ))
                {
                    insert.setString(
                            1,
                            game.getKey()
                    );

                    insert.setInt(
                            2,
                            userId
                    );

                    insert.setInt(
                            3,
                            score
                    );

                    insert.setInt(
                            4,
                            level
                    );

                    insert.setLong(
                            5,
                            durationMs
                    );

                    insert.executeUpdate();
                }
            }
            else
            {
                try(PreparedStatement update =
                            connection.prepareStatement(
                                    "UPDATE biribiri_arcade_scores " +
                                    "SET best_score=?,best_level=?," +
                                    "best_duration_ms=?,achieved_at=CURRENT_TIMESTAMP " +
                                    "WHERE game_key=? AND user_id=?"
                            ))
                {
                    update.setInt(
                            1,
                            score
                    );

                    update.setInt(
                            2,
                            level
                    );

                    update.setLong(
                            3,
                            durationMs
                    );

                    update.setString(
                            4,
                            game.getKey()
                    );

                    update.setInt(
                            5,
                            userId
                    );

                    update.executeUpdate();
                }
            }

            return true;
        }
    }

    private LeaderboardSnapshot loadLeaderboard(
            ArcadeGameDefinition game,
            int userId)
            throws Exception
    {
        this.initializeDatabase();

        try(Connection connection =
                    Emulator.getDatabase()
                            .getDataSource()
                            .getConnection())
        {
            PersonalScore personal =
                    this.loadPersonalScore(
                            connection,
                            game.getKey(),
                            userId
                    );

            int totalPlayers = 0;

            try(PreparedStatement total =
                        connection.prepareStatement(
                                "SELECT COUNT(*) " +
                                "FROM biribiri_arcade_scores " +
                                "WHERE game_key=?"
                        ))
            {
                total.setString(
                        1,
                        game.getKey()
                );

                try(ResultSet result =
                            total.executeQuery())
                {
                    if(result.next())
                    {
                        totalPlayers =
                                result.getInt(1);
                    }
                }
            }

            int personalRank = 0;

            if(personal != null)
            {
                try(PreparedStatement rank =
                            connection.prepareStatement(
                                    "SELECT COUNT(*) + 1 " +
                                    "FROM biribiri_arcade_scores " +
                                    "WHERE game_key=? AND (" +
                                    "best_score > ? OR " +
                                    "(best_score = ? AND best_level > ?) OR " +
                                    "(best_score = ? AND best_level = ? " +
                                    "AND best_duration_ms < ?) OR " +
                                    "(best_score = ? AND best_level = ? " +
                                    "AND best_duration_ms = ? AND user_id < ?)" +
                                    ")"
                            ))
                {
                    rank.setString(
                            1,
                            game.getKey()
                    );

                    rank.setInt(
                            2,
                            personal.score
                    );

                    rank.setInt(
                            3,
                            personal.score
                    );

                    rank.setInt(
                            4,
                            personal.level
                    );

                    rank.setInt(
                            5,
                            personal.score
                    );

                    rank.setInt(
                            6,
                            personal.level
                    );

                    rank.setLong(
                            7,
                            personal.durationMs
                    );

                    rank.setInt(
                            8,
                            personal.score
                    );

                    rank.setInt(
                            9,
                            personal.level
                    );

                    rank.setLong(
                            10,
                            personal.durationMs
                    );

                    rank.setInt(
                            11,
                            userId
                    );

                    try(ResultSet result =
                                rank.executeQuery())
                    {
                        if(result.next())
                        {
                            personalRank =
                                    result.getInt(1);
                        }
                    }
                }
            }

            List<LeaderboardEntry> entries =
                    new ArrayList<>();

            try(PreparedStatement top =
                        connection.prepareStatement(
                                "SELECT s.user_id,u.username," +
                                "s.best_score,s.best_level " +
                                "FROM biribiri_arcade_scores s " +
                                "LEFT JOIN users u ON u.id=s.user_id " +
                                "WHERE s.game_key=? " +
                                "ORDER BY s.best_score DESC," +
                                "s.best_level DESC," +
                                "s.best_duration_ms ASC," +
                                "s.user_id ASC LIMIT 10"
                        ))
            {
                top.setString(
                        1,
                        game.getKey()
                );

                try(ResultSet result =
                            top.executeQuery())
                {
                    int rank = 1;

                    while(result.next())
                    {
                        String username =
                                result.getString(
                                        "username"
                                );

                        if(username == null ||
                                username.trim().isEmpty())
                        {
                            username =
                                    "Jugador #" +
                                    result.getInt(
                                            "user_id"
                                    );
                        }

                        entries.add(
                                new LeaderboardEntry(
                                        rank,
                                        username,
                                        result.getInt(
                                                "best_score"
                                        ),
                                        result.getInt(
                                                "best_level"
                                        )
                                )
                        );

                        rank++;
                    }
                }
            }

            return new LeaderboardSnapshot(
                    personal == null
                            ? 0
                            : personal.score,
                    personal == null
                            ? 1
                            : personal.level,
                    personalRank,
                    totalPlayers,
                    entries
            );
        }
    }

    private PersonalScore loadPersonalScore(
            Connection connection,
            String gameKey,
            int userId)
            throws Exception
    {
        try(PreparedStatement query =
                    connection.prepareStatement(
                            "SELECT best_score,best_level,best_duration_ms " +
                            "FROM biribiri_arcade_scores " +
                            "WHERE game_key=? AND user_id=?"
                    ))
        {
            query.setString(
                    1,
                    gameKey
            );

            query.setInt(
                    2,
                    userId
            );

            try(ResultSet result =
                        query.executeQuery())
            {
                if(!result.next())
                {
                    return null;
                }

                return new PersonalScore(
                        result.getInt(
                                "best_score"
                        ),
                        result.getInt(
                                "best_level"
                        ),
                        result.getLong(
                                "best_duration_ms"
                        )
                );
            }
        }
    }

    private static final class ArcadeSession
    {
        private final int roomId;
        private final int itemId;
        private final ArcadeGameDefinition game;

        private volatile long expiresAt;
        private volatile String runToken = "";
        private volatile long runStartedAt = 0L;
        private volatile long runExpiresAt = 0L;

        private ArcadeSession(
                int roomId,
                int itemId,
                ArcadeGameDefinition game,
                long expiresAt)
        {
            this.roomId = roomId;
            this.itemId = itemId;
            this.game = game;
            this.expiresAt = expiresAt;
        }
    }

    private static final class PersonalScore
    {
        private final int score;
        private final int level;
        private final long durationMs;

        private PersonalScore(
                int score,
                int level,
                long durationMs)
        {
            this.score = score;
            this.level = level;
            this.durationMs = durationMs;
        }
    }

    private static final class LeaderboardEntry
    {
        private final int rank;
        private final String username;
        private final int score;
        private final int level;

        private LeaderboardEntry(
                int rank,
                String username,
                int score,
                int level)
        {
            this.rank = rank;
            this.username = username;
            this.score = score;
            this.level = level;
        }
    }

    private static final class LeaderboardSnapshot
    {
        private final int personalBest;
        private final int personalLevel;
        private final int personalRank;
        private final int totalPlayers;
        private final List<LeaderboardEntry> entries;

        private LeaderboardSnapshot(
                int personalBest,
                int personalLevel,
                int personalRank,
                int totalPlayers,
                List<LeaderboardEntry> entries)
        {
            this.personalBest = personalBest;
            this.personalLevel = personalLevel;
            this.personalRank = personalRank;
            this.totalPlayers = totalPlayers;
            this.entries = entries;
        }
    }
}
