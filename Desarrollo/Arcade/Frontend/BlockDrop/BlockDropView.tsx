import {
    ArcadeCloseEvent,
    ArcadeGameStartComposer,
    ArcadeGameStartedEvent,
    ArcadeLeaderboardEvent,
    ArcadeScoreSubmitComposer,
    BlockDropOpenEvent
} from '@nitrots/nitro-renderer';
import { FC, useEffect, useRef, useState } from 'react';
import {
    NitroCardContentView,
    NitroCardHeaderView,
    NitroCardView
} from '../../common';
import { SendMessageComposer } from '../../api';
import { useMessageEvent } from '../../hooks/events';
import { ArcadeLeaderboardView } from '../arcade/ArcadeLeaderboardView';
import './BlockDropView.scss';

type GamePhase = 'ready' | 'playing' | 'paused' | 'gameover';
type PieceType = 'I' | 'J' | 'L' | 'O' | 'S' | 'T' | 'Z';
type ServerResultState = 'idle' | 'accepted' | 'rejected';

interface Point
{
    x: number;
    y: number;
}

interface ActivePiece
{
    type: PieceType;
    rotation: number;
    x: number;
    y: number;
}

interface LeaderboardEntry
{
    rank: number;
    username: string;
    score: number;
    level: number;
}

interface GameModel
{
    phase: GamePhase;
    board: number[][];
    active: ActivePiece | null;
    next: PieceType;
    bag: PieceType[];
    score: number;
    level: number;
    lines: number;
    gravityMs: number;
    lockMs: number;
    feedback: string;
    feedbackTime: number;
    clearFlash: number;
}

const COLS = 10;
const ROWS = 20;
const CELL = 20;
const BOARD_X = 220;
const BOARD_Y = 18;
const WIDTH = 640;
const HEIGHT = 436;
const MAX_LEVEL = 50;
const LINES_PER_LEVEL = 10;
const GAME_KEY = 'block_drop';
const UI_MARKER = 'BIRIBIRI_BLOCK_DROP_V1';

const TYPES: PieceType[] = [
    'I',
    'J',
    'L',
    'O',
    'S',
    'T',
    'Z'
];

const PIECE_INDEX: Record<PieceType, number> = {
    I: 1,
    J: 2,
    L: 3,
    O: 4,
    S: 5,
    T: 6,
    Z: 7
};

const COLORS = [
    '#000000',
    '#43d6e8',
    '#4d72e6',
    '#e29b3c',
    '#e5d95a',
    '#69d36d',
    '#a76be3',
    '#e35d61'
];

const LIGHTS = [
    '#000000',
    '#93f4ff',
    '#92a8ff',
    '#ffd08a',
    '#fff3a0',
    '#a7f3a7',
    '#d7a6ff',
    '#ff9a9d'
];

const DARKS = [
    '#000000',
    '#1c8290',
    '#2a3d91',
    '#8b551a',
    '#8e8424',
    '#35783a',
    '#61358b',
    '#8f2d32'
];

const ROTATIONS: Record<PieceType, Point[][]> = {
    I: [
        [
            { x: 0, y: 1 },
            { x: 1, y: 1 },
            { x: 2, y: 1 },
            { x: 3, y: 1 }
        ],
        [
            { x: 2, y: 0 },
            { x: 2, y: 1 },
            { x: 2, y: 2 },
            { x: 2, y: 3 }
        ],
        [
            { x: 0, y: 2 },
            { x: 1, y: 2 },
            { x: 2, y: 2 },
            { x: 3, y: 2 }
        ],
        [
            { x: 1, y: 0 },
            { x: 1, y: 1 },
            { x: 1, y: 2 },
            { x: 1, y: 3 }
        ]
    ],
    J: [
        [
            { x: 0, y: 0 },
            { x: 0, y: 1 },
            { x: 1, y: 1 },
            { x: 2, y: 1 }
        ],
        [
            { x: 1, y: 0 },
            { x: 2, y: 0 },
            { x: 1, y: 1 },
            { x: 1, y: 2 }
        ],
        [
            { x: 0, y: 1 },
            { x: 1, y: 1 },
            { x: 2, y: 1 },
            { x: 2, y: 2 }
        ],
        [
            { x: 1, y: 0 },
            { x: 1, y: 1 },
            { x: 0, y: 2 },
            { x: 1, y: 2 }
        ]
    ],
    L: [
        [
            { x: 2, y: 0 },
            { x: 0, y: 1 },
            { x: 1, y: 1 },
            { x: 2, y: 1 }
        ],
        [
            { x: 1, y: 0 },
            { x: 1, y: 1 },
            { x: 1, y: 2 },
            { x: 2, y: 2 }
        ],
        [
            { x: 0, y: 1 },
            { x: 1, y: 1 },
            { x: 2, y: 1 },
            { x: 0, y: 2 }
        ],
        [
            { x: 0, y: 0 },
            { x: 1, y: 0 },
            { x: 1, y: 1 },
            { x: 1, y: 2 }
        ]
    ],
    O: [
        [
            { x: 1, y: 0 },
            { x: 2, y: 0 },
            { x: 1, y: 1 },
            { x: 2, y: 1 }
        ],
        [
            { x: 1, y: 0 },
            { x: 2, y: 0 },
            { x: 1, y: 1 },
            { x: 2, y: 1 }
        ],
        [
            { x: 1, y: 0 },
            { x: 2, y: 0 },
            { x: 1, y: 1 },
            { x: 2, y: 1 }
        ],
        [
            { x: 1, y: 0 },
            { x: 2, y: 0 },
            { x: 1, y: 1 },
            { x: 2, y: 1 }
        ]
    ],
    S: [
        [
            { x: 1, y: 0 },
            { x: 2, y: 0 },
            { x: 0, y: 1 },
            { x: 1, y: 1 }
        ],
        [
            { x: 1, y: 0 },
            { x: 1, y: 1 },
            { x: 2, y: 1 },
            { x: 2, y: 2 }
        ],
        [
            { x: 1, y: 1 },
            { x: 2, y: 1 },
            { x: 0, y: 2 },
            { x: 1, y: 2 }
        ],
        [
            { x: 0, y: 0 },
            { x: 0, y: 1 },
            { x: 1, y: 1 },
            { x: 1, y: 2 }
        ]
    ],
    T: [
        [
            { x: 1, y: 0 },
            { x: 0, y: 1 },
            { x: 1, y: 1 },
            { x: 2, y: 1 }
        ],
        [
            { x: 1, y: 0 },
            { x: 1, y: 1 },
            { x: 2, y: 1 },
            { x: 1, y: 2 }
        ],
        [
            { x: 0, y: 1 },
            { x: 1, y: 1 },
            { x: 2, y: 1 },
            { x: 1, y: 2 }
        ],
        [
            { x: 1, y: 0 },
            { x: 0, y: 1 },
            { x: 1, y: 1 },
            { x: 1, y: 2 }
        ]
    ],
    Z: [
        [
            { x: 0, y: 0 },
            { x: 1, y: 0 },
            { x: 1, y: 1 },
            { x: 2, y: 1 }
        ],
        [
            { x: 2, y: 0 },
            { x: 1, y: 1 },
            { x: 2, y: 1 },
            { x: 1, y: 2 }
        ],
        [
            { x: 0, y: 1 },
            { x: 1, y: 1 },
            { x: 1, y: 2 },
            { x: 2, y: 2 }
        ],
        [
            { x: 1, y: 0 },
            { x: 0, y: 1 },
            { x: 1, y: 1 },
            { x: 0, y: 2 }
        ]
    ]
};

