import {
    GetHoloGridWalkabilityComposer,
    GridEngine,
    GridGeometry,
    GridMap,
    GridMapCell,
    GridMapSummary,
    GridMovementPolicy,
    GridMovementRules,
    GridMovementStep,
    GridPoint,
    HoloGridFurnitureWalkability,
    HoloGridWalkabilityEvent,
    RoomMapData,
    RoomObjectCategory,
    RoomUnitWalkComposer,
    RoomObjectVariable
} from '@nitrots/nitro-renderer';
import { GetCommunication } from '../../nitro/GetCommunication';
import { GetNitroInstance } from '../../nitro/GetNitroInstance';
import { SendMessageComposer } from '../../nitro/SendMessageComposer';
import { GetOwnRoomObject } from '../../nitro/room/GetOwnRoomObject';
import { GetRoomEngine } from '../../nitro/room/GetRoomEngine';

interface OwnGridPosition extends GridPoint
{
    z: number;
}

interface FollowResult extends OwnGridPosition
{
    budget: number;
    movementCount: number;
    following: boolean;
    mapReady: boolean;
    serverWalkabilityReady: boolean;
}

interface MovementSessionSnapshot
{
    active: boolean;

    // Canonical runtime resource names.
    maximum: number;
    current: number;
    consumed: number;

    // Backwards-compatible aliases used by existing debug/tests.
    total: number;
    remaining: number;
    spent: number;

    selected: GridPoint | null;
    selectedCost: number | null;
    moving: boolean;
    stepsSpent: number;
    lastStepCost: number;
}

interface MovementSelectionResult extends MovementSessionSnapshot
{
    valid: boolean;
    reason: 'ok' | 'movement-inactive' | 'moving' | 'same-tile' | 'unreachable' | 'over-budget' | 'no-selection';
    target: GridPoint | null;
    path: GridPoint[];
    pathCost: number | null;
}

type FurnitureRule = 'respect-walkability' | 'block-all' | 'ignore';
type WalkabilitySource = 'server' | 'client-fallback' | 'unknown';

interface HumanMovementRuleUpdate
{
    diagonals?: boolean;
    straightCost?: number;
    diagonalCost?: number;
    passBetweenCorners?: boolean;
    furniture?: FurnitureRule;
    unknownFurnitureBlocks?: boolean;
    maxStepHeight?: number;
    allowFalling?: boolean;
}

interface HumanMovementRuleSnapshot
{
    diagonals: boolean;
    straightCost: number;
    diagonalCost: number;
    passBetweenCorners: boolean;
    furniture: FurnitureRule;
    unknownFurnitureBlocks: boolean;
    maxStepHeight: number;
    allowFalling: boolean;
}

interface RoomFurnitureInfo
{
    id: number;
    type: string;
    x: number;
    y: number;
    sizeX: number;
    sizeY: number;
    canStandOn: boolean | null;
    allowWalk: boolean | null;
    allowSit: boolean | null;
    allowLay: boolean | null;
    walkabilitySource: WalkabilitySource;
    blocks: boolean;
}

interface RoomGridSummary extends GridMapSummary
{
    roomId: number;
    holes: number;
    furnitureObjects: number;
    walkableFurnitureObjects: number;
    blockingFurnitureObjects: number;
    unknownFurnitureObjects: number;
    authoritativeFurnitureObjects: number;
    authoritativeHeightTiles: number;
    serverWalkabilityReady: boolean;
    serverHeightReady: boolean;
    rules: HumanMovementRuleSnapshot;
}

interface HoloGridDebugApi
{
    on: () => void;
    off: () => void;
    clear: () => void;
    position: () => OwnGridPosition;
    own: (budget?: number) => FollowResult;
    follow: (budget?: number) => FollowResult;
    stopFollow: () => void;
    following: () => boolean;
    budget: (budget: number) => FollowResult;
    beginMovement: (points?: number) => MovementSessionSnapshot;
    updateMovementTotal: (points: number) => MovementSessionSnapshot;
    resetMovement: (points?: number) => MovementSessionSnapshot;
    endMovement: () => MovementSessionSnapshot;
    movementSession: () => MovementSessionSnapshot;
    choose: (x: number, y: number) => MovementSelectionResult;
    confirmMove: () => MovementSelectionResult;
    cancelMove: () => MovementSessionSnapshot;
    select: (x: number, y: number) => void;
    movement: (tiles: GridPoint[]) => void;
    targets: (tiles: GridPoint[]) => void;
    target: (x: number, y: number) => void;
    blocked: (tiles: GridPoint[]) => void;
    encounterBlocked: (tiles: GridPoint[]) => void;
    rejoinMarker: (tile?: GridPoint | null) => void;
    pathLayer: (tiles: GridPoint[]) => void;
    state: () => unknown;
    rules: (update?: HumanMovementRuleUpdate) => HumanMovementRuleSnapshot;
    distance: (ax: number, ay: number, bx: number, by: number) => number;
    neighbors: (x: number, y: number) => GridPoint[];
    scan: () => RoomGridSummary;
    map: () => RoomGridSummary;
    tile: (x: number, y: number) => GridMapCell;
    furniture: () => RoomFurnitureInfo[];
    height: (x: number, y: number) => number;
    step: (ax: number, ay: number, bx: number, by: number) => unknown;
    refreshWalkability: () => void;
    showObstacles: (show?: boolean) => RoomGridSummary;
    furnitureBlocking: (enabled?: boolean) => RoomGridSummary;
    reachable: (budget?: number) => GridPoint[];
    path: (sx: number, sy: number, tx: number, ty: number) => GridPoint[];
    pathCost: (path: GridPoint[]) => number;
    pathTo: (x: number, y: number) => GridPoint[];
}

