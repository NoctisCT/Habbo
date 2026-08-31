# Documentación técnica del retro

Actualizado: 31/08/2026

## Estado general

Copia de trabajo real:
C:\Users\erale\Desktop\Habbo

Copia secundaria/congelada:
C:\Users\erale\Desktop\Habbo 2

IMPORTANTE:
- Todo desarrollo nuevo se hace sobre `C:\Users\erale\Desktop\Habbo`.
- `Habbo 2` no debe usarse como copia de trabajo ni borrarse por ahora.
- El retro real contiene las modificaciones actuales, incluidos Pokémon/RPG, furnis personalizados y Subastas.

## Estructura principal

Raíz:
C:\Users\erale\Desktop\Habbo

Contenido relevante:
- `1- Install`
- `Emulator`
- `PokemonBackend`
- `Translations`
- `xampp`
- `Desarrollo\Subastas`
- `PackAIO-V4.sql`
- `Tutorials.txt`
- `DOCUMENTACION-RETRO.md`

## Emulador

Ruta:
C:\Users\erale\Desktop\Habbo\Emulator

JAR principal:
Habbo-3.6.0-jar-with-dependencies.jar

Versión que muestra al arrancar:
Arcturus Morningstar 3.6.1

SHA256 del JAR:
CC6E5105457B2594AB3A51D109A74283BBDF826D10EB35C1D0EC3C43D2D542F7

Tecnología:
- Java
- Bytecode Java 16 (major version 60)
- Plugins mediante archivos `.jar` en `Emulator\plugins`

Java/Maven:
- JDK 25 Eclipse Adoptium disponible para Maven.
- Los plugins propios se compilan con `release 16`.
- Maven 3.9.16 disponible en:
  `C:\Users\erale\Downloads\apache-maven-3.9.16-bin\apache-maven-3.9.16`
- Dependencia local del emulador instalada en:
  `C:\Users\erale\.m2\repository\com\eu\habbo\Habbo\3.6.0\Habbo-3.6.0.jar`

Comando de compilación de plugins si `mvn` no está en PATH:
`& "C:\Users\erale\Downloads\apache-maven-3.9.16-bin\apache-maven-3.9.16\bin\mvn.cmd" clean package`

Puertos:
- GameServer: `0.0.0.0:3000`
- RCON: `127.0.0.1:3001`
- Nitro WebSocket: `ws://0.0.0.0:2096`

## Sistema de plugins

Clase base:
`com.eu.habbo.plugin.HabboPlugin`

Los plugins pueden implementar:
`com.eu.habbo.plugin.EventListener`

Métodos principales:
- `onEnable()`
- `onDisable()`
- `hasPermission(...)`

Eventos:
`Emulator.getPluginManager().registerEvents(plugin, listener)`

Acceso a base de datos:
`Emulator.getDatabase().getDataSource().getConnection()`

Acceso al gestor de paquetes:
`Emulator.getGameServer().getPacketManager()`

PacketManager permite:
- `registerHandler(Integer, Class<? extends MessageHandler>)`
- `registerCallable(Integer, ICallable)`

MessageHandler dispone de:
- `client`
- `packet`
- `isCancelled`
- `handle()`

IMPORTANTE:
No registrar paquetes personalizados directamente en `onEnable()`, porque GameServer puede seguir siendo `null`.

Patrón confirmado:
1. Registrar eventos en `onEnable()`.
2. Esperar a `EmulatorLoadedEvent`.
3. Registrar los handlers personalizados cuando el emulador ya está cargado.

Esto permite crear paquetes personalizados desde plugins sin modificar el JAR principal del emulador.

## Plugin Subastas

Proyecto:
C:\Users\erale\Desktop\Habbo\Desarrollo\Subastas

Group:
`com.retro`

Artifact:
`subastas`

Clase principal:
`com.retro.subastas.Subastas`

Descriptor:
`plugin.json`

JAR desplegado:
C:\Users\erale\Desktop\Habbo\Emulator\plugins\Subastas.jar

Estado confirmado:
- El plugin carga correctamente.
- Espera a `EmulatorLoadedEvent`.
- El paquete 5000 se registra correctamente.
- Nitro ha enviado el paquete 5000 y el emulador lo ha recibido correctamente.

Mensajes de prueba confirmados:
- `[Subastas] Plugin cargado, esperando al emulador`
- `[Subastas] Paquete 5000 registrado correctamente`
- `[Subastas] Paquete 5000 recibido`

## Base de datos / MariaDB / XAMPP

Base de datos del retro:
`habbo`

Ruta física:
C:\Users\erale\Desktop\Habbo\xampp\mysql\data\habbo

Configuración principal:
C:\Users\erale\Desktop\Habbo\xampp\mysql\bin\my.ini

Configuración corregida:
- Puerto: `3306`
- `basedir` y `datadir` apuntan a la instalación real dentro de `Habbo`.

