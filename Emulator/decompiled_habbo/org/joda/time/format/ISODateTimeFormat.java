/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.format;

import java.util.Collection;
import java.util.HashSet;
import org.joda.time.DateTimeFieldType;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ISODateTimeFormat {
    protected ISODateTimeFormat() {
    }

    public static DateTimeFormatter forFields(Collection<DateTimeFieldType> collection, boolean bl, boolean bl2) {
        if (collection == null || collection.size() == 0) {
            throw new IllegalArgumentException("The fields must not be null or empty");
        }
        HashSet<DateTimeFieldType> hashSet = new HashSet<DateTimeFieldType>(collection);
        int n = hashSet.size();
        boolean bl3 = false;
        DateTimeFormatterBuilder dateTimeFormatterBuilder = new DateTimeFormatterBuilder();
        if (hashSet.contains(DateTimeFieldType.monthOfYear())) {
            bl3 = ISODateTimeFormat.dateByMonth(dateTimeFormatterBuilder, hashSet, bl, bl2);
        } else if (hashSet.contains(DateTimeFieldType.dayOfYear())) {
            bl3 = ISODateTimeFormat.dateByOrdinal(dateTimeFormatterBuilder, hashSet, bl, bl2);
        } else if (hashSet.contains(DateTimeFieldType.weekOfWeekyear())) {
            bl3 = ISODateTimeFormat.dateByWeek(dateTimeFormatterBuilder, hashSet, bl, bl2);
        } else if (hashSet.contains(DateTimeFieldType.dayOfMonth())) {
            bl3 = ISODateTimeFormat.dateByMonth(dateTimeFormatterBuilder, hashSet, bl, bl2);
        } else if (hashSet.contains(DateTimeFieldType.dayOfWeek())) {
            bl3 = ISODateTimeFormat.dateByWeek(dateTimeFormatterBuilder, hashSet, bl, bl2);
        } else if (hashSet.remove(DateTimeFieldType.year())) {
            dateTimeFormatterBuilder.append(Constants.ye);
            bl3 = true;
        } else if (hashSet.remove(DateTimeFieldType.weekyear())) {
            dateTimeFormatterBuilder.append(Constants.we);
            bl3 = true;
        }
        boolean bl4 = hashSet.size() < n;
        ISODateTimeFormat.time(dateTimeFormatterBuilder, hashSet, bl, bl2, bl3, bl4);
        if (!dateTimeFormatterBuilder.canBuildFormatter()) {
            throw new IllegalArgumentException("No valid format for fields: " + collection);
        }
        try {
            collection.retainAll(hashSet);
        }
        catch (UnsupportedOperationException unsupportedOperationException) {
            // empty catch block
        }
        return dateTimeFormatterBuilder.toFormatter();
    }

    private static boolean dateByMonth(DateTimeFormatterBuilder dateTimeFormatterBuilder, Collection<DateTimeFieldType> collection, boolean bl, boolean bl2) {
        boolean bl3 = false;
        if (collection.remove(DateTimeFieldType.year())) {
            dateTimeFormatterBuilder.append(Constants.ye);
            if (collection.remove(DateTimeFieldType.monthOfYear())) {
                if (collection.remove(DateTimeFieldType.dayOfMonth())) {
                    ISODateTimeFormat.appendSeparator(dateTimeFormatterBuilder, bl);
                    dateTimeFormatterBuilder.appendMonthOfYear(2);
                    ISODateTimeFormat.appendSeparator(dateTimeFormatterBuilder, bl);
                    dateTimeFormatterBuilder.appendDayOfMonth(2);
                } else {
                    dateTimeFormatterBuilder.appendLiteral('-');
                    dateTimeFormatterBuilder.appendMonthOfYear(2);
                    bl3 = true;
                }
            } else if (collection.remove(DateTimeFieldType.dayOfMonth())) {
                ISODateTimeFormat.checkNotStrictISO(collection, bl2);
                dateTimeFormatterBuilder.appendLiteral('-');
                dateTimeFormatterBuilder.appendLiteral('-');
                dateTimeFormatterBuilder.appendDayOfMonth(2);
            } else {
                bl3 = true;
            }
        } else if (collection.remove(DateTimeFieldType.monthOfYear())) {
            dateTimeFormatterBuilder.appendLiteral('-');
            dateTimeFormatterBuilder.appendLiteral('-');
            dateTimeFormatterBuilder.appendMonthOfYear(2);
            if (collection.remove(DateTimeFieldType.dayOfMonth())) {
                ISODateTimeFormat.appendSeparator(dateTimeFormatterBuilder, bl);
                dateTimeFormatterBuilder.appendDayOfMonth(2);
            } else {
                bl3 = true;
            }
        } else if (collection.remove(DateTimeFieldType.dayOfMonth())) {
            dateTimeFormatterBuilder.appendLiteral('-');
            dateTimeFormatterBuilder.appendLiteral('-');
            dateTimeFormatterBuilder.appendLiteral('-');
            dateTimeFormatterBuilder.appendDayOfMonth(2);
        }
        return bl3;
    }

    private static boolean dateByOrdinal(DateTimeFormatterBuilder dateTimeFormatterBuilder, Collection<DateTimeFieldType> collection, boolean bl, boolean bl2) {
        boolean bl3 = false;
        if (collection.remove(DateTimeFieldType.year())) {
            dateTimeFormatterBuilder.append(Constants.ye);
            if (collection.remove(DateTimeFieldType.dayOfYear())) {
                ISODateTimeFormat.appendSeparator(dateTimeFormatterBuilder, bl);
                dateTimeFormatterBuilder.appendDayOfYear(3);
            } else {
                bl3 = true;
            }
        } else if (collection.remove(DateTimeFieldType.dayOfYear())) {
            dateTimeFormatterBuilder.appendLiteral('-');
            dateTimeFormatterBuilder.appendDayOfYear(3);
        }
        return bl3;
    }

    private static boolean dateByWeek(DateTimeFormatterBuilder dateTimeFormatterBuilder, Collection<DateTimeFieldType> collection, boolean bl, boolean bl2) {
        boolean bl3 = false;
        if (collection.remove(DateTimeFieldType.weekyear())) {
            dateTimeFormatterBuilder.append(Constants.we);
            if (collection.remove(DateTimeFieldType.weekOfWeekyear())) {
                ISODateTimeFormat.appendSeparator(dateTimeFormatterBuilder, bl);
                dateTimeFormatterBuilder.appendLiteral('W');
                dateTimeFormatterBuilder.appendWeekOfWeekyear(2);
                if (collection.remove(DateTimeFieldType.dayOfWeek())) {
                    ISODateTimeFormat.appendSeparator(dateTimeFormatterBuilder, bl);
                    dateTimeFormatterBuilder.appendDayOfWeek(1);
                } else {
                    bl3 = true;
                }
            } else if (collection.remove(DateTimeFieldType.dayOfWeek())) {
                ISODateTimeFormat.checkNotStrictISO(collection, bl2);
                ISODateTimeFormat.appendSeparator(dateTimeFormatterBuilder, bl);
                dateTimeFormatterBuilder.appendLiteral('W');
                dateTimeFormatterBuilder.appendLiteral('-');
                dateTimeFormatterBuilder.appendDayOfWeek(1);
            } else {
                bl3 = true;
            }
        } else if (collection.remove(DateTimeFieldType.weekOfWeekyear())) {
            dateTimeFormatterBuilder.appendLiteral('-');
            dateTimeFormatterBuilder.appendLiteral('W');
            dateTimeFormatterBuilder.appendWeekOfWeekyear(2);
            if (collection.remove(DateTimeFieldType.dayOfWeek())) {
                ISODateTimeFormat.appendSeparator(dateTimeFormatterBuilder, bl);
                dateTimeFormatterBuilder.appendDayOfWeek(1);
            } else {
                bl3 = true;
            }
        } else if (collection.remove(DateTimeFieldType.dayOfWeek())) {
            dateTimeFormatterBuilder.appendLiteral('-');
            dateTimeFormatterBuilder.appendLiteral('W');
            dateTimeFormatterBuilder.appendLiteral('-');
            dateTimeFormatterBuilder.appendDayOfWeek(1);
        }
        return bl3;
    }

    private static void time(DateTimeFormatterBuilder dateTimeFormatterBuilder, Collection<DateTimeFieldType> collection, boolean bl, boolean bl2, boolean bl3, boolean bl4) {
        boolean bl5 = collection.remove(DateTimeFieldType.hourOfDay());
        boolean bl6 = collection.remove(DateTimeFieldType.minuteOfHour());
        boolean bl7 = collection.remove(DateTimeFieldType.secondOfMinute());
        boolean bl8 = collection.remove(DateTimeFieldType.millisOfSecond());
        if (!(bl5 || bl6 || bl7 || bl8)) {
            return;
        }
        if (bl5 || bl6 || bl7 || bl8) {
            if (bl2 && bl3) {
                throw new IllegalArgumentException("No valid ISO8601 format for fields because Date was reduced precision: " + collection);
            }
            if (bl4) {
                dateTimeFormatterBuilder.appendLiteral('T');
            }
        }
        if (!(bl5 && bl6 && bl7 || bl5 && !bl7 && !bl8)) {
            if (bl2 && bl4) {
                throw new IllegalArgumentException("No valid ISO8601 format for fields because Time was truncated: " + collection);
            }
            if (!(!bl5 && (bl6 && bl7 || bl6 && !bl8 || bl7) || !bl2)) {
                throw new IllegalArgumentException("No valid ISO8601 format for fields: " + collection);
            }
        }
        if (bl5) {
            dateTimeFormatterBuilder.appendHourOfDay(2);
        } else if (bl6 || bl7 || bl8) {
            dateTimeFormatterBuilder.appendLiteral('-');
        }
        if (bl && bl5 && bl6) {
            dateTimeFormatterBuilder.appendLiteral(':');
        }
        if (bl6) {
            dateTimeFormatterBuilder.appendMinuteOfHour(2);
        } else if (bl7 || bl8) {
            dateTimeFormatterBuilder.appendLiteral('-');
        }
        if (bl && bl6 && bl7) {
            dateTimeFormatterBuilder.appendLiteral(':');
        }
        if (bl7) {
            dateTimeFormatterBuilder.appendSecondOfMinute(2);
        } else if (bl8) {
            dateTimeFormatterBuilder.appendLiteral('-');
        }
        if (bl8) {
            dateTimeFormatterBuilder.appendLiteral('.');
            dateTimeFormatterBuilder.appendMillisOfSecond(3);
        }
    }

    private static void checkNotStrictISO(Collection<DateTimeFieldType> collection, boolean bl) {
        if (bl) {
            throw new IllegalArgumentException("No valid ISO8601 format for fields: " + collection);
        }
    }

    private static void appendSeparator(DateTimeFormatterBuilder dateTimeFormatterBuilder, boolean bl) {
        if (bl) {
            dateTimeFormatterBuilder.appendLiteral('-');
        }
    }

    public static DateTimeFormatter dateParser() {
        return Constants.dp;
    }

    public static DateTimeFormatter localDateParser() {
        return Constants.ldp;
    }

    public static DateTimeFormatter dateElementParser() {
        return Constants.dpe;
    }

    public static DateTimeFormatter timeParser() {
        return Constants.tp;
    }

    public static DateTimeFormatter localTimeParser() {
        return Constants.ltp;
    }

    public static DateTimeFormatter timeElementParser() {
        return Constants.tpe;
    }

    public static DateTimeFormatter dateTimeParser() {
        return Constants.dtp;
    }

    public static DateTimeFormatter dateOptionalTimeParser() {
        return Constants.dotp;
    }

    public static DateTimeFormatter localDateOptionalTimeParser() {
        return Constants.ldotp;
    }

    public static DateTimeFormatter date() {
        return ISODateTimeFormat.yearMonthDay();
    }

    public static DateTimeFormatter time() {
        return Constants.t;
    }

    public static DateTimeFormatter timeNoMillis() {
        return Constants.tx;
    }

    public static DateTimeFormatter tTime() {
        return Constants.tt;
    }

    public static DateTimeFormatter tTimeNoMillis() {
        return Constants.ttx;
    }

    public static DateTimeFormatter dateTime() {
        return Constants.dt;
    }

    public static DateTimeFormatter dateTimeNoMillis() {
        return Constants.dtx;
    }

    public static DateTimeFormatter ordinalDate() {
        return Constants.od;
    }

    public static DateTimeFormatter ordinalDateTime() {
        return Constants.odt;
    }

    public static DateTimeFormatter ordinalDateTimeNoMillis() {
        return Constants.odtx;
    }

    public static DateTimeFormatter weekDate() {
        return Constants.wwd;
    }

    public static DateTimeFormatter weekDateTime() {
        return Constants.wdt;
    }

    public static DateTimeFormatter weekDateTimeNoMillis() {
        return Constants.wdtx;
    }

    public static DateTimeFormatter basicDate() {
        return Constants.bd;
    }

    public static DateTimeFormatter basicTime() {
        return Constants.bt;
    }

    public static DateTimeFormatter basicTimeNoMillis() {
        return Constants.btx;
    }

    public static DateTimeFormatter basicTTime() {
        return Constants.btt;
    }

    public static DateTimeFormatter basicTTimeNoMillis() {
        return Constants.bttx;
    }

    public static DateTimeFormatter basicDateTime() {
        return Constants.bdt;
    }

    public static DateTimeFormatter basicDateTimeNoMillis() {
        return Constants.bdtx;
    }

    public static DateTimeFormatter basicOrdinalDate() {
        return Constants.bod;
    }

    public static DateTimeFormatter basicOrdinalDateTime() {
        return Constants.bodt;
    }

    public static DateTimeFormatter basicOrdinalDateTimeNoMillis() {
        return Constants.bodtx;
    }

    public static DateTimeFormatter basicWeekDate() {
        return Constants.bwd;
    }

    public static DateTimeFormatter basicWeekDateTime() {
        return Constants.bwdt;
    }

    public static DateTimeFormatter basicWeekDateTimeNoMillis() {
        return Constants.bwdtx;
    }

    public static DateTimeFormatter year() {
        return Constants.ye;
    }

    public static DateTimeFormatter yearMonth() {
        return Constants.ym;
    }

    public static DateTimeFormatter yearMonthDay() {
        return Constants.ymd;
    }

    public static DateTimeFormatter weekyear() {
        return Constants.we;
    }

    public static DateTimeFormatter weekyearWeek() {
        return Constants.ww;
    }

    public static DateTimeFormatter weekyearWeekDay() {
        return Constants.wwd;
    }

    public static DateTimeFormatter hour() {
        return Constants.hde;
    }

    public static DateTimeFormatter hourMinute() {
        return Constants.hm;
    }

    public static DateTimeFormatter hourMinuteSecond() {
        return Constants.hms;
    }

    public static DateTimeFormatter hourMinuteSecondMillis() {
        return Constants.hmsl;
    }

    public static DateTimeFormatter hourMinuteSecondFraction() {
        return Constants.hmsf;
    }

    public static DateTimeFormatter dateHour() {
        return Constants.dh;
    }

    public static DateTimeFormatter dateHourMinute() {
        return Constants.dhm;
    }

    public static DateTimeFormatter dateHourMinuteSecond() {
        return Constants.dhms;
    }

    public static DateTimeFormatter dateHourMinuteSecondMillis() {
        return Constants.dhmsl;
    }

    public static DateTimeFormatter dateHourMinuteSecondFraction() {
        return Constants.dhmsf;
    }

    static final class Constants {
        private static final DateTimeFormatter ye = Constants.yearElement();
        private static final DateTimeFormatter mye = Constants.monthElement();
        private static final DateTimeFormatter dme = Constants.dayOfMonthElement();
        private static final DateTimeFormatter we = Constants.weekyearElement();
        private static final DateTimeFormatter wwe = Constants.weekElement();
        private static final DateTimeFormatter dwe = Constants.dayOfWeekElement();
        private static final DateTimeFormatter dye = Constants.dayOfYearElement();
        private static final DateTimeFormatter hde = Constants.hourElement();
        private static final DateTimeFormatter mhe = Constants.minuteElement();
        private static final DateTimeFormatter sme = Constants.secondElement();
        private static final DateTimeFormatter fse = Constants.fractionElement();
        private static final DateTimeFormatter ze = Constants.offsetElement();
        private static final DateTimeFormatter lte = Constants.literalTElement();
        private static final DateTimeFormatter ym = Constants.yearMonth();
        private static final DateTimeFormatter ymd = Constants.yearMonthDay();
        private static final DateTimeFormatter ww = Constants.weekyearWeek();
        private static final DateTimeFormatter wwd = Constants.weekyearWeekDay();
        private static final DateTimeFormatter hm = Constants.hourMinute();
        private static final DateTimeFormatter hms = Constants.hourMinuteSecond();
        private static final DateTimeFormatter hmsl = Constants.hourMinuteSecondMillis();
        private static final DateTimeFormatter hmsf = Constants.hourMinuteSecondFraction();
        private static final DateTimeFormatter dh = Constants.dateHour();
        private static final DateTimeFormatter dhm = Constants.dateHourMinute();
        private static final DateTimeFormatter dhms = Constants.dateHourMinuteSecond();
        private static final DateTimeFormatter dhmsl = Constants.dateHourMinuteSecondMillis();
        private static final DateTimeFormatter dhmsf = Constants.dateHourMinuteSecondFraction();
        private static final DateTimeFormatter t = Constants.time();
        private static final DateTimeFormatter tx = Constants.timeNoMillis();
        private static final DateTimeFormatter tt = Constants.tTime();
        private static final DateTimeFormatter ttx = Constants.tTimeNoMillis();
        private static final DateTimeFormatter dt = Constants.dateTime();
        private static final DateTimeFormatter dtx = Constants.dateTimeNoMillis();
        private static final DateTimeFormatter wdt = Constants.weekDateTime();
        private static final DateTimeFormatter wdtx = Constants.weekDateTimeNoMillis();
        private static final DateTimeFormatter od = Constants.ordinalDate();
        private static final DateTimeFormatter odt = Constants.ordinalDateTime();
        private static final DateTimeFormatter odtx = Constants.ordinalDateTimeNoMillis();
        private static final DateTimeFormatter bd = Constants.basicDate();
        private static final DateTimeFormatter bt = Constants.basicTime();
        private static final DateTimeFormatter btx = Constants.basicTimeNoMillis();
        private static final DateTimeFormatter btt = Constants.basicTTime();
        private static final DateTimeFormatter bttx = Constants.basicTTimeNoMillis();
        private static final DateTimeFormatter bdt = Constants.basicDateTime();
        private static final DateTimeFormatter bdtx = Constants.basicDateTimeNoMillis();
        private static final DateTimeFormatter bod = Constants.basicOrdinalDate();
        private static final DateTimeFormatter bodt = Constants.basicOrdinalDateTime();
        private static final DateTimeFormatter bodtx = Constants.basicOrdinalDateTimeNoMillis();
        private static final DateTimeFormatter bwd = Constants.basicWeekDate();
        private static final DateTimeFormatter bwdt = Constants.basicWeekDateTime();
        private static final DateTimeFormatter bwdtx = Constants.basicWeekDateTimeNoMillis();
        private static final DateTimeFormatter dpe = Constants.dateElementParser();
        private static final DateTimeFormatter tpe = Constants.timeElementParser();
        private static final DateTimeFormatter dp = Constants.dateParser();
        private static final DateTimeFormatter ldp = Constants.localDateParser();
        private static final DateTimeFormatter tp = Constants.timeParser();
        private static final DateTimeFormatter ltp = Constants.localTimeParser();
        private static final DateTimeFormatter dtp = Constants.dateTimeParser();
        private static final DateTimeFormatter dotp = Constants.dateOptionalTimeParser();
        private static final DateTimeFormatter ldotp = Constants.localDateOptionalTimeParser();

        Constants() {
        }

        private static DateTimeFormatter dateParser() {
            if (dp == null) {
                DateTimeParser dateTimeParser = new DateTimeFormatterBuilder().appendLiteral('T').append(Constants.offsetElement()).toParser();
                return new DateTimeFormatterBuilder().append(Constants.dateElementParser()).appendOptional(dateTimeParser).toFormatter();
            }
            return dp;
        }

        private static DateTimeFormatter localDateParser() {
            if (ldp == null) {
                return Constants.dateElementParser().withZoneUTC();
            }
            return ldp;
        }

        private static DateTimeFormatter dateElementParser() {
            if (dpe == null) {
                return new DateTimeFormatterBuilder().append(null, new DateTimeParser[]{new DateTimeFormatterBuilder().append(Constants.yearElement()).appendOptional(new DateTimeFormatterBuilder().append(Constants.monthElement()).appendOptional(Constants.dayOfMonthElement().getParser()).toParser()).toParser(), new DateTimeFormatterBuilder().append(Constants.weekyearElement()).append(Constants.weekElement()).appendOptional(Constants.dayOfWeekElement().getParser()).toParser(), new DateTimeFormatterBuilder().append(Constants.yearElement()).append(Constants.dayOfYearElement()).toParser()}).toFormatter();
            }
            return dpe;
        }

        private static DateTimeFormatter timeParser() {
            if (tp == null) {
                return new DateTimeFormatterBuilder().appendOptional(Constants.literalTElement().getParser()).append(Constants.timeElementParser()).appendOptional(Constants.offsetElement().getParser()).toFormatter();
            }
            return tp;
        }

        private static DateTimeFormatter localTimeParser() {
            if (ltp == null) {
                return new DateTimeFormatterBuilder().appendOptional(Constants.literalTElement().getParser()).append(Constants.timeElementParser()).toFormatter().withZoneUTC();
            }
            return ltp;
        }

        private static DateTimeFormatter timeElementParser() {
            if (tpe == null) {
                DateTimeParser dateTimeParser = new DateTimeFormatterBuilder().append(null, new DateTimeParser[]{new DateTimeFormatterBuilder().appendLiteral('.').toParser(), new DateTimeFormatterBuilder().appendLiteral(',').toParser()}).toParser();
                return new DateTimeFormatterBuilder().append(Constants.hourElement()).append(null, new DateTimeParser[]{new DateTimeFormatterBuilder().append(Constants.minuteElement()).append(null, new DateTimeParser[]{new DateTimeFormatterBuilder().append(Constants.secondElement()).appendOptional(new DateTimeFormatterBuilder().append(dateTimeParser).appendFractionOfSecond(1, 9).toParser()).toParser(), new DateTimeFormatterBuilder().append(dateTimeParser).appendFractionOfMinute(1, 9).toParser(), null}).toParser(), new DateTimeFormatterBuilder().append(dateTimeParser).appendFractionOfHour(1, 9).toParser(), null}).toFormatter();
            }
            return tpe;
        }

        private static DateTimeFormatter dateTimeParser() {
            if (dtp == null) {
                DateTimeParser dateTimeParser = new DateTimeFormatterBuilder().appendLiteral('T').append(Constants.timeElementParser()).appendOptional(Constants.offsetElement().getParser()).toParser();
                return new DateTimeFormatterBuilder().append(null, new DateTimeParser[]{dateTimeParser, Constants.dateOptionalTimeParser().getParser()}).toFormatter();
            }
            return dtp;
        }

        private static DateTimeFormatter dateOptionalTimeParser() {
            if (dotp == null) {
                DateTimeParser dateTimeParser = new DateTimeFormatterBuilder().appendLiteral('T').appendOptional(Constants.timeElementParser().getParser()).appendOptional(Constants.offsetElement().getParser()).toParser();
                return new DateTimeFormatterBuilder().append(Constants.dateElementParser()).appendOptional(dateTimeParser).toFormatter();
            }
            return dotp;
        }

        private static DateTimeFormatter localDateOptionalTimeParser() {
            if (ldotp == null) {
                DateTimeParser dateTimeParser = new DateTimeFormatterBuilder().appendLiteral('T').append(Constants.timeElementParser()).toParser();
                return new DateTimeFormatterBuilder().append(Constants.dateElementParser()).appendOptional(dateTimeParser).toFormatter().withZoneUTC();
            }
            return ldotp;
        }

        private static DateTimeFormatter time() {
            if (t == null) {
                return new DateTimeFormatterBuilder().append(Constants.hourMinuteSecondFraction()).append(Constants.offsetElement()).toFormatter();
            }
            return t;
        }

        private static DateTimeFormatter timeNoMillis() {
            if (tx == null) {
                return new DateTimeFormatterBuilder().append(Constants.hourMinuteSecond()).append(Constants.offsetElement()).toFormatter();
            }
            return tx;
        }

        private static DateTimeFormatter tTime() {
            if (tt == null) {
                return new DateTimeFormatterBuilder().append(Constants.literalTElement()).append(Constants.time()).toFormatter();
            }
            return tt;
        }

        private static DateTimeFormatter tTimeNoMillis() {
            if (ttx == null) {
                return new DateTimeFormatterBuilder().append(Constants.literalTElement()).append(Constants.timeNoMillis()).toFormatter();
            }
            return ttx;
        }

        private static DateTimeFormatter dateTime() {
            if (dt == null) {
                return new DateTimeFormatterBuilder().append(ISODateTimeFormat.date()).append(Constants.tTime()).toFormatter();
            }
            return dt;
        }

        private static DateTimeFormatter dateTimeNoMillis() {
            if (dtx == null) {
                return new DateTimeFormatterBuilder().append(ISODateTimeFormat.date()).append(Constants.tTimeNoMillis()).toFormatter();
            }
            return dtx;
        }

        private static DateTimeFormatter ordinalDate() {
            if (od == null) {
                return new DateTimeFormatterBuilder().append(Constants.yearElement()).append(Constants.dayOfYearElement()).toFormatter();
            }
            return od;
        }

        private static DateTimeFormatter ordinalDateTime() {
            if (odt == null) {
                return new DateTimeFormatterBuilder().append(Constants.ordinalDate()).append(Constants.tTime()).toFormatter();
            }
            return odt;
        }

        private static DateTimeFormatter ordinalDateTimeNoMillis() {
            if (odtx == null) {
                return new DateTimeFormatterBuilder().append(Constants.ordinalDate()).append(Constants.tTimeNoMillis()).toFormatter();
            }
            return odtx;
        }

        private static DateTimeFormatter weekDateTime() {
            if (wdt == null) {
                return new DateTimeFormatterBuilder().append(ISODateTimeFormat.weekDate()).append(Constants.tTime()).toFormatter();
            }
            return wdt;
        }

        private static DateTimeFormatter weekDateTimeNoMillis() {
            if (wdtx == null) {
                return new DateTimeFormatterBuilder().append(ISODateTimeFormat.weekDate()).append(Constants.tTimeNoMillis()).toFormatter();
            }
            return wdtx;
        }

        private static DateTimeFormatter basicDate() {
            if (bd == null) {
                return new DateTimeFormatterBuilder().appendYear(4, 4).appendFixedDecimal(DateTimeFieldType.monthOfYear(), 2).appendFixedDecimal(DateTimeFieldType.dayOfMonth(), 2).toFormatter();
            }
            return bd;
        }

        private static DateTimeFormatter basicTime() {
            if (bt == null) {
                return new DateTimeFormatterBuilder().appendFixedDecimal(DateTimeFieldType.hourOfDay(), 2).appendFixedDecimal(DateTimeFieldType.minuteOfHour(), 2).appendFixedDecimal(DateTimeFieldType.secondOfMinute(), 2).appendLiteral('.').appendFractionOfSecond(3, 9).appendTimeZoneOffset("Z", false, 2, 2).toFormatter();
            }
            return bt;
        }

        private static DateTimeFormatter basicTimeNoMillis() {
            if (btx == null) {
                return new DateTimeFormatterBuilder().appendFixedDecimal(DateTimeFieldType.hourOfDay(), 2).appendFixedDecimal(DateTimeFieldType.minuteOfHour(), 2).appendFixedDecimal(DateTimeFieldType.secondOfMinute(), 2).appendTimeZoneOffset("Z", false, 2, 2).toFormatter();
            }
            return btx;
        }

        private static DateTimeFormatter basicTTime() {
            if (btt == null) {
                return new DateTimeFormatterBuilder().append(Constants.literalTElement()).append(Constants.basicTime()).toFormatter();
            }
            return btt;
        }

        private static DateTimeFormatter basicTTimeNoMillis() {
            if (bttx == null) {
                return new DateTimeFormatterBuilder().append(Constants.literalTElement()).append(Constants.basicTimeNoMillis()).toFormatter();
            }
            return bttx;
        }

        private static DateTimeFormatter basicDateTime() {
            if (bdt == null) {
                return new DateTimeFormatterBuilder().append(Constants.basicDate()).append(Constants.basicTTime()).toFormatter();
            }
            return bdt;
        }

        private static DateTimeFormatter basicDateTimeNoMillis() {
            if (bdtx == null) {
                return new DateTimeFormatterBuilder().append(Constants.basicDate()).append(Constants.basicTTimeNoMillis()).toFormatter();
            }
            return bdtx;
        }

        private static DateTimeFormatter basicOrdinalDate() {
            if (bod == null) {
                return new DateTimeFormatterBuilder().appendYear(4, 4).appendFixedDecimal(DateTimeFieldType.dayOfYear(), 3).toFormatter();
            }
            return bod;
        }

        private static DateTimeFormatter basicOrdinalDateTime() {
            if (bodt == null) {
                return new DateTimeFormatterBuilder().append(Constants.basicOrdinalDate()).append(Constants.basicTTime()).toFormatter();
            }
            return bodt;
        }

        private static DateTimeFormatter basicOrdinalDateTimeNoMillis() {
            if (bodtx == null) {
                return new DateTimeFormatterBuilder().append(Constants.basicOrdinalDate()).append(Constants.basicTTimeNoMillis()).toFormatter();
            }
            return bodtx;
        }

        private static DateTimeFormatter basicWeekDate() {
            if (bwd == null) {
                return new DateTimeFormatterBuilder().appendWeekyear(4, 4).appendLiteral('W').appendFixedDecimal(DateTimeFieldType.weekOfWeekyear(), 2).appendFixedDecimal(DateTimeFieldType.dayOfWeek(), 1).toFormatter();
            }
            return bwd;
        }

        private static DateTimeFormatter basicWeekDateTime() {
            if (bwdt == null) {
                return new DateTimeFormatterBuilder().append(Constants.basicWeekDate()).append(Constants.basicTTime()).toFormatter();
            }
            return bwdt;
        }

        private static DateTimeFormatter basicWeekDateTimeNoMillis() {
            if (bwdtx == null) {
                return new DateTimeFormatterBuilder().append(Constants.basicWeekDate()).append(Constants.basicTTimeNoMillis()).toFormatter();
            }
            return bwdtx;
        }

        private static DateTimeFormatter yearMonth() {
            if (ym == null) {
                return new DateTimeFormatterBuilder().append(Constants.yearElement()).append(Constants.monthElement()).toFormatter();
            }
            return ym;
        }

        private static DateTimeFormatter yearMonthDay() {
            if (ymd == null) {
                return new DateTimeFormatterBuilder().append(Constants.yearElement()).append(Constants.monthElement()).append(Constants.dayOfMonthElement()).toFormatter();
            }
            return ymd;
        }

        private static DateTimeFormatter weekyearWeek() {
            if (ww == null) {
                return new DateTimeFormatterBuilder().append(Constants.weekyearElement()).append(Constants.weekElement()).toFormatter();
            }
            return ww;
        }

        private static DateTimeFormatter weekyearWeekDay() {
            if (wwd == null) {
                return new DateTimeFormatterBuilder().append(Constants.weekyearElement()).append(Constants.weekElement()).append(Constants.dayOfWeekElement()).toFormatter();
            }
            return wwd;
        }

        private static DateTimeFormatter hourMinute() {
            if (hm == null) {
                return new DateTimeFormatterBuilder().append(Constants.hourElement()).append(Constants.minuteElement()).toFormatter();
            }
            return hm;
        }

        private static DateTimeFormatter hourMinuteSecond() {
            if (hms == null) {
                return new DateTimeFormatterBuilder().append(Constants.hourElement()).append(Constants.minuteElement()).append(Constants.secondElement()).toFormatter();
            }
            return hms;
        }

        private static DateTimeFormatter hourMinuteSecondMillis() {
            if (hmsl == null) {
                return new DateTimeFormatterBuilder().append(Constants.hourElement()).append(Constants.minuteElement()).append(Constants.secondElement()).appendLiteral('.').appendFractionOfSecond(3, 3).toFormatter();
            }
            return hmsl;
        }

        private static DateTimeFormatter hourMinuteSecondFraction() {
            if (hmsf == null) {
                return new DateTimeFormatterBuilder().append(Constants.hourElement()).append(Constants.minuteElement()).append(Constants.secondElement()).append(Constants.fractionElement()).toFormatter();
            }
            return hmsf;
        }

        private static DateTimeFormatter dateHour() {
            if (dh == null) {
                return new DateTimeFormatterBuilder().append(ISODateTimeFormat.date()).append(Constants.literalTElement()).append(ISODateTimeFormat.hour()).toFormatter();
            }
            return dh;
        }

        private static DateTimeFormatter dateHourMinute() {
            if (dhm == null) {
                return new DateTimeFormatterBuilder().append(ISODateTimeFormat.date()).append(Constants.literalTElement()).append(Constants.hourMinute()).toFormatter();
            }
            return dhm;
        }

        private static DateTimeFormatter dateHourMinuteSecond() {
            if (dhms == null) {
                return new DateTimeFormatterBuilder().append(ISODateTimeFormat.date()).append(Constants.literalTElement()).append(Constants.hourMinuteSecond()).toFormatter();
            }
            return dhms;
        }

        private static DateTimeFormatter dateHourMinuteSecondMillis() {
            if (dhmsl == null) {
                return new DateTimeFormatterBuilder().append(ISODateTimeFormat.date()).append(Constants.literalTElement()).append(Constants.hourMinuteSecondMillis()).toFormatter();
            }
            return dhmsl;
        }

        private static DateTimeFormatter dateHourMinuteSecondFraction() {
            if (dhmsf == null) {
                return new DateTimeFormatterBuilder().append(ISODateTimeFormat.date()).append(Constants.literalTElement()).append(Constants.hourMinuteSecondFraction()).toFormatter();
            }
            return dhmsf;
        }

        private static DateTimeFormatter yearElement() {
            if (ye == null) {
                return new DateTimeFormatterBuilder().appendYear(4, 9).toFormatter();
            }
            return ye;
        }

        private static DateTimeFormatter monthElement() {
            if (mye == null) {
                return new DateTimeFormatterBuilder().appendLiteral('-').appendMonthOfYear(2).toFormatter();
            }
            return mye;
        }

        private static DateTimeFormatter dayOfMonthElement() {
            if (dme == null) {
                return new DateTimeFormatterBuilder().appendLiteral('-').appendDayOfMonth(2).toFormatter();
            }
            return dme;
        }

        private static DateTimeFormatter weekyearElement() {
            if (we == null) {
                return new DateTimeFormatterBuilder().appendWeekyear(4, 9).toFormatter();
            }
            return we;
        }

        private static DateTimeFormatter weekElement() {
            if (wwe == null) {
                return new DateTimeFormatterBuilder().appendLiteral("-W").appendWeekOfWeekyear(2).toFormatter();
            }
            return wwe;
        }

        private static DateTimeFormatter dayOfWeekElement() {
            if (dwe == null) {
                return new DateTimeFormatterBuilder().appendLiteral('-').appendDayOfWeek(1).toFormatter();
            }
            return dwe;
        }

        private static DateTimeFormatter dayOfYearElement() {
            if (dye == null) {
                return new DateTimeFormatterBuilder().appendLiteral('-').appendDayOfYear(3).toFormatter();
            }
            return dye;
        }

        private static DateTimeFormatter literalTElement() {
            if (lte == null) {
                return new DateTimeFormatterBuilder().appendLiteral('T').toFormatter();
            }
            return lte;
        }

        private static DateTimeFormatter hourElement() {
            if (hde == null) {
                return new DateTimeFormatterBuilder().appendHourOfDay(2).toFormatter();
            }
            return hde;
        }

        private static DateTimeFormatter minuteElement() {
            if (mhe == null) {
                return new DateTimeFormatterBuilder().appendLiteral(':').appendMinuteOfHour(2).toFormatter();
            }
            return mhe;
        }

        private static DateTimeFormatter secondElement() {
            if (sme == null) {
                return new DateTimeFormatterBuilder().appendLiteral(':').appendSecondOfMinute(2).toFormatter();
            }
            return sme;
        }

        private static DateTimeFormatter fractionElement() {
            if (fse == null) {
                return new DateTimeFormatterBuilder().appendLiteral('.').appendFractionOfSecond(3, 9).toFormatter();
            }
            return fse;
        }

        private static DateTimeFormatter offsetElement() {
            if (ze == null) {
                return new DateTimeFormatterBuilder().appendTimeZoneOffset("Z", true, 2, 4).toFormatter();
            }
            return ze;
        }
    }
}

