import { IMessageEvent } from '../../../../../api';
import { MessageEvent } from '../../../../../events';
import { EstadoBloqueoInventarioParser } from '../../parser/inventorylock';

export class EstadoBloqueoInventarioEvent extends MessageEvent implements IMessageEvent
{
    constructor(callBack: Function)
    {
        super(callBack, EstadoBloqueoInventarioParser);
    }

    public getParser(): EstadoBloqueoInventarioParser
    {
        return this.parser as EstadoBloqueoInventarioParser;
    }
}