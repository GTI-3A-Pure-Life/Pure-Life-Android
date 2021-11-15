package com.example.rparcas.btleandroid2021.modelo;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.rparcas.btleandroid2021.SQLITE.MedicionContract;
import com.example.rparcas.btleandroid2021.Utilidades;

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
public class Medicion {

    private final String FECHA_FROMATO = "yyyy-MM-dd hh:mm:ss";


    private final double medicion_valor;
    private final int usuario_id;
    private final String sensor_id;
    private final Posicion posicion;
    private  Timestamp medicion_fecha;
    private TipoMedicion tipoMedicion;

    private NivelPeligro nivelPeligro;

    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    public Medicion(double valor, int usuarioID, String sensorID, Posicion posicion, TipoMedicion tipoMedicion) {
        this.medicion_valor = valor;
        this.usuario_id = usuarioID;
        this.sensor_id = sensorID;
        this.posicion = posicion;

        this.tipoMedicion = tipoMedicion;
        this.nivelPeligro = calcularNivelPeligroGas(tipoMedicion,valor);
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
        this.nivelPeligro = calcularNivelPeligroGas(tipoMedicion,0);
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

    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    public Medicion(TramaIBeacon tramaIBeacon) {
        this.medicion_valor = Utilidades.bytesToInt(tramaIBeacon.getMinor());
        this.posicion = new Posicion(0,0);
        this.sensor_id = Utilidades.bytesToString(tramaIBeacon.getUUID()).split("%")[0];
        this.usuario_id = 4;//TODO CAMBIAR A POR USUARIO
        int major = Utilidades.bytesToInt( tramaIBeacon.getMajor());
        this.tipoMedicion = TipoMedicion.getTipoById(major);

        this.nivelPeligro = calcularNivelPeligroGas(tipoMedicion,medicion_valor);
        // obtener la fecha actual en formato Timestamp
        this.medicion_fecha = new Timestamp(System.currentTimeMillis());


    }


    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    public Medicion(JSONObject json) throws JSONException {
        this.medicion_valor = json.getDouble("valor");
        this.posicion = new Posicion(json.getJSONObject("posMedicion"));
        this.sensor_id = json.getString("idSensor");
        this.usuario_id = json.getInt("idUsuario");

        this.tipoMedicion = TipoMedicion.getTipoById(json.getInt("tipoGas"));
        this.nivelPeligro = calcularNivelPeligroGas(tipoMedicion,medicion_valor);

        this.medicion_fecha = Timestamp.valueOf(json.getString("fechaHora"));

    }


    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------

    /**
     * TipoMedicion,R -> calcularNivelPeligroGas() -> NivelPeligro
     * @param tipoMedicion tipo de la medicion
     * @param valor el valor de la medicion
     * @return valor de nivel de peligros del enum NivelPeligro (LEVE/MODERADO/ALTO/MUY_ALTO)
     */
    private NivelPeligro calcularNivelPeligroGas(TipoMedicion tipoMedicion, double valor) {
        // valores en ppm
        NivelPeligro nivelPeligro;
        switch (tipoMedicion){
            case CO:
                if(valor>=0 && valor<5){
                    //leve
                    nivelPeligro = NivelPeligro.LEVE;
                }
                else if(valor>=5 && valor<13){
                    // moderado
                    nivelPeligro = NivelPeligro.MODERADO;
                }
                else if(valor>=13 && valor<16){
                    // alto
                    nivelPeligro = NivelPeligro.ALTO;
                }else{
                    nivelPeligro = NivelPeligro.MUY_ALTO;
                }
                break;
            case O3:
                // leve 0-70, moderado 70-120, grave 120-180, muy grave 180 ug/m3
                // 1ppm - 2 ug/m3
                if(valor>=0 && valor<35){
                    //leve
                    nivelPeligro = NivelPeligro.LEVE;
                }
                else if(valor>=35 && valor<60){
                    // moderado
                    nivelPeligro = NivelPeligro.MODERADO;
                }
                else if(valor>=60 && valor<90){
                    // moderado
                    nivelPeligro = NivelPeligro.ALTO;
                }
                else{
                    // alto
                    nivelPeligro = NivelPeligro.MUY_ALTO;
                }
                break;
            case NO2:
                // leve 0-100, moderado 100-140, grave 140-200, muy grave 200 ug/m3
                // 1ppm - 1.88 ug/m3
                if(valor>=0 && valor<53){
                    //leve
                    nivelPeligro = NivelPeligro.LEVE;
                }
                else if(valor>=53 && valor<74){
                    // moderado
                    nivelPeligro = NivelPeligro.MODERADO;
                }
                else if(valor>=74 && valor<106){
                    // moderado
                    nivelPeligro = NivelPeligro.ALTO;
                }
                else{
                    // alto
                    nivelPeligro = NivelPeligro.MUY_ALTO;
                }
                break;
            case SO2:
                // leve 0-150, moderado 150-250, grave 250-350, muy grave 350 ug/m3
                // 1ppm - 2.62 ug/m3
                if(valor>=0 && valor<57){
                    //leve
                    nivelPeligro = NivelPeligro.LEVE;
                }
                else if(valor>=57 && valor<95){
                    // moderado
                    nivelPeligro = NivelPeligro.MODERADO;
                }
                else if(valor>=95 && valor<134){
                    // moderado
                    nivelPeligro = NivelPeligro.ALTO;
                }
                else{
                    // alto
                    nivelPeligro = NivelPeligro.MUY_ALTO;
                }
                break;

            default:
                nivelPeligro = null;
        }

        return nivelPeligro;
    }

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
                "\"posMedicion\":{\"latitud\":\""+this.posicion.getLatitud()+"\",\"longitud\":\""+this.posicion.getLatitud()+"\"}, " +
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
    public NivelPeligro getNivelPeligro() {
        return this.nivelPeligro;
    }
    public double getValor() {
        return this.medicion_valor;
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
                ", nivelPeligro=" + nivelPeligro +
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
    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------

}// class
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------

