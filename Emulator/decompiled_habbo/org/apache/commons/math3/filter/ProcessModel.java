/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.math3.filter;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public interface ProcessModel {
    public RealMatrix getStateTransitionMatrix();

    public RealMatrix getControlMatrix();

    public RealMatrix getProcessNoise();

    public RealVector getInitialStateEstimate();

    public RealMatrix getInitialErrorCovariance();
}

