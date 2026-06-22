/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.db.dialect;

import ch.qos.logback.core.db.dialect.SQLDialect;

public class MySQLDialect
implements SQLDialect {
    public static final String SELECT_LAST_INSERT_ID = "SELECT LAST_INSERT_ID()";

    @Override
    public String getSelectInsertId() {
        return SELECT_LAST_INSERT_ID;
    }
}

