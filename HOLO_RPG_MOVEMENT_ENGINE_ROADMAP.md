# Holo RPG Engine — Movement Engine: arquitectura, roadmap y handoff técnico

**Estado de referencia:** 29 de agosto de 2026  
**Proyecto:** Holo RPG Engine / Hobba  
**Objetivo del documento:** servir como documentación viva entre sesiones para continuar el desarrollo sin reconstruir decisiones, pruebas y arquitectura desde cero.

---

## 0. Visión del sistema

Holo RPG Engine no debe ser un único RPG predefinido. Debe ser una **plataforma configurable para crear RPGs dentro de Hobba**, donde cada comunidad pueda definir sus propias reglas, nombres de recursos, salas, parámetros de movimiento, estilos visuales y, en el futuro, otros sistemas como combate, estadísticas, inventario, habilidades, estados, etc.

Por tanto, el Movement Engine no puede limitarse a “pintar un grid y calcular casillas”. Debe terminar siendo un subsistema completo con:

- reglas configurables;
- persistencia;
- asociación entre RPGs y salas;
- estado por jugador;
- validación autoritativa en servidor;
- menús integrados en Nitro;
- sincronización multijugador;
- soporte para overrides;
- debugging;
- hooks para otros sistemas.

El objetivo es que, cuando el sistema esté terminado, un creador de RPG pueda montar un proyecto desde interfaz gráfica sin tocar consola ni código.

---

# 1. Estado actual del Movement Engine

## 1.1. Núcleo geométrico ya implementado

El sistema ya dispone de un grid táctico integrado directamente en Nitro.

Componentes principales:

- `GridEngine`
- `GridGeometry`
- `GridMap`
- `GridMovementPolicy`
- `GridNitroAdapter`
- integración con `RoomPlane`
- capas visuales del grid

Capas visuales actuales:

- movement
- path
- target
- selected
- blocked

El grid puede seguir al jugador y recalcular el área alcanzable conforme cambia su posición.

---

## 1.2. Direcciones y costes

El motor soporta:

- movimiento en 4 direcciones;
- movimiento en 8 direcciones;
- coste ortogonal configurable;
- coste diagonal configurable;
- control de paso diagonal entre obstáculos.

Decisión actual:

- coste diagonal por defecto: **1**
- corner cutting por defecto: desactivado
- una diagonal queda bloqueada únicamente cuando los **dos laterales ortogonales** impiden el paso.

Esto evita que un obstáculo aislado cree una sombra artificial detrás de él.

Ejemplo correcto:

- diagonal = 1 → el área puede rodear un pato/obstáculo aislado;
- diagonal = 2 → el área retrocede de forma natural porque la diagonal cuesta más.

---

## 1.3. Walkability autoritativa de furnis

Se añadió comunicación Nitro ↔ Arcturus mediante el plugin `HoloGrid.jar`.

El servidor responde con la walkability real de cada furni.

Esto solucionó un problema importante: algunos furnis custom aparecían como pisables según los metadatos del cliente, aunque Arcturus realmente impedía caminar sobre ellos.

La prioridad actual es:

1. datos autoritativos del servidor;
2. metadata del cliente como fallback.

La información de debug expone:

- `allowWalk`
- `allowSit`
- `allowLay`
- `walkabilitySource`
- `blocks`

Esto está validado con furnis custom, pato, Pokémon y WIRED.

---

## 1.4. Height Model v1 validado

El servidor envía la altura dinámica real del tile utilizando `RoomTile.getStackHeight()`.

El movimiento compara la diferencia de altura **entre dos pasos consecutivos**, no la altura absoluta del destino.

Regla validada:

- `maxStepHeight = 1.1`
- `allowFalling = true`

Prueba real realizada:

- suelo → dos WIREDs apilados:
  - `0 → 1.3`
  - delta: `1.3`
  - resultado: **no permitido**

- un WIRED → dos WIREDs:
  - `0.65 → 1.3`
  - delta: `0.65`
  - resultado: **permitido**

Esto confirma la lógica correcta:

> No puedes saltar directamente de 0 a una altura superior al límite, pero sí puedes subir progresivamente por una escalera 0 → 1 → 2.

