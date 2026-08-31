# Placas V1 — documentación técnica y funcional

**Estado:** TERMINADA  
**Fecha de cierre:** 31/08/2026  
**Proyecto:** Biribiri / Atom CMS + Arcturus Morningstar  
**Rama de cierre:** `rpg-engine-wip`

Este documento es la referencia autoritativa de **Placas V1**. Describe el sistema de creación de placas, moderación, autoría, regalos, licencias de vendedor, marketplace, compras, comisiones, payouts, notificaciones, inactividad y derecho permanente de distribución del hotel.

El marketplace nativo del emulador (`marketplace_items`) no forma parte de este sistema y no se modifica.

---

## 1. Separación de dominios

Placas es un sistema independiente de:

- marketplace P2P de créditos por dinero real;
- tienda oficial de créditos;
- marketplace de ropa;
- marketplace nativo de furnis del emulador.

No se deben reutilizar tablas o reglas de Placas para Ropa sin una inspección previa. Ropa tendrá otro flujo técnico: archivos Nitro, instalación de assets, venta, impuestos/comisiones y ciclo de publicación propios.

---

## 2. Identidad: cuenta, personaje y autoría

La arquitectura distingue:

- **Cuenta (`accounts`)**: propietario comercial y administrativo.
- **Personaje (`users`)**: identidad jugable; posee créditos e inventario.
- **Autor de una placa**: el `creator_user_id` concreto que la creó.
- **Elegibilidad para vender**: se evalúa a nivel de cuenta.

La autoría nunca se transfiere al comprar o regalar una placa.

Archivar el personaje creador no transfiere la autoría. Si se restaura el mismo personaje, conserva su identidad y sus derechos.

---

## 3. Creación de placas

La creación se realiza desde Marketplace → Placas → Crear placa.

### Coste

- **10 créditos por envío**.
- El usuario selecciona qué personaje paga.
- El cobro usa la infraestructura económica idempotente del CMS.
- Personajes online y offline siguen caminos seguros distintos.

### Archivo y preview

- Se aceptan PNG/GIF según el pipeline actual.
- El resultado destinado al hotel es de **40×40**.
- Cuando hay que redimensionar se prioriza pixel art y nearest-neighbor.
- Se muestra una preview final antes de confirmar el cobro/envío.
- Los archivos de trabajo viven bajo `storage/app/badge_creator`.
- Las placas publicadas usan códigos propios `USRBDG...`.

### Pendientes

Existe un máximo de envíos pendientes por cuenta para evitar spam de moderación.

---

## 4. Moderación

Las solicitudes se gestionan desde Housekeeping/Filament.

Estados de una solicitud de placa:

- `pending`
- `rejecting`
- `approved`
- `rejected`

### Aprobación

Al aprobar:

1. se publica el asset/código utilizable por el hotel;
2. se crea el registro correspondiente en `creator_badges`;
3. se entrega una copia al personaje creador;
4. la cuenta recibe la notificación correspondiente.

`creator_badges` representa placas de creador ya aprobadas.

### Rechazo

Al rechazar:

- se registra el motivo;
- se devuelve automáticamente el coste de **10 créditos**;
- la devolución es idempotente;
- el usuario recibe una notificación.

La moderación no debe producir dobles cobros ni dobles devoluciones ante reintentos.

---

## 5. Mis placas

`Mis placas` muestra las placas aprobadas creadas por la cuenta.

Cada placa conserva:

- cuenta autora;
- personaje creador;
- submission de origen;
- código;
- nombre;
- descripción;
- asset;
- estado de marketplace;
- derecho de distribución del hotel, cuando exista.

Una copia adquirida o regalada no convierte al receptor en autor y no le concede derecho de reventa como creador.

---

## 6. Regalos

Una placa propia puede regalarse a un personaje concreto.

### Reglas

- Coste: **3 créditos**.
- Se selecciona personaje pagador.
- Se selecciona personaje receptor.
- Se permiten regalos entre personajes de la misma cuenta.
- El receptor recibe una copia para usarla.
- El receptor no obtiene autoría.
- El receptor no obtiene derecho de reventa como creador.
- Si el receptor ya posee la placa, la operación se bloquea antes del cobro.
- Los fallos de entrega posteriores al cobro se compensan mediante refund idempotente.

Tabla principal:

- `badge_gifts`

---

