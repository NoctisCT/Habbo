/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.convert;

import org.joda.time.JodaTimePermission;
import org.joda.time.convert.CalendarConverter;
import org.joda.time.convert.Converter;
import org.joda.time.convert.ConverterSet;
import org.joda.time.convert.DateConverter;
import org.joda.time.convert.DurationConverter;
import org.joda.time.convert.InstantConverter;
import org.joda.time.convert.IntervalConverter;
import org.joda.time.convert.LongConverter;
import org.joda.time.convert.NullConverter;
import org.joda.time.convert.PartialConverter;
import org.joda.time.convert.PeriodConverter;
import org.joda.time.convert.ReadableDurationConverter;
import org.joda.time.convert.ReadableInstantConverter;
import org.joda.time.convert.ReadableIntervalConverter;
import org.joda.time.convert.ReadablePartialConverter;
import org.joda.time.convert.ReadablePeriodConverter;
import org.joda.time.convert.StringConverter;

public final class ConverterManager {
    private static ConverterManager INSTANCE;
    private ConverterSet iInstantConverters = new ConverterSet(new Converter[]{ReadableInstantConverter.INSTANCE, StringConverter.INSTANCE, CalendarConverter.INSTANCE, DateConverter.INSTANCE, LongConverter.INSTANCE, NullConverter.INSTANCE});
    private ConverterSet iPartialConverters = new ConverterSet(new Converter[]{ReadablePartialConverter.INSTANCE, ReadableInstantConverter.INSTANCE, StringConverter.INSTANCE, CalendarConverter.INSTANCE, DateConverter.INSTANCE, LongConverter.INSTANCE, NullConverter.INSTANCE});
    private ConverterSet iDurationConverters = new ConverterSet(new Converter[]{ReadableDurationConverter.INSTANCE, ReadableIntervalConverter.INSTANCE, StringConverter.INSTANCE, LongConverter.INSTANCE, NullConverter.INSTANCE});
    private ConverterSet iPeriodConverters = new ConverterSet(new Converter[]{ReadableDurationConverter.INSTANCE, ReadablePeriodConverter.INSTANCE, ReadableIntervalConverter.INSTANCE, StringConverter.INSTANCE, NullConverter.INSTANCE});
    private ConverterSet iIntervalConverters = new ConverterSet(new Converter[]{ReadableIntervalConverter.INSTANCE, StringConverter.INSTANCE, NullConverter.INSTANCE});

