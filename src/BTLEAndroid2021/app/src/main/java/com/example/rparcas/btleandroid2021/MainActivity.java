package com.example.rparcas.btleandroid2021;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.rparcas.btleandroid2021.BroadCastReceiver.ConexionChangeReceiver;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.rparcas.btleandroid2021.databinding.ActivityMainBinding;

/**
 * MainActivity.java
 * Ruben Pardo Casanova 01/11/2021
 * Clase principal de la aplicacion, maneja los fragments del menu de navegacion inferior
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;

    private BroadcastReceiver conexionBroadcast;

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_escaner, R.id.navigation_mapa, R.id.navigation_estadisticas,
                R.id.navigation_perfil)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);



    }

    @Override
    protected void onResume() {
        super.onResume();
        inicializarBroadcastCambioConexion();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterNetworkChanges();
    }

    @Override
    public void onBackPressed() {
        // TODO esta es la solucion rapida para que no vuelva a login cuando sales de esta
        // si no hay mas activities en la pila y pulsas atras la app muere y inicia de cero
        // aun no hay metodo para comprobar si ya estas logeado para entrar directo al main
        if(navController.getPreviousBackStackEntry() != null){
            super.onBackPressed();
        }
    }


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


}