import { AbrirSubastasComposer } from '@nitrots/nitro-renderer';
import { SendMessageComposer } from '..';

export function AbrirSubastas(): void
{
    SendMessageComposer(new AbrirSubastasComposer());
}