/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.navigation;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.navigation.NavigatorFilterComparator;
import com.eu.habbo.habbohotel.navigation.NavigatorFilterField;
import com.eu.habbo.habbohotel.navigation.SearchResultList;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public abstract class NavigatorFilter {
    public final String viewName;

    public NavigatorFilter(String viewName) {
        this.viewName = viewName;
    }

    public void filter(Method method, Object value, List<SearchResultList> collection) {
        if (method == null) {
            return;
        }
        if (value instanceof String && ((String)value).isEmpty()) {
            return;
        }
        for (SearchResultList result : collection) {
            if (!result.filter) continue;
            this.filterRooms(method, value, result.rooms);
        }
    }

    public void filterRooms(Method method, Object value, List<Room> result) {
        if (method == null) {
            return;
        }
        if (value instanceof String && ((String)value).isEmpty()) {
            return;
        }
        ArrayList<Room> toRemove = new ArrayList<Room>();
        try {
            method.setAccessible(true);
            for (Room room : result) {
                Object o = method.invoke((Object)room, new Object[0]);
                if (o.getClass() != value.getClass()) continue;
                if (o instanceof String) {
                    NavigatorFilterComparator comparator = Emulator.getGameEnvironment().getNavigatorManager().comperatorForField(method);
                    if (comparator != null) {
                        if (this.applies(comparator, (String)o, (String)value)) continue;
                        toRemove.add(room);
                        continue;
                    }
                    toRemove.add(room);
                    continue;
                }
                if (o instanceof String[]) {
                    for (String s : (String[])o) {
                        NavigatorFilterComparator comparator = Emulator.getGameEnvironment().getNavigatorManager().comperatorForField(method);
                        if (comparator == null || this.applies(comparator, s, (String)value)) continue;
                        toRemove.add(room);
                    }
                    continue;
                }
                if (o == value) continue;
                toRemove.add(room);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        result.removeAll(toRemove);
        toRemove.clear();
    }

    public abstract List<SearchResultList> getResult(Habbo var1);

    public List<SearchResultList> getResult(Habbo habbo, NavigatorFilterField filterField, String value, int roomCategory) {
        return this.getResult(habbo);
    }

    private boolean applies(NavigatorFilterComparator comparator, String o, String value) {
        switch (comparator) {
            case CONTAINS: {
                if (!StringUtils.containsIgnoreCase(o, value)) break;
                return true;
            }
            case EQUALS: {
                if (!o.equals(value)) break;
                return true;
            }
            case EQUALS_IGNORE_CASE: {
                if (!o.equalsIgnoreCase(value)) break;
                return true;
            }
        }
        return false;
    }
}

