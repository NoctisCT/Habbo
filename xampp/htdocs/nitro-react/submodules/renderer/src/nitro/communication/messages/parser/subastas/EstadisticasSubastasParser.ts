import { IMessageDataWrapper, IMessageParser } from '../../../../../api';

export interface IResumenFurniEstadisticas
{
    furniId: number;
    nombre: string;
    spriteId: number;
    tipo: string;
    ventas: number;
    precioMedioUnidad: number;
}

export interface IPuntoPrecioSubasta
{
    fecha: string;
    precioUnidad: number;
    precioLote: number;
    cantidad: number;
}

export interface IDetalleFurniEstadisticas
{
    furniId: number;
    nombre: string;
    spriteId: number;
    tipo: string;

    ventas: number;
    unidadesVendidas: number;
    precioMedioUnidad: number;
    precioMinimoUnidad: number;
    precioMaximoUnidad: number;
    ultimoPrecioUnidad: number;
    volumenCreditos: number;
    tasaVenta: number;
    pujasMedia: number;

    radarValor: number;
    radarDemanda: number;
    radarVolumen: number;
    radarActividad: number;
    radarExito: number;

    historial: IPuntoPrecioSubasta[];
}

export class EstadisticasSubastasParser implements IMessageParser
{
    private _totalVendidas = 0;
    private _totalFinalizadas = 0;
    private _volumenGlobal = 0;
    private _precioMedioGlobal = 0;
    private _tasaVentaGlobal = 0;
    private _recordPrecio = 0;
    private _recordNombre = '';
    private _top: IResumenFurniEstadisticas[] = [];
    private _detalle: IDetalleFurniEstadisticas = null;

    public flush(): boolean
    {
        this._totalVendidas = 0;
        this._totalFinalizadas = 0;
        this._volumenGlobal = 0;
        this._precioMedioGlobal = 0;
        this._tasaVentaGlobal = 0;
        this._recordPrecio = 0;
        this._recordNombre = '';
        this._top = [];
        this._detalle = null;

        return true;
    }

    public parse(wrapper: IMessageDataWrapper): boolean
    {
        if(!wrapper) return false;

        this._totalVendidas = wrapper.readInt();
        this._totalFinalizadas = wrapper.readInt();
        this._volumenGlobal = Number(wrapper.readString()) || 0;
        this._precioMedioGlobal = wrapper.readInt();
        this._tasaVentaGlobal = wrapper.readInt();
        this._recordPrecio = wrapper.readInt();
        this._recordNombre = wrapper.readString();

        const totalTop = wrapper.readInt();
        const top: IResumenFurniEstadisticas[] = [];

        for(let i = 0; i < totalTop; i++)
        {
            top.push({
                furniId: wrapper.readInt(),
                nombre: wrapper.readString(),
                spriteId: wrapper.readInt(),
                tipo: wrapper.readString(),
                ventas: wrapper.readInt(),
                precioMedioUnidad: wrapper.readInt()
            });
        }

        this._top = top;

        const hayDetalle = wrapper.readBoolean();

        if(!hayDetalle)
        {
            this._detalle = null;
            return true;
        }

        const detalle: IDetalleFurniEstadisticas = {
            furniId: wrapper.readInt(),
            nombre: wrapper.readString(),
            spriteId: wrapper.readInt(),
            tipo: wrapper.readString(),

            ventas: wrapper.readInt(),
            unidadesVendidas: wrapper.readInt(),
            precioMedioUnidad: wrapper.readInt(),
            precioMinimoUnidad: wrapper.readInt(),
            precioMaximoUnidad: wrapper.readInt(),
            ultimoPrecioUnidad: wrapper.readInt(),
            volumenCreditos: Number(wrapper.readString()) || 0,
            tasaVenta: wrapper.readInt(),
            pujasMedia: wrapper.readInt() / 100,

            radarValor: wrapper.readInt(),
            radarDemanda: wrapper.readInt(),
            radarVolumen: wrapper.readInt(),
            radarActividad: wrapper.readInt(),
            radarExito: wrapper.readInt(),

            historial: []
        };

        const totalPuntos = wrapper.readInt();

        for(let i = 0; i < totalPuntos; i++)
        {
            detalle.historial.push({
                fecha: wrapper.readString(),
                precioUnidad: wrapper.readInt(),
                precioLote: wrapper.readInt(),
                cantidad: wrapper.readInt()
            });
        }

        this._detalle = detalle;

        return true;
    }

    public get totalVendidas(): number { return this._totalVendidas; }
    public get totalFinalizadas(): number { return this._totalFinalizadas; }
    public get volumenGlobal(): number { return this._volumenGlobal; }
    public get precioMedioGlobal(): number { return this._precioMedioGlobal; }
    public get tasaVentaGlobal(): number { return this._tasaVentaGlobal; }
    public get recordPrecio(): number { return this._recordPrecio; }
    public get recordNombre(): string { return this._recordNombre; }
    public get top(): IResumenFurniEstadisticas[] { return this._top; }
    public get detalle(): IDetalleFurniEstadisticas { return this._detalle; }
}