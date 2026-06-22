/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.games.tag;

import com.eu.habbo.habbohotel.games.GameTeam;
import com.eu.habbo.habbohotel.games.tag.TagGame;
import com.eu.habbo.habbohotel.games.tag.TagGamePlayer;
import com.eu.habbo.habbohotel.items.interactions.games.tag.InteractionTagPole;
import com.eu.habbo.habbohotel.items.interactions.games.tag.icetag.InteractionIceTagPole;
import com.eu.habbo.habbohotel.rooms.Room;

public class IceTagGame
extends TagGame {
    private static final int MALE_SKATES = 38;
    private static final int FEMALE_SKATES = 39;

    public IceTagGame(Room room) {
        super(GameTeam.class, TagGamePlayer.class, room);
    }

    @Override
    public Class<? extends InteractionTagPole> getTagPole() {
        return InteractionIceTagPole.class;
    }

    @Override
    public int getMaleEffect() {
        return 38;
    }

    @Override
    public int getMaleTaggerEffect() {
        return 45;
    }

    @Override
    public int getFemaleEffect() {
        return 39;
    }

    @Override
    public int getFemaleTaggerEffect() {
        return 46;
    }
}

