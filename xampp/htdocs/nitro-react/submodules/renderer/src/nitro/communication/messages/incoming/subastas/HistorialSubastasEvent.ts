import { IMessageEvent } from '../../../../../api';
import { MessageEvent } from '../../../../../events';
import { HistorialSubastasParser } from '../../parser';

export class HistorialSubastasEvent extends MessageEvent implements IMessageEvent
{
    constructor(callBack: Function)
    {
        super(callBack, HistorialSubastasParser);
    }

    public getParser(): HistorialSubastasParser
    {
        return this.parser as HistorialSubastasParser;
    }
}