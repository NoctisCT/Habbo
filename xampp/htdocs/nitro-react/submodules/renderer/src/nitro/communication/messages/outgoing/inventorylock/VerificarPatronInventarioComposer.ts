import { IMessageComposer } from '../../../../../api';

export class VerificarPatronInventarioComposer implements IMessageComposer<ConstructorParameters<typeof VerificarPatronInventarioComposer>>
{
    private _data: ConstructorParameters<typeof VerificarPatronInventarioComposer>;

    constructor(pattern: string)
    {
        this._data = [ pattern ];
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