/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.util.figure;

import gnu.trove.map.hash.THashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.ArrayUtils;

public class FigureUtil {
    public static THashMap<String, String> getFigureBits(String looks) {
        String[] sets;
        THashMap<String, String> bits = new THashMap<String, String>();
        for (String set : sets = looks.split("\\.")) {
            String[] setBits = set.split("-", 2);
            bits.put(setBits[0], setBits.length > 1 ? setBits[1] : "");
        }
        return bits;
    }

    public static String mergeFigures(String figure1, String figure2) {
        return FigureUtil.mergeFigures(figure1, figure2, null, null);
    }

    public static String mergeFigures(String figure1, String figure2, String[] limitFigure1) {
        return FigureUtil.mergeFigures(figure1, figure2, limitFigure1, null);
    }

    public static boolean hasBlacklistedClothing(String figure, Set<Integer> blacklist) {
        for (String set : figure.split("\\.")) {
            String[] pieces = set.split("-");
            try {
                if (pieces.length < 2 || !blacklist.contains(Integer.parseInt(pieces[1]))) continue;
                return true;
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        return false;
    }

    public static String mergeFigures(String figure1, String figure2, String[] limitFigure1, String[] limitFigure2) {
        THashMap<String, String> figureBits1 = FigureUtil.getFigureBits(figure1);
        THashMap<String, String> figureBits2 = FigureUtil.getFigureBits(figure2);
        StringBuilder finalLook = new StringBuilder();
        for (Map.Entry<String, String> keys : figureBits1.entrySet()) {
            if (limitFigure1 != null && !ArrayUtils.contains(limitFigure1, keys.getKey())) continue;
            finalLook.append(keys.getKey()).append("-").append(keys.getValue()).append(".");
        }
        for (Map.Entry<String, String> keys : figureBits2.entrySet()) {
            if (limitFigure2 != null && !ArrayUtils.contains(limitFigure2, keys.getKey())) continue;
            finalLook.append(keys.getKey()).append("-").append(keys.getValue()).append(".");
        }
        if (finalLook.toString().endsWith(".")) {
            finalLook = new StringBuilder(finalLook.substring(0, finalLook.length() - 1));
        }
        return finalLook.toString();
    }
}

