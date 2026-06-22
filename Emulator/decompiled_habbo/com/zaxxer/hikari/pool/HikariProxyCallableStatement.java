/*
 * Decompiled with CFR 0.152.
 */
package com.zaxxer.hikari.pool;

import com.zaxxer.hikari.pool.ProxyCallableStatement;
import com.zaxxer.hikari.pool.ProxyConnection;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
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
import java.util.Map;

public class HikariProxyCallableStatement
extends ProxyCallableStatement
implements Wrapper,
AutoCloseable,
Statement,
PreparedStatement,
CallableStatement {
    public boolean isWrapperFor(Class clazz) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).isWrapperFor(clazz);
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
            return ((CallableStatement)this.delegate).getMaxFieldSize();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setMaxFieldSize(int n) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setMaxFieldSize(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getMaxRows() throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getMaxRows();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setMaxRows(int n) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setMaxRows(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setEscapeProcessing(boolean bl) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setEscapeProcessing(bl);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getQueryTimeout();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setQueryTimeout(int n) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setQueryTimeout(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void cancel() throws SQLException {
        try {
            ((CallableStatement)this.delegate).cancel();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getWarnings();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void clearWarnings() throws SQLException {
        try {
            ((CallableStatement)this.delegate).clearWarnings();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setCursorName(String string) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setCursorName(string);
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
            return ((CallableStatement)this.delegate).getUpdateCount();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getMoreResults();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setFetchDirection(int n) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setFetchDirection(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getFetchDirection() throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getFetchDirection();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setFetchSize(int n) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setFetchSize(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getFetchSize() throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getFetchSize();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getResultSetConcurrency();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getResultSetType() throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getResultSetType();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void addBatch(String string) throws SQLException {
        try {
            ((CallableStatement)this.delegate).addBatch(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void clearBatch() throws SQLException {
        try {
            ((CallableStatement)this.delegate).clearBatch();
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
            return ((CallableStatement)this.delegate).getMoreResults(n);
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
            return ((CallableStatement)this.delegate).getResultSetHoldability();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean isClosed() throws SQLException {
        try {
            return ((CallableStatement)this.delegate).isClosed();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setPoolable(boolean bl) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setPoolable(bl);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean isPoolable() throws SQLException {
        try {
            return ((CallableStatement)this.delegate).isPoolable();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void closeOnCompletion() throws SQLException {
        try {
            ((CallableStatement)this.delegate).closeOnCompletion();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        try {
            return ((CallableStatement)this.delegate).isCloseOnCompletion();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public long getLargeUpdateCount() throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getLargeUpdateCount();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setLargeMaxRows(long l) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setLargeMaxRows(l);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public long getLargeMaxRows() throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getLargeMaxRows();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public long[] executeLargeBatch() throws SQLException {
        try {
            return ((CallableStatement)this.delegate).executeLargeBatch();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public long executeLargeUpdate(String string) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).executeLargeUpdate(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public long executeLargeUpdate(String string, int n) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).executeLargeUpdate(string, n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public long executeLargeUpdate(String string, int[] nArray) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).executeLargeUpdate(string, nArray);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public long executeLargeUpdate(String string, String[] stringArray) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).executeLargeUpdate(string, stringArray);
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
            ((CallableStatement)this.delegate).setNull(n, n2);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setBoolean(int n, boolean bl) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setBoolean(n, bl);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setByte(int n, byte by) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setByte(n, by);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setShort(int n, short s) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setShort(n, s);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setInt(int n, int n2) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setInt(n, n2);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setLong(int n, long l) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setLong(n, l);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setFloat(int n, float f) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setFloat(n, f);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setDouble(int n, double d) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setDouble(n, d);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setBigDecimal(int n, BigDecimal bigDecimal) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setBigDecimal(n, bigDecimal);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setString(int n, String string) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setString(n, string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setBytes(int n, byte[] byArray) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setBytes(n, byArray);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setDate(int n, Date date) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setDate(n, date);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setTime(int n, Time time) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setTime(n, time);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setTimestamp(int n, Timestamp timestamp) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setTimestamp(n, timestamp);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setAsciiStream(int n, InputStream inputStream, int n2) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setAsciiStream(n, inputStream, n2);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setUnicodeStream(int n, InputStream inputStream, int n2) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setUnicodeStream(n, inputStream, n2);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setBinaryStream(int n, InputStream inputStream, int n2) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setBinaryStream(n, inputStream, n2);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void clearParameters() throws SQLException {
        try {
            ((CallableStatement)this.delegate).clearParameters();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setObject(int n, Object object, int n2) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setObject(n, object, n2);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setObject(int n, Object object) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setObject(n, object);
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
            ((CallableStatement)this.delegate).addBatch();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setCharacterStream(int n, Reader reader, int n2) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setCharacterStream(n, reader, n2);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setRef(int n, Ref ref) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setRef(n, ref);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setBlob(int n, Blob blob) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setBlob(n, blob);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setClob(int n, Clob clob) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setClob(n, clob);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setArray(int n, Array array) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setArray(n, array);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getMetaData();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setDate(int n, Date date, Calendar calendar) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setDate(n, date, calendar);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setTime(int n, Time time, Calendar calendar) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setTime(n, time, calendar);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setTimestamp(int n, Timestamp timestamp, Calendar calendar) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setTimestamp(n, timestamp, calendar);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setNull(int n, int n2, String string) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setNull(n, n2, string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setURL(int n, URL uRL) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setURL(n, uRL);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getParameterMetaData();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setRowId(int n, RowId rowId) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setRowId(n, rowId);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setNString(int n, String string) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setNString(n, string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setNCharacterStream(int n, Reader reader, long l) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setNCharacterStream(n, reader, l);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setNClob(int n, NClob nClob) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setNClob(n, nClob);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setClob(int n, Reader reader, long l) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setClob(n, reader, l);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setBlob(int n, InputStream inputStream, long l) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setBlob(n, inputStream, l);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setNClob(int n, Reader reader, long l) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setNClob(n, reader, l);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setSQLXML(int n, SQLXML sQLXML) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setSQLXML(n, sQLXML);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setObject(int n, Object object, int n2, int n3) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setObject(n, object, n2, n3);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setAsciiStream(int n, InputStream inputStream, long l) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setAsciiStream(n, inputStream, l);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setBinaryStream(int n, InputStream inputStream, long l) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setBinaryStream(n, inputStream, l);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setCharacterStream(int n, Reader reader, long l) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setCharacterStream(n, reader, l);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setAsciiStream(int n, InputStream inputStream) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setAsciiStream(n, inputStream);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setBinaryStream(int n, InputStream inputStream) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setBinaryStream(n, inputStream);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setCharacterStream(int n, Reader reader) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setCharacterStream(n, reader);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setNCharacterStream(int n, Reader reader) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setNCharacterStream(n, reader);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setClob(int n, Reader reader) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setClob(n, reader);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setBlob(int n, InputStream inputStream) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setBlob(n, inputStream);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setNClob(int n, Reader reader) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setNClob(n, reader);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setObject(int n, Object object, SQLType sQLType, int n2) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setObject(n, object, sQLType, n2);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setObject(int n, Object object, SQLType sQLType) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setObject(n, object, sQLType);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public long executeLargeUpdate() throws SQLException {
        try {
            return ((CallableStatement)this.delegate).executeLargeUpdate();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void registerOutParameter(int n, int n2) throws SQLException {
        try {
            ((CallableStatement)this.delegate).registerOutParameter(n, n2);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void registerOutParameter(int n, int n2, int n3) throws SQLException {
        try {
            ((CallableStatement)this.delegate).registerOutParameter(n, n2, n3);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean wasNull() throws SQLException {
        try {
            return ((CallableStatement)this.delegate).wasNull();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public String getString(int n) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getString(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean getBoolean(int n) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getBoolean(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public byte getByte(int n) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getByte(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public short getShort(int n) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getShort(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getInt(int n) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getInt(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public long getLong(int n) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getLong(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public float getFloat(int n) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getFloat(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public double getDouble(int n) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getDouble(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public BigDecimal getBigDecimal(int n, int n2) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getBigDecimal(n, n2);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public byte[] getBytes(int n) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getBytes(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Date getDate(int n) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getDate(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Time getTime(int n) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getTime(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Timestamp getTimestamp(int n) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getTimestamp(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Object getObject(int n) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getObject(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public BigDecimal getBigDecimal(int n) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getBigDecimal(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    public Object getObject(int n, Map map) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getObject(n, map);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Ref getRef(int n) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getRef(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Blob getBlob(int n) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getBlob(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Clob getClob(int n) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getClob(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Array getArray(int n) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getArray(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Date getDate(int n, Calendar calendar) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getDate(n, calendar);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Time getTime(int n, Calendar calendar) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getTime(n, calendar);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Timestamp getTimestamp(int n, Calendar calendar) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getTimestamp(n, calendar);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void registerOutParameter(int n, int n2, String string) throws SQLException {
        try {
            ((CallableStatement)this.delegate).registerOutParameter(n, n2, string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void registerOutParameter(String string, int n) throws SQLException {
        try {
            ((CallableStatement)this.delegate).registerOutParameter(string, n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void registerOutParameter(String string, int n, int n2) throws SQLException {
        try {
            ((CallableStatement)this.delegate).registerOutParameter(string, n, n2);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void registerOutParameter(String string, int n, String string2) throws SQLException {
        try {
            ((CallableStatement)this.delegate).registerOutParameter(string, n, string2);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public URL getURL(int n) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getURL(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setURL(String string, URL uRL) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setURL(string, uRL);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setNull(String string, int n) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setNull(string, n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setBoolean(String string, boolean bl) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setBoolean(string, bl);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setByte(String string, byte by) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setByte(string, by);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setShort(String string, short s) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setShort(string, s);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setInt(String string, int n) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setInt(string, n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setLong(String string, long l) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setLong(string, l);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setFloat(String string, float f) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setFloat(string, f);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setDouble(String string, double d) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setDouble(string, d);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setBigDecimal(String string, BigDecimal bigDecimal) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setBigDecimal(string, bigDecimal);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setString(String string, String string2) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setString(string, string2);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setBytes(String string, byte[] byArray) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setBytes(string, byArray);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setDate(String string, Date date) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setDate(string, date);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setTime(String string, Time time) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setTime(string, time);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setTimestamp(String string, Timestamp timestamp) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setTimestamp(string, timestamp);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setAsciiStream(String string, InputStream inputStream, int n) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setAsciiStream(string, inputStream, n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setBinaryStream(String string, InputStream inputStream, int n) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setBinaryStream(string, inputStream, n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setObject(String string, Object object, int n, int n2) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setObject(string, object, n, n2);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setObject(String string, Object object, int n) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setObject(string, object, n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setObject(String string, Object object) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setObject(string, object);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setCharacterStream(String string, Reader reader, int n) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setCharacterStream(string, reader, n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setDate(String string, Date date, Calendar calendar) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setDate(string, date, calendar);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setTime(String string, Time time, Calendar calendar) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setTime(string, time, calendar);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setTimestamp(String string, Timestamp timestamp, Calendar calendar) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setTimestamp(string, timestamp, calendar);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setNull(String string, int n, String string2) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setNull(string, n, string2);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public String getString(String string) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getString(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean getBoolean(String string) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getBoolean(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public byte getByte(String string) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getByte(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public short getShort(String string) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getShort(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getInt(String string) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getInt(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public long getLong(String string) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getLong(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public float getFloat(String string) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getFloat(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public double getDouble(String string) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getDouble(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public byte[] getBytes(String string) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getBytes(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Date getDate(String string) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getDate(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Time getTime(String string) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getTime(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Timestamp getTimestamp(String string) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getTimestamp(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Object getObject(String string) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getObject(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public BigDecimal getBigDecimal(String string) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getBigDecimal(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    public Object getObject(String string, Map map) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getObject(string, map);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Ref getRef(String string) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getRef(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Blob getBlob(String string) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getBlob(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Clob getClob(String string) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getClob(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Array getArray(String string) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getArray(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Date getDate(String string, Calendar calendar) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getDate(string, calendar);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Time getTime(String string, Calendar calendar) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getTime(string, calendar);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Timestamp getTimestamp(String string, Calendar calendar) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getTimestamp(string, calendar);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public URL getURL(String string) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getURL(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public RowId getRowId(int n) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getRowId(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public RowId getRowId(String string) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getRowId(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setRowId(String string, RowId rowId) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setRowId(string, rowId);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setNString(String string, String string2) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setNString(string, string2);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setNCharacterStream(String string, Reader reader, long l) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setNCharacterStream(string, reader, l);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setNClob(String string, NClob nClob) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setNClob(string, nClob);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setClob(String string, Reader reader, long l) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setClob(string, reader, l);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setBlob(String string, InputStream inputStream, long l) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setBlob(string, inputStream, l);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setNClob(String string, Reader reader, long l) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setNClob(string, reader, l);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public NClob getNClob(int n) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getNClob(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public NClob getNClob(String string) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getNClob(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setSQLXML(String string, SQLXML sQLXML) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setSQLXML(string, sQLXML);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public SQLXML getSQLXML(int n) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getSQLXML(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public SQLXML getSQLXML(String string) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getSQLXML(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public String getNString(int n) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getNString(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public String getNString(String string) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getNString(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Reader getNCharacterStream(int n) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getNCharacterStream(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Reader getNCharacterStream(String string) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getNCharacterStream(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Reader getCharacterStream(int n) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getCharacterStream(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Reader getCharacterStream(String string) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getCharacterStream(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setBlob(String string, Blob blob) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setBlob(string, blob);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setClob(String string, Clob clob) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setClob(string, clob);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setAsciiStream(String string, InputStream inputStream, long l) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setAsciiStream(string, inputStream, l);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setBinaryStream(String string, InputStream inputStream, long l) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setBinaryStream(string, inputStream, l);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setCharacterStream(String string, Reader reader, long l) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setCharacterStream(string, reader, l);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setAsciiStream(String string, InputStream inputStream) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setAsciiStream(string, inputStream);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setBinaryStream(String string, InputStream inputStream) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setBinaryStream(string, inputStream);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setCharacterStream(String string, Reader reader) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setCharacterStream(string, reader);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setNCharacterStream(String string, Reader reader) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setNCharacterStream(string, reader);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setClob(String string, Reader reader) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setClob(string, reader);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setBlob(String string, InputStream inputStream) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setBlob(string, inputStream);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setNClob(String string, Reader reader) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setNClob(string, reader);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    public Object getObject(int n, Class clazz) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getObject(n, clazz);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    public Object getObject(String string, Class clazz) throws SQLException {
        try {
            return ((CallableStatement)this.delegate).getObject(string, clazz);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setObject(String string, Object object, SQLType sQLType, int n) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setObject(string, object, sQLType, n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setObject(String string, Object object, SQLType sQLType) throws SQLException {
        try {
            ((CallableStatement)this.delegate).setObject(string, object, sQLType);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void registerOutParameter(int n, SQLType sQLType) throws SQLException {
        try {
            ((CallableStatement)this.delegate).registerOutParameter(n, sQLType);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void registerOutParameter(int n, SQLType sQLType, int n2) throws SQLException {
        try {
            ((CallableStatement)this.delegate).registerOutParameter(n, sQLType, n2);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void registerOutParameter(int n, SQLType sQLType, String string) throws SQLException {
        try {
            ((CallableStatement)this.delegate).registerOutParameter(n, sQLType, string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void registerOutParameter(String string, SQLType sQLType) throws SQLException {
        try {
            ((CallableStatement)this.delegate).registerOutParameter(string, sQLType);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void registerOutParameter(String string, SQLType sQLType, int n) throws SQLException {
        try {
            ((CallableStatement)this.delegate).registerOutParameter(string, sQLType, n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void registerOutParameter(String string, SQLType sQLType, String string2) throws SQLException {
        try {
            ((CallableStatement)this.delegate).registerOutParameter(string, sQLType, string2);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    protected HikariProxyCallableStatement(ProxyConnection proxyConnection, CallableStatement callableStatement) {
        super(proxyConnection, callableStatement);
    }
}

