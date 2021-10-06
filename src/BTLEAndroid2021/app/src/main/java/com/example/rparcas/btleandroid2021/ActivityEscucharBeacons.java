package com.example.rparcas.btleandroid2021;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.rparcas.btleandroid2021.BroadCastReceiver.ConexionChangeReceiver;

/**
 * Clase que arranca el servicio y escucha a un dispositivo en concreto
 * 06/10/2021
 * @author Ruben Pardo Casanova
 */
public class ActivityEscucharBeacons extends AppCompatActivity {

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    private static final String ETIQUETA_LOG = ">>>>";

    private String nombreBeaconAEscuchar = "";

    private Intent elIntentDelServicio = null;

    private BroadcastReceiver conexionBroadcast;

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------




    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    public void arrancarServicio( ) {
        Log.d(ETIQUETA_LOG, " boton arrancar servicio Pulsado" );

        if ( this.elIntentDelServicio != null ) {
            // ya estaba arrancado

            return;
        }

        Log.d(ETIQUETA_LOG, " MainActivity.constructor : voy a arrancar el servicio");

        this.elIntentDelServicio = new Intent(this, ServicioEscucharBeacons.class);
        this.elIntentDelServicio.putExtra(MainActivity.NOMBRE_DISPOSITIVO_A_ESCUCHAR_INTENT,this.nombreBeaconAEscuchar);
        this.elIntentDelServicio.putExtra("tiempoDeEspera", (long) 5000);
        startService( this.elIntentDelServicio );

    } // ()

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    public void botonDetenerServicioPulsado( View v ) {

        if ( this.elIntentDelServicio == null ) {
            // no estaba arrancado
            return;
        }

        stopService( this.elIntentDelServicio );

        this.elIntentDelServicio = null;

        Log.d(ETIQUETA_LOG, " boton detener servicio Pulsado" );
        super.onBackPressed();


    } // ()

    // --------------------------------------------------------------
    // --------------------------------------------------------------

    /**
     * Registrar el broadcast receiver ConexionChangeReciver
     */
    private void inicializarBroadcastCambioConexion() {

        conexionBroadcast = new ConexionChangeReceiver();

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        this.registerReceiver(conexionBroadcast, filter);
    }

    // --------------------------------------------------------------
    // --------------------------------------------------------------

    /**
     * Metodo que se llama desde el broadcast receiver ConexionChangeReceiver
     *
     */
    public static void onConexionChange(Context context){

        ServicioEscucharBeacons.onConexionChange(Utilidades.hayConexion(context), context);

    }

    // --------------------------------------------------------------
    // --------------------------------------------------------------

    /**
     * Metodo para des registrar el broadcast ConexionChangeReceiver
     */
    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(conexionBroadcast);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
    // --------------------------------------------------------------
    // --------------------------------------------------------------

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterNetworkChanges();
    }
    // --------------------------------------------------------------
    // --------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escuchar_beacons);

        nombreBeaconAEscuchar = getIntent().getExtras().getString(MainActivity.NOMBRE_DISPOSITIVO_A_ESCUCHAR_INTENT);

        TextView tvNombre = findViewById(R.id.tvNombreIBeacons);
        tvNombre.setText(nombreBeaconAEscuchar);

        inicializarBroadcastCambioConexion();
        arrancarServicio();

    }

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    @Override
    public void onBackPressed() {
        // no hacemos nada para que no pueda volver atras con el boton de volver
    }

}// class ()
// --------------------------------------------------------------
// --------------------------------------------------------------