También existía:
C:\Users\erale\Desktop\Habbo\xampp\mysql\data\my.ini

Ese archivo tenía un `datadir` incorrecto (`c:/xampp/mysql/data`) y fue corregido para apuntar a:
`C:/Users/erale/Desktop/Habbo/xampp/mysql/data`

Backup del archivo anterior:
`my.ini.backup-20260827`

### Recuperación de MariaDB

Se detectó corrupción masiva en metadatos de replicación de MariaDB.

Archivos sospechosos encontrados:
1534

No se borraron. Se movieron a:
C:\Users\erale\Desktop\RESCATE-HABBO-DB\replicacion-corrupta-20260827

Backups adicionales:
- `C:\Users\erale\Desktop\RESCATE-HABBO-DB\mysql-Habbo-antiguo`
- `C:\Users\erale\Desktop\RESCATE-HABBO-DB\mysql-Habbo2-actual`

Después de apartar los metadatos corruptos, MariaDB arrancó correctamente y mostró:
- `Reading of all Master_info entries succeeded`
- `Added new Master_info '' to hash table`
- `ready for connections`

Versión:
MariaDB 10.4.32

Integridad confirmada:
- La tabla `users` responde correctamente.
- Usuarios comprobados: `Systemaccount`, `Admin`, `Hokusei`, `Yserinde`.
- Tablas Pokémon/RPG presentes:
  - `pokemon_abilities`
  - `pokemon_inventory`
  - `pokemon_items`
  - `pokemon_natures`
  - `pokemon_pokedex`
  - `pokemon_routes`
  - `pokemon_storage`
  - `pokemon_trainers`
  - `usuarios_rpg_stats`

### Estado operativo actual de MariaDB

Actualmente MariaDB se mantiene arrancado manualmente desde CMD/consola.

Comando usado:
`C:\Users\erale\Desktop\Habbo\xampp\mysql\bin\mysqld.exe --defaults-file="C:\Users\erale\Desktop\Habbo\xampp\mysql\bin\my.ini" --console`

IMPORTANTE:
- Mientras esa instancia esté ejecutándose, NO iniciar otra instancia MySQL desde el panel de XAMPP.
- No copiar manualmente archivos individuales InnoDB.
- No borrar `ibdata1`.
- No importar `PackAIO-V4.sql` salvo que exista una razón concreta y se haya hecho backup.
- Este arranque manual es el estado operativo actual hasta solucionar definitivamente el comportamiento de XAMPP con MySQL/MariaDB.

## Apache / Atom CMS

Apache funciona correctamente.

Puertos reales:
- HTTP: `80`
- HTTPS: `443`

DocumentRoot real:
C:\Users\erale\Desktop\Habbo\xampp\htdocs\public

La página del hotel:
`http://localhost/game/nitro`

Atom CMS carga Nitro mediante un iframe hacia:
`http://localhost/dist/index.html?sso=...`

Por tanto, el cliente Nitro que usa realmente el hotel está servido desde:
C:\Users\erale\Desktop\Habbo\xampp\htdocs\public\dist

## Cliente Nitro

Versión:
Nitro React 2.1.1

Renderer:
1.6.6

Código fuente real:
C:\Users\erale\Desktop\Habbo\xampp\htdocs\nitro-react

Código React:
`src`

Renderer fuente:
`submodules\renderer`

Dependencia usada por Nitro:
`node_modules\@nitrots\nitro-renderer`

Comunicación:
`submodules\renderer\src\nitro\communication`

Archivo central:
`NitroMessages.ts`

Mensajes:
- `messages\incoming`
- `messages\outgoing`
- `messages\parser`

Headers servidor -> Nitro:
`messages\incoming\IncomingHeader.ts`

Headers Nitro -> servidor:
`messages\outgoing\OutgoingHeader.ts`

Nitro registra eventos con:
`this._events.set(IncomingHeader.X, XMessageEvent)`

Nitro registra compositores con:
`this._composers.set(OutgoingHeader.X, XMessageComposer)`

### Build de Nitro

Anteriormente `yarn build` generaba un `dist` dentro del proyecto y después había que mover los archivos manualmente.

Ahora Vite está configurado para compilar directamente sobre el cliente que usa Atom CMS:

Destino:
C:\Users\erale\Desktop\Habbo\xampp\htdocs\public\dist

`vite.config.mjs`:
- `outDir: resolve(__dirname, '../public/dist')`
- `emptyOutDir: false`

`package.json`:
- `build: vite build --base=/dist/`

Resultado:
`yarn build` actualiza directamente el Nitro usado por el hotel y ya no es necesario mover los archivos después de compilar.

`emptyOutDir: false` se mantiene para conservar archivos existentes como:
- `renderer-config.json`
- `ui-config.json`
- favicons y otros archivos estáticos

Incidencia corregida:
Al editar `package.json` y `vite.config.mjs` con Windows PowerShell 5.1 se introdujo BOM UTF-8 y Vite falló al parsear JSON.
Los archivos se reescribieron como UTF-8 sin BOM.

