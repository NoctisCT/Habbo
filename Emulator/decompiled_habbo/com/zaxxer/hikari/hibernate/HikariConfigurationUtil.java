/*
 * Decompiled with CFR 0.152.
 */
package com.zaxxer.hikari.hibernate;

import com.zaxxer.hikari.HikariConfig;
import java.util.Map;
import java.util.Properties;

public class HikariConfigurationUtil {
    public static final String CONFIG_PREFIX = "hibernate.hikari.";
    public static final String CONFIG_PREFIX_DATASOURCE = "hibernate.hikari.dataSource.";

    public static HikariConfig loadConfiguration(Map props) {
        Properties hikariProps = new Properties();
        HikariConfigurationUtil.copyProperty("hibernate.connection.isolation", props, "transactionIsolation", hikariProps);
        HikariConfigurationUtil.copyProperty("hibernate.connection.autocommit", props, "autoCommit", hikariProps);
        HikariConfigurationUtil.copyProperty("hibernate.connection.driver_class", props, "driverClassName", hikariProps);
        HikariConfigurationUtil.copyProperty("hibernate.connection.url", props, "jdbcUrl", hikariProps);
        HikariConfigurationUtil.copyProperty("hibernate.connection.username", props, "username", hikariProps);
        HikariConfigurationUtil.copyProperty("hibernate.connection.password", props, "password", hikariProps);
        for (Object keyo : props.keySet()) {
            String key = (String)keyo;
            if (!key.startsWith(CONFIG_PREFIX)) continue;
            hikariProps.setProperty(key.substring(CONFIG_PREFIX.length()), (String)props.get(key));
        }
        return new HikariConfig(hikariProps);
    }

    private static void copyProperty(String srcKey, Map src, String dstKey, Properties dst) {
        if (src.containsKey(srcKey)) {
            dst.setProperty(dstKey, (String)src.get(srcKey));
        }
    }
}

