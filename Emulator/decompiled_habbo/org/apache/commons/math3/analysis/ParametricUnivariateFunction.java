/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.math3.analysis;

public interface ParametricUnivariateFunction {
    public double value(double var1, double ... var3);

    public double[] gradient(double var1, double ... var3);
}

