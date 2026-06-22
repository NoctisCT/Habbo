/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.math3.linear;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.linear.RealVector;

public abstract class RealLinearOperator {
    public abstract int getRowDimension();

    public abstract int getColumnDimension();

    public abstract RealVector operate(RealVector var1) throws DimensionMismatchException;

    public RealVector operateTranspose(RealVector x) throws DimensionMismatchException, UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    public boolean isTransposable() {
        return false;
    }
}

