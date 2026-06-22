/*
 * Decompiled with CFR 0.152.
 */
package org.jsoup.select;

import org.jsoup.nodes.Node;

public interface NodeFilter {
    public FilterResult head(Node var1, int var2);

    public FilterResult tail(Node var1, int var2);

    public static enum FilterResult {
        CONTINUE,
        SKIP_CHILDREN,
        SKIP_ENTIRELY,
        REMOVE,
        STOP;

    }
}

