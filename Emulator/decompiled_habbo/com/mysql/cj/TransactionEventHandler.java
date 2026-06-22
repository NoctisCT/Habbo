/*
 * Decompiled with CFR 0.152.
 */
package com.mysql.cj;

public interface TransactionEventHandler {
    public void transactionBegun();

    public void transactionCompleted();
}

