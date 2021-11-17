package com.example.rparcas.btleandroid2021.ui.mapa;

import android.util.Log;

import com.example.rparcas.btleandroid2021.logica.EstadoPeticion;
import com.example.rparcas.btleandroid2021.logica.Logica;
import com.example.rparcas.btleandroid2021.logica.PeticionarioREST;
import com.example.rparcas.btleandroid2021.modelo.Medicion;
import com.example.rparcas.btleandroid2021.modelo.Posicion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


/**
 * MapaViewModel.java
 * @author Ruben Pardo Casanova
 * 01/11/2021
 * Clase que gestiona los datos que requiere el MapaFragment y se encarga de notificarlo para que
 * repinte la vista
 */
public class MapaViewModel extends ViewModel {

    private String textoErrorPeticion;
    private ArrayList<Medicion> medicionesObtenidas;
    public MutableLiveData<ArrayList<Medicion>> medicionesAMostrar;
    private MutableLiveData<EstadoPeticion> estadoPeticionObtenerMediciones;

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    public MapaViewModel() {

        medicionesAMostrar = new MutableLiveData<ArrayList<Medicion>>();
        estadoPeticionObtenerMediciones = new MutableLiveData<EstadoPeticion>();
        medicionesObtenidas =  new ArrayList<>();
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    public String getError() {
        return textoErrorPeticion;
    }
    public MutableLiveData<ArrayList<Medicion>> getMedicionesAMostrar() {
        return medicionesAMostrar;
    }
    public MutableLiveData<EstadoPeticion> getEstadoPeticionObtenerMediciones() {
        return estadoPeticionObtenerMediciones;
    }


    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    /**
     *
     * TipoMedicion -> filtrarMedicionPorTipo() -> Lista<Medicion>
     *
     * @author Ruben Pardo Casanova
     * 15/11/2021
     *
     * @param tipoGas tipo de gas a filtrar
     * @return devuelve la listas de mediciones filtradas en el observable de mediciones a mostrar
     */
    public void filtrarMedicionPorTipo(Medicion.TipoMedicion tipoGas) {

        ArrayList<Medicion> mediciones = new ArrayList<>();

        // todos
        if(tipoGas == null){
            mediciones = medicionesObtenidas;
        }else{
            for(Medicion m : medicionesObtenidas){
                if(m.getTipoMedicion().equals(tipoGas)){
                    mediciones.add(m);
                }
            }
        }



        medicionesAMostrar.setValue(mediciones);
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    /**
     *
     *  -> obtenerMedicionesHoy() -> Texto, Lista<Medicion>, EstadoPeticion
     *
     * @author Ruben Pardo Casanova
     * 15/11/2021
     *
     * @return devuelve la listas de mediciones filtradas en el observable de mediciones a mostrar
     */
    public void obtenerMedicionesHoy()  {
        estadoPeticionObtenerMediciones.setValue(EstadoPeticion.EN_PROCESO);

        medicionesObtenidas = new ArrayList<>();

        Logica l = new Logica();
        Timestamp hoy = new Timestamp(System.currentTimeMillis());
        String fechaHoy = new SimpleDateFormat("yyyy-MM-dd").format(hoy);

        String fechaIni = fechaHoy+" 00:00:00";
        String fechaFin = fechaHoy+" 23:59:59";

        l.obtenerMedicionesDeHasta(fechaIni,fechaFin,new PeticionarioREST.RespuestaREST() {
            @Override
            public void callback(int codigo, String cuerpo) {

                if(codigo == 200){
                    // hay datos
                    try {
                        JSONArray medicionesJSON = new JSONArray(cuerpo);
                        // obtenemos todas las referencias a los objetos
                        // recorremos los objetos
                        for(int i=0;i<medicionesJSON.length();i++){
                            Medicion m = new Medicion((JSONObject) medicionesJSON.get(i));
                            medicionesObtenidas.add(m);
                        }

                        // se realizo correctamente la peticion
                        estadoPeticionObtenerMediciones.setValue(EstadoPeticion.EXITO);
                        medicionesAMostrar.setValue(medicionesObtenidas);
                    } catch (JSONException e) {
                        estadoPeticionObtenerMediciones.setValue(EstadoPeticion.ERROR);
                        textoErrorPeticion = "Error inesperado";

                        Log.e("REST","obtenerMedicionesDeHasta(): error al intentar pasar a json el objeto de mediciones");
                        Log.e("REST","obtenerMedicionesDeHasta(): "+e.getMessage());
                    }

                }
                else if(codigo == 500){
                    // vacio
                    estadoPeticionObtenerMediciones.setValue(EstadoPeticion.ERROR);
                    textoErrorPeticion = "Error inesperado";
                    Log.e("REST","obtenerMedicionesDeHasta() codigo respuesta 500: "+cuerpo);
                }

            }


        });


    }

    /**
     *
     * Lista[Mediciones] -> interpolar() -> GeoJSON
     * interpolar las mediciones a mostrar y transformarlas en GeoJSON
     *
     * @param medicones mediciones a interpolar
     * @return GeoJSON que se pintara en leafleft
     */
    private void interpolar(ArrayList<Medicion> medicones){
        // 1. obtener la medicion mas antigua por punto
        // 2. interpolar
        // 3. transformar a GeoJSON
    }

}