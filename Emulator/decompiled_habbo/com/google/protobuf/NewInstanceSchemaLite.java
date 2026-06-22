/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.NewInstanceSchema;

final class NewInstanceSchemaLite
implements NewInstanceSchema {
    NewInstanceSchemaLite() {
    }

    @Override
    public Object newInstance(Object defaultInstance) {
        return ((GeneratedMessageLite)defaultInstance).dynamicMethod(GeneratedMessageLite.MethodToInvoke.NEW_MUTABLE_INSTANCE);
    }
}

