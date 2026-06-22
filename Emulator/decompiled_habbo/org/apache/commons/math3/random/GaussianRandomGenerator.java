/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.math3.random;

import org.apache.commons.math3.random.NormalizedRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;

public class GaussianRandomGenerator
implements NormalizedRandomGenerator {
    private final RandomGenerator generator;

    public GaussianRandomGenerator(RandomGenerator generator) {
        this.generator = generator;
    }

    public double nextNormalizedDouble() {
        return this.generator.nextGaussian();
    }
}

