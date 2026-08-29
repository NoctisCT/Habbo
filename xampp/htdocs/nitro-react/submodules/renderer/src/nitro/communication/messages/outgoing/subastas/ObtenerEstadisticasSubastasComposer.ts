import { IMessageComposer } from '../../../../../api';

export class ObtenerEstadisticasSubastasComposer implements IMessageComposer<ConstructorParameters<typeof ObtenerEstadisticasSubastasComposer>>
{
    private _data: ConstructorParameters<typeof ObtenerEstadisticasSubastasComposer>;

    constructor(furniId: number, busqueda: string = '')
    {
        this._data = [ furniId, busqueda ];
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