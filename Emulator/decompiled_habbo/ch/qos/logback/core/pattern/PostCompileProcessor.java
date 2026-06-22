/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.pattern;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.pattern.Converter;

public interface PostCompileProcessor<E> {
    public void process(Context var1, Converter<E> var2);
}

