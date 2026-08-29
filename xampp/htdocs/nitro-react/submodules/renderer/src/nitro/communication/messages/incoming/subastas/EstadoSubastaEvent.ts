import { IMessageEvent } from '../../../../../api';
import { MessageEvent } from '../../../../../events';
import { EstadoSubastaParser } from '../../parser';

export class EstadoSubastaEvent extends MessageEvent implements IMessageEvent
{
    constructor(callBack: Function)
    {
        super(callBack, EstadoSubastaParser);
    }

    public getParser(): EstadoSubastaParser
    {
        return this.parser as EstadoSubastaParser;
    }
}