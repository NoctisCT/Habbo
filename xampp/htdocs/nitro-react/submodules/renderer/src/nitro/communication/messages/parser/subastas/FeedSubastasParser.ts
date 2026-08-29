import { IMessageDataWrapper, IMessageParser } from '../../../../../api';

export interface IEventoFeedSubasta
{
    id: string;
    tipo: string;
    mensaje: string;
    hora: string;
}

export class FeedSubastasParser implements IMessageParser
{
    private _eventos: IEventoFeedSubasta[] = [];

    public flush(): boolean
    {
        this._eventos = [];
        return true;
    }

    public parse(wrapper: IMessageDataWrapper): boolean
    {
        if(!wrapper) return false;

        const total = wrapper.readInt();
        const eventos: IEventoFeedSubasta[] = [];

        for(let i = 0; i < total; i++)
        {
            eventos.push({
                id: wrapper.readString(),
                tipo: wrapper.readString(),
                mensaje: wrapper.readString(),
                hora: wrapper.readString()
            });
        }

        this._eventos = eventos;

        return true;
    }

    public get eventos(): IEventoFeedSubasta[]
    {
        return this._eventos;
    }
}