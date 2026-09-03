# Biribiri Arcade — Manifest

Snapshot generado: 2026-09-03T21:26:17
Branch: `rpg-engine-wip`

## Juegos

| Juego | Estado | Máquina | OPEN | game_key |
| --- | --- | --- | ---: | --- |
| Space Invaders | FINAL 100% · golden master | `arcade_c23_space` | 6100 | `space_invaders` |
| Duck Hunt | FINAL 100% · golden master | `arcade_c23_duckhunt` | 6110 | `duck_hunt` |
| Block Drop | Gameplay final · estética pendiente de adaptación | `arcade_c23_cyberpunk` | 6120 | `block_drop` |
| Pac-Man | Gameplay base · estética + música power pendientes | `tokyo_c18_retroarcade` | 6130 | `pac_man` |

## Arcade Core

Packets compartidos:

- 6101 — start request
- 6102 — started response
- 6103 — score submit
- 6104 — leaderboard
- 6105 — close

Persistencia:

`biribiri_arcade_scores`

## Backend

Archivado desde Desarrollo/SpaceInvaders

## Renderer protocol archivado

- No detectado automáticamente en este checkout.

## Política

Los archivos de `Frontend/` son snapshots exactos del runtime local en el momento de este commit.

Space Invaders y Duck Hunt son los golden masters visuales para adaptar Block Drop y Pac-Man.
