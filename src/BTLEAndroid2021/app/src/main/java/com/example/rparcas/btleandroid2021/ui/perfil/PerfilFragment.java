package com.example.rparcas.btleandroid2021.ui.perfil;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.rparcas.btleandroid2021.AjustesActivity;
import com.example.rparcas.btleandroid2021.PureLifeApplication;
import com.example.rparcas.btleandroid2021.databinding.FragmentPerfilBinding;
import com.example.rparcas.btleandroid2021.modelo.Usuario;
import com.example.rparcas.btleandroid2021.ui.autentificacion.AutentificacionActivity;

/**
 * PerfilFragment.java
 * @author Ruben Pardo Casanova
 * 01/11/2021
 * Clase que controla la vista del fragmento Perfil
 */
public class PerfilFragment extends Fragment {

    private PerfilViewModel perfilViewModel;
    private FragmentPerfilBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        perfilViewModel =
                new ViewModelProvider(this).get(PerfilViewModel.class);

        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initCallbacks();

        return root;

    }

    private void initCallbacks() {
        binding.botonAjustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AjustesActivity.class);
                startActivity(intent);
            }
        });

        binding.botonCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // borrar el usuario y volver al login
                PureLifeApplication appState = ((PureLifeApplication)getActivity().getApplication());
                appState.setUsuario(null);
                Intent intent = new Intent(getActivity(), AutentificacionActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}