/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.layout;

import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.LayoutBase;

public class EchoLayout<E>
extends LayoutBase<E> {
    @Override
    public String doLayout(E event) {
        return event + CoreConstants.LINE_SEPARATOR;
    }
}

