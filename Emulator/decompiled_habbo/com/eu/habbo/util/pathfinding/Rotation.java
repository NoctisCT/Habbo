/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.util.pathfinding;

public class Rotation {
    public static int Calculate(int X1, int Y1, int X2, int Y2) {
        int Rotation2 = 0;
        if (X1 > X2 && Y1 > Y2) {
            Rotation2 = 7;
        } else if (X1 < X2 && Y1 < Y2) {
            Rotation2 = 3;
        } else if (X1 > X2 && Y1 < Y2) {
            Rotation2 = 5;
        } else if (X1 < X2 && Y1 > Y2) {
            Rotation2 = 1;
        } else if (X1 > X2) {
            Rotation2 = 6;
        } else if (X1 < X2) {
            Rotation2 = 2;
        } else if (Y1 < Y2) {
            Rotation2 = 4;
        } else if (Y1 > Y2) {
            Rotation2 = 0;
        }
        return Rotation2;
    }
}

