/*
 * Decompiled with CFR 0.152.
 */
package com.zaxxer.hikari.pool;

import com.zaxxer.hikari.pool.ProxyConnection;
import com.zaxxer.hikari.pool.ProxyStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class ProxyResultSet
implements ResultSet {
    protected final ProxyConnection connection;
    protected final ProxyStatement statement;
    final ResultSet delegate;

    protected ProxyResultSet(ProxyConnection connection, ProxyStatement statement, ResultSet resultSet) {
        this.connection = connection;
        this.statement = statement;
        this.delegate = resultSet;
    }

    final SQLException checkException(SQLException e) {
        return this.connection.checkException(e);
    }

    public String toString() {
        return this.getClass().getSimpleName() + '@' + System.identityHashCode(this) + " wrapping " + this.delegate;
    }

    @Override
    public final Statement getStatement() throws SQLException {
        return this.statement;
    }

    @Override
    public void updateRow() throws SQLException {
        this.connection.markCommitStateDirty();
        this.delegate.updateRow();
    }

    @Override
    public void insertRow() throws SQLException {
        this.connection.markCommitStateDirty();
        this.delegate.insertRow();
    }

    @Override
    public void deleteRow() throws SQLException {
        this.connection.markCommitStateDirty();
        this.delegate.deleteRow();
    }

    @Override
    public final <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(this.delegate)) {
            return (T)this.delegate;
        }
        if (this.delegate != null) {
            return this.delegate.unwrap(iface);
        }
        throw new SQLException("Wrapped ResultSet is not an instance of " + iface);
    }
}

