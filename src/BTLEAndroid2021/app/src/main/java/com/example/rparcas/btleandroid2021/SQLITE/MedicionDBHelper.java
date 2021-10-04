package com.example.rparcas.btleandroid2021.SQLITE;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.rparcas.btleandroid2021.modelo.MedicionCO2;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que gestiona la BD de sqlite de Mediciones
 * 03/10/2021
 * @author Rubén Pardo Casanova
 */
public class MedicionDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Mediciones.db";


    public MedicionDBHelper( Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    /**
     * Lista<MedicionCO2> -> guardarMedicionSQLITE()
     *
     * Guardar mediciones co2 en la base de datos interna
     * @param mediciones mediciones a guardar
     */
    public void guardarMedicionesSQLITE(List<MedicionCO2> mediciones) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        for (MedicionCO2 m: mediciones) {
            sqLiteDatabase.insert(
                    MedicionCO2Contract.MedicionCO2Entry.NOMBRE_TABLA,
                    null,
                    m.toContentValues());
        }

    }


    /**
     * obtener50Mediciones -> Lista<MedicionCO2>
     *
     *  Obtiene las ultima 50 mediciones de la base de datos sqlite
     * @return Lista de mediciones de la base de datos local
     */
    public List<MedicionCO2> obtener50Mediciones(){
        List<MedicionCO2> mediciones = new ArrayList<>(50);

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        // hacemos el select
        try (Cursor cursor = sqLiteDatabase.query(
                false,  // Distinct
                MedicionCO2Contract.MedicionCO2Entry.NOMBRE_TABLA,  // Nombre de la tabla
                null,  //Lista de Columnas a consultar
                null,  // Columnas para la cláusula WHERE
                null,  // Valores a comparar con las columnas del WHERE
                null,  //Agrupar con GROUP BY
                null,  // Condición HAVING para GROUP BY
                null, // Cláusula ORDER BY
                "50" //LIMITE
        )) {

            // llenamos el array con los resultados
            while (cursor.moveToNext()) {
                mediciones.add(new MedicionCO2(cursor));
            }
        }

        return mediciones;
    }

    /**
     * borrarUltimas50Mediciones()
     *
     * Borrar los ultimas 50 mediciones de la tabla
     *
     */
    public void borrarUltimas50Mediciones(){

        // seleccionar los ultimos 50
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        try(Cursor c = sqLiteDatabase.rawQuery("SELECT " + MedicionCO2Contract.MedicionCO2Entry._ID
                        +" from " + MedicionCO2Contract.MedicionCO2Entry.NOMBRE_TABLA
                        +" LIMIT 50 ",null))
        {
            while (c.moveToNext()) {
                String id = c.getString(0);
                sqLiteDatabase.execSQL("Delete from "
                        + MedicionCO2Contract.MedicionCO2Entry.NOMBRE_TABLA
                        + " where "+  MedicionCO2Contract.MedicionCO2Entry._ID + "= "+id);
            }

        }


    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + MedicionCO2Contract.MedicionCO2Entry.NOMBRE_TABLA + " ("
                + MedicionCO2Contract.MedicionCO2Entry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + MedicionCO2Contract.MedicionCO2Entry.VALOR + " TEXT NOT NULL,"
                + MedicionCO2Contract.MedicionCO2Entry.LATITUD + " REAL NOT NULL,"
                + MedicionCO2Contract.MedicionCO2Entry.LONGITUD + " REAL NOT NULL,"
                + MedicionCO2Contract.MedicionCO2Entry.SENSOR+ " TEXT NOT NULL,"
                + MedicionCO2Contract.MedicionCO2Entry.USUARIO + " INTEGER,"
                + MedicionCO2Contract.MedicionCO2Entry.FECHA + " TEXT"
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
