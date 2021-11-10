package com.example.rparcas.btleandroid2021.ui.autentificacion.Login;

import android.util.Log;

import com.example.rparcas.btleandroid2021.PureLifeApplication;
import com.example.rparcas.btleandroid2021.Utilidades;
import com.example.rparcas.btleandroid2021.logica.EstadoPeticion;
import com.example.rparcas.btleandroid2021.logica.Logica;
import com.example.rparcas.btleandroid2021.logica.PeticionarioREST;
import com.example.rparcas.btleandroid2021.modelo.Usuario;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

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

    private Usuario usuario;// TODO cambiar a usuario
    private MutableLiveData<EstadoPeticion> estadoIniciarSesion;// TODO cambiar a usuario
    private String errorDeLaPeticion;

    public LoginViewModel() {
        estadoIniciarSesion = new MutableLiveData<EstadoPeticion>(EstadoPeticion.SIN_ACCION);
    }

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    public Usuario getUsuario() {
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

        // comprobamos el formulario
        if(comprobarFormulario(correo,contrasenya)){
            // formulario correcto, hacemos peticion
            estadoIniciarSesion.setValue(EstadoPeticion.EN_PROCESO);
            Logica l = new Logica();
            try {
                l.iniciar_sesion(correo, Utilidades.stringToSHA1(contrasenya), new PeticionarioREST.RespuestaREST() {
                    @Override
                    public void callback(int codigo, String cuerpo){
                        if(codigo == 200){

                            // obtener el usuario del cuerpo
                            try {

                                usuario = new Usuario(cuerpo);

                                // peticion exitosa
                                estadoIniciarSesion.setValue(EstadoPeticion.EXITO);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.d("LOGIN--", "callback: "+e.getMessage());
                                errorDeLaPeticion = "Error inesperado";
                                estadoIniciarSesion.setValue(EstadoPeticion.ERROR);
                            }

                        }else if(codigo == 401){
                            // credenciales erronoes
                            errorDeLaPeticion = "No existe ese usuario";
                            estadoIniciarSesion.setValue(EstadoPeticion.ERROR);
                        }else{
                            // error inesperado
                            errorDeLaPeticion = "Error inesperado";
                            estadoIniciarSesion.setValue(EstadoPeticion.ERROR);
                        }
                    }
                });
            } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
                e.printStackTrace();
                // error inesperado
                errorDeLaPeticion = "Error inesperado";
                estadoIniciarSesion.setValue(EstadoPeticion.ERROR);
            }
        }else{
            // formulario incorrecto, avisamos
            errorDeLaPeticion = "Todos los campos son obligatorios";
            estadoIniciarSesion.setValue(EstadoPeticion.ERROR);

        }


    }

    /**
     *
     * Texto, Texto -> iniciarSesion() -> T/F
     * @author Ruben Pardo Casanova
     *  09/11/2021
     *
     * Comprueba si el formulario de inicio de sesion es valido
     * @param correo el correo del usuario
     * @param contrasenya la contrasenya del usuario
     * @return T/F si el formulario esta correcto o no
     */
    private boolean comprobarFormulario(String correo, String contrasenya){
        boolean valido = true;
        // comprobar que no haya nada vacio
        if(correo.trim().length()==0 && contrasenya.trim().length()==0){
            valido = false;
        }

        return valido;
    }
}