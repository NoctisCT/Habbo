# Biribiri — Roadmap del vestidor

## Objetivo

Convertir el vestidor en una herramienta propia de Biribiri, más útil y divertida que el editor clásico, sin perder compatibilidad con el sistema de figuras.

## Prioridad alta

### 1. Selector libre HEX / RGB

Permitir recolorear prendas con un selector de color real:

- HEX (`#RRGGBB`)
- RGB
- selector visual
- colores recientes
- favoritos de color
- copiar/pegar color

La implementación debe estudiar cómo Hobba aplica color libre sobre las prendas para evitar limitar el sistema a la paleta estándar de Habbo.

### 2. Conjuntos guardados

- Guardar un look completo.
- Ponerle nombre.
- Cargarlo con un clic.
- Renombrar y borrar conjuntos.
- Miniatura/preview del avatar.

Ejemplos: `Arcade`, `Formal`, `Halloween`, `Verano`.

### 3. Randomizador con candados

Botón de outfit aleatorio con posibilidad de bloquear partes:

- pelo
- cara
- camiseta
- chaqueta
- pantalón
- zapatos
- accesorios
- colores

Ejemplo: bloquear el pelo y randomizar todo lo demás.

## Prioridad media

### 4. Favoritos

Marcar prendas y accesorios con corazón/estrella y disponer de una pestaña `Favoritos`.

### 5. Historial + deshacer / rehacer

- Deshacer el último cambio.
- Rehacer.
- Historial de looks recientes.
- Recuperar fácilmente un aspecto anterior.

### 6. Búsqueda y filtros

- Buscar por nombre/tag.
- Categoría.
- Color.
- Favoritos.
- Colección/temporada si existe metadata.
- Rareza o procedencia si Biribiri añade esas capas en el futuro.

### 7. Compartir / copiar look

- Copiar el código del outfit/figure.
- Generar un código de look de Biribiri.
- Abrir un look compartido directamente en el vestidor.
- Posible acción `Copiar look` desde el perfil de otro usuario.

Esto puede evolucionar más adelante a una galería social de looks.

### 8. Paletas y herramientas de color

- Colores favoritos.
- Historial de colores.
- Aplicar un mismo color a varias prendas compatibles.
- Paletas coordinadas.
- Copiar color de una prenda a otra.

### 9. Vista previa avanzada

- Zoom.
- Giro.
- Distintas poses/acciones.
- Fondo claro/oscuro.
- Preview más grande antes de guardar.

## Futuro

### Galería de looks de la comunidad

- Publicar conjuntos.
- Guardar conjuntos de otros usuarios.
- Abrirlos directamente en el vestidor.
- Likes/favoritos.
- Tendencias o destacados.

No es prioridad para la primera versión, pero `Conjuntos + compartir looks` deja preparada la base.

## Principios

- Debe sentirse Biribiri, no un simple parche del editor clásico.
- Mantener interfaz compacta y coherente con Holo/Habbo.
- Priorizar funciones que el usuario realmente utilice repetidamente.
- Evitar añadir complejidad visual gratuita.
- Todo código exclusivo de esta función debe archivarse bajo `Desarrollo/Vestidor/` además de permanecer en sus rutas runtime necesarias.
