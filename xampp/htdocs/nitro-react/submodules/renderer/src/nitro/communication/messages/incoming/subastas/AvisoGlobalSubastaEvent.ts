import { IMessageEvent } from '../../../../../api';
import { MessageEvent } from '../../../../../events';
import { AvisoGlobalSubastaParser } from '../../parser';

export class AvisoGlobalSubastaEvent extends MessageEvent implements IMessageEvent
{
    constructor(callBack: Function)
    {
        super(callBack, AvisoGlobalSubastaParser);
    }

    public getParser(): AvisoGlobalSubastaParser
    {
        return this.parser as AvisoGlobalSubastaParser;
    }
}