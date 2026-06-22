/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.math3.fitting.leastsquares;

import org.apache.commons.math3.fitting.leastsquares.MultivariateJacobianFunction;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public interface ValueAndJacobianFunction
extends MultivariateJacobianFunction {
    public RealVector computeValue(double[] var1);

    public RealMatrix computeJacobian(double[] var1);
}

