/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.db;

import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.db.ConnectionSource;
import ch.qos.logback.core.db.DBHelper;
import ch.qos.logback.core.db.dialect.DBUtil;
import ch.qos.logback.core.db.dialect.SQLDialect;
import ch.qos.logback.core.db.dialect.SQLDialectCode;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class DBAppenderBase<E>
extends UnsynchronizedAppenderBase<E> {
    protected ConnectionSource connectionSource;
    protected boolean cnxSupportsGetGeneratedKeys = false;
    protected boolean cnxSupportsBatchUpdates = false;
    protected SQLDialect sqlDialect;

    protected abstract Method getGeneratedKeysMethod();

    protected abstract String getInsertSQL();

    @Override
    public void start() {
        if (this.connectionSource == null) {
            throw new IllegalStateException("DBAppender cannot function without a connection source");
        }
        this.sqlDialect = DBUtil.getDialectFromCode(this.connectionSource.getSQLDialectCode());
        this.cnxSupportsGetGeneratedKeys = this.getGeneratedKeysMethod() != null ? this.connectionSource.supportsGetGeneratedKeys() : false;
        this.cnxSupportsBatchUpdates = this.connectionSource.supportsBatchUpdates();
        if (!this.cnxSupportsGetGeneratedKeys && this.sqlDialect == null) {
            throw new IllegalStateException("DBAppender cannot function if the JDBC driver does not support getGeneratedKeys method *and* without a specific SQL dialect");
        }
        super.start();
    }

    public ConnectionSource getConnectionSource() {
        return this.connectionSource;
    }

    public void setConnectionSource(ConnectionSource connectionSource) {
        this.connectionSource = connectionSource;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void append(E eventObject) {
        Connection connection = null;
        PreparedStatement insertStatement = null;
        try {
            long eventId;
            connection = this.connectionSource.getConnection();
            connection.setAutoCommit(false);
            if (this.cnxSupportsGetGeneratedKeys) {
                String EVENT_ID_COL_NAME = "EVENT_ID";
                if (this.connectionSource.getSQLDialectCode() == SQLDialectCode.POSTGRES_DIALECT) {
                    EVENT_ID_COL_NAME = EVENT_ID_COL_NAME.toLowerCase();
                }
                insertStatement = connection.prepareStatement(this.getInsertSQL(), new String[]{EVENT_ID_COL_NAME});
            } else {
                insertStatement = connection.prepareStatement(this.getInsertSQL());
            }
            DBAppenderBase dBAppenderBase = this;
            synchronized (dBAppenderBase) {
                this.subAppend(eventObject, connection, insertStatement);
                eventId = this.selectEventId(insertStatement, connection);
            }
            this.secondarySubAppend(eventObject, connection, eventId);
            connection.commit();
        }
        catch (Throwable sqle) {
            try {
                this.addError("problem appending event", sqle);
            }
            catch (Throwable throwable) {
                throw throwable;
            }
            finally {
                DBHelper.closeStatement(insertStatement);
                DBHelper.closeConnection(connection);
            }
        }
        DBHelper.closeStatement(insertStatement);
        DBHelper.closeConnection(connection);
    }

    protected abstract void subAppend(E var1, Connection var2, PreparedStatement var3) throws Throwable;

    protected abstract void secondarySubAppend(E var1, Connection var2, long var3) throws Throwable;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected long selectEventId(PreparedStatement insertStatement, Connection connection) throws SQLException, InvocationTargetException {
        long l;
        Statement idStatement;
        block13: {
            ResultSet rs = null;
            idStatement = null;
            try {
                long eventId;
                boolean gotGeneratedKeys = false;
                if (this.cnxSupportsGetGeneratedKeys) {
                    try {
                        rs = (ResultSet)this.getGeneratedKeysMethod().invoke((Object)insertStatement, (Object[])null);
                        gotGeneratedKeys = true;
                    }
                    catch (InvocationTargetException ex) {
                        Throwable target = ex.getTargetException();
                        if (target instanceof SQLException) {
                            throw (SQLException)target;
                        }
                        throw ex;
                    }
                    catch (IllegalAccessException ex) {
                        this.addWarn("IllegalAccessException invoking PreparedStatement.getGeneratedKeys", ex);
                    }
                }
                if (!gotGeneratedKeys) {
                    idStatement = connection.createStatement();
                    idStatement.setMaxRows(1);
                    String selectInsertIdStr = this.sqlDialect.getSelectInsertId();
                    rs = idStatement.executeQuery(selectInsertIdStr);
                }
                rs.next();
                l = eventId = rs.getLong(1);
                if (rs == null) break block13;
            }
            catch (Throwable throwable) {
                if (rs != null) {
                    try {
                        rs.close();
                    }
                    catch (SQLException sQLException) {
                        // empty catch block
                    }
                }
                DBHelper.closeStatement(idStatement);
                throw throwable;
            }
            try {
                rs.close();
            }
            catch (SQLException sQLException) {
                // empty catch block
            }
        }
        DBHelper.closeStatement(idStatement);
        return l;
    }

    @Override
    public void stop() {
        super.stop();
    }
}

