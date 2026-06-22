/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageV3;

public final class SourceContextProto {
    static final Descriptors.Descriptor internal_static_google_protobuf_SourceContext_descriptor;
    static final GeneratedMessageV3.FieldAccessorTable internal_static_google_protobuf_SourceContext_fieldAccessorTable;
    private static Descriptors.FileDescriptor descriptor;

    private SourceContextProto() {
    }

    public static void registerAllExtensions(ExtensionRegistryLite registry) {
    }

    public static void registerAllExtensions(ExtensionRegistry registry) {
        SourceContextProto.registerAllExtensions((ExtensionRegistryLite)registry);
    }

    public static Descriptors.FileDescriptor getDescriptor() {
        return descriptor;
    }

    static {
        String[] descriptorData = new String[]{"\n$google/protobuf/source_context.proto\u0012\u000fgoogle.protobuf\"\"\n\rSourceContext\u0012\u0011\n\tfile_name\u0018\u0001 \u0001(\tB\u0095\u0001\n\u0013com.google.protobufB\u0012SourceContextProtoP\u0001ZAgoogle.golang.org/genproto/protobuf/source_context;source_context\u00a2\u0002\u0003GPB\u00aa\u0002\u001eGoogle.Protobuf.WellKnownTypesb\u0006proto3"};
        descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0]);
        internal_static_google_protobuf_SourceContext_descriptor = SourceContextProto.getDescriptor().getMessageTypes().get(0);
        internal_static_google_protobuf_SourceContext_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_google_protobuf_SourceContext_descriptor, new String[]{"FileName"});
    }
}

