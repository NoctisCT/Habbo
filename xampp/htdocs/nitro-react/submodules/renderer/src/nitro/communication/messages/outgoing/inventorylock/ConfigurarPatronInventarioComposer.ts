import { IMessageComposer } from '../../../../../api';

export class ConfigurarPatronInventarioComposer implements IMessageComposer<ConstructorParameters<typeof ConfigurarPatronInventarioComposer>>
{
    private _data: ConstructorParameters<typeof ConfigurarPatronInventarioComposer>;

    constructor(action: number, oldPattern: string, newPattern: string)
    {
        this._data = [ action, oldPattern, newPattern ];
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