package com.example.rparcas.btleandroid2021.modelo;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * @author Ruben Pardo Casanova
 * 10/11/2021
 * Clase que representa un usuario de la aplicacion
 */
public class Usuario implements Serializable {

    private int id;
    private String nombre;
    private String correo;
    private String contrasenya;
    private Posicion posCasa;
    private Posicion posTrabajo;

    /**
     * Texto -> constructor()->
     *
     * @author Ruben Pardo Casanova
     * 10/11/2021
     *
     * @param json el json obtenido de la peticion de iniciar sesion
     */
    public Usuario(String json) throws JSONException {

        JSONObject jsonObject = new JSONObject(json);

        this.id = jsonObject.getInt("id");
        this.nombre = jsonObject.getString("nombre");
        this.correo = jsonObject.getString("correo");
        this.contrasenya = jsonObject.getString("contrasenya");


        if(!jsonObject.isNull("posCasa")){
            JSONObject posCasaJSON = jsonObject.getJSONObject("posCasa");
            this.posCasa = new Posicion(posCasaJSON.getDouble("latitud"),posCasaJSON.getDouble("longitud"));
        }

        if(!jsonObject.isNull("posTrabajo")){
            JSONObject posTrabajoJSON = jsonObject.getJSONObject("posTrabajo");
            this.posTrabajo = new Posicion(posTrabajoJSON.getDouble("latitud"),posTrabajoJSON.getDouble("longitud"));
        }



    }

    //-----------------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------------
    /**
     * Usuario -> toJSON() -> Texto
     * @author Ruben Pardo Casnova
     * 10/11/2021
     * @return devuelve el usuario en formato json
     */
    public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
    //-----------------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------------
    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", correo='" + correo + '\'' +
                ", contrasenya='" + contrasenya + '\'' +
                ", posCasa=" + posCasa +
                ", posTrabajo=" + posTrabajo +
                '}';
    }
    //-----------------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------------
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public String getContrasenya() {
        return contrasenya;
    }

    public Posicion getPosCasa() {
        return posCasa;
    }

    public Posicion getPosTrabajo() {
        return posTrabajo;
    }



}
