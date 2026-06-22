/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.pattern.color;

import ch.qos.logback.core.pattern.color.ForegroundCompositeConverterBase;

public class BoldMagentaCompositeConverter<E>
extends ForegroundCompositeConverterBase<E> {
    @Override
    protected String getForegroundColorCode(E event) {
        return "1;35";
    }
}

