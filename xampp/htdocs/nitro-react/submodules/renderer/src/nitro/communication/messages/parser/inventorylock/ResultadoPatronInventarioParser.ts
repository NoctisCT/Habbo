import { IMessageDataWrapper, IMessageParser } from '../../../../../api';

export class ResultadoPatronInventarioParser implements IMessageParser
{
    private _correct = false;
    private _blockedSeconds = 0;

    public flush(): boolean
    {
        this._correct = false;
        this._blockedSeconds = 0;
        return true;
    }

    public parse(wrapper: IMessageDataWrapper): boolean
    {
        if(!wrapper) return false;

        this._correct = wrapper.readBoolean();
        this._blockedSeconds = wrapper.readInt();

        return true;
    }

    public get correct(): boolean { return this._correct; }
    public get correcto(): boolean { return this._correct; }
    public get success(): boolean { return this._correct; }
    public get exito(): boolean { return this._correct; }

    public get blockedSeconds(): number { return this._blockedSeconds; }
    public get segundosBloqueo(): number { return this._blockedSeconds; }
}