# Documentación de desarrollo del retro

Última actualización: 28 de agosto de 2026

Este documento reúne descubrimientos técnicos y patrones ya comprobados en el proyecto.

Su objetivo principal es evitar tener que redescubrir APIs, flujos de Nitro, comportamiento de Arcturus, registro de paquetes, persistencia, sesiones y procedimientos de despliegue en futuros desarrollos.

---

# 1. Entorno principal

## Proyecto activo

Ruta raíz:

`C:\Users\erale\Desktop\Habbo`

Existe una copia secundaria:

`C:\Users\erale\Desktop\Habbo 2`

La copia `Habbo 2` está congelada y NO debe utilizarse para desarrollo ni eliminarse.

---

## Nitro

Código fuente:

`C:\Users\erale\Desktop\Habbo\xampp\htdocs\nitro-react`

Build servido:

`C:\Users\erale\Desktop\Habbo\xampp\htdocs\public\dist`

Renderer:

`C:\Users\erale\Desktop\Habbo\xampp\htdocs\nitro-react\node_modules\@nitrots\nitro-renderer\src`

---

## Emulator

Emulador:

Arcturus Morningstar 3.6.1

Directorio:

`C:\Users\erale\Desktop\Habbo\Emulator`

JAR principal:

`C:\Users\erale\Desktop\Habbo\Emulator\Habbo-3.6.0-jar-with-dependencies.jar`

Plugins:

`C:\Users\erale\Desktop\Habbo\Emulator\plugins`

Dependencia Maven local:

`C:\Users\erale\.m2\repository\com\eu\habbo\Habbo\3.6.0\Habbo-3.6.0.jar`

Java target:

16

Maven:

`C:\Users\erale\Downloads\apache-maven-3.9.16-bin\apache-maven-3.9.16\bin\mvn.cmd`

---

## Base de datos

Base de datos:

`habbo`

Motor:

MariaDB mediante XAMPP

Puerto:

3306

Los plugins pueden crear automáticamente sus propias tablas mediante JDBC.

No es obligatorio importar manualmente SQL desde phpMyAdmin.

Patrón utilizado:

- Esperar a `EmulatorLoadedEvent`.
- Obtener conexión mediante:
  `Emulator.getDatabase().getDataSource().getConnection()`
- Ejecutar `CREATE TABLE IF NOT EXISTS`.
- Si posteriormente hacen falta cambios de esquema, tratarlos como migraciones controladas.

---

# 2. Flujo recomendado para plugins de Arcturus

## onEnable

Registrar los eventos del plugin:

`Emulator.getPluginManager().registerEvents(this, this);`

No asumir que todos los subsistemas del emulador ya están completamente inicializados en este momento.

---

## EmulatorLoadedEvent

Las tareas dependientes del emulador deben realizarse aquí.

Ejemplos:

- preparar tablas;
- registrar packet handlers;
- inicializar servicios que dependan del GameServer.

Patrón comprobado:

`@EventHandler`
`public void onEmulatorLoaded(EmulatorLoadedEvent evento)`

Después:

`Emulator.getGameServer().getPacketManager().registerHandler(ID, Handler.class);`

---

## onDisable

Limpiar cualquier estado temporal almacenado por el plugin.

Ejemplo:

- sesiones autorizadas;
- cachés;
- colecciones concurrentes;
- estados temporales por usuario.

No dejar estado estático vivo innecesariamente.

---

# 3. Eventos de usuario comprobados

Los eventos de usuario están en:

`com.eu.habbo.plugin.events.users`

Para descubrir eventos disponibles:

usar `jar tf`.

Para comprobar su API real:

usar `javap`.

No asumir nombres de métodos o propiedades sin comprobar el JAR instalado.

---

## UserDisconnectEvent

Evento confirmado:

`com.eu.habbo.plugin.events.users.UserDisconnectEvent`

Hereda de:

`UserEvent`

`UserEvent` expone directamente:

`public final Habbo habbo`

Por tanto puede accederse al usuario con:

`evento.habbo`

Y a su ID mediante:

`evento.habbo.getHabboInfo().getId()`

Este evento debe usarse para limpiar cualquier autorización o sesión temporal asociada al usuario.

Esto es especialmente importante cuando se guarda estado en memoria usando el ID del usuario.

---

# 4. Paquetes custom

Antes de asignar nuevos IDs hay que comprobar siempre:

