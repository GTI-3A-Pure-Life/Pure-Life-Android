package com.example.rparcas.btleandroid2021;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.rparcas.btleandroid2021.logica.Logica;
import com.example.rparcas.btleandroid2021.modelo.MedicionCO2;
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

    private final int topeMesurasParaEnviar = 5; // numero de mediciones que ira en una peticion

    private boolean seguir = true;

    private static boolean hayConexion = true;


    private BluetoothLeScanner elEscanner;

    private ScanCallback callbackDelEscaneo = null;
    private String dispositivoABuscar;

    private static List<MedicionCO2> medicionesAEnviar;

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------

    /**
     * Constructor de la clase ServicioEscucharBeacons
     * constructor()
     */
    public ServicioEscucharBeacons( ) {
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

        inicializarServicio(intent);

        // esto lo ejecuta un WORKER THREAD !

        long contador = 1;
        final boolean[] seEstaGuardandoMedicionREST = {false}; // hay que esperar a que se resuelva la peticion para saber si se guardaron bien


        Log.d(ETIQUETA_LOG, " ServicioEscucharBeacons.onHandleIntent: empieza : thread=" + Thread.currentThread().getId() );


        try {
            // Codigo que se ejecutara cuando el servicio este activo
            while ( this.seguir ) {
                Thread.sleep(tiempoDeEspera);
                Log.d("PRUEBA", "ServicioEscucharBeacons.onHandleIntent: hay "+medicionesAEnviar.size() + " mediciones");
                if(medicionesAEnviar.size() >= topeMesurasParaEnviar){

                    if(hayConexion){
                        Log.d("PRUEBA", "ServicioEscucharBeacons.onHandleIntent: las envio");
                        // si no se estan guardando mesura

                            guardarMedicionABD(medicionesAEnviar);
                            Log.d("PRUEBA", "ServicioEscucharBeacons.onHandleIntent: la Medicion se envia a BD");

                    }else{

                        Log.d("PRUEBA", "ServicioEscucharBeacons.onHandleIntent: las guardo en local");
                        guardarMedicionesALocal(medicionesAEnviar,this.getApplicationContext());
                        Log.d("PRUEBA", "ServicioEscucharBeacons.onHandleIntent: la Medicion se guarda en LOCAL");
                    }
                    medicionesAEnviar.clear();
                }

                Log.d(ETIQUETA_LOG, " ServicioEscucharBeacons.onHandleIntent: tras la espera:  " + contador );



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

    // --------------------------------------------------------------
    // --------------------------------------------------------------

    /**
     * Inicializar el bluetooth, obtener el tiempo de espera y el dispostivio a buscar
     * @param intent datos enviados por quien llamo al servicio
     */
    private void inicializarServicio(Intent intent) {
        this.tiempoDeEspera = intent.getLongExtra("tiempoDeEspera", /* default */ 50000);
        this.dispositivoABuscar = intent.getStringExtra(MainActivity.NOMBRE_DISPOSITIVO_A_ESCUCHAR_INTENT);
        inicializarBlueTooth();
        buscarEsteDispositivoBTLE(this.dispositivoABuscar);

        medicionesAEnviar = new ArrayList<>();

        this.seguir = true;
    }

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    private void inicializarBlueTooth() {
        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): obtenemos adaptador BT ");

        BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): habilitamos adaptador BT ");

        bta.enable();

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): habilitado =  " + bta.isEnabled() );

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): estado =  " + bta.getState() );

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): obtenemos escaner btle ");

        this.elEscanner = bta.getBluetoothLeScanner();

        if ( this.elEscanner == null ) {
            Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): Socorro: NO hemos obtenido escaner btle  !!!!");

        }

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): voy a perdir permisos (si no los tuviera) !!!!");


    } // ()
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    /**
     *
     * ScanResult -> scanResultToTramaIBeacon() -> TramaIBeacon
     *
     * @author Rubén Pardo Casanova
     * @param resultado información bluetooth escaneada a mostrar
     * @return TramaIBeacon del scanresult
     */
    private TramaIBeacon scanResultToTramaIBeacon(ScanResult resultado ) {

        BluetoothDevice bluetoothDevice = resultado.getDevice();
        byte[] bytes = resultado.getScanRecord().getBytes();
        int rssi = resultado.getRssi();

        /*Log.d(ETIQUETA_LOG, " ****************************************************");
        Log.d(ETIQUETA_LOG, " ****** DISPOSITIVO DETECTADO BTLE ****************** ");
        Log.d(ETIQUETA_LOG, " ****************************************************");
        Log.d(ETIQUETA_LOG, " nombre = " + bluetoothDevice.getName());
        Log.d(ETIQUETA_LOG, " toString = " + bluetoothDevice.toString());


        ParcelUuid[] puuids = bluetoothDevice.getUuids();
        if ( puuids.length >= 1 ) {
            //Log.d(ETIQUETA_LOG, " uuid = " + puuids[0].getUuid());
           // Log.d(ETIQUETA_LOG, " uuid = " + puuids[0].toString());
        }

        Log.d(ETIQUETA_LOG, " dirección = " + bluetoothDevice.getAddress());
        Log.d(ETIQUETA_LOG, " rssi = " + rssi );

        Log.d(ETIQUETA_LOG, " bytes = " + new String(bytes));
        Log.d(ETIQUETA_LOG, " bytes (" + bytes.length + ") = " + Utilidades.bytesToHexString(bytes));



        Log.d(ETIQUETA_LOG, " ----------------------------------------------------");
        Log.d(ETIQUETA_LOG, " prefijo  = " + Utilidades.bytesToHexString(tib.getPrefijo()));
        Log.d(ETIQUETA_LOG, "          advFlags = " + Utilidades.bytesToHexString(tib.getAdvFlags()));
        Log.d(ETIQUETA_LOG, "          advHeader = " + Utilidades.bytesToHexString(tib.getAdvHeader()));
        Log.d(ETIQUETA_LOG, "          companyID = " + Utilidades.bytesToHexString(tib.getCompanyID()));
        Log.d(ETIQUETA_LOG, "          iBeacon type = " + Integer.toHexString(tib.getiBeaconType()));
        Log.d(ETIQUETA_LOG, "          iBeacon length 0x = " + Integer.toHexString(tib.getiBeaconLength()) + " ( "
                + tib.getiBeaconLength() + " ) ");
        Log.d(ETIQUETA_LOG, " uuid  = " + Utilidades.bytesToHexString(tib.getUUID()));
        Log.d(ETIQUETA_LOG, " uuid  = " + Utilidades.bytesToString(tib.getUUID()));
        Log.d(ETIQUETA_LOG, " major  = " + Utilidades.bytesToHexString(tib.getMajor()) + "( "
                + Utilidades.bytesToInt(tib.getMajor()) + " ) ");
        Log.d(ETIQUETA_LOG, " minor  = " + Utilidades.bytesToHexString(tib.getMinor()) + "( "
                + Utilidades.bytesToInt(tib.getMinor()) + " ) ");
        Log.d(ETIQUETA_LOG, " txPower  = " + Integer.toHexString(tib.getTxPower()) + " ( " + tib.getTxPower() + " )");
        Log.d(ETIQUETA_LOG, " ****************************************************");*/

        TramaIBeacon tib = new TramaIBeacon(bytes);
        return tib;
    } // ()

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    /**
     * Metodo para buscar iBeacons con el nombre indicado para mostrarlo posteriormente
     * Texto -> buscarEsteDispositivoBTLE()
     *
     * @author Rubén Pardo Casanova
     * @param dispositivoBuscado nombre del iBeacon a encontrar
     */
    private void buscarEsteDispositivoBTLE(final String dispositivoBuscado ) {
        Log.d(ETIQUETA_LOG, " buscarEsteDispositivoBTLE(): empieza ");

        Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): instalamos scan callback ");


        // super.onScanResult(ScanSettings.SCAN_MODE_LOW_LATENCY, result); para ahorro de energía

        this.callbackDelEscaneo = new ScanCallback() {
            @Override
            public void onScanResult( int callbackType, ScanResult resultado ) {
                super.onScanResult(callbackType, resultado);
                Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): onScanResult() ");


                TramaIBeacon tib = scanResultToTramaIBeacon( resultado );
                MedicionCO2 m = new MedicionCO2(tib);

                if(m.getSensorID().equals(dispositivoBuscado)){

                    medicionesAEnviar.add(m);
                }


            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): onBatchScanResults() ");

            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): onScanFailed() ");

            }
        };
        /*List<ScanFilter> filters = new ArrayList<>();;
        ScanFilter sf = new ScanFilter.Builder().setDeviceName( dispositivoBuscado ).build();
        filters.add(sf);


        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .build();*/


        Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): empezamos a escanear buscando: " + dispositivoBuscado );
        //Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): empezamos a escanear buscando: " + dispositivoBuscado
        //      + " -> " + Utilidades.stringToUUID( dispositivoBuscado ) );

        this.elEscanner.startScan( /*filters,settings, */this.callbackDelEscaneo);
    } // ()

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

        final boolean[] seEstaGuardandoREST = {false};

        Log.d("PRUEBA", "onConexionChange: intento enviar");
        // obtenemos como max 50 mediciones de la bd local
        List<MedicionCO2> mediciones = l.obtenerPrimeras50MedicionesDeBDLocal(context);
        if(mediciones.size()>0){
            do{

                // enviar al servidor solo si hay conexion (puede que se pierda a mitad
                if(hayConexion){

                        Log.d("PRUEBA", "onConexionChange: envio "+mediciones.size() + "al servidor");
                        guardarMedicionABD(mediciones);
                        l.borrarPrimeras50MedicionesDeBDLocal(context);
                }

                mediciones = l.obtenerPrimeras50MedicionesDeBDLocal(context);

            }while (mediciones.size()!=0  && hayConexion);
        }
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
     * Lista<MedicionCO2> -> guardarMedicionALocal()
     *
     * @param m mediciones a guardar en local
     * @param context Contexto de la aplicaicon
     */
    private void guardarMedicionesALocal(List<MedicionCO2> m, Context context) {

        Logica l = new Logica();
        l.guardarMedicionesEnLocal(m,context);

    }
} // class
// -------------------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------------------
