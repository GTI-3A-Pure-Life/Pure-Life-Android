package com.example.rparcas.btleandroid2021.logica;

import android.content.Context;
import android.util.Log;

import com.example.rparcas.btleandroid2021.Constantes.RESTConstantes;
import com.example.rparcas.btleandroid2021.SQLITE.MedicionDBHelper;
import com.example.rparcas.btleandroid2021.modelo.Medicion;
import com.example.rparcas.btleandroid2021.modelo.RegistroAveriaSensor;
import com.example.rparcas.btleandroid2021.modelo.RegistroBateriaSensor;
import com.example.rparcas.btleandroid2021.modelo.Usuario;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Clase con los metodos de la logica de negocio de la aplicacion
 * 03/10/2021
 * @author Rubén Pardo Casanova
 */
public class Logica {




    //-----------------------------------------------------
    //-----------------------------------------------------
    /**
     * Constructor de la clase Logica
     */
    public Logica(){

    }// ()


    //-----------------------------------------------------
    //-----------------------------------------------------


    /**
     *
     * Lista<MedicionesCO> -> publicarMediciones()
     *
     * Envia una lista de mediciones al servidor para guardarlas
     *
     * @param mediciones a enviar
     */
    public void publicarMediciones(List<Medicion> mediciones){

        PeticionarioREST elPeticionarioREST = new PeticionarioREST();

        String restEndpoint = RESTConstantes.URL + RESTConstantes.RESCURSO_MEDICIONES;


        elPeticionarioREST.hacerPeticionREST("POST", restEndpoint,
                "{\"res\": "+ Medicion.listaMedicionesToJSON(mediciones)+"}" ,
                new PeticionarioREST.RespuestaREST () {
                    @Override
                    public void callback(int codigo, String cuerpo) {

                        Log.d ("PRUEBA","codigo respuesta: " + codigo + " <-> \n" + cuerpo);

                    }
                });
    }

    /**
     * Lista<MedicionCO2> -> guardarMedicionEnLocal()
     *
     * Guardar mediciones co2 en la base de datos interna
     * @param mediciones mediciones a guardar
     * @param context Contexto de la aplicacion
     */
    public void guardarMedicionesEnLocal(List<Medicion> mediciones, Context context) {

        MedicionDBHelper medicionDBHelper = new MedicionDBHelper(context);
        medicionDBHelper.guardarMedicionesSQLITE(mediciones);
    }

    /**
     * obtener50MedicionesDeBDLocal() -> Lista<MedicionCO2>
     *
     * Obtiene como maximo 50 mediciones de la base de datos interna
     * @param context contexto de la aplicacion
     * @return Lista de mediciones
     */
    public List<Medicion> obtenerPrimeras50MedicionesDeBDLocal(Context context){
        MedicionDBHelper medicionDBHelper = new MedicionDBHelper(context);
        List<Medicion> mediciones = medicionDBHelper.obtener50Mediciones();

        return mediciones;
    }


    /**
     * borrar50MedicionesDeBDLocal()
     *
     * Borra como maximo las ultimas 50 mediciones de la base de datos interna
     * @param context contexto de la aplicacion
     *
     */
    public void borrarPrimeras50MedicionesDeBDLocal(Context context){
        MedicionDBHelper medicionDBHelper = new MedicionDBHelper(context);
        medicionDBHelper.borrarUltimas50Mediciones();

    }


    /**
     * RegistroBateriaSensor -> guardarRegistroBateria() <-
     * @author Ruben Pardo Casanova
     * @param registroBateriaSensor registro a enviar al servidor
     */
    public void guardarRegistroBateria(RegistroBateriaSensor registroBateriaSensor) {
        PeticionarioREST elPeticionarioREST = new PeticionarioREST();

        String restEndpoint = RESTConstantes.URL + RESTConstantes.RESCURSO_REGISTRO_ESTADO_BATERIA;

        Log.d("PRUEBA", "guardarRegistroBateria endpoint: "+restEndpoint);

        elPeticionarioREST.hacerPeticionREST("POST", restEndpoint,
                "{\"res\": "+ registroBateriaSensor.toJSON()+"}" ,
                new PeticionarioREST.RespuestaREST () {
                    @Override
                    public void callback(int codigo, String cuerpo) {

                        Log.d ("PRUEBA","codigo respuesta: " + codigo + " <-> \n" + cuerpo);

                    }
                });
    }

