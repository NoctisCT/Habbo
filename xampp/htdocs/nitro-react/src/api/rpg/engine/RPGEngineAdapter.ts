import {
    RpgEngineCommandComposer,
    RpgEngineContext,
    RpgEngineMovementConfig,
    RpgEngineProject,
    RpgEngineResultEvent,
    RpgEngineRoom,
    RpgEngineStatDefinition,
    RpgEngineStatValue,
    RpgEngineEncounter,
    RpgEngineEncounterConfig
} from '@nitrots/nitro-renderer';
import { GetCommunication } from '../../nitro/GetCommunication';
import { SendMessageComposer } from '../../nitro/SendMessageComposer';
import { GetRoomEngine } from '../../nitro/room/GetRoomEngine';

const ACTION_CREATE_PROJECT = 1;
const ACTION_LIST_PROJECTS = 2;
const ACTION_ADD_CURRENT_ROOM = 3;
const ACTION_LIST_ROOMS = 4;
const ACTION_REMOVE_CURRENT_ROOM = 5;
const ACTION_GET_CURRENT_CONTEXT = 6;
const ACTION_SET_GLOBAL_MOVEMENT = 7;
const ACTION_SET_ROOM_OVERRIDE = 8;
const ACTION_CLEAR_ROOM_OVERRIDE = 9;

const ACTION_CREATE_STAT = 10;
const ACTION_LIST_STATS = 11;
const ACTION_SET_PLAYER_STAT_BASE = 12;
const ACTION_UPSERT_STAT_MODIFIER = 13;
const ACTION_REMOVE_STAT_MODIFIER = 14;
const ACTION_GET_STAT_VALUE = 15;
const ACTION_BIND_MOVEMENT_STAT = 16;
const ACTION_SET_MOVEMENT_FIXED_SOURCE = 17;

const ACTION_GET_ENCOUNTER_CONFIG = 19;
const ACTION_SET_ENCOUNTER_CONFIG = 20;
const ACTION_CREATE_ENCOUNTER = 21;
const ACTION_START_ENCOUNTER = 22;
const ACTION_GET_ENCOUNTER = 23;
const ACTION_GET_MY_ENCOUNTER = 24;
const ACTION_ADD_ENCOUNTER_PARTICIPANT = 25;
const ACTION_MARK_PARTICIPANT_DISCONNECTED = 26;
const ACTION_MARK_PARTICIPANT_RETURNING = 27;
const ACTION_MARK_PARTICIPANT_ACTIVE = 28;
const ACTION_FLEE_ENCOUNTER = 29;
const ACTION_REMOVE_ENCOUNTER_PARTICIPANT = 30;
const ACTION_END_ENCOUNTER = 31;
const ACTION_START_PVP_ENCOUNTER = 32;
const ACTION_ENCOUNTER_PUSH = 33;

const OV_RESOURCE = 1;
const OV_BASE = 2;
const OV_DIRECTIONS = 4;
const OV_ORTHOGONAL = 8;
const OV_DIAGONAL = 16;
const OV_CORNERS = 32;
const OV_STEP_HEIGHT = 64;
const OV_FALLING = 128;
const OV_FURNITURE = 256;
const OV_UNKNOWN_BLOCKS = 512;
const OV_CONFIRMATION = 1024;

type MovementUpdate = Partial<Omit<RpgEngineMovementConfig, 'version' | 'sourceMode' | 'statId'>>;
type StatModifierOperation = 'add' | 'multiply';

interface RpgEngineResponseSnapshot
{
    action: number;
    success: boolean;
    message: string;
    projects: RpgEngineProject[];
    rooms: RpgEngineRoom[];
    stats: RpgEngineStatDefinition[];
    statValue: RpgEngineStatValue | null;
    context: RpgEngineContext | null;
    encounterConfig: RpgEngineEncounterConfig | null;
    encounter: RpgEngineEncounter | null;
}

interface RpgEngineStateSnapshot
{
    autoApply: boolean;
    activeRoomId: number;
    projects: RpgEngineProject[];
    rooms: RpgEngineRoom[];
    stats: RpgEngineStatDefinition[];
    context: RpgEngineContext | null;
    lastStatValue: RpgEngineStatValue | null;
    encounterConfig: RpgEngineEncounterConfig | null;
    encounter: RpgEngineEncounter | null;
    lastResponse: RpgEngineResponseSnapshot | null;
    appliedKey: string | null;
}

