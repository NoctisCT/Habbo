# Documentación - Tragaperras Biribiri V1

Actualizado: 02/09/2026

## Estado

**TRAGAPERRAS BIRIBIRI V1 TERMINADA**

La V1 funcional está cerrada para las tres monedas del hotel:

- Créditos
- Diamantes
- Duckets

La única comprobación pendiente de producción es observar el primer jackpot real y confirmar visualmente que el aviso global 5014 aparece una sola vez para todo el hotel.

No se considera bloqueante para la V1: el backend, el pago, el jackpot y el emisor global ya están instalados; falta únicamente esa validación runtime porque no se fuerza un jackpot artificial sobre la economía real.

---

## Objetivo del sistema

Crear tragaperras propias de Biribiri integradas con Morningstar y Nitro, con:

- una máquina física distinta por moneda;
- economía autoritativa en servidor;
- jackpot progresivo global por moneda;
- saldos nativos de Arcturus;
- UI propia de Nitro;
- sonidos propios;
- auditoría de tiradas;
- sincronización del jackpot en tiempo real;
- interacción física desde la casilla frontal visual de la máquina;
- catálogo integrado en la categoría Biribiri.

La instancia física del furni no define la economía. La moneda se resuelve por el tipo de interacción/base configurado y toda la lógica económica está centralizada en el motor compartido.

---

## Máquinas

| Moneda | Base item | Sprite | Nombre | Interaction |
|---|---:|---:|---|---|
| Créditos | 71244564 | 71244564 | `creds11_slot` | `holo_slot_credits` |
| Diamantes | 71244567 | 71244567 | `diams11_slot` | `holo_slot_diamonds` |
| Duckets | 71244572 | 71244572 | `ducks11_slot` | `holo_slot_duckets` |

Las tres son máquinas independientes visualmente, pero cada moneda comparte su jackpot global entre todas las máquinas de ese tipo.

El slot genérico antiguo permanece como furni decorativo y no forma parte de esta economía.

---

## Catálogo

Jerarquía administrativa:

```text
Personal
└── Biribiri
    └── Tragaperras
```

`Biribiri` funciona como categoría compartida para sistemas custom propios del hotel.

---

## Arquitectura

### Backend

Proyecto:

```text
Desarrollo/Tragaperras
```

Plugin desplegado:

```text
Emulator/plugins/Tragaperras.jar
```

El motor es compartido. No existen tres plugins separados.

Piezas principales:

- `TragaperrasPlugin`
- `SlotMachineManager`
- `SlotCurrency`
- `SlotEconomy`
- `InteractionSlotMachine`
- `InteractionSlotDiamonds`
- `InteractionSlotDuckets`
- handler de tirada `SpinSlotMachine`

### Nitro

UI:

```text
xampp/htdocs/nitro-react/src/components/tragaperras/SlotMachineView.tsx
xampp/htdocs/nitro-react/src/components/tragaperras/SlotMachineView.scss
```

API React:

```text
xampp/htdocs/nitro-react/src/api/tragaperras/
```

Renderer fuente:

```text
xampp/htdocs/nitro-react/submodules/renderer
```

Nitro usa además la copia runtime instalada en:

```text
xampp/htdocs/nitro-react/node_modules/@nitrots/nitro-renderer
```

Los cambios custom del renderer deben mantenerse sincronizados en ambas copias durante desarrollo.

---

## Protocolo de packets

| ID | Dirección | Uso |
|---:|---|---|
| 5042 | Nitro → servidor | Solicitud de tirada |
| 5043 | Servidor → Nitro | Abrir máquina |
| 5044 | Servidor → Nitro | Resultado autoritativo |
| 5045 | Servidor → Nitro | Actualización de jackpot compartido |
| 5046 | Servidor → Nitro | Cierre/invalidez de sesión física |
| 5014 | Servidor → Nitro | Aviso global reutilizado de Subastas |

### 5043 OPEN

Payload:

1. `itemId`
2. `bet`
3. `jackpot`
4. `balance`
5. `currencyKey`
6. `currencyType`

### 5044 RESULT

Payload:

1. `success`
2. `message`
3. `itemId`
4. `balanceAfter`
5. `jackpotAfter`
6. `normalPrize`
7. `jackpotPrize`
8. `jackpotHit`
9. `symbol1`
10. `symbol2`
11. `symbol3`

### 5045 STATE

Payload:

1. `currencyKey`
2. `jackpot`

Se difunde únicamente a sesiones abiertas válidas de la misma moneda.

### 5046 CLOSE

Se añadió como mecanismo de cierre/invalidez de la sesión física.

La seguridad de la economía no depende del cierre visual del menú: cada tirada vuelve a comprobar en servidor que la sesión y la posición física siguen siendo válidas.

