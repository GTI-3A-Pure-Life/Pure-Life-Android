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



    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------


}