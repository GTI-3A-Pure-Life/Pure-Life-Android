package com.example.rparcas.btleandroid2021.logica;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.rparcas.btleandroid2021.Constantes.RESTConstantes;
import com.example.rparcas.btleandroid2021.SQLITE.MedicionCO2Contract;
import com.example.rparcas.btleandroid2021.SQLITE.MedicionDBHelper;
import com.example.rparcas.btleandroid2021.modelo.MedicionCO2;

import java.util.List;

/**
 * Clase con los metodos de la logica de negocio de la aplicacion
 * 03/10/2021
 * @author Rubén Pardo Casanova
 */
public class Logica {




    //-----------------------------------------------------
    //-----------------------------------------------------
    /**
     * Constructor de la clase Logica
     */
    public Logica(){

    }// ()


    //-----------------------------------------------------
    //-----------------------------------------------------


    /**
     *
     * Lista<MedicionesCO> -> publicarMediciones()
     *
     * Envia una lista de mediciones al servidor para guardarlas
     *
     * @param mediciones a enviar
     */
    public void publicarMediciones(List<MedicionCO2> mediciones){

        PeticionarioREST elPeticionarioREST = new PeticionarioREST();

        String restEndpoint = RESTConstantes.URL + RESTConstantes.RESCURSO_MEDICIONES;

        Log.d("PRUEBA", "publicarMediciones endpoint: "+restEndpoint);

        elPeticionarioREST.hacerPeticionREST("PUT", restEndpoint,
                "{\"res\": "+MedicionCO2.listaMedicionesToJSON(mediciones)+"}" ,
                new PeticionarioREST.RespuestaREST () {
                    @Override
                    public void callback(int codigo, String cuerpo) {
                        Log.d ("PRUEBA","codigo respuesta: " + codigo + " <-> \n" + cuerpo);
                    }
                });
    }


    /**
     * MedicionCO2 -> guardarMedicionEnLocal()
     *
     * Guardar una medicion co2 en la base de datos interna
     * @param medicionCO2 medicion a guardar
     * @param context Contexto de la aplicacion
     */
    public void guardarMedicionEnLocal(MedicionCO2 medicionCO2, Context context) {

        MedicionDBHelper medicionDBHelper = new MedicionDBHelper(context);
        medicionDBHelper.guardarMedicionSQLITE(medicionCO2);
    }

    /**
     * obtener50MedicionesDeBDLocal() -> Lista<MedicionCO2>
     *
     * Obtiene como maximo 50 mediciones de la base de datos interna
     * @param context contexto de la aplicacion
     * @return Lista de mediciones
     */
    public List<MedicionCO2> obtenerPrimeras50MedicionesDeBDLocal(Context context){
        MedicionDBHelper medicionDBHelper = new MedicionDBHelper(context);
        List<MedicionCO2> mediciones = medicionDBHelper.obtener50Mediciones();

        return mediciones;
    }


    /**
     * borrar50MedicionesDeBDLocal()
     *
     * Borra como maximo las ultimas 50 mediciones de la base de datos interna
     * @param context contexto de la aplicacion
     *
     */
    public void borrarPrimeras50MedicionesDeBDLocal(Context context){
        MedicionDBHelper medicionDBHelper = new MedicionDBHelper(context);
        medicionDBHelper.borrarUltimas50Mediciones();

    }



}
