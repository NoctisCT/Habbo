/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.math3.fitting.leastsquares;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;

public interface LeastSquaresOptimizer {
    public Optimum optimize(LeastSquaresProblem var1);

    public static interface Optimum
    extends LeastSquaresProblem.Evaluation {
        public int getEvaluations();

        public int getIterations();
    }
}

