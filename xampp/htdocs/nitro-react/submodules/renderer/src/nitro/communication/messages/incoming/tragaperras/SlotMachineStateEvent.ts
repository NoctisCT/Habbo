import { IMessageEvent } from '../../../../../api';
import { MessageEvent } from '../../../../../events';
import { SlotMachineStateParser } from '../../parser';

export class SlotMachineStateEvent extends MessageEvent implements IMessageEvent
{
    constructor(callBack: Function)
    {
        super(callBack, SlotMachineStateParser);
    }

    public getParser(): SlotMachineStateParser
    {
        return this.parser as SlotMachineStateParser;
    }
}