package com.retro.rpgengine;

import com.eu.habbo.messages.ServerMessage;
import com.retro.rpgengine.ServicioRpgEngine.Contexto;
import com.retro.rpgengine.ServicioRpgEngine.Movimiento;
import com.retro.rpgengine.ServicioRpgEngine.Proyecto;
import com.retro.rpgengine.ServicioRpgEngine.Sala;
import com.retro.rpgengine.ServicioRpgEngine.StatDefinition;
import com.retro.rpgengine.ServicioRpgEngine.StatValue;
import com.retro.rpgengine.ServicioRpgEngine.Encounter;
import com.retro.rpgengine.ServicioRpgEngine.EncounterConfig;
import com.retro.rpgengine.ServicioRpgEngine.EncounterParticipant;
import com.retro.rpgengine.ServicioRpgEngine.EncounterReservation;

import java.util.ArrayList;
import java.util.List;

public final class RpgEnginePackets
{
    public static final int RESULT_PACKET = 5051;
    public static final int ACTION_CONTEXT_PUSH = 18;
    public static final int ACTION_ENCOUNTER_PUSH = 33;

    private RpgEnginePackets()
    {
    }

    public static ServerMessage result(
            int action,
            boolean success,
            String message,
            List<Proyecto> projects,
            List<Sala> rooms,
            List<StatDefinition> stats,
            StatValue statValue,
            Contexto context,
            EncounterConfig encounterConfig,
            Encounter encounter)
    {
        ServerMessage response = new ServerMessage(RESULT_PACKET);

        response.appendInt(action);
        response.appendBoolean(success);
        response.appendString(message == null ? "" : message);

        appendProjects(response, projects);
        appendRooms(response, rooms);
        appendStats(response, stats);
        appendStatValue(response, statValue);
        appendContext(response, context);
        appendEncounterConfig(response, encounterConfig);
        appendEncounter(response, encounter);

        return response;
    }

    private static int scaled(double value)
    {
        return (int)Math.round(value * 1000.0);
    }

    private static int furniturePolicyCode(String value)
    {
        if("block-all".equals(value)) return 1;
        if("ignore".equals(value)) return 2;
        return 0;
    }

    private static int confirmationModeCode(String value)
    {
        return "instant".equals(value) ? 1 : 0;
    }

    private static int movementSourceCode(String value)
    {
        return "stat".equals(value) ? 1 : 0;
    }

    private static void appendMovement(ServerMessage response, Movimiento movement)
    {
        response.appendString(movement.resourceName);
        response.appendInt(scaled(movement.baseMovement));
        response.appendInt(movement.directions);
        response.appendInt(scaled(movement.orthogonalCost));
        response.appendInt(scaled(movement.diagonalCost));
        response.appendBoolean(movement.allowCornerCutting);
        response.appendInt(scaled(movement.maxStepHeight));
        response.appendBoolean(movement.allowFalling);
        response.appendInt(furniturePolicyCode(movement.furniturePolicy));
        response.appendBoolean(movement.unknownFurnitureBlocks);
        response.appendInt(confirmationModeCode(movement.confirmationMode));
        response.appendInt(movementSourceCode(movement.sourceMode));
        response.appendInt(movement.statId);
        response.appendInt(movement.version);
    }

    private static void appendProjects(ServerMessage response, List<Proyecto> projects)
    {
        List<Proyecto> safe = projects == null
                ? new ArrayList<Proyecto>()
                : projects;

        response.appendInt(safe.size());

        for(Proyecto project : safe)
        {
            response.appendInt(project.id);
            response.appendString(project.name);
            response.appendInt(project.ownerUserId);
            response.appendBoolean(project.enabled);
            appendMovement(response, project.movement);
        }
    }

    private static void appendRooms(ServerMessage response, List<Sala> rooms)
    {
        List<Sala> safe = rooms == null
                ? new ArrayList<Sala>()
                : rooms;

        response.appendInt(safe.size());

        for(Sala room : safe)
        {
            response.appendInt(room.roomId);
            response.appendInt(room.rpgId);
            response.appendString(room.roomName);
            response.appendInt(room.overrideMask);
            response.appendInt(room.movementVersion);
        }
    }

    private static void appendStats(ServerMessage response, List<StatDefinition> stats)
    {
        List<StatDefinition> safe = stats == null
                ? new ArrayList<StatDefinition>()
                : stats;

        response.appendInt(safe.size());

        for(StatDefinition stat : safe)
        {
            response.appendInt(stat.id);
            response.appendInt(stat.rpgId);
            response.appendString(stat.key);
            response.appendString(stat.name);
            response.appendInt(scaled(stat.defaultValue));
            response.appendBoolean(stat.enabled);
            response.appendInt(stat.version);
        }
    }

