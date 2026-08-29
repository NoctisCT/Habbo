import { IMessageEvent } from '../../../../../api';
import { MessageEvent } from '../../../../../events';
import { MisSubastasParser } from '../../parser';

export class MisSubastasEvent extends MessageEvent implements IMessageEvent
{
    constructor(callBack: Function)
    {
        super(callBack, MisSubastasParser);
    }

    public getParser(): MisSubastasParser
    {
        return this.parser as MisSubastasParser;
    }
}