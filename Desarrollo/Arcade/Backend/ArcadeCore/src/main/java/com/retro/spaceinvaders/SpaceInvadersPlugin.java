package com.retro.spaceinvaders;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.ItemInteraction;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.EventHandler;
import com.eu.habbo.plugin.EventListener;
import com.eu.habbo.plugin.HabboPlugin;
import com.eu.habbo.plugin.events.emulator.EmulatorLoadItemsManagerEvent;
import com.eu.habbo.plugin.events.emulator.EmulatorLoadedEvent;
import com.retro.spaceinvaders.arcade.ArcadeManager;
import com.retro.spaceinvaders.messages.StartArcadeGame;
import com.retro.spaceinvaders.messages.SubmitArcadeScore;

public final class SpaceInvadersPlugin
        extends HabboPlugin
        implements EventListener
{
    public static final int PACKET_OPEN = 6100;
    public static final int PACKET_START = 6101;
    public static final int PACKET_STARTED = 6102;
    public static final int PACKET_SUBMIT = 6103;
    public static final int PACKET_LEADERBOARD = 6104;
    public static final int PACKET_CLOSE = 6105;
    public static final int PACKET_DUCK_HUNT_OPEN = 6110;
    public static final int PACKET_BLOCK_DROP_OPEN = 6120;
    public static final int PACKET_PAC_MAN_OPEN = 6130;

    public static final String INTERACTION = "holo_arcade_space";
    public static final String INTERACTION_DUCK_HUNT =
            "biribiri_arcade_duckhunt";
    public static final String INTERACTION_BLOCK_DROP =
            "biribiri_arcade_blockdrop";
    public static final String INTERACTION_PAC_MAN =
            "biribiri_arcade_pacman";

    public static final String BUILD =
            "biribiri-arcade-v1-space-invaders-v3";

    public static final String DUCK_HUNT_BUILD =
            "biribiri-duck-hunt-v1";

    public static final String BLOCK_DROP_BUILD =
            "biribiri-block-drop-v1";

    public static final String PAC_MAN_BUILD =
            "biribiri-pac-man-v1";

    private static SpaceInvadersPlugin instance;

    private final ArcadeManager manager =
            new ArcadeManager();

    private boolean spaceInteractionRegistered = false;
    private boolean duckHuntInteractionRegistered = false;
    private boolean blockDropInteractionRegistered = false;
    private boolean pacManInteractionRegistered = false;
    private boolean packetsRegistered = false;

    public static SpaceInvadersPlugin getInstance()
    {
        return instance;
    }

    public ArcadeManager getManager()
    {
        return this.manager;
    }

    @Override
    public void onEnable()
    {
        instance = this;

        Emulator.getPluginManager()
                .registerEvents(this, this);

        this.prepareRuntimeIfAvailable("onEnable");

        System.out.println(
                "[BiribiriArcade] habilitado build=" +
                BUILD +
                " duck=" +
                DUCK_HUNT_BUILD +
                " block=" +
                BLOCK_DROP_BUILD +
                " pac=" +
                PAC_MAN_BUILD
        );
    }

    @EventHandler
    public void onLoadItemsManager(
            EmulatorLoadItemsManagerEvent event)
    {
        this.registerInteractionsIfNeeded(
                "EmulatorLoadItemsManagerEvent"
        );

        this.prepareRuntimeIfAvailable(
                "EmulatorLoadItemsManagerEvent"
        );
    }

    @EventHandler
    public void onEmulatorLoaded(
            EmulatorLoadedEvent event)
    {
        this.registerInteractionsIfNeeded(
                "EmulatorLoadedEvent"
        );

        this.prepareRuntimeIfAvailable(
                "EmulatorLoadedEvent"
        );
    }

    private synchronized void registerInteractionsIfNeeded(
            String source)
    {
        if(!this.spaceInteractionRegistered)
        {
            try
            {
                Emulator.getGameEnvironment()
                        .getItemManager()
                        .addItemInteraction(
                                new ItemInteraction(
                                        INTERACTION,
                                        InteractionSpaceInvaders.class
                                )
                        );

                this.spaceInteractionRegistered = true;

                System.out.println(
                        "[BiribiriArcade] interaction registrada (" +
                        source +
                        "): " +
                        INTERACTION
                );
            }
            catch(Throwable throwable)
            {
                System.out.println(
                        "[BiribiriArcade] interaction pendiente (" +
                        source +
                        "): " +
                        INTERACTION +
                        " - " +
                        throwable.getClass().getSimpleName()
                );
            }
        }

        if(!this.duckHuntInteractionRegistered)
        {
            try
            {
                Emulator.getGameEnvironment()
                        .getItemManager()
                        .addItemInteraction(
                                new ItemInteraction(
                                        INTERACTION_DUCK_HUNT,
                                        InteractionDuckHunt.class
                                )
                        );

                this.duckHuntInteractionRegistered = true;

                System.out.println(
                        "[BiribiriArcade] interaction registrada (" +
                        source +
                        "): " +
                        INTERACTION_DUCK_HUNT
                );
            }
            catch(Throwable throwable)
            {
                System.out.println(
                        "[BiribiriArcade] interaction pendiente (" +
                        source +
                        "): " +
                        INTERACTION_DUCK_HUNT +
                        " - " +
                        throwable.getClass().getSimpleName()
                );
            }
        }

        if(!this.blockDropInteractionRegistered)
        {
            try
            {
                Emulator.getGameEnvironment()
                        .getItemManager()
                        .addItemInteraction(
                                new ItemInteraction(
                                        INTERACTION_BLOCK_DROP,
                                        InteractionBlockDrop.class
                                )
                        );

                this.blockDropInteractionRegistered = true;

                System.out.println(
                        "[BiribiriArcade] interaction registrada (" +
                        source +
                        "): " +
                        INTERACTION_BLOCK_DROP
                );
            }
            catch(Throwable throwable)
            {
                System.out.println(
                        "[BiribiriArcade] interaction pendiente (" +
                        source +
                        "): " +
                        INTERACTION_BLOCK_DROP +
                        " - " +
                        throwable.getClass().getSimpleName()
                );
            }
        }

        if(!this.pacManInteractionRegistered)
        {
            try
            {
                Emulator.getGameEnvironment()
                        .getItemManager()
                        .addItemInteraction(
                                new ItemInteraction(
                                        INTERACTION_PAC_MAN,
                                        InteractionPacMan.class
                                )
                        );

                this.pacManInteractionRegistered = true;

                System.out.println(
                        "[BiribiriArcade] interaction registrada (" +
                        source +
                        "): " +
                        INTERACTION_PAC_MAN
                );
            }
            catch(Throwable throwable)
            {
                System.out.println(
                        "[BiribiriArcade] interaction pendiente (" +
                        source +
                        "): " +
                        INTERACTION_PAC_MAN +
                        " - " +
                        throwable.getClass().getSimpleName()
                );
            }
        }

    }

    private synchronized void prepareRuntimeIfAvailable(
            String source)
    {
        try
        {
            this.manager.initializeDatabase();

            if(!this.packetsRegistered)
            {
                Emulator.getGameServer()
                        .getPacketManager()
                        .registerHandler(
                                PACKET_START,
                                StartArcadeGame.class
                        );

                Emulator.getGameServer()
                        .getPacketManager()
                        .registerHandler(
                                PACKET_SUBMIT,
                                SubmitArcadeScore.class
                        );

                this.packetsRegistered = true;

                System.out.println(
                        "[BiribiriArcade] packets registrados (" +
                        source +
                        "): " +
                        PACKET_START +
                        ", " +
                        PACKET_SUBMIT
                );
            }
        }
        catch(Throwable throwable)
        {
            System.out.println(
                    "[BiribiriArcade] runtime pendiente (" +
                    source +
                    "): " +
                    throwable.getClass().getSimpleName() +
                    " - " +
                    throwable.getMessage()
            );
        }
    }

    @Override
    public void onDisable()
    {
        this.manager.clearRuntimeState();
        instance = null;

        System.out.println(
                "[BiribiriArcade] deshabilitado."
        );
    }

    @Override
    public boolean hasPermission(
            Habbo habbo,
            String permission)
    {
        return false;
    }
}
