import { IMessageComposer } from '../../../../../api';

export class AbrirSubastasComposer implements IMessageComposer<ConstructorParameters<typeof AbrirSubastasComposer>>
{
    private _data: ConstructorParameters<typeof AbrirSubastasComposer>;

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