---

# Economía

## Créditos

Apuesta:

```text
5 créditos
```

Distribución por tirada:

- burn: 1
- jackpot: +1
- treasury: +3

Inicial:

- jackpot: 1000
- treasury: 1000
- inyección total inicial: 2000

Premios normales:

| Premio | Probabilidad |
|---:|---:|
| 6 | 11 % |
| 10 | 4 % |
| 20 | 1,3 % |
| 50 | 0,3 % |
| 100 | 0,1 % |
| 250 | 0,01 % |

Aproximadamente:

- hit normal: 16,71 %
- sin premio normal: 83,29 %
- EV de premio normal: 1,595 créditos/tirada

Jackpot:

| Treasury | Probabilidad |
|---|---:|
| < 1000 | 0 |
| 1000–1499 | 1 / 200000 |
| 1500–1999 | 1 / 100000 |
| 2000–2499 | 1 / 50000 |
| 2500–2999 | 1 / 25000 |
| 3000+ | 1 / 11000 |

---

## Diamantes

Apuesta:

```text
20 diamantes
```

Distribución:

- burn: 4
- jackpot: +4
- treasury: +12

Inicial:

- jackpot: 4000
- treasury: 4000
- inyección total inicial: 8000

Premios:

| Premio | Probabilidad |
|---:|---:|
| 24 | 11 % |
| 40 | 4 % |
| 80 | 1,3 % |
| 200 | 0,3 % |
| 400 | 0,1 % |
| 1000 | 0,01 % |

Jackpot:

| Treasury | Probabilidad |
|---|---:|
| < 4000 | 0 |
| 4000–5999 | 1 / 200000 |
| 6000–7999 | 1 / 100000 |
| 8000–9999 | 1 / 50000 |
| 10000–11999 | 1 / 25000 |
| 12000+ | 1 / 11000 |

La moneda nativa se resuelve mediante el tipo estacional primario de Arcturus. En la configuración actual corresponde al tipo 5.

No se modifica directamente `users_currency` para pagar tiradas.

---

## Duckets

Apuesta:

```text
10 duckets
```

Distribución:

- burn: 2
- jackpot: +2
- treasury: +6

Inicial:

- jackpot: 1000
- treasury: 1000
- inyección total inicial: 2000

Premios:

| Premio | Probabilidad |
|---:|---:|
| 12 | 11 % |
| 20 | 4 % |
| 40 | 1,3 % |
| 100 | 0,3 % |
| 200 | 0,1 % |
| 500 | 0,01 % |

Jackpot:

| Treasury | Probabilidad |
|---|---:|
| < 1000 | 0 |
| 1000–1499 | 1 / 200000 |
| 1500–1999 | 1 / 100000 |
| 2000–2499 | 1 / 50000 |
| 2500–2999 | 1 / 25000 |
| 3000+ | 1 / 11000 |

La moneda usa el tipo nativo `0`.

EV de premio normal:

```text
3,19 duckets/tirada
```

---

# Saldos y autoridad

El servidor es completamente autoritativo.

Nitro nunca decide:

- si una tirada es válida;
- cuánto se descuenta;
- qué premio sale;
- si hay jackpot;
- cuánto vale el jackpot;
- qué saldo queda.

APIs nativas utilizadas:

### Créditos

```java
HabboInfo.getCredits()
Habbo.giveCredits(int)
```

### Diamantes

```java
HabboInfo.getCurrencyAmount(seasonal.primary.type)
Habbo.givePoints(int)
```

Configuración actual:

```text
seasonal.primary.type = 5
```

### Duckets

```java
HabboInfo.getCurrencyAmount(0)
Habbo.givePoints(0, delta)
```

No se realizan mutaciones SQL directas del saldo del jugador durante una tirada.

---

# Persistencia

Tablas:

```text
holo_slots_state
holo_slots_spins
```

## holo_slots_state

Mantiene por moneda:

- jackpot
- treasury
- total wagered
- total burn
- total normal payout
- total jackpot payout
- total spins
- total admin injected
- jackpot count
- spins since jackpot
- timestamps

## holo_slots_spins

Auditoría por tirada:

- currency
- user
- room
- item
- bet
- burn
- normal prize
- jackpot prize
- jackpot hit
- balance before
- balance after
- jackpot after
- treasury after
- timestamp

---

# Transacción de tirada

La tirada se ejecuta con:

- sesión por usuario;
- lock por usuario;
- validación del item y moneda;
- validación física;
- lectura/bloqueo de estado económico;
- aplicación del saldo mediante API nativa;
- persistencia del resultado;
- commit SQL;
- respuesta autoritativa;
- actualización 5045;
- anuncio global si corresponde.

El `SlotSpinResult` se crea después del `connection.commit()`.

