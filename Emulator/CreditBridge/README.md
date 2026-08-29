# CreditBridge

CreditBridge es el plugin de Morningstar utilizado por la capa web para
realizar movimientos de créditos de forma segura cuando un personaje
está conectado.

## Regla fundamental

Cuando un personaje está conectado, el saldo autoritativo puede estar en
memoria del emulador.

Por tanto:

**Laravel no debe modificar directamente `users.credits` para realizar
un cobro o una devolución de un personaje conectado.**

## Operaciones

CreditBridge soporta:

- débito de créditos;
- crédito de créditos;
- idempotencia mediante `transaction_id`;
- ejecución online;
- crédito offline cuando corresponde;
- journal persistente.

Comandos RCON utilizados:

- `DebitCredits`;
- `CreditCredits`.

## Identificador de transacción

Cada operación utiliza un identificador estable.

Repetir una petición con el mismo identificador no debe aplicar el
movimiento una segunda vez.

Ejemplo conceptual:

- débito: `<purchase_id>`;
- devolución: `refund-<purchase_id>`.

## Journal

Tabla:

`credit_bridge_transactions`

Permite conocer:

- usuario;
- operación;
- cantidad;
- saldo anterior;
- saldo posterior;
- estado;
- identificador de transacción.

Los estados de CreditBridge se utilizan también durante recuperación de
crashes y reconciliación.

## Laravel

Cliente:

`app/Services/CreditBridgeClient.php`

Servicios autorizados que consumen CreditBridge directamente:

- `CreditTransactionService`;
- `AdminCreditAdjustmentService`.

El resto de la aplicación no debe saltarse estos servicios para
operaciones económicas equivalentes.

## Compras con créditos

Servicio:

`app/Services/CreditTransactionService.php`

Reglas:

### Personaje desconectado

El débito se realiza dentro de una transacción SQL junto con la
operación de negocio.

Si la operación falla, la transacción hace rollback.

### Personaje conectado

1. CreditBridge realiza el débito.
2. Laravel confirma el movimiento.
3. Laravel ejecuta la operación de negocio.
4. Se escribe ledger.
5. Se completa la compra.

Si falla después del débito:

1. CreditBridge devuelve la misma cantidad.
2. La devolución utiliza `refund-<purchase_id>`.
3. La devolución es idempotente.
4. La compra termina `refunded`.

## Crash recovery

Si CreditBridge aplicó un movimiento y PHP murió antes de completar el
journal de Laravel, el reintento:

- busca primero el `transaction_id`;
- valida usuario, operación, cantidad y balances;
- no repite el movimiento;
- completa el ledger pendiente.

Los balances de CreditBridge son históricos.

No debe exigirse que el saldo actual del personaje siga siendo igual a
`balance_after`, porque pueden existir movimientos legítimos
posteriores.

## Estados inseguros

Si no puede determinarse con seguridad si un movimiento fue aplicado,
no debe realizarse un segundo cargo a ciegas.

La operación debe terminar en revisión manual cuando corresponda.

## Ajustes administrativos

`AdminCreditAdjustmentService` utiliza CreditBridge para ajustes de
créditos sobre personajes conectados.

Reglas:

- solo administrador de máximo nivel;
- cantidad positiva o negativa;
- motivo obligatorio;
- UUID;
- ledger;
- saldo anterior/posterior;
- idempotencia;
- auditoría.

## Reconciliación

Laravel dispone de:

`php artisan credits:reconcile`

Es una herramienta de solo lectura.

Comprueba coherencia entre:

- `purchase_operations`;
- `admin_credit_adjustments`;
- `credit_transactions`;
- `credit_bridge_transactions`.

## Logging

Los comandos RCON pueden aparecer en el log de Morningstar.

En desarrollo, `DEBUG` puede mostrar:

- `user_id`;
- cantidad;
- UUID;
- balances;
- resultado RCON.

En producción es preferible mantener los detalles en `DEBUG` y utilizar
`INFO` para eventos operativos generales.

El log del emulador no sustituye el journal ni el ledger persistente.

## Alcance

CreditBridge protege las operaciones que atraviesan esta infraestructura.

No significa todavía que absolutamente todos los movimientos nativos de
créditos del emulador estén registrados en un ledger global.

Ese sistema está definido como trabajo futuro en:

`xampp/htdocs/docs/PENDIENTES.md`

## Build

Entorno utilizado:

- Morningstar 3.6.x;
- Java 23 para compilación/ejecución local;
- bytecode objetivo Java 16.

JAR activo:

`Emulator/plugins/CreditBridge.jar`

Código fuente:

`Emulator/CreditBridge`

## Principios que no deben romperse

1. No modificar créditos directamente en SQL si el personaje está
   conectado.
2. Toda operación económica web debe tener un identificador idempotente.
3. No confiar en el navegador como fuente del precio.
4. Una operación incierta no se repite a ciegas.
5. Las compensaciones también deben ser idempotentes.
6. Los saldos antes/después deben quedar auditables.
7. Los sistemas nuevos deben integrarse con reconciliación.