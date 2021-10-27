package com.example.rparcas.btleandroid2021.logica;

import android.content.Context;
import android.util.Log;

import com.example.rparcas.btleandroid2021.Constantes.RESTConstantes;
import com.example.rparcas.btleandroid2021.SQLITE.MedicionDBHelper;
import com.example.rparcas.btleandroid2021.modelo.Medicion;

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
    public void publicarMediciones(List<Medicion> mediciones){

        PeticionarioREST elPeticionarioREST = new PeticionarioREST();

        String restEndpoint = RESTConstantes.URL + RESTConstantes.RESCURSO_MEDICIONES;

        Log.d("PRUEBA", "publicarMediciones endpoint: "+restEndpoint);

        elPeticionarioREST.hacerPeticionREST("PUT", restEndpoint,
                "{\"res\": "+ Medicion.listaMedicionesToJSON(mediciones)+"}" ,
                new PeticionarioREST.RespuestaREST () {
                    @Override
                    public void callback(int codigo, String cuerpo) {

                        Log.d ("PRUEBA","codigo respuesta: " + codigo + " <-> \n" + cuerpo);

                    }
                });
    }


    /**
     * Lista<MedicionCO2> -> guardarMedicionEnLocal()
     *
     * Guardar mediciones co2 en la base de datos interna
     * @param mediciones mediciones a guardar
     * @param context Contexto de la aplicacion
     */
    public void guardarMedicionesEnLocal(List<Medicion> mediciones, Context context) {

        MedicionDBHelper medicionDBHelper = new MedicionDBHelper(context);
        medicionDBHelper.guardarMedicionesSQLITE(mediciones);
    }

    /**
     * obtener50MedicionesDeBDLocal() -> Lista<MedicionCO2>
     *
     * Obtiene como maximo 50 mediciones de la base de datos interna
     * @param context contexto de la aplicacion
     * @return Lista de mediciones
     */
    public List<Medicion> obtenerPrimeras50MedicionesDeBDLocal(Context context){
        MedicionDBHelper medicionDBHelper = new MedicionDBHelper(context);
        List<Medicion> mediciones = medicionDBHelper.obtener50Mediciones();

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