let installed = false;
let roomWatchTimer: number = null;
let lastObservedRoomId = -2;
let autoApply = true;
let projectsCache = new Map<number, RpgEngineProject>();
let roomsCache: RpgEngineRoom[] = [];
let statsCache = new Map<number, RpgEngineStatDefinition>();
let contextCache: RpgEngineContext = null;
let lastStatValue: RpgEngineStatValue = null;
let encounterConfigCache: RpgEngineEncounterConfig = null;
let encounterCache: RpgEngineEncounter = null;
let lastResponse: RpgEngineResponseSnapshot = null;
let appliedKey: string = null;
let appliedRoomId = -1;
let appliedRpgId = -1;
let actorUserIdCache = 0;

function cloneMovement(value: RpgEngineMovementConfig): RpgEngineMovementConfig
{
    return value ? { ...value } : null;
}

function cloneProject(value: RpgEngineProject): RpgEngineProject
{
    return value ? { ...value, movement: cloneMovement(value.movement) } : null;
}

function cloneStat(value: RpgEngineStatDefinition): RpgEngineStatDefinition
{
    return value ? { ...value } : null;
}

function cloneStatValue(value: RpgEngineStatValue): RpgEngineStatValue
{
    return value ? { ...value } : null;
}


function cloneEncounterConfig(value: RpgEngineEncounterConfig): RpgEngineEncounterConfig
{
    return value ? { ...value } : null;
}

function cloneEncounter(value: RpgEngineEncounter): RpgEngineEncounter
{
    return value ? {
        ...value,
        participants: value.participants.map(participant => ({ ...participant })),
        reservations: (value.reservations ?? []).map(reservation => ({ ...reservation }))
    } : null;
}

function cloneContext(value: RpgEngineContext): RpgEngineContext
{
    return value ? {
        ...value,
        movement: cloneMovement(value.movement),
        movementStat: cloneStatValue(value.movementStat)
    } : null;
}

function send(action: number, ...args: Array<string | number | boolean>): void
{
    SendMessageComposer(new RpgEngineCommandComposer(action, ...args));
}

function furnitureCode(value: string): number
{
    if(value === 'block-all') return 1;
    if(value === 'ignore') return 2;
    return 0;
}

function confirmationCode(value: string): number
{
    return value === 'instant' ? 1 : 0;
}

function movementSourceCode(value: string): number
{
    return value === 'stat' ? 1 : 0;
}

function scaled(value: number): number
{
    return Math.round(Number(value) * 1000);
}

function movementArgs(value: RpgEngineMovementConfig): Array<string | number | boolean>
{
    return [
        value.resourceName,
        scaled(value.baseMovement),
        value.directions,
        scaled(value.orthogonalCost),
        scaled(value.diagonalCost),
        value.allowCornerCutting ? 1 : 0,
        scaled(value.maxStepHeight),
        value.allowFalling ? 1 : 0,
        furnitureCode(value.furniturePolicy),
        value.unknownFurnitureBlocks ? 1 : 0,
        confirmationCode(value.confirmationMode),
        movementSourceCode(value.sourceMode),
        Math.trunc(value.statId || 0)
    ];
}

function getProject(rpgId: number): RpgEngineProject
{
    return projectsCache.get(Math.trunc(rpgId)) ?? null;
}

function movementApplyKey(context: RpgEngineContext): string
{
    return [
        context.roomId,
        context.rpgId,
        context.globalVersion,
        context.roomVersion,
        context.movementStat?.version ?? 0,
        scaled(context.movementBudget)
    ].join(':');
}


function ownParticipant(encounter: RpgEngineEncounter): any
{
    if(!encounter || actorUserIdCache <= 0) return null;

    return encounter.participants.find(
        participant => participant.userId === actorUserIdCache
    ) ?? null;
}

