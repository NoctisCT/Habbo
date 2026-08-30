# WIRED Custom — handoff operativo y referencia técnica

**Estado:** referencia operativa consolidada para futuras sesiones  
**Fecha de consolidación:** 28/08/2026  
**Entorno validado:** Arcturus Morningstar 3.6.1 + Nitro React 2.1.1 + Nitro Renderer 1.6.6 + MariaDB/MySQL  
**Java target:** 16  
**Raíz autoritativa:** `C:\Users\erale\Desktop\Habbo`

> Este documento existe para que una sesión nueva pueda continuar creando o modificando WIRED custom **sin repetir la investigación de entorno, JAR, parser, paquetes, persistencia, assets, lifecycle y UI**.
>
> Si este archivo se entrega al inicio de una nueva sesión, **leerlo primero y reutilizar lo ya validado**. No volver a escanear todo el proyecto ni a redescubrir el protocolo salvo que haya cambiado el JAR, Nitro o el esquema de datos.

---

# 0. LEER PRIMERO EN UNA NUEVA SESIÓN

## 0.1 Regla de autoridad

La autoridad técnica, por este orden, es:

```text
1. JAR local real del emulador.
2. Código Nitro local real.
3. Base de datos local real.
4. Este documento, mientras las versiones anteriores no hayan cambiado.
5. Plugins/forks/código público solo como referencia.
```

No asumir que un plugin de terceros, una clase de otro fork o documentación pública coincide exactamente con este Morningstar/Nitro.

## 0.2 No volver a investigar lo ya cerrado

Mientras sigan siendo válidas estas versiones:

```text
Arcturus Morningstar 3.6.1
Nitro React 2.1.1
Nitro Renderer 1.6.6
Java 16
```

no hace falta redescubrir:

```text
- estructura del packet de Wired Action
- parser de Nitro
- stuffTypeSelectionCode
- conflictingTriggers count
- Layout Codes 88/89/90 ya usados
- useWired/stringParam
- wired_data vs extradata
- registro ItemInteraction
- ClientMessage.clone()
- cancelación de packets
- formato básico de un .nitro
- ruta de FurnitureData
- pipeline de build/deploy
```

Solo volver a validar estas piezas si cambia alguna versión relevante.

## 0.3 Forma de trabajo recomendada

Para modificaciones futuras:

```text
- trabajar sobre C:\Users\erale\Desktop\Habbo
- no usar la copia secundaria Habbo 2
- cambios pequeños y verificables
- backup con timestamp antes de sobrescribir
- UTF-8 sin BOM
- una capa cada vez: backend -> Nitro -> DB/assets -> QA
- no tocar lógica ya validada para corregir un problema puramente visual
```

---

# 1. ENTORNO LOCAL AUTORITATIVO

## 1.1 Emulador

JAR real:

```text
C:\Users\erale\Desktop\Habbo\Emulator\Habbo-3.6.0-jar-with-dependencies.jar
```

La consola identifica:

```text
Arcturus Morningstar 3.6.1
```

Plugins activos:

```text
C:\Users\erale\Desktop\Habbo\Emulator\plugins
```

Plugins desactivados:

```text
C:\Users\erale\Desktop\Habbo\Emulator\plugins-disabled
```

Plugin AvatarSync desplegado:

```text
C:\Users\erale\Desktop\Habbo\Emulator\plugins\AvatarSync.jar
```

## 1.2 Proyecto Maven de AvatarSync

Proyecto:

```text
C:\Users\erale\Desktop\Habbo\avatar-sync-mvp
```

Dependencia local exacta de Arcturus:

```text
C:\Users\erale\Desktop\Habbo\avatar-sync-mvp\lib\Arcturus.jar
```

Fuentes principales:

```text
C:\Users\erale\Desktop\Habbo\avatar-sync-mvp\src\main\java\com\neah\avatarsync\AvatarSyncPlugin.java

C:\Users\erale\Desktop\Habbo\avatar-sync-mvp\src\main\java\com\neah\avatarsync\WiredEffectAvatarSync.java

C:\Users\erale\Desktop\Habbo\avatar-sync-mvp\src\main\java\com\neah\avatarsync\WiredEffectStopAvatarSync.java
```

Build:

```powershell
cd "C:\Users\erale\Desktop\Habbo\avatar-sync-mvp"
mvn clean package
```

JAR generado:

```text
C:\Users\erale\Desktop\Habbo\avatar-sync-mvp\target\avatar-sync-0.1.0.jar
```

Deploy:

```powershell
Copy-Item `
    "C:\Users\erale\Desktop\Habbo\avatar-sync-mvp\target\avatar-sync-0.1.0.jar" `
    "C:\Users\erale\Desktop\Habbo\Emulator\plugins\AvatarSync.jar" `
    -Force
