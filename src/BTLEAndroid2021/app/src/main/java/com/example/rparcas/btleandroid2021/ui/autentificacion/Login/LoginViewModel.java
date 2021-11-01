package com.example.rparcas.btleandroid2021.ui.autentificacion.Login;

import com.example.rparcas.btleandroid2021.logica.EstadoPeticion;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * LoginViewModel.java
 * Ruben Pardo Casanova 01/11/2021
 * Clase que gestiona los datos que requiere el LoginFragment y se encarga de notificarlo para que
 * repinte la vista
 */
public class LoginViewModel extends ViewModel {

    private MutableLiveData<String> usuario;// TODO cambiar a usuario
    private MutableLiveData<EstadoPeticion> estadoIniciarSesion;// TODO cambiar a usuario
    private String errorDeLaPeticion;

    public LoginViewModel() {
        estadoIniciarSesion = new MutableLiveData<EstadoPeticion>(EstadoPeticion.SIN_ACCION);
    }

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    public LiveData<String> getText() {
        return usuario;
    }
    public LiveData<EstadoPeticion> getEstadoPeticionIniciarSesion() {
        return estadoIniciarSesion;
    }

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    public String getError(){
        return errorDeLaPeticion;
    }

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------

    /**
     * Texto, Texto -> iniciarSesion() -> Texto, EstadoPeticion, Usuario
     *
     * Inicia la peticion de iniciar sesion al servidor
     * ciframos la contrasenya de lado del cliente siempre
     * cunado la peticion termine devolvera un usuario o un error
     * Indicamos este resultado a la vista mediante el EstadoPeticion
     *
     * @param correo del usuario
     * @param contrasenya del usuario
     */
    public void iniciarSesion(String correo, String contrasenya) {
        // TODO hacer logica
        //errorDeLaPeticion = "ERROR";
        estadoIniciarSesion.setValue(EstadoPeticion.EXITO);

    }
}