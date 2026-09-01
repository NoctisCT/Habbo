import { IMessageEvent } from '../../../../../api';
import { MessageEvent } from '../../../../../events';
import { SlotMachineCloseParser } from '../../parser';

export class SlotMachineCloseEvent extends MessageEvent implements IMessageEvent
{
    constructor(callBack: Function)
    {
        super(callBack, SlotMachineCloseParser);
    }

    public getParser(): SlotMachineCloseParser
    {
        return this.parser as SlotMachineCloseParser;
    }
}