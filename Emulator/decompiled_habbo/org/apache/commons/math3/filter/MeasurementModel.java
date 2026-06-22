/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.math3.filter;

import org.apache.commons.math3.linear.RealMatrix;

public interface MeasurementModel {
    public RealMatrix getMeasurementMatrix();

    public RealMatrix getMeasurementNoise();
}

