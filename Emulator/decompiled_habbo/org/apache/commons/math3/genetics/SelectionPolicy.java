/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.math3.genetics;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.genetics.ChromosomePair;
import org.apache.commons.math3.genetics.Population;

public interface SelectionPolicy {
    public ChromosomePair select(Population var1) throws MathIllegalArgumentException;
}

