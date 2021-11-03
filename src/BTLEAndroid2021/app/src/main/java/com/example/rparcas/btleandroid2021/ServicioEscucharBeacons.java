package com.example.rparcas.btleandroid2021;

import android.app.Activity;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.example.rparcas.btleandroid2021.logica.Logica;
import com.example.rparcas.btleandroid2021.logica.ManejadorNotificaciones;
import com.example.rparcas.btleandroid2021.logica.SharedPreferencesHelper;
import com.example.rparcas.btleandroid2021.modelo.InformeCalidadAire;
import com.example.rparcas.btleandroid2021.modelo.Medicion;
import com.example.rparcas.btleandroid2021.modelo.Posicion;
import com.example.rparcas.btleandroid2021.modelo.TramaIBeacon;
import com.example.rparcas.btleandroid2021.ui.autentificacion.NavegacionAutentificacionListener;
import com.example.rparcas.btleandroid2021.ui.escaner.EscanerFragment;

import java.util.ArrayList;
import java.util.List;

import androidx.core.app.NotificationCompat;


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

    private long tiempoDeEspera = 100;

    private final int topeMesurasParaEnviar = 5; // numero de mediciones que ira en una peticion

    private boolean seguir = true;

    private static boolean hayConexion = true;


    private BluetoothLeScanner elEscanner;

    private ScanCallback callbackDelEscaneo = null;
    private String dispositivoABuscar;

    private static List<Medicion> medicionesAEnviar;

    private Messenger messageHandler;

    // esta variable sirve para que no notifique cada vez que llegue una medicion nociva
    // sino que cuando entres en una zona nociva te avise y hasta que no salgas y vuelvas a entrar
    // te avise
    private boolean estoyEnZonaPeligrosa;

    //Cada manejador tiene un canal propio para las notificaciones
    ManejadorNotificaciones manejadorNotifNivelPeligro;
    ManejadorNotificaciones manejadorNotifEstadoNodo;

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------

    /**
     * Constructor de la clase ServicioEscucharBeacons
     * constructor()
     */
    public ServicioEscucharBeacons() {
        super("HelloIntentService");

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


        Log.d(ETIQUETA_LOG, " ServicioEscucharBeacons.onHandleIntent: empieza : thread=" + Thread.currentThread().getId() );


        try {
            // Codigo que se ejecutara cuando el servicio este activo
            double valor = 0;
            while ( this.seguir ) {
                Thread.sleep(tiempoDeEspera);
                Log.d(ETIQUETA_LOG, "ServicioEscucharBeacons.onHandleIntent: hay "+medicionesAEnviar.size() + " mediciones");

                valor += 20;
                valor = valor > 70 ? 0 : valor;
               
                Medicion m = new Medicion(valor,1,"1",new Posicion(1,1), Medicion.TipoMedicion.CO);
                comprobarNivelPeligroGas(m);


                enviarMensajeALaHostActivity(m.getNivelPeligro());

                /*if(medicionesAEnviar.size() >= topeMesurasParaEnviar){

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
                }*/

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
     * @author Ruben Pardo Casanova
     * Metodo para enviarle mensajes a la activity que arranco el servico
     * @param mensaje el mensaje a enviar
     */
    private void enviarMensajeALaHostActivity(Object mensaje) {

        Message message = Message.obtain();
        message.obj = mensaje;

        try {
            messageHandler.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    // --------------------------------------------------------------
    // --------------------------------------------------------------

    /**
     * Inicializar el bluetooth, obtener el tiempo de espera y el dispostivio a buscar
     * @param intent datos enviados por quien llamo al servicio
     */
    private void inicializarServicio(Intent intent) {

        SharedPreferencesHelper.initializeInstance(this);

        this.tiempoDeEspera = intent.getLongExtra("tiempoDeEspera", /* default */ 50000);
        this.dispositivoABuscar = intent.getStringExtra(MainActivityTEMP.NOMBRE_DISPOSITIVO_A_ESCUCHAR_INTENT);
        inicializarBlueTooth();
        buscarEsteDispositivoBTLE(this.dispositivoABuscar);

        medicionesAEnviar = new ArrayList<>();

        this.seguir = true;

        // creamos los dos canales de notificaciones
        manejadorNotifNivelPeligro = new ManejadorNotificaciones(
                "alertas-calidad-aire",
                "Alertas del nivel de peligro de la calidad de aire",
                "Recibirás las alertas cuando entres en una zona donde la calidad del aire no es buena para tu salud",
                NotificationManager.IMPORTANCE_HIGH,
                android.R.drawable.stat_notify_chat,
                this
        );

        manejadorNotifEstadoNodo = new ManejadorNotificaciones(
                "alertas-estado-nodo",
                "Alertas del estado del nodo",
                "Recibirás las alertas cuando el nodo este inactivo o tenga poca batería",
                NotificationManager.IMPORTANCE_HIGH,
                android.R.drawable.stat_notify_chat,
                this
        );

        // obtenemos el canal de mensajeria con la actividad que arranco el servicio
        Bundle extras = intent.getExtras();
        messageHandler = (Messenger) extras.get("MESSENGER");


        estoyEnZonaPeligrosa = false;
        Log.d(ETIQUETA_LOG, " ServicioEscucharBeacons.constructor: termina");
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
                Medicion mCO = new Medicion(tib);

                if(mCO.getSensorID().equals(dispositivoBuscado)){

                    comprobarNivelPeligroGas(mCO);

                    medicionesAEnviar.add(mCO);
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
     * Medicion -> comprobarNivelPeligroGas()
     *
     * Si el valor de la medicion esta por encima del nivel especificado por el usuario de alerta
     * (o el por defecto) lanzamos una notificacion de aviso
     *
     * @param medicion Medicion a comprobar
     */
    private void comprobarNivelPeligroGas(Medicion medicion) {

        // obtenemos el booleano de si tenemos que avisar en niveles moderados, si es false avisamos
        // a partir de alto, este valor se modifica en la pagina de ajustes
        SharedPreferencesHelper sharedPreferencesHelper = SharedPreferencesHelper.getInstance();
        boolean deboAvisarEnModerado = sharedPreferencesHelper.isNivelAlertaSensible();

        boolean hayQueNotificar = false; // si es true se lanza notificacion al final

        Log.d(ETIQUETA_LOG, "comprobarNivelPeligroGas: deboAvisar moderado: "+deboAvisarEnModerado);
        Log.d(ETIQUETA_LOG, "comprobarNivelPeligroGas: estoyZonaPeligro: "+ estoyEnZonaPeligrosa);

        switch (medicion.getNivelPeligro()){
            case LEVE:
                //estoy en zona segura
                // no avisar de nada poner a false el bool
                //yaEstoyEnZonaPeligrosaCO = !(medicion.getTipoMedicion() == TipoMedicion.CO);
                estoyEnZonaPeligrosa = false;
                Log.d(ETIQUETA_LOG, "comprobarNivelPeligroGas: LEVE");

                break;
            case MODERADO:
                if(deboAvisarEnModerado){
                    //estoy en zona peligrosa
                    // avisar si ya no se aviso antes
                    if(!estoyEnZonaPeligrosa){
                        Log.d(ETIQUETA_LOG, "comprobarNivelPeligroGas: MODERADO ENVIO NOTI");
                       // yaEstoyEnZonaPeligrosaCO = (medicion.getTipoMedicion() == TipoMedicion.CO);
                        estoyEnZonaPeligrosa = true;
                        hayQueNotificar = true;
                    }


                }
                break;
            case ALTO:
                //estoy en zona peligrosa
                // avisar si ya no se aviso antes
                if(!estoyEnZonaPeligrosa){
                    Log.d(ETIQUETA_LOG, "comprobarNivelPeligroGas: ALTO ENVIO NOTI");
                    // yaEstoyEnZonaPeligrosaCO = (medicion.getTipoMedicion() == TipoMedicion.CO);
                    estoyEnZonaPeligrosa = true;
                    hayQueNotificar = true;
                }
                break;
        }

        // lanzar notificacion
        if(hayQueNotificar){
            String titulo = medicion.getNivelPeligro() == Medicion.NivelPeligro.MODERADO
                    ? getString(R.string.notificacion_titulo_alerta_calidad_moderado)
                    : getString(R.string.notificacion_titulo_alerta_calidad_alto);
            String contenido = getString(R.string.notificacion_contenido_alerta_calidad);

            PendingIntent intencionPendiente = PendingIntent.getActivity(
                    this, 0, new Intent(this, MainActivityTEMP.class), 0);

            NotificationCompat.Builder notiCustom = manejadorNotifNivelPeligro.crearNotificacionPersonalizada(
                    titulo,contenido,
                    R.layout.notificacion_pequenya,
                    R.mipmap.ic_launcher,
                    intencionPendiente);



            manejadorNotifNivelPeligro.lanzarNotificacion(100,notiCustom);

        }



    }// ()


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
        List<Medicion> mediciones = l.obtenerPrimeras50MedicionesDeBDLocal(context);
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
    private static void guardarMedicionABD(List<Medicion> lm) {

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
    private void guardarMedicionesALocal(List<Medicion> m, Context context) {

        Logica l = new Logica();
        l.guardarMedicionesEnLocal(m,context);

    }


} // class
// -------------------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------------------
