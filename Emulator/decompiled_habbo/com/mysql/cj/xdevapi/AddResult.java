/*
 * Decompiled with CFR 0.152.
 */
package com.mysql.cj.xdevapi;

import com.mysql.cj.xdevapi.Result;
import java.util.List;

public interface AddResult
extends Result {
    public List<String> getGeneratedIds();
}

