import {
    ArcadeCloseEvent,
    ArcadeGameStartComposer,
    ArcadeGameStartedEvent,
    ArcadeLeaderboardEvent,
    ArcadeScoreSubmitComposer,
    DuckHuntOpenEvent
} from '@nitrots/nitro-renderer';
import { FC, useEffect, useRef, useState } from 'react';
import type { PointerEvent } from 'react';
import {
    NitroCardContentView,
    NitroCardHeaderView,
    NitroCardView
} from '../../common';
import { SendMessageComposer } from '../../api';
import { useMessageEvent } from '../../hooks/events';
import { ArcadeLeaderboardView } from '../arcade/ArcadeLeaderboardView';
import './DuckHuntView.scss';

type GamePhase = 'ready' | 'playing' | 'paused' | 'gameover';
type DuckState = 'flying' | 'hit' | 'escaping';
type ResultMark = 'pending' | 'hit' | 'miss';
type ServerResultState = 'idle' | 'accepted' | 'rejected';

interface Duck
{
    x: number;
    y: number;
    vx: number;
    vy: number;
    age: number;
    maxAge: number;
    state: DuckState;
    stateTime: number;
    variant: number;
    wing: number;
    motion: number;
    pathPhase: number;
}

interface Feather
{
    x: number;
    y: number;
    vx: number;
    vy: number;
    life: number;
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
    score: number;
    round: number;
    ammo: number;
    hits: number;
    resolved: number;
    results: ResultMark[];
    duck: Duck | null;
    feathers: Feather[];
    spawnDelay: number;
    roundBanner: number;
    feedback: string;
    feedbackTime: number;
    shotFlash: number;
    crosshairX: number;
    crosshairY: number;
    crosshairVisible: boolean;
}

const WIDTH = 640;
const HEIGHT = 420;
const TARGETS_PER_ROUND = 10;
const REQUIRED_HITS = 6;
const MAX_ROUND = 50;
const GAME_KEY = 'duck_hunt';
const UI_MARKER = 'BIRIBIRI_DUCK_HUNT_V11_FINAL';

const makeGame = (
    phase: GamePhase = 'ready'): GameModel => ({
    phase,
    score: 0,
    round: 1,
    ammo: 3,
    hits: 0,
    resolved: 0,
    results: Array.from(
        { length: TARGETS_PER_ROUND },
        () => 'pending' as ResultMark
    ),
    duck: null,
    feathers: [],
    spawnDelay: 0.75,
    roundBanner: 0,
    feedback: '',
    feedbackTime: 0,
    shotFlash: 0,
    crosshairX: WIDTH / 2,
    crosshairY: HEIGHT / 2,
    crosshairVisible: false
});

const PixelShell: FC<{ active: boolean }> = ({ active }) =>
{
    return (
        <svg
            className={ `duck-shell${ active ? ' is-active' : '' }` }
            viewBox="0 0 12 20"
            shapeRendering="crispEdges"
            aria-hidden="true">
            <rect className="shell-outline" x="2" y="2" width="8" height="15" />
            <rect className="shell-crimp" x="3" y="3" width="6" height="2" />
            <rect className="shell-crimp-notch" x="4" y="2" width="1" height="2" />
            <rect className="shell-crimp-notch" x="7" y="2" width="1" height="2" />
            <rect className="shell-body" x="3" y="5" width="6" height="9" />
            <rect className="shell-shadow" x="8" y="6" width="1" height="7" />
            <rect className="shell-light" x="4" y="6" width="1" height="6" />
            <rect className="shell-brass" x="2" y="14" width="8" height="3" />
            <rect className="shell-rim" x="1" y="17" width="10" height="2" />
            <rect className="shell-primer" x="5" y="15" width="2" height="1" />
        </svg>
    );
};

const PixelDuckIcon: FC<{}> = () =>
{
    return (
        <svg
            className="duck-status-icon"
            viewBox="0 0 26 20"
            shapeRendering="crispEdges"
            aria-hidden="true">
            <rect className="duck-icon-body" x="7" y="8" width="13" height="7" />
            <rect className="duck-icon-head" x="16" y="4" width="7" height="7" />
            <rect className="duck-icon-beak" x="22" y="7" width="4" height="3" />
            <rect className="duck-icon-wing" x="5" y="9" width="7" height="4" />
            <rect className="duck-icon-leg" x="10" y="15" width="2" height="3" />
            <rect className="duck-icon-leg" x="16" y="15" width="2" height="3" />
            <rect className="duck-icon-eye" x="20" y="5" width="1" height="1" />
        </svg>
    );
};

