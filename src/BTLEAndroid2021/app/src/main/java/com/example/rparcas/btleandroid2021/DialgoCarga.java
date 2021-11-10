package com.example.rparcas.btleandroid2021;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * DialogoCarga.java
 * Clase para mostrar una ventana de carga que bloquea la actividad
 * @author Ruben Pardo Casanova
 * 10/11/2021
 */
public class DialgoCarga {

    Activity activity;
    AlertDialog dialog;

    // ---------------------------------------------------------------------
    // ---------------------------------------------------------------------
    public DialgoCarga(Activity activity){
        this.activity = activity;
    }

    // ---------------------------------------------------------------------
    // ---------------------------------------------------------------------
    public void empezarDialogoCarga(String titulo){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        // obener el objeto view del xml del dialog personalizado
        LayoutInflater inflater = activity.getLayoutInflater();
        View viewDialog = inflater.inflate(R.layout.custom_dialog_carga,null);
        // modificarlo con el tiulo que nos pasan
        TextView t = viewDialog.findViewById(R.id.textoTituloLoading);
        t.setText(titulo);

        builder.setView(viewDialog);
        builder.setCancelable(false);


        dialog = builder.create();
        dialog.show();
    }

    // ---------------------------------------------------------------------
    // ---------------------------------------------------------------------
    public void esconderDialogCarga(){
       if(dialog!=null){
           dialog.dismiss();
       }
    }

}
