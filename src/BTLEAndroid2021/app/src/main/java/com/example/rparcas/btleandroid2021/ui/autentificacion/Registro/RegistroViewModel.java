package com.example.rparcas.btleandroid2021.ui.autentificacion.Registro;

import android.util.Log;

import com.example.rparcas.btleandroid2021.Utilidades;
import com.example.rparcas.btleandroid2021.logica.EstadoPeticion;
import com.example.rparcas.btleandroid2021.logica.Logica;
import com.example.rparcas.btleandroid2021.logica.PeticionarioREST;
import com.example.rparcas.btleandroid2021.modelo.Usuario;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

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

    private Usuario usuario;// TODO cambiar a usuario
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
    public Usuario getUsuario() {
        return usuario;
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

        // comprobamos el formulario
        if(comprobarFormulario(nombre,apellido,correo,contrasenya,repetirContrasenya)){
            // formulario correcto, hacemos peticion
            estadoIniciarSesion.setValue(EstadoPeticion.EN_PROCESO);

            Logica l = new Logica();
            try {

                // creamos el usuario
                 usuario = new Usuario(nombre,apellido,correo,Utilidades.stringToSHA1(contrasenya),telefono);

                // llamamos al registrar usuario, nos devuelve si se registra el id del usuario
                l.registrar_usuario(usuario,
                        new PeticionarioREST.RespuestaREST() {
                    @Override
                    public void callback(int codigo, String cuerpo){
                        if(codigo == 200){

                            // obtener el id del nuevo usuario del cuerpo

                            try {
                                JSONObject jsonObject = new JSONObject(cuerpo);
                                usuario.setId(jsonObject.getInt("id"));

                                // peticion exitosa
                                 estadoIniciarSesion.setValue(EstadoPeticion.EXITO);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }else if(codigo == 400){
                            // credenciales erronoes
                            errorDeLaPeticion = "Correo ya en uso";
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
            estadoIniciarSesion.setValue(EstadoPeticion.ERROR);

        }
    }

    /**
     *  Texto, Texto, Texto, Texto, Texto -> crearCuenta() -> T/F
     * Comprueba que el formulario este correcto, ningun campo vacio y las contrasenyas iguales
     * @author Ruben Pardo Casanova
     * 10/11/2021
     * @param nombre el nombre del usuario
     * @param apellido apellido del usuario
     * @param correo el correo del usuario
     * @param contrasenya la contrasenya
     * @param repetirContrasenya repetir la contrasenya anterior
     * @return True o False si el formulario es correcto
     */
    private boolean comprobarFormulario(String nombre, String apellido, String correo, String contrasenya, String repetirContrasenya) {

        boolean valido = true;

        if(nombre.trim().length()==0 || apellido.trim().length()==0
         || correo.trim().length()==0 || contrasenya.trim().length()==0
        || repetirContrasenya.trim().length()==0){
            // algun obligatorio esta vacio
            errorDeLaPeticion = "Todos los campos menos el telefono son obligatorios";
            valido = false;
        }else if(!contrasenya.equals(repetirContrasenya)){
            errorDeLaPeticion = "Las contraseñas deben coincidir";
            valido = false;
        }

        return valido;
    }
}