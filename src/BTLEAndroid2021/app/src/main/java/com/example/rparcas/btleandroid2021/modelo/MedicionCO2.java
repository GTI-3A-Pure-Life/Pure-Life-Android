package com.example.rparcas.btleandroid2021.modelo;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

/**
 * POJO para guardar la información de una Medicion CO2
 * 10/03/2021
 * @author Rubén Pardo Casanova
 */
public class MedicionCO2 {

    private static final String FECHA_FROMATO = "yyyy-MM-dd hh:mm:ss";


    private double medicion_valor;
    private int usuario_id;
    private String sensor_id;
    private Posicion posicion;
    private Timestamp medicion_fecha;

    public MedicionCO2(double valor, int usuarioID, String sensorID, Posicion posicion) {
        this.medicion_valor = valor;
        this.usuario_id = usuarioID;
        this.sensor_id = sensorID;
        this.posicion = posicion;

        // obtener la fecha actual en formato Timestamp
        this.medicion_fecha = new Timestamp(System.currentTimeMillis());

    }


    /**
     *
     * @param mediciones lista de mediciones a convertir en JSON
     * @return texto en formato json de una lista de mediciones
     */
    static public String listaMedicionesToJSON(List<MedicionCO2> mediciones){
        
        // crear objeto gson con un formato de fecha en concreto
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat(FECHA_FROMATO);
        Gson gson = gsonBuilder.create();
        
        return gson.toJson(mediciones);
     
             
    }
}
