/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.incoming.navigator;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.navigation.DisplayMode;
import com.eu.habbo.habbohotel.navigation.DisplayOrder;
import com.eu.habbo.habbohotel.navigation.ListMode;
import com.eu.habbo.habbohotel.navigation.NavigatorFilter;
import com.eu.habbo.habbohotel.navigation.NavigatorFilterField;
import com.eu.habbo.habbohotel.navigation.SearchAction;
import com.eu.habbo.habbohotel.navigation.SearchResultList;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomCategory;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.navigator.NewNavigatorSearchResultsComposer;
import gnu.trove.map.hash.THashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestNewNavigatorRoomsEvent
extends MessageHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestNewNavigatorRoomsEvent.class);

    @Override
    public void handle() throws Exception {
        List<Room> rooms;
        String view = this.packet.readString();
        String query = this.packet.readString();
        if (view.equals("query")) {
            view = "hotel_view";
        }
        if (view.equals("groups")) {
            view = "hotel_view";
        }
        NavigatorFilter filter = Emulator.getGameEnvironment().getNavigatorManager().filters.get(view);
        RoomCategory category = Emulator.getGameEnvironment().getRoomManager().getCategoryBySafeCaption(view);
        if (filter == null && (rooms = Emulator.getGameEnvironment().getNavigatorManager().getRoomsForCategory(view, this.client.getHabbo())) != null) {
            ArrayList<SearchResultList> resultLists = new ArrayList<SearchResultList>();
            resultLists.add(new SearchResultList(0, view, query, SearchAction.NONE, this.client.getHabbo().getHabboStats().navigatorWindowSettings.getListModeForCategory(view, ListMode.LIST), this.client.getHabbo().getHabboStats().navigatorWindowSettings.getDisplayModeForCategory(view, DisplayMode.VISIBLE), rooms, true, true, DisplayOrder.ACTIVITY, -1));
            this.client.sendResponse(new NewNavigatorSearchResultsComposer(view, query, resultLists));
            return;
        }
        String filterField = "anything";
        String part = query;
        NavigatorFilterField field = Emulator.getGameEnvironment().getNavigatorManager().filterSettings.get(filterField);
        if (filter != null) {
            if (query.contains(":")) {
                String[] parts = query.split(":");
                if (parts.length > 1) {
                    filterField = parts[0];
                    part = parts[1];
                } else {
                    filterField = parts[0].replace(":", "");
                    if (!Emulator.getGameEnvironment().getNavigatorManager().filterSettings.containsKey(filterField)) {
                        filterField = "anything";
                    }
                }
            }
            if (Emulator.getGameEnvironment().getNavigatorManager().filterSettings.get(filterField) != null) {
                field = Emulator.getGameEnvironment().getNavigatorManager().filterSettings.get(filterField);
            }
        }
        if (field == null || query.isEmpty()) {
            if (filter == null) {
                return;
            }
            List<SearchResultList> resultLists = filter.getResult(this.client.getHabbo());
            Collections.sort(resultLists);
            if (!query.isEmpty()) {
                resultLists = this.toQueryResults(resultLists);
            }
            this.client.sendResponse(new NewNavigatorSearchResultsComposer(view, query, resultLists));
            return;
        }
        if (filter == null) {
            filter = Emulator.getGameEnvironment().getNavigatorManager().filters.get("hotel_view");
        }
        if (category == null) {
            category = Emulator.getGameEnvironment().getRoomManager().getCategoryBySafeCaption("hotel_view");
        }
        if (filter == null) {
            return;
        }
        try {
            List<SearchResultList> resultLists2 = filter.getResult(this.client.getHabbo(), field, part, category != null ? category.getId() : -1);
            ArrayList<SearchResultList> resultLists = new ArrayList<SearchResultList>();
            for (SearchResultList searchResultList : resultLists2) {
                ArrayList<Room> rooms2 = new ArrayList<Room>();
                rooms2.addAll(searchResultList.rooms);
                resultLists.add(new SearchResultList(searchResultList.order, searchResultList.code, searchResultList.query, searchResultList.action, searchResultList.mode, searchResultList.hidden, rooms2, searchResultList.filter, searchResultList.showInvisible, searchResultList.displayOrder, searchResultList.categoryOrder));
            }
            filter.filter(field.field, part, resultLists);
            resultLists = this.toQueryResults(resultLists);
            this.client.sendResponse(new NewNavigatorSearchResultsComposer(view, query, resultLists));
        }
        catch (Exception e) {
            LOGGER.error("Caught exception", e);
        }
    }

    private ArrayList<SearchResultList> toQueryResults(List<SearchResultList> resultLists) {
        ArrayList<SearchResultList> nList = new ArrayList<SearchResultList>();
        THashMap<Integer, Room> searchRooms = new THashMap<Integer, Room>();
        for (SearchResultList li : resultLists) {
            for (Room room : li.rooms) {
                searchRooms.put(room.getId(), room);
            }
        }
        SearchResultList list = new SearchResultList(0, "query", "", SearchAction.NONE, ListMode.LIST, DisplayMode.VISIBLE, new ArrayList<Room>(searchRooms.values()), true, this.client.getHabbo().hasPermission(Permission.ACC_ENTERANYROOM) || this.client.getHabbo().hasPermission(Permission.ACC_ANYROOMOWNER), DisplayOrder.ACTIVITY, -1);
        nList.add(list);
        return nList;
    }

    private void filter(List<SearchResultList> resultLists, NavigatorFilter filter, String part) {
        ArrayList<SearchResultList> toRemove = new ArrayList<SearchResultList>();
        HashMap filteredRooms = new HashMap();
        for (NavigatorFilterField navigatorFilterField : Emulator.getGameEnvironment().getNavigatorManager().filterSettings.values()) {
            for (SearchResultList result : resultLists) {
                if (!result.filter) continue;
                ArrayList<Room> rooms = new ArrayList<Room>(result.rooms.subList(0, result.rooms.size()));
                filter.filterRooms(navigatorFilterField.field, part, rooms);
                if (!filteredRooms.containsKey(result.order)) {
                    filteredRooms.put(result.order, new HashMap());
                }
                for (Room room : rooms) {
                    ((HashMap)filteredRooms.get(result.order)).put(room.getId(), room);
                }
            }
        }
        for (Map.Entry entry : filteredRooms.entrySet()) {
            for (SearchResultList resultList : resultLists) {
                if (!resultList.filter) continue;
                resultList.rooms.clear();
                resultList.rooms.addAll(((HashMap)entry.getValue()).values());
                if (!resultList.rooms.isEmpty()) continue;
                toRemove.add(resultList);
            }
        }
        resultLists.removeAll(toRemove);
    }
}

