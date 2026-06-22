/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.hook;

import ch.qos.logback.core.hook.ShutdownHookBase;
import ch.qos.logback.core.util.Duration;

public class DelayingShutdownHook
extends ShutdownHookBase {
    public static final Duration DEFAULT_DELAY = Duration.buildByMilliseconds(0.0);
    private Duration delay = DEFAULT_DELAY;

    public Duration getDelay() {
        return this.delay;
    }

    public void setDelay(Duration delay) {
        this.delay = delay;
    }

    @Override
    public void run() {
        this.addInfo("Sleeping for " + this.delay);
        try {
            Thread.sleep(this.delay.getMilliseconds());
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
        super.stop();
    }
}

