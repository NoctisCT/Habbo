/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.math3.analysis.solvers;

import org.apache.commons.math3.analysis.solvers.BaseSecantSolver;

public class IllinoisSolver
extends BaseSecantSolver {
    public IllinoisSolver() {
        super(1.0E-6, BaseSecantSolver.Method.ILLINOIS);
    }

    public IllinoisSolver(double absoluteAccuracy) {
        super(absoluteAccuracy, BaseSecantSolver.Method.ILLINOIS);
    }

    public IllinoisSolver(double relativeAccuracy, double absoluteAccuracy) {
        super(relativeAccuracy, absoluteAccuracy, BaseSecantSolver.Method.ILLINOIS);
    }

    public IllinoisSolver(double relativeAccuracy, double absoluteAccuracy, double functionValueAccuracy) {
        super(relativeAccuracy, absoluteAccuracy, functionValueAccuracy, BaseSecantSolver.Method.PEGASUS);
    }
}

