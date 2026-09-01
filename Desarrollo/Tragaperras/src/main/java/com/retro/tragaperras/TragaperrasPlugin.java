package com.retro.tragaperras;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.ItemInteraction;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.EventHandler;
import com.eu.habbo.plugin.EventListener;
import com.eu.habbo.plugin.HabboPlugin;
import com.eu.habbo.plugin.events.emulator.EmulatorLoadedEvent;
import com.eu.habbo.plugin.events.emulator.EmulatorLoadItemsManagerEvent;
import com.retro.tragaperras.mensajes.SpinSlotMachine;

public final class TragaperrasPlugin extends HabboPlugin implements EventListener
{
    public static final int PACKET_SPIN = 5042;
    public static final int PACKET_OPEN = 5043;
    public static final int PACKET_RESULT = 5044;
    public static final int PACKET_STATE = 5045;
    public static final int PACKET_CLOSE = 5046;

    public static final String INTERACTION_CREDITS = "holo_slot_credits";
    public static final String INTERACTION_DIAMONDS = "holo_slot_diamonds";
    public static final String INTERACTION_DUCKETS = "holo_slot_duckets";

    private static TragaperrasPlugin instance;

    private final SlotMachineManager manager = new SlotMachineManager();

    public static TragaperrasPlugin getInstance()
    {
        return instance;
    }

    public SlotMachineManager getManager()
    {
        return this.manager;
    }

    @Override
    public void onEnable()
    {
        instance = this;
        Emulator.getPluginManager().registerEvents(this, this);
        System.out.println("[Tragaperras] Plugin habilitado.");
    }

    @EventHandler
    public void onLoadItemsManager(EmulatorLoadItemsManagerEvent event)
    {
        Emulator.getGameEnvironment()
                .getItemManager()
                .addItemInteraction(
                        new ItemInteraction(
                                INTERACTION_CREDITS,
                                InteractionSlotMachine.class
                        )
                );

        Emulator.getGameEnvironment()
                .getItemManager()
                .addItemInteraction(
                        new ItemInteraction(
                                INTERACTION_DIAMONDS,
                                InteractionSlotDiamonds.class
                        )
                );

        Emulator.getGameEnvironment()
                .getItemManager()
                .addItemInteraction(
                        new ItemInteraction(
                                INTERACTION_DUCKETS,
                                InteractionSlotDuckets.class
                        )
                );

        System.out.println(
                "[Tragaperras] Interactions registradas: " +
                INTERACTION_CREDITS + ", " +
                INTERACTION_DIAMONDS + ", " +
                INTERACTION_DUCKETS
        );
    }

    @EventHandler
    public void onEmulatorLoaded(EmulatorLoadedEvent event) throws Exception
    {
        this.manager.initializeDatabase();

        Emulator.getGameServer()
                .getPacketManager()
                .registerHandler(PACKET_SPIN, SpinSlotMachine.class);

        System.out.println("[Tragaperras] Packet de tirada registrado: " + PACKET_SPIN);
    }

    @Override
    public void onDisable()
    {
        this.manager.clearRuntimeState();
        instance = null;
        System.out.println("[Tragaperras] Plugin deshabilitado.");
    }

    @Override
    public boolean hasPermission(Habbo habbo, String permission)
    {
        return false;
    }
}