- paquetes nativos del emulador;
- paquetes custom ya existentes;
- Nitro Renderer;
- plugins propios.

No reutilizar IDs solo porque no aparezcan en documentación antigua.

El proyecto real es la fuente de verdad.

---

## Estado conocido

Paquetes custom anteriores llegan hasta:

5024

Los IDs:

5025–5029

están utilizados por funciones nativas de SnowStorm.

InventoryLock utiliza:

| ID | Dirección | Uso |
|----|-----------|-----|
| 5030 | Nitro -> Server | Consultar bloqueo del inventario |
| 5031 | Server -> Nitro | Estado del bloqueo |
| 5032 | Nitro -> Server | Verificar patrón |
| 5033 | Server -> Nitro | Resultado de verificación |
| 5034 | Nitro -> Server | Configurar patrón |
| 5035 | Server -> Nitro | Resultado de configuración |
| 5036 | Nitro -> Server | Cerrar sesión autorizada del inventario |

Por tanto:

5030–5036 están RESERVADOS para InventoryLock.

Para futuros desarrollos, buscar IDs libres de nuevo en el proyecto actual.

---

# 5. Flujo de paquetes Nitro Renderer

Para añadir un protocolo custom completo normalmente hay que modificar varias capas.

## Outgoing

Crear Composer.

Ejemplo conceptual:

- definir header;
- constructor;
- `_data`;
- `getMessageArray()`.

Después exportarlo desde los índices correspondientes.

---

## Incoming

Crear:

- Event;
- Parser.

Después:

- exportar desde sus índices;
- añadir ID en `IncomingHeader`;
- registrar evento/parser en `NitroMessages.ts`.

---

## OutgoingHeader

Añadir el ID custom correspondiente.

---

## IncomingHeader

Añadir el ID custom correspondiente.

---

## NitroMessages

Registrar explícitamente las nuevas clases.

No asumir que únicamente exportar el archivo hace que Nitro procese el paquete.

---

## Procedimiento recomendado

1. Añadir una sola pareja de paquetes.
2. Compilar.
3. Probar transporte real.
4. Confirmar servidor -> cliente.
5. Continuar con el siguiente paquete.

No implementar siete paquetes y toda la UI antes de comprobar que el primero funciona.

---

# 6. Inventario de Nitro: descubrimientos importantes

El inventario tiene varios caminos diferentes de apertura.

No proteger únicamente el botón del toolbar.

Entre los caminos observados están:

- `inventory/show`
- `inventory/toggle`
- reapertura tras colocación de furnis;
- bots;
- pets;
- trade.

Si se implementa una protección del inventario, debe existir un gate central en `InventoryView`.

---

## inventory/hide NO siempre significa cierre

Descubrimiento importante:

`inventory/hide` se utiliza durante la colocación de objetos.

Está presente en:

`src\api\inventory\InventoryUtilities.ts`

Flujos conocidos:

- furnis;
- pets;
- bots.

Durante construcción, Nitro oculta temporalmente el inventario mientras activa el object mover.

Por tanto:

`inventory/hide`

NO debe interpretarse automáticamente como:

"el usuario ha cerrado su sesión de inventario".

Si se hiciera, habría que volver a introducir el patrón después de colocar cada furni.

---

## Cierre real

InventoryLock considera cierre real, entre otros:

- cerrar manualmente el inventario;
- toggle manual que lo cierra;
- salir de la sala;
- desconexión;
- desactivación/reinicio del plugin.

Estos casos invalidan la sesión autorizada.

---

## Construcción

Mientras el inventario haya sido desbloqueado:

- colocar un furni puede ocultarlo;
- volver a abrirlo durante esa sesión no requiere otro patrón;
- colocar múltiples furnis no obliga a autenticar uno por uno.

Esto es intencionado por usabilidad.

---

## Trade

El trade también utiliza el inventario.

Por tanto, una protección del inventario debe contemplar trade.

Comportamiento actual:

- si el inventario todavía está bloqueado, el contenido continúa protegido;
- una solicitud de trade no sirve para saltarse el patrón;
- si el usuario ya tiene una sesión de inventario desbloqueada, trade puede continuar dentro de esa misma sesión.

---

# 7. InventoryLock

Proyecto:

`C:\Users\erale\Desktop\Habbo\Desarrollo\InventoryLock`

