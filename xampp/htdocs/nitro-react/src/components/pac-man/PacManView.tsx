import {
    ArcadeCloseEvent,
    ArcadeGameStartComposer,
    ArcadeGameStartedEvent,
    ArcadeLeaderboardEvent,
    ArcadeScoreSubmitComposer,
    PacManOpenEvent
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
import './PacManView.scss';

type GamePhase = 'ready' | 'playing' | 'paused' | 'gameover';
type Direction = 'left' | 'right' | 'up' | 'down' | 'none';
type ServerResultState = 'idle' | 'accepted' | 'rejected';

interface LeaderboardEntry
{
    rank: number;
    username: string;
    score: number;
    level: number;
}

interface Actor
{
    x: number;
    y: number;
    prevX: number;
    prevY: number;
    dir: Direction;
}

interface Ghost extends Actor
{
    kind: number;
    spawnX: number;
    spawnY: number;
    respawnTime: number;
}

interface Player extends Actor
{
    wanted: Direction;
}

interface GameModel
{
    phase: GamePhase;
    maze: string[][];
    player: Player;
    ghosts: Ghost[];
    score: number;
    level: number;
    lives: number;
    pelletsLeft: number;
    totalPellets: number;
    frightened: number;
    ghostChain: number;
    playerAccum: number;
    ghostAccum: number;
    feedback: string;
    feedbackTime: number;
    fruitActive: boolean;
    fruitShown: boolean;
    fruitTimer: number;
    respawnTimer: number;
    elapsed: number;
}

const GAME_KEY = 'pac_man';
const UI_MARKER = 'BIRIBIRI_PAC_MAN_V12_4_HUD_ICON_FIX';

const TILE = 24;
const COLS = 19;
const ROWS = 21;
const BOARD_W = COLS * TILE;
const BOARD_H = ROWS * TILE;
const TUNNEL_ROW = 10;
const MAX_LEVEL = 100;

const MAZE_TEMPLATE = [
    '###################',
    '#o.......#.......o#',
    '#.###.##.#.##.###.#',
    '#.................#',
    '#.###.#.###.#.###.#',
    '#.....#..#..#.....#',
    '#####.##.#.##.#####',
    '#.....#.....#.....#',
    '#.###.#.###.#.###.#',
    '#.....#..#..#.....#',
    '.....##.....##.....',
    '#.....#.###.#.....#',
    '#.###.#.....#.###.#',
    '#.....###.###.....#',
    '###.#.........#.###',
    '#...#.###.###.#...#',
    '#.###.....#.....###',
    '#.................#',
    '#.###.##.#.##.###.#',
    '#o....#.....#....o#',
    '###################'
];

const DIR_VECTOR: Record<Direction, { x: number; y: number }> = {
    left: { x: -1, y: 0 },
    right: { x: 1, y: 0 },
    up: { x: 0, y: -1 },
    down: { x: 0, y: 1 },
    none: { x: 0, y: 0 }
};

const OPPOSITE: Record<Direction, Direction> = {
    left: 'right',
    right: 'left',
    up: 'down',
    down: 'up',
    none: 'none'
};

const MOVE_DIRS: Direction[] = [
    'left',
    'right',
    'up',
    'down'
];

const GHOST_COLORS = [
    '#ef4055',
    '#ff8fcf',
    '#50d7e8',
    '#f6a53d'
];

const makeMaze = (): string[][] =>
{
    const maze = MAZE_TEMPLATE.map(row => row.split(''));

    const clear = [
        [ 9, 17 ],
        [ 7, 10 ],
        [ 8, 10 ],
        [ 9, 10 ],
        [ 10, 10 ],
        [ 11, 10 ]
    ];

    for(const [ x, y ] of clear)
    {
        if(maze[y] && maze[y][x] !== '#')
        {
            maze[y][x] = ' ';
        }
    }

    return maze;
};

const countPellets = (maze: string[][]): number =>
    maze.reduce(
        (total, row) =>
            total +
            row.filter(cell => cell === '.' || cell === 'o').length,
        0
    );

const makePlayer = (): Player => ({
    x: 9,
    y: 17,
    prevX: 9,
    prevY: 17,
    dir: 'right',
    wanted: 'right'
});

const makeGhosts = (): Ghost[] => [
    {
        x: 8,
        y: 10,
        prevX: 8,
        prevY: 10,
        dir: 'left',
        kind: 0,
        spawnX: 8,
        spawnY: 10,
        respawnTime: 0
    },
    {
        x: 9,
        y: 10,
        prevX: 9,
        prevY: 10,
        dir: 'up',
        kind: 1,
        spawnX: 9,
        spawnY: 10,
        respawnTime: 0.7
    },
    {
        x: 10,
        y: 10,
        prevX: 10,
        prevY: 10,
        dir: 'right',
        kind: 2,
        spawnX: 10,
        spawnY: 10,
        respawnTime: 1.4
    },
    {
        x: 11,
        y: 10,
        prevX: 11,
        prevY: 10,
        dir: 'down',
        kind: 3,
        spawnX: 11,
        spawnY: 10,
        respawnTime: 2.1
    }
];

const freshGame = (
    phase: GamePhase = 'ready',
    level = 1,
    score = 0,
    lives = 3): GameModel =>
{
    const maze = makeMaze();
    const totalPellets = countPellets(maze);

    return {
        phase,
        maze,
        player: makePlayer(),
        ghosts: makeGhosts(),
        score,
        level,
        lives,
        pelletsLeft: totalPellets,
        totalPellets,
        frightened: 0,
        ghostChain: 0,
        playerAccum: 0,
        ghostAccum: 0,
        feedback: '',
        feedbackTime: 0,
        fruitActive: false,
        fruitShown: false,
        fruitTimer: 0,
        respawnTimer: 0,
        elapsed: 0
    };
};

const playerStepMs = (level: number): number =>
    Math.max(
        116,
        186 - ((level - 1) * 4.8)
    );

const ghostStepMs = (
    level: number,
    frightened: boolean): number =>
{
    const normal =
        Math.max(
            108,
            202 - ((level - 1) * 5.4)
        );

    return frightened
        ? normal * (
            1.34 -
            Math.min(0.18, Math.max(0, level - 1) * 0.01)
        )
        : normal;
};

const frightenedDuration = (level: number): number =>
    Math.max(
        3.8,
        6.8 - ((level - 1) * 0.18)
    );


const PacHudIcon: FC<{
    active?: boolean;
    className?: string;
}> = ({
    active = true,
    className = ''
}) =>
(
    <svg
        className={
            `pac-hud-pac${ active ? ' is-on' : '' }${
                className ? ` ${ className }` : ''
            }`
        }
        viewBox="0 0 32 32"
        aria-hidden="true"
        focusable="false">
        <path
            className="pac-hud-pac-body"
            d="M16 16 L29 11 A14 14 0 1 0 29 21 Z" />
        <circle
            className="pac-hud-pac-eye"
            cx="18"
            cy="8.5"
            r="1.6" />
    </svg>
);

const normalizedX = (x: number): number =>
{
    if(x < 0) return COLS - 1;
    if(x >= COLS) return 0;

    return x;
};

const canMove = (
    maze: string[][],
    x: number,
    y: number,
    direction: Direction): boolean =>
{
    if(direction === 'none') return false;

    const vector = DIR_VECTOR[direction];
    let nx = x + vector.x;
    const ny = y + vector.y;

    if(ny < 0 || ny >= ROWS) return false;

    if(nx < 0 || nx >= COLS)
    {
        if(y !== TUNNEL_ROW) return false;

        nx = normalizedX(nx);
    }

    return maze[ny][nx] !== '#';
};

const moveCell = (
    x: number,
    y: number,
    direction: Direction): {
        x: number;
        y: number;
    } =>
{
    const vector = DIR_VECTOR[direction];

    return {
        x: normalizedX(x + vector.x),
        y: y + vector.y
    };
};

const manhattan = (
    ax: number,
    ay: number,
    bx: number,
    by: number): number =>
    Math.abs(ax - bx) + Math.abs(ay - by);

const clampTarget = (
    x: number,
    y: number): {
        x: number;
        y: number;
    } => ({
    x: Math.max(0, Math.min(COLS - 1, x)),
    y: Math.max(0, Math.min(ROWS - 1, y))
});

export const PacManView: FC<{}> = () =>
{
    const canvasRef = useRef<HTMLCanvasElement | null>(null);
    const visibleRef = useRef(false);
    const gameRef = useRef<GameModel>(freshGame());
    const itemIdRef = useRef(0);
    const runTokenRef = useRef('');
    const submittedRunRef = useRef(false);
    const startPendingRef = useRef(false);
    const lastFrameRef = useRef(0);
    const soundEnabledRef = useRef(true);
    const audioRef = useRef<AudioContext | null>(null);
    const powerBeatRef = useRef(-1);

    const [ isVisible, setIsVisible ] = useState(false);
    const [ itemId, setItemId ] = useState(0);
    const [ score, setScore ] = useState(0);
    const [ level, setLevel ] = useState(1);
    const [ lives, setLives ] = useState(3);
    const [ pelletsLeft, setPelletsLeft ] = useState(0);
    const [ totalPellets, setTotalPellets ] = useState(1);
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
        volume = 0.025,
        delay = 0,
        endFrequency?: number) =>
    {
        const context = ensureAudio();
        if(!context) return;

        const start = context.currentTime + delay;
        const oscillator = context.createOscillator();
        const gain = context.createGain();

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
            Math.min(0.12, Math.max(0.0001, volume)),
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

    const playStart = () =>
    {
        tone(330, 0.07, 'square', 0.025);
        tone(440, 0.07, 'square', 0.028, 0.08);
        tone(660, 0.11, 'square', 0.032, 0.16);
    };

    const playPellet = () =>
    {
        tone(250, 0.018, 'square', 0.009, 0, 310);
    };

    const playPower = () =>
    {
        tone(210, 0.08, 'square', 0.03, 0, 520);
        tone(620, 0.08, 'square', 0.025, 0.07, 300);
    };

    const playPowerBeat = (beat: number) =>
    {
        const bass = [
            174,
            196,
            220,
            196
        ][beat % 4];

        tone(
            bass,
            0.085,
            'square',
            0.010,
            0,
            bass * 1.12
        );

        if(beat % 2 === 0)
        {
            tone(
                bass * 2,
                0.045,
                'square',
                0.006,
                0.045,
                bass * 1.75
            );
        }
    };

    const playGhost = (chain: number) =>
    {
        const base = 420 + (chain * 120);

        tone(base, 0.07, 'square', 0.035, 0, base * 1.45);
    };

    const playFruit = () =>
    {
        tone(520, 0.05, 'square', 0.026);
        tone(780, 0.09, 'square', 0.03, 0.05);
    };

    const playDeath = () =>
    {
        tone(520, 0.11, 'square', 0.035, 0, 210);
        tone(240, 0.18, 'sawtooth', 0.028, 0.11, 70);
    };

    const playLevel = () =>
    {
        tone(430, 0.07, 'square', 0.028);
        tone(570, 0.07, 'square', 0.03, 0.08);
        tone(760, 0.12, 'square', 0.035, 0.16);
    };

    const playGameOver = () =>
    {
        tone(330, 0.10, 'square', 0.03, 0, 190);
        tone(180, 0.22, 'sawtooth', 0.03, 0.10, 65);
    };

    const syncHud = () =>
    {
        const game = gameRef.current;

        setScore(game.score);
        setLevel(game.level);
        setLives(game.lives);
        setPelletsLeft(game.pelletsLeft);
        setTotalPellets(game.totalPellets);
        setPhase(game.phase);
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
        game.feedbackTime = 1.1;

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

    useMessageEvent(
        PacManOpenEvent,
        (event: PacManOpenEvent) =>
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
                'KeyA',
                'KeyD',
                'KeyS',
                'KeyW',
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
                const current = gameRef.current.phase;

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

            if(
                event.code === 'ArrowLeft' ||
                event.code === 'KeyA'
            )
            {
                gameRef.current.player.wanted = 'left';
            }
            else if(
                event.code === 'ArrowRight' ||
                event.code === 'KeyD'
            )
            {
                gameRef.current.player.wanted = 'right';
            }
            else if(
                event.code === 'ArrowUp' ||
                event.code === 'KeyW'
            )
            {
                gameRef.current.player.wanted = 'up';
            }
            else if(
                event.code === 'ArrowDown' ||
                event.code === 'KeyS'
            )
            {
                gameRef.current.player.wanted = 'down';
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
            window.removeEventListener('keydown', down);
            window.removeEventListener('blur', blur);
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
            game.feedback = 'FIN DE LA PARTIDA';
            game.feedbackTime = 1.5;

            setPhase('gameover');
            syncHud();
            playGameOver();

            submitRunResult(
                game.score,
                game.level
            );
        };

        const resetActors = (
            game: GameModel,
            pause = 1.15) =>
        {
            game.player = makePlayer();
            game.ghosts = makeGhosts();
            game.playerAccum = 0;
            game.ghostAccum = 0;
            game.frightened = 0;
            game.ghostChain = 0;
            game.respawnTimer = pause;
        };

        const completeLevel = (game: GameModel) =>
        {
            game.score += 1000;
            game.level = Math.min(
                MAX_LEVEL,
                game.level + 1
            );

            game.maze = makeMaze();
            game.totalPellets = countPellets(game.maze);
            game.pelletsLeft = game.totalPellets;
            game.player = makePlayer();
            game.ghosts = makeGhosts();
            game.frightened = 0;
            game.ghostChain = 0;
            game.playerAccum = 0;
            game.ghostAccum = 0;
            game.fruitActive = false;
            game.fruitShown = false;
            game.fruitTimer = 0;
            game.respawnTimer = 0.9;
            game.feedback =
                `NIVEL ${ game.level } · +1000`;
            game.feedbackTime = 1.3;

            syncHud();
            playLevel();
        };

        const consumeCell = (game: GameModel) =>
        {
            const { x, y } = game.player;
            const cell = game.maze[y]?.[x];

            if(cell === '.')
            {
                game.maze[y][x] = ' ';
                game.score += 10;
                game.pelletsLeft -= 1;
                playPellet();
            }
            else if(cell === 'o')
            {
                game.maze[y][x] = ' ';
                game.score += 50;
                game.pelletsLeft -= 1;
                game.frightened = frightenedDuration(game.level);
                game.ghostChain = 0;
                game.feedback = '¡PODER!';
                game.feedbackTime = 0.65;
                playPower();
            }

            if(
                !game.fruitShown &&
                game.pelletsLeft <=
                    Math.floor(game.totalPellets * 0.52)
            )
            {
                game.fruitShown = true;
                game.fruitActive = true;
                game.fruitTimer = 8.0;
            }

            if(
                game.fruitActive &&
                game.player.x === 9 &&
                game.player.y === 13
            )
            {
                const fruitScore =
                    500 +
                    (
                        Math.min(
                            5,
                            Math.max(0, game.level - 1)
                        ) * 100
                    );

                game.fruitActive = false;
                game.fruitTimer = 0;
                game.score += fruitScore;
                game.feedback = `FRUTA +${ fruitScore }`;
                game.feedbackTime = 0.9;
                playFruit();
            }

            setScore(game.score);
            setPelletsLeft(game.pelletsLeft);

            if(game.pelletsLeft <= 0)
            {
                completeLevel(game);
            }
        };

        const playerAheadTarget = (
            game: GameModel,
            distance: number) =>
        {
            const vector = DIR_VECTOR[game.player.dir];

            return clampTarget(
                game.player.x + (vector.x * distance),
                game.player.y + (vector.y * distance)
            );
        };

        const ghostTarget = (
            game: GameModel,
            ghost: Ghost) =>
        {
            if(ghost.kind === 0)
            {
                return {
                    x: game.player.x,
                    y: game.player.y
                };
            }

            if(ghost.kind === 1)
            {
                return playerAheadTarget(game, 4);
            }

            if(ghost.kind === 2)
            {
                const ahead = playerAheadTarget(game, 2);

                return clampTarget(
                    ahead.x + (game.player.x - 9),
                    ahead.y + (game.player.y - 10)
                );
            }

            if(
                manhattan(
                    ghost.x,
                    ghost.y,
                    game.player.x,
                    game.player.y
                ) > 6
            )
            {
                return {
                    x: game.player.x,
                    y: game.player.y
                };
            }

            return {
                x: 1,
                y: ROWS - 2
            };
        };

        const chooseGhostDirection = (
            game: GameModel,
            ghost: Ghost): Direction =>
        {
            let candidates = MOVE_DIRS.filter(direction =>
                canMove(
                    game.maze,
                    ghost.x,
                    ghost.y,
                    direction
                )
            );

            const reverse = OPPOSITE[ghost.dir];

            if(candidates.length > 1)
            {
                const withoutReverse =
                    candidates.filter(
                        direction => direction !== reverse
                    );

                if(withoutReverse.length > 0)
                {
                    candidates = withoutReverse;
                }
            }

            if(candidates.length === 0)
            {
                return reverse;
            }

            if(game.frightened > 0)
            {
                return candidates[
                    Math.floor(
                        Math.random() * candidates.length
                    )
                ];
            }

            const target = ghostTarget(game, ghost);

            let best = candidates[0];
            let bestScore = Number.POSITIVE_INFINITY;

            for(const direction of candidates)
            {
                const next = moveCell(
                    ghost.x,
                    ghost.y,
                    direction
                );

                let score =
                    manhattan(
                        next.x,
                        next.y,
                        target.x,
                        target.y
                    );

                if(direction !== ghost.dir)
                {
                    score += 0.18;
                }

                score += Math.random() * 0.08;

                if(score < bestScore)
                {
                    bestScore = score;
                    best = direction;
                }
            }

            return best;
        };

        const sameOrCrossed = (
            player: Player,
            ghost: Ghost): boolean =>
        {
            if(
                player.x === ghost.x &&
                player.y === ghost.y
            )
            {
                return true;
            }

            return (
                player.prevX === ghost.x &&
                player.prevY === ghost.y &&
                ghost.prevX === player.x &&
                ghost.prevY === player.y
            );
        };

        const checkCollisions = (game: GameModel) =>
        {
            if(
                game.phase !== 'playing' ||
                game.respawnTimer > 0
            )
            {
                return;
            }

            for(const ghost of game.ghosts)
            {
                if(
                    ghost.respawnTime > 0 ||
                    !sameOrCrossed(game.player, ghost)
                )
                {
                    continue;
                }

                if(game.frightened > 0)
                {
                    const chain =
                        Math.min(3, game.ghostChain);

                    const points =
                        200 * Math.pow(2, chain);

                    game.score += points;
                    game.ghostChain += 1;
                    ghost.x = ghost.spawnX;
                    ghost.y = ghost.spawnY;
                    ghost.prevX = ghost.spawnX;
                    ghost.prevY = ghost.spawnY;
                    ghost.dir = 'up';
                    ghost.respawnTime = 1.7;
                    game.feedback = `FANTASMA +${ points }`;
                    game.feedbackTime = 0.8;

                    setScore(game.score);
                    playGhost(chain);
                    continue;
                }

                game.lives -= 1;
                setLives(game.lives);
                playDeath();

                if(game.lives <= 0)
                {
                    finishGame();
                    return;
                }

                game.feedback = 'VIDA PERDIDA';
                game.feedbackTime = 1.0;
                resetActors(game, 1.25);
                return;
            }
        };

        const stepPlayer = (game: GameModel) =>
        {
            const player = game.player;

            if(
                canMove(
                    game.maze,
                    player.x,
                    player.y,
                    player.wanted
                )
            )
            {
                player.dir = player.wanted;
            }

            player.prevX = player.x;
            player.prevY = player.y;

            if(
                canMove(
                    game.maze,
                    player.x,
                    player.y,
                    player.dir
                )
            )
            {
                const next = moveCell(
                    player.x,
                    player.y,
                    player.dir
                );

                player.x = next.x;
                player.y = next.y;

                consumeCell(game);
                checkCollisions(game);
            }
        };

        const stepGhosts = (game: GameModel) =>
        {
            for(const ghost of game.ghosts)
            {
                if(ghost.respawnTime > 0)
                {
                    continue;
                }

                ghost.prevX = ghost.x;
                ghost.prevY = ghost.y;
                ghost.dir =
                    chooseGhostDirection(game, ghost);

                if(
                    canMove(
                        game.maze,
                        ghost.x,
                        ghost.y,
                        ghost.dir
                    )
                )
                {
                    const next = moveCell(
                        ghost.x,
                        ghost.y,
                        ghost.dir
                    );

                    ghost.x = next.x;
                    ghost.y = next.y;
                }
            }

            checkCollisions(game);
        };

        const drawWall = (
            context: CanvasRenderingContext2D,
            x: number,
            y: number) =>
        {
            const px = x * TILE;
            const py = y * TILE;

            context.fillStyle = '#0a1547';
            context.fillRect(
                px + 1,
                py + 1,
                TILE - 2,
                TILE - 2
            );

            context.strokeStyle = '#2758ee';
            context.lineWidth = 2;
            context.strokeRect(
                px + 4,
                py + 4,
                TILE - 8,
                TILE - 8
            );
        };

        const drawPlayer = (
            context: CanvasRenderingContext2D,
            game: GameModel,
            alpha: number) =>
        {
            const player = game.player;
            let dx = player.x - player.prevX;
            const dy = player.y - player.prevY;

            if(
                player.prevY === TUNNEL_ROW &&
                Math.abs(dx) > 1
            )
            {
                dx = dx > 0
                    ? dx - COLS
                    : dx + COLS;
            }

            let visualX =
                player.prevX +
                (dx * alpha);

            if(visualX < -0.5)
            {
                visualX += COLS;
            }
            else if(visualX > COLS - 0.5)
            {
                visualX -= COLS;
            }

            const visualY =
                player.prevY +
                (dy * alpha);

            const cx =
                (visualX * TILE) +
                (TILE / 2);

            const cy =
                (visualY * TILE) +
                (TILE / 2);

            const directionAngle: Record<Direction, number> = {
                right: 0,
                down: Math.PI / 2,
                left: Math.PI,
                up: -Math.PI / 2,
                none: 0
            };

            const pulse =
                0.20 +
                (
                    Math.abs(
                        Math.sin(game.elapsed * 11)
                    ) * 0.22
                );

            const angle =
                directionAngle[player.dir];

            context.fillStyle = '#ffd52d';
            context.beginPath();
            context.moveTo(cx, cy);
            context.arc(
                cx,
                cy,
                TILE * 0.41,
                angle + pulse,
                angle + (Math.PI * 2) - pulse
            );
            context.closePath();
            context.fill();

            const eyeAngle =
                player.dir === 'left'
                    ? angle + 1.0
                    : angle - 1.0;

            context.fillStyle = '#17130a';
            context.beginPath();
            context.arc(
                cx + Math.cos(eyeAngle) * 4,
                cy + Math.sin(eyeAngle) * 4,
                1.5,
                0,
                Math.PI * 2
            );
            context.fill();
        };

        const drawGhost = (
            context: CanvasRenderingContext2D,
            game: GameModel,
            ghost: Ghost,
            alpha: number) =>
        {
            if(ghost.respawnTime > 0) return;

            let dx = ghost.x - ghost.prevX;
            const dy = ghost.y - ghost.prevY;

            if(
                ghost.prevY === TUNNEL_ROW &&
                Math.abs(dx) > 1
            )
            {
                dx = dx > 0
                    ? dx - COLS
                    : dx + COLS;
            }

            let visualX =
                ghost.prevX +
                (dx * alpha);

            if(visualX < -0.5)
            {
                visualX += COLS;
            }
            else if(visualX > COLS - 0.5)
            {
                visualX -= COLS;
            }

            const visualY =
                ghost.prevY +
                (dy * alpha);

            const cx =
                (visualX * TILE) +
                (TILE / 2);

            const cy =
                (visualY * TILE) +
                (TILE / 2);

            const frightened =
                game.frightened > 0;

            const flashing =
                frightened &&
                game.frightened < 2 &&
                Math.floor(game.elapsed * 8) % 2 === 0;

            context.fillStyle =
                frightened
                    ? (flashing ? '#f3f0d7' : '#3157ce')
                    : GHOST_COLORS[ghost.kind];

            context.beginPath();
            context.arc(
                cx,
                cy - 2,
                TILE * 0.36,
                Math.PI,
                0
            );
            context.lineTo(
                cx + (TILE * 0.36),
                cy + 8
            );
            context.lineTo(cx + 5, cy + 4);
            context.lineTo(cx, cy + 8);
            context.lineTo(cx - 5, cy + 4);
            context.lineTo(
                cx - (TILE * 0.36),
                cy + 8
            );
            context.closePath();
            context.fill();

            if(frightened)
            {
                context.fillStyle =
                    flashing ? '#3157ce' : '#f1f4ff';

                context.fillRect(cx - 5, cy - 3, 2, 2);
                context.fillRect(cx + 3, cy - 3, 2, 2);
                return;
            }

            const look = DIR_VECTOR[ghost.dir];

            for(const offset of [ -4, 4 ])
            {
                context.fillStyle = '#ffffff';
                context.beginPath();
                context.arc(
                    cx + offset,
                    cy - 4,
                    3.5,
                    0,
                    Math.PI * 2
                );
                context.fill();

                context.fillStyle = '#163278';
                context.beginPath();
                context.arc(
                    cx + offset + (look.x * 1.4),
                    cy - 4 + (look.y * 1.4),
                    1.5,
                    0,
                    Math.PI * 2
                );
                context.fill();
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

            context.fillStyle = '#04050a';
            context.fillRect(
                0,
                0,
                BOARD_W,
                BOARD_H
            );

            for(let y = 0; y < ROWS; y++)
            {
                for(let x = 0; x < COLS; x++)
                {
                    const cell = game.maze[y][x];

                    if(cell === '#')
                    {
                        drawWall(context, x, y);
                        continue;
                    }

                    if(cell === '.')
                    {
                        context.fillStyle = '#f6ddb0';
                        context.beginPath();
                        context.arc(
                            (x * TILE) + (TILE / 2),
                            (y * TILE) + (TILE / 2),
                            2.1,
                            0,
                            Math.PI * 2
                        );
                        context.fill();
                    }
                    else if(
                        cell === 'o' &&
                        Math.floor(game.elapsed * 5) % 2 === 0
                    )
                    {
                        context.fillStyle = '#fff0c8';
                        context.beginPath();
                        context.arc(
                            (x * TILE) + (TILE / 2),
                            (y * TILE) + (TILE / 2),
                            5.2,
                            0,
                            Math.PI * 2
                        );
                        context.fill();
                    }
                }
            }

            if(game.fruitActive)
            {
                const cx = (9 * TILE) + (TILE / 2);
                const cy = (13 * TILE) + (TILE / 2);

                context.fillStyle = '#e63946';
                context.beginPath();
                context.arc(
                    cx - 4,
                    cy + 2,
                    5,
                    0,
                    Math.PI * 2
                );
                context.arc(
                    cx + 4,
                    cy + 2,
                    5,
                    0,
                    Math.PI * 2
                );
                context.fill();

                context.strokeStyle = '#61b95d';
                context.lineWidth = 2;
                context.beginPath();
                context.moveTo(cx - 3, cy - 3);
                context.lineTo(cx + 2, cy - 10);
                context.lineTo(cx + 7, cy - 8);
                context.stroke();
            }

            const pStep =
                playerStepMs(game.level);

            const gStep =
                ghostStepMs(
                    game.level,
                    game.frightened > 0
                );

            const playerAlpha =
                game.phase === 'playing'
                    ? Math.min(1, game.playerAccum / pStep)
                    : 1;

            const ghostAlpha =
                game.phase === 'playing'
                    ? Math.min(1, game.ghostAccum / gStep)
                    : 1;

            drawPlayer(
                context,
                game,
                playerAlpha
            );

            for(const ghost of game.ghosts)
            {
                drawGhost(
                    context,
                    game,
                    ghost,
                    ghostAlpha
                );
            }
        };

        const frame = (now: number) =>
        {
            const game = gameRef.current;
            const previous =
                lastFrameRef.current || now;

            const deltaMs =
                Math.min(
                    70,
                    Math.max(0, now - previous)
                );

            const dt = deltaMs / 1000;
            lastFrameRef.current = now;

            if(game.phase === 'playing')
            {
                game.elapsed += dt;

                if(game.frightened > 0)
                {
                    const powerBeat =
                        Math.floor(
                            game.elapsed / 0.22
                        );

                    if(powerBeatRef.current !== powerBeat)
                    {
                        powerBeatRef.current = powerBeat;
                        playPowerBeat(powerBeat);
                    }
                }
                else
                {
                    powerBeatRef.current = -1;
                }

                if(game.feedbackTime > 0)
                {
                    game.feedbackTime =
                        Math.max(
                            0,
                            game.feedbackTime - dt
                        );
                }

                if(game.respawnTimer > 0)
                {
                    game.respawnTimer =
                        Math.max(
                            0,
                            game.respawnTimer - dt
                        );
                }
                else
                {
                    if(game.frightened > 0)
                    {
                        game.frightened =
                            Math.max(
                                0,
                                game.frightened - dt
                            );

                        if(game.frightened <= 0)
                        {
                            game.ghostChain = 0;
                        }
                    }

                    if(game.fruitActive)
                    {
                        game.fruitTimer =
                            Math.max(
                                0,
                                game.fruitTimer - dt
                            );

                        if(game.fruitTimer <= 0)
                        {
                            game.fruitActive = false;
                        }
                    }

                    for(const ghost of game.ghosts)
                    {
                        if(ghost.respawnTime > 0)
                        {
                            ghost.respawnTime =
                                Math.max(
                                    0,
                                    ghost.respawnTime - dt
                                );
                        }
                    }

                    game.playerAccum += deltaMs;
                    game.ghostAccum += deltaMs;

                    const pStep =
                        playerStepMs(game.level);

                    while(
                        game.playerAccum >= pStep &&
                        game.phase === 'playing'
                    )
                    {
                        game.playerAccum -= pStep;
                        stepPlayer(game);
                    }

                    const gStep =
                        ghostStepMs(
                            game.level,
                            game.frightened > 0
                        );

                    while(
                        game.ghostAccum >= gStep &&
                        game.phase === 'playing'
                    )
                    {
                        game.ghostAccum -= gStep;
                        stepGhosts(game);
                    }
                }
            }

            draw();

            frameId =
                window.requestAnimationFrame(frame);
        };

        frameId =
            window.requestAnimationFrame(frame);

        return () =>
        {
            window.cancelAnimationFrame(frameId);
        };
    }, []);

    if(!isVisible) return null;

    const formattedScore =
        score.toString().padStart(5, '0');

    const formattedBest =
        serverBest.toString().padStart(5, '0');

    const eaten =
        Math.max(
            0,
            totalPellets - pelletsLeft
        );

    const progress =
        totalPellets > 0
            ? Math.min(
                100,
                Math.round(
                    (eaten / totalPellets) * 100
                )
            )
            : 0;

    const statusLabel =
        phase === 'playing'
            ? 'JUGANDO'
            : phase === 'paused'
                ? 'PAUSA'
                : phase === 'gameover'
                    ? 'FIN'
                    : 'PREPARADO';

    return (
        <>
            <NitroCardView
                uniqueKey="biribiri-pac-man"
                className="nitro-pac-man"
                data-engine={ UI_MARKER }
                data-item-id={ itemId }>
                <NitroCardHeaderView
                    headerText="Pac-Man"
                    onCloseClick={ close } />

                <NitroCardContentView
                    gap={ 0 }
                    className="pac-content">
                    <div className="pac-cabinet">
                        <span
                            className="pac-cabinet-screw is-top-left"
                            aria-hidden="true" />
                        <span
                            className="pac-cabinet-screw is-top-right"
                            aria-hidden="true" />
                        <span
                            className="pac-cabinet-screw is-bottom-left"
                            aria-hidden="true" />
                        <span
                            className="pac-cabinet-screw is-bottom-right"
                            aria-hidden="true" />
                        <div className="pac-hud">
                            <div className="pac-hud-cell">
                                <span>PUNTUACIÓN</span>
                                <b>{ formattedScore }</b>
                            </div>

                            <div className="pac-hud-cell">
                                <span>NIVEL</span>
                                <b>{ level.toString().padStart(2, '0') }</b>
                            </div>

                            <div className="pac-hud-cell is-lives">
                                <span>VIDAS</span>
                                <div
                                    className="pac-life-row"
                                    aria-label={ `${ lives } vidas` }>
                                    { Array.from(
                                        { length: 3 },
                                        (_, index) =>
                                            <PacHudIcon
                                                key={ index }
                                                active={ index < lives } />
                                    ) }
                                </div>
                            </div>

                            <div className={
                                    `pac-hud-cell is-status is-${ phase }`
                                }>
                                <PacHudIcon
                                    active
                                    className="is-status-icon" />
                                <b>{ statusLabel }</b>
                            </div>
                        </div>

                        <div className="pac-stage">
                            <div className="pac-board-shell">
                                <canvas
                                    ref={ canvasRef }
                                    className="pac-canvas"
                                    width={ BOARD_W * 2 }
                                    height={ BOARD_H * 2 }
                                    tabIndex={ 0 } />

                                { phase === 'ready' &&
                                    <div className="pac-overlay">
                                        <div className="pac-overlay-panel">
                                            <span className="pac-overlay-kicker">
                                                LABERINTO ARCADE
                                            </span>
                                            <strong className="pac-overlay-title">
                                                PAC-MAN
                                            </strong>
                                            <span className="pac-overlay-copy">
                                                COME TODOS LOS PUNTOS
                                                Y EVITA A LOS FANTASMAS
                                            </span>
                                            <button
                                                type="button"
                                                className="pac-primary-button"
                                                disabled={ startPending }
                                                onClick={ requestStartGame }>
                                                { startPending
                                                    ? 'PREPARANDO...'
                                                    : 'JUGAR' }
                                            </button>
                                            <small>
                                                ENTER TAMBIÉN INICIA
                                            </small>
                                        </div>
                                    </div> }

                                { phase === 'paused' &&
                                    <div className="pac-overlay is-pause">
                                        <div className="pac-overlay-panel">
                                            <span className="pac-overlay-kicker">
                                                PARTIDA DETENIDA
                                            </span>
                                            <strong className="pac-overlay-title">
                                                PAUSA
                                            </strong>
                                            <button
                                                type="button"
                                                className="pac-primary-button"
                                                onClick={ togglePause }>
                                                CONTINUAR
                                            </button>
                                        </div>
                                    </div> }

                                { phase === 'gameover' &&
                                    <div className="pac-overlay">
                                        <div className="pac-overlay-panel">
                                            <span className="pac-overlay-kicker">
                                                SIN VIDAS
                                            </span>
                                            <strong className="pac-overlay-title">
                                                FIN DE LA PARTIDA
                                            </strong>

                                            <div className="pac-gameover-results">
                                                <span>
                                                    PUNTUACIÓN
                                                    <b>{ formattedScore }</b>
                                                </span>
                                                <span>
                                                    NIVEL
                                                    <b>{ level }</b>
                                                </span>
                                            </div>

                                            { newServerRecord &&
                                                <span className="pac-new-record">
                                                    ★ NUEVO RÉCORD PERSONAL
                                                </span> }

                                            { resultState !== 'idle' &&
                                                <span
                                                    className={
                                                        `pac-result-message ${
                                                            resultState === 'accepted'
                                                                ? 'is-success'
                                                                : 'is-error'
                                                        }`
                                                    }>
                                                    { resultMessage }
                                                </span> }

                                            <button
                                                type="button"
                                                className="pac-primary-button"
                                                disabled={ startPending }
                                                onClick={ requestStartGame }>
                                                { startPending
                                                    ? 'PREPARANDO...'
                                                    : 'JUGAR DE NUEVO' }
                                            </button>
                                        </div>
                                    </div> }

                                { gameRef.current.feedbackTime > 0 &&
                                    phase === 'playing' &&
                                    <div className="pac-feedback">
                                        { gameRef.current.feedback }
                                    </div> }
                            </div>

                            <div className="pac-side-panel">
                                <section>
                                    <span className="pac-side-title">
                                        OBJETIVO
                                    </span>
                                    <strong>
                                        COME TODO
                                    </strong>
                                    <p>
                                        Los puntos grandes vuelven
                                        vulnerables a los fantasmas.
                                    </p>
                                </section>

                                <section>
                                    <span className="pac-side-title">
                                        PROGRESO
                                    </span>
                                    <div className="pac-progress-labels">
                                        <span>PUNTOS</span>
                                        <b>{ eaten }/{ totalPellets }</b>
                                    </div>
                                    <div className="pac-progress-track">
                                        <span
                                            style={{
                                                width: `${ progress }%`
                                            }} />
                                    </div>
                                    <small>
                                        { progress }% DEL NIVEL
                                    </small>
                                </section>

                            </div>
                        </div>

                        <div className="pac-console">
                            <div className="pac-controls-panel">
                                <div className="pac-control-group">
                                    <span className="pac-keycap">
                                        ← ↑ ↓ →
                                    </span>
                                    <span className="pac-control-action">
                                        MOVER
                                    </span>
                                </div>

                                <div className="pac-console-divider" />

                                <div className="pac-control-group">
                                    <span className="pac-keycap">
                                        WASD
                                    </span>
                                    <span className="pac-control-action">
                                        MOVER
                                    </span>
                                </div>

                                <div className="pac-console-divider" />

                                <div className="pac-control-group">
                                    <span className="pac-keycap">
                                        P
                                    </span>
                                    <span className="pac-control-action">
                                        PAUSA
                                    </span>
                                </div>
                            </div>

                            <div className="pac-console-actions">
                                <button
                                    type="button"
                                    className={
                                        `pac-sound-button${
                                            soundEnabled
                                                ? ' is-on'
                                                : ''
                                        }`
                                    }
                                    onClick={ toggleSound }
                                    aria-pressed={ soundEnabled }>
                                    <span
                                        className="pac-sound-icon"
                                        aria-hidden="true">
                                        ♪
                                    </span>
                                    <span>
                                        SONIDO
                                        <b>
                                            { soundEnabled
                                                ? 'ON'
                                                : 'OFF' }
                                        </b>
                                    </span>
                                </button>

                                <button
                                    type="button"
                                    className="pac-restart-button"
                                    disabled={ startPending }
                                    onClick={ requestStartGame }>
                                    <span
                                        className="pac-restart-shine"
                                        aria-hidden="true" />
                                    REINICIAR
                                </button>
                            </div>
                        </div>

                        <div className="pac-arcade-summary-bar">
                            <div className="pac-arcade-summary-brand">
                                <span>RANKING GLOBAL</span>
                                <strong>PAC-MAN</strong>
                            </div>

                            <div className="pac-arcade-summary-stats">
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
                                className="pac-records-button"
                                onClick={ openRecords }>
                                <span className="pac-records-star">
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
                gameName="Pac-Man"
                levelLabel="NIVEL"
                leaderboard={ leaderboard }
                personalBest={ serverBest }
                personalRank={ personalRank }
                totalPlayers={ totalPlayers }
                onClose={ closeRecords } />
        </>
    );
};
