package com.example.rparcas.btleandroid2021.logica;

import android.util.Log;

import com.example.rparcas.btleandroid2021.Constantes.RESTConstantes;
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

    public void prueba(){

        PeticionarioREST elPeticionarioREST = new PeticionarioREST();

        String restEndpoint = RESTConstantes.URL + "prueba";

        Log.d("PRUEBA", "prueba endpoint: "+restEndpoint);
        elPeticionarioREST.hacerPeticionREST("GET", restEndpoint,
                null,
                new PeticionarioREST.RespuestaREST () {
                    @Override
                    public void callback(int codigo, String cuerpo) {
                        Log.d ("PRUEBA","codigo respuesta: " + codigo + " <-> \n" + cuerpo);
                    }
                });
    }


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






}
