/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.db.dialect;

import ch.qos.logback.core.db.dialect.SQLDialect;

public class OracleDialect
implements SQLDialect {
    public static final String SELECT_CURRVAL = "SELECT logging_event_id_seq.currval from dual";

    @Override
    public String getSelectInsertId() {
        return SELECT_CURRVAL;
    }
}

