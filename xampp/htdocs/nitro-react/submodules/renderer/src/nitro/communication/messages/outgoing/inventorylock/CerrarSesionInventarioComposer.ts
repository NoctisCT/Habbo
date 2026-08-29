import { IMessageComposer } from '../../../../../api';

export class CerrarSesionInventarioComposer implements IMessageComposer<ConstructorParameters<typeof CerrarSesionInventarioComposer>>
{
    private _data: ConstructorParameters<typeof CerrarSesionInventarioComposer>;

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