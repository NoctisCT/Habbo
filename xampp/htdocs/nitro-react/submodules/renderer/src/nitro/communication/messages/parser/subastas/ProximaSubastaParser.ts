import { IMessageDataWrapper, IMessageParser } from '../../../../../api';

export class ProximaSubastaParser implements IMessageParser
{
    private _hay = false;
    private _subastaId = 0;
    private _objeto = '';
    private _spriteId = 0;
    private _tipo = 's';
    private _vendedor = '';
    private _precioInicial = 0;
    private _cantidad = 0;
    private _posicionCola = 0;
    private _sesion = '';
    private _inicioSesion = '';

    public flush(): boolean
    {
        this._hay = false;
        this._subastaId = 0;
        this._objeto = '';
        this._spriteId = 0;
        this._tipo = 's';
        this._vendedor = '';
        this._precioInicial = 0;
        this._cantidad = 0;
        this._posicionCola = 0;
        this._sesion = '';
        this._inicioSesion = '';

        return true;
    }

    public parse(wrapper: IMessageDataWrapper): boolean
    {
        if(!wrapper) return false;

        this._hay = wrapper.readBoolean();
        this._subastaId = wrapper.readInt();
        this._objeto = wrapper.readString();
        this._spriteId = wrapper.readInt();
        this._tipo = wrapper.readString();
        this._vendedor = wrapper.readString();
        this._precioInicial = wrapper.readInt();
        this._cantidad = wrapper.readInt();
        this._posicionCola = wrapper.readInt();
        this._sesion = wrapper.readString();
        this._inicioSesion = wrapper.readString();

        return true;
    }

    public get hay(): boolean { return this._hay; }
    public get subastaId(): number { return this._subastaId; }
    public get objeto(): string { return this._objeto; }
    public get spriteId(): number { return this._spriteId; }
    public get tipo(): string { return this._tipo; }
    public get vendedor(): string { return this._vendedor; }
    public get precioInicial(): number { return this._precioInicial; }
    public get cantidad(): number { return this._cantidad; }
    public get posicionCola(): number { return this._posicionCola; }
    public get sesion(): string { return this._sesion; }
    public get inicioSesion(): string { return this._inicioSesion; }
}