/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.math3.optim.nonlinear.scalar.gradient;

public interface Preconditioner {
    public double[] precondition(double[] var1, double[] var2);
}

