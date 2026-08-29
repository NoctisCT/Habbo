import { IMessageDataWrapper, IMessageParser } from '../../../../../api';

export class EstadoBloqueoInventarioParser implements IMessageParser
{
    private _enabled = false;
    private _unlocked = false;
    private _blockedSeconds = 0;

    public flush(): boolean
    {
        this._enabled = false;
        this._unlocked = false;
        this._blockedSeconds = 0;
        return true;
    }

    public parse(wrapper: IMessageDataWrapper): boolean
    {
        if(!wrapper) return false;

        this._enabled = wrapper.readBoolean();
        this._unlocked = wrapper.readBoolean();
        this._blockedSeconds = wrapper.readInt();

        return true;
    }

    public get enabled(): boolean { return this._enabled; }
    public get habilitado(): boolean { return this._enabled; }
    public get activo(): boolean { return this._enabled; }

    public get unlocked(): boolean { return this._unlocked; }
    public get desbloqueado(): boolean { return this._unlocked; }
    public get bloqueado(): boolean { return !this._unlocked; }

    public get blockedSeconds(): number { return this._blockedSeconds; }
    public get segundosBloqueo(): number { return this._blockedSeconds; }
}