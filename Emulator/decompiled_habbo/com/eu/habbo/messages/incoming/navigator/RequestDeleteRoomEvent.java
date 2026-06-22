/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.incoming.navigator;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.modtool.ScripterManager;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.RideablePet;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.inventory.AddPetComposer;
import com.eu.habbo.plugin.events.navigator.NavigatorRoomDeletedEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestDeleteRoomEvent
extends MessageHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestDeleteRoomEvent.class);

    @Override
    public void handle() throws Exception {
        int roomId = this.packet.readInt();
        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(roomId);
        if (room != null) {
            if (room.isOwner(this.client.getHabbo())) {
                Guild guild;
                if (room.getId() == this.client.getHabbo().getHabboInfo().getHomeRoom()) {
                    return;
                }
                if (Emulator.getPluginManager().fireEvent(new NavigatorRoomDeletedEvent(this.client.getHabbo(), room)).isCancelled()) {
                    return;
                }
                room.ejectAll();
                room.ejectUserFurni(room.getOwnerId());
                ArrayList<Bot> bots = new ArrayList<Bot>(room.getCurrentBots().valueCollection());
                for (Bot bot : bots) {
                    Emulator.getGameEnvironment().getBotManager().pickUpBot(bot, null);
                }
                ArrayList<Pet> pets = new ArrayList<Pet>(room.getCurrentPets().valueCollection());
                for (Pet pet : pets) {
                    RideablePet rideablePet;
                    if (pet instanceof RideablePet && (rideablePet = (RideablePet)pet).getRider() != null) {
                        rideablePet.getRider().getHabboInfo().dismountPet(true);
                    }
                    pet.removeFromRoom();
                    Emulator.getThreading().run(pet);
                    Habbo owner = Emulator.getGameEnvironment().getHabboManager().getHabbo(pet.getUserId());
                    if (owner == null) continue;
                    owner.getClient().sendResponse(new AddPetComposer(pet));
                    owner.getInventory().getPetsComponent().addPet(pet);
                }
                if (room.getGuildId() > 0 && (guild = Emulator.getGameEnvironment().getGuildManager().getGuild(room.getGuildId())) != null) {
                    Emulator.getGameEnvironment().getGuildManager().deleteGuild(guild);
                }
                room.preventUnloading = false;
                room.dispose();
                Emulator.getGameEnvironment().getRoomManager().uncacheRoom(room);
                try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();){
                    try (PreparedStatement statement = connection.prepareStatement("DELETE FROM rooms WHERE id = ? LIMIT 1");){
                        statement.setInt(1, roomId);
                        statement.execute();
                    }
                    if (room.hasCustomLayout()) {
                        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM room_models_custom WHERE id = ? LIMIT 1");){
                            stmt.setInt(1, roomId);
                            stmt.execute();
                        }
                    }
                    Emulator.getGameEnvironment().getRoomManager().unloadRoom(room);
                    try (PreparedStatement rights = connection.prepareStatement("DELETE FROM room_rights WHERE room_id = ?");){
                        rights.setInt(1, roomId);
                        rights.execute();
                    }
                    try (PreparedStatement votes = connection.prepareStatement("DELETE FROM room_votes WHERE room_id = ?");){
                        votes.setInt(1, roomId);
                        votes.execute();
                    }
                    try (PreparedStatement filter = connection.prepareStatement("DELETE FROM room_wordfilter WHERE room_id = ?");){
                        filter.setInt(1, roomId);
                        filter.execute();
                    }
                }
                catch (SQLException sQLException) {
                    LOGGER.error("Caught SQL exception", sQLException);
                }
            } else {
                String message = Emulator.getTexts().getValue("scripter.warning.room.delete").replace("%username%", this.client.getHabbo().getHabboInfo().getUsername()).replace("%roomname%", room.getName()).replace("%roomowner%", room.getOwnerName());
                ScripterManager.scripterDetected(this.client, message);
                LOGGER.info(message);
            }
        }
    }
}

