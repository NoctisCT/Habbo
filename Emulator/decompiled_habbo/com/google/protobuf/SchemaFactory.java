/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.Schema;

interface SchemaFactory {
    public <T> Schema<T> createSchema(Class<T> var1);
}

