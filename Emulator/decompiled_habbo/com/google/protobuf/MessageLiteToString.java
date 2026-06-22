/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.MessageLite;
import com.google.protobuf.TextFormatEscaper;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

final class MessageLiteToString {
    private static final String LIST_SUFFIX = "List";
    private static final String BUILDER_LIST_SUFFIX = "OrBuilderList";
    private static final String MAP_SUFFIX = "Map";
    private static final String BYTES_SUFFIX = "Bytes";

    MessageLiteToString() {
    }

    static String toString(MessageLite messageLite, String commentString) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("# ").append(commentString);
        MessageLiteToString.reflectivePrintWithIndent(messageLite, buffer, 0);
        return buffer.toString();
    }

    private static void reflectivePrintWithIndent(MessageLite messageLite, StringBuilder buffer, int indent) {
        HashMap<String, Method> nameToNoArgMethod = new HashMap<String, Method>();
        HashMap<String, Method> nameToMethod = new HashMap<String, Method>();
        TreeSet<String> getters = new TreeSet<String>();
        for (Method method : messageLite.getClass().getDeclaredMethods()) {
            nameToMethod.put(method.getName(), method);
            if (method.getParameterTypes().length != 0) continue;
            nameToNoArgMethod.put(method.getName(), method);
            if (!method.getName().startsWith("get")) continue;
            getters.add(method.getName());
        }
        for (String getter : getters) {
            Method setter;
            String camelCase;
            String suffix;
            String string = suffix = getter.startsWith("get") ? getter.substring(3) : getter;
            if (suffix.endsWith(LIST_SUFFIX) && !suffix.endsWith(BUILDER_LIST_SUFFIX) && !suffix.equals(LIST_SUFFIX)) {
                camelCase = suffix.substring(0, 1).toLowerCase() + suffix.substring(1, suffix.length() - LIST_SUFFIX.length());
                Method listMethod = (Method)nameToNoArgMethod.get(getter);
                if (listMethod != null && listMethod.getReturnType().equals(List.class)) {
                    MessageLiteToString.printField(buffer, indent, MessageLiteToString.camelCaseToSnakeCase(camelCase), GeneratedMessageLite.invokeOrDie(listMethod, messageLite, new Object[0]));
                    continue;
                }
            }
            if (suffix.endsWith(MAP_SUFFIX) && !suffix.equals(MAP_SUFFIX)) {
                camelCase = suffix.substring(0, 1).toLowerCase() + suffix.substring(1, suffix.length() - MAP_SUFFIX.length());
                Method mapMethod = (Method)nameToNoArgMethod.get(getter);
                if (mapMethod != null && mapMethod.getReturnType().equals(Map.class) && !mapMethod.isAnnotationPresent(Deprecated.class) && Modifier.isPublic(mapMethod.getModifiers())) {
                    MessageLiteToString.printField(buffer, indent, MessageLiteToString.camelCaseToSnakeCase(camelCase), GeneratedMessageLite.invokeOrDie(mapMethod, messageLite, new Object[0]));
                    continue;
                }
            }
            if ((setter = (Method)nameToMethod.get("set" + suffix)) == null || suffix.endsWith(BYTES_SUFFIX) && nameToNoArgMethod.containsKey("get" + suffix.substring(0, suffix.length() - BYTES_SUFFIX.length()))) continue;
            String camelCase2 = suffix.substring(0, 1).toLowerCase() + suffix.substring(1);
            Method getMethod = (Method)nameToNoArgMethod.get("get" + suffix);
            Method hasMethod = (Method)nameToNoArgMethod.get("has" + suffix);
            if (getMethod == null) continue;
            Object value = GeneratedMessageLite.invokeOrDie(getMethod, messageLite, new Object[0]);
            boolean bl = hasMethod == null ? !MessageLiteToString.isDefaultValue(value) : (Boolean)GeneratedMessageLite.invokeOrDie(hasMethod, messageLite, new Object[0]);
            boolean hasValue = bl;
            if (!hasValue) continue;
            MessageLiteToString.printField(buffer, indent, MessageLiteToString.camelCaseToSnakeCase(camelCase2), value);
        }
        if (messageLite instanceof GeneratedMessageLite.ExtendableMessage) {
            Iterator<Map.Entry<GeneratedMessageLite.ExtensionDescriptor, Object>> iter = ((GeneratedMessageLite.ExtendableMessage)messageLite).extensions.iterator();
            while (iter.hasNext()) {
                Map.Entry<GeneratedMessageLite.ExtensionDescriptor, Object> entry = iter.next();
                MessageLiteToString.printField(buffer, indent, "[" + entry.getKey().getNumber() + "]", entry.getValue());
            }
        }
        if (((GeneratedMessageLite)messageLite).unknownFields != null) {
            ((GeneratedMessageLite)messageLite).unknownFields.printWithIndent(buffer, indent);
        }
    }

    private static boolean isDefaultValue(Object o) {
        if (o instanceof Boolean) {
            return (Boolean)o == false;
        }
        if (o instanceof Integer) {
            return (Integer)o == 0;
        }
        if (o instanceof Float) {
            return ((Float)o).floatValue() == 0.0f;
        }
        if (o instanceof Double) {
            return (Double)o == 0.0;
        }
        if (o instanceof String) {
            return o.equals("");
        }
        if (o instanceof ByteString) {
            return o.equals(ByteString.EMPTY);
        }
        if (o instanceof MessageLite) {
            return o == ((MessageLite)o).getDefaultInstanceForType();
        }
        if (o instanceof Enum) {
            return ((Enum)o).ordinal() == 0;
        }
        return false;
    }

    static final void printField(StringBuilder buffer, int indent, String name, Object object) {
        int i;
        if (object instanceof List) {
            List list = (List)object;
            for (Object entry : list) {
                MessageLiteToString.printField(buffer, indent, name, entry);
            }
            return;
        }
        if (object instanceof Map) {
            Map map = (Map)object;
            for (Map.Entry entry : map.entrySet()) {
                MessageLiteToString.printField(buffer, indent, name, entry);
            }
            return;
        }
        buffer.append('\n');
        for (i = 0; i < indent; ++i) {
            buffer.append(' ');
        }
        buffer.append(name);
        if (object instanceof String) {
            buffer.append(": \"").append(TextFormatEscaper.escapeText((String)object)).append('\"');
        } else if (object instanceof ByteString) {
            buffer.append(": \"").append(TextFormatEscaper.escapeBytes((ByteString)object)).append('\"');
        } else if (object instanceof GeneratedMessageLite) {
            buffer.append(" {");
            MessageLiteToString.reflectivePrintWithIndent((GeneratedMessageLite)object, buffer, indent + 2);
            buffer.append("\n");
            for (i = 0; i < indent; ++i) {
                buffer.append(' ');
            }
            buffer.append("}");
        } else if (object instanceof Map.Entry) {
            buffer.append(" {");
            Map.Entry entry = (Map.Entry)object;
            MessageLiteToString.printField(buffer, indent + 2, "key", entry.getKey());
            MessageLiteToString.printField(buffer, indent + 2, "value", entry.getValue());
            buffer.append("\n");
            for (int i2 = 0; i2 < indent; ++i2) {
                buffer.append(' ');
            }
            buffer.append("}");
        } else {
            buffer.append(": ").append(object.toString());
        }
    }

    private static final String camelCaseToSnakeCase(String camelCase) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < camelCase.length(); ++i) {
            char ch = camelCase.charAt(i);
            if (Character.isUpperCase(ch)) {
                builder.append("_");
            }
            builder.append(Character.toLowerCase(ch));
        }
        return builder.toString();
    }
}

