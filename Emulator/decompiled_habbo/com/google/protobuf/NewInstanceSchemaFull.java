/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.NewInstanceSchema;

final class NewInstanceSchemaFull
implements NewInstanceSchema {
    NewInstanceSchemaFull() {
    }

    @Override
    public Object newInstance(Object defaultInstance) {
        return ((GeneratedMessageV3)defaultInstance).newInstance(GeneratedMessageV3.UnusedPrivateParameter.INSTANCE);
    }
}