## IDs personalizados

Comprobado en Nitro:
`5000-5050` libres.

Comprobado también contra el registro de PacketManager del emulador:
no aparecen IDs `5000-5050`.

IMPORTANTE:
Solo está comprobado/reservado el rango `5000-5050`. No asumir rangos mayores sin verificarlos.

Primer paquete de Subastas:
`5000 = ABRIR_SUBASTAS`

## Paquete 5000 de Subastas

Nitro:
- `OutgoingHeader.ABRIR_SUBASTAS = 5000`
- `AbrirSubastasComposer`
- Registro en `NitroMessages.ts`
- API React `AbrirSubastas()`
- Envío mediante `SendMessageComposer`

Servidor:
- Handler `AbrirSubastas`
- Registro durante `EmulatorLoadedEvent`

Prueba de extremo a extremo confirmada el 27/08/2026:
1. Se ejecutó `AbrirSubastas()` desde Nitro.
2. Nitro resolvió el composer al header `5000`.
3. El paquete se codificó y se envió por la conexión del cliente.
4. Arcturus recibió el paquete.
5. El plugin mostró:
   `[Subastas] Paquete 5000 recibido`

La prueba temporal se hizo desde el botón Navegador y desde `NavigatorDoorStateView`.
Ambas llamadas temporales fueron eliminadas después de confirmar el funcionamiento.

## Sistema de Subastas

Objetivo:
- Sistema nativo mediante panel de Nitro.
- Sin comandos para los usuarios.
- Backend mediante `Subastas.jar`.
- MySQL/MariaDB para persistencia.
- Servidor autoritativo para pujas, objetos y economía.

Arquitectura prevista:

Nitro
  -> paquetes personalizados
Subastas.jar
  -> GestorSubastas
  -> inventario / economía / MariaDB
Subastas.jar
  -> paquetes de respuesta
Nitro

Nombres previstos del backend:
- `GestorSubastas`
- `ProgramadorSubastas`
- `ColaSubastas`
- `SubastaActual`
- `ServicioPujas`
- `ServicioLiquidacion`
- `RepositorioSubastas`
- `DifusorSubastas`

Tablas previstas:
- `subastas`
- `pujas_subastas`
- `configuracion_subastas`

Estados previstos:
- `en_cola`
- `activa`
- `vendida`
- `sin_vender`
- `cancelada`

Interfaz prevista:
- En curso
- Vender
- Mis subastas
- Historial

Funcionamiento previsto:
- Horario configurable de apertura.
- Usuarios ponen objetos en cola.
- Se subasta un objeto cada vez.
- Pujas controladas por el servidor.
- Mensajes globales de inicio, nuevas pujas y resultado.
- Duración configurable por objeto.
- Incremento mínimo.
- Posible anti-sniping.
- Posible comisión.
- Posible límite de objetos por usuario/día.
- Escrow/bloqueo del objeto mientras está en subasta.
- Liquidación atómica.
- Recuperación ante caída del servidor.

Estado actual:
- Arquitectura base definida.
- Proyecto Java creado.
- Plugin compilado y desplegado.
- Comunicación Nitro -> plugin confirmada mediante paquete 5000.
- Build de Nitro automatizado directamente a `public\dist`.
- Pruebas temporales retiradas.

Siguiente fase:
1. Crear el panel real de Subastas en Nitro.
2. Definir los paquetes de respuesta servidor -> Nitro.
3. Crear las tablas de base de datos.
4. Implementar cola, subasta activa y pujas.
5. Añadir liquidación, seguridad y recuperación.
6. Probar cada fase antes de avanzar.

## Notas de seguridad operativa

- Trabajar siempre sobre `C:\Users\erale\Desktop\Habbo`.
- No usar `Habbo 2` para desarrollo.
- No levantar dos procesos MariaDB/MySQL a la vez.
- No borrar backups de rescate.
- No tocar el JAR principal del emulador salvo necesidad real.
- Priorizar plugins y paquetes personalizados para mantener el núcleo actualizable.
- Hacer backup antes de cambios estructurales de base de datos o cliente.

---

## Estado actual real de Subastas - 27/08/2026

> Esta sección sustituye al bloque antiguo de "Estado actual / Siguiente fase" que quedó desactualizado.

### Backend y economía
- Panel Nitro nativo con pestañas `En curso`, `Vender`, `Mis subastas` e `Historial`.
- Cola real con furnis bloqueados en escrow mediante `items.user_id = -1`.
- Motor automático de cola.
- Recuperación tras reinicio/caducidad.
- Pujas autoritativas en servidor.
- El mejor postor reserva créditos al pujar.
- Al ser superado, recupera su puja anterior.
- Si el mismo mejor postor aumenta su oferta, paga solo la diferencia.
- El vendedor no puede pujar por su propio furni.
- Entrega automática del furni al ganador.
- Pago automático al vendedor.
- Comisión configurable para quema de créditos; actualmente en pruebas a `0%`.
- Anti-sniping interno configurable: umbral 5 s y extensión +5 s.
- Límite simultáneo configurable: 3 normal / 5 VIP-club.

