package com.example.cocktailapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RecommendedRecyclerViewAdapter extends RecyclerView.Adapter<RecommendedRecyclerViewAdapter.ViewHolder> {
    private ArrayList<RecommendedLayoutClass> recommendedLayoutArrayList;
    private Context context;

    public RecommendedRecyclerViewAdapter(ArrayList<RecommendedLayoutClass> recommendedLayoutArrayList, Context context) {
        this.recommendedLayoutArrayList = recommendedLayoutArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecommendedRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recommended_rec_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendedRecyclerViewAdapter.ViewHolder holder, int position) {
        RecommendedLayoutClass recommendedLayoutClass = recommendedLayoutArrayList.get(position);
        int image_id = getImageID(recommendedLayoutArrayList.get(position).getBevanda().getNome());
        holder.setPosition(position);

        String ingredienti = recommendedLayoutClass.getBevanda().getIngredienti().toString();
        String prezzoBevanda = String.valueOf(recommendedLayoutClass.getBevanda().getPrezzo());
        if(recommendedLayoutClass.getBevanda() instanceof Cocktail){
            String GradazioneAlcolica = String.valueOf(((Cocktail) recommendedLayoutClass.getBevanda()).getGradazione_alcolica());
            GradazioneAlcolica = String.format("%.2f", ((Cocktail) recommendedLayoutClass.getBevanda()).getGradazione_alcolica());
            holder.recommendedAlcoholVolume.setText("Gradazione Alcolica: "+GradazioneAlcolica+"%");
        }else{
            holder.recommendedAlcoholVolume.setText("Gradazione Alcolica: Analcolico");
        }
        prezzoBevanda = String.format("%.2f",recommendedLayoutClass.getBevanda().getPrezzo());
        ingredienti = ingredienti.substring(1,ingredienti.length()-1);

        holder.recommendedName.setText(recommendedLayoutClass.getBevanda().getNome());
        holder.recommendedPrice.setText("Prezzo: "+prezzoBevanda+"€");
        holder.recommendedIngredients.setText("Ingredienti: "+ingredienti);
        holder.RecommendedImageView.setImageResource(image_id);
        holder.SpinnerInitializer(holder.amountSpinner, position, holder.itemView.getContext());

    }

    @Override
    public int getItemCount() {
        return recommendedLayoutArrayList.size();
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
            case "Frullato di frutta":
                imageResId = R.drawable.frullatodifrutta;
                break;
            case "Frullato tropicale":
                imageResId = R.drawable.frullatotropicale;
                break;
            case "Frullato di bacche":
                imageResId = R.drawable.frullatodibacche;
                break;
            case "Frullato proteico":
                imageResId = R.drawable.frullatoproteico;
                break;
            case "Frullato esotico":
                imageResId = R.drawable.frullatoesotico;
                break;
            default:
                imageResId = R.drawable.cocktail_app_icon; // Replace with your default image resource ID
        }
        return imageResId;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView RecommendedImageView;
        private Spinner amountSpinner;
        private int position;
        private TextView recommendedName, recommendedPrice, recommendedAlcoholVolume, recommendedIngredients;

        public void setPosition(int position) {
            this.position = position;
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            recommendedName = itemView.findViewById(R.id.recommended_name);
            recommendedPrice = itemView.findViewById(R.id.recommended_price);
            recommendedAlcoholVolume = itemView.findViewById(R.id.recommended_alcohol_volume);
            recommendedIngredients = itemView.findViewById(R.id.recommended_ingredients);
            RecommendedImageView = itemView.findViewById(R.id.recommended_image);
            amountSpinner = itemView.findViewById(R.id.recommendedAmountSpinner);
        }

        private void SpinnerInitializer(Spinner spinner, int position, Context context) {
            int amount = recommendedLayoutArrayList.get(position).getBevanda().getQuantita();


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
