package com.example.rparcas.btleandroid2021.ui.mapa;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.rparcas.btleandroid2021.databinding.FragmentMapaBinding;

/**
 * MapaFragment.java
 * @author Ruben Pardo Casanova
 * 01/11/2021
 * Clase que controla la vista del fragmento Mapa
 */
public class MapaFragment extends Fragment {

    private MapaViewModel mapaViewModel;
    private FragmentMapaBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mapaViewModel =
                new ViewModelProvider(this).get(MapaViewModel.class);

        binding = FragmentMapaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        /*
        final TextView textView = binding.textMapa;
        mapaViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
         */
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}