    public static ConverterManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ConverterManager();
        }
        return INSTANCE;
    }

    protected ConverterManager() {
    }

    public InstantConverter getInstantConverter(Object object) {
        InstantConverter instantConverter = (InstantConverter)this.iInstantConverters.select(object == null ? null : object.getClass());
        if (instantConverter != null) {
            return instantConverter;
        }
        throw new IllegalArgumentException("No instant converter found for type: " + (object == null ? "null" : object.getClass().getName()));
    }

    public InstantConverter[] getInstantConverters() {
        ConverterSet converterSet = this.iInstantConverters;
        Converter[] converterArray = new InstantConverter[converterSet.size()];
        converterSet.copyInto(converterArray);
        return converterArray;
    }

    public InstantConverter addInstantConverter(InstantConverter instantConverter) throws SecurityException {
        this.checkAlterInstantConverters();
        if (instantConverter == null) {
            return null;
        }
        Converter[] converterArray = new InstantConverter[1];
        this.iInstantConverters = this.iInstantConverters.add(instantConverter, converterArray);
        return converterArray[0];
    }

    public InstantConverter removeInstantConverter(InstantConverter instantConverter) throws SecurityException {
        this.checkAlterInstantConverters();
        if (instantConverter == null) {
            return null;
        }
        Converter[] converterArray = new InstantConverter[1];
        this.iInstantConverters = this.iInstantConverters.remove(instantConverter, converterArray);
        return converterArray[0];
    }

    private void checkAlterInstantConverters() throws SecurityException {
        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new JodaTimePermission("ConverterManager.alterInstantConverters"));
        }
    }

    public PartialConverter getPartialConverter(Object object) {
        PartialConverter partialConverter = (PartialConverter)this.iPartialConverters.select(object == null ? null : object.getClass());
        if (partialConverter != null) {
            return partialConverter;
        }
        throw new IllegalArgumentException("No partial converter found for type: " + (object == null ? "null" : object.getClass().getName()));
    }

    public PartialConverter[] getPartialConverters() {
        ConverterSet converterSet = this.iPartialConverters;
        Converter[] converterArray = new PartialConverter[converterSet.size()];
        converterSet.copyInto(converterArray);
        return converterArray;
    }

    public PartialConverter addPartialConverter(PartialConverter partialConverter) throws SecurityException {
        this.checkAlterPartialConverters();
        if (partialConverter == null) {
            return null;
        }
        Converter[] converterArray = new PartialConverter[1];
        this.iPartialConverters = this.iPartialConverters.add(partialConverter, converterArray);
        return converterArray[0];
    }

    public PartialConverter removePartialConverter(PartialConverter partialConverter) throws SecurityException {
        this.checkAlterPartialConverters();
        if (partialConverter == null) {
            return null;
        }
        Converter[] converterArray = new PartialConverter[1];
        this.iPartialConverters = this.iPartialConverters.remove(partialConverter, converterArray);
        return converterArray[0];
    }

    private void checkAlterPartialConverters() throws SecurityException {
        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new JodaTimePermission("ConverterManager.alterPartialConverters"));
        }
    }

    public DurationConverter getDurationConverter(Object object) {
        DurationConverter durationConverter = (DurationConverter)this.iDurationConverters.select(object == null ? null : object.getClass());
        if (durationConverter != null) {
            return durationConverter;
        }
        throw new IllegalArgumentException("No duration converter found for type: " + (object == null ? "null" : object.getClass().getName()));
    }

    public DurationConverter[] getDurationConverters() {
        ConverterSet converterSet = this.iDurationConverters;
        Converter[] converterArray = new DurationConverter[converterSet.size()];
        converterSet.copyInto(converterArray);
        return converterArray;
    }

    public DurationConverter addDurationConverter(DurationConverter durationConverter) throws SecurityException {
        this.checkAlterDurationConverters();
        if (durationConverter == null) {
            return null;
        }
        Converter[] converterArray = new DurationConverter[1];
        this.iDurationConverters = this.iDurationConverters.add(durationConverter, converterArray);
        return converterArray[0];
    }

    public DurationConverter removeDurationConverter(DurationConverter durationConverter) throws SecurityException {
        this.checkAlterDurationConverters();
        if (durationConverter == null) {
            return null;
        }
        Converter[] converterArray = new DurationConverter[1];
        this.iDurationConverters = this.iDurationConverters.remove(durationConverter, converterArray);
        return converterArray[0];
    }

    private void checkAlterDurationConverters() throws SecurityException {
        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new JodaTimePermission("ConverterManager.alterDurationConverters"));
        }
    }

    public PeriodConverter getPeriodConverter(Object object) {
        PeriodConverter periodConverter = (PeriodConverter)this.iPeriodConverters.select(object == null ? null : object.getClass());
        if (periodConverter != null) {
            return periodConverter;
        }
        throw new IllegalArgumentException("No period converter found for type: " + (object == null ? "null" : object.getClass().getName()));
    }

    public PeriodConverter[] getPeriodConverters() {
        ConverterSet converterSet = this.iPeriodConverters;
        Converter[] converterArray = new PeriodConverter[converterSet.size()];
        converterSet.copyInto(converterArray);
        return converterArray;
    }

    public PeriodConverter addPeriodConverter(PeriodConverter periodConverter) throws SecurityException {
        this.checkAlterPeriodConverters();
        if (periodConverter == null) {
            return null;
        }
        Converter[] converterArray = new PeriodConverter[1];
        this.iPeriodConverters = this.iPeriodConverters.add(periodConverter, converterArray);
        return converterArray[0];
    }

    public PeriodConverter removePeriodConverter(PeriodConverter periodConverter) throws SecurityException {
        this.checkAlterPeriodConverters();
        if (periodConverter == null) {
            return null;
        }
        Converter[] converterArray = new PeriodConverter[1];
        this.iPeriodConverters = this.iPeriodConverters.remove(periodConverter, converterArray);
        return converterArray[0];
    }

    private void checkAlterPeriodConverters() throws SecurityException {
        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new JodaTimePermission("ConverterManager.alterPeriodConverters"));
        }
    }

    public IntervalConverter getIntervalConverter(Object object) {
        IntervalConverter intervalConverter = (IntervalConverter)this.iIntervalConverters.select(object == null ? null : object.getClass());
        if (intervalConverter != null) {
            return intervalConverter;
        }
        throw new IllegalArgumentException("No interval converter found for type: " + (object == null ? "null" : object.getClass().getName()));
    }

    public IntervalConverter[] getIntervalConverters() {
        ConverterSet converterSet = this.iIntervalConverters;
        Converter[] converterArray = new IntervalConverter[converterSet.size()];
        converterSet.copyInto(converterArray);
        return converterArray;
    }

    public IntervalConverter addIntervalConverter(IntervalConverter intervalConverter) throws SecurityException {
        this.checkAlterIntervalConverters();
        if (intervalConverter == null) {
            return null;
        }
        Converter[] converterArray = new IntervalConverter[1];
        this.iIntervalConverters = this.iIntervalConverters.add(intervalConverter, converterArray);
        return converterArray[0];
    }

    public IntervalConverter removeIntervalConverter(IntervalConverter intervalConverter) throws SecurityException {
        this.checkAlterIntervalConverters();
        if (intervalConverter == null) {
            return null;
        }
        Converter[] converterArray = new IntervalConverter[1];
        this.iIntervalConverters = this.iIntervalConverters.remove(intervalConverter, converterArray);
        return converterArray[0];
    }

    private void checkAlterIntervalConverters() throws SecurityException {
        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new JodaTimePermission("ConverterManager.alterIntervalConverters"));
        }
    }

    public String toString() {
        return "ConverterManager[" + this.iInstantConverters.size() + " instant," + this.iPartialConverters.size() + " partial," + this.iDurationConverters.size() + " duration," + this.iPeriodConverters.size() + " period," + this.iIntervalConverters.size() + " interval]";
    }
}

