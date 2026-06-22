/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.math3.analysis.function;

import org.apache.commons.math3.analysis.DifferentiableUnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math3.analysis.function.Cos;
import org.apache.commons.math3.util.FastMath;

public class Sin
implements UnivariateDifferentiableFunction,
DifferentiableUnivariateFunction {
    public double value(double x) {
        return FastMath.sin(x);
    }

    @Deprecated
    public DifferentiableUnivariateFunction derivative() {
        return new Cos();
    }

    public DerivativeStructure value(DerivativeStructure t) {
        return t.sin();
    }
}