## 7. Diseñador de Placas y licencias comunitarias

Hay dos vías para vender placas creadas.

### Diseñador de Placas oficial

El rol oficial se determina mediante el sistema de equipos de la web:

- `website_teams`
- `user_website_team`

Nombre canónico:

- `Diseñador de Placas`

Un Diseñador de Placas:

- puede vender automáticamente;
- no necesita licencia comunitaria;
- no consume uno de los huecos comunitarios.

`users.rank` sigue representando autoridad/jerarquía técnica. Los equipos web representan funciones.

### Licencia comunitaria

Requisitos:

- mínimo **5 placas aprobadas** a nivel de cuenta;
- solicitud gratuita;
- revisión manual por staff;
- máximo **3 licencias comunitarias activas** simultáneamente.

---

## 8. Estados de licencia y lista de espera

Estados definitivos:

- `pending`: solicitud enviada, todavía pendiente de revisión por staff.
- `waitlisted`: solicitud **ya aprobada por staff**, pero esperando que quede libre una plaza.
- `active`: licencia activa con `community_slot`.
- `revoked`: licencia retirada.

Flujo:

`pending → active`

si hay plaza disponible.

`pending → waitlisted`

si staff aprueba pero las tres plazas están ocupadas.

Cuando se libera una plaza, la solicitud aprobada `waitlisted` más antigua se promociona automáticamente a `active`.

La promoción reutiliza la plaza comunitaria libre y genera una notificación.

---

## 9. Inactividad de licencias comunitarias

La licencia comunitaria no es un derecho permanente a bloquear una de las tres plazas.

Campos preparados para el ciclo:

- `last_activity_at`
- `warning_sent_at`
- `revoked_at`
- `revocation_reason`

### Política V1

- **30 días sin actividad:** aviso.
- **45 días sin actividad:** retirada automática.
- Deben haber transcurrido al menos **15 días desde el aviso** antes de la retirada.
- Si hay actividad real después del aviso, `warning_sent_at` se limpia.
- Una actividad registrada en el mismo segundo que el aviso también cuenta correctamente.

La actividad se deriva de operaciones reales del catálogo/marketplace y ventas; no de poseer simplemente la licencia.

### Al retirar una licencia

- pasa a `revoked`;
- libera `community_slot`;
- sus anuncios activos se desactivan;
- se registra el motivo;
- se notifica a la cuenta;
- se promociona automáticamente la solicitud `waitlisted` aprobada más antigua si existe.

Una retirada manual desde staff también libera y reasigna la plaza.

### Scheduler

Comando:

`php artisan badge-marketplace:maintain-seller-licenses`

Programación Laravel:

- diario a las **03:15**;
- `withoutOverlapping()`.

**Requisito de despliegue:** Laravel Scheduler necesita un runner real (`schedule:run` mediante cron/Task Scheduler o `schedule:work`). Registrar el comando en `Kernel.php` no ejecuta por sí solo el scheduler del sistema operativo.

---

## 10. Marketplace público de placas

El marketplace vende copias de placas creadas por usuarios a cambio de créditos del hotel.

### Elegibilidad del vendedor

Puede vender si:

- es Diseñador de Placas oficial; o
- tiene licencia comunitaria `active`.

La placa debe:

- pertenecer a la cuenta autora;
- estar aprobada;
- ser apta para marketplace;
- respetar las restricciones del personaje creador.

### Límites de anuncios

- Cuenta normal: **3 anuncios activos**.
- Cuenta con HC activo: **6 anuncios activos**.

HC se comprueba mediante `users_subscriptions` y `HABBO_CLUB` sobre personajes activos/no archivados vinculados a la cuenta.

No se usa `rank` como sustituto de HC.

---

## 11. Precio, comisión e ingresos

El vendedor elige sus ingresos:

- mínimo: **0 créditos**;
- máximo: **7 créditos**.

Comisión fija del hotel:

- **3 créditos**.

Por tanto:

- precio comprador: **3–10 créditos**;
- vendedor recibe: **0–7 créditos**;
- hotel absorbe siempre los **3 créditos** de comisión.

La interfaz muestra al comprador el precio final.

---

## 12. Compra

La compra tiene protecciones transaccionales e idempotentes.

### Reglas

