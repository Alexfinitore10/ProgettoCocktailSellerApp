package com.example.cocktailapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class CartRecyclerViewAdapter extends RecyclerView.Adapter <CartRecyclerViewAdapter.ViewHolder> {
    private ArrayList<CartLayoutClass> cartLayoutClassArrayList;
    private Context context;


    public CartRecyclerViewAdapter(ArrayList<CartLayoutClass> cartLayoutClassArrayList, Context context) {
        this.cartLayoutClassArrayList = cartLayoutClassArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CartRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_rec_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartRecyclerViewAdapter.ViewHolder holder, int position) {
                CartLayoutClass cartLayoutClass = cartLayoutClassArrayList.get(position);
                int image_id = getImageID(cartLayoutClassArrayList.get(position).getBevanda().getNome());
                holder.setPosition(position);
                String ingredienti = cartLayoutClass.getBevanda().getIngredienti().toString();
                String prezzoBevanda = String.valueOf(cartLayoutClass.getBevanda().getPrezzo());
                if(cartLayoutClass.getBevanda() instanceof Cocktail){
                    String GradazioneAlcolica = String.valueOf(((Cocktail) cartLayoutClass.getBevanda()).getGradazione_alcolica());
                    GradazioneAlcolica = String.format("%.2f", ((Cocktail) cartLayoutClass.getBevanda()).getGradazione_alcolica());
                    holder.beverageAlcoholVolume.setText(GradazioneAlcolica);
                }
                prezzoBevanda = String.format("%.2f",cartLayoutClass.getBevanda().getPrezzo());
                ingredienti = ingredienti.substring(1,ingredienti.length()-1);

                holder.beverageName.setText(cartLayoutClass.getBevanda().getNome());
                holder.beveragePrice.setText(prezzoBevanda);
                holder.beverageIngredients.setText(ingredienti);
                holder.beverageQuantity.setText("Quantità:"+String.valueOf(cartLayoutClass.getBevanda().getQuantita()));
                holder.CartImageView.setImageResource(image_id);


    }

    @Override
    public int getItemCount() {
        return cartLayoutClassArrayList.size();
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


    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView CartImageView;
        private TextView beverageName,beveragePrice,beverageAlcoholVolume,beverageIngredients,beverageQuantity;
        private int position;

        public void setPosition(int position) {
            this.position = position;
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            beverageName = itemView.findViewById(R.id.beverage_name);
            beveragePrice = itemView.findViewById(R.id.beverage_price);
            beverageAlcoholVolume = itemView.findViewById(R.id.alcohol_volume_opt);
            beverageIngredients = itemView.findViewById(R.id.beverage_ingredients);
            beverageQuantity = itemView.findViewById(R.id.beverage_quantity);
            CartImageView = itemView.findViewById(R.id.beverage_image);
        }
    }
}
