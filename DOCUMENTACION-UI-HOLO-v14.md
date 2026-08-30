# DOCUMENTACIÓN UI — HOLO

**Versión estable:** Holo Hobba Classic v14  
**Estado:** checkpoint visual estable  
**Cliente:** Nitro React 2.1.1  
**Renderer:** Nitro Renderer 1.6.6  

---

## 1. Objetivo visual

Holo debe sentirse como un **cliente Habbo clásico refinado**, no como una aplicación web moderna.

La referencia visual principal es **Hobba**, especialmente por:

- sensación de cliente clásico;
- densidad visual correcta;
- ventanas con aspecto de interfaz de juego;
- menús compactos;
- integración natural de pixel art;
- pocos redondeos modernos;
- ausencia de estética “dashboard”, “SaaS” o “skin de WordPress”.

No se busca copiar Hobba 1:1. La intención es crear una identidad propia de Holo a partir de ese lenguaje visual.

---

## 2. Elementos aprobados al 100% — NO TOCAR

Los siguientes elementos se consideran ya resueltos visualmente y no deberían rediseñarse sin un motivo claro.

### 2.1 Toolbar inferior

La barra inferior actual queda aprobada.

Se valora:

- estructura continua;
- sensación de cliente clásico;
- buena integración de los iconos pixel;
- densidad correcta;
- ausencia de botones flotantes redondos/pills;
- chat y barra de amigos integrados en el mismo lenguaje.

No volver al sistema inicial de Nitro basado en botones circulares independientes.

---

### 2.2 Botón del avatar en la toolbar

El botón de la cara del avatar queda aprobado en **v14**.

Cambios consolidados:

- eliminado el círculo beige anterior;
- integrado en el mismo lenguaje visual que el resto de slots del toolbar;
- sin marco pesado;
- sin sensación de elemento ajeno al resto de la barra.

---

### 2.3 Menú desplegable del avatar

El menú que aparece al pulsar la cara del avatar queda aprobado en **v14**.

Se valora:

- tamaño mayor que en las primeras versiones;
- iconos visibles;
- iconos a tamaño nativo;
- estilo coherente con la barra inferior;
- sin cajas pesadas;
- sin reescalado que degrade los sprites.

Regla importante:

**No aplicar `transform: scale(...)` a iconos pixelados.**

---

### 2.4 HUD superior derecho

El HUD de monedas, HC y acciones queda aprobado.

Se valora:

- cantidades suficientemente grandes y legibles;
- HC aprovechando correctamente el espacio;
- estructura compacta;
- agrupación clara;
- sensación de HUD de juego.

---

### 2.5 Menú contextual del avatar

El menú con opciones como:

- Decorate Room
- Change Looks
- Dance
- Actions
- Signs

queda aprobado al 100%.

Es una de las principales referencias internas para futuras mejoras de otros menús.

Características deseadas:

- compacto;
- elegante;
- oscuro;
- bordes discretos;
- filas finas;
- jerarquía clara;
- sensación de cliente clásico.

---

### 2.6 Nombre flotante del avatar

La pequeña placa flotante con el nombre del avatar queda aprobada.

Debe mantenerse separada conceptualmente de los bocadillos de chat.

Regla:

**Modificar el nombre flotante no debe alterar el tamaño de los mensajes de chat.**

---

### 2.7 Room tools / menú inferior izquierdo

El panel con:

- Settings
- Zoom
- Chat history
- Link to this room
- mute/unmute
- historial de salas

queda aprobado.

Se valora:

- tamaño actual;
- iconos suficientemente grandes;
- sprites a tamaño nativo;
- estructura compacta;
- aspecto de herramienta del cliente y no de menú web.

---

### 2.8 Perfil compacto dentro de sala

El perfil pequeño / infostand mostrado dentro de la sala queda aprobado.

No confundir con la ventana grande de perfil de usuario.

---

## 3. Elementos NO aprobados — pendientes de rediseño futuro

Estos elementos funcionan y conviven ya con el resto de la interfaz, pero **no representan todavía el diseño final de Holo**.

---

### 3.1 Menús de navegación / ventanas generales

Principal punto pendiente.

Incluye:

- Friends
- Mod Tools
- Subastas
- inventario protegido
- Help
- HC Center
- Chat History
- otras ventanas basadas en `NitroCardView`

Problema actual:

- siguen sintiéndose visualmente más pobres que Hobba;
- falta sensación de “cliente Habbo”;
- algunos interiores continúan recordando demasiado a Nitro/web;
- la relación entre header, tabs, contenido, botones y separadores todavía no es ideal.

Referencia futura:

**Hobba como base estructural, mejorada para Holo.**

---

### 3.2 Inventario

El inventario actual es funcional y los furnis renderizan correctamente.

Sin embargo, **su diseño no se considera final**.

Problemas:

- no encaja todavía al 100% con el lenguaje elegante del menú contextual;
- algunas zonas siguen pareciendo demasiado “app”;
- tabs, filtros, preview y composición general necesitan una revisión más profunda;
- debe sentirse como inventario de cliente clásico, no como grid web.

Regla técnica crítica:

