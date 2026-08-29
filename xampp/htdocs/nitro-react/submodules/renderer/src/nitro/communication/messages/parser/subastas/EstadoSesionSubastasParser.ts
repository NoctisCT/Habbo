import { IMessageDataWrapper, IMessageParser } from '../../../../../api';

export class EstadoSesionSubastasParser implements IMessageParser
{
    private _disponible = false;
    private _sesionId = 0;
    private _nombre = '';
    private _cupoMaximo = 0;
    private _ocupados = 0;
    private _inicio = '';
    private _fin = '';

    public flush(): boolean
    {
        this._disponible = false;
        this._sesionId = 0;
        this._nombre = '';
        this._cupoMaximo = 0;
        this._ocupados = 0;
        this._inicio = '';
        this._fin = '';

        return true;
    }

    public parse(wrapper: IMessageDataWrapper): boolean
    {
        if(!wrapper) return false;

        this._disponible = wrapper.readBoolean();
        this._sesionId = wrapper.readInt();
        this._nombre = wrapper.readString();
        this._cupoMaximo = wrapper.readInt();
        this._ocupados = wrapper.readInt();
        this._inicio = wrapper.readString();
        this._fin = wrapper.readString();

        return true;
    }

    public get disponible(): boolean { return this._disponible; }
    public get sesionId(): number { return this._sesionId; }
    public get nombre(): string { return this._nombre; }
    public get cupoMaximo(): number { return this._cupoMaximo; }
    public get ocupados(): number { return this._ocupados; }
    public get inicio(): string { return this._inicio; }
    public get fin(): string { return this._fin; }
}