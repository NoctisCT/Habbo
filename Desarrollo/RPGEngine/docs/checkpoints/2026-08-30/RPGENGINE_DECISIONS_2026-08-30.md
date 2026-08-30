# RPGEngine — Decisiones de arquitectura

Checkpoint: 2026-08-30

## Identidad

- Producto: **RPGEngine**.
- No usar HoloRPG.
- “Holo” queda reservado para la plataforma/proyecto global.
- `HoloGrid` puede sobrevivir temporalmente como nombre técnico legacy.

## Autoridad

- RPGEngine vive en servidor y es autoridad de reglas.
- Nitro = UI, preview, configuración y comunicación.
- Grid/Geometry = librería técnica.
- Core Arcturus solo se modifica si los hooks no bastan.

## Config

`Engine defaults → RPG global → room profile opcional → sparse room override → runtime modifiers`

## STAT

Fórmula:

`effective = (base + sum(add)) × product(multiply)`

Movimiento redondea positivos half-up.

Los modificadores deben ser genéricos:

- sourceType/sourceKey
- operation
- value
- scope
- lifetimeMode
- remaining
- encounterId opcional
- equipment instance opcional
- metadata/condición futura

Training/race/passive no son “sistemas permanentes” separados.

## Movement

Defaults:

- directions = 8
- orthogonalCost = 1
- diagonalCost = 1
- allowCornerCutting = false
- maxStepHeight = 1.1
- allowFalling = true
- furniture = authoritative walkability
- unknown furniture blocks = true

Movimiento canónico:

- maximum
- current
- consumed

## Encounter separado de Movement

Encounter maneja lifecycle y participant runtime. Movement lo consume como dependencia.

## Sala física ≠ Encounter

Un participante puede seguir en Encounter mientras atraviesa otras salas para regresar.

Estados:

- ACTIVE
- RETURNING
- DISCONNECTED
- LEFT

Motivos de salida separados:

- fled
- removed
- reconnect-timeout
- return-timeout
- encounter-ended
- etc.

## PvP

Flujo normal:

`click usuario → Retar → startPvP(attacker,target) → ACTIVE`

No hay ChallengeRequest/accept/reject.

## Initiative facts

Encounter conserva:

- initiatorUserId
- creationMode
- joinSequence
- entryType
- joinedAt

Las reglas de Turn evalúan esos facts; no se hardcodea VEL.

## Reserva de casilla — regla definitiva

Si un participante ausente conserva una casilla:

### Usuario fuera de combate
Puede ocuparla. Al rejoin, EncounterEngine lo aparta a un tile válido cercano.

### Usuario del mismo encounter
No puede ocuparla. Debe quedar bloqueada en reachable/pathfinding.

### Usuario de otro encounter en la misma sala
Tampoco puede ocuparla.

Por tanto la reserva es un blocker táctico global por sala para usuarios que están en combate, pero no bloquea el paseo normal.

## Rejoin

Al volver a `combatRoomId`:

1. localizar saved position;
2. apartar ocupantes fuera de combate;
3. no apartar combatientes;
4. teleport del retornante a saved tile;
5. restaurar runtime;
6. ACTIVE;
7. reactivar Movement/grid.

## BHRPG

Servirá como referencia real, nunca como plantilla hardcodeada.