const movementPolicy = new GridMovementPolicy();

let trackerTimer: number = null;
let movementBudget = 4;
let lastFollowKey: string = null;
let installed = false;

let movementSessionActive = false;
let movementTotal = 4;
let movementRemaining = 4;
let movementSpent = 0;
let movementSelected: GridPoint = null;
let movementSelectedPath: GridPoint[] = [];
let movementSelectedCost: number = null;
let movementInFlight = false;
let movementQueuedPath: GridPoint[] = [];
let movementStepsSpent = 0;
let movementLastStepCost = 0;
let movementCommandAt = 0;
let lastTrackedPosition: GridPoint = null;

let currentMap: GridMap = null;
let currentRoomId = -1;
let lastMapScanAt = 0;
let mapRevision = 0;
let lastMapSummary: RoomGridSummary = null;
let lastFurnitureInfo: RoomFurnitureInfo[] = [];

let showRoomObstacles = false;
let furnitureRule: FurnitureRule = 'respect-walkability';
let unknownFurnitureBlocks = true;
let maxStepHeight = 1.1;
let allowFalling = true;
let manualBlocked = new Map<string, GridPoint>();
let encounterBlockedTiles = new Map<string, GridPoint>();

let walkabilityRoomId = -1;
let walkabilitySnapshotAt = 0;
let lastWalkabilityRequestAt = 0;
let serverWalkability = new Map<number, HoloGridFurnitureWalkability>();
let serverTileHeights = new Map<string, number>();

function normalizeBudget(value: number): number
{
    const parsed = Number(value);

    if(!Number.isFinite(parsed)) return 0;

    return Math.max(0, Math.min(100000, parsed));
}

function activeMovementBudget(): number
{
    return movementSessionActive ? movementRemaining : movementBudget;
}

function movementSnapshot(): MovementSessionSnapshot
{
    return {
        active: movementSessionActive,

        maximum: movementTotal,
        current: movementRemaining,
        consumed: movementSpent,

        total: movementTotal,
        remaining: movementRemaining,
        spent: movementSpent,

        selected: movementSelected ? { ...movementSelected } : null,
        selectedCost: movementSelectedCost,
        moving: movementInFlight,
        stepsSpent: movementStepsSpent,
        lastStepCost: movementLastStepCost
    };
}

function movementSelectionResult(
    valid: boolean,
    reason: MovementSelectionResult['reason'],
    target: GridPoint = movementSelected,
    path: GridPoint[] = movementSelectedPath,
    pathCost: number = movementSelectedCost): MovementSelectionResult
{
    return {
        ...movementSnapshot(),
        valid,
        reason,
        target: target ? { ...target } : null,
        path: (path ?? []).map(point => ({ ...point })),
        pathCost
    };
}

function clearMovementSelection(): void
{
    movementSelected = null;
    movementSelectedPath = [];
    movementSelectedCost = null;
    movementQueuedPath = [];
    movementInFlight = false;
    movementCommandAt = 0;
    lastFollowKey = null;
}

function getOwnGridPosition(): OwnGridPosition
{
    const roomObject = GetOwnRoomObject();

    if(!roomObject) return null;

    const location = roomObject.getLocation();

    if(!location) return null;

    return {
        x: Math.round(location.x),
        y: Math.round(location.y),
        z: location.z
    };
}

function getHumanRules(): HumanMovementRuleSnapshot
{
    const rules = movementPolicy.rules;

    return {
        diagonals: (rules.directions === 8),
        straightCost: rules.orthogonalCost,
        diagonalCost: rules.diagonalCost,
        passBetweenCorners: rules.allowCornerCutting,
        furniture: furnitureRule,
        unknownFurnitureBlocks,
        maxStepHeight,
        allowFalling
    };
}

function configureHumanRules(update: HumanMovementRuleUpdate = null): HumanMovementRuleSnapshot
{
    if(!update) return getHumanRules();

    const policyUpdate: Partial<GridMovementRules> = {};

    if(typeof update.diagonals === 'boolean')
    {
        policyUpdate.directions = update.diagonals ? 8 : 4;
    }

    if(Number.isFinite(update.straightCost))
    {
        policyUpdate.orthogonalCost = Math.max(0.001, update.straightCost);
    }

    if(Number.isFinite(update.diagonalCost))
    {
        policyUpdate.diagonalCost = Math.max(0.001, update.diagonalCost);
    }

    if(typeof update.passBetweenCorners === 'boolean')
    {
        policyUpdate.allowCornerCutting = update.passBetweenCorners;
    }

    movementPolicy.configure(policyUpdate);

    if(update.furniture === 'respect-walkability' ||
        update.furniture === 'block-all' ||
        update.furniture === 'ignore')
    {
        furnitureRule = update.furniture;
    }

    if(typeof update.unknownFurnitureBlocks === 'boolean')
    {
        unknownFurnitureBlocks = update.unknownFurnitureBlocks;
    }

    if(Number.isFinite(update.maxStepHeight))
    {
        maxStepHeight = Math.max(0, update.maxStepHeight);
    }

    if(typeof update.allowFalling === 'boolean')
    {
        allowFalling = update.allowFalling;
    }

    lastFollowKey = null;
    scanRoomMap(true);
    applyFollow(true);

    return getHumanRules();
}

