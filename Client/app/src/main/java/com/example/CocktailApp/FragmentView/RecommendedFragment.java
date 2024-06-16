package com.example.CocktailApp.FragmentView;

import static com.example.CocktailApp.ActivityView.ShopActivity.COCKTAILS;
import static com.example.CocktailApp.ActivityView.ShopActivity.RECOMMENDED_COCKTAILS;
import static com.example.CocktailApp.ActivityView.ShopActivity.RECOMMENDED_SHAKES;
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

import com.example.CocktailApp.Adapter.RecommendedLayoutClass;
import com.example.CocktailApp.Adapter.RecommendedRecyclerViewAdapter;
import com.example.CocktailApp.Model.Cocktail;
import com.example.CocktailApp.Model.CustomSharedPreferences;
import com.example.CocktailApp.Model.Shake;
import com.example.CocktailApp.R;

import java.util.ArrayList;

public class RecommendedFragment extends Fragment {
    private static RecommendedFragment instance;
    private static final String TAG = "RecommendedFragment";
    private RecyclerView recyclerView;

    private RecommendedFragment() {
        // Required empty public constructor
    }

    public static RecommendedFragment getInstance() {
        if(instance == null){
            Log.d(TAG,"creazione istanza");
            instance = new RecommendedFragment();
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
        View view = inflater.inflate(R.layout.fragment_recommended, container, false);
        recyclerView = view.findViewById(R.id.RecommendedRecyclerView);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ArrayList<Cocktail> allCocktails = new ArrayList<>();
        ArrayList<Cocktail> recommendedCocktails;
        ArrayList<Shake> allShakes = new ArrayList<>();
        ArrayList<Shake> recommendedShakes;
        ArrayList<RecommendedLayoutClass> recommendedLayoutClassArrayList = new ArrayList<>();



        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        CustomSharedPreferences exportString = CustomSharedPreferences.getInstance(requireActivity());
        String allCocktailsString = exportString.read(COCKTAILS,"");
        String recommendedCocktailsString = exportString.read(RECOMMENDED_COCKTAILS,"");
        String allShakesString = exportString.read(SHAKES,"");
        String recommendedShakesString = exportString.read(RECOMMENDED_SHAKES,"");

        Log.v(TAG,"Stringa cocktail raccomandati:" +recommendedCocktailsString);
        Log.v(TAG,"Stringa shake raccomandati:" +recommendedShakesString);

        try {
            allCocktails = Cocktail.parseCocktails(allCocktailsString);
        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "Stringa dei cocktail non conforme all'arraylist:" +e.getMessage());
            allCocktailsString = "";
            exportString.write(COCKTAILS,allCocktailsString);
        } catch(Exception e) {
            Log.e(TAG, "Errore parsing cocktails:" +e.getMessage());
            allCocktailsString = "";
            exportString.write(COCKTAILS,allCocktailsString);
        }

        if (!recommendedCocktailsString.isEmpty()) {
            recommendedCocktails = Cocktail.setRecommendedCocktails(recommendedCocktailsString);
            for (Cocktail cocktail : recommendedCocktails) {
                for (Cocktail c : allCocktails) {
                    if (cocktail.getNome().equals(c.getNome())) {
                        cocktail.setQuantita(c.getQuantita());
                    }
                }
            }

            for(Cocktail cocktail : recommendedCocktails){
                recommendedLayoutClassArrayList.add(new RecommendedLayoutClass(cocktail));
            }
        }else{
            Log.w(TAG, "Non ci sono cocktail da consigliare al momento");
        }

        try {
            allShakes = Shake.parseShakes(allShakesString);
        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "Stringa dei frullati non conforme all'arraylist:" +e.getMessage());
            allShakesString = "";
            exportString.write(SHAKES,allShakesString);
        } catch(Exception e) {
            Log.e(TAG, "Errore parsing frullati:" +e.getMessage());
        }

        if (!recommendedShakesString.isEmpty()) {
            recommendedShakes = Shake.setRecommendedShakes(recommendedShakesString);
            for(Shake shake : recommendedShakes){
                for(Shake s : allShakes) {
                    if (shake.getNome().equals(s.getNome())) {
                        shake.setQuantita(s.getQuantita());
                    }
                }
            }

            for(Shake shake : recommendedShakes){
                recommendedLayoutClassArrayList.add(new RecommendedLayoutClass(shake));
            }
        }else{
            Log.w(TAG, "Non ci sono frullati da consigliare al momento");
        }


        RecommendedRecyclerViewAdapter adapter = new RecommendedRecyclerViewAdapter(recommendedLayoutClassArrayList, this.getContext(), allCocktails, allShakes);
        recyclerView.setAdapter(adapter);
    }
}