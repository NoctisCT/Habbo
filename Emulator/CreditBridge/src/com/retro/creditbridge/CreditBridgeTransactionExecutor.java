package com.retro.creditbridge;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.users.UserCreditsComposer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CreditBridgeTransactionExecutor
{
    private static final int STATUS_OK = 0;
    private static final int STATUS_ERROR = 1;
    private static final int SYSTEM_ERROR = 4;

    public Result execute(
        Habbo habbo,
        int userId,
        int amount,
        String transactionId,
        String operation
    )
    {
        synchronized (habbo.getHabboInfo())
        {
            try
            {
                TransactionRecord existing =
                    this.findTransaction(transactionId);

                if (existing != null)
                {
                    if (
                        existing.userId != userId ||
                        existing.amount != amount ||
                        !existing.operation.equals(operation)
                    )
                    {
                        return new Result(
                            STATUS_ERROR,
                            "transaction_conflict"
                        );
                    }

                    if ("applied".equals(existing.status))
                    {
                        return this.appliedResult(existing);
                    }

                    return this.recoverPending(
                        habbo,
                        existing
                    );
                }

                TransactionRecord pending =
                    this.findPendingForUser(userId);

                if (pending != null)
                {
                    return new Result(
                        STATUS_ERROR,
                        "pending_transaction|" +
                        pending.transactionId
                    );
                }

                int memoryBalance =
                    habbo.getHabboInfo().getCredits();

                int databaseBalance =
                    this.readDatabaseBalance(userId);

                if (memoryBalance != databaseBalance)
                {
                    return new Result(
                        SYSTEM_ERROR,
                        "balance_mismatch|" +
                        memoryBalance +
                        "|" +
                        databaseBalance
                    );
                }

                if (
                    "debit".equals(operation) &&
                    memoryBalance < amount
                )
                {
                    return new Result(
                        STATUS_ERROR,
                        "insufficient_funds|" +
                        memoryBalance
                    );
                }

                long calculatedAfter;

                if ("debit".equals(operation))
                {
                    calculatedAfter =
                        (long) memoryBalance - amount;
                }
                else if ("credit".equals(operation))
                {
                    calculatedAfter =
                        (long) memoryBalance + amount;
                }
                else
                {
                    return new Result(
                        STATUS_ERROR,
                        "invalid_operation"
                    );
                }

                if (
                    calculatedAfter < 0 ||
                    calculatedAfter > Integer.MAX_VALUE
                )
                {
                    return new Result(
                        STATUS_ERROR,
                        "balance_overflow"
                    );
                }

                TransactionRecord record =
                    new TransactionRecord();

                record.transactionId = transactionId;
                record.userId = userId;
                record.amount = amount;
                record.operation = operation;
                record.balanceBefore = memoryBalance;
                record.balanceAfter =
                    (int) calculatedAfter;
                record.status = "pending";

                if (!this.insertPending(record))
                {
                    TransactionRecord raced =
                        this.findTransaction(transactionId);

                    if (raced == null)
                    {
                        return new Result(
                            SYSTEM_ERROR,
                            "transaction_reservation_failed"
                        );
                    }

                    if (
                        raced.userId != userId ||
                        raced.amount != amount ||
                        !raced.operation.equals(operation)
                    )
                    {
                        return new Result(
                            STATUS_ERROR,
                            "transaction_conflict"
                        );
                    }

                    if ("applied".equals(raced.status))
                    {
                        return this.appliedResult(raced);
                    }

                    return this.recoverPending(
                        habbo,
                        raced
                    );
                }

                return this.applyPending(
                    habbo,
                    record
                );
            }
            catch (SQLException exception)
            {
                exception.printStackTrace();

                return new Result(
                    SYSTEM_ERROR,
                    "sql_error"
                );
            }
        }
    }

    private Result recoverPending(
        Habbo habbo,
        TransactionRecord record
    ) throws SQLException
    {
        int memoryBalance =
            habbo.getHabboInfo().getCredits();

        int databaseBalance =
            this.readDatabaseBalance(record.userId);

        boolean memoryBefore =
            memoryBalance == record.balanceBefore;

        boolean memoryAfter =
            memoryBalance == record.balanceAfter;

        boolean databaseBefore =
            databaseBalance == record.balanceBefore;

        boolean databaseAfter =
            databaseBalance == record.balanceAfter;

        if (
            (memoryBefore || memoryAfter) &&
            (databaseBefore || databaseAfter)
        )
        {
            return this.applyPending(
                habbo,
                record
            );
        }

        return new Result(
            SYSTEM_ERROR,
            "pending_balance_mismatch|" +
            memoryBalance +
            "|" +
            databaseBalance
        );
    }

    private Result applyPending(
        Habbo habbo,
        TransactionRecord record
    ) throws SQLException
    {
        int memoryBalance =
            habbo.getHabboInfo().getCredits();

        int databaseBalance =
            this.readDatabaseBalance(record.userId);

        boolean memoryBefore =
            memoryBalance == record.balanceBefore;

        boolean memoryAfter =
            memoryBalance == record.balanceAfter;

        boolean databaseBefore =
            databaseBalance == record.balanceBefore;

        boolean databaseAfter =
            databaseBalance == record.balanceAfter;

        if (
            !(
                (memoryBefore || memoryAfter) &&
                (databaseBefore || databaseAfter)
            )
        )
        {
            return new Result(
                SYSTEM_ERROR,
                "pending_balance_mismatch|" +
                memoryBalance +
                "|" +
                databaseBalance
            );
        }

        if (
            memoryBefore ||
            databaseBefore
        )
        {
            habbo.getHabboInfo()
                .setCredits(record.balanceAfter);
        }

        int finalMemoryBalance =
            habbo.getHabboInfo().getCredits();

        int finalDatabaseBalance =
            this.readDatabaseBalance(record.userId);

        if (
            finalMemoryBalance != record.balanceAfter ||
            finalDatabaseBalance != record.balanceAfter
        )
        {
            return new Result(
                SYSTEM_ERROR,
                "persistence_mismatch|" +
                finalMemoryBalance +
                "|" +
                finalDatabaseBalance
            );
        }

        if (!this.markApplied(record.transactionId))
        {
            TransactionRecord current =
                this.findTransaction(
                    record.transactionId
                );

            if (
                current == null ||
                !"applied".equals(current.status)
            )
            {
                return new Result(
                    SYSTEM_ERROR,
                    "mark_applied_failed"
                );
            }
        }

        if (habbo.getClient() != null)
        {
            habbo.getClient().sendResponse(
                new UserCreditsComposer(habbo)
            );
        }

        record.status = "applied";

        return this.appliedResult(record);
    }

    private Result appliedResult(
        TransactionRecord record
    )
    {
        String prefix =
            "credit".equals(record.operation)
                ? "credited"
                : "debited";

        return new Result(
            STATUS_OK,
            prefix +
            "|" +
            record.balanceBefore +
            "|" +
            record.balanceAfter
        );
    }

    private TransactionRecord findTransaction(
        String transactionId
    ) throws SQLException
    {
        try (
            Connection connection =
                Emulator.getDatabase()
                    .getDataSource()
                    .getConnection();

            PreparedStatement statement =
                connection.prepareStatement(
                    "SELECT transaction_id, user_id, amount, " +
                    "operation, balance_before, balance_after, status " +
                    "FROM credit_bridge_transactions " +
                    "WHERE transaction_id = ? LIMIT 1"
                )
        )
        {
            statement.setString(
                1,
                transactionId
            );

            try (ResultSet result = statement.executeQuery())
            {
                if (!result.next())
                {
                    return null;
                }

                return this.mapRecord(result);
            }
        }
    }

    private TransactionRecord findPendingForUser(
        int userId
    ) throws SQLException
    {
        try (
            Connection connection =
                Emulator.getDatabase()
                    .getDataSource()
                    .getConnection();

            PreparedStatement statement =
                connection.prepareStatement(
                    "SELECT transaction_id, user_id, amount, " +
                    "operation, balance_before, balance_after, status " +
                    "FROM credit_bridge_transactions " +
                    "WHERE user_id = ? AND status = 'pending' " +
                    "ORDER BY created_at ASC LIMIT 1"
                )
        )
        {
            statement.setInt(
                1,
                userId
            );

            try (ResultSet result = statement.executeQuery())
            {
                if (!result.next())
                {
                    return null;
                }

                return this.mapRecord(result);
            }
        }
    }

    private boolean insertPending(
        TransactionRecord record
    ) throws SQLException
    {
        try (
            Connection connection =
                Emulator.getDatabase()
                    .getDataSource()
                    .getConnection();

            PreparedStatement statement =
                connection.prepareStatement(
                    "INSERT INTO credit_bridge_transactions " +
                    "(transaction_id, user_id, amount, operation, " +
                    "balance_before, balance_after, status, " +
                    "created_at, updated_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, 'pending', NOW(), NOW())"
                )
        )
        {
            statement.setString(
                1,
                record.transactionId
            );

            statement.setInt(
                2,
                record.userId
            );

            statement.setInt(
                3,
                record.amount
            );

            statement.setString(
                4,
                record.operation
            );

            statement.setInt(
                5,
                record.balanceBefore
            );

            statement.setInt(
                6,
                record.balanceAfter
            );

            try
            {
                return statement.executeUpdate() == 1;
            }
            catch (SQLException exception)
            {
                if (
                    exception.getErrorCode() == 1062 ||
                    "23000".equals(
                        exception.getSQLState()
                    )
                )
                {
                    return false;
                }

                throw exception;
            }
        }
    }

    private int readDatabaseBalance(
        int userId
    ) throws SQLException
    {
        try (
            Connection connection =
                Emulator.getDatabase()
                    .getDataSource()
                    .getConnection();

            PreparedStatement statement =
                connection.prepareStatement(
                    "SELECT credits FROM users " +
                    "WHERE id = ? LIMIT 1"
                )
        )
        {
            statement.setInt(
                1,
                userId
            );

            try (ResultSet result = statement.executeQuery())
            {
                if (!result.next())
                {
                    throw new SQLException(
                        "User not found: " +
                        userId
                    );
                }

                return result.getInt(
                    "credits"
                );
            }
        }
    }

    private boolean markApplied(
        String transactionId
    ) throws SQLException
    {
        try (
            Connection connection =
                Emulator.getDatabase()
                    .getDataSource()
                    .getConnection();

            PreparedStatement statement =
                connection.prepareStatement(
                    "UPDATE credit_bridge_transactions " +
                    "SET status = 'applied', updated_at = NOW() " +
                    "WHERE transaction_id = ? " +
                    "AND status = 'pending' LIMIT 1"
                )
        )
        {
            statement.setString(
                1,
                transactionId
            );

            return statement.executeUpdate() == 1;
        }
    }

    private TransactionRecord mapRecord(
        ResultSet result
    ) throws SQLException
    {
        TransactionRecord record =
            new TransactionRecord();

        record.transactionId =
            result.getString(
                "transaction_id"
            );

        record.userId =
            result.getInt(
                "user_id"
            );

        record.amount =
            result.getInt(
                "amount"
            );

        record.operation =
            result.getString(
                "operation"
            );

        record.balanceBefore =
            result.getInt(
                "balance_before"
            );

        record.balanceAfter =
            result.getInt(
                "balance_after"
            );

        record.status =
            result.getString(
                "status"
            );

        return record;
    }

    public static class Result
    {
        public final int status;
        public final String message;

        public Result(
            int status,
            String message
        )
        {
            this.status = status;
            this.message = message;
        }
    }

    private static class TransactionRecord
    {
        public String transactionId;
        public int userId;
        public int amount;
        public String operation;
        public int balanceBefore;
        public int balanceAfter;
        public String status;
    }
}