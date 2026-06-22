/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.rcon;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.rcon.RCONMessage;
import com.google.gson.Gson;

public class UpdateWordfilter
extends RCONMessage<WordFilterJSON> {
    public UpdateWordfilter() {
        super(WordFilterJSON.class);
    }

    @Override
    public void handle(Gson gson, WordFilterJSON object) {
        Emulator.getGameEnvironment().getWordFilter().reload();
    }

    static class WordFilterJSON {
        WordFilterJSON() {
        }
    }
}

