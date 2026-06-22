/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.math3.analysis.interpolation;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NullArgumentException;

public interface MultivariateInterpolator {
    public MultivariateFunction interpolate(double[][] var1, double[] var2) throws MathIllegalArgumentException, DimensionMismatchException, NoDataException, NullArgumentException;
}

