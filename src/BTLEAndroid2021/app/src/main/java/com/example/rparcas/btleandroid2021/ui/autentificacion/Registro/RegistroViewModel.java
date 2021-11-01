package com.example.rparcas.btleandroid2021.ui.autentificacion.Registro;

import com.example.rparcas.btleandroid2021.logica.EstadoPeticion;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * RegistroViewModel.java
 * Ruben Pardo Casanova 01/11/2021
 * Clase que gestiona los datos que requiere el RegistroFragment y se encarga de notificarlo para que
 * repinte la vista
 */
public class RegistroViewModel extends ViewModel {

    private MutableLiveData<String> usuario;// TODO cambiar a usuario
    private MutableLiveData<EstadoPeticion> estadoIniciarSesion;
    private String errorDeLaPeticion;

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    public RegistroViewModel() {
        estadoIniciarSesion = new MutableLiveData<EstadoPeticion>(EstadoPeticion.SIN_ACCION);
    }

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    public String getError() {
        return errorDeLaPeticion;
    }

    public LiveData<EstadoPeticion> getEstadoPeticionIniciarSesion() {
        return estadoIniciarSesion;
    }


    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------

    /**
     * Texto, Texto, Texto, Texto, Texto, Texto -> crearCuenta() -> Texto, Usuario, EstadoPeticion
     *
     * Inicia la peticion de crear cuenta al servidor
     * ciframos la contrasenya de lado del cliente siempre
     * validamos el formulario, error si no es valido, si correcto realiza la peticion al server
     * cunado la peticion termine devolvera un usuario o un error
     * Indicamos este resultado a la vista mediante el EstadoPeticion
     *
     * @param nombre el nombre del usuario
     * @param apellido los apellidos del usuario
     * @param correo el correo
     * @param contrasenya la contrasenya
     * @param repetirContrasenya repetir la contrasenya para evitar que se equivoque
     * @param telefono telefono de contacto (opcional)
     */
    public void crearCuenta(String nombre, String apellido, String correo, String contrasenya,
                            String repetirContrasenya, String telefono){

        estadoIniciarSesion.setValue(EstadoPeticion.EN_PROCESO);

        // TODO comprobar formulario y hacer peticion

        estadoIniciarSesion.setValue(EstadoPeticion.EXITO);
    }
}