import { IMessageDataWrapper, IMessageParser } from '../../../../../api';

export type RpgEngineFurniturePolicy = 'respect-walkability' | 'block-all' | 'ignore';
export type RpgEngineConfirmationMode = 'confirm' | 'instant';
export type RpgEngineMovementSource = 'fixed' | 'stat';


export type RpgEngineEncounterStatus = 'draft' | 'active' | 'ended';
export type RpgEngineParticipantStatus = 'active' | 'returning' | 'disconnected' | 'left';
export type RpgEngineEncounterCreationMode = 'gm' | 'pvp' | 'system' | string;

export interface RpgEngineEncounterConfig
{
    rpgId: number;
    reconnectGraceSeconds: number;
    returnGraceSeconds: number;
    version: number;
}

export interface RpgEngineEncounterParticipant
{
    userId: number;
    joinSequence: number;
    entryType: string;
    entryByUserId: number;
    status: RpgEngineParticipantStatus;
    exitReason: string | null;
    reconnectDeadlineEpoch: number;
    returnDeadlineEpoch: number;
    hasSavedPosition: boolean;
    savedX: number;
    savedY: number;
    savedZ: number;
}

export interface RpgEngineEncounterReservation
{
    encounterId: number;
    userId: number;
    status: RpgEngineParticipantStatus;
    x: number;
    y: number;
    z: number;
}

export interface RpgEngineEncounter
{
    id: number;
    rpgId: number;
    roomId: number;
    createdByUserId: number;
    initiatorUserId: number;
    creationMode: RpgEngineEncounterCreationMode;
    status: RpgEngineEncounterStatus;
    version: number;
    createdAtEpoch: number;
    startedAtEpoch: number;
    endedAtEpoch: number;
    participants: RpgEngineEncounterParticipant[];
    reservations: RpgEngineEncounterReservation[];
}


export interface RpgEngineMovementConfig
{
    resourceName: string;
    baseMovement: number;
    directions: 4 | 8;
    orthogonalCost: number;
    diagonalCost: number;
    allowCornerCutting: boolean;
    maxStepHeight: number;
    allowFalling: boolean;
    furniturePolicy: RpgEngineFurniturePolicy;
    unknownFurnitureBlocks: boolean;
    confirmationMode: RpgEngineConfirmationMode;
    sourceMode: RpgEngineMovementSource;
    statId: number;
    version: number;
}

export interface RpgEngineProject
{
    id: number;
    name: string;
    ownerUserId: number;
    enabled: boolean;
    movement: RpgEngineMovementConfig;
}

export interface RpgEngineRoom
{
    roomId: number;
    rpgId: number;
    roomName: string;
    overrideMask: number;
    movementVersion: number;
}

export interface RpgEngineStatDefinition
{
    id: number;
    rpgId: number;
    key: string;
    name: string;
    defaultValue: number;
    enabled: boolean;
    version: number;
}

export interface RpgEngineStatValue
{
    statId: number;
    rpgId: number;
    userId: number;
    key: string;
    name: string;
    baseValue: number;
    additiveTotal: number;
    multiplierTotal: number;
    effectiveValue: number;
    version: number;
}

export interface RpgEngineContext
{
    roomId: number;
    rpgId: number;
    rpgName: string;
    ownerUserId: number;
    globalVersion: number;
    roomVersion: number;
    overrideMask: number;
    movement: RpgEngineMovementConfig;

    actorUserId: number;
    movementBudget: number;
    movementStat: RpgEngineStatValue | null;
}

export class RpgEngineResultParser implements IMessageParser
{
    private _action = 0;
    private _success = false;
    private _message = '';
    private _projects: RpgEngineProject[] = [];
    private _rooms: RpgEngineRoom[] = [];
    private _stats: RpgEngineStatDefinition[] = [];
    private _statValue: RpgEngineStatValue = null;
    private _context: RpgEngineContext = null;
    private _encounterConfig: RpgEngineEncounterConfig = null;
    private _encounter: RpgEngineEncounter = null;

    public flush(): boolean
    {
        this._action = 0;
        this._success = false;
        this._message = '';
        this._projects = [];
        this._rooms = [];
        this._stats = [];
        this._statValue = null;
        this._context = null;
        this._encounterConfig = null;
        this._encounter = null;

        return true;
    }

