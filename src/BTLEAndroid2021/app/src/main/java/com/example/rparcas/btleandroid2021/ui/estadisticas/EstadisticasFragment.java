package com.example.rparcas.btleandroid2021.ui.estadisticas;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.rparcas.btleandroid2021.PureLifeApplication;
import com.example.rparcas.btleandroid2021.R;
import com.example.rparcas.btleandroid2021.Utilidades;
import com.example.rparcas.btleandroid2021.customViews.CalidadAireZona;
import com.example.rparcas.btleandroid2021.databinding.FragmentEstadisticasBinding;
import com.example.rparcas.btleandroid2021.modelo.Medicion;
import com.example.rparcas.btleandroid2021.modelo.Usuario;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * EstadisticasFragment.java
 * @author Ruben Pardo Casanova
 * 01/11/2021
 * Clase que controla la vista del fragmento Estadisticas
 */
public class EstadisticasFragment extends Fragment {

    private EstadisticasViewModel estadisticasViewModel;
    private FragmentEstadisticasBinding binding;


    private ArrayList<Entry> entryListGrafica;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        estadisticasViewModel =
                new ViewModelProvider(this).get(EstadisticasViewModel.class);

        binding = FragmentEstadisticasBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        setMaximo(350); // set maximo progress bars
        initObservables();
        initCallbacks();
        inicializarGrafica();

        // obtener el usuario
        PureLifeApplication appState = ((PureLifeApplication) getActivity().getApplication());
        Usuario u = appState.getUsuario();

        estadisticasViewModel.obtenerCalidadAireDeLasZonasDeUnUsuario(u.getPosCasa(),u.getPosTrabajo(),u.getId());
        estadisticasViewModel.obtenerMedicionesDeUnUsuarioHoy(u.getId());



