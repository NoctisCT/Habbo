/*
 * Decompiled with CFR 0.152.
 */
package com.mysql.cj.jdbc.admin;

import com.mysql.cj.util.TimeUtil;
import java.sql.DriverManager;
import java.sql.ResultSet;

public class TimezoneDump {
    private static final String DEFAULT_URL = "jdbc:mysql:///test";

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void main(String[] args) throws Exception {
        String jdbcUrl = DEFAULT_URL;
        if (args.length == 1 && args[0] != null) {
            jdbcUrl = args[0];
        }
        Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        try (ResultSet rs = null;){
            rs = DriverManager.getConnection(jdbcUrl).createStatement().executeQuery("SHOW VARIABLES LIKE 'timezone'");
            while (rs.next()) {
                String timezoneFromServer = rs.getString(2);
                System.out.println("MySQL timezone name: " + timezoneFromServer);
                String canonicalTimezone = TimeUtil.getCanonicalTimezone(timezoneFromServer, null);
                System.out.println("Java timezone name: " + canonicalTimezone);
            }
        }
    }
}

