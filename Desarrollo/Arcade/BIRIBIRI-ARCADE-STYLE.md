# Biribiri Arcade — Canonical UI Reference

## 1. Golden masters

**Space Invaders** y **Duck Hunt** son los dos _golden masters_ visuales del sistema Biribiri Arcade.

No son un mismo template recoloreado. Comparten lenguaje visual, proporciones y jerarquía, pero cada juego conserva identidad propia.

Regla práctica:

**70% sistema Biribiri Arcade común + 30% identidad específica del juego.**

Los nuevos arcades deben compararse siempre contra **ambos** antes de considerarse terminados.

---

## 2. Estructura canónica común

1. `NitroCardHeaderView` nativo.
2. Marco exterior gris/beige de Holo/Nitro.
3. Gabinete interior oscuro, biselado y con profundidad.
4. Cuatro tornillos visibles, uno cerca de cada esquina.
5. HUD superior grande y legible.
6. Pantalla de juego claramente enmarcada como monitor arcade.
7. Consola inferior de controles.
8. Controles agrupados a la **izquierda**.
9. Hueco libre después de los controles; no centrar controles por estética.
10. Acciones del sistema a la derecha:
   - SONIDO
   - REINICIAR
11. Barra inferior de ranking global.
12. Botón RÉCORDS que abre `ArcadeLeaderboardView`.

---

## 3. HUD superior

El HUD no debe sentirse como un dashboard web pequeño.

Referencias actuales:

- Space Invaders: HUD de ~78 px.
- Duck Hunt: HUD de ~76 px.
- Valores principales en torno a 25–27 px.
- El bloque de estado debe tener presencia similar al resto de celdas.

Campos típicos:

- PUNTUACIÓN
- NIVEL
- recurso específico del juego
- progreso/objetivo específico
- ESTADO

No es obligatorio que todos los juegos tengan exactamente el mismo número de columnas; sí deben conservar una jerarquía y peso visual equivalentes.

---

## 4. Estado de partida: obligatorio que sea visual y dinámico

`PREPARADO`, `JUGANDO`, `PAUSA` y `FIN` no son solo textos.

El bloque de estado debe cambiar visualmente según la fase:

### PREPARADO
- familia cian/azul
- icono del juego visible

### JUGANDO
- familia verde
- fondo verdoso
- sensación activa

### PAUSA
- familia amarillo/ámbar
- fondo ámbar oscuro

### FIN / GAME OVER
- familia rojo/rosa
- fondo rojizo

Cada juego debe tener **su propio icono de estado**:

- Space Invaders → alien
- Duck Hunt → pato
- Block Drop → tetrominó/pieza
- Pac-Man → Pac-Man

Evitar indicadores genéricos pequeños cuando el resto de golden masters utilizan iconografía grande.

---

## 5. Identidad de cada juego

La geometría común no implica una paleta común.

### Space Invaders
- azul/navy/cian
- estética espacial
- acción principal `ESPACIO` con accent morado
- alien como icono de estado

### Duck Hunt
- verde/bosque
- tonos tierra
- acción principal `CLIC` con accent amarillo/dorado
- pato como icono de estado

### Regla para nuevos juegos

Cada arcade debe definir como mínimo:

1. paleta de gabinete/HUD;
2. color de puntuación;
3. color de NIVEL;
4. icono de estado;
5. accent de acción principal;
6. ambientación de la pantalla/overlay.

No convertir todos los keycaps en el mismo color.

La acción principal debe destacar con un accent propio del juego cuando tenga sentido.

---

## 6. Controles

La forma general de las teclas puede ser común, pero la identidad no.

Ejemplos canónicos:

- Space Invaders: teclas de movimiento azules + `ESPACIO` morado.
- Duck Hunt: consola verde + `CLIC` dorado.

Los controles deben quedar a la izquierda y mantener aire visual antes de las acciones de sistema.

### Acciones de sistema

Estas sí deben ser reconocibles entre máquinas:

- `SONIDO ON` → verde canónico.
- `SONIDO OFF` → neutro/apagado.
- `REINICIAR` → ámbar/dorado.
- `RÉCORDS` → tratamiento compartido del sistema arcade.

---

## 7. Ranking

La barra compacta inferior es parte del sistema compartido:

- RANKING GLOBAL
- TU RÉCORD
- TU PUESTO
- JUGADORES
- RÉCORDS

Los valores compactos usan el amarillo canónico establecido con Duck Hunt.

La ventana completa utiliza el componente reutilizable:

`ArcadeLeaderboardView`

No crear un modal de récords distinto para cada juego salvo que exista una razón funcional fuerte.

---

## 8. Canvas y tipografía

- Pixel-art: correcto dentro del gameplay.
- Textos de interfaz fuera del canvas: deben verse nítidos.
- Evitar pixelar títulos informativos, instrucciones o paneles innecesariamente.
- Si se dibuja texto dentro del canvas, usar backing resolution suficiente para evitar texto borroso.

---

## 9. Qué puede variar y qué no

### Debe permanecer común
- marco Holo/Nitro;
- estructura de gabinete;
- cuatro tornillos;
- jerarquía del HUD;
- comportamiento visual de los estados;
- controles a la izquierda;
- acciones del sistema a la derecha;
- sonido ON verde;
- ranking inferior;
- modal compartido de récords;
- término visible `NIVEL`.

### Debe tener identidad propia
- paleta;
- icono del estado;
- accent de acción principal;
- decoración de HUD;
- colores de score/nivel;
- overlays;
- gameplay;
- audio del juego.

---

## 10. Convención de nombre

Usar **NIVEL** en la interfaz de todos los arcades.

La lógica interna puede seguir usando `round`, `wave`, etc. si conviene técnicamente.

---

## 11. Política de archivo Biribiri

`Desarrollo/Arcade/` es el archivo canónico de todo lo exclusivo creado para Biribiri Arcade.

Los archivos que necesita Nitro para funcionar permanecen en sus rutas runtime, por ejemplo:

`xampp/htdocs/nitro-react/src/components/...`

Pero cada juego/componente exclusivo debe tener también una copia localizable en:

`Desarrollo/Arcade/`

Cuando se modifique un arcade:

1. modificar/probar el runtime;
2. copiar el estado final a `Desarrollo/Arcade`;
3. actualizar esta guía si cambia una convención;
4. subir runtime y archivo de desarrollo de forma controlada.

No dejar una feature exclusiva únicamente enterrada dentro de Nitro, renderer, plugin o build.

---

## 12. Golden masters archivados

Referencias principales:

- `Desarrollo/Arcade/Frontend/SpaceInvaders/`
- `Desarrollo/Arcade/Frontend/DuckHunt/`
- `Desarrollo/Arcade/Frontend/Shared/ArcadeLeaderboardView.*`

Juegos pendientes de adaptación visual:

- `Desarrollo/Arcade/Frontend/BlockDrop/`
- `Desarrollo/Arcade/Frontend/PacMan/`

Antes de retocar Block Drop, Pac-Man o cualquier arcade nuevo, comparar directamente sus TSX/SCSS con los dos golden masters.
