# Pendientes de economía, seguridad y marketplace

Última revisión: 29/08/2026.

Este documento reúne ideas, decisiones todavía abiertas y sistemas que
deben diseñarse más adelante.

No describe necesariamente funcionalidades ya implementadas.

---

## 1. Registro global de movimientos de créditos

Objetivo futuro prioritario:

**registrar todo movimiento de créditos, independientemente de qué
sistema lo origine.**

El registro deberá ser append-only y permitir reconstruir el historial
económico de un personaje.

Cada movimiento debería almacenar como mínimo:

- identificador único de transacción;
- `user_id`;
- `account_id` cuando corresponda;
- cantidad positiva o negativa;
- saldo anterior;
- saldo posterior;
- origen del movimiento;
- tipo de operación;
- actor, si existe;
- identificador correlacionado de compra/venta/ajuste;
- fecha y hora;
- metadatos relevantes.

Orígenes previstos:

- compras web;
- recargas de créditos;
- slots;
- restauraciones;
- VIP;
- marketplace;
- devoluciones;
- ajustes administrativos;
- comandos o herramientas internas;
- sistemas nativos del emulador;
- recompensas que concedan créditos;
- cualquier futura mecánica que cree, retire o transfiera créditos.

La infraestructura actual ya cubre compras web y ajustes
administrativos, pero **no debe confundirse con este ledger global
futuro**.

Idealmente Morningstar deberá emitir o persistir los movimientos nativos
que no atraviesen Laravel.

---

## 2. Ban de personaje y ban de cuenta

Mantener dos niveles distintos.

### Ban de personaje

Afecta únicamente al personaje sancionado.

Debe impedir como mínimo:

- entrada al hotel con ese personaje;
- acciones económicas propias del personaje cuando corresponda.

No debe bloquear automáticamente los demás personajes de la cuenta.

### Ban de cuenta

Afecta al propietario completo y a todos sus personajes.

Pensado para casos graves como:

- evasión de sanciones;
- fraude;
- chargebacks abusivos;
- abuso económico;
- otras infracciones de propietario.

Pendiente diseñar:

- modelo/tablas definitivas;
- duración;
- motivo;
- actor administrativo;
- historial;
- apelaciones;
- interacción con marketplace y pagos;
- tratamiento de personajes nuevos durante un ban de cuenta.

---

## 3. Marketplace P2P de créditos

Modelo decidido a alto nivel:

- los usuarios podrán vender créditos por dinero real;
- los créditos publicados pasan a escrow;
- no pueden permanecer simultáneamente gastables y en venta;
- si se cancela la publicación, vuelven al vendedor;
- si se completa, pasan al personaje comprador;
- el dinero del vendedor no se mantiene como saldo interno del Holo;
- la plataforma cobra comisión.

Pendiente:

- proveedor definitivo de pagos;
- flujo de payout al vendedor;
- comisión base;
- reducciones por VIP;
- límites mensuales definitivos;
- máximo de anuncios simultáneos;
- mínimos/máximos por operación;
- cancelaciones;
- disputas;
- chargebacks;
- retenciones de seguridad;
- reglas antifraude;
- resolución administrativa de incidencias;
- conciliación del dinero real con el movimiento de créditos.

La implementación deberá reutilizar identificadores idempotentes y el
sistema de auditoría económica.

---

## 4. Auditoría de tradeos entre usuarios

Crear en el futuro un sistema de registro de tradeos del emulador.

Cada trade debería poder registrar:

- personaje A;
- cuenta A;
- personaje B;
- cuenta B;
- objetos entregados por A;
- objetos entregados por B;
- cantidades;
- identificadores únicos de los objetos cuando existan;
- fecha y hora;
- resultado del trade;
- identificador de transacción.

Objetivo:

detectar transferencias usadas para saltarse el marketplace o mover
valor de forma fraudulenta.

Señales futuras posibles:

- transferencias sistemáticamente unidireccionales;
- objetos de alto valor entregados sin contraprestación;
- múltiples operaciones entre las mismas cuentas;
- patrones circulares;
- concentración anómala de objetos;
- cuentas relacionadas que intercambian valor de forma repetitiva.

Este sistema debe vivir dentro de **Auditoría**, pero separado del
historial de ajustes administrativos de créditos.

---

