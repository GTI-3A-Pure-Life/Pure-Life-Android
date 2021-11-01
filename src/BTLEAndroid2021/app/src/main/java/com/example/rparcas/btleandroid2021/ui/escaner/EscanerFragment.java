package com.example.rparcas.btleandroid2021.ui.escaner;

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

import com.example.rparcas.btleandroid2021.databinding.FragmentEscanerBinding;

/**
 * EscanerFragment.java
 * @author Ruben Pardo Casanova
 * 01/11/2021
 * Clase que controla la vista del fragmento Escaner
 */
public class EscanerFragment extends Fragment {

    private EscanerViewModel escanerViewModel;
    private FragmentEscanerBinding binding; // este objeto hace referncia a un xml layout, no es una clase creada

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        escanerViewModel =
                new ViewModelProvider(this).get(EscanerViewModel.class);

        binding = FragmentEscanerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textEscaner;
        escanerViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}