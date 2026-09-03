package com.retro.spaceinvaders.arcade;

import com.eu.habbo.habbohotel.users.HabboItem;
import com.retro.spaceinvaders.InteractionBlockDrop;
import com.retro.spaceinvaders.InteractionPacMan;
import com.retro.spaceinvaders.InteractionDuckHunt;
import com.retro.spaceinvaders.InteractionSpaceInvaders;
import com.retro.spaceinvaders.SpaceInvadersPlugin;

public enum ArcadeGameDefinition
{
    SPACE_INVADERS(
            "space_invaders",
            InteractionSpaceInvaders.class,
            SpaceInvadersPlugin.PACKET_OPEN,
            0,
            1150,
            1150,
            900,
            100,
            10,
            30,
            100,
            2500
    ),

    DUCK_HUNT(
            "duck_hunt",
            InteractionDuckHunt.class,
            SpaceInvadersPlugin.PACKET_DUCK_HUNT_OPEN,
            0,
            600,
            1500,
            1500,
            50,
            100,
            100,
            100,
            5000
    ),

    BLOCK_DROP(
            "block_drop",
            InteractionBlockDrop.class,
            SpaceInvadersPlugin.PACKET_BLOCK_DROP_OPEN,
            0,
            1000,
            2000,
            2000,
            50,
            100,
            100,
            120,
            5000
    ),

    PAC_MAN(
            "pac_man",
            InteractionPacMan.class,
            SpaceInvadersPlugin.PACKET_PAC_MAN_OPEN,
            0,
            1000,
            18000,
            18000,
            100,
            10,
            100,
            250,
            12000
    );

    private final String key;
    private final Class<? extends HabboItem> interactionClass;
    private final int openPacketId;
    private final int frontRotationOffset;
    private final int completedLevelMinimum;
    private final int completedLevelMaximum;
    private final int currentLevelMaximum;
    private final int maximumLevel;
    private final int scoreQuantum;
    private final int durationScoreDivisor;
    private final int durationStepMs;
    private final int durationPerLevelMs;

    ArcadeGameDefinition(
            String key,
            Class<? extends HabboItem> interactionClass,
            int openPacketId,
            int frontRotationOffset,
            int completedLevelMinimum,
            int completedLevelMaximum,
            int currentLevelMaximum,
            int maximumLevel,
            int scoreQuantum,
            int durationScoreDivisor,
            int durationStepMs,
            int durationPerLevelMs)
    {
        this.key = key;
        this.interactionClass = interactionClass;
        this.openPacketId = openPacketId;
        this.frontRotationOffset = frontRotationOffset;
        this.completedLevelMinimum = completedLevelMinimum;
        this.completedLevelMaximum = completedLevelMaximum;
        this.currentLevelMaximum = currentLevelMaximum;
        this.maximumLevel = maximumLevel;
        this.scoreQuantum = scoreQuantum;
        this.durationScoreDivisor = durationScoreDivisor;
        this.durationStepMs = durationStepMs;
        this.durationPerLevelMs = durationPerLevelMs;
    }

    public String getKey()
    {
        return this.key;
    }

    public Class<? extends HabboItem> getInteractionClass()
    {
        return this.interactionClass;
    }

    public int getOpenPacketId()
    {
        return this.openPacketId;
    }

    public int getFrontRotationOffset()
    {
        return this.frontRotationOffset;
    }

    public boolean isValidScore(
            int score,
            int level)
    {
        if(score < 0 ||
                level < 1 ||
                level > this.maximumLevel ||
                this.scoreQuantum <= 0 ||
                score % this.scoreQuantum != 0)
        {
            return false;
        }

        long completedLevels =
                (long)(level - 1);

        long minimum =
                completedLevels *
                (long)this.completedLevelMinimum;

        long maximum =
                completedLevels *
                (long)this.completedLevelMaximum +
                (long)this.currentLevelMaximum;

        return score >= minimum &&
                score <= maximum;
    }

    public long minimumPlausibleDurationMs(
            int score,
            int level)
    {
        long scoreFloor =
                (
                        (long)score /
                        (long)Math.max(
                                1,
                                this.durationScoreDivisor
                        )
                ) *
                (long)this.durationStepMs;

        long levelFloor =
                Math.max(
                        0,
                        level - 1
                ) *
                (long)this.durationPerLevelMs;

        return Math.max(
                800L,
                Math.max(
                        scoreFloor,
                        levelFloor
                )
        );
    }

    public static ArcadeGameDefinition byKey(
            String key)
    {
        if(key == null)
        {
            return null;
        }

        for(ArcadeGameDefinition value : values())
        {
            if(value.key.equals(key))
            {
                return value;
            }
        }

        return null;
    }
}
