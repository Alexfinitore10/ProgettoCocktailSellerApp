package com.example.cocktailapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class CocktailRecyclerViewAdapter extends RecyclerView.Adapter <CocktailRecyclerViewAdapter.ViewHolder> {
    private ArrayList<CocktailLayoutClass> drinkLayoutArrayList;
    private Context context;
    private ArrayList<Cocktail> cocktailList;




    public CocktailRecyclerViewAdapter(ArrayList<CocktailLayoutClass> drinkLayoutArrayList, Context context,ArrayList<Cocktail> cocktailList) {
        this.drinkLayoutArrayList = drinkLayoutArrayList;
        this.context = context;
        this.cocktailList = cocktailList;
    }

    @NonNull
    @Override
    public CocktailRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cocktail_rec_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CocktailRecyclerViewAdapter.ViewHolder holder, int position) {
        CocktailLayoutClass cocktailLayoutClass = drinkLayoutArrayList.get(position);
        String ingredienti = cocktailLayoutClass.getIngredienti().toString();
        String PrezzoCocktail = String.valueOf(cocktailLayoutClass.getPrezzo());
        String GradazioneAlcolica = String.valueOf(cocktailLayoutClass.getGradazione_alcolica());
        PrezzoCocktail = String.format("%.2f",cocktailLayoutClass.getPrezzo());
        GradazioneAlcolica = String.format("%.2f",cocktailLayoutClass.getGradazione_alcolica());
        ingredienti = ingredienti.substring(1,ingredienti.length()-1);

        holder.cocktailName.setText(cocktailLayoutClass.getNome());
        holder.cocktailPrice.setText("Prezzo: "+ PrezzoCocktail+"€");
        holder.alcoholVolume.setText("Gradazione Alcolica: "+GradazioneAlcolica+"%");
        holder.cocktailIngredients.setText("Ingredienti: "+ingredienti);
    }

    @Override
    public int getItemCount() {
        return drinkLayoutArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private Button addButton;
        private Spinner amountSpinner;
        TextView cocktailName, cocktailPrice, alcoholVolume, cocktailIngredients;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            addButton = itemView.findViewById(R.id.addButton);
            amountSpinner = itemView.findViewById(R.id.amountSpinner);
            cocktailName = itemView.findViewById(R.id.cocktail_name);
            cocktailPrice = itemView.findViewById(R.id.cocktail_price);
            alcoholVolume = itemView.findViewById(R.id.alcohol_volume);
            cocktailIngredients = itemView.findViewById(R.id.cocktail_ingredients);

            SpinnerInitializer(amountSpinner,context);

            addButton.setOnClickListener(v -> {
                Log.d("onBindViewHolder: ","Sto nel addButton listener");
            });
        }

        private void SpinnerInitializer(Spinner amountSpinner,Context context) {
            List<Integer> amounts_list = new ArrayList<>();
            for (int i = 1; i <= cocktailList.size(); i++) {
                amounts_list.add(i);
            }
            // Create a Spinner adapter
            ArrayAdapter<Integer> adapter = new ArrayAdapter<>(
                    context,   
                    android.R.layout.simple_spinner_item,
                    amounts_list
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // Set the adapter to the Spinner
            amountSpinner.setAdapter(adapter);
        }

    }




}