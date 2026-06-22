/*
 * Decompiled with CFR 0.152.
 */
package com.zaxxer.hikari.pool;

import com.zaxxer.hikari.pool.ProxyConnection;
import com.zaxxer.hikari.pool.ProxyPreparedStatement;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Wrapper;
import java.util.Calendar;

public class HikariProxyPreparedStatement
extends ProxyPreparedStatement
implements Wrapper,
AutoCloseable,
Statement,
PreparedStatement {
    public boolean isWrapperFor(Class clazz) throws SQLException {
        try {
            return ((PreparedStatement)this.delegate).isWrapperFor(clazz);
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
            return ((PreparedStatement)this.delegate).getMaxFieldSize();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setMaxFieldSize(int n) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setMaxFieldSize(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getMaxRows() throws SQLException {
        try {
            return ((PreparedStatement)this.delegate).getMaxRows();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setMaxRows(int n) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setMaxRows(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setEscapeProcessing(boolean bl) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setEscapeProcessing(bl);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        try {
            return ((PreparedStatement)this.delegate).getQueryTimeout();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setQueryTimeout(int n) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setQueryTimeout(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void cancel() throws SQLException {
        try {
            ((PreparedStatement)this.delegate).cancel();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        try {
            return ((PreparedStatement)this.delegate).getWarnings();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void clearWarnings() throws SQLException {
        try {
            ((PreparedStatement)this.delegate).clearWarnings();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setCursorName(String string) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setCursorName(string);
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
            return ((PreparedStatement)this.delegate).getUpdateCount();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        try {
            return ((PreparedStatement)this.delegate).getMoreResults();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setFetchDirection(int n) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setFetchDirection(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getFetchDirection() throws SQLException {
        try {
            return ((PreparedStatement)this.delegate).getFetchDirection();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setFetchSize(int n) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setFetchSize(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getFetchSize() throws SQLException {
        try {
            return ((PreparedStatement)this.delegate).getFetchSize();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        try {
            return ((PreparedStatement)this.delegate).getResultSetConcurrency();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getResultSetType() throws SQLException {
        try {
            return ((PreparedStatement)this.delegate).getResultSetType();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void addBatch(String string) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).addBatch(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void clearBatch() throws SQLException {
        try {
            ((PreparedStatement)this.delegate).clearBatch();
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
            return ((PreparedStatement)this.delegate).getMoreResults(n);
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
            return ((PreparedStatement)this.delegate).getResultSetHoldability();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean isClosed() throws SQLException {
        try {
            return ((PreparedStatement)this.delegate).isClosed();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setPoolable(boolean bl) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setPoolable(bl);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean isPoolable() throws SQLException {
        try {
            return ((PreparedStatement)this.delegate).isPoolable();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void closeOnCompletion() throws SQLException {
        try {
            ((PreparedStatement)this.delegate).closeOnCompletion();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        try {
            return ((PreparedStatement)this.delegate).isCloseOnCompletion();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public long getLargeUpdateCount() throws SQLException {
        try {
            return ((PreparedStatement)this.delegate).getLargeUpdateCount();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setLargeMaxRows(long l) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setLargeMaxRows(l);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public long getLargeMaxRows() throws SQLException {
        try {
            return ((PreparedStatement)this.delegate).getLargeMaxRows();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public long[] executeLargeBatch() throws SQLException {
        try {
            return ((PreparedStatement)this.delegate).executeLargeBatch();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public long executeLargeUpdate(String string) throws SQLException {
        try {
            return ((PreparedStatement)this.delegate).executeLargeUpdate(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public long executeLargeUpdate(String string, int n) throws SQLException {
        try {
            return ((PreparedStatement)this.delegate).executeLargeUpdate(string, n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public long executeLargeUpdate(String string, int[] nArray) throws SQLException {
        try {
            return ((PreparedStatement)this.delegate).executeLargeUpdate(string, nArray);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public long executeLargeUpdate(String string, String[] stringArray) throws SQLException {
        try {
            return ((PreparedStatement)this.delegate).executeLargeUpdate(string, stringArray);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        try {
            return super.executeQuery();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int executeUpdate() throws SQLException {
        try {
            return super.executeUpdate();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setNull(int n, int n2) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setNull(n, n2);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setBoolean(int n, boolean bl) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setBoolean(n, bl);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setByte(int n, byte by) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setByte(n, by);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setShort(int n, short s) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setShort(n, s);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setInt(int n, int n2) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setInt(n, n2);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setLong(int n, long l) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setLong(n, l);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setFloat(int n, float f) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setFloat(n, f);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setDouble(int n, double d) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setDouble(n, d);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setBigDecimal(int n, BigDecimal bigDecimal) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setBigDecimal(n, bigDecimal);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setString(int n, String string) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setString(n, string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setBytes(int n, byte[] byArray) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setBytes(n, byArray);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setDate(int n, Date date) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setDate(n, date);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setTime(int n, Time time) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setTime(n, time);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setTimestamp(int n, Timestamp timestamp) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setTimestamp(n, timestamp);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setAsciiStream(int n, InputStream inputStream, int n2) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setAsciiStream(n, inputStream, n2);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setUnicodeStream(int n, InputStream inputStream, int n2) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setUnicodeStream(n, inputStream, n2);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setBinaryStream(int n, InputStream inputStream, int n2) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setBinaryStream(n, inputStream, n2);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void clearParameters() throws SQLException {
        try {
            ((PreparedStatement)this.delegate).clearParameters();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setObject(int n, Object object, int n2) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setObject(n, object, n2);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setObject(int n, Object object) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setObject(n, object);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean execute() throws SQLException {
        try {
            return super.execute();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void addBatch() throws SQLException {
        try {
            ((PreparedStatement)this.delegate).addBatch();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setCharacterStream(int n, Reader reader, int n2) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setCharacterStream(n, reader, n2);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setRef(int n, Ref ref) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setRef(n, ref);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setBlob(int n, Blob blob) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setBlob(n, blob);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setClob(int n, Clob clob) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setClob(n, clob);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setArray(int n, Array array) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setArray(n, array);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        try {
            return ((PreparedStatement)this.delegate).getMetaData();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setDate(int n, Date date, Calendar calendar) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setDate(n, date, calendar);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setTime(int n, Time time, Calendar calendar) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setTime(n, time, calendar);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setTimestamp(int n, Timestamp timestamp, Calendar calendar) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setTimestamp(n, timestamp, calendar);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setNull(int n, int n2, String string) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setNull(n, n2, string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setURL(int n, URL uRL) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setURL(n, uRL);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        try {
            return ((PreparedStatement)this.delegate).getParameterMetaData();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setRowId(int n, RowId rowId) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setRowId(n, rowId);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setNString(int n, String string) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setNString(n, string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setNCharacterStream(int n, Reader reader, long l) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setNCharacterStream(n, reader, l);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setNClob(int n, NClob nClob) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setNClob(n, nClob);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setClob(int n, Reader reader, long l) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setClob(n, reader, l);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setBlob(int n, InputStream inputStream, long l) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setBlob(n, inputStream, l);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setNClob(int n, Reader reader, long l) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setNClob(n, reader, l);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setSQLXML(int n, SQLXML sQLXML) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setSQLXML(n, sQLXML);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setObject(int n, Object object, int n2, int n3) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setObject(n, object, n2, n3);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setAsciiStream(int n, InputStream inputStream, long l) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setAsciiStream(n, inputStream, l);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setBinaryStream(int n, InputStream inputStream, long l) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setBinaryStream(n, inputStream, l);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setCharacterStream(int n, Reader reader, long l) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setCharacterStream(n, reader, l);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setAsciiStream(int n, InputStream inputStream) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setAsciiStream(n, inputStream);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setBinaryStream(int n, InputStream inputStream) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setBinaryStream(n, inputStream);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setCharacterStream(int n, Reader reader) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setCharacterStream(n, reader);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setNCharacterStream(int n, Reader reader) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setNCharacterStream(n, reader);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setClob(int n, Reader reader) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setClob(n, reader);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setBlob(int n, InputStream inputStream) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setBlob(n, inputStream);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setNClob(int n, Reader reader) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setNClob(n, reader);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setObject(int n, Object object, SQLType sQLType, int n2) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setObject(n, object, sQLType, n2);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setObject(int n, Object object, SQLType sQLType) throws SQLException {
        try {
            ((PreparedStatement)this.delegate).setObject(n, object, sQLType);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public long executeLargeUpdate() throws SQLException {
        try {
            return ((PreparedStatement)this.delegate).executeLargeUpdate();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    HikariProxyPreparedStatement(ProxyConnection proxyConnection, PreparedStatement preparedStatement) {
        super(proxyConnection, preparedStatement);
    }
}

