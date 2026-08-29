import { GridMovementPolicy, GridMovementStep } from './GridMovementPolicy';

export interface GridPoint
{
    x: number;
    y: number;
}

export interface GridPathOptions
{
    policy?: GridMovementPolicy;
    isWalkable?: (point: GridPoint) => boolean;
    canStep?: (from: GridPoint, to: GridPoint, step: GridMovementStep) => boolean;
    maxNodes?: number;
}

export interface GridReachableOptions
{
    policy?: GridMovementPolicy;
    canStep?: (from: GridPoint, to: GridPoint, step: GridMovementStep) => boolean;
    maxNodes?: number;
}

interface GridFrontierNode
{
    point: GridPoint;
    cost: number;
}

export class GridGeometry
{
    public static normalize(point: GridPoint): GridPoint
    {
        if(!point) return null;

        return {
            x: Math.trunc(point.x),
            y: Math.trunc(point.y)
        };
    }

    public static key(point: GridPoint): string
    {
        const normalized = this.normalize(point);

        if(!normalized) return '';

        return `${ normalized.x },${ normalized.y }`;
    }

    public static equals(a: GridPoint, b: GridPoint): boolean
    {
        if(!a || !b) return false;

        return (Math.trunc(a.x) === Math.trunc(b.x)) &&
            (Math.trunc(a.y) === Math.trunc(b.y));
    }

    public static manhattan(a: GridPoint, b: GridPoint): number
    {
        if(!a || !b) return Number.POSITIVE_INFINITY;

        return Math.abs(Math.trunc(a.x) - Math.trunc(b.x)) +
            Math.abs(Math.trunc(a.y) - Math.trunc(b.y));
    }

    public static chebyshev(a: GridPoint, b: GridPoint): number
    {
        if(!a || !b) return Number.POSITIVE_INFINITY;

        return Math.max(
            Math.abs(Math.trunc(a.x) - Math.trunc(b.x)),
            Math.abs(Math.trunc(a.y) - Math.trunc(b.y))
        );
    }

    public static neighbors4(point: GridPoint): GridPoint[]
    {
        const p = this.normalize(point);

        if(!p) return [];

        return [
            { x: p.x + 1, y: p.y },
            { x: p.x - 1, y: p.y },
            { x: p.x, y: p.y + 1 },
            { x: p.x, y: p.y - 1 }
        ];
    }

    public static neighbors8(point: GridPoint): GridPoint[]
    {
        const p = this.normalize(point);

        if(!p) return [];

        return [
            ...this.neighbors4(p),
            { x: p.x + 1, y: p.y + 1 },
            { x: p.x + 1, y: p.y - 1 },
            { x: p.x - 1, y: p.y + 1 },
            { x: p.x - 1, y: p.y - 1 }
        ];
    }

    public static diamond(center: GridPoint, radius: number, includeCenter: boolean = false): GridPoint[]
    {
        const origin = this.normalize(center);

        if(!origin) return [];

        const safeRadius = Math.max(0, Math.trunc(radius));
        const tiles: GridPoint[] = [];

        for(let dx = -safeRadius; dx <= safeRadius; dx++)
        {
            for(let dy = -safeRadius; dy <= safeRadius; dy++)
            {
                const distance = (Math.abs(dx) + Math.abs(dy));

                if(distance > safeRadius) continue;
                if(!includeCenter && (distance === 0)) continue;

                tiles.push({
                    x: origin.x + dx,
                    y: origin.y + dy
                });
            }
        }

        return tiles;
    }

    public static contains(points: GridPoint[], point: GridPoint): boolean
    {
        if(!points || !point) return false;

        const key = this.key(point);

        return points.some(item => this.key(item) === key);
    }

