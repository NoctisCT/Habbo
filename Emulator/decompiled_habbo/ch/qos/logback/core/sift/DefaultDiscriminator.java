/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.sift;

import ch.qos.logback.core.sift.AbstractDiscriminator;

public class DefaultDiscriminator<E>
extends AbstractDiscriminator<E> {
    public static final String DEFAULT = "default";

    @Override
    public String getDiscriminatingValue(E e) {
        return DEFAULT;
    }

    @Override
    public String getKey() {
        return DEFAULT;
    }
}