```

## 1.3 Nitro

Fuente:

```text
C:\Users\erale\Desktop\Habbo\xampp\htdocs\nitro-react
```

Cliente servido realmente:

```text
C:\Users\erale\Desktop\Habbo\xampp\htdocs\public\dist
```

Build:

```powershell
cd "C:\Users\erale\Desktop\Habbo\xampp\htdocs\nitro-react"
yarn build
```

El build actual ya escribe directamente en el `dist` servido.

Después de cambios de cliente:

```text
Ctrl + F5
```

No es necesario copiar manualmente otro `dist`.

## 1.4 Assets

Assets de furnis:

```text
C:\Users\erale\Desktop\Habbo\xampp\htdocs\public\nitro-assets\bundled\furniture
```

FurnitureData:

```text
C:\Users\erale\Desktop\Habbo\xampp\htdocs\public\nitro-assets\gamedata\FurnitureData.json
```

Legacy furnidata:

```text
C:\Users\erale\Desktop\Habbo\xampp\htdocs\public\swf\gamedata\furnidata.xml
```

Workspace usado durante ingeniería de assets:

```text
C:\Users\erale\Desktop\Habbo\AssetWork
```

---

# 2. ESTADO FINAL DE LOS WIRED YA TERMINADOS

## 2.1 Resumen

| WIRED | Estado | Layout Nitro | `items_base.id` | `sprite_id` | `catalog_items.id` | interaction |
|---|---|---:|---:|---:|---:|---|
| Avatar Sync | Terminado y probado | 89 | 1996663548 | 1996663548 | 1996671572 | `wf_act_avatar_sync` |
| Stop Avatar Sync | Terminado y probado | 90 | 1996663549 | 1996663549 | 1996671573 | `wf_act_stop_avatar_sync` |

Página de catálogo usada:

```text
catalog_pages.id = 64
caption          = Efectos
parent_id        = 218
```

Avatar Sync se dejó a 5 créditos. Stop se clonó desde esa entrada de catálogo.

## 2.2 Avatar Sync

Identidad:

```text
items_base.id    = 1996663548
sprite_id        = 1996663548
item_name        = wf_act_avatar_sync
public_name      = WIRED Effect: Avatar Sync
interaction_type = wf_act_avatar_sync
catalog item     = 1996671572
layout Nitro     = 89
Java class       = WiredEffectAvatarSync
```

Formato de configuración:

```text
mode;durationSeconds
```

Ejemplos:

```text
1;0
2;0
3;0
2;30
```

`durationSeconds = 0` significa ilimitado.

### Modo 1

```text
primer causante = líder
resto = followers
followers pueden moverse libremente
solo el líder origina la replicación
```

### Modo 2

```text
primer causante = líder
resto = followers
movimiento manual de followers bloqueado
```

### Modo 3

```text
todos son peers
todos pueden originar acciones
```

### Estado final validado

```text
- persistencia de configuración
- segunda apertura correcta
- ejecución correcta tras reapertura
- duración ilimitada
- duración temporal
- timer empieza solo cuando hay >= 2 participantes
- participantes posteriores heredan tiempo restante
- expiración destruye la sesión completa
- sesiones separadas por WIRED físico
- salida/desconexión limpian sesión
- pickup limpia estado runtime
- modos 1, 2 y 3 funcionales
```

No volver a añadir un whisper final al WIRED salvo petición explícita. Se retiró intencionadamente.

## 2.3 Stop Avatar Sync

Identidad:

```text
items_base.id    = 1996663549
sprite_id        = 1996663549
item_name        = wf_act_stop_avatar_sync
public_name      = WIRED Effect: Stop Avatar Sync
interaction_type = wf_act_stop_avatar_sync
catalog item     = 1996671573
layout Nitro     = 90
Java class       = WiredEffectStopAvatarSync
```

Semántica final:

```text
causante
    ↓
buscar si participa en una sesión Avatar Sync creada por WIRED
    ↓
resolver el wiredItemId de esa sesión
    ↓
terminar la sesión COMPLETA asociada a ese Avatar Sync físico
```

Métodos relevantes:

```java
stopWiredAvatarSyncForParticipant(RoomUnit roomUnit, Room room)

stopWiredAvatarSync(int wiredItemId)
```

Mapas relevantes:

```java
wiredSessionsByItemId
participantToWiredItemId
```

Stop usa específicamente la capa de sesiones WIRED.

### Separación crítica

Stop Avatar Sync **NO debe afectar** a grupos creados por:

```text
:sync1
:sync2
:sync3
:unsync
```

Los comandos admin y las sesiones WIRED son arquitecturas separadas.

Esta separación fue probada y funciona.

### Configuración

Stop no necesita:

```text
modo
duración
selección de furnis
string custom persistente
```

Sí necesita causante:

```java
@Override
public boolean requiresTriggeringUser() {
    return true;
}
```

El editor usa layout `90`.

---

# 3. ARQUITECTURA GENERAL DE UN WIRED CUSTOM

Un WIRED completo tiene al menos tres capas:

```text
MariaDB / MySQL
    ↓
Arcturus Morningstar
    ↓
Nitro React / nitro-renderer
```

Y, para un furni realmente custom, una cuarta capa práctica:

```text
assets / FurnitureData
```

Cada capa puede estar bien mientras otra está rota.

Ejemplo real ya resuelto:

```text
items.wired_data = 3;0
UI primera apertura = modo 3
extradata runtime = 0
execute() leyendo extradata = modo 1
segunda apertura = modo 1
```

La DB estaba bien. El bug estaba en mezclar configuración persistente y estado runtime.

---

# 4. BASE DE DATOS: IDS Y TABLAS

## 4.1 `items_base`

Define el tipo de furni.

Campos relevantes:

```text
id
sprite_id
item_name
public_name
width
length
stack_height
allow_stack
allow_walk
type
interaction_type
interaction_modes_count
```

Regla:

```text
items_base.interaction_type
```

debe coincidir exactamente con la key registrada por Java.

Ejemplo:

```java
new ItemInteraction(
    "wf_act_avatar_sync",
    WiredEffectAvatarSync.class
)
```

## 4.2 `catalog_items`

Hace comprable el base item.

No confundir:

```text
items_base.id
catalog_items.id
catalog_items.item_ids
```

`item_ids` es plural y apunta al `items_base.id`.

Avatar Sync:

```text
catalog_items.id = 1996671572
page_id          = 64
item_ids         = 1996663548
catalog_name     = Avatar Sync
cost_credits     = 5
amount           = 1
```

Stop:

```text
catalog_items.id = 1996671573
page_id          = 64
item_ids         = 1996663549
```

El resto se clonó desde Avatar Sync.

## 4.3 `items`

Representa una instancia física colocada.

Ejemplo histórico útil:

```text
items_base.id = 1996663548   -> tipo Avatar Sync
items.id      = 452          -> una instancia física concreta
```

Para WIRED:

```text
items.wired_data
```

es la configuración persistente.

Consulta:

```sql
SELECT id, wired_data
FROM items
WHERE id = 452;
```

---

# 5. FORMA SEGURA DE CLONAR UN `items_base`

No usar una lista manual de columnas si se puede evitar: este esquema puede tener columnas adicionales.

El método que terminó funcionando bien fue:

```sql
CREATE TEMPORARY TABLE tmp_x
AS
SELECT *
FROM items_base
WHERE id = ID_ORIGEN
LIMIT 1;

