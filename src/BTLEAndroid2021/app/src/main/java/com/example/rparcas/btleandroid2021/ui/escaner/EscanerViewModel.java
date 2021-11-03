package com.example.rparcas.btleandroid2021.ui.escaner;

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

    private MutableLiveData<Medicion.NivelPeligro> nivelPeligro;
    private String nombreDispositivo = "";
    //prefijo que usaremos en el nombre del sensor para identificar nuestros sensores
    private static String prefijo = "GTI-3A-";
    private MutableLiveData<Boolean> resultadoEscaner;

    public EscanerViewModel() {
        nivelPeligro = new MutableLiveData<>();
        nivelPeligro.setValue(Medicion.NivelPeligro.ALTO);
    }

    public LiveData<Medicion.NivelPeligro> getNivelPeligro() {
        return nivelPeligro;
    }

    /**
     * Getter del resultado de escaner
     * @author Lorena Florescu
     * @version 02/11/2021
     * @return devuelve true o false según si la lectura ha sido correcta o no
     */

    public MutableLiveData<Boolean> getResultadoEscaner() {
        return resultadoEscaner;
    }

    /**
     * Getter del nombre del dispositivo
     * @author Lorena Florescu
     * @version 02/11/2021
     * @return devuelve el nombre del dispositivo
     */
    public String getNombreDispositivo() {
        return nombreDispositivo;
    }

    /**
     *Arrancaremos el servicio según si la lectura QR ha sido correcta o no y cambiamos
     * el estado de la variable resultadoEscaner según si la lectura coincide con nuestros
     * dispositivos o no
     * @author Lorena Florescu
     * @version 02/11/2021
     * @param lectura le pasamos la lectura que recogemos del escaner QR
     */
    public void arrancarServicio(String lectura){

        nombreDispositivo=lectura;

        if(lectura != null && lectura.startsWith(prefijo)) {
            resultadoEscaner.setValue(true);
        }
        else
           resultadoEscaner.setValue(false);

    }
}