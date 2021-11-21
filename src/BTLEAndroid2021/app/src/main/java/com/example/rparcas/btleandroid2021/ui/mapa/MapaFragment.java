package com.example.rparcas.btleandroid2021.ui.mapa;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.rparcas.btleandroid2021.Constantes.RESTConstantes;
import com.example.rparcas.btleandroid2021.DialgoCarga;
import com.example.rparcas.btleandroid2021.R;
import com.example.rparcas.btleandroid2021.Utilidades;
import com.example.rparcas.btleandroid2021.databinding.FragmentMapaBinding;
import com.example.rparcas.btleandroid2021.modelo.Medicion;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import java.util.ArrayList;

import static android.content.Context.LOCATION_SERVICE;

/**
 * MapaFragment.java
 * @author Ruben Pardo Casanova
 * 01/11/2021
 * Clase que controla la vista del fragmento Mapa
 */
public class MapaFragment extends Fragment implements OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerClickListener{

    private MapaViewModel mapaViewModel;
    private FragmentMapaBinding binding;
    private DialgoCarga elDialogoDeCarga;

    private GoogleMap mMap;
    private LocationManager manejador;
    private Location ultimaLocalizacion;
    private String proveedor;
    public static final int PERMISION_CODE_LOCATION = 1235;
    private static final long TIEMPO_MIN_REFRESCO_LOCALIZACION = 5 * 1000 ; // 5 segundos
    private static final long DISTANCIA_MIN_REFRESCO_LOCALIZACION = 5; // 5 metros

    private TileOverlay overlayActual;// la capa del mapa que representa el mapa de calor
    private SupportMapFragment mapFragment;


    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mapaViewModel =
                new ViewModelProvider(this).get(MapaViewModel.class);

