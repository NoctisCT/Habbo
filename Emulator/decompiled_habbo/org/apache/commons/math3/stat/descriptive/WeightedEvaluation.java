/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.math3.stat.descriptive;

import org.apache.commons.math3.exception.MathIllegalArgumentException;

public interface WeightedEvaluation {
    public double evaluate(double[] var1, double[] var2) throws MathIllegalArgumentException;

    public double evaluate(double[] var1, double[] var2, int var3, int var4) throws MathIllegalArgumentException;
}

