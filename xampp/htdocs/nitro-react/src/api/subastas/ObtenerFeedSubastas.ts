import { ObtenerFeedSubastasComposer } from '@nitrots/nitro-renderer';
import { SendMessageComposer } from '..';

export function ObtenerFeedSubastas(): void
{
    SendMessageComposer(new ObtenerFeedSubastasComposer());
}