**No usar `background` ni `background-image` con `!important` sobre `.inventory-items`.**

Los furnis se renderizan mediante `background-image` inline.  
Usar el shorthand `background` puede borrar el sprite del furni.

---

### 3.3 Catálogo

El catálogo actual tampoco se considera diseño final.

Problemas:

- estructura aún demasiado Nitro/web;
- tabs y navegación necesitan más personalidad clásica;
- falta riqueza visual;
- debería compartir el mismo lenguaje que el futuro inventario definitivo.

El problema no es si es claro u oscuro.

**El problema es el lenguaje visual.**

---

### 3.4 Perfil de usuario grande

La ventana grande de perfil convive ya con el sistema actual, pero no alcanza todavía el nivel visual de Hobba.

Pendiente:

- jerarquía interna;
- densidad;
- separadores;
- botones;
- composición;
- sensación general de ventana de cliente.

---

### 3.5 Tipografía

La tipografía global **no está aprobada al 100%**.

Problemas:

- algunos textos pequeños no se ven suficientemente nítidos;
- algunas pruebas anteriores empeoraron la legibilidad;
- no se busca una fuente moderna de app/web;
- debe convivir bien con pixel art y tamaños pequeños.

La futura tipografía debe priorizar:

1. nitidez;
2. lectura a 10–13 px;
3. estética de cliente clásico;
4. buena convivencia con pixel art;
5. coherencia entre todos los menús.

No cambiar la fuente global sin probar primero en un grupo pequeño de componentes.

---

## 4. Principios visuales de Holo

### Sí

- Habbo/Hobba clásico;
- compacto;
- interfaz densa pero legible;
- marcos definidos;
- bevel ligero;
- pixel art a tamaño nativo;
- tabs físicas;
- fondos crema/gris cuando encajen;
- azul/teal como acento;
- controles pequeños;
- jerarquía visual clara;
- sensación de “cliente”.

### No

- dashboard moderno;
- grandes cards;
- pills;
- exceso de border-radius;
- sombras tipo SaaS;
- botones gigantes;
- demasiado espacio vacío;
- Bootstrap visible;
- estética WordPress;
- reescalado fraccionario de sprites pixel;
- cambiar estilos globales sin revisar el componente real.

---

## 5. Arquitectura actual de la UI

La versión estable incluye cambios estructurales en React para las zonas aprobadas.

Componentes relevantes:

- `ToolbarView.tsx`
- `ToolbarMeView.tsx`
- `RoomToolsWidgetView.tsx`
- `PurseView.tsx`
- `NitroCardView.tsx`

El sistema de ventanas está unificado para evitar que convivan varios temas incompatibles.

Inventario, catálogo y perfil grande conservan además sus componentes/SCSS controlados dentro del checkpoint estable.

---

## 6. Lecciones técnicas de las iteraciones anteriores

### 6.1 No intentar resolver estructura solo con CSS global

Los cambios más exitosos llegaron cuando se modificó la estructura real de los componentes.

Ejemplos:

- toolbar;
- room tools;
- HUD;
- normalización de ventanas.

Para futuros rediseños del inventario y catálogo probablemente será necesario trabajar en:

**TSX + SCSS**, no solo añadir overrides.

---

### 6.2 No apilar skins experimentales

Las versiones intermedias llegaron a generar cascadas difíciles de controlar.

La versión estable debe mantenerse como **una sola skin consolidada**.

---

### 6.3 No escalar sprites pixel

Evitar:

```scss
transform: scale(.86);
transform: scale(.72);
```

Preferir:

- mantener sprite 1:1;
- aumentar el contenedor;
- dar más espacio alrededor.

---

### 6.4 No usar `background` sobre slots con sprites dinámicos

En el inventario, `LayoutGridItem` puede inyectar el furni mediante `background-image`.

Por tanto:

```scss
background: ...
```

puede eliminar el furni incluso aunque el asset exista.

---

### 6.5 Nombre flotante y chat son componentes distintos

No modificar bocadillos de chat cuando el objetivo sea únicamente el nombre flotante del avatar.

---

## 7. Estado estable actual

**Holo Hobba Classic v14** es el checkpoint visual estable.

Si una futura prueba empeora la UI:

1. volver a v14;
2. no arreglar una prueba fallida encima de otra;
3. partir siempre del checkpoint estable;
4. trabajar por componente.

---

## 8. Próxima fase de UI recomendada

Cuando se retome el rediseño visual:

1. Inventario.
2. Catálogo.
3. Perfil grande.
4. Ventanas de navegación restantes.
5. Tipografía.

La referencia principal debe ser Hobba, pero adaptada y mejorada para Holo.

Todo elemento marcado como **aprobado al 100%** debe permanecer intacto durante esas fases.

---

## 9. Decisión actual

La fase de UI queda temporalmente cerrada con v14.

La interfaz actual ya tiene una identidad mucho más cercana al cliente clásico deseado, aunque las ventanas grandes todavía tengan margen claro de mejora.

El objetivo de futuras iteraciones no será “modernizar Nitro”, sino **hacer que las partes pendientes alcancen el mismo nivel de coherencia que las partes ya aprobadas**.