UPDATE tmp_x
SET
    id = ID_NUEVO,
    sprite_id = ID_NUEVO,
    item_name = 'wf_act_xxx',
    public_name = 'WIRED Effect: Xxx',
    interaction_type = 'wf_act_xxx';

INSERT INTO items_base
SELECT *
FROM tmp_x;

DROP TEMPORARY TABLE tmp_x;
```

Mismo patrón para `catalog_items`.

### Importante: error FULLTEXT ya conocido

Esto falló:

```sql
CREATE TEMPORARY TABLE tmp_x
LIKE items_base;
```

con:

```text
#1796 - Cannot create FULLTEXT index on temporary InnoDB table
```

Causa:

```text
LIKE copia índices, incluido FULLTEXT.
```

Solución validada:

```text
CREATE TEMPORARY TABLE ... AS SELECT ...
```

que copia las columnas/datos necesarios sin copiar ese índice.

No volver a perder tiempo con ese error.

---

# 6. PROPIEDADES FÍSICAS DEL FURNI

Un asset correcto no basta para que el furni se comporte como un WIRED normal.

Avatar Sync inicialmente tenía comportamiento físico incorrecto:

```text
- se podía atravesar
- no se podía pisar/apilar como correspondía
```

La solución fue copiar del WIRED Effect oficial usado como base:

```text
width
length
stack_height
allow_stack
allow_walk
type
interaction_modes_count
```

Regla para futuros WIRED:

```text
si se clona visualmente/lógicamente un Effect oficial,
clonar también sus propiedades físicas relevantes en items_base
```

Después de modificar `items_base`, reiniciar Arcturus.

---

# 7. REGISTRO DE INTERACTIONS EN MORNINGSTAR

Evento validado:

```text
EmulatorLoadItemsManagerEvent
```

Patrón:

```java
@EventHandler
public void onLoadItemsManager(
        EmulatorLoadItemsManagerEvent event) {

    Emulator.getGameEnvironment()
            .getItemManager()
            .addItemInteraction(
                    new ItemInteraction(
                            "wf_act_xxx",
                            WiredEffectX.class
                    )
            );
}
```

Avatar Sync registra:

```text
wf_act_avatar_sync
    -> WiredEffectAvatarSync.class
```

Stop registra:

```text
wf_act_stop_avatar_sync
    -> WiredEffectStopAvatarSync.class
```

Las interaction keys deben ser únicas.

Después de añadir una interaction nueva:

```text
reiniciar Arcturus
```

Un `Ctrl+F5` no puede cargar una clase Java ni registrar una interaction nueva.

---

# 8. CLASE JAVA BASE DE UN WIRED EFFECT

Base:

```java
public class WiredEffectX
        extends InteractionWiredEffect
```

Imports habituales:

```java
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.messages.ServerMessage;

import java.sql.ResultSet;
import java.sql.SQLException;
```

Constructores usados:

```java
public WiredEffectX(
        ResultSet set,
        Item baseItem) throws SQLException {

    super(set, baseItem);
}
```

y:

```java
public WiredEffectX(
        int id,
        int userId,
        Item item,
        String extradata,
        int limitedStack,
        int limitedSells) {

    super(
        id,
        userId,
        item,
        extradata,
        limitedStack,
        limitedSells
    );
}
```

Métodos importantes:

```text
execute(...)
getWiredData()
loadWiredData(...)
serializeWiredData(...)
saveData(...)
requiresTriggeringUser()
onPickUp()
getType()
```

No asumir semántica por el nombre del método. Si hay duda de API, inspeccionar el JAR local.

---

# 9. `getType()` Y `requiresTriggeringUser()`

Tipo base que funcionó:

```java
@Override
public WiredEffectType getType() {
    return WiredEffectType.SHOW_MESSAGE;
}
```

Se usa como tipo compatible local; el nombre del enum no define la lógica real del custom.

Para efectos dependientes del causante:

```java
@Override
public boolean requiresTriggeringUser() {
    return true;
}
```

Avatar Sync y Stop Avatar Sync lo necesitan.

---

# 10. PERMISOS DEL PLUGIN

Peligro crítico ya descubierto.

NO usar:

```java
@Override
public boolean hasPermission(Habbo habbo, String key) {
    return true;
}
```

Puede conceder permisos globales de staff a usuarios normales.

En AvatarSync:

```java
@Override
public boolean hasPermission(Habbo habbo, String key) {
    return false;
}
```

Antes de activar un plugin WIRED de terceros, revisar siempre `hasPermission()`.

---

# 11. PACKET SERVIDOR -> NITRO PARA WIRED ACTION

Este contrato ya está validado. No volver a inferirlo por ensayo/error mientras no cambie Nitro.

Orden exacto:

```text
boolean
int
int
spriteId
itemId
stringData
intParams count
stuffTypeSelectionCode
action type / layout code
delay
conflictingTriggers count
```

Ejemplo:

```text
false
5
0
sprite
id
string
0
0
89
delay
0
```

Serializer base:

```java
packet.appendBoolean(false);
packet.appendInt(5);
packet.appendInt(0);
packet.appendInt(this.getBaseItem().getSpriteId());
packet.appendInt(this.getId());
packet.appendString(wiredConfig);
packet.appendInt(0); // intParams count
packet.appendInt(0); // stuffTypeSelectionCode
packet.appendInt(LAYOUT_CODE);
packet.appendInt(this.getDelay());
packet.appendInt(0); // conflictingTriggers count
```

Para Stop, `stringData` puede ser `""` y el layout es `90`.

## 11.1 Bug ya resuelto: entero ausente

Un serializer de referencia tenía:

```text
string
0
89
delay
0
```

Faltaba:

```text
stuffTypeSelectionCode
```

Correcto:

```text
string
0    <- intParams count
0    <- stuffTypeSelectionCode
89   <- action layout
delay
0    <- conflictingTriggers count
```

Un solo entero desplazaba todos los campos posteriores.

Regla:

```text
Nunca copiar serializeWiredData() desde un plugin de terceros
sin compararlo con el parser local.
```

---

# 12. PARSER DE NITRO YA VALIDADO

Ruta relevante:

```text
node_modules\@nitrots\nitro-renderer\src\nitro\communication\messages\parser\roomevents
```

Archivos:

```text
Triggerable.ts
WiredActionDefinition.ts
WiredFurniActionParser.ts
```

Flujo:

```text
WiredFurniActionEvent
    ↓
