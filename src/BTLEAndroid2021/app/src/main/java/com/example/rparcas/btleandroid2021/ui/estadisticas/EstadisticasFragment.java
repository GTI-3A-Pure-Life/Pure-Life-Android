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
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * EstadisticasFragment.java
 * @author Ruben Pardo Casanova
 * 01/11/2021
 * Clase que controla la vista del fragmento Estadisticas
 */
public class EstadisticasFragment extends Fragment {

    private EstadisticasViewModel estadisticasViewModel;
    private FragmentEstadisticasBinding binding;


    private ArrayList<BarEntry> entryListGrafica;


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
        binding.grafico.getXAxis().setValueFormatter(new TimeXAxisValueFormatter());
        binding.grafico.getAxisRight().setEnabled(false);
        binding.grafico.getXAxis().setDrawGridLines(false);
        //binding.grafico.getXAxis().setEnabled(false);
        binding.grafico.getAxisLeft().setDrawGridLines(false);
        binding.grafico.getLegend().setEnabled(false);
        binding.grafico.setTouchEnabled(true);
        binding.grafico.setDragEnabled(false);
        binding.grafico.setScaleEnabled(false);
        binding.grafico.setPinchZoom(false);
        // mostrar 8 labels de eje x y el true es para mostrar el ultimo si o si
        binding.grafico.getXAxis().setLabelCount(8,true);
        binding.grafico.getAxisLeft().setAxisMaximum(300);
        binding.grafico.getAxisLeft().setAxisMinimum(0);

        // añadimos las barras de limite

        float longitudLinea = 4f;
        float longitudEspacio = 4f;
        float lineafase = 0f;
        float anchoLinea = 1f;
        // nivel moderado
        LimitLine limiteModerado = new LimitLine(Medicion.VALOR_AQI.NIVEL_BUENO);
        limiteModerado.setLineColor(getResources().getColor(R.color.amarillo_ffc300));
        limiteModerado.enableDashedLine(longitudLinea,longitudEspacio,lineafase);
        limiteModerado.setLineWidth(anchoLinea);

        LimitLine limiteMalo = new LimitLine(Medicion.VALOR_AQI.NIVEL_MODERADO);
        limiteMalo.setLineColor(getResources().getColor(R.color.rojo_e23636));
        limiteMalo.enableDashedLine(longitudLinea,longitudEspacio,lineafase);
        limiteMalo.setLineWidth(anchoLinea);

        LimitLine limiteMuyMalo = new LimitLine(Medicion.VALOR_AQI.NIVEL_ALTO);
        limiteMuyMalo.setLineColor(getResources().getColor(R.color.rojo_900C3F));
        limiteMuyMalo.enableDashedLine(longitudLinea,longitudEspacio,lineafase);
        limiteMuyMalo.setLineWidth(anchoLinea);

        binding.grafico.getAxisLeft().addLimitLine(limiteMalo);
        binding.grafico.getAxisLeft().addLimitLine(limiteModerado);
        binding.grafico.getAxisLeft().addLimitLine(limiteMuyMalo);





    }

    /**
     * Metodo para mostrar y setear la gráfica
     * @author Lorena-Ioana Florescu
     * @version 19/11/2021
     * @param mediciones
     */
    private void mostrarGrafica(ArrayList<Medicion> mediciones, int color) {

        ArrayList<Integer> coloresBarras = new ArrayList<>();
        entryListGrafica = new ArrayList<>();
        // variable para nuestra gráfica

        binding.grafico.setVisibility(View.VISIBLE);
        binding.tablaGases.setVisibility(View.INVISIBLE);
        binding.cerrarGrafica.setVisibility(View.VISIBLE);

        if(!mediciones.isEmpty()){
            ArrayList<Medicion> medicionesAMostrar = Utilidades.transformarMedicionesAArrayMedicionesPorHora24Horas(mediciones);
            for(int i = 0; i< medicionesAMostrar.size(); i ++){

                Log.d("GRAFICA", "mostrarGrafica: se añade el color: "+ Utilidades.obtenerColorPorValorAQI(
                        (int) medicionesAMostrar.get(i).getValorAQI(),
                        getContext())+ " para el valor: "+ (int) medicionesAMostrar.get(i).getValorAQI());

                // por cada barra añadimos un color distinto
                coloresBarras.add(Utilidades.obtenerColorPorValorAQI(
                        (int) medicionesAMostrar.get(i).getValorAQI(),
                        getContext()));

                // creamos la barra
                BarEntry e = new BarEntry(i,
                        (float)medicionesAMostrar.get(i).getValorAQI());

                // la añadimos
                entryListGrafica.add(e);
            }

            // creamos el data set con todas las barras
            BarDataSet barDataSet = new BarDataSet(entryListGrafica,null);
            // añadimos los colores
            barDataSet.setColors(coloresBarras);

            // descripicion del grafico
            Description desc = new Description();
            desc.setText("Exposición (AQI) de "+medicionesAMostrar.get(0).getTipoMedicion().getNombreGas());
            binding.grafico.setDescription(desc);


            // formateamos los valores de cada elemento
            BarData barData = new BarData(barDataSet);
            barData.setValueFormatter(new BarChartValueFormatter());

            // mostramos la grafica
            binding.grafico.setData(barData);
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

    /**
     * Clase para formatear el eje x de las graficas
     * este formateo se le asigna el getAxisX()
     */
    public static class TimeXAxisValueFormatter extends IndexAxisValueFormatter{

        @Override
        public String getFormattedValue(float value) {
            // Convert float value to date string
            // Convert from days back to milliseconds to format time  to show to the user
            int hora = (int) value;

            return hora>=10 ? String.valueOf(hora)+":00": "0"+hora+":00";
        }
    }

    /**
     * Clase para formatear las etiquetas de los valores
     * Por cada barra comprobamos si el valor es 0 no mostrarmos la etiqueta
     *
     * este formateo se le asigna al barData
     *
     */
    public static class BarChartValueFormatter extends ValueFormatter {

        @Override
        public String getFormattedValue(float value) {

            if(value > 0) {
                // añadimos los colores de las barras a un array para luego añadirlo al data set cuando termine el
                // formateo
                return String.valueOf(Math.round(value));
            } else {
                return "";
            }


        }



    }
}