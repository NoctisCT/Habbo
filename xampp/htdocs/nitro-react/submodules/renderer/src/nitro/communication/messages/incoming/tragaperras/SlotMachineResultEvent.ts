import { IMessageEvent } from '../../../../../api';
import { MessageEvent } from '../../../../../events';
import { SlotMachineResultParser } from '../../parser';

export class SlotMachineResultEvent extends MessageEvent implements IMessageEvent
{
    constructor(callBack: Function)
    {
        super(callBack, SlotMachineResultParser);
    }

    public getParser(): SlotMachineResultParser
    {
        return this.parser as SlotMachineResultParser;
    }
}