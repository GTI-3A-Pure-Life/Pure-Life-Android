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
                intent.setData(Uri.parse("https://www.airnow.gov/aqi/aqi-basics-in-spanish/"));
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
     * Transoforma un array de n mediciones en un array de 1440 minutos(los minutos de un dia)
     * todoas las mediciones que comparten el mismo minuto se hace una media
     *
     * List<Medicion> -> transformarMedicionesAArrayMedicionesPorMinuto24Horas() -> Lista<Medicion>
     * @param mediciones mediciones a transformar
     */
    public static ArrayList<Medicion> transformarMedicionesAArrayMedicionesPorMinuto24Horas(ArrayList<Medicion> mediciones) {
        int minutos24Horas = 1440;
        ArrayList<Medicion> mediciones24Horas = new ArrayList<>(minutos24Horas);
        Timestamp hoy = new Timestamp(System.currentTimeMillis());
        String fechaHoy = new SimpleDateFormat("yyyy-MM-dd").format(hoy);
        fechaHoy = "2021-09-29";//TODO quitar
        // inicializar array
        for(int i = 0; i<minutos24Horas; i++){
            int hora = i/60;
            int minuto = i%60;
            String fecha = fechaHoy;
            // multiplo de 60, solo hora
            String horaStr = hora>=10 ? String.valueOf(hora) : "0"+hora;
            String minutoStr = minuto>=10 ? String.valueOf(minuto) : "0"+minuto;
            fecha += " "+horaStr+":"+minutoStr+":00";

            mediciones24Horas.add(new Medicion(fecha,0));
        }

        // poner los valores en cada uno de los minutos correspondientes, si hay varios en el mismo
        // media aritmetica

        int anteriorMinuto = -1;
        int contadorMedicionesMismoMintuo = 1;
        double media = 1;
        for(Medicion m : mediciones){
            int hora = m.getMedicion_fecha().getHours();
            int minutos = m.getMedicion_fecha().getMinutes();

            int indice = hora*60+minutos;
            Log.d("FECHA24", "----------------------------------------------");
            Log.d("FECHA24", "transformarMedicionesAArrayMedicionesPorMinuto24Horas: indice: "+indice);
            Log.d("FECHA24", "transformarMedicionesAArrayMedicionesPorMinuto24Horas: valor: "+m.getValor());

            if(anteriorMinuto != indice){
                anteriorMinuto = indice;
                contadorMedicionesMismoMintuo = 1;
                media = m.getValor();
                mediciones24Horas.get(indice).setValor(m.getValor());

            }
            else{

                double valorAnterior = mediciones24Horas.get(indice).getValor();
                Log.d("FECHA24", "transformarMedicionesAArrayMedicionesPorMinuto24Horas: valor anterior: "+valorAnterior+"");

                Log.d("FECHA24", "transformarMedicionesAArrayMedicionesPorMinuto24Horas: media anterior: "+media+"");
                Log.d("FECHA24", "transformarMedicionesAArrayMedicionesPorMinuto24Horas: hay : "+contadorMedicionesMismoMintuo+" mediciones");
                contadorMedicionesMismoMintuo++;
                double nuevaMedia = (contadorMedicionesMismoMintuo*media - valorAnterior + m.getValor())/ contadorMedicionesMismoMintuo;
                Log.d("FECHA24", "transformarMedicionesAArrayMedicionesPorMinuto24Horas: nueva media: "+nuevaMedia+"");

                media = nuevaMedia;
                mediciones24Horas.get(indice).setValor(nuevaMedia);

            }



        }

        return mediciones24Horas;
    }
} // class
// -----------------------------------------------------------------------------------
// -----------------------------------------------------------------------------------
// -----------------------------------------------------------------------------------
// -----------------------------------------------------------------------------------
