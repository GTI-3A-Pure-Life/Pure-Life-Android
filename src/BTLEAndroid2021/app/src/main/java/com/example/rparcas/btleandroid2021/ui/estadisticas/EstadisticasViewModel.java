package com.example.rparcas.btleandroid2021.ui.estadisticas;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.rparcas.btleandroid2021.logica.EstadoPeticion;
import com.example.rparcas.btleandroid2021.logica.Logica;
import com.example.rparcas.btleandroid2021.logica.PeticionarioREST;
import com.example.rparcas.btleandroid2021.modelo.Medicion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * EstadisticasViewModel.java
 * @author Ruben Pardo Casanova
 * 01/11/2021
 * Clase que gestiona los datos que requiere el EstadisticasFragment y se encarga de notificarlo para que
 * repinte la vista
 */
public class EstadisticasViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private String textoErrorPeticion;
    public ArrayList<Medicion> medicionesCOObtenidas;
    public ArrayList<Medicion> medicionesNO2Obtenidas;
    public ArrayList<Medicion> medicionesSO2Obtenidas;
    public ArrayList<Medicion> medicionesO3Obtenidas;
    public MutableLiveData<ArrayList<Medicion>> medicionesAMostrar;
    private MutableLiveData<EstadoPeticion> estadoPeticionObtenerMediciones;

    public EstadisticasViewModel() {

        medicionesCOObtenidas =  new ArrayList<>();
        medicionesSO2Obtenidas =  new ArrayList<>();
        medicionesNO2Obtenidas =  new ArrayList<>();
        medicionesO3Obtenidas =  new ArrayList<>();
    }


    public LiveData<String> getText() {
        return mText;
    }

    public void obtenerMedicionesHoy()  {

        medicionesCOObtenidas =  new ArrayList<>();
        medicionesSO2Obtenidas =  new ArrayList<>();
        medicionesNO2Obtenidas =  new ArrayList<>();
        medicionesO3Obtenidas =  new ArrayList<>();

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
                        for (int i = 0; i < medicionesJSON.length(); i++) {
                            Medicion m = new Medicion((JSONObject) medicionesJSON.get(i));
                            if(Medicion.TipoMedicion.CO==m.getTipoMedicion())
                                medicionesCOObtenidas.add(m);
                            if(Medicion.TipoMedicion.NO2==m.getTipoMedicion())
                                medicionesNO2Obtenidas.add(m);
                            if(Medicion.TipoMedicion.SO2==m.getTipoMedicion())
                                medicionesSO2Obtenidas.add(m);
                            if(Medicion.TipoMedicion.O3==m.getTipoMedicion())
                                medicionesO3Obtenidas.add(m);
                        }

                        //ordenar por fecha

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if(codigo == 204){

                }
                else if(codigo == 500){
                    // vacio
                    //estadoPeticion.setValue(EstadoPeticion.ERROR);
                    //textoErrorPeticion = "Error inesperado";
                    Log.e("REST","obtenerMedicionesDeHasta() codigo respuesta 500: "+cuerpo);
                }
            }
        });
    }
}