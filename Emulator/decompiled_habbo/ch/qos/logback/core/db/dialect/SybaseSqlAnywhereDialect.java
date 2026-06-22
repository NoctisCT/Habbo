/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.db.dialect;

import ch.qos.logback.core.db.dialect.SQLDialect;

public class SybaseSqlAnywhereDialect
implements SQLDialect {
    public static final String SELECT_CURRVAL = "SELECT @@identity id";

    @Override
    public String getSelectInsertId() {
        return SELECT_CURRVAL;
    }
}

