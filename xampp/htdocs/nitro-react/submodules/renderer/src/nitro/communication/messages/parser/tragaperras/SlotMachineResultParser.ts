import { IMessageDataWrapper, IMessageParser } from '../../../../../api';

export class SlotMachineResultParser implements IMessageParser
{
    private _success: boolean;
    private _message: string;
    private _itemId: number;
    private _balanceAfter: number;
    private _jackpotAfter: number;
    private _normalPrize: number;
    private _jackpotPrize: number;
    private _jackpotHit: boolean;
    private _symbol1: number;
    private _symbol2: number;
    private _symbol3: number;

    public flush(): boolean
    {
        this._success = false;
        this._message = '';
        this._itemId = 0;
        this._balanceAfter = 0;
        this._jackpotAfter = 0;
        this._normalPrize = 0;
        this._jackpotPrize = 0;
        this._jackpotHit = false;
        this._symbol1 = -1;
        this._symbol2 = -1;
        this._symbol3 = -1;

        return true;
    }

    public parse(wrapper: IMessageDataWrapper): boolean
    {
        if(!wrapper) return false;

        this._success = wrapper.readBoolean();
        this._message = wrapper.readString();
        this._itemId = wrapper.readInt();
        this._balanceAfter = wrapper.readInt();
        this._jackpotAfter = wrapper.readInt();
        this._normalPrize = wrapper.readInt();
        this._jackpotPrize = wrapper.readInt();
        this._jackpotHit = wrapper.readBoolean();
        this._symbol1 = wrapper.readInt();
        this._symbol2 = wrapper.readInt();
        this._symbol3 = wrapper.readInt();

        return true;
    }

    public get success(): boolean { return this._success; }
    public get message(): string { return this._message; }
    public get itemId(): number { return this._itemId; }
    public get balanceAfter(): number { return this._balanceAfter; }
    public get jackpotAfter(): number { return this._jackpotAfter; }
    public get normalPrize(): number { return this._normalPrize; }
    public get jackpotPrize(): number { return this._jackpotPrize; }
    public get jackpotHit(): boolean { return this._jackpotHit; }
    public get symbol1(): number { return this._symbol1; }
    public get symbol2(): number { return this._symbol2; }
    public get symbol3(): number { return this._symbol3; }
}