import { IMessageComposer } from '../../../../../api';

export class ObtenerProximaSubastaComposer implements IMessageComposer<ConstructorParameters<typeof ObtenerProximaSubastaComposer>>
{
    private _data: ConstructorParameters<typeof ObtenerProximaSubastaComposer>;

    constructor()
    {
        this._data = [];
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