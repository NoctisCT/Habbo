import { IMessageDataWrapper, IMessageParser } from '../../../../../api';

export interface IHistorialSubasta
{
    id: number;
    furniId: number;
    spriteId: number;
    tipo: string;
    nombre: string;
    cantidad: number;
    vendedor: string;
    ganador: string;
    precioInicial: number;
    precioFinal: number;
    estado: string;
    fecha: string;
}

export class HistorialSubastasParser implements IMessageParser
{
    private _pagina = 1;
    private _paginas = 1;
    private _total = 0;
    private _subastas: IHistorialSubasta[] = [];

    public flush(): boolean
    {
        this._pagina = 1;
        this._paginas = 1;
        this._total = 0;
        this._subastas = [];

        return true;
    }

    public parse(wrapper: IMessageDataWrapper): boolean
    {
        if(!wrapper) return false;

        this._pagina = wrapper.readInt();
        this._paginas = wrapper.readInt();
        this._total = wrapper.readInt();

        const cantidad = wrapper.readInt();
        const subastas: IHistorialSubasta[] = [];

        for(let i = 0; i < cantidad; i++)
        {
            subastas.push({
                id: wrapper.readInt(),
                furniId: wrapper.readInt(),
                spriteId: wrapper.readInt(),
                tipo: wrapper.readString(),
                nombre: wrapper.readString(),
                cantidad: wrapper.readInt(),
                vendedor: wrapper.readString(),
                ganador: wrapper.readString(),
                precioInicial: wrapper.readInt(),
                precioFinal: wrapper.readInt(),
                estado: wrapper.readString(),
                fecha: wrapper.readString()
            });
        }

        this._subastas = subastas;

        return true;
    }

    public get pagina(): number { return this._pagina; }
    public get paginas(): number { return this._paginas; }
    public get total(): number { return this._total; }
    public get subastas(): IHistorialSubasta[] { return this._subastas; }
}