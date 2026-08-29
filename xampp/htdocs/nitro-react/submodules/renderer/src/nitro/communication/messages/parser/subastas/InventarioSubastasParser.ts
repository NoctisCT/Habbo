import { IMessageDataWrapper, IMessageParser } from '../../../../../api';

export interface FurniInventarioSubastas
{
    instanciaId: number;
    furniId: number;
    spriteId: number;
    nombre: string;
    itemName: string;
    tipo: string;
    rareza: string;
    limitedData: string;
}

export class InventarioSubastasParser implements IMessageParser
{
    private _furnis: FurniInventarioSubastas[] = [];

    public flush(): boolean
    {
        this._furnis = [];

        return true;
    }

    public parse(wrapper: IMessageDataWrapper): boolean
    {
        if(!wrapper) return false;

        const cantidad = wrapper.readInt();
        const furnis: FurniInventarioSubastas[] = [];

        for(let i = 0; i < cantidad; i++)
        {
            furnis.push({
                instanciaId: wrapper.readInt(),
                furniId: wrapper.readInt(),
                spriteId: wrapper.readInt(),
                nombre: wrapper.readString(),
                itemName: wrapper.readString(),
                tipo: wrapper.readString(),
                rareza: wrapper.readString(),
                limitedData: wrapper.readString()
            });
        }

        this._furnis = furnis;

        return true;
    }

    public get furnis(): FurniInventarioSubastas[]
    {
        return this._furnis;
    }
}