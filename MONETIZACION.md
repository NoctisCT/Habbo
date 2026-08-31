# Monetización y economía

Última actualización: 2026-08-31

Este documento recoge las decisiones actuales sobre monetización, economía, cuentas, personajes y marketplace.

---

## 1. Principio general

La arquitectura separa claramente:

- **Cuenta (`accounts`)**: identidad comercial y administrativa.
- **Personaje (`users`)**: identidad jugable y economía dentro del hotel.

No existe por ahora una suscripción Premium a nivel de cuenta.

La monetización recurrente principal será el **VIP por personaje**.

Los servicios permanentes o puntuales de cuenta/personajes se venderán como compras únicas.

---

## 2. Personajes y slots

### Slots gratuitos

Cada cuenta nueva tendrá:

- **3 slots gratuitos**.

### Slots adicionales

Los slots extra serán una compra permanente.

Precio inicial:

- **50 créditos por slot adicional**.

Funcionamiento:

1. El usuario elige qué personaje paga el slot.
2. Se descuentan 50 créditos de ese personaje.
3. Se aumenta `accounts.character_slots` en 1.
4. El slot pertenece a la cuenta para siempre.

Los créditos siguen perteneciendo individualmente a cada personaje.

No se creará una bolsa común de créditos de cuenta.

### Cuenta de administración/desarrollo

La cuenta principal de administración puede tener slots concedidos
manualmente además de los comprados normalmente.

Estado actual de la cuenta de administración:

- 3 slots base.
- 1 slot adicional concedido administrativamente.
- 1 slot adicional comprado mediante el sistema económico real.
- Total actual: **5 slots**.

La compra real permanece registrada en el ledger económico.

---
## 3. Restauración de personajes

Eliminar un personaje desde el CMS significa **archivarlo**, no borrar
físicamente sus datos.

### Usuario normal

La restauración es una compra única.

Precio actual:

- **100 créditos por restauración**.

El usuario:

1. selecciona el personaje archivado;
2. selecciona qué personaje activo paga;
3. confirma la compra;
4. el personaje se restaura conservando su mismo `users.id`.

El personaje pagador puede estar **conectado o desconectado**.

- Si está desconectado, el cobro se realiza transaccionalmente en base
  de datos.
- Si está conectado, el cobro se realiza mediante CreditBridge para
  respetar el saldo autoritativo en memoria de Morningstar.

### VIP

VIP no incluye restauraciones gratuitas.

La restauración continúa siendo una compra independiente de **100
créditos**.

### Administración

Solo los administradores del **máximo nivel** pueden restaurar
personajes manualmente sin compra.

Moderadores, staff intermedio y administradores inferiores no pueden
hacerlo.

Toda restauración administrativa queda registrada.

### Reglas de restauración

Para restaurar:

- debe existir un slot libre;
- se mantiene el mismo `users.id`;
- se conserva el nombre;
- se conserva inventario;
- se conservan créditos;
- se conserva progreso;
- se conservan placas;
- se conservan relaciones y demás datos asociados;
- no se crea automáticamente un slot adicional.

Una restauración pagada genera trazabilidad económica y registro de la
restauración.

---
## 4. VIP

VIP pertenece exclusivamente al personaje.

No existe actualmente Premium de cuenta.

Ejemplos:

- Hokusei puede ser VIP.
- Yserinde puede no ser VIP.
- Los beneficios jugables y de perfil de Hokusei no se transfieren a Yserinde.

### Posibles beneficios

Entre otros:

- personalización avanzada del perfil del personaje;
- ventajas cosméticas;
- funciones sociales;
- ventajas concretas dentro del hotel;
- beneficios indirectos en el marketplace.

La lista definitiva se diseñará más adelante.

---

## 5. Beneficios agregados de tener varios personajes VIP

Aunque VIP pertenece al personaje, el número de personajes VIP de una cuenta puede mejorar ciertos límites de cuenta.

Principal aplicación prevista:

- Marketplace.

Modelo orientativo todavía no definitivo:

| VIPs activos | Límite mensual orientativo |
|---|---:|
| 0 | 300 créditos |
| 1 | 400 créditos |
| 2 | 500 créditos |
| 3+ | 600 créditos |

A partir de cierto número de VIPs, en vez de seguir aumentando el límite, podrían reducirse las comisiones.

Ejemplo orientativo:

- 3 VIPs: reducción de comisión.
- VIPs adicionales: posibles reducciones adicionales hasta un límite.

Los números y porcentajes exactos quedan pendientes.

---

## 6. Marketplace P2P

Existirá un marketplace común donde los usuarios podrán vender créditos por dinero real.

### El dinero real NO se almacena en el Holo

Cuando una venta se completa:

