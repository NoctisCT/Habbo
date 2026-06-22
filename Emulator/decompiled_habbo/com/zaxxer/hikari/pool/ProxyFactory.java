/*
 * Decompiled with CFR 0.152.
 */
package com.zaxxer.hikari.pool;

import com.zaxxer.hikari.pool.HikariProxyCallableStatement;
import com.zaxxer.hikari.pool.HikariProxyConnection;
import com.zaxxer.hikari.pool.HikariProxyDatabaseMetaData;
import com.zaxxer.hikari.pool.HikariProxyPreparedStatement;
import com.zaxxer.hikari.pool.HikariProxyResultSet;
import com.zaxxer.hikari.pool.HikariProxyStatement;
import com.zaxxer.hikari.pool.PoolEntry;
import com.zaxxer.hikari.pool.ProxyConnection;
import com.zaxxer.hikari.pool.ProxyLeakTask;
import com.zaxxer.hikari.pool.ProxyStatement;
import com.zaxxer.hikari.util.FastList;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public final class ProxyFactory {
    private ProxyFactory() {
    }

    static ProxyConnection getProxyConnection(PoolEntry poolEntry, Connection connection, FastList<Statement> fastList, ProxyLeakTask proxyLeakTask, long l, boolean bl, boolean bl2) {
        return new HikariProxyConnection(poolEntry, connection, (FastList)fastList, proxyLeakTask, l, bl, bl2);
    }

    static Statement getProxyStatement(ProxyConnection proxyConnection, Statement statement) {
        return new HikariProxyStatement(proxyConnection, statement);
    }

    static CallableStatement getProxyCallableStatement(ProxyConnection proxyConnection, CallableStatement callableStatement) {
        return new HikariProxyCallableStatement(proxyConnection, callableStatement);
    }

    static PreparedStatement getProxyPreparedStatement(ProxyConnection proxyConnection, PreparedStatement preparedStatement) {
        return new HikariProxyPreparedStatement(proxyConnection, preparedStatement);
    }

    static ResultSet getProxyResultSet(ProxyConnection proxyConnection, ProxyStatement proxyStatement, ResultSet resultSet) {
        return new HikariProxyResultSet(proxyConnection, proxyStatement, resultSet);
    }

    static DatabaseMetaData getProxyDatabaseMetaData(ProxyConnection proxyConnection, DatabaseMetaData databaseMetaData) {
        return new HikariProxyDatabaseMetaData(proxyConnection, databaseMetaData);
    }
}

