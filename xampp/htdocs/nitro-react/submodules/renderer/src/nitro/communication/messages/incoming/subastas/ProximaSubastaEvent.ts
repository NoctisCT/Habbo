import { IMessageEvent } from '../../../../../api';
import { MessageEvent } from '../../../../../events';
import { ProximaSubastaParser } from '../../parser';

export class ProximaSubastaEvent extends MessageEvent implements IMessageEvent
{
    constructor(callBack: Function)
    {
        super(callBack, ProximaSubastaParser);
    }

    public getParser(): ProximaSubastaParser
    {
        return this.parser as ProximaSubastaParser;
    }
}