/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.db;

import ch.qos.logback.core.db.dialect.SQLDialectCode;
import ch.qos.logback.core.spi.LifeCycle;
import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionSource
extends LifeCycle {
    public Connection getConnection() throws SQLException;

    public SQLDialectCode getSQLDialectCode();

    public boolean supportsGetGeneratedKeys();

    public boolean supportsBatchUpdates();
}

