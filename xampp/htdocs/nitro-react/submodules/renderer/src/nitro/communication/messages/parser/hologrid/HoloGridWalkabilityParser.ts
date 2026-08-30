import { IMessageDataWrapper, IMessageParser } from '../../../../../api';

export interface HoloGridFurnitureWalkability
{
    itemId: number;
    allowWalk: boolean;
    allowSit: boolean;
    allowLay: boolean;
}

export interface HoloGridTileHeight
{
    x: number;
    y: number;
    height: number;
}

export class HoloGridWalkabilityParser implements IMessageParser
{
    private _roomId = -1;
    private _items: HoloGridFurnitureWalkability[] = [];
    private _tiles: HoloGridTileHeight[] = [];

    public flush(): boolean
    {
        this._roomId = -1;
        this._items = [];
        this._tiles = [];

        return true;
    }

    public parse(wrapper: IMessageDataWrapper): boolean
    {
        if(!wrapper) return false;

        this._roomId = wrapper.readInt();

        const count = Math.max(0, wrapper.readInt());
        const items: HoloGridFurnitureWalkability[] = [];

        for(let i = 0; i < count; i++)
        {
            items.push({
                itemId: wrapper.readInt(),
                allowWalk: wrapper.readBoolean(),
                allowSit: wrapper.readBoolean(),
                allowLay: wrapper.readBoolean()
            });
        }

        const tileCount = Math.max(0, wrapper.readInt());
        const tiles: HoloGridTileHeight[] = [];

        for(let i = 0; i < tileCount; i++)
        {
            tiles.push({
                x: wrapper.readInt(),
                y: wrapper.readInt(),
                height: wrapper.readInt() / 1000
            });
        }

        this._items = items;
        this._tiles = tiles;

        return true;
    }

    public get roomId(): number
    {
        return this._roomId;
    }

    public get items(): HoloGridFurnitureWalkability[]
    {
        return this._items.map(item => ({ ...item }));
    }

    public get tiles(): HoloGridTileHeight[]
    {
        return this._tiles.map(tile => ({ ...tile }));
    }
}