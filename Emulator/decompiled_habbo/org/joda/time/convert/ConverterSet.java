/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.convert;

import org.joda.time.convert.Converter;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class ConverterSet {
    private final Converter[] iConverters;
    private Entry[] iSelectEntries;

    ConverterSet(Converter[] converterArray) {
        this.iConverters = converterArray;
        this.iSelectEntries = new Entry[16];
    }

    Converter select(Class<?> clazz) throws IllegalStateException {
        int n;
        Entry entry;
        int n2;
        Entry[] entryArray = this.iSelectEntries;
        int n3 = entryArray.length;
        int n4 = n2 = clazz == null ? 0 : clazz.hashCode() & n3 - 1;
        while ((entry = entryArray[n2]) != null) {
            if (entry.iType == clazz) {
                return entry.iConverter;
            }
            if (++n2 < n3) continue;
            n2 = 0;
        }
        Converter converter = ConverterSet.selectSlow(this, clazz);
        entry = new Entry(clazz, converter);
        entryArray = (Entry[])entryArray.clone();
        entryArray[n2] = entry;
        for (n = 0; n < n3; ++n) {
            if (entryArray[n] != null) continue;
            this.iSelectEntries = entryArray;
            return converter;
        }
        n = n3 << 1;
        Entry[] entryArray2 = new Entry[n];
        for (int i = 0; i < n3; ++i) {
            entry = entryArray[i];
            clazz = entry.iType;
            int n5 = n2 = clazz == null ? 0 : clazz.hashCode() & n - 1;
            while (entryArray2[n2] != null) {
                if (++n2 < n) continue;
                n2 = 0;
            }
            entryArray2[n2] = entry;
        }
        this.iSelectEntries = entryArray2;
        return converter;
    }

    int size() {
        return this.iConverters.length;
    }

    void copyInto(Converter[] converterArray) {
        System.arraycopy(this.iConverters, 0, converterArray, 0, this.iConverters.length);
    }

    ConverterSet add(Converter converter, Converter[] converterArray) {
        Converter[] converterArray2 = this.iConverters;
        int n = converterArray2.length;
        for (int i = 0; i < n; ++i) {
            Converter converter2 = converterArray2[i];
            if (converter.equals(converter2)) {
                if (converterArray != null) {
                    converterArray[0] = null;
                }
                return this;
            }
            if (converter.getSupportedType() != converter2.getSupportedType()) continue;
            Converter[] converterArray3 = new Converter[n];
            for (int j = 0; j < n; ++j) {
                converterArray3[j] = j != i ? converterArray2[j] : converter;
            }
            if (converterArray != null) {
                converterArray[0] = converter2;
            }
            return new ConverterSet(converterArray3);
        }
        Converter[] converterArray4 = new Converter[n + 1];
        System.arraycopy(converterArray2, 0, converterArray4, 0, n);
        converterArray4[n] = converter;
        if (converterArray != null) {
            converterArray[0] = null;
        }
        return new ConverterSet(converterArray4);
    }

    ConverterSet remove(Converter converter, Converter[] converterArray) {
        Converter[] converterArray2 = this.iConverters;
        int n = converterArray2.length;
        for (int i = 0; i < n; ++i) {
            if (!converter.equals(converterArray2[i])) continue;
            return this.remove(i, converterArray);
        }
        if (converterArray != null) {
            converterArray[0] = null;
        }
        return this;
    }

    ConverterSet remove(int n, Converter[] converterArray) {
        Converter[] converterArray2 = this.iConverters;
        int n2 = converterArray2.length;
        if (n >= n2) {
            throw new IndexOutOfBoundsException();
        }
        if (converterArray != null) {
            converterArray[0] = converterArray2[n];
        }
        Converter[] converterArray3 = new Converter[n2 - 1];
        int n3 = 0;
        for (int i = 0; i < n2; ++i) {
            if (i == n) continue;
            converterArray3[n3++] = converterArray2[i];
        }
        return new ConverterSet(converterArray3);
    }

    private static Converter selectSlow(ConverterSet converterSet, Class<?> clazz) {
        Class<?> clazz2;
        Converter converter;
        int n;
        Converter[] converterArray = converterSet.iConverters;
        int n2 = n = converterArray.length;
        while (--n2 >= 0) {
            converter = converterArray[n2];
            clazz2 = converter.getSupportedType();
            if (clazz2 == clazz) {
                return converter;
            }
            if (clazz2 != null && (clazz == null || clazz2.isAssignableFrom(clazz))) continue;
            converterSet = converterSet.remove(n2, null);
            converterArray = converterSet.iConverters;
            n = converterArray.length;
        }
        if (clazz == null || n == 0) {
            return null;
        }
        if (n == 1) {
            return converterArray[0];
        }
        n2 = n;
        while (--n2 >= 0) {
            converter = converterArray[n2];
            clazz2 = converter.getSupportedType();
            int n3 = n;
            while (--n3 >= 0) {
                if (n3 == n2 || !converterArray[n3].getSupportedType().isAssignableFrom(clazz2)) continue;
                converterSet = converterSet.remove(n3, null);
                converterArray = converterSet.iConverters;
                n = converterArray.length;
                n2 = n - 1;
            }
        }
        if (n == 1) {
            return converterArray[0];
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Unable to find best converter for type \"");
        stringBuilder.append(clazz.getName());
        stringBuilder.append("\" from remaining set: ");
        for (int i = 0; i < n; ++i) {
            converter = converterArray[i];
            Class<?> clazz3 = converter.getSupportedType();
            stringBuilder.append(converter.getClass().getName());
            stringBuilder.append('[');
            stringBuilder.append(clazz3 == null ? null : clazz3.getName());
            stringBuilder.append("], ");
        }
        throw new IllegalStateException(stringBuilder.toString());
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static class Entry {
        final Class<?> iType;
        final Converter iConverter;

        Entry(Class<?> clazz, Converter converter) {
            this.iType = clazz;
            this.iConverter = converter;
        }
    }
}

