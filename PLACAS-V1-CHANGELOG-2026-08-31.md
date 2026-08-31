# Placas V1 — changelog de cierre

Fecha: 31/08/2026  
Estado: V1 terminada

## Creación y moderación
- Creación de placas desde Marketplace.
- Preview final 40×40.
- Coste de 10 créditos.
- Pipeline de assets y publicación `USRBDG...`.
- Moderación Filament.
- Refund idempotente al rechazar.
- Entrega al personaje creador al aprobar.

## Autoría y regalos
- Autoría persistente por `creator_user_id`.
- `Mis placas`.
- Regalo por 3 créditos.
- Regalo entre personajes de la misma cuenta permitido.
- Bloqueo de duplicados antes del cobro.
- El receptor no adquiere autoría ni derecho de reventa.

## Roles y licencias
- Separación entre `users.rank` y equipos funcionales.
- Equipo oficial `Diseñador de Placas`.
- Diseñadores oficiales venden sin consumir plaza comunitaria.
- Licencia comunitaria con mínimo de 5 placas aprobadas.
- Máximo 3 plazas comunitarias.
- Estados definitivos: `pending`, `waitlisted`, `active`, `revoked`.
- `waitlisted` reservado a solicitudes aprobadas sin plaza.

## Marketplace
- Listings por cuenta autora.
- Límite normal 3 / HC 6.
- Ingreso vendedor 0–7.
- Comisión hotel fija 3.
- Precio comprador 3–10.
- Desactivación/reactivación de listings.
- Contadores y filtros sin recarga.

## Compra y economía
- Compra transaccional.
- Caminos online/offline.
- Bloqueo de duplicados.
- Compra con otro personaje de la cuenta autora permitida.
- Refund por fallo de entrega.
- `manual_review` si la entrega ocurrió pero el payout es ambiguo.
- Payout al personaje autor.
- Ledger de payout.
- Notificación al vendedor.

## Inactividad
- Aviso a 30 días.
- Retirada a 45 días.
- Mínimo 15 días desde el aviso.
- Actividad posterior limpia warning, incluido mismo segundo.
- Retirada desactiva anuncios activos.
- Promoción automática de la espera aprobada más antigua.
- Scheduler diario 03:15.

## Derecho de distribución
- Concesión permanente, no exclusiva e irreversible a Biribiri.
- Timestamp monotónico por placa.
- Sin revocación.
- Sin cambios económicos, de inventario, autoría o marketplace.

## QA de cierre
- 10 casos automatizados del motor de compra.
- QA transaccional completo de inactividad/lista de espera.
- Smoke transaccional del derecho de distribución.
- Rollbacks verificados.
- Datos reales no modificados por los QA finales.
