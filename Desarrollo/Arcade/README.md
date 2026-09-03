# Biribiri Arcade

Este directorio centraliza el código y la documentación **exclusivos de Biribiri Arcade**.

## Política

La copia runtime necesaria para que Nitro funcione puede vivir en:

`xampp/htdocs/nitro-react/...`

pero el trabajo exclusivo de Biribiri debe quedar además archivado y localizable aquí.

**No volver a dejar un arcade únicamente enterrado dentro de Nitro.**

## Estructura

- `Frontend/SpaceInvaders/` — golden master, FINAL.
- `Frontend/DuckHunt/` — golden master, FINAL.
- `Frontend/BlockDrop/` — snapshot actual antes de adaptación estética.
- `Frontend/PacMan/` — snapshot actual antes de adaptación estética.
- `Frontend/Shared/` — leaderboard y componentes comunes.
- `Backend/ArcadeCore/` — espejo del proyecto backend compartido si existe localmente.
- `RendererProtocol/` — archivos custom de protocolo arcade detectados en el renderer source.
- `BIRIBIRI-ARCADE-STYLE.md` — reglas visuales.
- `MANIFEST.md` — mapa rápido de juegos, máquinas, packets y estado.

## Fuente runtime

Los snapshots de este directorio **no sustituyen** automáticamente las rutas que compila Nitro.

Son archivo de desarrollo, referencia y recuperación.

Al terminar una modificación:
- probar el runtime;
- sincronizar el snapshot correspondiente;
- commit controlado.

## Golden masters

Space Invaders y Duck Hunt son la referencia visual oficial.

Block Drop y Pac-Man deben adaptarse tomando ambos como base, no copiando literalmente uno de los dos.
