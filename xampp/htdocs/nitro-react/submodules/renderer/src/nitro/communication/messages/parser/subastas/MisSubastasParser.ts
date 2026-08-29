import { IMessageDataWrapper, IMessageParser } from '../../../../../api';

export interface SubastaPropia
{
    id: number;
    objetoId: number;
    furniId: number;
    spriteId: number;
    tipo: string;
    nombre: string;
    precioInicial: number;
    pujaActual: number;
    estado: string;
    posicionCola: number;
    fechaCreacion: string;
    ganador: string;
    comisionAplicada: number;
    netoVendedor: number;
}

export class MisSubastasParser implements IMessageParser
{
    private _limite = 3;
    private _simultaneas = 0;
    private _subastas: SubastaPropia[] = [];

    public flush(): boolean
    {
        this._limite = 3;
        this._simultaneas = 0;
        this._subastas = [];

        return true;
    }

    public parse(wrapper: IMessageDataWrapper): boolean
    {
        if(!wrapper) return false;

        this._limite = wrapper.readInt();
        this._simultaneas = wrapper.readInt();

        const cantidad = wrapper.readInt();
        const subastas: SubastaPropia[] = [];

        for(let i = 0; i < cantidad; i++)
        {
            subastas.push({
                id: wrapper.readInt(),
                objetoId: wrapper.readInt(),
                furniId: wrapper.readInt(),
                spriteId: wrapper.readInt(),
                tipo: wrapper.readString(),
                nombre: wrapper.readString(),
                precioInicial: wrapper.readInt(),
                pujaActual: wrapper.readInt(),
                estado: wrapper.readString(),
                posicionCola: wrapper.readInt(),
                fechaCreacion: wrapper.readString(),
                ganador: wrapper.readString(),
                comisionAplicada: wrapper.readInt(),
                netoVendedor: wrapper.readInt()
            });
        }

        this._subastas = subastas;

        return true;
    }

    public get limite(): number
    {
        return this._limite;
    }

    public get simultaneas(): number
    {
        return this._simultaneas;
    }

    public get subastas(): SubastaPropia[]
    {
        return this._subastas;
    }
}