/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.math3.analysis;

import org.apache.commons.math3.analysis.UnivariateMatrixFunction;

@Deprecated
public interface DifferentiableUnivariateMatrixFunction
extends UnivariateMatrixFunction {
    public UnivariateMatrixFunction derivative();
}

