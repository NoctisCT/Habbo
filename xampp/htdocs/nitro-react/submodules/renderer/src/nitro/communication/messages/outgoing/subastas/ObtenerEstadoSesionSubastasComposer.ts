import { IMessageComposer } from '../../../../../api';

export class ObtenerEstadoSesionSubastasComposer implements IMessageComposer<ConstructorParameters<typeof ObtenerEstadoSesionSubastasComposer>>
{
    private _data: ConstructorParameters<typeof ObtenerEstadoSesionSubastasComposer>;

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