function setManualBlocked(tiles: GridPoint[]): void
{
    manualBlocked = new Map();

    for(const tile of (tiles ?? []))
    {
        if(!tile || !Number.isFinite(tile.x) || !Number.isFinite(tile.y)) continue;

        const normalized = GridGeometry.normalize(tile);

        manualBlocked.set(GridGeometry.key(normalized), normalized);
    }

    refreshBlockedLayer();
}

function setEncounterBlocked(tiles: GridPoint[]): void
{
    encounterBlockedTiles = new Map();

    for(const tile of (tiles ?? []))
    {
        if(!tile || !Number.isFinite(tile.x) || !Number.isFinite(tile.y)) continue;

        const normalized = GridGeometry.normalize(tile);

        encounterBlockedTiles.set(GridGeometry.key(normalized), normalized);
    }

    lastFollowKey = null;
    refreshBlockedLayer();

    if(trackerTimer !== null) applyFollow(true);
}

function isManuallyBlocked(point: GridPoint): boolean
{
    return manualBlocked.has(GridGeometry.key(point));
}

function isEncounterBlocked(point: GridPoint): boolean
{
    return encounterBlockedTiles.has(GridGeometry.key(point));
}

function refreshBlockedLayer(): void
{
    const blocked = new Map<string, GridPoint>();

    for(const point of manualBlocked.values())
    {
        blocked.set(GridGeometry.key(point), point);
    }

    for(const point of encounterBlockedTiles.values())
    {
        blocked.set(GridGeometry.key(point), point);
    }

    if(showRoomObstacles && currentMap)
    {
        for(const point of currentMap.blockedTiles)
        {
            blocked.set(GridGeometry.key(point), point);
        }
    }

    GridEngine.updateState({
        blocked: Array.from(blocked.values())
    });
}

function isServerWalkabilityReady(roomId: number): boolean
{
    return (roomId >= 0) &&
        (walkabilityRoomId === roomId) &&
        (walkabilitySnapshotAt > 0);
}

function requestServerWalkability(force: boolean = false): void
{
    const roomId = GetRoomEngine().activeRoomId;

    if(roomId < 0) return;

    const now = Date.now();

    if(!force && ((now - lastWalkabilityRequestAt) < 750)) return;

    lastWalkabilityRequestAt = now;

    SendMessageComposer(new GetHoloGridWalkabilityComposer());
}

function onServerWalkability(event: HoloGridWalkabilityEvent): void
{
    const parser = event?.getParser();

    if(!parser) return;

    const next = new Map<number, HoloGridFurnitureWalkability>();

    for(const item of parser.items)
    {
        next.set(item.itemId, item);
    }

    const nextHeights = new Map<string, number>();

    for(const tile of parser.tiles)
    {
        nextHeights.set(GridGeometry.key(tile), tile.height);
    }

    walkabilityRoomId = parser.roomId;
    walkabilitySnapshotAt = Date.now();
    serverWalkability = next;
    serverTileHeights = nextHeights;

    mapRevision++;
    lastFollowKey = null;

    if(GetRoomEngine().activeRoomId === parser.roomId)
    {
        scanRoomMap(true);
        applyFollow(true);
    }
}

function makeSummary(
    roomId: number,
    map: GridMap,
    holes: number,
    furnitureObjects: number,
    walkableFurnitureObjects: number,
    blockingFurnitureObjects: number,
    unknownFurnitureObjects: number,
    authoritativeFurnitureObjects: number): RoomGridSummary
{
    if(!map) return null;

    return {
        roomId,
        ...map.summary,
        holes,
        furnitureObjects,
        walkableFurnitureObjects,
        blockingFurnitureObjects,
        unknownFurnitureObjects,
        authoritativeFurnitureObjects,
        authoritativeHeightTiles: serverTileHeights.size,
        serverWalkabilityReady: isServerWalkabilityReady(roomId),
        serverHeightReady: isServerWalkabilityReady(roomId) && (serverTileHeights.size > 0),
        rules: getHumanRules()
    };
}

function markRectangle(
    map: GridMap,
    startX: number,
    startY: number,
    width: number,
    height: number,
    kind: 'terrain' | 'furniture'): void
{
    const safeWidth = Math.max(1, Math.round(width || 1));
    const safeHeight = Math.max(1, Math.round(height || 1));
    const baseX = Math.round(startX);
    const baseY = Math.round(startY);

    for(let dy = 0; dy < safeHeight; dy++)
    {
        for(let dx = 0; dx < safeWidth; dx++)
        {
            const point = {
                x: baseX + dx,
                y: baseY + dy
            };

            if(kind === 'terrain') map.setTerrainBlocked(point, true);
            else map.setFurnitureBlocked(point, true);
        }
    }
}

function furnitureBlocks(allowWalk: boolean | null): boolean
{
    switch(furnitureRule)
    {
        case 'ignore':
            return false;
        case 'block-all':
            return true;
        case 'respect-walkability':
        default:
            if(allowWalk === null) return unknownFurnitureBlocks;

            return !allowWalk;
    }
}

