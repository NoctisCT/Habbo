import { RetirarSubastaComposer } from '@nitrots/nitro-renderer';
import { SendMessageComposer } from '..';

export function RetirarSubasta(subastaId: number): void
{
    SendMessageComposer(new RetirarSubastaComposer(subastaId));
}