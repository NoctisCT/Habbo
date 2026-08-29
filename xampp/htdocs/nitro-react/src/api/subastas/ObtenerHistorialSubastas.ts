import { ObtenerHistorialSubastasComposer } from '@nitrots/nitro-renderer';
import { SendMessageComposer } from '..';

export function ObtenerHistorialSubastas(pagina: number = 1, busqueda: string = '', filtro: string = 'todos'): void
{
    SendMessageComposer(new ObtenerHistorialSubastasComposer(pagina, busqueda, filtro));
}