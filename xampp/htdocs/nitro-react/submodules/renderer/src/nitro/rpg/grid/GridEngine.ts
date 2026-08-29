import { GridGeometry, GridPoint } from './GridGeometry';

export interface GridVisualStyle
{
    lineColor: number;
    lineAlpha: number;
    lineWidth: number;
}

export type GridTileKind = 'movement' | 'path' | 'target' | 'selected' | 'blocked';

export interface GridTile extends GridPoint
{
    kind: GridTileKind;
}

export interface GridTileVisualStyle
{
    fillColor: number;
    fillAlpha: number;
    lineColor: number;
    lineAlpha: number;
    lineWidth: number;
}

export interface GridTacticalStateUpdate
{
    origin?: GridPoint | null;
    movementRadius?: number;
    movement?: GridPoint[];
    path?: GridPoint[];
    targets?: GridPoint[];
    blocked?: GridPoint[];
    selected?: GridPoint | null;
}

export interface GridTacticalStateSnapshot
{
    enabled: boolean;
    revision: number;
    origin: GridPoint | null;
    movementRadius: number;
    movement: GridPoint[];
    path: GridPoint[];
    targets: GridPoint[];
    blocked: GridPoint[];
    selected: GridPoint | null;
}

export class GridEngine
{
    private static _enabled = true;
    private static _revision = 0;
    private static _origin: GridPoint = null;
    private static _movementRadius = 0;

    private static _style: GridVisualStyle = {
        lineColor: 0xD7E6F0,
        lineAlpha: 0.65,
        lineWidth: 1
    };

    private static _layers: Record<GridTileKind, Map<string, GridTile>> = {
        movement: new Map(),
        path: new Map(),
        target: new Map(),
        selected: new Map(),
        blocked: new Map()
    };

    private static _tileStyles: Record<GridTileKind, GridTileVisualStyle> = {
        movement: {
            fillColor: 0x2D8CFF,
            fillAlpha: 0.28,
            lineColor: 0x6DB2FF,
            lineAlpha: 0.9,
            lineWidth: 1
        },
        path: {
            fillColor: 0x36C98F,
            fillAlpha: 0.38,
            lineColor: 0x8AF0C8,
            lineAlpha: 0.95,
            lineWidth: 1
        },
        target: {
            fillColor: 0xE24B4B,
            fillAlpha: 0.32,
            lineColor: 0xFF8A8A,
            lineAlpha: 0.95,
            lineWidth: 1
        },
        selected: {
            fillColor: 0xF2C94C,
            fillAlpha: 0.4,
            lineColor: 0xFFF0A0,
            lineAlpha: 1,
            lineWidth: 2
        },
        blocked: {
            fillColor: 0x252A30,
            fillAlpha: 0.45,
            lineColor: 0x111418,
            lineAlpha: 0.9,
            lineWidth: 1
        }
    };

    public static get enabled(): boolean
    {
        return this._enabled;
    }

    public static get revision(): number
    {
        return this._revision;
    }

    public static get style(): GridVisualStyle
    {
        return this._style;
    }

    public static get origin(): GridPoint
    {
        return this._origin ? { ...this._origin } : null;
    }

    public static get movementRadius(): number
    {
        return this._movementRadius;
    }

    public static get tiles(): GridTile[]
    {
        const order: GridTileKind[] = [ 'movement', 'blocked', 'path', 'target', 'selected' ];
        const result: GridTile[] = [];

        for(const kind of order)
        {
            for(const tile of this._layers[kind].values())
            {
                result.push({ ...tile });
            }
        }

        return result;
    }

    public static get snapshot(): GridTacticalStateSnapshot
    {
        const selected = this.getTiles('selected')[0] ?? null;

        return {
            enabled: this._enabled,
            revision: this._revision,
            origin: this.origin,
            movementRadius: this._movementRadius,
            movement: this.getTiles('movement'),
            path: this.getTiles('path'),
            targets: this.getTiles('target'),
            blocked: this.getTiles('blocked'),
            selected
        };
    }

