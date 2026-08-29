import { IMessageDataWrapper, IMessageParser } from '../../../../../api';

export class EstadoSubastaParser implements IMessageParser
{
    private _activa = false;
    private _mensaje = '';
    private _subastaId = 0;
    private _objeto = '';
    private _spriteId = 0;
    private _tipo = 's';
    private _vendedor = '';
    private _precioInicial = 0;
    private _pujaActual = 0;
    private _segundosRestantes = 0;

    public flush(): boolean
    {
        this._activa = false;
        this._mensaje = '';
        this._subastaId = 0;
        this._objeto = '';
        this._spriteId = 0;
        this._tipo = 's';
        this._vendedor = '';
        this._precioInicial = 0;
        this._pujaActual = 0;
        this._segundosRestantes = 0;

        return true;
    }

    public parse(wrapper: IMessageDataWrapper): boolean
    {
        if(!wrapper) return false;

        this._activa = wrapper.readBoolean();
        this._mensaje = wrapper.readString();
        this._subastaId = wrapper.readInt();
        this._objeto = wrapper.readString();
        this._spriteId = wrapper.readInt();
        this._tipo = wrapper.readString();
        this._vendedor = wrapper.readString();
        this._precioInicial = wrapper.readInt();
        this._pujaActual = wrapper.readInt();
        this._segundosRestantes = wrapper.readInt();

        return true;
    }

    public get activa(): boolean { return this._activa; }
    public get mensaje(): string { return this._mensaje; }
    public get subastaId(): number { return this._subastaId; }
    public get objeto(): string { return this._objeto; }
    public get spriteId(): number { return this._spriteId; }
    public get tipo(): string { return this._tipo; }
    public get vendedor(): string { return this._vendedor; }
    public get precioInicial(): number { return this._precioInicial; }
    public get pujaActual(): number { return this._pujaActual; }
    public get segundosRestantes(): number { return this._segundosRestantes; }
}