    private static void appendStatValue(ServerMessage response, StatValue value)
    {
        response.appendBoolean(value != null);

        if(value == null) return;

        response.appendInt(value.statId);
        response.appendInt(value.rpgId);
        response.appendInt(value.userId);
        response.appendString(value.key);
        response.appendString(value.name);
        response.appendInt(scaled(value.baseValue));
        response.appendInt(scaled(value.additiveTotal));
        response.appendInt(scaled(value.multiplierTotal));
        response.appendInt(scaled(value.effectiveValue));
        response.appendInt(value.version);
    }

    private static void appendContext(ServerMessage response, Contexto context)
    {
        response.appendBoolean(context != null);

        if(context == null) return;

        response.appendInt(context.roomId);
        response.appendInt(context.rpgId);
        response.appendString(context.rpgName);
        response.appendInt(context.ownerUserId);
        response.appendInt(context.globalVersion);
        response.appendInt(context.roomVersion);
        response.appendInt(context.overrideMask);
        appendMovement(response, context.movement);

        response.appendInt(context.actorUserId);
        response.appendInt(scaled(context.movementBudget));
        appendStatValue(response, context.movementStat);
    }

    private static void appendEncounterConfig(ServerMessage response, EncounterConfig config)
    {
        response.appendBoolean(config != null);

        if(config == null) return;

        response.appendInt(config.rpgId);
        response.appendInt(config.reconnectGraceSeconds);
        response.appendInt(config.returnGraceSeconds);
        response.appendInt(config.version);
    }

    private static void appendEncounterParticipant(
            ServerMessage response,
            EncounterParticipant participant)
    {
        response.appendInt(participant.userId);
        response.appendInt(participant.joinSequence);
        response.appendString(participant.entryType == null ? "" : participant.entryType);
        response.appendInt(participant.entryByUserId);
        response.appendString(participant.status == null ? "" : participant.status);
        response.appendString(participant.exitReason == null ? "" : participant.exitReason);
        response.appendInt((int)Math.min(Integer.MAX_VALUE, Math.max(0L, participant.reconnectDeadlineEpoch)));
        response.appendInt((int)Math.min(Integer.MAX_VALUE, Math.max(0L, participant.returnDeadlineEpoch)));
        response.appendBoolean(participant.hasSavedPosition);

        if(participant.hasSavedPosition)
        {
            response.appendInt(participant.savedX);
            response.appendInt(participant.savedY);
            response.appendInt(scaled(participant.savedZ));
        }
    }

    private static void appendEncounter(ServerMessage response, Encounter encounter)
    {
        response.appendBoolean(encounter != null);

        if(encounter == null) return;

        response.appendInt(encounter.id);
        response.appendInt(encounter.rpgId);
        response.appendInt(encounter.roomId);
        response.appendInt(encounter.createdByUserId);
        response.appendInt(encounter.initiatorUserId);
        response.appendString(encounter.creationMode == null ? "" : encounter.creationMode);
        response.appendString(encounter.status == null ? "" : encounter.status);
        response.appendInt(encounter.version);
        response.appendInt((int)Math.min(Integer.MAX_VALUE, Math.max(0L, encounter.createdAtEpoch)));
        response.appendInt((int)Math.min(Integer.MAX_VALUE, Math.max(0L, encounter.startedAtEpoch)));
        response.appendInt((int)Math.min(Integer.MAX_VALUE, Math.max(0L, encounter.endedAtEpoch)));

        List<EncounterParticipant> participants = encounter.participants == null
                ? new ArrayList<EncounterParticipant>()
                : encounter.participants;

        response.appendInt(participants.size());

        for(EncounterParticipant participant : participants)
            appendEncounterParticipant(response, participant);

        List<EncounterReservation> reservations = encounter.reservations == null
                ? new ArrayList<EncounterReservation>()
                : encounter.reservations;

        response.appendInt(reservations.size());

        for(EncounterReservation reservation : reservations)
        {
            response.appendInt(reservation.encounterId);
            response.appendInt(reservation.userId);
            response.appendString(reservation.status == null ? "" : reservation.status);
            response.appendInt(reservation.x);
            response.appendInt(reservation.y);
            response.appendInt(scaled(reservation.z));
        }
    }

}
