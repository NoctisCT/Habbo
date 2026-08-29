import { ObtenerEstadisticasSubastasComposer } from '@nitrots/nitro-renderer';
import { SendMessageComposer } from '..';

export function ObtenerEstadisticasSubastas(furniId: number = 0, busqueda: string = ''): void
{
    SendMessageComposer(new ObtenerEstadisticasSubastasComposer(furniId, busqueda));
}