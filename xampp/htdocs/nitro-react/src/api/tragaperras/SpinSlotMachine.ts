import { SpinSlotMachineComposer } from '@nitrots/nitro-renderer';
import { SendMessageComposer } from '..';

export function SpinSlotMachine(itemId: number): void
{
    if(itemId <= 0) return;

    SendMessageComposer(new SpinSlotMachineComposer(itemId));
}