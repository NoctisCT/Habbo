/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.pattern;

import ch.qos.logback.core.pattern.Converter;

public final class LiteralConverter<E>
extends Converter<E> {
    String literal;

    public LiteralConverter(String literal) {
        this.literal = literal;
    }

    @Override
    public String convert(E o) {
        return this.literal;
    }
}

