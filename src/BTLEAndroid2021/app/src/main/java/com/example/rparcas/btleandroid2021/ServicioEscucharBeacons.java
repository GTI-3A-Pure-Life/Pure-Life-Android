package com.example.rparcas.btleandroid2021;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.example.rparcas.btleandroid2021.logica.Logica;
import com.example.rparcas.btleandroid2021.logica.ManejadorNotificaciones;
import com.example.rparcas.btleandroid2021.logica.PeticionarioREST;
import com.example.rparcas.btleandroid2021.logica.SharedPreferencesHelper;
import com.example.rparcas.btleandroid2021.modelo.Medicion;
import com.example.rparcas.btleandroid2021.modelo.Posicion;
import com.example.rparcas.btleandroid2021.modelo.RegistroAveriaSensor;
import com.example.rparcas.btleandroid2021.modelo.RegistroBateriaSensor;
import com.example.rparcas.btleandroid2021.modelo.RegistroDescalibrado;
import com.example.rparcas.btleandroid2021.modelo.TramaIBeacon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;


// -------------------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------------------
/**
 * Servicio Que escucha IBeacons periodicamente
 * @author Rubén Pardo Casanova 21/09/2021
 */
public class ServicioEscucharBeacons extends IntentService implements LocationListener {

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    private static final String ETIQUETA_LOG = ">>>>";
    private static final int DISTANCIA_CERCA_ESTACION = 20;//metros

    private long tiempoDeEspera = 50; // decimas de segundo
    private final int tiempoDeEsperaINT = 50; // decimas de segundo
    private final int tiempoDeEsperaAveria = 3000; // 3000 decimas de segundo = 5 mins

    private final int topeMesurasParaEnviar =  1;// numero de mediciones que ira en una peticion

    private boolean seguir = true;

    private static boolean hayConexion = true;

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    private BluetoothLeScanner elEscanner;

    private ScanCallback callbackDelEscaneo = null;
    private String dispositivoABuscar;

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------

    private static List<Medicion> medicionesAEnviar;

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    private Messenger messageHandler; // comunicador con la activity

    // esta variable sirve para que no notifique cada vez que llegue una medicion nociva
    // sino que cuando entres en una zona nociva te avise y hasta que no salgas y vuelvas a entrar
    // te avise
    private boolean estoyEnZonaPeligrosa;

    //Cada manejador tiene un canal propio para las notificaciones
    ManejadorNotificaciones manejadorNotifNivelPeligro;
    ManejadorNotificaciones manejadorNotifEstadoNodo;

    // notificar al usuario y al servidor sobre la averia una vez por conexion
    private boolean seHaEnviadoNotificacionAveria = false;
    private boolean seHaEnviadoNotificacionNoAveria = false;

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    // variables para controlar el nivel de peligro mas altos
    private final Medicion[] medicionesPorTipoUltimasRegistradas = new Medicion[]{
            new Medicion(Medicion.TipoMedicion.CO),
            new Medicion(Medicion.TipoMedicion.NO2),
            new Medicion(Medicion.TipoMedicion.SO2),
            new Medicion(Medicion.TipoMedicion.O3)
    }; // posicion 0 = CO2, 1 = NO2, 2 = SO2, 3 = O3

    private Medicion medicionMasAltaRegistrada;
    private int idUsuario;


    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    // Atributos para controlar la localizacion
    private LocationManager manejador;
    private Posicion ultimaLocalizacion;
    private Posicion posicionEstacionMasCercana;
    private float valorPosicionEstacionMasCercana;
    private String proveedor;
    private static final long TIEMPO_MIN_REFRESCO_LOCALIZACION = 1 * 1000 ; // 5 segundos
    private static final long TIEMPO_REFRESCO_ESTACION_CERCANA = 10000 ; // milisegundos
    private static final long DISTANCIA_MIN_REFRESCO_LOCALIZACION = 5; // 5 metros




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

