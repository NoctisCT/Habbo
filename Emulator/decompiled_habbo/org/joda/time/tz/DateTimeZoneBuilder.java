/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.tz;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.tz.CachedDateTimeZone;
import org.joda.time.tz.FixedDateTimeZone;
import org.joda.time.tz.ZoneInfoLogger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class DateTimeZoneBuilder {
    private final ArrayList<RuleSet> iRuleSets = new ArrayList(10);

    public static DateTimeZone readFrom(InputStream inputStream, String string) throws IOException {
        if (inputStream instanceof DataInput) {
            return DateTimeZoneBuilder.readFrom((DataInput)((Object)inputStream), string);
        }
        return DateTimeZoneBuilder.readFrom(new DataInputStream(inputStream), string);
    }

    public static DateTimeZone readFrom(DataInput dataInput, String string) throws IOException {
        switch (dataInput.readUnsignedByte()) {
            case 70: {
                DateTimeZone dateTimeZone = new FixedDateTimeZone(string, dataInput.readUTF(), (int)DateTimeZoneBuilder.readMillis(dataInput), (int)DateTimeZoneBuilder.readMillis(dataInput));
                if (((DateTimeZone)dateTimeZone).equals(DateTimeZone.UTC)) {
                    dateTimeZone = DateTimeZone.UTC;
                }
                return dateTimeZone;
            }
            case 67: {
                return CachedDateTimeZone.forZone(PrecalculatedZone.readFrom(dataInput, string));
            }
            case 80: {
                return PrecalculatedZone.readFrom(dataInput, string);
            }
        }
        throw new IOException("Invalid encoding");
    }

    static void writeMillis(DataOutput dataOutput, long l) throws IOException {
        long l2;
        if (l % 1800000L == 0L && (l2 = l / 1800000L) << 58 >> 58 == l2) {
            dataOutput.writeByte((int)(l2 & 0x3FL));
            return;
        }
        if (l % 60000L == 0L && (l2 = l / 60000L) << 34 >> 34 == l2) {
            dataOutput.writeInt(0x40000000 | (int)(l2 & 0x3FFFFFFFL));
            return;
        }
        if (l % 1000L == 0L && (l2 = l / 1000L) << 26 >> 26 == l2) {
            dataOutput.writeByte(0x80 | (int)(l2 >> 32 & 0x3FL));
            dataOutput.writeInt((int)(l2 & 0xFFFFFFFFFFFFFFFFL));
            return;
        }
        dataOutput.writeByte(l < 0L ? 255 : 192);
        dataOutput.writeLong(l);
    }

    static long readMillis(DataInput dataInput) throws IOException {
        int n = dataInput.readUnsignedByte();
        switch (n >> 6) {
            default: {
                n = n << 26 >> 26;
                return (long)n * 1800000L;
            }
            case 1: {
                n = n << 26 >> 2;
                n |= dataInput.readUnsignedByte() << 16;
                n |= dataInput.readUnsignedByte() << 8;
                return (long)(n |= dataInput.readUnsignedByte()) * 60000L;
            }
            case 2: {
                long l = (long)n << 58 >> 26;
                l |= (long)(dataInput.readUnsignedByte() << 24);
                l |= (long)(dataInput.readUnsignedByte() << 16);
                l |= (long)(dataInput.readUnsignedByte() << 8);
                return (l |= (long)dataInput.readUnsignedByte()) * 1000L;
            }
            case 3: 
        }
        return dataInput.readLong();
    }

    private static DateTimeZone buildFixedZone(String string, String string2, int n, int n2) {
        if ("UTC".equals(string) && string.equals(string2) && n == 0 && n2 == 0) {
            return DateTimeZone.UTC;
        }
        return new FixedDateTimeZone(string, string2, n, n2);
    }

    public DateTimeZoneBuilder addCutover(int n, char c, int n2, int n3, int n4, boolean bl, int n5) {
        if (this.iRuleSets.size() > 0) {
            OfYear ofYear = new OfYear(c, n2, n3, n4, bl, n5);
            RuleSet ruleSet = this.iRuleSets.get(this.iRuleSets.size() - 1);
            ruleSet.setUpperLimit(n, ofYear);
        }
        this.iRuleSets.add(new RuleSet());
        return this;
    }

    public DateTimeZoneBuilder setStandardOffset(int n) {
        this.getLastRuleSet().setStandardOffset(n);
        return this;
    }

    public DateTimeZoneBuilder setFixedSavings(String string, int n) {
        this.getLastRuleSet().setFixedSavings(string, n);
        return this;
    }

    public DateTimeZoneBuilder addRecurringSavings(String string, int n, int n2, int n3, char c, int n4, int n5, int n6, boolean bl, int n7) {
        if (n2 <= n3) {
            OfYear ofYear = new OfYear(c, n4, n5, n6, bl, n7);
            Recurrence recurrence = new Recurrence(ofYear, string, n);
            Rule rule = new Rule(recurrence, n2, n3);
            this.getLastRuleSet().addRule(rule);
        }
        return this;
    }

    private RuleSet getLastRuleSet() {
        if (this.iRuleSets.size() == 0) {
            this.addCutover(Integer.MIN_VALUE, 'w', 1, 1, 0, false, 0);
        }
        return this.iRuleSets.get(this.iRuleSets.size() - 1);
    }

    public DateTimeZone toDateTimeZone(String string, boolean bl) {
        if (string == null) {
            throw new IllegalArgumentException();
        }
        ArrayList<Transition> arrayList = new ArrayList<Transition>();
        DSTZone dSTZone = null;
        long l = Long.MIN_VALUE;
        int n = 0;
        int n2 = this.iRuleSets.size();
        for (int i = 0; i < n2; ++i) {
            RuleSet ruleSet = this.iRuleSets.get(i);
            Transition transition = ruleSet.firstTransition(l);
            if (transition == null) continue;
            this.addTransition(arrayList, transition);
            l = transition.getMillis();
            n = transition.getSaveMillis();
            ruleSet = new RuleSet(ruleSet);
            while (!((transition = ruleSet.nextTransition(l, n)) == null || this.addTransition(arrayList, transition) && dSTZone != null)) {
                l = transition.getMillis();
                n = transition.getSaveMillis();
                if (dSTZone != null || i != n2 - 1) continue;
                dSTZone = ruleSet.buildTailZone(string);
            }
            l = ruleSet.getUpperLimit(n);
        }
        if (arrayList.size() == 0) {
            if (dSTZone != null) {
                return dSTZone;
            }
            return DateTimeZoneBuilder.buildFixedZone(string, "UTC", 0, 0);
        }
        if (arrayList.size() == 1 && dSTZone == null) {
            Transition transition = (Transition)arrayList.get(0);
            return DateTimeZoneBuilder.buildFixedZone(string, transition.getNameKey(), transition.getWallOffset(), transition.getStandardOffset());
        }
        PrecalculatedZone precalculatedZone = PrecalculatedZone.create(string, bl, arrayList, dSTZone);
        if (precalculatedZone.isCachable()) {
            return CachedDateTimeZone.forZone(precalculatedZone);
        }
        return precalculatedZone;
    }

    private boolean addTransition(ArrayList<Transition> arrayList, Transition transition) {
        int n = arrayList.size();
        if (n == 0) {
            arrayList.add(transition);
            return true;
        }
        Transition transition2 = arrayList.get(n - 1);
        if (!transition.isTransitionFrom(transition2)) {
            return false;
        }
        int n2 = 0;
        if (n >= 2) {
            n2 = arrayList.get(n - 2).getWallOffset();
        }
        int n3 = transition2.getWallOffset();
        long l = transition2.getMillis() + (long)n2;
        long l2 = transition.getMillis() + (long)n3;
        if (l2 != l) {
            arrayList.add(transition);
            return true;
        }
        Transition transition3 = arrayList.remove(n - 1);
        Transition transition4 = transition.withMillis(transition3.getMillis());
        return this.addTransition(arrayList, transition4);
    }

    public void writeTo(String string, OutputStream outputStream) throws IOException {
        if (outputStream instanceof DataOutput) {
            this.writeTo(string, (DataOutput)((Object)outputStream));
        } else {
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            this.writeTo(string, dataOutputStream);
            dataOutputStream.flush();
        }
    }

    public void writeTo(String string, DataOutput dataOutput) throws IOException {
        DateTimeZone dateTimeZone = this.toDateTimeZone(string, false);
        if (dateTimeZone instanceof FixedDateTimeZone) {
            dataOutput.writeByte(70);
            dataOutput.writeUTF(dateTimeZone.getNameKey(0L));
            DateTimeZoneBuilder.writeMillis(dataOutput, dateTimeZone.getOffset(0L));
            DateTimeZoneBuilder.writeMillis(dataOutput, dateTimeZone.getStandardOffset(0L));
        } else {
            if (dateTimeZone instanceof CachedDateTimeZone) {
                dataOutput.writeByte(67);
                dateTimeZone = ((CachedDateTimeZone)dateTimeZone).getUncachedZone();
            } else {
                dataOutput.writeByte(80);
            }
            ((PrecalculatedZone)dateTimeZone).writeTo(dataOutput);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class PrecalculatedZone
    extends DateTimeZone {
        private static final long serialVersionUID = 7811976468055766265L;
        private final long[] iTransitions;
        private final int[] iWallOffsets;
        private final int[] iStandardOffsets;
        private final String[] iNameKeys;
        private final DSTZone iTailZone;

        static PrecalculatedZone readFrom(DataInput dataInput, String string) throws IOException {
            int n;
            int n2 = dataInput.readUnsignedShort();
            String[] stringArray = new String[n2];
            for (n = 0; n < n2; ++n) {
                stringArray[n] = dataInput.readUTF();
            }
            n = dataInput.readInt();
            long[] lArray = new long[n];
            int[] nArray = new int[n];
            int[] nArray2 = new int[n];
            String[] stringArray2 = new String[n];
            for (int i = 0; i < n; ++i) {
                lArray[i] = DateTimeZoneBuilder.readMillis(dataInput);
                nArray[i] = (int)DateTimeZoneBuilder.readMillis(dataInput);
                nArray2[i] = (int)DateTimeZoneBuilder.readMillis(dataInput);
                try {
                    int n3 = n2 < 256 ? dataInput.readUnsignedByte() : dataInput.readUnsignedShort();
                    stringArray2[i] = stringArray[n3];
                    continue;
                }
                catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
                    throw new IOException("Invalid encoding");
                }
            }
            DSTZone dSTZone = null;
            if (dataInput.readBoolean()) {
                dSTZone = DSTZone.readFrom(dataInput, string);
            }
            return new PrecalculatedZone(string, lArray, nArray, nArray2, stringArray2, dSTZone);
        }

        static PrecalculatedZone create(String string, boolean bl, ArrayList<Transition> arrayList, DSTZone dSTZone) {
            String[][] stringArray;
            int n = arrayList.size();
            if (n == 0) {
                throw new IllegalArgumentException();
            }
            long[] lArray = new long[n];
            int[] nArray = new int[n];
            int[] nArray2 = new int[n];
            String[] stringArray2 = new String[n];
            String[][] stringArray3 = null;
            for (int i = 0; i < n; ++i) {
                stringArray = arrayList.get(i);
                if (!stringArray.isTransitionFrom((Transition)stringArray3)) {
                    throw new IllegalArgumentException(string);
                }
                lArray[i] = stringArray.getMillis();
                nArray[i] = stringArray.getWallOffset();
                nArray2[i] = stringArray.getStandardOffset();
                stringArray2[i] = stringArray.getNameKey();
                stringArray3 = stringArray;
            }
            String[] stringArray4 = new String[5];
            stringArray = new DateFormatSymbols(Locale.ENGLISH).getZoneStrings();
            for (int i = 0; i < stringArray.length; ++i) {
                String[] stringArray5 = stringArray[i];
                if (stringArray5 == null || stringArray5.length != 5 || !string.equals(stringArray5[0])) continue;
                stringArray4 = stringArray5;
            }
            ISOChronology iSOChronology = ISOChronology.getInstanceUTC();
            for (int i = 0; i < stringArray2.length - 1; ++i) {
                String string2 = stringArray2[i];
                String string3 = stringArray2[i + 1];
                long l = nArray[i];
                long l2 = nArray[i + 1];
                long l3 = nArray2[i];
                long l4 = nArray2[i + 1];
                Period period = new Period(lArray[i], lArray[i + 1], PeriodType.yearMonthDay(), iSOChronology);
                if (l == l2 || l3 != l4 || !string2.equals(string3) || period.getYears() != 0 || period.getMonths() <= 4 || period.getMonths() >= 8 || !string2.equals(stringArray4[2]) || !string2.equals(stringArray4[4])) continue;
                if (ZoneInfoLogger.verbose()) {
                    System.out.println("Fixing duplicate name key - " + string3);
                    System.out.println("     - " + new DateTime(lArray[i], (Chronology)iSOChronology) + " - " + new DateTime(lArray[i + 1], (Chronology)iSOChronology));
                }
                if (l > l2) {
                    stringArray2[i] = (string2 + "-Summer").intern();
                    continue;
                }
                if (l >= l2) continue;
                stringArray2[i + 1] = (string3 + "-Summer").intern();
                ++i;
            }
            if (dSTZone != null && dSTZone.iStartRecurrence.getNameKey().equals(dSTZone.iEndRecurrence.getNameKey())) {
                if (ZoneInfoLogger.verbose()) {
                    System.out.println("Fixing duplicate recurrent name key - " + dSTZone.iStartRecurrence.getNameKey());
                }
                dSTZone = dSTZone.iStartRecurrence.getSaveMillis() > 0 ? new DSTZone(dSTZone.getID(), dSTZone.iStandardOffset, dSTZone.iStartRecurrence.renameAppend("-Summer"), dSTZone.iEndRecurrence) : new DSTZone(dSTZone.getID(), dSTZone.iStandardOffset, dSTZone.iStartRecurrence, dSTZone.iEndRecurrence.renameAppend("-Summer"));
            }
            return new PrecalculatedZone(bl ? string : "", lArray, nArray, nArray2, stringArray2, dSTZone);
        }

        private PrecalculatedZone(String string, long[] lArray, int[] nArray, int[] nArray2, String[] stringArray, DSTZone dSTZone) {
            super(string);
            this.iTransitions = lArray;
            this.iWallOffsets = nArray;
            this.iStandardOffsets = nArray2;
            this.iNameKeys = stringArray;
            this.iTailZone = dSTZone;
        }

        @Override
        public String getNameKey(long l) {
            long[] lArray = this.iTransitions;
            int n = Arrays.binarySearch(lArray, l);
            if (n >= 0) {
                return this.iNameKeys[n];
            }
            if ((n ^= 0xFFFFFFFF) < lArray.length) {
                if (n > 0) {
                    return this.iNameKeys[n - 1];
                }
                return "UTC";
            }
            if (this.iTailZone == null) {
                return this.iNameKeys[n - 1];
            }
            return this.iTailZone.getNameKey(l);
        }

        @Override
        public int getOffset(long l) {
            long[] lArray = this.iTransitions;
            int n = Arrays.binarySearch(lArray, l);
            if (n >= 0) {
                return this.iWallOffsets[n];
            }
            if ((n ^= 0xFFFFFFFF) < lArray.length) {
                if (n > 0) {
                    return this.iWallOffsets[n - 1];
                }
                return 0;
            }
            if (this.iTailZone == null) {
                return this.iWallOffsets[n - 1];
            }
            return this.iTailZone.getOffset(l);
        }

        @Override
        public int getStandardOffset(long l) {
            long[] lArray = this.iTransitions;
            int n = Arrays.binarySearch(lArray, l);
            if (n >= 0) {
                return this.iStandardOffsets[n];
            }
            if ((n ^= 0xFFFFFFFF) < lArray.length) {
                if (n > 0) {
                    return this.iStandardOffsets[n - 1];
                }
                return 0;
            }
            if (this.iTailZone == null) {
                return this.iStandardOffsets[n - 1];
            }
            return this.iTailZone.getStandardOffset(l);
        }

        @Override
        public boolean isFixed() {
            return false;
        }

        @Override
        public long nextTransition(long l) {
            long[] lArray = this.iTransitions;
            int n = Arrays.binarySearch(lArray, l);
            int n2 = n = n >= 0 ? n + 1 : ~n;
            if (n < lArray.length) {
                return lArray[n];
            }
            if (this.iTailZone == null) {
                return l;
            }
            long l2 = lArray[lArray.length - 1];
            if (l < l2) {
                l = l2;
            }
            return this.iTailZone.nextTransition(l);
        }

        @Override
        public long previousTransition(long l) {
            long l2;
            long[] lArray = this.iTransitions;
            int n = Arrays.binarySearch(lArray, l);
            if (n >= 0) {
                if (l > Long.MIN_VALUE) {
                    return l - 1L;
                }
                return l;
            }
            if ((n ^= 0xFFFFFFFF) < lArray.length) {
                long l3;
                if (n > 0 && (l3 = lArray[n - 1]) > Long.MIN_VALUE) {
                    return l3 - 1L;
                }
                return l;
            }
            if (this.iTailZone != null && (l2 = this.iTailZone.previousTransition(l)) < l) {
                return l2;
            }
            l2 = lArray[n - 1];
            if (l2 > Long.MIN_VALUE) {
                return l2 - 1L;
            }
            return l;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object instanceof PrecalculatedZone) {
                PrecalculatedZone precalculatedZone = (PrecalculatedZone)object;
                return this.getID().equals(precalculatedZone.getID()) && Arrays.equals(this.iTransitions, precalculatedZone.iTransitions) && Arrays.equals(this.iNameKeys, precalculatedZone.iNameKeys) && Arrays.equals(this.iWallOffsets, precalculatedZone.iWallOffsets) && Arrays.equals(this.iStandardOffsets, precalculatedZone.iStandardOffsets) && (this.iTailZone == null ? null == precalculatedZone.iTailZone : this.iTailZone.equals(precalculatedZone.iTailZone));
            }
            return false;
        }

        public void writeTo(DataOutput dataOutput) throws IOException {
            int n;
            int n2 = this.iTransitions.length;
            HashSet<String> hashSet = new HashSet<String>();
            for (n = 0; n < n2; ++n) {
                hashSet.add(this.iNameKeys[n]);
            }
            n = hashSet.size();
            if (n > 65535) {
                throw new UnsupportedOperationException("String pool is too large");
            }
            String[] stringArray = new String[n];
            Iterator iterator = hashSet.iterator();
            int n3 = 0;
            while (iterator.hasNext()) {
                stringArray[n3] = (String)iterator.next();
                ++n3;
            }
            dataOutput.writeShort(n);
            for (n3 = 0; n3 < n; ++n3) {
                dataOutput.writeUTF(stringArray[n3]);
            }
            dataOutput.writeInt(n2);
            block3: for (n3 = 0; n3 < n2; ++n3) {
                DateTimeZoneBuilder.writeMillis(dataOutput, this.iTransitions[n3]);
                DateTimeZoneBuilder.writeMillis(dataOutput, this.iWallOffsets[n3]);
                DateTimeZoneBuilder.writeMillis(dataOutput, this.iStandardOffsets[n3]);
                String string = this.iNameKeys[n3];
                for (int i = 0; i < n; ++i) {
                    if (!stringArray[i].equals(string)) continue;
                    if (n < 256) {
                        dataOutput.writeByte(i);
                        continue block3;
                    }
                    dataOutput.writeShort(i);
                    continue block3;
                }
            }
            dataOutput.writeBoolean(this.iTailZone != null);
            if (this.iTailZone != null) {
                this.iTailZone.writeTo(dataOutput);
            }
        }

        public boolean isCachable() {
            if (this.iTailZone != null) {
                return true;
            }
            long[] lArray = this.iTransitions;
            if (lArray.length <= 1) {
                return false;
            }
            double d = 0.0;
            int n = 0;
            for (int i = 1; i < lArray.length; ++i) {
                long l = lArray[i] - lArray[i - 1];
                if (l >= 63158400000L) continue;
                d += (double)l;
                ++n;
            }
            if (n > 0) {
                double d2 = d / (double)n;
                if ((d2 /= 8.64E7) >= 25.0) {
                    return true;
                }
            }
            return false;
        }
    }

    private static final class DSTZone
    extends DateTimeZone {
        private static final long serialVersionUID = 6941492635554961361L;
        final int iStandardOffset;
        final Recurrence iStartRecurrence;
        final Recurrence iEndRecurrence;

        static DSTZone readFrom(DataInput dataInput, String string) throws IOException {
            return new DSTZone(string, (int)DateTimeZoneBuilder.readMillis(dataInput), Recurrence.readFrom(dataInput), Recurrence.readFrom(dataInput));
        }

        DSTZone(String string, int n, Recurrence recurrence, Recurrence recurrence2) {
            super(string);
            this.iStandardOffset = n;
            this.iStartRecurrence = recurrence;
            this.iEndRecurrence = recurrence2;
        }

        public String getNameKey(long l) {
            return this.findMatchingRecurrence(l).getNameKey();
        }

        public int getOffset(long l) {
            return this.iStandardOffset + this.findMatchingRecurrence(l).getSaveMillis();
        }

        public int getStandardOffset(long l) {
            return this.iStandardOffset;
        }

        public boolean isFixed() {
            return false;
        }

        public long nextTransition(long l) {
            long l2;
            long l3;
            int n = this.iStandardOffset;
            Recurrence recurrence = this.iStartRecurrence;
            Recurrence recurrence2 = this.iEndRecurrence;
            try {
                l3 = recurrence.next(l, n, recurrence2.getSaveMillis());
                if (l > 0L && l3 < 0L) {
                    l3 = l;
                }
            }
            catch (IllegalArgumentException illegalArgumentException) {
                l3 = l;
            }
            catch (ArithmeticException arithmeticException) {
                l3 = l;
            }
            try {
                l2 = recurrence2.next(l, n, recurrence.getSaveMillis());
                if (l > 0L && l2 < 0L) {
                    l2 = l;
                }
            }
            catch (IllegalArgumentException illegalArgumentException) {
                l2 = l;
            }
            catch (ArithmeticException arithmeticException) {
                l2 = l;
            }
            return l3 > l2 ? l2 : l3;
        }

        public long previousTransition(long l) {
            long l2;
            long l3;
            ++l;
            int n = this.iStandardOffset;
            Recurrence recurrence = this.iStartRecurrence;
            Recurrence recurrence2 = this.iEndRecurrence;
            try {
                l3 = recurrence.previous(l, n, recurrence2.getSaveMillis());
                if (l < 0L && l3 > 0L) {
                    l3 = l;
                }
            }
            catch (IllegalArgumentException illegalArgumentException) {
                l3 = l;
            }
            catch (ArithmeticException arithmeticException) {
                l3 = l;
            }
            try {
                l2 = recurrence2.previous(l, n, recurrence.getSaveMillis());
                if (l < 0L && l2 > 0L) {
                    l2 = l;
                }
            }
            catch (IllegalArgumentException illegalArgumentException) {
                l2 = l;
            }
            catch (ArithmeticException arithmeticException) {
                l2 = l;
            }
            return (l3 > l2 ? l3 : l2) - 1L;
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object instanceof DSTZone) {
                DSTZone dSTZone = (DSTZone)object;
                return this.getID().equals(dSTZone.getID()) && this.iStandardOffset == dSTZone.iStandardOffset && this.iStartRecurrence.equals(dSTZone.iStartRecurrence) && this.iEndRecurrence.equals(dSTZone.iEndRecurrence);
            }
            return false;
        }

        public void writeTo(DataOutput dataOutput) throws IOException {
            DateTimeZoneBuilder.writeMillis(dataOutput, this.iStandardOffset);
            this.iStartRecurrence.writeTo(dataOutput);
            this.iEndRecurrence.writeTo(dataOutput);
        }

        private Recurrence findMatchingRecurrence(long l) {
            long l2;
            long l3;
            int n = this.iStandardOffset;
            Recurrence recurrence = this.iStartRecurrence;
            Recurrence recurrence2 = this.iEndRecurrence;
            try {
                l3 = recurrence.next(l, n, recurrence2.getSaveMillis());
            }
            catch (IllegalArgumentException illegalArgumentException) {
                l3 = l;
            }
            catch (ArithmeticException arithmeticException) {
                l3 = l;
            }
            try {
                l2 = recurrence2.next(l, n, recurrence.getSaveMillis());
            }
            catch (IllegalArgumentException illegalArgumentException) {
                l2 = l;
            }
            catch (ArithmeticException arithmeticException) {
                l2 = l;
            }
            return l3 > l2 ? recurrence : recurrence2;
        }
    }

    private static final class RuleSet {
        private static final int YEAR_LIMIT;
        private int iStandardOffset;
        private ArrayList<Rule> iRules;
        private String iInitialNameKey;
        private int iInitialSaveMillis;
        private int iUpperYear;
        private OfYear iUpperOfYear;

        RuleSet() {
            this.iRules = new ArrayList(10);
            this.iUpperYear = Integer.MAX_VALUE;
        }

        RuleSet(RuleSet ruleSet) {
            this.iStandardOffset = ruleSet.iStandardOffset;
            this.iRules = new ArrayList<Rule>(ruleSet.iRules);
            this.iInitialNameKey = ruleSet.iInitialNameKey;
            this.iInitialSaveMillis = ruleSet.iInitialSaveMillis;
            this.iUpperYear = ruleSet.iUpperYear;
            this.iUpperOfYear = ruleSet.iUpperOfYear;
        }

        public int getStandardOffset() {
            return this.iStandardOffset;
        }

        public void setStandardOffset(int n) {
            this.iStandardOffset = n;
        }

        public void setFixedSavings(String string, int n) {
            this.iInitialNameKey = string;
            this.iInitialSaveMillis = n;
        }

        public void addRule(Rule rule) {
            if (!this.iRules.contains(rule)) {
                this.iRules.add(rule);
            }
        }

        public void setUpperLimit(int n, OfYear ofYear) {
            this.iUpperYear = n;
            this.iUpperOfYear = ofYear;
        }

        public Transition firstTransition(long l) {
            Transition transition;
            if (this.iInitialNameKey != null) {
                return new Transition(l, this.iInitialNameKey, this.iStandardOffset + this.iInitialSaveMillis, this.iStandardOffset);
            }
            ArrayList<Rule> arrayList = new ArrayList<Rule>(this.iRules);
            long l2 = Long.MIN_VALUE;
            int n = 0;
            Transition transition2 = null;
            while ((transition = this.nextTransition(l2, n)) != null) {
                l2 = transition.getMillis();
                if (l2 == l) {
                    transition2 = new Transition(l, transition);
                    break;
                }
                if (l2 > l) {
                    if (transition2 == null) {
                        for (Rule rule : arrayList) {
                            if (rule.getSaveMillis() != 0) continue;
                            transition2 = new Transition(l, rule, this.iStandardOffset);
                            break;
                        }
                    }
                    if (transition2 != null) break;
                    transition2 = new Transition(l, transition.getNameKey(), this.iStandardOffset, this.iStandardOffset);
                    break;
                }
                transition2 = new Transition(l, transition);
                n = transition.getSaveMillis();
            }
            this.iRules = arrayList;
            return transition2;
        }

        public Transition nextTransition(long l, int n) {
            long l2;
            ISOChronology iSOChronology = ISOChronology.getInstanceUTC();
            Rule rule = null;
            long l3 = Long.MAX_VALUE;
            Iterator<Rule> iterator = this.iRules.iterator();
            while (iterator.hasNext()) {
                Rule rule2 = iterator.next();
                long l4 = rule2.next(l, this.iStandardOffset, n);
                if (l4 <= l) {
                    iterator.remove();
                    continue;
                }
                if (l4 > l3) continue;
                rule = rule2;
                l3 = l4;
            }
            if (rule == null) {
                return null;
            }
            if (((Chronology)iSOChronology).year().get(l3) >= YEAR_LIMIT) {
                return null;
            }
            if (this.iUpperYear < Integer.MAX_VALUE && l3 >= (l2 = this.iUpperOfYear.setInstant(this.iUpperYear, this.iStandardOffset, n))) {
                return null;
            }
            return new Transition(l3, rule, this.iStandardOffset);
        }

        public long getUpperLimit(int n) {
            if (this.iUpperYear == Integer.MAX_VALUE) {
                return Long.MAX_VALUE;
            }
            return this.iUpperOfYear.setInstant(this.iUpperYear, this.iStandardOffset, n);
        }

        public DSTZone buildTailZone(String string) {
            if (this.iRules.size() == 2) {
                Rule rule = this.iRules.get(0);
                Rule rule2 = this.iRules.get(1);
                if (rule.getToYear() == Integer.MAX_VALUE && rule2.getToYear() == Integer.MAX_VALUE) {
                    return new DSTZone(string, this.iStandardOffset, rule.iRecurrence, rule2.iRecurrence);
                }
            }
            return null;
        }

        public String toString() {
            return this.iInitialNameKey + " initial: " + this.iInitialSaveMillis + " std: " + this.iStandardOffset + " upper: " + this.iUpperYear + " " + this.iUpperOfYear + " " + this.iRules;
        }

        static {
            long l = DateTimeUtils.currentTimeMillis();
            YEAR_LIMIT = ISOChronology.getInstanceUTC().year().get(l) + 100;
        }
    }

    private static final class Transition {
        private final long iMillis;
        private final String iNameKey;
        private final int iWallOffset;
        private final int iStandardOffset;

        Transition(long l, Transition transition) {
            this.iMillis = l;
            this.iNameKey = transition.iNameKey;
            this.iWallOffset = transition.iWallOffset;
            this.iStandardOffset = transition.iStandardOffset;
        }

        Transition(long l, Rule rule, int n) {
            this.iMillis = l;
            this.iNameKey = rule.getNameKey();
            this.iWallOffset = n + rule.getSaveMillis();
            this.iStandardOffset = n;
        }

        Transition(long l, String string, int n, int n2) {
            this.iMillis = l;
            this.iNameKey = string;
            this.iWallOffset = n;
            this.iStandardOffset = n2;
        }

        public long getMillis() {
            return this.iMillis;
        }

        public String getNameKey() {
            return this.iNameKey;
        }

        public int getWallOffset() {
            return this.iWallOffset;
        }

        public int getStandardOffset() {
            return this.iStandardOffset;
        }

        public int getSaveMillis() {
            return this.iWallOffset - this.iStandardOffset;
        }

        public Transition withMillis(long l) {
            return new Transition(l, this.iNameKey, this.iWallOffset, this.iStandardOffset);
        }

        public boolean isTransitionFrom(Transition transition) {
            if (transition == null) {
                return true;
            }
            return this.iMillis > transition.iMillis && (this.iWallOffset != transition.iWallOffset || this.iStandardOffset != transition.iStandardOffset || !this.iNameKey.equals(transition.iNameKey));
        }

        public String toString() {
            return new DateTime(this.iMillis, DateTimeZone.UTC) + " " + this.iStandardOffset + " " + this.iWallOffset;
        }
    }

    private static final class Rule {
        final Recurrence iRecurrence;
        final int iFromYear;
        final int iToYear;

        Rule(Recurrence recurrence, int n, int n2) {
            this.iRecurrence = recurrence;
            this.iFromYear = n;
            this.iToYear = n2;
        }

        public int getFromYear() {
            return this.iFromYear;
        }

        public int getToYear() {
            return this.iToYear;
        }

        public OfYear getOfYear() {
            return this.iRecurrence.getOfYear();
        }

        public String getNameKey() {
            return this.iRecurrence.getNameKey();
        }

        public int getSaveMillis() {
            return this.iRecurrence.getSaveMillis();
        }

        public long next(long l, int n, int n2) {
            long l2;
            ISOChronology iSOChronology = ISOChronology.getInstanceUTC();
            int n3 = n + n2;
            long l3 = l;
            int n4 = l == Long.MIN_VALUE ? Integer.MIN_VALUE : ((Chronology)iSOChronology).year().get(l + (long)n3);
            if (n4 < this.iFromYear) {
                l3 = ((Chronology)iSOChronology).year().set(0L, this.iFromYear) - (long)n3;
                --l3;
            }
            if ((l2 = this.iRecurrence.next(l3, n, n2)) > l && (n4 = ((Chronology)iSOChronology).year().get(l2 + (long)n3)) > this.iToYear) {
                l2 = l;
            }
            return l2;
        }

        public String toString() {
            return this.iFromYear + " to " + this.iToYear + " using " + this.iRecurrence;
        }
    }

    private static final class Recurrence {
        final OfYear iOfYear;
        final String iNameKey;
        final int iSaveMillis;

        static Recurrence readFrom(DataInput dataInput) throws IOException {
            return new Recurrence(OfYear.readFrom(dataInput), dataInput.readUTF(), (int)DateTimeZoneBuilder.readMillis(dataInput));
        }

        Recurrence(OfYear ofYear, String string, int n) {
            this.iOfYear = ofYear;
            this.iNameKey = string;
            this.iSaveMillis = n;
        }

        public OfYear getOfYear() {
            return this.iOfYear;
        }

        public long next(long l, int n, int n2) {
            return this.iOfYear.next(l, n, n2);
        }

        public long previous(long l, int n, int n2) {
            return this.iOfYear.previous(l, n, n2);
        }

        public String getNameKey() {
            return this.iNameKey;
        }

        public int getSaveMillis() {
            return this.iSaveMillis;
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object instanceof Recurrence) {
                Recurrence recurrence = (Recurrence)object;
                return this.iSaveMillis == recurrence.iSaveMillis && this.iNameKey.equals(recurrence.iNameKey) && this.iOfYear.equals(recurrence.iOfYear);
            }
            return false;
        }

        public void writeTo(DataOutput dataOutput) throws IOException {
            this.iOfYear.writeTo(dataOutput);
            dataOutput.writeUTF(this.iNameKey);
            DateTimeZoneBuilder.writeMillis(dataOutput, this.iSaveMillis);
        }

        Recurrence rename(String string) {
            return new Recurrence(this.iOfYear, string, this.iSaveMillis);
        }

        Recurrence renameAppend(String string) {
            return this.rename((this.iNameKey + string).intern());
        }

        public String toString() {
            return this.iOfYear + " named " + this.iNameKey + " at " + this.iSaveMillis;
        }
    }

    private static final class OfYear {
        final char iMode;
        final int iMonthOfYear;
        final int iDayOfMonth;
        final int iDayOfWeek;
        final boolean iAdvance;
        final int iMillisOfDay;

        static OfYear readFrom(DataInput dataInput) throws IOException {
            return new OfYear((char)dataInput.readUnsignedByte(), dataInput.readUnsignedByte(), dataInput.readByte(), dataInput.readUnsignedByte(), dataInput.readBoolean(), (int)DateTimeZoneBuilder.readMillis(dataInput));
        }

        OfYear(char c, int n, int n2, int n3, boolean bl, int n4) {
            if (c != 'u' && c != 'w' && c != 's') {
                throw new IllegalArgumentException("Unknown mode: " + c);
            }
            this.iMode = c;
            this.iMonthOfYear = n;
            this.iDayOfMonth = n2;
            this.iDayOfWeek = n3;
            this.iAdvance = bl;
            this.iMillisOfDay = n4;
        }

        public long setInstant(int n, int n2, int n3) {
            int n4 = this.iMode == 'w' ? n2 + n3 : (this.iMode == 's' ? n2 : 0);
            ISOChronology iSOChronology = ISOChronology.getInstanceUTC();
            long l = ((Chronology)iSOChronology).year().set(0L, n);
            l = ((Chronology)iSOChronology).monthOfYear().set(l, this.iMonthOfYear);
            l = ((Chronology)iSOChronology).millisOfDay().set(l, this.iMillisOfDay);
            l = this.setDayOfMonth(iSOChronology, l);
            if (this.iDayOfWeek != 0) {
                l = this.setDayOfWeek(iSOChronology, l);
            }
            return l - (long)n4;
        }

        public long next(long l, int n, int n2) {
            int n3 = this.iMode == 'w' ? n + n2 : (this.iMode == 's' ? n : 0);
            ISOChronology iSOChronology = ISOChronology.getInstanceUTC();
            long l2 = ((Chronology)iSOChronology).monthOfYear().set(l += (long)n3, this.iMonthOfYear);
            l2 = ((Chronology)iSOChronology).millisOfDay().set(l2, 0);
            l2 = ((Chronology)iSOChronology).millisOfDay().add(l2, this.iMillisOfDay);
            l2 = this.setDayOfMonthNext(iSOChronology, l2);
            if (this.iDayOfWeek == 0) {
                if (l2 <= l) {
                    l2 = ((Chronology)iSOChronology).year().add(l2, 1);
                    l2 = this.setDayOfMonthNext(iSOChronology, l2);
                }
            } else if ((l2 = this.setDayOfWeek(iSOChronology, l2)) <= l) {
                l2 = ((Chronology)iSOChronology).year().add(l2, 1);
                l2 = ((Chronology)iSOChronology).monthOfYear().set(l2, this.iMonthOfYear);
                l2 = this.setDayOfMonthNext(iSOChronology, l2);
                l2 = this.setDayOfWeek(iSOChronology, l2);
            }
            l2 = ((Chronology)iSOChronology).millisOfDay().set(l2, 0);
            l2 = ((Chronology)iSOChronology).millisOfDay().add(l2, this.iMillisOfDay);
            return l2 - (long)n3;
        }

        public long previous(long l, int n, int n2) {
            int n3 = this.iMode == 'w' ? n + n2 : (this.iMode == 's' ? n : 0);
            ISOChronology iSOChronology = ISOChronology.getInstanceUTC();
            long l2 = ((Chronology)iSOChronology).monthOfYear().set(l += (long)n3, this.iMonthOfYear);
            l2 = ((Chronology)iSOChronology).millisOfDay().set(l2, 0);
            l2 = ((Chronology)iSOChronology).millisOfDay().add(l2, this.iMillisOfDay);
            l2 = this.setDayOfMonthPrevious(iSOChronology, l2);
            if (this.iDayOfWeek == 0) {
                if (l2 >= l) {
                    l2 = ((Chronology)iSOChronology).year().add(l2, -1);
                    l2 = this.setDayOfMonthPrevious(iSOChronology, l2);
                }
            } else if ((l2 = this.setDayOfWeek(iSOChronology, l2)) >= l) {
                l2 = ((Chronology)iSOChronology).year().add(l2, -1);
                l2 = ((Chronology)iSOChronology).monthOfYear().set(l2, this.iMonthOfYear);
                l2 = this.setDayOfMonthPrevious(iSOChronology, l2);
                l2 = this.setDayOfWeek(iSOChronology, l2);
            }
            l2 = ((Chronology)iSOChronology).millisOfDay().set(l2, 0);
            l2 = ((Chronology)iSOChronology).millisOfDay().add(l2, this.iMillisOfDay);
            return l2 - (long)n3;
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object instanceof OfYear) {
                OfYear ofYear = (OfYear)object;
                return this.iMode == ofYear.iMode && this.iMonthOfYear == ofYear.iMonthOfYear && this.iDayOfMonth == ofYear.iDayOfMonth && this.iDayOfWeek == ofYear.iDayOfWeek && this.iAdvance == ofYear.iAdvance && this.iMillisOfDay == ofYear.iMillisOfDay;
            }
            return false;
        }

        public String toString() {
            return "[OfYear]\nMode: " + this.iMode + '\n' + "MonthOfYear: " + this.iMonthOfYear + '\n' + "DayOfMonth: " + this.iDayOfMonth + '\n' + "DayOfWeek: " + this.iDayOfWeek + '\n' + "AdvanceDayOfWeek: " + this.iAdvance + '\n' + "MillisOfDay: " + this.iMillisOfDay + '\n';
        }

        public void writeTo(DataOutput dataOutput) throws IOException {
            dataOutput.writeByte(this.iMode);
            dataOutput.writeByte(this.iMonthOfYear);
            dataOutput.writeByte(this.iDayOfMonth);
            dataOutput.writeByte(this.iDayOfWeek);
            dataOutput.writeBoolean(this.iAdvance);
            DateTimeZoneBuilder.writeMillis(dataOutput, this.iMillisOfDay);
        }

        private long setDayOfMonthNext(Chronology chronology, long l) {
            try {
                l = this.setDayOfMonth(chronology, l);
            }
            catch (IllegalArgumentException illegalArgumentException) {
                if (this.iMonthOfYear == 2 && this.iDayOfMonth == 29) {
                    while (!chronology.year().isLeap(l)) {
                        l = chronology.year().add(l, 1);
                    }
                    l = this.setDayOfMonth(chronology, l);
                }
                throw illegalArgumentException;
            }
            return l;
        }

        private long setDayOfMonthPrevious(Chronology chronology, long l) {
            try {
                l = this.setDayOfMonth(chronology, l);
            }
            catch (IllegalArgumentException illegalArgumentException) {
                if (this.iMonthOfYear == 2 && this.iDayOfMonth == 29) {
                    while (!chronology.year().isLeap(l)) {
                        l = chronology.year().add(l, -1);
                    }
                    l = this.setDayOfMonth(chronology, l);
                }
                throw illegalArgumentException;
            }
            return l;
        }

        private long setDayOfMonth(Chronology chronology, long l) {
            if (this.iDayOfMonth >= 0) {
                l = chronology.dayOfMonth().set(l, this.iDayOfMonth);
            } else {
                l = chronology.dayOfMonth().set(l, 1);
                l = chronology.monthOfYear().add(l, 1);
                l = chronology.dayOfMonth().add(l, this.iDayOfMonth);
            }
            return l;
        }

        private long setDayOfWeek(Chronology chronology, long l) {
            int n = chronology.dayOfWeek().get(l);
            int n2 = this.iDayOfWeek - n;
            if (n2 != 0) {
                if (this.iAdvance) {
                    if (n2 < 0) {
                        n2 += 7;
                    }
                } else if (n2 > 0) {
                    n2 -= 7;
                }
                l = chronology.dayOfWeek().add(l, n2);
            }
            return l;
        }
    }
}

