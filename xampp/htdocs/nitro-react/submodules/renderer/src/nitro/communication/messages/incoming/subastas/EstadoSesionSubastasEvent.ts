import { IMessageEvent } from '../../../../../api';
import { MessageEvent } from '../../../../../events';
import { EstadoSesionSubastasParser } from '../../parser';

export class EstadoSesionSubastasEvent extends MessageEvent implements IMessageEvent
{
    constructor(callBack: Function)
    {
        super(callBack, EstadoSesionSubastasParser);
    }

    public getParser(): EstadoSesionSubastasParser
    {
        return this.parser as EstadoSesionSubastasParser;
    }
}