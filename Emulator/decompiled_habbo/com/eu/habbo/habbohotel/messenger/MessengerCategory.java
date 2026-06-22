/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.messenger;

public class MessengerCategory {
    private int user_id;
    private String name;
    private int id;

    public MessengerCategory(String name, int user_id, int id) {
        this.name = name;
        this.user_id = user_id;
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public int getUserId() {
        return this.user_id;
    }

    public int getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }
}

