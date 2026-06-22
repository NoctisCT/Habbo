/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items.interactions.wired;

public class WiredSettings {
    private int[] intParams;
    private String stringParam;
    private int[] furniIds;
    private int stuffTypeSelectionCode;
    private int delay;

    public WiredSettings(int[] intParams, String stringParam, int[] furniIds, int stuffTypeSelectionCode, int delay) {
        this.furniIds = furniIds;
        this.intParams = intParams;
        this.stringParam = stringParam;
        this.stuffTypeSelectionCode = stuffTypeSelectionCode;
        this.delay = delay;
    }

    public WiredSettings(int[] intParams, String stringParam, int[] furniIds, int stuffTypeSelectionCode) {
        this(intParams, stringParam, furniIds, stuffTypeSelectionCode, 0);
    }

    public int getStuffTypeSelectionCode() {
        return this.stuffTypeSelectionCode;
    }

    public void setStuffTypeSelectionCode(int stuffTypeSelectionCode) {
        this.stuffTypeSelectionCode = stuffTypeSelectionCode;
    }

    public int[] getFurniIds() {
        return this.furniIds;
    }

    public void setFurniIds(int[] furniIds) {
        this.furniIds = furniIds;
    }

    public String getStringParam() {
        return this.stringParam;
    }

    public void setStringParam(String stringParam) {
        this.stringParam = stringParam;
    }

    public int[] getIntParams() {
        return this.intParams;
    }

    public void setIntParams(int[] intParams) {
        this.intParams = intParams;
    }

    public int getDelay() {
        return this.delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }
}

