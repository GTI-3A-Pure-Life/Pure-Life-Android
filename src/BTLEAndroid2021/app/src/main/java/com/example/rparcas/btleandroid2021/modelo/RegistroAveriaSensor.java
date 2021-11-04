package com.example.rparcas.btleandroid2021.modelo;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * RegistroAveriaSensor.java
 * @author Ruben Pardo Casanova
 * 04/11/2021
 * Representa un registro de averia de un sensor
 */
public class RegistroAveriaSensor {

    private final String uuidSensor;
    private final boolean estaAveriado;
    private final Timestamp fechaHora;

    private final String FECHA_FROMATO = "yyyy-MM-dd hh:mm:ss";

    public RegistroAveriaSensor(String uuidSensor, boolean estaAveriado) {
        this.uuidSensor = uuidSensor;
        this.estaAveriado = estaAveriado;
        this.fechaHora = new Timestamp(System.currentTimeMillis());;
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    /**
     * toJSON() -> String
     * @return string en formato json del objeto
     */
    public String toJSON(){
        String fechaConFormato = new SimpleDateFormat(FECHA_FROMATO).format(this.fechaHora);

        int averia = (this.estaAveriado) ? 1 : 0;

        String res = "{" +
                "\"uuidSensor\":\""+this.uuidSensor+ "\", " +
                "\"estaAveriado\":\""+averia+"\", " +
                "\"fechaHora\":\""+fechaConFormato+"\" " +
                "}";

        return res;


    }

}
