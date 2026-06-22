/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.math3.util;

import org.apache.commons.math3.exception.MathIllegalArgumentException;

public interface PivotingStrategyInterface {
    public int pivotIndex(double[] var1, int var2, int var3) throws MathIllegalArgumentException;
}