    /**
     * Medcion -> obtenerMedicionMasPeligrosa() -> Medicion
     * Obtener la medicion mas peligrosa de las ultimas registradas
     * @return Medicion mas alta registrada actualizada
     */
    private Medicion calcularMedicionMasPeligrosa(Medicion mNueva){

        // primero registramos le mNuva en su posicion
        switch (mNueva.getTipoMedicion()){
            case CO:
                medicionesPorTipoUltimasRegistradas[0] = mNueva;
                break;
            case NO2:
                medicionesPorTipoUltimasRegistradas[1] = mNueva;
                break;
            case SO2:
                medicionesPorTipoUltimasRegistradas[2] = mNueva;
                break;
            case O3:
                medicionesPorTipoUltimasRegistradas[3] = mNueva;
                break;

        }


        // al principio no hay ninguna mas alta
        if(medicionMasAltaRegistrada == null){
            medicionMasAltaRegistrada = mNueva;
        }else{

            // si llega una medicion del mismo tipo
            if(mNueva.getTipoMedicion() == medicionMasAltaRegistrada.getTipoMedicion()){
                // si es mas alta
                if(mNueva.getValor() > medicionMasAltaRegistrada.getValor()){
                    medicionMasAltaRegistrada = mNueva;
                }else{
                    // si no es mayor y es del mismo tipo y la medicion ha bajado, hay que obtener
                    // la siguiente con nivel mas alto
                    medicionMasAltaRegistrada = mNueva;// seteamos porque sino nos da la misma siempre
                    for (int i = 0; i< medicionesPorTipoUltimasRegistradas.length; i++) {
                        // compare to devuelve -1 menor que, 0 igual, 1 mayor que
                        if(medicionesPorTipoUltimasRegistradas[i].getValorAQI() > (medicionMasAltaRegistrada.getValorAQI())){
                            medicionMasAltaRegistrada = medicionesPorTipoUltimasRegistradas[i];
                        }

                    }
                }
            }else{
                // si es otro tipo de medicion
                // si el nivel de peligro es mas alto que el actual
                // compare to devuelve -1 menor que, 0 igual, 1 mayor que
                if(mNueva.getValorAQI() > medicionMasAltaRegistrada.getValorAQI()){
                    medicionMasAltaRegistrada = mNueva;
                }
            }
        }

        Log.d("MEDICION", "MAS ALTA: "+medicionMasAltaRegistrada.getTipoMedicion()+": "+
                medicionMasAltaRegistrada.getValor()+" - "+medicionMasAltaRegistrada.getValorAQI());

        return  medicionMasAltaRegistrada;
    }


    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------

