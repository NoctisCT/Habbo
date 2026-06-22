/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.math3.analysis.function;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.util.FastMath;

public class Ceil
implements UnivariateFunction {
    public double value(double x) {
        return FastMath.ceil(x);
    }
}

