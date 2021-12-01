package com.example.rparcas.btleandroid2021.ui.estadisticas;

import android.util.Log;

import com.example.rparcas.btleandroid2021.Utilidades;
import com.example.rparcas.btleandroid2021.logica.EstadoPeticion;
import com.example.rparcas.btleandroid2021.logica.Logica;
import com.example.rparcas.btleandroid2021.logica.PeticionarioREST;
import com.example.rparcas.btleandroid2021.modelo.InformeCalidad;
import com.example.rparcas.btleandroid2021.modelo.Medicion;
import com.example.rparcas.btleandroid2021.modelo.Posicion;
import com.example.rparcas.btleandroid2021.modelo.Usuario;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.rparcas.btleandroid2021.logica.EstadoPeticion;
import com.example.rparcas.btleandroid2021.logica.Logica;
import com.example.rparcas.btleandroid2021.logica.PeticionarioREST;
import com.example.rparcas.btleandroid2021.modelo.Medicion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * EstadisticasViewModel.java
 * @author Ruben Pardo Casanova
 * 01/11/2021
 * Clase que gestiona los datos que requiere el EstadisticasFragment y se encarga de notificarlo para que
 * repinte la vista
 */
public class EstadisticasViewModel extends ViewModel {

    private static final String TAG = "EstadisticasViewModel";
    //-------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------
    private MutableLiveData<InformeCalidad[]> informesCalidadCasa;
    private MutableLiveData<InformeCalidad[]> informesCalidadTrabajo;
    private MutableLiveData<InformeCalidad[]> informesCalidadExterior;
    private MutableLiveData<String> textoExposicionDeGas;
    private MutableLiveData<EstadoPeticion> estadoPeticionCalidadAire;
    private MutableLiveData<String> mText;
    private String textoErrorPeticion;
    public ArrayList<Medicion> medicionesCOObtenidas;
    public ArrayList<Medicion> medicionesNO2Obtenidas;
    public ArrayList<Medicion> medicionesSO2Obtenidas;
    public ArrayList<Medicion> medicionesO3Obtenidas;
    public MutableLiveData<ArrayList<Medicion>> medicionesAMostrar;
    private MutableLiveData<EstadoPeticion> estadoPeticionObtenerMediciones;

    //-------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------
    public EstadisticasViewModel() {
        informesCalidadCasa = new MutableLiveData<>();
        informesCalidadTrabajo = new MutableLiveData<>();
        informesCalidadExterior = new MutableLiveData<>();
        textoExposicionDeGas = new MutableLiveData<>();
        estadoPeticionCalidadAire = new MutableLiveData<>();

        medicionesCOObtenidas =  new ArrayList<>();
        medicionesSO2Obtenidas =  new ArrayList<>();
        medicionesNO2Obtenidas =  new ArrayList<>();
        medicionesO3Obtenidas =  new ArrayList<>();
    }

    //-------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------
    /**
     * Usuario -> obtenerCalidadAireDeLasZonasDeUnUsuario() -> Texto, Lista<Lista<InformeCalidad>>
     * Ruben Pardo Casanova
     * 23/11/2021
     *
     * Hacemos tres peticiones, obtener la calidad de aire por zona de hoy de las ubicaciones
     * de casa y trabajo si tiene(InformeCalidadCasa y InformeCalidadTrabajo),
     * y una tercera de la calidad de aire de hoy del usuario (InformeCalidadExterior)
     *
     *
     * @param idUsuario id del usuario que inicio sesion en la aplicacion
     * @param casa posicion de la casa del usuario
     * @param trabajo posicion del trabajo del usuario
     */
    public void obtenerCalidadAireDeLasZonasDeUnUsuario(Posicion casa, Posicion trabajo, int idUsuario){
        // ponemos a cargar los tres
        estadoPeticionCalidadAire.setValue(EstadoPeticion.EN_PROCESO);

        // obtener fecha inicio y fecha fin
        Timestamp hoy = new Timestamp(System.currentTimeMillis());
        String fechaHoy = new SimpleDateFormat("yyyy-MM-dd").format(hoy);

        String fechaIni = fechaHoy+" 00:00:00";
        String fechaFin = fechaHoy+" 23:59:59";

        fechaIni = "2021-11-26 00:00:00";
        fechaFin = "2021-11-26 23:59:59";

        obtenerCalidadAireDeUnaZonaDeHasta(casa,informesCalidadCasa,fechaIni,fechaFin);
        obtenerCalidadAireDeUnaZonaDeHasta(trabajo,informesCalidadTrabajo,fechaIni,fechaFin);
        obtenerCalidadAireDeUnUsuarioHoy(idUsuario,fechaIni,fechaFin);

    }

