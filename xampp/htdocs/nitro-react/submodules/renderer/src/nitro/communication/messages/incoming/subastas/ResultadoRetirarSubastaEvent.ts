import { IMessageEvent } from '../../../../../api';
import { MessageEvent } from '../../../../../events';
import { ResultadoRetirarSubastaParser } from '../../parser';

export class ResultadoRetirarSubastaEvent extends MessageEvent implements IMessageEvent
{
    constructor(callBack: Function)
    {
        super(callBack, ResultadoRetirarSubastaParser);
    }

    public getParser(): ResultadoRetirarSubastaParser
    {
        return this.parser as ResultadoRetirarSubastaParser;
    }
}