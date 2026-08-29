import { IMessageEvent } from '../../../../../api';
import { MessageEvent } from '../../../../../events';
import { RecordsSubastasParser } from '../../parser';

export class RecordsSubastasEvent extends MessageEvent implements IMessageEvent
{
    constructor(callBack: Function)
    {
        super(callBack, RecordsSubastasParser);
    }

    public getParser(): RecordsSubastasParser
    {
        return this.parser as RecordsSubastasParser;
    }
}