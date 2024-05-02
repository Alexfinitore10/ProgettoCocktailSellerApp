package com.example.cocktailapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShakesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShakesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Spinner FruitShakeAmount;
    private Spinner TropicalShakeAmount;
    private Spinner BerryShakeAmount;
    private Spinner ProteinShakeAmount;
    private Spinner EsoticShakeAmount;

    public ShakesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShakesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ShakesFragment newInstance(String param1, String param2) {
        ShakesFragment fragment = new ShakesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shakes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FruitShakeAmount = view.findViewById(R.id.FruitShakeAmount);
        TropicalShakeAmount = view.findViewById(R.id.TropicalShakeAmount);
        BerryShakeAmount = view.findViewById(R.id.BerryShakeAmount);
        ProteinShakeAmount = view.findViewById(R.id.ProteinShakeAmount);
        EsoticShakeAmount = view.findViewById(R.id.EsoticShakeAmount);

        fruitShakeSpinnerInitializer(FruitShakeAmount);
        tropicalShakeSpinnerInitializer(TropicalShakeAmount);
        berryShakeSpinnerInitializer(BerryShakeAmount);
        proteinShakeSpinnerInitializer(ProteinShakeAmount);
        esoticShakeSpinnerInitializer(EsoticShakeAmount);
    }


    private void fruitShakeSpinnerInitializer(Spinner fruitShakeAmount) {
        // Create a list of integers from 1 to 10
        List<Integer> fruitShakeAmounts = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            fruitShakeAmounts.add(i);
        }

        // Create a Spinner adapter
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                fruitShakeAmounts
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the adapter to the Spinner
        fruitShakeAmount.setAdapter(adapter);
    }

    private void tropicalShakeSpinnerInitializer(Spinner tropicalShakeAmount) {
        List<Integer> tropicalShakeAmounts = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            tropicalShakeAmounts.add(i);
        }

        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                tropicalShakeAmounts
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        tropicalShakeAmount.setAdapter(adapter);

    }

    private void berryShakeSpinnerInitializer(Spinner berryShakeAmount) {
        List<Integer> berryShakeAmounts = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            berryShakeAmounts.add(i);
        }

        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                berryShakeAmounts
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        berryShakeAmount.setAdapter(adapter);
    }

    private void proteinShakeSpinnerInitializer(Spinner proteinShakeAmount) {
        List<Integer> proteinShakeAmounts = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            proteinShakeAmounts.add(i);
        }

        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                proteinShakeAmounts
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        proteinShakeAmount.setAdapter(adapter);
    }

    private void esoticShakeSpinnerInitializer(Spinner esoticShakeAmount) {
        List<Integer> esoticShakeAmounts = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            esoticShakeAmounts.add(i);
        }

        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                esoticShakeAmounts
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        esoticShakeAmount.setAdapter(adapter);
    }

}