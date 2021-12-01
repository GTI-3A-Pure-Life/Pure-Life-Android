package com.example.rparcas.btleandroid2021;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.example.rparcas.btleandroid2021.modelo.Medicion;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.UUID;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Clase con metodo utiles para el proyecto
 * @author Rubén Pardo Casanova 21/09/2021
 */
public class Utilidades {



    /**
     * Metodo para obtener la distancia entre el sensor y el movil.
     * No es un valor muy fiable ya que el RSSI difiere mucho entre beacons en el mismo punto
     * @author Lorena-Ioana Florescu
     * 12/11/2021
     * @param rssi valor que cambia según la distancia
     * @param txPower valor constante a un metro
     * @return devuelve la distancia en metros
     */
    public static double calcularDistanciaDispositivoBluetooth (int rssi, int txPower){
        double n = 4.0;
        double power = (Math.abs(rssi) - txPower)/(10*n);

        return Math.pow (10, power);

        /*
        >2200 muy lejos
        <1800 mas o menos 5 metros
         */
    }


    /**
     * Metodo para mostrar el dialog y darle funcionalidad a los botones
     * @author Lorena-Ioana Florescu
     * @version 17/11/2021
     */
    public static void showCustomDialog(Context c){
        final Dialog dialog = new Dialog(c);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //para que el usuario cancele el dialog cuando apriete en cualquier sitio fuera
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_inf_gases);

        Button leerMas = dialog.findViewById(R.id.btLeerMas);
        Button volver = dialog.findViewById(R.id.btVolver);

        leerMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://www.who.int/phe/health_topics/AQG_spanish.pdf"));
                c.startActivity(intent);
                dialog.dismiss();
            }
        });

        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }





    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    public static String stringToSHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] textBytes = text.getBytes("iso-8859-1");
        md.update(textBytes, 0, textBytes.length);
        byte[] sha1hash = md.digest();
        return convertToHex(sha1hash);
    }

    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    public static byte[] stringToBytes ( String texto ) {
        return texto.getBytes();
        // byte[] b = string.getBytes(StandardCharsets.UTF_8); // Ja
    } // ()

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    public static UUID stringToUUID( String uuid ) {
        if ( uuid.length() != 16 ) {
            throw new Error( "stringUUID: string no tiene 16 caracteres ");
        }
        byte[] comoBytes = uuid.getBytes();

        String masSignificativo = uuid.substring(0, 8);
        String menosSignificativo = uuid.substring(8, 16);
        UUID res = new UUID( Utilidades.bytesToLong( masSignificativo.getBytes() ), Utilidades.bytesToLong( menosSignificativo.getBytes() ) );

        // Log.d( MainActivity.ETIQUETA_LOG, " \n\n***** stringToUUID *** " + uuid  + "=?=" + Utilidades.uuidToString( res ) );

        // UUID res = UUID.nameUUIDFromBytes( comoBytes ); no va como quiero

        return res;
    } // ()

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    public static String uuidToString ( UUID uuid ) {
        return bytesToString( dosLongToBytes( uuid.getMostSignificantBits(), uuid.getLeastSignificantBits() ) );
    } // ()

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    public static String uuidToHexString ( UUID uuid ) {
        return bytesToHexString( dosLongToBytes( uuid.getMostSignificantBits(), uuid.getLeastSignificantBits() ) );
    } // ()

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    public static String bytesToString( byte[] bytes ) {
        if (bytes == null ) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append( (char) b );
        }
        return sb.toString();
    }

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    public static byte[] dosLongToBytes( long masSignificativos, long menosSignificativos ) {
        ByteBuffer buffer = ByteBuffer.allocate( 2 * Long.BYTES );
        buffer.putLong( masSignificativos );
        buffer.putLong( menosSignificativos );
        return buffer.array();
    }

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    public static int bytesToInt( byte[] bytes ) {
        return new BigInteger(bytes).intValue();
    }

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    public static long bytesToLong( byte[] bytes ) {
        return new BigInteger(bytes).longValue();
    }

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    public static int bytesToIntOK( byte[] bytes ) {
        if (bytes == null ) {
            return 0;
        }

        if ( bytes.length > 4 ) {
            throw new Error( "demasiados bytes para pasar a int ");
        }
        int res = 0;



        for( byte b : bytes ) {
           /*
           Log.d( MainActivity.ETIQUETA_LOG, "bytesToInt(): byte: hex=" + Integer.toHexString( b )
                   + " dec=" + b + " bin=" + Integer.toBinaryString( b ) +
                   " hex=" + Byte.toString( b )
           );
           */
            res =  (res << 8) // * 16
                    + (b & 0xFF); // para quedarse con 1 byte (2 cuartetos) de lo que haya en b
        } // for

        if ( (bytes[ 0 ] & 0x8) != 0 ) {
            // si tiene signo negativo (un 1 a la izquierda del primer byte
            res = -(~(byte)res)-1; // complemento a 2 (~) de res pero como byte, -1
        }
       /*
        Log.d( MainActivity.ETIQUETA_LOG, "bytesToInt(): res = " + res + " ~res=" + (res ^ 0xffff)
                + "~res=" + ~((byte) res)
        );
        */

        return res;
    } // ()

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------
    public static String bytesToHexString( byte[] bytes ) {

        if (bytes == null ) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
            sb.append(':');
        }
        return sb.toString();
    } // ()


    /**
     * hayConexion() -> T/F
     *
     * @return T/F si el dispositivo tiene conexion a internet
     */
    public static boolean hayConexion(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null
                && connectivityManager.getActiveNetworkInfo().isAvailable();
    }


    public static void permissionAsk(String permission, Activity activity, int requestCode) {
        int grant = ContextCompat.checkSelfPermission(activity, permission);
        if (grant != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list = new String[1];
            permission_list[0] = permission;
            ActivityCompat.requestPermissions(activity, permission_list, requestCode);
        }
    }

    /**
     *
     * Devuelve la referencia de color respecto a valor aqi
     * @param valorAQI valor a calcular
     * @return referencia color
     */
    public static int obtenerColorPorValorAQI(int valorAQI,Context context) {
        if(valorAQI<=50){
            return context.getResources().getColor(R.color.verde_3abb90);
        }else if(valorAQI<=150){
            return context.getResources().getColor(R.color.amarillo_ffc300);
        }else if(valorAQI<=200){
            return context.getResources().getColor(R.color.rojo_e23636);
        }else{
            return context.getResources().getColor(R.color.rojo_900C3F);
        }
    }



    /**
     * List<Medicion> -> ordenarMedicionesPorFecha() -> Lista<Medicion>
     * @param mediciones mediciones a ordenar porfecha
     */
    public static void ordenarMedicionesPorFecha(ArrayList<Medicion> mediciones) {
        Collections.sort(mediciones, new Comparator<Medicion>() {
            public int compare(Medicion o1, Medicion o2) {
                return o1.getMedicion_fecha().compareTo(o2.getMedicion_fecha());
            }
        });
    }


    /**
     *
     * Transoforma un array de n mediciones en un array de 24 horas
     * todoas las mediciones que comparten el mismo minuto se hace una media
     *
     * List<Medicion> -> transformarMedicionesAArrayMedicionesPorHora24Horas() -> Lista<Medicion>
     * @param mediciones mediciones a transformar
     */
    public static ArrayList<Medicion> transformarMedicionesAArrayMedicionesPorHora24Horas(ArrayList<Medicion> mediciones) {
        int horas = 24;
        ArrayList<Medicion> mediciones24Horas = new ArrayList<>(horas);
        Timestamp hoy = new Timestamp(System.currentTimeMillis());
        String fechaHoy = new SimpleDateFormat("yyyy-MM-dd").format(hoy);
        fechaHoy = "2021-11-16";//TODO quitar
        // inicializar array
        for(int i = 0; i<horas; i++){
            String fecha = fechaHoy;
            // multiplo de 60, solo hora
            String horaStr = i>=10 ? String.valueOf(i) : "0"+i;
            fecha += " "+horaStr+":00:00";
            mediciones24Horas.add(new Medicion(fecha,0));
        }

        // poner los valores en cada uno de los minutos correspondientes, si hay varios en el mismo
        // media aritmetica

        int anteriorHora = -1;
        int contadorMedicionesMismaHora = 1;
        double media = 1;
        for(Medicion m : mediciones){
            int hora = m.getMedicion_fecha().getHours();

            Log.d("FECHA24", "----------------------------------------------");
            Log.d("FECHA24", "transformarMedicionesAArrayMedicionesPorMinuto24Horas: indice: "+hora);
            Log.d("FECHA24", "transformarMedicionesAArrayMedicionesPorMinuto24Horas: valor: "+m.getValor());

            if(anteriorHora != hora){
                anteriorHora = hora;
                contadorMedicionesMismaHora = 1;
                media = m.getValorAQI();
                mediciones24Horas.get(hora).setValorAQI(m.getValorAQI());

            }
            else{

                double valorAnterior = mediciones24Horas.get(hora).getValorAQI();
                Log.d("FECHA24", "transformarMedicionesAArrayMedicionesPorMinuto24Horas: valor anterior: "+valorAnterior+"");

                Log.d("FECHA24", "transformarMedicionesAArrayMedicionesPorMinuto24Horas: media anterior: "+media+"");
                Log.d("FECHA24", "transformarMedicionesAArrayMedicionesPorMinuto24Horas: hay : "+contadorMedicionesMismaHora+" mediciones");
                contadorMedicionesMismaHora++;
                double nuevaMedia = (contadorMedicionesMismaHora*media - valorAnterior + m.getValorAQI())/ contadorMedicionesMismaHora;
                Log.d("FECHA24", "transformarMedicionesAArrayMedicionesPorMinuto24Horas: nueva media: "+nuevaMedia+"");

                media = nuevaMedia;
                mediciones24Horas.get(hora).setValorAQI(nuevaMedia);

            }



        }
        Log.d("GRAFICA", "transformarMedicionesAArrayMedicionesPorHora24Horas------------------------------");

        for(Medicion m  : mediciones24Horas){
            Log.d("GRAFICA", "Fecha: "+m.getMedicion_fecha()+ " - valor: "+m.getValorAQI());
        }
        Log.d("GRAFICA", "transformarMedicionesAArrayMedicionesPorHora24Horas------------------------------");


        return mediciones24Horas;
    }

    /**
     * Calcular el valor de AQI
     * Ruben Pardo Casnova 25/11/2021
     * @param valor en ppm
     * @param tipoMedicion tipo de gas
     * @return valor en indice AQI
     */
    public static double calcularValorAQI(double valor, Medicion.TipoMedicion tipoMedicion) {
        double valorAQI = 0;
        switch (tipoMedicion){
            case CO:
                if(valor>=0 && valor< Medicion.RangoNivelesPeligro.TOPE_LEVE_CO){
                    //leve
                    valorAQI = valor* Medicion.VALOR_AQI.NIVEL_BUENO/ Medicion.RangoNivelesPeligro.TOPE_LEVE_CO;
                }
                else if(valor>= Medicion.RangoNivelesPeligro.TOPE_LEVE_CO && valor< Medicion.RangoNivelesPeligro.TOPE_MODERADO_CO){
                    // moderado
                    valorAQI = valor* Medicion.VALOR_AQI.NIVEL_MODERADO/ Medicion.RangoNivelesPeligro.TOPE_MODERADO_CO;
                }
                else if(valor>= Medicion.RangoNivelesPeligro.TOPE_MODERADO_CO && valor< Medicion.RangoNivelesPeligro.TOPE_ALTO_CO){
                    // alto
                    valorAQI = valor* Medicion.VALOR_AQI.NIVEL_ALTO/ Medicion.RangoNivelesPeligro.TOPE_ALTO_CO;
                }
                else{
                    valorAQI = valor* Medicion.VALOR_AQI.NIVEL_MUY_ALTO/ Medicion.RangoNivelesPeligro.TOPE_MUY_ALTO_CO;
                }
                break;
            case O3:
                if(valor>=0 && valor< Medicion.RangoNivelesPeligro.TOPE_LEVE_O3){
                    //leve
                    valorAQI = valor* Medicion.VALOR_AQI.NIVEL_BUENO/ Medicion.RangoNivelesPeligro.TOPE_LEVE_O3;
                }
                else if(valor>= Medicion.RangoNivelesPeligro.TOPE_LEVE_O3 && valor< Medicion.RangoNivelesPeligro.TOPE_MODERADO_O3){
                    // moderado
                    valorAQI = valor* Medicion.VALOR_AQI.NIVEL_MODERADO/ Medicion.RangoNivelesPeligro.TOPE_MODERADO_O3;
                }
                else if(valor>= Medicion.RangoNivelesPeligro.TOPE_MODERADO_O3 && valor< Medicion.RangoNivelesPeligro.TOPE_ALTO_O3){
                    // moderado
                    valorAQI = valor* Medicion.VALOR_AQI.NIVEL_ALTO/ Medicion.RangoNivelesPeligro.TOPE_ALTO_O3;
                }
                else{
                    // alto
                    valorAQI = valor* Medicion.VALOR_AQI.NIVEL_MUY_ALTO/ Medicion.RangoNivelesPeligro.TOPE_MUY_ALTO_O3;
                }
                break;
            case NO2:
                // leve 0-100, moderado 100-140, grave 140-200, muy grave 200 ug/m3
                // 1ppm - 1.88 ug/m3
                if(valor>=0 && valor< Medicion.RangoNivelesPeligro.TOPE_LEVE_NO2){
                    //leve
                    valorAQI = valor* Medicion.VALOR_AQI.NIVEL_BUENO/ Medicion.RangoNivelesPeligro.TOPE_LEVE_NO2;
                }
                else if(valor>= Medicion.RangoNivelesPeligro.TOPE_LEVE_NO2 && valor< Medicion.RangoNivelesPeligro.TOPE_MODERADO_NO2){
                    // moderado
                    valorAQI = valor* Medicion.VALOR_AQI.NIVEL_MODERADO/ Medicion.RangoNivelesPeligro.TOPE_MODERADO_NO2;
                }
                else if(valor>= Medicion.RangoNivelesPeligro.TOPE_MODERADO_NO2 && valor< Medicion.RangoNivelesPeligro.TOPE_ALTO_NO2){
                    // alto
                    valorAQI = valor* Medicion.VALOR_AQI.NIVEL_ALTO/ Medicion.RangoNivelesPeligro.TOPE_ALTO_NO2;
                }
                else{
                    // muy alto
                    valorAQI = valor* Medicion.VALOR_AQI.NIVEL_MUY_ALTO/ Medicion.RangoNivelesPeligro.TOPE_MUY_ALTO_NO2;
                }
                break;
            case SO2:
                // leve 0-150, moderado 150-250, grave 250-350, muy grave 350 ug/m3
                // 1ppm - 2.62 ug/m3
                if(valor>=0 && valor< Medicion.RangoNivelesPeligro.TOPE_LEVE_SO2){
                    //leve
                    valorAQI = valor* Medicion.VALOR_AQI.NIVEL_BUENO/ Medicion.RangoNivelesPeligro.TOPE_LEVE_SO2;
                }
                else if(valor>= Medicion.RangoNivelesPeligro.TOPE_LEVE_SO2 && valor< Medicion.RangoNivelesPeligro.TOPE_MODERADO_SO2){
                    // moderado
                    valorAQI = valor* Medicion.VALOR_AQI.NIVEL_MODERADO/ Medicion.RangoNivelesPeligro.TOPE_MODERADO_SO2;
                }
                else if(valor>= Medicion.RangoNivelesPeligro.TOPE_MODERADO_SO2 && valor< Medicion.RangoNivelesPeligro.TOPE_ALTO_SO2){
                    // alto
                    valorAQI = valor* Medicion.VALOR_AQI.NIVEL_ALTO/ Medicion.RangoNivelesPeligro.TOPE_ALTO_SO2;
                }
                else{
                    // muy alto
                    valorAQI = valor* Medicion.VALOR_AQI.NIVEL_MUY_ALTO/ Medicion.RangoNivelesPeligro.TOPE_MUY_ALTO_SO2;
                }
                break;

        }

        return Math.round(valorAQI * 100.0) / 100.0;
    }


    /**
     * Calcular el nivel de peligro respecto al valor aqi
     * Ruben Pardo Casnova 25/11/2021
     * @param valorAQI en ppm
     * @return devuvelve un objeto NivelPeligro
     */
    public static Medicion.NivelPeligro obtenerNivelPeligroAQI(double valorAQI) {
        Medicion.NivelPeligro nivelPeligro = null;

        if(valorAQI<= Medicion.VALOR_AQI.NIVEL_BUENO){
            // bueno
            nivelPeligro = Medicion.NivelPeligro.LEVE;
        }else if(valorAQI> Medicion.VALOR_AQI.NIVEL_BUENO && valorAQI<= Medicion.VALOR_AQI.NIVEL_MODERADO){
            // moderado
            nivelPeligro = Medicion.NivelPeligro.MODERADO;
        }else if(valorAQI> Medicion.VALOR_AQI.NIVEL_MODERADO && valorAQI<= Medicion.VALOR_AQI.NIVEL_ALTO){
            // malo
            nivelPeligro = Medicion.NivelPeligro.ALTO;
        }else{
            // muy malo
            nivelPeligro = Medicion.NivelPeligro.MUY_ALTO;
        }

        return nivelPeligro;
    }
} // class
// -----------------------------------------------------------------------------------
// -----------------------------------------------------------------------------------
// -----------------------------------------------------------------------------------
// -----------------------------------------------------------------------------------
