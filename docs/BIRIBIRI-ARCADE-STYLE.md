# Biribiri Arcade — Canonical UI Reference

## Status

**Space Invaders** and **Duck Hunt** are the canonical visual reference for Biribiri arcade machines.

When creating or polishing another arcade game, preserve their structural language instead of inventing a new dashboard.

## Canonical structure

1. Native Nitro/Holo card header.
2. Beige/grey `NitroCardContentView` frame around the game.
3. Dark beveled cabinet shell inside that frame.
4. Four visible cabinet screws, one near each corner.
5. Compact HUD at the top:
   - PUNTUACIÓN
   - NIVEL
   - game-specific resource/status
   - status
6. Main game area framed as an arcade screen, not a generic web panel.
7. Controls aligned to the **left**.
8. Free/empty space remains after the PAUSA control; controls are not centered for symmetry.
9. Action area on the right:
   - SONIDO
   - REINICIAR
10. SONIDO ON uses the canonical **green** treatment.
11. REINICIAR uses the canonical amber/gold treatment.
12. Bottom ranking bar:
   - RANKING GLOBAL
   - TU RÉCORD
   - TU PUESTO
   - JUGADORES
   - RÉCORDS
13. RÉCORDS opens the reusable `ArcadeLeaderboardView`.
14. The values in the compact ranking/achievement area use the canonical Duck Hunt yellow.
15. The generic leaderboard window remains shared between games.

## Naming

Use **NIVEL** consistently between arcade machines.

Internal game logic may use `round`, `wave`, etc., but the user-facing arcade vocabulary is `NIVEL`.

## Visual rules

- Pixel-art is appropriate for the actual game canvas.
- Interface labels outside the game canvas should remain sharp/readable.
- Do not make controls, status cards, or side information look like a generic web dashboard.
- Do not remove the outer Holo/Nitro frame merely because a game window is large.
- Do not change the canonical green sound button without a game-specific reason.
- Do not omit the four cabinet screws.
- Game-specific palettes can change, but **layout language and interaction hierarchy stay consistent**.

## Canonical source

- `xampp/htdocs/nitro-react/src/components/space-invaders/`
- `xampp/htdocs/nitro-react/src/components/duck-hunt/`
- `xampp/htdocs/nitro-react/src/components/arcade/ArcadeLeaderboardView.*`

Before styling a new arcade game, compare against these files first.