WiredFurniActionParser
    ↓
WiredActionDefinition
    ↓
Triggerable
```

`WiredActionDefinition` lee después de `super(wrapper)`:

```ts
this._conflictingTriggers = [];
this._type = wrapper.readInt();
this._delayInPulses = wrapper.readInt();

let count = wrapper.readInt();

while(count > 0)
{
    this._conflictingTriggers.push(wrapper.readInt());
    count--;
}
```

Conclusión:

```text
Java debe escribir exactamente lo que Nitro espera leer.
```

---

# 13. LAYOUT CODES CUSTOM EN NITRO

Archivo:

```text
C:\Users\erale\Desktop\Habbo\xampp\htdocs\nitro-react\src\api\wired\WiredActionLayoutCode.ts
```

Custom IDs ya usados:

```ts
public static VISUAL_MODIFIER: number = 88;
public static AVATAR_SYNC: number = 89;
public static STOP_AVATAR_SYNC: number = 90;
```

No reutilizar 88/89/90 para otro layout.

Mapeo:

```text
C:\Users\erale\Desktop\Habbo\xampp\htdocs\nitro-react\src\components\wired\views\actions\WiredActionLayoutView.tsx
```

Debe incluir:

```tsx
case WiredActionLayoutCode.AVATAR_SYNC:
    return <WiredActionAvatarSyncView />;

case WiredActionLayoutCode.STOP_AVATAR_SYNC:
    return <WiredActionStopAvatarSyncView />;
```

Views actuales:

```text
C:\Users\erale\Desktop\Habbo\xampp\htdocs\nitro-react\src\components\wired\views\actions\WiredActionAvatarSyncView.tsx

C:\Users\erale\Desktop\Habbo\xampp\htdocs\nitro-react\src\components\wired\views\actions\WiredActionStopAvatarSyncView.tsx
```

---

# 14. UI CUSTOM Y `useWired()`

Hook:

```text
src\hooks\wired\useWired.ts
```

Estados relevantes:

```ts
const [ trigger, setTrigger ] = useState<Triggerable>(null);
const [ intParams, setIntParams ] = useState<number[]>([]);
const [ stringParam, setStringParam ] = useState<string>('');
const [ furniIds, setFurniIds ] = useState<number[]>([]);
const [ actionDelay, setActionDelay ] = useState<number>(0);
```

Al guardar una Action:

```ts
SendMessageComposer(
    new UpdateActionMessageComposer(
        trigger.id,
        intParams,
        stringParam,
        furniIds,
        actionDelay,
        trigger.stuffTypeSelectionCode
    )
);
```

Regla crítica:

```text
El backend recibe stringParam.
No recibe automáticamente el state local del componente React.
```

Si existe:

```ts
const [ mode, setMode ] = useState(...)
```

pero `stringParam` no se actualiza, el backend no recibe ese `mode`.

Flujo:

```text
React local state
    ↓
setStringParam(...)
    ↓
useWired
    ↓
UpdateActionMessageComposer
    ↓
Morningstar WiredSettings
    ↓
saveData(...)
```

Backend:

```java
String data = settings.getStringParam();
```

---

# 15. WIRED SIN SELECCIÓN DE FURNIS

Patrón validado:

```tsx
<WiredActionBaseView
    requiresFurni={ WiredFurniType.STUFF_SELECTION_OPTION_NONE }
    hasSpecialInput={ true }
    save={ save }>
```

Avatar Sync usa input custom.

Stop Avatar Sync no necesita selección de furnis ni configuración funcional.

---

# 16. INCIDENCIA DE UI: TEXTO BLANCO EN STOP

El estilo base del editor WIRED seguía imponiendo texto blanco aunque se usara `text-black`.

Solución final validada: selector específico con `!important`.

Patrón actual:

```tsx
<style>
    {`
        #stop-avatar-sync-text,
        #stop-avatar-sync-text *,
        #stop-avatar-sync-text div
        {
            color: #000000 !important;
        }
    `}
</style>
```

Y:

```tsx
<div id="stop-avatar-sync-text">
    ...
</div>
```

No cambiar esta solución por simples clases Bootstrap si el texto vuelve a quedar blanco.

Este override está limitado al componente Stop y no debe afectar al resto de WIRED.

---

# 17. REGLA CRÍTICA: `wired_data` NO ES `extradata`

Este fue el bug más costoso del desarrollo.

`items.wired_data`:

```text
configuración persistente del WIRED
```

`extradata`:

```text
estado runtime normal del furni/Morningstar
```

No son intercambiables.

Caso real:

```text
items.wired_data = 3;0
wiredConfig      = 3;0
extradata        = 0
```

Esto puede ser completamente normal.

Si `execute()` usa:

```java
String data = this.getExtradata();
```

puede ejecutar una configuración distinta de la persistida.

---

# 18. SOLUCIÓN DE PERSISTENCIA: `wiredConfig`

Patrón validado en Avatar Sync:

```java
private volatile String wiredConfig = "1;0";
```

## `loadWiredData()`

```java
String data = set.getString("wired_data");

this.wiredConfig =
        (data == null || data.trim().isEmpty())
                ? "1;0"
                : data.trim();
```

## `getWiredData()`

```java
@Override
public String getWiredData() {
    return this.wiredConfig;
}
```

## `serializeWiredData()`

```java
packet.appendString(this.wiredConfig);
```

## `saveData()`

```java
this.wiredConfig =
        settings.getStringParam().trim();
