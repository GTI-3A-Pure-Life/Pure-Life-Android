package com.example.rparcas.btleandroid2021.modelo;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class RegistroBateriaSensor {

    private String uuidSensor;
    private int valor;
    private Timestamp fechaHora;
    public static int NIVEL_BATERIA_BAJO = 20;

    private final String FECHA_FROMATO = "yyyy-MM-dd hh:mm:ss";

    public RegistroBateriaSensor(String uuidSensor, int valorBateria) {
        this.uuidSensor = uuidSensor;
        this.valor = valorBateria;
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

        int tieneBateriaBaja = (valor<=NIVEL_BATERIA_BAJO) ? 1 : 0;

        String res = "{" +
                "\"uuidSensor\":\""+this.uuidSensor+ "\", " +
                "\"tieneBateriaBaja\":\""+tieneBateriaBaja+"\", " +
                "\"fechaHora\":\""+fechaConFormato+"\" " +
                "}";

        return res;


    }
}
