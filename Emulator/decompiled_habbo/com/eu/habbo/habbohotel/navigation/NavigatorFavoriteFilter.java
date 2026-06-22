/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.navigation;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.navigation.DisplayMode;
import com.eu.habbo.habbohotel.navigation.DisplayOrder;
import com.eu.habbo.habbohotel.navigation.ListMode;
import com.eu.habbo.habbohotel.navigation.NavigatorFilter;
import com.eu.habbo.habbohotel.navigation.SearchAction;
import com.eu.habbo.habbohotel.navigation.SearchResultList;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import java.util.ArrayList;
import java.util.List;

public class NavigatorFavoriteFilter
extends NavigatorFilter {
    public static final String name = "favorites";

    public NavigatorFavoriteFilter() {
        super(name);
    }

    @Override
    public List<SearchResultList> getResult(Habbo habbo) {
        ArrayList<SearchResultList> resultLists = new ArrayList<SearchResultList>();
        List<Room> rooms = Emulator.getGameEnvironment().getNavigatorManager().getRoomsForCategory(name, habbo);
        resultLists.add(new SearchResultList(0, name, "", SearchAction.NONE, habbo.getHabboStats().navigatorWindowSettings.getListModeForCategory(name, ListMode.LIST), habbo.getHabboStats().navigatorWindowSettings.getDisplayModeForCategory("popular", DisplayMode.VISIBLE), rooms, true, true, DisplayOrder.ACTIVITY, -1));
        return resultLists;
    }
}

