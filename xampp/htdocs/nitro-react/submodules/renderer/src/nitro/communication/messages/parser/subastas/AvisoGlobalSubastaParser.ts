import { IMessageDataWrapper, IMessageParser } from '../../../../../api';

export class AvisoGlobalSubastaParser implements IMessageParser
{
    private _mensaje = '';

    public flush(): boolean
    {
        this._mensaje = '';
        return true;
    }

    public parse(wrapper: IMessageDataWrapper): boolean
    {
        if(!wrapper) return false;

        this._mensaje = wrapper.readString();
        return true;
    }

    public get mensaje(): string
    {
        return this._mensaje;
    }
}