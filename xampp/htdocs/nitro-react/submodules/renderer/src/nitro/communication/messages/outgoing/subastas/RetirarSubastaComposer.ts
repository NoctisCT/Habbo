import { IMessageComposer } from '../../../../../api';

export class RetirarSubastaComposer implements IMessageComposer<ConstructorParameters<typeof RetirarSubastaComposer>>
{
    private _data: ConstructorParameters<typeof RetirarSubastaComposer>;

    constructor(subastaId: number)
    {
        this._data = [ subastaId ];
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