    /**
     * parar()
     * Metodo para detener el servicio y su broadcast receiver
     */
    @SuppressLint("MissingPermission")
    public void parar () {

        Log.d(ETIQUETA_LOG, " ServicioEscucharBeacons.parar() " );


        if ( this.seguir == false ) {
            return;
        }

        if(this.elEscanner !=null){
            this.elEscanner.stopScan(this.callbackDelEscaneo);
            this.callbackDelEscaneo = null;
        }

        // quitar las actualizaciones de la posiciones
        if(manejador!=null){
            manejador.removeUpdates(this);
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
        long contadorESTACION = 1;
        int tiempoRestanteParaAveria =  tiempoDeEsperaAveria; // reinciamos

        Log.d(ETIQUETA_LOG, " ServicioEscucharBeacons.onHandleIntent: empieza : thread=" + Thread.currentThread().getId() );

        try {
            // Codigo que se ejecutara cuando el servicio este activo

            while ( this.seguir ) {
                Thread.sleep(tiempoDeEspera);
                // comprobar averia del sensor
                tiempoRestanteParaAveria -= tiempoDeEsperaINT;
                comprobarAveriaSensor(tiempoRestanteParaAveria);

                // cada X tiempo refrescar la mas cercana
                Log.d("ESTACION", "onHandleIntent: tiempo refresco estacion: "+(TIEMPO_REFRESCO_ESTACION_CERCANA-tiempoDeEspera*contadorESTACION));
                Log.d("ESTACION", "onHandleIntent: tiempo refresco contador: "+contadorESTACION);
                if(TIEMPO_REFRESCO_ESTACION_CERCANA-(tiempoDeEspera*contadorESTACION) <= 0){
                    Log.d("ESTACION", "onHandleIntent: refrescar estacion");
                    contadorESTACION = 0;
                    Log.d("ESTACION", "onHandleIntent: ultimaLocalizacion: "+ultimaLocalizacion);
                    if(ultimaLocalizacion!=null){
                        obtenerValorEstacionMasCercana(ultimaLocalizacion);
                    }
                }

                Log.d(ETIQUETA_LOG, "ServicioEscucharBeacons.onHandleIntent: hay "+medicionesAEnviar.size() + " mediciones");

                // control para enviar mediciones por lotes
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
                contadorESTACION++;
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
     * N -> comrpobarAveriaSensor()
     * si el tiempo restante es 0 envia notificacion y guarda el registro en la bd
     * @param tiempoRestanteParaAveria tiempo restante
     */
    private void comprobarAveriaSensor(int tiempoRestanteParaAveria) {

        // comprobar averia
        Log.d("AVERIA", " comprobar averia, falta: "+ tiempoRestanteParaAveria);
        // si hay medicioens y no se ha notificado, notificar que el sensor esta OK
        if(!medicionesAEnviar.isEmpty() && !seHaEnviadoNotificacionNoAveria){

            Log.d("AVERIA", " no hay averia ");
            tiempoRestanteParaAveria = tiempoDeEsperaAveria;
            seHaEnviadoNotificacionNoAveria = true;
            notificarAveriado(dispositivoABuscar,false);

        }else if(tiempoRestanteParaAveria <= 0 && !seHaEnviadoNotificacionAveria){
            // si no hay medicioens, no se ha notificado y han pasado el tiempo para la averia
            // notificar que el sensor esta averiado

            Log.d("AVERIA", "hay averia ");
            // hay una averia
            seHaEnviadoNotificacionAveria = true;
            notificarAveriado(dispositivoABuscar,true);

            // esta averiado, paramos el servicio
            enviarMensajeALaHostActivity(new RegistroAveriaSensor(dispositivoABuscar,true));
        }

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
        this.dispositivoABuscar = intent.getStringExtra(MainActivity.NOMBRE_DISPOSITIVO_A_ESCUCHAR_INTENT);
        this.idUsuario = intent.getIntExtra(MainActivity.ID_USUARIO_INTENT,-1);
        inicializarBlueTooth();
        initLocation();
        if(ultimaLocalizacion!=null){
            obtenerValorEstacionMasCercana(ultimaLocalizacion);
        }
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

        byte[] bytes = resultado.getScanRecord().getBytes();

        return new TramaIBeacon(bytes);
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

                TramaIBeacon tib = scanResultToTramaIBeacon( resultado );
                tratarTramaBeacon(tib,dispositivoBuscado,resultado.getRssi());


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
     * Metodo que trata la trama beacon para su posterior envio
     * @author Ruben Pardo Casanova
     * @param tib trama beacon a tratar
     * @param dispositivoBuscado el dispositivo que buscamos
     * @param rssi
     */
    private void tratarTramaBeacon(TramaIBeacon tib, String dispositivoBuscado, int rssi) {
        int major = Utilidades.bytesToInt( tib.getMajor()); //  tipo 1000 = bateria, 1,2,3,4 gas
        int minor = Utilidades.bytesToInt( tib.getMinor()); // valor
        String dispositivo = Utilidades.bytesToString(tib.getUUID()).split("%")[0];


        if(dispositivo.equals(dispositivoBuscado) && ultimaLocalizacion!=null){
            comprobarDesconexionPorDistancia(Utilidades.calcularDistanciaDispositivoBluetooth(rssi,tib.getTxPower()));
            Log.d(ETIQUETA_LOG, "tratarTramaBeacon: nombre: " + dispositivo);
            // si el major es 1000 significa que el minor es el valor de la bateria
            if(major == 1000){
                Log.d(ETIQUETA_LOG, "tratarTramaBeacon: bateria: " + minor);
                // tipo bateria
                notificarBateria(dispositivo,minor);
            }else{
                // por otra parte el major indica el tipo de medicion y el minor su valor
                // medicion
                Medicion m = new Medicion(minor,this.idUsuario,dispositivo,ultimaLocalizacion,
                        Medicion.TipoMedicion.getTipoById(major));

                comprobarNivelPeligroGas(m); // lanzar notificacion

                enviarMensajeALaHostActivity(calcularMedicionMasPeligrosa(m)); // avisar a la activity host

                // si esta descalibrado poner que su valor sea la de la estacion
                comprobarDescalibrado(m,posicionEstacionMasCercana,valorPosicionEstacionMasCercana);


                medicionesAEnviar.add(m);

            }
        }
    }

    /**
     *
     * Comprueba si esta cerca de la medicion, si lo esta y si su valor se va un 20 % de la estacion
     * enviar notificacion de descalibrado
     *
     * medicion:Medicion, posicionEstacion:Posicion, valorEstacion:R -> comprobarCalibracion()->
     * <-
     *
     * @author Ruben Pardo Casanova
     * 09/12/2021
     *
     * @param m Medicion actual
     * @param posicionEstacionMasCercana la posicion de la estacion de gas mas cercana
     * @param valorPosicionEstacionMasCercana valor de la estacion en AQI
     *
     *
     */
    private void comprobarDescalibrado(Medicion m, Posicion posicionEstacionMasCercana, float valorPosicionEstacionMasCercana) {

        double distancia = m.getPosicion().calcularDistanciaA(posicionEstacionMasCercana);
        Log.d("ESTACION", "valor (AQI) medicion: "+ m.getValorAQI());
        Log.d("ESTACION", "comprobarDescalibrado distancia(m): "+ distancia);
        Log.d("ESTACION", "descalibrado: "+ m.getValorAQI()/valorPosicionEstacionMasCercana);

        if(distancia>DISTANCIA_CERCA_ESTACION){
            double factorDescalibracion = m.getValorAQI()/valorPosicionEstacionMasCercana;
            boolean estaDesaclibrado = factorDescalibracion <= 0.8 || factorDescalibracion >= 1.2;

                new Logica().guardarRegistroDescalibrado(new RegistroDescalibrado(m.getSensorID(),estaDesaclibrado,factorDescalibracion));
                Log.d("ESTACION", "esta descalibrado");


        }


    }

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------

    /**
     * Metodo para comprobar si supera la distancia máxima, si la supera manda un mensaje a la actividad
     * y desconecta el servicio
     * 12/11/2021
     * @param distancia en m que tenemos entre el sensor y el movil
     */
    private void comprobarDesconexionPorDistancia(double distancia){
        Log.d(ETIQUETA_LOG, "comprobarDesconexion: "+distancia);
        if (distancia > 3000) {
            //desconectar cuando está a mas de 3 metros
            enviarMensajeALaHostActivity("DistanciaMaxima");
        }
    }


    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------

    /**
     * Envia notificacion de bateria
     * @param valorBateria valor de la bateria
     * @param dispositivo el dispositivo
     */
    private void notificarBateria(String dispositivo, int valorBateria) {

        // siempre notificar al servidor
        Logica l = new Logica();
        l.guardarRegistroBateria(new RegistroBateriaSensor(dispositivo,valorBateria));


        // enviar notificacion si nivel por debajo de 20
        if(valorBateria<=RegistroBateriaSensor.NIVEL_BATERIA_BAJO){
            // creamos la notificacion
            String titulo = getString(R.string.notificacion_titulo_alerta_bateria_baja)
                    + " "+ valorBateria + "%";

            String contenido = getString(R.string.notificacion_contenido_bateria_baja);

            PendingIntent intencionPendiente = PendingIntent.getActivity(
                    this, 0, new Intent(this, MainActivity.class), 0);

            NotificationCompat.Builder notiCustom = manejadorNotifNivelPeligro.crearNotificacionPersonalizada(
                    titulo,contenido,
                    R.layout.notificacion_pequenya,
                    R.mipmap.ic_launcher,
                    intencionPendiente);

            // enviamos notificacion
            manejadorNotifEstadoNodo.lanzarNotificacion(100,notiCustom);
        }

    }



    /**
     * Envia notificacion de averia del dispositivo
     * @param isAveriado si esta averiado
     * @param dispositivo el dispositivo
     */
    private void notificarAveriado(String dispositivo, boolean isAveriado) {

        // siempre notificar al servidor
        Logica l = new Logica();
        l.guardarRegistroAveria(new RegistroAveriaSensor(dispositivo,isAveriado));

        // enviar notificacion si nivel por debajo de 20
        if(isAveriado){
            // creamos la notificacion
            String titulo = getString(R.string.notificacion_titulo_alerta_averiado);

            String contenido = getString(R.string.notificacion_contenido_averiado);

            PendingIntent intencionPendiente = PendingIntent.getActivity(
                    this, 0, new Intent(this, MainActivity.class), 0);

            NotificationCompat.Builder notiCustom = manejadorNotifNivelPeligro.crearNotificacionPersonalizada(
                    titulo,contenido,
                    R.layout.notificacion_pequenya,
                    R.mipmap.ic_launcher,
                    intencionPendiente);

            // enviamos notificacion
            manejadorNotifEstadoNodo.lanzarNotificacion(100,notiCustom);
        }

    }

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
        Medicion.NivelPeligro nivelPeligro = Utilidades.obtenerNivelPeligroAQI(medicion.getValorAQI());
        switch (nivelPeligro){
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
            String titulo = nivelPeligro == Medicion.NivelPeligro.MODERADO
                    ? getString(R.string.notificacion_titulo_alerta_calidad_moderado)
                    : getString(R.string.notificacion_titulo_alerta_calidad_alto);
            String contenido = getString(R.string.notificacion_contenido_alerta_calidad);

            PendingIntent intencionPendiente = PendingIntent.getActivity(
                    this, 0, new Intent(this, MainActivity.class), 0);

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


    /**
     * Realiza una peticion REST a API externa
     * Devuelve el valor AQI de la estacion mas cercana
     * @author Ruben Pardo Casanova
     * 09/12/2021
     *
     * Posicion -> obtenerValorEstacionMasCercana() ->
     * <- Posicion, R
     *
     * @param posiconActual objeto Posicion con coordenadas actuales
     *
     */
    private void obtenerValorEstacionMasCercana(Posicion posiconActual){
        Log.d("ESTACION", "obtenerValorEstacionMasCercana: posicion: "+posiconActual);
        Logica l = new Logica();
        l.obtenerValorEstacionMasCercana(posiconActual, new PeticionarioREST.RespuestaREST() {
            @Override
            public void callback(int codigo, String cuerpo) {
                if(codigo == 200){
                    try {
                        Log.d("ESTACION","----------------cuerpo: "+cuerpo);
                        JSONObject json = new JSONObject(cuerpo);
                        JSONObject data = json.getJSONObject("data");
                        JSONArray posEstacion = data.getJSONObject("city").getJSONArray("geo");

                        // valor AQI
                        valorPosicionEstacionMasCercana = data.getInt("aqi");
                        // posicion de estacion mas cercana
                        posicionEstacionMasCercana = new Posicion(posEstacion.getDouble(0),
                                posEstacion.getDouble(1));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    // LOCALIZACION
    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    @SuppressLint("MissingPermission") // en teoria para llegar aqui se pide el permiso
    private void initLocation() {

        manejador = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);

        Criteria criterio = new Criteria();
        criterio.setCostAllowed(false);
        criterio.setAltitudeRequired(false);
        criterio.setAccuracy(Criteria.ACCURACY_FINE);
        proveedor = manejador.getBestProvider(criterio, true);

        if(proveedor!=null){
            Location location = manejador.getLastKnownLocation(proveedor);
            if(location!=null){
                ultimaLocalizacion = new Posicion(location.getLatitude(),location.getLongitude());
            }
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                manejador.requestLocationUpdates(proveedor, TIEMPO_MIN_REFRESCO_LOCALIZACION, DISTANCIA_MIN_REFRESCO_LOCALIZACION,
                        this);
            }
        }

    }

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    @Override
    public void onLocationChanged(@NonNull Location location) {
        if(location!=null){
            ultimaLocalizacion = new Posicion(location.getLatitude(),location.getLongitude());
        }
    }

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    @Override
    public void onProviderEnabled(@NonNull String provider) {
        initLocation();
    }



} // class
// -------------------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------------------
