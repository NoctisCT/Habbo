/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DBHelper {
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            }
            catch (SQLException sQLException) {
                // empty catch block
            }
        }
    }

    public static void closeStatement(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            }
            catch (SQLException sQLException) {
                // empty catch block
            }
        }
    }
}

