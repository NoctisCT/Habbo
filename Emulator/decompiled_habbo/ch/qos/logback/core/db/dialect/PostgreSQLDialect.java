/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.db.dialect;

import ch.qos.logback.core.db.dialect.SQLDialect;

public class PostgreSQLDialect
implements SQLDialect {
    public static final String SELECT_CURRVAL = "SELECT currval('logging_event_id_seq')";

    @Override
    public String getSelectInsertId() {
        return SELECT_CURRVAL;
    }
}

