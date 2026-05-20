package com.example.experiencias.entity;

import java.time.LocalDateTime;

/**
 * Representa un pago simulado asociado a una o varias reservas.
 *
 * Campos:
 *  - referencia      : código único generado internamente (ej. PAY-20260520-AB3F)
 *  - metodoPago      : "tarjeta", "transferencia" o "paypal"
 *  - estadoPago      : siempre "completado" en la simulación
 *  - importeTotal    : suma de todos los precios de las reservas
 *  - fechaPago       : instante de la transacción
 */
public class Pago {

    private Integer id;
    private Integer usuarioId;
    private String  referencia;
    private String  metodoPago;
    private String  estadoPago;
    private double  importeTotal;
    private LocalDateTime fechaPago;

    public Pago() {}

    public Pago(Integer id, Integer usuarioId, String referencia,
                String metodoPago, String estadoPago,
                double importeTotal, LocalDateTime fechaPago) {
        this.id           = id;
        this.usuarioId    = usuarioId;
        this.referencia   = referencia;
        this.metodoPago   = metodoPago;
        this.estadoPago   = estadoPago;
        this.importeTotal = importeTotal;
        this.fechaPago    = fechaPago;
    }

    public Integer getId()                         { return id; }
    public void    setId(Integer id)               { this.id = id; }
    public Integer getUsuarioId()                  { return usuarioId; }
    public void    setUsuarioId(Integer v)         { this.usuarioId = v; }
    public String  getReferencia()                 { return referencia; }
    public void    setReferencia(String v)         { this.referencia = v; }
    public String  getMetodoPago()                 { return metodoPago; }
    public void    setMetodoPago(String v)         { this.metodoPago = v; }
    public String  getEstadoPago()                 { return estadoPago; }
    public void    setEstadoPago(String v)         { this.estadoPago = v; }
    public double  getImporteTotal()               { return importeTotal; }
    public void    setImporteTotal(double v)       { this.importeTotal = v; }
    public LocalDateTime getFechaPago()            { return fechaPago; }
    public void    setFechaPago(LocalDateTime v)   { this.fechaPago = v; }
}
