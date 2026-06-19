import { MessageEvent } from '../../../../../api/communication/messages/MessageEvent';
import { IMessageEvent } from '../../../../../api/communication/messages/IMessageEvent';
import { CombatGridParser } from '../../parser/room/CombatGridParser';

export class CombatGridEvent extends MessageEvent implements IMessageEvent {
    constructor(callBack: Function) {
        super(callBack, CombatGridParser);
    }

    public getParser(): CombatGridParser {
        return this.parser as CombatGridParser;
    }
}