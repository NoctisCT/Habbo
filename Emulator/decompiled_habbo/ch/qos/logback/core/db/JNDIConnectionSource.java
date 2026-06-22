/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.db;

import ch.qos.logback.core.db.ConnectionSourceBase;
import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class JNDIConnectionSource
extends ConnectionSourceBase {
    private String jndiLocation = null;
    private DataSource dataSource = null;

    @Override
    public void start() {
        if (this.jndiLocation == null) {
            this.addError("No JNDI location specified for JNDIConnectionSource.");
        }
        this.discoverConnectionProperties();
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection conn = null;
        try {
            if (this.dataSource == null) {
                this.dataSource = this.lookupDataSource();
            }
            if (this.getUser() != null) {
                this.addWarn("Ignoring property [user] with value [" + this.getUser() + "] for obtaining a connection from a DataSource.");
            }
            conn = this.dataSource.getConnection();
        }
        catch (NamingException ne) {
            this.addError("Error while getting data source", ne);
            throw new SQLException("NamingException while looking up DataSource: " + ne.getMessage());
        }
        catch (ClassCastException cce) {
            this.addError("ClassCastException while looking up DataSource.", cce);
            throw new SQLException("ClassCastException while looking up DataSource: " + cce.getMessage());
        }
        return conn;
    }

    public String getJndiLocation() {
        return this.jndiLocation;
    }

    public void setJndiLocation(String jndiLocation) {
        this.jndiLocation = jndiLocation;
    }

    private DataSource lookupDataSource() throws NamingException, SQLException {
        this.addInfo("Looking up [" + this.jndiLocation + "] in JNDI");
        InitialContext initialContext = new InitialContext();
        Object obj = initialContext.lookup(this.jndiLocation);
        DataSource ds = (DataSource)obj;
        if (ds == null) {
            throw new SQLException("Failed to obtain data source from JNDI location " + this.jndiLocation);
        }
        return ds;
    }
}

