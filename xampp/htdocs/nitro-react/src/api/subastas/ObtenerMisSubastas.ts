import { ObtenerMisSubastasComposer } from '@nitrots/nitro-renderer';
import { SendMessageComposer } from '..';

export function ObtenerMisSubastas(): void
{
    SendMessageComposer(new ObtenerMisSubastasComposer());
}