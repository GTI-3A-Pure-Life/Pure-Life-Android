package com.example.rparcas.btleandroid2021.ui.escaner;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.rparcas.btleandroid2021.R;
import com.example.rparcas.btleandroid2021.databinding.FragmentEscanerBinding;
import com.example.rparcas.btleandroid2021.modelo.Medicion;


/**
 * EscanerFragment.java
 * @author Ruben Pardo Casanova
 * 01/11/2021
 * Clase que controla la vista del fragmento Escaner
 */
public class EscanerFragment extends Fragment {

    private EscanerViewModel escanerViewModel;
    private FragmentEscanerBinding binding; // este objeto hace referncia a un xml layout, no es una clase creada

    private final int codigo_activity_result_escaner = 1234;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        escanerViewModel =
                new ViewModelProvider(this).get(EscanerViewModel.class);

        binding = FragmentEscanerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initCallbacks();
        initObserversViewModel();

        return root;
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    private void initObserversViewModel() {

        escanerViewModel.getNivelPeligro().observe(getViewLifecycleOwner(), new Observer<Medicion.NivelPeligro>() {
            @Override
            public void onChanged(@Nullable Medicion.NivelPeligro nivelPeligro) {

                switch (nivelPeligro){
                    case LEVE:
                        binding.fondoEscaner.setBackgroundColor(Color.GREEN);
                        break;
                    case MODERADO:
                        binding.fondoEscaner.setBackgroundColor(Color.YELLOW);
                        break;
                    case ALTO:
                        binding.fondoEscaner.setBackgroundColor(Color.RED);
                        break;
                }

            }
        });
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    private void initCallbacks() {

        // callback de boton escanear y detener
        binding.botonEscanear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getTag().equals("escanear")){
                    // TODO lanzar escaner
                    v.setTag("desconectar");
                    binding.botonEscanear.setText(getString(R.string.desconectar));

                }else if(v.getTag().equals("desconectar")){

                    // TODO parar servicio
                    v.setTag("escanear");
                    binding.botonEscanear.setText(getString(R.string.escanear));
                }


            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}