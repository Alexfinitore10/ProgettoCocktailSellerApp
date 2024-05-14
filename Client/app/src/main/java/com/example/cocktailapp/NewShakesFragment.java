package com.example.cocktailapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewShakesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewShakesFragment extends Fragment {
    private ShakesRecyclerViewAdapter adapter;
    private ArrayList<ShakesLayoutClass> list;
    private RecyclerView recyclerView;
    private int backButtonCount = 0;
    private Client client;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NewShakesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewShakesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewShakesFragment newInstance(String param1, String param2) {
        NewShakesFragment fragment = new NewShakesFragment();
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
        return inflater.inflate(R.layout.fragment_new_shakes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        list = new ArrayList<>();
        client = Client.getIstanza();

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Increment the back button count
                backButtonCount++;

                // Show the pop-up dialog if the back button has been pressed 3 times
                if (backButtonCount == 3) {
                    showLogoutDialog(client);
                    backButtonCount = 0;
                }

            }
        });

        recyclerView = view.findViewById(R.id.ShakesRecyclerView);

        //list.add(new ShakesLayoutClass("Frullato di frutta","banana,fragola,kiwi,latte",3,5));

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ShakesRecyclerViewAdapter(list,getContext());
        recyclerView.setAdapter(adapter);
    }

    private void showLogoutDialog(Client client) {
        // Create a pop-up dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Log Out")
                .setMessage("Vuoi davvero disconnetterti?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        client.setLogged(false);
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                    }
                })
                .setNegativeButton("No", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Change the color of the positive button
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#F98500"));

        // Change the color of the negative button
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#F98500"));


    }
}