Por tanto, el aviso global de jackpot se genera únicamente a partir de un resultado ya confirmado.

No existe 2PC real entre la API de saldo del emulador y MariaDB, pero existe lógica de verificación/compensación para fallos conocidos.

---

# Jackpot progresivo compartido

Cada moneda tiene un único estado global.

Ejemplo:

```text
Créditos máquina A ─┐
Créditos máquina B ─┼── holo_slots_state[credits]
Créditos máquina C ─┘
```

El furni físico no contiene un bote independiente.

Tras una tirada correcta:

```text
5044 RESULT
    ↓
5045 STATE a sesiones de la misma moneda
```

El spinner también puede recibir el 5045 después de su 5044; es intencionado e inocuo.

El sistema 5045 fue probado en runtime y quedó cerrado.

---

# Aviso global de jackpot

Cuando:

```java
result.jackpotHit == true
```

el servidor ejecuta una única llamada hotel-wide y reutiliza el packet global 5014 ya existente en Subastas.

Formato previsto:

```text
¡JACKPOT! <usuario> ha ganado <cantidad> <moneda> en las tragaperras.
```

Se envía una copia a cada usuario online.

Un fallo al enviar a un cliente individual no revierte una tirada que ya ha sido confirmada.

## Estado

- implementación: OK
- build/deploy: OK
- Arcturus reiniciado: OK
- consumidor Nitro 5014: confirmado
- prueba de un jackpot real: **PENDIENTE**

No se fuerza un jackpot artificial únicamente para cerrar esta comprobación.

---

# Interacción física

## Regla

La máquina solo puede usarse desde su **casilla frontal visual exacta**.

No basta con:

- estar al lado;
- estar detrás;
- estar en diagonal;
- estar a una casilla cualquiera de distancia.

## Doble clic

Si el usuario está lejos:

```text
doble clic
→ calcula frontal visual
→ camina a esa casilla
→ callback al llegar
→ revalida posición/item/room
→ mira a la máquina
→ abre la UI
```

Si ya está en la casilla válida, puede abrirse directamente.

## Rotación visual

Morningstar usa:

```java
RoomLayout.getTileInFront(tile, rotation)
```

pero los sprites elegidos de tragaperras presentan un desfase visual respecto a la rotación numérica del furni.

Corrección final:

```java
visualFrontRotation = (item.getRotation() + 2) % 8;
```

Ejemplo para furni en rotación 0:

```text
Morningstar rot 0 → y-1
frontal visual    → rot 2 → x+1
```

Esta corrección está centralizada en `getExactFrontTile()` y se reutiliza para:

- target de walking;
- apertura;
- validación de sesión;
- validación de cada tirada;
- vigilancia física.

Fue comprobada en runtime y el avatar llega a la casilla visualmente frontal correcta.

## Alejarse

Comportamiento observado actualmente:

- al abandonar la casilla frontal, el menú puede permanecer visible;
- el servidor ya no permite utilizar correctamente la máquina;
- cada `TIRAR` revalida la posición;
- la casilla frontal solo admite físicamente un avatar.

Por decisión de producto, esto no bloquea la V1.

La seguridad nunca depende de que el cliente cierre visualmente la ventana.

---

# UI Nitro

La interfaz es compartida por las tres monedas y recibe de servidor:

- moneda;
- tipo de icono;
- apuesta;
- saldo;
- jackpot;
- símbolos;
- premios;
- estado de jackpot.

Elementos principales:

- título dinámico por moneda;
- jackpot;
- tres rodillos;
- apuesta;
- botón `TIRAR`;
- saldo;
- premio actual;
- estados de victoria/error.

La UI no calcula pagos.

---

# Sonidos

Sonidos custom:

```text
holo_slot_pull
holo_slot_spin
holo_slot_win
holo_slot_jackpot
```

Assets:

```text
xampp/htdocs/public/nitro-assets/sounds/
```

Usan `PlaySound` de Nitro y respetan el volumen del cliente.

El sonido de premio/jackpot se reproduce al finalizar la animación correspondiente.

---

# QA realizado

## Créditos

QA formal realizado sobre cientos de tiradas.

Una ejecución cerrada registró:

```text
437 spins
2185 wagered
437 burn
702 normal payouts
0 jackpots
PASS 33
WARN 1
FAIL 0
```

Se validaron:

- reconstrucción de economía;
- saldo de auditoría;
- premios válidos;
- ausencia de negativos;
- consistencia de treasury/jackpot.

## Diamantes

Economía activada y probada en runtime.

Se validó:

- apuesta 20;
- premios;
- saldo nativo;
- aislamiento respecto a Créditos;
- jackpot independiente;
- UI multicurrency.

Estado: cerrado.

