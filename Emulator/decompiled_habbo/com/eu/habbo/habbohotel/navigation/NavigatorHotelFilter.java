/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.navigation;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.navigation.DisplayOrder;
import com.eu.habbo.habbohotel.navigation.ListMode;
import com.eu.habbo.habbohotel.navigation.NavigatorFilter;
import com.eu.habbo.habbohotel.navigation.NavigatorFilterField;
import com.eu.habbo.habbohotel.navigation.SearchAction;
import com.eu.habbo.habbohotel.navigation.SearchResultList;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomCategory;
import com.eu.habbo.habbohotel.users.Habbo;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NavigatorHotelFilter
extends NavigatorFilter {
    public static final String name = "hotel_view";

    public NavigatorHotelFilter() {
        super(name);
    }

    @Override
    public List<SearchResultList> getResult(Habbo habbo) {
        boolean showInvisible = habbo.hasPermission(Permission.ACC_ENTERANYROOM) || habbo.hasPermission(Permission.ACC_ANYROOMOWNER);
        ArrayList<SearchResultList> resultLists = new ArrayList<SearchResultList>();
        int i = 0;
        resultLists.add(new SearchResultList(i, "popular", "", SearchAction.NONE, habbo.getHabboStats().navigatorWindowSettings.getListModeForCategory("popular", ListMode.fromType(Emulator.getConfig().getInt("hotel.navigator.popular.listtype"))), habbo.getHabboStats().navigatorWindowSettings.getDisplayModeForCategory("popular"), Emulator.getGameEnvironment().getNavigatorManager().getRoomsForCategory("popular", habbo), false, showInvisible, DisplayOrder.ORDER_NUM, -1));
        ++i;
        for (Map.Entry<Integer, List<Room>> set : Emulator.getGameEnvironment().getRoomManager().getPopularRoomsByCategory(Emulator.getConfig().getInt("hotel.navigator.popular.category.maxresults")).entrySet()) {
            if (set.getValue().isEmpty()) continue;
            RoomCategory category = Emulator.getGameEnvironment().getRoomManager().getCategory(set.getKey());
            if (category != null) {
                resultLists.add(new SearchResultList(i, category.getCaption(), category.getCaption(), SearchAction.MORE, habbo.getHabboStats().navigatorWindowSettings.getListModeForCategory(category.getCaptionSave()), habbo.getHabboStats().navigatorWindowSettings.getDisplayModeForCategory(category.getCaptionSave()), set.getValue(), true, showInvisible, DisplayOrder.ORDER_NUM, category.getOrder()));
            }
            ++i;
        }
        return resultLists;
    }

    @Override
    public List<SearchResultList> getResult(Habbo habbo, NavigatorFilterField filterField, String value, int roomCategory) {
        boolean showInvisible;
        boolean bl = showInvisible = habbo.hasPermission(Permission.ACC_ENTERANYROOM) || habbo.hasPermission(Permission.ACC_ANYROOMOWNER);
        if (!filterField.databaseQuery.isEmpty()) {
            ArrayList<SearchResultList> resultLists = new ArrayList<SearchResultList>();
            int i = 0;
            for (Map.Entry<Integer, List<Room>> set : Emulator.getGameEnvironment().getRoomManager().findRooms(filterField, value, roomCategory, showInvisible).entrySet()) {
                if (set.getValue().isEmpty()) continue;
                RoomCategory category = Emulator.getGameEnvironment().getRoomManager().getCategory(set.getKey());
                if (category != null) {
                    resultLists.add(new SearchResultList(i, category.getCaptionSave(), category.getCaption(), SearchAction.MORE, habbo.getHabboStats().navigatorWindowSettings.getListModeForCategory(category.getCaptionSave()), habbo.getHabboStats().navigatorWindowSettings.getDisplayModeForCategory(category.getCaptionSave()), set.getValue(), true, showInvisible, DisplayOrder.ACTIVITY, category.getOrder()));
                }
                ++i;
            }
            return resultLists;
        }
        return this.getResult(habbo);
    }
}

