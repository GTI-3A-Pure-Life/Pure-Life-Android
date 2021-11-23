package com.example.rparcas.btleandroid2021.ui.estadisticas;

import android.content.Context;
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

import com.example.rparcas.btleandroid2021.Utilidades;
import com.example.rparcas.btleandroid2021.customViews.CalidadAireZona;
import com.example.rparcas.btleandroid2021.databinding.FragmentEstadisticasBinding;
import com.example.rparcas.btleandroid2021.modelo.InformeCalidad;
import com.example.rparcas.btleandroid2021.modelo.Medicion;

/**
 * EstadisticasFragment.java
 * @author Ruben Pardo Casanova
 * 01/11/2021
 * Clase que controla la vista del fragmento Estadisticas
 */
public class EstadisticasFragment extends Fragment {

    private EstadisticasViewModel estadisticasViewModel;
    private FragmentEstadisticasBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        estadisticasViewModel =
                new ViewModelProvider(this).get(EstadisticasViewModel.class);

        binding = FragmentEstadisticasBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        binding.calidadZonaCasa.setInformesCalidad(new InformeCalidad[]{
                new InformeCalidad(10, Medicion.TipoMedicion.CO),
                new InformeCalidad(50,Medicion.TipoMedicion.SO2),
                new InformeCalidad(30,Medicion.TipoMedicion.NO2),
                new InformeCalidad(10,Medicion.TipoMedicion.O3),
        });

        binding.calidadZonaTrabajo.setInformesCalidad(new InformeCalidad[]{
                new InformeCalidad(100, Medicion.TipoMedicion.CO),
                new InformeCalidad(50,Medicion.TipoMedicion.SO2),
                new InformeCalidad(30,Medicion.TipoMedicion.NO2),
                new InformeCalidad(100,Medicion.TipoMedicion.O3),
        });

        binding.calidadAireExterior.setInformesCalidad(new InformeCalidad[]{
                new InformeCalidad(30, Medicion.TipoMedicion.CO),
                new InformeCalidad(50,Medicion.TipoMedicion.SO2),
                new InformeCalidad(30,Medicion.TipoMedicion.NO2),
                new InformeCalidad(300,Medicion.TipoMedicion.O3),
        });

        binding.calidadZonaTrabajo.setEstadoCalidadAire(CalidadAireZona.EstadoCalidadAire.CARGANDO);
        binding.calidadZonaCasa.setEstadoCalidadAire(CalidadAireZona.EstadoCalidadAire.VACIO);

        /*
        final TextView textView = binding.textNotifications;

        estadisticasViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        */
        Context c = this.getContext();

        binding.btInformacion2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilidades.showCustomDialog(c);

            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}