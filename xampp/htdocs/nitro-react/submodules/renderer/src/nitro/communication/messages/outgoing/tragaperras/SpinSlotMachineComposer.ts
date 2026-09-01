import { IMessageComposer } from '../../../../../api';

export class SpinSlotMachineComposer implements IMessageComposer<ConstructorParameters<typeof SpinSlotMachineComposer>>
{
    private _data: ConstructorParameters<typeof SpinSlotMachineComposer>;

    constructor(itemId: number)
    {
        this._data = [ itemId ];
    }

    public getMessageArray()
    {
        return this._data;
    }

    public dispose(): void
    {
        return;
    }
}