/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.core;

import com.eu.habbo.Emulator;
import com.eu.habbo.core.DatabaseLoggable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseLogger {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseLogger.class);
    private final ConcurrentLinkedQueue<DatabaseLoggable> loggables = new ConcurrentLinkedQueue();

    public void store(DatabaseLoggable loggable) {
        this.loggables.add(loggable);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void save() {
        if (Emulator.getDatabase() == null) return;
        if (Emulator.getDatabase().getDataSource() == null) {
            return;
        }
        if (this.loggables.isEmpty()) {
            return;
        }
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();){
            while (!this.loggables.isEmpty()) {
                DatabaseLoggable loggable = (DatabaseLoggable)this.loggables.remove();
                PreparedStatement statement = connection.prepareStatement(loggable.getQuery());
                try {
                    loggable.log(statement);
                    statement.executeBatch();
                }
                finally {
                    if (statement == null) continue;
                    statement.close();
                }
            }
            return;
        }
        catch (SQLException e) {
            LOGGER.error("Exception caught while saving loggables to database.", e);
        }
    }
}

