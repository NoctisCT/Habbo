/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.incoming.hotelview;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.hotelview.HotelViewDataComposer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HotelViewDataEvent
extends MessageHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(HotelViewDataEvent.class);

    @Override
    public void handle() {
        try {
            String data = this.packet.readString();
            if (data.contains(";")) {
                int n = 0;
                String[] d = data.split(";");
                String[] stringArray = d;
                int n2 = stringArray.length;
                if (n < n2) {
                    String s = stringArray[n];
                    if (s.contains(",")) {
                        this.client.sendResponse(new HotelViewDataComposer(s, s.split(",")[s.split(",").length - 1]));
                    } else {
                        this.client.sendResponse(new HotelViewDataComposer(data, s));
                    }
                }
            } else {
                this.client.sendResponse(new HotelViewDataComposer(data, data.split(",")[data.split(",").length - 1]));
            }
        }
        catch (Exception e) {
            LOGGER.error("Caught exception", e);
        }
    }
}

