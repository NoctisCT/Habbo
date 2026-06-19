package com.eu.habbo.casualwireds;

import com.eu.habbo.Emulator;
import com.eu.habbo.plugin.HabboPlugin;
import com.eu.habbo.plugin.EventHandler;
import com.eu.habbo.plugin.EventListener;
import com.eu.habbo.plugin.events.emulator.EmulatorLoadItemsManagerEvent;
import com.eu.habbo.habbohotel.items.ItemInteraction; // Importamos la clase nativa de MS3

public class CasualWiredsPlugin extends HabboPlugin implements EventListener {

    @Override
    public void onEnable() {
        Emulator.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
    }

    @Override
    public boolean hasPermission(com.eu.habbo.habbohotel.users.Habbo habbo, String permission) {
        return true;
    }

    @EventHandler
    public void onLoadItemsManager(EmulatorLoadItemsManagerEvent event) {
        // En Arcturus Morningstar 3.x envolvemos la id y la clase dentro de un objeto
        // ItemInteraction
        Emulator.getGameEnvironment().getItemManager()
                .addItemInteraction(new ItemInteraction("wf_act_visual_modifier", WiredEffectVisualModifier.class));
        System.out.println(
                "[CasualWireds] >> Registrada interaccion 'wf_act_visual_modifier' de forma nativa en MS3. <<");
    }
}