```

## `execute()`

```java
String data = this.wiredConfig;
```

Regla:

```text
No volver a usar extradata como fuente única de config persistente custom.
```

---

# 19. PRUEBA MÍNIMA DE PERSISTENCIA

No basta:

```text
guardar -> abrir -> parece correcto
```

QA correcta:

```text
1. Guardar configuración no-default.
2. Consultar items.wired_data.
3. Abrir WIRED.
4. Confirmar UI.
5. Cerrar sin Ready.
6. Consultar DB.
7. Abrir una segunda vez.
8. Confirmar mismo valor visual.
9. Ejecutar.
10. Confirmar comportamiento real.
11. Consultar DB otra vez.
12. Comparar wiredConfig y extradata runtime si hay duda.
```

No considerar terminado un WIRED persistente hasta pasar esta secuencia.

---

# 20. ARQUITECTURA DE SESIONES DE AVATAR SYNC

## 20.1 Sesiones WIRED

Clave primaria runtime:

```text
wiredItemId
    ↓
WiredSyncSession
```

Mapa:

```java
wiredSessionsByItemId
```

Índice participante -> WIRED:

```java
participantToWiredItemId
```

Ventaja:

```text
cada Avatar Sync físico mantiene su propia sesión
```

No usar únicamente el usuario como identidad de sesión.

## 20.2 Grupos internos de sincronización

Los `SyncGroup` usados por la lógica de movimiento/acciones se indexan por líder/ancla.

Mapas relevantes:

```java
groupsByLeader
participantToGroupLeader
```

Los comandos admin usan la lógica de grupos pero **no deben confundirse con la capa de ownership WIRED**.

---

# 21. SESIÓN PENDIENTE Y PRIMER CAUSANTE

Avatar Sync requiere al menos dos participantes para formar sincronización real.

Flujo:

```text
primer causante
    ↓
sesión pendiente

segundo causante
    ↓
crear SyncGroup
    ↓
arrancar timer si duration > 0
```

Estado:

```text
1 participante = esperando
>= 2 = sesión activa
```

## Bug ya resuelto

Antes:

```text
primer causante -> mode 2
segundo causante -> llegaba con mode 1
```

y el segundo podía sobrescribir la configuración.

Regla final:

```text
solo el primer causante puede reconfigurar una sesión pendiente
```

Patrón:

```java
if(session.group == null
        && session.firstParticipantId == actorId
        && configChanged) {

    ...
}
```

No volver a permitir que el segundo causante redefina modo/duración.

---

# 22. TEMPORIZADORES

Semántica validada:

```text
- el timer NO empieza con el primer participante
- empieza cuando hay al menos 2
- duration = 0 significa ilimitado
- participantes posteriores heredan el tiempo restante
- al expirar se destruye la sesión completa
```

Patrón:

```java
session.expiresAt =
        System.currentTimeMillis() + delay;

Emulator.getThreading().run(
        () -> expireSession(...),
        delay
);
```

Regla:

```text
un timer viejo no debe destruir una sesión nueva
que reutilice el mismo wiredItemId
```

Comparar siempre la sesión esperada antes de eliminar.

---

# 23. STOP AVATAR SYNC: CONTRATO RUNTIME

Stop debe actuar solamente si el causante pertenece a una sesión WIRED.

Esquema:

```text
actorId
    ↓
cleanupStaleWiredBinding(actorId)
    ↓
participantToWiredItemId.get(actorId)
    ↓
wiredSessionsByItemId.get(wiredItemId)
    ↓
verificar misma room
    ↓
stopWiredAvatarSync(wiredItemId)
```

Si no existe binding WIRED:

```text
return false
```

Esto cubre también a un usuario que esté solo en una sincronización creada por comandos.

No implementar Stop buscando simplemente `participantToGroupLeader`, porque eso rompería la separación WIRED/comandos.

---

# 24. LIFECYCLE OBLIGATORIO

Una sesión runtime debe poder limpiarse por:

```text
pickup
salida de sala
disconnect
expiración
Stop effect
plugin disable
```

La limpieza debe ser idempotente.

Llamarla dos veces no debe:

```text
romper mapas
dejar índices huérfanos
destruir una sesión distinta
mantener bloqueo de movimiento fantasma
```

En Mode 2, al destruir grupo/sesión debe desaparecer cualquier bloqueo de movimiento asociado.

---

# 25. PAQUETES ENTRANTES: `ClientMessage.clone()`

Cuando se observa un packet que Morningstar también debe procesar, no consumir el original.

Usar:

```java
ClientMessage packetCopy =
        handler.packet.clone();
```

Leer la copia.

Motivo:

```text
los callables del PacketManager se ejecutan antes del handler nativo
```

Consumir el readerIndex del packet real puede romper el handler original.

---

# 26. CANCELACIÓN DE PACKETS

`MessageHandler` dispone de:

```java
public boolean isCancelled = false;
```

Si se cancela, el handler nativo no continúa.

Esto se usa de forma intencional para bloquear el movimiento manual de followers en Mode 2.

Antes de cancelar:

```text
¿solo quiero observar?
o
¿quiero impedir la acción nativa?
```

No cancelar indiscriminadamente.

---

# 27. REGISTRO DE CALLABLES

Hooks dependientes del GameServer deben registrarse cuando GameServer ya existe.

Patrón:

```java
Emulator.getGameServer()
        .getPacketManager()
        .registerCallable(
                Incoming.X,
                callable
        );
```

Al deshabilitar:

```java
Emulator.getGameServer()
        .getPacketManager()
        .unregisterCallables(
                Incoming.X,
                callable
        );
```

Usar flags:

```java
private volatile boolean hookRegistered = false;
```

para impedir duplicados.

---

# 28. ASSETS `.NITRO`: FORMATO YA DESCUBIERTO

No volver a reverse-engineer el contenedor mientras el renderer no cambie.

Formato validado:

```text
U16BE cantidad de archivos

por cada archivo:
    U16BE longitud del nombre
    bytes UTF-8 del nombre
    U32BE longitud del payload comprimido
    payload zlib
```

Header zlib observado:

```text
78 9C
```

Un WIRED Effect oficial usado como referencia:

```text
wf_act_show_message.nitro
```

Contenía exactamente:

```text
wf_act_show_message.json
wf_act_show_message.png
```

Durante la investigación:

```text
JSON descomprimido = 6720 bytes
PNG descomprimido  = 33282 bytes
PNG                = 64x438
```

## 28.1 Anatomía del PNG de referencia

Sheet:

```text
64 x 438
```

Frames:

```text
8 frames de 64x51

