# Subastas V1 — documentación definitiva

**Estado:** V1 TERMINADA  
**Cierre técnico:** 27/08/2026 11:02  
**Proyecto:** C:\Users\erale\Desktop\Habbo\Desarrollo\Subastas  
**Plugin desplegado:** C:\Users\erale\Desktop\Habbo\Emulator\plugins\Subastas.jar

Este documento sustituye como referencia autoritativa las notas históricas y listas de pendientes anteriores sobre Subastas.

## Arquitectura

- Interfaz nativa en Nitro.
- Backend autoritativo mediante Subastas.jar.
- Persistencia en MariaDB.
- Sin comandos de usuario para operar subastas.
- Una única subasta global activa.
- Cola por sesión.
- Escrow de furnis.
- Pujas con reserva inmediata de créditos.
- Liquidación, recuperación tras reinicio y anti-sniping.

## Configuración de producción V1

- Sesión: **viernes 22:00–00:00**.
- Duración por lote: **60 segundos**.
- Transición: **1 segundo**.
- Margen de capacidad: **85%**.
- Capacidad prevista: **100 lotes por sesión**.
- Comisión: **5%**.
- Límite normal: **3** subastas simultáneas.
- Límite VIP: **5**.
- Anti-sniping: con **5 segundos o menos**, extensión de **5 segundos**.
- Máximo de Mis subastas: **25**.

## Lotes / stacks

- Un lote de unidades idénticas consume una sola posición de cola.
- subastas.cantidad almacena la cantidad.
- subastas_items asocia cada instancia física al lote.
- El precio publicado y la puja son por el lote completo.
- Estadísticas e historial normalizan precio por unidad cuando corresponde.

## Economía

- Primera puja: igual o superior al precio inicial.
- Siguientes: mínimo puja actual + 1.
- El vendedor no puede pujar.
- Una puja aceptada reserva/descuenta créditos inmediatamente.
- Al superar una puja se devuelve la reserva anterior.
- Si el mismo mejor postor aumenta su puja solo paga la diferencia.
- Venta: vendedor recibe precio final menos comisión.
- La comisión del 5% se destruye como sumidero de créditos.
- Sin pujas: el lote vuelve al vendedor.

## Interfaz V1

1. **En curso** — subasta activa, pujas y feed; cuando no hay activa muestra Próxima subasta real + actividad reciente.
2. **Vender** — inventario, búsqueda/filtros, stacks y cantidad.
3. **Mis subastas** — búsqueda/filtros, cola, retirada, resultado, comisión y neto.
4. **Historial** — búsqueda global, filtros, paginación y precio/u.
5. **Estadísticas** — métricas globales, detalle, gráfico de precio/u, radar, variación, tendencia y últimas ventas.
6. **Records** — rankings Top 5 de Usuarios y Furnis.

## Paquetes personalizados

