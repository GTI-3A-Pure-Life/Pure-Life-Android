package com.example.rparcas.btleandroid2021.modelo;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.example.rparcas.btleandroid2021.SQLITE.MedicionContract;
import com.example.rparcas.btleandroid2021.Utilidades;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.heatmaps.WeightedLatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * POJO para guardar la información de una Medicion CO2
 * 10/03/2021
 * @author Rubén Pardo Casanova
 */
public class Medicion{

    private final String FECHA_FROMATO = "yyyy-MM-dd HH:mm:ss";


    private double medicion_valor;
    private final int usuario_id;
    private final String sensor_id;
    private final Posicion posicion;
    private  Timestamp medicion_fecha;
    private TipoMedicion tipoMedicion;
    private double valorAQI;

    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    public Medicion(double valor, int usuarioID, String sensorID, Posicion posicion, TipoMedicion tipoMedicion) {


        this.medicion_valor = tipoMedicion == TipoMedicion.O3 ? valor/1000 : valor;
        this.usuario_id = usuarioID;
        this.sensor_id = sensorID;
        this.posicion = posicion;

        this.tipoMedicion = tipoMedicion;
        this.valorAQI = Utilidades.calcularValorAQI(medicion_valor,tipoMedicion);
        // obtener la fecha actual en formato Timestamp
        this.medicion_fecha = new Timestamp(System.currentTimeMillis());

    }

    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    public Medicion(TipoMedicion tipoMedicion) {
        this.medicion_valor = 0;
        this.usuario_id = -1;
        this.sensor_id = "";
        this.posicion = new Posicion(0,0);

        this.tipoMedicion = tipoMedicion;
        this.valorAQI = Utilidades.calcularValorAQI(medicion_valor,tipoMedicion);
        // obtener la fecha actual en formato Timestamp
        this.medicion_fecha = new Timestamp(System.currentTimeMillis());

    }

    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    /**
     * Constructor de MedicionCO2 a partir de un cursor de sqlite
     * indices del cursor:
     * 1 - valor | double
     * 2 - latitud | double
     * 3 - longitud | double
     * 4 - sensor | TEXTO
     * 5 - usuario | INTEGER
     * 6 - fecha | TEXTO
     * 7 - tipoMedicion | INTEGER
     * @param cursor cursor de sqlite
     */
    public Medicion(Cursor cursor) {
        this.medicion_valor = cursor.getDouble(1);
        this.posicion = new Posicion(cursor.getDouble(2),cursor.getDouble(3));
        this.sensor_id = cursor.getString(4);
        this.usuario_id = cursor.getInt(5);


        this.tipoMedicion = TipoMedicion.getTipoById(cursor.getInt(7));
        this.valorAQI = Utilidades.calcularValorAQI(medicion_valor,tipoMedicion);
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

    public Medicion(String fecha, double valor) {
        this.medicion_valor = valor;

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(FECHA_FROMATO);
            java.util.Date parsedDate = null;
            parsedDate = dateFormat.parse(fecha);
            this.medicion_fecha = new java.sql.Timestamp(parsedDate.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.tipoMedicion = TipoMedicion.CO;
        this.valorAQI = Utilidades.calcularValorAQI(medicion_valor,tipoMedicion);
        usuario_id = 0;
        posicion = null;
        sensor_id = null;
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    public Medicion(TramaIBeacon tramaIBeacon,int usuario_id) {
        int valor = Utilidades.bytesToInt(tramaIBeacon.getMinor());
        this.medicion_valor = tipoMedicion == TipoMedicion.O3 ? valor/1000.0 : valor;
        this.posicion = new Posicion(0,0);
        this.sensor_id = Utilidades.bytesToString(tramaIBeacon.getUUID()).split("%")[0];
        this.usuario_id = usuario_id;
        int major = Utilidades.bytesToInt( tramaIBeacon.getMajor());
        this.tipoMedicion = TipoMedicion.getTipoById(major);
        this.valorAQI = Utilidades.calcularValorAQI(medicion_valor,tipoMedicion);
        // obtener la fecha actual en formato Timestamp
        this.medicion_fecha = new Timestamp(System.currentTimeMillis());


    }


    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    public Medicion(JSONObject json) throws JSONException {
        // de la BD llega el valor AQI
        this.valorAQI = json.getDouble("valor");
        this.posicion = new Posicion(json.getJSONObject("posMedicion"));
        this.sensor_id = json.getString("idSensor");
        this.usuario_id = json.getInt("idUsuario");
        this.tipoMedicion = TipoMedicion.getTipoById(json.getInt("tipoGas"));
        this.medicion_fecha = Timestamp.valueOf(json.getString("fechaHora"));

    }


    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------



    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    /**
     * toJSON() -> String
     * @return string en formato json del objeto
     */
    public String toJSON(){
        String fechaConFormato = new SimpleDateFormat(FECHA_FROMATO).format(this.medicion_fecha);

        String res = "{" +
                "\"valor\":\""+this.medicion_valor+ "\", " +
                "\"fechaHora\":\""+fechaConFormato+"\", " +
                "\"posMedicion\":{\"latitud\":\""+this.posicion.getLatitud()+"\",\"longitud\":\""+this.posicion.getLongitud()+"\"}, " +
                "\"tipoGas\":\""+this.tipoMedicion.getIdGas()+"\", " +
                "\"idUsuario\":\""+this.usuario_id+"\", " +
                "\"uuidSensor\":\""+this.sensor_id+"\"" +

                "}";

        return res;


    }

    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    /**
     * toContentValues() -> ContentValues
     * @return content values con los datos del objeto
     */
    public ContentValues toContentValues(){
        ContentValues values = new ContentValues();
        values.put(MedicionContract.MedicionEntry.VALOR, this.medicion_valor);
        values.put(MedicionContract.MedicionEntry.LATITUD, this.posicion.getLatitud());
        values.put(MedicionContract.MedicionEntry.LONGITUD, this.posicion.getLongitud());
        values.put(MedicionContract.MedicionEntry.FECHA, this.medicion_fecha.toString());
        values.put(MedicionContract.MedicionEntry.SENSOR, this.sensor_id);
        values.put(MedicionContract.MedicionEntry.USUARIO, this.usuario_id);
        values.put(MedicionContract.MedicionEntry.TIPO_MEDICION, this.tipoMedicion.getIdGas());

        return values;
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    public String getSensorID() {
        return this.sensor_id;
    }
    public TipoMedicion getTipoMedicion() {
        return this.tipoMedicion;
    }
    public double getValor() {
        return this.medicion_valor;
    }
    public Timestamp getMedicion_fecha() {return this.medicion_fecha;}
    public void setValor(double valor) {
        this.medicion_valor = valor;
        this.valorAQI = Utilidades.calcularValorAQI(medicion_valor,tipoMedicion);

    }
    public double getValorAQI() {
        return this.valorAQI;
    }
    public void setValorAQI(double valorAQI) {
        this.valorAQI = valorAQI;
    }
    public Posicion getPosicion() {
        return posicion;
    }
    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    /**
     *
     * @param mediciones lista de mediciones a convertir en JSON
     * @return texto en formato json de una lista de mediciones
     */
    static public String listaMedicionesToJSON(List<Medicion> mediciones){

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

    @Override
    public String toString() {
        return "Medicion{" +
                "medicion_valor=" + medicion_valor +
                ", usuario_id=" + usuario_id +
                ", sensor_id='" + sensor_id + '\'' +
                ", posicion=" + posicion +
                ", medicion_fecha=" + medicion_fecha +
                ", tipoMedicion=" + tipoMedicion +
                '}';
    }




    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    /**
     * TipoMedicion.java
     * Enum con los distintos gases que medimos en la aplicacion
     * @author Ruben Pardo Casanova
     * 26/10/2021
     */
    public enum TipoMedicion {

        CO("Monoxido Carbono",1), NO2("Dioxido de nitrogeno",2),
        SO2("Dioxido de azufre",3), O3("Ozono",4);

        private final String nombreGas;
        private final int idGas;

        private TipoMedicion (String nombreGas, int idGAS){
            this.nombreGas = nombreGas;
            this.idGas = idGAS;
        }

        public String getNombreGas() {
            return nombreGas;
        }

        public int getIdGas() {
            return idGas;
        }

        /**
         * N -> getTipoById() -> TipoMedicion
         * @param id el id del gas
         * @return Objeto TipoMedicion
         */
        public static TipoMedicion getTipoById(int id){

            // no me deja hacerlo con un switch
            if(id == TipoMedicion.CO.getIdGas()){
                return TipoMedicion.CO;
            }else if(id == TipoMedicion.SO2.getIdGas()){
                return TipoMedicion.SO2;
            }else if(id == TipoMedicion.O3.getIdGas()){
                return TipoMedicion.O3;
            }else if(id == TipoMedicion.NO2.getIdGas()){
                return TipoMedicion.NO2;
            }else{
                // default
                return null;
            }
        }
    }// class
    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------

    /**
     * NivelPeligro.java
     * Enum que cuando se cree el objeto dependiendo del valor y los rangos establecidos de peligro
     * se asignara un valor o otro
     * @author Ruben Pardo Casanova
     * 26/10/2021
     */
    public enum NivelPeligro{
        LEVE,MODERADO,ALTO,MUY_ALTO
    } // class


    public static class RangoNivelesPeligro{
        public static float TOPE_LEVE_CO = 4.4f;
        public static float TOPE_MODERADO_CO = 12.4f;
        static public float TOPE_ALTO_CO = 15.4f;
        static public float TOPE_MUY_ALTO_CO = 20.0f;

        public static float TOPE_LEVE_NO2 = 53.0f;
        public static float TOPE_MODERADO_NO2 = 360.0f;
        static public float TOPE_ALTO_NO2 = 649.0f;
        static public float TOPE_MUY_ALTO_NO2 = 1249.0f;

        public static float TOPE_LEVE_SO2 = 35.0f;
        public static float TOPE_MODERADO_SO2 = 185.0f;
        static public float TOPE_ALTO_SO2 = 304.0f;
        static public float TOPE_MUY_ALTO_SO2 = 604.0f;

        public static float TOPE_LEVE_O3 = 0.054f;
        public static float TOPE_MODERADO_O3 = 0.164f;
        static public float TOPE_ALTO_O3 = 0.204f;
        static public float TOPE_MUY_ALTO_O3 = 0.404f;
    } // class

    public static class VALOR_AQI{
        public static int NIVEL_BUENO = 50;
        public static int NIVEL_MODERADO = 150;
        static public int NIVEL_ALTO = 200;
        static public int NIVEL_MUY_ALTO = 300;
    } // class

    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------

}// class
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------

