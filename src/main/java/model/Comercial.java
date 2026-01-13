package model;

/**
 * Entidad principal "Comercial".
 * Relaciones:
 *  - 1:N con Pedido (un comercial gestiona muchos pedidos).
 */
public class Comercial {
    private Integer id;
    private String nombre;
    private String email;

    public Comercial() {}
    public Comercial(Integer id, String nombre, String email) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
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

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Comercial{id=%d, nombre='%s', email='%s'}".formatted(id, nombre, email);
    }
}
