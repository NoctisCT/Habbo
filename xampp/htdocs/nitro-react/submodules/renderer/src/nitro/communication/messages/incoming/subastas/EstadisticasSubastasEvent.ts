import { IMessageEvent } from '../../../../../api';
import { MessageEvent } from '../../../../../events';
import { EstadisticasSubastasParser } from '../../parser';

export class EstadisticasSubastasEvent extends MessageEvent implements IMessageEvent
{
    constructor(callBack: Function)
    {
        super(callBack, EstadisticasSubastasParser);
    }

    public getParser(): EstadisticasSubastasParser
    {
        return this.parser as EstadisticasSubastasParser;
    }
}