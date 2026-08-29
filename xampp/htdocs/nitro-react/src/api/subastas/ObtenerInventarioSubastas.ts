import { ObtenerInventarioSubastasComposer } from '@nitrots/nitro-renderer';
import { SendMessageComposer } from '..';

export function ObtenerInventarioSubastas(): void
{
    SendMessageComposer(new ObtenerInventarioSubastasComposer());
}