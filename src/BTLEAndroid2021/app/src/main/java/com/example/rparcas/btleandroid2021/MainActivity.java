package com.example.rparcas.btleandroid2021;

// ------------------------------------------------------------------
// ------------------------------------------------------------------

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.rparcas.btleandroid2021.adapters.DispositivosBLEAdapter;
import com.example.rparcas.btleandroid2021.modelo.TramaIBeacon;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

// ------------------------------------------------------------------
// ------------------------------------------------------------------

/**
 * Activity principal del proyecto de Biometría GTI-3A
 * Gestiona un servicio en segundo plano que escanea iBeacons
 * @author Rubén Pardo Casanova 21/09/2021
 */
public class MainActivity extends AppCompatActivity /*implements BeaconConsumer*/ {

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    private static final String ETIQUETA_LOG = ">>>>";

    private static final int CODIGO_PETICION_PERMISOS = 11223344;
    public static final String NOMBRE_DISPOSITIVO_A_ESCUCHAR_INTENT = "intent_servicio_nombre_dispositivo";

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    private BluetoothLeScanner elEscanner;

    private ScanCallback callbackDelEscaneo = null;

    private final String prefijoDeDispositivosAbuscar = "GTI-3A-"; // buscar todos los dispositivos que empiecen por este prefijo



    private RecyclerView recyclerView;
    private TextView emptyView;
    private DispositivosBLEAdapter dispositivosBLEAdapter;
    private HashSet<String> listaDipositivos;

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    /**
     * Metodo para buscar cualquier iBeacon que se encuentre con nuestro prefijo
     * buscarTodosLosDispositivosConPrefijoBTLE()
     *
     * @author Rubén Pardo Casanova
     *
     */
    private void buscarTodosLosDispositivosConPrefijoBTLE() {
        Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosConPrefijoBTLE(): empieza ");

        Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosConPrefijoBTLE(): instalamos scan callback ");

        this.detenerBusquedaDispositivosBTLE();



        this.callbackDelEscaneo = new ScanCallback() {
            @Override
            public void onScanResult( int callbackType, ScanResult resultado ) {
                super.onScanResult(callbackType, resultado);
                Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosConPrefijoBTLE(): onScanResult() ");

                //mostrarInformacionDispositivoBTLE( resultado );

                // obtenemos la trama ibeacon
                byte[] bytes = resultado.getScanRecord().getBytes();
                TramaIBeacon tib = new TramaIBeacon(bytes);
                // vemos si empieza por el prefijo de neustros dispositivos
                // ( no se puede hacer por filtro, el filtro solo si es nombre exacto)
                if(Utilidades.bytesToString(tib.getUUID()).contains(prefijoDeDispositivosAbuscar)){

                    // añadimos al hashset y actualizamos la lista
                    Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosConPrefijoBTLE(): UUID CON EL PREFIJO ");
                    listaDipositivos.add(Utilidades.bytesToString(tib.getUUID()).split("%")[0]);
                    actualizarRecyvlerView(listaDipositivos);
                }


            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosConPrefijoBTLE(): onBatchScanResults() ");

            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosConPrefijoBTLE(): onScanFailed(): "+errorCode);

            }
        };



        Log.d(ETIQUETA_LOG, "  buscarTodosLosDispositivosConPrefijoBTLE(): empezamos a escanear buscando: " + prefijoDeDispositivosAbuscar );
        //Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): empezamos a escanear buscando: " + dispositivoBuscado
        //      + " -> " + Utilidades.stringToUUID( dispositivoBuscado ) );

        this.elEscanner.startScan( this.callbackDelEscaneo);


    } // ()

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    /**
     * Metodo para mostrar la infrmación bluetooth escaneada
     * ScanResult -> mostrarInformacionDispositivoBTLE()
     *
     * @author Rubén Pardo Casanova
     * @param resultado información bluetooth escaneada a mostrar
     */
    private void mostrarInformacionDispositivoBTLE( ScanResult resultado ) {

        BluetoothDevice bluetoothDevice = resultado.getDevice();
        byte[] bytes = resultado.getScanRecord().getBytes();
        int rssi = resultado.getRssi();

        Log.d(ETIQUETA_LOG, " ****************************************************");
        Log.d(ETIQUETA_LOG, " ****** DISPOSITIVO DETECTADO BTLE ****************** ");
        Log.d(ETIQUETA_LOG, " ****************************************************");
        Log.d(ETIQUETA_LOG, " nombre = " + bluetoothDevice.getName());
        Log.d(ETIQUETA_LOG, " toString = " + bluetoothDevice.toString());

        /*
        ParcelUuid[] puuids = bluetoothDevice.getUuids();
        if ( puuids.length >= 1 ) {
            //Log.d(ETIQUETA_LOG, " uuid = " + puuids[0].getUuid());
           // Log.d(ETIQUETA_LOG, " uuid = " + puuids[0].toString());
        }*/

        Log.d(ETIQUETA_LOG, " dirección = " + bluetoothDevice.getAddress());
        Log.d(ETIQUETA_LOG, " rssi = " + rssi );

        Log.d(ETIQUETA_LOG, " bytes = " + new String(bytes));
        Log.d(ETIQUETA_LOG, " bytes (" + bytes.length + ") = " + Utilidades.bytesToHexString(bytes));

        TramaIBeacon tib = new TramaIBeacon(bytes);

        Log.d(ETIQUETA_LOG, " ----------------------------------------------------");
        Log.d(ETIQUETA_LOG, " prefijo  = " + Utilidades.bytesToHexString(tib.getPrefijo()));
        Log.d(ETIQUETA_LOG, "          advFlags = " + Utilidades.bytesToHexString(tib.getAdvFlags()));
        Log.d(ETIQUETA_LOG, "          advHeader = " + Utilidades.bytesToHexString(tib.getAdvHeader()));
        Log.d(ETIQUETA_LOG, "          companyID = " + Utilidades.bytesToHexString(tib.getCompanyID()));
        Log.d(ETIQUETA_LOG, "          iBeacon type = " + Integer.toHexString(tib.getiBeaconType()));
        Log.d(ETIQUETA_LOG, "          iBeacon length 0x = " + Integer.toHexString(tib.getiBeaconLength()) + " ( "
                + tib.getiBeaconLength() + " ) ");
        Log.d(ETIQUETA_LOG, " uuid  = " + Utilidades.bytesToHexString(tib.getUUID()));
        Log.d(ETIQUETA_LOG, " uuid  = " + Utilidades.bytesToString(tib.getUUID()));
        Log.d(ETIQUETA_LOG, " major  = " + Utilidades.bytesToHexString(tib.getMajor()) + "( "
                + Utilidades.bytesToInt(tib.getMajor()) + " ) ");
        Log.d(ETIQUETA_LOG, " minor  = " + Utilidades.bytesToHexString(tib.getMinor()) + "( "
                + Utilidades.bytesToInt(tib.getMinor()) + " ) ");
        Log.d(ETIQUETA_LOG, " txPower  = " + Integer.toHexString(tib.getTxPower()) + " ( " + tib.getTxPower() + " )");
        Log.d(ETIQUETA_LOG, " ****************************************************");

    } // ()

