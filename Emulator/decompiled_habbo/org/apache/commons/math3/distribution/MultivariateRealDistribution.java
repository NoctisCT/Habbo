/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;

public interface MultivariateRealDistribution {
    public double density(double[] var1);

    public void reseedRandomGenerator(long var1);

    public int getDimension();

    public double[] sample();

    public double[][] sample(int var1) throws NotStrictlyPositiveException;
}