- el comprador paga;
- el dinero correspondiente al vendedor se envía a su cuenta de PayPal;
- el Holo no mantiene una bolsa interna de euros del usuario;
- se aplica la comisión correspondiente de la plataforma.

La implementación concreta del flujo PayPal se diseñará posteriormente.

### Créditos en escrow

Cuando un personaje publica créditos:

1. Se retiran inmediatamente de `users.credits`.
2. Pasan a estado de escrow/bloqueo del marketplace.
3. Ya no pueden gastarse dentro del hotel.
4. Si el anuncio se cancela, vuelven al personaje vendedor.
5. Si la venta se completa, se entregan al personaje comprador.
6. Nunca pueden existir simultáneamente en el personaje y en el marketplace.

### Límite mensual

El límite para introducir créditos en el marketplace será **por cuenta**, no por personaje.

Ejemplo:

Una cuenta tiene un límite mensual de 300.

Puede aportar:

- Hokusei: 100.
- Yserinde: 150.
- N: 50.

Total:

- 300 / 300.

Crear más personajes no permite evadir el límite.

Los VIPs de los distintos personajes pueden aumentar ese límite según las reglas definidas anteriormente.

---

## 7. Monedas

Se evita crear una moneda Premium adicional.

### Créditos

Los créditos son la moneda premium.

Características:

- se compran con dinero real;
- pertenecen al personaje;
- pueden utilizarse para servicios premium;
- pueden pagar slots adicionales;
- pueden participar en el marketplace P2P;
- podrán utilizarse para otras compras premium.

No deben regalarse de forma habitual por simplemente estar conectado o realizar actividades normales.

### Diamantes

Moneda obtenida principalmente por:

- tiempo real de juego.

Tendrá:

- su propia tienda;
- contenido específico.

Su objetivo es recompensar permanencia y actividad continuada.

### Duckets

Moneda obtenida mediante:

- actividades del hotel;
- minijuegos;
- eventos;
- logros;
- participación.

Tendrá:

- su propia tienda;
- contenido específico.

### Catálogo normal

El catálogo normal será gratuito.

Los objetos gratuitos:

- no mostrarán un precio absurdo de `0 créditos`;
- simplemente aparecerán como disponibles/gratuitos.

---

## 8. Compras y destinatarios

Cada operación debe distinguir claramente entre:

- quién paga;
- qué cuenta recibe el beneficio;
- qué personaje recibe el beneficio.

### Slot adicional

Paga:

- un personaje seleccionado.

Se aplica a:

- `accounts.character_slots`.

### VIP

Paga:

- un personaje / mecanismo de compra definido.

Se aplica a:

- un `users.id` concreto.

### Recarga de créditos

El comprador selecciona:

- qué personaje recibe los créditos.

Los créditos se añaden únicamente a ese `users.id`.

### Restauración

El comprador selecciona:

- qué personaje archivado restaurar.

Se aplica al mapping existente de ese personaje.

### Principio de seguridad

Las confirmaciones externas de pago nunca deben depender de cuál sea el personaje principal en ese momento.

Las operaciones deben guardar explícitamente:

- `account_id`;
- `user_id` pagador cuando proceda;
- `target_user_id` cuando proceda;
- tipo de producto;
- importe;
- estado;
- referencia externa de pago.

---

## 9. Bans

Existirán dos niveles.

### Ban de personaje

Afecta únicamente a:

- un `users.id`.

Los demás personajes de la cuenta pueden seguir funcionando si la sanción lo permite.

### Ban de cuenta

Afecta a:

- `account_id`;
- todos los personajes actuales;
- cualquier personaje futuro de esa cuenta.

Se utilizará para sanciones graves como evasiones, fraude, chargebacks u otras infracciones a nivel de propietario.

---

## 10. Resets diarios

Todos los sistemas diarios deben resetearse a:

- **00:00 HPE — Hora Peninsular Española**.

No deben utilizarse ciclos del tipo:

- 86400 segundos desde el arranque;
- 24 horas desde el último reset;
- reset automático al iniciar el emulador.

Se auditarán todos los sistemas diarios del emulador y CMS.

Primer sistema identificado:

- respetos diarios.

---

## 11. Estado online

`users.online` actualmente es:

- `ENUM('0','1','2')`.

Atom y Arcturus utilizan actualmente:

- `'0'` = offline;
- `'1'` = online.

El funcionamiento normal ha sido comprobado correctamente.

No se modificará la estructura hasta encontrar un problema reproducible que justifique normalizarla.

---

## 12. Decisiones pendientes

Las decisiones e ideas todavía no cerradas se mantienen ahora en un
documento independiente:

- [`PENDIENTES.md`](PENDIENTES.md)

Entre ellas están:

- beneficios definitivos de VIP;
- ciclo/duración definitiva de VIP;
- límite máximo de slots;
- límites y comisiones definitivos del marketplace;
- integración del proveedor de pagos y payouts;
- chargebacks y reglas antifraude;
- auditoría de tradeos entre usuarios;
- registro global de todos los movimientos de créditos;
- economía inicial y catálogo gratuito;
- implementación definitiva del reset diario.

Este documento refleja principalmente decisiones vigentes y
comportamiento implementado.

---
## 13. Resumen del modelo

### Cuenta

Contiene:

- identidad;
- personajes;
- número de slots;
- historial comercial;
- límite del marketplace;
- bans de cuenta.

### Personaje

Contiene:

- créditos;
- diamantes;
- duckets;
- inventario;
- progreso;
- VIP;
- perfil;
- bans de personaje.

### Monetización

- VIP: recurrente y por personaje.
- Slots: pago único.
- Restauración: pago único.
- Créditos: compra premium.
- Marketplace: P2P con comisión.
- Cosméticos/servicios adicionales: pago único cuando corresponda.

No existe Premium de cuenta mientras no haya suficientes beneficios exclusivos de cuenta que justifiquen una segunda suscripción.

---

## 14. Economía inicial de personajes

Actualmente los personajes nuevos reciben créditos mediante:

- `setting('start_credits')`.

Esto es comportamiento heredado y se modificará cuando se rehaga la economía inicial y el catálogo gratuito.

Objetivo:

- los personajes nuevos no deben recibir automáticamente una cantidad elevada de créditos premium;
- el catálogo normal será gratuito;
- los créditos conservarán su función de moneda premium;
- diamantes y duckets cubrirán las recompensas obtenidas jugando.

La cantidad inicial definitiva de créditos queda pendiente de decidir.

---

## 15. Créditos incluidos con VIP

VIP podrá incluir una asignación periódica de créditos para el personaje
VIP.

Regla actual prevista:

- Mes 1: **1 crédito**.
- La recompensa aumenta progresivamente con la antigüedad.
- Máximo previsto: **10 créditos mensuales**.
- La entrega pertenece al personaje VIP, no a la cuenta.
- Cada personaje VIP tendrá su propio control de mensualidades.
- Debe registrarse la última mensualidad entregada para impedir dobles
  entregas.

Como los créditos podrán participar en el marketplace P2P por dinero
real, esta recompensa debe mantenerse coordinada con:

- precio del VIP;
- comisión del marketplace;
- límites mensuales de venta;
- valor efectivo de los créditos.

Debe evitarse que la recompensa mensual de VIP genere un arbitraje
económico garantizado.

---
## 16. Precio y retorno mensual de VIP

Decisión actual:

- VIP pertenece al personaje.
- Precio previsto: **80 créditos / 8 EUR**.
- El personaje VIP recibirá créditos mensualmente.
- Mes 1: 1 crédito.
- La recompensa aumenta progresivamente con la antigüedad.
- Máximo previsto: **10 créditos mensuales**.

A precio oficial, 10 créditos equivalen aproximadamente a 1 EUR.

El crédito vendido P2P previsiblemente cotizará por debajo del precio oficial y además estará sujeto a comisión del marketplace, por lo que la recompensa mensual de VIP no pretende funcionar como un retorno monetario garantizado.

---

## 17. Compra de slots implementada

Estado implementado:

- 3 slots gratuitos por cuenta.
- Slot adicional: **50 créditos**.
- El usuario selecciona qué personaje paga.
- El personaje pagador puede estar conectado o desconectado.
- Los créditos siguen perteneciendo individualmente al personaje.
- No existe una bolsa común de créditos de cuenta.
- El slot adquirido pertenece permanentemente a la cuenta.
- Cada compra utiliza un `purchase_id` UUID.
- La operación queda registrada en `purchase_operations`.
- El movimiento económico queda registrado en `credit_transactions`.
- Las operaciones repetidas con el mismo identificador son idempotentes.

Para personajes desconectados, el cobro y el aumento de
`accounts.character_slots` se realizan dentro de una transacción de
base de datos.

Para personajes conectados, el débito se realiza mediante CreditBridge
y la modificación del slot se finaliza después de confirmar el cobro.

---

## 18. Infraestructura de transacciones de créditos

Las compras web que consumen créditos utilizan
`CreditTransactionService`.

Principios:

- nunca realizar un débito SQL directo de un personaje conectado;
- personaje desconectado: transacción SQL;
- personaje conectado: CreditBridge;
- cada compra utiliza un UUID estable;
- una misma compra no puede cobrar dos veces;
- las compensaciones también son idempotentes;
- los estados inciertos pasan a revisión manual.

### Fallos después de un cobro online

Si el cobro se realizó pero posteriormente falla la operación:

1. se devuelve la misma cantidad;
2. la devolución utiliza `refund-<purchase_id>`;
3. la devolución es idempotente;
4. la compra queda `refunded`;
5. un reintento no vuelve a cobrar ni a devolver.

