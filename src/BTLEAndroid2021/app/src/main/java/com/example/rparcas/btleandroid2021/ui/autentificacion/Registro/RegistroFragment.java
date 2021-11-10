package com.example.rparcas.btleandroid2021.ui.autentificacion.Registro;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rparcas.btleandroid2021.MainActivity;
import com.example.rparcas.btleandroid2021.PureLifeApplication;
import com.example.rparcas.btleandroid2021.R;
import com.example.rparcas.btleandroid2021.databinding.FragmentLoginBinding;
import com.example.rparcas.btleandroid2021.databinding.FragmentRegistroBinding;
import com.example.rparcas.btleandroid2021.logica.EstadoPeticion;
import com.example.rparcas.btleandroid2021.ui.autentificacion.Login.LoginViewModel;

/**
 * RegistroFragment.java
 * @author Ruben Pardo Casanova
 * 01/11/2021
 * Clase que controla la vista del fragmento Registro
 */
public class RegistroFragment extends Fragment {

    private RegistroViewModel registroViewModel;
    private FragmentRegistroBinding binding; // este objeto hace referncia a un xml layout, no es una clase creada


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        registroViewModel =
                new ViewModelProvider(this).get(RegistroViewModel.class);



        binding = FragmentRegistroBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initCallbacks();
        initObservablesViewModel();

        return root;
    }


    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    private void initObservablesViewModel() {

        registroViewModel.getEstadoPeticionIniciarSesion().observe(getViewLifecycleOwner(), new Observer<EstadoPeticion>() {
            @Override
            public void onChanged(@Nullable EstadoPeticion estado) {
                switch(estado){
                    case SIN_ACCION:
                        // no hacer nada
                        binding.progressBarRegistro.setVisibility(View.INVISIBLE);
                        break;
                    case EN_PROCESO:
                        // mostrar el progress bar y bloquear el formulario
                        binding.progressBarRegistro.setVisibility(View.VISIBLE);
                        // TODO bloquear los edit del registro
                        break;
                    case EXITO:
                        // esconder el progress bar e ir a main con el usuario resultado de la peticion
                        binding.progressBarRegistro.setVisibility(View.INVISIBLE);
                        Intent intentMainActivity = new Intent(getActivity(), MainActivity.class);

                        // guardamos el usuario de forma global
                        PureLifeApplication appState = ((PureLifeApplication)getActivity().getApplication());
                        appState.setUsuario(registroViewModel.getUsuario());

                        startActivity(intentMainActivity);
                        getActivity().finish();
                        break;
                    case ERROR:
                        // esconder el progress bar y mostrar error
                        binding.progressBarRegistro.setVisibility(View.INVISIBLE);
                        binding.textViewError.setText(registroViewModel.getError());
                        break;
                }
            }
        });
    }

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    private void initCallbacks() {


        binding.botonCrearCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO logica en el view model de iniciar sesion y poner el user
                registroViewModel.crearCuenta(
                        binding.editTextNombre.getText().toString(),
                        binding.editTextApellidos.getText().toString(),
                        binding.editTextCorreo.getText().toString(),
                        binding.editTextContrasenya.getText().toString(),
                        binding.editTextRepetirContrasenya.getText().toString(),
                        binding.editTextPhone.getText().toString()
                );
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}