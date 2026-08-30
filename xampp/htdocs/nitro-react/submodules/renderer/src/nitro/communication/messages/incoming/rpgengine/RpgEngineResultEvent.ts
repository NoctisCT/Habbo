import { IMessageEvent } from '../../../../../api';
import { MessageEvent } from '../../../../../events';
import { RpgEngineResultParser } from '../../parser/rpgengine';

export class RpgEngineResultEvent extends MessageEvent implements IMessageEvent
{
    constructor(callBack: Function)
    {
        super(callBack, RpgEngineResultParser);
    }

    public getParser(): RpgEngineResultParser
    {
        return this.parser as RpgEngineResultParser;
    }
}