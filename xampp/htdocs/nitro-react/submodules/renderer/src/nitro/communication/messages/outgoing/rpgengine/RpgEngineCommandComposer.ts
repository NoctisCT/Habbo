import { IMessageComposer } from '../../../../../api';

export class RpgEngineCommandComposer implements IMessageComposer<ConstructorParameters<typeof RpgEngineCommandComposer>>
{
    private _data: ConstructorParameters<typeof RpgEngineCommandComposer>;

    constructor(action: number, ...args: Array<string | number | boolean>)
    {
        this._data = [ action, ...args ];
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