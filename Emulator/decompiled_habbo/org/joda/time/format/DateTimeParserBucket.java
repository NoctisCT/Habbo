/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.format;

import java.util.Arrays;
import java.util.Locale;
import org.joda.time.Chronology;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.DurationField;
import org.joda.time.DurationFieldType;
import org.joda.time.IllegalFieldValueException;
import org.joda.time.IllegalInstantException;
import org.joda.time.format.DateTimeParser;
import org.joda.time.format.DateTimeParserInternalParser;
import org.joda.time.format.FormatUtils;
import org.joda.time.format.InternalParser;

public class DateTimeParserBucket {
    private final Chronology iChrono;
    private final long iMillis;
    private final Locale iLocale;
    private final int iDefaultYear;
    private final DateTimeZone iDefaultZone;
    private final Integer iDefaultPivotYear;
    private DateTimeZone iZone;
    private Integer iOffset;
    private Integer iPivotYear;
    private SavedField[] iSavedFields;
    private int iSavedFieldsCount;
    private boolean iSavedFieldsShared;
    private Object iSavedState;

    @Deprecated
    public DateTimeParserBucket(long l, Chronology chronology, Locale locale) {
        this(l, chronology, locale, null, 2000);
    }

    @Deprecated
    public DateTimeParserBucket(long l, Chronology chronology, Locale locale, Integer n) {
        this(l, chronology, locale, n, 2000);
    }

    public DateTimeParserBucket(long l, Chronology chronology, Locale locale, Integer n, int n2) {
        chronology = DateTimeUtils.getChronology(chronology);
        this.iMillis = l;
        this.iDefaultZone = chronology.getZone();
        this.iChrono = chronology.withUTC();
        this.iLocale = locale == null ? Locale.getDefault() : locale;
        this.iDefaultYear = n2;
        this.iDefaultPivotYear = n;
        this.iZone = this.iDefaultZone;
        this.iPivotYear = this.iDefaultPivotYear;
        this.iSavedFields = new SavedField[8];
    }

    public void reset() {
        this.iZone = this.iDefaultZone;
        this.iOffset = null;
        this.iPivotYear = this.iDefaultPivotYear;
        this.iSavedFieldsCount = 0;
        this.iSavedFieldsShared = false;
        this.iSavedState = null;
    }

    public long parseMillis(DateTimeParser dateTimeParser, CharSequence charSequence) {
        this.reset();
        return this.doParseMillis(DateTimeParserInternalParser.of(dateTimeParser), charSequence);
    }

    long doParseMillis(InternalParser internalParser, CharSequence charSequence) {
        int n = internalParser.parseInto(this, charSequence, 0);
        if (n >= 0) {
            if (n >= charSequence.length()) {
                return this.computeMillis(true, charSequence);
            }
        } else {
            n ^= 0xFFFFFFFF;
        }
        throw new IllegalArgumentException(FormatUtils.createErrorMessage(charSequence.toString(), n));
    }

    public Chronology getChronology() {
        return this.iChrono;
    }

    public Locale getLocale() {
        return this.iLocale;
    }

    public DateTimeZone getZone() {
        return this.iZone;
    }

    public void setZone(DateTimeZone dateTimeZone) {
        this.iSavedState = null;
        this.iZone = dateTimeZone;
    }

    @Deprecated
    public int getOffset() {
        return this.iOffset != null ? this.iOffset : 0;
    }

    public Integer getOffsetInteger() {
        return this.iOffset;
    }

    @Deprecated
    public void setOffset(int n) {
        this.iSavedState = null;
        this.iOffset = n;
    }

    public void setOffset(Integer n) {
        this.iSavedState = null;
        this.iOffset = n;
    }

    public Integer getPivotYear() {
        return this.iPivotYear;
    }

    @Deprecated
    public void setPivotYear(Integer n) {
        this.iPivotYear = n;
    }

    public void saveField(DateTimeField dateTimeField, int n) {
        this.obtainSaveField().init(dateTimeField, n);
    }

    public void saveField(DateTimeFieldType dateTimeFieldType, int n) {
        this.obtainSaveField().init(dateTimeFieldType.getField(this.iChrono), n);
    }

    public void saveField(DateTimeFieldType dateTimeFieldType, String string, Locale locale) {
        this.obtainSaveField().init(dateTimeFieldType.getField(this.iChrono), string, locale);
    }

    private SavedField obtainSaveField() {
        Object object;
        int n = this.iSavedFieldsCount;
        SavedField[] savedFieldArray = this.iSavedFields;
        if (n == savedFieldArray.length || this.iSavedFieldsShared) {
            object = new SavedField[n == savedFieldArray.length ? n * 2 : savedFieldArray.length];
            System.arraycopy(savedFieldArray, 0, object, 0, n);
            savedFieldArray = object;
            this.iSavedFields = object;
            this.iSavedFieldsShared = false;
        }
        this.iSavedState = null;
        object = savedFieldArray[n];
        if (object == null) {
            savedFieldArray[n] = new SavedField();
            object = savedFieldArray[n];
        }
        this.iSavedFieldsCount = n + 1;
        return object;
    }

    public Object saveState() {
        if (this.iSavedState == null) {
            this.iSavedState = new SavedState();
        }
        return this.iSavedState;
    }

    public boolean restoreState(Object object) {
        if (object instanceof SavedState && ((SavedState)object).restoreState(this)) {
            this.iSavedState = object;
            return true;
        }
        return false;
    }

    public long computeMillis() {
        return this.computeMillis(false, (CharSequence)null);
    }

    public long computeMillis(boolean bl) {
        return this.computeMillis(bl, (CharSequence)null);
    }

    public long computeMillis(boolean bl, String string) {
        return this.computeMillis(bl, (CharSequence)string);
    }

