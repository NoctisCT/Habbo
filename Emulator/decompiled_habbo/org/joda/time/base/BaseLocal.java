/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.base;

import org.joda.time.base.AbstractPartial;

public abstract class BaseLocal
extends AbstractPartial {
    private static final long serialVersionUID = 276453175381783L;

    protected BaseLocal() {
    }

    protected abstract long getLocalMillis();
}

