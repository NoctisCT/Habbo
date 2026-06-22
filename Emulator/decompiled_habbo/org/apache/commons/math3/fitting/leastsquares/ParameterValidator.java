/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.math3.fitting.leastsquares;

import org.apache.commons.math3.linear.RealVector;

public interface ParameterValidator {
    public RealVector validate(RealVector var1);
}

