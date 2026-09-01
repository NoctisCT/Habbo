package com.retro.tragaperras;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public final class SlotMachineManager
{
    private static final long SESSION_TTL_MS = 5L * 60L * 1000L;

    // 0=cherry, 1=lemon, 2=bell, 3=bar, 4=diamond, 5=seven, 6=jackpot.
    private static final int SYMBOL_CHERRY = 0;
    private static final int SYMBOL_LEMON = 1;
    private static final int SYMBOL_BELL = 2;
    private static final int SYMBOL_BAR = 3;
    private static final int SYMBOL_DIAMOND = 4;
    private static final int SYMBOL_SEVEN = 5;
    private static final int SYMBOL_JACKPOT = 6;

    private final SecureRandom random = new SecureRandom();

    private final Map<SlotCurrency, SlotEconomy> economies =
            new EnumMap<>(SlotCurrency.class);

    private final ConcurrentHashMap<Integer, SlotSession> sessions =
            new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Integer, Long> pendingOpenTokens =
            new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Integer, ReentrantLock> spinLocks =
            new ConcurrentHashMap<>();

    public SlotMachineManager()
    {
        // V5 generaliza el motor, pero mantiene SOLO Creditos habilitado.
        // Diamonds y Duckets ya tienen adapter nativo + interaction registrada;
        // su economia se anadira en las siguientes capas.
        this.registerEconomy(
                new SlotEconomy(
                        SlotCurrency.CREDITS,
                        5,
                        1,
                        1,
                        3,
                        1000L,
                        1000L,
                        2000L,
                        new int[] { 6, 10, 20, 50, 100, 250 },
                        new int[] { 11000, 4000, 1300, 300, 100, 10 },
                        new int[] {
                                SYMBOL_CHERRY,
                                SYMBOL_LEMON,
                                SYMBOL_BELL,
                                SYMBOL_BAR,
                                SYMBOL_DIAMOND,
                                SYMBOL_SEVEN
                        },
                        100000,
                        1000L,
                        new long[] { 1500L, 2000L, 2500L, 3000L },
                        new int[] { 200000, 100000, 50000, 25000, 11000 }
                )
        );

        // Diamonds V1: misma matematica relativa que Creditos, escalada x4.
        this.registerEconomy(
                new SlotEconomy(
                        SlotCurrency.DIAMONDS,
                        20,
                        4,
                        4,
                        12,
                        4000L,
                        4000L,
                        8000L,
                        new int[] { 24, 40, 80, 200, 400, 1000 },
                        new int[] { 11000, 4000, 1300, 300, 100, 10 },
                        new int[] {
                                SYMBOL_CHERRY,
                                SYMBOL_LEMON,
                                SYMBOL_BELL,
                                SYMBOL_BAR,
                                SYMBOL_DIAMOND,
                                SYMBOL_SEVEN
                        },
                        100000,
                        4000L,
                        new long[] { 6000L, 8000L, 10000L, 12000L },
                        new int[] { 200000, 100000, 50000, 25000, 11000 }
                )
        );

        // Duckets V1: apuesta x2 vs Creditos, pero bote base de 1000.
        this.registerEconomy(
                new SlotEconomy(
                        SlotCurrency.DUCKETS,
                        10,
                        2,
                        2,
                        6,
                        1000L,
                        1000L,
                        2000L,
                        new int[] { 12, 20, 40, 100, 200, 500 },
                        new int[] { 11000, 4000, 1300, 300, 100, 10 },
                        new int[] {
                                SYMBOL_CHERRY,
                                SYMBOL_LEMON,
                                SYMBOL_BELL,
                                SYMBOL_BAR,
                                SYMBOL_DIAMOND,
                                SYMBOL_SEVEN
                        },
                        100000,
                        1000L,
                        new long[] { 1500L, 2000L, 2500L, 3000L },
                        new int[] { 200000, 100000, 50000, 25000, 11000 }
                )
        );
    }

    private void registerEconomy(SlotEconomy economy)
    {
        if(economy == null)
        {
            throw new IllegalArgumentException("Economia null.");
        }

        this.economies.put(economy.currency, economy);
    }

    public void initializeDatabase() throws Exception
    {
        try(Connection connection =
                    Emulator.getDatabase()
                            .getDataSource()
                            .getConnection();
            Statement statement =
                    connection.createStatement())
        {
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS holo_slots_state (" +
                    "currency VARCHAR(32) NOT NULL PRIMARY KEY," +
                    "jackpot BIGINT NOT NULL," +
                    "treasury BIGINT NOT NULL," +
                    "total_wagered BIGINT NOT NULL DEFAULT 0," +
                    "total_burn BIGINT NOT NULL DEFAULT 0," +
                    "total_normal_payout BIGINT NOT NULL DEFAULT 0," +
                    "total_jackpot_payout BIGINT NOT NULL DEFAULT 0," +
                    "total_spins BIGINT NOT NULL DEFAULT 0," +
                    "total_admin_injected BIGINT NOT NULL DEFAULT 0," +
                    "jackpot_count BIGINT NOT NULL DEFAULT 0," +
                    "spins_since_jackpot BIGINT NOT NULL DEFAULT 0," +
                    "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4"
            );

            statement.execute(
                    "CREATE TABLE IF NOT EXISTS holo_slots_spins (" +
                    "id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                    "currency VARCHAR(32) NOT NULL," +
                    "user_id INT NOT NULL," +
                    "room_id INT NOT NULL," +
                    "item_id INT NOT NULL," +
                    "bet INT NOT NULL," +
                    "burn INT NOT NULL," +
                    "normal_prize INT NOT NULL," +
                    "jackpot_prize BIGINT NOT NULL," +
                    "jackpot_hit TINYINT(1) NOT NULL," +
                    "balance_before INT NOT NULL," +
                    "balance_after INT NOT NULL," +
                    "jackpot_after BIGINT NOT NULL," +
                    "treasury_after BIGINT NOT NULL," +
                    "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                    "INDEX idx_holo_slots_spins_user_time (user_id, created_at)," +
                    "INDEX idx_holo_slots_spins_currency_time (currency, created_at)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4"
            );
        }

        for(SlotEconomy economy : this.economies.values())
        {
            try(Connection connection =
                        Emulator.getDatabase()
                                .getDataSource()
                                .getConnection();
                PreparedStatement insert =
                        connection.prepareStatement(
                                "INSERT IGNORE INTO holo_slots_state (" +
                                "currency, jackpot, treasury, total_admin_injected" +
                                ") VALUES (?, ?, ?, ?)"
                        ))
            {
                insert.setString(1, economy.currency.getKey());
                insert.setLong(2, economy.initialJackpot);
                insert.setLong(3, economy.initialTreasury);
                insert.setLong(4, economy.initialAdminInjected);
                insert.executeUpdate();
            }

            System.out.println(
                    "[Tragaperras] BD preparada. " +
                    economy.currency.getKey() +
                    ": jackpot inicial=" +
                    economy.initialJackpot +
                    ", tesoreria inicial=" +
                    economy.initialTreasury
            );
        }
    }

    public void clearRuntimeState()
    {
        this.sessions.clear();
        this.pendingOpenTokens.clear();
        this.spinLocks.clear();
    }

    public void requestOpenMachine(
            GameClient client,
            Room room,
            InteractionSlotMachine item)
    {
        if(client == null ||
                client.getHabbo() == null ||
                room == null ||
                item == null)
        {
            return;
        }

        Habbo habbo = client.getHabbo();
        com.eu.habbo.habbohotel.rooms.RoomUnit unit =
                habbo.getRoomUnit();

        if(unit == null ||
                unit.getCurrentLocation() == null)
        {
            return;
        }

        HabboItem current =
                room.getHabboItem(item.getId());

        if(!(current instanceof InteractionSlotMachine))
        {
            return;
        }

        InteractionSlotMachine slotItem =
                (InteractionSlotMachine)current;

        SlotCurrency currency =
                slotItem.getSlotCurrency();

        if(currency == null ||
                this.economies.get(currency) == null)
        {
            return;
        }

        com.eu.habbo.habbohotel.rooms.RoomTile front =
                this.getExactFrontTile(
                        room,
                        slotItem
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

        if(this.isOnTile(unit, front))
        {
            this.pendingOpenTokens.remove(
                    userId,
                    token
            );

            this.openMachine(
                    client,
                    room,
                    slotItem
            );

            return;
        }

        java.util.ArrayList<Runnable> onSuccess =
                new java.util.ArrayList<>();

        java.util.ArrayList<Runnable> onFail =
                new java.util.ArrayList<>();

        final int roomId = room.getId();
        final int itemId = slotItem.getId();
        final short targetX = front.x;
        final short targetY = front.y;

        onSuccess.add(
                () -> this.completePendingOpen(
                        userId,
                        roomId,
                        itemId,
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

        unit.setGoalLocation(front);

        Emulator.getThreading().run(
                new com.eu.habbo.threading.runnables.RoomUnitWalkToLocation(
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

        Habbo habbo =
                Emulator.getGameEnvironment()
                        .getHabboManager()
                        .getHabbo(userId);

        if(habbo == null ||
                habbo.getClient() == null ||
                habbo.getRoomUnit() == null ||
                habbo.getRoomUnit().getCurrentLocation() == null)
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
                room.getHabboItem(itemId);

        if(!(current instanceof InteractionSlotMachine))
        {
            this.pendingOpenTokens.remove(
                    userId,
                    token
            );
            return;
        }

        InteractionSlotMachine slotItem =
                (InteractionSlotMachine)current;

        com.eu.habbo.habbohotel.rooms.RoomTile front =
                this.getExactFrontTile(
                        room,
                        slotItem
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
                token
        ))
        {
            return;
        }

        this.openMachine(
                habbo.getClient(),
                room,
                slotItem
        );
    }

    private com.eu.habbo.habbohotel.rooms.RoomTile getExactFrontTile(
            Room room,
            InteractionSlotMachine item)
    {
        if(room == null ||
                room.getLayout() == null ||
                item == null)
        {
            return null;
        }

        com.eu.habbo.habbohotel.rooms.RoomTile machineTile =
                room.getLayout()
                        .getTile(
                                item.getX(),
                                item.getY()
                        );

        if(machineTile == null)
        {
            return null;
        }

        // Los sprites de las tragaperras tienen su frontal visual
        // desplazado +2 respecto a la rotacion numerica del furni.
        //
        // Ejemplo:
        //   item rot 0 -> frente visual rot 2 -> x+1
        int visualFrontRotation =
                (item.getRotation() + 2) % 8;

        return room.getLayout()
                .getTileInFront(
                        machineTile,
                        visualFrontRotation
                );
    }

    private boolean isOnTile(
            com.eu.habbo.habbohotel.rooms.RoomUnit unit,
            com.eu.habbo.habbohotel.rooms.RoomTile tile)
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
            InteractionSlotMachine item)
    {
        if(habbo == null ||
                habbo.getRoomUnit() == null)
        {
            return false;
        }

        com.eu.habbo.habbohotel.rooms.RoomTile front =
                this.getExactFrontTile(
                        room,
                        item
                );

        return this.isOnTile(
                habbo.getRoomUnit(),
                front
        );
    }

    private void faceMachine(
            Habbo habbo,
            InteractionSlotMachine item)
    {
        if(habbo == null ||
                habbo.getRoomUnit() == null ||
                item == null)
        {
            return;
        }

        com.eu.habbo.habbohotel.rooms.RoomUnit unit =
                habbo.getRoomUnit();

        int rotation =
                com.eu.habbo.util.pathfinding.Rotation.Calculate(
                        unit.getX(),
                        unit.getY(),
                        item.getX(),
                        item.getY()
                );

        unit.setRotation(
                com.eu.habbo.habbohotel.rooms.RoomUserRotation
                        .values()[rotation]
        );

        unit.statusUpdate(true);
    }

    private void watchSessionPosition(
            int userId,
            SlotSession session)
    {
        Emulator.getThreading().run(
                () ->
                {
                    if(session == null ||
                            this.sessions.get(userId) != session)
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
                                "La sesion de la tragaperras ha caducado."
                        );
                        return;
                    }

                    Room room =
                            habbo.getHabboInfo()
                                    .getCurrentRoom();

                    if(room == null ||
                            room.getId() != session.roomId)
                    {
                        this.invalidateSessionAndClose(
                                userId,
                                session,
                                "Has abandonado la tragaperras."
                        );
                        return;
                    }

                    HabboItem current =
                            room.getHabboItem(
                                    session.itemId
                            );

                    if(!(current instanceof InteractionSlotMachine) ||
                            ((InteractionSlotMachine)current)
                                    .getSlotCurrency() !=
                                    session.currency)
                    {
                        this.invalidateSessionAndClose(
                                userId,
                                session,
                                "La tragaperras ya no esta disponible."
                        );
                        return;
                    }

                    if(!this.isExactlyInFront(
                            habbo,
                            room,
                            (InteractionSlotMachine)current
                    ))
                    {
                        this.invalidateSessionAndClose(
                                userId,
                                session,
                                "Te has alejado de la tragaperras."
                        );
                        return;
                    }

                    this.watchSessionPosition(
                            userId,
                            session
                    );
                },
                150L
        );
    }

    private void invalidateSessionAndClose(
            int userId,
            SlotSession session,
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
                reason
        );
    }

    private void sendClose(
            GameClient client,
            int itemId,
            String reason)
    {
        if(client == null)
        {
            return;
        }

        ServerMessage packet =
                new ServerMessage(
                        TragaperrasPlugin.PACKET_CLOSE
                );

        packet.appendInt(itemId);
        packet.appendString(
                reason == null ? "" : reason
        );

        client.sendResponse(packet);
    }
    public void openMachine(
            GameClient client,
            Room room,
            InteractionSlotMachine item)
    {
        if(client == null ||
                client.getHabbo() == null ||
                room == null ||
                item == null)
        {
            return;
        }

        SlotCurrency currency = item.getSlotCurrency();
        SlotEconomy economy = this.economies.get(currency);

        // Interactions futuras pueden estar registradas sin economia activa.
        if(currency == null || economy == null)
        {
            return;
        }

        HabboItem current = room.getHabboItem(item.getId());

        if(current == null ||
                !(current instanceof InteractionSlotMachine) ||
                ((InteractionSlotMachine)current).getSlotCurrency() != currency)
        {
            return;
        }

        if(!this.isExactlyInFront(
                client.getHabbo(),
                room,
                (InteractionSlotMachine)current
        ))
        {
            return;
        }

        this.faceMachine(
                client.getHabbo(),
                (InteractionSlotMachine)current
        );

        int userId =
                client.getHabbo()
                        .getHabboInfo()
                        .getId();

        SlotSession session =
                new SlotSession(
                        room.getId(),
                        item.getId(),
                        currency,
                        economy,
                        System.currentTimeMillis() + SESSION_TTL_MS
                );

        this.sessions.put(
                userId,
                session
        );

        try
        {
            long jackpot = this.readJackpot(currency);

            ServerMessage packet =
                    new ServerMessage(
                            TragaperrasPlugin.PACKET_OPEN
                    );

            packet.appendInt(item.getId());
            packet.appendInt(economy.bet);
            packet.appendInt(safeInt(jackpot));
            packet.appendInt(
                    currency.getBalance(
                            client.getHabbo()
                    )
            );
            packet.appendString(currency.getKey());
            packet.appendInt(currency.getClientType());

            client.sendResponse(packet);

            this.watchSessionPosition(
                    userId,
                    session
            );
        }
        catch(Exception error)
        {
            this.sessions.remove(userId);
            System.out.println(
                    "[Tragaperras] ERROR abriendo maquina: " +
                    error.getClass().getSimpleName() +
                    ": " +
                    error.getMessage()
            );
        }
    }

    public void spin(GameClient client, int itemId)
    {
        if(client == null || client.getHabbo() == null)
        {
            return;
        }

        Habbo habbo = client.getHabbo();
        int userId = habbo.getHabboInfo().getId();

        SlotSession session = this.sessions.get(userId);
        SlotCurrency failureCurrency =
                this.resolveCurrencyForFailure(
                        habbo,
                        session,
                        itemId
                );

        if(!this.validSession(habbo, session, itemId))
        {
            this.sessions.remove(userId);

            this.sendClose(
                    client,
                    itemId,
                    "Debes estar exactamente delante de la tragaperras."
            );

            return;
        }

        ReentrantLock lock =
                this.spinLocks.computeIfAbsent(
                        userId,
                        ignored -> new ReentrantLock()
                );

        if(!lock.tryLock())
        {
            this.sendFailure(
                    client,
                    itemId,
                    "La tirada anterior aun se esta procesando.",
                    session.currency
            );
            return;
        }

        try
        {
            SlotSpinResult result;

            synchronized(habbo.getHabboInfo())
            {
                result =
                        this.executeSpinTransaction(
                                habbo,
                                session,
                                itemId
                        );
            }

            session.expiresAt =
                    System.currentTimeMillis() +
                    SESSION_TTL_MS;

            this.sendSuccess(client, result);

            // 5045: todas las sesiones abiertas de ESTA moneda ven
            // inmediatamente el nuevo bote autoritativo.
            this.broadcastJackpotState(
                    session.currency,
                    result.jackpotAfter
            );

            if(result.jackpotHit)
            {
                // El resultado ya esta confirmado: executeSpinTransaction()
                // hizo connection.commit() antes de devolver SlotSpinResult.
                // Este es el UNICO callsite del aviso global de jackpot.
                this.broadcastGlobalJackpotAnnouncement(
                        habbo,
                        session.currency,
                        result.jackpotPrize
                );
            }
        }
        catch(InsufficientCurrencyException error)
        {
            this.sendFailure(
                    client,
                    itemId,
                    "No tienes suficientes " +
                    error.currency.getDisplayName() +
                    ".",
                    error.currency
            );
        }
        catch(CurrencyCompensationException error)
        {
            System.out.println(
                    "[Tragaperras] CRITICAL compensacion de saldo fallida user=" +
                    userId +
                    ": " +
                    error.getMessage()
            );

            error.printStackTrace();

            this.sendFailure(
                    client,
                    itemId,
                    "La tirada quedo en revision por un error de saldo. No repitas la operacion y avisa a un administrador.",
                    session.currency
            );
        }
        catch(Exception error)
        {
            System.out.println(
                    "[Tragaperras] ERROR en tirada user=" +
                    userId +
                    ": " +
                    error.getClass().getSimpleName() +
                    ": " +
                    error.getMessage()
            );

            error.printStackTrace();

            this.sendFailure(
                    client,
                    itemId,
                    "No se pudo completar la tirada. Cualquier movimiento aplicado fue revertido.",
                    session.currency
            );
        }
        finally
        {
            lock.unlock();
        }
    }

    private boolean validSession(
            Habbo habbo,
            SlotSession session,
            int itemId)
    {
        if(session == null ||
                session.itemId != itemId ||
                session.expiresAt < System.currentTimeMillis())
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
                room.getHabboItem(itemId);

        if(!(item instanceof InteractionSlotMachine))
        {
            return false;
        }

        InteractionSlotMachine slotItem =
                (InteractionSlotMachine)item;

        return slotItem.getSlotCurrency() ==
                        session.currency &&
                this.economies.get(session.currency) ==
                        session.economy &&
                this.isExactlyInFront(
                        habbo,
                        room,
                        slotItem
                );
    }

    private SlotCurrency resolveCurrencyForFailure(
            Habbo habbo,
            SlotSession session,
            int itemId)
    {
        if(session != null)
        {
            return session.currency;
        }

        if(habbo != null &&
                habbo.getHabboInfo() != null)
        {
            Room room =
                    habbo.getHabboInfo()
                            .getCurrentRoom();

            if(room != null)
            {
                HabboItem item =
                        room.getHabboItem(itemId);

                if(item instanceof InteractionSlotMachine)
                {
                    return ((InteractionSlotMachine)item)
                            .getSlotCurrency();
                }
            }
        }

        // Mientras solo Creditos esta habilitado, es el fallback seguro.
        return SlotCurrency.CREDITS;
    }

    private SlotSpinResult executeSpinTransaction(
            Habbo habbo,
            SlotSession session,
            int itemId) throws Exception
    {
        Connection connection = null;
        boolean movementApplied = false;
        int appliedDelta = 0;
        int balanceBefore = 0;

        SlotCurrency currency = session.currency;
        SlotEconomy economy = session.economy;

        try
        {
            connection =
                    Emulator.getDatabase()
                            .getDataSource()
                            .getConnection();

            connection.setAutoCommit(false);

            int userId =
                    habbo.getHabboInfo()
                            .getId();

            balanceBefore =
                    currency.getBalance(habbo);

            if(balanceBefore < economy.bet)
            {
                throw new InsufficientCurrencyException(
                        currency
                );
            }

            long jackpotBefore;
            long treasuryBefore;
            long spinsSinceJackpot;

            try(PreparedStatement stateQuery =
                        connection.prepareStatement(
                                "SELECT jackpot, treasury, spins_since_jackpot " +
                                "FROM holo_slots_state " +
                                "WHERE currency = ? FOR UPDATE"
                        ))
            {
                stateQuery.setString(
                        1,
                        currency.getKey()
                );

                try(ResultSet result =
                            stateQuery.executeQuery())
                {
                    if(!result.next())
                    {
                        throw new IllegalStateException(
                                "Estado de " +
                                currency.getKey() +
                                " no inicializado."
                        );
                    }

                    jackpotBefore =
                            result.getLong("jackpot");

                    treasuryBefore =
                            result.getLong("treasury");

                    spinsSinceJackpot =
                            result.getLong(
                                    "spins_since_jackpot"
                            );
                }
            }

            long jackpot =
                    jackpotBefore +
                    economy.jackpotContribution;

            long treasury =
                    treasuryBefore +
                    economy.treasuryContribution;

            boolean jackpotHit =
                    this.rollJackpot(
                            economy,
                            treasury
                    );

            int normalPrize = 0;
            long jackpotPrize = 0L;

            int symbol1;
            int symbol2;
            int symbol3;

            if(jackpotHit)
            {
                jackpotPrize = jackpot;

                treasury -=
                        economy.initialJackpot;

                jackpot =
                        economy.initialJackpot;

                spinsSinceJackpot = 0L;

                symbol1 = SYMBOL_JACKPOT;
                symbol2 = SYMBOL_JACKPOT;
                symbol3 = SYMBOL_JACKPOT;
            }
            else
            {
                normalPrize =
                        this.rollNormalPrize(
                                economy
                        );

                if(normalPrize > treasury)
                {
                    normalPrize = 0;
                }

                if(normalPrize > 0)
                {
                    treasury -=
                            normalPrize;

                    int symbol =
                            this.symbolForPrize(
                                    economy,
                                    normalPrize
                            );

                    symbol1 = symbol;
                    symbol2 = symbol;
                    symbol3 = symbol;
                }
                else
                {
                    int[] symbols =
                            this.nonWinningSymbols();

                    symbol1 = symbols[0];
                    symbol2 = symbols[1];
                    symbol3 = symbols[2];
                }

                spinsSinceJackpot++;
            }

            if(treasury < 0L)
            {
                throw new IllegalStateException(
                        "Tesoreria negativa; transaccion cancelada."
                );
            }

            long totalPrize =
                    (long)normalPrize +
                    jackpotPrize;

            long deltaLong =
                    totalPrize -
                    economy.bet;

            long expectedBalanceLong =
                    (long)balanceBefore +
                    deltaLong;

            if(deltaLong > Integer.MAX_VALUE ||
                    deltaLong < Integer.MIN_VALUE ||
                    expectedBalanceLong > Integer.MAX_VALUE ||
                    expectedBalanceLong < 0L)
            {
                throw new IllegalStateException(
                        "El movimiento de saldo excede los limites permitidos."
                );
            }

            appliedDelta =
                    (int)deltaLong;

            int balanceAfter =
                    this.applyNativeCurrency(
                            currency,
                            habbo,
                            appliedDelta,
                            balanceBefore
                    );

            movementApplied =
                    appliedDelta != 0;

            try(PreparedStatement updateState =
                        connection.prepareStatement(
                                "UPDATE holo_slots_state SET " +
                                "jackpot = ?, " +
                                "treasury = ?, " +
                                "total_wagered = total_wagered + ?, " +
                                "total_burn = total_burn + ?, " +
                                "total_normal_payout = total_normal_payout + ?, " +
                                "total_jackpot_payout = total_jackpot_payout + ?, " +
                                "total_spins = total_spins + 1, " +
                                "jackpot_count = jackpot_count + ?, " +
                                "spins_since_jackpot = ?, " +
                                "updated_at = CURRENT_TIMESTAMP " +
                                "WHERE currency = ?"
                        ))
            {
                updateState.setLong(1, jackpot);
                updateState.setLong(2, treasury);
                updateState.setInt(3, economy.bet);
                updateState.setInt(4, economy.burnPerSpin);
                updateState.setInt(5, normalPrize);
                updateState.setLong(6, jackpotPrize);
                updateState.setInt(7, jackpotHit ? 1 : 0);
                updateState.setLong(8, spinsSinceJackpot);
                updateState.setString(
                        9,
                        currency.getKey()
                );

                if(updateState.executeUpdate() != 1)
                {
                    throw new IllegalStateException(
                            "No se pudo actualizar el estado del casino."
                    );
                }
            }

            try(PreparedStatement audit =
                        connection.prepareStatement(
                                "INSERT INTO holo_slots_spins (" +
                                "currency, user_id, room_id, item_id, " +
                                "bet, burn, normal_prize, jackpot_prize, " +
                                "jackpot_hit, balance_before, balance_after, " +
                                "jackpot_after, treasury_after" +
                                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                        ))
            {
                audit.setString(
                        1,
                        currency.getKey()
                );
                audit.setInt(2, userId);
                audit.setInt(3, session.roomId);
                audit.setInt(4, itemId);
                audit.setInt(5, economy.bet);
                audit.setInt(6, economy.burnPerSpin);
                audit.setInt(7, normalPrize);
                audit.setLong(8, jackpotPrize);
                audit.setBoolean(9, jackpotHit);
                audit.setInt(10, balanceBefore);
                audit.setInt(11, balanceAfter);
                audit.setLong(12, jackpot);
                audit.setLong(13, treasury);
                audit.executeUpdate();
            }

            connection.commit();

            return new SlotSpinResult(
                    itemId,
                    balanceBefore,
                    balanceAfter,
                    normalPrize,
                    jackpotPrize,
                    jackpotHit,
                    jackpot,
                    treasury,
                    symbol1,
                    symbol2,
                    symbol3
            );
        }
        catch(Exception error)
        {
            if(connection != null)
            {
                try
                {
                    connection.rollback();
                }
                catch(Exception ignored)
                {
                }
            }

            if(movementApplied)
            {
                boolean compensated =
                        this.compensateNativeCurrency(
                                currency,
                                habbo,
                                -appliedDelta
                        );

                if(!compensated)
                {
                    throw new CurrencyCompensationException(
                            "No se pudo restaurar automaticamente el saldo de " +
                            currency.getKey() +
                            " despues de: " +
                            error.getClass().getSimpleName() +
                            ": " +
                            error.getMessage(),
                            error
                    );
                }
            }

            throw error;
        }
        finally
        {
            if(connection != null)
            {
                try
                {
                    connection.setAutoCommit(true);
                }
                catch(Exception ignored)
                {
                }

                try
                {
                    connection.close();
                }
                catch(Exception ignored)
                {
                }
            }
        }
    }

    private int applyNativeCurrency(
            SlotCurrency currency,
            Habbo habbo,
            int delta,
            int balanceBefore) throws Exception
    {
        if(delta == 0)
        {
            return balanceBefore;
        }

        long expectedLong =
                (long)balanceBefore +
                delta;

        if(expectedLong < 0L ||
                expectedLong > Integer.MAX_VALUE)
        {
            throw new IllegalStateException(
                    "Saldo resultante fuera de rango."
            );
        }

        currency.applyDelta(
                habbo,
                delta
        );

        int balanceAfter =
                currency.getBalance(
                        habbo
                );

        if(balanceAfter != (int)expectedLong)
        {
            throw new CurrencyMovementException(
                    "Morningstar no aplico exactamente el movimiento esperado de " +
                    currency.getKey() +
                    ". Antes=" +
                    balanceBefore +
                    ", delta=" +
                    delta +
                    ", despues=" +
                    balanceAfter
            );
        }

        return balanceAfter;
    }

    private boolean compensateNativeCurrency(
            SlotCurrency currency,
            Habbo habbo,
            int delta)
    {
        if(delta == 0)
        {
            return true;
        }

        try
        {
            int before =
                    currency.getBalance(
                            habbo
                    );

            long expected =
                    (long)before +
                    delta;

            if(expected < 0L ||
                    expected > Integer.MAX_VALUE)
            {
                return false;
            }

            currency.applyDelta(
                    habbo,
                    delta
            );

            int after =
                    currency.getBalance(
                            habbo
                    );

            return after == (int)expected;
        }
        catch(Exception error)
        {
            System.out.println(
                    "[Tragaperras] CRITICAL error compensando " +
                    currency.getKey() +
                    ": " +
                    error.getClass().getSimpleName() +
                    ": " +
                    error.getMessage()
            );

            return false;
        }
    }

    private boolean rollJackpot(
            SlotEconomy economy,
            long treasury)
    {
        int denominator =
                economy.jackpotDenominator(
                        treasury
                );

        return denominator > 0 &&
                this.random.nextInt(denominator) == 0;
    }

    private int rollNormalPrize(SlotEconomy economy)
    {
        int roll =
                this.random.nextInt(
                        economy.prizeRollRange
                );

        for(int i = 0;
                i < economy.prizeAmounts.length;
                i++)
        {
            int weight =
                    economy.prizeWeights[i];

            if(roll < weight)
            {
                return economy.prizeAmounts[i];
            }

            roll -= weight;
        }

        return 0;
    }

    private int symbolForPrize(
            SlotEconomy economy,
            int prize)
    {
        for(int i = 0;
                i < economy.prizeAmounts.length;
                i++)
        {
            if(economy.prizeAmounts[i] == prize)
            {
                return economy.prizeSymbols[i];
            }
        }

        return SYMBOL_CHERRY;
    }

    private int[] nonWinningSymbols()
    {
        int first =
                this.random.nextInt(6);

        int second =
                this.random.nextInt(6);

        int third =
                this.random.nextInt(6);

        // Nunca mostrar un triple "ganador" cuando el resultado real es 0.
        if(first == second && second == third)
        {
            third =
                    (third + 1) % 6;
        }

        return new int[] {
                first,
                second,
                third
        };
    }

    private long readJackpot(
            SlotCurrency currency) throws Exception
    {
        try(Connection connection =
                    Emulator.getDatabase()
                            .getDataSource()
                            .getConnection();
            PreparedStatement query =
                    connection.prepareStatement(
                            "SELECT jackpot FROM holo_slots_state " +
                            "WHERE currency = ?"
                    ))
        {
            query.setString(
                    1,
                    currency.getKey()
            );

            try(ResultSet result =
                        query.executeQuery())
            {
                if(!result.next())
                {
                    throw new IllegalStateException(
                            "Estado " +
                            currency.getKey() +
                            " no encontrado."
                    );
                }

                return result.getLong("jackpot");
            }
        }
    }

    private void broadcastGlobalJackpotAnnouncement(
            Habbo winner,
            SlotCurrency currency,
            long jackpotPrize)
    {
        if(winner == null ||
                currency == null ||
                jackpotPrize <= 0L)
        {
            return;
        }

        String username =
                winner.getHabboInfo()
                        .getUsername();

        if(username == null || username.isBlank())
        {
            username = "Un usuario";
        }

        String currencyName;

        switch(currency)
        {
            case CREDITS:
                currencyName = "cr\u00e9ditos";
                break;

            case DIAMONDS:
                currencyName = "diamantes";
                break;

            case DUCKETS:
                currencyName = "duckets";
                break;

            default:
                currencyName = currency.getDisplayName();
                break;
        }

        String message =
                "\u00a1JACKPOT! " +
                username +
                " ha ganado " +
                jackpotPrize +
                " " +
                currencyName +
                " en las tragaperras.";

        for(Habbo target :
                Emulator.getGameEnvironment()
                        .getHabboManager()
                        .getOnlineHabbos()
                        .values())
        {
            if(target == null ||
                    target.getClient() == null)
            {
                continue;
            }

            try
            {
                // Reutiliza el aviso global de Subastas/Nitro.
                ServerMessage packet =
                        new ServerMessage(5014);

                packet.appendString(message);

                target.getClient()
                        .sendResponse(packet);
            }
            catch(Exception error)
            {
                // Un cliente roto no impide el aviso al resto del hotel.
                System.out.println(
                        "[Tragaperras] Aviso jackpot 5014 fallo: " +
                        error.getMessage()
                );
            }
        }

        System.out.println(
                "[Tragaperras] JACKPOT GLOBAL: " +
                message
        );
    }

    private void broadcastJackpotState(
            SlotCurrency currency,
            long jackpot)
    {
        if(currency == null)
        {
            return;
        }

        long now = System.currentTimeMillis();

        for(Map.Entry<Integer, SlotSession> entry :
                this.sessions.entrySet())
        {
            int userId = entry.getKey();
            SlotSession session = entry.getValue();

            if(session == null ||
                    session.currency != currency)
            {
                continue;
            }

            if(session.expiresAt < now)
            {
                this.sessions.remove(userId, session);
                continue;
            }

            try
            {
                Habbo target =
                        Emulator.getGameEnvironment()
                                .getHabboManager()
                                .getHabbo(userId);

                if(target == null ||
                        target.getClient() == null)
                {
                    this.sessions.remove(userId, session);
                    continue;
                }

                Room currentRoom =
                        target.getHabboInfo()
                                .getCurrentRoom();

                if(currentRoom == null ||
                        currentRoom.getId() != session.roomId)
                {
                    this.sessions.remove(userId, session);
                    continue;
                }

                HabboItem currentItem =
                        currentRoom.getHabboItem(
                                session.itemId
                        );

                if(!(currentItem instanceof InteractionSlotMachine) ||
                        ((InteractionSlotMachine)currentItem)
                                .getSlotCurrency() != currency)
                {
                    this.sessions.remove(userId, session);
                    continue;
                }

                ServerMessage packet =
                        new ServerMessage(
                                TragaperrasPlugin.PACKET_STATE
                        );

                packet.appendString(currency.getKey());
                packet.appendInt(safeInt(jackpot));

                target.getClient()
                        .sendResponse(packet);
            }
            catch(Exception error)
            {
                System.out.println(
                        "[Tragaperras] Aviso 5045 user=" +
                        userId +
                        ": " +
                        error.getMessage()
                );
            }
        }
    }

    private void sendSuccess(
            GameClient client,
            SlotSpinResult result)
    {
        ServerMessage packet =
                new ServerMessage(
                        TragaperrasPlugin.PACKET_RESULT
                );

        packet.appendBoolean(true);
        packet.appendString("");
        packet.appendInt(result.itemId);
        packet.appendInt(result.balanceAfter);
        packet.appendInt(safeInt(result.jackpotAfter));
        packet.appendInt(result.normalPrize);
        packet.appendInt(safeInt(result.jackpotPrize));
        packet.appendBoolean(result.jackpotHit);
        packet.appendInt(result.symbol1);
        packet.appendInt(result.symbol2);
        packet.appendInt(result.symbol3);

        client.sendResponse(packet);
    }

    private void sendFailure(
            GameClient client,
            int itemId,
            String message,
            SlotCurrency currency)
    {
        if(client == null)
        {
            return;
        }

        SlotCurrency safeCurrency =
                currency == null ?
                        SlotCurrency.CREDITS :
                        currency;

        int balance = 0;

        if(client.getHabbo() != null)
        {
            balance =
                    safeCurrency.getBalance(
                            client.getHabbo()
                    );
        }

        long jackpot = 0L;

        if(this.economies.containsKey(safeCurrency))
        {
            try
            {
                jackpot =
                        this.readJackpot(
                                safeCurrency
                        );
            }
            catch(Exception ignored)
            {
            }
        }

        ServerMessage packet =
                new ServerMessage(
                        TragaperrasPlugin.PACKET_RESULT
                );

        packet.appendBoolean(false);
        packet.appendString(message == null ? "" : message);
        packet.appendInt(itemId);
        packet.appendInt(balance);
        packet.appendInt(safeInt(jackpot));
        packet.appendInt(0);
        packet.appendInt(0);
        packet.appendBoolean(false);
        packet.appendInt(-1);
        packet.appendInt(-1);
        packet.appendInt(-1);

        client.sendResponse(packet);
    }

    private static int safeInt(long value)
    {
        if(value > Integer.MAX_VALUE)
        {
            return Integer.MAX_VALUE;
        }

        if(value < Integer.MIN_VALUE)
        {
            return Integer.MIN_VALUE;
        }

        return (int)value;
    }

    private static final class SlotSession
    {
        private final int roomId;
        private final int itemId;
        private final SlotCurrency currency;
        private final SlotEconomy economy;
        private volatile long expiresAt;

        private SlotSession(
                int roomId,
                int itemId,
                SlotCurrency currency,
                SlotEconomy economy,
                long expiresAt)
        {
            this.roomId = roomId;
            this.itemId = itemId;
            this.currency = currency;
            this.economy = economy;
            this.expiresAt = expiresAt;
        }
    }

    private static final class SlotEconomy
    {
        private final SlotCurrency currency;
        private final int bet;
        private final int burnPerSpin;
        private final int jackpotContribution;
        private final int treasuryContribution;
        private final long initialJackpot;
        private final long initialTreasury;
        private final long initialAdminInjected;

        private final int[] prizeAmounts;
        private final int[] prizeWeights;
        private final int[] prizeSymbols;
        private final int prizeRollRange;

        private final long jackpotMinTreasury;
        private final long[] jackpotUpperBounds;
        private final int[] jackpotDenominators;

        private SlotEconomy(
                SlotCurrency currency,
                int bet,
                int burnPerSpin,
                int jackpotContribution,
                int treasuryContribution,
                long initialJackpot,
                long initialTreasury,
                long initialAdminInjected,
                int[] prizeAmounts,
                int[] prizeWeights,
                int[] prizeSymbols,
                int prizeRollRange,
                long jackpotMinTreasury,
                long[] jackpotUpperBounds,
                int[] jackpotDenominators)
        {
            if(currency == null)
            {
                throw new IllegalArgumentException("currency null.");
            }

            if(bet <= 0 ||
                    burnPerSpin < 0 ||
                    jackpotContribution < 0 ||
                    treasuryContribution < 0 ||
                    initialJackpot < 0L ||
                    initialTreasury < 0L ||
                    initialAdminInjected < 0L)
            {
                throw new IllegalArgumentException(
                        "Economia invalida para " +
                        currency.getKey()
                );
            }

            if(prizeAmounts == null ||
                    prizeWeights == null ||
                    prizeSymbols == null ||
                    prizeAmounts.length == 0 ||
                    prizeAmounts.length != prizeWeights.length ||
                    prizeAmounts.length != prizeSymbols.length ||
                    prizeRollRange <= 0)
            {
                throw new IllegalArgumentException(
                        "Tabla de premios invalida para " +
                        currency.getKey()
                );
            }

            if(jackpotUpperBounds == null ||
                    jackpotDenominators == null ||
                    jackpotDenominators.length !=
                            jackpotUpperBounds.length + 1)
            {
                throw new IllegalArgumentException(
                        "Tabla de jackpot invalida para " +
                        currency.getKey()
                );
            }

            int totalWeight = 0;

            for(int weight : prizeWeights)
            {
                if(weight < 0)
                {
                    throw new IllegalArgumentException(
                            "Peso de premio negativo."
                    );
                }

                totalWeight += weight;
            }

            if(totalWeight > prizeRollRange)
            {
                throw new IllegalArgumentException(
                        "Pesos de premio exceden el rango."
                );
            }

            this.currency = currency;
            this.bet = bet;
            this.burnPerSpin = burnPerSpin;
            this.jackpotContribution = jackpotContribution;
            this.treasuryContribution = treasuryContribution;
            this.initialJackpot = initialJackpot;
            this.initialTreasury = initialTreasury;
            this.initialAdminInjected = initialAdminInjected;
            this.prizeAmounts = prizeAmounts.clone();
            this.prizeWeights = prizeWeights.clone();
            this.prizeSymbols = prizeSymbols.clone();
            this.prizeRollRange = prizeRollRange;
            this.jackpotMinTreasury = jackpotMinTreasury;
            this.jackpotUpperBounds = jackpotUpperBounds.clone();
            this.jackpotDenominators = jackpotDenominators.clone();
        }

        private int jackpotDenominator(long treasury)
        {
            if(treasury < this.jackpotMinTreasury)
            {
                return 0;
            }

            for(int i = 0;
                    i < this.jackpotUpperBounds.length;
                    i++)
            {
                if(treasury < this.jackpotUpperBounds[i])
                {
                    return this.jackpotDenominators[i];
                }
            }

            return this.jackpotDenominators[
                    this.jackpotDenominators.length - 1
            ];
        }
    }

    private static final class CurrencyMovementException
            extends Exception
    {
        private CurrencyMovementException(String message)
        {
            super(message);
        }
    }

    private static final class CurrencyCompensationException
            extends Exception
    {
        private CurrencyCompensationException(
                String message,
                Throwable cause)
        {
            super(message, cause);
        }
    }

    private static final class InsufficientCurrencyException
            extends Exception
    {
        private final SlotCurrency currency;

        private InsufficientCurrencyException(
                SlotCurrency currency)
        {
            this.currency = currency;
        }
    }
}