package com.example.rparcas.btleandroid2021.modelo;

import java.io.Serializable;

/**
 * POJO para guardar la información de una Posicion geografica
 * 10/03/2021
 * @author Rubén Pardo Casanova
 */
public class Posicion implements Serializable {

    private final double latitud;
    private final double longitud;

    public Posicion(double latitud, double longitud) {
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    @Override
    public String toString() {
        return "Posicion{" +
                "latitud=" + latitud +
                ", longitud=" + longitud +
                '}';
    }
}
