package com.example.rparcas.btleandroid2021.modelo;

import org.json.JSONException;
import org.json.JSONObject;

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

    public Posicion(JSONObject posMedicion) throws JSONException {
        this.latitud = posMedicion.getDouble("latitud");
        this.longitud = posMedicion.getDouble("longitud");
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


    /**
     * Calcula la distancia entre dos puntos de coordenadas
     * @author Ruben Pardo Casanova
     * 09/12/2021
     * Posicion -> calcularDistanciaA() -> R
     * @param posicionEstacionMasCercana posicion a calcular la distancia
     * @return distancia en metros
     */
    public double calcularDistanciaA(Posicion posicionEstacionMasCercana) {

        double lat1 = this.latitud;
        double lng1 = this.longitud;
        double lat2 = posicionEstacionMasCercana.latitud;
        double lng2 = posicionEstacionMasCercana.longitud;
        //double radioTierra = 3958.75;//en millas
        double radioTierra = 6371000;//en metros
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double va1 = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double va2 = 2 * Math.atan2(Math.sqrt(va1), Math.sqrt(1 - va1));
        double distancia = radioTierra * va2;

        return distancia;
    }
}
