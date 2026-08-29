import { IMessageDataWrapper, IMessageParser } from '../../../../../api';

export interface IRecordUsuarioSubastas
{
    id: number;
    username: string;
    look: string;
    gender: string;
    valor: number;
    secundario: number;
}

export interface IRecordFurniSubastas
{
    furniId: number;
    nombre: string;
    spriteId: number;
    tipo: string;
    valor: number;
}

export class RecordsSubastasParser implements IMessageParser
{
    private _dineroGanado: IRecordUsuarioSubastas[] = [];
    private _furnisVendidos: IRecordUsuarioSubastas[] = [];
    private _dineroGastado: IRecordUsuarioSubastas[] = [];
    private _compras: IRecordUsuarioSubastas[] = [];

    private _masCaros: IRecordFurniSubastas[] = [];
    private _masVendidos: IRecordFurniSubastas[] = [];
    private _mayorVolumen: IRecordFurniSubastas[] = [];
    private _masPujados: IRecordFurniSubastas[] = [];

    public flush(): boolean
    {
        this._dineroGanado = [];
        this._furnisVendidos = [];
        this._dineroGastado = [];
        this._compras = [];

        this._masCaros = [];
        this._masVendidos = [];
        this._mayorVolumen = [];
        this._masPujados = [];

        return true;
    }

    public parse(wrapper: IMessageDataWrapper): boolean
    {
        if(!wrapper) return false;

        this._dineroGanado = this.leerUsuarios(wrapper);
        this._furnisVendidos = this.leerUsuarios(wrapper);
        this._dineroGastado = this.leerUsuarios(wrapper);
        this._compras = this.leerUsuarios(wrapper);

        this._masCaros = this.leerFurnis(wrapper);
        this._masVendidos = this.leerFurnis(wrapper);
        this._mayorVolumen = this.leerFurnis(wrapper);
        this._masPujados = this.leerFurnis(wrapper);

        return true;
    }

    private leerUsuarios(wrapper: IMessageDataWrapper): IRecordUsuarioSubastas[]
    {
        const total = wrapper.readInt();
        const filas: IRecordUsuarioSubastas[] = [];

        for(let i = 0; i < total; i++)
        {
            filas.push({
                id: wrapper.readInt(),
                username: wrapper.readString(),
                look: wrapper.readString(),
                gender: wrapper.readString(),
                valor: Number(wrapper.readString()) || 0,
                secundario: Number(wrapper.readString()) || 0
            });
        }

        return filas;
    }

    private leerFurnis(wrapper: IMessageDataWrapper): IRecordFurniSubastas[]
    {
        const total = wrapper.readInt();
        const filas: IRecordFurniSubastas[] = [];

        for(let i = 0; i < total; i++)
        {
            filas.push({
                furniId: wrapper.readInt(),
                nombre: wrapper.readString(),
                spriteId: wrapper.readInt(),
                tipo: wrapper.readString(),
                valor: Number(wrapper.readString()) || 0
            });
        }

        return filas;
    }

    public get dineroGanado(): IRecordUsuarioSubastas[] { return this._dineroGanado; }
    public get furnisVendidos(): IRecordUsuarioSubastas[] { return this._furnisVendidos; }
    public get dineroGastado(): IRecordUsuarioSubastas[] { return this._dineroGastado; }
    public get compras(): IRecordUsuarioSubastas[] { return this._compras; }

    public get masCaros(): IRecordFurniSubastas[] { return this._masCaros; }
    public get masVendidos(): IRecordFurniSubastas[] { return this._masVendidos; }
    public get mayorVolumen(): IRecordFurniSubastas[] { return this._mayorVolumen; }
    public get masPujados(): IRecordFurniSubastas[] { return this._masPujados; }
}