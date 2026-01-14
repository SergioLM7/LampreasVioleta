# App de Gestión - Lampreas Violeta
Software que permite gestionar la base de datos de la empresa Lampreas Violeta con CRUD completos, búsqueda universal y exportación de tablas a JSON.

## Clases añadidas
- Se han añadido las clases Comercial y Repartidor (con sus respectivas tablas en base de datos).
- Se relacionan con el resto de tablas ya implementadas a través de una relación de 1:N con la tabla Pedido (en la que se ubican como Foreign Keys sus respectivos id).
- El Pedido puede tener a null el repartidorId inicialmente, hasta que se le asignase uno, pero siempre necesitará un comercialId (por decisión de negocio, en la que los pedidos siempre pasan por un comercial y no los gestiona el cliente directamente).

## Exportación a JSON
- Se ha implementado una clase utils JsonIO en la que se implementan los métodos de lectura y escritura empleando la librería jackson.
- Se han implementado métodods privados para exportar todos los datos de comerciales, repartidores y clientes en las respectivas vistas de cada submenú.
- En el caso particular de Cliente y DetalleCliente se ha añadido un método en el service que se encarga de añadir a una lista de un nuevo objeto DTO (ClienteCompletoDTO) mapeado con los datos de ambas tablas teniendo en cuenta el id. Es este ClienteCompletoDTO el que conformará el JSON de exportación.
- A nivel UI, se han dispuesto botones "Exportar" en cada submenú que desencadenan la lógica de exportación a JSON de todos los datos de sus respectivas tablas.
- Se ha creado un directorio "exportaciones" en la raiz del proyecto donde se recogen todos los archivos exportados, en cuyos nombres se indica la tabla exportada, la fecha y la hora en la que se ha exportado para facilitar su gestión.

## Otras mejoras en el proyecto
- Todos los CRUD de cada una de las entidades son 100% funcionales, incluida la búsqueda universal y la inserción/actualización con transacción en el caso de Cliente y DetalleCliente.
- Se ha refactorizado el sistema de alertas para que esté unificado en una clase utils.
- Se han refactorizado los mapeos de cada clase para que estén unificados en una clase utils.
- Se ha implementado una clase MenuPrincipalView para poder acceder desde la UI a la gestión de Clientes, Comerciales y Repartidores de manera sencilla.
- Se han reubicado las vistas de cada submenú bajo el directorio "app/ui".
- Se ha implementado un botón "Volver al menú" y su lógica asociada en cada submenú.
- Se ha creado una nueva clase Main para lanzar directamente desde ella LampreasVioletaApp sin que Java 21 dé problemas con la gestión de los módulos de JavaFX.
- Se desarrollado el CRUD de Comercial y Repartidor teniendo en cuenta que podría llegar a darse el caso de necesitar un DetalleComercial y DetalleRepartidor en el futuro.