    public static getTileStyle(kind: GridTileKind): GridTileVisualStyle
    {
        return this._tileStyles[kind];
    }

    public static getTiles(kind: GridTileKind): GridPoint[]
    {
        return Array.from(this._layers[kind].values()).map(tile => ({
            x: tile.x,
            y: tile.y
        }));
    }

    public static setEnabled(enabled: boolean): void
    {
        if(enabled === this._enabled) return;

        this._enabled = enabled;
        this._revision++;
    }

    public static configure(style: Partial<GridVisualStyle>): void
    {
        this._style = { ...this._style, ...style };
        this._revision++;
    }

    public static configureTile(kind: GridTileKind, style: Partial<GridTileVisualStyle>): void
    {
        this._tileStyles[kind] = { ...this._tileStyles[kind], ...style };
        this._revision++;
    }

    public static updateState(update: GridTacticalStateUpdate): void
    {
        let changed = false;

        if(Object.prototype.hasOwnProperty.call(update, 'origin'))
        {
            const nextOrigin = update.origin ? GridGeometry.normalize(update.origin) : null;

            if(!GridGeometry.equals(this._origin, nextOrigin) && !(!this._origin && !nextOrigin))
            {
                this._origin = nextOrigin;
                changed = true;
            }
        }

        if(Object.prototype.hasOwnProperty.call(update, 'movementRadius'))
        {
            const nextRadius = Math.max(0, Math.trunc(update.movementRadius ?? 0));

            if(nextRadius !== this._movementRadius)
            {
                this._movementRadius = nextRadius;
                changed = true;
            }
        }

        if(Object.prototype.hasOwnProperty.call(update, 'movement'))
        {
            changed = this.replaceLayer('movement', update.movement ?? []) || changed;
        }

        if(Object.prototype.hasOwnProperty.call(update, 'path'))
        {
            changed = this.replaceLayer('path', update.path ?? []) || changed;
        }

        if(Object.prototype.hasOwnProperty.call(update, 'targets'))
        {
            changed = this.replaceLayer('target', update.targets ?? []) || changed;
        }

        if(Object.prototype.hasOwnProperty.call(update, 'blocked'))
        {
            changed = this.replaceLayer('blocked', update.blocked ?? []) || changed;
        }

        if(Object.prototype.hasOwnProperty.call(update, 'selected'))
        {
            changed = this.replaceLayer('selected', update.selected ? [ update.selected ] : []) || changed;
        }

        if(changed) this._revision++;
    }

    public static setTiles(kind: GridTileKind, tiles: GridPoint[]): void
    {
        if(this.replaceLayer(kind, tiles ?? [])) this._revision++;
    }

    public static setSelected(x: number, y: number): void
    {
        this.updateState({ selected: { x, y } });
    }

    public static clearTiles(kind: GridTileKind = null): void
    {
        if(kind !== null)
        {
            if(!this._layers[kind].size) return;

            this._layers[kind].clear();
            this._revision++;
            return;
        }

        let changed = false;

        for(const key of Object.keys(this._layers) as GridTileKind[])
        {
            if(!this._layers[key].size) continue;

            this._layers[key].clear();
            changed = true;
        }

        if(this._origin)
        {
            this._origin = null;
            changed = true;
        }

        if(this._movementRadius !== 0)
        {
            this._movementRadius = 0;
            changed = true;
        }

        if(changed) this._revision++;
    }

    private static replaceLayer(kind: GridTileKind, tiles: GridPoint[]): boolean
    {
        const next = new Map<string, GridTile>();

        for(const tile of tiles)
        {
            if(!tile || !Number.isFinite(tile.x) || !Number.isFinite(tile.y)) continue;

            const normalized = GridGeometry.normalize(tile);
            const key = GridGeometry.key(normalized);

            next.set(key, {
                ...normalized,
                kind
            });
        }

        const current = this._layers[kind];

        if(current.size !== next.size)
        {
            this._layers[kind] = next;
            return true;
        }

        for(const key of next.keys())
        {
            if(!current.has(key))
            {
                this._layers[kind] = next;
                return true;
            }
        }

        return false;
    }
}