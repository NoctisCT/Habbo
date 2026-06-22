/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.spi;

import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import java.util.List;

public interface FilterAttachable<E> {
    public void addFilter(Filter<E> var1);

    public void clearAllFilters();

    public List<Filter<E>> getCopyOfAttachedFiltersList();

    public FilterReply getFilterChainDecision(E var1);
}

