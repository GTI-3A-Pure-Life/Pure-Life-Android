package com.example.rparcas.btleandroid2021.ui.estadisticas;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * EstadisticasViewModel.java
 * @author Ruben Pardo Casanova
 * 01/11/2021
 * Clase que gestiona los datos que requiere el EstadisticasFragment y se encarga de notificarlo para que
 * repinte la vista
 */
public class EstadisticasViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public EstadisticasViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is estadisticas fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}