### Sesiones y cupos
- Existe `sesiones_subastas`.
- Cada furni pertenece a una sesión concreta.
- Cada sesión tiene fecha de inicio, fin y cupo máximo.
- El cupo se calcula automáticamente según duración de sesión, duración por furni, transición y margen.
- `vendida`, `sin_vender`, `activa` y `en_cola` consumen plaza.
- `cancelada` libera plaza.
- `no_emitida` no consume plaza.
- Si una sesión termina con furnis pendientes, vuelven automáticamente al vendedor.
- No se arrastran silenciosamente a semanas posteriores.
- Horario de producción previsto: viernes 22:00-00:00.
- Durante pruebas sigue usándose horario ampliado/configurable.

### Avisos
- `5014` se reserva para avisos globales de sesión.
- Política acordada: solo aviso global al abrir y al cerrar la sesión.
- No hay avisos globales por cada puja, furni o venta.
- Aviso de apertura confirmado visualmente en Nitro.
- Aviso de cierre pendiente de verificación final con sesión corta.
- Aviso global centrado en la parte superior de la pantalla.

### Historial
- Historial público muestra solo subastas `vendida`.
- No muestra `sin_vender`, `cancelada` ni `no_emitida`.
- Incluye imagen, vendedor, ganador, precio final y fecha.
- Paginación de 20 resultados con `Anterior / Siguiente`.

### Feed en tiempo real
- Disponible dentro de `En curso`.
- Máximo 20 eventos recientes.
- Consulta cada 2 segundos solo mientras la pestaña está visible.
- Eventos: inicio de furni, puja, venta y final sin pujas.
- Se deriva de `subastas` y `pujas_subastas`; no modifica la lógica económica.

### Interfaz En curso
- Diseño actual: actividad en directo a la izquierda.
- Furni e información de la subasta a la derecha.
- Campo de puja y botón en la parte inferior izquierda.
- Puja mínima, puja actual y tiempo restante se muestran bajo el furni.
- Anti-sniping no se muestra en la interfaz porque es una regla interna.
- Estado sin subasta usa "Próxima subasta" / "Aquí se muestra el próximo furni".

### Paquetes de Subastas
- `5000` Abrir subastas.
- `5001` Estado de subasta.
- `5002/5003` Inventario de subastas.
- `5004/5005` Poner en subasta / resultado.
- `5006/5007` Mis subastas.
- `5008/5009` Retirar subasta / resultado.
- `5010/5011` Pujar / resultado.
- `5012/5013` Estado de sesión y cupo.
- `5014` Aviso global de sesión.
- `5015/5016` Historial de ventas.
- `5017/5018` Feed de actividad.

### Pendiente
- Buscador y filtros en `Vender`.
- Selección de cantidad desde stacks.
- Más detalle en `Mis subastas`: ganador, precio final, neto recibido y comisión.
- Actualización en tiempo real de posiciones de cola / Mis subastas.
- Pruebas de concurrencia y desconexiones.
- Estadísticas.
- Récords.
- Precio medio histórico por furni.
- Confirmar aviso de cierre.
- Fijar horario y comisión definitivos de producción.
## Estado actual del sistema de subastas - 27/08/2026

### Funcional
- Panel Nitro nativo con pestañas **En curso**, **Vender**, **Mis subastas** e **Historial**.
- Cola con escrow real del furni (`items.user_id = -1` mientras está bloqueado).
- Límite de subastas simultáneas configurable: normal 3, VIP/club 5.
- Motor automático de cola y recuperación tras reinicio.
- Pujas autoritativas en servidor, reserva de créditos, devolución al postor superado y entrega al ganador.
- Anti-sniping interno configurable; actualmente umbral 5 s y extensión +5 s.
- Comisión configurable en `configuracion_subastas`; valor actual de pruebas: 0%.
- Sesiones con cupo propio. Un furni no pasa silenciosamente a semanas posteriores.
- Si una sesión termina con furnis pendientes, vuelven al vendedor como `no_emitida`.
- Avisos globales únicamente al abrir/cerrar sesión; no hay spam por cada puja o furni.
- Historial público: solo subastas `vendida`, 20 resultados por página con Anterior/Siguiente.
- Feed en tiempo real dentro de **En curso**, hasta 20 eventos y consulta cada 2 segundos solo mientras esa pestaña está visible.
- Diseño **En curso**: actividad a la izquierda, furni y datos a la derecha, campo de puja inferior. Anti-sniping no se muestra al usuario.

### Vender
- Inventario agrupado visualmente por furni, manteniendo instancias individuales en backend.
- Preview mediante `RoomPreviewer`.
- Cupo y sesión visibles.
- Buscador por nombre.
- Filtro por tipo: todos / suelo / pared.
- Filtro dinámico por rareza.
- Filtro de furnis con varias unidades.
- Contador de resultados filtrados.