### Recuperación después de un crash

Si CreditBridge aplicó el movimiento antes de que PHP terminara:

- el reintento encuentra la operación ya aplicada;
- no vuelve a ejecutarla;
- conserva los balances históricos;
- completa la operación pendiente.

El saldo actual puede ser distinto del `balance_after` histórico si
existieron movimientos legítimos posteriores.

---

## 19. Ajustes administrativos de créditos

Los créditos no son editables directamente desde el formulario normal
de usuario de Filament.

Existe la acción:

- **Ajustar créditos**.

Reglas:

- solo administrador del máximo nivel;
- cantidad positiva o negativa;
- motivo obligatorio;
- UUID;
- actor y personaje objetivo registrados;
- saldo anterior y posterior;
- personajes conectados y desconectados;
- CreditBridge cuando está conectado;
- ledger económico;
- idempotencia.

### Auditoría

Dentro de **Auditoría** existen:

- `Registros`;
- `Historial de créditos`;
- `Incidencias económicas`.

`Historial de créditos` muestra los ajustes administrativos.

`Incidencias económicas` ejecuta reconciliación económica de solo
lectura.

### Reconciliación

Comando:

`php artisan credits:reconcile`

Comprueba coherencia entre:

- compras;
- ajustes administrativos;
- ledger;
- CreditBridge;
- refunds;
- operaciones pendientes o en revisión manual.

No corrige automáticamente los datos.

---

## 20. Economía web heredada

Las rutas antiguas de Shop, Voucher y PayPal permanecen en el código
como referencia, pero sus endpoints económicos están desactivados.

No deben utilizarse para nuevas compras.

La futura compra de créditos con dinero real deberá utilizar la
infraestructura idempotente actual.

El proveedor definitivo de pagos queda pendiente.

---

## 21. Alcance actual del ledger

`credit_transactions` registra actualmente los movimientos gestionados
por la nueva infraestructura web, incluyendo compras y ajustes
administrativos.

Esto **no significa todavía que todos los movimientos de créditos del
emulador estén registrados globalmente**.

El objetivo futuro de registrar absolutamente todo movimiento de
créditos, además de tradeos y señales antifraude, está documentado en:

- [`PENDIENTES.md`](PENDIENTES.md)

---

---

## 22. Marketplace de placas V1

**Estado: implementado y cerrado como V1 el 31/08/2026.**

El marketplace de placas es independiente del futuro marketplace P2P de créditos por dinero real y del marketplace de ropa.

### Creación
- Crear una placa cuesta **10 créditos**.
- El usuario elige qué personaje paga.
- El cobro utiliza la infraestructura económica idempotente.
- Un rechazo de moderación genera devolución automática de 10 créditos.

### Regalos
- Regalar una placa propia cuesta **3 créditos**.
- El receptor obtiene una copia, no la autoría.
- Los duplicados se bloquean antes del cobro.
- Un fallo posterior al cobro utiliza refund idempotente.

### Vendedores
- `Diseñador de Placas` puede vender automáticamente y no consume plaza comunitaria.
- Una licencia comunitaria requiere al menos **5 placas aprobadas**.
- Existen **3 plazas comunitarias activas**.
- `pending` = pendiente de revisión.
- `waitlisted` = aprobada por staff, esperando plaza.
- `active` = licencia activa.
- `revoked` = licencia retirada.
- Al liberarse una plaza se promociona automáticamente la espera aprobada más antigua.

### Inactividad
- aviso: **30 días** sin actividad;
- retirada: **45 días** sin actividad y al menos 15 días desde el aviso;
- volver a tener actividad limpia el aviso;
- retirar la licencia desactiva sus anuncios activos y libera su plaza.

### Anuncios y HC
- normal: **3 anuncios activos**;
- HC activo: **6 anuncios activos**.

HC se comprueba mediante la suscripción real `HABBO_CLUB`, no mediante rango.

### Precio
- ingreso elegido por vendedor: **0–7 créditos**;
- comisión fija del hotel: **3 créditos**;
- precio final comprador: **3–10 créditos**.

### Compra
- el comprador recibe una copia;
- la autoría nunca cambia;
- se bloquea el duplicado antes del cobro;
- otro personaje de la cuenta autora puede comprar si no posee la placa;
- fallo de entrega después del cobro: refund completo;
- entrega completada + payout ambiguo: `manual_review`, sin retirar la placa ni duplicar valor.

### Derecho de distribución del hotel
El creador puede conceder voluntariamente a Biribiri un derecho:
- permanente;
- no exclusivo;
- irreversible.

El creador conserva autoría, uso, regalo y capacidad de venta. La concesión no mueve créditos.

Documentación técnica completa:

- [`DOCUMENTACION-PLACAS-V1.md`](DOCUMENTACION-PLACAS-V1.md)
