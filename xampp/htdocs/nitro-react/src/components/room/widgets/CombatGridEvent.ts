import { IMessageEvent, IMessageParser, MessageEvent } from '@nitrots/nitro-renderer';

export class CombatGridParser implements IMessageParser {
    private _tiles: { x: number; y: number }[];

    public flush(): boolean {
        this._tiles = [];
        return true;
    }

    public parse(wrapper: any): boolean {
        if (!wrapper) return false;
        this._tiles = [];
        try {
            const rawData = wrapper.readString();
            if (!rawData) return true;

            if (rawData.trim().startsWith('[')) {
                this._tiles = JSON.parse(rawData);
            } else {
                const pairs = rawData.split(';');
                for (const pair of pairs) {
                    if (!pair) continue;
                    const coords = pair.split(',');
                    if (coords.length === 2) {
                        this._tiles.push({
                            x: parseInt(coords[0], 10),
                            y: parseInt(coords[1], 10)
                        });
                    }
                }
            }
        } catch (e) {
            console.error("Error leyendo el paquete CombatGrid:", e);
        }
        return true;
    }

    public get tiles(): { x: number; y: number }[] {
        return this._tiles;
    }
}

export class CombatGridEvent extends MessageEvent<CombatGridParser> implements IMessageEvent {
    public static id: number = 3500;
    public id: number = 3500;
    constructor(callBack: Function) {
        super(callBack, CombatGridParser);
        (this as any)._id = 3500;
        (this as any).id = 3500;
    }
}