    public static reachable(start: GridPoint, movementBudget: number, options: GridReachableOptions = {}): GridPoint[]
    {
        const origin = this.normalize(start);

        if(!origin) return [];

        const budget = Math.max(0, Number(movementBudget) || 0);
        const policy = options.policy ?? new GridMovementPolicy();
        const canStep = options.canStep ?? (() => true);
        const maxNodes = Math.max(1, Math.trunc(options.maxNodes ?? 4096));

        const costs = new Map<string, number>([[this.key(origin), 0]]);
        const frontier: GridFrontierNode[] = [{ point: origin, cost: 0 }];
        const result: GridPoint[] = [];

        while(frontier.length)
        {
            const current = frontier.shift();
            const currentKey = this.key(current.point);
            const bestKnown = costs.get(currentKey);

            if((bestKnown === undefined) || (current.cost > bestKnown)) continue;
            if(current.cost >= budget) continue;
            if(costs.size >= maxNodes) break;

            for(const step of policy.stepsFrom(current.point))
            {
                if(!canStep(current.point, step, step)) continue;

                const nextCost = current.cost + step.cost;

                if(nextCost > budget) continue;

                const nextPoint = { x: step.x, y: step.y };
                const nextKey = this.key(nextPoint);
                const oldCost = costs.get(nextKey);

                if((oldCost !== undefined) && (oldCost <= nextCost)) continue;

                costs.set(nextKey, nextCost);
                this.insertFrontier(frontier, { point: nextPoint, cost: nextCost });
            }
        }

        for(const [key] of costs)
        {
            if(key === this.key(origin)) continue;

            const [x, y] = key.split(',').map(Number);

            result.push({ x, y });
        }

        return result;
    }

    public static findPath(start: GridPoint, goal: GridPoint, options: GridPathOptions = {}): GridPoint[]
    {
        const from = this.normalize(start);
        const to = this.normalize(goal);

        if(!from || !to) return [];
        if(this.equals(from, to)) return [from];

        const policy = options.policy ?? new GridMovementPolicy();
        const maxNodes = Math.max(1, Math.trunc(options.maxNodes ?? 4096));
        const isWalkable = options.isWalkable ?? (() => true);
        const canStep = options.canStep ?? ((current, next) => isWalkable(next));

        if(!isWalkable(to)) return [];

        const frontier: GridFrontierNode[] = [{ point: from, cost: 0 }];
        const costs = new Map<string, number>([[this.key(from), 0]]);
        const parents = new Map<string, string>();
        const points = new Map<string, GridPoint>([[this.key(from), from]]);

        while(frontier.length)
        {
            const current = frontier.shift();
            const currentKey = this.key(current.point);
            const bestKnown = costs.get(currentKey);

            if((bestKnown === undefined) || (current.cost > bestKnown)) continue;
            if(costs.size >= maxNodes) break;

            if(this.equals(current.point, to))
            {
                const path: GridPoint[] = [];
                let cursor = currentKey;

                while(cursor)
                {
                    const point = points.get(cursor);

                    if(point) path.push(point);

                    if(cursor === this.key(from)) break;

                    cursor = parents.get(cursor);
                }

                path.reverse();

                return path;
            }

            for(const step of policy.stepsFrom(current.point))
            {
                if(!canStep(current.point, step, step)) continue;

                const nextPoint = { x: step.x, y: step.y };
                const nextKey = this.key(nextPoint);
                const nextCost = current.cost + step.cost;
                const oldCost = costs.get(nextKey);

                if((oldCost !== undefined) && (oldCost <= nextCost)) continue;

                costs.set(nextKey, nextCost);
                parents.set(nextKey, currentKey);
                points.set(nextKey, nextPoint);
                this.insertFrontier(frontier, { point: nextPoint, cost: nextCost });
            }
        }

        return [];
    }

    public static pathCost(path: GridPoint[], policy: GridMovementPolicy = new GridMovementPolicy()): number
    {
        if(!path || path.length < 2) return 0;

        let cost = 0;

        for(let i = 1; i < path.length; i++)
        {
            const step = policy.stepBetween(path[i - 1], path[i]);

            if(!step) return Number.POSITIVE_INFINITY;

            cost += step.cost;
        }

        return cost;
    }

    private static insertFrontier(frontier: GridFrontierNode[], node: GridFrontierNode): void
    {
        let low = 0;
        let high = frontier.length;

        while(low < high)
        {
            const mid = (low + high) >> 1;

            if(frontier[mid].cost <= node.cost) low = mid + 1;
            else high = mid;
        }

        frontier.splice(low, 0, node);
    }
}