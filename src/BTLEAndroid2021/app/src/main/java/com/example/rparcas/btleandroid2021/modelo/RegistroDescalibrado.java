package com.example.rparcas.btleandroid2021.modelo;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * RegistroDescalibrado.java
 * @author Ruben Pardo Casanova
 * 13/12/2021
 * Representa un registro de calibracion de un sensor
 */
public class RegistroDescalibrado {

    private final String uuidSensor;
    private final boolean descalibrado;
    private final Timestamp fechaHora;
    private final double factorDescalibracion;


    private final String FECHA_FROMATO = "yyyy-MM-dd hh:mm:ss";


    public RegistroDescalibrado(String sensorID, boolean descalibrado,double factorDescalibracion) {
        this.uuidSensor = sensorID;
        this.descalibrado = descalibrado;
        this.fechaHora = new Timestamp(System.currentTimeMillis());
        this.factorDescalibracion = factorDescalibracion;
    }


    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    /**
     * toJSON() -> String
     * @return string en formato json del objeto
     */
    public String toJSON(){
        String fechaConFormato = new SimpleDateFormat(FECHA_FROMATO).format(this.fechaHora);

        String res = "{" +
                "\"uuidSensor\":\""+this.uuidSensor+ "\", " +
                "\"descalibrado\":\""+descalibrado+"\", " +
                "\"factorDescalibracion\":\""+factorDescalibracion+"\", " +
                "\"fechaHora\":\""+fechaConFormato+"\" " +
                "}";

        return res;


    }
}