    // --------------------------------------------------------------
    // --------------------------------------------------------------



    // --------------------------------------------------------------
    // --------------------------------------------------------------
    private void detenerBusquedaDispositivosBTLE() {

        if ( this.callbackDelEscaneo == null ) {
            return;
        }

        this.elEscanner.stopScan( this.callbackDelEscaneo );
        this.callbackDelEscaneo = null;



    } // ()



    // --------------------------------------------------------------
    // --------------------------------------------------------------
    private void inicializarBlueTooth() {
        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): obtenemos adaptador BT ");

        BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): habilitamos adaptador BT ");

        bta.enable();

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): habilitado =  " + bta.isEnabled() );

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): estado =  " + bta.getState() );

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): obtenemos escaner btle ");

        this.elEscanner = bta.getBluetoothLeScanner();

        if ( this.elEscanner == null ) {
            Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): Socorro: NO hemos obtenido escaner btle  !!!!");

        }

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): voy a perdir permisos (si no los tuviera) !!!!");

        if (
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        )
        {
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION},
                    CODIGO_PETICION_PERMISOS);
        }
        else {
            Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): parece que YA tengo los permisos necesarios !!!!");

        }
    } // ()


    // --------------------------------------------------------------
    // --------------------------------------------------------------

    public void prueba(View v) {

        /*MedicionCO2 m = new MedicionCO2(1.2,4,"GTI-3A-1",new Posicion(31.56,32.5323));
        MedicionCO2 m2 = new MedicionCO2(2.51,4,"GTI-3A-1",new Posicion(31.56,32.7));
        List<MedicionCO2> lm = new ArrayList<>();
        lm.add(m);
        lm.add(m2);*/

        /*Logica l = new Logica();
        Log.d("PRUEBA", "prueba: AÑADIR 51");
        for(int i = 1; i<=120;i++){
            MedicionCO2 m = new MedicionCO2(i,4,"GTI-3A-1",new Posicion(31.56,32.5323));
            l.guardarMedicionesEnLocal(m,this);
        }*/


       /* Log.d("PRUEBA", "prueba: LISTAR TODAS");
        List<MedicionCO2> mediciones2 = l.obtenerPrimeras50MedicionesDeBDLocal(this);
        Log.d("PRUEBA", "SE VAN A BORRAR: M-> "+MedicionCO2.listaMedicionesToJSON(mediciones2));
        Log.d("PRUEBA", "prueba: BORRAR 50");
        l.borrarPrimeras50MedicionesDeBDLocal(this);*/



        //l.publicarMediciones(lm);

    }

    // --------------------------------------------------------------
    // --------------------------------------------------------------

    private void inicializarVistas() {

        recyclerView = findViewById(R.id.beacon_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        emptyView = (TextView) findViewById(R.id.empty_view);




        listaDipositivos = new HashSet<>();
        dispositivosBLEAdapter = new DispositivosBLEAdapter(this,new ArrayList<>(listaDipositivos));

        comprobarVacioRecyvlerView();


        recyclerView.setAdapter(dispositivosBLEAdapter);
        dispositivosBLEAdapter.setOnItemClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Log.d(ETIQUETA_LOG,"BEACON ADAPTER ONCLICK()");
                if(recyclerView!=null){

                   itemListaDispositivosPulsado(v);
                }
            }
        });



    }

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    private void actualizarRecyvlerView(HashSet<String> listaDipositivos){

        dispositivosBLEAdapter.updateData(new ArrayList<>(listaDipositivos));
        comprobarVacioRecyvlerView();
    }

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    /**
     * Metodo que esconde/muestra el recycler view o el emptytext
     * dependiendo de si hay items o no
     */
    private void comprobarVacioRecyvlerView() {
        if (listaDipositivos.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    // --------------------------------------------------------------
    // --------------------------------------------------------------

    /**
     * Callback de los elementos de la lista de dispositivos bluetooth
     * @param v elementos pulsado
     */
    private void itemListaDispositivosPulsado(View v) {
        int pos = recyclerView.getChildAdapterPosition(v);

        String dispositivoPulsado = dispositivosBLEAdapter.getItemAtPosition(pos);

        Log.d(ETIQUETA_LOG,"item pulsado: "+ dispositivoPulsado);

        // paramos la busqueda
        detenerBusquedaDispositivosBTLE();

        // nos dirgimos a la activity que lanza el servicio
        Intent intentActivityEscuchar = new Intent(this,ActivityEscucharBeacons.class);
        intentActivityEscuchar.putExtra(NOMBRE_DISPOSITIVO_A_ESCUCHAR_INTENT,dispositivoPulsado);

        startActivity(intentActivityEscuchar);

    }


    // --------------------------------------------------------------
    // --------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(ETIQUETA_LOG, " onCreate(): empieza ");

        inicializarVistas();

        inicializarBlueTooth();

        Log.d(ETIQUETA_LOG, " onCreate(): termina ");

    } // onCreate()



    // --------------------------------------------------------------
    // --------------------------------------------------------------
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults);

        switch (requestCode) {
            case CODIGO_PETICION_PERMISOS:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.d(ETIQUETA_LOG, " onRequestPermissionResult(): permisos concedidos  !!!!");
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                }  else {

                    Log.d(ETIQUETA_LOG, " onRequestPermissionResult(): Socorro: permisos NO concedidos  !!!!");

                }
                return;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    } // ()

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }
    // --------------------------------------------------------------
    // --------------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.menu_item_escanear) {



            // true = cambiar a escanear, false cambiar a parar
            if(item.isChecked()){
                item.setIcon(android.R.drawable.ic_media_play);
                Log.d(ETIQUETA_LOG, "onOptionsItemSelected: parar escanear: ");

                detenerBusquedaDispositivosBTLE();

            }else{
                item.setIcon(android.R.drawable.ic_media_pause);
                Log.d(ETIQUETA_LOG, "onOptionsItemSelected: escanear: ");

                buscarTodosLosDispositivosConPrefijoBTLE();

            }



            item.setChecked(!item.isChecked());


            return true;
        }

        return super.onOptionsItemSelected(item);
    }


} // class
// --------------------------------------------------------------
// --------------------------------------------------------------
// --------------------------------------------------------------
// --------------------------------------------------------------