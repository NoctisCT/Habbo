/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.rolling.helper;

import ch.qos.logback.core.pattern.Converter;
import ch.qos.logback.core.pattern.LiteralConverter;
import ch.qos.logback.core.rolling.helper.ArchiveRemover;
import ch.qos.logback.core.rolling.helper.DateTokenConverter;
import ch.qos.logback.core.rolling.helper.FileFilterUtil;
import ch.qos.logback.core.rolling.helper.FileNamePattern;
import ch.qos.logback.core.rolling.helper.RollingCalendar;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.util.FileSize;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

public class TimeBasedArchiveRemover
extends ContextAwareBase
implements ArchiveRemover {
    protected static final long UNINITIALIZED = -1L;
    protected static final long INACTIVITY_TOLERANCE_IN_MILLIS = 2764800000L;
    static final int MAX_VALUE_FOR_INACTIVITY_PERIODS = 336;
    final FileNamePattern fileNamePattern;
    final RollingCalendar rc;
    private int maxHistory = 0;
    private long totalSizeCap = 0L;
    final boolean parentClean;
    long lastHeartBeat = -1L;
    int callCount = 0;

    public TimeBasedArchiveRemover(FileNamePattern fileNamePattern, RollingCalendar rc) {
        this.fileNamePattern = fileNamePattern;
        this.rc = rc;
        this.parentClean = this.computeParentCleaningFlag(fileNamePattern);
    }

    @Override
    public void clean(Date now) {
        long nowInMillis = now.getTime();
        int periodsElapsed = this.computeElapsedPeriodsSinceLastClean(nowInMillis);
        this.lastHeartBeat = nowInMillis;
        if (periodsElapsed > 1) {
            this.addInfo("Multiple periods, i.e. " + periodsElapsed + " periods, seem to have elapsed. This is expected at application start.");
        }
        for (int i = 0; i < periodsElapsed; ++i) {
            int offset = this.getPeriodOffsetForDeletionTarget() - i;
            Date dateOfPeriodToClean = this.rc.getEndOfNextNthPeriod(now, offset);
            this.cleanPeriod(dateOfPeriodToClean);
        }
    }

    protected File[] getFilesInPeriod(Date dateOfPeriodToClean) {
        String filenameToDelete = this.fileNamePattern.convert(dateOfPeriodToClean);
        File file2Delete = new File(filenameToDelete);
        if (this.fileExistsAndIsFile(file2Delete)) {
            return new File[]{file2Delete};
        }
        return new File[0];
    }

    private boolean fileExistsAndIsFile(File file2Delete) {
        return file2Delete.exists() && file2Delete.isFile();
    }

    public void cleanPeriod(Date dateOfPeriodToClean) {
        File[] matchingFileArray;
        for (File f : matchingFileArray = this.getFilesInPeriod(dateOfPeriodToClean)) {
            this.addInfo("deleting " + f);
            f.delete();
        }
        if (this.parentClean && matchingFileArray.length > 0) {
            File parentDir = this.getParentDir(matchingFileArray[0]);
            this.removeFolderIfEmpty(parentDir);
        }
    }

    void capTotalSize(Date now) {
        long totalSize = 0L;
        long totalRemoved = 0L;
        for (int offset = 0; offset < this.maxHistory; ++offset) {
            Date date = this.rc.getEndOfNextNthPeriod(now, -offset);
            File[] matchingFileArray = this.getFilesInPeriod(date);
            this.descendingSortByLastModified(matchingFileArray);
            for (File f : matchingFileArray) {
                long size = f.length();
                if (totalSize + size > this.totalSizeCap) {
                    this.addInfo("Deleting [" + f + "]" + " of size " + new FileSize(size));
                    totalRemoved += size;
                    f.delete();
                }
                totalSize += size;
            }
        }
        this.addInfo("Removed  " + new FileSize(totalRemoved) + " of files");
    }

    private void descendingSortByLastModified(File[] matchingFileArray) {
        Arrays.sort(matchingFileArray, new Comparator<File>(){

            @Override
            public int compare(File f1, File f2) {
                long l2;
                long l1 = f1.lastModified();
                if (l1 == (l2 = f2.lastModified())) {
                    return 0;
                }
                if (l2 < l1) {
                    return -1;
                }
                return 1;
            }
        });
    }

    File getParentDir(File file) {
        File absolute = file.getAbsoluteFile();
        File parentDir = absolute.getParentFile();
        return parentDir;
    }

    int computeElapsedPeriodsSinceLastClean(long nowInMillis) {
        long periodsElapsed = 0L;
        if (this.lastHeartBeat == -1L) {
            this.addInfo("first clean up after appender initialization");
            periodsElapsed = this.rc.periodBarriersCrossed(nowInMillis, nowInMillis + 2764800000L);
            periodsElapsed = Math.min(periodsElapsed, 336L);
        } else {
            periodsElapsed = this.rc.periodBarriersCrossed(this.lastHeartBeat, nowInMillis);
        }
        return (int)periodsElapsed;
    }

    boolean computeParentCleaningFlag(FileNamePattern fileNamePattern) {
        Converter<Object> p;
        DateTokenConverter<Object> dtc = fileNamePattern.getPrimaryDateTokenConverter();
        if (dtc.getDatePattern().indexOf(47) != -1) {
            return true;
        }
        for (p = fileNamePattern.headTokenConverter; p != null && !(p instanceof DateTokenConverter); p = p.getNext()) {
        }
        while (p != null) {
            String s;
            if (p instanceof LiteralConverter && (s = p.convert(null)).indexOf(47) != -1) {
                return true;
            }
            p = p.getNext();
        }
        return false;
    }

    void removeFolderIfEmpty(File dir) {
        this.removeFolderIfEmpty(dir, 0);
    }

    private void removeFolderIfEmpty(File dir, int depth) {
        if (depth >= 3) {
            return;
        }
        if (dir.isDirectory() && FileFilterUtil.isEmptyDirectory(dir)) {
            this.addInfo("deleting folder [" + dir + "]");
            dir.delete();
            this.removeFolderIfEmpty(dir.getParentFile(), depth + 1);
        }
    }

    @Override
    public void setMaxHistory(int maxHistory) {
        this.maxHistory = maxHistory;
    }

    protected int getPeriodOffsetForDeletionTarget() {
        return -this.maxHistory - 1;
    }

    @Override
    public void setTotalSizeCap(long totalSizeCap) {
        this.totalSizeCap = totalSizeCap;
    }

    public String toString() {
        return "c.q.l.core.rolling.helper.TimeBasedArchiveRemover";
    }

    @Override
    public Future<?> cleanAsynchronously(Date now) {
        ArhiveRemoverRunnable runnable = new ArhiveRemoverRunnable(now);
        ScheduledExecutorService executorService = this.context.getScheduledExecutorService();
        Future<?> future = executorService.submit(runnable);
        return future;
    }

    public class ArhiveRemoverRunnable
    implements Runnable {
        Date now;

        ArhiveRemoverRunnable(Date now) {
            this.now = now;
        }

        @Override
        public void run() {
            TimeBasedArchiveRemover.this.clean(this.now);
            if (TimeBasedArchiveRemover.this.totalSizeCap != 0L && TimeBasedArchiveRemover.this.totalSizeCap > 0L) {
                TimeBasedArchiveRemover.this.capTotalSize(this.now);
            }
        }
    }
}

