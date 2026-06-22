/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.math3.geometry;

import java.io.Serializable;
import org.apache.commons.math3.exception.MathUnsupportedOperationException;

public interface Space
extends Serializable {
    public int getDimension();

    public Space getSubSpace() throws MathUnsupportedOperationException;
}