        binding = FragmentMapaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mapFragment = (SupportMapFragment) getChildFragmentManager().
                findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);

        elDialogoDeCarga = new DialgoCarga(getActivity());
        //mapaViewModel.obtenerMedicionesHoy();

        initCallbacks();
        initObservablesViewModel();


        return root;
    }


    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // pedir permisos de localizacion
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            Log.d("MAPA","onview created permisos dados");
            initLocation();
        }else{
            Utilidades.permissionAsk(Manifest.permission.ACCESS_FINE_LOCATION,getActivity(),PERMISION_CODE_LOCATION);
        }

    }


    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    private void initObservablesViewModel() {
        // controlar la ventana de carga y el texto de error
        mapaViewModel.getEstadoPeticionObtenerMediciones().observe(getViewLifecycleOwner(), estadoPeticion -> {

            switch (estadoPeticion){
                case SIN_ACCION:
                case EXITO:
                case ERROR:
                    esconderVentanaCarga();
                    break;
                case EN_PROCESO:
                    mostrarVentanaCarga();
                    break;
            }
        });

        // obtener las mediciones cuando se carguen de bd y de filtrar
        mapaViewModel.getMedicionesAMostrar().observe(getViewLifecycleOwner(), this::representarMedicionesEnMapa);
    }

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    private void mostrarVentanaCarga() {
        elDialogoDeCarga.empezarDialogoCarga(getString(R.string.iniciando_sesion));
    }

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    private void esconderVentanaCarga() {
        elDialogoDeCarga.esconderDialogCarga();
    }


    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    /**
     * Lista[Mediciones] -> representarMedicionesEnMapa()
     *
     * @author Ruben Pardo Casanova
     * 15/11/2021
     *
     * @param mediciones las mediciones
     */
    private void representarMedicionesEnMapa(ArrayList<WeightedLatLng> mediciones) {
        Log.d("SPINNER","Se pintan en el mapa: -----"+ mediciones);

        //int[] colors = {R.color.verde_3abb90, R.color.amarillo_ffc300, R.color.rojo_e23636,R.color.rojo_900C3F};
        //float[] startPoints = {0.2f,.65f, .8f, 1.0f};
        //Gradient gradient = new Gradient(colors, startPoints);

        if(!mediciones.isEmpty()){
            HeatmapTileProvider heatmapTileProvider = new HeatmapTileProvider.Builder()
                    .weightedData(mediciones)
                    .radius(40)
                    .opacity(0.4)
                    //.maxIntensity(20)
                    //.gradient(gradient)
                    .build();

            if(overlayActual!=null){
                overlayActual.remove();
            }
            overlayActual = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(heatmapTileProvider));
        }else{
            if(overlayActual!=null){
                overlayActual.remove();
            }
        }

    }

    private void representarMedicionesEnMapaFALSO() {
        ArrayList<WeightedLatLng> mediciones = new ArrayList<>();

        for(int i = 1; i<50; i++){
            for(int j = 1; j<10; j++){
                mediciones.add(new WeightedLatLng(new LatLng(Double.parseDouble("38.99"+j),Double.parseDouble("-0.16"+(100-i))), j*5 ));
            }
        }




        int[] colors = {Color.GREEN, Color.parseColor("#FF0000")};
        float[] startPoints = {0.2f, 1f};
        Gradient gradient = new Gradient(colors, startPoints);

        HeatmapTileProvider heatmapTileProvider = new HeatmapTileProvider.Builder()
                .weightedData(mediciones)
                .radius(20)
                .opacity(0.4)
                //.gradient(gradient)
                .build();

        if(overlayActual!=null){
            overlayActual.remove();
        }
        overlayActual = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(heatmapTileProvider));
    }


    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    private void initCallbacks() {
        binding.spinnerTipoGas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Log.d("SPINNER","item selected: "+parent.getItemAtPosition(position));
                mapaViewModel.filtrarMedicionPorTipo(Medicion.TipoMedicion.getTipoById(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.toggleButtonCambiarTipoMapa.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    cambiarModoMapaCalor();
                }else{
                    cambiarModoMapaRutas();
                }
            }
        });
    }

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    private void cambiarModoMapaRutas() {

        binding.spinnerTipoGas.setVisibility(View.INVISIBLE);
        binding.webview.setVisibility(View.INVISIBLE);
        mapFragment.getView().setVisibility(View.VISIBLE);

        /*if(overlayActual!=null){
            overlayActual.remove();
        }
        mMap.setMapStyle(null);*/
    }


    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    private void cambiarModoMapaCalor() {
        binding.spinnerTipoGas.setVisibility(View.VISIBLE);
        mapFragment.getView().setVisibility(View.INVISIBLE);
        binding.webview.setVisibility(View.VISIBLE);

        binding.webview.clearCache(true);
        binding.webview.getSettings().setJavaScriptEnabled(true);
        binding.webview.loadUrl("http://192.168.1.141/prueba.html");
        //representarMedicionesEnMapaFALSO();
        //mapaViewModel.obtenerMedicionesHoy();
        //mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(),R.raw.estilo_google_map_sin_poi_json));
    }

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("MAPA","onMapReady");
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.setOnMarkerClickListener(this);
        if(ultimaLocalizacion!=null){
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(ultimaLocalizacion.getLatitude(), ultimaLocalizacion.getLongitude()),15.0F));

        }

        if (ActivityCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            Log.d("MAPA","setMyLocationEnabled");

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);
        }

    }

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    @SuppressLint("MissingPermission")
    private void initLocation() {
        Log.d("MAPA","initLocation");

        manejador = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        Criteria criterio = new Criteria();
        criterio.setCostAllowed(false);
        criterio.setAltitudeRequired(false);
        criterio.setAccuracy(Criteria.ACCURACY_FINE);
        proveedor = manejador.getBestProvider(criterio, true);

        if(proveedor!=null){
            Log.d("MAPA","proveedor: "+proveedor);
            ultimaLocalizacion = manejador.getLastKnownLocation(proveedor);
            Log.d("MAPA","ultima loc: "+ultimaLocalizacion);
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                manejador.requestLocationUpdates(proveedor, TIEMPO_MIN_REFRESCO_LOCALIZACION, DISTANCIA_MIN_REFRESCO_LOCALIZACION,
                        this);
            }
        }

    }

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    // Métodos del ciclo de vida de la actividad
    @SuppressLint("MissingPermission")
    @Override
    public void onResume() {
        super.onResume();
        if(manejador !=null){
            if(proveedor!=null){
                manejador.requestLocationUpdates(proveedor, TIEMPO_MIN_REFRESCO_LOCALIZACION, DISTANCIA_MIN_REFRESCO_LOCALIZACION,
                        this);
            }

        }
    }
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    @SuppressLint("MissingPermission")
    @Override
    public void onPause() {
        super.onPause();
        if(manejador!=null){
            manejador.removeUpdates(this);
        }
    }
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    @Override
    public void onLocationChanged(@NonNull Location location) {
        Log.d("MAPA","locationChanged");

        ultimaLocalizacion = location;
    }

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    @Override
    public void onProviderEnabled(@NonNull String provider) {
        Log.d("MAPA","provider enabled");
        initLocation();
    }

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }


    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISION_CODE_LOCATION) {
            Log.d("MAPA","permissions result");
            if (grantResults.length== 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                initLocation();

            } else {
                Toast.makeText(getActivity(), "Sin el permiso, no puedo acceder a la " +
                        "localización", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


}