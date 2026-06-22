/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.pattern;

import ch.qos.logback.core.pattern.CompositeConverter;

public class IdentityCompositeConverter<E>
extends CompositeConverter<E> {
    @Override
    protected String transform(E event, String in) {
        return in;
    }
}

