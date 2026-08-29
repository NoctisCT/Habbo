import { ObtenerProximaSubastaComposer } from '@nitrots/nitro-renderer';
import { SendMessageComposer } from '..';

export function ObtenerProximaSubasta(): void
{
    SendMessageComposer(new ObtenerProximaSubastaComposer());
}