export * from './useCommunicationEvent';
export * from './useConfigurationEvent';



import { useCommunicationEvent } from './useCommunicationEvent';
import { useMemo } from 'react';
(window as any).useMessageEvent = (EventClass: any, callBack: Function) => {
    useCommunicationEvent(useMemo(() => new EventClass(callBack), [EventClass, callBack]));
};
