/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.socksx.v5;

import io.netty.handler.codec.socksx.v5.Socks5AuthMethod;
import io.netty.handler.codec.socksx.v5.Socks5Message;

public interface Socks5InitialResponse
extends Socks5Message {
    public Socks5AuthMethod authMethod();
}

