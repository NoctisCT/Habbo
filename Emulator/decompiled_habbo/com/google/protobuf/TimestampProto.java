/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageV3;

public final class TimestampProto {
    static final Descriptors.Descriptor internal_static_google_protobuf_Timestamp_descriptor;
    static final GeneratedMessageV3.FieldAccessorTable internal_static_google_protobuf_Timestamp_fieldAccessorTable;
    private static Descriptors.FileDescriptor descriptor;

    private TimestampProto() {
    }

    public static void registerAllExtensions(ExtensionRegistryLite registry) {
    }

    public static void registerAllExtensions(ExtensionRegistry registry) {
        TimestampProto.registerAllExtensions((ExtensionRegistryLite)registry);
    }

    public static Descriptors.FileDescriptor getDescriptor() {
        return descriptor;
    }

    static {
        String[] descriptorData = new String[]{"\n\u001fgoogle/protobuf/timestamp.proto\u0012\u000fgoogle.protobuf\"+\n\tTimestamp\u0012\u000f\n\u0007seconds\u0018\u0001 \u0001(\u0003\u0012\r\n\u0005nanos\u0018\u0002 \u0001(\u0005B~\n\u0013com.google.protobufB\u000eTimestampProtoP\u0001Z+github.com/golang/protobuf/ptypes/timestamp\u00f8\u0001\u0001\u00a2\u0002\u0003GPB\u00aa\u0002\u001eGoogle.Protobuf.WellKnownTypesb\u0006proto3"};
        descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0]);
        internal_static_google_protobuf_Timestamp_descriptor = TimestampProto.getDescriptor().getMessageTypes().get(0);
        internal_static_google_protobuf_Timestamp_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_google_protobuf_Timestamp_descriptor, new String[]{"Seconds", "Nanos"});
    }
}

