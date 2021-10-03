package com.example.rparcas.btleandroid2021.modelo;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * POJO para guardar la información de una Medicion CO2
 * 10/03/2021
 * @author Rubén Pardo Casanova
 */
public class MedicionCO2 {

    private static final String FECHA_FROMATO = "yyyy-MM-dd hh:mm:ss";


    private final double medicion_valor;
    private final int usuario_id;
    private final String sensor_id;
    private final Posicion posicion;
    private final Timestamp medicion_fecha;

    public MedicionCO2(double valor, int usuarioID, String sensorID, Posicion posicion) {
        this.medicion_valor = valor;
        this.usuario_id = usuarioID;
        this.sensor_id = sensorID;
        this.posicion = posicion;

        // obtener la fecha actual en formato Timestamp
        this.medicion_fecha = new Timestamp(System.currentTimeMillis());

    }


    /**
     * toJSON() -> String
     * @return string en formato json del objeto
     */
    public String toJSON(){
        String fechaConFormato = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(this.medicion_fecha);

        String res = "{" +
                "\"medicion_valor\":\""+this.medicion_valor+ "\", " +
                "\"medicion_fecha\":\""+fechaConFormato+"\", " +
                "\"medicion_latitud\":\""+this.posicion.getLatitud()+"\", " +
                "\"medicion_longitud\":\""+this.posicion.getLongitud()+"\", " +
                "\"usuario_id\":\""+this.usuario_id+"\", " +
                "\"sensor_id\":\""+this.sensor_id+"\"" +

        "}";

        return res;


    }

    /**
     *
     * @param mediciones lista de mediciones a convertir en JSON
     * @return texto en formato json de una lista de mediciones
     */
    static public String listaMedicionesToJSON(List<MedicionCO2> mediciones){

        StringBuilder res = new StringBuilder("[ ");

        for(int i=0;i<mediciones.size();i++){
            if(i != mediciones.size()-1){
                res.append(mediciones.get(i).toJSON()).append(", ");
            }else{
                res.append(mediciones.get(i).toJSON());
            }
        }
        res.append(" ]");

        return res.toString();
        // crear objeto gson con un formato de fecha en concreto
        /*GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat(FECHA_FROMATO);
        Gson gson = gsonBuilder.create();*/
        
       // return gson.toJson(mediciones);
     
             
    }
}