    /**
     * RegistroBateriaSensor -> guardarRegistroBateria() <-
     * @author Ruben Pardo Casanova
     * @param registroAveriaSensor registro a enviar al servidor
     */
    public void guardarRegistroAveria(RegistroAveriaSensor registroAveriaSensor) {
        PeticionarioREST elPeticionarioREST = new PeticionarioREST();

        String restEndpoint = RESTConstantes.URL + RESTConstantes.RESCURSO_REGISTRO_ESTADO_AVERIA;

        Log.d("PRUEBA", "guardarRegistroBateria endpoint: "+restEndpoint);

        elPeticionarioREST.hacerPeticionREST("POST", restEndpoint,
                "{\"res\": "+ registroAveriaSensor.toJSON()+"}" ,
                new PeticionarioREST.RespuestaREST () {
                    @Override
                    public void callback(int codigo, String cuerpo) {

                        Log.d ("PRUEBA","codigo respuesta: " + codigo + " <-> \n" + cuerpo);

                    }
                });
    }

    /**
     * correo:Texto,contrasenya:Texto-> iniciar_sesion() <-
     * Usuario <-
     * @author Ruben Pardo Casanova
     * 09/11/2021
     * @param correo el correo del usuario
     * @param contrasenya la contrasenya del usuario
     * @param  callbackRespuestaRest equivalente al return
     * @return el return se realiza mediante el callback que se pasa por parametro
     */
    public void iniciar_sesion(String correo, String contrasenya,
                               PeticionarioREST.RespuestaREST callbackRespuestaRest) {
        PeticionarioREST elPeticionarioREST = new PeticionarioREST();

        String restEndpoint = RESTConstantes.URL + RESTConstantes.RESCURSO_INICIAR_SESION;

        Log.d("PRUEBA", "iniciar_sesion endpoint: "+restEndpoint);

        elPeticionarioREST.hacerPeticionREST("POST", restEndpoint,
                "{\"res\": {\"correo\":\""+ correo+"\",\"contrasenya\":\""+ contrasenya+"\"}}" ,
                callbackRespuestaRest);
    }

    /**
     * Usuario -> iniciar_sesion() <-
     * id:N <-
     * @author Ruben Pardo Casanova
     * 10/11/2021
     * @param usuarioARegistrar el usuario que se envia por la peticion
     * @return el return se realiza mediante el callback que se pasa por parametro
     */
    public void registrar_usuario(Usuario usuarioARegistrar, PeticionarioREST.RespuestaREST respuestaREST) {
        PeticionarioREST elPeticionarioREST = new PeticionarioREST();

        String restEndpoint = RESTConstantes.URL + RESTConstantes.RESCURSO_REGISTRARSE;

        Log.d("PRUEBA", "registrar_usuario endpoint: "+restEndpoint);
        String cuerpo = "{\"res\":"+usuarioARegistrar.toJSON()+"}";
        Log.d("PRUEBA", "registrar_usuario cuerpo: "+cuerpo);
        elPeticionarioREST.hacerPeticionREST("POST", restEndpoint,
                 cuerpo,
                respuestaREST);
    }


    /**
     * Texto,Texto -> obtenerMedicionesDeHasta() <-
     * Lista<Medicion> <-
     * @author Ruben Pardo Casanova
     * 12/11/2021
     *
     * Obtener mediciones dentro de un rango temporal
     *
     * @param fechaInicio
     * @param fechaFin
     * @return lista de mediciones mediante el callback
     */
    public void obtenerMedicionesDeHasta(String fechaInicio, String fechaFin, PeticionarioREST.RespuestaREST laRespuesta) {



        PeticionarioREST elPeticionarioREST = new PeticionarioREST();

        String restEndpoint = RESTConstantes.URL + RESTConstantes.RESCURSO_MEDICIONES
                +"/"+fechaInicio+"/"+fechaFin;

        elPeticionarioREST.hacerPeticionREST("GET", restEndpoint,
                null, laRespuesta);

    }