function scanRoomMap(force: boolean = false): RoomGridSummary
{
    const engine = GetRoomEngine();
    const roomId = engine.activeRoomId;
    const now = Date.now();

    if(roomId !== currentRoomId)
    {
        currentMap = null;
        currentRoomId = roomId;
        lastMapSummary = null;
        lastFurnitureInfo = [];
        lastFollowKey = null;

        if(walkabilityRoomId !== roomId)
        {
            walkabilityRoomId = -1;
            walkabilitySnapshotAt = 0;
            serverWalkability.clear();
            serverTileHeights.clear();
        }

        requestServerWalkability(true);
    }

    if(!force &&
        currentMap &&
        ((now - lastMapScanAt) < 500))
    {
        return lastMapSummary;
    }

    const roomObject = engine.getRoomObject(roomId, -1, RoomObjectCategory.ROOM);

    if(!roomObject?.model)
    {
        currentMap = null;
        lastMapSummary = null;
        lastFurnitureInfo = [];
        lastMapScanAt = now;

        return null;
    }

    const mapData = roomObject.model.getValue<RoomMapData>(RoomObjectVariable.ROOM_MAP_DATA);

    if(!mapData?.tileMap?.length)
    {
        currentMap = null;
        lastMapSummary = null;
        lastFurnitureInfo = [];
        lastMapScanAt = now;

        return null;
    }

    const width = Math.max(0, Math.trunc(mapData.width || mapData.tileMap[0]?.length || 0));
    const height = Math.max(0, Math.trunc(mapData.height || mapData.tileMap.length || 0));
    const nextMap = new GridMap(width, height);
    const authoritativeHeightReady = isServerWalkabilityReady(roomId) && (serverTileHeights.size > 0);

    for(let y = 0; y < height; y++)
    {
        const row = mapData.tileMap[y] ?? [];

        for(let x = 0; x < width; x++)
        {
            const rawHeight = row[x]?.height;
            const hasFloor = Number.isFinite(rawHeight) && (rawHeight >= 0);
            const serverHeight = authoritativeHeightReady
                ? serverTileHeights.get(GridGeometry.key({ x, y }))
                : undefined;
            const effectiveHeight = Number.isFinite(serverHeight)
                ? serverHeight
                : (hasFloor ? rawHeight : 0);

            nextMap.setCell(
                x,
                y,
                effectiveHeight,
                !hasFloor
            );
        }
    }

    const holes = mapData.holeMap ?? [];

    for(const hole of holes)
    {
        markRectangle(
            nextMap,
            hole.x,
            hole.y,
            hole.width,
            hole.height,
            'terrain'
        );
    }

    const sessionData = GetNitroInstance().sessionDataManager;
    const totalFurniture = engine.getTotalObjectsForManager(roomId, RoomObjectCategory.FLOOR);
    const authoritativeReady = isServerWalkabilityReady(roomId);

    let walkableFurnitureObjects = 0;
    let blockingFurnitureObjects = 0;
    let unknownFurnitureObjects = 0;
    let authoritativeFurnitureObjects = 0;

    const furnitureInfo: RoomFurnitureInfo[] = [];

    for(let i = 0; i < totalFurniture; i++)
    {
        const object = engine.getRoomObjectByIndex(roomId, i, RoomObjectCategory.FLOOR);

        if(!object?.model) continue;

        const location = object.getLocation();

        if(!location) continue;

        const serverEntry = authoritativeReady ? (serverWalkability.get(object.id) ?? null) : null;
        const furnitureData = sessionData?.getFloorItemDataByName(object.type) ?? null;

        let allowWalk: boolean | null = null;
        let allowSit: boolean | null = null;
        let allowLay: boolean | null = null;
        let source: WalkabilitySource = 'unknown';

        if(serverEntry)
        {
            allowWalk = serverEntry.allowWalk;
            allowSit = serverEntry.allowSit;
            allowLay = serverEntry.allowLay;
            source = 'server';
            authoritativeFurnitureObjects++;
        }
        else if(!authoritativeReady && furnitureData)
        {
            allowWalk = !!furnitureData.canStandOn;
            allowSit = !!furnitureData.canSitOn;
            allowLay = !!furnitureData.canLayOn;
            source = 'client-fallback';
        }

        if(allowWalk === true) walkableFurnitureObjects++;
        else if(allowWalk === null) unknownFurnitureObjects++;

        const blocks = furnitureBlocks(allowWalk);

        if(blocks) blockingFurnitureObjects++;

        let sizeX = furnitureData?.tileSizeX;
        let sizeY = furnitureData?.tileSizeY;

        if(!Number.isFinite(sizeX) || (sizeX <= 0))
        {
            sizeX = object.model.getValue<number>(RoomObjectVariable.FURNITURE_SIZE_X);
        }

        if(!Number.isFinite(sizeY) || (sizeY <= 0))
        {
            sizeY = object.model.getValue<number>(RoomObjectVariable.FURNITURE_SIZE_Y);
        }

        if(!Number.isFinite(sizeX) || (sizeX <= 0)) sizeX = 1;
        if(!Number.isFinite(sizeY) || (sizeY <= 0)) sizeY = 1;

        const direction = object.getDirection();
        const degrees = ((((Math.round(direction?.x ?? 0) % 360) + 360) % 360));

        if((degrees === 90) || (degrees === 270))
        {
            const swap = sizeX;
            sizeX = sizeY;
            sizeY = swap;
        }

        if(blocks)
        {
            markRectangle(
                nextMap,
                location.x,
                location.y,
                sizeX,
                sizeY,
                'furniture'
            );
        }

        furnitureInfo.push({
            id: object.id,
            type: object.type,
            x: Math.round(location.x),
            y: Math.round(location.y),
            sizeX: Math.max(1, Math.round(sizeX)),
            sizeY: Math.max(1, Math.round(sizeY)),
            canStandOn: allowWalk,
            allowWalk,
            allowSit,
            allowLay,
            walkabilitySource: source,
            blocks
        });
    }

    currentMap = nextMap;
    lastMapScanAt = now;
    mapRevision++;
    lastFurnitureInfo = furnitureInfo;
    lastMapSummary = makeSummary(
        roomId,
        nextMap,
        holes.length,
        totalFurniture,
        walkableFurnitureObjects,
        blockingFurnitureObjects,
        unknownFurnitureObjects,
        authoritativeFurnitureObjects
    );

    refreshBlockedLayer();

    return lastMapSummary;
}

