/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.catalog;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.ISerialize;
import com.eu.habbo.messages.ServerMessage;

public class CatalogFeaturedPage
implements ISerialize {
    private final int slotId;
    private final String caption;
    private final String image;
    private final Type type;
    private final int expireTimestamp;
    private final String pageName;
    private final int pageId;
    private final String productName;

    public CatalogFeaturedPage(int slotId, String caption, String image, Type type, int expireTimestamp, String pageName, int pageId, String productName) {
        this.slotId = slotId;
        this.caption = caption;
        this.image = image;
        this.type = type;
        this.expireTimestamp = expireTimestamp;
        this.pageName = pageName;
        this.pageId = pageId;
        this.productName = productName;
    }

    @Override
    public void serialize(ServerMessage message) {
        message.appendInt(this.slotId);
        message.appendString(this.caption);
        message.appendString(this.image);
        message.appendInt(this.type.type);
        switch (this.type.ordinal()) {
            case 0: {
                message.appendString(this.pageName);
                break;
            }
            case 1: {
                message.appendInt(this.pageId);
                break;
            }
            case 2: {
                message.appendString(this.productName);
            }
        }
        message.appendInt(Emulator.getIntUnixTimestamp() - this.expireTimestamp);
    }

    public static enum Type {
        PAGE_NAME(0),
        PAGE_ID(1),
        PRODUCT_NAME(2);

        public final int type;

        private Type(int type) {
            this.type = type;
        }
    }
}

