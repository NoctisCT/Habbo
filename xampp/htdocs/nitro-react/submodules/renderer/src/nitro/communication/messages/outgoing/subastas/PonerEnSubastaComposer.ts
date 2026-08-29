import { IMessageComposer } from '../../../../../api';

export class PonerEnSubastaComposer implements IMessageComposer<ConstructorParameters<typeof PonerEnSubastaComposer>>
{
    private _data: ConstructorParameters<typeof PonerEnSubastaComposer>;

    constructor(instanciaIds: number[], precioInicial: number)
    {
        this._data = [ instanciaIds, precioInicial ];
    }

    public getMessageArray()
    {
        return [ this._data[0].length, ...this._data[0], this._data[1] ];
    }

    public dispose(): void
    {
        return;
    }
}