    public void obtenerMedicionesDeUnUsuarioHoy(int id)  {

        medicionesCOObtenidas =  new ArrayList<>();
        medicionesSO2Obtenidas =  new ArrayList<>();
        medicionesNO2Obtenidas =  new ArrayList<>();
        medicionesO3Obtenidas =  new ArrayList<>();

        Logica l = new Logica();
        Timestamp hoy = new Timestamp(System.currentTimeMillis());
        String fechaHoy = new SimpleDateFormat("yyyy-MM-dd").format(hoy);

        String fechaIni = fechaHoy+" 00:00:00";
        String fechaFin = fechaHoy+" 23:59:59";

        fechaIni = "2021-11-26 00:00:00";
        fechaFin = "2021-11-26 23:59:59";

        l.obtenerMedicionesDeUnUsuarioHoy(fechaIni,fechaFin,id,new PeticionarioREST.RespuestaREST() {
            @Override
            public void callback(int codigo, String cuerpo) {

                if(codigo == 200){
                    // hay datos
                    try {
                        JSONArray medicionesJSON = new JSONArray(cuerpo);
                        // obtenemos todas las referencias a los objetos
                        // recorremos los objetos
                        for (int i = 0; i < medicionesJSON.length(); i++) {
                            Medicion m = new Medicion((JSONObject) medicionesJSON.get(i));
                            if(Medicion.TipoMedicion.CO==m.getTipoMedicion())
                                medicionesCOObtenidas.add(m);
                            if(Medicion.TipoMedicion.NO2==m.getTipoMedicion())
                                medicionesNO2Obtenidas.add(m);
                            if(Medicion.TipoMedicion.SO2==m.getTipoMedicion())
                                medicionesSO2Obtenidas.add(m);
                            if(Medicion.TipoMedicion.O3==m.getTipoMedicion())
                                medicionesO3Obtenidas.add(m);
                        }

                        //ordenar por fecha
                        Utilidades.ordenarMedicionesPorFecha(medicionesCOObtenidas);
                        Utilidades.ordenarMedicionesPorFecha(medicionesNO2Obtenidas);
                        Utilidades.ordenarMedicionesPorFecha(medicionesSO2Obtenidas);
                        Utilidades.ordenarMedicionesPorFecha(medicionesO3Obtenidas);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if(codigo == 204){

                }
                else if(codigo == 500){
                    // vacio
                    //estadoPeticion.setValue(EstadoPeticion.ERROR);
                    //textoErrorPeticion = "Error inesperado";
                    Log.e("REST","obtenerMedicionesDeHasta() codigo respuesta 500: "+cuerpo);
                }
            }
        });
    }

    //-------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------
    /**
     * (Solo replica la peticion de la logica, mas comentarios en ese metodo)
     * Metodo que hace la peticion de obtener la calidad de aire de una zona por tiempo
     * @param zona la zona donde se quiere caluclar la calidad aire
     * @param informesCalidadLiveData informe de calidad que se va a poner el resultado (return)
     * @param fechaIni la fecha inicio
     * @param fechaFin la fecha final
     */
    private void obtenerCalidadAireDeUnaZonaDeHasta(Posicion zona, MutableLiveData<InformeCalidad[]> informesCalidadLiveData,
                                                    String fechaIni, String fechaFin) {
        if(zona==null){
            // si no tiene posicion
            informesCalidadLiveData.setValue(null);
        }
        else{
            // hacer peticion
            Logica l = new Logica();
            l.obtenerCalidadAirePorTiempoYZona(fechaIni, fechaFin, zona.getLatitud(), zona.getLongitud(), 18, new PeticionarioREST.RespuestaREST() {
                @Override
                public void callback(int codigo, String cuerpo) {
                    if(codigo == 200){
                        try {
                            JSONArray informesJSON = new JSONArray(cuerpo);
                            InformeCalidad[] informesCalidad = new InformeCalidad[4];
                            for(int i=0;i<informesJSON.length();i++){
                                InformeCalidad m = new InformeCalidad((JSONObject) informesJSON.get(i));
                                informesCalidad[i] = m;
                            }

                            informesCalidadLiveData.setValue(informesCalidad);

                        } catch (JSONException e) {
                            estadoPeticionCalidadAire.setValue(EstadoPeticion.ERROR);
                            e.printStackTrace();
                        }
                    }else{
                        estadoPeticionCalidadAire.setValue(EstadoPeticion.ERROR);
                    }

                }
            });

        }

    }

    //-------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------
    /**
     * (Solo replica la peticion de la logica, mas comentarios en ese metodo)
     * Metodo que hace la peticion de obtener la calidad de aire de un rango temporal de un usuario
     * @param idUsuario el usuario del que se le quiere obtener las mediciones
     * @param fechaIni la fecha inicio
     * @param fechaFin la fecha final
     *
     * el return lo hace por el mutable de calidad aire exterior y el texto de gas expuesto
     */
    private void obtenerCalidadAireDeUnUsuarioHoy(int idUsuario, String fechaIni, String fechaFin) {

        // hacer peticion
        Logica l = new Logica();
        l.obtenerCalidadAirePorTiempoYUsuario(fechaIni, fechaFin, idUsuario, new PeticionarioREST.RespuestaREST() {
            @Override
            public void callback(int codigo, String cuerpo) {
                if(codigo == 200){
                    try {
                        JSONArray informesJSON = new JSONArray(cuerpo);
                        InformeCalidad[] informesCalidad = new InformeCalidad[4];
                        for(int i=0;i<informesJSON.length();i++){
                            InformeCalidad m = new InformeCalidad((JSONObject) informesJSON.get(i));
                            informesCalidad[i] = m;
                        }

                        informesCalidadExterior.setValue(informesCalidad);

                    } catch (JSONException e) {
                        estadoPeticionCalidadAire.setValue(EstadoPeticion.ERROR);
                        e.printStackTrace();
                    }
                }else{
                    estadoPeticionCalidadAire.setValue(EstadoPeticion.ERROR);
                }

            }
        });



    }

    //-------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------
    public MutableLiveData<InformeCalidad[]> getInformesCalidadCasa() {
        return informesCalidadCasa;
    }
    public MutableLiveData<InformeCalidad[]> getInformesCalidadTrabajo() {
        return informesCalidadTrabajo;
    }
    public MutableLiveData<InformeCalidad[]> getInformesCalidadExterior() {
        return informesCalidadExterior;
    }
    public MutableLiveData<String> getTextoExposicionDeGas() {
        return textoExposicionDeGas;
    }
    public MutableLiveData<EstadoPeticion> getEstadoPeticionCalidadAire() {
        return estadoPeticionCalidadAire;
    }
}