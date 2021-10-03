package com.example.rparcas.btleandroid2021.modelo;
/**
 * POJO para guardar la información de una Posicion geografica
 * 10/03/2021
 * @author Rubén Pardo Casanova
 */
public class Posicion {

    private final double medicion_latitud;
    private final double medicion_longitud;

    public Posicion(double latitud, double longitud) {
        this.medicion_latitud = latitud;
        this.medicion_longitud = longitud;
    }

    public double getLatitud() {
        return medicion_latitud;
    }

    public double getLongitud() {
        return medicion_longitud;
    }
}
