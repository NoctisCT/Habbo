/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.socks;

import io.netty.handler.codec.socks.SocksMessage;
import io.netty.handler.codec.socks.SocksMessageType;
import io.netty.handler.codec.socks.SocksRequestType;
import io.netty.util.internal.ObjectUtil;

public abstract class SocksRequest
extends SocksMessage {
    private final SocksRequestType requestType;

    protected SocksRequest(SocksRequestType requestType) {
        super(SocksMessageType.REQUEST);
        this.requestType = ObjectUtil.checkNotNull(requestType, "requestType");
    }

    public SocksRequestType requestType() {
        return this.requestType;
    }
}

