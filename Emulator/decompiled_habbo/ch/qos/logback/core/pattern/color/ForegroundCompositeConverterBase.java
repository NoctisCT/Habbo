/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.pattern.color;

import ch.qos.logback.core.pattern.CompositeConverter;

public abstract class ForegroundCompositeConverterBase<E>
extends CompositeConverter<E> {
    private static final String SET_DEFAULT_COLOR = "\u001b[0;39m";

    @Override
    protected String transform(E event, String in) {
        StringBuilder sb = new StringBuilder();
        sb.append("\u001b[");
        sb.append(this.getForegroundColorCode(event));
        sb.append("m");
        sb.append(in);
        sb.append(SET_DEFAULT_COLOR);
        return sb.toString();
    }

    protected abstract String getForegroundColorCode(E var1);
}

