import { FC, MouseEvent as ReactMouseEvent, useEffect, useRef, useState } from 'react';

interface InventoryPatternLockViewProps
{
    title: string;
    subtitle?: string;
    error?: string;
    blockedSeconds?: number;
    disabled?: boolean;
    onComplete: (pattern: string) => void;
    onCancel?: () => void;
}

const NODES = [
    { id: '1', x: 34, y: 34 },
    { id: '2', x: 100, y: 34 },
    { id: '3', x: 166, y: 34 },
    { id: '4', x: 34, y: 100 },
    { id: '5', x: 100, y: 100 },
    { id: '6', x: 166, y: 100 },
    { id: '7', x: 34, y: 166 },
    { id: '8', x: 100, y: 166 },
    { id: '9', x: 166, y: 166 }
];

const MIDPOINTS: Record<string, string> = {
    '13': '2',
    '31': '2',
    '17': '4',
    '71': '4',
    '19': '5',
    '91': '5',
    '37': '5',
    '73': '5',
    '39': '6',
    '93': '6',
    '79': '8',
    '97': '8'
};

export const InventoryPatternLockView: FC<InventoryPatternLockViewProps> = props =>
{
    const {
        title,
        subtitle = '',
        error = '',
        blockedSeconds = 0,
        disabled = false,
        onComplete,
        onCancel
    } = props;

    const [ selected, setSelected ] = useState<string[]>([]);
    const [ pointer, setPointer ] = useState<{ x: number; y: number } | null>(null);
    const [ drawing, setDrawing ] = useState(false);
    const [ localError, setLocalError ] = useState('');

    const selectedRef = useRef<string[]>([]);

    const updateSelected = (value: string[]) =>
    {
        selectedRef.current = value;
        setSelected(value);
    };

    const getCoordinates = (event: ReactMouseEvent<HTMLDivElement>) =>
    {
        const rect = event.currentTarget.getBoundingClientRect();

        return {
            x: event.clientX - rect.left,
            y: event.clientY - rect.top
        };
    };

    const getNodeAt = (x: number, y: number) =>
    {
        return NODES.find(node =>
            Math.hypot(node.x - x, node.y - y) <= 30
        );
    };

    const addNode = (nodeId: string) =>
    {
        const current = selectedRef.current;

        if(current.includes(nodeId)) return;

        const next = [ ...current ];

        if(next.length)
        {
            const previous = next[next.length - 1];
            const middle = MIDPOINTS[previous + nodeId];

            if(middle && !next.includes(middle))
            {
                next.push(middle);
            }
        }

        next.push(nodeId);
        updateSelected(next);
    };

    const onMouseDown = (event: ReactMouseEvent<HTMLDivElement>) =>
    {
        if(disabled || blockedSeconds > 0) return;

        event.preventDefault();

        const point = getCoordinates(event);
        const node = getNodeAt(point.x, point.y);

        if(!node) return;

        setLocalError('');
        updateSelected([]);
        setDrawing(true);
        setPointer(point);

        addNode(node.id);
    };

    const onMouseMove = (event: ReactMouseEvent<HTMLDivElement>) =>
    {
        if(!drawing || disabled || blockedSeconds > 0) return;

        event.preventDefault();

        const point = getCoordinates(event);
        const node = getNodeAt(point.x, point.y);

        setPointer(point);

        if(node)
        {
            addNode(node.id);
        }
    };

    const finishPattern = () =>
    {
        if(!drawing) return;

        setDrawing(false);
        setPointer(null);

        const pattern = selectedRef.current.join('');

        if(pattern.length < 4)
        {
            setLocalError('Une al menos 4 puntos.');
            updateSelected([]);
            return;
        }

        updateSelected([]);
        onComplete(pattern);
    };

    useEffect(() =>
    {
        if(!drawing) return;

        const handleMouseUp = () =>
        {
            finishPattern();
        };

        window.addEventListener('mouseup', handleMouseUp);

        return () =>
        {
            window.removeEventListener('mouseup', handleMouseUp);
        };
    }, [ drawing ]);

    const lastSelectedId = selected.length
        ? selected[selected.length - 1]
        : null;

    const lastSelectedNode = lastSelectedId
        ? NODES.find(node => node.id === lastSelectedId)
        : null;

    const shownError = error || localError;
    const isBlocked = disabled || blockedSeconds > 0;

    return (
        <div
            className="d-flex flex-column align-items-center w-100"
            style={{
                padding: onCancel ? '5px 12px 3px' : '10px 12px 12px',
                userSelect: 'none'
            }}
        >
            <div
                className="w-100"
                style={{
                    textAlign: 'center',
                    marginBottom: onCancel ? 3 : 8
                }}
            >
                <div
                    style={{
                        fontSize: 15,
                        lineHeight: '18px',
                        fontWeight: 700,
                        color: '#29323b'
                    }}
                >
                    { title }
                </div>

                { subtitle &&
                    <div
                        style={{
                            fontSize: 11,
                            lineHeight: '14px',
                            color: '#69747e',
                            marginTop: 2
                        }}
                    >
                        { subtitle }
                    </div> }
            </div>

            <div
                style={{
                    width: 218,
                    padding: 8,
                    borderRadius: 9,
                    background: '#e8ecef',
                    border: '1px solid #c6ced4',
                    boxShadow: 'inset 0 1px 0 rgba(255,255,255,0.75)'
                }}
            >
                <div
                    style={{
                        position: 'relative',
                        width: 200,
                        height: 200,
                        borderRadius: 7,
                        background: '#f8fafb',
                        border: '1px solid #d7dde1',
                        cursor: isBlocked ? 'not-allowed' : 'crosshair',
                        overflow: 'visible'
                    }}
                    onMouseDown={ onMouseDown }
                    onMouseMove={ onMouseMove }
                    onMouseUp={ finishPattern }
                    onMouseLeave={ () => setPointer(null) }
                >
                    <svg
                        viewBox="0 0 200 200"
                        style={{
                            position: 'absolute',
                            inset: 0,
                            width: '100%',
                            height: '100%',
                            pointerEvents: 'none',
                            overflow: 'visible'
                        }}
                    >
                        { selected.slice(1).map((id, index) =>
                        {
                            const previous = NODES.find(
                                node => node.id === selected[index]
                            );

                            const current = NODES.find(
                                node => node.id === id
                            );

                            if(!previous || !current) return null;

                            return (
                                <line
                                    key={ `${ previous.id }-${ current.id }` }
                                    x1={ previous.x }
                                    y1={ previous.y }
                                    x2={ current.x }
                                    y2={ current.y }
                                    stroke="#18758c"
                                    strokeWidth="4"
                                    strokeLinecap="round"
                                />
                            );
                        }) }

                        { drawing &&
                            pointer &&
                            lastSelectedNode &&
                            <line
                                x1={ lastSelectedNode.x }
                                y1={ lastSelectedNode.y }
                                x2={ pointer.x }
                                y2={ pointer.y }
                                stroke="#18758c"
                                strokeWidth="4"
                                strokeLinecap="round"
                                opacity="0.55"
                            /> }
                    </svg>

                    { NODES.map(node =>
                    {
                        const active = selected.includes(node.id);

                        return (
                            <div
                                key={ node.id }
                                style={{
                                    position: 'absolute',
                                    width: 28,
                                    height: 28,
                                    left: node.x - 14,
                                    top: node.y - 14,
                                    borderRadius: '50%',
                                    border: active
                                        ? '3px solid #18758c'
                                        : '3px solid #6d7b84',
                                    background: active
                                        ? '#18758c'
                                        : '#ffffff',
                                    boxShadow: active
                                        ? '0 0 0 5px rgba(24,117,140,0.15)'
                                        : '0 1px 2px rgba(0,0,0,0.12)',
                                    pointerEvents: 'none',
                                    boxSizing: 'border-box'
                                }}
                            >
                                <div
                                    style={{
                                        position: 'absolute',
                                        width: 6,
                                        height: 6,
                                        left: 8,
                                        top: 8,
                                        borderRadius: '50%',
                                        background: active
                                            ? '#ffffff'
                                            : '#6d7b84',
                                        opacity: active ? 0.95 : 0.65
                                    }}
                                />
                            </div>
                        );
                    }) }
                </div>
            </div>

            <div
                style={{
                    minHeight: onCancel ? 20 : 32,
                    width: '100%',
                    textAlign: 'center',
                    paddingTop: onCancel ? 2 : 6
                }}
            >
                { blockedSeconds > 0
                    ? <div
                        style={{
                            fontSize: 11,
                            fontWeight: 600,
                            color: '#b42318'
                        }}
                    >
                        Espera { blockedSeconds } s para volver a intentarlo
                    </div>
                    : shownError
                        ? <div
                            style={{
                                fontSize: 11,
                                fontWeight: 600,
                                color: '#b42318'
                            }}
                        >
                            { shownError }
                        </div>
                        : <div
                            style={{
                                fontSize: 10,
                                color: '#7a858e'
                            }}
                        >
                            Mantén pulsado y une al menos 4 puntos
                        </div> }
            </div>

            { onCancel &&
                <button
                    type="button"
                    className="btn btn-sm btn-secondary"
                    style={{
                        minWidth: 82
                    }}
                    onClick={ onCancel }
                >
                    Volver
                </button> }
        </div>
    );
};