/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.math3.ode;

public interface SecondOrderDifferentialEquations {
    public int getDimension();

    public void computeSecondDerivatives(double var1, double[] var3, double[] var4, double[] var5);
}

