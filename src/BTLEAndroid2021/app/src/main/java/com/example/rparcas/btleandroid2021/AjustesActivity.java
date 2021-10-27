package com.example.rparcas.btleandroid2021;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

/**
 * AjustesActivity.java
 * @author Rubén Pardo Casanova
 * 26/10/2021
 * Activity con los ajustes de la aplicacion
 */
public class AjustesActivity extends AppCompatActivity {


    // la definicion de los ajustes estan en res/xml/root_preferences.xml
    // la valores estan en res/values/arrays.xml

    // en root_preferences el elemento del layout tiene referenciado la clave (usada para obtener el valor en
    // la app mediante shared preferences) en el atributo app:key, los nombres de los valores en entries
    // los valores en si en entrie_values en el caso que sean listas

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }
}