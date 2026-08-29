import { IMessageEvent } from '../../../../../api';
import { MessageEvent } from '../../../../../events';
import { ResultadoPatronInventarioParser } from '../../parser/inventorylock';

export class ResultadoPatronInventarioEvent extends MessageEvent implements IMessageEvent
{
    constructor(callBack: Function)
    {
        super(callBack, ResultadoPatronInventarioParser);
    }

    public getParser(): ResultadoPatronInventarioParser
    {
        return this.parser as ResultadoPatronInventarioParser;
    }
}