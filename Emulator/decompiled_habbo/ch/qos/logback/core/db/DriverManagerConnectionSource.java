/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.db;

import ch.qos.logback.core.db.ConnectionSourceBase;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DriverManagerConnectionSource
extends ConnectionSourceBase {
    private String driverClass = null;
    private String url = null;

    @Override
    public void start() {
        try {
            if (this.driverClass != null) {
                Class.forName(this.driverClass);
                this.discoverConnectionProperties();
            } else {
                this.addError("WARNING: No JDBC driver specified for logback DriverManagerConnectionSource.");
            }
        }
        catch (ClassNotFoundException cnfe) {
            this.addError("Could not load JDBC driver class: " + this.driverClass, cnfe);
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (this.getUser() == null) {
            return DriverManager.getConnection(this.url);
        }
        return DriverManager.getConnection(this.url, this.getUser(), this.getPassword());
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDriverClass() {
        return this.driverClass;
    }

    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }
}

