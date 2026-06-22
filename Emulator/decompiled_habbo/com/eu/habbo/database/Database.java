/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.database;

import com.eu.habbo.Emulator;
import com.eu.habbo.core.ConfigurationManager;
import com.eu.habbo.database.DatabasePool;
import com.zaxxer.hikari.HikariDataSource;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Database {
    private static final Logger LOGGER = LoggerFactory.getLogger(Database.class);
    private HikariDataSource dataSource;
    private DatabasePool databasePool;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Database(ConfigurationManager config) {
        long millis = System.currentTimeMillis();
        boolean SQLException2 = false;
        try {
            this.databasePool = new DatabasePool();
            if (!this.databasePool.getStoragePooling(config)) {
                LOGGER.info("Failed to connect to the database. Please check config.ini and make sure the MySQL process is running. Shutting down...");
                SQLException2 = true;
                return;
            }
            this.dataSource = this.databasePool.getDatabase();
        }
        catch (Exception e) {
            SQLException2 = true;
            LOGGER.error("Failed to connect to your database.", e);
        }
        finally {
            if (SQLException2) {
                Emulator.prepareShutdown();
            }
        }
        LOGGER.info("Database -> Connected! ({} MS)", (Object)(System.currentTimeMillis() - millis));
    }

    public void dispose() {
        if (this.databasePool != null) {
            this.databasePool.getDatabase().close();
        }
        this.dataSource.close();
    }

    public HikariDataSource getDataSource() {
        return this.dataSource;
    }

    public DatabasePool getDatabasePool() {
        return this.databasePool;
    }

    public static PreparedStatement preparedStatementWithParams(Connection connection, String query, THashMap<String, Object> queryParams) throws SQLException {
        THashMap<Integer, Object> params = new THashMap<Integer, Object>();
        THashSet<String> quotedParams = new THashSet<String>();
        for (String key : queryParams.keySet()) {
            quotedParams.add(Pattern.quote(key));
        }
        String regex = "(" + String.join((CharSequence)"|", quotedParams) + ")";
        Matcher m = Pattern.compile(regex).matcher(query);
        int i = 1;
        while (m.find()) {
            try {
                params.put(i, queryParams.get(m.group(1)));
                ++i;
            }
            catch (Exception exception) {}
        }
        PreparedStatement statement = connection.prepareStatement(query.replaceAll(regex, "?"));
        for (Map.Entry set : params.entrySet()) {
            if (set.getValue().getClass() == String.class) {
                statement.setString((Integer)set.getKey(), (String)set.getValue());
                continue;
            }
            if (set.getValue().getClass() == Integer.class) {
                statement.setInt((Integer)set.getKey(), (Integer)set.getValue());
                continue;
            }
            if (set.getValue().getClass() == Double.class) {
                statement.setDouble((Integer)set.getKey(), (Double)set.getValue());
                continue;
            }
            if (set.getValue().getClass() == Float.class) {
                statement.setFloat((Integer)set.getKey(), ((Float)set.getValue()).floatValue());
                continue;
            }
            if (set.getValue().getClass() == Long.class) {
                statement.setLong((Integer)set.getKey(), (Long)set.getValue());
                continue;
            }
            statement.setObject((Integer)set.getKey(), set.getValue());
        }
        return statement;
    }
}