### Paquetes añadidos
- `5000` abrir subastas.
- `5001` estado de subasta.
- `5002/5003` inventario.
- `5004/5005` publicar.
- `5006/5007` Mis subastas.
- `5008/5009` retirar.
- `5010/5011` pujar.
- `5012/5013` estado de sesión/cupo.
- `5014` aviso global de sesión.
- `5015/5016` historial de ventas.
- `5017/5018` feed de actividad.

### Pendiente
- Confirmar aviso global de cierre con una sesión corta de prueba.
- Elegir horario definitivo de producción; objetivo actual: viernes 22:00-00:00.
- Elegir comisión definitiva para quema de créditos.
- Permitir seleccionar cantidad desde stacks.
- Añadir más detalle en **Mis subastas**: ganador, importe final, neto recibido y comisión.
- Actualización en tiempo real de posiciones de cola / Mis subastas.
- Pruebas de concurrencia y desconexiones.
- Estadísticas, récords y precio medio histórico por furni.
- Búsqueda/filtros adicionales en Historial si llegan a ser necesarios.
## Lotes / stacks en Subastas

- Un stack se publica como **una sola subasta**, no como varias subastas.
- Un lote ocupa **1 posicion de cola, 1 cupo de sesion y 1 limite simultaneo**, independientemente del numero de unidades.
- En `Vender` se puede elegir cualquier cantidad entre 1 y las unidades realmente disponibles, incluido el boton `Todas`.
- El precio inicial indicado corresponde al lote completo.
- El paquete `5004` ahora envia: cantidad, IDs reales de todas las instancias y precio inicial.
- Se anade `subastas.cantidad`, con valor 1 para las subastas antiguas.
- Se anade `subastas_items` para relacionar una subasta con todas las instancias reales del lote.
- Las subastas antiguas se migran automaticamente como lotes de una unidad.
- Todas las unidades pasan a escrow (`items.user_id = -1`) dentro de la misma transaccion.
- Al venderse, todas las unidades pasan al ganador.
- Si queda sin pujas, se retira o no llega a emitirse, todas las unidades vuelven al vendedor.
- El nombre visible de nuevos lotes incluye `xN` para que En curso, Mis subastas, feed e Historial indiquen claramente la cantidad sin cambiar sus paquetes.
- El servidor valida que todos los IDs pertenezcan al usuario, esten en inventario, sean comerciables y correspondan al mismo furni base.
### Limpieza visual de Vender
- El panel de venta muestra solo información útil para el jugador.
- Se eliminaron de la interfaz datos técnicos como ID de instancia, ID interno del furni, `Rareza: 0` y `LTD: 0:0`.
- La vista previa utiliza la imagen directa del furni para evitar que el RoomPreviewer muestre suelo/paredes sin aportar valor.
- Se mantiene el nombre del furni y, cuando hay varias unidades, un contador de disponibles.
- El selector de lote se simplificó a `Cantidad` con controles `- / número / + / Todas`.
- Se mantiene únicamente `Precio inicial` y el botón de publicación.
- El backend de lotes/stacks no cambia: una cantidad N sigue siendo una única subasta y un único cupo.
### Mis subastas: detalle de venta
- `Mis subastas` se actualiza automáticamente cada 2 segundos mientras la pestaña está visible.
- Las subastas vendidas muestran el nombre del ganador.
- Se guarda en la propia fila de `subastas` la comisión realmente aplicada al finalizar.
- Se guarda también el neto realmente recibido por el vendedor.
- Nuevas columnas: `comision_aplicada` y `neto_vendedor`.
- Esto evita recalcular ventas históricas con una comisión futura diferente.
- Las ventas antiguas anteriores a este cambio conservan esos campos como `NULL`; no se inventan datos históricos.
- El paquete `5007` añade al final de cada fila: ganador, comisión aplicada y neto del vendedor.
## Estadisticas y mercado de Subastas
- Nueva pestaña `Estadisticas`.
- `5019 = OBTENER_ESTADISTICAS_SUBASTAS`, Nitro -> servidor.
- `5020 = ESTADISTICAS_SUBASTAS`, servidor -> Nitro.
- Resumen global: ventas, volumen de creditos, precio medio por unidad y tasa de venta.
- Ranking de furnis por numero de ventas y volumen.
- Buscador de furnis dentro del ranking.
- Detalle por furni: ventas, unidades, media, ultimo precio, minimo, maximo, volumen, tasa de venta y pujas medias.
- Grafico de linea con las ultimas 20 ventas.
- El historico utiliza **precio por unidad**, no precio del lote, para que un stack x10 pueda compararse correctamente con una venta x1.
- El precio completo del lote tambien viaja en los puntos historicos para uso futuro.
- Radar `Perfil de mercado` con indices 0-100:
  - Valor: precio medio por unidad relativo al furni con mayor media.
  - Demanda: numero de ventas relativo al mas vendido.
  - Volumen: creditos movidos relativos al mayor volumen.
  - Actividad: pujas medias por venta relativas al maximo del mercado.
  - Exito: porcentaje real de subastas vendidas frente a vendidas + sin vender.
