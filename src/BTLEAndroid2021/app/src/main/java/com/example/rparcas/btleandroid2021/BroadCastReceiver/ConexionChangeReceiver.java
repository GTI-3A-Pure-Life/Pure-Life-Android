package com.example.rparcas.btleandroid2021.BroadCastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.rparcas.btleandroid2021.ActivityEscucharBeacons;

/**
 *  Clase que detecta un cambio en la conexion del dispositivo
 *  y avisa al MainActivity 04/10/2021
 * @author Ruben Pardo Casanova
 */
public class ConexionChangeReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        ActivityEscucharBeacons.onConexionChange(context);

    }
}