    public long computeMillis(boolean bl, CharSequence charSequence) {
        int n;
        SavedField[] savedFieldArray = this.iSavedFields;
        int n2 = this.iSavedFieldsCount;
        if (this.iSavedFieldsShared) {
            savedFieldArray = (SavedField[])this.iSavedFields.clone();
            this.iSavedFields = savedFieldArray;
            this.iSavedFieldsShared = false;
        }
        DateTimeParserBucket.sort(savedFieldArray, n2);
        if (n2 > 0) {
            DurationField durationField = DurationFieldType.months().getField(this.iChrono);
            DurationField durationField2 = DurationFieldType.days().getField(this.iChrono);
            DurationField durationField3 = savedFieldArray[0].iField.getDurationField();
            if (DateTimeParserBucket.compareReverse(durationField3, durationField) >= 0 && DateTimeParserBucket.compareReverse(durationField3, durationField2) <= 0) {
                this.saveField(DateTimeFieldType.year(), this.iDefaultYear);
                return this.computeMillis(bl, charSequence);
            }
        }
        long l = this.iMillis;
        try {
            for (n = 0; n < n2; ++n) {
                l = savedFieldArray[n].set(l, bl);
            }
            if (bl) {
                for (n = 0; n < n2; ++n) {
                    if (savedFieldArray[n].iField.isLenient()) continue;
                    l = savedFieldArray[n].set(l, n == n2 - 1);
                }
            }
        }
        catch (IllegalFieldValueException illegalFieldValueException) {
            if (charSequence != null) {
                illegalFieldValueException.prependMessage("Cannot parse \"" + charSequence + '\"');
            }
            throw illegalFieldValueException;
        }
        if (this.iOffset != null) {
            l -= (long)this.iOffset.intValue();
        } else if (this.iZone != null && (n = this.iZone.getOffsetFromLocal(l)) != this.iZone.getOffset(l -= (long)n)) {
            String string = "Illegal instant due to time zone offset transition (" + this.iZone + ')';
            if (charSequence != null) {
                string = "Cannot parse \"" + charSequence + "\": " + string;
            }
            throw new IllegalInstantException(string);
        }
        return l;
    }

    private static void sort(SavedField[] savedFieldArray, int n) {
        if (n > 10) {
            Arrays.sort(savedFieldArray, 0, n);
        } else {
            for (int i = 0; i < n; ++i) {
                for (int j = i; j > 0 && savedFieldArray[j - 1].compareTo(savedFieldArray[j]) > 0; --j) {
                    SavedField savedField = savedFieldArray[j];
                    savedFieldArray[j] = savedFieldArray[j - 1];
                    savedFieldArray[j - 1] = savedField;
                }
            }
        }
    }

    static int compareReverse(DurationField durationField, DurationField durationField2) {
        if (durationField == null || !durationField.isSupported()) {
            if (durationField2 == null || !durationField2.isSupported()) {
                return 0;
            }
            return -1;
        }
        if (durationField2 == null || !durationField2.isSupported()) {
            return 1;
        }
        return -durationField.compareTo(durationField2);
    }

    static /* synthetic */ SavedField[] access$202(DateTimeParserBucket dateTimeParserBucket, SavedField[] savedFieldArray) {
        dateTimeParserBucket.iSavedFields = savedFieldArray;
        return savedFieldArray;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static class SavedField
    implements Comparable<SavedField> {
        DateTimeField iField;
        int iValue;
        String iText;
        Locale iLocale;

        SavedField() {
        }

        void init(DateTimeField dateTimeField, int n) {
            this.iField = dateTimeField;
            this.iValue = n;
            this.iText = null;
            this.iLocale = null;
        }

        void init(DateTimeField dateTimeField, String string, Locale locale) {
            this.iField = dateTimeField;
            this.iValue = 0;
            this.iText = string;
            this.iLocale = locale;
        }

        long set(long l, boolean bl) {
            l = this.iText == null ? this.iField.setExtended(l, this.iValue) : this.iField.set(l, this.iText, this.iLocale);
            if (bl) {
                l = this.iField.roundFloor(l);
            }
            return l;
        }

        @Override
        public int compareTo(SavedField savedField) {
            DateTimeField dateTimeField = savedField.iField;
            int n = DateTimeParserBucket.compareReverse(this.iField.getRangeDurationField(), dateTimeField.getRangeDurationField());
            if (n != 0) {
                return n;
            }
            return DateTimeParserBucket.compareReverse(this.iField.getDurationField(), dateTimeField.getDurationField());
        }
    }

    class SavedState {
        final DateTimeZone iZone;
        final Integer iOffset;
        final SavedField[] iSavedFields;
        final int iSavedFieldsCount;

        SavedState() {
            this.iZone = DateTimeParserBucket.this.iZone;
            this.iOffset = DateTimeParserBucket.this.iOffset;
            this.iSavedFields = DateTimeParserBucket.this.iSavedFields;
            this.iSavedFieldsCount = DateTimeParserBucket.this.iSavedFieldsCount;
        }

        boolean restoreState(DateTimeParserBucket dateTimeParserBucket) {
            if (dateTimeParserBucket != DateTimeParserBucket.this) {
                return false;
            }
            dateTimeParserBucket.iZone = this.iZone;
            dateTimeParserBucket.iOffset = this.iOffset;
            DateTimeParserBucket.access$202(dateTimeParserBucket, this.iSavedFields);
            if (this.iSavedFieldsCount < dateTimeParserBucket.iSavedFieldsCount) {
                dateTimeParserBucket.iSavedFieldsShared = true;
            }
            dateTimeParserBucket.iSavedFieldsCount = this.iSavedFieldsCount;
            return true;
        }
    }
}

