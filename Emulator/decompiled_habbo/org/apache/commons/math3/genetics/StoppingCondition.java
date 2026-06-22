/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.math3.genetics;

import org.apache.commons.math3.genetics.Population;

public interface StoppingCondition {
    public boolean isSatisfied(Population var1);
}

