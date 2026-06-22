/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.rolling;

import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.rolling.RollingPolicy;
import ch.qos.logback.core.rolling.RollingPolicyBase;
import ch.qos.logback.core.rolling.RolloverFailure;
import ch.qos.logback.core.rolling.TriggeringPolicy;
import ch.qos.logback.core.rolling.helper.CompressionMode;
import ch.qos.logback.core.rolling.helper.FileNamePattern;
import ch.qos.logback.core.util.ContextUtil;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class RollingFileAppender<E>
extends FileAppender<E> {
    File currentlyActiveFile;
    TriggeringPolicy<E> triggeringPolicy;
    RollingPolicy rollingPolicy;
    private static String RFA_NO_TP_URL = "http://logback.qos.ch/codes.html#rfa_no_tp";
    private static String RFA_NO_RP_URL = "http://logback.qos.ch/codes.html#rfa_no_rp";
    private static String COLLISION_URL = "http://logback.qos.ch/codes.html#rfa_collision";
    private static String RFA_LATE_FILE_URL = "http://logback.qos.ch/codes.html#rfa_file_after";

    @Override
    public void start() {
        if (this.triggeringPolicy == null) {
            this.addWarn("No TriggeringPolicy was set for the RollingFileAppender named " + this.getName());
            this.addWarn("For more information, please visit " + RFA_NO_TP_URL);
            return;
        }
        if (!this.triggeringPolicy.isStarted()) {
            this.addWarn("TriggeringPolicy has not started. RollingFileAppender will not start");
            return;
        }
        if (this.checkForCollisionsInPreviousRollingFileAppenders()) {
            this.addError("Collisions detected with FileAppender/RollingAppender instances defined earlier. Aborting.");
            this.addError("For more information, please visit " + COLLISION_WITH_EARLIER_APPENDER_URL);
            return;
        }
        if (!this.append) {
            this.addWarn("Append mode is mandatory for RollingFileAppender. Defaulting to append=true.");
            this.append = true;
        }
        if (this.rollingPolicy == null) {
            this.addError("No RollingPolicy was set for the RollingFileAppender named " + this.getName());
            this.addError("For more information, please visit " + RFA_NO_RP_URL);
            return;
        }
        if (this.checkForFileAndPatternCollisions()) {
            this.addError("File property collides with fileNamePattern. Aborting.");
            this.addError("For more information, please visit " + COLLISION_URL);
            return;
        }
        if (this.isPrudent()) {
            if (this.rawFileProperty() != null) {
                this.addWarn("Setting \"File\" property to null on account of prudent mode");
                this.setFile(null);
            }
            if (this.rollingPolicy.getCompressionMode() != CompressionMode.NONE) {
                this.addError("Compression is not supported in prudent mode. Aborting");
                return;
            }
        }
        this.currentlyActiveFile = new File(this.getFile());
        this.addInfo("Active log file name: " + this.getFile());
        super.start();
    }

    private boolean checkForFileAndPatternCollisions() {
        if (this.triggeringPolicy instanceof RollingPolicyBase) {
            RollingPolicyBase base = (RollingPolicyBase)((Object)this.triggeringPolicy);
            FileNamePattern fileNamePattern = base.fileNamePattern;
            if (fileNamePattern != null && this.fileName != null) {
                String regex = fileNamePattern.toRegex();
                return this.fileName.matches(regex);
            }
        }
        return false;
    }

    private boolean checkForCollisionsInPreviousRollingFileAppenders() {
        boolean collisionResult = false;
        if (this.triggeringPolicy instanceof RollingPolicyBase) {
            RollingPolicyBase base = (RollingPolicyBase)((Object)this.triggeringPolicy);
            FileNamePattern fileNamePattern = base.fileNamePattern;
            boolean collisionsDetected = this.innerCheckForFileNamePatternCollisionInPreviousRFA(fileNamePattern);
            if (collisionsDetected) {
                collisionResult = true;
            }
        }
        return collisionResult;
    }

    private boolean innerCheckForFileNamePatternCollisionInPreviousRFA(FileNamePattern fileNamePattern) {
        boolean collisionsDetected = false;
        Map map = (Map)this.context.getObject("RFA_FILENAME_PATTERN_COLLISION_MAP");
        if (map == null) {
            return collisionsDetected;
        }
        for (Map.Entry entry : map.entrySet()) {
            if (!fileNamePattern.equals(entry.getValue())) continue;
            this.addErrorForCollision("FileNamePattern", ((FileNamePattern)entry.getValue()).toString(), (String)entry.getKey());
            collisionsDetected = true;
        }
        if (this.name != null) {
            map.put(this.getName(), fileNamePattern);
        }
        return collisionsDetected;
    }

    @Override
    public void stop() {
        Map<String, FileNamePattern> map;
        super.stop();
        if (this.rollingPolicy != null) {
            this.rollingPolicy.stop();
        }
        if (this.triggeringPolicy != null) {
            this.triggeringPolicy.stop();
        }
        if ((map = ContextUtil.getFilenamePatternCollisionMap(this.context)) != null && this.getName() != null) {
            map.remove(this.getName());
        }
    }

    @Override
    public void setFile(String file) {
        if (file != null && (this.triggeringPolicy != null || this.rollingPolicy != null)) {
            this.addError("File property must be set before any triggeringPolicy or rollingPolicy properties");
            this.addError("For more information, please visit " + RFA_LATE_FILE_URL);
        }
        super.setFile(file);
    }

    @Override
    public String getFile() {
        return this.rollingPolicy.getActiveFileName();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void rollover() {
        this.lock.lock();
        try {
            this.closeOutputStream();
            this.attemptRollover();
            this.attemptOpenFile();
        }
        finally {
            this.lock.unlock();
        }
    }

    private void attemptOpenFile() {
        try {
            this.currentlyActiveFile = new File(this.rollingPolicy.getActiveFileName());
            this.openFile(this.rollingPolicy.getActiveFileName());
        }
        catch (IOException e) {
            this.addError("setFile(" + this.fileName + ", false) call failed.", e);
        }
    }

    private void attemptRollover() {
        try {
            this.rollingPolicy.rollover();
        }
        catch (RolloverFailure rf) {
            this.addWarn("RolloverFailure occurred. Deferring roll-over.");
            this.append = true;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void subAppend(E event) {
        TriggeringPolicy<E> triggeringPolicy = this.triggeringPolicy;
        synchronized (triggeringPolicy) {
            if (this.triggeringPolicy.isTriggeringEvent(this.currentlyActiveFile, event)) {
                this.rollover();
            }
        }
        super.subAppend(event);
    }

    public RollingPolicy getRollingPolicy() {
        return this.rollingPolicy;
    }

    public TriggeringPolicy<E> getTriggeringPolicy() {
        return this.triggeringPolicy;
    }

    public void setRollingPolicy(RollingPolicy policy) {
        this.rollingPolicy = policy;
        if (this.rollingPolicy instanceof TriggeringPolicy) {
            this.triggeringPolicy = (TriggeringPolicy)((Object)policy);
        }
    }

    public void setTriggeringPolicy(TriggeringPolicy<E> policy) {
        this.triggeringPolicy = policy;
        if (policy instanceof RollingPolicy) {
            this.rollingPolicy = (RollingPolicy)((Object)policy);
        }
    }
}

