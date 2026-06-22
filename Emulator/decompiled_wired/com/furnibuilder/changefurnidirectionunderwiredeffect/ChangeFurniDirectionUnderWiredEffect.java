/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.eu.habbo.Emulator
 *  com.eu.habbo.habbohotel.items.ItemInteraction
 *  com.eu.habbo.habbohotel.users.Habbo
 *  com.eu.habbo.plugin.EventHandler
 *  com.eu.habbo.plugin.EventListener
 *  com.eu.habbo.plugin.HabboPlugin
 *  com.eu.habbo.plugin.events.emulator.EmulatorLoadItemsManagerEvent
 */
package com.furnibuilder.changefurnidirectionunderwiredeffect;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.ItemInteraction;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.EventHandler;
import com.eu.habbo.plugin.EventListener;
import com.eu.habbo.plugin.HabboPlugin;
import com.eu.habbo.plugin.events.emulator.EmulatorLoadItemsManagerEvent;
import com.furnibuilder.changefurnidirectionunderwiredeffect.WiredEffectChangeFurniDirectionUnder;

public class ChangeFurniDirectionUnderWiredEffect
extends HabboPlugin
implements EventListener {
    public void onEnable() throws Exception {
        Emulator.getPluginManager().registerEvents((HabboPlugin)this, (EventListener)this);
        if (Emulator.isReady && !Emulator.isShuttingDown) {
            this.onLoadItemsManager(null);
        }
    }

    public void onDisable() throws Exception {
    }

    public boolean hasPermission(Habbo habbo, String string) {
        return false;
    }

    @EventHandler
    public void onLoadItemsManager(EmulatorLoadItemsManagerEvent e) {
        Emulator.getGameEnvironment().getItemManager().addItemInteraction(new ItemInteraction("wf_act_change_direction_under", WiredEffectChangeFurniDirectionUnder.class));
    }
}