## Duckets

Prueba manual:

```text
20 tiradas correctas
```

QA formal:

```text
PASS=19
WARN=0
FAIL=0
```

Estado: cerrado.

## Jackpot compartido 5045

Probado en runtime entre sesiones.

Estado: cerrado.

## Posición física

Probado en runtime:

- caminar hacia la máquina;
- llegada;
- orientación del avatar;
- apertura;
- corrección del frontal visual.

Estado: cerrado.

---

# Decisiones de producto

## No se incluyen por ahora

### Palanca física animada

Descartada como prioridad.

Se considera un detalle visual que puede resultar innecesario o incluso cutre frente al coste de implementación.

### Estadísticas personales

No se mostrarán:

- tiradas;
- dinero total apostado;
- pérdidas;
- balance histórico;
- RTP personal.

Motivo: pueden desanimar al jugador y reducir el uso del sistema.

### Estadísticas de sesión

Misma decisión.

No se mostrará un resumen de:

```text
apostado / ganado / perdido
```

### Historial personal de tiradas

No se prioriza por el mismo motivo.

### Bloqueo adicional de "máquina ocupada"

No se considera necesario actualmente.

La interacción exige una única casilla frontal física y en ella solo se coloca un avatar.

---

# Pendiente

## Pendiente real de cierre

### 1. QA del primer jackpot global real

Cuando ocurra el primer jackpot natural:

comprobar que:

- el ganador recibe correctamente el pago;
- 5045 muestra el bote reiniciado/actualizado;
- aparece el aviso 5014;
- aparece **exactamente una vez** por usuario;
- muestra usuario, cantidad y moneda correctos;
- no se genera para premios normales.

Este es el único pendiente técnico de validación de la V1.

---

## Posibles mejoras futuras

No forman parte de la V1 y no están comprometidas.

### 2. Últimos jackpots del hotel

Mostrar un pequeño historial global:

```text
Usuario — cantidad — moneda — fecha
```

Puede alimentarse automáticamente desde la auditoría existente o desde una tabla específica de jackpots.

No requiere estadísticas negativas del jugador.

### 3. Récord visible

Mostrar en la UI algo como:

```text
Récord: 8.421 créditos — Usuario
```

Preferiblemente por moneda, ya que el jackpot es global por moneda y no por item físico.

### 4. Tabla de premios

Añadir una vista `Premios` que explique qué cantidades puede entregar la máquina.

No es necesario exponer las probabilidades si no se desea.

Debe ser dinámica por moneda:

- Créditos: 6 / 10 / 20 / 50 / 100 / 250
- Diamantes: 24 / 40 / 80 / 200 / 400 / 1000
- Duckets: 12 / 20 / 40 / 100 / 200 / 500

### 5. Presentación especial de jackpot

Posible mejora puramente visual:

- final de rodillos más marcado;
- `JACKPOT` destacado;
- animación especial del marco/UI;
- contador visual del premio;
- uso reforzado del sonido `holo_slot_jackpot`.

No debe modificar el resultado ni retrasar la autoridad del servidor.

---

# Operación y mantenimiento

## Backend

Después de modificar Java:

```text
mvn clean package
```

Desplegar el JAR y reiniciar Arcturus.

## Nitro

Después de modificar Nitro/renderer:

```text
yarn build
```

No utilizar el cliente durante el build.

Después:

```text
Ctrl+F5
```

## Renderer

Mantener sincronizados:

```text
submodules/renderer
node_modules/@nitrots/nitro-renderer
```

La copia de `submodules/renderer` es la fuente custom a conservar.

---

# Reglas de seguridad para cambios futuros

1. Diagnosticar la fuente local actual antes de parchear.
2. No construir patches desde estructuras históricas asumidas.
3. Backup timestamped antes de mutar.
4. Cambios pequeños y verificables.
5. Build antes de deploy.
6. Rollback automático ante fallo crítico.
7. No tocar saldos de jugador por SQL.
8. No cambiar economía sin una decisión explícita.
9. No reutilizar packets sin comprobar colisiones.
10. No depender del cliente para reglas económicas o físicas.
11. Mantener UTF-8 sin BOM en archivos custom.
12. No mezclar cambios de otras features en commits de Tragaperras.

---

# Cierre V1

La V1 dispone de:

- tres monedas;
- tres furnis;
- un motor compartido;
- economía cerrada y auditada;
- saldo nativo;
- jackpots progresivos;
- jackpot compartido en tiempo real;
- UI multicurrency;
- sonidos;
- auditoría;
- catálogo Biribiri;
- anuncio global implementado;
- interacción física frontal.

**Estado final: V1 terminada.**

Pendiente únicamente:

```text
observar y validar el primer aviso global de jackpot real
```
