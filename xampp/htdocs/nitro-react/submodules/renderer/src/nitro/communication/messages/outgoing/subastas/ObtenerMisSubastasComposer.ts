import { IMessageComposer } from '../../../../../api';

export class ObtenerMisSubastasComposer implements IMessageComposer<ConstructorParameters<typeof ObtenerMisSubastasComposer>>
{
    private _data: ConstructorParameters<typeof ObtenerMisSubastasComposer>;

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