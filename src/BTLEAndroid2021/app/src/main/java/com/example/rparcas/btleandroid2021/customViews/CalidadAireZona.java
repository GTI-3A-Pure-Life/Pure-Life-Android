package com.example.rparcas.btleandroid2021.customViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.rparcas.btleandroid2021.R;
import com.example.rparcas.btleandroid2021.modelo.InformeCalidad;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

/**
 * CalidadAireZona.java
 * 21/11/2021
 * Clase que representa al componente View de calidad de aire
 */
public class CalidadAireZona extends CardView {
    //-----------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------
    private float valorAQI = 0.0f;
    private int gasMasExpuesto = 0;
    private int icono = 0;
    private String nombreSitio = "";
    //-----------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------
    private InformeCalidad[] informesCalidad;
    //-----------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------
    private LinearLayout fondo;
    private ImageView ivIcono;
    private TextView tvNombeLugar;
    private TextView tvValorAQI;
    private TextView tvCalidadAire;
    private LinearLayout layoutCarga;
    private LinearLayout layoutDatos;

    private EstadoCalidadAire estadoCalidadAire = EstadoCalidadAire.VACIO;

    private final String TAG = "calidad_aire_zona";
    private String calidadAire;

    //-----------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------
    public CalidadAireZona(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Log.d(TAG, "CalidadAireZona: on create");
        // obtener los atributos puestos en el xml
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CalidadAireZona,
                0, 0);

        try {

            // obtener la inflater de la view para settear los valores
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.custom_calidad_aire_zona,this);

            tvCalidadAire = findViewById(R.id.customCalidadAire_tvCalidadAire);
            ivIcono = findViewById(R.id.customCalidadAire_img_icono);
            fondo = findViewById(R.id.customCalidadAire_linearIcono);
            tvValorAQI = findViewById(R.id.customCalidadAire_tvValorAQI);
            tvNombeLugar = findViewById(R.id.customCalidadAire_tvNombreLugar);
            layoutCarga = findViewById(R.id.customCalidadAire_linearCarga);
            layoutDatos = findViewById(R.id.customCalidadAire_linearDatos);

            // obtner valor de los atributos
            icono = a.getResourceId(R.styleable.CalidadAireZona_icono, 0);
            nombreSitio = a.getString(R.styleable.CalidadAireZona_nombreSitio);


            personalizarVista();

        } finally {
            a.recycle();
        }



    }


    // -----------------------------------------------------------------------
    // -----------------------------------------------------------------------
    private void personalizarVista() {

        switch (estadoCalidadAire){
            case VACIO:
                layoutCarga.setVisibility(INVISIBLE);
                layoutDatos.setVisibility(VISIBLE);
                tvValorAQI.setText(getResources().getString(R.string.no_tienes_posicion_asignada_de)+" "+nombreSitio);
                fondo.setBackgroundColor(getResources().getColor(R.color.design_default_color_background));
                tvCalidadAire.setText("");
                ivIcono.setImageResource(icono);
                tvNombeLugar.setText(nombreSitio);
                break;
            case CARGANDO:
                layoutCarga.setVisibility(VISIBLE);
                layoutDatos.setVisibility(INVISIBLE);
                break;
            case CON_DATOS:
                layoutCarga.setVisibility(INVISIBLE);
                layoutDatos.setVisibility(VISIBLE);
                // la calidad de aire de una zona viene dada por el peor valor AQI de todos los gases
                InformeCalidad informeCalidadAMostrar = informesCalidad[0];

                for(InformeCalidad informeIT : informesCalidad){
                    if(informeIT.getValorAQI() > informeCalidadAMostrar.getValorAQI()){
                        informeCalidadAMostrar = informeIT;
                    }
                }

                valorAQI = informeCalidadAMostrar.getValorAQI();
                calidadAire="";
                int colorFondo = 0;
                if(valorAQI<=50){
                    // bueno

                    calidadAire = getResources().getString(R.string.buena);
                    colorFondo = (getResources().getColor(R.color.verde_3abb90));

                }else if(valorAQI>50 && valorAQI<=150){
                    // moderado

                    calidadAire = getResources().getString(R.string.moderada);
                    colorFondo = (getResources().getColor(R.color.amarillo_ffc300));


                }else if(valorAQI>150 && valorAQI<=200){
                    // malo
                    calidadAire = getResources().getString(R.string.mala);
                    colorFondo = (getResources().getColor(R.color.rojo_e23636));

                }else{
                    // muy malo
                    calidadAire = getResources().getString(R.string.muy_mala);
                    colorFondo = (getResources().getColor(R.color.rojo_900C3F));
                }

                tvCalidadAire.setText(calidadAire+". "+getResources().getString(R.string.estas_mas_expuesto_a) +" " +informeCalidadAMostrar.getTipoGas().getNombreGas());
                fondo.setBackgroundColor(colorFondo);
                tvValorAQI.setText(valorAQI+" AQI");
                ivIcono.setImageResource(icono);
                tvNombeLugar.setText(nombreSitio);
                break;
        }

    }



    // -----------------------------------------------------------------------
    // -----------------------------------------------------------------------
    public void setInformesCalidad(InformeCalidad[] informesCalidad) {
        Log.d(TAG, "setInformesCalidad: "+informesCalidad);
        this.informesCalidad = informesCalidad;
        estadoCalidadAire = EstadoCalidadAire.CON_DATOS;
        personalizarVista();
        invalidate();
        requestLayout();
    }

    public void setEstadoCalidadAire(EstadoCalidadAire estadoCalidadAire){
        this.estadoCalidadAire =estadoCalidadAire;
        personalizarVista();
        invalidate();
        requestLayout();
    }

    public String getResumenCalidadAire() {
        return calidadAire;
    }

    public enum EstadoCalidadAire{
        CARGANDO,VACIO,CON_DATOS
    }
}
