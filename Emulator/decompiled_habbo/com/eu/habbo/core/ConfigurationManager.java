/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.core;

import com.eu.habbo.Emulator;
import com.eu.habbo.plugin.events.emulator.EmulatorConfigUpdatedEvent;
import gnu.trove.map.hash.THashMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationManager.class);
    private final Properties properties = new Properties();
    private final String configurationPath;
    public boolean loaded = false;
    public boolean isLoading = false;

    public ConfigurationManager(String configurationPath) {
        this.configurationPath = configurationPath;
        this.reload();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void reload() {
        this.isLoading = true;
        this.properties.clear();
        InputStream input = null;
        String envDbHostname = System.getenv("DB_HOSTNAME");
        boolean useEnvVarsForDbConnection = false;
        if (envDbHostname != null) {
            boolean bl = useEnvVarsForDbConnection = envDbHostname.length() > 1;
        }
        if (!useEnvVarsForDbConnection) {
            try {
                File f = new File(this.configurationPath);
                input = new FileInputStream(f);
                this.properties.load(input);
            }
            catch (IOException ex) {
                LOGGER.error("Failed to load config file.", ex);
                ex.printStackTrace();
            }
            finally {
                if (input != null) {
                    try {
                        input.close();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            THashMap<String, String> envMapping = new THashMap<String, String>();
            envMapping.put("db.hostname", "DB_HOSTNAME");
            envMapping.put("db.port", "DB_PORT");
            envMapping.put("db.database", "DB_DATABASE");
            envMapping.put("db.username", "DB_USERNAME");
            envMapping.put("db.password", "DB_PASSWORD");
            envMapping.put("db.params", "DB_PARAMS");
            envMapping.put("game.host", "EMU_HOST");
            envMapping.put("game.port", "EMU_PORT");
            envMapping.put("rcon.host", "RCON_HOST");
            envMapping.put("rcon.port", "RCON_PORT");
            envMapping.put("rcon.allowed", "RCON_ALLOWED");
            envMapping.put("runtime.threads", "RT_THREADS");
            envMapping.put("logging.errors.runtime", "RT_LOG_ERRORS");
            for (Map.Entry entry : envMapping.entrySet()) {
                String envValue = System.getenv((String)entry.getValue());
                if (envValue == null || envValue.length() == 0) {
                    LOGGER.info("Cannot find environment-value for variable `{}`", entry.getValue());
                    continue;
                }
                this.properties.setProperty((String)entry.getKey(), envValue);
            }
        }
        if (this.loaded) {
            this.loadFromDatabase();
        }
        this.isLoading = false;
        LOGGER.info("Configuration Manager -> Loaded!");
        if (Emulator.getPluginManager() != null) {
            Emulator.getPluginManager().fireEvent(new EmulatorConfigUpdatedEvent());
        }
    }

    public void loadFromDatabase() {
        long millis;
        block21: {
            LOGGER.info("Loading configuration from database...");
            millis = System.currentTimeMillis();
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                 Statement statement = connection.createStatement();){
                if (!statement.execute("SELECT * FROM emulator_settings")) break block21;
                try (ResultSet set = statement.getResultSet();){
                    while (set.next()) {
                        this.properties.put(set.getString("key"), set.getString("value"));
                    }
                }
            }
            catch (SQLException e) {
                LOGGER.error("Caught SQL exception", e);
            }
        }
        LOGGER.info("Configuration -> loaded! ({} MS)", (Object)(System.currentTimeMillis() - millis));
    }

    public void saveToDatabase() {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE emulator_settings SET `value` = ? WHERE `key` = ? LIMIT 1");){
            for (Map.Entry<Object, Object> entry : this.properties.entrySet()) {
                statement.setString(1, entry.getValue().toString());
                statement.setString(2, entry.getKey().toString());
                statement.executeUpdate();
            }
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }

    public String getValue(String key) {
        return this.getValue(key, "");
    }

    public String getValue(String key, String defaultValue) {
        if (this.isLoading) {
            return defaultValue;
        }
        if (!this.properties.containsKey(key)) {
            LOGGER.error("Config key not found {}", (Object)key);
        }
        return this.properties.getProperty(key, defaultValue);
    }

    public boolean getBoolean(String key) {
        return this.getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        if (this.isLoading) {
            return defaultValue;
        }
        try {
            return this.getValue(key, "0").equals("1") || this.getValue(key, "false").equals("true");
        }
        catch (Exception e) {
            LOGGER.error("Failed to parse key {} with value '{}' to type boolean.", (Object)key, (Object)this.getValue(key));
            return defaultValue;
        }
    }

    public int getInt(String key) {
        return this.getInt(key, 0);
    }

    public int getInt(String key, Integer defaultValue) {
        if (this.isLoading) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(this.getValue(key, defaultValue.toString()));
        }
        catch (Exception e) {
            LOGGER.error("Failed to parse key {} with value '{}' to type integer.", (Object)key, (Object)this.getValue(key));
            return defaultValue;
        }
    }

    public double getDouble(String key) {
        return this.getDouble(key, 0.0);
    }

    public double getDouble(String key, Double defaultValue) {
        if (this.isLoading) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(this.getValue(key, defaultValue.toString()));
        }
        catch (Exception e) {
            LOGGER.error("Failed to parse key {} with value '{}' to type double.", (Object)key, (Object)this.getValue(key));
            return defaultValue;
        }
    }

    public void update(String key, String value) {
        this.properties.setProperty(key, value);
    }

    public void register(String key, String value) {
        if (this.properties.getProperty(key, null) != null) {
            return;
        }
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO emulator_settings VALUES (?, ?)");){
            statement.setString(1, key);
            statement.setString(2, value);
            statement.execute();
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
        this.update(key, value);
    }
}

