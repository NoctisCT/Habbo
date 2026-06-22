/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import java.io.IOException;

public final class WireFormat {
    static final int FIXED32_SIZE = 4;
    static final int FIXED64_SIZE = 8;
    static final int MAX_VARINT32_SIZE = 5;
    static final int MAX_VARINT64_SIZE = 10;
    static final int MAX_VARINT_SIZE = 10;
    public static final int WIRETYPE_VARINT = 0;
    public static final int WIRETYPE_FIXED64 = 1;
    public static final int WIRETYPE_LENGTH_DELIMITED = 2;
    public static final int WIRETYPE_START_GROUP = 3;
    public static final int WIRETYPE_END_GROUP = 4;
    public static final int WIRETYPE_FIXED32 = 5;
    static final int TAG_TYPE_BITS = 3;
    static final int TAG_TYPE_MASK = 7;
    static final int MESSAGE_SET_ITEM = 1;
    static final int MESSAGE_SET_TYPE_ID = 2;
    static final int MESSAGE_SET_MESSAGE = 3;
    static final int MESSAGE_SET_ITEM_TAG = WireFormat.makeTag(1, 3);
    static final int MESSAGE_SET_ITEM_END_TAG = WireFormat.makeTag(1, 4);
    static final int MESSAGE_SET_TYPE_ID_TAG = WireFormat.makeTag(2, 0);
    static final int MESSAGE_SET_MESSAGE_TAG = WireFormat.makeTag(3, 2);

    private WireFormat() {
    }

    public static int getTagWireType(int tag) {
        return tag & 7;
    }

    public static int getTagFieldNumber(int tag) {
        return tag >>> 3;
    }

    static int makeTag(int fieldNumber, int wireType) {
        return fieldNumber << 3 | wireType;
    }

    static Object readPrimitiveField(CodedInputStream input, FieldType type, Utf8Validation utf8Validation) throws IOException {
        switch (type) {
            case DOUBLE: {
                return input.readDouble();
            }
            case FLOAT: {
                return Float.valueOf(input.readFloat());
            }
            case INT64: {
                return input.readInt64();
            }
            case UINT64: {
                return input.readUInt64();
            }
            case INT32: {
                return input.readInt32();
            }
            case FIXED64: {
                return input.readFixed64();
            }
            case FIXED32: {
                return input.readFixed32();
            }
            case BOOL: {
                return input.readBool();
            }
            case BYTES: {
                return input.readBytes();
            }
            case UINT32: {
                return input.readUInt32();
            }
            case SFIXED32: {
                return input.readSFixed32();
            }
            case SFIXED64: {
                return input.readSFixed64();
            }
            case SINT32: {
                return input.readSInt32();
            }
            case SINT64: {
                return input.readSInt64();
            }
            case STRING: {
                return utf8Validation.readString(input);
            }
            case GROUP: {
                throw new IllegalArgumentException("readPrimitiveField() cannot handle nested groups.");
            }
            case MESSAGE: {
                throw new IllegalArgumentException("readPrimitiveField() cannot handle embedded messages.");
            }
            case ENUM: {
                throw new IllegalArgumentException("readPrimitiveField() cannot handle enums.");
            }
        }
        throw new RuntimeException("There is no way to get here, but the compiler thinks otherwise.");
    }

    static enum Utf8Validation {
        LOOSE{

            @Override
            Object readString(CodedInputStream input) throws IOException {
                return input.readString();
            }
        }
        ,
        STRICT{

            @Override
            Object readString(CodedInputStream input) throws IOException {
                return input.readStringRequireUtf8();
            }
        }
        ,
        LAZY{

            @Override
            Object readString(CodedInputStream input) throws IOException {
                return input.readBytes();
            }
        };


        abstract Object readString(CodedInputStream var1) throws IOException;
    }

    public static enum FieldType {
        DOUBLE(JavaType.DOUBLE, 1),
        FLOAT(JavaType.FLOAT, 5),
        INT64(JavaType.LONG, 0),
        UINT64(JavaType.LONG, 0),
        INT32(JavaType.INT, 0),
        FIXED64(JavaType.LONG, 1),
        FIXED32(JavaType.INT, 5),
        BOOL(JavaType.BOOLEAN, 0),
        STRING(JavaType.STRING, 2){

            @Override
            public boolean isPackable() {
                return false;
            }
        }
        ,
        GROUP(JavaType.MESSAGE, 3){

            @Override
            public boolean isPackable() {
                return false;
            }
        }
        ,
        MESSAGE(JavaType.MESSAGE, 2){

            @Override
            public boolean isPackable() {
                return false;
            }
        }
        ,
        BYTES(JavaType.BYTE_STRING, 2){

            @Override
            public boolean isPackable() {
                return false;
            }
        }
        ,
        UINT32(JavaType.INT, 0),
        ENUM(JavaType.ENUM, 0),
        SFIXED32(JavaType.INT, 5),
        SFIXED64(JavaType.LONG, 1),
        SINT32(JavaType.INT, 0),
        SINT64(JavaType.LONG, 0);

        private final JavaType javaType;
        private final int wireType;

        private FieldType(JavaType javaType, int wireType) {
            this.javaType = javaType;
            this.wireType = wireType;
        }

        public JavaType getJavaType() {
            return this.javaType;
        }

        public int getWireType() {
            return this.wireType;
        }

        public boolean isPackable() {
            return true;
        }
    }

    public static enum JavaType {
        INT(0),
        LONG(0L),
        FLOAT(Float.valueOf(0.0f)),
        DOUBLE(0.0),
        BOOLEAN(false),
        STRING(""),
        BYTE_STRING(ByteString.EMPTY),
        ENUM(null),
        MESSAGE(null);

        private final Object defaultDefault;

        private JavaType(Object defaultDefault) {
            this.defaultDefault = defaultDefault;
        }

        Object getDefaultDefault() {
            return this.defaultDefault;
        }
    }
}

