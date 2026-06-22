/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import java.util.List;

public class UnknownHabboWayQuizComposer
extends MessageComposer {
    private final String unknownString;
    private final List<Integer> unknownIntegerList;

    public UnknownHabboWayQuizComposer(String unknownString, List<Integer> unknownIntegerList) {
        this.unknownString = unknownString;
        this.unknownIntegerList = unknownIntegerList;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2772);
        this.response.appendString(this.unknownString);
        this.response.appendInt(this.unknownIntegerList.size());
        for (Integer i : this.unknownIntegerList) {
            this.response.appendInt(i);
        }
        return this.response;
    }

    public String getUnknownString() {
        return this.unknownString;
    }

    public List<Integer> getUnknownIntegerList() {
        return this.unknownIntegerList;
    }
}

