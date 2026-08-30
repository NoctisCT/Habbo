import { IMessageEvent } from '../../../../../api';
import { MessageEvent } from '../../../../../events';
import { HoloGridWalkabilityParser } from '../../parser/hologrid';

export class HoloGridWalkabilityEvent extends MessageEvent implements IMessageEvent
{
    constructor(callBack: Function)
    {
        super(callBack, HoloGridWalkabilityParser);
    }

    public getParser(): HoloGridWalkabilityParser
    {
        return this.parser as HoloGridWalkabilityParser;
    }
}