function encounterSuppressesMovement(context: RpgEngineContext): boolean
{
    if(!context || !encounterCache || encounterCache.status !== 'active')
        return false;

    const participant = ownParticipant(encounterCache);

    if(!participant || participant.status === 'left')
        return false;

    if(context.roomId !== encounterCache.roomId)
        return true;

    return participant.status !== 'active';
}

function suspendMovementGrid(): void
{
    const grid = (globalThis as any).HoloGrid;

    if(!grid) return;

    grid.endMovement?.();
    grid.stopFollow?.();
    grid.encounterBlocked?.([]);
    appliedKey = null;
    appliedRoomId = -1;
    appliedRpgId = -1;
}

function reconcileEncounterGrid(): void
{
    const grid = (globalThis as any).HoloGrid;

    if(!grid) return;

    const roomId = GetRoomEngine().activeRoomId;
    const encounter = encounterCache;

    if(!encounter || encounter.status !== 'active')
    {
        grid.encounterBlocked?.([]);
        grid.rejoinMarker?.(null);
        return;
    }

    const self = ownParticipant(encounter);

    if(!self || self.status === 'left')
    {
        grid.encounterBlocked?.([]);
        grid.rejoinMarker?.(null);
        return;
    }

    if(roomId !== encounter.roomId)
    {
        grid.encounterBlocked?.([]);
        grid.rejoinMarker?.(null);
        suspendMovementGrid();
        grid.off?.();
        return;
    }

    const reserved = (encounter.reservations ?? [])
        .filter(reservation => reservation.userId !== self.userId)
        .map(reservation => ({
            x: reservation.x,
            y: reservation.y
        }));

    grid.encounterBlocked?.(reserved);

    if(self.status === 'returning' && self.hasSavedPosition)
    {
        suspendMovementGrid();
        grid.on?.();
        grid.encounterBlocked?.(reserved);
        grid.rejoinMarker?.({
            x: self.savedX,
            y: self.savedY
        });
    }
    else
    {
        grid.rejoinMarker?.(null);
    }
}

function applyContext(context: RpgEngineContext): void
{
    if(!autoApply || !context) return;

    if(encounterSuppressesMovement(context))
    {
        reconcileEncounterGrid();
        return;
    }

    const key = movementApplyKey(context);

    if(key === appliedKey) return;

    const grid = (globalThis as any).HoloGrid;

    if(!grid)
    {
        console.warn('[RPGEngine] HoloGrid no esta instalado todavia.');
        return;
    }

    const movement = context.movement;
    const budget = Math.max(0, Number(context.movementBudget) || 0);

    grid.on?.();

    grid.rules?.({
        diagonals: movement.directions === 8,
        straightCost: movement.orthogonalCost,
        diagonalCost: movement.diagonalCost,
        passBetweenCorners: movement.allowCornerCutting,
        furniture: movement.furniturePolicy,
        unknownFurnitureBlocks: movement.unknownFurnitureBlocks,
        maxStepHeight: movement.maxStepHeight,
        allowFalling: movement.allowFalling
    });

    grid.showObstacles?.(true);

    const sameRuntime =
        appliedRoomId === context.roomId &&
        appliedRpgId === context.rpgId &&
        grid.movementSession?.()?.active === true;

    if(sameRuntime && typeof grid.updateMovementTotal === 'function')
    {
        grid.updateMovementTotal(budget);
    }
    else if(typeof grid.beginMovement === 'function')
    {
        grid.beginMovement(budget);
    }
    else
    {
        grid.follow?.(budget);
    }

    appliedKey = key;
    appliedRoomId = context.roomId;
    appliedRpgId = context.rpgId;

    if(context.movementStat)
    {
        const stat = context.movementStat;

        console.info(
            `[RPGEngine] ${ context.rpgName } | room ${ context.roomId } | ` +
            `${ stat.key } ${ budget } | effective ${ stat.effectiveValue } | ` +
            `base ${ stat.baseValue } | add ${ stat.additiveTotal } | ` +
            `x${ stat.multiplierTotal } | Movement applied`
        );
    }
    else
    {
        console.info(
            `[RPGEngine] ${ context.rpgName } | room ${ context.roomId } | ` +
            `${ movement.resourceName } ${ budget } | fixed Movement applied`
        );
    }
}

