package com.example.rparcas.btleandroid2021.ui.mapa;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.rparcas.btleandroid2021.DialgoCarga;
import com.example.rparcas.btleandroid2021.R;
import com.example.rparcas.btleandroid2021.databinding.FragmentMapaBinding;
import com.example.rparcas.btleandroid2021.modelo.Medicion;

import java.util.ArrayList;

/**
 * MapaFragment.java
 * @author Ruben Pardo Casanova
 * 01/11/2021
 * Clase que controla la vista del fragmento Mapa
 */
public class MapaFragment extends Fragment {

    private MapaViewModel mapaViewModel;
    private FragmentMapaBinding binding;
    private DialgoCarga elDialogoDeCarga;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mapaViewModel =
                new ViewModelProvider(this).get(MapaViewModel.class);

        binding = FragmentMapaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        elDialogoDeCarga = new DialgoCarga(getActivity());
        mapaViewModel.obtenerMedicionesHoy();

        initCallbacks();
        initObservablesViewModel();


        return root;
    }


    //---------------------------------------------------------------------
    //---------------------------------------------------------------------
    private void initObservablesViewModel() {
        // controlar la ventana de carga y el texto de error
        mapaViewModel.getEstadoPeticionObtenerMediciones().observe(getViewLifecycleOwner(), estadoPeticion -> {

            switch (estadoPeticion){
                case SIN_ACCION:
                case EXITO:
                case ERROR:
                    esconderVentanaCarga();
                    break;
                case EN_PROCESO:
                    mostrarVentanaCarga();
                    break;
            }
        });

        // obtener las mediciones cuando se carguen de bd y de filtrar
        mapaViewModel.getMedicionesAMostrar().observe(getViewLifecycleOwner(), this::representarMedicionesEnMapa);
    }



    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    private void mostrarVentanaCarga() {
        elDialogoDeCarga.empezarDialogoCarga(getString(R.string.iniciando_sesion));
    }

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    private void esconderVentanaCarga() {
        elDialogoDeCarga.esconderDialogCarga();
    }

    //---------------------------------------------------------------------
    //---------------------------------------------------------------------

    /**
     * Lista[Mediciones] -> representarMedicionesEnMapa()
     *
     * @author Ruben Pardo Casanova
     * 15/11/2021
     *
     * @param mediciones las mediciones
     */
    private void representarMedicionesEnMapa(ArrayList<Medicion> mediciones) {
        Log.d("SPINNER","Se pintan en el mapa: -----"+ mediciones);
    }

    //---------------------------------------------------------------------
    //---------------------------------------------------------------------
    private void initCallbacks() {
        binding.spinnerTipoGas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Log.d("SPINNER","item selected: "+parent.getItemAtPosition(position));
                mapaViewModel.filtrarMedicionPorTipo(Medicion.TipoMedicion.getTipoById(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}