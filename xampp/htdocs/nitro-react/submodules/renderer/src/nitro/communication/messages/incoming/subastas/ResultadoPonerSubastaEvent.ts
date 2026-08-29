import { IMessageEvent } from '../../../../../api';
import { MessageEvent } from '../../../../../events';
import { ResultadoPonerSubastaParser } from '../../parser';

export class ResultadoPonerSubastaEvent extends MessageEvent implements IMessageEvent
{
    constructor(callBack: Function)
    {
        super(callBack, ResultadoPonerSubastaParser);
    }

    public getParser(): ResultadoPonerSubastaParser
    {
        return this.parser as ResultadoPonerSubastaParser;
    }
}