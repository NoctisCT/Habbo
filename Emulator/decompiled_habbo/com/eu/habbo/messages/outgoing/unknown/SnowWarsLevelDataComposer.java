/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class SnowWarsLevelDataComposer
extends MessageComposer {
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(3874);
        this.response.appendInt(0);
        this.response.appendInt(10);
        this.response.appendInt(2);
        this.response.appendInt(2);
        this.response.appendInt(0);
        this.response.appendString("Admin");
        this.response.appendString("ca-1807-64.lg-275-78.hd-3093-1.hr-802-42.ch-3110-65-62.fa-1211-62");
        this.response.appendString("m");
        this.response.appendInt(1);
        this.response.appendInt(1);
        this.response.appendString("Droppy");
        this.response.appendString("ca-1807-64.lg-275-78.hd-3093-1.hr-802-42.ch-3110-65-62.fa-1211-62");
        this.response.appendString("m");
        this.response.appendInt(2);
        this.response.appendInt(50);
        this.response.appendInt(1);
        this.response.appendString("00000000000000000000000000000000000000000000000000\rxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\rxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\rxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\rxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\rxxxxxxxxxxxxxx000000000000000xxxxxxxxxxxxxxxxxxxxx\rxxxxxxxxxxxxx00000000000000000xxxxxxxxxxxxxxxxxxxx\rxxxxxxxxxxxx0000000000000000000xxxxxxxxxxxxxxxxxxx\rxxxxxxxxxxx000000000000000000000xxxxxxxxxxxxxxxxxx\rxxxxxxxxxx00000000000000000000000xxxxxxxxxxxxxxxxx\rxxxxxxxxx0000000000000000000000000xxxxxxxxxxxxxxxx\rxxxxxxxx000000000000000000000000000xxxxxxxxxxxxxxx\rxxxxxxx00000000000000000000000000000xxxxxxxxxxxxxx\rxxxxxx0000000000000000000000000000000xxxxxxxxxxxxx\rxxxxx000000000000000000000000000000000xxxxxxxxxxxx\rxxxxx0000000000000000000000000000000000xxxxxxxxxxx\rxxxxx00000000000000000000000000000000000xxxxxxxxxx\rxxxxx000000000000000000000000000000000000xxxxxxxxx\rxxxxx0000000000000000000000000000000000000xxxxxxxx\rxxxxx00000000000000000000000000000000000000xxxxxxx\rxxxxx000000000000000000000000000000000000000xxxxxx\rxxxxx0000000000000000000000000000000000000000xxxxx\r0xxxx00000000000000000000000000000000000000000xxxx\rxxxxx00000000000000000000000000000000000000000xxxx\rxxxxx00000000000000000000000000000000000000000xxxx\rxxxxx000000000000000000000000000000000000000000xxx\rxxxxx000000000000000000000000000000000000000000xxx\rxxxxx000000000000000000000000000000000000000000xxx\rxxxxxx00000000000000000000000000000000000000000xxx\rxxxxxxx0000000000000000000000000000000000000000xxx\rxxxxxxxx0000000000000000000000000000000000000xxxxx\rxxxxxxxxx00000000000000000000000000000000000xxxxxx\rxxxxxxxxxx000000000000000000000000000000000xxxxxxx\rxxxxxxxxxxx00000000000000000000000000000000xxxx0xx\rxxxxxxxxxxxx0000000000000000000000000000000xxxxxxx\rxxxxxxxxxxxxx00000000000000000000000000000xxxxxxxx\rxxxxxxxxxxxxxx0000000000000000000000000000xxxxxxxx\rxxxxxxxxxxxxxxx00000000000000000000000000xxxxxxxxx\rxxxxxxxxxxxxxxxx0000000000000000000000000xxxxxxxxx\rxxxxxxxxxxxxxxxxx00000000000000000000000xxxxxxxxxx\rxxxxxxxxxxxxxxxxxx0000000000000000000000xxxxxxxxxx\rxxxxxxxxxxxxxxxxxxx00000000000000000000xxxxxxxxxxx\rxxxxxxxxxxxxxxxxxxxxxxx000000000000000xxxxxxxxxxxx\rxxxxxxxxxxxxxxxxxxxxxxxx0000000000000xxxxxxxxxxxxx\rxxxxxxxxxxxxxxxxxxxxxxxxx00000000000xxxxxxxxxxxxxx\rxxxxxxxxxxxxxxxxxxxxxxxxxx0000000xxxxxxxxxxxxxxxxx\rxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\rxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\rxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\rxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        this.response.appendInt(0);
        return this.response;
    }
}