    /**
     *   fecha-desde:Texto, fecha_hasta:Texto, latitud:R, longitud:R, radio:R ->
     *   obtenerCalidadAirePorTiempoYZona() <-
     *  <- Lista<InformeCalidadAire>
     * @author Ruben Pardo Casanova
     * 23/11/2021
     *
     * Obtener la calidad de aire dentro de un rango temporal y una zona ciruclar
     *
     * @param fechaInicio
     * @param fechaFin
     * @param latitud latitud del punto central de la zona
     * @param longitud longitud del punto central de la zona
     * @param radio radio de la zona a obtener la calidad
     * @return lista de informes de calidad
     */
    public void obtenerCalidadAirePorTiempoYZona(String fechaInicio, String fechaFin,
                                                 double latitud, double longitud, double radio,
                                                 PeticionarioREST.RespuestaREST laRespuesta) {



        PeticionarioREST elPeticionarioREST = new PeticionarioREST();

        // GET/calidad_aire/zona?fecha_inicio:Texto&fecha_fin:Texto&latitud:R&longitud:R&radio:N
        String restEndpoint = RESTConstantes.URL + RESTConstantes.RESCURSO_CALIDAD_AIRE_ZONA
                +"?fecha_inicio="+fechaInicio+"&fecha_fin="+fechaFin
                +"&latitud="+latitud+"&longitud="+longitud+"&radio="+radio;

        elPeticionarioREST.hacerPeticionREST("GET", restEndpoint,
                null, laRespuesta);

    }


    /**
     *   fecha-desde:Texto, fecha_hasta:Texto, idUsuario:N->
     *   obtenerCalidadAirePorTiempoYUsuario() <-
     *  <- Lista<InformeCalidadAire>
     * @author Ruben Pardo Casanova
     * 23/11/2021
     *
     * Obtener la calidad de aire dentro de un rango temporal de un usuario
     *
     * @param fechaIni la fecha inicio
     * @param fechaFin la fecha final
     * @param idUsuario latitud del punto central de la zona
     * @return lista de informes de calidad
     */
    public void obtenerCalidadAirePorTiempoYUsuario(String fechaIni, String fechaFin, int idUsuario, PeticionarioREST.RespuestaREST laRespuesta) {

        PeticionarioREST elPeticionarioREST = new PeticionarioREST();

        // GET/calidad_aire/zona?fecha_inicio:Texto&fecha_fin:Texto&latitud:R&longitud:R&radio:N
        String restEndpoint = RESTConstantes.URL + RESTConstantes.RESCURSO_CALIDAD_AIRE_USUARIO
                +"?fecha_inicio="+fechaIni+"&fecha_fin="+fechaFin
                +"&idUsuario="+idUsuario;

        elPeticionarioREST.hacerPeticionREST("GET", restEndpoint,
                null, laRespuesta);
    }


    /**
     *   fecha-desde:Texto, fecha_hasta:Texto, idUsuario:N->
     *   obtenerMedicionesDeUnUsuarioHoy() <-
     *  <- Lista<Medicion>
     * @author Ruben Pardo Casanova
     * 23/11/2021
     *
     * Obtener las mediciones de un dia de un usuario
     *
     * @param fechaIni la fecha inicio
     * @param fechaFin la fecha final
     * @param idUsuario latitud del punto central de la zona
     * @return lista de mediciones
     */
    public void obtenerMedicionesDeUnUsuarioHoy(String fechaIni, String fechaFin, int idUsuario, PeticionarioREST.RespuestaREST laRespuesta) {

        PeticionarioREST elPeticionarioREST = new PeticionarioREST();
        // GET/medicion/usuario?fecha_inicio:Texto&fecha_fin:Texto&idUsuario:N
        String restEndpoint = RESTConstantes.URL + RESTConstantes.RESCURSO_MEDICIONES_USUARIO
                +"?fecha_inicio="+fechaIni+"&fecha_fin="+fechaFin
                +"&idUsuario="+idUsuario;

        elPeticionarioREST.hacerPeticionREST("GET", restEndpoint,
                null, laRespuesta);
    }
}
