/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.navigator;

import com.eu.habbo.habbohotel.navigation.EventCategory;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import java.util.ArrayList;
import java.util.List;

public class NewNavigatorEventCategoriesComposer
extends MessageComposer {
    public static List<EventCategory> CATEGORIES = new ArrayList<EventCategory>();

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(3244);
        this.response.appendInt(CATEGORIES.size());
        for (EventCategory category : CATEGORIES) {
            category.serialize(this.response);
        }
        return this.response;
    }
}

