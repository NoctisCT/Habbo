import { IMessageEvent } from '../../../../../api';
import { MessageEvent } from '../../../../../events';
import { FeedSubastasParser } from '../../parser';

export class FeedSubastasEvent extends MessageEvent implements IMessageEvent
{
    constructor(callBack: Function)
    {
        super(callBack, FeedSubastasParser);
    }

    public getParser(): FeedSubastasParser
    {
        return this.parser as FeedSubastasParser;
    }
}