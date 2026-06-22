/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.helpers;

import ch.qos.logback.core.AppenderBase;

public final class NOPAppender<E>
extends AppenderBase<E> {
    @Override
    protected void append(E eventObject) {
    }
}

