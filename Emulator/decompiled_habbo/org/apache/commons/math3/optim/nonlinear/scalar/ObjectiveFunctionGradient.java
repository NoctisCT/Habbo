/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.math3.optim.nonlinear.scalar;

import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.optim.OptimizationData;

public class ObjectiveFunctionGradient
implements OptimizationData {
    private final MultivariateVectorFunction gradient;

    public ObjectiveFunctionGradient(MultivariateVectorFunction g) {
        this.gradient = g;
    }

    public MultivariateVectorFunction getObjectiveFunctionGradient() {
        return this.gradient;
    }
}

