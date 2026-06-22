/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.trading;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class TradeStartFailComposer
extends MessageComposer {
    public static final int HOTEL_TRADING_NOT_ALLOWED = 1;
    public static final int YOU_TRADING_OFF = 2;
    public static final int TARGET_TRADING_NOT_ALLOWED = 4;
    public static final int ROOM_TRADING_NOT_ALLOWED = 6;
    public static final int YOU_ALREADY_TRADING = 7;
    public static final int TARGET_ALREADY_TRADING = 8;
    private final String username;
    private final int code;

    public TradeStartFailComposer(int code) {
        this.code = code;
        this.username = "";
    }

    public TradeStartFailComposer(int code, String username) {
        this.code = code;
        this.username = username;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(217);
        this.response.appendInt(this.code);
        this.response.appendString(this.username);
        return this.response;
    }

    public String getUsername() {
        return this.username;
    }

    public int getCode() {
        return this.code;
    }
}

