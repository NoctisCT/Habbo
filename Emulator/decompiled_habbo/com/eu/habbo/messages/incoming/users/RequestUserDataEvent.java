/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.incoming.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.users.MeMenuSettingsComposer;
import com.eu.habbo.messages.outgoing.users.UserDataComposer;
import com.eu.habbo.messages.outgoing.users.UserPerksComposer;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestUserDataEvent
extends MessageHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestUserDataEvent.class);

    @Override
    public void handle() throws Exception {
        if (this.client.getHabbo() != null) {
            ArrayList<ServerMessage> messages = new ArrayList<ServerMessage>();
            messages.add(new UserDataComposer(this.client.getHabbo()).compose());
            messages.add(new UserPerksComposer(this.client.getHabbo()).compose());
            messages.add(new MeMenuSettingsComposer(this.client.getHabbo()).compose());
            this.client.sendResponses(messages);
        } else {
            LOGGER.debug("Attempted to request user data where Habbo was null.");
            Emulator.getGameServer().getGameClientManager().disposeClient(this.client);
        }
    }
}

