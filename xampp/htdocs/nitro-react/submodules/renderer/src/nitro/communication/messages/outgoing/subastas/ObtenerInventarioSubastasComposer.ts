import { IMessageComposer } from '../../../../../api';

export class ObtenerInventarioSubastasComposer implements IMessageComposer<ConstructorParameters<typeof ObtenerInventarioSubastasComposer>>
{
    private _data: ConstructorParameters<typeof ObtenerInventarioSubastasComposer>;

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