JAR desplegado:

`C:\Users\erale\Desktop\Habbo\Emulator\plugins\InventoryLock.jar`

Paquete Java:

`com.retro.inventorylock`

---

# 8. Arquitectura de InventoryLock

Componentes principales:

## InventoryLock.java

Responsable de:

- registrar eventos;
- esperar a EmulatorLoaded;
- registrar handlers;
- limpiar sesiones al desconectarse;
- limpiar sesiones al deshabilitar el plugin.

---

## BaseDatosInventoryLock.java

Responsable de:

- crear automáticamente la tabla necesaria;
- preparar persistencia al arrancar el emulador.

---

## SeguridadPatron.java

Responsable de:

- validar estructura del patrón;
- generar salt;
- generar hash;
- verificar patrón.

---

## ServicioInventoryLock.java

Contiene la lógica principal:

- consultar estado;
- verificar patrón;
- cooldown;
- guardar patrón;
- cambiar patrón;
- desactivar protección.

---

## SesionesInventoryLock.java

Gestiona qué usuarios tienen temporalmente desbloqueado el inventario.

La sesión es temporal.

No debe confundirse con la configuración persistente almacenada en MariaDB.

---

# 9. Persistencia de InventoryLock

Tabla:

`inventory_pattern_lock`

Campos:

| Campo | Uso |
|-------|-----|
| user_id | Usuario |
| enabled | Protección activa |
| pattern_hash | Hash del patrón |
| salt | Salt aleatorio |
| failed_attempts | Intentos incorrectos |
| blocked_until | Fin del cooldown |
| created_at | Creación |
| updated_at | Última modificación |

La tabla se crea automáticamente al arrancar el plugin.

No requiere intervención manual en phpMyAdmin.

---

# 10. Seguridad del patrón

El patrón nunca debe almacenarse directamente.

Configuración actual:

- salt aleatorio de 16 bytes;
- PBKDF2WithHmacSHA256;
- 120000 iteraciones;
- hash de 256 bits;
- Base64 para almacenamiento;
- comparación mediante `MessageDigest.isEqual`.

Nunca:

- guardar patrón sin hash;
- imprimir patrón en logs;
- enviar el patrón guardado de vuelta a Nitro;
- confiar en Nitro para decidir si un patrón es correcto.

El servidor es autoritativo.

---

## Formato

El patrón admite:

- nodos 1–9;
- mínimo 4;
- máximo 9;
- sin nodos repetidos.

Nitro implementa además comportamiento similar a Android:

si se cruza un nodo intermedio válido, se añade automáticamente.

Ejemplos:

1 -> 3 incluye 2.

1 -> 9 incluye 5.

---

# 11. Protección contra fuerza bruta

Configuración actual:

5 intentos fallidos.

Después:

30 segundos de bloqueo.

El servidor controla el cooldown.

Nitro únicamente lo representa visualmente.

Nunca depender exclusivamente de un temporizador client-side para seguridad.

---

# 12. Acciones de configuración

5034 utiliza acciones:

| Acción | Uso |
|--------|-----|
| 0 | Activar protección |
| 1 | Cambiar patrón |
| 2 | Desactivar protección |

Para cambiar patrón:

se requiere el patrón actual.

Para desactivar:

se requiere el patrón actual.

---

## Códigos de resultado 5035

| Código | Significado |
|--------|-------------|
| 0 | Correcto |
| 1 | Nuevo patrón inválido |
| 2 | Estado inesperado/cambió |
| 3 | Patrón actual incorrecto |
| 4 | Cooldown |
| 5 | Error genérico/acción inválida |

La respuesta también incluye:

- success;
- enabled;
- blockedSeconds.

El campo `enabled` debe representar el estado real del servidor incluso cuando la operación falle.

---

# 13. Sesión desbloqueada

Una verificación correcta no desactiva la protección.

Simplemente autoriza temporalmente la sesión de inventario.

Mientras esa sesión siga viva:

- el inventario puede ocultarse temporalmente por construcción;
- puede reabrirse;
- trade puede usarlo.

Se vuelve a bloquear al producirse un cierre real.

---

## Limpieza obligatoria

La autorización temporal debe eliminarse en:

- cierre manual;
- salida de sala;
- desconexión;
- plugin disable.

InventoryLock utiliza `UserDisconnectEvent` para evitar que un usuario que pierde conexión deje una autorización huérfana asociada a su ID.

