import { IMessageParser } from '../../../../../api/communication/messages/IMessageParser';
import { IMessageDataWrapper } from '../../../../../api/communication/messages/IMessageDataWrapper';

export class CombatGridParser implements IMessageParser {
    private _tiles: { x: number, y: number }[] = [];

    public flush(): boolean {
        this._tiles = [];
        return true;
    }

    public parse(wrapper: IMessageDataWrapper): boolean {
        if (!wrapper) return false;

        // Leemos la cadena de texto que mandó el emulador ("X,Y;X,Y;")
        const rawData = wrapper.readString();

        if (!rawData || rawData === "") {
            this._tiles = [];
            return true;
        }

        // Troceamos el texto para sacar las coordenadas numéricas puras
        const parts = rawData.split(";");
        for (const part of parts) {
            if (!part) continue;
            const [x, y] = part.split(",").map(Number);
            this._tiles.push({ x, y });
        }

        return true;
    }

    public get tiles() {
        return this._tiles;
    }
}