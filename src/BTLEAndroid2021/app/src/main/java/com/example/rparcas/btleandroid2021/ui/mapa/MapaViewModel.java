package com.example.rparcas.btleandroid2021.ui.mapa;

import android.util.Log;

import com.example.rparcas.btleandroid2021.logica.EstadoPeticion;
import com.example.rparcas.btleandroid2021.logica.Logica;
import com.example.rparcas.btleandroid2021.modelo.Medicion;
import com.example.rparcas.btleandroid2021.modelo.Posicion;

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

        Logica l = new Logica();
        //l.obtenerMedicionesDeHasta()
        medicionesObtenidas.add(new Medicion(10,1,"",new Posicion(0,0), Medicion.TipoMedicion.CO));
        medicionesObtenidas.add(new Medicion(10,1,"",new Posicion(0,0), Medicion.TipoMedicion.SO2));
        medicionesObtenidas.add(new Medicion(10,1,"",new Posicion(1,0), Medicion.TipoMedicion.SO2));
        medicionesObtenidas.add(new Medicion(10,1,"",new Posicion(0,1), Medicion.TipoMedicion.NO2));
        medicionesObtenidas.add(new Medicion(10,1,"",new Posicion(1,1), Medicion.TipoMedicion.O3));


        estadoPeticionObtenerMediciones.setValue(EstadoPeticion.EXITO);

    }



}