Esta regla debe aplicarse a cualquier futuro plugin que mantenga sesiones en memoria.

---

# 14. QA realizado en InventoryLock

Pruebas completadas correctamente:

- activar protección;
- crear patrón;
- confirmar patrón;
- abrir inventario bloqueado;
- patrón incorrecto rechazado;
- patrón correcto aceptado;
- cerrar y volver a abrir;
- cambio de patrón;
- patrón antiguo rechazado después del cambio;
- patrón nuevo aceptado;
- desactivar protección;
- límite de 5 intentos;
- cooldown de 30 segundos;
- persistencia tras reiniciar cliente;
- persistencia tras reiniciar emulador;
- trade sin bypass del bloqueo;
- colocación de furnis sin bypass;
- construcción sin pedir patrón después de cada furni;
- desconexión con inventario abierto;
- limpieza de sesión al reconectar.

---

# 15. Logs en producción

Regla general:

UNA ACCIÓN NORMAL DEL USUARIO = CERO LOGS.

No imprimir:

- apertura del inventario;
- cierre;
- consultas;
- IDs de paquetes procesados;
- patrones correctos;
- patrones incorrectos;
- cambio de patrón;
- activación;
- desactivación;
- trade;
- construcción;
- cooldown;
- sesiones normales.

No imprimir tampoco:

"Paquete XXXX registrado correctamente"

si el resto del emulador no hace lo mismo.

Con cientos de usuarios estos mensajes solo generan ruido.

---

## Qué sí debe registrarse

Errores reales.

Ejemplos:

- excepción SQL;
- error inesperado verificando patrón;
- error configurando protección;
- fallo grave de inicialización.

Un error debe aportar información suficiente para diagnosticar el problema, pero nunca incluir secretos o patrones.

---

# 16. Consola Nitro

Antes de considerar terminada una función custom, buscar:

`console.log`

relacionados con la característica.

Los logs temporales utilizados durante desarrollo deben eliminarse.

InventoryLock se dejó sin logs de tráfico en Nitro.

---

# 17. PowerShell y edición de archivos

En Windows PowerShell se observó que ciertos usos relativos de `System.IO.File` podían resolver rutas de forma inesperada.

Regla:

usar rutas absolutas.

---

## UTF-8 sin BOM

Patrón recomendado:

`$utf8 = New-Object System.Text.UTF8Encoding -ArgumentList $false`

Y después:

`[System.IO.File]::WriteAllText($ruta, $contenido, $utf8)`

o:

`[System.IO.File]::WriteAllLines($ruta, $lineas, $utf8)`

---

## Backups

Antes de sobrescribir archivos:

crear backup con timestamp.

Ejemplo conceptual:

`archivo.tsx.backup-YYYYMMDD-HHMMSS`

Especialmente importante para:

- InventoryView;
- renderer;
- plugins Java;
- archivos centrales.

---

# 18. Build de Nitro

Desde:

`C:\Users\erale\Desktop\Habbo\xampp\htdocs\nitro-react`

Ejecutar:

`yarn build`

Después realizar hard refresh del cliente cuando sea necesario.

No asumir que un cambio de TypeScript está activo hasta haber compilado correctamente.

---

# 19. Build de plugins Java

Desde el proyecto del plugin:

usar Maven.

Maven instalado:

`C:\Users\erale\Downloads\apache-maven-3.9.16-bin\apache-maven-3.9.16\bin\mvn.cmd`

Comando habitual:

`clean package`

Después copiar el JAR generado a:

`C:\Users\erale\Desktop\Habbo\Emulator\plugins`

Y reiniciar el emulador.

---

# 20. Procedimiento recomendado para futuros desarrollos

## Paso 1: inspeccionar antes de modificar

Buscar primero cómo funciona el sistema real.

No asumir que Nitro upstream, documentación externa o recuerdos antiguos coinciden con esta instalación.

---

## Paso 2: comprobar APIs del JAR

Utilizar:

- `jar tf`
- `javap`

para confirmar:

- clases;
- eventos;
- constructores;
- campos;
- métodos.

---

## Paso 3: reservar protocolo

Comprobar colisiones reales.

Documentar inmediatamente los nuevos IDs.

---

## Paso 4: crear mínimo backend funcional

Primero conseguir:

Nitro -> Server -> Nitro.