y = 0
y = 51
y = 102
y = 153
y = 204
y = 255
y = 306
y = 357
```

Icono:

```text
zona inferior desde y = 408
icono 30x30
```

Esto es útil si en el futuro se vuelve a crear una skin visual.

## 28.2 Nombres internos

No basta con:

```text
copiar wf_act_show_message.nitro
renombrar archivo externo a wf_act_xxx.nitro
```

El JSON interno y las referencias de assets deben usar el classname nuevo.

Proceso correcto:

```text
1. descomprimir JSON + PNG
2. reemplazar nombres internos
3. conservar/editar PNG
4. comprimir payloads con zlib
5. reconstruir contenedor
6. validar descomprimiendo de nuevo
7. instalar en bundled\furniture
8. actualizar FurnitureData
```

---

# 29. ESTADO VISUAL ACTUAL

Avatar Sync y Stop Avatar Sync usan actualmente un visual de WIRED Effect basado en el Effect oficial, no un diseño artístico custom definitivo.

Decisión de proyecto:

```text
no invertir más tiempo en rediseño visual por ahora
```

No reabrir automáticamente la fase de diseño de sprite en una futura sesión.

Solo hacerlo si se pide explícitamente.

Assets:

```text
C:\Users\erale\Desktop\Habbo\xampp\htdocs\public\nitro-assets\bundled\furniture\wf_act_avatar_sync.nitro

C:\Users\erale\Desktop\Habbo\xampp\htdocs\public\nitro-assets\bundled\furniture\wf_act_stop_avatar_sync.nitro
```

El asset Stop se clonó del Avatar Sync actual y se renombraron correctamente sus nombres internos.

---

# 30. FURNITUREDATA

Archivo:

```text
C:\Users\erale\Desktop\Habbo\xampp\htdocs\public\nitro-assets\gamedata\FurnitureData.json
```

Avatar Sync:

```text
id        = 1996663548
classname = wf_act_avatar_sync
category  = wired
defaultdir = 0
xdim = 1
ydim = 1
name = WIRED Effect: Avatar Sync
```

Stop:

```text
id        = 1996663549
classname = wf_act_stop_avatar_sync
category  = wired
defaultdir = 0
xdim = 1
ydim = 1
name = WIRED Effect: Stop Avatar Sync
```

Regla:

```text
items_base.id
FurnitureData id
sprite_id custom
asset classname
nombre interno del .nitro
```

deben mantenerse coherentes.

---

# 31. REINICIO VS CTRL+F5

Usar esta tabla mental:

| Cambio | Acción |
|---|---|
| React / Layout / View | `yarn build` + `Ctrl+F5` |
| `.nitro` / FurnitureData | normalmente `Ctrl+F5`; si hay caché fuerte, recarga completa |
| JAR del plugin | reiniciar Arcturus |
| nueva `ItemInteraction` | reiniciar Arcturus |
| cambio relevante en `items_base` | reiniciar Arcturus |
| cambio de lógica Java | `mvn clean package` + deploy + reiniciar Arcturus |

Bug histórico:

```text
custom asset aparecía como cubo negro
```

Antes de tocar assets/JSON otra vez, verificar primero si Arcturus/Nitro fue recargado correctamente.

---

# 32. ENCODING DE MENSAJES JAVA

Se produjo mojibake en mensajes españoles:

```text
Añadido
sincronización
estás
```

terminaban visualmente dañados.

Solución final:

```text
usar escapes Unicode Java para texto con acentos cuando sea necesario
```

Ejemplos:

```java
"A\u00f1adido a la sincronizaci\u00f3n"

"Ahora est\u00e1s sincronizado"

"Sincronizaci\u00f3n terminada."

"No est\u00e1s en ninguna sincronizaci\u00f3n."
```

Esto evita depender del encoding que Windows/Maven interprete al compilar.

Los archivos editados deben mantenerse en:

```text
UTF-8 sin BOM
```

---

# 33. LOGS DE DIAGNÓSTICO

Durante desarrollo fueron útiles logs como:

```text
CTOR
SERIALIZE
SAVE
EXECUTE
WIRED waiting
WIRED group started
WIRED member joined
Dance replicated
Group destroyed
```

Pero son **logs temporales de desarrollo**, no comportamiento final.

Estado actual:

```text
logs de diagnóstico ruidosos de AvatarSync retirados
```

No volver a añadirlos permanentemente.

Si un futuro WIRED falla, se pueden añadir de forma temporal:

```java
System.out.println(
    "[WIRED-DIAG] SAVE stringParam="
        + settings.getStringParam()
);
```

o equivalentes para:

```text
CTOR
SERIALIZE
SAVE
EXECUTE
```

y retirarlos al cerrar QA.

---

# 34. `SERIALIZE` NO DEMUESTRA PERSISTENCIA

Ver:

```text
SERIALIZE config=2;0
```

solo demuestra el valor usado para construir el packet.

No demuestra:

```text
items.wired_data = 2;0
```

Y al revés:

```text
DB = 2;0
```

no demuestra que `execute()` esté leyendo `2;0`.

Siempre distinguir:

```text
DB
objeto Java
packet
UI
save
execute
runtime state
```

---

# 35. BUGS YA RESUELTOS — NO REDESCUBRIR

## 35.1 Editor roto / fields desplazados

**Síntoma:** Nitro interpretaba mal layout/delay.  
**Causa:** faltaba `stuffTypeSelectionCode` en serializer.  
**Solución:** usar el packet exacto documentado en la sección 11.

## 35.2 Configuración cambia al ejecutar

**Síntoma:**

```text
DB 3;0
primera UI modo 3
execute modo 1
segunda UI modo 1
```

**Causa:** usar `extradata` como config.  
**Solución:** `wiredConfig` separado.

## 35.3 Segundo causante cambia el modo

**Causa:** la sesión pendiente aceptaba reconfiguración del segundo usuario.  
**Solución:** solo `firstParticipantId` puede modificar configuración antes de formar grupo.

## 35.4 Furni parecía Trigger

**Causa:** sprite temporal `3683` pertenecía al Trigger `wf_trg_enter_room`.  
**Solución:** asset custom + sprite ID propio.

El `3683` es historia de debugging, no el estado final.

## 35.5 Cubo negro

**Causa observada:** estado/caché/carga del asset durante la transición.  
**Solución práctica:** comprobar primero restart/recarga antes de reconstruir el asset otra vez.

## 35.6 Furni atravesable / física incorrecta

**Causa:** faltaban propiedades físicas coherentes en `items_base`.  
**Solución:** copiar `width`, `length`, `stack_height`, `allow_stack`, `allow_walk`, `type`, `interaction_modes_count` del Effect base.

## 35.7 Stop funciona pero texto sale blanco

**Causa:** CSS del editor WIRED gana a `text-black`.  
**Solución:** selector `#stop-avatar-sync-text` con `color: #000 !important`.

