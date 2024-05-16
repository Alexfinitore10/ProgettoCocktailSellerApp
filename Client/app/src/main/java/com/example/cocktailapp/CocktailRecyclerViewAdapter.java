package com.example.cocktailapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CocktailRecyclerViewAdapter extends RecyclerView.Adapter <CocktailRecyclerViewAdapter.ViewHolder> {
    private ArrayList<CocktailLayoutClass> cocktailLayoutArrayList;
    private Context context;
    private ArrayList<Cocktail> cocktailList;




    public CocktailRecyclerViewAdapter(ArrayList<CocktailLayoutClass> cocktailLayoutArrayList, Context context, ArrayList<Cocktail> cocktailList) {
        this.cocktailLayoutArrayList = cocktailLayoutArrayList;
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
        CocktailLayoutClass cocktailLayoutClass = cocktailLayoutArrayList.get(position);
        int image_id = getImageID(cocktailLayoutArrayList.get(position).getNome());
        holder.setPosition(position);


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
        holder.cocktailImage.setImageResource(image_id);

        holder.SpinnerInitializer(holder.amountSpinner, position, holder.itemView.getContext());

    }



    @Override
    public int getItemCount() {
        return cocktailLayoutArrayList.size();
    }

    private int getImageID(String nome) {
        int imageResId;
        switch (nome) {
            case "Mojito":
                imageResId = R.drawable.mojito;
                break;
            case "Bloody Mary":
                imageResId = R.drawable.bloodymary;
                break;
            case "Daquiri":
                imageResId = R.drawable.daquiri;
                break;
            case "White Russian":
                imageResId = R.drawable.whiterussian;
                break;
            case "Negroni":
                imageResId = R.drawable.negroni;
                break;
            case "Dry Martini":
                imageResId = R.drawable.drymartini;
                break;
            case "Margarita":
                imageResId = R.drawable.margarita;
                break;
            case "Manhattan":
                imageResId = R.drawable.manhattan;
                break;
            case "Whiskey Sour":
                imageResId = R.drawable.whiskeysour;
                break;
            case "Moscow Mule":
                imageResId = R.drawable.moscowmule;
                break;
            default:
                imageResId = R.drawable.cocktail_app_icon; // Replace with your default image resource ID
        }
        return imageResId;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private Button addButton;
        private Spinner amountSpinner;
        private ImageView cocktailImage;
        private int position;
        private int selectedAmount;

        public void setPosition(int position) {
            this.position = position;
        }

        TextView cocktailName, cocktailPrice, alcoholVolume, cocktailIngredients;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cocktailImage = itemView.findViewById(R.id.cocktail_image);
            addButton = itemView.findViewById(R.id.addButton);
            cocktailName = itemView.findViewById(R.id.cocktail_name);
            cocktailPrice = itemView.findViewById(R.id.cocktail_price);
            alcoholVolume = itemView.findViewById(R.id.alcohol_volume);
            cocktailIngredients = itemView.findViewById(R.id.cocktail_ingredients);
            amountSpinner = itemView.findViewById(R.id.amountSpinner);
            Carrello carrello = Carrello.getInstance();


            addButton.setOnClickListener(v -> {
                Cocktail cocktail = cocktailList.get(position);
                selectedAmount = amountSpinner.getSelectedItemPosition();
                cocktail.setQuantita(selectedAmount);
                carrello.addCocktail(cocktail);
                Toast.makeText(itemView.getContext(), "Cocktail aggiunto al carrello", Toast.LENGTH_SHORT).show();
                carrello.viewItems();

            });
        }

        void SpinnerInitializer(Spinner spinner,int position,Context context) {
            int amount = cocktailList.get(position).getQuantita();
        

            List<Integer> amounts_list = new ArrayList<>();
            for (int i = 1; i <= amount; i++) {
                amounts_list.add(i);
            }
        

            ArrayAdapter<Integer> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, amounts_list);
        

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinner.setAdapter(adapter);
        }

    }




}
