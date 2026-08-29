import type { GridPoint } from './GridGeometry';

export type GridDirectionMode = 4 | 8;

export interface GridMovementRules
{
    directions: GridDirectionMode;
    orthogonalCost: number;
    diagonalCost: number;
    allowCornerCutting: boolean;
}

export interface GridMovementRulesUpdate
{
    directions?: GridDirectionMode;
    orthogonalCost?: number;
    diagonalCost?: number;
    allowCornerCutting?: boolean;
}

export interface GridMovementStep extends GridPoint
{
    cost: number;
    diagonal: boolean;
}

export class GridMovementPolicy
{
    private _rules: GridMovementRules = {
        directions: 8,
        orthogonalCost: 1,
        diagonalCost: 1,
        allowCornerCutting: false
    };

    public get rules(): GridMovementRules
    {
        return { ...this._rules };
    }

    public configure(update: GridMovementRulesUpdate): GridMovementRules
    {
        if(!update) return this.rules;

        const next = { ...this._rules };

        if(update.directions === 4 || update.directions === 8)
        {
            next.directions = update.directions;
        }

        if(Number.isFinite(update.orthogonalCost))
        {
            next.orthogonalCost = Math.max(0.001, update.orthogonalCost);
        }

        if(Number.isFinite(update.diagonalCost))
        {
            next.diagonalCost = Math.max(0.001, update.diagonalCost);
        }

        if(typeof update.allowCornerCutting === 'boolean')
        {
            next.allowCornerCutting = update.allowCornerCutting;
        }

        this._rules = next;

        return this.rules;
    }

    public stepsFrom(point: GridPoint): GridMovementStep[]
    {
        if(!point) return [];

        const x = Math.trunc(point.x);
        const y = Math.trunc(point.y);

        const steps: GridMovementStep[] = [
            { x: x + 1, y, cost: this._rules.orthogonalCost, diagonal: false },
            { x: x - 1, y, cost: this._rules.orthogonalCost, diagonal: false },
            { x, y: y + 1, cost: this._rules.orthogonalCost, diagonal: false },
            { x, y: y - 1, cost: this._rules.orthogonalCost, diagonal: false }
        ];

        if(this._rules.directions === 8)
        {
            steps.push(
                { x: x + 1, y: y + 1, cost: this._rules.diagonalCost, diagonal: true },
                { x: x + 1, y: y - 1, cost: this._rules.diagonalCost, diagonal: true },
                { x: x - 1, y: y + 1, cost: this._rules.diagonalCost, diagonal: true },
                { x: x - 1, y: y - 1, cost: this._rules.diagonalCost, diagonal: true }
            );
        }

        return steps;
    }

    public stepBetween(from: GridPoint, to: GridPoint): GridMovementStep
    {
        if(!from || !to) return null;

        const dx = Math.trunc(to.x) - Math.trunc(from.x);
        const dy = Math.trunc(to.y) - Math.trunc(from.y);
        const absX = Math.abs(dx);
        const absY = Math.abs(dy);

        if((absX === 0) && (absY === 0)) return null;

        if((absX + absY) === 1)
        {
            return {
                x: Math.trunc(to.x),
                y: Math.trunc(to.y),
                cost: this._rules.orthogonalCost,
                diagonal: false
            };
        }

        if((this._rules.directions === 8) && (absX === 1) && (absY === 1))
        {
            return {
                x: Math.trunc(to.x),
                y: Math.trunc(to.y),
                cost: this._rules.diagonalCost,
                diagonal: true
            };
        }

        return null;
    }
}