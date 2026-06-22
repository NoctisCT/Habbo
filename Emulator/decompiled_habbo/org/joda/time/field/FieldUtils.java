/*
 * Decompiled with CFR 0.152.
 */
package org.joda.time.field;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeFieldType;
import org.joda.time.IllegalFieldValueException;

public class FieldUtils {
    private FieldUtils() {
    }

    public static int safeNegate(int n) {
        if (n == Integer.MIN_VALUE) {
            throw new ArithmeticException("Integer.MIN_VALUE cannot be negated");
        }
        return -n;
    }

    public static int safeAdd(int n, int n2) {
        int n3 = n + n2;
        if ((n ^ n3) < 0 && (n ^ n2) >= 0) {
            throw new ArithmeticException("The calculation caused an overflow: " + n + " + " + n2);
        }
        return n3;
    }

    public static long safeAdd(long l, long l2) {
        long l3 = l + l2;
        if ((l ^ l3) < 0L && (l ^ l2) >= 0L) {
            throw new ArithmeticException("The calculation caused an overflow: " + l + " + " + l2);
        }
        return l3;
    }

    public static long safeSubtract(long l, long l2) {
        long l3 = l - l2;
        if ((l ^ l3) < 0L && (l ^ l2) < 0L) {
            throw new ArithmeticException("The calculation caused an overflow: " + l + " - " + l2);
        }
        return l3;
    }

    public static int safeMultiply(int n, int n2) {
        long l = (long)n * (long)n2;
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            throw new ArithmeticException("Multiplication overflows an int: " + n + " * " + n2);
        }
        return (int)l;
    }

    public static long safeMultiply(long l, int n) {
        switch (n) {
            case -1: {
                if (l == Long.MIN_VALUE) {
                    throw new ArithmeticException("Multiplication overflows a long: " + l + " * " + n);
                }
                return -l;
            }
            case 0: {
                return 0L;
            }
            case 1: {
                return l;
            }
        }
        long l2 = l * (long)n;
        if (l2 / (long)n != l) {
            throw new ArithmeticException("Multiplication overflows a long: " + l + " * " + n);
        }
        return l2;
    }

    public static long safeMultiply(long l, long l2) {
        if (l2 == 1L) {
            return l;
        }
        if (l == 1L) {
            return l2;
        }
        if (l == 0L || l2 == 0L) {
            return 0L;
        }
        long l3 = l * l2;
        if (l3 / l2 != l || l == Long.MIN_VALUE && l2 == -1L || l2 == Long.MIN_VALUE && l == -1L) {
            throw new ArithmeticException("Multiplication overflows a long: " + l + " * " + l2);
        }
        return l3;
    }

    public static long safeDivide(long l, long l2) {
        if (l == Long.MIN_VALUE && l2 == -1L) {
            throw new ArithmeticException("Multiplication overflows a long: " + l + " / " + l2);
        }
        return l / l2;
    }

    public static long safeDivide(long l, long l2, RoundingMode roundingMode) {
        if (l == Long.MIN_VALUE && l2 == -1L) {
            throw new ArithmeticException("Multiplication overflows a long: " + l + " / " + l2);
        }
        BigDecimal bigDecimal = new BigDecimal(l);
        BigDecimal bigDecimal2 = new BigDecimal(l2);
        return bigDecimal.divide(bigDecimal2, roundingMode).longValue();
    }

    public static int safeToInt(long l) {
        if (Integer.MIN_VALUE <= l && l <= Integer.MAX_VALUE) {
            return (int)l;
        }
        throw new ArithmeticException("Value cannot fit in an int: " + l);
    }

    public static int safeMultiplyToInt(long l, long l2) {
        long l3 = FieldUtils.safeMultiply(l, l2);
        return FieldUtils.safeToInt(l3);
    }

    public static void verifyValueBounds(DateTimeField dateTimeField, int n, int n2, int n3) {
        if (n < n2 || n > n3) {
            throw new IllegalFieldValueException(dateTimeField.getType(), (Number)n, (Number)n2, (Number)n3);
        }
    }

    public static void verifyValueBounds(DateTimeFieldType dateTimeFieldType, int n, int n2, int n3) {
        if (n < n2 || n > n3) {
            throw new IllegalFieldValueException(dateTimeFieldType, (Number)n, (Number)n2, (Number)n3);
        }
    }

    public static void verifyValueBounds(String string, int n, int n2, int n3) {
        if (n < n2 || n > n3) {
            throw new IllegalFieldValueException(string, (Number)n, (Number)n2, (Number)n3);
        }
    }

    public static int getWrappedValue(int n, int n2, int n3, int n4) {
        return FieldUtils.getWrappedValue(n + n2, n3, n4);
    }

    public static int getWrappedValue(int n, int n2, int n3) {
        if (n2 >= n3) {
            throw new IllegalArgumentException("MIN > MAX");
        }
        int n4 = n3 - n2 + 1;
        if ((n -= n2) >= 0) {
            return n % n4 + n2;
        }
        int n5 = -n % n4;
        if (n5 == 0) {
            return 0 + n2;
        }
        return n4 - n5 + n2;
    }

    public static boolean equals(Object object, Object object2) {
        if (object == object2) {
            return true;
        }
        if (object == null || object2 == null) {
            return false;
        }
        return object.equals(object2);
    }
}

