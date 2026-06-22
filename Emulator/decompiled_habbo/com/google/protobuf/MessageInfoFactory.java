/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.MessageInfo;

interface MessageInfoFactory {
    public boolean isSupported(Class<?> var1);

    public MessageInfo messageInfoFor(Class<?> var1);
}

