import { Graphics } from '@pixi/graphics';
import { IRoomGeometry, RoomObjectVariable } from '../../../../../api';
import { FurnitureAnimatedVisualization } from '../furniture';
import { CombatGridManager } from '../../../../communication/NitroMessages';

export class TileCursorVisualization extends FurnitureAnimatedVisualization {
    private _tileHeight: number;
    private _gridGraphics: Graphics = null;

    constructor() {
        super();
        this._tileHeight = 0;
    }

    protected getLayerYOffset(scale: number, direction: number, layerId: number): number {
        if (layerId === 1) {
            this._tileHeight = this.object.model.getValue<number>(RoomObjectVariable.TILE_CURSOR_HEIGHT);
            return -(this._tileHeight) * 32;
        }

        return super.getLayerYOffset(scale, direction, layerId);
    }

    public update(geometry: IRoomGeometry, time: number, update: boolean, skipUpdate: boolean): void {
        super.update(geometry, time, update, skipUpdate);

        const container = (this as any)._container || (this as any).container;
        if (!container) return;

        if (!this._gridGraphics) {
            this._gridGraphics = new Graphics();
            container.addChild(this._gridGraphics);
        }

        this._gridGraphics.clear();

        const tiles = CombatGridManager.tiles;
        if (!tiles || tiles.length === 0) return;

        const cursorLoc = this.object.getLocation();
        const cursorScreen = geometry.getScreenPoint(cursorLoc);
        if (!cursorScreen) return;

        // Estilo visual de la cuadrícula táctica (Azul neón RPG)
        this._gridGraphics.lineStyle(2, 0x00D2FF, 0.6);
        this._gridGraphics.beginFill(0x0055FF, 0.25);

        for (const tile of tiles) {
            const p1 = geometry.getScreenPoint({ x: tile.x, y: tile.y, z: 0 } as any);
            const p2 = geometry.getScreenPoint({ x: tile.x + 1, y: tile.y, z: 0 } as any);
            const p3 = geometry.getScreenPoint({ x: tile.x + 1, y: tile.y + 1, z: 0 } as any);
            const p4 = geometry.getScreenPoint({ x: tile.x, y: tile.y + 1, z: 0 } as any);

            if (!p1 || !p2 || !p3 || !p4) continue;

            const c1x = p1.x - cursorScreen.x;
            const c1y = p1.y - cursorScreen.y;
            const c2x = p2.x - cursorScreen.x;
            const c2y = p2.y - cursorScreen.y;
            const c3x = p3.x - cursorScreen.x;
            const c3y = p3.y - cursorScreen.y;
            const c4x = p4.x - cursorScreen.x;
            const c4y = p4.y - cursorScreen.y;

            this._gridGraphics.moveTo(c1x, c1y);
            this._gridGraphics.lineTo(c2x, c2y);
            this._gridGraphics.lineTo(c3x, c3y);
            this._gridGraphics.lineTo(c4x, c4y);
            this._gridGraphics.closePath();
        }

        this._gridGraphics.endFill();
    }
}