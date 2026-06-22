/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.math3.analysis;

import org.apache.commons.math3.analysis.MultivariateMatrixFunction;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;

@Deprecated
public interface DifferentiableMultivariateVectorFunction
extends MultivariateVectorFunction {
    public MultivariateMatrixFunction jacobian();
}

