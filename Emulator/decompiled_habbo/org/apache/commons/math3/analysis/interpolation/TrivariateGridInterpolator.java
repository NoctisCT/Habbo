/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.math3.analysis.interpolation;

import org.apache.commons.math3.analysis.TrivariateFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;

public interface TrivariateGridInterpolator {
    public TrivariateFunction interpolate(double[] var1, double[] var2, double[] var3, double[][][] var4) throws NoDataException, NumberIsTooSmallException, DimensionMismatchException, NonMonotonicSequenceException;
}

