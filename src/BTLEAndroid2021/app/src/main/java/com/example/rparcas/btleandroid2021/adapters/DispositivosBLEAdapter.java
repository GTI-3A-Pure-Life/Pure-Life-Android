package com.example.rparcas.btleandroid2021.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.rparcas.btleandroid2021.R;

import org.altbeacon.beacon.Beacon;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Adapter para listar los distintos beacon que llegan al dispositivo
 * Se diferencian por el UUID
 *
 * 04/10/2021
 *
 * @author Rubén Pardo Casanova
 *
 */
/*public class BeaconAdapter extends RecyclerView.Adapter<BeaconAdapter.ViewHolder>{
    protected List<String> uuidIBeacons; // Lista de uuid a mostrar
    private View.OnClickListener onClickListener;


    public BeaconAdapter(List<String> uuidIBeacons) {
        this.uuidIBeacons = uuidIBeacons;
    }

    public void updateData(List<String> data) {
        uuidIBeacons.clear();
        uuidIBeacons.addAll(data);
        notifyDataSetChanged();
    }


    //Creamos nuestro ViewHolder, con los tipos de elementos a modificar
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nombre, txPower,instanceMajorMinor;

        public ViewHolder(View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.tvUUID);

        }
        // Personalizamos un ViewHolder a partir de un lugar
        public void personaliza(String uuid) {
            nombre.setText(uuid);

        }
    }

    public void setOnItemClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    // Creamos el ViewHolder con la vista de un elemento sin personalizar
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflamos la vista desde el xml
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.beacon_item, parent, false);
        return new ViewHolder(v);
    }

    // Usando como base el ViewHolder y lo personalizamos
    @Override
    public void onBindViewHolder(ViewHolder holder, int posicion) {
        String uuid = uuidIBeacons.get(posicion);
        holder.itemView.setOnClickListener(onClickListener);
        holder.personaliza(uuid);
    }

    // Indicamos el número de elementos de la lista
    @Override public int getItemCount() {
        return uuidIBeacons.size();
    }


}*/

public class DispositivosBLEAdapter extends RecyclerView.Adapter<DispositivosBLEAdapter.DispositivoBLEViewHolder> {

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------

    private Context context;
    private List<String> dispositivos;
    private View.OnClickListener onClickListener;

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------

    public DispositivosBLEAdapter(Context context, List<String> dispositivos) {
        this.context = context;
        this.dispositivos = dispositivos;
    }

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    public void updateData(List<String> data) {
        dispositivos.clear();
        dispositivos.addAll(data);
        notifyDataSetChanged();
    }
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    public void setOnItemClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    @NonNull
    @Override
    public DispositivoBLEViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.dispositivo_item, viewGroup, false);
        return new DispositivoBLEViewHolder(view);
    }
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    @Override
    public void onBindViewHolder(@NonNull DispositivoBLEViewHolder holder, int position) {
        String dispositivo = dispositivos.get(position);
        holder.itemView.setOnClickListener(onClickListener);
        holder.dispositivo.setText(dispositivo);
    }
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    public String getItemAtPosition(int pos){
        return dispositivos.get(pos);
    }
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    @Override
    public int getItemCount() {
        return dispositivos.size();
    }


    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    public static class DispositivoBLEViewHolder extends RecyclerView.ViewHolder {

        private TextView dispositivo;

        public DispositivoBLEViewHolder(@NonNull View itemView) {
            super(itemView);

            dispositivo = itemView.findViewById(R.id.tvUUID);
        }
    }// class()
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------

}// class()
// ---------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------