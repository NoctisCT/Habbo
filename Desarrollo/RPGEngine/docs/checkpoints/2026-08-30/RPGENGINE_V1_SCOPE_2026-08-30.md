# RPGEngine V1 — Scope de cierre

Fecha: 2026-08-30  
Rama: `rpg-engine-wip`

## Objetivo

Cerrar una primera versión de RPGEngine útil para comunidades RPG actuales de Habbo sin convertir todavía el motor en un RPG Maker completo.

RPGEngine V1 debe dejar terminado:

- RPG Engine / Core
- Registro de Proyecto
- Asignación de Salas
- Configuración General
- Movimiento
- Retos / Encounter
- Turnos
- Fichas
- Menú RPGEngine dentro del Holo

## Core

- `RPGEngine.jar` como plugin autoritativo del servidor.
- Contexto RPG automático por sala.
- Persistencia en DB.
- Protocolo Nitro ↔ servidor.
- Sincronización.
- Permisos básicos.
- Base común para Project, Room Registry, STAT, Character, Movement, Encounter y Turn.

## Registro de Proyecto

- Crear RPG.
- Listar Mis RPG.
- Propietario.
- Editar datos básicos.
- Resolver contexto.

Estado: base implementada y validada.

## Asignación de Salas

- Añadir sala al RPG.
- Eliminar sala.
- Listar salas.
- Una sala pertenece a un RPG activo a la vez.
- Contexto automático al entrar.
- Config global + sparse room overrides.

Estado: implementado y validado.

## Configuración General

- Datos generales.
- Defaults.
- Opciones de Movement.
- Opciones de Encounter.
- Reconnect grace.
- Return grace.
- Opciones futuras de Turn.

Backend parcial ya existente; UI pendiente.

## Movimiento

V1 debe cerrar:

- grid integrado;
- 4/8 direcciones;
- coste ortogonal/diagonal;
- diagonal por defecto = 1;
- anti-squeeze;
- furniture walkability autoritativa;
- height authority;
- `MAXIMUM_STEP_HEIGHT = 1.1`;
- falling;
- reachable/pathfinding;
- preview/confirm;
- Movement Resource `maximum/current/consumed`;
- fuente fixed o STAT;
- realtime STAT push;
- cambios de maximum preservando ratio de current;
- posición de Encounter;
- reservas;
- reconexión/rejoin;
- server-authoritative movement validation;
- configuración desde menú.

Movement NO se considera terminado aún.

## Retos / Encounter

- PvP unilateral: no aceptar/rechazar.
- `Retar` inicia encounter si las reglas permiten.
- `initiatorUserId`.
- `creationMode`.
- `joinSequence`.
- `entryType`.
- GM puede crear encounters manuales.
- flee/remove/end.
- disconnect/return/reconnect.
- timeouts.
- pertenencia al encounter independiente de la sala física.
- combate físico ligado a `combatRoomId`.
- posición guardada.
- rejoin automático.

## Turn Engine

V1:

- orden de participantes;
- STAT configurable para iniciativa;
- desempates configurables;
- initiator como posible desempate;
- joinSequence;
- turno actual;
- next/previous;
- rondas;
- joins tardíos;
- disconnected/returning;
- reset de Movement al inicio del turno;
- corrección manual por GM.

No requiere ataques ni habilidades.

## Fichas

- Character/Sheet por RPG.
- Datos básicos.
- STATs.
- Valores base.
- Modificadores.
- Fuentes persistentes genéricas.
- Preparación para raza/entrenamiento/pasivas/equipo.
- Estados persistentes preparados.
- Permisos de edición.
- Vista desde Holo.

## Menú en el Holo

Entrada RPG:

- Mis RPG
- Mi ficha / Fichas
- Combate actual

Dentro de RPG:

- General
- Salas
- Fichas
- Estadísticas
- Movimiento
- Combate / Retos
- Turnos
- Permisos
- Debug / Inspector

## Fuera de V1

Se aplaza:

- alcance/targeting;
- ataques;
- daño automático;
- habilidades completas;
- cooldown avanzado;
- tiendas;
- economía avanzada;
- misiones;
- NPC avanzados;
- loot;
- crafting;
- quests;
- IA.

## Bloques de cierre

### Tanda 1 — Core + panel
Project + Rooms + General + permisos + menú + Fichas + STAT UI.

### Tanda 2 — Juego
Movement + Retos/Encounter + Turn Engine + integración con Fichas.

### Tanda 3 — Hardening
Authority final + reconexión + reservas + edge cases + Inspector + QA + UX + limpieza debug.

Al terminar Tanda 3: **RPGEngine V1 terminada**.
