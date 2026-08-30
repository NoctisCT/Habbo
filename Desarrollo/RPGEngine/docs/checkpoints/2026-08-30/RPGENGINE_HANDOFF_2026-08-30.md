# RPGEngine — Handoff

Checkpoint: 2026-08-30  
Repo: `NoctisCT/Habbo`  
Branch: `rpg-engine-wip`  
WIP remoto previo conocido: `04640042103ee2206d94cbff74f8b760873023ee`

## Arquitectura

Servidor:
- `Desarrollo/RPGEngine`
- package `com.retro.rpgengine`
- `Emulator/plugins/RPGEngine.jar`

Cliente:
- `src/api/rpg/engine`
- `src/api/rpg/grid`
- renderer protocol/parser `rpgengine`
- `globalThis.RPGEngine` para debug/transición.

Authority grid legacy:
- `Desarrollo/HoloGrid`
- `Emulator/plugins/HoloGrid.jar`
- request 5040 / response 5041.

## Project/Rooms

Validado:

- project id 1 `Pokemon RPG`
- owner user 5
- room 203 registrada
- context auto al entrar
- context null al salir
- global inheritance
- sparse room override

## Movement

Validado:

- grid táctico
- terrain/furniture/holes
- authoritative furniture
- authoritative heights
- max step 1.1
- falling
- diagonal squeeze
- path/reachable
- Movement Session
- spend por pasos
- fixed/stat Movement
- realtime STAT push
- cross-client
- offline persistence
- ratio-preserving max changes

Pendiente:
- server-authoritative movement final
- reservas integradas en producción
- UI
- hardening

## STAT

DB:
- `rpg_engine_stats`
- `rpg_engine_player_stats`
- `rpg_engine_stat_modifiers`

STAT id 1:
- key VEL
- default 4

Push realtime = action 18.

## Encounter V1 validado

- create/start/get/mine
- add participant
- RETURNING/ACTIVE/DISCONNECTED manual
- room-independent membership
- flee
- end
- Nitro cache hotfix para `encounterMine -> null`

Encounter 1:
- user 6 terminó `left/fled`
- user 5 terminó `left/null`
- V2 debe corregir ese null a `encounter-ended`

## Encounter V2 actual

Añadido:
- PvP directo
- initiatorUserId
- creationMode
- joinSequence
- entryType
- saved combat position
- reservations
- reconnect/return hooks
- rejoin
- occupant displacement
- cross-encounter room reservation
- timeout runtime

Primera prueba V2:

`RPGEngine.encounterPvP(6)`

Resultado:
- action 33
- success true
- message `pvp-started`
- push action 32
- encounter id 2
- rpgId 1
- roomId 203
- createdByUserId 5
- initiatorUserId 5
- creationMode `pvp`
- status `active`
- 2 participants
- reservations []

## Próxima prueba exacta

1. `RPGEngine.state().encounter.participants`
   - validar joinSequence
   - entryType
   - saved position

2. Desconectar user 6 físicamente.

3. Desde user 5:
   - participant = disconnected
   - reservation aparece
   - saved tile bloqueada para Movement

4. Reconectar user 6:
   - fuera de combatRoomId = RETURNING
   - puede atravesar salas normales
   - al entrar room 203 = rejoin

5. Rejoin sin ocupante:
   - teleport automático a saved tile

6. Rejoin con tercero fuera de combate sobre saved tile:
   - tercero es apartado
   - user 6 vuelve a saved tile

7. Otro Encounter en misma sala:
   - sus combatientes tampoco pueden entrar en la reserva

8. End:
   - activos → `left/encounter-ended`
   - quienes ya huyeron conservan `fled`

## Cambio de ritmo aprobado

A partir de aquí trabajar en bloques grandes:

### A — Core + Holo Menu + Fichas
### B — Movement + Retos + Turnos
### C — QA / cierre V1

No abrir todavía targeting, attacks, abilities, shops, missions, economy avanzada, NPC avanzados, loot/crafting.
