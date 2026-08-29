import { IMessageEvent } from '../../../../../api';
import { MessageEvent } from '../../../../../events';
import { ResultadoPujaSubastaParser } from '../../parser';

export class ResultadoPujaSubastaEvent extends MessageEvent implements IMessageEvent
{
    constructor(callBack: Function)
    {
        super(callBack, ResultadoPujaSubastaParser);
    }

    public getParser(): ResultadoPujaSubastaParser
    {
        return this.parser as ResultadoPujaSubastaParser;
    }
}