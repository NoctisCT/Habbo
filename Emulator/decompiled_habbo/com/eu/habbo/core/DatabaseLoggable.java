/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.core;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface DatabaseLoggable {
    public String getQuery();

    public void log(PreparedStatement var1) throws SQLException;
}

