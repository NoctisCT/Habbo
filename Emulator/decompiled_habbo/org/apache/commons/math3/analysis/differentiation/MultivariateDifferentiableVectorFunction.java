/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.math3.analysis.differentiation;

import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.exception.MathIllegalArgumentException;

public interface MultivariateDifferentiableVectorFunction
extends MultivariateVectorFunction {
    public DerivativeStructure[] value(DerivativeStructure[] var1) throws MathIllegalArgumentException;
}