## 35.8 Acentos destruidos

**Causa:** encoding del pipeline Java/Windows.  
**Solución:** escapes Java `\uXXXX` para strings visibles.

## 35.9 Logs excesivos en consola

**Solución:** retirar logs de diagnóstico después del QA.

## 35.10 SQL temporal con FULLTEXT

**Error:**

```text
#1796 - Cannot create FULLTEXT index on temporary InnoDB table
```

**Causa:** `CREATE TEMPORARY TABLE ... LIKE items_base`.  
**Solución:** `CREATE TEMPORARY TABLE ... AS SELECT ...`.

## 35.11 `hasPermission()` peligroso

**Error de diseño:** devolver `true`.  
**Riesgo:** permisos globales de staff.  
**Solución:** `return false` salvo que el plugin realmente gestione permisos.

---

# 36. COMANDOS ADMIN Y WIRED SON COSAS DISTINTAS

Comandos existentes:

```text
:sync1
:sync2
:sync3
:unsync
```

Los comandos deben seguir funcionando aunque se creen/destruyan sesiones WIRED.

No diseñar un futuro Stop WIRED usando solamente:

```java
participantToGroupLeader
```

porque ese mapa también forma parte del motor de grupos y podría alcanzar grupos de comandos.

Para lógica específica WIRED, usar el índice WIRED:

```java
participantToWiredItemId
wiredSessionsByItemId
```

---

# 37. CHECKLIST PARA CREAR EL SIGUIENTE WIRED ACTION

## FASE A — Contrato

```text
[ ] nombre del furni
[ ] interaction_type
[ ] clase Java
[ ] requiere causante o no
[ ] selecciona furnis o no
[ ] qué configuración persiste
[ ] stringParam / intParams / furniIds
[ ] lifecycle
[ ] comportamiento de pickup
[ ] comportamiento en disconnect/salida
```

## FASE B — Backend

```text
[ ] reservar interaction_type única
[ ] extends InteractionWiredEffect
[ ] constructores
[ ] execute()
[ ] getWiredData()
[ ] loadWiredData()
[ ] saveData()
[ ] serializeWiredData()
[ ] getType()
[ ] requiresTriggeringUser()
[ ] onPickUp() si procede
[ ] registrar ItemInteraction
[ ] hasPermission() no concede permisos accidentales
```

## FASE C — Packet

Mientras las versiones no cambien:

```text
NO redescubrirlo
```

Usar contrato ya validado:

```text
boolean
5
0
spriteId
itemId
stringData
intParams count
stuffTypeSelectionCode
layout
delay
conflictingTriggers count
```

## FASE D — Nitro

```text
[ ] reservar nuevo Layout Code libre
[ ] añadirlo a WiredActionLayoutCode.ts
[ ] crear View
[ ] import en WiredActionLayoutView.tsx
[ ] case del layout
[ ] conectar setStringParam/intParams si aplica
[ ] yarn build
[ ] Ctrl+F5
```

## FASE E — SQL

```text
[ ] clonar items_base desde un Effect compatible
[ ] cambiar id
[ ] sprite_id
[ ] item_name
[ ] public_name
[ ] interaction_type
[ ] verificar propiedades físicas
[ ] crear catalog_items
[ ] item_ids = items_base.id
[ ] page_id correcto
```

Preferir:

```text
CREATE TEMPORARY TABLE ... AS SELECT ...
```

si se quiere clonar una fila completa.

## FASE F — Asset

Si se necesita classname nuevo:

```text
[ ] .nitro con nombre nuevo
[ ] JSON interno con nombre nuevo
[ ] refs internas con nombre nuevo
[ ] PNG correcto
[ ] FurnitureData entry
[ ] id coherente
[ ] classname coherente
[ ] validar .nitro descomprimiéndolo
```

## FASE G — QA

```text
[ ] emulador limpio
[ ] interaction registrada
[ ] comprar
[ ] colocar
[ ] abrir editor
[ ] guardar no-default
[ ] revisar DB
[ ] cerrar
[ ] segunda apertura
[ ] ejecutar
[ ] revisar comportamiento
[ ] pickup
[ ] salida/desconexión
[ ] timer si existe
[ ] restart
[ ] probar coexistencia con otros WIRED
[ ] retirar logs temporales
```

---

# 38. DIAGNÓSTICO RÁPIDO POR SÍNTOMA

## Editor no abre

Comprobar en este orden:

```text
1. interaction_type coincide
2. ItemInteraction registrada
3. clase Java correcta instanciada
4. serializeWiredData() se ejecuta
5. packet completo
6. stuffTypeSelectionCode presente
7. Layout Code existe
8. WiredActionLayoutView tiene case
9. View existe
10. Nitro recompilado
11. Ctrl+F5
```

## Guarda mal

Separar:

```text
React local state
stringParam
settings.getStringParam()
items.wired_data
wiredConfig
```

Orden:

```text
1. state UI
2. setStringParam
3. composer
4. settings.getStringParam()
5. DB
6. segunda apertura
7. execute
```

## DB correcta pero ejecuta mal

Comprobar:

```text
wiredConfig
extradata
getWiredData()
loadWiredData()
serializeWiredData()
saveData()
execute()
```

Si:

