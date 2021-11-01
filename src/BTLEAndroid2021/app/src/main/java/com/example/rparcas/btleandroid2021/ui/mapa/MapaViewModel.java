package com.example.rparcas.btleandroid2021.ui.mapa;

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

    private MutableLiveData<String> mText;

    public MapaViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is mapa fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}