## 5. Antifraude económico

Construir señales combinando:

- marketplace;
- créditos;
- tradeos;
- cuentas;
- personajes;
- historial administrativo;
- chargebacks;
- restauraciones;
- comportamiento de venta.

No bloquear automáticamente por una sola señal.

Las alertas deberían terminar inicialmente en revisión administrativa.

---

## 6. Resolución de incidencias económicas

`Incidencias económicas` es actualmente de solo lectura.

En el futuro podría añadirse un flujo seguro para:

- inspeccionar una incidencia;
- ver compras y movimientos relacionados;
- marcarla como revisada;
- realizar una devolución explícita;
- completar manualmente una operación cuando sea demostrablemente
  seguro;
- registrar quién resolvió el caso y por qué.

Nunca debe existir un botón genérico de "arreglar automáticamente".

---

## 7. VIP

Decisiones actuales:

- VIP pertenece al personaje;
- precio previsto: 80 créditos / 8 EUR;
- recompensa mensual desde 1 crédito;
- crecimiento por antigüedad;
- máximo previsto de 10 créditos mensuales.

Pendiente:

- duración/ciclo definitivo;
- beneficios jugables;
- beneficios de perfil;
- ventajas definitivas del marketplace;
- reducción exacta de comisiones;
- reglas al expirar;
- renovación;
- tratamiento de períodos discontinuos de VIP.

---

## 8. Límites de marketplace por VIP

Modelo orientativo actual:

| VIPs activos en la cuenta | Límite mensual |
|---|---:|
| 0 | 300 créditos |
| 1 | 400 créditos |
| 2 | 500 créditos |
| 3+ | 600 créditos |

Los valores todavía no son definitivos.

A partir de cierto número de VIPs puede ser preferible reducir comisión
en vez de seguir aumentando el límite.

---

## 9. Economía inicial y catálogo

Pendiente retirar el comportamiento heredado basado en:

`setting('start_credits')`

Objetivo:

- no regalar una cantidad elevada de moneda premium a cada personaje
  nuevo;
- catálogo normal gratuito;
- ocultar precios absurdos de `0 créditos`;
- créditos reservados para economía premium;
- diamantes obtenidos principalmente por horas/actividad;
- duckets obtenidos por actividades, eventos, minijuegos y logros.

Pendiente definir la cantidad inicial definitiva de cada moneda.

---

## 10. Slots

Decidido:

- 3 gratuitos;
- 50 créditos por slot extra;
- propiedad permanente de la cuenta.

Pendiente:

- máximo total de slots por cuenta;
- posibles límites especiales;
- reglas futuras para cuentas de staff.

---

## 11. Resets diarios

Pendiente implementar de forma uniforme los resets diarios a:

**00:00 Europe/Madrid**.

Los sistemas futuros que dependan de día natural deberán utilizar la
misma zona horaria.

---

## 12. Compra de créditos con dinero real

Pendiente seleccionar proveedor.

La implementación futura debe:

- ser independiente del proveedor cuando sea posible;
- no confiar en importes enviados por el navegador;
- validar el pago en servidor;
- utilizar el identificador del proveedor como referencia estable;
- entregar créditos de forma idempotente;
- permitir seleccionar el personaje destinatario;
- registrar pago y entrega;
- tolerar reintentos/webhooks duplicados;
- contemplar refunds y chargebacks.

---

## 13. Logs del emulador

Mantener capacidad de logging de CreditBridge.

En producción:

- `INFO` puede conservar el evento de que un comando RCON fue procesado;
- los payloads detallados con usuario, cantidad, UUID y balances pueden
  permanecer en `DEBUG`;
- el historial económico persistente no debe depender de archivos de
  log del emulador.

Los logs son soporte de diagnóstico, no el ledger económico principal.

---

## 14. Otros sistemas económicos futuros

Cualquier sistema nuevo que mueva créditos deberá decidir antes de
implementarse:

1. quién es el propietario económico;
2. qué `users.id` paga o recibe;
3. cómo funciona conectado;
4. cómo funciona desconectado;
5. qué UUID garantiza idempotencia;
6. dónde queda el ledger;
7. cómo se revierte;
8. qué ocurre tras un crash;
9. cómo lo ve Auditoría;
10. cómo lo reconcilia el sistema.