package com.example.rparcas.btleandroid2021.ui.mapa;

import android.util.Log;

import com.example.rparcas.btleandroid2021.logica.EstadoPeticion;
import com.example.rparcas.btleandroid2021.logica.Logica;
import com.example.rparcas.btleandroid2021.logica.PeticionarioREST;
import com.example.rparcas.btleandroid2021.modelo.Medicion;
import com.google.maps.android.heatmaps.WeightedLatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

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
    private HashMap<Medicion.TipoMedicion, List<WeightedLatLng>> weigthLatLngPorTipo;
    public MutableLiveData<ArrayList<WeightedLatLng>> medicionesAMostrar;
    private MutableLiveData<EstadoPeticion> estadoPeticionObtenerMediciones;

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    public MapaViewModel() {

        medicionesAMostrar = new MutableLiveData<ArrayList<WeightedLatLng>>();
        estadoPeticionObtenerMediciones = new MutableLiveData<EstadoPeticion>();

       limpiarWeigthLatLngPorTipo();
    }

    private void limpiarWeigthLatLngPorTipo() {
        weigthLatLngPorTipo =  new HashMap<Medicion.TipoMedicion,List<WeightedLatLng>>();
        weigthLatLngPorTipo.put(Medicion.TipoMedicion.CO, new ArrayList<WeightedLatLng>());
        weigthLatLngPorTipo.put(Medicion.TipoMedicion.SO2, new ArrayList<WeightedLatLng>());
        weigthLatLngPorTipo.put(Medicion.TipoMedicion.O3, new ArrayList<WeightedLatLng>());
        weigthLatLngPorTipo.put(Medicion.TipoMedicion.NO2, new ArrayList<WeightedLatLng>());
    }

    private ArrayList<WeightedLatLng> obtenerTodasListaWeightLatLngEnUna() {
        ArrayList<WeightedLatLng> weightedLatLngs = new ArrayList<>();

        for(List<WeightedLatLng> lista : weigthLatLngPorTipo.values())
            weightedLatLngs.addAll(lista);

        return  weightedLatLngs;
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    public String getError() {
        return textoErrorPeticion;
    }
    public MutableLiveData<ArrayList<WeightedLatLng>> getMedicionesAMostrar() {
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

        ArrayList<WeightedLatLng> mediciones = new ArrayList<>();

        // todos
        if(tipoGas == null){
            mediciones.addAll(obtenerTodasListaWeightLatLngEnUna());
        }else{
            mediciones.addAll(weigthLatLngPorTipo.get(tipoGas));
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

        limpiarWeigthLatLngPorTipo();

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
                            weigthLatLngPorTipo.get(m.getTipoMedicion()).add(m.toWeightedLatLng());
                        }

                        // se realizo correctamente la peticion
                        estadoPeticionObtenerMediciones.setValue(EstadoPeticion.EXITO);
                        medicionesAMostrar.setValue(obtenerTodasListaWeightLatLngEnUna());

                    } catch (JSONException e) {
                        estadoPeticionObtenerMediciones.setValue(EstadoPeticion.ERROR);
                        textoErrorPeticion = "Error inesperado";

                        Log.e("REST","obtenerMedicionesDeHasta(): error al intentar pasar a json el objeto de mediciones");
                        Log.e("REST","obtenerMedicionesDeHasta(): "+e.getMessage());
                    }

                }else if(codigo == 204){
                    // vacio
                    // se realizo correctamente la peticion
                    estadoPeticionObtenerMediciones.setValue(EstadoPeticion.EXITO);
                    medicionesAMostrar.setValue(obtenerTodasListaWeightLatLngEnUna());
                }
                else if(codigo == 500){
                    // error
                    estadoPeticionObtenerMediciones.setValue(EstadoPeticion.ERROR);
                    textoErrorPeticion = "Error inesperado";
                    Log.e("REST","obtenerMedicionesDeHasta() codigo respuesta 500: "+cuerpo);
                }

            }


        });


    }

}