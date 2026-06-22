/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Internal;
import com.google.protobuf.ProtocolMessageEnum;
import com.google.protobuf.TypeProto;

public enum Syntax implements ProtocolMessageEnum
{
    SYNTAX_PROTO2(0),
    SYNTAX_PROTO3(1),
    UNRECOGNIZED(-1);

    public static final int SYNTAX_PROTO2_VALUE = 0;
    public static final int SYNTAX_PROTO3_VALUE = 1;
    private static final Internal.EnumLiteMap<Syntax> internalValueMap;
    private static final Syntax[] VALUES;
    private final int value;

    @Override
    public final int getNumber() {
        if (this == UNRECOGNIZED) {
            throw new IllegalArgumentException("Can't get the number of an unknown enum value.");
        }
        return this.value;
    }

    @Deprecated
    public static Syntax valueOf(int value) {
        return Syntax.forNumber(value);
    }

    public static Syntax forNumber(int value) {
        switch (value) {
            case 0: {
                return SYNTAX_PROTO2;
            }
            case 1: {
                return SYNTAX_PROTO3;
            }
        }
        return null;
    }

    public static Internal.EnumLiteMap<Syntax> internalGetValueMap() {
        return internalValueMap;
    }

    @Override
    public final Descriptors.EnumValueDescriptor getValueDescriptor() {
        return Syntax.getDescriptor().getValues().get(this.ordinal());
    }

    @Override
    public final Descriptors.EnumDescriptor getDescriptorForType() {
        return Syntax.getDescriptor();
    }

    public static final Descriptors.EnumDescriptor getDescriptor() {
        return TypeProto.getDescriptor().getEnumTypes().get(0);
    }

    public static Syntax valueOf(Descriptors.EnumValueDescriptor desc) {
        if (desc.getType() != Syntax.getDescriptor()) {
            throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
        }
        if (desc.getIndex() == -1) {
            return UNRECOGNIZED;
        }
        return VALUES[desc.getIndex()];
    }

    private Syntax(int value) {
        this.value = value;
    }

    static {
        internalValueMap = new Internal.EnumLiteMap<Syntax>(){

            @Override
            public Syntax findValueByNumber(int number) {
                return Syntax.forNumber(number);
            }
        };
        VALUES = Syntax.values();
    }
}

