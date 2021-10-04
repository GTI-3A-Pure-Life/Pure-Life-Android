package com.example.rparcas.btleandroid2021;

import android.app.IntentService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.util.Log;

import com.example.rparcas.btleandroid2021.BroadCastReceiver.ConexionChangeReceiver;
import com.example.rparcas.btleandroid2021.logica.Logica;
import com.example.rparcas.btleandroid2021.modelo.MedicionCO2;
import com.example.rparcas.btleandroid2021.modelo.Posicion;
import com.example.rparcas.btleandroid2021.modelo.TramaIBeacon;

import java.util.ArrayList;
import java.util.List;


// -------------------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------------------
/**
 * Servicio Que escucha IBeacons periodicamente
 * @author Rubén Pardo Casanova 21/09/2021
 */
public class ServicioEscucharBeacons extends IntentService {

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    private static final String ETIQUETA_LOG = ">>>>";

    private long tiempoDeEspera = 500;

    private boolean seguir = true;

    public static boolean hayConexion = true;

    private BroadcastReceiver conexionBroadcast;

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------

    /**
     * Constructor de la clase ServicioEscucharBeacons
     * constructor()
     */
    public ServicioEscucharBeacons(  ) {
        super("HelloIntentService");



        Log.d(ETIQUETA_LOG, " ServicioEscucharBeacons.constructor: termina");
    }



    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    /*
    @Override
    public int onStartCommand( Intent elIntent, int losFlags, int startId) {

        // creo que este método no es necesario usarlo. Lo ejecuta el thread principal !!!
        super.onStartCommand( elIntent, losFlags, startId );

        this.tiempoDeEspera = elIntent.getLongExtra("tiempoDeEspera", 50000);

        Log.d(ETIQUETA_LOG, " ServicioEscucharBeacons.onStartCommand : empieza: thread=" + Thread.currentThread().getId() );

        return Service.START_CONTINUATION_MASK | Service.START_STICKY;
    } // ()

     */

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------

    /**
     * parar()
     * Metodo para detener el servicio y su broadcast receiver
     */
    public void parar () {

        Log.d(ETIQUETA_LOG, " ServicioEscucharBeacons.parar() " );


        if ( this.seguir == false ) {
            return;
        }

        this.seguir = false;
        this.stopSelf();

        Log.d(ETIQUETA_LOG, " ServicioEscucharBeacons.parar() : acaba " );

    }

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    public void onDestroy() {

        Log.d(ETIQUETA_LOG, " ServicioEscucharBeacons.onDestroy() " );


        this.parar(); // posiblemente no haga falta, si stopService() ya se carga el servicio y su worker thread
    }

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     */
    @Override
    protected void onHandleIntent(Intent intent) {

        this.tiempoDeEspera = intent.getLongExtra("tiempoDeEspera", /* default */ 50000);
        this.seguir = true;

        // esto lo ejecuta un WORKER THREAD !

        long contador = 1;

        Log.d(ETIQUETA_LOG, " ServicioEscucharBeacons.onHandleIntent: empieza : thread=" + Thread.currentThread().getId() );

        try {
            // Codigo que se ejecutara cuando el servicio este activo
            while ( this.seguir ) {
                Thread.sleep(tiempoDeEspera);

                MedicionCO2 m = obtenerMedicionDeTramaBeacon(null);


                Log.d(ETIQUETA_LOG, " ServicioEscucharBeacons.onHandleIntent: tras la espera:  " + contador );

                if(hayConexion){
                    List<MedicionCO2> lm = new ArrayList<>();
                    lm.add(m);
                    guardarMedicionABD(lm);
                    Log.d("PRUEBA", "ServicioEscucharBeacons.onHandleIntent: la Medicion se envia a BD");
                }else{


                    guardarMedicionALocal(m,this.getApplicationContext());
                    Log.d("PRUEBA", "ServicioEscucharBeacons.onHandleIntent: la Medicion se guarda en LOCAL");
                }

                contador++;
            }

            Log.d(ETIQUETA_LOG, " ServicioEscucharBeacons.onHandleIntent : tarea terminada ( tras while(true) )" );


        } catch (InterruptedException e) {
            // Restore interrupt status.
            Log.d(ETIQUETA_LOG, " ServicioEscucharBeacons.onHandleItent: problema con el thread");

            Thread.currentThread().interrupt();
        }

        Log.d(ETIQUETA_LOG, " ServicioEscucharBeacons.onHandleItent: termina");

    }

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    /**
     * T/F -> onConexionChange()
     *
     * Metodo que recibira los cambios de conexion. Si ha vuelto la conexion intentara guardar las
     * mediciones en local
     * @param nuevoEstadoConexion T/F si hay conexion
     */
    public static void onConexionChange(boolean nuevoEstadoConexion, Context context){
        // si el que llega es true y el que hay es false es que ha vuelto la conexion

        if(nuevoEstadoConexion){
            hayConexion = true;
            publicarMedicionesDeLocal(context);

        }else{
            hayConexion = false;
        }
    } // ()

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    /**
     * publicarMedicionesDeLocal()
     *
     * guardara las mediciones de la bd local de 50 en 50 hasta que esten todas o se vaya la conexion
     *
     */
    private static void publicarMedicionesDeLocal(Context context) {
        Logica l = new Logica();


        // obtenemos como max 50 mediciones de la bd local
        List<MedicionCO2> mediciones = l.obtenerPrimeras50MedicionesDeBDLocal(context);
        if(mediciones.size()>0){
            do{

                // enviar al servidor solo si hay conexion (puede que se pierda a mitad
                if(hayConexion){
                    Log.d("PRUEBA", "onConexionChange: envio "+mediciones.size() + "al servidor");
                    guardarMedicionABD(mediciones);

                    Log.d("PRUEBA", "onConexionChange: borro como maximo 50 mediciones de local");
                    l.borrarPrimeras50MedicionesDeBDLocal(context);
                    mediciones = l.obtenerPrimeras50MedicionesDeBDLocal(context);
                }

            }while (mediciones.size()!=0 && hayConexion);
        }
    }

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    /**
     *  obtenerTramaBeacon() -> TramaIBeacon
     *
     *
     * @return objeto TramaIBeacon
     */
    private MedicionCO2 obtenerMedicionDeTramaBeacon(TramaIBeacon t) {
        MedicionCO2 m = new MedicionCO2(1,4,"GTI-3A-1",new Posicion(31.56,32.5323));
        return m;
    }

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    /**
     * Lista<MedicionCO2> -> guardarMedicionABD()
     * @param lm lista de mediciones a enviar por REST al servidor
     */
    private static void guardarMedicionABD(List<MedicionCO2> lm) {

        Logica l = new Logica();
        l.publicarMediciones(lm);

    }

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    /**
     * Medicion -> guardarMedicionALocal()
     *
     * @param m medicion a guardar en local
     * @param context Contexto de la aplicaicon
     */
    private void guardarMedicionALocal(MedicionCO2 m, Context context) {

        Logica l = new Logica();
        l.guardarMedicionEnLocal(m,context);

    }
} // class
// -------------------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------------------
