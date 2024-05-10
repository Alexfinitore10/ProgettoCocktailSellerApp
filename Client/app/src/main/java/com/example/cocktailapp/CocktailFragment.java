package com.example.cocktailapp;

import android.app.Activity;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
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
 * Use the {@link CocktailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CocktailFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Spinner MojitoAmount;
    private Spinner BloodyMaryAmount;
    private Spinner WhiteRussianAmount;
    private Spinner NegroniAmount;
    private Spinner DaiquiriAmount;
    private Spinner DryMartiniAmount;
    private Spinner MargaritaAmount;
    private Spinner ManhattanAmount;
    private Spinner WhiskeySourAmount;
    private Spinner MoscowMuleAmount;

    public CocktailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DrinkFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CocktailFragment newInstance(String param1, String param2) {
        CocktailFragment fragment = new CocktailFragment();
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

        return inflater.inflate(R.layout.fragment_cocktail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the Spinner
        MojitoAmount = view.findViewById(R.id.MojitoAmount);
        BloodyMaryAmount = view.findViewById(R.id.BloodyMaryAmount);
        WhiteRussianAmount = view.findViewById(R.id.WhiteRussianAmount);
        NegroniAmount = view.findViewById(R.id.NegroniAmount);
        DaiquiriAmount = view.findViewById(R.id.DaiquiriAmount);
        DryMartiniAmount = view.findViewById(R.id.DryMartiniAmount);
        MargaritaAmount = view.findViewById(R.id.MargaritaAmount);
        ManhattanAmount = view.findViewById(R.id.ManhattanAmount);
        WhiskeySourAmount = view.findViewById(R.id.WhiskeySourAmount);
        MoscowMuleAmount = view.findViewById(R.id.MoscowAmount);

        // Call the method to initialize the Spinner
        mojitoSpinnerInitializer(MojitoAmount);
        bloodyMarySpinnerInitializer(BloodyMaryAmount);
        whiteRussianSpinnerInitializer(WhiteRussianAmount);
        negroniSpinnerInitializer(NegroniAmount);
        daiquiriSpinnerInitializer(DaiquiriAmount);
        dryMartiniSpinnerInitializer(DryMartiniAmount);
        margaritaSpinnerInitializer(MargaritaAmount);
        manhattanSpinnerInitializer(ManhattanAmount);
        whiskeySourSpinnerInitializer(WhiskeySourAmount);
        moscowMuleSpinnerInitializer(MoscowMuleAmount);

    }


    private void mojitoSpinnerInitializer(Spinner mojitoAmount) {
        // Create a list of integers from 1 to 10
        List<Integer> mojitoAmounts = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            mojitoAmounts.add(i);
        }

        // Create a Spinner adapter
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                mojitoAmounts
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the adapter to the Spinner
        mojitoAmount.setAdapter(adapter);
    }

    private void bloodyMarySpinnerInitializer(Spinner bloodyMaryAmount) {
        // Create a list of integers from 1 to 10
        List<Integer> bloodyMaryAmounts = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            bloodyMaryAmounts.add(i);
        }

        // Create a Spinner adapter
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                bloodyMaryAmounts
                );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the adapter to the Spinner
        bloodyMaryAmount.setAdapter(adapter);
    }

    private void whiteRussianSpinnerInitializer(Spinner whiteRussianAmount) {
        // Create a list of integers from 1 to 10
        List<Integer> whiteRussianAmounts = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            whiteRussianAmounts.add(i);
        }

        // Create a Spinner adapter
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                whiteRussianAmounts
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the adapter to the Spinner
        whiteRussianAmount.setAdapter(adapter);
    }

    private void daiquiriSpinnerInitializer(Spinner daiquiriAmount) {
        // Create a list of integers from 1 to 10
        List<Integer> daiquiriAmounts = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            daiquiriAmounts.add(i);
        }

        // Create a Spinner adapter
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                daiquiriAmounts
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the adapter to the Spinner
        daiquiriAmount.setAdapter(adapter);
    }

    private void negroniSpinnerInitializer(Spinner negroniAmount) {

        // Create a list of integers from 1 to 10
        List<Integer> negroniAmounts = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            negroniAmounts.add(i);
        }

        // Create a Spinner adapter
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                negroniAmounts
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the adapter to the Spinner
        negroniAmount.setAdapter(adapter);
    }

    private void dryMartiniSpinnerInitializer(Spinner dryMartiniAmount) {
        // Create a list of integers from 1 to 10
        List<Integer> dryMartiniAmounts = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            dryMartiniAmounts.add(i);
        }

        // Create a Spinner adapter
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                dryMartiniAmounts
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the adapter to the Spinner
        dryMartiniAmount.setAdapter(adapter);
    }

    private void margaritaSpinnerInitializer(Spinner margaritaAmount) {
        // Create a list of integers from 1 to 10
        List<Integer> margaritaAmounts = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            margaritaAmounts.add(i);
        }

        // Create a Spinner adapter
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                margaritaAmounts
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the adapter to the Spinner
        margaritaAmount.setAdapter(adapter);
    }

    private void manhattanSpinnerInitializer(Spinner manhattanAmount) {
        // Create a list of integers from 1 to 10
        List<Integer> manhattanAmounts = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            manhattanAmounts.add(i);
        }

        // Create a Spinner adapter
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                manhattanAmounts
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the adapter to the Spinner
        manhattanAmount.setAdapter(adapter);
    }

    private void whiskeySourSpinnerInitializer(Spinner whiskeySourAmount) {
        // Create a list of integers from 1 to 10
        List<Integer> whiskeySourAmounts = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            whiskeySourAmounts.add(i);
        }

        // Create a Spinner adapter
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                whiskeySourAmounts
                );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the adapter to the Spinner
        whiskeySourAmount.setAdapter(adapter);
    }

    private void moscowMuleSpinnerInitializer(Spinner moscowMuleAmount) {
        // Create a list of integers from 1 to 10
        List<Integer> moscowMuleAmounts = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            moscowMuleAmounts.add(i);
        }

        // Create a Spinner adapter
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                moscowMuleAmounts
                );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the adapter to the Spinner
        moscowMuleAmount.setAdapter(adapter);
    }
}


