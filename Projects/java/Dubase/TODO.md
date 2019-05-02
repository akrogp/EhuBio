# General

* [x] Meter logo
* [x] Meter resto de DUBs
* [x] Mejorar menú
* [ ] Meter metainfo de los experimentos (me lo pasa Unai). Se meterá enlace a PX para poder descargar los datos raw.

# Vistas

## Inicio

* [ ] Contenido: explicar mejor el fold change, etc.
* [ ] Captura del vulcano en lugar de la de g:profiler

## About

* [ ] Me pasan info

## Búsqueda

* [x] Descargar csv
* [x] Info de cómo calculadas las cosas
* [x] Verde más oscuro
* [ ] Mostrar botón de vulcano sólo en el caso de búsqueda por DUB -> a ser posible en un popup o en una ventana nueva
* [ ] Tabla compacta solo con unique peptides y en extendida también los all 
* [ ] Dentro de las tablas que aparecen en "SEARCH" ¿Habría opción de que cuando le das a la opción "More Fields", en lugar de que te lleve a una nueva ventana esa única proteina, pudieras elegir que more fields quieres y que se añadieran para todas las proteinas de la lista, como columnas adicionales?? -> Mantener detalles de cada una, y botón general para tabla extendida.
* [ ] Meter id de MaxQuant (Juanma) -> en search columna ID con exp+id mxq y al clickar en cada una te muestra diferentes cosas.
* [ ] En search separar búsqueda DUB/substrato/todo en tres botones.
* [x] ¿Se podría poner una opción para ordenar las tablas en función de las columnas? Es decir ordenar la columna por el nombre del gen, el fold change, etc. Yo creo que ahora aparecen ordenas en función del fold change, verdad? Con respecto a esto, el software tiene por igual el valor 3.32 y -3.32 (negativo), en lugar de situar al final los negativos....
* [x] La "i" de info añadida en el título de Fold Change, a veces no sale...
* [x] Estaria bien añadir una leyenda, que explicara los colores verde y rojo de la columna Fold Change (green: up-ubiquitinated, red: down)
* [x] Ordenar -> quitar el orden por valor absoluto.
* [x] En la opción de G:Profiler, te manda tanto las proteinas up-ubiquitinadas y las down. Se podría elegir que proteinas quiere enviar a G:profiler???
* [x] Cuando te descargas los datos aparecen las columans de de 1 y 0, para indicar que valor se ha imputado y cual no. Pero no viene indicado que significa "1" y "0".
* [ ] Meter información de los accessions de UniProt aunque sea sólo para descargar.

## Detalles

* [x] Valores de la vista normal
* [x] LFQ de cada caso
* [x] Sequence coverage
* [x] Resto de campos del xls
* [ ] Enlace a búsqueda de los sitios GlyGly en phosphosite
* [ ] Cuando entras en la opción more fields, se podría añadir un link a uniprot, por ejemplo en la descripción de la proteina?? -> query UniProt por nombre de gen.

## Gráfica

* [x] Nueva representación (tamaño según fold-change?) -> https://observablehq.com/@d3/zoomable-sunburst
* [ ] Juanma me pasa familias actualizadas
* [x] En la opción Browse, en el diagrama, sería posible quitar la última capa?? Es decir los nombres de los sustratos. O que solo aparezcan cuando haces click en la respectiva DUB??

## Análisis

* [x] GO -> https://biit.cs.ut.ee/gprofiler/gost
* [ ] Seleccionar una USP y buscar contra el resto resultados cruzados (tabla)
* [ ] Vulcano: grises sólo el borde y el resto (rojos y verdes) con menos transparencia. El signo del eje y hay que revisarlo.
* [ ] Vulcano: controles de calidad en azul. Me los pasa Juanma y aparte está el DUB que se haya usado para la búsqueda.
* [ ] STRING (pendiente de que Juanma vea si los resultados que salen merecen)

# Preferencias

## Filtrado

* [x] Cargar todos los datos
* [x] Mejorar eficiencia de búsqueda
* [x] Poder seleccionar umbrales
* [ ] Poder seleccionar imputaciones
* [x] Por defecto solo verdes pero poder incluir rojas o sólo rojas
* [x] Cambiar nombre de settings a thresholds y ponerlo como pestaña entre home y search

## Unidades

* [ ] Escala de LFQ
* [ ] Escala de p-value