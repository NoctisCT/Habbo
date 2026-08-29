import { ObtenerRecordsSubastasComposer } from '@nitrots/nitro-renderer';
import { SendMessageComposer } from '..';

export function ObtenerRecordsSubastas(): void
{
    SendMessageComposer(new ObtenerRecordsSubastasComposer());
}