import { GridGeometry, GridPoint } from './GridGeometry';

export interface GridMapCell extends GridPoint
{
    height: number;
    terrainBlocked: boolean;
    furnitureBlocked: boolean;
}

export interface GridMapSummary
{
    width: number;
    height: number;
    totalTiles: number;
    terrainTiles: number;
    walkableTiles: number;
    terrainBlockedTiles: number;
    furnitureBlockedTiles: number;
}

export class GridMap
{
    private _width = 0;
    private _height = 0;
    private _cells = new Map<string, GridMapCell>();

    constructor(width: number = 0, height: number = 0)
    {
        this.setBounds(width, height);
    }

    public get width(): number
    {
        return this._width;
    }

    public get height(): number
    {
        return this._height;
    }

    public get cells(): GridMapCell[]
    {
        return Array.from(this._cells.values()).map(cell => ({ ...cell }));
    }

    public get blockedTiles(): GridPoint[]
    {
        const result: GridPoint[] = [];

        for(const cell of this._cells.values())
        {
            if(!cell.terrainBlocked && !cell.furnitureBlocked) continue;

            result.push({ x: cell.x, y: cell.y });
        }

        return result;
    }

    public get furnitureBlockedTiles(): GridPoint[]
    {
        const result: GridPoint[] = [];

        for(const cell of this._cells.values())
        {
            if(!cell.furnitureBlocked) continue;

            result.push({ x: cell.x, y: cell.y });
        }

        return result;
    }

    public get summary(): GridMapSummary
    {
        let terrainTiles = 0;
        let walkableTiles = 0;
        let terrainBlockedTiles = 0;
        let furnitureBlockedTiles = 0;

        for(const cell of this._cells.values())
        {
            if(cell.terrainBlocked)
            {
                terrainBlockedTiles++;
            }
            else
            {
                terrainTiles++;

                if(!cell.furnitureBlocked) walkableTiles++;
            }

            if(cell.furnitureBlocked) furnitureBlockedTiles++;
        }

        return {
            width: this._width,
            height: this._height,
            totalTiles: this._width * this._height,
            terrainTiles,
            walkableTiles,
            terrainBlockedTiles,
            furnitureBlockedTiles
        };
    }

    public setBounds(width: number, height: number): void
    {
        this._width = Math.max(0, Math.trunc(width));
        this._height = Math.max(0, Math.trunc(height));
    }

    public setCell(x: number, y: number, height: number, terrainBlocked: boolean = false): void
    {
        const point = GridGeometry.normalize({ x, y });

        if(!point) return;

        const key = GridGeometry.key(point);
        const existing = this._cells.get(key);

        this._cells.set(key, {
            x: point.x,
            y: point.y,
            height: Number.isFinite(height) ? height : 0,
            terrainBlocked,
            furnitureBlocked: existing?.furnitureBlocked ?? false
        });
    }

    public getCell(point: GridPoint): GridMapCell
    {
        const cell = this._cells.get(GridGeometry.key(point));

        return cell ? { ...cell } : null;
    }

    public hasTile(point: GridPoint): boolean
    {
        return this._cells.has(GridGeometry.key(point));
    }

    public isTerrainWalkable(point: GridPoint): boolean
    {
        const cell = this._cells.get(GridGeometry.key(point));

        return !!cell && !cell.terrainBlocked;
    }

    public isWalkable(point: GridPoint): boolean
    {
        const cell = this._cells.get(GridGeometry.key(point));

        return !!cell && !cell.terrainBlocked && !cell.furnitureBlocked;
    }

    public heightAt(point: GridPoint): number
    {
        const cell = this._cells.get(GridGeometry.key(point));

        return cell?.height ?? Number.NaN;
    }

    public setTerrainBlocked(point: GridPoint, blocked: boolean = true): void
    {
        const key = GridGeometry.key(point);
        const cell = this._cells.get(key);

        if(!cell) return;

        cell.terrainBlocked = blocked;
    }

    public clearFurniture(): void
    {
        for(const cell of this._cells.values()) cell.furnitureBlocked = false;
    }

    public setFurnitureBlocked(point: GridPoint, blocked: boolean = true): void
    {
        const key = GridGeometry.key(point);
        const cell = this._cells.get(key);

        if(!cell) return;

        cell.furnitureBlocked = blocked;
    }
}