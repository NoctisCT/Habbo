import { IMessageDataWrapper, IMessageParser } from '../../../../../api';

export class SlotMachineStateParser implements IMessageParser
{
    private _currencyKey: string;
    private _jackpot: number;

    public flush(): boolean
    {
        this._currencyKey = '';
        this._jackpot = 0;

        return true;
    }

    public parse(wrapper: IMessageDataWrapper): boolean
    {
        if(!wrapper) return false;

        this._currencyKey = wrapper.readString();
        this._jackpot = wrapper.readInt();

        return true;
    }

    public get currencyKey(): string
    {
        return this._currencyKey;
    }

    public get jackpot(): number
    {
        return this._jackpot;
    }
}