function clearAppliedContext(): void
{
    contextCache = null;
    appliedKey = null;
    appliedRoomId = -1;
    appliedRpgId = -1;

    const grid = (globalThis as any).HoloGrid;

    if(!grid) return;

    grid.endMovement?.();
    grid.stopFollow?.();
    grid.off?.();
}

function onServerResult(event: RpgEngineResultEvent): void
{
    const parser = event?.getParser();

    if(!parser) return;

    const projects = parser.projects;
    const rooms = parser.rooms;
    const stats = parser.stats;
    const statValue = parser.statValue;
    const context = parser.context;
    const encounterConfig = parser.encounterConfig;
    const encounter = parser.encounter;

    for(const project of projects)
    {
        projectsCache.set(project.id, cloneProject(project));
    }

    if(parser.action === ACTION_LIST_PROJECTS)
    {
        projectsCache = new Map(projects.map(project => [ project.id, cloneProject(project) ]));
    }

    if(rooms.length || parser.action === ACTION_LIST_ROOMS ||
        parser.action === ACTION_REMOVE_CURRENT_ROOM ||
        parser.action === ACTION_SET_ROOM_OVERRIDE ||
        parser.action === ACTION_CLEAR_ROOM_OVERRIDE)
    {
        roomsCache = rooms.map(room => ({ ...room }));
    }

    if(parser.action === ACTION_LIST_STATS)
    {
        statsCache = new Map(stats.map(stat => [ stat.id, cloneStat(stat) ]));
    }
    else
    {
        for(const stat of stats)
        {
            statsCache.set(stat.id, cloneStat(stat));
        }
    }

    if(statValue) lastStatValue = cloneStatValue(statValue);
    if(encounterConfig) encounterConfigCache = cloneEncounterConfig(encounterConfig);

    if(parser.success && parser.action === ACTION_FLEE_ENCOUNTER)
    {
        encounterCache = null;
    }
    else if(parser.success && parser.action === ACTION_ENCOUNTER_PUSH && encounter)
    {
        const pushed = cloneEncounter(encounter);
        const self = actorUserIdCache > 0
            ? pushed.participants.find(participant => participant.userId === actorUserIdCache)
            : null;

        encounterCache =
            (pushed.status === 'ended' || self?.status === 'left')
                ? null
                : pushed;
    }
    else if(encounter)
    {
        encounterCache = cloneEncounter(encounter);
    }
    else if(parser.success &&
        (parser.action === ACTION_GET_MY_ENCOUNTER ||
         parser.action === ACTION_GET_CURRENT_CONTEXT))
    {
        encounterCache = null;
    }

    if(context)
    {
        contextCache = cloneContext(context);
        actorUserIdCache = context.actorUserId;
    }
    else if(parser.success &&
        (parser.action === ACTION_GET_CURRENT_CONTEXT ||
         parser.action === ACTION_REMOVE_CURRENT_ROOM) &&
        !context)
    {
        clearAppliedContext();
    }

    lastResponse = {
        action: parser.action,
        success: parser.success,
        message: parser.message,
        projects: projects.map(cloneProject),
        rooms: rooms.map(room => ({ ...room })),
        stats: stats.map(cloneStat),
        statValue: cloneStatValue(statValue),
        context: cloneContext(context),
        encounterConfig: cloneEncounterConfig(encounterConfig),
        encounter: cloneEncounter(encounter)
    };

    if(parser.success)
    {
        reconcileEncounterGrid();
    }

    if(parser.success && context)
    {
        applyContext(context);
        reconcileEncounterGrid();
    }

    if(parser.success)
    {
        console.info('[RPGEngine]', lastResponse);
    }
    else
    {
        console.warn('[RPGEngine]', lastResponse);
    }
}

