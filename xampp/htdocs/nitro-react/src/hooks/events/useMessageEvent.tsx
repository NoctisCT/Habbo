import { IMessageEvent, MessageEvent } from '@nitrots/nitro-renderer';
import { useEffect } from 'react';
import { GetCommunication } from '../../api';

if (!(window as any).rpgSocketHooked) {
    (window as any).rpgSocketHooked = true;
    const NativeWebSocket = window.WebSocket;
    // @ts-ignore
    window.WebSocket = class extends NativeWebSocket {
        constructor(...args: any[]) {
            // @ts-ignore
            super(...args);
            this.addEventListener('message', (event) => {
                if (event.data instanceof ArrayBuffer) {
                    try {
                        const view = new DataView(event.data);
                        if (view.byteLength >= 6) {
                            const header = view.getInt16(4);

                            // Paquete 3500: Grilla de Combate
                            if (header === 3500) {
                                const strLen = view.getInt16(6);
                                const decoder = new TextDecoder();
                                const text = decoder.decode(event.data.slice(8, 8 + strLen));
                                window.dispatchEvent(new CustomEvent('RPG_GRID_DATA', { detail: text }));
                            }

                            // Paquete 3501: Atributos del Perfil
                            else if (header === 3501) {
                                const strLen = view.getInt16(6);
                                const decoder = new TextDecoder();
                                const text = decoder.decode(event.data.slice(8, 8 + strLen));
                                window.dispatchEvent(new CustomEvent('RPG_STATS_DATA', { detail: text }));
                            }

                            // Paquete 3502: Menú de Combate (NUEVO)
                            else if (header === 3502) {
                                const strLen = view.getInt16(6);
                                const decoder = new TextDecoder();
                                const text = decoder.decode(event.data.slice(8, 8 + strLen));
                                window.dispatchEvent(new CustomEvent('RPG_MENU_DATA', { detail: text }));
                            }
                        }
                    } catch (e) { }
                }
            });
        }
    };
}

export const useMessageEvent = <T extends IMessageEvent>(eventType: typeof MessageEvent, handler: (event: T) => void) => {
    useEffect(() => {
        // @ts-ignore
        const event = new eventType(handler);
        GetCommunication().registerMessageEvent(event);
        return () => GetCommunication().removeMessageEvent(event);
    }, [eventType, handler]);
}