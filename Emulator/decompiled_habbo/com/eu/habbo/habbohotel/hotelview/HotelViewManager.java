/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.hotelview;

import com.eu.habbo.habbohotel.hotelview.HallOfFame;
import com.eu.habbo.habbohotel.hotelview.NewsList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HotelViewManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(HotelViewManager.class);
    private final HallOfFame hallOfFame;
    private final NewsList newsList;

    public HotelViewManager() {
        long millis = System.currentTimeMillis();
        this.hallOfFame = new HallOfFame();
        this.newsList = new NewsList();
        LOGGER.info("Hotelview Manager -> Loaded! ({} MS)", (Object)(System.currentTimeMillis() - millis));
    }

    public HallOfFame getHallOfFame() {
        return this.hallOfFame;
    }

    public NewsList getNewsList() {
        return this.newsList;
    }

    public void dispose() {
        LOGGER.info("HotelView Manager -> Disposed!");
    }
}

