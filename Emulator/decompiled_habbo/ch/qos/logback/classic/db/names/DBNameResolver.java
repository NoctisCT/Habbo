/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.db.names;

public interface DBNameResolver {
    public <N extends Enum<?>> String getTableName(N var1);

    public <N extends Enum<?>> String getColumnName(N var1);
}

