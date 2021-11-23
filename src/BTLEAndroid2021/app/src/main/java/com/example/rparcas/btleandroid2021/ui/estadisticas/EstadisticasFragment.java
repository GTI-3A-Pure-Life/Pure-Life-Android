package com.example.rparcas.btleandroid2021.ui.estadisticas;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.rparcas.btleandroid2021.R;
import com.example.rparcas.btleandroid2021.Utilidades;
import com.example.rparcas.btleandroid2021.databinding.FragmentEstadisticasBinding;
import com.example.rparcas.btleandroid2021.logica.EstadoPeticion;
import com.example.rparcas.btleandroid2021.logica.Logica;
import com.example.rparcas.btleandroid2021.logica.PeticionarioREST;
import com.example.rparcas.btleandroid2021.modelo.Medicion;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
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


        medicionesAMostrar =  new ArrayList<>();


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

        setProgreso(50,40,40, 30);

        //conectamos grafica con su vista en el xml
        lineChart = binding.grafico;

        binding.cerrarGrafica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.grafico.setVisibility(View.INVISIBLE);
                binding.tablaGases.setVisibility(View.VISIBLE);
                binding.cerrarGrafica.setVisibility(View.INVISIBLE);
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

        return root;
    }

    /**
     * Metodo para llenar el vector con los datos que vamos a necesitar en la gráfica
     * @author Lorena-Ioana Florescu
     * @version 20/11/2021

    public void llenarMediciones (){

        entryList.clear();

        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));

        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        for(int i = 0; i< medicionesAMostrar.size(); i ++){

            Medicion medida = medicionesAMostrar.get(i);
            cal.setTime(medida.getMedicion_fecha());
            float hora = cal.get(Calendar.MINUTE);
            entryList.add(new Entry(hora, (float)medida.getValor()));

        }


    }*/


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

                entryList.add(new Entry(i, (float)mediciones.get(i).getValor()));
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
    private void setProgreso(int cantidadCO, int cantidadNO2, int cantidadO3, int cantidadSO2){
        binding.pBarCO.setProgress(cantidadCO);
        binding.pBarNO2.setProgress(cantidadNO2);
        binding.pBarO3.setProgress(cantidadO3);
        binding.pBarSO2.setProgress(cantidadSO2);
    }

    /**
     * Settear el maximo valor de los gases
     * @author Lorena-Ioana Florescu
     * @version 19/11/2021
     * @param cantidadCO cantidad máxima de gas diaria
     * @param cantidadNO2 cantidad máxima de gas diaria
     * @param cantidadO3 cantidad máxima de gas diaria
     * @param cantidadSO2 cantidad máxima de gas diaria
     */
    private void setMaximo(int cantidadCO, int cantidadNO2, int cantidadO3, int cantidadSO2){
        binding.pBarCO.setMax(cantidadCO);
        binding.pBarNO2.setMax(cantidadNO2);
        binding.pBarO3.setMax(cantidadO3);
        binding.pBarSO2.setMax(cantidadSO2);
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
            long emissionsMilliSince1970Time = TimeUnit.DAYS.toMillis((long)value);
            // Show time in local version
            Date timeMilliseconds = new Date(emissionsMilliSince1970Time);
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat( "h:mm a");

            return dateTimeFormat.format(timeMilliseconds);
        }
    }
}