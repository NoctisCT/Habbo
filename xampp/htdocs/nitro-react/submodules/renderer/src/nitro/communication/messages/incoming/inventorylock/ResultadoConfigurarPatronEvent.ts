import { IMessageEvent } from '../../../../../api';
import { MessageEvent } from '../../../../../events';
import { ResultadoConfigurarPatronParser } from '../../parser/inventorylock';

export class ResultadoConfigurarPatronEvent extends MessageEvent implements IMessageEvent
{
    constructor(callBack: Function)
    {
        super(callBack, ResultadoConfigurarPatronParser);
    }

    public getParser(): ResultadoConfigurarPatronParser
    {
        return this.parser as ResultadoConfigurarPatronParser;
    }
}