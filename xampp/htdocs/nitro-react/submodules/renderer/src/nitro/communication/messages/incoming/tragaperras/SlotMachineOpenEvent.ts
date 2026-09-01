import { IMessageEvent } from '../../../../../api';
import { MessageEvent } from '../../../../../events';
import { SlotMachineOpenParser } from '../../parser';

export class SlotMachineOpenEvent extends MessageEvent implements IMessageEvent
{
    constructor(callBack: Function)
    {
        super(callBack, SlotMachineOpenParser);
    }

    public getParser(): SlotMachineOpenParser
    {
        return this.parser as SlotMachineOpenParser;
    }
}