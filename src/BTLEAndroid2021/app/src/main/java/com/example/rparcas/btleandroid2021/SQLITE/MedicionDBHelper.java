package com.example.rparcas.btleandroid2021.SQLITE;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.rparcas.btleandroid2021.modelo.Medicion;

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
    public void guardarMedicionesSQLITE(List<Medicion> mediciones) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        for (Medicion m: mediciones) {
            sqLiteDatabase.insert(
                    MedicionContract.MedicionEntry.NOMBRE_TABLA,
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
    public List<Medicion> obtener50Mediciones(){
        List<Medicion> mediciones = new ArrayList<>(50);

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        // hacemos el select
        try (Cursor cursor = sqLiteDatabase.query(
                false,  // Distinct
                MedicionContract.MedicionEntry.NOMBRE_TABLA,  // Nombre de la tabla
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
                mediciones.add(new Medicion(cursor));
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

        try(Cursor c = sqLiteDatabase.rawQuery("SELECT " + MedicionContract.MedicionEntry._ID
                        +" from " + MedicionContract.MedicionEntry.NOMBRE_TABLA
                        +" LIMIT 50 ",null))
        {
            while (c.moveToNext()) {
                String id = c.getString(0);
                sqLiteDatabase.execSQL("Delete from "
                        + MedicionContract.MedicionEntry.NOMBRE_TABLA
                        + " where "+  MedicionContract.MedicionEntry._ID + "= "+id);
            }

        }


    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + MedicionContract.MedicionEntry.NOMBRE_TABLA + " ("
                + MedicionContract.MedicionEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + MedicionContract.MedicionEntry.VALOR + " TEXT NOT NULL,"
                + MedicionContract.MedicionEntry.LATITUD + " REAL NOT NULL,"
                + MedicionContract.MedicionEntry.LONGITUD + " REAL NOT NULL,"
                + MedicionContract.MedicionEntry.SENSOR+ " TEXT NOT NULL,"
                + MedicionContract.MedicionEntry.USUARIO + " INTEGER,"
                + MedicionContract.MedicionEntry.FECHA + " TEXT"
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
