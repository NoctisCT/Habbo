/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.db;

import ch.qos.logback.core.db.ConnectionSource;
import ch.qos.logback.core.db.DBHelper;
import ch.qos.logback.core.db.dialect.DBUtil;
import ch.qos.logback.core.db.dialect.SQLDialectCode;
import ch.qos.logback.core.spi.ContextAwareBase;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public abstract class ConnectionSourceBase
extends ContextAwareBase
implements ConnectionSource {
    private boolean started;
    private String user = null;
    private String password = null;
    private SQLDialectCode dialectCode = SQLDialectCode.UNKNOWN_DIALECT;
    private boolean supportsGetGeneratedKeys = false;
    private boolean supportsBatchUpdates = false;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void discoverConnectionProperties() {
        Connection connection = null;
        try {
            connection = this.getConnection();
            if (connection == null) {
                this.addWarn("Could not get a connection");
                return;
            }
            DatabaseMetaData meta = connection.getMetaData();
            DBUtil util = new DBUtil();
            util.setContext(this.getContext());
            this.supportsGetGeneratedKeys = util.supportsGetGeneratedKeys(meta);
            this.supportsBatchUpdates = util.supportsBatchUpdates(meta);
            this.dialectCode = DBUtil.discoverSQLDialect(meta);
            this.addInfo("Driver name=" + meta.getDriverName());
            this.addInfo("Driver version=" + meta.getDriverVersion());
            this.addInfo("supportsGetGeneratedKeys=" + this.supportsGetGeneratedKeys);
        }
        catch (SQLException se) {
            this.addWarn("Could not discover the dialect to use.", se);
        }
        finally {
            DBHelper.closeConnection(connection);
        }
    }

    @Override
    public final boolean supportsGetGeneratedKeys() {
        return this.supportsGetGeneratedKeys;
    }

    @Override
    public final SQLDialectCode getSQLDialectCode() {
        return this.dialectCode;
    }

    public final String getPassword() {
        return this.password;
    }

    public final void setPassword(String password) {
        this.password = password;
    }

    public final String getUser() {
        return this.user;
    }

    public final void setUser(String username) {
        this.user = username;
    }

    @Override
    public final boolean supportsBatchUpdates() {
        return this.supportsBatchUpdates;
    }

    @Override
    public boolean isStarted() {
        return this.started;
    }

    @Override
    public void start() {
        this.started = true;
    }

    @Override
    public void stop() {
        this.started = false;
    }
}

