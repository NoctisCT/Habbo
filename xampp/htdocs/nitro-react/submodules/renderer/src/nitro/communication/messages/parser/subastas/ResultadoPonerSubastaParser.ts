import { IMessageDataWrapper, IMessageParser } from '../../../../../api';

export class ResultadoPonerSubastaParser implements IMessageParser
{
    private _exito: boolean;
    private _mensaje: string;
    private _subastaId: number;

    public flush(): boolean
    {
        this._exito = false;
        this._mensaje = '';
        this._subastaId = 0;

        return true;
    }

    public parse(wrapper: IMessageDataWrapper): boolean
    {
        if(!wrapper) return false;

        this._exito = wrapper.readBoolean();
        this._mensaje = wrapper.readString();
        this._subastaId = wrapper.readInt();

        return true;
    }

    public get exito(): boolean
    {
        return this._exito;
    }

    public get mensaje(): string
    {
        return this._mensaje;
    }

    public get subastaId(): number
    {
        return this._subastaId;
    }
}