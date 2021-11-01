package com.example.rparcas.btleandroid2021.ui.perfil;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


/**
 * PerfilViewModel.java
 * @author Ruben Pardo Casanova
 * 01/11/2021
 * Clase que gestiona los datos que requiere el PerfilFragment y se encarga de notificarlo para que
 * repinte la vista
 */
public class PerfilViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PerfilViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is perfil fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}