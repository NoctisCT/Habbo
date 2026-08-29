import { IMessageComposer } from '../../../../../api';

export class ObtenerRecordsSubastasComposer implements IMessageComposer<ConstructorParameters<typeof ObtenerRecordsSubastasComposer>>
{
    private _data: ConstructorParameters<typeof ObtenerRecordsSubastasComposer>;

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