function ensureMap(force: boolean = false): GridMap
{
    scanRoomMap(force);

    return currentMap;
}

function canOccupy(point: GridPoint): boolean
{
    const map = ensureMap(false);

    if(!map) return false;
    if(isManuallyBlocked(point)) return false;
    if(isEncounterBlocked(point)) return false;

    return map.isWalkable(point);
}

function canStep(from: GridPoint, to: GridPoint, step: GridMovementStep): boolean
{
    const map = ensureMap(false);

    if(!map || !step) return false;
    if(!canOccupy(to)) return false;

    const fromHeight = map.heightAt(from);
    const toHeight = map.heightAt(to);

    if(!Number.isFinite(fromHeight) || !Number.isFinite(toHeight)) return false;

    const deltaHeight = toHeight - fromHeight;
    const epsilon = 0.0001;

    if(deltaHeight > (maxStepHeight + epsilon)) return false;
    if(!allowFalling && (deltaHeight < -(maxStepHeight + epsilon))) return false;

    const rules = movementPolicy.rules;

    if(step.diagonal && !rules.allowCornerCutting)
    {
        const dx = Math.trunc(to.x) - Math.trunc(from.x);
        const dy = Math.trunc(to.y) - Math.trunc(from.y);

        const sideA = {
            x: Math.trunc(from.x) + dx,
            y: Math.trunc(from.y)
        };

        const sideB = {
            x: Math.trunc(from.x),
            y: Math.trunc(from.y) + dy
        };

        // Bloquea solo el "squeeze" entre DOS obstaculos.
        // Un obstaculo aislado se puede rodear diagonalmente.
        if(!canOccupy(sideA) && !canOccupy(sideB)) return false;
    }

    return true;
}

function getReachable(position: GridPoint, budget: number): GridPoint[]
{
    const map = ensureMap(false);

    if(!map) return [];

    return GridGeometry.reachable(
        position,
        budget,
        {
            policy: movementPolicy,
            canStep,
            maxNodes: Math.max(4096, map.width * map.height * 2)
        }
    );
}

function applyFollow(force: boolean = false): FollowResult
{
    const position = getOwnGridPosition();

    if(!position) return null;

    const map = ensureMap(false);
    const rules = movementPolicy.rules;
    const budget = activeMovementBudget();
    const key = [
        position.x,
        position.y,
        budget,
        movementSessionActive ? 1 : 0,
        movementSpent,
        movementSelected ? GridGeometry.key(movementSelected) : '-',
        movementSelectedCost ?? '-',
        movementInFlight ? 1 : 0,
        movementQueuedPath.map(point => GridGeometry.key(point)).join('|'),
        mapRevision,
        rules.directions,
        rules.orthogonalCost,
        rules.diagonalCost,
        rules.allowCornerCutting ? 1 : 0,
        furnitureRule,
        unknownFurnitureBlocks ? 1 : 0,
        maxStepHeight,
        allowFalling ? 1 : 0,
        walkabilityRoomId,
        walkabilitySnapshotAt
    ].join(':');

    let movementCount = 0;

    if(force || (key !== lastFollowKey))
    {
        const movement = getReachable(position, budget);
        movementCount = movement.length;

        let visiblePath: GridPoint[] = [];
        let selected: GridPoint = position;
        let targets: GridPoint[] = [];

        if(movementSessionActive && movementSelected)
        {
            const sourcePath = movementInFlight ? [ position, ...movementQueuedPath ] : movementSelectedPath;

            visiblePath = sourcePath.length > 1 ? sourcePath.slice(1, -1) : [];
            selected = movementSelected;
            targets = [ movementSelected ];
        }

        GridEngine.updateState({
            origin: position,
            movementRadius: budget,
            movement,
            path: visiblePath,
            targets,
            selected
        });

        refreshBlockedLayer();

        lastFollowKey = key;
    }
    else
    {
        movementCount = getReachable(position, budget).length;
    }

    return {
        ...position,
        budget,
        movementCount,
        following: (trackerTimer !== null),
        mapReady: !!map,
        serverWalkabilityReady: isServerWalkabilityReady(GetRoomEngine().activeRoomId)
    };
}

