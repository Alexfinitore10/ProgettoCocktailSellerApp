package com.example.CocktailApp.FragmentView;

import static com.example.CocktailApp.ActivityView.ShopActivity.COCKTAILS;

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

import com.example.CocktailApp.Adapter.CocktailLayoutClass;
import com.example.CocktailApp.Adapter.CocktailRecyclerViewAdapter;
import com.example.CocktailApp.Model.Cocktail;
import com.example.CocktailApp.Model.CustomSharedPreferences;
import com.example.CocktailApp.R;

import java.util.ArrayList;


public class CocktailFragment extends Fragment {
    private static CocktailFragment instance;
    private static final String TAG = "CocktailFragment";
    private RecyclerView recyclerView;

    private CocktailFragment() {

    }

    public static CocktailFragment getInstance() {
        if(instance == null){
            Log.d(TAG,"creazione istanza");
            instance = new CocktailFragment();
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
        View view = inflater.inflate(R.layout.fragment_cocktail, container, false);
        recyclerView = view.findViewById(R.id.CocktailRecyclerView);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ArrayList<Cocktail> allCocktails = new ArrayList<>();
        ArrayList<CocktailLayoutClass> cocktailLayoutClassArrayList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        CustomSharedPreferences exportString = CustomSharedPreferences.getInstance(requireActivity());
        String allCocktailsString = exportString.read(COCKTAILS,"");

        try {
            allCocktails = Cocktail.parseCocktails(allCocktailsString);
        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "Stringa dei cocktail non conforme all'arraylist:" +e.getMessage());
        } catch(Exception e) {
            Log.e(TAG, "Errore parsing cocktails:" +e.getMessage());
        }


        for (Cocktail c : allCocktails) {
            cocktailLayoutClassArrayList.add(new CocktailLayoutClass(c.getNome(),c.getIngredienti(),c.getGradazione_alcolica(),c.getPrezzo(),c.getQuantita()));
        }

        CocktailRecyclerViewAdapter adapter = new CocktailRecyclerViewAdapter(cocktailLayoutClassArrayList, this.getContext(), allCocktails);
        recyclerView.setAdapter(adapter);
    }


}