import { IMessageDataWrapper, IMessageParser } from '../../../../../api';

export class ResultadoConfigurarPatronParser implements IMessageParser
{
    private _success = false;
    private _enabled = false;
    private _code = 0;
    private _blockedSeconds = 0;

    public flush(): boolean
    {
        this._success = false;
        this._enabled = false;
        this._code = 0;
        this._blockedSeconds = 0;
        return true;
    }

    public parse(wrapper: IMessageDataWrapper): boolean
    {
        if(!wrapper) return false;

        this._success = wrapper.readBoolean();
        this._enabled = wrapper.readBoolean();
        this._code = wrapper.readInt();
        this._blockedSeconds = wrapper.readInt();

        return true;
    }

    public get success(): boolean { return this._success; }
    public get exito(): boolean { return this._success; }

    public get enabled(): boolean { return this._enabled; }
    public get habilitado(): boolean { return this._enabled; }
    public get activo(): boolean { return this._enabled; }

    public get code(): number { return this._code; }
    public get codigo(): number { return this._code; }

    public get blockedSeconds(): number { return this._blockedSeconds; }
    public get segundosBloqueo(): number { return this._blockedSeconds; }
}