function updateSelectedPathFromCurrent(): MovementSelectionResult
{
    if(!movementSessionActive)
    {
        return movementSelectionResult(false, 'movement-inactive', null, [], null);
    }

    if(!movementSelected)
    {
        return movementSelectionResult(false, 'no-selection', null, [], null);
    }

    const position = getOwnGridPosition();

    if(!position)
    {
        clearMovementSelection();

        return movementSelectionResult(false, 'unreachable', null, [], null);
    }

    if(GridGeometry.equals(position, movementSelected))
    {
        const target = { ...movementSelected };

        clearMovementSelection();
        applyFollow(true);

        return movementSelectionResult(true, 'same-tile', target, [ position ], 0);
    }

    const path = findPath(position, movementSelected);

    if(path.length < 2)
    {
        const target = { ...movementSelected };

        movementSelectedPath = [];
        movementSelectedCost = null;
        movementQueuedPath = [];
        movementInFlight = false;
        lastFollowKey = null;
        applyFollow(true);

        return movementSelectionResult(false, 'unreachable', target, [], null);
    }

    const cost = GridGeometry.pathCost(path, movementPolicy);

    movementSelectedPath = path;
    movementSelectedCost = cost;
    lastFollowKey = null;

    if(cost > (movementRemaining + 0.0001))
    {
        movementQueuedPath = [];
        movementInFlight = false;
        applyFollow(true);

        return movementSelectionResult(false, 'over-budget', movementSelected, path, cost);
    }

    applyFollow(true);

    return movementSelectionResult(true, 'ok', movementSelected, path, cost);
}

function chooseMovementTarget(x: number, y: number): MovementSelectionResult
{
    if(!movementSessionActive)
    {
        return movementSelectionResult(false, 'movement-inactive', null, [], null);
    }

    if(movementInFlight)
    {
        return movementSelectionResult(false, 'moving', movementSelected, movementSelectedPath, movementSelectedCost);
    }

    const position = getOwnGridPosition();
    const target = GridGeometry.normalize({ x, y });

    if(!position)
    {
        return movementSelectionResult(false, 'unreachable', target, [], null);
    }

    movementSelected = target;
    movementQueuedPath = [];
    movementInFlight = false;

    return updateSelectedPathFromCurrent();
}

function finishMovementPath(): void
{
    clearMovementSelection();
    applyFollow(true);
}

function sendNextMovementStep(): void
{
    if(!movementSessionActive || !movementInFlight) return;

    const position = getOwnGridPosition();

    if(!position)
    {
        finishMovementPath();
        return;
    }

    if(!movementQueuedPath.length)
    {
        finishMovementPath();
        return;
    }

    const next = movementQueuedPath[0];
    const step = movementPolicy.stepBetween(position, next);

    if(!step || !canStep(position, next, step) || (step.cost > (movementRemaining + 0.0001)))
    {
        movementInFlight = false;
        movementQueuedPath = [];
        updateSelectedPathFromCurrent();
        return;
    }

    movementCommandAt = Date.now();

    SendMessageComposer(new RoomUnitWalkComposer(next.x, next.y));
}

function confirmMovementSelection(): MovementSelectionResult
{
    if(!movementSessionActive)
    {
        return movementSelectionResult(false, 'movement-inactive', null, [], null);
    }

    if(movementInFlight)
    {
        return movementSelectionResult(false, 'moving', movementSelected, movementSelectedPath, movementSelectedCost);
    }

    if(!movementSelected)
    {
        return movementSelectionResult(false, 'no-selection', null, [], null);
    }

    const validation = updateSelectedPathFromCurrent();

    if(!validation.valid || validation.reason === 'same-tile') return validation;

    movementQueuedPath = movementSelectedPath.slice(1);
    movementInFlight = true;
    movementCommandAt = 0;
    lastFollowKey = null;

    sendNextMovementStep();
    applyFollow(true);

    return movementSelectionResult(true, 'ok', movementSelected, movementSelectedPath, movementSelectedCost);
}

function handleGridWalkRequest(x: number, y: number): boolean
{
    if(!movementSessionActive) return false;

    const target = GridGeometry.normalize({ x, y });

    if(movementInFlight) return true;

    if(movementSelected && GridGeometry.equals(movementSelected, target))
    {
        confirmMovementSelection();
        return true;
    }

    chooseMovementTarget(target.x, target.y);

    return true;
}