- No se añade ninguna libreria de graficos: linea y radar se dibujan con SVG nativo en Nitro.
### Estadisticas: buscador global y UI compacta
- Los cuatro KPIs superiores se compactaron a una franja fija de 48 px.
- El buscador ya no filtra solo los furnis cargados inicialmente.
- `5019` ahora recibe `furniId + busqueda`.
- Sin busqueda se muestran los 12 furnis con mas ventas/volumen.
- Con busqueda, MariaDB busca por `public_name` e `item_name` entre todos los furnis con ventas y devuelve hasta 20 coincidencias.
- La busqueda usa debounce de 250 ms para evitar una peticion por cada pulsacion inmediata.
- La lista de resultados tiene altura fija y scroll, evitando que desaparezca por el layout flexible.
- Se mantiene el detalle seleccionado mientras se escribe; al pulsar un resultado se abre ese furni.
### Estadisticas: layout mercado izquierda/derecha
- La columna izquierda queda dedicada exclusivamente a busqueda y lista de furnis.
- Todas las tarjetas del buscador tienen una altura fija de 38 px para evitar diferencias visuales entre furnis.
- La columna derecha contiene todas las estadisticas globales y del furni seleccionado.
- El resumen superior se identifica como `Estadisticas totales de las subastas`.
- `Volumen total` se refiere al volumen de creditos de todas las ventas del mercado.
- `Volumen vendido de este furni` se refiere solo al furni seleccionado.
- Las seis metricas del furni se muestran en una sola fila: media, ultimo, minimo, maximo, exito y pujas/venta.
- El radar se mueve a la zona inferior derecha junto con volumen y record de venta.
- El record de venta permite salto de linea para evitar solapes con nombres largos.
- El grafico historico ocupa el ancho principal de la columna derecha.
### Estadisticas: refinado visual final
- Las tarjetas de furnis del buscador usan una altura uniforme de 44 px.
- Los iconos de furnis se muestran dentro de un viewport fijo para que sprites grandes no deformen ni corten la tarjeta.
- El detalle del furni utiliza el mismo criterio: icono contenido en un area fija y nombre con elipsis si fuera necesario.
- Se elimina el texto Min/Max duplicado del encabezado del grafico historico.
- Las seis metricas del furni pasan a tarjetas compactas de una sola fila.
- El radar aumenta a 190x170 px y sus etiquetas suben a 10 px para mejorar legibilidad y evitar pixelado.
- La zona inferior se divide claramente en radar + tarjetas de volumen y record de mercado.
- Volumen y record ya no comparten una misma linea de texto, evitando solapes.
### Estadisticas: iconos y radar refinados
- Los iconos de furnis del buscador y del detalle aumentan ligeramente de tamaño para mejorar su lectura.
- Se mantiene un viewport fijo para que sprites grandes no deformen el layout.
- Las etiquetas del radar se colocan respecto al borde real del pentagono con anclajes distintos por lado.
- `Valor`, `Demanda`, `Volumen`, `Actividad` y `Exito` quedan a una distancia visual similar del grafico.
## Configuracion final de produccion - Subastas
- Sesion semanal: viernes de 22:00 a 00:00.
- `dia_semana = 5` porque Java `DayOfWeek.getValue()` usa lunes=1 y viernes=5.
- Duracion base de cada subasta: 60 segundos.
- Transicion entre subastas: 1 segundo.
- Anti-sniping: si una puja valida entra con <=5 segundos, se añaden 5 segundos.
- Margen de cupo: 85%.
- Ventana semanal: 7200 segundos.
- Capacidad teorica con bloques de 61 segundos: 118 subastas.
- Cupo publicado con margen del 85%: 100 subastas por sesion.
- Limite simultaneo normal: 3.
- Limite simultaneo VIP: 5.
- Comision: 5% sobre la puja final, usando aritmetica entera del servidor.
- La comision funciona como sumidero de creditos: el vendedor recibe `puja final - comision`; la comision no se acredita a ningun usuario.
- Las sesiones programadas/abiertas de pruebas anteriores se conservan pero quedan `cancelada`, para que no compitan con el calendario de produccion.
### Cupo de sesion en tiempo real
- Nitro consulta el estado/cupo de la sesion cada 2 segundos mientras el panel de Subastas esta abierto.
- El contador de ocupacion ya no requiere cambiar de pestana para actualizarse.
- Si `ocupados >= cupo_maximo`, Vender muestra `No queda cupo disponible en esta sesion (X/Y)`.
- El boton de publicar queda desactivado mientras la sesion esta completa.
- El servidor sigue siendo la autoridad final y mantiene su propia comprobacion de cupo.
- La respuesta de crear subasta refresca el estado de sesion tanto en exito como en error.
### Proteccion contra doble creacion desde Vender
- `publicarSubasta()` usa `instanciaPublicando.current` como bloqueo sincronico adicional al state `procesandoSubasta`.
- Esto evita que un doble click o dos eventos consecutivos envien dos peticiones antes de que React haya renderizado el estado `procesando`.
- El bloqueo se libera ante cualquier `ResultadoPonerSubastaEvent`, tanto exito como error.
- El servidor sigue siendo la autoridad y valida propiedad, cupo y limites.
### Preview segura de furnis custom en Vender
- La preview grande de `Vender` deja de renderizar el furni seleccionado mediante `LayoutFurniImageView`/RoomEngine.
- Se reutiliza `grupoSeleccionado.iconUrl`, el mismo recurso seguro que ya utiliza la cuadrícula del inventario.
- La imagen se muestra dentro de una caja fija de 185 px con `overflow: hidden`.
- La imagen ocupa un cuadro de 128x128 px con `object-fit: contain`, por lo que un custom enorme nunca invade el resto del panel.
- Si el icono no puede cargarse se muestra `Vista previa no disponible`, pero el furni sigue pudiéndose poner en subasta.
- El objetivo es aislar Vender de assets custom corruptos o con dimensiones anómalas que puedan romper/crashear el renderer.
### Preview hibrida de furnis en Vender
- La preview principal vuelve a usar `LayoutFurniImageView` para mostrar el render real del furni.
- Se mantiene eliminado el `RoomPreviewer` asociado a seleccionar furnis, evitando volver al flujo que provocaba crashes con ciertos custom.
- El render real usa escala 1 y una caja interna fija de 156x156 px.
- Se fuerza `background-size: contain`, centrado y `overflow: hidden`, por lo que los furnis custom grandes no pueden invadir el panel.
- Un `ErrorBoundary` protege la preview y usa `grupoSeleccionado.iconUrl` como fallback si el render falla.
- La caja exterior sigue limitada a 185 px de alto.
### Blindaje global de imagenes de furnis en Subastas
- Todas las apariciones directas de `LayoutFurniImageView` dentro de `src/components/subastas` pasan por `SubastasFurniImageSeguro`.
- El wrapper añade `ErrorBoundary`, `overflow: hidden`, `background-size: contain`, centrado y limites al 100% del contenedor.
- Si un custom falla durante el render se sustituye por un fallback local y no debe derribar la pestaña completa.
- La preview grande de Vender conserva ademas su fallback por `iconUrl`.
- Se corrige en origen el fallo de `LayoutFurniImageView` que podia intentar asignar `onload` cuando `imageElement` aun era `null`.
- Este blindaje cubre En curso, Feed, Historial, Estadisticas, Mis subastas y cualquier otra vista de Subastas que use `LayoutFurniImageView`.
### Limites fisicos de previews de Subastas
- `SubastasFurniImageSeguro` limita también el tamaño físico del div renderizado, no solo el desbordamiento.
- `scale < 1`: máximo 72 px; `scale = 1`: máximo 96 px; `scale > 1`: máximo 170 px.
- Los tamaños explícitos de vistas como Vender y Estadísticas tienen prioridad.
- Esto evita que customs enormes hagan crecer tarjetas de Mis subastas, Historial o feeds compactos.
### Próxima subasta real
- Paquete `5021 = OBTENER_PROXIMA_SUBASTA`.
- Paquete `5022 = PROXIMA_SUBASTA`.
- Cuando no existe una subasta activa, la pestaña En curso consulta cada 2 segundos el primer lote `en_cola`.
- Se prioriza la sesión abierta actual; si no la hay, la próxima sesión programada con cola.
- La vista muestra furni, cantidad del lote, vendedor, precio inicial, posición en cola, sesión e inicio.
- Si la cola está vacía se muestra `No hay subastas en cola`.
- La imagen usa `SubastasFurniImageSeguro`, por lo que también queda protegida frente a customs grandes o rotos.
- Cuando el motor activa la siguiente subasta, vuelve automáticamente a la vista normal de subasta activa.
### Próxima subasta: diseño final
- Cuando no hay una subasta activa, `En curso` muestra arriba una tarjeta compacta de `Próxima subasta` y mantiene debajo el feed/actividad reciente.
- Cuando hay una subasta activa, `EnCursoSubastasView` permanece sin cambios, conservando pujas, contador y actividad en tiempo real.
- La tarjeta de próxima subasta coloca los datos a la izquierda y la preview protegida del furni a la derecha.
- Se elimina cualquier texto técnico como `cuando el motor quede libre`.
- El texto público usa `Siguiente en la cola`.
- `inicioSesion` se formatea en Nitro con locale `es-ES`, por ejemplo `Viernes, 28 de agosto · 22:00`, en vez de mostrar el timestamp crudo de base de datos.
- La tarjeta se limita a 198 px de alto para dejar espacio al feed debajo.
### Próxima subasta: compactación y fecha exacta
- La tarjeta se reduce a 156 px para dejar más espacio al feed.
- Los datos quedan a la izquierda y la preview del furni a la derecha.
- Vendedor y precio usan bloques compactos de 36 px, evitando huecos verticales.
- Sesión y fecha de inicio comparten una sola línea compacta.
- El servidor devuelve `fecha_inicio` mediante `DATE_FORMAT(..., '%d/%m/%Y %H:%i')`, evitando desplazamientos horarios por `Timestamp`/timezone.
- Nitro muestra la fecha en español, por ejemplo `Viernes 28/08/2026 · 22:00`.
- Se mantiene `Siguiente en la cola` y no se muestra terminología técnica interna.
### Historial: búsqueda, filtros y precio por unidad
- `Historial` general usa búsqueda global por nombre de furni en base de datos.
- Filtros públicos: Todos, Vendidas y Sin pujas.
- El paquete 5015 envía `pagina`, `busqueda` y `filtro`.
- El paquete 5016 devuelve página, páginas totales, resultados filtrados y 20 filas por página.
- Los lotes muestran cantidad (`xN`) y, cuando `N > 1`, precio total + precio por unidad.
- Las fechas se devuelven en formato `dd/MM/yyyy HH:mm`.
- Las filas muestran vendedor y comprador cuando la subasta fue vendida.
- `Mis subastas` añade búsqueda local y filtros de estado sobre las filas cargadas si el layout actual permite insertar el control de forma segura.
### Corrección de ubicación del buscador de Mis subastas
- El buscador y filtro personal se muestran únicamente dentro de la pestaña `Mis subastas`.
- Se retiró su inserción accidental de `En curso`.
- El filtrado sigue usando `misSubastasFiltradas` y no modifica el backend.
### Pulido final de Estadísticas y Records
- Se eliminan símbolos Unicode usados como placeholders/flechas porque la fuente del cliente los mostraba como notas musicales.
- Variación y tendencia usan texto puro (`Sin datos`, `Al alza`, `A la baja`, `Estable`).
- Las posiciones vacías de Records muestran `Sin dato`.
- Los avatares de Records usan `LayoutAvatarImageView` a escala nativa 1 en vez de 0.8 para evitar reescalado fraccional.
- `Más compras` sigue contando operaciones; además muestra como dato secundario los furnis/unidades comprados.
- Ejemplo esperado con los datos actuales: Yserinde = 7 compras / 8 furnis.
- `Más pujado` se mantiene como métrica de hype/interés y cuenta pujas recibidas.
### Ajuste vertical de Records
- Las cuatro tablas de Records pasan de 358 px a 378 px de alto.
- La zona del puesto #1 pasa de 150 px a 170 px.
- El nombre del usuario dispone de una línea fija de 18 px centrada para evitar que quede oculto bajo el avatar.
- Se mantiene el ancho, colores, Top 5 y diseño general.
## Subastas V1 — cierre definitivo

