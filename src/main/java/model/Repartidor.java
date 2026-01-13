package model;

/**
 * Entidad principal "Repartidor".
 * Relaciones:
 *  - 1:N con Pedido (un repartidor gestiona muchos pedidos).
 */
public class Repartidor {
    private Integer id;
    private String nombre;
    private String telefono;

    public Repartidor() {}
    public Repartidor(Integer id, String nombre, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    @Override
    public String toString() {
        return "Repartidor{id=%d, nombre='%s', telefono='%s'}".formatted(id, nombre, telefono);
    }
}
