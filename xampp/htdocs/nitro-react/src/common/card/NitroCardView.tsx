import { FC, useMemo, useRef } from 'react';
import { Column, ColumnProps } from '..';
import { DraggableWindow, DraggableWindowPosition, DraggableWindowProps } from '../draggable-window';
import { NitroCardContextProvider } from './NitroCardContext';

export interface NitroCardViewProps extends DraggableWindowProps, ColumnProps
{
    theme?: string;
}

export const NitroCardView: FC<NitroCardViewProps> = props =>
{
    const { theme = 'holo-classic', uniqueKey = null, handleSelector = '.drag-handler', windowPosition = DraggableWindowPosition.CENTER, disableDrag = false, overflow = 'hidden', position = 'relative', gap = 0, classNames = [], ...rest } = props;
    const elementRef = useRef<HTMLDivElement>();

    const resolvedTheme = useMemo(() =>
    {
        // Holo uses one coherent classic window language.
        // Legacy Nitro components still request primary / primary-slim,
        // so normalize both here instead of editing dozens of views.
        if(!theme || theme === 'primary' || theme === 'primary-slim') return 'holo-classic';

        return theme;
    }, [ theme ]);

    const getClassNames = useMemo(() =>
    {
        const newClassNames: string[] = [ 'nitro-card' ];

        newClassNames.push(`theme-${ resolvedTheme }`);

        if(classNames.length) newClassNames.push(...classNames);

        return newClassNames;
    }, [ resolvedTheme, classNames ]);

    return (
        <NitroCardContextProvider value={ { theme: resolvedTheme } }>
            <DraggableWindow uniqueKey={ uniqueKey } handleSelector={ handleSelector } windowPosition={ windowPosition } disableDrag={ disableDrag }>
                <Column innerRef={ elementRef } overflow={ overflow } position={ position } gap={ gap } classNames={ getClassNames } { ...rest } />
            </DraggableWindow>
        </NitroCardContextProvider>
    );
}