Sin UI compleja.

---

## Paso 5: probar transporte real

Utilizar logs temporales únicamente durante desarrollo.

Cuando funcione:

eliminarlos.

---

## Paso 6: añadir persistencia

Si hace falta tabla nueva:

hacer que el propio plugin pueda inicializarla.

Evitar procesos manuales innecesarios.

---

## Paso 7: añadir UI

Una vez demostrado el protocolo.

No construir una interfaz completa alrededor de paquetes todavía no comprobados.

---

## Paso 8: revisar caminos alternativos

Preguntarse siempre:

"¿Existe otra forma de llegar a esta funcionalidad?"

Ejemplos encontrados con InventoryLock:

- toolbar;
- links;
- trade;
- construcción;
- bots;
- pets;
- reapertura automática.

---

## Paso 9: revisar ciclo de vida

Si existe estado temporal:

limpiarlo en desconexión y shutdown.

---

## Paso 10: QA completo

Probar:

- camino correcto;
- errores;
- repetición;
- reinicio de cliente;
- reinicio de emulador;
- desconexión;
- concurrencia conceptual;
- posibles bypass;
- persistencia;
- UX.

---

## Paso 11: producción silenciosa

Eliminar:

- console.log;
- System.out.println de debugging;
- mensajes por paquete;
- logs por acción normal.

Mantener solo errores realmente útiles.

---

# 21. Principio importante: cliente vs servidor

Nitro es un cliente controlable/modificable por el usuario.

Por tanto:

las decisiones importantes deben realizarse en servidor.

No confiar en Nitro para:

- validar credenciales;
- autorizar una operación sensible;
- controlar cooldowns;
- decidir si un usuario tiene permiso;
- mantener datos secretos.

Nitro puede encargarse de:

- interfaz;
- animaciones;
- estado visual;
- feedback;
- enviar solicitudes.

El servidor debe ser la fuente de verdad.

---

# 22. Limitación específica de InventoryLock

Nitro ya dispone de información del inventario en cliente.

Por eso InventoryLock debe entenderse como:

protección funcional de acceso mediante el cliente normal del retro.

No es una frontera criptográfica absoluta contra alguien que modifique deliberadamente Nitro.

Sin embargo:

- el patrón se valida en servidor;
- el patrón guardado nunca se expone;
- la configuración se persiste en servidor;
- los intentos y cooldown se validan en servidor.

---

# 23. InventoryLock: estado final

Estado:

FUNCIONAL Y VALIDADO.

Backend:

- persistente;
- server-authoritative;
- protegido contra fuerza bruta básica;
- limpia sesiones al desconectar;
- sin logs de tráfico.

Nitro:

- gate central;
- compatible con trade;
- compatible con construcción;
- patrón 3x3 por arrastre;
- midpoint automático;
- tolerancia al salir del tablero mientras se mantiene pulsado;
- UI de activación;
- UI de cambio;
- UI de desactivación;
- navegación de Volver corregida;
- sin logs temporales.

InventoryLock puede utilizarse como implementación de referencia para futuros plugins que necesiten:

- paquetes custom;
- persistencia;
- autenticación temporal;
- estado de sesión;
- UI Nitro;
- integración backend/renderer/client.

<!-- BIRIBIRI_ARCADE_INDEX_V1 -->
## Biribiri Arcade

El código, snapshots y convenciones de los juegos arcade exclusivos de Biribiri se centralizan en:

`Desarrollo/Arcade/`

Space Invaders y Duck Hunt son los _golden masters_ visuales. Block Drop y Pac-Man se conservan ahí como snapshots para comparación y adaptación.

**Regla:** ninguna feature exclusiva de Biribiri Arcade debe quedar únicamente dentro de una ruta runtime de Nitro/renderer/plugin sin una referencia localizable en `Desarrollo/Arcade/`.
<!-- /BIRIBIRI_ARCADE_INDEX_V1 -->

<!-- BIRIBIRI_VESTIDOR_INDEX_V1 -->
## Vestidor Biribiri

Roadmap y código exclusivo del vestidor:

`Desarrollo/Vestidor/`

Prioridades actuales: selector libre HEX/RGB, conjuntos guardados, randomizador con candados, favoritos, historial, búsqueda/filtros y compartir looks.
<!-- /BIRIBIRI_VESTIDOR_INDEX_V1 -->
