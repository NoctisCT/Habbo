/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.math3.genetics;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.ChromosomePair;

public interface CrossoverPolicy {
    public ChromosomePair crossover(Chromosome var1, Chromosome var2) throws MathIllegalArgumentException;
}