También demuestra que se utilizan alturas reales de Arcturus, no niveles artificiales.

---

## 1.5. Presupuesto de movimiento

El runtime actual ya puede:

- asignar presupuesto de movimiento;
- calcular rutas;
- calcular coste;
- mostrar área alcanzable;
- seleccionar ruta;
- confirmar movimiento;
- gastar movimiento;
- recalcular el área desde la nueva posición;
- reducir correctamente el presupuesto restante.

Prueba validada:

> Si empiezas con 4 puntos, gastas 2 y terminas en otra casilla, el área se vuelve a calcular desde la nueva posición con 2 puntos restantes.

Cuando el presupuesto llega a 0:

- desaparece el área de movimiento;
- el usuario ya no puede continuar moviéndose.

Ese comportamiento es correcto para un RPG por turnos. El futuro Turn Engine será quien decida cuándo vuelve a cargarse el recurso.

---

## 1.6. Selección y confirmación actuales

El comportamiento actual utiliza dos clics:

1. primer clic → preview de ruta;
2. segundo clic → confirmación y ejecución.

Esta modalidad encaja bien con RPG táctico y debe mantenerse como opción predeterminada.

En el futuro debe ser configurable:

- `confirm`
- `instant`

Antes de confirmar, debe poder cambiarse o cancelarse el destino sin gastar recurso.

---

## 1.7. Bug corregido de `GridGeometry.key`

Se detectó:

`Cannot read properties of undefined (reading 'normalize')`

La causa era utilizar `GridGeometry.key` como callback directo de `.map()` cuando internamente dependía de `this.normalize()`.

Se corrigió para utilizar:

`GridGeometry.normalize(...)`

de forma explícita.

El error ya no aparece.

---

# 2. Decisión arquitectónica principal: el RPG es la unidad raíz

No debe configurarse cada sala como si fuese un sistema aislado.

La unidad principal del sistema debe ser el **RPG Project**.

Ejemplo:

## Pokémon RPG

- General
- Movimiento
- Salas
- Apariencia
- Permisos
- Jugadores
- Reglas
- futuros sistemas

Un RPG puede contener muchas salas:

- Pueblo Paleta
- Ruta 1
- Bosque Verde
- Ciudad Plateada
- Cueva Celeste
- etc.

Cuando se crea una sala nueva, se añade al proyecto y **hereda automáticamente su configuración global**.

---

# 3. Room Registry

Debe existir un registro autoritativo que relacione salas con RPGs.

Modelo conceptual:

`room_id → rpg_id`

Decisión inicial:

> Una sala puede pertenecer como máximo a **un RPG activo**.

No se recomienda que la misma sala pertenezca simultáneamente a varios RPGs porque aparecerían conflictos sobre:

- qué reglas están activas;
- qué jugadores pertenecen a qué RPG;
- qué presupuesto aplicar;
- qué grid mostrar;
- qué sistemas están activos;
- qué combate o turno es válido.

Una sala podrá reasignarse a otro RPG si el administrador lo desea.

---

# 4. Separación fundamental de responsabilidades

El Movement Engine debe separar tres conceptos.

## 4.1. Movement Rules

Define cómo funciona el movimiento en ese RPG.

Ejemplos:

- nombre del recurso;
- movimiento base;
- direcciones;
- coste ortogonal;
- coste diagonal;
- corner cutting;
- altura máxima;
- caídas;
- obstáculos;
- confirmación;
- costes por terreno;
- restricciones.

Estas reglas son configuración.

---

## 4.2. Movement State

Representa el estado actual de un jugador.

Ejemplos:

- máximo: 4;
- restante: 2;
- ruta seleccionada;
- moviéndose;
- agotado;
- bloqueado;
- modificadores temporales;
- turno activo.

Este estado es runtime.

---

## 4.3. Room Map State

Representa la realidad actual de la sala.

Ejemplos:

- tiles;
- alturas;
- furnis;
- holes;
- obstáculos;
- usuarios;
- objetos dinámicos;
- cambios recientes.

Esto no debe confundirse ni con reglas ni con estado del jugador.

---

# 5. Herencia de configuración

La configuración efectiva debe resolverse por capas.

Orden propuesto:

1. **Engine Defaults**
2. **RPG Global Configuration**
3. **Room Profile** opcional
4. **Room Overrides**
5. **Runtime Player Modifiers**

Conceptualmente:

`defaults → RPG → profile → room → runtime`

---

## 5.1. Engine Defaults

Valores de seguridad usados cuando el RPG no establece una regla.

Ejemplos iniciales:

- 8 direcciones;
- coste ortogonal 1;
- coste diagonal 1;
- `maxStepHeight = 1.1`;
- `allowFalling = true`;
- confirmación de movimiento activada.

---

## 5.2. RPG Global Configuration

Reglas generales del proyecto.

Ejemplo:

Pokémon RPG:

- recurso: `VEL`
- base: 4
- diagonales: sí
- diagonal cost: 1
- step height: 1.1

Todas las salas lo heredan.

---

## 5.3. Room Profiles

No es obligatorio implementarlo en la primera versión, pero la arquitectura debe permitirlo.

Un perfil permite compartir overrides entre muchas salas.

Ejemplos:

### Normal
Reglas globales.

### Mazmorra
- diagonales desactivadas;
- costes especiales;
- quizá movimiento reducido.

### Pantano
- algunos tiles cuestan más.

### Hielo
- futuras reglas de deslizamiento.

Esto evita configurar veinte salas una por una.

---

## 5.4. Room Overrides

Una sala puede modificar únicamente lo necesario.

Ejemplo global:

- movimiento: 4
- diagonales: sí
- maxStepHeight: 1.1

Cueva Celeste:

- `diagonals = false`

Nada más.

La sala sigue heredando automáticamente movimiento 4 y altura 1.1.

---

## 5.5. Los overrides deben ser parciales

Nunca debe copiarse toda la configuración global a cada sala.

Si una sala solo cambia diagonales, se guarda solo:

`diagonals = false`

Si mañana el administrador cambia el movimiento global:

`4 → 5`

esa sala pasa automáticamente a 5.

Esto evita configuraciones duplicadas y desincronizadas.

---

## 5.6. Runtime Player Modifiers

La última capa procede del estado del personaje.

Ejemplos futuros:

- +2 VEL por buff;
- -1 movimiento por lesión;
- root → 0;
- terreno ignorado por una habilidad;
- movimiento gratis;
- estado de vuelo;
- penalización temporal.

No modifica la configuración persistente.

---

# 6. Configuración efectiva e inspector

El servidor debe poder generar una **Effective Movement Configuration**.

Ejemplo:

- movimiento: 5
- diagonales: false
- coste diagonal: 1
- altura máxima: 1.1
- caída: true

Además debe conocer el origen de cada valor.

Ejemplo de inspector:

- Movimiento: `5` — origen: Pokémon RPG
- Diagonales: `false` — origen: Cueva Celeste
- Max Step Height: `1.1` — origen: Engine Default
- Bonus temporal: `+2` — origen: personaje

Este inspector será muy importante para debugging y para administradores.

---

# 7. Menús integrados en Nitro

El sistema final no debe depender de consola.

La estructura conceptual sería:

## Holo RPG

- Mis RPG
- Crear RPG

Al abrir un proyecto:

## Pokémon RPG

- General
- Movimiento
- Salas
- Apariencia
- Permisos
- Jugadores
- Sistemas
- Debug / Inspector

---

## 7.1. Menú Movimiento

### Recurso

- nombre visible:
  - VEL
  - PM
  - Movimiento
  - AP
  - cualquier texto definido por el creador

- valor base

El motor internamente debe trabajar con un concepto genérico de presupuesto. No debe asumir que siempre se llama “PM”.

---

### Direcciones

- 4 / 8
- coste recto
- coste diagonal
- paso entre esquinas

---

### Altura

- altura máxima escalable
- permitir caídas grandes

---

### Control

- preview antes de mover
- segundo clic para confirmar
- movimiento instantáneo
- posibilidad de cancelar
- posibilidad de cambiar destino

---

### Visual

- mostrar área;
- mostrar ruta;
- mostrar destino;
- mostrar obstáculos;
- mostrar coste;
- estilos;
- colores;
- opacidad;
- futuras animaciones.

---

# 8. Gestión de salas

Dentro de `RPG → Salas`:

- Añadir sala actual
- Buscar/añadir otra sala si los permisos lo permiten
- Eliminar sala del RPG
- Reasignar sala
- Ver configuración efectiva
- Seleccionar perfil
- Activar overrides

Ejemplo de tabla:

| Sala | Configuración |
|---|---|
| Pueblo Paleta | Global |
| Ruta 1 | Global |
| Bosque Verde | Perfil: Bosque |
| Cueva Celeste | Override |

También debe existir acceso rápido desde una sala:

`Holo RPG → Configurar sala actual`

Pero esa interfaz simplemente edita la configuración de esa sala dentro de su proyecto.

---

# 9. Runtime al entrar en una sala RPG

Flujo objetivo:

1. el usuario entra en una sala;
2. Arcturus obtiene `room_id`;
3. Room Registry comprueba si pertenece a un RPG;
4. carga el RPG;
5. carga sus reglas globales;
6. carga perfil de la sala;
7. carga overrides;
8. resuelve configuración efectiva;
9. carga el estado del jugador;
10. aplica modificadores runtime;
11. envía configuración a Nitro;
12. envía mapa autoritativo;
13. HoloGrid se inicializa automáticamente.

En el sistema final no debería ser necesario ejecutar manualmente:

`HoloGrid.follow(4)`

El grid debe activarse automáticamente cuando el contexto RPG diga que corresponde.

---

# 10. Separación entre Movement Engine y Range/Targeting

El sistema de ataque **NO debe construirse dentro del Movement Engine**.

Cuando llegue el momento, habrá otro subsistema:

`Range / Targeting Engine`

Pero reutilizará componentes comunes:

- `GridGeometry`
- mapas;
- alturas;
- tiles;
- distancias;
- obstáculos;
- line of sight futura.

Movimiento y alcance tienen reglas diferentes.

Ejemplo:

Un ataque podría:

- atravesar obstáculos;
- ignorar altura;
- usar Manhattan;
- usar Chebyshev;
- usar diamante;
- usar cono;
- usar línea;
- requerir LOS;
- afectar áreas.

Por ello no debe heredar automáticamente las reglas de caminar.

**No empezar Range/Targeting hasta que Movement Engine esté realmente completo.**

---

# 11. Roadmap pendiente del Movement Engine

A continuación están los **11 bloques principales que faltan**.

No significa que cada bloque tenga el mismo tamaño. Algunos probablemente se dividirán en varias fases durante el desarrollo.

---

## Paso 1 — RPG Project + Room Registry + Movement Configuration Resolver

### Objetivo

Crear la estructura base que convierte HoloGrid en parte de un RPG real.

### Debe incluir

- entidad RPG;
- creación de RPG;
- identificador;
- nombre;
- owner;
- estado;
- Room Registry;
- relación `room_id → rpg_id`;
- configuración global de movimiento;
- overrides por sala;
- arquitectura preparada para perfiles;
- resolución de configuración efectiva.

### Resultado esperado

El servidor debe poder responder conceptualmente:

> La sala 123 pertenece a Pokémon RPG y su configuración efectiva de movimiento es X.

### Prioridad

**Siguiente bloque a desarrollar.**

---

## Paso 2 — Persistencia autoritativa de configuración

### Objetivo

Eliminar dependencia de configuración temporal en consola.

### Debe persistirse

- RPGs;
- salas asociadas;
- reglas globales;
- overrides;
- perfiles cuando existan;
- permisos;
- futuras preferencias visuales.

### Principio

Nitro no es la fuente de verdad.

La fuente de verdad debe ser el servidor / base de datos.

---

## Paso 3 — Menús integrados de configuración

### Objetivo

Permitir que un creador configure el RPG desde Nitro.

### Primera versión

- crear RPG;
- editar datos básicos;
- añadir sala actual;
- eliminar sala;
- editar movimiento global;
- editar override de sala;
- ver configuración efectiva.

### Después

- perfiles;
- apariencia;
- permisos;
- herramientas avanzadas.

### Regla

Nada esencial debe requerir consola para un usuario final.

---

## Paso 4 — Sistema de permisos del RPG

### Objetivo

Evitar que cualquier usuario modifique el sistema.

Roles conceptuales:

- Owner
- Admin
- GM
- Builder / Configurator
- Player
- quizá Observer

Permisos independientes futuros:

- editar RPG;
- editar movimiento;
- añadir salas;
- modificar sala;
- iniciar combate;
- modificar jugadores;
- usar debug.

No es necesario cerrar todos los roles desde el primer día, pero sí diseñar el sistema como permisos y no como simples checks dispersos.

---

## Paso 5 — Player Movement State + integración futura con turnos

### Objetivo

Convertir el presupuesto de movimiento actual en estado formal por jugador.

Estado mínimo:

- movementMax;
- movementRemaining;
- movementSpent;
- phase/state;
- selectedDestination;
- selectedPath;
- selectedCost;
- moving;
- exhausted;
- movementLocked.

Estados conceptuales:

- `idle`
- `preview`
- `moving`
- `ready`
- `exhausted`
- `locked`

Hooks que debe poder usar un futuro Turn Engine:

- startMovementTurn
- resetMovement
- grantMovement
- spendMovement
- lockMovement
- unlockMovement
- endMovementTurn

El Turn Engine será quien decida cuándo recargar el recurso.

Movement Engine solo debe ofrecer las operaciones.

---

## Paso 6 — Selección, preview, confirmación y UX completa

### Objetivo

Cerrar la interacción táctica.

Debe contemplar:

- clic en casilla válida;
- preview de ruta;
- coste;
- segundo clic confirma;
- clic en otra casilla cambia selección;
- cancelar;
- destino inválido;
- coste superior al restante;
- ruta inexistente;
- movimiento bloqueado;
- feedback visual;
- limpiar selección tras movimiento;
- impedir spam de clics mientras se está moviendo.

Configuración:

- confirm mode;
- instant mode.

La previsualización nunca debe gastar movimiento.

---

## Paso 7 — Validación server-authoritative del movimiento

### Objetivo

No confiar en Nitro para decisiones de juego.

Flujo final deseado:

### Cliente

> Quiero moverme a este destino.

### Servidor

Valida:

- RPG;
- sala;
- jugador;
- estado;
- presupuesto;
- ruta;
- altura;
- walkability;
- obstáculos;
- costes;
- reglas;
- modificadores;
- permisos para mover.

Después:

- acepta/rechaza;
- calcula coste real;
- descuenta el recurso;
- ejecuta o autoriza movimiento;
- devuelve estado final.

Nitro debe utilizar sus cálculos para **preview**, pero el servidor decide.

---

## Paso 8 — Sincronización dinámica del mapa y multijugador

### Objetivo

Que el mapa no sea una foto estática.

Debe reaccionar a:

- furni movido;
- furni colocado;
- furni retirado;
- furni apilado;
- cambio de altura;
- cambio de walkability;
- puertas;
- blockers temporales;
- otros jugadores si las reglas los consideran obstáculos;
- cambios durante una ruta.

Preguntas que habrá que definir:

- ¿los jugadores bloquean tiles?
- ¿puedes atravesar aliados?
- ¿enemigos?
- ¿puedes terminar en tile ocupado?
- ¿qué ocurre si alguien ocupa tu destino durante tu movimiento?

Debe existir política configurable.

---

## Paso 9 — Costes avanzados, terreno y modificadores

### Objetivo

Pasar del coste uniforme a reglas RPG reales.

Soporte futuro:

- coste por tile;
- terreno difícil;
- zonas lentas;
- zonas gratuitas;
- coste de entrada;
- coste de salida;
- coste por dirección;
- coste por altura;
- modificadores de personaje;
- buffs;
- debuffs;
- estados.

Ejemplos:

- hierba = 1;
- barro = 2;
- agua = 3;
- personaje acuático ignora coste de agua.

El core geométrico debe calcular siempre el camino de menor coste según reglas efectivas.

---

## Paso 10 — Movimientos especiales e interrupciones

### Objetivo

Cubrir casos que un RPG real necesitará y que no deben convertirse en hacks.

Casos:

- movimiento forzado;
- empuje;
- tirón;
- dash;
- teleport;
- salto especial;
- desplazamiento sin consumir recurso;
- desplazamiento con coste fijo;
- root;
- stun;
- cancelación;
- interrupción en mitad de ruta;
- destino ocupado durante movimiento;
- caída;
- cambio de sala;
- reconexión;
- jugador desconectado durante su movimiento.