export const DuckHuntView: FC<{}> = () =>
{
    const canvasRef = useRef<HTMLCanvasElement | null>(null);
    const visibleRef = useRef(false);
    const lastFrameRef = useRef(0);
    const gameRef = useRef<GameModel>(makeGame());
    const itemIdRef = useRef(0);
    const runTokenRef = useRef('');
    const submittedRunRef = useRef(false);
    const startPendingRef = useRef(false);
    const soundEnabledRef = useRef(true);
    const audioRef = useRef<AudioContext | null>(null);

    const [ isVisible, setIsVisible ] = useState(false);
    const [ itemId, setItemId ] = useState(0);
    const [ score, setScore ] = useState(0);
    const [ round, setRound ] = useState(1);
    const [ ammo, setAmmo ] = useState(3);
    const [ hits, setHits ] = useState(0);
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
        volume = 0.045,
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
        oscillator.frequency.setValueAtTime(frequency, start);

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
            start + 0.006
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

    const playShot = () =>
    {
        tone(125, 0.085, 'sawtooth', 0.072, 0, 48);
        tone(64, 0.055, 'square', 0.048, 0.018, 38);
    };

    const playHit = () =>
    {
        tone(520, 0.07, 'square', 0.05, 0, 790);
        tone(820, 0.08, 'square', 0.035, 0.06, 1120);
    };

    const playEscape = () =>
    {
        tone(245, 0.09, 'square', 0.035, 0, 165);
        tone(155, 0.12, 'triangle', 0.03, 0.08, 92);
    };

    const playRoundClear = () =>
    {
        tone(392, 0.08, 'square', 0.038, 0);
        tone(523, 0.08, 'square', 0.04, 0.08);
        tone(659, 0.12, 'square', 0.045, 0.16);
        tone(784, 0.15, 'square', 0.045, 0.28);
    };

    const playGameOver = () =>
    {
        tone(330, 0.12, 'square', 0.04, 0);
        tone(247, 0.14, 'square', 0.042, 0.12);
        tone(165, 0.22, 'sawtooth', 0.05, 0.26, 68);
    };

    const playStart = () =>
    {
        tone(330, 0.07, 'square', 0.032, 0);
        tone(440, 0.07, 'square', 0.034, 0.07);
        tone(660, 0.11, 'square', 0.04, 0.14);
    };

    const syncHud = () =>
    {
        const game = gameRef.current;

        setScore(game.score);
        setRound(game.round);
        setAmmo(game.ammo);
        setHits(game.hits);
        setPhase(game.phase);
    };

    const resetReadyGame = () =>
    {
        gameRef.current = makeGame('ready');
        lastFrameRef.current = 0;
        runTokenRef.current = '';
        submittedRunRef.current = false;
        startPendingRef.current = false;

        setStartPending(false);
        setResultState('idle');
        setResultMessage('');
        setNewServerRecord(false);
        syncHud();
    };

    const beginLocalRun = (token: string) =>
    {
        const next = makeGame('playing');

        next.roundBanner = 1.15;
        next.feedback = 'NIVEL 1';
        next.feedbackTime = 1.15;

        gameRef.current = next;
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
        finalRound: number) =>
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
                finalRound
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
            tone(520, 0.06, 'square', 0.028);
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

    const getCanvasPoint = (
        event: PointerEvent<HTMLCanvasElement>) =>
    {
        const canvas = canvasRef.current;

        if(!canvas)
        {
            return {
                x: WIDTH / 2,
                y: HEIGHT / 2
            };
        }

        const rect = canvas.getBoundingClientRect();

        return {
            x: (
                (event.clientX - rect.left) /
                Math.max(1, rect.width)
            ) * WIDTH,
            y: (
                (event.clientY - rect.top) /
                Math.max(1, rect.height)
            ) * HEIGHT
        };
    };

    const handlePointerMove = (
        event: PointerEvent<HTMLCanvasElement>) =>
    {
        const point = getCanvasPoint(event);
        const game = gameRef.current;

        game.crosshairX = point.x;
        game.crosshairY = point.y;
        game.crosshairVisible = true;
    };

    const handlePointerLeave = () =>
    {
        gameRef.current.crosshairVisible = false;
    };

    const handleShot = (
        event: PointerEvent<HTMLCanvasElement>) =>
    {
        if(event.button !== 0) return;

        const game = gameRef.current;

        if(
            game.phase !== 'playing' ||
            !game.duck ||
            game.duck.state !== 'flying' ||
            game.ammo <= 0
        )
        {
            return;
        }

        const point = getCanvasPoint(event);

        game.crosshairX = point.x;
        game.crosshairY = point.y;
        game.crosshairVisible = true;
        game.ammo -= 1;
        game.shotFlash = 0.085;

        setAmmo(game.ammo);
        playShot();

        const duck = game.duck;
        const halfW = 24;
        const halfH = 17;

        const hit =
            point.x >= duck.x - halfW &&
            point.x <= duck.x + halfW &&
            point.y >= duck.y - halfH &&
            point.y <= duck.y + halfH;

        if(hit)
        {
            duck.state = 'hit';
            duck.stateTime = 0;
            duck.vy = 40;

            game.score += 100;
            game.hits += 1;
            game.results[game.resolved] = 'hit';
            game.feedback = '¡ACIERTO! +100';
            game.feedbackTime = 0.7;

            for(let i = 0; i < 7; i++)
            {
                const angle =
                    (Math.PI * 2 * i) / 7;

                game.feathers.push({
                    x: duck.x,
                    y: duck.y,
                    vx: Math.cos(angle) * (45 + (i * 4)),
                    vy: Math.sin(angle) * 35 - 35,
                    life: 0.55 + ((i % 3) * 0.08)
                });
            }

            setScore(game.score);
            setHits(game.hits);
            playHit();
        }
        else
        {
            game.feedback =
                game.ammo > 0
                    ? 'FALLO'
                    : 'SIN MUNICIÓN';

            game.feedbackTime = 0.42;

            if(game.ammo <= 0)
            {
                duck.state = 'escaping';
                duck.stateTime = 0;
                game.results[game.resolved] = 'miss';
                playEscape();
            }
        }
    };

    useMessageEvent(
        DuckHuntOpenEvent,
        (event: DuckHuntOpenEvent) =>
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

            if([
                'Enter',
                'KeyP',
                'Escape'
            ].includes(event.code))
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

        const roundBonus = (roundHits: number) =>
        {
            if(roundHits >= 10) return 500;
            if(roundHits === 9) return 300;
            if(roundHits === 8) return 200;
            if(roundHits === 7) return 100;

            return 0;
        };

        const finishGame = () =>
        {
            const game = gameRef.current;

            if(game.phase === 'gameover') return;

            game.phase = 'gameover';
            game.duck = null;
            game.crosshairVisible = false;

            setPhase('gameover');
            setScore(game.score);
            setRound(game.round);
            setHits(game.hits);

            playGameOver();

            submitRunResult(
                game.score,
                game.round
            );
        };

        const startNextRound = () =>
        {
            const game = gameRef.current;
            const bonus = roundBonus(game.hits);

            game.score += bonus;

            if(game.round >= MAX_ROUND)
            {
                game.feedback =
                    bonus > 0
                        ? `NIVEL PERFECTO +${ bonus }`
                        : 'CAMPEONATO COMPLETADO';

                game.feedbackTime = 1.3;

                setScore(game.score);
                finishGame();
                return;
            }

            game.round += 1;
            game.hits = 0;
            game.resolved = 0;
            game.results = Array.from(
                { length: TARGETS_PER_ROUND },
                () => 'pending' as ResultMark
            );
            game.duck = null;
            game.ammo = 3;
            game.spawnDelay = 1.05;
            game.roundBanner = 1.2;
            game.feedback =
                bonus > 0
                    ? `BONUS +${ bonus }`
                    : `NIVEL ${ game.round }`;
            game.feedbackTime = 1.05;

            setScore(game.score);
            setRound(game.round);
            setHits(0);
            setAmmo(3);

            playRoundClear();
        };

        const resolveDuck = () =>
        {
            const game = gameRef.current;

            game.resolved += 1;
            game.duck = null;
            game.ammo = 3;
            game.spawnDelay = 0.62;

            setAmmo(3);

            if(game.resolved >= TARGETS_PER_ROUND)
            {
                if(game.hits < REQUIRED_HITS)
                {
                    game.feedback = 'NIVEL NO SUPERADO';
                    game.feedbackTime = 1.2;
                    finishGame();
                    return;
                }

                startNextRound();
            }
        };

        const spawnDuck = () =>
        {
            const game = gameRef.current;
            const fromLeft =
                Math.random() > 0.5;

            const difficulty =
                Math.max(0, game.round - 1);

            const speed =
                145 +
                (difficulty * 9) +
                (Math.random() * 36);

            const y =
                82 +
                (Math.random() * 205);

            const vertical =
                -46 +
                (Math.random() * 92);

            let motion = 0;

            if(game.round >= 4)
            {
                const roll = Math.random();

                if(game.round >= 8 && roll > 0.76)
                {
                    motion = 3;
                }
                else if(roll > 0.48)
                {
                    motion = 2;
                }
                else if(roll > 0.20)
                {
                    motion = 1;
                }
            }

            game.duck = {
                x: fromLeft ? -34 : WIDTH + 34,
                y,
                vx: fromLeft ? speed : -speed,
                vy: vertical,
                age: 0,
                maxAge: Math.max(
                    1.95,
                    4.05 - (difficulty * 0.08)
                ),
                state: 'flying',
                stateTime: 0,
                variant: Math.floor(Math.random() * 3),
                wing: Math.random() * Math.PI * 2,
                motion,
                pathPhase: Math.random() * Math.PI * 2
            };

            game.ammo = 3;
            setAmmo(3);

            tone(
                310,
                0.035,
                'square',
                0.012,
                0,
                350
            );
        };

        const update = (dt: number) =>
        {
            const game = gameRef.current;

            game.roundBanner = Math.max(
                0,
                game.roundBanner - dt
            );
            game.feedbackTime = Math.max(
                0,
                game.feedbackTime - dt
            );
            game.shotFlash = Math.max(
                0,
                game.shotFlash - dt
            );

            for(const feather of game.feathers)
            {
                feather.life -= dt;
                feather.x += feather.vx * dt;
                feather.y += feather.vy * dt;
                feather.vy += 105 * dt;
            }

            game.feathers =
                game.feathers.filter(
                    feather => feather.life > 0
                );

            if(game.phase !== 'playing')
            {
                return;
            }

            if(!game.duck)
            {
                game.spawnDelay -= dt;

                if(
                    game.spawnDelay <= 0 &&
                    game.resolved < TARGETS_PER_ROUND
                )
                {
                    spawnDuck();
                }

                return;
            }

            const duck = game.duck;

            duck.stateTime += dt;
            duck.wing += dt * 12;

            if(duck.state === 'flying')
            {
                duck.age += dt;

                const difficulty =
                    Math.max(0, game.round - 1);

                const pathSpeed =
                    2.15 +
                    Math.min(1.85, difficulty * 0.055);

                duck.pathPhase += dt * pathSpeed;

                let horizontalFactor = 1;
                let verticalWave =
                    Math.sin(duck.wing * 0.48) * 16;

                if(duck.motion === 1)
                {
                    verticalWave +=
                        Math.sin(duck.pathPhase * 2.25) *
                        (
                            42 +
                            Math.min(34, difficulty * 1.8)
                        );
                }
                else if(duck.motion === 2)
                {
                    verticalWave +=
                        Math.sin(duck.pathPhase) *
                        (
                            78 +
                            Math.min(48, difficulty * 2.2)
                        );

                    horizontalFactor =
                        0.90 +
                        (
                            Math.sin(duck.pathPhase * 0.72) *
                            0.10
                        );
                }
                else if(duck.motion === 3)
                {
                    verticalWave +=
                        Math.sin(duck.pathPhase * 2.7) *
                        (
                            58 +
                            Math.min(42, difficulty * 2.0)
                        );

                    horizontalFactor =
                        0.82 +
                        (
                            Math.cos(duck.pathPhase * 1.55) *
                            0.18
                        );
                }

                duck.x +=
                    duck.vx *
                    horizontalFactor *
                    dt;

                duck.y +=
                    duck.vy * dt +
                    verticalWave * dt;

                if(duck.y < 70)
                {
                    duck.y = 70;
                    duck.vy = Math.abs(duck.vy);
                }
                else if(duck.y > 300)
                {
                    duck.y = 300;
                    duck.vy = -Math.abs(duck.vy);
                }

                const outHorizontally =
                    duck.x < -58 ||
                    duck.x > WIDTH + 58;

                if(
                    duck.age >= duck.maxAge ||
                    outHorizontally
                )
                {
                    duck.state = 'escaping';
                    duck.stateTime = 0;
                    game.results[game.resolved] = 'miss';
                    game.feedback = 'SE ESCAPÓ';
                    game.feedbackTime = 0.55;
                    playEscape();
                }
            }
            else if(duck.state === 'hit')
            {
                duck.y +=
                    (165 + (duck.stateTime * 120)) *
                    dt;
                duck.x += duck.vx * 0.12 * dt;
                duck.wing += dt * 20;

                if(
                    duck.stateTime >= 0.62 ||
                    duck.y > HEIGHT - 42
                )
                {
                    resolveDuck();
                }
            }
            else if(duck.state === 'escaping')
            {
                duck.y -=
                    (125 + (duck.stateTime * 95)) *
                    dt;
                duck.x += duck.vx * 0.35 * dt;

                if(
                    duck.stateTime >= 0.58 ||
                    duck.y < 25
                )
                {
                    resolveDuck();
                }
            }
        };

        const drawPixelDuck = (
            ctx: CanvasRenderingContext2D,
            duck: Duck) =>
        {
            const x = Math.round(duck.x);
            const y = Math.round(duck.y);
            const flap =
                Math.sin(duck.wing) > 0;

            const facing =
                duck.vx >= 0 ? 1 : -1;

            ctx.save();
            ctx.translate(x, y);
            ctx.scale(facing, 1);

            let body = '#6e4c2c';
            let head = '#23815f';
            let wing = '#b8894d';
            let neck = '#f0ece1';

            if(duck.variant === 1)
            {
                body = '#945946';
                head = '#a23f46';
                wing = '#d79060';
                neck = '#f4d2a1';
            }
            else if(duck.variant === 2)
            {
                body = '#54789b';
                head = '#315a83';
                wing = '#91b4cf';
                neck = '#d5edf6';
            }

            if(duck.state === 'hit')
            {
                ctx.rotate(
                    Math.min(
                        Math.PI * 0.42,
                        duck.stateTime * 2.1
                    )
                );
            }

            ctx.fillStyle = body;
            ctx.fillRect(-15, -7, 27, 15);
            ctx.fillRect(-20, -3, 8, 8);

            ctx.fillStyle = head;
            ctx.fillRect(8, -14, 12, 12);
            ctx.fillRect(5, -10, 8, 7);

            ctx.fillStyle = neck;
            ctx.fillRect(6, -6, 5, 5);

            ctx.fillStyle = '#f0a83b';
            ctx.fillRect(18, -10, 9, 5);
            ctx.fillRect(22, -8, 7, 3);

            ctx.fillStyle = '#111820';
            ctx.fillRect(15, -11, 2, 2);

            ctx.fillStyle = wing;

            if(flap)
            {
                ctx.fillRect(-8, -17, 13, 8);
                ctx.fillRect(-4, -21, 8, 5);
            }
            else
            {
                ctx.fillRect(-8, -2, 15, 9);
                ctx.fillRect(-2, 5, 9, 5);
            }

            ctx.fillStyle = '#2b2d31';
            ctx.fillRect(-18, 7, 7, 3);
            ctx.fillRect(-9, 7, 6, 3);

            ctx.restore();
        };

        const drawCloud = (
            ctx: CanvasRenderingContext2D,
            x: number,
            y: number,
            scale: number) =>
        {
            ctx.fillStyle = 'rgba(255,255,255,.82)';
            ctx.fillRect(
                x,
                y + (8 * scale),
                54 * scale,
                14 * scale
            );
            ctx.fillRect(
                x + (10 * scale),
                y,
                18 * scale,
                18 * scale
            );
            ctx.fillRect(
                x + (28 * scale),
                y + (4 * scale),
                18 * scale,
                16 * scale
            );
        };

        const drawScene = (
            ctx: CanvasRenderingContext2D) =>
        {
            const sky =
                ctx.createLinearGradient(
                    0,
                    0,
                    0,
                    HEIGHT
                );

            sky.addColorStop(0, '#64bfe5');
            sky.addColorStop(.58, '#b8e7e9');
            sky.addColorStop(1, '#e8ddb0');

            ctx.fillStyle = sky;
            ctx.fillRect(0, 0, WIDTH, HEIGHT);

            ctx.fillStyle = '#ffe29b';
            ctx.fillRect(520, 38, 34, 34);
            ctx.fillStyle = '#fff1bd';
            ctx.fillRect(526, 44, 22, 22);

            drawCloud(ctx, 70, 45, 1);
            drawCloud(ctx, 330, 86, .72);

            ctx.fillStyle = '#7ea68f';
            ctx.beginPath();
            ctx.moveTo(0, 285);
            ctx.lineTo(80, 215);
            ctx.lineTo(150, 278);
            ctx.lineTo(230, 205);
            ctx.lineTo(320, 280);
            ctx.lineTo(420, 220);
            ctx.lineTo(510, 285);
            ctx.lineTo(590, 225);
            ctx.lineTo(640, 276);
            ctx.lineTo(640, 340);
            ctx.lineTo(0, 340);
            ctx.closePath();
            ctx.fill();

            ctx.fillStyle = '#5e8b70';
            ctx.fillRect(0, 286, WIDTH, 54);

            ctx.fillStyle = '#7fae64';
            ctx.fillRect(0, 330, WIDTH, 90);

            ctx.fillStyle = '#527a48';

            for(let x = 0; x < WIDTH; x += 18)
            {
                const height =
                    12 + ((x * 7) % 21);

                ctx.fillRect(
                    x,
                    330 - height,
                    4,
                    height
                );
            }

            ctx.fillStyle = '#3f6841';

            for(let x = 6; x < WIDTH; x += 26)
            {
                ctx.fillRect(
                    x,
                    344,
                    3,
                    30 + ((x * 3) % 22)
                );
                ctx.fillRect(
                    x - 5,
                    351,
                    8,
                    3
                );
                ctx.fillRect(
                    x + 2,
                    360,
                    8,
                    3
                );
            }

            ctx.fillStyle = '#335a39';
            ctx.fillRect(0, 386, WIDTH, 34);

            ctx.fillStyle = '#2a4b31';
            for(let x = 0; x < WIDTH; x += 14)
            {
                ctx.fillRect(
                    x,
                    382 - ((x * 5) % 8),
                    3,
                    14
                );
            }
        };

        const drawProgress = (
            ctx: CanvasRenderingContext2D,
            game: GameModel) =>
        {
            const totalWidth = 244;
            const startX =
                Math.round(
                    (WIDTH - totalWidth) / 2
                );
            const y = HEIGHT - 28;

            ctx.fillStyle = 'rgba(7, 18, 20, .76)';
            ctx.fillRect(
                startX - 10,
                y - 7,
                totalWidth + 20,
                25
            );

            for(let i = 0; i < TARGETS_PER_ROUND; i++)
            {
                const x =
                    startX + (i * 24);
                const result =
                    game.results[i];

                ctx.fillStyle =
                    result === 'hit'
                        ? '#69d77b'
                        : result === 'miss'
                            ? '#e96565'
                            : '#294452';

                ctx.fillRect(
                    x,
                    y,
                    17,
                    12
                );

                ctx.fillStyle =
                    result === 'hit'
                        ? '#e9ffe9'
                        : result === 'miss'
                            ? '#ffdede'
                            : '#57717c';

                if(result === 'hit')
                {
                    ctx.fillRect(x + 4, y + 6, 3, 3);
                    ctx.fillRect(x + 7, y + 8, 3, 3);
                    ctx.fillRect(x + 9, y + 4, 5, 3);
                }
                else if(result === 'miss')
                {
                    ctx.fillRect(x + 4, y + 3, 3, 3);
                    ctx.fillRect(x + 10, y + 3, 3, 3);
                    ctx.fillRect(x + 7, y + 6, 3, 3);
                    ctx.fillRect(x + 4, y + 9, 3, 2);
                    ctx.fillRect(x + 10, y + 9, 3, 2);
                }
            }

            ctx.textAlign = 'left';
            ctx.textBaseline = 'alphabetic';
            ctx.font = 'bold 9px monospace';
            ctx.fillStyle = '#eaf8f7';
            ctx.fillText(
                `${ REQUIRED_HITS }/10 PARA PASAR`,
                startX,
                y - 10
            );
        };

        const drawCrosshair = (
            ctx: CanvasRenderingContext2D,
            game: GameModel) =>
        {
            if(
                !game.crosshairVisible ||
                game.phase !== 'playing'
            )
            {
                return;
            }

            const x = Math.round(game.crosshairX);
            const y = Math.round(game.crosshairY);

            ctx.strokeStyle = '#0d1920';
            ctx.lineWidth = 4;
            ctx.beginPath();
            ctx.arc(x, y, 12, 0, Math.PI * 2);
            ctx.stroke();

            ctx.strokeStyle = '#fff5df';
            ctx.lineWidth = 2;
            ctx.beginPath();
            ctx.arc(x, y, 12, 0, Math.PI * 2);
            ctx.stroke();

            ctx.strokeStyle = '#0d1920';
            ctx.lineWidth = 4;
            ctx.beginPath();
            ctx.moveTo(x - 20, y);
            ctx.lineTo(x - 6, y);
            ctx.moveTo(x + 6, y);
            ctx.lineTo(x + 20, y);
            ctx.moveTo(x, y - 20);
            ctx.lineTo(x, y - 6);
            ctx.moveTo(x, y + 6);
            ctx.lineTo(x, y + 20);
            ctx.stroke();

            ctx.strokeStyle = '#fff5df';
            ctx.lineWidth = 2;
            ctx.beginPath();
            ctx.moveTo(x - 20, y);
            ctx.lineTo(x - 6, y);
            ctx.moveTo(x + 6, y);
            ctx.lineTo(x + 20, y);
            ctx.moveTo(x, y - 20);
            ctx.lineTo(x, y - 6);
            ctx.moveTo(x, y + 6);
            ctx.lineTo(x, y + 20);
            ctx.stroke();
        };

        const draw = () =>
        {
            const canvas = canvasRef.current;

            if(!canvas) return;

            const ctx =
                canvas.getContext('2d');

            if(!ctx) return;

            const game = gameRef.current;

            ctx.imageSmoothingEnabled = false;

            drawScene(ctx);

            if(game.duck)
            {
                drawPixelDuck(
                    ctx,
                    game.duck
                );
            }

            for(const feather of game.feathers)
            {
                ctx.fillStyle =
                    feather.life > .32
                        ? '#f8f2df'
                        : '#d4c8a5';

                ctx.fillRect(
                    Math.round(feather.x),
                    Math.round(feather.y),
                    4,
                    2
                );
            }

            drawProgress(
                ctx,
                game
            );

            if(game.feedbackTime > 0)
            {
                ctx.textAlign = 'center';
                ctx.textBaseline = 'middle';
                ctx.font = 'bold 15px monospace';

                const width =
                    Math.max(
                        126,
                        ctx.measureText(
                            game.feedback
                        ).width + 28
                    );

                ctx.fillStyle =
                    'rgba(6, 21, 25, .78)';
                ctx.fillRect(
                    (WIDTH - width) / 2,
                    34,
                    width,
                    32
                );

                ctx.strokeStyle = '#d9c46d';
                ctx.lineWidth = 1;
                ctx.strokeRect(
                    (WIDTH - width) / 2,
                    34,
                    width,
                    32
                );

                ctx.fillStyle = '#fff1ac';
                ctx.fillText(
                    game.feedback,
                    WIDTH / 2,
                    50
                );
            }

            if(game.shotFlash > 0)
            {
                ctx.fillStyle =
                    `rgba(255, 244, 196, ${
                        Math.min(
                            .16,
                            game.shotFlash * 1.6
                        )
                    })`;

                ctx.fillRect(
                    0,
                    0,
                    WIDTH,
                    HEIGHT
                );
            }

            drawCrosshair(
                ctx,
                game
            );
        };

        const frame = (now: number) =>
        {
            if(visibleRef.current)
            {
                const last =
                    lastFrameRef.current;

                lastFrameRef.current = now;

                if(last > 0)
                {
                    const dt =
                        Math.min(
                            0.033,
                            Math.max(
                                0,
                                (now - last) / 1000
                            )
                        );

                    update(dt);
                }

                draw();
            }
            else
            {
                lastFrameRef.current = now;
            }

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
            ? 'CAZANDO'
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
                uniqueKey="duck-hunt"
                className="nitro-duck-hunt"
                theme="primary-slim"
                style={ { width: '760px' } }>
                <NitroCardHeaderView
                    headerText="Duck Hunt"
                    onCloseClick={ close } />

                <NitroCardContentView
                    gap={ 0 }
                    className="duck-hunt-content">
                    <div
                        className="duck-hunt-shell"
                        data-engine={ UI_MARKER }
                    data-shell="BIRIBIRI_DUCK_HUNT_V111_THIN_SHELL"
                    data-level-naming="BIRIBIRI_DUCK_HUNT_V112_LEVEL_NAMING"
                        data-item-id={ itemId }>
                        <span
                            className="duck-cabinet-screw is-top-left"
                            aria-hidden="true" />
                        <span
                            className="duck-cabinet-screw is-top-right"
                            aria-hidden="true" />
                        <span
                            className="duck-cabinet-screw is-bottom-left"
                            aria-hidden="true" />
                        <span
                            className="duck-cabinet-screw is-bottom-right"
                            aria-hidden="true" />

                        <div className="duck-hunt-hud">
                            <div className="duck-hud-cell is-score">
                                <span className="duck-hud-label">
                                    PUNTUACIÓN
                                </span>
                                <strong className="duck-score-value">
                                    { formattedScore }
                                </strong>
                            </div>

                            <div className="duck-hud-cell is-round">
                                <span className="duck-hud-label">
                                    NIVEL
                                </span>
                                <strong className="duck-round-value">
                                    { round
                                        .toString()
                                        .padStart(2, '0') }
                                </strong>
                            </div>

                            <div className="duck-hud-cell is-ammo">
                                <span className="duck-hud-label">
                                    MUNICIÓN
                                </span>
                                <div
                                    className="duck-ammo-row"
                                    aria-label={ `${ ammo } disparos` }>
                                    { Array.from(
                                        { length: 3 },
                                        (_, index) =>
                                            <PixelShell
                                                key={ index }
                                                active={ index < ammo } />
                                    ) }
                                </div>
                            </div>

                            <div className="duck-hud-cell is-hits">
                                <span className="duck-hud-label">
                                    ACIERTOS
                                </span>
                                <strong className="duck-hits-value">
                                    { hits }/10
                                </strong>
                            </div>

                            <div
                                className={
                                    `duck-hud-cell is-status is-${ phase }`
                                }>
                                <PixelDuckIcon />
                                <strong>{ phaseLabel }</strong>
                            </div>
                        </div>

                        <div className="duck-hunt-stage">
                            <canvas
                                ref={ canvasRef }
                                className="duck-hunt-canvas"
                                width={ WIDTH }
                                height={ HEIGHT }
                                tabIndex={ 0 }
                                aria-label="Duck Hunt"
                                onPointerMove={ handlePointerMove }
                                onPointerLeave={ handlePointerLeave }
                                onPointerDown={ handleShot } />

                            { phase === 'ready' &&
                                <div className="duck-game-overlay">
                                    <div className="duck-overlay-panel">
                                        <span className="duck-overlay-kicker">
                                            GALERÍA DE CAZA
                                        </span>
                                        <strong className="duck-overlay-title">
                                            DUCK HUNT
                                        </strong>
                                        <span className="duck-overlay-copy">
                                            Apunta con el ratón y derriba
                                            al menos 6 de 10 patos.
                                            Tienes 3 disparos por objetivo.
                                        </span>

                                        <button
                                            type="button"
                                            className="duck-primary-button"
                                            disabled={ startPending }
                                            onClick={ requestStartGame }>
                                            { startPending
                                                ? 'PREPARANDO...'
                                                : 'JUGAR' }
                                        </button>

                                        <span className="duck-overlay-hint">
                                            ENTER también inicia la partida
                                        </span>

                                        { resultState === 'rejected' &&
                                            <span className="duck-result-message is-error">
                                                { resultMessage }
                                            </span> }
                                    </div>
                                </div> }

                            { phase === 'paused' &&
                                <div className="duck-game-overlay is-pause">
                                    <div className="duck-overlay-panel is-compact">
                                        <span className="duck-overlay-kicker">
                                            PARTIDA DETENIDA
                                        </span>
                                        <strong className="duck-overlay-title">
                                            PAUSA
                                        </strong>

                                        <button
                                            type="button"
                                            className="duck-primary-button"
                                            onClick={ togglePause }>
                                            CONTINUAR
                                        </button>

                                        <span className="duck-overlay-hint">
                                            P o ESC para continuar
                                        </span>
                                    </div>
                                </div> }

                            { phase === 'gameover' &&
                                <div className="duck-game-overlay is-gameover">
                                    <div className="duck-overlay-panel">
                                        <span className="duck-overlay-kicker">
                                            TEMPORADA FINALIZADA
                                        </span>
                                        <strong className="duck-overlay-title">
                                            FIN DE LA PARTIDA
                                        </strong>

                                        <div className="duck-gameover-results">
                                            <span>
                                                PUNTUACIÓN
                                                <b>{ formattedScore }</b>
                                            </span>

                                            <span>
                                                NIVEL
                                                <b>{ round }</b>
                                            </span>

                                            <span>
                                                ACIERTOS
                                                <b>{ hits }/10</b>
                                            </span>
                                        </div>

                                        { newServerRecord &&
                                            <div className="duck-new-record">
                                                ★ NUEVO RÉCORD PERSONAL ★
                                            </div> }

                                        { resultState !== 'idle' &&
                                            <span
                                                className={
                                                    `duck-result-message${
                                                        resultState === 'rejected'
                                                            ? ' is-error'
                                                            : ' is-success'
                                                    }`
                                                }>
                                                { resultMessage }
                                            </span> }

                                        <button
                                            type="button"
                                            className="duck-primary-button"
                                            disabled={ startPending }
                                            onClick={ requestStartGame }>
                                            { startPending
                                                ? 'PREPARANDO...'
                                                : 'JUGAR DE NUEVO' }
                                        </button>
                                    </div>
                                </div> }
                        </div>

                        <div className="duck-hunt-console">
                            <div className="duck-controls-panel">
                                <div className="duck-control-group">
                                    <span className="duck-mouse-icon">
                                        +
                                    </span>
                                    <span className="duck-control-action">
                                        RATÓN · APUNTAR
                                    </span>
                                </div>

                                <span className="duck-console-divider" />

                                <div className="duck-control-group">
                                    <span className="duck-keycap is-click">
                                        CLIC
                                    </span>
                                    <span className="duck-control-action">
                                        DISPARAR
                                    </span>
                                </div>

                                <span className="duck-console-divider" />

                                <div className="duck-control-group">
                                    <span className="duck-keycap">
                                        P
                                    </span>
                                    <span className="duck-control-action">
                                        PAUSA
                                    </span>
                                </div>
                            </div>

                            <div className="duck-console-actions">
                                <button
                                    type="button"
                                    className={
                                        `duck-sound-button${
                                            soundEnabled
                                                ? ' is-on'
                                                : ''
                                        }`
                                    }
                                    onClick={ toggleSound }
                                    aria-pressed={ soundEnabled }>
                                    <span
                                        className="duck-sound-icon"
                                        aria-hidden="true">
                                        ♪
                                    </span>
                                    <span>
                                        SONIDO
                                        <b>
                                            { soundEnabled ? 'ON' : 'OFF' }
                                        </b>
                                    </span>
                                </button>

                                <button
                                    type="button"
                                    className="duck-restart-button"
                                    disabled={ startPending }
                                    onClick={ requestStartGame }>
                                    <span className="duck-restart-shine" />
                                    <span>REINICIAR</span>
                                </button>
                            </div>
                        </div>

                        <div className="duck-arcade-summary-bar">
                            <div className="duck-arcade-summary-brand">
                                <span>RANKING GLOBAL</span>
                                <strong>DUCK HUNT</strong>
                            </div>

                            <div className="duck-arcade-summary-stats">
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
                                className="duck-records-button"
                                onClick={ openRecords }>
                                <span
                                    className="duck-records-star"
                                    aria-hidden="true">
                                    ★
                                </span>
                                <span>RÉCORDS</span>
                            </button>
                        </div>
                    </div>
                </NitroCardContentView>
            </NitroCardView>

            <ArcadeLeaderboardView
                visible={ recordsOpen }
                gameName="Duck Hunt"
                levelLabel="NIVEL"
                leaderboard={ leaderboard }
                personalBest={ serverBest }
                personalRank={ personalRank }
                totalPlayers={ totalPlayers }
                onClose={ closeRecords } />
        </>
    );
};
