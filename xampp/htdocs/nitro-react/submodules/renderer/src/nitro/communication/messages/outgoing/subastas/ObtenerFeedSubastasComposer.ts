import { IMessageComposer } from '../../../../../api';

export class ObtenerFeedSubastasComposer implements IMessageComposer<ConstructorParameters<typeof ObtenerFeedSubastasComposer>>
{
    private _data: ConstructorParameters<typeof ObtenerFeedSubastasComposer>;

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