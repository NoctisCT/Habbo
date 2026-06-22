/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.pattern.ClassicConverter;

public abstract class ThrowableHandlingConverter
extends ClassicConverter {
    boolean handlesThrowable() {
        return true;
    }
}

