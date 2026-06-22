/*
 * Decompiled with CFR 0.152.
 */
package com.zaxxer.hikari.pool;

import com.zaxxer.hikari.pool.ProxyConnection;
import com.zaxxer.hikari.pool.ProxyStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Wrapper;

public class HikariProxyStatement
extends ProxyStatement
implements Wrapper,
AutoCloseable,
Statement {
    public boolean isWrapperFor(Class clazz) throws SQLException {
        try {
            return this.delegate.isWrapperFor(clazz);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public ResultSet executeQuery(String string) throws SQLException {
        try {
            return super.executeQuery(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int executeUpdate(String string) throws SQLException {
        try {
            return super.executeUpdate(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        try {
            return this.delegate.getMaxFieldSize();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setMaxFieldSize(int n) throws SQLException {
        try {
            this.delegate.setMaxFieldSize(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getMaxRows() throws SQLException {
        try {
            return this.delegate.getMaxRows();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setMaxRows(int n) throws SQLException {
        try {
            this.delegate.setMaxRows(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setEscapeProcessing(boolean bl) throws SQLException {
        try {
            this.delegate.setEscapeProcessing(bl);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        try {
            return this.delegate.getQueryTimeout();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setQueryTimeout(int n) throws SQLException {
        try {
            this.delegate.setQueryTimeout(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void cancel() throws SQLException {
        try {
            this.delegate.cancel();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        try {
            return this.delegate.getWarnings();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void clearWarnings() throws SQLException {
        try {
            this.delegate.clearWarnings();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setCursorName(String string) throws SQLException {
        try {
            this.delegate.setCursorName(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean execute(String string) throws SQLException {
        try {
            return super.execute(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        try {
            return super.getResultSet();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getUpdateCount() throws SQLException {
        try {
            return this.delegate.getUpdateCount();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        try {
            return this.delegate.getMoreResults();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setFetchDirection(int n) throws SQLException {
        try {
            this.delegate.setFetchDirection(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getFetchDirection() throws SQLException {
        try {
            return this.delegate.getFetchDirection();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setFetchSize(int n) throws SQLException {
        try {
            this.delegate.setFetchSize(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getFetchSize() throws SQLException {
        try {
            return this.delegate.getFetchSize();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        try {
            return this.delegate.getResultSetConcurrency();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getResultSetType() throws SQLException {
        try {
            return this.delegate.getResultSetType();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void addBatch(String string) throws SQLException {
        try {
            this.delegate.addBatch(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void clearBatch() throws SQLException {
        try {
            this.delegate.clearBatch();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int[] executeBatch() throws SQLException {
        try {
            return super.executeBatch();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        try {
            return super.getConnection();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean getMoreResults(int n) throws SQLException {
        try {
            return this.delegate.getMoreResults(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        try {
            return super.getGeneratedKeys();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int executeUpdate(String string, int n) throws SQLException {
        try {
            return super.executeUpdate(string, n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int executeUpdate(String string, int[] nArray) throws SQLException {
        try {
            return super.executeUpdate(string, nArray);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int executeUpdate(String string, String[] stringArray) throws SQLException {
        try {
            return super.executeUpdate(string, stringArray);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean execute(String string, int n) throws SQLException {
        try {
            return super.execute(string, n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean execute(String string, int[] nArray) throws SQLException {
        try {
            return super.execute(string, nArray);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean execute(String string, String[] stringArray) throws SQLException {
        try {
            return super.execute(string, stringArray);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        try {
            return this.delegate.getResultSetHoldability();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean isClosed() throws SQLException {
        try {
            return this.delegate.isClosed();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setPoolable(boolean bl) throws SQLException {
        try {
            this.delegate.setPoolable(bl);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean isPoolable() throws SQLException {
        try {
            return this.delegate.isPoolable();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void closeOnCompletion() throws SQLException {
        try {
            this.delegate.closeOnCompletion();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        try {
            return this.delegate.isCloseOnCompletion();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public long getLargeUpdateCount() throws SQLException {
        try {
            return this.delegate.getLargeUpdateCount();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setLargeMaxRows(long l) throws SQLException {
        try {
            this.delegate.setLargeMaxRows(l);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public long getLargeMaxRows() throws SQLException {
        try {
            return this.delegate.getLargeMaxRows();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public long[] executeLargeBatch() throws SQLException {
        try {
            return this.delegate.executeLargeBatch();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public long executeLargeUpdate(String string) throws SQLException {
        try {
            return this.delegate.executeLargeUpdate(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public long executeLargeUpdate(String string, int n) throws SQLException {
        try {
            return this.delegate.executeLargeUpdate(string, n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public long executeLargeUpdate(String string, int[] nArray) throws SQLException {
        try {
            return this.delegate.executeLargeUpdate(string, nArray);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public long executeLargeUpdate(String string, String[] stringArray) throws SQLException {
        try {
            return this.delegate.executeLargeUpdate(string, stringArray);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    HikariProxyStatement(ProxyConnection proxyConnection, Statement statement) {
        super(proxyConnection, statement);
    }
}

