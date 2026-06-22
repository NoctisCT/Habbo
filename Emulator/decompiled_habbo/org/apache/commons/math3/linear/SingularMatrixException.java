/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.math3.linear;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public class SingularMatrixException
extends MathIllegalArgumentException {
    private static final long serialVersionUID = -4206514844735401070L;

    public SingularMatrixException() {
        super(LocalizedFormats.SINGULAR_MATRIX, new Object[0]);
    }
}