    private readMovement(wrapper: IMessageDataWrapper): RpgEngineMovementConfig
    {
        const resourceName = wrapper.readString();
        const baseMovement = wrapper.readInt() / 1000;
        const directions = wrapper.readInt() === 4 ? 4 : 8;
        const orthogonalCost = wrapper.readInt() / 1000;
        const diagonalCost = wrapper.readInt() / 1000;
        const allowCornerCutting = wrapper.readBoolean();
        const maxStepHeight = wrapper.readInt() / 1000;
        const allowFalling = wrapper.readBoolean();

        const furnitureCode = wrapper.readInt();
        const furniturePolicy: RpgEngineFurniturePolicy =
            furnitureCode === 1 ? 'block-all' :
                (furnitureCode === 2 ? 'ignore' : 'respect-walkability');

        const unknownFurnitureBlocks = wrapper.readBoolean();
        const confirmationMode: RpgEngineConfirmationMode =
            wrapper.readInt() === 1 ? 'instant' : 'confirm';

        const sourceMode: RpgEngineMovementSource =
            wrapper.readInt() === 1 ? 'stat' : 'fixed';

        const statId = wrapper.readInt();
        const version = wrapper.readInt();

        return {
            resourceName,
            baseMovement,
            directions,
            orthogonalCost,
            diagonalCost,
            allowCornerCutting,
            maxStepHeight,
            allowFalling,
            furniturePolicy,
            unknownFurnitureBlocks,
            confirmationMode,
            sourceMode,
            statId,
            version
        };
    }

    private readStatDefinition(wrapper: IMessageDataWrapper): RpgEngineStatDefinition
    {
        return {
            id: wrapper.readInt(),
            rpgId: wrapper.readInt(),
            key: wrapper.readString(),
            name: wrapper.readString(),
            defaultValue: wrapper.readInt() / 1000,
            enabled: wrapper.readBoolean(),
            version: wrapper.readInt()
        };
    }

    private readStatValue(wrapper: IMessageDataWrapper): RpgEngineStatValue
    {
        if(!wrapper.readBoolean()) return null;

        return {
            statId: wrapper.readInt(),
            rpgId: wrapper.readInt(),
            userId: wrapper.readInt(),
            key: wrapper.readString(),
            name: wrapper.readString(),
            baseValue: wrapper.readInt() / 1000,
            additiveTotal: wrapper.readInt() / 1000,
            multiplierTotal: wrapper.readInt() / 1000,
            effectiveValue: wrapper.readInt() / 1000,
            version: wrapper.readInt()
        };
    }

