/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.networking.camera.messages.outgoing;

import com.eu.habbo.Emulator;
import com.eu.habbo.networking.camera.CameraOutgoingMessage;
import io.netty.channel.Channel;

public class CameraLoginComposer
extends CameraOutgoingMessage {
    public CameraLoginComposer() {
        super((short)1);
    }

    @Override
    public void compose(Channel channel) {
        this.appendString(Emulator.getConfig().getValue("username").trim());
        this.appendString(Emulator.getConfig().getValue("password").trim());
        this.appendString("Arcturus Morningstar 3.6.1 ");
    }
}

