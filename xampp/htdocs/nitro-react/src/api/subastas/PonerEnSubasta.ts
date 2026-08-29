import { PonerEnSubastaComposer } from '@nitrots/nitro-renderer';
import { SendMessageComposer } from '..';

export function PonerEnSubasta(instanciaIds: number[], precioInicial: number): void
{
    SendMessageComposer(new PonerEnSubastaComposer(instanciaIds, precioInicial));
}