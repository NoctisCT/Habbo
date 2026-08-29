import { IMessageComposer } from '../../../../../api';

export class ObtenerHistorialSubastasComposer implements IMessageComposer<ConstructorParameters<typeof ObtenerHistorialSubastasComposer>>
{
    private _data: ConstructorParameters<typeof ObtenerHistorialSubastasComposer>;

    constructor(pagina: number = 1, busqueda: string = '', filtro: string = 'todos')
    {
        this._data = [ pagina, busqueda, filtro ];
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