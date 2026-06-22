/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.math3.optimization;

import org.apache.commons.math3.optimization.OptimizationData;

@Deprecated
public class Target
implements OptimizationData {
    private final double[] target;

    public Target(double[] observations) {
        this.target = (double[])observations.clone();
    }

    public double[] getTarget() {
        return (double[])this.target.clone();
    }
}

