import { IMessageDataWrapper, IMessageParser } from '../../../../../api';

export class SlotMachineOpenParser implements IMessageParser
{
    private _itemId: number;
    private _bet: number;
    private _jackpot: number;
    private _balance: number;
    private _currencyKey: string;
    private _currencyType: number;

    public flush(): boolean
    {
        this._itemId = 0;
        this._bet = 0;
        this._jackpot = 0;
        this._balance = 0;
        this._currencyKey = 'credits';
        this._currencyType = -1;

        return true;
    }

    public parse(wrapper: IMessageDataWrapper): boolean
    {
        if(!wrapper) return false;

        this._itemId = wrapper.readInt();
        this._bet = wrapper.readInt();
        this._jackpot = wrapper.readInt();
        this._balance = wrapper.readInt();
        this._currencyKey = wrapper.readString();
        this._currencyType = wrapper.readInt();

        return true;
    }

    public get itemId(): number
    {
        return this._itemId;
    }

    public get bet(): number
    {
        return this._bet;
    }

    public get jackpot(): number
    {
        return this._jackpot;
    }

    public get balance(): number
    {
        return this._balance;
    }

    public get currencyKey(): string
    {
        return this._currencyKey;
    }

    public get currencyType(): number
    {
        return this._currencyType;
    }
}