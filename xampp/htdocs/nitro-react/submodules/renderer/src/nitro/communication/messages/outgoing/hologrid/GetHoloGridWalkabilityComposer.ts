import { IMessageComposer } from '../../../../../api';

export class GetHoloGridWalkabilityComposer implements IMessageComposer<ConstructorParameters<typeof GetHoloGridWalkabilityComposer>>
{
    private _data: ConstructorParameters<typeof GetHoloGridWalkabilityComposer>;

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