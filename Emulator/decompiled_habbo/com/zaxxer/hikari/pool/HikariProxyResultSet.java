/*
 * Decompiled with CFR 0.152.
 */
package com.zaxxer.hikari.pool;

import com.zaxxer.hikari.pool.ProxyConnection;
import com.zaxxer.hikari.pool.ProxyResultSet;
import com.zaxxer.hikari.pool.ProxyStatement;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Wrapper;
import java.util.Calendar;
import java.util.Map;

public class HikariProxyResultSet
extends ProxyResultSet
implements Wrapper,
AutoCloseable,
ResultSet {
    public boolean isWrapperFor(Class clazz) throws SQLException {
        try {
            return this.delegate.isWrapperFor(clazz);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void close() throws Exception {
        this.delegate.close();
    }

    @Override
    public boolean next() throws SQLException {
        try {
            return this.delegate.next();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean wasNull() throws SQLException {
        try {
            return this.delegate.wasNull();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public String getString(int n) throws SQLException {
        try {
            return this.delegate.getString(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean getBoolean(int n) throws SQLException {
        try {
            return this.delegate.getBoolean(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public byte getByte(int n) throws SQLException {
        try {
            return this.delegate.getByte(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public short getShort(int n) throws SQLException {
        try {
            return this.delegate.getShort(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getInt(int n) throws SQLException {
        try {
            return this.delegate.getInt(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public long getLong(int n) throws SQLException {
        try {
            return this.delegate.getLong(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public float getFloat(int n) throws SQLException {
        try {
            return this.delegate.getFloat(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public double getDouble(int n) throws SQLException {
        try {
            return this.delegate.getDouble(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public BigDecimal getBigDecimal(int n, int n2) throws SQLException {
        try {
            return this.delegate.getBigDecimal(n, n2);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public byte[] getBytes(int n) throws SQLException {
        try {
            return this.delegate.getBytes(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Date getDate(int n) throws SQLException {
        try {
            return this.delegate.getDate(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Time getTime(int n) throws SQLException {
        try {
            return this.delegate.getTime(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Timestamp getTimestamp(int n) throws SQLException {
        try {
            return this.delegate.getTimestamp(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public InputStream getAsciiStream(int n) throws SQLException {
        try {
            return this.delegate.getAsciiStream(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public InputStream getUnicodeStream(int n) throws SQLException {
        try {
            return this.delegate.getUnicodeStream(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public InputStream getBinaryStream(int n) throws SQLException {
        try {
            return this.delegate.getBinaryStream(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public String getString(String string) throws SQLException {
        try {
            return this.delegate.getString(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean getBoolean(String string) throws SQLException {
        try {
            return this.delegate.getBoolean(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public byte getByte(String string) throws SQLException {
        try {
            return this.delegate.getByte(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public short getShort(String string) throws SQLException {
        try {
            return this.delegate.getShort(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getInt(String string) throws SQLException {
        try {
            return this.delegate.getInt(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public long getLong(String string) throws SQLException {
        try {
            return this.delegate.getLong(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public float getFloat(String string) throws SQLException {
        try {
            return this.delegate.getFloat(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public double getDouble(String string) throws SQLException {
        try {
            return this.delegate.getDouble(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public BigDecimal getBigDecimal(String string, int n) throws SQLException {
        try {
            return this.delegate.getBigDecimal(string, n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public byte[] getBytes(String string) throws SQLException {
        try {
            return this.delegate.getBytes(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Date getDate(String string) throws SQLException {
        try {
            return this.delegate.getDate(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Time getTime(String string) throws SQLException {
        try {
            return this.delegate.getTime(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Timestamp getTimestamp(String string) throws SQLException {
        try {
            return this.delegate.getTimestamp(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public InputStream getAsciiStream(String string) throws SQLException {
        try {
            return this.delegate.getAsciiStream(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public InputStream getUnicodeStream(String string) throws SQLException {
        try {
            return this.delegate.getUnicodeStream(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public InputStream getBinaryStream(String string) throws SQLException {
        try {
            return this.delegate.getBinaryStream(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        try {
            return this.delegate.getWarnings();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void clearWarnings() throws SQLException {
        try {
            this.delegate.clearWarnings();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public String getCursorName() throws SQLException {
        try {
            return this.delegate.getCursorName();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        try {
            return this.delegate.getMetaData();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Object getObject(int n) throws SQLException {
        try {
            return this.delegate.getObject(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Object getObject(String string) throws SQLException {
        try {
            return this.delegate.getObject(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int findColumn(String string) throws SQLException {
        try {
            return this.delegate.findColumn(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Reader getCharacterStream(int n) throws SQLException {
        try {
            return this.delegate.getCharacterStream(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Reader getCharacterStream(String string) throws SQLException {
        try {
            return this.delegate.getCharacterStream(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public BigDecimal getBigDecimal(int n) throws SQLException {
        try {
            return this.delegate.getBigDecimal(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public BigDecimal getBigDecimal(String string) throws SQLException {
        try {
            return this.delegate.getBigDecimal(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean isBeforeFirst() throws SQLException {
        try {
            return this.delegate.isBeforeFirst();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean isAfterLast() throws SQLException {
        try {
            return this.delegate.isAfterLast();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean isFirst() throws SQLException {
        try {
            return this.delegate.isFirst();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean isLast() throws SQLException {
        try {
            return this.delegate.isLast();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void beforeFirst() throws SQLException {
        try {
            this.delegate.beforeFirst();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void afterLast() throws SQLException {
        try {
            this.delegate.afterLast();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean first() throws SQLException {
        try {
            return this.delegate.first();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean last() throws SQLException {
        try {
            return this.delegate.last();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getRow() throws SQLException {
        try {
            return this.delegate.getRow();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean absolute(int n) throws SQLException {
        try {
            return this.delegate.absolute(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean relative(int n) throws SQLException {
        try {
            return this.delegate.relative(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean previous() throws SQLException {
        try {
            return this.delegate.previous();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setFetchDirection(int n) throws SQLException {
        try {
            this.delegate.setFetchDirection(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getFetchDirection() throws SQLException {
        try {
            return this.delegate.getFetchDirection();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void setFetchSize(int n) throws SQLException {
        try {
            this.delegate.setFetchSize(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getFetchSize() throws SQLException {
        try {
            return this.delegate.getFetchSize();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getType() throws SQLException {
        try {
            return this.delegate.getType();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getConcurrency() throws SQLException {
        try {
            return this.delegate.getConcurrency();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean rowUpdated() throws SQLException {
        try {
            return this.delegate.rowUpdated();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean rowInserted() throws SQLException {
        try {
            return this.delegate.rowInserted();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean rowDeleted() throws SQLException {
        try {
            return this.delegate.rowDeleted();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateNull(int n) throws SQLException {
        try {
            this.delegate.updateNull(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateBoolean(int n, boolean bl) throws SQLException {
        try {
            this.delegate.updateBoolean(n, bl);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateByte(int n, byte by) throws SQLException {
        try {
            this.delegate.updateByte(n, by);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateShort(int n, short s) throws SQLException {
        try {
            this.delegate.updateShort(n, s);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateInt(int n, int n2) throws SQLException {
        try {
            this.delegate.updateInt(n, n2);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateLong(int n, long l) throws SQLException {
        try {
            this.delegate.updateLong(n, l);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateFloat(int n, float f) throws SQLException {
        try {
            this.delegate.updateFloat(n, f);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateDouble(int n, double d) throws SQLException {
        try {
            this.delegate.updateDouble(n, d);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateBigDecimal(int n, BigDecimal bigDecimal) throws SQLException {
        try {
            this.delegate.updateBigDecimal(n, bigDecimal);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateString(int n, String string) throws SQLException {
        try {
            this.delegate.updateString(n, string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateBytes(int n, byte[] byArray) throws SQLException {
        try {
            this.delegate.updateBytes(n, byArray);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateDate(int n, Date date) throws SQLException {
        try {
            this.delegate.updateDate(n, date);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateTime(int n, Time time) throws SQLException {
        try {
            this.delegate.updateTime(n, time);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateTimestamp(int n, Timestamp timestamp) throws SQLException {
        try {
            this.delegate.updateTimestamp(n, timestamp);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateAsciiStream(int n, InputStream inputStream, int n2) throws SQLException {
        try {
            this.delegate.updateAsciiStream(n, inputStream, n2);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateBinaryStream(int n, InputStream inputStream, int n2) throws SQLException {
        try {
            this.delegate.updateBinaryStream(n, inputStream, n2);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateCharacterStream(int n, Reader reader, int n2) throws SQLException {
        try {
            this.delegate.updateCharacterStream(n, reader, n2);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateObject(int n, Object object, int n2) throws SQLException {
        try {
            this.delegate.updateObject(n, object, n2);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateObject(int n, Object object) throws SQLException {
        try {
            this.delegate.updateObject(n, object);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateNull(String string) throws SQLException {
        try {
            this.delegate.updateNull(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateBoolean(String string, boolean bl) throws SQLException {
        try {
            this.delegate.updateBoolean(string, bl);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateByte(String string, byte by) throws SQLException {
        try {
            this.delegate.updateByte(string, by);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateShort(String string, short s) throws SQLException {
        try {
            this.delegate.updateShort(string, s);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateInt(String string, int n) throws SQLException {
        try {
            this.delegate.updateInt(string, n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateLong(String string, long l) throws SQLException {
        try {
            this.delegate.updateLong(string, l);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateFloat(String string, float f) throws SQLException {
        try {
            this.delegate.updateFloat(string, f);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateDouble(String string, double d) throws SQLException {
        try {
            this.delegate.updateDouble(string, d);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateBigDecimal(String string, BigDecimal bigDecimal) throws SQLException {
        try {
            this.delegate.updateBigDecimal(string, bigDecimal);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateString(String string, String string2) throws SQLException {
        try {
            this.delegate.updateString(string, string2);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateBytes(String string, byte[] byArray) throws SQLException {
        try {
            this.delegate.updateBytes(string, byArray);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateDate(String string, Date date) throws SQLException {
        try {
            this.delegate.updateDate(string, date);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateTime(String string, Time time) throws SQLException {
        try {
            this.delegate.updateTime(string, time);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateTimestamp(String string, Timestamp timestamp) throws SQLException {
        try {
            this.delegate.updateTimestamp(string, timestamp);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateAsciiStream(String string, InputStream inputStream, int n) throws SQLException {
        try {
            this.delegate.updateAsciiStream(string, inputStream, n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateBinaryStream(String string, InputStream inputStream, int n) throws SQLException {
        try {
            this.delegate.updateBinaryStream(string, inputStream, n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateCharacterStream(String string, Reader reader, int n) throws SQLException {
        try {
            this.delegate.updateCharacterStream(string, reader, n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateObject(String string, Object object, int n) throws SQLException {
        try {
            this.delegate.updateObject(string, object, n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateObject(String string, Object object) throws SQLException {
        try {
            this.delegate.updateObject(string, object);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void insertRow() throws SQLException {
        try {
            super.insertRow();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateRow() throws SQLException {
        try {
            super.updateRow();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void deleteRow() throws SQLException {
        try {
            super.deleteRow();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void refreshRow() throws SQLException {
        try {
            this.delegate.refreshRow();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void cancelRowUpdates() throws SQLException {
        try {
            this.delegate.cancelRowUpdates();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void moveToInsertRow() throws SQLException {
        try {
            this.delegate.moveToInsertRow();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void moveToCurrentRow() throws SQLException {
        try {
            this.delegate.moveToCurrentRow();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    public Object getObject(int n, Map map) throws SQLException {
        try {
            return this.delegate.getObject(n, map);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Ref getRef(int n) throws SQLException {
        try {
            return this.delegate.getRef(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Blob getBlob(int n) throws SQLException {
        try {
            return this.delegate.getBlob(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Clob getClob(int n) throws SQLException {
        try {
            return this.delegate.getClob(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Array getArray(int n) throws SQLException {
        try {
            return this.delegate.getArray(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    public Object getObject(String string, Map map) throws SQLException {
        try {
            return this.delegate.getObject(string, map);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Ref getRef(String string) throws SQLException {
        try {
            return this.delegate.getRef(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Blob getBlob(String string) throws SQLException {
        try {
            return this.delegate.getBlob(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Clob getClob(String string) throws SQLException {
        try {
            return this.delegate.getClob(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Array getArray(String string) throws SQLException {
        try {
            return this.delegate.getArray(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Date getDate(int n, Calendar calendar) throws SQLException {
        try {
            return this.delegate.getDate(n, calendar);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Date getDate(String string, Calendar calendar) throws SQLException {
        try {
            return this.delegate.getDate(string, calendar);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Time getTime(int n, Calendar calendar) throws SQLException {
        try {
            return this.delegate.getTime(n, calendar);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Time getTime(String string, Calendar calendar) throws SQLException {
        try {
            return this.delegate.getTime(string, calendar);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Timestamp getTimestamp(int n, Calendar calendar) throws SQLException {
        try {
            return this.delegate.getTimestamp(n, calendar);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Timestamp getTimestamp(String string, Calendar calendar) throws SQLException {
        try {
            return this.delegate.getTimestamp(string, calendar);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public URL getURL(int n) throws SQLException {
        try {
            return this.delegate.getURL(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public URL getURL(String string) throws SQLException {
        try {
            return this.delegate.getURL(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateRef(int n, Ref ref) throws SQLException {
        try {
            this.delegate.updateRef(n, ref);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateRef(String string, Ref ref) throws SQLException {
        try {
            this.delegate.updateRef(string, ref);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateBlob(int n, Blob blob) throws SQLException {
        try {
            this.delegate.updateBlob(n, blob);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateBlob(String string, Blob blob) throws SQLException {
        try {
            this.delegate.updateBlob(string, blob);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateClob(int n, Clob clob) throws SQLException {
        try {
            this.delegate.updateClob(n, clob);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateClob(String string, Clob clob) throws SQLException {
        try {
            this.delegate.updateClob(string, clob);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateArray(int n, Array array) throws SQLException {
        try {
            this.delegate.updateArray(n, array);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateArray(String string, Array array) throws SQLException {
        try {
            this.delegate.updateArray(string, array);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public RowId getRowId(int n) throws SQLException {
        try {
            return this.delegate.getRowId(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public RowId getRowId(String string) throws SQLException {
        try {
            return this.delegate.getRowId(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateRowId(int n, RowId rowId) throws SQLException {
        try {
            this.delegate.updateRowId(n, rowId);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateRowId(String string, RowId rowId) throws SQLException {
        try {
            this.delegate.updateRowId(string, rowId);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public int getHoldability() throws SQLException {
        try {
            return this.delegate.getHoldability();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public boolean isClosed() throws SQLException {
        try {
            return this.delegate.isClosed();
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateNString(int n, String string) throws SQLException {
        try {
            this.delegate.updateNString(n, string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateNString(String string, String string2) throws SQLException {
        try {
            this.delegate.updateNString(string, string2);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateNClob(int n, NClob nClob) throws SQLException {
        try {
            this.delegate.updateNClob(n, nClob);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateNClob(String string, NClob nClob) throws SQLException {
        try {
            this.delegate.updateNClob(string, nClob);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public NClob getNClob(int n) throws SQLException {
        try {
            return this.delegate.getNClob(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public NClob getNClob(String string) throws SQLException {
        try {
            return this.delegate.getNClob(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public SQLXML getSQLXML(int n) throws SQLException {
        try {
            return this.delegate.getSQLXML(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public SQLXML getSQLXML(String string) throws SQLException {
        try {
            return this.delegate.getSQLXML(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateSQLXML(int n, SQLXML sQLXML) throws SQLException {
        try {
            this.delegate.updateSQLXML(n, sQLXML);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateSQLXML(String string, SQLXML sQLXML) throws SQLException {
        try {
            this.delegate.updateSQLXML(string, sQLXML);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public String getNString(int n) throws SQLException {
        try {
            return this.delegate.getNString(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public String getNString(String string) throws SQLException {
        try {
            return this.delegate.getNString(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Reader getNCharacterStream(int n) throws SQLException {
        try {
            return this.delegate.getNCharacterStream(n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public Reader getNCharacterStream(String string) throws SQLException {
        try {
            return this.delegate.getNCharacterStream(string);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateNCharacterStream(int n, Reader reader, long l) throws SQLException {
        try {
            this.delegate.updateNCharacterStream(n, reader, l);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateNCharacterStream(String string, Reader reader, long l) throws SQLException {
        try {
            this.delegate.updateNCharacterStream(string, reader, l);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateAsciiStream(int n, InputStream inputStream, long l) throws SQLException {
        try {
            this.delegate.updateAsciiStream(n, inputStream, l);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateBinaryStream(int n, InputStream inputStream, long l) throws SQLException {
        try {
            this.delegate.updateBinaryStream(n, inputStream, l);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateCharacterStream(int n, Reader reader, long l) throws SQLException {
        try {
            this.delegate.updateCharacterStream(n, reader, l);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateAsciiStream(String string, InputStream inputStream, long l) throws SQLException {
        try {
            this.delegate.updateAsciiStream(string, inputStream, l);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateBinaryStream(String string, InputStream inputStream, long l) throws SQLException {
        try {
            this.delegate.updateBinaryStream(string, inputStream, l);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateCharacterStream(String string, Reader reader, long l) throws SQLException {
        try {
            this.delegate.updateCharacterStream(string, reader, l);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateBlob(int n, InputStream inputStream, long l) throws SQLException {
        try {
            this.delegate.updateBlob(n, inputStream, l);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateBlob(String string, InputStream inputStream, long l) throws SQLException {
        try {
            this.delegate.updateBlob(string, inputStream, l);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateClob(int n, Reader reader, long l) throws SQLException {
        try {
            this.delegate.updateClob(n, reader, l);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateClob(String string, Reader reader, long l) throws SQLException {
        try {
            this.delegate.updateClob(string, reader, l);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateNClob(int n, Reader reader, long l) throws SQLException {
        try {
            this.delegate.updateNClob(n, reader, l);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateNClob(String string, Reader reader, long l) throws SQLException {
        try {
            this.delegate.updateNClob(string, reader, l);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateNCharacterStream(int n, Reader reader) throws SQLException {
        try {
            this.delegate.updateNCharacterStream(n, reader);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateNCharacterStream(String string, Reader reader) throws SQLException {
        try {
            this.delegate.updateNCharacterStream(string, reader);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateAsciiStream(int n, InputStream inputStream) throws SQLException {
        try {
            this.delegate.updateAsciiStream(n, inputStream);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateBinaryStream(int n, InputStream inputStream) throws SQLException {
        try {
            this.delegate.updateBinaryStream(n, inputStream);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateCharacterStream(int n, Reader reader) throws SQLException {
        try {
            this.delegate.updateCharacterStream(n, reader);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateAsciiStream(String string, InputStream inputStream) throws SQLException {
        try {
            this.delegate.updateAsciiStream(string, inputStream);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateBinaryStream(String string, InputStream inputStream) throws SQLException {
        try {
            this.delegate.updateBinaryStream(string, inputStream);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateCharacterStream(String string, Reader reader) throws SQLException {
        try {
            this.delegate.updateCharacterStream(string, reader);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateBlob(int n, InputStream inputStream) throws SQLException {
        try {
            this.delegate.updateBlob(n, inputStream);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateBlob(String string, InputStream inputStream) throws SQLException {
        try {
            this.delegate.updateBlob(string, inputStream);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateClob(int n, Reader reader) throws SQLException {
        try {
            this.delegate.updateClob(n, reader);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateClob(String string, Reader reader) throws SQLException {
        try {
            this.delegate.updateClob(string, reader);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateNClob(int n, Reader reader) throws SQLException {
        try {
            this.delegate.updateNClob(n, reader);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateNClob(String string, Reader reader) throws SQLException {
        try {
            this.delegate.updateNClob(string, reader);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    public Object getObject(int n, Class clazz) throws SQLException {
        try {
            return this.delegate.getObject(n, clazz);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    public Object getObject(String string, Class clazz) throws SQLException {
        try {
            return this.delegate.getObject(string, clazz);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateObject(int n, Object object, SQLType sQLType, int n2) throws SQLException {
        try {
            this.delegate.updateObject(n, object, sQLType, n2);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateObject(String string, Object object, SQLType sQLType, int n) throws SQLException {
        try {
            this.delegate.updateObject(string, object, sQLType, n);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateObject(int n, Object object, SQLType sQLType) throws SQLException {
        try {
            this.delegate.updateObject(n, object, sQLType);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    @Override
    public void updateObject(String string, Object object, SQLType sQLType) throws SQLException {
        try {
            this.delegate.updateObject(string, object, sQLType);
        }
        catch (SQLException sQLException) {
            throw this.checkException(sQLException);
        }
    }

    protected HikariProxyResultSet(ProxyConnection proxyConnection, ProxyStatement proxyStatement, ResultSet resultSet) {
        super(proxyConnection, proxyStatement, resultSet);
    }
}