function trackMovementSession(): void
{
    const currentPosition = getOwnGridPosition();

    if(!currentPosition) return;

    const current = GridGeometry.normalize(currentPosition);

    if(!lastTrackedPosition)
    {
        lastTrackedPosition = current;
        return;
    }

    if(GridGeometry.equals(lastTrackedPosition, current))
    {
        if(movementSessionActive &&
            movementInFlight &&
            (movementCommandAt > 0) &&
            ((Date.now() - movementCommandAt) > 2000))
        {
            movementInFlight = false;
            movementQueuedPath = [];
            movementCommandAt = 0;
            updateSelectedPathFromCurrent();
        }

        return;
    }

    const previous = lastTrackedPosition;
    lastTrackedPosition = current;

    if(!movementSessionActive)
    {
        return;
    }

    const actualStep = movementPolicy.stepBetween(previous, current);

    if(actualStep)
    {
        const charged = Math.min(movementRemaining, actualStep.cost);

        movementRemaining = Math.max(0, movementRemaining - charged);
        movementSpent = Math.max(0, movementSpent + charged);
        movementLastStepCost = charged;
        movementStepsSpent++;

        if(movementInFlight && movementQueuedPath.length && GridGeometry.equals(movementQueuedPath[0], current))
        {
            movementQueuedPath.shift();
        }
        else if(movementInFlight && movementSelected)
        {
            const reroute = findPath(current, movementSelected);
            const rerouteCost = reroute.length > 1 ? GridGeometry.pathCost(reroute, movementPolicy) : 0;

            if((reroute.length > 1) && (rerouteCost <= (movementRemaining + 0.0001)))
            {
                movementQueuedPath = reroute.slice(1);
                movementSelectedPath = reroute;
                movementSelectedCost = rerouteCost;
            }
            else
            {
                movementQueuedPath = [];
                movementInFlight = false;
            }
        }
    }
    else
    {
        // Teleports, WIREDs y saltos no adyacentes no consumen movimiento normal.
        movementQueuedPath = [];
        movementInFlight = false;
    }

    movementCommandAt = 0;
    lastFollowKey = null;

    if(movementSelected && GridGeometry.equals(movementSelected, current))
    {
        finishMovementPath();
        return;
    }

    if(movementRemaining <= 0.0001)
    {
        movementQueuedPath = [];
        movementInFlight = false;
        movementSelectedPath = [];
        movementSelectedCost = null;
        applyFollow(true);
        return;
    }

    if(movementInFlight && movementQueuedPath.length)
    {
        movementSelectedPath = [ current, ...movementQueuedPath ];
        movementSelectedCost = GridGeometry.pathCost(movementSelectedPath, movementPolicy);
        sendNextMovementStep();
    }

    applyFollow(true);
}

function beginMovementSession(points: number = movementBudget): MovementSessionSnapshot
{
    const total = normalizeBudget(points);

    movementSessionActive = true;
    movementTotal = total;
    movementRemaining = total;
    movementSpent = 0;
    movementStepsSpent = 0;
    movementLastStepCost = 0;
    clearMovementSelection();

    const position = getOwnGridPosition();

    lastTrackedPosition = position ? GridGeometry.normalize(position) : null;
    movementBudget = total;

    startFollow(total);
    applyFollow(true);

    return movementSnapshot();
}

function updateMovementSessionTotal(points: number): MovementSessionSnapshot
{
    const maximum = normalizeBudget(points);

    if(!movementSessionActive)
    {
        return beginMovementSession(maximum);
    }

    const previousMaximum = Math.max(0, movementTotal);
    const previousCurrent = Math.max(0, movementRemaining);

    // Current movement is a real resource, not "maximum - spent".
    // When a live STAT effect changes the maximum, preserve the percentage
    // of Movement that remained. Example: 5 max / 1 current + x1.5 =>
    // 8 max / 2 current after RPG half-up rounding.
    let nextCurrent = 0;

    if(previousMaximum > 0.0001)
    {
        const ratio = Math.max(0, Math.min(1, previousCurrent / previousMaximum));
        nextCurrent = Math.floor((maximum * ratio) + 0.5);
    }

    movementTotal = maximum;
    movementRemaining = Math.max(0, Math.min(maximum, nextCurrent));
    movementBudget = maximum;

    // A runtime STAT/rule change invalidates the current preview/path,
    // but does not erase the amount actually consumed by walking.
    clearMovementSelection();
    startFollow(maximum);
    applyFollow(true);

    return movementSnapshot();
}

function resetMovementSession(points: number = movementTotal): MovementSessionSnapshot
{
    return beginMovementSession(points);
}

function endMovementSession(): MovementSessionSnapshot
{
    movementSessionActive = false;
    clearMovementSelection();

    const position = getOwnGridPosition();

    lastTrackedPosition = position ? GridGeometry.normalize(position) : null;
    applyFollow(true);

    return movementSnapshot();
}

function cancelMovementSelection(): MovementSessionSnapshot
{
    clearMovementSelection();
    applyFollow(true);

    return movementSnapshot();
}

function startFollow(budget: number = 4): FollowResult
{
    movementBudget = normalizeBudget(budget);
    lastFollowKey = null;

    requestServerWalkability(true);
    ensureMap(true);

    if(trackerTimer === null)
    {
        trackerTimer = window.setInterval(() =>
        {
            requestServerWalkability(false);
            scanRoomMap(false);
            trackMovementSession();
            applyFollow(false);
        }, 100);
    }

    return applyFollow(true);
}

function stopFollow(): void
{
    if(trackerTimer !== null)
    {
        window.clearInterval(trackerTimer);
        trackerTimer = null;
    }

    lastFollowKey = null;
}

function findPath(start: GridPoint, goal: GridPoint): GridPoint[]
{
    const map = ensureMap(false);

    if(!map) return [];

    return GridGeometry.findPath(
        start,
        goal,
        {
            policy: movementPolicy,
            isWalkable: canOccupy,
            canStep,
            maxNodes: Math.max(4096, map.width * map.height * 2)
        }
    );
}

