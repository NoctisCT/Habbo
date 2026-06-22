/*
 * Decompiled with CFR 0.152.
 */
package com.mysql.cj.xdevapi;

import com.mysql.cj.QueryResult;
import com.mysql.cj.xdevapi.Warning;
import java.util.Iterator;

public interface Result
extends QueryResult {
    public long getAffectedItemsCount();

    public int getWarningsCount();

    public Iterator<Warning> getWarnings();
}

