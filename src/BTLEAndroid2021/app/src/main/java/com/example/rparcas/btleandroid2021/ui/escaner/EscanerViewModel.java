package com.example.rparcas.btleandroid2021.ui.escaner;

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

    private MutableLiveData<String> mText;

    public EscanerViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is escaner fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}