El motor debe distinguir entre:

- movimiento voluntario;
- movimiento forzado;
- teletransporte.

No todas las reglas del movement budget deben aplicarse a los tres.

---

## Paso 11 — Debugging, inspector, QA y cierre de producto

### Objetivo

Hacer que el sistema sea mantenible.

Herramientas deseables:

- inspector de configuración efectiva;
- origen de cada regla;
- inspector de tile;
- altura autoritativa;
- walkability;
- coste;
- reason de bloqueo;
- ruta calculada;
- presupuesto;
- estado del jugador;
- sincronización cliente/servidor.

Ejemplos de razones de rechazo:

- `blocked-furniture`
- `step-too-high`
- `insufficient-movement`
- `occupied`
- `movement-locked`
- `invalid-room`
- `not-in-rpg`
- `path-invalidated`

También debe incluir:

- tests geométricos;
- pruebas de regresión;
- pruebas con diagonales;
- alturas;
- stacks;
- terreno;
- multijugador;
- reconexión;
- cambio dinámico de mapa;
- errores de protocolo;
- estados inválidos.

Cuando este bloque esté cerrado y la UX configurada desde menús, podrá considerarse Movement Engine realmente completo.

---

# 12. Modelo conceptual inicial de datos

No es un esquema SQL definitivo. Sirve para orientar la arquitectura.

## RPG

Campos conceptuales:

- id
- name
- slug opcional
- ownerUserId
- enabled
- createdAt
- updatedAt

---

## RPG Member / Permission

- rpgId
- userId
- role
- permissions

---

## RPG Room

- rpgId
- roomId
- profileId nullable
- enabled
- overrides JSON / estructura equivalente

Restricción:

`roomId` único entre RPGs activos.

---

## RPG Movement Configuration

Configuración global:

- resourceName
- baseMovement
- directions
- orthogonalCost
- diagonalCost
- allowCornerCutting
- maxStepHeight
- allowFalling
- furniturePolicy
- unknownFurnitureBlocks
- confirmationMode
- visual settings futuros

---

## Room Profile

Futuro:

- id
- rpgId
- name
- movementOverrides

---

## Player Movement State

Runtime, no necesariamente persistido completamente:

- rpgId
- roomId
- userId
- max
- remaining
- spent
- state
- locked
- selectedPath
- selectedCost
- revision

---

# 13. Eventos/API que debería exponer el Movement Engine

Diseñar el motor con una API clara permitirá que otros sistemas lo utilicen sin modificar internals.

Conceptos futuros:

- `getEffectiveRules`
- `getMovementState`
- `startMovement`
- `previewMovement`
- `confirmMovement`
- `cancelMovement`
- `spendMovement`
- `grantMovement`
- `resetMovement`
- `lockMovement`
- `unlockMovement`
- `forceMove`
- `teleport`
- `invalidatePath`
- `refreshMap`

Eventos:

- movement.previewed
- movement.cancelled
- movement.started
- movement.step
- movement.completed
- movement.interrupted
- movement.rejected
- movement.exhausted
- movement.reset
- movement.rulesChanged
- room.mapChanged

Los nombres definitivos pueden cambiar.

Lo importante es que otros sistemas consuman eventos y APIs en vez de acoplarse directamente al grid.

---

# 14. Principios de diseño que no deben romperse

## 14.1. Server authoritative

El cliente previsualiza. El servidor decide.

---

## 14.2. Configuración global con overrides parciales

Nunca duplicar reglas completas por sala salvo necesidad real.

---

## 14.3. RPG como raíz

Las salas pertenecen al RPG; el RPG no es una configuración pegada a cada sala.

---

## 14.4. Recurso genérico

El motor no debe asumir “PM” o “VEL”.

Debe manejar un presupuesto abstracto y dejar que cada RPG defina el nombre visible.

---

## 14.5. Geometría reutilizable

`GridGeometry` debe ser una librería común.

Movement Engine utiliza geometría, pero no debe monopolizarla.

---

## 14.6. Movimiento y ataque separados