function pathTo(x: number, y: number): GridPoint[]
{
    const position = getOwnGridPosition();

    if(!position) return [];

    const target = GridGeometry.normalize({ x, y });
    const path = findPath(position, target);
    const visiblePath = path.length > 1 ? path.slice(1, -1) : [];

    GridEngine.updateState({
        origin: position,
        path: visiblePath,
        targets: path.length ? [ target ] : [],
        selected: position
    });

    refreshBlockedLayer();

    return path;
}

function createDebugApi(): HoloGridDebugApi
{
    return {
        on: () => GridEngine.setEnabled(true),
        off: () => GridEngine.setEnabled(false),
        clear: () =>
        {
            GridEngine.clearTiles();
            manualBlocked.clear();
            showRoomObstacles = false;
        },
        position: () => getOwnGridPosition(),
        own: (budget: number = 4) => startFollow(budget),
        follow: (budget: number = 4) => startFollow(budget),
        stopFollow,
        following: () => (trackerTimer !== null),
        budget: (budget: number) => startFollow(budget),
        beginMovement: (points: number = movementBudget) => beginMovementSession(points),
        updateMovementTotal: (points: number) => updateMovementSessionTotal(points),
        resetMovement: (points: number = movementTotal) => resetMovementSession(points),
        endMovement: () => endMovementSession(),
        movementSession: () => movementSnapshot(),
        choose: (x: number, y: number) => chooseMovementTarget(x, y),
        confirmMove: () => confirmMovementSelection(),
        cancelMove: () => cancelMovementSelection(),
        select: (x: number, y: number) => GridEngine.updateState({ selected: { x, y } }),
        movement: (tiles: GridPoint[]) => GridEngine.updateState({ movement: tiles }),
        targets: (tiles: GridPoint[]) => GridEngine.updateState({ targets: tiles }),
        target: (x: number, y: number) => GridEngine.updateState({ targets: [{ x, y }] }),
        blocked: (tiles: GridPoint[]) => setManualBlocked(tiles),
        encounterBlocked: (tiles: GridPoint[]) => setEncounterBlocked(tiles),
        rejoinMarker: (tile: GridPoint | null = null) => GridEngine.updateState({
            targets: tile ? [ GridGeometry.normalize(tile) ] : []
        }),
        pathLayer: (tiles: GridPoint[]) => GridEngine.updateState({ path: tiles }),
        state: () => GridEngine.snapshot,
        rules: (update: HumanMovementRuleUpdate = null) => configureHumanRules(update),
        distance: (ax: number, ay: number, bx: number, by: number) =>
            GridGeometry.chebyshev({ x: ax, y: ay }, { x: bx, y: by }),
        neighbors: (x: number, y: number) =>
            movementPolicy.stepsFrom({ x, y }).map(step => ({ x: step.x, y: step.y })),
        scan: () =>
        {
            requestServerWalkability(true);

            return scanRoomMap(true);
        },
        map: () => scanRoomMap(false),
        tile: (x: number, y: number) =>
        {
            const map = ensureMap(false);

            return map?.getCell({ x, y }) ?? null;
        },
        furniture: () =>
        {
            requestServerWalkability(true);
            scanRoomMap(true);

            return lastFurnitureInfo.map(item => ({ ...item }));
        },
        height: (x: number, y: number) =>
        {
            const map = ensureMap(false);

            return map?.heightAt({ x, y }) ?? Number.NaN;
        },
        step: (ax: number, ay: number, bx: number, by: number) =>
        {
            const map = ensureMap(false);
            const from = GridGeometry.normalize({ x: ax, y: ay });
            const to = GridGeometry.normalize({ x: bx, y: by });
            const movementStep = movementPolicy.stepBetween(from, to);
            const fromHeight = map?.heightAt(from) ?? Number.NaN;
            const toHeight = map?.heightAt(to) ?? Number.NaN;

            return {
                from,
                to,
                fromHeight,
                toHeight,
                delta: toHeight - fromHeight,
                maxStepHeight,
                allowFalling,
                allowed: !!movementStep && canStep(from, to, movementStep)
            };
        },
        refreshWalkability: () => requestServerWalkability(true),
        showObstacles: (show: boolean = true) =>
        {
            showRoomObstacles = show;
            requestServerWalkability(true);
            scanRoomMap(true);
            refreshBlockedLayer();

            return lastMapSummary;
        },
        furnitureBlocking: (enabled: boolean = true) =>
        {
            furnitureRule = enabled ? 'respect-walkability' : 'ignore';
            lastFollowKey = null;
            requestServerWalkability(true);
            scanRoomMap(true);
            applyFollow(true);

            return lastMapSummary;
        },
        reachable: (budget: number = movementBudget) =>
        {
            const position = getOwnGridPosition();

            if(!position) return [];

            return getReachable(position, normalizeBudget(budget));
        },
        path: (sx: number, sy: number, tx: number, ty: number) =>
            findPath({ x: sx, y: sy }, { x: tx, y: ty }),
        pathCost: (path: GridPoint[]) => GridGeometry.pathCost(path, movementPolicy),
        pathTo
    };
}

export function InstallGridNitroAdapter(): boolean
{
    if(installed) return true;

    const communication = GetCommunication();

    if(!communication) return false;

    communication.registerMessageEvent(
        new HoloGridWalkabilityEvent(onServerWalkability)
    );

    GridEngine.setWalkRequestHandler(handleGridWalkRequest);

    (globalThis as any).HoloGrid = createDebugApi();

    installed = true;

    return true;
}