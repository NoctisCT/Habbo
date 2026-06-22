/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.navigation;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.navigation.NavigatorFavoriteFilter;
import com.eu.habbo.habbohotel.navigation.NavigatorFilter;
import com.eu.habbo.habbohotel.navigation.NavigatorFilterComparator;
import com.eu.habbo.habbohotel.navigation.NavigatorFilterField;
import com.eu.habbo.habbohotel.navigation.NavigatorHotelFilter;
import com.eu.habbo.habbohotel.navigation.NavigatorPublicCategory;
import com.eu.habbo.habbohotel.navigation.NavigatorPublicFilter;
import com.eu.habbo.habbohotel.navigation.NavigatorRoomAdsFilter;
import com.eu.habbo.habbohotel.navigation.NavigatorUserFilter;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import gnu.trove.map.hash.THashMap;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NavigatorManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(NavigatorManager.class);
    public static int MAXIMUM_RESULTS_PER_PAGE = 10;
    public static boolean CATEGORY_SORT_USING_ORDER_NUM = false;
    public final THashMap<Integer, NavigatorPublicCategory> publicCategories = new THashMap();
    public final ConcurrentHashMap<String, NavigatorFilterField> filterSettings = new ConcurrentHashMap();
    public final THashMap<String, NavigatorFilter> filters = new THashMap();

    public NavigatorManager() {
        long millis = System.currentTimeMillis();
        this.filters.put("official_view", new NavigatorPublicFilter());
        this.filters.put("hotel_view", new NavigatorHotelFilter());
        this.filters.put("roomads_view", new NavigatorRoomAdsFilter());
        this.filters.put("myworld_view", new NavigatorUserFilter());
        this.filters.put("favorites", new NavigatorFavoriteFilter());
        LOGGER.info("Navigator Manager -> Loaded! ({} MS)", (Object)(System.currentTimeMillis() - millis));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void loadNavigator() {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();){
            ResultSet set;
            Statement statement;
            ConcurrentHashMap<String, NavigatorFilterField> concurrentHashMap = this.publicCategories;
            synchronized (concurrentHashMap) {
                this.publicCategories.clear();
                try {
                    statement = connection.createStatement();
                    try {
                        set = statement.executeQuery("SELECT * FROM navigator_publiccats WHERE visible = '1' ORDER BY order_num DESC");
                        try {
                            while (set.next()) {
                                this.publicCategories.put(set.getInt("id"), new NavigatorPublicCategory(set));
                            }
                        }
                        finally {
                            if (set != null) {
                                set.close();
                            }
                        }
                    }
                    finally {
                        if (statement != null) {
                            statement.close();
                        }
                    }
                }
                catch (SQLException e) {
                    LOGGER.error("Caught SQL exception", e);
                }
                try {
                    statement = connection.createStatement();
                    try {
                        set = statement.executeQuery("SELECT * FROM navigator_publics WHERE visible = '1'");
                        try {
                            while (set.next()) {
                                NavigatorPublicCategory category = this.publicCategories.get(set.getInt("public_cat_id"));
                                if (category == null) continue;
                                Room room = Emulator.getGameEnvironment().getRoomManager().loadRoom(set.getInt("room_id"));
                                if (room != null) {
                                    category.addRoom(room);
                                    continue;
                                }
                                LOGGER.error("Public room (ID: {} defined in navigator_publics does not exist!", (Object)set.getInt("room_id"));
                            }
                        }
                        finally {
                            if (set != null) {
                                set.close();
                            }
                        }
                    }
                    finally {
                        if (statement != null) {
                            statement.close();
                        }
                    }
                }
                catch (SQLException e) {
                    LOGGER.error("Caught SQL exception", e);
                }
            }
            concurrentHashMap = this.filterSettings;
            synchronized (concurrentHashMap) {
                try {
                    statement = connection.createStatement();
                    try {
                        set = statement.executeQuery("SELECT * FROM navigator_filter");
                        try {
                            while (set.next()) {
                                Method field = null;
                                Class clazz = Room.class;
                                if (set.getString("field").contains(".")) {
                                    for (String s : set.getString("field").split("\\.")) {
                                        try {
                                            field = clazz.getDeclaredMethod(s, new Class[0]);
                                            clazz = field.getReturnType();
                                        }
                                        catch (Exception e) {
                                            LOGGER.error("Caught exception", e);
                                            break;
                                        }
                                    }
                                } else {
                                    try {
                                        field = clazz.getDeclaredMethod(set.getString("field"), new Class[0]);
                                    }
                                    catch (Exception e) {
                                        LOGGER.error("Caught exception", e);
                                        continue;
                                    }
                                }
                                if (field == null) continue;
                                this.filterSettings.put(set.getString("key"), new NavigatorFilterField(set.getString("key"), field, set.getString("database_query"), NavigatorFilterComparator.valueOf(set.getString("compare").toUpperCase())));
                            }
                        }
                        finally {
                            if (set != null) {
                                set.close();
                            }
                        }
                    }
                    finally {
                        if (statement != null) {
                            statement.close();
                        }
                    }
                }
                catch (SQLException e) {
                    LOGGER.error("Caught SQL exception", e);
                }
            }
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
        ArrayList<Room> staffPromotedRooms = Emulator.getGameEnvironment().getRoomManager().getRoomsStaffPromoted();
        for (Room room : staffPromotedRooms) {
            this.publicCategories.get(Emulator.getConfig().getInt("hotel.navigator.staffpicks.categoryid")).addRoom(room);
        }
    }

    public NavigatorFilterComparator comperatorForField(Method field) {
        for (Map.Entry<String, NavigatorFilterField> set : this.filterSettings.entrySet()) {
            if (set.getValue().field != field) continue;
            return set.getValue().comparator;
        }
        return null;
    }

    public List<Room> getRoomsForCategory(String category, Habbo habbo) {
        ArrayList<Room> rooms = new ArrayList();
        switch (category) {
            case "my": {
                rooms = Emulator.getGameEnvironment().getRoomManager().getRoomsForHabbo(habbo);
                break;
            }
            case "favorites": {
                rooms = Emulator.getGameEnvironment().getRoomManager().getRoomsFavourite(habbo);
                break;
            }
            case "history_freq": {
                rooms = Emulator.getGameEnvironment().getRoomManager().getRoomsVisited(habbo, false, 10);
                break;
            }
            case "my_groups": {
                rooms = Emulator.getGameEnvironment().getRoomManager().getGroupRooms(habbo, 25);
                break;
            }
            case "with_rights": {
                rooms = Emulator.getGameEnvironment().getRoomManager().getRoomsWithRights(habbo);
                break;
            }
            case "official-root": {
                rooms = Emulator.getGameEnvironment().getRoomManager().getPublicRooms();
                break;
            }
            case "popular": {
                rooms = Emulator.getGameEnvironment().getRoomManager().getPopularRooms(Emulator.getConfig().getInt("hotel.navigator.popular.amount"));
                break;
            }
            case "categories": {
                rooms = Emulator.getGameEnvironment().getRoomManager().getRoomsPromoted();
                break;
            }
            case "with_friends": {
                rooms = Emulator.getGameEnvironment().getRoomManager().getRoomsWithFriendsIn(habbo, 25);
                break;
            }
            case "highest_score": {
                rooms = Emulator.getGameEnvironment().getRoomManager().getTopRatedRooms(25);
                break;
            }
            default: {
                return null;
            }
        }
        Collections.sort(rooms);
        return rooms;
    }
}