function snapshot(): RpgEngineStateSnapshot
{
    return {
        autoApply,
        activeRoomId: GetRoomEngine().activeRoomId,
        projects: Array.from(projectsCache.values()).map(cloneProject),
        rooms: roomsCache.map(room => ({ ...room })),
        stats: Array.from(statsCache.values()).map(cloneStat),
        context: cloneContext(contextCache),
        lastStatValue: cloneStatValue(lastStatValue),
        encounterConfig: cloneEncounterConfig(encounterConfigCache),
        encounter: cloneEncounter(encounterCache),
        lastResponse: lastResponse ? {
            ...lastResponse,
            projects: lastResponse.projects.map(cloneProject),
            rooms: lastResponse.rooms.map(room => ({ ...room })),
            stats: lastResponse.stats.map(cloneStat),
            statValue: cloneStatValue(lastResponse.statValue),
            context: cloneContext(lastResponse.context),
            encounterConfig: cloneEncounterConfig(lastResponse.encounterConfig),
            encounter: cloneEncounter(lastResponse.encounter)
        } : null,
        appliedKey
    };
}

function mergeMovement(base: RpgEngineMovementConfig, update: MovementUpdate): RpgEngineMovementConfig
{
    return {
        ...base,
        ...(update ?? {}),
        sourceMode: base.sourceMode,
        statId: base.statId,
        version: base.version
    };
}

function overrideMask(update: MovementUpdate): number
{
    let mask = 0;

    if(Object.prototype.hasOwnProperty.call(update, 'resourceName')) mask |= OV_RESOURCE;
    if(Object.prototype.hasOwnProperty.call(update, 'baseMovement')) mask |= OV_BASE;
    if(Object.prototype.hasOwnProperty.call(update, 'directions')) mask |= OV_DIRECTIONS;
    if(Object.prototype.hasOwnProperty.call(update, 'orthogonalCost')) mask |= OV_ORTHOGONAL;
    if(Object.prototype.hasOwnProperty.call(update, 'diagonalCost')) mask |= OV_DIAGONAL;
    if(Object.prototype.hasOwnProperty.call(update, 'allowCornerCutting')) mask |= OV_CORNERS;
    if(Object.prototype.hasOwnProperty.call(update, 'maxStepHeight')) mask |= OV_STEP_HEIGHT;
    if(Object.prototype.hasOwnProperty.call(update, 'allowFalling')) mask |= OV_FALLING;
    if(Object.prototype.hasOwnProperty.call(update, 'furniturePolicy')) mask |= OV_FURNITURE;
    if(Object.prototype.hasOwnProperty.call(update, 'unknownFurnitureBlocks')) mask |= OV_UNKNOWN_BLOCKS;
    if(Object.prototype.hasOwnProperty.call(update, 'confirmationMode')) mask |= OV_CONFIRMATION;

    return mask;
}

