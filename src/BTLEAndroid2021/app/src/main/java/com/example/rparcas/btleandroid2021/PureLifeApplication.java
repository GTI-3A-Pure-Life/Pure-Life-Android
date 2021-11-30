package com.example.rparcas.btleandroid2021;

import android.app.Application;

import com.example.rparcas.btleandroid2021.logica.SharedPreferencesHelper;
import com.example.rparcas.btleandroid2021.modelo.Usuario;

/**
 * PureLifeApplication.java
 * Clase para guardar variables importantes de manera global
 * 10/11/2021
 * @author Ruben Pardo Casanova
 */
public class PureLifeApplication extends Application {

    //---------------------------------------------------------------
    //---------------------------------------------------------------
    private Usuario usuario; // el usuario logeado

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferencesHelper.initializeInstance(this);
        SharedPreferencesHelper pref = SharedPreferencesHelper.getInstance();
        usuario = pref.getUsuario();
    }

    public Usuario getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        SharedPreferencesHelper pref = SharedPreferencesHelper.getInstance();
        pref.setUsuario(usuario);
    }
}