```text
wiredConfig = 3;0
extradata   = 0
```

puede ser normal.

## Visual incorrecto pero lógica funciona

No tocar primero Java.

Comprobar:

```text
sprite_id
FurnitureData
classname
.nitro
nombres internos
restart/recarga
```

## Física incorrecta

Comprobar:

```text
width
length
stack_height
allow_stack
allow_walk
type
interaction_modes_count
```

---

# 39. QUÉ NO VOLVER A HACER

```text
- No asumir que extradata es almacenamiento persistente.
- No copiar serializers de terceros sin revisar el contrato local.
- No volver a inferir el packet si las versiones no han cambiado.
- No confundir items_base.id, catalog_items.id e items.id.
- No asumir que SERIALIZE demuestra DB.
- No asumir que DB correcta implica execute correcto.
- No registrar hooks dependientes de GameServer demasiado pronto.
- No consumir ClientMessage original si Morningstar debe seguir leyéndolo.
- No cancelar packets si solo se quiere observar.
- No devolver true indiscriminadamente en hasPermission().
- No tocar lógica estable para arreglar CSS, assets o persistencia.
- No considerar persistencia validada tras una sola apertura.
- No renombrar solo el archivo .nitro dejando nombres internos antiguos.
- No usar CREATE TEMPORARY TABLE ... LIKE items_base en este esquema por el FULLTEXT.
- No volver a usar sprite 3683 como estado final de Avatar Sync.
- No gastar tiempo rediseñando Avatar Sync/Stop salvo petición explícita.
- No dejar logs WIRED-DIAG en el build final.
```

---

# 40. REFERENCIAS FORENSES DE ASSETS

Estas referencias son útiles solo si el pipeline de assets vuelve a romperse.

Asset oficial investigado:

```text
wf_act_show_message.nitro
```

SHA256 observado durante la investigación:

```text
308898B30FC6497AEDB30335A00ABB12B037482BB8808157BE4567FA3A46D511
```

Contenedor Avatar Sync original reconstruido a partir del Effect y con nombres internos custom:

```text
SHA256 observado:
C80D979BE79F3BF95D7DC903B9FC9F4F74A31F24F6395675425641DC6226D904
```

Importante:

```text
estos hashes son referencias históricas de builds concretos
```

Antes de usarlos para comparar un archivo actual, calcular el hash actual; no asumir que sigue idéntico si el asset se modificó posteriormente.

---

# 41. ESTADO DE CIERRE ACTUAL

A fecha de esta consolidación:

```text
Avatar Sync
    backend: OK
    modos 1/2/3: OK
    persistencia: OK
    timer: OK
    lifecycle: OK
    UI: OK
    asset: OK
    catálogo: OK

Stop Avatar Sync
    backend: OK
    localiza sesión WIRED del causante: OK
    termina sesión completa: OK
    no rompe comandos admin: OK
    layout 90: OK
    UI: OK
    texto negro: OK
    asset: OK
    catálogo: OK

Mensajes admin
    acentos: OK mediante Unicode escapes

Consola
    logs diagnósticos ruidosos: retirados
```

Los dos WIRED fueron probados en sala y funcionan.

---

# 42. BLOQUE DE HANDOFF PARA COPIAR A OTRA SESIÓN

Si una futura sesión necesita contexto rápido, este es el resumen mínimo:

```text
PROYECTO:
C:\Users\erale\Desktop\Habbo

EMULADOR:
Arcturus Morningstar 3.6.1
Java 16
JAR:
C:\Users\erale\Desktop\Habbo\Emulator\Habbo-3.6.0-jar-with-dependencies.jar

NITRO:
Nitro React 2.1.1
Renderer 1.6.6
source:
C:\Users\erale\Desktop\Habbo\xampp\htdocs\nitro-react
served dist:
C:\Users\erale\Desktop\Habbo\xampp\htdocs\public\dist

PLUGIN:
C:\Users\erale\Desktop\Habbo\avatar-sync-mvp
deploy:
C:\Users\erale\Desktop\Habbo\Emulator\plugins\AvatarSync.jar

AVATAR SYNC:
layout 89
base 1996663548
sprite 1996663548
catalog 1996671572
interaction wf_act_avatar_sync
config mode;duration
terminado y probado

STOP AVATAR SYNC:
layout 90
base 1996663549
sprite 1996663549
catalog 1996671573
interaction wf_act_stop_avatar_sync
sin configuración funcional
terminado y probado

PACKET ACTION:
false
5
0
sprite
itemId
stringData
intParams count
stuffTypeSelectionCode
layout
delay
conflictingTriggers count

PERSISTENCIA:
wiredConfig = configuración persistente
extradata = runtime
NO mezclarlos

WIRED SESSION:
wiredSessionsByItemId
participantToWiredItemId

STOP:
causante -> participantToWiredItemId -> wiredItemId
-> stopWiredAvatarSync(wiredItemId)
No tocar grupos de comandos.

ASSETS:
public\nitro-assets\bundled\furniture
FurnitureData:
public\nitro-assets\gamedata\FurnitureData.json

.NITRO:
U16BE count
por archivo:
U16BE name length
UTF8 name
U32BE compressed length
zlib payload

BUILD BACKEND:
mvn clean package
copiar target\avatar-sync-0.1.0.jar a Emulator\plugins\AvatarSync.jar
reiniciar Arcturus

BUILD NITRO:
yarn build
Ctrl+F5

REGLA:
No volver a investigar estas piezas mientras no cambien las versiones.
```

---

# 43. CUÁNDO ACTUALIZAR ESTE DOCUMENTO

Actualizarlo cuando ocurra cualquiera de estas cosas:

```text
- cambia Arcturus/Morningstar
- cambia Nitro React
- cambia nitro-renderer
- cambia el formato del packet WIRED
- cambia el esquema SQL
- se reserva un nuevo Layout Code
- se añade otro WIRED custom terminado
- se descubre una regla de lifecycle nueva
- se modifica el formato de assets
- cambia la estrategia de persistencia
```

Si las versiones siguen iguales, este documento debe ser el punto de partida y no una invitación a investigar todo de nuevo.
