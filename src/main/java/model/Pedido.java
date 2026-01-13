package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Pedido realizado por un cliente (FK cliente_id).
 * Relación N:M con Producto -> lista de DetallePedido.
 */
public class Pedido {
    private Integer id;                 // PK
    private Integer clienteId;          // FK a Cliente (lado N de 1:N)
    private Integer comercialId;        // FK a Comercial (lado N de 1:N)
    private Integer repartidorId;       // FK a Repartidor (lado N de 1:N)
    private LocalDate fecha;

    // N:M mediante filas en la tabla detalle_pedido
    private List<DetallePedido> lineas = new ArrayList<>();

    public Pedido() {}
    public Pedido(Integer id, Integer clienteId, LocalDate fecha) {
        this.id = id;
        this.clienteId = clienteId;
        this.fecha = fecha;
    }
    public Pedido (Integer id, Integer clienteId, LocalDate fecha, Integer repartidorId, Integer comercialId) {
        this.id = id;
        this.clienteId = clienteId;
        this.comercialId = comercialId;
        this.repartidorId = repartidorId;
        this.fecha = fecha;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getClienteId() { return clienteId; }
    public void setClienteId(Integer clienteId) { this.clienteId = clienteId; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public List<DetallePedido> getLineas() { return lineas; }
    public void setLineas(List<DetallePedido> lineas) { this.lineas = lineas; }

    public Integer getComercialId() {
        return comercialId;
    }
    public void setComercialId(Integer comercialId) {
        this.comercialId = comercialId;
    }

    public Integer getRepartidorId() {
        return repartidorId;
    }
    public void setRepartidorId(Integer repartidorId) {
        this.repartidorId = repartidorId;
    }

    public double getTotal() {
        return lineas.stream().mapToDouble(DetallePedido::getImporte).sum();
    }

    @Override public String toString() {
        return "Pedido{id=%d, clienteId=%d, fecha=%s, comercialId=%d, repartidorId=%s, total=%.2f}"
                .formatted(id, clienteId, fecha, comercialId, repartidorId !=null ? repartidorId.toString() : "", getTotal());
    }
}
