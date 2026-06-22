/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.threading;

import java.io.IOException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HabboExecutorService
extends ScheduledThreadPoolExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(HabboExecutorService.class);

    public HabboExecutorService(int corePoolSize, ThreadFactory threadFactory) {
        super(corePoolSize, threadFactory);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (t != null && !(t instanceof IOException)) {
            LOGGER.error("Error in HabboExecutorService", t);
        }
    }
}

