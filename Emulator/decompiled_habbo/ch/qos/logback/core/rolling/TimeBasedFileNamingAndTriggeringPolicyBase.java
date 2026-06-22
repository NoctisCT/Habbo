/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.rolling;

import ch.qos.logback.core.rolling.TimeBasedFileNamingAndTriggeringPolicy;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.rolling.helper.ArchiveRemover;
import ch.qos.logback.core.rolling.helper.DateTokenConverter;
import ch.qos.logback.core.rolling.helper.RollingCalendar;
import ch.qos.logback.core.spi.ContextAwareBase;
import java.io.File;
import java.util.Date;
import java.util.Locale;

public abstract class TimeBasedFileNamingAndTriggeringPolicyBase<E>
extends ContextAwareBase
implements TimeBasedFileNamingAndTriggeringPolicy<E> {
    private static String COLLIDING_DATE_FORMAT_URL = "http://logback.qos.ch/codes.html#rfa_collision_in_dateFormat";
    protected TimeBasedRollingPolicy<E> tbrp;
    protected ArchiveRemover archiveRemover = null;
    protected String elapsedPeriodsFileName;
    protected RollingCalendar rc;
    protected long artificialCurrentTime = -1L;
    protected Date dateInCurrentPeriod = null;
    protected long nextCheck;
    protected boolean started = false;
    protected boolean errorFree = true;

    @Override
    public boolean isStarted() {
        return this.started;
    }

    @Override
    public void start() {
        File currentFile;
        DateTokenConverter<Object> dtc = this.tbrp.fileNamePattern.getPrimaryDateTokenConverter();
        if (dtc == null) {
            throw new IllegalStateException("FileNamePattern [" + this.tbrp.fileNamePattern.getPattern() + "] does not contain a valid DateToken");
        }
        this.rc = dtc.getTimeZone() != null ? new RollingCalendar(dtc.getDatePattern(), dtc.getTimeZone(), Locale.getDefault()) : new RollingCalendar(dtc.getDatePattern());
        this.addInfo("The date pattern is '" + dtc.getDatePattern() + "' from file name pattern '" + this.tbrp.fileNamePattern.getPattern() + "'.");
        this.rc.printPeriodicity(this);
        if (!this.rc.isCollisionFree()) {
            this.addError("The date format in FileNamePattern will result in collisions in the names of archived log files.");
            this.addError("For more information, please visit " + COLLIDING_DATE_FORMAT_URL);
            this.withErrors();
            return;
        }
        this.setDateInCurrentPeriod(new Date(this.getCurrentTime()));
        if (this.tbrp.getParentsRawFileProperty() != null && (currentFile = new File(this.tbrp.getParentsRawFileProperty())).exists() && currentFile.canRead()) {
            this.setDateInCurrentPeriod(new Date(currentFile.lastModified()));
        }
        this.addInfo("Setting initial period to " + this.dateInCurrentPeriod);
        this.computeNextCheck();
    }

    @Override
    public void stop() {
        this.started = false;
    }

    protected void computeNextCheck() {
        this.nextCheck = this.rc.getNextTriggeringDate(this.dateInCurrentPeriod).getTime();
    }

    protected void setDateInCurrentPeriod(long now) {
        this.dateInCurrentPeriod.setTime(now);
    }

    public void setDateInCurrentPeriod(Date _dateInCurrentPeriod) {
        this.dateInCurrentPeriod = _dateInCurrentPeriod;
    }

    @Override
    public String getElapsedPeriodsFileName() {
        return this.elapsedPeriodsFileName;
    }

    @Override
    public String getCurrentPeriodsFileNameWithoutCompressionSuffix() {
        return this.tbrp.fileNamePatternWithoutCompSuffix.convert(this.dateInCurrentPeriod);
    }

    @Override
    public void setCurrentTime(long timeInMillis) {
        this.artificialCurrentTime = timeInMillis;
    }

    @Override
    public long getCurrentTime() {
        if (this.artificialCurrentTime >= 0L) {
            return this.artificialCurrentTime;
        }
        return System.currentTimeMillis();
    }

    @Override
    public void setTimeBasedRollingPolicy(TimeBasedRollingPolicy<E> _tbrp) {
        this.tbrp = _tbrp;
    }

    @Override
    public ArchiveRemover getArchiveRemover() {
        return this.archiveRemover;
    }

    protected void withErrors() {
        this.errorFree = false;
    }

    protected boolean isErrorFree() {
        return this.errorFree;
    }
}

