/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import java.util.Map;

public class UnkownPetPackageComposer
extends MessageComposer {
    private final Map<String, String> unknownMap;

    public UnkownPetPackageComposer(Map<String, String> unknownMap) {
        this.unknownMap = unknownMap;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1723);
        this.response.appendInt(this.unknownMap.size());
        for (Map.Entry<String, String> entry : this.unknownMap.entrySet()) {
            this.response.appendString(entry.getKey());
            this.response.appendString(entry.getValue());
        }
        return this.response;
    }

    public Map<String, String> getUnknownMap() {
        return this.unknownMap;
    }
}

