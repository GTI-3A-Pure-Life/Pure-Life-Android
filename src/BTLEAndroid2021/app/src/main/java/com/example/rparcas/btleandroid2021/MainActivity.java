package com.example.rparcas.btleandroid2021;

import android.os.Bundle;
import android.util.Log;

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
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
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
}