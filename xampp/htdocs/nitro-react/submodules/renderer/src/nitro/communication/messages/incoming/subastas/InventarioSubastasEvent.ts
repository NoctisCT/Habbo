import { IMessageEvent } from '../../../../../api';
import { MessageEvent } from '../../../../../events';
import { InventarioSubastasParser } from '../../parser';

export class InventarioSubastasEvent extends MessageEvent implements IMessageEvent
{
    constructor(callBack: Function)
    {
        super(callBack, InventarioSubastasParser);
    }

    public getParser(): InventarioSubastasParser
    {
        return this.parser as InventarioSubastasParser;
    }
}