/*
 * Decompiled with CFR 0.152.
 */
package com.zaxxer.hikari.pool;

import com.zaxxer.hikari.pool.PoolEntry;
import com.zaxxer.hikari.pool.ProxyConnection;
import com.zaxxer.hikari.pool.ProxyLeakTask;
import com.zaxxer.hikari.util.FastList;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.sql.Wrapper;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

public class HikariProxyConnection
extends ProxyConnection
implements Wrapper,
AutoCloseable,
Connection {
    @Override
    public Statement createStatement() throws SQLException {
        try {
            return super.createStatement();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public PreparedStatement prepareStatement(String string) throws SQLException {
        try {
            return super.prepareStatement(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public CallableStatement prepareCall(String string) throws SQLException {
        try {
            return super.prepareCall(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public String nativeSQL(String string) throws SQLException {
        try {
            return this.delegate.nativeSQL(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setAutoCommit(boolean bl) throws SQLException {
        try {
            super.setAutoCommit(bl);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        try {
            return this.delegate.getAutoCommit();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void commit() throws SQLException {
        try {
            super.commit();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void rollback() throws SQLException {
        try {
            super.rollback();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean isClosed() throws SQLException {
        try {
            return super.isClosed();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        try {
            return super.getMetaData();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setReadOnly(boolean bl) throws SQLException {
        try {
            super.setReadOnly(bl);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        try {
            return this.delegate.isReadOnly();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setCatalog(String string) throws SQLException {
        try {
            super.setCatalog(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public String getCatalog() throws SQLException {
        try {
            return this.delegate.getCatalog();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setTransactionIsolation(int n) throws SQLException {
        try {
            super.setTransactionIsolation(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        try {
            return this.delegate.getTransactionIsolation();
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
    public Statement createStatement(int n, int n2) throws SQLException {
        try {
            return super.createStatement(n, n2);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public PreparedStatement prepareStatement(String string, int n, int n2) throws SQLException {
        try {
            return super.prepareStatement(string, n, n2);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public CallableStatement prepareCall(String string, int n, int n2) throws SQLException {
        try {
            return super.prepareCall(string, n, n2);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    public Map getTypeMap() throws SQLException {
        try {
            return this.delegate.getTypeMap();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    public void setTypeMap(Map map) throws SQLException {
        try {
            this.delegate.setTypeMap(map);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setHoldability(int n) throws SQLException {
        try {
            this.delegate.setHoldability(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getHoldability() throws SQLException {
        try {
            return this.delegate.getHoldability();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        try {
            return this.delegate.setSavepoint();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Savepoint setSavepoint(String string) throws SQLException {
        try {
            return this.delegate.setSavepoint(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        try {
            super.rollback(savepoint);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        try {
            this.delegate.releaseSavepoint(savepoint);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Statement createStatement(int n, int n2, int n3) throws SQLException {
        try {
            return super.createStatement(n, n2, n3);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public PreparedStatement prepareStatement(String string, int n, int n2, int n3) throws SQLException {
        try {
            return super.prepareStatement(string, n, n2, n3);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public CallableStatement prepareCall(String string, int n, int n2, int n3) throws SQLException {
        try {
            return super.prepareCall(string, n, n2, n3);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public PreparedStatement prepareStatement(String string, int n) throws SQLException {
        try {
            return super.prepareStatement(string, n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public PreparedStatement prepareStatement(String string, int[] nArray) throws SQLException {
        try {
            return super.prepareStatement(string, nArray);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public PreparedStatement prepareStatement(String string, String[] stringArray) throws SQLException {
        try {
            return super.prepareStatement(string, stringArray);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Clob createClob() throws SQLException {
        try {
            return this.delegate.createClob();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Blob createBlob() throws SQLException {
        try {
            return this.delegate.createBlob();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public NClob createNClob() throws SQLException {
        try {
            return this.delegate.createNClob();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        try {
            return this.delegate.createSQLXML();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean isValid(int n) throws SQLException {
        try {
            return this.delegate.isValid(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setClientInfo(String string, String string2) throws SQLClientInfoException {
        this.delegate.setClientInfo(string, string2);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        this.delegate.setClientInfo(properties);
    }

    @Override
    public String getClientInfo(String string) throws SQLException {
        try {
            return this.delegate.getClientInfo(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        try {
            return this.delegate.getClientInfo();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Array createArrayOf(String string, Object[] objectArray) throws SQLException {
        try {
            return this.delegate.createArrayOf(string, objectArray);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Struct createStruct(String string, Object[] objectArray) throws SQLException {
        try {
            return this.delegate.createStruct(string, objectArray);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setSchema(String string) throws SQLException {
        try {
            super.setSchema(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public String getSchema() throws SQLException {
        try {
            return this.delegate.getSchema();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        try {
            this.delegate.abort(executor);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setNetworkTimeout(Executor executor, int n) throws SQLException {
        try {
            super.setNetworkTimeout(executor, n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        try {
            return this.delegate.getNetworkTimeout();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    protected HikariProxyConnection(PoolEntry poolEntry, Connection connection, FastList fastList, ProxyLeakTask proxyLeakTask, long l, boolean bl, boolean bl2) {
        super(poolEntry, connection, fastList, proxyLeakTask, l, bl, bl2);
    }
}

