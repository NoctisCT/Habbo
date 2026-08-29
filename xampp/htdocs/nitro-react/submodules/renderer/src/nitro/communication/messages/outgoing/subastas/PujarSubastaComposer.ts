import { IMessageComposer } from '../../../../../api';

export class PujarSubastaComposer implements IMessageComposer<ConstructorParameters<typeof PujarSubastaComposer>>
{
    private _data: ConstructorParameters<typeof PujarSubastaComposer>;

    constructor(subastaId: number, cantidad: number)
    {
        this._data = [ subastaId, cantidad ];
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