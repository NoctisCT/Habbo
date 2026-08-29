import {
    GridEngine,
    GridGeometry,
    GridMap,
    GridMapCell,
    GridMapSummary,
    GridMovementPolicy,
    GridMovementRules,
    GridMovementStep,
    GridPoint,
    RoomMapData,
    RoomObjectCategory,
    RoomObjectVariable
} from '@nitrots/nitro-renderer';
import { GetNitroInstance } from '../../nitro/GetNitroInstance';
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
}

type FurnitureRule = 'respect-walkability' | 'block-all' | 'ignore';

interface HumanMovementRuleUpdate
{
    diagonals?: boolean;
    straightCost?: number;
    diagonalCost?: number;
    passBetweenCorners?: boolean;
    furniture?: FurnitureRule;
    unknownFurnitureBlocks?: boolean;
}

interface HumanMovementRuleSnapshot
{
    diagonals: boolean;
    straightCost: number;
    diagonalCost: number;
    passBetweenCorners: boolean;
    furniture: FurnitureRule;
    unknownFurnitureBlocks: boolean;
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
    select: (x: number, y: number) => void;
    movement: (tiles: GridPoint[]) => void;
    targets: (tiles: GridPoint[]) => void;
    target: (x: number, y: number) => void;
    blocked: (tiles: GridPoint[]) => void;
    pathLayer: (tiles: GridPoint[]) => void;
    state: () => unknown;
    rules: (update?: HumanMovementRuleUpdate) => HumanMovementRuleSnapshot;
    distance: (ax: number, ay: number, bx: number, by: number) => number;
    neighbors: (x: number, y: number) => GridPoint[];
    scan: () => RoomGridSummary;
    map: () => RoomGridSummary;
    tile: (x: number, y: number) => GridMapCell;
    furniture: () => RoomFurnitureInfo[];
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

let currentMap: GridMap = null;
let currentRoomId = -1;
let lastMapScanAt = 0;
let mapRevision = 0;
let lastMapSummary: RoomGridSummary = null;
let lastFurnitureInfo: RoomFurnitureInfo[] = [];

let showRoomObstacles = false;
let furnitureRule: FurnitureRule = 'respect-walkability';
let unknownFurnitureBlocks = true;
let manualBlocked = new Map<string, GridPoint>();

function normalizeBudget(value: number): number
{
    const parsed = Number(value);

    if(!Number.isFinite(parsed)) return 0;

    return Math.max(0, Math.min(100, parsed));
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
        unknownFurnitureBlocks
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

function isManuallyBlocked(point: GridPoint): boolean
{
    return manualBlocked.has(GridGeometry.key(point));
}

function refreshBlockedLayer(): void
{
    const blocked = new Map<string, GridPoint>();

    for(const point of manualBlocked.values())
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

function makeSummary(
    roomId: number,
    map: GridMap,
    holes: number,
    furnitureObjects: number,
    walkableFurnitureObjects: number,
    blockingFurnitureObjects: number,
    unknownFurnitureObjects: number): RoomGridSummary
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

function furnitureBlocks(canStandOn: boolean | null): boolean
{
    switch(furnitureRule)
    {
        case 'ignore':
            return false;
        case 'block-all':
            return true;
        case 'respect-walkability':
        default:
            if(canStandOn === null) return unknownFurnitureBlocks;

            return !canStandOn;
    }
}

function scanRoomMap(force: boolean = false): RoomGridSummary
{
    const engine = GetRoomEngine();
    const roomId = engine.activeRoomId;
    const now = Date.now();

    if(!force &&
        currentMap &&
        (roomId === currentRoomId) &&
        ((now - lastMapScanAt) < 500))
    {
        return lastMapSummary;
    }

    const roomObject = engine.getRoomObject(roomId, -1, RoomObjectCategory.ROOM);

    if(!roomObject?.model)
    {
        currentMap = null;
        currentRoomId = roomId;
        lastMapSummary = null;
        lastFurnitureInfo = [];
        lastMapScanAt = now;

        return null;
    }

    const mapData = roomObject.model.getValue<RoomMapData>(RoomObjectVariable.ROOM_MAP_DATA);

    if(!mapData?.tileMap?.length)
    {
        currentMap = null;
        currentRoomId = roomId;
        lastMapSummary = null;
        lastFurnitureInfo = [];
        lastMapScanAt = now;

        return null;
    }

    const width = Math.max(0, Math.trunc(mapData.width || mapData.tileMap[0]?.length || 0));
    const height = Math.max(0, Math.trunc(mapData.height || mapData.tileMap.length || 0));
    const nextMap = new GridMap(width, height);

    for(let y = 0; y < height; y++)
    {
        const row = mapData.tileMap[y] ?? [];

        for(let x = 0; x < width; x++)
        {
            const rawHeight = row[x]?.height;
            const hasFloor = Number.isFinite(rawHeight) && (rawHeight >= 0);

            nextMap.setCell(
                x,
                y,
                hasFloor ? rawHeight : 0,
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

    let walkableFurnitureObjects = 0;
    let blockingFurnitureObjects = 0;
    let unknownFurnitureObjects = 0;

    const furnitureInfo: RoomFurnitureInfo[] = [];

    for(let i = 0; i < totalFurniture; i++)
    {
        const object = engine.getRoomObjectByIndex(roomId, i, RoomObjectCategory.FLOOR);

        if(!object?.model) continue;

        const location = object.getLocation();

        if(!location) continue;

        const furnitureData = sessionData?.getFloorItemDataByName(object.type) ?? null;
        const canStandOn = furnitureData ? !!furnitureData.canStandOn : null;

        if(canStandOn === true) walkableFurnitureObjects++;
        else if(canStandOn === null) unknownFurnitureObjects++;

        const blocks = furnitureBlocks(canStandOn);

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
            canStandOn,
            blocks
        });
    }

    currentMap = nextMap;
    currentRoomId = roomId;
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
        unknownFurnitureObjects
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

    return map.isWalkable(point);
}

function canStep(from: GridPoint, to: GridPoint, step: GridMovementStep): boolean
{
    const map = ensureMap(false);

    if(!map || !step) return false;
    if(!canOccupy(to)) return false;

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

        if(!canOccupy(sideA) || !canOccupy(sideB)) return false;
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
    const key = [
        position.x,
        position.y,
        movementBudget,
        mapRevision,
        rules.directions,
        rules.orthogonalCost,
        rules.diagonalCost,
        rules.allowCornerCutting ? 1 : 0,
        furnitureRule,
        unknownFurnitureBlocks ? 1 : 0
    ].join(':');

    if(force || (key !== lastFollowKey))
    {
        const movement = getReachable(position, movementBudget);

        GridEngine.updateState({
            origin: position,
            movementRadius: movementBudget,
            movement,
            path: [],
            targets: [],
            selected: position
        });

        refreshBlockedLayer();

        lastFollowKey = key;
    }

    return {
        ...position,
        budget: movementBudget,
        movementCount: getReachable(position, movementBudget).length,
        following: (trackerTimer !== null),
        mapReady: !!map
    };
}

function startFollow(budget: number = 4): FollowResult
{
    movementBudget = normalizeBudget(budget);
    lastFollowKey = null;

    ensureMap(true);

    if(trackerTimer === null)
    {
        trackerTimer = window.setInterval(() =>
        {
            scanRoomMap(false);
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
        select: (x: number, y: number) => GridEngine.updateState({ selected: { x, y } }),
        movement: (tiles: GridPoint[]) => GridEngine.updateState({ movement: tiles }),
        targets: (tiles: GridPoint[]) => GridEngine.updateState({ targets: tiles }),
        target: (x: number, y: number) => GridEngine.updateState({ targets: [{ x, y }] }),
        blocked: (tiles: GridPoint[]) => setManualBlocked(tiles),
        pathLayer: (tiles: GridPoint[]) => GridEngine.updateState({ path: tiles }),
        state: () => GridEngine.snapshot,
        rules: (update: HumanMovementRuleUpdate = null) => configureHumanRules(update),
        distance: (ax: number, ay: number, bx: number, by: number) =>
            GridGeometry.chebyshev({ x: ax, y: ay }, { x: bx, y: by }),
        neighbors: (x: number, y: number) =>
            movementPolicy.stepsFrom({ x, y }).map(step => ({ x: step.x, y: step.y })),
        scan: () => scanRoomMap(true),
        map: () => scanRoomMap(false),
        tile: (x: number, y: number) =>
        {
            const map = ensureMap(false);

            return map?.getCell({ x, y }) ?? null;
        },
        furniture: () =>
        {
            scanRoomMap(true);

            return lastFurnitureInfo.map(item => ({ ...item }));
        },
        showObstacles: (show: boolean = true) =>
        {
            showRoomObstacles = show;
            scanRoomMap(true);
            refreshBlockedLayer();

            return lastMapSummary;
        },
        furnitureBlocking: (enabled: boolean = true) =>
        {
            furnitureRule = enabled ? 'respect-walkability' : 'ignore';
            lastFollowKey = null;
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

    (globalThis as any).HoloGrid = createDebugApi();

    installed = true;

    return true;
}