- Se valida que el anuncio siga activo.
- Se valida el precio esperado antes de cobrar.
- Se bloquea la compra si el personaje comprador ya posee la placa.
- El comprador recibe una copia; no recibe autoría.
- Comprar con otro personaje de la misma cuenta autora **está permitido**.
- Comprar con el propio personaje que ya posee la placa queda bloqueado por duplicado.

### Online/offline

- Personaje offline: camino transaccional SQL.
- Personaje online: CreditBridge para respetar el saldo autoritativo del emulador.

### Payout

Tras entregar correctamente la placa:

- el payout se dirige al `creator_user_id` autor;
- si está offline se acredita en BD;
- si está online se usa CreditBridge;
- payout de 0 créditos completa sin transferencia.

Si la entrega falla después del cobro:

- refund completo al comprador.

Si la entrega ya ocurrió pero el payout queda ambiguo:

- no se quita la placa al comprador;
- no se hace un refund que duplicaría valor;
- la operación pasa a `manual_review`.

---

## 13. Estados de venta

Estados del motor:

- `paid_pending_delivery`
- `delivered_pending_payout`
- `completed`
- `refunded`
- `manual_review`

Tabla:

- `badge_marketplace_sales`

Anuncios:

- `badge_marketplace_listings`

El motor está diseñado para tolerar reintentos y evitar dobles cobros, dobles entregas o dobles payouts.

---

## 14. Ledger económico

Las compras del marketplace y los payouts generan trazabilidad económica.

El payout histórico corregido quedó registrado sin volver a mover créditos.

La reconciliación y los identificadores deterministas deben mantenerse en cualquier evolución futura del sistema.

No se debe reconstruir un payout ambiguo basándose únicamente en el saldo actual.

---

## 15. Notificaciones

Las notificaciones relevantes son a nivel de cuenta mediante `account_notifications`.

Se usan, entre otros casos, para:

- aprobación/rechazo de placas;
- aprobación de licencia;
- entrada/promoción desde lista de espera;
- aviso de inactividad;
- retirada de licencia;
- ventas y eventos asociados cuando corresponde.

Las notificaciones no sustituyen al ledger económico.

---

## 16. Derecho permanente de distribución del hotel

Cada placa aprobada puede conceder voluntariamente a Biribiri un derecho permanente de distribución.

Campo:

- `creator_badges.hotel_distribution_granted_at`

### Reglas

- voluntario;
- explícito;
- **permanente**;
- **irreversible**;
- **no exclusivo**.

El creador mantiene:

- autoría;
- uso;
- capacidad de regalar;
- capacidad de vender según sus permisos.

El hotel gana el derecho permanente a distribuir la placa mediante mecanismos oficiales, por ejemplo:

- eventos;
- premios;
- campañas;
- tienda oficial;
- otros sistemas controlados por Biribiri.

El derecho sobrevive al archivado del personaje creador o cambios posteriores de estado de la cuenta.

No existe endpoint de revocación.

La segunda concesión es idempotente y conserva la fecha original.

No mueve créditos ni cambia inventario, listings o autoría.

---

## 17. Tablas principales

Placas V1 utiliza o integra principalmente:

- `badge_submissions`
- `creator_badges`
- `badge_gifts`
- `badge_seller_licenses`
- `badge_marketplace_listings`
- `badge_marketplace_sales`
- `account_notifications`
- `credit_refunds`
- `credit_transactions`
- `purchase_operations`
- `users_badges`
- `users_subscriptions`
- `website_teams`
- `user_website_team`
- `account_characters`
- `emulator_heartbeats`

El marketplace nativo de furnis permanece separado.

---

## 18. Housekeeping / Filament

La administración integra:

- moderación de solicitudes de placas;
- solicitudes de vendedor;
- capacidad comunitaria;
- estados de licencia;
- aprobación;
- retirada;
- motivos y actividad;
- permisos específicos de housekeeping.

El hub `Solicitudes` agrupa la gestión relacionada sin mezclar los distintos estados de negocio.

---

## 19. QA completado

Antes del cierre V1 se realizaron pruebas manuales y automáticas.

### Compra — QA automatizado

Casos cubiertos:

1. precio cambiado antes del cobro;
2. duplicado antes del cobro;
3. fondos insuficientes;
4. compra offline completa;
5. fallo de entrega con refund automático;
6. fallo de payout → `manual_review`, comprador conserva placa;
7. entrega RCON simulada;
8. payout CreditBridge simulado;
9. venta pendiente antigua no se reanuda incorrectamente al comprar;
10. compra con otro personaje de la misma cuenta autora permitida.

