/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.pattern;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.pattern.CompositeConverter;
import ch.qos.logback.core.pattern.Converter;
import ch.qos.logback.core.pattern.DynamicConverter;
import ch.qos.logback.core.spi.ContextAware;

public class ConverterUtil {
    public static <E> void startConverters(Converter<E> head) {
        for (Converter<E> c = head; c != null; c = c.getNext()) {
            if (c instanceof CompositeConverter) {
                CompositeConverter cc = (CompositeConverter)c;
                Converter childConverter = cc.childConverter;
                ConverterUtil.startConverters(childConverter);
                cc.start();
                continue;
            }
            if (!(c instanceof DynamicConverter)) continue;
            DynamicConverter dc = (DynamicConverter)c;
            dc.start();
        }
    }

    public static <E> Converter<E> findTail(Converter<E> head) {
        Converter<E> next;
        Converter<E> p = head;
        while (p != null && (next = p.getNext()) != null) {
            p = next;
        }
        return p;
    }

    public static <E> void setContextForConverters(Context context, Converter<E> head) {
        for (Converter<E> c = head; c != null; c = c.getNext()) {
            if (!(c instanceof ContextAware)) continue;
            ((ContextAware)((Object)c)).setContext(context);
        }
    }
}

