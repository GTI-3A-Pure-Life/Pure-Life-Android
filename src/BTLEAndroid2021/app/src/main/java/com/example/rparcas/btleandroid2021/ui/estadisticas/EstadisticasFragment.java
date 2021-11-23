package com.example.rparcas.btleandroid2021.ui.estadisticas;

import android.content.Context;
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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * EstadisticasFragment.java
 * @author Ruben Pardo Casanova
 * 01/11/2021
 * Clase que controla la vista del fragmento Estadisticas
 */
public class EstadisticasFragment extends Fragment {

    private EstadisticasViewModel estadisticasViewModel;
    private FragmentEstadisticasBinding binding;

    // variable para nuestra gráfica
    private LineChart lineChart;
    private LineData lineData;


    private ArrayList<Medicion> medicionesAMostrar;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        estadisticasViewModel =
                new ViewModelProvider(this).get(EstadisticasViewModel.class);

        binding = FragmentEstadisticasBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        setMaximo(350); // set maximo progress bars
        initObservables();
        initCallbacks();
        // obtener el usuario


        PureLifeApplication appState = ((PureLifeApplication) getActivity().getApplication());
        Usuario u = appState.getUsuario();

        estadisticasViewModel.obtenerCalidadAireDeLasZonasDeUnUsuario(u.getPosCasa(),u.getPosTrabajo(),u.getId());
        estadisticasViewModel.obtenerMedicionesDeUnUsuarioHoy(u.getId());



        return root;
    }


    private void initCallbacks(){

        //conectamos grafica con su vista en el xml
        lineChart = binding.grafico;

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
                binding.grafico.setData(null);
            }
        });

        binding.tbCO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarGrafica(estadisticasViewModel.medicionesCOObtenidas);
            }
        });

        binding.tbNO2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarGrafica(estadisticasViewModel.medicionesNO2Obtenidas);
            }
        });

        binding.tbO3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarGrafica(estadisticasViewModel.medicionesO3Obtenidas);
            }
        });

        binding.tbSO2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarGrafica(estadisticasViewModel.medicionesSO2Obtenidas);
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


    /**
     * Metodo para mostrar y setear la gráfica
     * @author Lorena-Ioana Florescu
     * @version 19/11/2021
     * @param mediciones
     */
    private void mostrarGrafica(ArrayList<Medicion> mediciones) {

        List<Entry> entryList = new ArrayList<>();

        binding.grafico.setVisibility(View.VISIBLE);
        binding.tablaGases.setVisibility(View.INVISIBLE);
        binding.cerrarGrafica.setVisibility(View.VISIBLE);
        if(!mediciones.isEmpty()){
            for(int i = 0; i< mediciones.size(); i ++){

                entryList.add(new Entry(mediciones.get(i).getMedicion_fecha().getTime(), (float)mediciones.get(i).getValor()));
            }


            LineDataSet lineDataSet = new LineDataSet(entryList,null);
            Description desc = new Description();
            desc.setText("Gráfica de exposición diaria a " + mediciones.get(0).getTipoMedicion() + " ug/m3");
            lineChart.setDescription(desc);


            XAxis xAxis = lineChart.getXAxis();
            xAxis.setValueFormatter(new MyXAxisValueFormatter());
            xAxis.setLabelCount(5);
            YAxis yAxisLeft = lineChart.getAxisLeft();
            YAxis yAxisRight = lineChart.getAxisRight();

            lineDataSet.setColors(ColorTemplate.PASTEL_COLORS);
            lineDataSet.setFillAlpha(110);
            //actualizamos grafica si se actualizan los datos
            lineData = new LineData(lineDataSet);
            lineChart.setData(lineData);

            lineChart.invalidate();
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

            Log.d("ENTRA", "getFormattedValue: "+value);
            // Convert float value to date string
            // Convert from days back to milliseconds to format time  to show to the user
            long emissionsMilliSince1970Time = TimeUnit.DAYS.toMillis((long)value);
            // Show time in local version
            Date timeMilliseconds = new Date(emissionsMilliSince1970Time);
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat( "hh:mm a");

            return dateTimeFormat.format(timeMilliseconds);
        }
    }
}