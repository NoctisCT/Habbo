/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core;

import ch.qos.logback.core.OutputStreamAppender;
import ch.qos.logback.core.joran.spi.ConsoleTarget;
import ch.qos.logback.core.status.WarnStatus;
import ch.qos.logback.core.util.EnvUtil;
import ch.qos.logback.core.util.OptionHelper;
import java.io.OutputStream;
import java.util.Arrays;

public class ConsoleAppender<E>
extends OutputStreamAppender<E> {
    protected ConsoleTarget target = ConsoleTarget.SystemOut;
    protected boolean withJansi = false;
    private static final String WindowsAnsiOutputStream_CLASS_NAME = "org.fusesource.jansi.WindowsAnsiOutputStream";

    public void setTarget(String value) {
        ConsoleTarget t = ConsoleTarget.findByName(value.trim());
        if (t == null) {
            this.targetWarn(value);
        } else {
            this.target = t;
        }
    }

    public String getTarget() {
        return this.target.getName();
    }

    private void targetWarn(String val) {
        WarnStatus status = new WarnStatus("[" + val + "] should be one of " + Arrays.toString((Object[])ConsoleTarget.values()), this);
        status.add(new WarnStatus("Using previously set target, System.out by default.", this));
        this.addStatus(status);
    }

    @Override
    public void start() {
        OutputStream targetStream = this.target.getStream();
        if (EnvUtil.isWindows() && this.withJansi) {
            targetStream = this.getTargetStreamForWindows(targetStream);
        }
        this.setOutputStream(targetStream);
        super.start();
    }

    private OutputStream getTargetStreamForWindows(OutputStream targetStream) {
        try {
            this.addInfo("Enabling JANSI WindowsAnsiOutputStream for the console.");
            Object windowsAnsiOutputStream = OptionHelper.instantiateByClassNameAndParameter(WindowsAnsiOutputStream_CLASS_NAME, Object.class, this.context, OutputStream.class, (Object)targetStream);
            return (OutputStream)windowsAnsiOutputStream;
        }
        catch (Exception e) {
            this.addWarn("Failed to create WindowsAnsiOutputStream. Falling back on the default stream.", e);
            return targetStream;
        }
    }

    public boolean isWithJansi() {
        return this.withJansi;
    }

    public void setWithJansi(boolean withJansi) {
        this.withJansi = withJansi;
    }
}