### Inactividad/lista de espera

Verificado:

- aviso a 30 días;
- actividad en el mismo segundo limpia aviso;
- retirada a 45 días;
- motivo de retirada;
- desactivación de anuncios;
- promoción de la espera aprobada;
- reutilización del slot;
- notificación de retirada;
- notificación de promoción;
- rollback íntegro de fixtures.

### Derecho de distribución

Verificado:

- schema;
- ruta POST;
- controlador;
- compilación Blade;
- primera concesión;
- idempotencia de segunda concesión;
- créditos sin cambios;
- listings sin cambios;
- flag de marketplace sin cambios;
- rollback de smoke.

### Límite del QA

Las rutas online de RCON/CreditBridge se han probado mediante mocks en el QA automatizado. El comportamiento funcional real de compra offline y los flujos manuales principales fueron probados durante el desarrollo.

---

## 20. Archivos principales

Además del núcleo específico de Placas, V1 depende de infraestructura compartida de economía, presencia y notificaciones que debe viajar en el mismo checkpoint para que una instalación limpia reproduzca el comportamiento validado.

Infraestructura compartida relevante:

- `app/Services/CreditTransactionService.php`
- `app/Services/CreditRefundService.php`
- `app/Services/CreditBridgeClient.php`
- `app/Services/EmulatorPresenceService.php`
- `app/Services/AccountNotificationService.php`
- `app/Http/Controllers/AccountNotificationController.php`
- `app/Http/Controllers/Api/NitroExternalTextsController.php`
- `app/Models/AccountNotification.php`
- `routes/api.php`
- `routes/web.php`
- `public/renderer-config.json`
- `Emulator/CreditBridge/src/com/retro/creditbridge/CreditBridge.java`
- `Emulator/plugins/CreditBridge.jar`


Backend:

- `app/Http/Controllers/Marketplace/BadgeCreatorController.php`
- `app/Http/Controllers/Marketplace/BadgeGiftController.php`
- `app/Http/Controllers/Marketplace/BadgeMarketplaceListingController.php`
- `app/Http/Controllers/Marketplace/BadgeMarketplacePurchaseController.php`
- `app/Http/Controllers/Marketplace/BadgeSellerApplicationController.php`
- `app/Http/Controllers/Marketplace/BadgeHotelDistributionController.php`
- `app/Services/BadgePixelArtService.php`
- `app/Services/BadgeSubmissionModerationService.php`
- `app/Services/BadgeGiftService.php`
- `app/Services/BadgeSellerEligibilityService.php`
- `app/Services/BadgeMarketplaceListingService.php`
- `app/Services/BadgeMarketplacePurchaseService.php`
- `app/Console/Commands/BadgeSellerLicenseMaintenanceCommand.php`

UI:

- `resources/themes/atom/views/marketplace/badges.blade.php`
- `app/Filament/Pages/Applications.php`
- `app/Livewire/Filament/BadgeApplicationsTable.php`
- `app/Livewire/Filament/BadgeSellerApplicationsTable.php`

Configuración:

- `config/badge_marketplace.php`
- `app/Console/Kernel.php`

---

## 21. Datos de desarrollo

Durante la construcción de V1 se crearon placas, regalos, ventas, copias, notificaciones, archivos de preview y movimientos económicos de prueba.

La **limpieza de esos fixtures no forma parte del código V1** y debe ejecutarse después de conservar el código/documentación en Git.

La limpieza debe:

- inventariar primero;
- realizar backup;
- revertir exactamente el efecto económico neto de las pruebas;
- no volver a cobrar ni pagar;
- eliminar únicamente datos identificados como pruebas;
- retirar assets `USRBDG...` de prueba;
- limpiar previews/submissions de desarrollo;
- dejar las tablas funcionales y vacías/listas para uso real;
- verificar saldos e integridad al terminar.

---

## 22. Cierre

**Placas V1 está terminada.**

Cualquier evolución posterior debe partir de esta versión y mantener separadas:

- autoría;
- propiedad de copias;
- economía;
- licencia de vendedor;
- roles oficiales;
- marketplace;
- derecho de distribución del hotel.

**Ropa se diseñará en una sesión independiente y no debe implementarse copiando mecánicamente Placas.**
