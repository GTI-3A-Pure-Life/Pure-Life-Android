package com.example.rparcas.btleandroid2021.logica;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import android.os.AsyncTask;
import android.util.Log;

// ------------------------------------------------------------------------
// ------------------------------------------------------------------------

/**
 * Clase que gestiona las Peticiones REST
 * 03/10/2021
 * @author Rubén Pardo Casanova
 */
public class PeticionarioREST extends AsyncTask<Void, Void, Boolean> {

    // --------------------------------------------------------------------
    // --------------------------------------------------------------------
    public interface RespuestaREST {
        void callback (int codigo, String cuerpo);
    }

    // --------------------------------------------------------------------
    // --------------------------------------------------------------------
    private String elMetodo;
    private String urlDestino;
    private String elCuerpo = null;
    private RespuestaREST laRespuesta;

    private int codigoRespuesta;
    private String cuerpoRespuesta = "";

    // --------------------------------------------------------------------
    // --------------------------------------------------------------------

    /**
     * Texto, Texto, Texto, RespuestaREST -> hacerPeticionREST()
     *
     * Metodo que realica la peticion REST
     *
     * @param metodo metodo REST ( GET, PUT, POST, DELETE)
     * @param urlDestino por ejemplo http://192.168.1.113:8080/<recurso>
     * @param cuerpo datos en JSON
     * @param laRespuesta callback a la peticion REST
     */
    public void hacerPeticionREST (String metodo, String urlDestino, String cuerpo, RespuestaREST  laRespuesta) {
        this.elMetodo = metodo;
        this.urlDestino = urlDestino;
        this.elCuerpo = cuerpo;
        Log.d("PRUEBA", "hacerPeticionREST: cuerpo -> "+this.elCuerpo);
        this.laRespuesta = laRespuesta;

        this.execute(); // otro thread ejecutará doInBackground()
    }

    // --------------------------------------------------------------------
    // --------------------------------------------------------------------

    /**
     * CONSTRUCTOR de la clase PeticionarioREST
     */
    public PeticionarioREST() {
        //Log.d("clienterestandroid", "constructor()");
    }

    // --------------------------------------------------------------------
    // --------------------------------------------------------------------
    // Hacemos las peticiones en segundo plano
    @Override
    protected Boolean doInBackground(Void... params) {
        Log.d("clienterestandroid", "doInBackground()");
        HttpURLConnection connection = null;
        try {

            // envio la peticion

            // pagina web para hacer pruebas: URL url = new URL("https://httpbin.org/html");
            // ordinador del despatx 158.42.144.126 // OK URL url = new URL("http://158.42.144.126:8080");

            Log.d("clienterestandroid", "doInBackground() me conecto a >" + urlDestino + "<");

            URL url = new URL(urlDestino);

            connection = (HttpURLConnection) url.openConnection();
           // connection.setRequestProperty( "Content-Type", "application/json; charset-utf-8" );
            connection.setRequestProperty( "Content-Type", "application/json" );
            connection.setRequestMethod(this.elMetodo);
            // connection.setRequestProperty("Accept", "*/*);

            // connection.setUseCaches(false);
            connection.setDoInput(true);

            // añadimos datos al cuerpo si el metodo no es GET y hay datos
            if ( ! this.elMetodo.equals("GET") && this.elCuerpo != null ) {
                Log.d("clienterestandroid", "doInBackground(): no es get, pongo cuerpo");
                connection.setDoOutput(true);
                // si no es GET, pongo el cuerpo que me den en la peticin
                DataOutputStream dos = new DataOutputStream (connection.getOutputStream());
                dos.writeBytes(this.elCuerpo);
                dos.flush();
                dos.close();
            }

            // ya he enviado la peticion
            Log.d("clienterestandroid", "doInBackground(): peticion enviada ");

            // ahora obtengo la respuesta
            int rc = connection.getResponseCode();
            String rm = connection.getResponseMessage();
            String respuesta = "" + rc + " : " + rm;
            Log.d("clienterestandroid", "doInBackground() recibo respuesta = " + respuesta);
            this.codigoRespuesta = rc;

            try {

                InputStream is = connection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));

                Log.d("clienterestandroid", "leyendo cuerpo");
                StringBuilder acumulador = new StringBuilder ();
                String linea;
                while ( (linea = br.readLine()) != null) {
                    Log.d("clienterestandroid", linea);
                    acumulador.append(linea);
                }
                Log.d("clienterestandroid", "FIN leyendo cuerpo");

                this.cuerpoRespuesta = acumulador.toString();
                Log.d("clienterestandroid", "cuerpo recibido=" + this.cuerpoRespuesta);

                connection.disconnect();

            } catch (IOException ex) {
                // dispara excepcin cuando la respuesta REST no tiene cuerpo y yo intento getInputStream()
                Log.d("clienterestandroid", "doInBackground() : parece que no hay cuerpo en la respuesta");
            }

            return true; // doInBackground() termina bien

        } catch (Exception ex) {
            Log.d("clienterestandroid", "doInBackground(): ocurrio alguna otra excepcion: " + ex.getMessage());
        }finally {
            // cerramos la conexion siempre por si ocurre un error inesperado
            if(connection!=null){
                connection.disconnect();
            }
        }

        return false; // doInBackground() NO termina bien
    } // ()

    // --------------------------------------------------------------------
    // --------------------------------------------------------------------
    protected void onPostExecute(Boolean comoFue) {
        // llamado tras doInBackground()
        Log.d("clienterestandroid", "onPostExecute() comoFue = " + comoFue);
        this.laRespuesta.callback(this.codigoRespuesta, this.cuerpoRespuesta);
    }

} // class