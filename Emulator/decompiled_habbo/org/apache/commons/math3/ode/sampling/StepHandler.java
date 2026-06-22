/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.math3.ode.sampling;

import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.sampling.StepInterpolator;

public interface StepHandler {
    public void init(double var1, double[] var3, double var4);

    public void handleStep(StepInterpolator var1, boolean var2) throws MaxCountExceededException;
}