**Estado: TERMINADA.**

La documentación autoritativa y actual de Subastas es:

`C:\Users\erale\Desktop\Habbo\DOCUMENTACION-SUBASTAS-V1.md`

Las secciones anteriores de este documento que describan Subastas como `prevista`, `pendiente` o `siguiente fase` son históricas y quedan supersedidas por la documentación V1 definitiva.

## Placas V1 — cierre definitivo

**Estado: TERMINADA.**

La documentación autoritativa y actual de Placas es:

`C:\Users\erale\Desktop\Habbo\DOCUMENTACION-PLACAS-V1.md`

Changelog de cierre:

`C:\Users\erale\Desktop\Habbo\PLACAS-V1-CHANGELOG-2026-08-31.md`

Placas V1 incluye creación y moderación de placas, refunds, regalos, autoría, Diseñador de Placas, licencias comunitarias, lista de espera, marketplace, compras, comisión y payout, notificaciones, inactividad automática y derecho permanente de distribución del hotel.

Regla de inactividad de licencias comunitarias:
- aviso a los 30 días sin actividad;
- retirada a los 45 días, garantizando al menos 15 días desde el aviso;
- los anuncios activos se desactivan al retirar la licencia;
- la solicitud `waitlisted` aprobada más antigua ocupa automáticamente la plaza liberada.

`waitlisted` significa exclusivamente **solicitud aprobada por staff que está esperando una plaza comunitaria**.

El mantenimiento está registrado en Laravel Scheduler a las 03:15. El entorno de despliegue debe ejecutar `schedule:run` mediante cron/Task Scheduler o mantener `schedule:work`.

Ropa queda expresamente fuera de Placas V1 y debe diseñarse por separado: assets Nitro, instalación, publicación, venta e impuestos/comisiones tienen un ciclo distinto.
