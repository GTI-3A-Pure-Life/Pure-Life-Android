package com.example.rparcas.btleandroid2021.ui.escaner;

import android.util.Log;

import com.example.rparcas.btleandroid2021.modelo.Medicion;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * EscanerViewModel.java
 * Ruben Pardo Casanova 01/11/2021
 * Clase que gestiona los datos que requiere el EscanerFragment y se encarga de notificarlo para que
 * repinte la vista
 */
public class EscanerViewModel extends ViewModel {

    private static MutableLiveData<Medicion.NivelPeligro> nivelPeligro;
    private  static MutableLiveData<Boolean> estoyEscaneando;
    private String nombreDispositivo = "";
    //prefijo que usaremos en el nombre del sensor para identificar nuestros sensores
    private final String PREFIJO = "GTI-3A-";


    public EscanerViewModel() {
        /*
         * Cuando se vuelve a crear el objeto si ya hay datos de antes
         * inicialzar el nivel de peligro a este para que la vista
         * se pinte con el ultimo
         */
        if(nivelPeligro !=null && estoyEscaneando.getValue()){
            nivelPeligro = new MutableLiveData<>(nivelPeligro.getValue());
        }else{
            nivelPeligro = new MutableLiveData<>();
        }

        if(estoyEscaneando !=null){
            estoyEscaneando = new MutableLiveData<>(estoyEscaneando.getValue());
        }else{
            estoyEscaneando = new MutableLiveData<>(false);
        }

    }

    public LiveData<Medicion.NivelPeligro> getNivelPeligro() {
        return nivelPeligro;
    }
    public void setNivelPeligro(Medicion.NivelPeligro nivel) {
        Log.d("PRUEBA", "setNivelPeligro: buenas tardes");
        nivelPeligro.setValue(nivel);
    }

    //--------------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------------
    public String getNombreDispositivo() {
        return nombreDispositivo;
    }

    //--------------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------------
    public void setNombreDispositivo(String nombre){
        this.nombreDispositivo=nombre;
    }

    public MutableLiveData<Boolean> getEstoyEscaneando() {
        return estoyEscaneando;
    }

    public void setEstoyEscaneando(Boolean estoyEscaneando) {
        this.estoyEscaneando.setValue(estoyEscaneando);
    }
}