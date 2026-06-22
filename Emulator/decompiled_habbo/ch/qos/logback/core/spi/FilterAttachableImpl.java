/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.spi;

import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterAttachable;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.util.COWArrayList;
import java.util.ArrayList;
import java.util.List;

public final class FilterAttachableImpl<E>
implements FilterAttachable<E> {
    COWArrayList<Filter<E>> filterList = new COWArrayList<Filter>(new Filter[0]);

    @Override
    public void addFilter(Filter<E> newFilter) {
        this.filterList.add(newFilter);
    }

    @Override
    public void clearAllFilters() {
        this.filterList.clear();
    }

    @Override
    public FilterReply getFilterChainDecision(E event) {
        Filter<E>[] filterArrray = this.filterList.asTypedArray();
        int len = filterArrray.length;
        for (int i = 0; i < len; ++i) {
            FilterReply r = filterArrray[i].decide(event);
            if (r != FilterReply.DENY && r != FilterReply.ACCEPT) continue;
            return r;
        }
        return FilterReply.NEUTRAL;
    }

    @Override
    public List<Filter<E>> getCopyOfAttachedFiltersList() {
        return new ArrayList<Filter<E>>(this.filterList);
    }
}