function createDebugApi()
{
    return {
        create: (name: string) =>
        {
            send(ACTION_CREATE_PROJECT, String(name ?? ''));
            return { sent: true, action: 'create', name };
        },

        projects: () =>
        {
            send(ACTION_LIST_PROJECTS);
            return { sent: true, action: 'projects' };
        },

        addCurrentRoom: (rpgId: number) =>
        {
            send(ACTION_ADD_CURRENT_ROOM, Math.trunc(rpgId));
            return { sent: true, action: 'add-current-room', rpgId: Math.trunc(rpgId) };
        },

        rooms: (rpgId: number) =>
        {
            send(ACTION_LIST_ROOMS, Math.trunc(rpgId));
            return { sent: true, action: 'rooms', rpgId: Math.trunc(rpgId) };
        },

        removeCurrentRoom: (rpgId: number) =>
        {
            send(ACTION_REMOVE_CURRENT_ROOM, Math.trunc(rpgId));
            return { sent: true, action: 'remove-current-room', rpgId: Math.trunc(rpgId) };
        },

        context: () =>
        {
            send(ACTION_GET_CURRENT_CONTEXT);
            return { sent: true, action: 'context' };
        },

        movement: (rpgId: number, update: MovementUpdate = {}) =>
        {
            const project = getProject(rpgId);

            if(!project)
            {
                return {
                    sent: false,
                    reason: 'project-not-cached',
                    hint: 'Ejecuta RPGEngine.projects() primero.'
                };
            }

            const movement = mergeMovement(project.movement, update);

            send(
                ACTION_SET_GLOBAL_MOVEMENT,
                Math.trunc(rpgId),
                ...movementArgs(movement)
            );

            return {
                sent: true,
                action: 'movement',
                rpgId: Math.trunc(rpgId),
                movement
            };
        },

        movementStat: (rpgId: number, statId: number) =>
        {
            send(
                ACTION_BIND_MOVEMENT_STAT,
                Math.trunc(rpgId),
                Math.trunc(statId)
            );

            return {
                sent: true,
                action: 'movement-stat',
                rpgId: Math.trunc(rpgId),
                statId: Math.trunc(statId)
            };
        },

        movementFixed: (rpgId: number, value: number = 4) =>
        {
            send(
                ACTION_SET_MOVEMENT_FIXED_SOURCE,
                Math.trunc(rpgId),
                scaled(value)
            );

            return {
                sent: true,
                action: 'movement-fixed',
                rpgId: Math.trunc(rpgId),
                value: Number(value)
            };
        },

        roomOverride: (rpgId: number, update: MovementUpdate = {}) =>
        {
            const project = getProject(rpgId);

            if(!project)
            {
                return {
                    sent: false,
                    reason: 'project-not-cached',
                    hint: 'Ejecuta RPGEngine.projects() primero.'
                };
            }

            const mask = overrideMask(update);
            const movement = mergeMovement(project.movement, update);

            send(
                ACTION_SET_ROOM_OVERRIDE,
                Math.trunc(rpgId),
                mask,
                ...movementArgs(movement)
            );

            return {
                sent: true,
                action: 'room-override',
                rpgId: Math.trunc(rpgId),
                mask,
                update
            };
        },

        clearRoomOverride: (rpgId: number) =>
        {
            send(ACTION_CLEAR_ROOM_OVERRIDE, Math.trunc(rpgId));

            return {
                sent: true,
                action: 'clear-room-override',
                rpgId: Math.trunc(rpgId)
            };
        },

        statCreate: (
            rpgId: number,
            key: string,
            name: string = key,
            defaultValue: number = 0
        ) =>
        {
            send(
                ACTION_CREATE_STAT,
                Math.trunc(rpgId),
                String(key ?? ''),
                String(name ?? ''),
                scaled(defaultValue)
            );

            return {
                sent: true,
                action: 'stat-create',
                rpgId: Math.trunc(rpgId),
                key,
                name,
                defaultValue: Number(defaultValue)
            };
        },

        stats: (rpgId: number) =>
        {
            send(ACTION_LIST_STATS, Math.trunc(rpgId));
            return { sent: true, action: 'stats', rpgId: Math.trunc(rpgId) };
        },

        statSet: (rpgId: number, userId: number, statId: number, value: number) =>
        {
            send(
                ACTION_SET_PLAYER_STAT_BASE,
                Math.trunc(rpgId),
                Math.trunc(userId),
                Math.trunc(statId),
                scaled(value)
            );

            return {
                sent: true,
                action: 'stat-set',
                rpgId: Math.trunc(rpgId),
                userId: Math.trunc(userId),
                statId: Math.trunc(statId),
                value: Number(value)
            };
        },

        statValue: (rpgId: number, userId: number, statId: number) =>
        {
            send(
                ACTION_GET_STAT_VALUE,
                Math.trunc(rpgId),
                Math.trunc(userId),
                Math.trunc(statId)
            );

            return {
                sent: true,
                action: 'stat-value',
                rpgId: Math.trunc(rpgId),
                userId: Math.trunc(userId),
                statId: Math.trunc(statId)
            };
        },

        statModifier: (
            rpgId: number,
            userId: number,
            statId: number,
            sourceType: string,
            sourceKey: string,
            operation: StatModifierOperation,
            value: number
        ) =>
        {
            send(
                ACTION_UPSERT_STAT_MODIFIER,
                Math.trunc(rpgId),
                Math.trunc(userId),
                Math.trunc(statId),
                String(sourceType ?? ''),
                String(sourceKey ?? ''),
                String(operation ?? ''),
                scaled(value)
            );

            return {
                sent: true,
                action: 'stat-modifier',
                rpgId: Math.trunc(rpgId),
                userId: Math.trunc(userId),
                statId: Math.trunc(statId),
                sourceType,
                sourceKey,
                operation,
                value: Number(value)
            };
        },

        statModifierRemove: (
            rpgId: number,
            userId: number,
            statId: number,
            sourceType: string,
            sourceKey: string
        ) =>
        {
            send(
                ACTION_REMOVE_STAT_MODIFIER,
                Math.trunc(rpgId),
                Math.trunc(userId),
                Math.trunc(statId),
                String(sourceType ?? ''),
                String(sourceKey ?? '')
            );

            return {
                sent: true,
                action: 'stat-modifier-remove',
                rpgId: Math.trunc(rpgId),
                userId: Math.trunc(userId),
                statId: Math.trunc(statId),
                sourceType,
                sourceKey
            };
        },


        encounterConfig: (rpgId: number) =>
        {
            send(ACTION_GET_ENCOUNTER_CONFIG, Math.trunc(rpgId));

            return {
                sent: true,
                action: 'encounter-config',
                rpgId: Math.trunc(rpgId)
            };
        },

        encounterConfigSet: (
            rpgId: number,
            reconnectGraceSeconds: number,
            returnGraceSeconds: number
        ) =>
        {
            send(
                ACTION_SET_ENCOUNTER_CONFIG,
                Math.trunc(rpgId),
                Math.max(0, Math.trunc(reconnectGraceSeconds)),
                Math.max(0, Math.trunc(returnGraceSeconds))
            );

            return {
                sent: true,
                action: 'encounter-config-set',
                rpgId: Math.trunc(rpgId),
                reconnectGraceSeconds: Math.max(0, Math.trunc(reconnectGraceSeconds)),
                returnGraceSeconds: Math.max(0, Math.trunc(returnGraceSeconds))
            };
        },

        encounterCreate: (rpgId: number) =>
        {
            send(ACTION_CREATE_ENCOUNTER, Math.trunc(rpgId));

            return {
                sent: true,
                action: 'encounter-create',
                rpgId: Math.trunc(rpgId)
            };
        },

        encounterStart: (encounterId: number) =>
        {
            send(ACTION_START_ENCOUNTER, Math.trunc(encounterId));
            return { sent: true, action: 'encounter-start', encounterId: Math.trunc(encounterId) };
        },

        encounterGet: (encounterId: number) =>
        {
            send(ACTION_GET_ENCOUNTER, Math.trunc(encounterId));
            return { sent: true, action: 'encounter-get', encounterId: Math.trunc(encounterId) };
        },

        encounterMine: (rpgId: number) =>
        {
            send(ACTION_GET_MY_ENCOUNTER, Math.trunc(rpgId));
            return { sent: true, action: 'encounter-mine', rpgId: Math.trunc(rpgId) };
        },

        encounterAdd: (encounterId: number, userId: number) =>
        {
            send(ACTION_ADD_ENCOUNTER_PARTICIPANT, Math.trunc(encounterId), Math.trunc(userId));

            return {
                sent: true,
                action: 'encounter-add',
                encounterId: Math.trunc(encounterId),
                userId: Math.trunc(userId)
            };
        },

        encounterDebugDisconnected: (encounterId: number, userId: number) =>
        {
            send(ACTION_MARK_PARTICIPANT_DISCONNECTED, Math.trunc(encounterId), Math.trunc(userId));

            return {
                sent: true,
                action: 'encounter-debug-disconnected',
                encounterId: Math.trunc(encounterId),
                userId: Math.trunc(userId)
            };
        },

        encounterDebugReturning: (encounterId: number, userId: number) =>
        {
            send(ACTION_MARK_PARTICIPANT_RETURNING, Math.trunc(encounterId), Math.trunc(userId));

            return {
                sent: true,
                action: 'encounter-debug-returning',
                encounterId: Math.trunc(encounterId),
                userId: Math.trunc(userId)
            };
        },

        encounterDebugActive: (encounterId: number, userId: number) =>
        {
            send(ACTION_MARK_PARTICIPANT_ACTIVE, Math.trunc(encounterId), Math.trunc(userId));

            return {
                sent: true,
                action: 'encounter-debug-active',
                encounterId: Math.trunc(encounterId),
                userId: Math.trunc(userId)
            };
        },

        encounterFlee: (encounterId: number) =>
        {
            send(ACTION_FLEE_ENCOUNTER, Math.trunc(encounterId));
            return { sent: true, action: 'encounter-flee', encounterId: Math.trunc(encounterId) };
        },

        encounterRemove: (encounterId: number, userId: number) =>
        {
            send(ACTION_REMOVE_ENCOUNTER_PARTICIPANT, Math.trunc(encounterId), Math.trunc(userId));

            return {
                sent: true,
                action: 'encounter-remove',
                encounterId: Math.trunc(encounterId),
                userId: Math.trunc(userId)
            };
        },

        encounterPvP: (targetUserId: number) =>
        {
            send(ACTION_START_PVP_ENCOUNTER, Math.trunc(targetUserId));

            return {
                sent: true,
                action: 'encounter-pvp',
                targetUserId: Math.trunc(targetUserId)
            };
        },

        encounterEnd: (encounterId: number) =>
        {
            send(ACTION_END_ENCOUNTER, Math.trunc(encounterId));
            return { sent: true, action: 'encounter-end', encounterId: Math.trunc(encounterId) };
        },

        auto: (enabled: boolean = true) =>
        {
            autoApply = !!enabled;

            if(autoApply && contextCache) applyContext(contextCache);

            return snapshot();
        },

        state: snapshot,

        help: () => ({
            create: 'RPGEngine.create("Pokemon RPG")',
            projects: 'RPGEngine.projects()',
            addCurrentRoom: 'RPGEngine.addCurrentRoom(rpgId)',
            rooms: 'RPGEngine.rooms(rpgId)',
            movement: 'RPGEngine.movement(rpgId, { directions:8, diagonalCost:1 })',
            movementStat: 'RPGEngine.movementStat(rpgId, statId)',
            movementFixed: 'RPGEngine.movementFixed(rpgId, 4)',
            roomOverride: 'RPGEngine.roomOverride(rpgId, { directions:4 })',
            clearRoomOverride: 'RPGEngine.clearRoomOverride(rpgId)',
            statCreate: 'RPGEngine.statCreate(rpgId, "VEL", "Velocidad", 4)',
            stats: 'RPGEngine.stats(rpgId)',
            statSet: 'RPGEngine.statSet(rpgId, userId, statId, 5)',
            statValue: 'RPGEngine.statValue(rpgId, userId, statId)',
            statModifier: 'RPGEngine.statModifier(rpgId, userId, statId, "ability", "haste", "multiply", 1.5)',
            statModifierRemove: 'RPGEngine.statModifierRemove(rpgId, userId, statId, "ability", "haste")',
            context: 'RPGEngine.context()',
            encounterConfig: 'RPGEngine.encounterConfig(rpgId)',
            encounterConfigSet: 'RPGEngine.encounterConfigSet(rpgId, reconnectSeconds, returnSeconds)',
            encounterCreate: 'RPGEngine.encounterCreate(rpgId)',
            encounterStart: 'RPGEngine.encounterStart(encounterId)',
            encounterGet: 'RPGEngine.encounterGet(encounterId)',
            encounterMine: 'RPGEngine.encounterMine(rpgId)',
            encounterAdd: 'RPGEngine.encounterAdd(encounterId, userId)',
            encounterFlee: 'RPGEngine.encounterFlee(encounterId)',
            encounterPvP: 'RPGEngine.encounterPvP(targetUserId)',
            encounterEnd: 'RPGEngine.encounterEnd(encounterId)',
            state: 'RPGEngine.state()'
        })
    };
}

export function InstallRpgEngineAdapter(): boolean
{
    if(installed) return true;

    const communication = GetCommunication();

    if(!communication) return false;

    communication.registerMessageEvent(
        new RpgEngineResultEvent(onServerResult)
    );

    (globalThis as any).RPGEngine = createDebugApi();

    if(roomWatchTimer === null)
    {
        roomWatchTimer = window.setInterval(() =>
        {
            const roomId = GetRoomEngine().activeRoomId;

            if(roomId === lastObservedRoomId) return;

            lastObservedRoomId = roomId;
            appliedKey = null;
            appliedRoomId = -1;
            appliedRpgId = -1;

            suspendMovementGrid();

            if(roomId >= 0)
            {
                window.setTimeout(() => send(ACTION_GET_CURRENT_CONTEXT), 350);
            }
            else
            {
                clearAppliedContext();
            }
        }, 500);
    }

    installed = true;

    return true;
}