Range/Targeting llegará más adelante y reutilizará el core geométrico.

---

## 14.7. Estado separado de reglas

No guardar “remaining = 2” dentro de la configuración del RPG.

---

## 14.8. Alturas relativas por paso

Nunca bloquear un tile únicamente por su altura absoluta.

La regla es transición:

`height(to) - height(from)`

---

## 14.9. Runtime observable

Debe poder explicarse por qué un movimiento se acepta o rechaza.

---

## 14.10. Evitar lógica específica de un RPG

Holo debe proporcionar primitivas.

Cada RPG configura qué significan.

---

# 15. Próximo bloque de trabajo

La siguiente fase acordada es:

# RPG Project + Room Registry + Movement Configuration Resolver

Orden recomendado:

1. definir modelos servidor;
2. crear persistencia mínima;
3. crear RPG;
4. asociar sala;
5. guardar configuración global;
6. guardar override parcial;
7. resolver configuración efectiva;
8. exponerla por protocolo a Nitro;
9. añadir API de debug temporal;
10. probar dos salas:
    - una global;
    - una con override;
11. comprobar que un cambio global se propaga a la sala sin override.

Todavía no hace falta crear el menú final.

Primero debe existir un backend correcto.

---

# 16. Criterio de éxito de la próxima fase

La fase estará validada cuando pueda hacerse algo equivalente a:

### RPG

Pokémon RPG:

- movement = 4
- diagonals = true
- maxStepHeight = 1.1

### Sala A

Configuración: global.

Resultado efectivo:

- movement = 4
- diagonals = true
- maxStepHeight = 1.1

### Sala B

Override:

- diagonals = false

Resultado efectivo:

- movement = 4
- diagonals = false
- maxStepHeight = 1.1

Después se cambia globalmente:

- movement = 6

Sin tocar ninguna sala:

### Sala A

- movement = 6

### Sala B

- movement = 6
- diagonals = false

Cuando eso funcione de forma persistente y autoritativa, tendremos el fundamento correcto para construir los menús y conectar el runtime de movimiento.

---

# 17. Estado resumido para futuras sesiones

Si una futura sesión solo puede leer una sección, debe leer esta.

### Ya funciona

- grid táctico integrado en Nitro;
- seguimiento del jugador;
- reachable;
- pathfinding;
- coste ortogonal/diagonal;
- diagonal cost 1;
- obstáculos;
- diagonal alrededor de obstáculo aislado;
- furnis con walkability autoritativa desde Arcturus;
- alturas autoritativas;
- Height Model v1;
- límite 1.1;
- escaleras por pasos;
- preview de ruta;
- confirmación con segundo clic;
- gasto de movimiento;
- presupuesto restante;
- recalculado del área tras moverse;
- agotamiento a 0;
- bug de `GridGeometry.key` corregido.

### No está terminado

El Movement Engine completo todavía necesita los 11 bloques descritos anteriormente.

### Próxima tarea

**Implementar RPG Project + Room Registry + Movement Configuration Resolver.**

### Decisiones fijadas

- RPG es unidad principal;
- sala pertenece a un RPG activo;
- configuración global;
- overrides parciales;
- perfiles en arquitectura futura;
- reglas / player state / room map separados;
- server authoritative;
- recurso de movimiento genérico;
- ataques fuera del Movement Engine;
- menús integrados vendrán después del backend;
- Movement Engine debe llegar al 100% antes de trabajar en Range/Targeting.

---

# 18. Nota de mantenimiento de este documento

Este archivo debe actualizarse conforme avance el desarrollo.

Cuando una fase quede completada:

1. actualizar `Estado actual`;
2. marcar el paso correspondiente como completado;
3. anotar decisiones nuevas;
4. registrar bugs relevantes y su solución;
5. modificar el próximo bloque de trabajo;
6. evitar borrar contexto histórico importante si puede afectar futuras decisiones.

Este documento debe actuar como:

- roadmap;
- arquitectura;
- handoff entre sesiones;
- registro de decisiones del Movement Engine.

No sustituye a un changelog detallado ni a documentación API futura, pero debe mantener suficiente contexto para que una nueva sesión pueda continuar el proyecto sin reconstruirlo desde cero.
