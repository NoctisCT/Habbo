package com.retro.creditbridge;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.users.UserCreditsComposer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OfflineCreditTransactionExecutor
{
    public CreditBridgeTransactionExecutor.Result execute(
        int userId,
        int amount,
        String transactionId
    )
    {
        try (
            Connection connection =
                Emulator.getDatabase()
                    .getDataSource()
                    .getConnection()
        )
        {
            connection.setAutoCommit(false);

            try
            {
                TransactionRecord existing =
                    this.findTransaction(
                        connection,
                        transactionId
                    );

                if (existing != null)
                {
                    if (
                        existing.userId != userId ||
                        existing.amount != amount ||
                        !"credit".equals(existing.operation)
                    )
                    {
                        connection.rollback();

                        return new CreditBridgeTransactionExecutor.Result(
                            1,
                            "transaction_conflict"
                        );
                    }

                    if ("applied".equals(existing.status))
                    {
                        connection.commit();

                        this.syncIfOnline(
                            userId,
                            existing.balanceAfter
                        );

                        return this.applied(existing);
                    }

                    if (!"pending".equals(existing.status))
                    {
                        connection.rollback();

                        return new CreditBridgeTransactionExecutor.Result(
                            4,
                            "invalid_transaction_status"
                        );
                    }

                    int currentBalance =
                        this.readBalance(
                            connection,
                            userId
                        );

                    if (
                        currentBalance ==
                        existing.balanceBefore
                    )
                    {
                        this.updateBalance(
                            connection,
                            userId,
                            existing.balanceAfter
                        );
                    }
                    else if (
                        currentBalance !=
                        existing.balanceAfter
                    )
                    {
                        connection.rollback();

                        return new CreditBridgeTransactionExecutor.Result(
                            4,
                            "pending_balance_mismatch|" +
                            currentBalance
                        );
                    }

                    this.markApplied(
                        connection,
                        transactionId
                    );

                    connection.commit();

                    this.syncIfOnline(
                        userId,
                        existing.balanceAfter
                    );

                    existing.status = "applied";

                    return this.applied(existing);
                }

                int balanceBefore =
                    this.readBalance(
                        connection,
                        userId
                    );

                long calculatedAfter =
                    (long) balanceBefore + amount;

                if (
                    calculatedAfter > Integer.MAX_VALUE
                )
                {
                    connection.rollback();

                    return new CreditBridgeTransactionExecutor.Result(
                        1,
                        "balance_overflow"
                    );
                }

                int balanceAfter =
                    (int) calculatedAfter;

                TransactionRecord record =
                    new TransactionRecord();

                record.transactionId =
                    transactionId;

                record.userId =
                    userId;

                record.amount =
                    amount;

                record.operation =
                    "credit";

                record.balanceBefore =
                    balanceBefore;

                record.balanceAfter =
                    balanceAfter;

                record.status =
                    "pending";

                this.insertPending(
                    connection,
                    record
                );

                this.updateBalance(
                    connection,
                    userId,
                    balanceAfter
                );

                this.markApplied(
                    connection,
                    transactionId
                );

                connection.commit();

                this.syncIfOnline(
                    userId,
                    balanceAfter
                );

                record.status = "applied";

                return this.applied(record);
            }
            catch (SQLException exception)
            {
                connection.rollback();

                if (
                    exception.getErrorCode() == 1062 ||
                    "23000".equals(
                        exception.getSQLState()
                    )
                )
                {
                    return this.execute(
                        userId,
                        amount,
                        transactionId
                    );
                }

                exception.printStackTrace();

                return new CreditBridgeTransactionExecutor.Result(
                    4,
                    "sql_error"
                );
            }
            finally
            {
                try
                {
                    connection.setAutoCommit(true);
                }
                catch (SQLException ignored)
                {
                }
            }
        }
        catch (SQLException exception)
        {
            exception.printStackTrace();

            return new CreditBridgeTransactionExecutor.Result(
                4,
                "sql_error"
            );
        }
    }

    private TransactionRecord findTransaction(
        Connection connection,
        String transactionId
    ) throws SQLException
    {
        try (
            PreparedStatement statement =
                connection.prepareStatement(
                    "SELECT transaction_id, user_id, amount, " +
                    "operation, balance_before, balance_after, status " +
                    "FROM credit_bridge_transactions " +
                    "WHERE transaction_id = ? LIMIT 1 FOR UPDATE"
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
        }
    }

    private int readBalance(
        Connection connection,
        int userId
    ) throws SQLException
    {
        try (
            PreparedStatement statement =
                connection.prepareStatement(
                    "SELECT credits FROM users " +
                    "WHERE id = ? LIMIT 1 FOR UPDATE"
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

    private void insertPending(
        Connection connection,
        TransactionRecord record
    ) throws SQLException
    {
        try (
            PreparedStatement statement =
                connection.prepareStatement(
                    "INSERT INTO credit_bridge_transactions " +
                    "(transaction_id, user_id, amount, operation, " +
                    "balance_before, balance_after, status, " +
                    "created_at, updated_at) " +
                    "VALUES (?, ?, ?, 'credit', ?, ?, 'pending', NOW(), NOW())"
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

            statement.setInt(
                4,
                record.balanceBefore
            );

            statement.setInt(
                5,
                record.balanceAfter
            );

            if (statement.executeUpdate() != 1)
            {
                throw new SQLException(
                    "Could not reserve transaction."
                );
            }
        }
    }

    private void updateBalance(
        Connection connection,
        int userId,
        int balance
    ) throws SQLException
    {
        try (
            PreparedStatement statement =
                connection.prepareStatement(
                    "UPDATE users SET credits = ? " +
                    "WHERE id = ? LIMIT 1"
                )
        )
        {
            statement.setInt(
                1,
                balance
            );

            statement.setInt(
                2,
                userId
            );

            if (statement.executeUpdate() != 1)
            {
                throw new SQLException(
                    "Could not update balance."
                );
            }
        }
    }

    private void markApplied(
        Connection connection,
        String transactionId
    ) throws SQLException
    {
        try (
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

            if (statement.executeUpdate() != 1)
            {
                throw new SQLException(
                    "Could not mark transaction applied."
                );
            }
        }
    }

    private void syncIfOnline(
        int userId,
        int balance
    )
    {
        Habbo habbo =
            Emulator.getGameEnvironment()
                .getHabboManager()
                .getHabbo(userId);

        if (habbo == null)
        {
            return;
        }

        synchronized (habbo.getHabboInfo())
        {
            if (
                habbo.getHabboInfo().getCredits()
                != balance
            )
            {
                habbo.getHabboInfo()
                    .setCredits(balance);
            }

            if (habbo.getClient() != null)
            {
                habbo.getClient().sendResponse(
                    new UserCreditsComposer(habbo)
                );
            }
        }
    }

    private CreditBridgeTransactionExecutor.Result applied(
        TransactionRecord record
    )
    {
        return new CreditBridgeTransactionExecutor.Result(
            0,
            "credited|" +
            record.balanceBefore +
            "|" +
            record.balanceAfter
        );
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