package com.example.rparcas.btleandroid2021.SQLITE;

import android.provider.BaseColumns;

/**
 * Esquema de Medicion para la base de datos interna
 * @author Rubén Pardo Casanova
 */
public class MedicionContract {

    public static abstract class MedicionEntry implements BaseColumns {

        public static final String NOMBRE_TABLA ="medicionco2";

        public static final String VALOR = "valor";
        public static final String LATITUD = "latitud";
        public static final String LONGITUD = "longitud";
        public static final String FECHA = "fecha";
        public static final String USUARIO = "usuario";
        public static final String SENSOR = "sensor";
        public static final String TIPO_MEDICION = "tipo";
    }
}
