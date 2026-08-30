package com.retro.rpgengine;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.retro.rpgengine.ServicioRpgEngine.Contexto;
import com.retro.rpgengine.ServicioRpgEngine.StatValue;
import com.retro.rpgengine.ServicioRpgEngine.Encounter;
import com.retro.rpgengine.ServicioRpgEngine.EncounterParticipant;

import java.util.Collections;
import java.util.List;

public final class RpgEngineRealtime
{
    private RpgEngineRealtime()
    {
    }

    public static void pushStatContext(int rpgId, int targetUserId, StatValue statValue)
    {
        try
        {
            Habbo target = Emulator.getGameEnvironment()
                    .getHabboManager()
                    .getHabbo(targetUserId);

            if(target == null || target.getClient() == null) return;

            Room room = target.getHabboInfo().getCurrentRoom();

            if(room == null) return;

            Contexto context = ServicioRpgEngine.getContext(room.getId(), targetUserId);

            if(context == null || context.rpgId != rpgId) return;

            target.getClient().sendResponse(RpgEnginePackets.result(
                    RpgEnginePackets.ACTION_CONTEXT_PUSH,
                    true,
                    "stat-push",
                    Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyList(),
                    statValue,
                    context,
                    null,
                    null
            ));
        }
        catch(Exception error)
        {
            System.out.println(
                    "[RPGEngine] Realtime push failed for user " + targetUserId +
                    ": " + error.getClass().getName() + ": " + error.getMessage()
            );
        }
    }

    public static void pushEncounter(Encounter encounter, String message)
    {
        if(encounter == null || encounter.participants == null) return;

        for(EncounterParticipant participant : encounter.participants)
        {
            try
            {
                Habbo target = Emulator.getGameEnvironment()
                        .getHabboManager()
                        .getHabbo(participant.userId);

                if(target == null || target.getClient() == null) continue;

                Contexto context = null;
                Room room = target.getHabboInfo().getCurrentRoom();

                if(room != null)
                {
                    Contexto candidate = ServicioRpgEngine.getContext(
                            room.getId(), participant.userId);

                    if(candidate != null && candidate.rpgId == encounter.rpgId)
                        context = candidate;
                }

                target.getClient().sendResponse(RpgEnginePackets.result(
                        RpgEnginePackets.ACTION_ENCOUNTER_PUSH,
                        true,
                        message == null ? "encounter-push" : message,
                        Collections.emptyList(),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        null,
                        context,
                        null,
                        encounter
                ));
            }
            catch(Exception error)
            {
                System.out.println(
                        "[RPGEngine] Encounter push failed for user " + participant.userId +
                        ": " + error.getClass().getName() + ": " + error.getMessage()
                );
            }
        }
    }


    public static void pushRoomEncounters(int roomId, String message)
    {
        try
        {
            List<Encounter> encounters = ServicioRpgEngine.getActiveEncountersInRoom(roomId);

            for(Encounter encounter : encounters)
                pushEncounter(encounter, message);
        }
        catch(Exception error)
        {
            System.out.println(
                    "[RPGEngine] Room encounter push failed for room " + roomId +
                    ": " + error.getClass().getName() + ": " + error.getMessage()
            );
        }
    }

}
