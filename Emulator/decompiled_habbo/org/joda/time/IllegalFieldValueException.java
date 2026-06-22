/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time;

import org.joda.time.DateTimeFieldType;
import org.joda.time.DurationFieldType;

public class IllegalFieldValueException
extends IllegalArgumentException {
    private static final long serialVersionUID = 6305711765985447737L;
    private final DateTimeFieldType iDateTimeFieldType;
    private final DurationFieldType iDurationFieldType;
    private final String iFieldName;
    private final Number iNumberValue;
    private final String iStringValue;
    private final Number iLowerBound;
    private final Number iUpperBound;
    private String iMessage;

    private static String createMessage(String string, Number number, Number number2, Number number3, String string2) {
        StringBuilder stringBuilder = new StringBuilder().append("Value ").append(number).append(" for ").append(string).append(' ');
        if (number2 == null) {
            if (number3 == null) {
                stringBuilder.append("is not supported");
            } else {
                stringBuilder.append("must not be larger than ").append(number3);
            }
        } else if (number3 == null) {
            stringBuilder.append("must not be smaller than ").append(number2);
        } else {
            stringBuilder.append("must be in the range [").append(number2).append(',').append(number3).append(']');
        }
        if (string2 != null) {
            stringBuilder.append(": ").append(string2);
        }
        return stringBuilder.toString();
    }

    private static String createMessage(String string, String string2) {
        StringBuffer stringBuffer = new StringBuffer().append("Value ");
        if (string2 == null) {
            stringBuffer.append("null");
        } else {
            stringBuffer.append('\"');
            stringBuffer.append(string2);
            stringBuffer.append('\"');
        }
        stringBuffer.append(" for ").append(string).append(' ').append("is not supported");
        return stringBuffer.toString();
    }

    public IllegalFieldValueException(DateTimeFieldType dateTimeFieldType, Number number, Number number2, Number number3) {
        super(IllegalFieldValueException.createMessage(dateTimeFieldType.getName(), number, number2, number3, null));
        this.iDateTimeFieldType = dateTimeFieldType;
        this.iDurationFieldType = null;
        this.iFieldName = dateTimeFieldType.getName();
        this.iNumberValue = number;
        this.iStringValue = null;
        this.iLowerBound = number2;
        this.iUpperBound = number3;
        this.iMessage = super.getMessage();
    }

    public IllegalFieldValueException(DateTimeFieldType dateTimeFieldType, Number number, String string) {
        super(IllegalFieldValueException.createMessage(dateTimeFieldType.getName(), number, null, null, string));
        this.iDateTimeFieldType = dateTimeFieldType;
        this.iDurationFieldType = null;
        this.iFieldName = dateTimeFieldType.getName();
        this.iNumberValue = number;
        this.iStringValue = null;
        this.iLowerBound = null;
        this.iUpperBound = null;
        this.iMessage = super.getMessage();
    }

    public IllegalFieldValueException(DurationFieldType durationFieldType, Number number, Number number2, Number number3) {
        super(IllegalFieldValueException.createMessage(durationFieldType.getName(), number, number2, number3, null));
        this.iDateTimeFieldType = null;
        this.iDurationFieldType = durationFieldType;
        this.iFieldName = durationFieldType.getName();
        this.iNumberValue = number;
        this.iStringValue = null;
        this.iLowerBound = number2;
        this.iUpperBound = number3;
        this.iMessage = super.getMessage();
    }

    public IllegalFieldValueException(String string, Number number, Number number2, Number number3) {
        super(IllegalFieldValueException.createMessage(string, number, number2, number3, null));
        this.iDateTimeFieldType = null;
        this.iDurationFieldType = null;
        this.iFieldName = string;
        this.iNumberValue = number;
        this.iStringValue = null;
        this.iLowerBound = number2;
        this.iUpperBound = number3;
        this.iMessage = super.getMessage();
    }

    public IllegalFieldValueException(DateTimeFieldType dateTimeFieldType, String string) {
        super(IllegalFieldValueException.createMessage(dateTimeFieldType.getName(), string));
        this.iDateTimeFieldType = dateTimeFieldType;
        this.iDurationFieldType = null;
        this.iFieldName = dateTimeFieldType.getName();
        this.iStringValue = string;
        this.iNumberValue = null;
        this.iLowerBound = null;
        this.iUpperBound = null;
        this.iMessage = super.getMessage();
    }

    public IllegalFieldValueException(DurationFieldType durationFieldType, String string) {
        super(IllegalFieldValueException.createMessage(durationFieldType.getName(), string));
        this.iDateTimeFieldType = null;
        this.iDurationFieldType = durationFieldType;
        this.iFieldName = durationFieldType.getName();
        this.iStringValue = string;
        this.iNumberValue = null;
        this.iLowerBound = null;
        this.iUpperBound = null;
        this.iMessage = super.getMessage();
    }

    public IllegalFieldValueException(String string, String string2) {
        super(IllegalFieldValueException.createMessage(string, string2));
        this.iDateTimeFieldType = null;
        this.iDurationFieldType = null;
        this.iFieldName = string;
        this.iStringValue = string2;
        this.iNumberValue = null;
        this.iLowerBound = null;
        this.iUpperBound = null;
        this.iMessage = super.getMessage();
    }

    public DateTimeFieldType getDateTimeFieldType() {
        return this.iDateTimeFieldType;
    }

    public DurationFieldType getDurationFieldType() {
        return this.iDurationFieldType;
    }

    public String getFieldName() {
        return this.iFieldName;
    }

    public Number getIllegalNumberValue() {
        return this.iNumberValue;
    }

    public String getIllegalStringValue() {
        return this.iStringValue;
    }

    public String getIllegalValueAsString() {
        String string = this.iStringValue;
        if (string == null) {
            string = String.valueOf(this.iNumberValue);
        }
        return string;
    }

    public Number getLowerBound() {
        return this.iLowerBound;
    }

    public Number getUpperBound() {
        return this.iUpperBound;
    }

    public String getMessage() {
        return this.iMessage;
    }

    public void prependMessage(String string) {
        if (this.iMessage == null) {
            this.iMessage = string;
        } else if (string != null) {
            this.iMessage = string + ": " + this.iMessage;
        }
    }
}

