import { IMessageComposer } from '../../../../../api';

export class ConsultarBloqueoInventarioComposer implements IMessageComposer<ConstructorParameters<typeof ConsultarBloqueoInventarioComposer>>
{
    private _data: ConstructorParameters<typeof ConsultarBloqueoInventarioComposer>;

    constructor()
    {
        this._data = [  ];
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