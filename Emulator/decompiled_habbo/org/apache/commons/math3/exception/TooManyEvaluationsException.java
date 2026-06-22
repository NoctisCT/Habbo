/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.math3.exception;

import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public class TooManyEvaluationsException
extends MaxCountExceededException {
    private static final long serialVersionUID = 4330003017885151975L;

    public TooManyEvaluationsException(Number max) {
        super(max);
        this.getContext().addMessage(LocalizedFormats.EVALUATIONS, new Object[0]);
    }
}

