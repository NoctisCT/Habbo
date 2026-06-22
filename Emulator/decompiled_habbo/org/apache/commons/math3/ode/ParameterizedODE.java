/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.math3.ode;

import org.apache.commons.math3.ode.Parameterizable;
import org.apache.commons.math3.ode.UnknownParameterException;

public interface ParameterizedODE
extends Parameterizable {
    public double getParameter(String var1) throws UnknownParameterException;

    public void setParameter(String var1, double var2) throws UnknownParameterException;
}

