package com.retro.rpgengine.handlers;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.retro.rpgengine.ServicioRpgEngine;
import com.retro.rpgengine.RpgEnginePackets;
import com.retro.rpgengine.ServicioRpgEngine.Contexto;
import com.retro.rpgengine.ServicioRpgEngine.Movimiento;
import com.retro.rpgengine.ServicioRpgEngine.Proyecto;
import com.retro.rpgengine.ServicioRpgEngine.Sala;
import com.retro.rpgengine.ServicioRpgEngine.StatDefinition;
import com.retro.rpgengine.ServicioRpgEngine.StatValue;
import com.retro.rpgengine.ServicioRpgEngine.Encounter;
import com.retro.rpgengine.ServicioRpgEngine.EncounterConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RpgEngineCommandHandler extends MessageHandler
{
    private static final int CREATE_PROJECT = 1;
    private static final int LIST_PROJECTS = 2;
    private static final int ADD_CURRENT_ROOM = 3;
    private static final int LIST_ROOMS = 4;
    private static final int REMOVE_CURRENT_ROOM = 5;
    private static final int GET_CURRENT_CONTEXT = 6;
    private static final int SET_GLOBAL_MOVEMENT = 7;
    private static final int SET_ROOM_OVERRIDE = 8;
    private static final int CLEAR_ROOM_OVERRIDE = 9;

    private static final int CREATE_STAT = 10;
    private static final int LIST_STATS = 11;
    private static final int SET_PLAYER_STAT_BASE = 12;
    private static final int UPSERT_STAT_MODIFIER = 13;
    private static final int REMOVE_STAT_MODIFIER = 14;
    private static final int GET_STAT_VALUE = 15;
    private static final int BIND_MOVEMENT_STAT = 16;
    private static final int SET_MOVEMENT_FIXED_SOURCE = 17;

    private static final int GET_ENCOUNTER_CONFIG = 19;
    private static final int SET_ENCOUNTER_CONFIG = 20;
    private static final int CREATE_ENCOUNTER = 21;
    private static final int START_ENCOUNTER = 22;
    private static final int GET_ENCOUNTER = 23;
    private static final int GET_MY_ENCOUNTER = 24;
    private static final int ADD_ENCOUNTER_PARTICIPANT = 25;
    private static final int MARK_PARTICIPANT_DISCONNECTED = 26;
    private static final int MARK_PARTICIPANT_RETURNING = 27;
    private static final int MARK_PARTICIPANT_ACTIVE = 28;
    private static final int FLEE_ENCOUNTER = 29;
    private static final int REMOVE_ENCOUNTER_PARTICIPANT = 30;
    private static final int END_ENCOUNTER = 31;
    private static final int START_PVP_ENCOUNTER = 32;

    @Override
    public void handle()
    {
        if(this.client == null || this.client.getHabbo() == null) return;

        Habbo habbo = this.client.getHabbo();
        int userId = habbo.getHabboInfo().getId();
        Room currentRoom = habbo.getHabboInfo().getCurrentRoom();
        int action = this.packet.readInt().intValue();

        boolean success = false;
        String message = "ok";
        List<Proyecto> projects = Collections.emptyList();
        List<Sala> rooms = Collections.emptyList();
        List<StatDefinition> stats = Collections.emptyList();
        StatValue statValue = null;
        Contexto context = null;
        EncounterConfig encounterConfig = null;
        Encounter encounter = null;

        try
        {
            switch(action)
            {
                case CREATE_PROJECT:
                {
                    Proyecto project = ServicioRpgEngine.createProject(userId, this.packet.readString());
                    projects = Collections.singletonList(project);
                    success = true;
                    break;
                }

                case LIST_PROJECTS:
                {
                    projects = ServicioRpgEngine.listProjects(userId);
                    success = true;
                    break;
                }

                case ADD_CURRENT_ROOM:
                {
                    int rpgId = this.packet.readInt().intValue();
                    ServicioRpgEngine.registerRoom(userId, rpgId, habbo, currentRoom);
                    rooms = ServicioRpgEngine.listRooms(userId, rpgId);
                    context = currentContext(currentRoom, userId, rpgId);
                    success = true;
                    break;
                }

                case LIST_ROOMS:
                {
                    int rpgId = this.packet.readInt().intValue();
                    rooms = ServicioRpgEngine.listRooms(userId, rpgId);
                    success = true;
                    break;
                }

                case REMOVE_CURRENT_ROOM:
                {
                    int rpgId = this.packet.readInt().intValue();
                    ServicioRpgEngine.removeRoom(userId, rpgId, habbo, currentRoom);
                    rooms = ServicioRpgEngine.listRooms(userId, rpgId);
                    success = true;
                    break;
                }

                case GET_CURRENT_CONTEXT:
                {
                    if(currentRoom != null)
                        context = ServicioRpgEngine.getContext(currentRoom.getId(), userId);

                    encounter = ServicioRpgEngine.getCurrentEncounterForUser(userId);
                    success = true;
                    break;
                }

                case SET_GLOBAL_MOVEMENT:
                {
                    int rpgId = this.packet.readInt().intValue();
                    Movimiento movement = readMovement();
                    Proyecto project = ServicioRpgEngine.setGlobalMovement(userId, rpgId, movement);
                    projects = Collections.singletonList(project);
                    context = currentContext(currentRoom, userId, rpgId);
                    success = true;
                    break;
                }

                case SET_ROOM_OVERRIDE:
                {
                    int rpgId = this.packet.readInt().intValue();
                    int mask = this.packet.readInt().intValue();
                    Movimiento movement = readMovement();
                    context = ServicioRpgEngine.setRoomOverrides(
                            userId, rpgId, habbo, currentRoom, mask, movement);
                    rooms = ServicioRpgEngine.listRooms(userId, rpgId);
                    success = true;
                    break;
                }

                case CLEAR_ROOM_OVERRIDE:
                {
                    int rpgId = this.packet.readInt().intValue();
                    context = ServicioRpgEngine.clearRoomOverrides(
                            userId, rpgId, habbo, currentRoom);
                    rooms = ServicioRpgEngine.listRooms(userId, rpgId);
                    success = true;
                    break;
                }

                case CREATE_STAT:
                {
                    int rpgId = this.packet.readInt().intValue();
                    String key = this.packet.readString();
                    String name = this.packet.readString();
                    double defaultValue = this.packet.readInt().intValue() / 1000.0;

                    StatDefinition stat = ServicioRpgEngine.createStat(
                            userId, rpgId, key, name, defaultValue);

                    stats = Collections.singletonList(stat);
                    success = true;
                    break;
                }

                case LIST_STATS:
                {
                    int rpgId = this.packet.readInt().intValue();
                    stats = ServicioRpgEngine.listStats(userId, rpgId);
                    success = true;
                    break;
                }

                case SET_PLAYER_STAT_BASE:
                {
                    int rpgId = this.packet.readInt().intValue();
                    int targetUserId = this.packet.readInt().intValue();
                    int statId = this.packet.readInt().intValue();
                    double value = this.packet.readInt().intValue() / 1000.0;

                    statValue = ServicioRpgEngine.setPlayerStatBase(
                            userId, rpgId, targetUserId, statId, value);

                    if(targetUserId == userId)
                        context = currentContext(currentRoom, userId, rpgId);

                    success = true;
                    break;
                }

                case UPSERT_STAT_MODIFIER:
                {
                    int rpgId = this.packet.readInt().intValue();
                    int targetUserId = this.packet.readInt().intValue();
                    int statId = this.packet.readInt().intValue();
                    String sourceType = this.packet.readString();
                    String sourceKey = this.packet.readString();
                    String operation = this.packet.readString();
                    double value = this.packet.readInt().intValue() / 1000.0;

                    statValue = ServicioRpgEngine.upsertStatModifier(
                            userId,
                            rpgId,
                            targetUserId,
                            statId,
                            sourceType,
                            sourceKey,
                            operation,
                            value
                    );

                    if(targetUserId == userId)
                        context = currentContext(currentRoom, userId, rpgId);

                    success = true;
                    break;
                }

                case REMOVE_STAT_MODIFIER:
                {
                    int rpgId = this.packet.readInt().intValue();
                    int targetUserId = this.packet.readInt().intValue();
                    int statId = this.packet.readInt().intValue();
                    String sourceType = this.packet.readString();
                    String sourceKey = this.packet.readString();

                    statValue = ServicioRpgEngine.removeStatModifier(
                            userId,
                            rpgId,
                            targetUserId,
                            statId,
                            sourceType,
                            sourceKey
                    );

                    if(targetUserId == userId)
                        context = currentContext(currentRoom, userId, rpgId);

                    success = true;
                    break;
                }

                case GET_STAT_VALUE:
                {
                    int rpgId = this.packet.readInt().intValue();
                    int targetUserId = this.packet.readInt().intValue();
                    int statId = this.packet.readInt().intValue();

                    statValue = ServicioRpgEngine.getStatValueForOwner(
                            userId, rpgId, targetUserId, statId);

                    success = true;
                    break;
                }

                case BIND_MOVEMENT_STAT:
                {
                    int rpgId = this.packet.readInt().intValue();
                    int statId = this.packet.readInt().intValue();

                    Proyecto project = ServicioRpgEngine.bindMovementStat(userId, rpgId, statId);
                    projects = Collections.singletonList(project);
                    context = currentContext(currentRoom, userId, rpgId);
                    success = true;
                    break;
                }

                case SET_MOVEMENT_FIXED_SOURCE:
                {
                    int rpgId = this.packet.readInt().intValue();
                    double value = this.packet.readInt().intValue() / 1000.0;

                    Proyecto project = ServicioRpgEngine.setMovementFixedSource(userId, rpgId, value);
                    projects = Collections.singletonList(project);
                    context = currentContext(currentRoom, userId, rpgId);
                    success = true;
                    break;
                }


                case GET_ENCOUNTER_CONFIG:
                {
                    int rpgId = this.packet.readInt().intValue();
                    encounterConfig = ServicioRpgEngine.getEncounterConfig(userId, rpgId);
                    success = true;
                    break;
                }

                case SET_ENCOUNTER_CONFIG:
                {
                    int rpgId = this.packet.readInt().intValue();
                    int reconnectGraceSeconds = this.packet.readInt().intValue();
                    int returnGraceSeconds = this.packet.readInt().intValue();

                    encounterConfig = ServicioRpgEngine.setEncounterConfig(
                            userId, rpgId, reconnectGraceSeconds, returnGraceSeconds);

                    success = true;
                    break;
                }

                case CREATE_ENCOUNTER:
                {
                    int rpgId = this.packet.readInt().intValue();
                    encounter = ServicioRpgEngine.createEncounter(userId, rpgId, currentRoom);
                    encounterConfig = ServicioRpgEngine.getEncounterConfig(userId, rpgId);
                    success = true;
                    break;
                }

                case START_ENCOUNTER:
                {
                    int encounterId = this.packet.readInt().intValue();
                    encounter = ServicioRpgEngine.startEncounter(userId, encounterId);
                    success = true;
                    break;
                }

                case GET_ENCOUNTER:
                {
                    int encounterId = this.packet.readInt().intValue();
                    encounter = ServicioRpgEngine.getEncounterForOwner(userId, encounterId);
                    success = true;
                    break;
                }

                case GET_MY_ENCOUNTER:
                {
                    int rpgId = this.packet.readInt().intValue();
                    encounter = ServicioRpgEngine.getMyCurrentEncounter(userId, rpgId);
                    success = true;
                    break;
                }

                case ADD_ENCOUNTER_PARTICIPANT:
                {
                    int encounterId = this.packet.readInt().intValue();
                    int targetUserId = this.packet.readInt().intValue();
                    encounter = ServicioRpgEngine.addEncounterParticipant(
                            userId, encounterId, targetUserId);
                    success = true;
                    break;
                }

                case MARK_PARTICIPANT_DISCONNECTED:
                {
                    int encounterId = this.packet.readInt().intValue();
                    int targetUserId = this.packet.readInt().intValue();
                    encounter = ServicioRpgEngine.markParticipantDisconnected(
                            userId, encounterId, targetUserId);
                    success = true;
                    break;
                }

                case MARK_PARTICIPANT_RETURNING:
                {
                    int encounterId = this.packet.readInt().intValue();
                    int targetUserId = this.packet.readInt().intValue();
                    encounter = ServicioRpgEngine.markParticipantReturning(
                            userId, encounterId, targetUserId);
                    success = true;
                    break;
                }

                case MARK_PARTICIPANT_ACTIVE:
                {
                    int encounterId = this.packet.readInt().intValue();
                    int targetUserId = this.packet.readInt().intValue();
                    encounter = ServicioRpgEngine.markParticipantActive(
                            userId, encounterId, targetUserId);
                    success = true;
                    break;
                }

                case FLEE_ENCOUNTER:
                {
                    int encounterId = this.packet.readInt().intValue();
                    encounter = ServicioRpgEngine.fleeEncounter(userId, encounterId);
                    success = true;
                    break;
                }

                case REMOVE_ENCOUNTER_PARTICIPANT:
                {
                    int encounterId = this.packet.readInt().intValue();
                    int targetUserId = this.packet.readInt().intValue();
                    encounter = ServicioRpgEngine.removeEncounterParticipant(
                            userId, encounterId, targetUserId);
                    success = true;
                    break;
                }

                case END_ENCOUNTER:
                {
                    int encounterId = this.packet.readInt().intValue();
                    encounter = ServicioRpgEngine.endEncounter(userId, encounterId);
                    success = true;
                    break;
                }

                case START_PVP_ENCOUNTER:
                {
                    int targetUserId = this.packet.readInt().intValue();
                    encounter = ServicioRpgEngine.startPvpEncounter(
                            userId, targetUserId, currentRoom);
                    context = currentRoom == null
                            ? null
                            : ServicioRpgEngine.getContext(currentRoom.getId(), userId);
                    success = true;
                    break;
                }

                default:
                    message = "unknown-action";
                    break;
            }
        }
        catch(ServicioRpgEngine.RpgEngineException error)
        {
            message = error.code;
        }
        catch(Exception error)
        {
            message = "internal-error";

            System.out.println(
                    "[RPGEngine] ERROR action " + action + ": " +
                    error.getClass().getName() + ": " + error.getMessage()
            );
            error.printStackTrace();
        }

        this.client.sendResponse(RpgEnginePackets.result(
                action,
                success,
                message,
                projects,
                rooms,
                stats,
                statValue,
                context,
                encounterConfig,
                encounter
        ));
    }

    private static Contexto currentContext(Room room, int actorUserId, int rpgId) throws Exception
    {
        if(room == null) return null;

        Contexto context = ServicioRpgEngine.getContext(room.getId(), actorUserId);

        if(context == null || context.rpgId != rpgId) return null;

        return context;
    }

    private Movimiento readMovement()
    {
        Movimiento movement = new Movimiento();

        movement.resourceName = this.packet.readString();
        movement.baseMovement = this.packet.readInt().intValue() / 1000.0;
        movement.directions = this.packet.readInt().intValue();
        movement.orthogonalCost = this.packet.readInt().intValue() / 1000.0;
        movement.diagonalCost = this.packet.readInt().intValue() / 1000.0;
        movement.allowCornerCutting = this.packet.readInt().intValue() == 1;
        movement.maxStepHeight = this.packet.readInt().intValue() / 1000.0;
        movement.allowFalling = this.packet.readInt().intValue() == 1;
        movement.furniturePolicy = furniturePolicy(this.packet.readInt().intValue());
        movement.unknownFurnitureBlocks = this.packet.readInt().intValue() == 1;
        movement.confirmationMode = confirmationMode(this.packet.readInt().intValue());
        movement.sourceMode = movementSource(this.packet.readInt().intValue());
        movement.statId = this.packet.readInt().intValue();

        return movement;
    }

    private static String furniturePolicy(int value)
    {
        if(value == 1) return "block-all";
        if(value == 2) return "ignore";
        return "respect-walkability";
    }

    private static String confirmationMode(int value)
    {
        return value == 1 ? "instant" : "confirm";
    }

    private static String movementSource(int value)
    {
        return value == 1 ? "stat" : "fixed";
    }

}
