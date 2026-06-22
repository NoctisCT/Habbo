/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.db;

import ch.qos.logback.core.db.ConnectionSourceBase;
import ch.qos.logback.core.db.dialect.SQLDialectCode;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

public class DataSourceConnectionSource
extends ConnectionSourceBase {
    private DataSource dataSource;

    @Override
    public void start() {
        if (this.dataSource == null) {
            this.addWarn("WARNING: No data source specified");
        } else {
            this.discoverConnectionProperties();
            if (!this.supportsGetGeneratedKeys() && this.getSQLDialectCode() == SQLDialectCode.UNKNOWN_DIALECT) {
                this.addWarn("Connection does not support GetGeneratedKey method and could not discover the dialect.");
            }
        }
        super.start();
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (this.dataSource == null) {
            this.addError("WARNING: No data source specified");
            return null;
        }
        if (this.getUser() == null) {
            return this.dataSource.getConnection();
        }
        return this.dataSource.getConnection(this.getUser(), this.getPassword());
    }

    public DataSource getDataSource() {
        return this.dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}

