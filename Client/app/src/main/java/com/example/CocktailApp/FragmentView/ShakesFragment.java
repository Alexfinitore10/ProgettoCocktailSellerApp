package com.example.CocktailApp.FragmentView;

import static com.example.CocktailApp.ActivityView.ShopActivity.SHAKES;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.CocktailApp.Adapter.ShakesLayoutClass;
import com.example.CocktailApp.Adapter.ShakesRecyclerViewAdapter;
import com.example.CocktailApp.Model.CustomSharedPreferences;
import com.example.CocktailApp.Model.Shake;
import com.example.CocktailApp.R;

import java.util.ArrayList;


public class ShakesFragment extends Fragment {
    private static ShakesFragment instance;
    private static final String TAG = "ShakesFragment";
    private RecyclerView recyclerView;

    private ShakesFragment() {

    }

    public static ShakesFragment getInstance() {
        if(instance == null){
            Log.d(TAG,"creazione istanza");
            instance = new ShakesFragment();
        }
        return instance;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_shakes, container, false);
        recyclerView = view.findViewById(R.id.ShakesRecyclerView);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ArrayList<Shake> allshakes = new ArrayList<>();
        ArrayList<ShakesLayoutClass> shakesLayoutClassArrayList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        CustomSharedPreferences exportString = CustomSharedPreferences.getInstance(requireActivity());
        String allShakesString = exportString.read(SHAKES,"");

        try {
            allshakes = Shake.parseShakes(allShakesString);
        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "Stringa dei frullati non conforme all'arraylist:" +e.getMessage());
        } catch(Exception e) {
            Log.e(TAG, "Errore parsing frullati:" +e.getMessage());
        }


        for (Shake s : allshakes) {
            shakesLayoutClassArrayList.add(new ShakesLayoutClass(s.getNome(),s.getIngredienti(),s.getPrezzo(),s.getQuantita()));
        }

        ShakesRecyclerViewAdapter adapter = new ShakesRecyclerViewAdapter(shakesLayoutClassArrayList, this.getContext(), allshakes);
        recyclerView.setAdapter(adapter);


    }
}