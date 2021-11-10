package com.example.rparcas.btleandroid2021.ui.autentificacion.Login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rparcas.btleandroid2021.MainActivity;
import com.example.rparcas.btleandroid2021.PureLifeApplication;
import com.example.rparcas.btleandroid2021.databinding.FragmentLoginBinding;
import com.example.rparcas.btleandroid2021.logica.EstadoPeticion;
import com.example.rparcas.btleandroid2021.ui.autentificacion.NavegacionAutentificacionListener;

/**
 * LoginFragment.java
 * @author Ruben Pardo Casanova
 * 01/11/2021
 * Clase que controla la vista del fragmento Login
 */
public class LoginFragment extends Fragment {

    private LoginViewModel loginViewModel;
    private NavegacionAutentificacionListener autentificacionCallbacks;
    private FragmentLoginBinding binding; // este objeto hace referncia a un xml layout, no es una clase creada

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        loginViewModel =
                new ViewModelProvider(this).get(LoginViewModel.class);

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initCallbacks();
        initObservablesViewModel();

        return root;
    }

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    private void initObservablesViewModel() {

        loginViewModel.getEstadoPeticionIniciarSesion().observe(getViewLifecycleOwner(), new Observer<EstadoPeticion>() {
            @Override
            public void onChanged(@Nullable EstadoPeticion estado) {
                switch(estado){
                    case SIN_ACCION:
                        // no hacer nada
                        binding.progressBarLogin.setVisibility(View.INVISIBLE);
                        break;
                    case EN_PROCESO:
                        // mostrar el progress bar y bloquear el formulario
                        binding.progressBarLogin.setVisibility(View.VISIBLE);
                        binding.textViewError.setText("");
                        //binding.setCanceledOnTouchOutside(false)
                        // TODO bloquear los edit del login
                        break;
                    case EXITO:
                        // esconder el progress bar e ir a main con el usuario resultado de la peticion
                        binding.progressBarLogin.setVisibility(View.INVISIBLE);
                        Intent intentMainActivity = new Intent(getActivity(), MainActivity.class);

                        // guardamos el usuario de forma global
                        PureLifeApplication appState = ((PureLifeApplication)getActivity().getApplication());
                        appState.setUsuario(loginViewModel.getUsuario());

                        startActivity(intentMainActivity);
                        getActivity().finish();
                        break;
                    case ERROR:
                        // esconder el progress bar y mostrar error
                        binding.progressBarLogin.setVisibility(View.INVISIBLE);
                        binding.textViewError.setText(loginViewModel.getError());

                        break;
                }
            }
        });
    }

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    private void initCallbacks() {
        binding.botonNoTienesCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                autentificacionCallbacks.irARegistrar();
            }
        });

        binding.botonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO logica en el view model de iniciar sesion y poner el user
                loginViewModel.iniciarSesion(
                        binding.editTextCorreo.getText().toString(),
                        binding.editTextContrasenyaLogin.getText().toString()

                );
            }
        });
    }

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    @Override
    public void onDestroyView() {

        super.onDestroyView();
        binding = null;
    }

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------

    /**
     * Hay que vincular la acitividad con el fragment
     * @param activity la actividad que hace de host al fragment
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            autentificacionCallbacks = (NavegacionAutentificacionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement NavegacionAutentificacionListener");
        }
    }

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------

    @Override
    public void onDetach() {
        autentificacionCallbacks = null;
        super.onDetach();
    }
}