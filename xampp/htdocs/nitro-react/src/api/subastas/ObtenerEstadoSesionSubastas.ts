import { ObtenerEstadoSesionSubastasComposer } from '@nitrots/nitro-renderer';
import { SendMessageComposer } from '..';

export function ObtenerEstadoSesionSubastas(): void
{
    SendMessageComposer(new ObtenerEstadoSesionSubastasComposer());
}