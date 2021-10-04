package com.example.rparcas.btleandroid2021.modelo;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.example.rparcas.btleandroid2021.SQLITE.MedicionCO2Contract;
import com.example.rparcas.btleandroid2021.Utilidades;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    private  Timestamp medicion_fecha;

    public MedicionCO2(double valor, int usuarioID, String sensorID, Posicion posicion) {
        this.medicion_valor = valor;
        this.usuario_id = usuarioID;
        this.sensor_id = sensorID;
        this.posicion = posicion;

        // obtener la fecha actual en formato Timestamp
        this.medicion_fecha = new Timestamp(System.currentTimeMillis());

    }

    /**
     * Constructor de MedicionCO2 a partir de un cursor de sqlite
     * indices del cursor:
     * 1 - valor | double
     * 2 - latitud | double
     * 3 - longitud | double
     * 4 - sensor | TEXTO
     * 5 - usuario | INTEGER
     * 6 - fecha | TEXTO
     * @param cursor cursor de sqlite
     */
    public MedicionCO2(Cursor cursor) {
        this.medicion_valor = cursor.getDouble(1);
        this.posicion = new Posicion(cursor.getDouble(2),cursor.getDouble(3));
        this.sensor_id = cursor.getString(4);
        this.usuario_id = cursor.getInt(5);


       // pasar de texto a timestamp
        this.medicion_fecha = new Timestamp(0);
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(FECHA_FROMATO);
            java.util.Date parsedDate = null;
            parsedDate = dateFormat.parse(cursor.getString(6));
            this.medicion_fecha = new java.sql.Timestamp(parsedDate.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }


    public MedicionCO2(TramaIBeacon tramaIBeacon) {
        this.medicion_valor = Utilidades.bytesToInt(tramaIBeacon.getMinor());
        this.posicion = new Posicion(0,0);
        this.sensor_id = Utilidades.bytesToString(tramaIBeacon.getUUID()).split("%")[0];
        this.usuario_id = 4;

        // obtener la fecha actual en formato Timestamp
        this.medicion_fecha = new Timestamp(System.currentTimeMillis());


    }

    /**
     * toJSON() -> String
     * @return string en formato json del objeto
     */
    public String toJSON(){
        String fechaConFormato = new SimpleDateFormat(FECHA_FROMATO).format(this.medicion_fecha);

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
     * toContentValues() -> ContentValues
     * @return content values con los datos del objeto
     */
    public ContentValues toContentValues(){
        ContentValues values = new ContentValues();
        values.put(MedicionCO2Contract.MedicionCO2Entry.VALOR, this.medicion_valor);
        values.put(MedicionCO2Contract.MedicionCO2Entry.LATITUD, this.posicion.getLatitud());
        values.put(MedicionCO2Contract.MedicionCO2Entry.LONGITUD, this.posicion.getLongitud());
        values.put(MedicionCO2Contract.MedicionCO2Entry.FECHA, this.medicion_fecha.toString());
        values.put(MedicionCO2Contract.MedicionCO2Entry.SENSOR, this.sensor_id);
        values.put(MedicionCO2Contract.MedicionCO2Entry.USUARIO, this.usuario_id);

        return values;
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