const makeBoard = (): number[][] =>
    Array.from(
        { length: ROWS },
        () => Array.from(
            { length: COLS },
            () => 0
        )
    );

const shuffleBag = (): PieceType[] =>
{
    const bag = [ ...TYPES ];

    for(let i = bag.length - 1; i > 0; i--)
    {
        const j = Math.floor(
            Math.random() * (i + 1)
        );

        [ bag[i], bag[j] ] = [ bag[j], bag[i] ];
    }

    return bag;
};

const drawFromBag = (
    bag: PieceType[]): {
        type: PieceType;
        bag: PieceType[];
    } =>
{
    const current =
        bag.length > 0
            ? [ ...bag ]
            : shuffleBag();

    const type = current.shift();

    return {
        type: type || 'T',
        bag: current
    };
};

const initialBag = shuffleBag();
const initialFirst = drawFromBag(initialBag);
const initialNext = drawFromBag(initialFirst.bag);

const makeGame = (
    phase: GamePhase = 'ready'): GameModel => ({
    phase,
    board: makeBoard(),
    active: null,
    next: initialNext.type,
    bag: initialNext.bag,
    score: 0,
    level: 1,
    lines: 0,
    gravityMs: 0,
    lockMs: 0,
    feedback: '',
    feedbackTime: 0,
    clearFlash: 0
});

const cellsFor = (
    piece: ActivePiece): Point[] =>
    ROTATIONS[piece.type][piece.rotation % 4]
        .map(cell => ({
            x: piece.x + cell.x,
            y: piece.y + cell.y
        }));

const collides = (
    board: number[][],
    piece: ActivePiece): boolean =>
{
    for(const cell of cellsFor(piece))
    {
        if(
            cell.x < 0 ||
            cell.x >= COLS ||
            cell.y >= ROWS
        )
        {
            return true;
        }

        if(
            cell.y >= 0 &&
            board[cell.y][cell.x] !== 0
        )
        {
            return true;
        }
    }

    return false;
};

const gravityForLevel = (level: number): number =>
    Math.max(
        85,
        760 * Math.pow(
            0.86,
            Math.max(0, level - 1)
        )
    );

const linePoints = (count: number): number =>
{
    if(count === 1) return 100;
    if(count === 2) return 300;
    if(count === 3) return 500;
    if(count >= 4) return 800;

    return 0;
};