        return root;
    }


    private void initCallbacks(){

        Context c = this.getContext();

        binding.btInformacion2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilidades.showCustomDialog(c);

            }
        });
        binding.cerrarGrafica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.grafico.setVisibility(View.INVISIBLE);
                binding.tablaGases.setVisibility(View.VISIBLE);
                binding.cerrarGrafica.setVisibility(View.GONE);

                // limpiar grafico
                if(entryListGrafica !=null){
                    entryListGrafica.clear();
                    binding.grafico.notifyDataSetChanged();
                }


            }
        });

        binding.tbCO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarGrafica(estadisticasViewModel.medicionesCOObtenidas,Utilidades.obtenerColorPorValorAQI(binding.pBarCO.getProgress(),getContext()));
            }
        });

        binding.tbNO2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarGrafica(estadisticasViewModel.medicionesNO2Obtenidas,Utilidades.obtenerColorPorValorAQI(binding.pBarNO2.getProgress(),getContext()));
            }
        });

        binding.tbO3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarGrafica(estadisticasViewModel.medicionesO3Obtenidas,Utilidades.obtenerColorPorValorAQI(binding.pBarO3.getProgress(),getContext()));
            }
        });

        binding.tbSO2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarGrafica(estadisticasViewModel.medicionesSO2Obtenidas,Utilidades.obtenerColorPorValorAQI(binding.pBarSO2.getProgress(),getContext()));
            }
        });
    }
    private void initObservables() {

        estadisticasViewModel.getEstadoPeticionCalidadAire().observe(getViewLifecycleOwner(),
                estadoPeticion -> {
                    switch (estadoPeticion){
                        case EN_PROCESO:
                            binding.calidadZonaCasa.setEstadoCalidadAire(CalidadAireZona.EstadoCalidadAire.CARGANDO);
                            binding.tvExposicionDeGasHoy.setTextColor(getResources().getColor(R.color.gris_666));
                            binding.calidadZonaExterior.setEstadoCalidadAire(CalidadAireZona.EstadoCalidadAire.CARGANDO);
                            binding.calidadZonaTrabajo.setEstadoCalidadAire(CalidadAireZona.EstadoCalidadAire.CARGANDO);
                            break;
                        case ERROR:
                            binding.tvExposicionDeGasHoy.setText(getResources().getString(R.string.error_calidad_aire));
                            binding.tvExposicionDeGasHoy.setTextColor(getResources().getColor(R.color.rojo_e23636));
                            binding.calidadZonaCasa.setEstadoCalidadAire(CalidadAireZona.EstadoCalidadAire.VACIO);
                            binding.calidadZonaExterior.setEstadoCalidadAire(CalidadAireZona.EstadoCalidadAire.VACIO);
                            binding.calidadZonaTrabajo.setEstadoCalidadAire(CalidadAireZona.EstadoCalidadAire.VACIO);
                            break;
                    }
                }
        );
        // informe casa --------------------------------------------------------
        estadisticasViewModel.getInformesCalidadCasa().observe(getViewLifecycleOwner(),
                informeCalidadCasa -> {
                    if(informeCalidadCasa==null){
                        binding.calidadZonaCasa.setEstadoCalidadAire(CalidadAireZona.EstadoCalidadAire.VACIO);
                    }else{
                        binding.calidadZonaCasa.setInformesCalidad(informeCalidadCasa);
                    }
                }
        );
        // informe trabajo --------------------------------------------------------
        estadisticasViewModel.getInformesCalidadTrabajo().observe(getViewLifecycleOwner(),
                informeCalidadTrabajo -> {
                    if(informeCalidadTrabajo==null){
                        binding.calidadZonaTrabajo.setEstadoCalidadAire(CalidadAireZona.EstadoCalidadAire.VACIO);
                    }else{
                        binding.calidadZonaTrabajo.setInformesCalidad(informeCalidadTrabajo);
                    }
                }
        );

        // informe exterior --------------------------------------------------------
        estadisticasViewModel.getInformesCalidadExterior().observe(getViewLifecycleOwner(),
                informeCalidadExterior -> {
                    if(informeCalidadExterior==null){
                        binding.calidadZonaExterior.setEstadoCalidadAire(CalidadAireZona.EstadoCalidadAire.VACIO);
                    }else{
                        binding.calidadZonaExterior.setInformesCalidad(informeCalidadExterior);
                        String texto = getResources().getString(R.string.la_calidad_de_aire_respirada_hoy)
                                +" "+binding.calidadZonaExterior.getResumenCalidadAire();
                        binding.tvExposicionDeGasHoy.setText(texto);

                        binding.tvCOAQI.setText(String.valueOf(Math.round(informeCalidadExterior[0].getValorAQI()))+" AQI");
                        binding.tvO3AQI.setText(String.valueOf(Math.round(informeCalidadExterior[3].getValorAQI()))+" AQI");
                        binding.tvNO2AQI.setText(String.valueOf(Math.round(informeCalidadExterior[1].getValorAQI()))+" AQI");
                        binding.tvSO2AQI.setText(String.valueOf(Math.round(informeCalidadExterior[2].getValorAQI()))+" AQI");
                        // modificar barras de progreso
                        setProgreso(Math.round(informeCalidadExterior[0].getValorAQI()),
                                Math.round(informeCalidadExterior[1].getValorAQI()),
                                Math.round(informeCalidadExterior[2].getValorAQI()),
                                Math.round(informeCalidadExterior[3].getValorAQI()));
                    }
                }
        );

        // texto exposicion de gases --------------------------------------------------------
        estadisticasViewModel.getTextoExposicionDeGas().observe(getViewLifecycleOwner(),
                textoExposicionDeGas -> {
                    binding.tvExposicionDeGasHoy.setText(textoExposicionDeGas);
                }
        );

    }

    private void inicializarGrafica(){
        //binding.grafico.getXAxis().setValueFormatter(new MyXAxisValueFormatter());
        binding.grafico.getAxisRight().setEnabled(false);
        binding.grafico.getXAxis().setDrawGridLines(false);
        binding.grafico.getXAxis().setEnabled(false);
        binding.grafico.getAxisLeft().setDrawGridLines(false);
        binding.grafico.getLegend().setEnabled(false);
        binding.grafico.setTouchEnabled(false);
        binding.grafico.setDragEnabled(false);
        binding.grafico.setScaleEnabled(false);
        binding.grafico.setPinchZoom(false);
        binding.grafico.setAutoScaleMinMaxEnabled(true);
        binding.grafico.getXAxis().setLabelCount(5);
    }

    /**
     * Metodo para mostrar y setear la gráfica
     * @author Lorena-Ioana Florescu
     * @version 19/11/2021
     * @param mediciones
     */
    private void mostrarGrafica(ArrayList<Medicion> mediciones, int color) {

        entryListGrafica = new ArrayList<>();
        // variable para nuestra gráfica

        binding.grafico.setVisibility(View.VISIBLE);
        binding.tablaGases.setVisibility(View.INVISIBLE);
        binding.cerrarGrafica.setVisibility(View.VISIBLE);


        if(!mediciones.isEmpty()){
          // ArrayList<Medicion> medicionesAMostrar = Utilidades.transformarMedicionesAArrayMedicionesPorMinuto24Horas(mediciones);
           //Log.d("GRAFICA", "mostrarGrafica: "+medicionesAMostrar);
            for(int i = 0; i< mediciones.size(); i ++){

                Entry e = new Entry(i, (float)mediciones.get(i).getValor());
                entryListGrafica.add(e);
            }

            LineDataSet lineDataSet = new LineDataSet(entryListGrafica,null);
            Description desc = new Description();
            desc.setText("Exposición (AQI) de "+mediciones.get(0).getTipoMedicion().getNombreGas());
            binding.grafico.setDescription(desc);




            lineDataSet.setColors(ColorTemplate.PASTEL_COLORS);
            lineDataSet.setColor(color);
            lineDataSet.setFillColor(color);
            //lineDataSet.setFillAlpha(300);
            lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            lineDataSet.setCubicIntensity(.09f);
            lineDataSet.setDrawFilled(true);
            lineDataSet.setLineWidth(2);
            lineDataSet.setDrawCircles(false);
            lineDataSet.setValueTextSize(0f);

            //lineDataSet.setM(10);

            //actualizamos grafica si se actualizan los datos
            LineData lineData = new LineData(lineDataSet);

            binding.grafico.setData(lineData);
            binding.grafico.notifyDataSetChanged();
        }


    }


    /**
     * Settear la cantidad diaria de cada gas
     * @author Lorena-Ioana Florescu
     * @version 19/11/2021
     * @param cantidadCO cantidad de gas diaria
     * @param cantidadNO2 cantidad de gas diaria
     * @param cantidadO3 cantidad de gas diaria
     * @param cantidadSO2 cantidad de gas diaria
     */
    private void setProgreso(int cantidadCO, int cantidadNO2, int cantidadSO2, int cantidadO3 ){

        int colorCO = Utilidades.obtenerColorPorValorAQI(cantidadCO,getContext());
        int colorNO2 = Utilidades.obtenerColorPorValorAQI(cantidadNO2,getContext());
        int colorSO2 = Utilidades.obtenerColorPorValorAQI(cantidadSO2,getContext());
        int colorO3 = Utilidades.obtenerColorPorValorAQI(cantidadO3,getContext());

        binding.pBarCO.getProgressDrawable().setColorFilter(colorCO,android.graphics.PorterDuff.Mode.MULTIPLY);
        binding.pBarNO2.getProgressDrawable().setColorFilter(colorNO2,android.graphics.PorterDuff.Mode.MULTIPLY);
        binding.pBarO3.getProgressDrawable().setColorFilter(colorO3,android.graphics.PorterDuff.Mode.MULTIPLY);
        binding.pBarSO2.getProgressDrawable().setColorFilter(colorSO2,android.graphics.PorterDuff.Mode.MULTIPLY);

        binding.pBarCO.setProgress(cantidadCO);
        binding.pBarNO2.setProgress(cantidadNO2);
        binding.pBarO3.setProgress(cantidadO3);
        binding.pBarSO2.setProgress(cantidadSO2);
    }

    /**
     * Settear el maximo valor de los gases
     * @author Lorena-Ioana Florescu
     * @version 19/11/2021
     * @param maximo el maximo de la barra de progreso
     */
    private void setMaximo(int maximo){
        binding.pBarCO.setMax(maximo);
        binding.pBarNO2.setMax(maximo);
        binding.pBarO3.setMax(maximo);
        binding.pBarSO2.setMax(maximo);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public class MyXAxisValueFormatter extends IndexAxisValueFormatter{



        @Override
        public String getFormattedValue(float value) {
            // Convert float value to date string
            // Convert from days back to milliseconds to format time  to show to the user

            // Show time in local version
            Date timeMilliseconds = new Date((long)value);
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat( "HH:mm");


            return dateTimeFormat.format(timeMilliseconds);
        }
    }
}