| ID | Dirección | Nombre | Función |
|---:|---|---|---|
| 5000 | Nitro -> servidor | ABRIR_SUBASTAS | Abre/consulta el sistema |
| 5001 | servidor -> Nitro | ESTADO_SUBASTA | Estado de la subasta activa |
| 5002 | Nitro -> servidor | OBTENER_INVENTARIO_SUBASTAS | Inventario comerciable |
| 5003 | servidor -> Nitro | INVENTARIO_SUBASTAS | Inventario para Vender |
| 5004 | Nitro -> servidor | PONER_EN_SUBASTA | Publica furni/lote |
| 5005 | servidor -> Nitro | RESULTADO_PONER_SUBASTA | Resultado de publicación |
| 5006 | Nitro -> servidor | OBTENER_MIS_SUBASTAS | Solicita subastas del usuario |
| 5007 | servidor -> Nitro | MIS_SUBASTAS | Lista personal |
| 5008 | Nitro -> servidor | RETIRAR_SUBASTA | Retira un lote en cola |
| 5009 | servidor -> Nitro | RESULTADO_RETIRAR_SUBASTA | Resultado de retirada |
| 5010 | Nitro -> servidor | PUJAR_SUBASTA | Envía una puja |
| 5011 | servidor -> Nitro | RESULTADO_PUJA_SUBASTA | Resultado de puja |
| 5012 | Nitro -> servidor | OBTENER_ESTADO_SESION_SUBASTAS | Sesión/cupo |
| 5013 | servidor -> Nitro | ESTADO_SESION_SUBASTAS | Estado de sesión |
| 5014 | servidor -> Nitro | AVISO_GLOBAL_SUBASTA | Avisos de apertura/cierre |
| 5015 | Nitro -> servidor | OBTENER_HISTORIAL_SUBASTAS | Historial con búsqueda/filtros |
| 5016 | servidor -> Nitro | HISTORIAL_SUBASTAS | Página de historial |
| 5017 | Nitro -> servidor | OBTENER_FEED_SUBASTAS | Actividad reciente |
| 5018 | servidor -> Nitro | FEED_SUBASTAS | Feed en tiempo real |
| 5019 | Nitro -> servidor | OBTENER_ESTADISTICAS_SUBASTAS | Mercado/detalle de furni |
| 5020 | servidor -> Nitro | ESTADISTICAS_SUBASTAS | Estadísticas |
| 5021 | Nitro -> servidor | OBTENER_PROXIMA_SUBASTA | Primer lote de la cola |
| 5022 | servidor -> Nitro | PROXIMA_SUBASTA | Datos de próxima subasta |
| 5023 | Nitro -> servidor | OBTENER_RECORDS_SUBASTAS | Rankings históricos |
| 5024 | servidor -> Nitro | RECORDS_SUBASTAS | Records Usuarios/Furnis |

**Cobertura revisada:** todos los paquetes 5000–5020 solicitados están documentados y, además, quedan documentados los paquetes finales 5021–5024.

## Records

### Usuarios
- Más créditos ganados.
- Más furnis vendidos.
- Más créditos gastados.
- Más compras; además se muestran furnis adquiridos.

### Furnis
- Más caro vendido por unidad.
- Más vendido por unidades.
- Mayor volumen.
- Más pujado: total de pujas recibidas, usado como indicador de interés/hype.

## QA e integridad

Integridad V2 final: **14/14 checks OK**.

Comprobaciones incluidas:
1. Máximo una activa.
2. Cantidad del lote = items asociados.
3. Cola/activa en escrow.
4. Sin escrow huérfano.
5. Terminadas fuera de escrow.
6. Vendidas con ganador y puja positiva.
7. No vendidas sin ganador final.
8. Puja actual = máxima histórica.
9. Sin posiciones duplicadas.
10. Cola consecutiva.
11. Un item no aparece en dos subastas vivas.
12. Todas las asociaciones apuntan a items existentes.
13. Comisión + neto = precio final.
14. Neto implica comisión guardada.

También se validaron durante el desarrollo:
- puja y sobrepuja del mismo usuario;
- saldo insuficiente;
- ganador desconectado;
- recuperación tras reinicio;
- retirada de cola;
- lotes/stacks;
- lote sin pujas;
- cierre de sesión y 
o_emitida;
- devolución de items;
- aviso global de cierre;
- estadísticas, historial, próxima subasta y records.

## Cierre pre-lanzamiento

En el cierre V1 se eliminaron los datos transaccionales de QA para que producción empiece limpia:

- Subastas eliminadas: **33**
- Sesiones de prueba eliminadas: **5**
- Pujas de prueba eliminadas: **8**
- Relaciones de lotes eliminadas: **34**
- Lotes que estaban en cola y fueron restaurados antes del reset: **0**
- Escrow restante tras el reset: **0**

Al reiniciar Arcturus, GestorSesionesSubastas recreará la siguiente sesión programada usando la configuración de producción.

## Logs y archivos auxiliares

Se retiraron de la consola los mensajes de depuración:
- Inventario enviado a ...
- Mis subastas enviadas a ...

Los errores y mensajes operativos importantes se mantienen.

Backups, diagnósticos, informes QA y resultados de integridad quedan archivados en:

C:\Users\erale\Desktop\Habbo\Archivo\Subastas-V1

## Build final

- Maven: **BUILD SUCCESS**
- JAR target SHA-256: $targetHash
- JAR desplegado SHA-256: $deployHash
- Coincidencia: **sí**

# SUBASTAS V1 TERMINADA

La V1 queda cerrada técnicamente y lista para arrancar con histórico limpio.