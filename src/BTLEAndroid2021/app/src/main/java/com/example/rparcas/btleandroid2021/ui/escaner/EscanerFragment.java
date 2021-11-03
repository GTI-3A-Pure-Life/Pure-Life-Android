package com.example.rparcas.btleandroid2021.ui.escaner;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.rparcas.btleandroid2021.R;
import com.example.rparcas.btleandroid2021.databinding.FragmentEscanerBinding;
import com.example.rparcas.btleandroid2021.modelo.Medicion;

import com.blikoon.qrcodescanner.QrCodeActivity;


/**
 * EscanerFragment.java
 * @author Ruben Pardo Casanova
 * 01/11/2021
 * Clase que controla la vista del fragmento Escaner
 */
public class EscanerFragment extends Fragment {

    private EscanerViewModel escanerViewModel;
    private FragmentEscanerBinding binding; // este objeto hace referncia a un xml layout, no es una clase creada

    //codigos de peticion de permisos
    private static final int REQUEST_CODE_QR_SCAN = 101;
    private final int REQUEST_CODE_CAMERA = 123;

    private static String prefijo = "GTI-3A-";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        escanerViewModel =
                new ViewModelProvider(this).get(EscanerViewModel.class);

        binding = FragmentEscanerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initCallbacks();
        initObserversViewModel();

        return root;
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    private void initObserversViewModel() {

        escanerViewModel.getNivelPeligro().observe(getViewLifecycleOwner(), new Observer<Medicion.NivelPeligro>() {
            @Override
            public void onChanged(@Nullable Medicion.NivelPeligro nivelPeligro) {

                switch (nivelPeligro){
                    case LEVE:
                        binding.fondoEscaner.setBackgroundColor(ResourcesCompat.getColor(getResources(),R.color.verde_008a62,null));
                        break;
                    case MODERADO:
                        binding.fondoEscaner.setBackgroundColor(ResourcesCompat.getColor(getResources(),R.color.amarillo_ffc300,null));
                        break;
                    case ALTO:
                        binding.fondoEscaner.setBackgroundColor(ResourcesCompat.getColor(getResources(),R.color.rojo_e43939,null));
                        break;
                }

            }
        });


        /*escanerViewModel.getResultadoEscaner().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean lectura) {
                if(lectura)
                    Toast.makeText(getContext(), "Leído: " + escanerViewModel.getNombreDispositivo(), Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getContext(), "Código QR no valido", Toast.LENGTH_SHORT).show();
            }
        });*/

    }

    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    private void initCallbacks() {

        // callback de boton escanear y detener
        binding.botonEscanear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    empezarAEscanear();
            }
        });
    }

    /**
     * En el momento que se pulse el botón, mira a ver si hay permisos de camara, sino hay los pide
     * @author Lorena Florescu
     * @version 02/11/2021
     */
    private void empezarAEscanear() {
        if(ActivityCompat.checkSelfPermission(getActivity().getBaseContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            //lanzamos actividad cuando hacemos click en el boton
            Intent i = new Intent(getActivity(), QrCodeActivity.class);
            startActivityForResult(i, REQUEST_CODE_QR_SCAN);
        } else {
            solicitarPermiso(Manifest.permission.CAMERA, "Sin el permiso de cámara no se puede acceder a la lectura de código QR",
                    this.getActivity(), REQUEST_CODE_CAMERA);
        }
    }

    /**
     * Metodo para pedir el permiso de cámara
     * @author Lorena Florescu
     * @version 02/11/2021
     * @param permiso el nombre del permiso que queremos
     * @param justificacion justificacion que usaremos para mostrar por pantalla al usuario
     * @param activity la actividad en la que se produce
     * @param requestCode codigo para identificar la peticion
     */
    public static void solicitarPermiso(final String permiso, String justificacion, final Activity activity, final int requestCode){

        if(ActivityCompat.shouldShowRequestPermissionRationale(activity,permiso)) {
            new AlertDialog.Builder(activity).setTitle("Solicitud de permiso")
                    .setMessage(justificacion).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ActivityCompat.requestPermissions(activity,new String[]{permiso}, requestCode);
                }
            }).show();
        } else{
            ActivityCompat.requestPermissions(activity,new String[]{permiso}, requestCode);
        }
    }

    /**
     * Si se identifica con el codigo del permiso de la camara, identificamos si se ha dado ya o no el permiso
     * si se ha dado, abrimos el escaner
     * si se ha denegado, avisamos al usuario que necesitamos el permiso
     * @author Lorena Florescu
     * @version 02/11/2021
     * @param requestCode codigo de la peticion de permiso
     * @param permissions permisos que queremos obtener
     * @param grantResult resultado de la peticion que habrá que comprobar si tenemos o no el permiso
     */
    @Override public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResult) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResult);
        if (requestCode == REQUEST_CODE_CAMERA) {
            if (grantResult.length == 1 && grantResult[0] == PackageManager.PERMISSION_GRANTED) {
                empezarAEscanear();
            } else {
                Toast.makeText(this.getActivity(), "Sin el permiso de cámara no se puede acceder a la lectura de código QR", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Si al leer el QR no nos devuelve que ha ido bien avisamos al usuario
     * Si el codigo corresponde con el codigo de peticion qr, debemos asegurarnos que recibimos datos
     * Al recibir los datos, mandaremos los datos a analizar y comprobar que coincide el prefijo
     * @author Lorena Florescu
     * @version 02/11/2021
     * @param requestCode codigo que corresponde a la peticion qr
     * @param resultCode codigo de contestación de la lectura
     * @param data dato que recibimos de la lectura
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(getContext(), "No se pudo obtener una respuesta", Toast.LENGTH_SHORT).show();

        }
        if (requestCode == REQUEST_CODE_QR_SCAN) {
            if (data != null) {
                String lectura = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");
                //escanerViewModel.arrancarServicio(lectura);
                if(!lectura.equals(null) &&lectura.indexOf(prefijo)!= -1) {
                    Toast.makeText(getContext(), "Leído: " + lectura, Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(getContext(), "Código QR no valido", Toast.LENGTH_SHORT).show();

            }
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}