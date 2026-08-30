# RPGEngine — Changelog checkpoint 2026-08-30

## Hecho

### Identidad
- RPGEngine como nombre oficial.
- package Java `com.retro.rpgengine`.
- API `globalThis.RPGEngine`.

### Project / Rooms
- Project base.
- Room Registry.
- context automático.
- config inheritance.
- sparse overrides.

### Movement
- grid táctico.
- furniture authority.
- height authority.
- falling.
- diagonal cost = 1.
- anti-squeeze.
- pathfinding/reachable.
- Movement Session.
- Movement Resource V2.
- fixed/stat source.

### STAT
- stats.
- bases.
- modifiers.
- realtime push.
- cross-client.
- offline persistence.

### Encounter V1
- config.
- create/start/get/mine.
- participants.
- disconnected/returning/active.
- flee/remove/end.
- room-independent membership.
- Nitro cache hotfix.

### Encounter V2
- PvP directo.
- initiator.
- creationMode.
- joinSequence.
- entryType.
- saved position.
- reservations.
- reconnect/return.
- rejoin.
- occupant displacement.
- cross-encounter reservations.
- timeouts.

### Última validación
`RPGEngine.encounterPvP(6)` creó encounter 2 ACTIVE con initiator user 5 y creationMode `pvp`.

## Scope congelado V1
Core + Project + Rooms + General + Movement + Retos + Turnos + Fichas + Holo Menu.

Targeting/abilities/shops/missions y demás quedan para después.
