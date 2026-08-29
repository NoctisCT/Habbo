import { PujarSubastaComposer } from '@nitrots/nitro-renderer';
import { SendMessageComposer } from '..';

export function PujarSubasta(subastaId: number, cantidad: number): void
{
    SendMessageComposer(new PujarSubastaComposer(subastaId, cantidad));
}