export const BlockDropView: FC<{}> = () =>
{
    const canvasRef = useRef<HTMLCanvasElement | null>(null);
    const visibleRef = useRef(false);
    const gameRef = useRef<GameModel>(makeGame());
    const itemIdRef = useRef(0);
    const runTokenRef = useRef('');
    const submittedRunRef = useRef(false);
    const startPendingRef = useRef(false);
    const lastFrameRef = useRef(0);
    const soundEnabledRef = useRef(true);
    const audioRef = useRef<AudioContext | null>(null);

    const [ isVisible, setIsVisible ] = useState(false);
    const [ itemId, setItemId ] = useState(0);
    const [ score, setScore ] = useState(0);
    const [ level, setLevel ] = useState(1);
    const [ lines, setLines ] = useState(0);
    const [ phase, setPhase ] = useState<GamePhase>('ready');
    const [ soundEnabled, setSoundEnabled ] = useState(true);
    const [ startPending, setStartPending ] = useState(false);
    const [ leaderboard, setLeaderboard ] =
        useState<LeaderboardEntry[]>([]);
    const [ serverBest, setServerBest ] = useState(0);
    const [ personalRank, setPersonalRank ] = useState(0);
    const [ totalPlayers, setTotalPlayers ] = useState(0);
    const [ recordsOpen, setRecordsOpen ] = useState(false);
    const [ resultState, setResultState ] =
        useState<ServerResultState>('idle');
    const [ resultMessage, setResultMessage ] = useState('');
    const [ newServerRecord, setNewServerRecord ] = useState(false);

    const ensureAudio = (): AudioContext | null =>
    {
        if(!soundEnabledRef.current) return null;

        try
        {
            if(!audioRef.current)
            {
                const AudioContextClass =
                    window.AudioContext ||
                    (window as any).webkitAudioContext;

                if(!AudioContextClass) return null;

                audioRef.current = new AudioContextClass();
            }

            if(audioRef.current.state === 'suspended')
            {
                void audioRef.current.resume();
            }

            return audioRef.current;
        }
        catch
        {
            return null;
        }
    };

    const tone = (
        frequency: number,
        duration: number,
        type: OscillatorType = 'square',
        volume = 0.03,
        delay = 0,
        endFrequency?: number) =>
    {
        const context = ensureAudio();
        if(!context) return;

        const start = context.currentTime + delay;
        const oscillator = context.createOscillator();
        const gain = context.createGain();
        const mixedVolume = Math.min(
            0.12,
            Math.max(0.0001, volume)
        );

        oscillator.type = type;
        oscillator.frequency.setValueAtTime(
            Math.max(1, frequency),
            start
        );

        if(endFrequency !== undefined)
        {
            oscillator.frequency.exponentialRampToValueAtTime(
                Math.max(1, endFrequency),
                start + duration
            );
        }

        gain.gain.setValueAtTime(0.0001, start);
        gain.gain.exponentialRampToValueAtTime(
            mixedVolume,
            start + 0.004
        );
        gain.gain.exponentialRampToValueAtTime(
            0.0001,
            start + duration
        );

        oscillator.connect(gain);
        gain.connect(context.destination);
        oscillator.start(start);
        oscillator.stop(start + duration + 0.02);
    };

    const playMove = () =>
    {
        tone(180, 0.025, 'square', 0.014);
    };

    const playRotate = () =>
    {
        tone(260, 0.035, 'square', 0.018, 0, 390);
    };

    const playLock = () =>
    {
        tone(105, 0.045, 'square', 0.022, 0, 72);
    };

    const playClear = (count: number) =>
    {
        if(count >= 4)
        {
            tone(330, 0.07, 'square', 0.032, 0);
            tone(495, 0.08, 'square', 0.036, 0.07);
            tone(660, 0.10, 'square', 0.04, 0.15);
            tone(990, 0.15, 'square', 0.042, 0.25);
            return;
        }

        tone(320, 0.06, 'square', 0.027, 0);
        tone(
            440 + (count * 70),
            0.09,
            'square',
            0.032,
            0.06
        );
    };

    const playLevel = () =>
    {
        tone(392, 0.07, 'square', 0.03, 0);
        tone(523, 0.07, 'square', 0.032, 0.07);
        tone(784, 0.12, 'square', 0.038, 0.14);
    };

    const playStart = () =>
    {
        tone(262, 0.055, 'square', 0.026, 0);
        tone(392, 0.055, 'square', 0.028, 0.06);
        tone(523, 0.09, 'square', 0.033, 0.12);
    };

    const playGameOver = () =>
    {
        tone(310, 0.11, 'square', 0.032, 0);
        tone(220, 0.13, 'square', 0.034, 0.11);
        tone(110, 0.22, 'sawtooth', 0.04, 0.24, 48);
    };

    const syncHud = () =>
    {
        const game = gameRef.current;

        setScore(game.score);
        setLevel(game.level);
        setLines(game.lines);
        setPhase(game.phase);
    };

    const freshGame = (
        phaseValue: GamePhase = 'ready'): GameModel =>
    {
        const firstBag = shuffleBag();
        const first = drawFromBag(firstBag);
        const next = drawFromBag(first.bag);

        const game = makeGame(phaseValue);

        game.bag = next.bag;
        game.next = next.type;
        game.active = {
            type: first.type,
            rotation: 0,
            x: 3,
            y: -1
        };

        return game;
    };

    const resetReadyGame = () =>
    {
        gameRef.current = freshGame('ready');
        runTokenRef.current = '';
        submittedRunRef.current = false;
        startPendingRef.current = false;
        lastFrameRef.current = 0;

        setStartPending(false);
        setResultState('idle');
        setResultMessage('');
        setNewServerRecord(false);
        syncHud();
    };

    const beginLocalRun = (token: string) =>
    {
        const game = freshGame('playing');

        game.feedback = 'NIVEL 1';
        game.feedbackTime = 1.15;

        gameRef.current = game;
        runTokenRef.current = token;
        submittedRunRef.current = false;
        lastFrameRef.current = performance.now();

        setResultState('idle');
        setResultMessage('');
        setNewServerRecord(false);
        syncHud();
        playStart();

        window.setTimeout(() =>
        {
            canvasRef.current?.focus();
        }, 0);
    };

    const requestStartGame = () =>
    {
        if(
            !visibleRef.current ||
            itemIdRef.current <= 0 ||
            startPendingRef.current
        )
        {
            return;
        }

        ensureAudio();

        startPendingRef.current = true;
        setStartPending(true);
        setResultState('idle');
        setResultMessage('');
        setNewServerRecord(false);

        SendMessageComposer(
            new ArcadeGameStartComposer(
                itemIdRef.current,
                GAME_KEY
            )
        );
    };

    const submitRunResult = (
        finalScore: number,
        finalLevel: number) =>
    {
        if(
            submittedRunRef.current ||
            !runTokenRef.current ||
            itemIdRef.current <= 0
        )
        {
            return;
        }

        submittedRunRef.current = true;

        SendMessageComposer(
            new ArcadeScoreSubmitComposer(
                itemIdRef.current,
                GAME_KEY,
                runTokenRef.current,
                finalScore,
                finalLevel
            )
        );
    };

    const close = () =>
    {
        visibleRef.current = false;
        runTokenRef.current = '';
        submittedRunRef.current = false;
        startPendingRef.current = false;

        setStartPending(false);
        setRecordsOpen(false);
        setIsVisible(false);

        if(gameRef.current.phase === 'playing')
        {
            gameRef.current.phase = 'paused';
        }
    };

    const togglePause = () =>
    {
        const game = gameRef.current;

        if(game.phase === 'playing')
        {
            game.phase = 'paused';
        }
        else if(game.phase === 'paused')
        {
            game.phase = 'playing';
            lastFrameRef.current = performance.now();
        }
        else
        {
            return;
        }

        setPhase(game.phase);
    };

    const toggleSound = () =>
    {
        const next = !soundEnabledRef.current;

        soundEnabledRef.current = next;
        setSoundEnabled(next);

        if(next)
        {
            ensureAudio();
            tone(520, 0.06, 'square', 0.025);
        }
    };

    const openRecords = () =>
    {
        if(gameRef.current.phase === 'playing')
        {
            gameRef.current.phase = 'paused';
            setPhase('paused');
        }

        setRecordsOpen(true);
    };

    const closeRecords = () =>
    {
        setRecordsOpen(false);

        window.setTimeout(() =>
        {
            canvasRef.current?.focus();
        }, 0);
    };

    const movePiece = (
        dx: number,
        dy: number): boolean =>
    {
        const game = gameRef.current;

        if(
            game.phase !== 'playing' ||
            !game.active
        )
        {
            return false;
        }

        const moved: ActivePiece = {
            ...game.active,
            x: game.active.x + dx,
            y: game.active.y + dy
        };

        if(collides(game.board, moved))
        {
            return false;
        }

        game.active = moved;
        game.lockMs = 0;

        return true;
    };

    const rotatePiece = (direction: number) =>
    {
        const game = gameRef.current;

        if(
            game.phase !== 'playing' ||
            !game.active
        )
        {
            return;
        }

        const nextRotation =
            (
                game.active.rotation +
                direction +
                4
            ) % 4;

        for(const kick of [ 0, -1, 1, -2, 2 ])
        {
            const rotated: ActivePiece = {
                ...game.active,
                rotation: nextRotation,
                x: game.active.x + kick
            };

            if(!collides(game.board, rotated))
            {
                game.active = rotated;
                game.lockMs = 0;
                playRotate();
                return;
            }
        }
    };

    const hardDrop = () =>
    {
        const game = gameRef.current;

        if(
            game.phase !== 'playing' ||
            !game.active
        )
        {
            return;
        }

        let moved = false;

        while(movePiece(0, 1))
        {
            moved = true;
        }

        if(moved)
        {
            tone(150, 0.025, 'square', 0.014);
        }

        game.lockMs = 1000;
    };

    useMessageEvent(
        BlockDropOpenEvent,
        (event: BlockDropOpenEvent) =>
    {
        const parser = event.getParser();

        itemIdRef.current = parser.itemId;
        setItemId(parser.itemId);

        resetReadyGame();

        setLeaderboard([]);
        setServerBest(0);
        setPersonalRank(0);
        setTotalPlayers(0);
        setRecordsOpen(false);

        visibleRef.current = true;
        setIsVisible(true);

        window.setTimeout(() =>
        {
            canvasRef.current?.focus();
        }, 0);
    });

    useMessageEvent(
        ArcadeGameStartedEvent,
        (event: ArcadeGameStartedEvent) =>
    {
        const parser = event.getParser();

        if(
            !visibleRef.current ||
            parser.gameKey !== GAME_KEY ||
            parser.itemId !== itemIdRef.current
        )
        {
            return;
        }

        startPendingRef.current = false;
        setStartPending(false);

        if(!parser.success)
        {
            setResultState('rejected');
            setResultMessage(
                parser.message ||
                'No se pudo iniciar la partida.'
            );
            return;
        }

        beginLocalRun(parser.token);
    });

    useMessageEvent(
        ArcadeLeaderboardEvent,
        (event: ArcadeLeaderboardEvent) =>
    {
        const parser = event.getParser();

        if(parser.gameKey !== GAME_KEY) return;

        const entries: LeaderboardEntry[] =
            parser.entries.map(entry => ({
                rank: entry.rank,
                username: entry.username,
                score: entry.score,
                level: entry.level
            }));

        setLeaderboard(entries);
        setPersonalRank(parser.personalRank);
        setTotalPlayers(parser.totalPlayers);
        setServerBest(parser.personalBest);

        if(parser.context === 1)
        {
            setResultState('accepted');
            setResultMessage(
                parser.message ||
                'Puntuación registrada.'
            );
            setNewServerRecord(parser.newRecord);
        }
        else if(parser.context === 2)
        {
            setResultState('rejected');
            setResultMessage(
                parser.message ||
                'La puntuación no pudo validarse.'
            );
            setNewServerRecord(false);
        }
    });

    useMessageEvent(
        ArcadeCloseEvent,
        (event: ArcadeCloseEvent) =>
    {
        const parser = event.getParser();

        if(
            parser.gameKey !== GAME_KEY ||
            parser.itemId !== itemIdRef.current
        )
        {
            return;
        }

        visibleRef.current = false;
        runTokenRef.current = '';
        submittedRunRef.current = false;
        startPendingRef.current = false;

        setStartPending(false);
        setRecordsOpen(false);
        setIsVisible(false);
    });

    useEffect(() =>
    {
        const down = (event: KeyboardEvent) =>
        {
            if(!visibleRef.current) return;

            const handled = [
                'ArrowLeft',
                'ArrowRight',
                'ArrowDown',
                'ArrowUp',
                'KeyZ',
                'KeyX',
                'Space',
                'Enter',
                'KeyP',
                'Escape'
            ].includes(event.code);

            if(handled)
            {
                event.preventDefault();
            }

            if(event.code === 'Enter')
            {
                const current =
                    gameRef.current.phase;

                if(
                    current === 'ready' ||
                    current === 'gameover'
                )
                {
                    requestStartGame();
                }

                return;
            }

            if(
                event.code === 'KeyP' ||
                event.code === 'Escape'
            )
            {
                togglePause();
                return;
            }

            if(gameRef.current.phase !== 'playing')
            {
                return;
            }

            if(event.code === 'ArrowLeft')
            {
                if(movePiece(-1, 0)) playMove();
            }
            else if(event.code === 'ArrowRight')
            {
                if(movePiece(1, 0)) playMove();
            }
            else if(event.code === 'ArrowDown')
            {
                if(movePiece(0, 1))
                {
                    tone(130, 0.018, 'square', 0.01);
                }
            }
            else if(
                event.code === 'ArrowUp' ||
                event.code === 'KeyX'
            )
            {
                rotatePiece(1);
            }
            else if(event.code === 'KeyZ')
            {
                rotatePiece(-1);
            }
            else if(event.code === 'Space')
            {
                hardDrop();
            }
        };

        const blur = () =>
        {
            if(
                visibleRef.current &&
                gameRef.current.phase === 'playing'
            )
            {
                gameRef.current.phase = 'paused';
                setPhase('paused');
            }
        };

        window.addEventListener(
            'keydown',
            down,
            { passive: false }
        );
        window.addEventListener('blur', blur);

        return () =>
        {
            window.removeEventListener(
                'keydown',
                down
            );
            window.removeEventListener(
                'blur',
                blur
            );
        };
    }, []);

    useEffect(() =>
    {
        let frameId = 0;

        const finishGame = () =>
        {
            const game = gameRef.current;

            if(game.phase === 'gameover') return;

            game.phase = 'gameover';
            game.active = null;

            setPhase('gameover');
            setScore(game.score);
            setLevel(game.level);
            setLines(game.lines);

            playGameOver();

            submitRunResult(
                game.score,
                game.level
            );
        };

        const spawnNext = () =>
        {
            const game = gameRef.current;
            const currentType = game.next;
            const nextDraw = drawFromBag(game.bag);

            game.bag = nextDraw.bag;
            game.next = nextDraw.type;
            game.active = {
                type: currentType,
                rotation: 0,
                x: 3,
                y: -1
            };
            game.gravityMs = 0;
            game.lockMs = 0;

            if(collides(game.board, game.active))
            {
                finishGame();
            }
        };

        const clearLines = (): number =>
        {
            const game = gameRef.current;
            const kept = game.board.filter(
                row => row.some(value => value === 0)
            );
            const cleared = ROWS - kept.length;

            if(cleared <= 0) return 0;

            while(kept.length < ROWS)
            {
                kept.unshift(
                    Array.from(
                        { length: COLS },
                        () => 0
                    )
                );
            }

            game.board = kept;
            game.lines += cleared;
            game.score += linePoints(cleared);
            game.clearFlash = 0.18;

            const oldLevel = game.level;

            game.level = Math.min(
                MAX_LEVEL,
                Math.floor(
                    game.lines / LINES_PER_LEVEL
                ) + 1
            );

            if(cleared >= 4)
            {
                game.feedback = '¡BLOCK DROP! +800';
            }
            else
            {
                game.feedback =
                    `${ cleared } LÍNEA${ cleared > 1 ? 'S' : '' } ` +
                    `+${ linePoints(cleared) }`;
            }

            game.feedbackTime = 0.9;
            playClear(cleared);

            if(game.level > oldLevel)
            {
                game.feedback = `NIVEL ${ game.level }`;
                game.feedbackTime = 1.15;
                playLevel();
            }

            setScore(game.score);
            setLevel(game.level);
            setLines(game.lines);

            return cleared;
        };

        const lockPiece = () =>
        {
            const game = gameRef.current;

            if(!game.active) return;

            const index = PIECE_INDEX[game.active.type];
            let aboveTop = false;

            for(const cell of cellsFor(game.active))
            {
                if(cell.y < 0)
                {
                    aboveTop = true;
                    continue;
                }

                if(
                    cell.y >= 0 &&
                    cell.y < ROWS &&
                    cell.x >= 0 &&
                    cell.x < COLS
                )
                {
                    game.board[cell.y][cell.x] = index;
                }
            }

            game.active = null;
            playLock();

            if(aboveTop)
            {
                finishGame();
                return;
            }

            clearLines();
            spawnNext();
        };

        const update = (dt: number) =>
        {
            const game = gameRef.current;

            game.feedbackTime = Math.max(
                0,
                game.feedbackTime - dt
            );

            game.clearFlash = Math.max(
                0,
                game.clearFlash - dt
            );

            if(
                game.phase !== 'playing' ||
                !game.active
            )
            {
                return;
            }

            const dtMs = dt * 1000;
            const gravity = gravityForLevel(game.level);

            game.gravityMs += dtMs;

            while(game.gravityMs >= gravity)
            {
                game.gravityMs -= gravity;

                if(!movePiece(0, 1))
                {
                    break;
                }
            }

            const below: ActivePiece = {
                ...game.active,
                y: game.active.y + 1
            };

            if(collides(game.board, below))
            {
                game.lockMs += dtMs;

                if(game.lockMs >= 330)
                {
                    lockPiece();
                }
            }
            else
            {
                game.lockMs = 0;
            }
        };

        const drawCell = (
            context: CanvasRenderingContext2D,
            x: number,
            y: number,
            index: number,
            alpha = 1) =>
        {
            const px = BOARD_X + (x * CELL);
            const py = BOARD_Y + (y * CELL);

            context.save();
            context.globalAlpha = alpha;

            context.fillStyle = '#0d1822';
            context.fillRect(
                px + 1,
                py + 1,
                CELL - 2,
                CELL - 2
            );

            context.fillStyle = COLORS[index];
            context.fillRect(
                px + 2,
                py + 2,
                CELL - 4,
                CELL - 4
            );

            context.fillStyle = LIGHTS[index];
            context.fillRect(
                px + 3,
                py + 3,
                CELL - 6,
                2
            );
            context.fillRect(
                px + 3,
                py + 3,
                2,
                CELL - 6
            );

            context.fillStyle = DARKS[index];
            context.fillRect(
                px + CELL - 5,
                py + 4,
                2,
                CELL - 7
            );
            context.fillRect(
                px + 4,
                py + CELL - 5,
                CELL - 7,
                2
            );

            context.restore();
        };

        const drawMiniPiece = (
            context: CanvasRenderingContext2D,
            type: PieceType,
            centerX: number,
            centerY: number) =>
        {
            const cells = ROTATIONS[type][0];
            const index = PIECE_INDEX[type];
            const minX = Math.min(...cells.map(cell => cell.x));
            const maxX = Math.max(...cells.map(cell => cell.x));
            const minY = Math.min(...cells.map(cell => cell.y));
            const maxY = Math.max(...cells.map(cell => cell.y));
            const mini = 17;
            const pieceW = (maxX - minX + 1) * mini;
            const pieceH = (maxY - minY + 1) * mini;
            const originX = centerX - (pieceW / 2);
            const originY = centerY - (pieceH / 2);

            for(const cell of cells)
            {
                const x =
                    originX +
                    ((cell.x - minX) * mini);
                const y =
                    originY +
                    ((cell.y - minY) * mini);

                context.fillStyle = COLORS[index];
                context.fillRect(
                    Math.round(x),
                    Math.round(y),
                    mini - 2,
                    mini - 2
                );

                context.fillStyle = LIGHTS[index];
                context.fillRect(
                    Math.round(x) + 2,
                    Math.round(y) + 2,
                    mini - 6,
                    2
                );

                context.fillStyle = DARKS[index];
                context.fillRect(
                    Math.round(x) + mini - 5,
                    Math.round(y) + 3,
                    2,
                    mini - 7
                );
            }
        };

        const draw = () =>
        {
            const canvas = canvasRef.current;
            if(!canvas) return;

            const context = canvas.getContext('2d');
            if(!context) return;

            const game = gameRef.current;

            context.setTransform(2, 0, 0, 2, 0, 0);
            context.imageSmoothingEnabled = false;

            const gradient =
                context.createLinearGradient(
                    0,
                    0,
                    0,
                    HEIGHT
                );

            gradient.addColorStop(0, '#07121b');
            gradient.addColorStop(0.55, '#091a22');
            gradient.addColorStop(1, '#050c12');

            context.fillStyle = gradient;
            context.fillRect(0, 0, WIDTH, HEIGHT);

            context.fillStyle = '#102933';
            for(let x = 0; x < WIDTH; x += 32)
            {
                context.fillRect(x, 0, 1, HEIGHT);
            }
            for(let y = 0; y < HEIGHT; y += 32)
            {
                context.fillRect(0, y, WIDTH, 1);
            }

            context.fillStyle = '#071017';
            context.fillRect(
                BOARD_X - 8,
                BOARD_Y - 8,
                (COLS * CELL) + 16,
                (ROWS * CELL) + 16
            );

            context.strokeStyle = '#3c7581';
            context.lineWidth = 2;
            context.strokeRect(
                BOARD_X - 7,
                BOARD_Y - 7,
                (COLS * CELL) + 14,
                (ROWS * CELL) + 14
            );

            context.strokeStyle = '#1d3b46';
            context.lineWidth = 1;

            for(let x = 0; x <= COLS; x++)
            {
                context.beginPath();
                context.moveTo(
                    BOARD_X + (x * CELL),
                    BOARD_Y
                );
                context.lineTo(
                    BOARD_X + (x * CELL),
                    BOARD_Y + (ROWS * CELL)
                );
                context.stroke();
            }

            for(let y = 0; y <= ROWS; y++)
            {
                context.beginPath();
                context.moveTo(
                    BOARD_X,
                    BOARD_Y + (y * CELL)
                );
                context.lineTo(
                    BOARD_X + (COLS * CELL),
                    BOARD_Y + (y * CELL)
                );
                context.stroke();
            }

            for(let y = 0; y < ROWS; y++)
            {
                for(let x = 0; x < COLS; x++)
                {
                    const value = game.board[y][x];

                    if(value > 0)
                    {
                        drawCell(
                            context,
                            x,
                            y,
                            value
                        );
                    }
                }
            }

            if(game.active)
            {
                let ghostY = game.active.y;

                while(
                    !collides(
                        game.board,
                        {
                            ...game.active,
                            y: ghostY + 1
                        }
                    )
                )
                {
                    ghostY += 1;
                }

                const ghost: ActivePiece = {
                    ...game.active,
                    y: ghostY
                };

                if(ghostY !== game.active.y)
                {
                    for(const cell of cellsFor(ghost))
                    {
                        if(cell.y >= 0)
                        {
                            drawCell(
                                context,
                                cell.x,
                                cell.y,
                                PIECE_INDEX[ghost.type],
                                0.18
                            );
                        }
                    }
                }

                for(const cell of cellsFor(game.active))
                {
                    if(cell.y >= 0)
                    {
                        drawCell(
                            context,
                            cell.x,
                            cell.y,
                            PIECE_INDEX[game.active.type]
                        );
                    }
                }
            }

            context.fillStyle = '#0b1d25';
            context.fillRect(24, 28, 158, 178);
            context.strokeStyle = '#315c67';
            context.strokeRect(24, 28, 158, 178);

            context.fillStyle = '#6dd8e5';
            context.font = 'bold 11px monospace';
            context.fillText(
                'BLOCK DROP',
                42,
                52
            );

            context.fillStyle = '#8ba8ad';
            context.font = 'bold 9px monospace';
            context.fillText(
                '10 LÍNEAS = +1 NIVEL',
                42,
                76
            );
            context.fillText(
                '1 LÍNEA      +100',
                42,
                105
            );
            context.fillText(
                '2 LÍNEAS     +300',
                42,
                125
            );
            context.fillText(
                '3 LÍNEAS     +500',
                42,
                145
            );
            context.fillText(
                '4 LÍNEAS     +800',
                42,
                165
            );

            context.fillStyle = '#e4c967';
            context.fillText(
                `CAÍDA ${ Math.round(gravityForLevel(game.level)) }ms`,
                42,
                190
            );

            context.fillStyle = '#0b1d25';
            context.fillRect(458, 28, 158, 178);
            context.strokeStyle = '#315c67';
            context.strokeRect(458, 28, 158, 178);

            context.fillStyle = '#6dd8e5';
            context.font = 'bold 10px monospace';
            context.fillText(
                'SIGUIENTE',
                500,
                52
            );

            drawMiniPiece(
                context,
                game.next,
                537,
                112
            );

            context.fillStyle = '#0b1d25';
            context.fillRect(458, 226, 158, 158);
            context.strokeStyle = '#315c67';
            context.strokeRect(458, 226, 158, 158);

            context.fillStyle = '#6dd8e5';
            context.font = 'bold 10px monospace';
            context.fillText(
                'PROGRESO',
                505,
                251
            );

            const levelLines =
                game.lines % LINES_PER_LEVEL;

            context.fillStyle = '#8ba8ad';
            context.font = 'bold 9px monospace';
            context.fillText(
                `LÍNEAS ${ game.lines }`,
                483,
                281
            );
            context.fillText(
                `NIVEL  ${ game.level }`,
                483,
                301
            );

            context.fillStyle = '#10242d';
            context.fillRect(483, 324, 108, 16);

            context.fillStyle = '#4bc9d8';
            context.fillRect(
                485,
                326,
                Math.round(
                    104 *
                    (levelLines / LINES_PER_LEVEL)
                ),
                12
            );

            context.strokeStyle = '#315c67';
            context.strokeRect(483, 324, 108, 16);

            context.fillStyle = '#7c9195';
            context.font = 'bold 8px monospace';
            context.fillText(
                `${ levelLines }/${ LINES_PER_LEVEL } PARA NIVEL`,
                483,
                359
            );

            if(game.clearFlash > 0)
            {
                context.fillStyle =
                    `rgba(145, 246, 255, ${
                        Math.min(0.22, game.clearFlash)
                    })`;

                context.fillRect(
                    BOARD_X,
                    BOARD_Y,
                    COLS * CELL,
                    ROWS * CELL
                );
            }

            if(game.feedbackTime > 0)
            {
                context.fillStyle = 'rgba(4, 12, 17, .82)';
                context.fillRect(
                    BOARD_X - 2,
                    191,
                    (COLS * CELL) + 4,
                    38
                );

                context.strokeStyle = '#5fcbd8';
                context.strokeRect(
                    BOARD_X - 1,
                    192,
                    (COLS * CELL) + 2,
                    36
                );

                context.fillStyle = '#fff1a6';
                context.font = 'bold 13px monospace';
                context.textAlign = 'center';
                context.fillText(
                    game.feedback,
                    BOARD_X + (COLS * CELL / 2),
                    215
                );
                context.textAlign = 'left';
            }

            context.fillStyle = 'rgba(109, 224, 235, .055)';
            for(let y = 2; y < HEIGHT; y += 4)
            {
                context.fillRect(0, y, WIDTH, 1);
            }
        };

        const frame = (time: number) =>
        {
            const previous = lastFrameRef.current;
            const dt =
                previous > 0
                    ? Math.min(
                        0.05,
                        Math.max(
                            0,
                            (time - previous) / 1000
                        )
                    )
                    : 0;

            lastFrameRef.current = time;

            update(dt);
            draw();

            frameId =
                window.requestAnimationFrame(
                    frame
                );
        };

        frameId =
            window.requestAnimationFrame(
                frame
            );

        return () =>
        {
            window.cancelAnimationFrame(
                frameId
            );
        };
    }, []);

    if(!isVisible) return null;

    const phaseLabel =
        phase === 'playing'
            ? 'JUGANDO'
            : phase === 'paused'
                ? 'PAUSA'
                : phase === 'gameover'
                    ? 'FIN'
                    : 'PREPARADO';

    const formattedScore =
        score.toString().padStart(5, '0');

    const formattedBest =
        serverBest.toString().padStart(5, '0');

    return (
        <>
            <NitroCardView
                uniqueKey="block-drop"
                className="nitro-block-drop"
                theme="primary-slim"
                style={ { width: '760px' } }>
                <NitroCardHeaderView
                    headerText="Block Drop"
                    onCloseClick={ close } />

                <NitroCardContentView
                    gap={ 0 }
                    className="block-drop-content">
                    <div
                        className="block-drop-shell"
                        data-engine={ UI_MARKER }
                        data-ui-parity="BIRIBIRI_BLOCK_DROP_V11_UI_PARITY"
                        data-item-id={ itemId }>
                        <span
                            className="block-cabinet-screw is-top-left"
                            aria-hidden="true" />
                        <span
                            className="block-cabinet-screw is-top-right"
                            aria-hidden="true" />
                        <span
                            className="block-cabinet-screw is-bottom-left"
                            aria-hidden="true" />
                        <span
                            className="block-cabinet-screw is-bottom-right"
                            aria-hidden="true" />

                        <div className="block-drop-hud">
                            <div className="block-hud-cell">
                                <span className="block-hud-label">
                                    PUNTUACIÓN
                                </span>
                                <strong className="block-score-value">
                                    { formattedScore }
                                </strong>
                            </div>

                            <div className="block-hud-cell">
                                <span className="block-hud-label">
                                    NIVEL
                                </span>
                                <strong className="block-level-value">
                                    { level
                                        .toString()
                                        .padStart(2, '0') }
                                </strong>
                            </div>

                            <div className="block-hud-cell">
                                <span className="block-hud-label">
                                    LÍNEAS
                                </span>
                                <strong className="block-lines-value">
                                    { lines
                                        .toString()
                                        .padStart(3, '0') }
                                </strong>
                            </div>

                            <div
                                className={
                                    `block-hud-cell is-status is-${ phase }`
                                }>
                                <span
                                    className="block-status-glyph"
                                    aria-hidden="true">
                                    ▦
                                </span>
                                <strong>{ phaseLabel }</strong>
                            </div>
                        </div>

                        <div className="block-drop-stage">
                            <canvas
                                ref={ canvasRef }
                                className="block-drop-canvas"
                                width={ WIDTH * 2 }
                                height={ HEIGHT * 2 }
                                tabIndex={ 0 }
                                aria-label="Block Drop" />

                            { phase === 'ready' &&
                                <div className="block-game-overlay">
                                    <div className="block-overlay-panel">
                                        <span className="block-overlay-kicker">
                                            PUZZLE ARCADE
                                        </span>
                                        <strong className="block-overlay-title">
                                            BLOCK DROP
                                        </strong>
                                        <span className="block-overlay-copy">
                                            Completa líneas para despejar
                                            el tablero. Cada 10 líneas
                                            aumenta el nivel y la velocidad.
                                        </span>

                                        <button
                                            type="button"
                                            className="block-primary-button"
                                            disabled={ startPending }
                                            onClick={ requestStartGame }>
                                            { startPending
                                                ? 'PREPARANDO...'
                                                : 'JUGAR' }
                                        </button>

                                        <span className="block-overlay-hint">
                                            ENTER también inicia la partida
                                        </span>

                                        { resultState === 'rejected' &&
                                            <span className="block-result-message is-error">
                                                { resultMessage }
                                            </span> }
                                    </div>
                                </div> }

                            { phase === 'paused' &&
                                <div className="block-game-overlay is-pause">
                                    <div className="block-overlay-panel is-compact">
                                        <span className="block-overlay-kicker">
                                            PARTIDA DETENIDA
                                        </span>
                                        <strong className="block-overlay-title">
                                            PAUSA
                                        </strong>

                                        <button
                                            type="button"
                                            className="block-primary-button"
                                            onClick={ togglePause }>
                                            CONTINUAR
                                        </button>

                                        <span className="block-overlay-hint">
                                            P o ESC para continuar
                                        </span>
                                    </div>
                                </div> }

                            { phase === 'gameover' &&
                                <div className="block-game-overlay is-gameover">
                                    <div className="block-overlay-panel">
                                        <span className="block-overlay-kicker">
                                            TORRE BLOQUEADA
                                        </span>
                                        <strong className="block-overlay-title">
                                            FIN DE LA PARTIDA
                                        </strong>

                                        <div className="block-gameover-results">
                                            <span>
                                                PUNTUACIÓN
                                                <b>{ formattedScore }</b>
                                            </span>

                                            <span>
                                                NIVEL
                                                <b>{ level }</b>
                                            </span>

                                            <span>
                                                LÍNEAS
                                                <b>{ lines }</b>
                                            </span>
                                        </div>

                                        { newServerRecord &&
                                            <span className="block-new-record">
                                                ★ NUEVO RÉCORD PERSONAL
                                            </span> }

                                        { resultState !== 'idle' &&
                                            <span
                                                className={
                                                    `block-result-message ${
                                                        resultState === 'accepted'
                                                            ? 'is-success'
                                                            : 'is-error'
                                                    }`
                                                }>
                                                { resultMessage }
                                            </span> }

                                        <button
                                            type="button"
                                            className="block-primary-button"
                                            disabled={ startPending }
                                            onClick={ requestStartGame }>
                                            { startPending
                                                ? 'PREPARANDO...'
                                                : 'JUGAR DE NUEVO' }
                                        </button>
                                    </div>
                                </div> }
                        </div>

                        <div className="block-drop-console">
                            <div className="block-controls-panel">
                                <div className="block-control-group">
                                    <span className="block-keycap">
                                        ← →
                                    </span>
                                    <span className="block-control-action">
                                        MOVER
                                    </span>
                                </div>

                                <div className="block-console-divider" />

                                <div className="block-control-group">
                                    <span className="block-keycap">
                                        ↓
                                    </span>
                                    <span className="block-control-action">
                                        BAJAR
                                    </span>
                                </div>

                                <div className="block-console-divider" />

                                <div className="block-control-group">
                                    <span className="block-keycap">
                                        ↑
                                    </span>
                                    <span className="block-control-action">
                                        GIRAR
                                    </span>
                                </div>

                                <div className="block-console-divider" />

                                <div className="block-control-group">
                                    <span className="block-keycap is-wide">
                                        ESPACIO
                                    </span>
                                    <span className="block-control-action">
                                        CAÍDA
                                    </span>
                                </div>

                                <div className="block-console-divider" />

                                <div className="block-control-group">
                                    <span className="block-keycap">
                                        P
                                    </span>
                                    <span className="block-control-action">
                                        PAUSA
                                    </span>
                                </div>
                            </div>

                            <div className="block-console-actions">
                                <button
                                    type="button"
                                    className={
                                        `block-sound-button${
                                            soundEnabled
                                                ? ' is-on'
                                                : ''
                                        }`
                                    }
                                    onClick={ toggleSound }>
                                    <span className="block-sound-icon">
                                        ♪
                                    </span>
                                    SONIDO
                                    <b>
                                        { soundEnabled
                                            ? 'ON'
                                            : 'OFF' }
                                    </b>
                                </button>

                                <button
                                    type="button"
                                    className="block-restart-button"
                                    disabled={ startPending }
                                    onClick={ requestStartGame }>
                                    <span
                                        className="block-restart-shine"
                                        aria-hidden="true" />
                                    REINICIAR
                                </button>
                            </div>
                        </div>

                        <div className="block-arcade-summary-bar">
                            <div className="block-arcade-summary-brand">
                                <span>RANKING GLOBAL</span>
                                <strong>BLOCK DROP</strong>
                            </div>

                            <div className="block-arcade-summary-stats">
                                <span>
                                    TU RÉCORD
                                    <b>{ formattedBest }</b>
                                </span>

                                <span>
                                    TU PUESTO
                                    <b>
                                        { personalRank > 0
                                            ? `#${ personalRank }`
                                            : '—' }
                                    </b>
                                </span>

                                <span>
                                    JUGADORES
                                    <b>{ totalPlayers }</b>
                                </span>
                            </div>

                            <button
                                type="button"
                                className="block-records-button"
                                onClick={ openRecords }>
                                <span className="block-records-star">
                                    ★
                                </span>
                                RÉCORDS
                            </button>
                        </div>
                    </div>
                </NitroCardContentView>
            </NitroCardView>

            <ArcadeLeaderboardView
                visible={ recordsOpen }
                gameName="Block Drop"
                levelLabel="NIVEL"
                leaderboard={ leaderboard }
                personalBest={ serverBest }
                personalRank={ personalRank }
                totalPlayers={ totalPlayers }
                onClose={ closeRecords } />
        </>
    );
};
