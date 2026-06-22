/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.hotelview;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.hotelview.NewsWidget;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NewsList {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewsList.class);
    private final ArrayList<NewsWidget> newsWidgets = new ArrayList();

    public NewsList() {
        this.reload();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void reload() {
        ArrayList<NewsWidget> arrayList = this.newsWidgets;
        synchronized (arrayList) {
            this.newsWidgets.clear();
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                 Statement statement = connection.createStatement();
                 ResultSet set = statement.executeQuery("SELECT * FROM hotelview_news ORDER BY id DESC LIMIT 10");){
                while (set.next()) {
                    this.newsWidgets.add(new NewsWidget(set));
                }
            }
            catch (SQLException e) {
                LOGGER.error("Caught SQL exception", e);
            }
        }
    }

    public ArrayList<NewsWidget> getNewsWidgets() {
        return this.newsWidgets;
    }
}

