/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.math3.genetics;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.genetics.Chromosome;

public interface MutationPolicy {
    public Chromosome mutate(Chromosome var1) throws MathIllegalArgumentException;
}