    public parse(wrapper: IMessageDataWrapper): boolean
    {
        if(!wrapper) return false;

        this._action = wrapper.readInt();
        this._success = wrapper.readBoolean();
        this._message = wrapper.readString();

        const projects: RpgEngineProject[] = [];
        const projectCount = Math.max(0, wrapper.readInt());

        for(let i = 0; i < projectCount; i++)
        {
            projects.push({
                id: wrapper.readInt(),
                name: wrapper.readString(),
                ownerUserId: wrapper.readInt(),
                enabled: wrapper.readBoolean(),
                movement: this.readMovement(wrapper)
            });
        }

        const rooms: RpgEngineRoom[] = [];
        const roomCount = Math.max(0, wrapper.readInt());

        for(let i = 0; i < roomCount; i++)
        {
            rooms.push({
                roomId: wrapper.readInt(),
                rpgId: wrapper.readInt(),
                roomName: wrapper.readString(),
                overrideMask: wrapper.readInt(),
                movementVersion: wrapper.readInt()
            });
        }

        const stats: RpgEngineStatDefinition[] = [];
        const statCount = Math.max(0, wrapper.readInt());

        for(let i = 0; i < statCount; i++)
        {
            stats.push(this.readStatDefinition(wrapper));
        }

        const statValue = this.readStatValue(wrapper);

        let context: RpgEngineContext = null;

        if(wrapper.readBoolean())
        {
            context = {
                roomId: wrapper.readInt(),
                rpgId: wrapper.readInt(),
                rpgName: wrapper.readString(),
                ownerUserId: wrapper.readInt(),
                globalVersion: wrapper.readInt(),
                roomVersion: wrapper.readInt(),
                overrideMask: wrapper.readInt(),
                movement: this.readMovement(wrapper),

                actorUserId: wrapper.readInt(),
                movementBudget: wrapper.readInt() / 1000,
                movementStat: this.readStatValue(wrapper)
            };
        }

        let encounterConfig: RpgEngineEncounterConfig = null;

        if(wrapper.readBoolean())
        {
            encounterConfig = {
                rpgId: wrapper.readInt(),
                reconnectGraceSeconds: wrapper.readInt(),
                returnGraceSeconds: wrapper.readInt(),
                version: wrapper.readInt()
            };
        }

        let encounter: RpgEngineEncounter = null;

        if(wrapper.readBoolean())
        {
            const id = wrapper.readInt();
            const rpgId = wrapper.readInt();
            const roomId = wrapper.readInt();
            const createdByUserId = wrapper.readInt();
            const initiatorUserId = wrapper.readInt();
            const creationMode = wrapper.readString() as RpgEngineEncounterCreationMode;
            const status = wrapper.readString() as RpgEngineEncounterStatus;
            const version = wrapper.readInt();
            const createdAtEpoch = wrapper.readInt();
            const startedAtEpoch = wrapper.readInt();
            const endedAtEpoch = wrapper.readInt();
            const participantCount = Math.max(0, wrapper.readInt());
            const participants: RpgEngineEncounterParticipant[] = [];

            for(let i = 0; i < participantCount; i++)
            {
                const userId = wrapper.readInt();
                const joinSequence = wrapper.readInt();
                const entryType = wrapper.readString();
                const entryByUserId = wrapper.readInt();
                const participantStatus = wrapper.readString() as RpgEngineParticipantStatus;
                const exitReason = wrapper.readString();
                const reconnectDeadlineEpoch = wrapper.readInt();
                const returnDeadlineEpoch = wrapper.readInt();
                const hasSavedPosition = wrapper.readBoolean();

                let savedX = 0;
                let savedY = 0;
                let savedZ = 0;

                if(hasSavedPosition)
                {
                    savedX = wrapper.readInt();
                    savedY = wrapper.readInt();
                    savedZ = wrapper.readInt() / 1000;
                }

                participants.push({
                    userId,
                    joinSequence,
                    entryType,
                    entryByUserId,
                    status: participantStatus,
                    exitReason: exitReason.length ? exitReason : null,
                    reconnectDeadlineEpoch,
                    returnDeadlineEpoch,
                    hasSavedPosition,
                    savedX,
                    savedY,
                    savedZ
                });
            }

            const reservationCount = Math.max(0, wrapper.readInt());
            const reservations: RpgEngineEncounterReservation[] = [];

            for(let i = 0; i < reservationCount; i++)
            {
                reservations.push({
                    encounterId: wrapper.readInt(),
                    userId: wrapper.readInt(),
                    status: wrapper.readString() as RpgEngineParticipantStatus,
                    x: wrapper.readInt(),
                    y: wrapper.readInt(),
                    z: wrapper.readInt() / 1000
                });
            }

            encounter = {
                id,
                rpgId,
                roomId,
                createdByUserId,
                initiatorUserId,
                creationMode,
                status,
                version,
                createdAtEpoch,
                startedAtEpoch,
                endedAtEpoch,
                participants,
                reservations
            };
        }

        this._projects = projects;
        this._rooms = rooms;
        this._stats = stats;
        this._statValue = statValue;
        this._context = context;
        this._encounterConfig = encounterConfig;
        this._encounter = encounter;

        return true;
    }

    public get action(): number { return this._action; }
    public get success(): boolean { return this._success; }
    public get message(): string { return this._message; }

    public get projects(): RpgEngineProject[]
    {
        return this._projects.map(project => ({
            ...project,
            movement: { ...project.movement }
        }));
    }

    public get rooms(): RpgEngineRoom[]
    {
        return this._rooms.map(room => ({ ...room }));
    }

    public get stats(): RpgEngineStatDefinition[]
    {
        return this._stats.map(stat => ({ ...stat }));
    }

    public get statValue(): RpgEngineStatValue
    {
        return this._statValue ? { ...this._statValue } : null;
    }

    public get context(): RpgEngineContext
    {
        if(!this._context) return null;

        return {
            ...this._context,
            movement: { ...this._context.movement },
            movementStat: this._context.movementStat
                ? { ...this._context.movementStat }
                : null
        };
    }

    public get encounterConfig(): RpgEngineEncounterConfig
    {
        return this._encounterConfig ? { ...this._encounterConfig } : null;
    }

    public get encounter(): RpgEngineEncounter
    {
        if(!this._encounter) return null;

        return {
            ...this._encounter,
            participants: this._encounter.participants.map(participant => ({ ...participant }))
        };
    }

}
