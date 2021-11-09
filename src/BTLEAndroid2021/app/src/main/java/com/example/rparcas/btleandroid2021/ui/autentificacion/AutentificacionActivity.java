package com.example.rparcas.btleandroid2021.ui.autentificacion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.util.Log;

import com.example.rparcas.btleandroid2021.R;
import com.example.rparcas.btleandroid2021.databinding.ActivityAutentificacionBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AutentificacionActivity extends AppCompatActivity implements NavegacionAutentificacionListener {

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    private ActivityAutentificacionBinding binding;
    private NavController navController;


    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityAutentificacionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_login, R.id.navigation_register)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_auth_activity);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    /**
     * callback para ir a iniciar sesion
     */
    /* @Override
    public void irALogin() {
        navController.navigate(R.id.navigation_login);
    }*/
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    /**
     * callback para ir a crear una cuenta
     */
    @Override
    public void irARegistrar() {

        navController.navigate(R.id.navigation_register);
    }


}