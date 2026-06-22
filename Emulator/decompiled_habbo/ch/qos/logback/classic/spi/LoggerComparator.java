/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.spi;

import ch.qos.logback.classic.Logger;
import java.util.Comparator;

public class LoggerComparator
implements Comparator<Logger> {
    @Override
    public int compare(Logger l1, Logger l2) {
        if (l1.getName().equals(l2.getName())) {
            return 0;
        }
        if (l1.getName().equals("ROOT")) {
            return -1;
        }
        if (l2.getName().equals("ROOT")) {
            return 1;
        }
        return l1.getName().compareTo(l2.getName());
    }
}

