package com.example.rparcas.btleandroid2021.logica;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * @author Ruben Pardo Casanova
 * Singleton SharedPreferences
 * La funcion de esta clase es centralizar las funciones de los shared preferences
 */
public class SharedPreferencesHelper {

    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    // nombre del fichero shared preferences
    //private static final String PREF_NAME = "com.example.app.PREFERNCIAS";
    // claves de las variables
    private static final String KEY_AJUSTES_NIVEL_ALERTA_NOTIFICACION = "nivel_alerta_notificacion_gas";

    private static SharedPreferencesHelper sInstance;
    private final SharedPreferences mPref;

    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    private SharedPreferencesHelper(Context context) {
        mPref = PreferenceManager.getDefaultSharedPreferences(context);
    }
    public static synchronized void initializeInstance(Context context) {
        if (sInstance == null) {
            sInstance = new SharedPreferencesHelper(context);
        }
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    public static synchronized SharedPreferencesHelper getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException(SharedPreferencesHelper.class.getSimpleName() +
                    " is not initialized, call initializeInstance(..) method first.");
        }
        return sInstance;
    }


    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    /**
     * isNivelAlertaSensible() -> T/F
     * @return True si esta activado el ajuste de persona con problemas respiratorios
     */
    public Boolean isNivelAlertaSensible() {
        return mPref.getBoolean(KEY_AJUSTES_NIVEL_ALERTA_NOTIFICACION,false);
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    /**
     *
     * Texto -> remove()
     *
     * Elimina el shared preferences
     * @param key el key del shared a borrar
     */
    public void remove(String key) {
        mPref.edit()
                .remove(key)
                .apply();
    }
    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    /**
     * clear()->T/F
     * borra todos los clave-valor del shared preference
     * @return devuelve true si pudo borrarlo
     */
    public boolean clear() {
        return mPref.edit()
                .clear()
                .commit();
    }



}// class
//----------------------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------